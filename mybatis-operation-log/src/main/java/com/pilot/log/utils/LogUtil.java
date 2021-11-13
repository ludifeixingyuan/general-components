package com.pilot.log.utils;

import com.pilot.log.enums.OperationEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 日志工具类
 *
 * @author ludifeixingyuan
 * @date 2021-10-26
 */
public class LogUtil {
    /** 过滤sql空白符 */
    private static final Pattern trimPattern = Pattern.compile("[\\s]+");
    /** sql参数填充正则 */
    private static final Pattern paramPattern = Pattern.compile("\\?(?=\\s*[^']*\\s*,?\\s*(\\w|$))");

    /**
     * sql参数赋值
     *
     * @param configuration 配置
     * @param boundSql      绑定sql
     * @param operationEnum 操作枚举
     * @return {@link String}
     */
    public static String getParameterizedSql(Configuration configuration, BoundSql boundSql, OperationEnum operationEnum) {
        String sql = trimSqlWhitespaces(boundSql.getSql());
        if (Objects.equals(OperationEnum.INSERT, operationEnum)) {
            return sql;
        }

        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (CollectionUtils.isNotEmpty(parameterMappings) && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = paramPattern.matcher(sql).replaceFirst(Matcher.quoteReplacement(getParameterValue(parameterObject)));
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = paramPattern.matcher(sql).replaceFirst(Matcher.quoteReplacement(getParameterValue(obj)));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = paramPattern.matcher(sql).replaceFirst(Matcher.quoteReplacement(getParameterValue(obj)));
                    } else {
                        sql = paramPattern.matcher(sql).replaceFirst("缺失");
                    }
                }
            }
        }
        return sql;
    }

    /**
     * 过滤sql空白符
     *
     * @param sql sql
     * @return {@link String}
     */
    public static String trimSqlWhitespaces(String sql) {
        return trimPattern.matcher(sql).replaceAll(" ");
    }


    /**
     * 获取参数值
     *
     * @param obj obj
     * @return {@link String}
     */
    private static String getParameterValue(Object obj) {
        String value;
        if (obj instanceof String) {
            value = "'" + obj.toString().replaceAll("'", "''").replaceAll("\\?", "？") + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(
                    DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(new Date()) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "null";
            }

        }
        return value;
    }
}
