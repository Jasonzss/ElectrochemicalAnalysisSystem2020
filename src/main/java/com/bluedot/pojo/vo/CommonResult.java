package com.bluedot.pojo.vo;

import com.bluedot.exception.ErrorCode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/8/16 10:23
 * @created: 通常结果类
 */
public class CommonResult{

    private int code;

    private String msg;

    private Object data;

    // 成功封装
    public static CommonResult successResult(String msg, Object data){
        return new CommonResult(200,msg,data);
    }

    // 错误封装
    public static CommonResult errorResult(int code, String msg){
        return new CommonResult(code,msg,null);
    }

    // errorCode封装
    public static CommonResult commonErrorCode(ErrorCode errorCode){
        return CommonResult.errorResult(errorCode.getCode(), errorCode.getMsg());
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public CommonResult() {
    }

    public CommonResult(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    @Override
    public String toString() {
        return "CommonResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
