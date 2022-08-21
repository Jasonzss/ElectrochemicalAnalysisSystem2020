package com.bluedot.exception;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/8/17 11:47
 * @created: 自定义异常信息枚举类
 */
public enum CommonErrorCode implements ErrorCode {

    /**
     * 用户模块错误:E_1xxx
     */
    E_1001(1001,"登录失败!"),
    E_1002(1002,"该邮箱已注册"),
    E_1003(1003,"非法邮箱"),
    E_1004(1004,"验证码错误"),
    E_1005(1005,"该用户邮箱不存在"),

    /**
     * 数据管理模块错误:E_2xxx
     */
    E_2001(2001,"数据管理失败!"),

    /**
     * 权限方面的异常
     */
    E_3001(3001,"您没有此操作的权限"),

    /**
     * 系统方面的异常
     */
    E_9001(9001,"错误的请求参数"),

    ;

    private final int code;
    private String msg;

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

    @Override
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
