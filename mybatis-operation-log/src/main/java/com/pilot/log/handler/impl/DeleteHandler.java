package com.pilot.log.handler.impl;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.pilot.log.annotations.OperationLog;
import com.pilot.log.config.OperationLogContext;
import com.pilot.log.dao.ChangeLogsMapper;
import com.pilot.log.domain.ChangeLogs;
import com.pilot.log.domain.OperatorInfo;
import com.pilot.log.enums.FieldFormatEnums;
import com.pilot.log.enums.OperationEnum;
import com.pilot.log.handler.AbstractOperationHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 删除 处理器
 *
 * @author ludifeixingyuan
 * @date 2021-10-18
 */
@Slf4j
public class DeleteHandler extends AbstractOperationHandler {
    /** 前处理 */
    private Boolean preHandled = Boolean.FALSE;
    /** 更新语句 */
    private MySqlDeleteStatement deleteStatement;
    /** 删除前数据 */
    private Map<String, Map<String, Object>> auditLogsBeforeDelete;

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
        SQLTableSource from = Objects.nonNull(deleteStatement.getFrom()) ? deleteStatement.getFrom() :
                deleteStatement.getTableSource();
        SQLExpr where = deleteStatement.getWhere();
        MySqlSelectQueryBlock selectQueryBlock = new MySqlSelectQueryBlock();
        selectQueryBlock.getSelectList().add(new SQLSelectItem(SQLUtils.toSQLExpr("*")));
        selectQueryBlock.setFrom(from);
        selectQueryBlock.setWhere(where);
        String querySql = SQLUtils.toMySqlString(selectQueryBlock);

        auditLogsBeforeDelete = getCurrentDataForTables(querySql);
        preHandled = Boolean.TRUE;
    }

    @Override
    public void postHandle(Object result) {
        if (preHandled) {
            List<ChangeLogs> auditLogList = Lists.newArrayList();
            auditLogsBeforeDelete.forEach((k, v) ->
                    auditLogList.add(constructChangeLogs(k, v, OperationEnum.DELETE)));
            saveAuditLogWithMapper(auditLogList);
        }
    }

    /**
     * 查询sql获取结果
     *
     * @param querySQL
     * @return Map<主键, Map < 列名, 值>>
     */
    private Map<String, Map<String, Object>> getCurrentDataForTables(String querySQL) {
        Map<String, Map<String, Object>> resultListMap = new CaseInsensitiveMap();
        PreparedStatement statement = null;
        try {
            OperationLog operationLog = OperationLogContext.logAnnotationMap.get(tableName);
            statement = getConnection().prepareStatement(querySQL);
            ResultSet resultSet = statement.executeQuery();
            int columnCount = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                Map<String, Object> rowMap = new HashMap();
                Object primaryKeyValue = null;
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
                resultListMap.put(primaryKeyValue.toString(), rowMap);
            }
            resultSet.close();
        } catch (SQLException e) {
            log.error("查询删除前数据异常", e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    log.error("关闭statement异常。", e);
                }
            }
        }
        return resultListMap;
    }
}
