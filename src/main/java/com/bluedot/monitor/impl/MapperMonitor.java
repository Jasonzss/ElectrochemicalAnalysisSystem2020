package com.bluedot.monitor.impl;


import com.bluedot.mapper.BaseMapper;
import com.bluedot.monitor.Monitor;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.queue.BlockQueue;
import com.bluedot.queue.enterQueue.Impl.ServiceMapperQueue;
import com.bluedot.mapper.bean.EntityInfo;
import com.bluedot.utils.LogUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author SDJin
 * @CreationDate 2022/08/16 - 11:54
 * @Description ：
 */
public class MapperMonitor extends Monitor<ServiceMapperQueue> {
    /**
     * 单例线程安全MapperMonitor对象
     */
    private static volatile MapperMonitor instance;
    /**
     * 当前运行环境的可用处理器数量
     */
    private final static int POLLER_THREAD_COUNT = Runtime.getRuntime().availableProcessors();
    /**
     * 线程池
     */
    private ExecutorService executors;

    private MapperMonitor(){
    }

    /**
     * 实例化MapperMonitor监听器
     * @return 返回MapperMonitor对象
     */
    public static MapperMonitor getInstance() {
        if(instance != null) {
            return instance;
        }
        synchronized (MapperMonitor.class) {
            if(instance != null) {
                return instance;
            }
            LogUtil.getLogger().debug("MapperMonitor初始化");
            instance = new MapperMonitor();
            instance.queue=ServiceMapperQueue.getInstance();
            instance.executors = new ThreadPoolExecutor(10,
                    POLLER_THREAD_COUNT * 8,
                    2, TimeUnit.SECONDS,
                    new BlockQueue<Runnable>(10),
                    Executors.defaultThreadFactory(),
                    new ThreadPoolExecutor.CallerRunsPolicy());
            return instance;
        }
    }


    @Override
    public void run() {
        while (!queue.isEmpty()){
            EntityInfo poll = queue.take();
            LogUtil.getLogger().debug("MapperMonitor取出数据--key:"+poll.getKey());
            //分配线程任务处理数据
            executors.execute(() -> {
                new BaseMapper(poll);
            });
        }
    }
}
