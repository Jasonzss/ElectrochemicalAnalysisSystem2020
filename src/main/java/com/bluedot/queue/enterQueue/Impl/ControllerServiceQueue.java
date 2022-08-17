package com.bluedot.queue.enterQueue.Impl;

import com.bluedot.queue.enterQueue.EnterQueue;
import com.bluedot.pojo.Dto.Data;

import java.util.Comparator;
import java.util.PriorityQueue;

public class ControllerServiceQueue extends EnterQueue<Data> {
    //单例懒汉
    private static volatile ControllerServiceQueue instance;
    private ControllerServiceQueue() {

    }

    /***
     * 实例化队列
     * @return
     */
    public static ControllerServiceQueue getInstance() {
        if(instance != null) {
            return instance;
        }
        synchronized (ControllerServiceQueue.class) {
            if(instance != null) {
                return instance;
            }
            instance = new ControllerServiceQueue();
            instance.queue=new PriorityQueue<>(new Comparator<Data>() {
                @Override
                public int compare(Data o1, Data o2) {
                    return o1.getPriority()-o2.getPriority();
                }
            });
            return instance;
        }
    }


}
