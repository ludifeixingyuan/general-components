package com.pilot.log.config;

import com.pilot.log.annotion.OperationLog;
import com.pilot.log.handler.OperatorInfoService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;


/**
 * 操作日志对象定义
 *
 * @author wangzongbin
 * @date 2021-10-18
 */
@Data
@Slf4j
@Component
public class OperationLogContext implements ApplicationContextAware {

    /** 记录所有有自定义日志注解的表。Map<表名,注解> */
    public static Map<String, OperationLog> logAnnotationMap = new CaseInsensitiveMap();
    /** 表名对应的日志表 */
    public static Map<String, String> table2LogMap = new CaseInsensitiveMap();
    /** 操作人信息 service */
    public static Map<String, OperatorInfoService> operatorInfoServiceMap = new HashedMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        String[] messageConsumerBeans = applicationContext.getBeanNamesForAnnotation(OperationLog.class);
        for (String beanName : messageConsumerBeans) {
            Object clazz = applicationContext.getBean(beanName);
            OperationLog operationLog = clazz.getClass().getAnnotation(OperationLog.class);
            Type[] genericInterfaces = clazz.getClass().getGenericInterfaces();
            if (Objects.isNull(operationLog) && genericInterfaces.length > 0) {
                operationLog = (OperationLog) ((Class) genericInterfaces[0]).getAnnotation(OperationLog.class);
            }
            if (Objects.nonNull(operationLog)) {
                logAnnotationMap.put(operationLog.tableName(), operationLog);
            }
        }

        //获取操作人信息的实现类
        Map<String, OperatorInfoService> beansOfType = applicationContext.getBeansOfType(OperatorInfoService.class);
        if (Objects.nonNull(beansOfType) && beansOfType.size() > 0) {
            operatorInfoServiceMap.putAll(beansOfType);
        }
    }

}
