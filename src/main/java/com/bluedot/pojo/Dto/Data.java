package com.bluedot.pojo.Dto;

import javax.servlet.http.HttpSession;
import java.util.HashMap;

public class Data {
    //优先级
    private Integer priority = 1;
   //请求参数
    private HashMap<String, Object> map = new HashMap<>();
    //请求生成随机key
    private Long key;
    //请求Service的全限定名
    private String serviceName;
    //请求的操作类型
    private String operation;
    //请求IP地址
    private String ip;
    //操作者的Session
    private HttpSession session;




    public Data(Integer priority, HashMap<String, Object> map, Long key, String serviceName, String operation, HttpSession session) {
        this.priority = priority;
        this.map = map;
        this.key = key;
        this.serviceName = serviceName;
        this.operation = operation;
        this.session = session;
    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Data(Integer priority, HashMap<String, Object> map, Long key, String serviceName, String operation) {
        this.priority = priority;
        this.map = map;
        this.key = key;
        this.serviceName = serviceName;
        this.operation = operation;
    }
    public Data() {
    }
    public Integer getPriority() {
        return priority;
    }


    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public HashMap<String, Object> getMap() {
        return map;
    }

    public void setMap(HashMap<String, Object> map) {
        this.map = map;
    }

    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    @Override
    public String toString() {
        return "Data" +
                " key=" + key ;

    }
}

