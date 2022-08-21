package com.bluedot.exception;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/8/17 11:47
 * @created: 用户异常信息枚举类
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
    E_1006(1006,"用户信息非法修改"),
    E_1007(1007,"禁止进行未验证的密码修改"),
    E_1008(1008,"账号或密码错误"),

    /**
     * 数据管理模块错误:E_2xxx
     */
    E_2001(2001,"数据管理失败!"),

    /**
     * 权限方面的异常
     */
    E_3001(3001,"您没有此操作的权限"),

    /**
     * 通用异常
     */
    E_4001(4001,"错误的请求参数"),

    /**
     * 系统异常
     */
    E_5001(5001,"系统出现异常"),

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
