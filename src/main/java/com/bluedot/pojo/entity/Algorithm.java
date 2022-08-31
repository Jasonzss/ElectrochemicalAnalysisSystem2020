package com.bluedot.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

/**
 * @Author Jason
 * @CreationDate  2022/07/29 - 0:56
 * @Description ：
 */
public class Algorithm {
    private Integer algorithmId;
    private String algorithmName;
    private Integer algorithmType;
    private Integer algorithmLanguage;
    private User user;
    private String algorithmDesc;

    /**
     * 加上注解，让对象在被转化成JSON时（为了传给前段）按规定格式转化（默认会有毫秒）
     * 还有时区默认是GMT，咱们是东八区。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp algorithmCreateTime;
    /**
     * 加上注解，让对象在被转化成JSON时（为了传给前段）按规定格式转化（默认会有毫秒）
     * 还有时区默认是GMT，咱们是东八区。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp algorithmUpdateTime;
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


    public String getAlgorithmDesc() {
        return algorithmDesc;
    }

    public void setAlgorithmDesc(String algorithmDesc) {
        this.algorithmDesc = algorithmDesc;
    }

    public Timestamp getAlgorithmCreateTime() {
        return algorithmCreateTime;
    }

    public void setAlgorithmCreateTime(Timestamp algorithmCreateTime) {
        this.algorithmCreateTime = algorithmCreateTime;
    }

    public Timestamp getAlgorithmUpdateTime() {
        return algorithmUpdateTime;
    }

    public void setAlgorithmUpdateTime(Timestamp algorithmUpdateTime) {
        this.algorithmUpdateTime = algorithmUpdateTime;
    }

    public Integer getAlgorithmStatus() {
        return algorithmStatus;
    }

    public void setAlgorithmStatus(Integer algorithmStatus) {
        this.algorithmStatus = algorithmStatus;
    }

    @Override
    public String toString() {
        return "Algorithm{" +
                "algorithmId=" + algorithmId +
                ", algorithmName='" + algorithmName + '\'' +
                ", algorithmType=" + algorithmType +
                ", algorithmLanguage=" + algorithmLanguage +
                ", user=" + user +
                ", algorithmDesc='" + algorithmDesc + '\'' +
                ", algorithmCreateTime=" + algorithmCreateTime +
                ", algorithmUpdateTime=" + algorithmUpdateTime +
                ", algorithmStatus=" + algorithmStatus +
                '}';
    }
}
