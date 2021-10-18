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

    @Resource
    ChangeLogsMapper changeLogsMapper;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        return null;
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
