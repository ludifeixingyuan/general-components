package com.pilot.log.constants;


/**
 * 表常数
 *
 * @author ludifeixingyuan
 * @date 2021-10-30
 */
public class TableConstant {
    /** 当前连接insert语句的偏移量 */
    public static final String INSERT_SQL = "select * from %s where %s >= (SELECT LAST_INSERT_ID()) limit %s";
    /** 列更新 */
    public static final String COLUMN_UPDATE_AT = "updated_at";
}
