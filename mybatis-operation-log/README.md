# mybatis-operation-log (mapper数据修改监控插件)

<br/>
mybatis-operation-log 数据修改日志插件对各种mapper的增删改进行监控（目前版本支持MySQL）。

本插件通过自定义注解@OperationLog+mybatis拦截器记录Mybatis下所有Mapper类在进行增删改动作时所影响数据的
oldValue（修改前，插入前，删除前），newValue（修改后，插入后，删除后）合并到表中data字段，并将此类数据日志插入到`change_logs`数据审计表中，方便运维人员进行数据复原。

#### 使用说明

完成如下两个步骤后，当启动基于mybatis的项目时，将会自动建立数据监控表，监控并记录所有由mapper引起的数据变动。

包扫描路径新增

```` 
@ComponentScan({"com.pilot.log"})
````

指定要扫描的Mapper类的包的路径

```
@MapperScan({"com.pilot.log.dao"})
```

pom.xml引入

```
<dependency>
  <groupId>io.github.ludifeixingyuan</groupId>
  <artifactId>mybatis-operation-log</artifactId>
  <version>1.0.0-RELEASE</version>
</dependency>
```

mybatis-config.xml加入

```
// 待定
```

#### 核心注解使用方式

> 注解：@OperationLog
>
> tableName（必填）：该业务表名
>
> logTableName：审计表名（不填时则为默认的change_logs）
>
> primaryKey：主键id（不填时默认为id）
>
> fieldFormat：字段格式（驼峰：FieldFormatEnums.HUMP/非驼峰FieldFormatEnums.FIELD）(不填时默认为驼峰)

示例：

````java
@OperationLog(tableName = "sys_logininfor", logTableName = "abc_logs", primaryKey = "info_id", fieldFormat = FieldFormatEnums.FIELD)
````

#### 测试示例

![image-20220113210317258](https://tva1.sinaimg.cn/large/008i3skNly1gycci495rdj324i0k0aex.jpg)

#### 数据审计记录表（change_logs）的字段介绍

```
create table change_logs
(
    id            int unsigned auto_increment
        primary key,
    related_table varchar(100)   default ''                not null comment '对应修改表名',
    related_id    bigint         default 0                 not null comment '关联id',
    action        varchar(64)    default ''                not null comment '动作',
    data          varchar(10000) default ''                not null comment '操作的结果',
    remark        varchar(255)   default ''                not null comment '备注',
    operator_id   int                                      not null comment '修改人id',
    operator_name varchar(100)   default ''                not null comment '修改人姓名',
    created_at    timestamp      default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    updated_at    timestamp      default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
)
    charset = utf8;

create index idx_created_at
    on change_logs (created_at);

create index idx_related_id
    on change_logs (related_id, related_table);

create index idx_updated_at
    on change_logs (updated_at);
