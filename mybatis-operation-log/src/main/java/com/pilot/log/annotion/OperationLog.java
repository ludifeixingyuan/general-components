package com.pilot.log.annotion;

import com.pilot.log.enums.FieldFormatEnums;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作变更日志注解
 *
 * @author ludifeixingyuan
 * @date 2021-10-18
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {

    /**
     * 表名
     *
     * @return {@link String}
     */
    String tableName();

    /**
     * 字段格式
     *
     * @return {@link FieldFormatEnums}
     */
    FieldFormatEnums fieldFormat() default FieldFormatEnums.HUMP;

    /**
     * 日志表名（不指定的话记录到通用日志表）
     *
     * @return {@link String}
     */
    String logTableName() default "";

    /**
     * 是否开启日志。默认开启
     *
     * @return boolean
     */
    boolean enable() default true;

    /**
     * 是否开启按月分表。默认关闭
     *
     * @return boolean
     */
    boolean tableMonthSplit() default false;
}
