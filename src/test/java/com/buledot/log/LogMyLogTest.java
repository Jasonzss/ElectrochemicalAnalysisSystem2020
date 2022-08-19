package com.buledot.log;

import com.bluedot.utils.LogUtil;
import org.junit.Test;
import org.slf4j.Logger;

import java.util.Date;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/8/19 0:09
 * @created: 日志测试
 */
public class LogMyLogTest {

    @Test
    public void test(){
        Logger logger = LogUtil.getLogger();
        logger.debug("hello");

    }

}
