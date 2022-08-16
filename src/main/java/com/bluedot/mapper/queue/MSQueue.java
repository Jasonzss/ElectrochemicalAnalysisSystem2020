package com.bluedot.mapper.queue;

import com.bluedot.mapper.info.CommonResult;


import java.util.LinkedHashMap;
import java.util.Map;

public class MSQueue {
    //单例懒汉
    private static volatile MSQueue instance;
    private Map<Long, CommonResult> map;
    private MSQueue(){
    }

    /**
     * 实例化队列
     * @return
     */
    public static MSQueue getInstance() {
        if(instance != null) {
            return instance;
        }
        synchronized (MSQueue.class) {
            if(instance != null) {
                return instance;
            }
            instance = new MSQueue();
            instance.map=new LinkedHashMap<Long,CommonResult>();
            return instance;
        }
    }

    /***
     * 加入数据到队列中
     * @param key
     * @param commonResult
     */
    public void put(Long key,CommonResult commonResult){
        map.put(key,commonResult);
    }

    /**
     * 判断队列中是否有键为key的数据
     * @param key
     * @return
     */
    public boolean containKey(Long key){
        return map.containsKey(key);
    }

    /***
     * 通过key取出数据
     * @param key
     * @return
     */
    public CommonResult getByKey(Long key){
        CommonResult commonResult = map.get(key);
        map.remove(key);
        return commonResult;
    }

}
