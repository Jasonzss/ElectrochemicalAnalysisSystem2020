package com.bluedot.queue.enterQueue;


import java.util.PriorityQueue;

/**
 * @Author SDJin
 * @CreationDate 2022/08/16 - 11:54
 * @Description ：
 */
public abstract class EnterQueue<T> {
    /**
     * 优先队列
     */
    protected PriorityQueue<T> queue;
    /**
     * 队列容量
     */
    protected Integer capacity;

    /**
     * 判断队列是否为空
     *
     * @return 返回true或false
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * 弹出队头元素
     *
     * @return 返回队列头的T对象
     */
    public T take() {
        synchronized (queue) {
            T poll = queue.poll();
            //队列有空位，唤醒queue对象的等待队列中的所有线程
            queue.notifyAll();
            return poll;
        }
    }

    /***
     * 添加Data到对队列中
     * @param data T对象
     */
    public void put(T data) {
        synchronized (queue) {
            while (queue.size() == capacity) {
                System.out.println(this + "队列已满，wait");
                try {
                    //队列已满，将当前线程加入等待queue对象的等待队列中等待
                    queue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(data.hashCode() + "加入到" + this.getClass() + "队列中");
            queue.add(data);
        }
    }
}
