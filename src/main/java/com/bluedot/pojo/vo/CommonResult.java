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
public class CommonResult {
    public static final String JSON = "application/json";
    public static final String PNG = "image/png";
    public static final String EXCEL="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    //响应码
    private int code;
    //响应信息
    private String msg;
    //响应数据
    private Object data;
    //响应头类型
    private String respHeadType = JSON;

    // 成功封装
    public static CommonResult successResult(String msg, Object data) {
        return new CommonResult(200, msg, data);
    }

    // 错误封装
    public static CommonResult errorResult(int code, String msg) {
        return new CommonResult(code, msg, null);
    }

    // errorCode封装
    public static CommonResult commonErrorCode(ErrorCode errorCode) {
        return CommonResult.errorResult(errorCode.getCode(), errorCode.getMsg());
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public Map<String, Object> mapValue() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("msg", msg);
        map.put("data", data);
        return map;
    }

    public CommonResult() {
    }

    public CommonResult(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public String getRespHeadType() {
        return respHeadType;
    }

    public void setRespHeadType(String respHeadType) {
        this.respHeadType = respHeadType;
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
