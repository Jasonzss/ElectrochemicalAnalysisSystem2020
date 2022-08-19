package com.bluedot.mapper.bean;

import java.util.ArrayList;
import java.util.List;

public class EntityInfo<T> {
    //优先级
    private Integer priority = 1;
    //生成的随机key
    private Long key;
    //实体类
    private List<T> entity;
    //操作类型
    private String operation;
    //查询条件
    private Condition condition;

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public List<T> getEntity() {
        return entity;
    }

    public void addEntity(T e){
        if (entity == null){
            entity = new ArrayList<>();
        }
        entity.add(e);
    }
    public void setEntity(List<T> entity) {
        this.entity = entity;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "pojo.EntityInfo{" +
                "\npriority=" + priority +
                "\nkey=" + key +
                "\nentity=" + entity +
                "\noperation='" + operation + '\'' +
                "\ncondition=" + condition +
                "\n}";
    }
}
