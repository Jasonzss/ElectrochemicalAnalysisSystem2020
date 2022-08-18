package com.buledot.exception;


import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/8/17 11:57
 * @created: 异常类处理测试
 */
public class Test {

    @org.junit.Test
    public void test(){
        // 抛出自定义异常示范
        throw new UserException(CommonErrorCode.E_3001);
    }

}
