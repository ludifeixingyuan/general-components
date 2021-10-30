package com.pilot.log.interceptor;

import com.pilot.log.annotations.OperationLog;
import com.pilot.log.config.OperationLogContext;
import com.pilot.log.constants.Constants;
import com.pilot.log.dao.ChangeLogsMapper;
import com.pilot.log.domain.TableHandler;
import com.pilot.log.enums.OperationEnum;
import com.pilot.log.handler.HandlerFactory;
import com.pilot.log.utils.ApplicationContextUtil;
import com.pilot.log.utils.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;
import org.springframework.util.StopWatch;

import java.sql.Connection;
import java.util.Objects;
import java.util.Properties;

/**
 * 日志拦截器
 *
 * @author ludifeixingyuan
 * @date 2021-10-26
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
@Slf4j
public class OperationLogInterceptor implements Interceptor {
    /** 是否开启数据库日志监控，默认为false */
    private boolean logEnable;
    /** 日志监控表是否开启按月分表，默认为false。 */
    private boolean defaultTableMonthSplit;
    /** 全局日志监控表的表名，默认为change_logs */
    private String defaultTableName;
    /** 变更日志映射器 */
    private ChangeLogsMapper changeLogsMapper;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            if (Objects.isNull(changeLogsMapper)) {
                changeLogsMapper = ApplicationContextUtil.getBean("changeLogsMapper");
            }
            // 获取MappedStatement实例, 并获取当前SQL命令类型
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            String sqlCommandType = mappedStatement.getSqlCommandType().name().toLowerCase();
            OperationEnum operationEnum = OperationEnum.operationEnum.get(sqlCommandType);
            // 未开启 || 日志的mapper || 未知操作 过滤
            boolean flag = logEnable == Boolean.FALSE || mappedStatement.getResource().contains(Constants.SELF_LOG_MAPPER) || Objects.isNull(operationEnum);
            if (flag) {
                return invocation.proceed();
            }
            StopWatch stopWatch = new StopWatch();
            // 拦截 {@link Executor#update(MappedStatement ms, Object parameter)} 方法
            // 拦截 Executor，强转为 Executor，默认情况下，这个Executor 是个 SimpleExecutor
            Executor executor = (Executor) invocation.getTarget();
            Connection connection = executor.getTransaction().getConnection();
            stopWatch.start("parsing to get sql");
            //获取执行参数
            Object parameter = invocation.getArgs()[1];
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            Configuration configuration = mappedStatement.getConfiguration();
            String sql = LogUtil.getParameterizedSql(configuration, boundSql, operationEnum);
            log.info("OperationLogInterceptor#intercept,sql: {}", sql);
            // 获取处理器
            TableHandler handler = HandlerFactory.findEvent(operationEnum, connection, defaultTableName,
                    sql, changeLogsMapper);
            OperationLog operationLog = OperationLogContext.logAnnotationMap.get(handler.getTableName());
            // 该表无注解 || （有注解，单关闭了日志记录的话）
            if (Objects.isNull(operationLog) || (Objects.nonNull(operationLog) && !operationLog.enable())) {
                return invocation.proceed();
            }
            // preHandle
            handler.getHandler().preHandle();
            stopWatch.stop();
            Object result = invocation.proceed();
            // postHandle
            handler.getHandler().postHandle(result);
            return result;
        } catch (Exception e) {
            log.error("OperationLogInterceptor#OperationLogInterceptor error", e);
        }
        return invocation.proceed();
    }

    /**
     * Mybatis判断依据是利用反射，获取这个拦截器的注解 Intercepts和Signature，然后解析里面的值
     *
     * @param target 目标
     * @return {@link Object}
     */
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /**
     * 配置拦截器属性参数
     *
     * @param properties 属性
     */
    @Override
    public void setProperties(Properties properties) {
        logEnable = Boolean.valueOf(properties.getProperty(Constants.KEY_LOG_ENABLE, Boolean.FALSE.toString()));
        defaultTableName = properties.getProperty(Constants.KEY_DEFAULT_TABLE_NAME, Constants.DEFAULT_TABLE_NAME);
    }
}
