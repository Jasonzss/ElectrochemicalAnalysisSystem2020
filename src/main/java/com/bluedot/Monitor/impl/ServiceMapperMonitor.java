package com.bluedot.Monitor.impl;


import com.bluedot.Monitor.Monitor;
import com.bluedot.Quque.OutQueue.impl.MapperServiceQueue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.LockSupport;

public class ServiceMapperMonitor extends Monitor<MapperServiceQueue> {
    //懒汉单例
    private static volatile ServiceMapperMonitor instance;
    //线程集合
    private Map<Long, Thread> map;
    //私有构造
    private ServiceMapperMonitor() {

    }

    /**
     * 实例化Controller监听器
     * @return
     */
    public static ServiceMapperMonitor getInstance() {
        if(instance != null) {
            return instance;
        }
        synchronized (ServiceMapperMonitor.class) {
            if(instance != null) {
                return instance;
            }
            System.out.println("ServiceMapperMonitor初始化");
            instance = new ServiceMapperMonitor();
            instance.map= new HashMap<>();
            instance.queue=MapperServiceQueue.getInstance();
            return instance;
        }
    }

    @Override
    public void run() {
        System.out.println("ServiceMapper监听中");
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
       LockSupport.unpark(map.remove(key));
    }
}
