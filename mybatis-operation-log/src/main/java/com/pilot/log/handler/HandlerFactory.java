package com.pilot.log.handler;

import com.pilot.log.enums.OperationEnum;
import com.pilot.log.handler.impl.DeleteHandler;
import com.pilot.log.handler.impl.InsertHandler;
import com.pilot.log.handler.impl.SelectHandler;
import com.pilot.log.handler.impl.UpdateHandler;

/**
 * @author wangzongbin
 * @date 2021-10-26
 */
public class HandlerFactory {

    /**
     * 发现事件
     *
     * @param event 事件
     * @return {@link AbstractOperationHandler}
     */
    public static AbstractOperationHandler findEvent(OperationEnum event) {
        switch (event) {
            case SELECT:
                return new SelectHandler();
            case INSERT:
                return new InsertHandler();
            case DELETE:
                return new DeleteHandler();
            case UPDATE:
                return new UpdateHandler();
        }
        return null;
    }
}
