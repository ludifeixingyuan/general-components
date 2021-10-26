package com.pilot.log.domain;

import com.pilot.log.handler.AbstractOperationHandler;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author wangzongbin
 * @date 2021-10-26
 */
@Data
@Accessors(chain = true)
public class TableHandler {
    /** 表名 */
    private String tableName;
    /** 处理程序 */
    private AbstractOperationHandler handler;
}
