package com.pilot.log.handler;

import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.pilot.log.config.OperationLogContext;
import com.pilot.log.dao.ChangeLogsMapper;
import com.pilot.log.domain.OperatorInfo;
import com.pilot.log.domain.TableHandler;
import com.pilot.log.enums.OperationEnum;
import com.pilot.log.handler.impl.DeleteHandler;
import com.pilot.log.handler.impl.InsertHandler;
import com.pilot.log.handler.impl.UpdateHandler;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.Map;
import java.util.Objects;

/**
 * @author ludifeixingyuan
 * @date 2021-10-26
 */
@Slf4j
public class HandlerFactory {

    /**
     * 发现事件
     *
     * @param operationEnum    操作枚举
     * @param connection       连接
     * @param defaultTableName 默认的表名
     * @param sql              sql
     * @param changeLogsMapper 变更日志映射器
     * @return {@link TableHandler}
     */
    public static TableHandler findEvent(OperationEnum operationEnum, Connection connection, String defaultTableName, String sql, ChangeLogsMapper changeLogsMapper) {
        OperatorInfo userInfo = getOperatorInfo();
        String tableName = "";
        AbstractOperationHandler operationHandler = null;
        switch (operationEnum) {
            case INSERT:
                MySqlInsertStatement sqlInsertStatement = (MySqlInsertStatement) new MySqlStatementParser(sql).parseInsert();
                tableName = sqlInsertStatement.getTableName().getSimpleName();
                operationHandler = new InsertHandler(connection, defaultTableName, sql, tableName, userInfo, changeLogsMapper, sqlInsertStatement);
                break;
            case DELETE:
                MySqlDeleteStatement sqlDeleteStatement = (MySqlDeleteStatement) new MySqlStatementParser(sql).parseDeleteStatement();
                tableName = sqlDeleteStatement.getTableName().getSimpleName();
                operationHandler = new DeleteHandler(connection, defaultTableName, sql, tableName, userInfo, changeLogsMapper, sqlDeleteStatement);
                break;
            case UPDATE:
                MySqlUpdateStatement sqlUpdateStatement = (MySqlUpdateStatement) new MySqlStatementParser(sql).parseUpdateStatement();
                tableName = sqlUpdateStatement.getTableName().getSimpleName();
                operationHandler = new UpdateHandler(connection, defaultTableName, sql, tableName, userInfo, changeLogsMapper, sqlUpdateStatement);
                break;
            default:
                log.error("no found AbstractOperationHandler,param: {}", operationEnum);
        }
        return new TableHandler().setTableName(tableName).setHandler(operationHandler);
    }

    /**
     * 获取操作人信息，由外部实现
     *
     * @return {@link OperatorInfo}
     */
    public static OperatorInfo getOperatorInfo() {
        OperatorInfo operatorInfo = null;
        for (Map.Entry<String, OperatorInfoService> entry : OperationLogContext.operatorInfoServiceMap.entrySet()) {
            operatorInfo = entry.getValue().getOperatorInfo();
        }
        if (Objects.isNull(operatorInfo)) {
            log.error("外部没有实现获取操作人信息的接口，默认id=0,name=''.");
            operatorInfo = new OperatorInfo();
            operatorInfo.setOperatorId(0L);
            operatorInfo.setOperatorName("");
        }
        return operatorInfo;
    }
}
