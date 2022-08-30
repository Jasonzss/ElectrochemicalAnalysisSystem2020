package com.bluedot.mapper.bean;

import com.bluedot.utils.StringUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Condition {
    //返回值类型
    private String returnType;
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
        views = new ArrayList<>();
        viewCondition = new ArrayList<>();
        fields = new ArrayList<>();
        fields.add("*");
        andCondition = new ArrayList<>();
        orCondition = new ArrayList<>();
    }

    /**
     * 添加查询字段
     * @param field 需要在物理表查询的字段
     */
    public void addFields(String field) {
        if (fields.size() == 1 && "*".equals(fields.get(0))){
            fields = new ArrayList<>();
        }
        fields.add(field);
    }

    /**
     * 添加查询表
     * @param view 查询的表名
     */
    public void addView(String view) {
        if (!this.views.contains(view)){
            //当views列表中无此表则加入此表
            this.views.add(view);
        }
    }

    /**
     * 添加多表查询的连接条件
     * @param viewCondition 多表的连接字段
     * @param viewName 与主表连接的表名
     */
    public void addViewCondition(String viewCondition,String viewName) {
        if (!views.contains(viewName)){
            //尚不存在此表,添加这个表
            addView(viewName);
        }

        //获得此表在列表中的位置
        int i = views.indexOf(viewName);
        if (i != 0 && i != -1){
            //非主表
            //设置此表与主表的连接条件
            this.viewCondition.add(i-1,viewCondition);
        }
    }

    /**
     * 添加用and连接的查询条件
     * @param andCondition 查询条件
     */
    public void addAndConditionWithView(Term andCondition) {
        this.andCondition.add(andCondition);
        addView(andCondition.getViewName());
    }

    /**
     * 添加用or连接的查询条件
     * @param orCondition 查询条件
     */
    public void addOrConditionWithView(Term orCondition) {
        this.orCondition.add(orCondition);
        addView(orCondition.getViewName());
    }

    /**
     * 设置某个实体类的属性为sql查找的目标，并排除个别
     * @param clazz 实体类
     * @param exceptFields 排除属性的list
     */
    public void setFieldsInEntityExcept(Class<?> clazz,List<String> exceptFields){
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field:declaredFields){
            String fieldName = field.getName();
            if (!exceptFields.contains(fieldName)){
                //此属性不为排除属性,加入查找
                addFields(StringUtil.humpToLine(fieldName));
            }
        }
    }

    /**
     * 以下全是get和set方法
     */
    public List<String> getFields() {
        return fields;
    }

    public String getReturnType() {
        return returnType;
    }

    public List<String> getViews() {
        return views;
    }

    public List<String> getViewCondition() {
        return viewCondition;
    }

    public List<Term> getAndCondition() {
        return andCondition;
    }

    public List<Term> getOrCondition() {
        return orCondition;
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

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public void setViews(List<String> views) {
        this.views = views;
    }

    public void setViewCondition(List<String> viewCondition) {
        this.viewCondition = viewCondition;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public void setAndCondition(List<Term> andCondition) {
        this.andCondition = andCondition;
    }

    public void setOrCondition(List<Term> orCondition) {
        this.orCondition = orCondition;
    }

    @Override
    public String toString() {
        return "Condition{" +
                "returnType='" + returnType + '\'' +
                ", views=" + views +
                ", viewCondition=" + viewCondition +
                ", fields=" + fields +
                ", andCondition=" + andCondition +
                ", orCondition=" + orCondition +
                ", startIndex=" + startIndex +
                ", size=" + size +
                '}';
    }
}
