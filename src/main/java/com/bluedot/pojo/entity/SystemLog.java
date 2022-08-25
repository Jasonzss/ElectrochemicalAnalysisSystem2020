package com.bluedot.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

/**
 * @Author Jason
 * @CreationDate  2022/07/29 - 0:57
 * @Description ：
 */
public class SystemLog {
    private Integer systemLogId;
    private String systemLogType;
    private String systemLogLevel;
    private String systemLogDetails;
    /**
     * 加上注解，让对象在被转化成JSON时（为了传给前段）按规定格式转化（默认会有毫秒）
     * 还有时区默认是GMT，咱们是东八区。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp systemLogTime;

    public Integer getSystemLogId() {
        return systemLogId;
    }

    public void setSystemLogId(Integer systemLogId) {
        this.systemLogId = systemLogId;
    }

    public String getSystemLogType() {
        return systemLogType;
    }

    public void setSystemLogType(String systemLogType) {
        this.systemLogType = systemLogType;
    }

    public String getSystemLogLevel() {
        return systemLogLevel;
    }

    public void setSystemLogLevel(String systemLogLevel) {
        this.systemLogLevel = systemLogLevel;
    }

    public String getSystemLogDetails() {
        return systemLogDetails;
    }

    public void setSystemLogDetails(String systemLogDetails) {
        this.systemLogDetails = systemLogDetails;
    }

    public Timestamp getSystemLogTime() {
        return systemLogTime;
    }

    public void setSystemLogTime(Timestamp systemLogTime) {
        this.systemLogTime = systemLogTime;
    }

    @Override
    public String toString() {
        return "SystemLog{" +
                "systemLogId=" + systemLogId +
                ", systemLogType='" + systemLogType + '\'' +
                ", systemLogLevel='" + systemLogLevel + '\'' +
                ", systemLogDetails='" + systemLogDetails + '\'' +
                ", systemLogTime=" + systemLogTime +
                '}';
    }
}
