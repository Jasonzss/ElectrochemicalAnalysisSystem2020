package com.bluedot.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

/**
 * @Author Jason
 * @CreationDate  2022/07/29 - 0:56
 * @Description ：
 */
public class Backup {
    private Integer backupDataId;
    private Integer backupType;
    private String backupDataFileName;
    /**
     * 加上注解，让对象在被转化成JSON时（为了传给前段）按规定格式转化（默认会有毫秒）
     * 还有时区默认是GMT，咱们是东八区。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp backupTime;

    public Integer getBackupDataId() {
        return backupDataId;
    }

    public void setBackupDataId(Integer backupDataId) {
        this.backupDataId = backupDataId;
    }

    public Integer getBackupType() {
        return backupType;
    }

    public void setBackupType(Integer backupType) {
        this.backupType = backupType;
    }

    public String getBackupDataFileName() {
        return backupDataFileName;
    }

    public void setBackupDataFileName(String backupDataFileName) {
        this.backupDataFileName = backupDataFileName;
    }

    public Timestamp getBackupTime() {
        return backupTime;
    }

    public void setBackupTime(Timestamp backupTime) {
        this.backupTime = backupTime;
    }

    @Override
    public String toString() {
        return "Backup{" +
                "backupDataId=" + backupDataId +
                ", backupType=" + backupType +
                ", backupDataFileName='" + backupDataFileName + '\'' +
                ", backupTime=" + backupTime +
                '}';
    }
}
