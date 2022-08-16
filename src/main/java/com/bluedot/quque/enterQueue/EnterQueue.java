package com.bluedot.quque.enterQueue;


import java.util.PriorityQueue;

public abstract class EnterQueue<T>  {
    //线程安全
    protected PriorityQueue<T> queue;
    private final Integer capacity =1;
    /**
     * 判断队列是否为空
     * @return
     */

    public boolean isEmpty(){
        return queue.isEmpty();
    }
    /**
     * 弹出队头元素
     * @return
     */
    public T take(){
        synchronized (queue){
            T poll = queue.poll();
            //队列有空位，唤醒queue对象的等待队列中的所有线程
            System.out.println(poll.hashCode()+"数据从"+this.getClass()+"取出");
            queue.notifyAll();
            return poll;
        }
    }

    /***
     * 添加Data到对队列中
     * @param data
     */
    public void put(T data){
        synchronized (queue){
            while (queue.size()==capacity){
                System.out.println(this+"队列已满，wait");
                try {
                    //队列已满，将当前线程加入等待queue对象的等待队列中等待
                    queue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(data.hashCode()+"加入到"+this.getClass()+"队列中");
            queue.add(data);
        }
    }
}
