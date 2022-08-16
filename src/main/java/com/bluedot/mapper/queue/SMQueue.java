package com.bluedot.mapper.queue;




import com.bluedot.mapper.info.EntityInfo;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class SMQueue {
    private static volatile  SMQueue instance;
    private Queue<EntityInfo> queue;
    private SMQueue(){
    }

    /***
     * 实例化队列
     * @return
     */
    public static SMQueue getInstance() {
        if(instance != null) {
            return instance;
        }
        synchronized (SMQueue.class) {
            if(instance != null) {
                return instance;
            }
            instance = new SMQueue();
            instance.queue=new PriorityQueue<EntityInfo>(new Comparator<EntityInfo>() {

                @Override
                public int compare(EntityInfo o1, EntityInfo o2) {
                    return o1.getPriority()-o2.getPriority();
                }
            });
            return instance;
        }
    }

    /***
     * 判断队列是否为空
     * @return
     */
    public boolean isEmpty(){
        return queue.isEmpty();
    }

    /***
     * 弹出队头元素
     * @return
     */
    public EntityInfo poll(){
        return queue.poll();
    }

    /***
     * 添加EntityInfo到队列中
     * @param data
     */
    public void add(EntityInfo data){
        queue.add(data);
    }
}
