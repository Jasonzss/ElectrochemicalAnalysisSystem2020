package com.bluedot.monitor.impl;


import com.bluedot.mapper.BaseMapper;
import com.bluedot.monitor.Monitor;
import com.bluedot.queue.enterQueue.Impl.ServiceMapperQueue;
import com.bluedot.mapper.bean.EntityInfo;
import com.bluedot.utils.LogUtil;

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
            return instance;
        }
    }

    /***
     * 根据entityInfo实例化BaseMapper
     * @param entityInfo
     */
    private void MapperInstance(EntityInfo entityInfo){
       BaseMapper basemapper = new BaseMapper(entityInfo);
       basemapper=null;
    }
    @Override
    public void run() {
        while (!queue.isEmpty()){
            EntityInfo poll = queue.take();
            MapperInstance(poll);
        }
    }
}
