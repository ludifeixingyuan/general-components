package com.pilot.log.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 变更日志表
 *
 * @author ludifeixingyuan
 * @date 2021-10-18
 */
@Data
@Accessors(chain = true)
public class ChangeLogs {

    /** id */
    private Integer id;
    /** 相关的表 */
    private String relatedTable;
    /** 相关id */
    private Long relatedId;
    /** 操作 */
    private String action;
    /** 数据 */
    private String data;
    /** 备注 */
    private String remark;
    /** 来源 */
    private Integer from;
    /** 操作人id */
    private Integer operatorId;
    /** 操作人名字 */
    private String operatorName;
    /** 创建时间 */
    private Date createdAt;
    /** 更新时间 */
    private Date updatedAt;
}
