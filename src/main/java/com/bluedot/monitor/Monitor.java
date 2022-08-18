package com.bluedot.monitor;
/**
 * @Author SDJin
 * @CreationDate 2022/08/16 - 11:54
 * @Description ：
 */
public abstract class Monitor<T> implements  Runnable{
     /**
      * 监听的队列
      */
     protected T queue;

}
