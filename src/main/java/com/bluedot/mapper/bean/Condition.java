package com.bluedot.mapper.bean;

import java.util.List;

/**
 * @Author Jason
 * @CreationDate 2022/08/02 - 19:42
 * @Description ：
 */
public class Condition {
    //查询涉及表,无论查几个表，第一个表为主表，也就是其他表都与第一个表有外键关联
    private List<String> views;
    //连接表条件，为主表与其他表连接的字段,第i个条件为第i+1个表与主表关联的外键字段，size为views.size()-1，
    private List<String> viewCondition;
    //查询字段
    private List<String> fields;
    //以and为连接符的查询条件
    private List<Term> andCondition;
    //以or为连接符的查询条件
    private List<Term> orCondition;
    //分页查询参数
    private Long startIndex;
    private Integer size;

    public Condition() {
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public Condition(List<String> views, List<String> viewCondition, List<String> fields, List<Term> andCondition, List<Term> orCondition, Long startIndex, Integer size) {
        this.views = views;
        this.viewCondition = viewCondition;
        this.fields = fields;
        this.andCondition = andCondition;
        this.orCondition = orCondition;
        this.startIndex = startIndex;
        this.size = size;
    }

    public List<String> getViews() {
        return views;
    }

    public void setViews(List<String> views) {
        this.views = views;
    }

    public List<String> getViewCondition() {
        return viewCondition;
    }

    public void setViewCondition(List<String> viewCondition) {
        this.viewCondition = viewCondition;
    }

    public List<Term> getAndCondition() {
        return andCondition;
    }

    public void setAndCondition(List<Term> andCondition) {
        this.andCondition = andCondition;
    }

    public List<Term> getOrCondition() {
        return orCondition;
    }

    public void setOrCondition(List<Term> orCondition) {
        this.orCondition = orCondition;
    }

    public Long getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Long startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
