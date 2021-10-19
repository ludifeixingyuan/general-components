package com.pilot.log.interceptor;

import com.pilot.log.constants.Constants;
import com.pilot.log.dao.ChangeLogsMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
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
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        if (!globalEnable || mappedStatement.getResource().contains(SELF_LOG_MAPPER)) {
            return invocation.proceed();
        }

        return null;
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
