package com.bluedot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author Jason
 * @CreationDate 2022/08/16 - 15:46
 * @Description ：
 */
public class LogUtil {
    public static Logger getLogger() {
        return LoggerFactory.getLogger(Thread.currentThread().getName());
    }
}
