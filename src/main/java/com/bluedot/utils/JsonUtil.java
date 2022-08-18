package com.bluedot.utils;

import com.bluedot.pojo.vo.CommonResult;
import com.bluedot.queue.outQueue.impl.ServiceControllerQueue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;

/**
 * @Author SDJin
 * @CreationDate 2022/8/17 17:51
 * @Description ：
 */
public class JsonUtil {
    private static volatile ObjectMapper objectMapper = null;

    /**
     * 获取单例的ObjectMapper对象
     * @return
     */
    public static ObjectMapper getObjectMapper() {
        if(objectMapper != null) {
            return objectMapper;
        }
        synchronized (ServiceControllerQueue.class) {
            if(objectMapper != null) {
                return objectMapper;
            }
            objectMapper = new ObjectMapper();
            return objectMapper;
        }
    }


}
