package com.bluedot.mapper.bean;

public class MappedStatement {

    private String returnType;
    private String sql;


    public MappedStatement() {

    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

}
