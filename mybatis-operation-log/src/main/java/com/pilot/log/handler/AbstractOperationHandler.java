package com.pilot.log.handler;

import com.alibaba.fastjson.JSON;
import com.pilot.log.config.OperationLogContext;
import com.pilot.log.dao.ChangeLogsMapper;
import com.pilot.log.domain.ChangeLogs;
import com.pilot.log.domain.OperatorInfo;
import com.pilot.log.enums.OperationEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.List;

/**
 * 操作处理器
 *
 * @author ludifeixingyuan
 * @date 2021-10-18
 */
@Slf4j
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
     * 构造ChangeLogs对象
     *
     * @param key
     * @param data
     * @param operationEnum
     * @return
     */
    public ChangeLogs constructChangeLogs(String key, Object data, OperationEnum operationEnum) {
        return new ChangeLogs().setRelatedTable(tableName)
                .setRelatedId(Long.parseLong(key))
                .setData(JSON.toJSONString(data))
                .setAction(operationEnum.getValue())
                .setOperatorId(userInfo.getOperatorId().intValue())
                .setOperatorName(userInfo.getOperatorName());
    }

    /**
     * 获得连接
     *
     * @return {@link Connection}
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * 保存与mapper审计日志
     *
     * @param auditLogList 审计日志列表
     */
    public void saveAuditLogWithMapper(List<ChangeLogs> auditLogList) {
        try {
            changeLogsMapper.batchInsert(getRealTableName(), auditLogList);
        } catch (Exception e) {
            log.error("save changes error", e);
        }
    }

    /**
     * 获取日志表
     *
     * @return
     */
    private String getRealTableName() {
        String realTableName = OperationLogContext.table2LogMap.get(tableName);
        if (StringUtils.isEmpty(realTableName)) {
            realTableName = defaultLogTableName;
        }
        return realTableName;
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
