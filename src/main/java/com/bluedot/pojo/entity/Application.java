package com.bluedot.pojo.entity;

import java.util.Date;

/**
 * @Author Jason
 * @CreationDate 2022/07/29 - 0:56
 * @Description ：
 */
public class Application {
    private Integer applicationId;
    private User user;
    private Integer applicationType;
    private String applicationContent;
    private Date applicationTime;
    private Integer applicationStatus;
    private String applicationRejectReason;

    /**
     * 空参构造器：初始化一些默认的属性值
     */
    public Application(){
        this.applicationStatus = 0;
        this.applicationTime = new Date();
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

    public Date getApplicationTime() {
        return applicationTime;
    }

    public void setApplicationTime(Date applicationTime) {
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
}
