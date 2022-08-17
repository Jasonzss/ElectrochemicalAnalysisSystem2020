package com.bluedot.quque.outQueue.impl;


import com.bluedot.quque.outQueue.OutQueue;
import com.bluedot.pojo.vo.CommonResult;

import java.util.LinkedHashMap;

public class ServiceControllerQueue extends OutQueue<CommonResult> {
    //单例懒汉
    private static volatile ServiceControllerQueue instance;
    private ServiceControllerQueue() {
    }

    /***
     * 实例化队列
     * @return
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
