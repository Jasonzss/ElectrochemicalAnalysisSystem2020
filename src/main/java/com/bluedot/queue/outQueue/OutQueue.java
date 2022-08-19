package com.bluedot.queue.outQueue;


import java.util.LinkedHashMap;
import java.util.Set;
/**
 * @Author SDJin
 * @CreationDate 2022/08/16 - 11:54
 * @Description ：
 */
public abstract class OutQueue<T> {
    /**
     * 有序集合
     */
    protected LinkedHashMap<Long,T> map;

    /***
     * 加入数据到队列中
     * @param key 结果数据对应的key
     * @param commonResult 结果数据T对象
     */
    public synchronized void put(Long key, T commonResult){
        map.put(key,commonResult);
    }




    /**
     * 判断队列是否为空
     * @return 返回true或false
     */
    public boolean isEmpty(){
        return map.isEmpty();
    }
    /***
     * 通过key取出数据
     * @param key 取出数据对应的key
     * @return 队列中key对应的结果数据
     */
    public synchronized T take(Long key){
        return map.remove(key);
    }


    /***
     * 取出所有key
     * @return 队列的集合中所有key的集合
     */
    public Set<Long> getKeys(){
        return map.keySet();
    }
}
