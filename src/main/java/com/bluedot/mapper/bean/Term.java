package com.bluedot.mapper.bean;

public class Term {
    //字段所属表名
    private  String viewName;
    //字段名
    private  String fieldName;
    //字段值
    private  Object value;
    //条件类型
    private TermType termType;

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }


    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public TermType getTermType() {
        return termType;
    }

    public void setTermType(TermType termType) {
        this.termType = termType;
    }

    public Term(String viewName, String fieldName, Object value, TermType termType) {
        this.viewName = viewName;
        this.fieldName = fieldName;
        this.value = value;
        this.termType = termType;
    }
}
