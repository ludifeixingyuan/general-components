package com.pilot.log.handler.impl;

import com.pilot.log.handler.AbstractOperationHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 更新 处理器
 *
 * @author ludifeixingyuan
 * @date 2021-10-18
 */
@Slf4j
public class UpdateHandler extends AbstractOperationHandler {

    @Override
    public void preHandle() {
        System.out.println("执行更新请求");
    }

    @Override
    public void postHandle(Object result) {

    }
}
