package com.bluedot.queue.outQueue;


import java.util.LinkedHashMap;
import java.util.Set;

public abstract class OutQueue<T> {
    protected LinkedHashMap<Long,T> map;

    /***
     * 加入数据到队列中
     * @param key
     * @param commonResult
     */
    public synchronized void put(Long key, T commonResult){
        map.put(key,commonResult);
    }




    /**
     * 判断队列是否为空
     * @return
     */
    public boolean isEmpty(){
        return map.isEmpty();
    }
    /***
     * 通过key取出数据
     * @param key
     * @return
     */
    public synchronized T take(Long key){
        return map.remove(key);
    }


    /***
     * 取出所有key
     * @return
     */
    public Set<Long> getKeys(){
        return map.keySet();
    }
}
