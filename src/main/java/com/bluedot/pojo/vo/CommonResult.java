package com.bluedot.pojo.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author FireMan
 * @version 1.0
 * @date 2022/8/16 10:23
 * @created: 返回结果封装
 */
public class CommonResult<T> implements Serializable {

    private int code;

    private String msg;

    private T data;

    public static <E> CommonResult<E> successResult(String msg, E data){
        return CommonResult.<E>builder()
                .code(200)
                .msg(msg)
                .data(data)
                .build();
    }

    public static CommonResult<Void> errorResult(int code, String msg){
        return CommonResult.<Void>builder()
                .code(code)
                .msg(msg)
                .build();
    }

    public static <E> Builder<E> builder(){
        return new Builder<E>();
    }

    public Map<String, Object> mapValue(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("code",code);
        map.put("msg",msg);
        map.put("data",data);
        return map;
    }

    private CommonResult() {
    }

    private CommonResult(CommonResult<T> target) {
        this.code = target.code;
        this.msg = target.msg;
        this.data = target.data;
    }

    /**
     * Builder构建CommonResult
     */
    public static class Builder<T>{
        //构建目标
        private final CommonResult<T> target;

        public Builder(){
            this.target = new CommonResult<T>();
        }

        public Builder<T> code(Integer code){
            target.code = code;
            return this;
        }

        public Builder<T> msg(String msg){
            target.msg = msg;
            return this;
        }

        public Builder<T> data(T data){
            target.data = data;
            return this;
        }

        //创建
        public CommonResult<T> build(){
            return new CommonResult<T>(target);
        }

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
