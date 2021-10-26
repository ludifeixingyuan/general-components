package com.pilot.log.handler.impl;

import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.pilot.log.dao.ChangeLogsMapper;
import com.pilot.log.domain.OperatorInfo;
import com.pilot.log.handler.AbstractOperationHandler;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;

/**
 * 删除 处理器
 *
 * @author ludifeixingyuan
 * @date 2021-10-18
 */
@Slf4j
public class DeleteHandler extends AbstractOperationHandler {
    /** 更新语句 */
    private MySqlDeleteStatement deleteStatement;

    /**
     * 抽象操作处理程序
     *
     * @param connection          连接
     * @param defaultLogTableName 默认的日志表名
     * @param sql                 sql
     * @param tableName           表名
     * @param userInfo            用户信息
     * @param changeLogsMapper    改变日志映射器
     * @param deleteStatement     delete语句
     */
    public DeleteHandler(Connection connection, String defaultLogTableName, String sql,
                         String tableName, OperatorInfo userInfo, ChangeLogsMapper changeLogsMapper,
                         MySqlDeleteStatement deleteStatement) {
        super(connection, defaultLogTableName, sql, tableName, userInfo, changeLogsMapper);
        this.deleteStatement = deleteStatement;
    }

    @Override
    public void preHandle() {
        System.out.println("执行删除请求");
    }

    @Override
    public void postHandle(Object result) {

    }
}
