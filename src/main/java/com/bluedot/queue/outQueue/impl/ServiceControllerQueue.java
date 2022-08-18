package com.bluedot.queue.outQueue.impl;


import com.bluedot.queue.outQueue.OutQueue;
import com.bluedot.pojo.vo.CommonResult;

import java.util.LinkedHashMap;
/**
 * @Author SDJin
 * @CreationDate 2022/08/16 - 11:54
 * @Description ：
 */
public class ServiceControllerQueue extends OutQueue<CommonResult> {
    /**
     * 单例线程安全ServiceControllerQueue对象
     */
    private static volatile ServiceControllerQueue instance;
    private ServiceControllerQueue() {
    }

    /***
     * 实例化队列
     * @return ServiceControllerQueue对象
     */
    public static ServiceControllerQueue getInstance() {
        if(instance != null) {
            return instance;
        }
        synchronized (ServiceControllerQueue.class) {
            if(instance != null) {
                return instance;
            }
            instance = new ServiceControllerQueue();
            instance.map=new LinkedHashMap<Long, CommonResult>();
            return instance;
        }
    }

}
