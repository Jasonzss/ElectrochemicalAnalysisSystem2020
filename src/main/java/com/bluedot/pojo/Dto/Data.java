package com.bluedot.pojo.Dto;

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

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
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

    @Override
    public String toString() {
        return "Data{" +
                "priority=" + priority +
                ", map=" + map +
                ", key=" + key +
                ", serviceName='" + serviceName + '\'' +
                ", operation='" + operation + '\'' +
                '}';
    }
}

