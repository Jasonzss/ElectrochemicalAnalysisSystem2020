package com.bluedot.exception;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/8/17 11:37
 * @created:  自定义异常信息规定方法接口
 */
public interface ErrorCode {

    /**
     * 获取错误编号
     * @return code
     */
    int getCode();

    /**
     * 获取信息
     * @return msg
     */
    String getMsg();

    /**
     * 设置自定义异常信息
     * @param msg 信息
     */
    void setMsg(String msg);
}
