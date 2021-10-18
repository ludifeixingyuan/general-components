package com.pilot.log.handler;

/**
 * 操作处理器
 *
 * @author ludifeixingyuan
 * @date 2021-10-18
 */
public abstract class AbstractOperationHandler {

    /**
     * 操作前，需要处理的
     */
    public abstract void preHandle();

    /**
     * 操作后，需要处理的
     *
     * @param result 结果
     */
    public abstract void postHandle(Object result);
}
