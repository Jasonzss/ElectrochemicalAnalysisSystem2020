package com.bluedot.monitor.impl;


import com.bluedot.adapt.Adapt;
import com.bluedot.monitor.Monitor;
import com.bluedot.queue.BlockQueue;
import com.bluedot.queue.enterQueue.Impl.ControllerServiceQueue;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.utils.LogUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author SDJin
 * @CreationDate 2022/08/16 - 11:54
 * @Description ：
 */
public class ServiceControllerMonitor extends Monitor<ControllerServiceQueue> {
    /**
     * 单例线程安全ServiceControllerMonitor对象
     */
    private static volatile ServiceControllerMonitor instance;
    /**
     * 当前运行环境的可用处理器数量
     */
    private final static int POLLER_THREAD_COUNT = Runtime.getRuntime().availableProcessors();
    /**
     * 线程池
     */
    private ExecutorService executors;

    private ServiceControllerMonitor() {

    }

    /**
     * 实例化Service监听器
     *
     * @return 返回ServiceControllerMonitor对象
     */
    public static ServiceControllerMonitor getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (ServiceControllerMonitor.class) {
            if (instance != null) {
                return instance;
            }
            LogUtil.getLogger().debug("ServiceControllerMonitor初始化");
            instance = new ServiceControllerMonitor();
            instance.queue = ControllerServiceQueue.getInstance();
            instance.executors = new ThreadPoolExecutor(4,
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
        while (!queue.isEmpty()) {
            Data poll = queue.take();
            Adapt adapt = new Adapt(poll);
            //分配线程任务处理数据
            executors.execute(adapt);
        }
    }
}
