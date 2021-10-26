package com.pilot.log.handler;

import com.pilot.log.dao.ChangeLogsMapper;
import com.pilot.log.domain.OperatorInfo;

import java.sql.Connection;

/**
 * 操作处理器
 *
 * @author ludifeixingyuan
 * @date 2021-10-18
 */
public abstract class AbstractOperationHandler {
    /** sql */
    public String sql;
    /** 表名 */
    public String tableName;
    /** 连接 */
    private Connection connection;
    /** 用户信息 */
    private OperatorInfo userInfo;
    /** 默认的日志表名 */
    private String defaultLogTableName;
    /** 改变日志映射器 */
    private ChangeLogsMapper changeLogsMapper;

    /**
     * 抽象操作处理程序
     *
     * @param connection          连接
     * @param defaultLogTableName 默认的日志表名
     * @param sql                 sql
     * @param tableName           表名
     * @param userInfo            用户信息
     * @param changeLogsMapper    变更日志映射器
     */
    public AbstractOperationHandler(Connection connection, String defaultLogTableName, String sql, String tableName,
                                    OperatorInfo userInfo, ChangeLogsMapper changeLogsMapper) {
        this.connection = connection;
        this.defaultLogTableName = defaultLogTableName;
        this.sql = sql;
        this.tableName = tableName;
        this.userInfo = userInfo;
        this.changeLogsMapper = changeLogsMapper;
    }

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
