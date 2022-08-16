package com.bluedot.quque.enterQueue.Impl;

import com.bluedot.quque.enterQueue.EnterQueue;
import com.bluedot.mapper.bean.EntityInfo;

import java.util.Comparator;
import java.util.PriorityQueue;

public class ServiceMapperQueue extends EnterQueue<EntityInfo> {
    private static volatile ServiceMapperQueue instance;
    private ServiceMapperQueue(){
    }
    /***
     * 实例化队列
     * @return
     */
    public static ServiceMapperQueue getInstance() {
        if(instance != null) {
            return instance;
        }
        synchronized (ServiceMapperQueue.class) {
            if(instance != null) {
                return instance;
            }
            instance = new ServiceMapperQueue();
            instance.queue=new PriorityQueue<EntityInfo>(new Comparator<EntityInfo>() {
                @Override
                public int compare(EntityInfo o1, EntityInfo o2) {
                    return o1.getPriority()-o2.getPriority();
                }
            });
            return instance;
        }
    }


}
