package com.bluedot.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

/**
 * @Author Jason
 * @CreationDate  2022/07/29 - 0:57
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
    /**
     * 加上注解，让对象在被转化成JSON时（为了传给前段）按规定格式转化（默认会有毫秒）
     * 还有时区默认是GMT，咱们是东八区。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp userLogOperateTime;

    public UserLog() {
        user = new User();
    }

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

    public Timestamp getUserLogOperateTime() {
        return userLogOperateTime;
    }

    public void setUserLogOperateTime(Timestamp userLogOperateTime) {
        this.userLogOperateTime = userLogOperateTime;
    }

    @Override
    public String toString() {
        return "UserLog{" +
                "userLogId=" + userLogId +
                ", user=" + user +
                ", userIp='" + userIp + '\'' +
                ", userLogLevel='" + userLogLevel + '\'' +
                ", userLogClassMethodName='" + userLogClassMethodName + '\'' +
                ", userLogParameter='" + userLogParameter + '\'' +
                ", userLogMethodReturnValue='" + userLogMethodReturnValue + '\'' +
                ", userLogDetail='" + userLogDetail + '\'' +
                ", userLogOperateTime=" + userLogOperateTime +
                '}';
    }
}
