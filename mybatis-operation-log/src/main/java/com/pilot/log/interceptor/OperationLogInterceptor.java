package com.pilot.log.interceptor;

import com.pilot.log.constants.Constants;
import com.pilot.log.enums.OperationEnum;
import com.pilot.log.handler.AbstractOperationHandler;
import com.pilot.log.handler.HandlerFactory;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Properties;

/**
 * 日志拦截器
 *
 * @author wangzongbin
 * @date 2021-10-26
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class OperationLogInterceptor implements Interceptor {
    /** 正则匹配 insert、delete、update操作 */
    private static final String REGEX = ".*insert\\\\u0020.*|.*delete\\\\u0020.*|.*update\\\\u0020.*";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    /** 是否开启数据库日志监控，默认为false */
    private boolean logEnable;
    /** 日志监控表是否开启按月分表，默认为false。 */
    private boolean defaultTableMonthSplit;
    /** 全局日志监控表的表名，默认为change_logs */
    private boolean defaultTableName;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 拦截 {@link Executor#update(MappedStatement ms, Object parameter)} 方法
        if (logEnable) {
            // 拦截 Executor，强转为 Executor，默认情况下，这个Executor 是个 SimpleExecutor
            Executor executor = (Executor) invocation.getTarget();
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];

            if (mappedStatement.getResource().contains(Constants.SELF_LOG_MAPPER)) {
                return invocation.proceed();
            }
            //获取执行参数
            Object[] objects = invocation.getArgs();
            MappedStatement ms = (MappedStatement) objects[0];
            String sqlCommandType = mappedStatement.getSqlCommandType().name().toLowerCase();
            OperationEnum operationEnum = OperationEnum.operationEnum.get(sqlCommandType);
            AbstractOperationHandler handler = HandlerFactory.findEvent(operationEnum);
            handler.preHandle();
            BoundSql boundSql = ms.getSqlSource().getBoundSql(objects[1]);
            String sql = boundSql.getSql().toLowerCase(Locale.CHINA).replace("[\\t\\n\\r]", " ");
            logger.info("OperationLogInterceptor#intercept" + sql);
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
        defaultTableName = Boolean.valueOf(properties.getProperty(Constants.KEY_DEFAULT_TABLE_NAME, Constants.DEFAULT_TABLE_NAME));
    }
}
