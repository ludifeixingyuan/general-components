package com.pilot.log.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 操作枚举
 *
 * @author ludifeixingyuan
 * @date 2021-10-18
 */
@Getter
@AllArgsConstructor
public enum OperationEnum {

    /** 插入 */
    INSERT("insert"),
    /** 删除 */
    DELETE("delete"),
    /** 更新 */
    UPDATE("update"),
    /** 查询 */
    SELECT("select"),
    ;

    /** enum */
    public static final Map<String, OperationEnum> operationEnum = new ConcurrentHashMap<>();

    static {
        OperationEnum[] types = OperationEnum.values();
        for (OperationEnum type : types) {
            operationEnum.put(type.getValue(), type);
        }
    }

    private String value;
}
