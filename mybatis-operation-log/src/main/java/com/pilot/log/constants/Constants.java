package com.pilot.log.constants;

/**
 * 常量
 *
 * @author ludifeixingyuan
 * @date 2021-10-18
 */
public class Constants {

    /** 更新 */
    public static final String UPDATE = "update";
    /** 日志的mapper不进行过滤 */
    public static final String SELF_LOG_MAPPER = "ChangeLogsMapper";

    /** KEY:是否开启数据库日志监控 */
    public static final String KEY_LOG_ENABLE = "logEnable";
    /** KEY:默认的日志表表名 */
    public static final String KEY_DEFAULT_TABLE_NAME = "defaultTableName";
    /** VALUE:默认的日志表表名 */
    public static final String DEFAULT_TABLE_NAME = "change_logs";
}
