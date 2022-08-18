package com.bluedot.queue.enterQueue.Impl;

import com.bluedot.queue.enterQueue.EnterQueue;
import com.bluedot.pojo.Dto.Data;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @Author SDJin
 * @CreationDate 2022/08/16 - 11:54
 * @Description ：
 */
public class ControllerServiceQueue extends EnterQueue<Data> {
    /**
     * 单例线程安全ControllerServiceQueue
     */
    private static volatile ControllerServiceQueue instance;

    private ControllerServiceQueue() {

    }

    /***
     * 实例化队列
     * @return ControllerServiceQueue对象
     */
    public static ControllerServiceQueue getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (ControllerServiceQueue.class) {
            if (instance != null) {
                return instance;
            }
            instance = new ControllerServiceQueue();
            instance.capacity = 10;
            instance.queue = new PriorityQueue<>((o1, o2) -> o1.getPriority() - o2.getPriority());
            return instance;
        }
    }


}
