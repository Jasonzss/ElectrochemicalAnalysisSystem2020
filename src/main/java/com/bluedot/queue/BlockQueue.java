package com.bluedot.queue;

import com.bluedot.utils.LogUtil;
import org.slf4j.Logger;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class BlockQueue<E> extends AbstractQueue<E>
        implements BlockingQueue<E> {

    /**
     * 排序大小
     */
    private final int threshold;

    private final Logger logger = LogUtil.getLogger();

    private Object[] items;

    private int count = 0;

    final ReentrantLock lock;

    private final Condition notEmpty;

    /**
     * Condition for waiting puts
     */
    private final Condition notFull;

    private int putIndex;
    private int takeIndex;

    public BlockQueue(int capacity) {
        this(capacity, 50);
    }

    public BlockQueue(Integer capacity, long frequency) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }
        this.items = new Object[capacity];
        lock = new ReentrantLock(false);
        notEmpty = lock.newCondition();
        notFull = lock.newCondition();
        this.threshold = (int) (capacity * (frequency / 100));
    }

    public void sort() {
        Object[] newItem = new Object[count];
        for (int i = 0; i < count; i++) {
            if (takeIndex + i >= items.length) {

            }else {
                newItem[i] = items[takeIndex + i];
            }
        }
    }

    /**
     * 添加元素
     * @param e items
     * @return 成功 ture 失败 false
     */
    @Override
    public boolean offer(E e) {
        checkNotNull(e);
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (count == items.length) {
                return false;
            } else {
                enqueue(e);
                return true;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E poll() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return (count == 0) ? null : dequeue();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E peek() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return (E) items[takeIndex];
        } finally {
            lock.unlock();
        }
    }

    /**
     * 添加元素 已满则等待
     * @param e
     * @throws InterruptedException
     */
    @Override
    public void put(E e) throws InterruptedException {
        checkNotNull(e);
        checkSort();
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == items.length) {
                notFull.await();
            }
            logger.info("数据 ---> 放入队列: {}",e);
            enqueue(e);
        } finally {
            lock.unlock();
        }
    }

    private void checkSort() {
        if (count >= threshold) {
            sort();
        }
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
            checkNotNull(e);
            long nanos = unit.toNanos(timeout);
            final ReentrantLock lock = this.lock;
            lock.lockInterruptibly();
            try {
                while (count == items.length) {
                    if (nanos <= 0) {
                        return false;
                    }
                    nanos = notFull.awaitNanos(nanos);
                }
                enqueue(e);
                return true;
            } finally {
                lock.unlock();
            }
    }

    /**
     * 获取元素 队列已空则等待
     * @return
     * @throws InterruptedException
     */
    @Override
    public E take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == 0) {
                notEmpty.await();
            }
            logger.info("数据 ---> 取出队列");
            return dequeue();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == 0) {
                if (nanos <= 0) {
                    return null;
                }
                nanos = notEmpty.awaitNanos(nanos);
            }
            return dequeue();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int remainingCapacity() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return items.length - count;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        return drainTo(c, Integer.MAX_VALUE);
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        return 0;
    }

    /**
     * 插入元素
     * @param x 对象
     */
    private void enqueue(E x) {
        final Object[] items = this.items;
        items[putIndex] = x;
        if (++putIndex == items.length) {
            putIndex = 0;
        }
        count++;
        notEmpty.signal();
    }

    /**
     * 取出元素
     * @return E
     */
    private E dequeue() {
        // assert lock.getHoldCount() == 1;
        // assert items[takeIndex] != null;
        final Object[] items = this.items;
        @SuppressWarnings("unchecked")
        E x = (E) items[takeIndex];
        items[takeIndex] = null;
        if (++takeIndex == items.length) {
            takeIndex = 0;
        }
        count--;
        notFull.signal();
        return x;
    }

    /**
     * 判断是否为空
     *
     * @param e : E
     */
    private void checkNotNull(E e) {
        if (e == null || "".equals(e)) {
            throw new NullPointerException();
        }
    }

    /**
     * not null
     * @return Iterator
     */
    @Override
    public Iterator<E> iterator() {
        return null;
    }

    /**
     * 队列大小
     * @return
     */
    @Override
    public int size() {
        return this.count;
    }
}
