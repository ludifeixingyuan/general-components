package com.pilot.log.handler.impl;

import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.google.common.base.CaseFormat;
import com.pilot.log.annotations.OperationLog;
import com.pilot.log.config.OperationLogContext;
import com.pilot.log.constants.TableConstant;
import com.pilot.log.dao.ChangeLogsMapper;
import com.pilot.log.domain.ChangeLogs;
import com.pilot.log.domain.OperatorInfo;
import com.pilot.log.enums.FieldFormatEnums;
import com.pilot.log.enums.OperationEnum;
import com.pilot.log.handler.AbstractOperationHandler;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

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
    /** 前处理 */
    private Boolean preHandled = Boolean.FALSE;

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
        preHandled = Boolean.TRUE;
    }

    @Override
    public void postHandle(Object result) {
        if (preHandled) {
            int insertCount = Integer.parseInt(result.toString());
            if (Objects.isNull(result)) {
                return;
            }
            List<ChangeLogs> auditLogList = new ArrayList<>();
            Statement statement = null;
            try {
                OperationLog operationLog = OperationLogContext.logAnnotationMap.get(tableName);
                statement = getConnection().createStatement();
                ResultSet resultSet = statement.executeQuery(String.format(TableConstant.INSERT_SQL,
                        tableName, operationLog.primaryKey(), insertCount));
                int columnCount = resultSet.getMetaData().getColumnCount();
                Object primaryKeyValue = null;
                while (resultSet.next()) {
                    Map<String, Object> rowMap = new HashMap<>();
                    for (int i = 1; i < columnCount + 1; i++) {
                        String columnName = resultSet.getMetaData().getColumnName(i);
                        Object columnValue = resultSet.getObject(i);
                        if (operationLog.primaryKey().equals(columnName)) {
                            primaryKeyValue = columnValue;
                        }
                        rowMap.put(operationLog.fieldFormat() == FieldFormatEnums.FIELD ? columnName :
                                        CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName),
                                columnValue);
                    }
                    ChangeLogs entity = constructChangeLogs(Optional.ofNullable(primaryKeyValue).get().toString(),
                            rowMap, OperationEnum.INSERT);
                    auditLogList.add(entity);
                }
                resultSet.close();
            } catch (SQLException e) {
                log.error("execute insertHandler SQLException。", e);
            } catch (Exception e) {
                log.error("execute insertHandler Exception。", e);
            } finally {
                if (Objects.nonNull(statement)) {
                    try {
                        statement.close();
                    } catch (SQLException e) {
                        log.error("statement close SQLException。", e);
                    } catch (Exception e) {
                        log.error("statement close Exception。", e);
                    }
                }
            }
            saveAuditLogWithMapper(auditLogList);
        }
    }
}
