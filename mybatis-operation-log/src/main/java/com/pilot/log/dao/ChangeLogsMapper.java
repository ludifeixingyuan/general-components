package com.pilot.log.dao;

import com.pilot.log.domain.ChangeLogs;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 操作日志 mapper
 *
 * @author ludifeixingyuan
 * @date 2021-10-18
 */
@Mapper
public interface ChangeLogsMapper {

    /**
     * 批量插入
     *
     * @param tableName
     * @param list
     * @return
     */
    @Insert({
            "<script>",
            "insert into ${tableName}(related_table, related_id, action, data, operator_id, operator_name) values ",
            "<foreach collection='list' item='item' index='index' separator=','>",
            "(#{item.relatedTable}, #{item.relatedId}, #{item.action}, #{item.data}, #{item.operatorId}, #{item.operatorName} )",
            "</foreach>",
            "</script>"
    })
    int batchInsert(String tableName, List<ChangeLogs> list);
}
