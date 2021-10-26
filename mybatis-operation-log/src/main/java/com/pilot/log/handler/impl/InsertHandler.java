package com.pilot.log.handler.impl;

import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.pilot.log.dao.ChangeLogsMapper;
import com.pilot.log.domain.OperatorInfo;
import com.pilot.log.handler.AbstractOperationHandler;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;

/**
 * 插入 处理器
 *
 * @author ludifeixingyuan
 * @date 2021-10-18
 */
@Slf4j
public class InsertHandler extends AbstractOperationHandler {
    /** 更新语句 */
    private MySqlInsertStatement insertStatement;

    /**
     * 抽象操作处理程序
     *
     * @param connection          连接
     * @param defaultLogTableName 默认的日志表名
     * @param sql                 sql
     * @param tableName           表名
     * @param userInfo            用户信息
     * @param changeLogsMapper    改变日志映射器
     * @param insertStatement     insert语句
     */
    public InsertHandler(Connection connection, String defaultLogTableName, String sql,
                         String tableName, OperatorInfo userInfo, ChangeLogsMapper changeLogsMapper,
                         MySqlInsertStatement insertStatement) {
        super(connection, defaultLogTableName, sql, tableName, userInfo, changeLogsMapper);
        this.insertStatement = insertStatement;
    }

    @Override
    public void preHandle() {
        System.out.println("执行插入请求");
    }

    @Override
    public void postHandle(Object result) {

    }
}
