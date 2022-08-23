package com.bluedot.pojo.entity;

import java.util.Date;
import java.util.Map;

/**
 * @Author Jason
 * @CreationDate 2022/07/29 - 0:56
 * @Description ：
 */
public class Algorithm {
    private Integer algorithmId;
    private String algorithmName;
    private Integer algorithmType;
    private Integer algorithmLanguage;
    private User user;
    private String algorithmFileName;
    private String algorithmDesc;
    private Date algorithmCreateTime;
    private Date algorithmUpdateTime;
    private Integer algorithmStatus;

    public Integer getAlgorithmId() {
        return algorithmId;
    }

    public void setAlgorithmId(Integer algorithmId) {
        this.algorithmId = algorithmId;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public Integer getAlgorithmType() {
        return algorithmType;
    }

    public void setAlgorithmType(Integer algorithmType) {
        this.algorithmType = algorithmType;
    }

    public Integer getAlgorithmLanguage() {
        return algorithmLanguage;
    }

    public void setAlgorithmLanguage(Integer algorithmLanguage) {
        this.algorithmLanguage = algorithmLanguage;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAlgorithmFileName() {
        return algorithmFileName;
    }

    public void setAlgorithmFileName(String algorithmFileName) {
        this.algorithmFileName = algorithmFileName;
    }

    public String getAlgorithmDesc() {
        return algorithmDesc;
    }

    public void setAlgorithmDesc(String algorithmDesc) {
        this.algorithmDesc = algorithmDesc;
    }

    public Date getAlgorithmCreateTime() {
        return algorithmCreateTime;
    }

    public void setAlgorithmCreateTime(Date algorithmCreateTime) {
        this.algorithmCreateTime = algorithmCreateTime;
    }

    public Date getAlgorithmUpdateTime() {
        return algorithmUpdateTime;
    }

    public void setAlgorithmUpdateTime(Date algorithmUpdateTime) {
        this.algorithmUpdateTime = algorithmUpdateTime;
    }

    public Integer getAlgorithmStatus() {
        return algorithmStatus;
    }

    public void setAlgorithmStatus(Integer algorithmStatus) {
        this.algorithmStatus = algorithmStatus;
    }
}
