package com.bluedot.exception;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/8/17 11:47
 * @created: 用户异常信息枚举类
 */
public enum CommonErrorCode implements ErrorCode {

    /**
     * 登录模块错误:E_1xxx
     */
    E_1001(1001,"登录失败!"),

    /**
     * 数据管理模块错误:E_2xxx
     */
    E_2001(2001,"数据管理失败!"),

    /**
     * 权限方面的异常
     */
    E_3001(3001,"您没有此操作的权限"),


    ;

    private final int code;
    private final String msg;

    CommonErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
