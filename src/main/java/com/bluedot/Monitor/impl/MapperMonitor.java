package com.bluedot.Monitor.impl;

import com.Mvc.Mapper.BaseMapper;
import com.Mvc.Monitor.Monitor;
import com.Mvc.Quque.EnterQueue.Impl.ServiceMapperQueue;
import com.song.Test.EntityInfo;

public class MapperMonitor extends Monitor<ServiceMapperQueue> {
    //懒汉单例
    private static volatile MapperMonitor instance;
    private MapperMonitor(){
    }
    //实例化Mapper监听器
    public static MapperMonitor getInstance() {
        if(instance != null) {
            return instance;
        }
        synchronized (MapperMonitor.class) {
            if(instance != null) {
                return instance;
            }
            System.out.println("MapperMonitor初始化");
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
        System.out.println("mapper监听中");
        while (!queue.isEmpty()){
            EntityInfo poll = queue.take();
            MapperInstance(poll);
        }
    }
}
