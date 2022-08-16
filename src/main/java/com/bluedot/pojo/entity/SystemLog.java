package com.bluedot.pojo.entity;

import java.util.Date;

/**
 * @Author Jason
 * @CreationDate 2022/07/29 - 0:57
 * @Description ：
 */
public class SystemLog {
    private Integer systemLogId;
    private String systemLogType;
    private String systemLogLevel;
    private String systemLogDetails;
    private Date systemLogTime;

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

    public Date getSystemLogTime() {
        return systemLogTime;
    }

    public void setSystemLogTime(Date systemLogTime) {
        this.systemLogTime = systemLogTime;
    }
}
