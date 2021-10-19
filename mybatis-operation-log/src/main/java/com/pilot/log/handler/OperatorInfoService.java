package com.pilot.log.handler;

import com.pilot.log.domain.OperatorInfo;

/**
 * 操作人信息 service
 *
 * @author ludifeixingyuan
 * @date 2021-10-18
 */
public interface OperatorInfoService {

    /**
     * 获取操作人用户信息，由使用方实现
     *
     * @return {@link OperatorInfo}
     */
    OperatorInfo getOperatorInfo();
}
