package com.bluedot.mapper.info;

import java.util.HashMap;
import java.util.Map;

public class CommonResult<T> {

    private int code;

    private String msg;

    private T data;

    @Override
    public String toString() {
        return "CommonResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public static <E> CommonResult<E> successResult(String msg, E data){
        return CommonResult.<E>builder()
                .setCode(200)
                .setMsg(msg)
                .setData(data)
                .build();
    }

    public static CommonResult<Void> errorResult(int code, String msg){
        return CommonResult.<Void>builder()
                .setCode(code)
                .setMsg(msg)
                .build();
    }

    private CommonResult() {
    }

    private CommonResult(CommonResult<T> target) {
        this.code = target.code;
        this.msg = target.msg;
        this.data = target.data;
    }

    public static  <E> Builder<E> builder(){
        return new Builder<E>();
    }

    public Map<String, Object> mapValue(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("code",code);
        map.put("msg",msg);
        map.put("data",data);
        return map;
    }

    /**
     * Builder构建CommonResult
     */
    public static class Builder<T>{
        //构建目标
        private CommonResult<T> target;

        public Builder(){
            this.target = new CommonResult<T>();
        }

        public Builder<T> setCode(Integer code){

            target.code = code;
            return this;
        }

        public Builder<T> setMsg(String msg){
            target.msg = msg;
            return this;
        }

        public Builder<T> setData(T data){
            target.data = data;
            return this;
        }

        //创建
        public CommonResult<T> build(){
            return new CommonResult<T>(target);
        }

    }

}
