package com.pilot.log.handler.impl;

import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.pilot.log.dao.ChangeLogsMapper;
import com.pilot.log.domain.OperatorInfo;
import com.pilot.log.handler.AbstractOperationHandler;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;

/**
 * 更新 处理器
 *
 * @author ludifeixingyuan
 * @date 2021-10-18
 */
@Slf4j
public class UpdateHandler extends AbstractOperationHandler {
    /** 更新语句 */
    private MySqlUpdateStatement updateStatement;

    /**
     * 抽象操作处理程序
     *
     * @param connection          连接
     * @param defaultLogTableName 默认的日志表名
     * @param sql                 sql
     * @param tableName           表名
     * @param userInfo            用户信息
     * @param changeLogsMapper    改变日志映射器
     * @param updateStatement     更新语句
     */
    public UpdateHandler(Connection connection, String defaultLogTableName, String sql,
                         String tableName, OperatorInfo userInfo, ChangeLogsMapper changeLogsMapper,
                         MySqlUpdateStatement updateStatement) {
        super(connection, defaultLogTableName, sql, tableName, userInfo, changeLogsMapper);
        this.updateStatement = updateStatement;
    }

    @Override
    public void preHandle() {
        System.out.println("执行更新请求");
    }

    @Override
    public void postHandle(Object result) {

    }
}
