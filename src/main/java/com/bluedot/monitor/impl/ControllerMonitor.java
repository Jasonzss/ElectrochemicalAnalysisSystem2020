package com.bluedot.monitor.impl;

import com.bluedot.monitor.Monitor;
import com.bluedot.quque.outQueue.impl.ServiceControllerQueue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.LockSupport;

public class ControllerMonitor extends Monitor<ServiceControllerQueue> {
    //懒汉单例
    private static volatile ControllerMonitor instance;
    //线程集合
    private Map<Long,Thread> map;
    //私有构造
    private ControllerMonitor() {

    }

    /**
     * 实例化Controller监听器
     * @return
     */
    public static ControllerMonitor getInstance() {
        if(instance != null) {
            return instance;
        }
        synchronized (ControllerMonitor.class) {
            if(instance != null) {
                return instance;
            }
            System.out.println("ControllerMonitor初始化");
            instance = new ControllerMonitor();
            instance.map= new HashMap<>();
            instance.queue=ServiceControllerQueue.getInstance();
            return instance;
        }
    }

    @Override
    public void run() {
        System.out.println("controller监听中");
        if(!queue.isEmpty()){
            Set<Long> keys = queue.getKeys();
            for (Long key : keys) {
                notice(key);
            }
        }

    }

    /**
     * 添加线程到线程map中
     * @param key
     * @param value
     */
    public void addThread(Long key,Thread value){
         map.put(key, value);
    }



    /**
     * 根据key获取线程map中对应线程并唤醒
     * @param key
     */
    private void notice(Long key){
        //唤醒线程
       LockSupport.unpark(map.remove(key));
    }
}
