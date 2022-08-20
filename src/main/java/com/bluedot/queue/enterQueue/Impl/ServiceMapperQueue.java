package com.bluedot.queue.enterQueue.Impl;

import com.bluedot.queue.enterQueue.EnterQueue;
import com.bluedot.mapper.bean.EntityInfo;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @Author SDJin
 * @CreationDate 2022/08/16 - 11:54
 * @Description ：
 */
public class ServiceMapperQueue extends EnterQueue<EntityInfo> {
    /**
     * 单例线程安全ServiceMapperQueue对象
     */
    private static volatile ServiceMapperQueue instance;

    private ServiceMapperQueue() {
    }

    /***
     * 实例化队列
     * @return ServiceMapperQueue对象
     */
    public static ServiceMapperQueue getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (ServiceMapperQueue.class) {
            if (instance != null) {
                return instance;
            }
            instance = new ServiceMapperQueue();
            instance.capacity = 10;
            instance.queue = new PriorityQueue<EntityInfo>((o1, o2) -> o1.getPriority() - o2.getPriority());
            return instance;
        }
    }


}
