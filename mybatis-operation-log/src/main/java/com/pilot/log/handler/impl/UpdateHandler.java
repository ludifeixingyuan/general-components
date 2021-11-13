package com.pilot.log.handler.impl;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pilot.log.annotations.OperationLog;
import com.pilot.log.config.OperationLogContext;
import com.pilot.log.constants.TableConstant;
import com.pilot.log.dao.ChangeLogsMapper;
import com.pilot.log.domain.ChangeLogs;
import com.pilot.log.domain.OperatorInfo;
import com.pilot.log.enums.FieldFormatEnums;
import com.pilot.log.enums.OperationEnum;
import com.pilot.log.handler.AbstractOperationHandler;
import com.pilot.log.utils.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    /** 前处理 */
    private Boolean preHandled = Boolean.FALSE;
    /** 原数据 */
    private Map<String, Map<String, Object>> rowsBeforeUpdateListMap;

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
        //获取更新前的数据（组装查询sql）
        SQLTableSource tableSource = updateStatement.getTableSource();
        SQLExpr where = updateStatement.getWhere();
        MySqlSelectQueryBlock selectQueryBlock = new MySqlSelectQueryBlock();
        selectQueryBlock.setFrom(tableSource);
        selectQueryBlock.setWhere(where);
        selectQueryBlock.getSelectList().add(new SQLSelectItem(SQLUtils.toSQLExpr("*")));

        rowsBeforeUpdateListMap = getTableData(LogUtil.trimSqlWhitespaces(SQLUtils.toMySqlString(selectQueryBlock)));
        preHandled = Boolean.TRUE;
    }

    @Override
    public void postHandle(Object result) {
        if (preHandled) {
            List<ChangeLogs> auditLogList = Lists.newArrayList();
            if (rowsBeforeUpdateListMap.size() > 0) {
                Map<String, Map<String, Object>> rowsAfterUpdateListMap = getTablesDataAfterUpdate();
                for (String key : rowsBeforeUpdateListMap.keySet()) {
                    Map<String, Object> oldObj = rowsBeforeUpdateListMap.get(key);
                    Map<String, Object> newObj = rowsAfterUpdateListMap.get(key);

                    //比较，组装成Map<列,List>类型，list存放新旧值
                    Map<String, List<Object>> compareMap = Maps.newLinkedHashMap();
                    oldObj.forEach((k, v) -> {
                        if (!Objects.equals(TableConstant.COLUMN_UPDATE_AT, k) && !Objects.equals(v, newObj.get(k))) {
                            List<Object> list = Lists.newArrayList();
                            list.add(v);
                            list.add(newObj.get(k));
                            compareMap.put(k, list);
                        }
                    });

                    if (compareMap.size() > 0) {
                        auditLogList.add(constructChangeLogs(key, compareMap, OperationEnum.UPDATE));
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(auditLogList)) {
                saveAuditLogWithMapper(auditLogList);
            }
        }
    }

    /**
     * 获取更新后的信息
     *
     * @return
     */
    private Map<String, Map<String, Object>> getTablesDataAfterUpdate() {
        //查询
        MySqlSelectQueryBlock selectQueryBlock = new MySqlSelectQueryBlock();
        selectQueryBlock.getSelectList().add(new SQLSelectItem(SQLUtils.toSQLExpr("*")));
        selectQueryBlock.setFrom(new SQLExprTableSource(new SQLIdentifierExpr(tableName)));
        SQLInListExpr sqlInListExpr = new SQLInListExpr();
        List<SQLExpr> sqlExprList = new ArrayList<>();
        rowsBeforeUpdateListMap.keySet().forEach(key -> sqlExprList.add(SQLUtils.toSQLExpr(key)));
        sqlInListExpr.setExpr(new SQLIdentifierExpr(OperationLogContext.logAnnotationMap.get(tableName).primaryKey()));
        sqlInListExpr.setTargetList(sqlExprList);
        selectQueryBlock.setWhere(sqlInListExpr);
        return getTableData(LogUtil.trimSqlWhitespaces(SQLUtils.toMySqlString(selectQueryBlock)));
    }

    /**
     * 根据sql获取数据
     *
     * @param querySQL
     * @return Map<主键id, Map < 列名, 值>>
     */
    private Map<String, Map<String, Object>> getTableData(String querySQL) {
        Map<String, Map<String, Object>> resultListMap = new CaseInsensitiveMap();
        PreparedStatement statement = null;
        try {
            statement = getConnection().prepareStatement(querySQL);
            ResultSet resultSet = statement.executeQuery();
            int columnCount = resultSet.getMetaData().getColumnCount();
            OperationLog operationLog = OperationLogContext.logAnnotationMap.get(tableName);
            while (resultSet.next()) {
                Map<String, Object> rowMap = new CaseInsensitiveMap();
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
            log.error("查询更新数据异常", e);
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
