package com.bluedot.monitor.impl;



import com.bluedot.adapt.Adapt;
import com.bluedot.monitor.Monitor;
import com.bluedot.queue.BlockQueue;
import com.bluedot.queue.enterQueue.Impl.ControllerServiceQueue;
import com.bluedot.pojo.Dto.Data;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ServiceControllerMonitor extends Monitor<ControllerServiceQueue> {
    //懒汉单例
    private static volatile ServiceControllerMonitor instance;
    private final static   int POLLER_THREAD_COUNT = Runtime.getRuntime().availableProcessors();

    private  ExecutorService executors ;
    //私有构造
    private ServiceControllerMonitor() {

    }

    /**
     * 实例化Service监听器
     * @return
     */
    public static ServiceControllerMonitor getInstance() {
        if(instance != null) {
            return instance;
        }
        synchronized (ServiceControllerMonitor.class) {
            if(instance != null) {
                return instance;
            }
            System.out.println("ServiceControllerMonitor初始化");
            instance = new ServiceControllerMonitor();
            instance.queue=ControllerServiceQueue.getInstance();
            instance.executors=new ThreadPoolExecutor(4,
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
        System.out.println("serviceController监听中");
        while(!queue.isEmpty()){
            Data poll = queue.take();
            Adapt adapt=new Adapt(poll);
            //分配线程任务处理数据
            executors.execute(adapt);
        }
    }
}
