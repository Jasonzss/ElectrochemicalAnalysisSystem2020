package com.bluedot.pojo.entity;

import java.util.Date;

/**
 * @Author Jason
 * @CreationDate 2022/07/29 - 0:56
 * @Description ：
 */
public class Backup {
    private Integer backupDataId;
    private Integer backupType;
    private String backupDataFileName;
    private Date backupTime;

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

    public Date getBackupTime() {
        return backupTime;
    }

    public void setBackupTime(Date backupTime) {
        this.backupTime = backupTime;
    }
}
