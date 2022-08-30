package com.bluedot.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @Author Jason
 * @CreationDate  2022/07/29 - 0:56
 * @Description ：
 */
public class Application {
    private Integer applicationId;
    private User user;
    private Integer applicationType;
    private String applicationContent;
    /**
     * 加上注解，让对象在被转化成JSON时（为了传给前段）按规定格式转化（默认会有毫秒）
     * 还有时区默认是GMT，咱们是东八区。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp applicationTime = new Timestamp(System.currentTimeMillis());
    private Integer applicationStatus;
    private String applicationRejectReason;

    public Application() {
        user = new User();
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(Integer applicationType) {
        this.applicationType = applicationType;
    }

    public String getApplicationContent() {
        return applicationContent;
    }

    public void setApplicationContent(String applicationContent) {
        this.applicationContent = applicationContent;
    }

    public Timestamp getApplicationTime() {
        return applicationTime;
    }

    public void setApplicationTime(Timestamp applicationTime) {
        this.applicationTime = applicationTime;
    }

    public Integer getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(Integer applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public String getApplicationRejectReason() {
        return applicationRejectReason;
    }

    public void setApplicationRejectReason(String applicationRejectReason) {
        this.applicationRejectReason = applicationRejectReason;
    }

    @Override
    public String toString() {
        return "Application{" +
                "applicationId=" + applicationId +
                ", user=" + user +
                ", applicationType=" + applicationType +
                ", applicationContent='" + applicationContent + '\'' +
                ", applicationTime=" + applicationTime +
                ", applicationStatus=" + applicationStatus +
                ", applicationRejectReason='" + applicationRejectReason + '\'' +
                '}';
    }
}
