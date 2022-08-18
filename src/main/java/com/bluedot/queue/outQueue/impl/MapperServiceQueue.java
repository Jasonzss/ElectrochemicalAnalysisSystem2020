package com.bluedot.queue.outQueue.impl;


import com.bluedot.queue.outQueue.OutQueue;
import com.bluedot.pojo.vo.CommonResult;

import java.util.LinkedHashMap;
/**
 * @Author SDJin
 * @CreationDate 2022/08/16 - 11:54
 * @Description ：
 */
public class MapperServiceQueue extends OutQueue<CommonResult> {
    /**
     * 单例线程安全MapperServiceQueue对象
     */
    private static volatile MapperServiceQueue instance;
    private MapperServiceQueue(){
    }

    /**
     * 实例化队列
     * @return MapperServiceQueue对象
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
