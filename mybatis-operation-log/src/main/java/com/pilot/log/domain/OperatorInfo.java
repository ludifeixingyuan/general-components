package com.pilot.log.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 操作人信息
 *
 * @author ludifeixingyuan
 * @date 2021-10-18
 */
@Data
@Accessors(chain = true)
public class OperatorInfo {

    /** 操作人id */
    private Long operatorId;
    /** 操作人名字 */
    private String operatorName;
}
