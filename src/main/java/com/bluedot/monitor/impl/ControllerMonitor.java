package com.bluedot.monitor.impl;

import com.bluedot.monitor.Monitor;
import com.bluedot.queue.outQueue.impl.ServiceControllerQueue;
import com.bluedot.utils.LogUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.LockSupport;
/**
 * @Author SDJin
 * @CreationDate 2022/08/16 - 11:54
 * @Description ：
 */
public class ControllerMonitor extends Monitor<ServiceControllerQueue> {
    /**
     * 单例线程安全ControllerMonitor对象
     */
    private static volatile ControllerMonitor instance;
    /**
     * 线程集合
     */
    private Map<Long,Thread> map;

    private ControllerMonitor() {

    }

    /**
     * 实例化Controller监听器
     * @return 返回ControllerMonitor对象
     */
    public static ControllerMonitor getInstance() {
        if(instance != null) {
            return instance;
        }
        synchronized (ControllerMonitor.class) {
            if(instance != null) {
                return instance;
            }
            LogUtil.getLogger().debug("ControllerMonitor初始化");
            instance = new ControllerMonitor();
            instance.map= new HashMap<>();
            instance.queue=ServiceControllerQueue.getInstance();
            return instance;
        }
    }

    @Override
    public void run() {
        if(!queue.isEmpty()){
            Set<Long> keys = queue.getKeys();
            for (Long key : keys) {
                notice(key);
            }
        }

    }

    /**
     * 添加线程到线程map中
     * @param key 线程对应key
     * @param value 线程对象
     */
    public void addThread(Long key,Thread value){
         map.put(key, value);
    }



    /**
     * 根据key获取线程map中对应线程并唤醒
     * @param key 线程对应key
     */
    private void notice(Long key){
        //唤醒线程
       LockSupport.unpark(map.remove(key));
    }
}
