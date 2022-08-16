package com.fuck.pojo.entity;

import java.util.Date;

/**
 * @Author Jason
 * @CreationDate 2022/07/29 - 0:57
 * @Description ：
 */
public class UserLog {
    private Integer userLogId;
    private User user;
    private String userIp;
    private String userLogLevel;
    private String userLogClassMethodName;
    private String userLogParameter;
    private String userLogMethodReturnValue;
    private String userLogDetail;
    private Date userLogOperateTime;

    public Integer getUserLogId() {
        return userLogId;
    }

    public void setUserLogId(Integer userLogId) {
        this.userLogId = userLogId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public String getUserLogLevel() {
        return userLogLevel;
    }

    public void setUserLogLevel(String userLogLevel) {
        this.userLogLevel = userLogLevel;
    }

    public String getUserLogClassMethodName() {
        return userLogClassMethodName;
    }

    public void setUserLogClassMethodName(String userLogClassMethodName) {
        this.userLogClassMethodName = userLogClassMethodName;
    }

    public String getUserLogParameter() {
        return userLogParameter;
    }

    public void setUserLogParameter(String userLogParameter) {
        this.userLogParameter = userLogParameter;
    }

    public String getUserLogMethodReturnValue() {
        return userLogMethodReturnValue;
    }

    public void setUserLogMethodReturnValue(String userLogMethodReturnValue) {
        this.userLogMethodReturnValue = userLogMethodReturnValue;
    }

    public String getUserLogDetail() {
        return userLogDetail;
    }

    public void setUserLogDetail(String userLogDetail) {
        this.userLogDetail = userLogDetail;
    }

    public Date getUserLogOperateTime() {
        return userLogOperateTime;
    }

    public void setUserLogOperateTime(Date userLogOperateTime) {
        this.userLogOperateTime = userLogOperateTime;
    }
}
