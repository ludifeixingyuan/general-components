package com.pilot.log.interceptor;

import com.pilot.log.constants.Constants;
import com.pilot.log.dao.ChangeLogsMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.util.Properties;

/**
 * 操作变更日志拦截器
 *
 * @author ludifeixingyuan
 * @date 2021-10-18
 */
@Slf4j
@Intercepts({@Signature(type = Executor.class, method = Constants.UPDATE, args = {MappedStatement.class, Object.class})})
public class OperationLogInterceptor implements Interceptor {

    /** 日志的mapper不进行过滤 */
    private static final String SELF_LOG_MAPPER = "ChangeLogsMapper";
    @Resource
    ChangeLogsMapper changeLogsMapper;
    /** 全局开关 */
    @Value("${globalEnable:false}")
    private Boolean globalEnable;
    /** 通用日志开关 */
    @Value("${globalEnable:false}")
    private Boolean genericLogEnable;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // // 根据签名指定的args顺序获取具体的实现类
        // // 获取MappedStatement实例, 并获取当前SQL命令类型
        // MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        // if (!globalEnable || mappedStatement.getResource().contains(SELF_LOG_MAPPER)) {
        //     return invocation.proceed();
        // }
        // SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        // if (Objects.nonNull(sqlCommandType)) {
        //     try {
        //         Executor executor = (Executor) invocation.getTarget();
        //         Connection connection = executor.getTransaction().getConnection();
        //
        //     } catch (Exception e) {
        //         log.error("数据库变更日志预处理失败。,sql:{}", "sql", e);
        //     }
        // }

        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql();
        log.info("mybatis intercept sql:{}", sql);
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
