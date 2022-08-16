package com.bluedot.Quque.OutQueue.impl;


import java.util.LinkedHashMap;

public class MapperServiceQueue extends OutQueue<CommonResult> {
    //单例懒汉
    private static volatile MapperServiceQueue instance;
    private MapperServiceQueue(){
    }

    /**
     * 实例化队列
     * @return
     */
    public static MapperServiceQueue getInstance() {
        if(instance != null) {
            return instance;
        }
        synchronized (MapperServiceQueue.class) {
            if(instance != null) {
                return instance;
            }
            instance = new MapperServiceQueue();
            instance.map=new LinkedHashMap<Long,CommonResult>();
            return instance;
        }
    }



}
