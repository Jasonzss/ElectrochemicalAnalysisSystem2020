package com.bluedot.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/8/29 16:53
 * @created: 实验报告分页vo
 */
public class ReportPageVo {

    private Integer reportId;
    private String reportTitle;
    private String reportMaterialName;
    private String pretreatmentAlgorithmName;
    private String reportDataModelName;
    private String reportResultModel;
    private String userEmail;
    private String reportDesc;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp reportCreateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp reportLastUpdateTime;

    public Timestamp getReportCreateTime() {
        return reportCreateTime;
    }

    public void setReportCreateTime(Timestamp reportCreateTime) {
        this.reportCreateTime = reportCreateTime;
    }

    public Timestamp getReportLastUpdateTime() {
        return reportLastUpdateTime;
    }

    public void setReportLastUpdateTime(Timestamp reportLastUpdateTime) {
        this.reportLastUpdateTime = reportLastUpdateTime;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public String getReportMaterialName() {
        return reportMaterialName;
    }

    public void setReportMaterialName(String reportMaterialName) {
        this.reportMaterialName = reportMaterialName;
    }

    public String getPretreatmentAlgorithmName() {
        return pretreatmentAlgorithmName;
    }

    public void setPretreatmentAlgorithmName(String pretreatmentAlgorithmName) {
        this.pretreatmentAlgorithmName = pretreatmentAlgorithmName;
    }

    public String getReportDataModelName() {
        return reportDataModelName;
    }

    public void setReportDataModelName(String reportDataModelName) {
        this.reportDataModelName = reportDataModelName;
    }

    public String getReportResultModel() {
        return reportResultModel;
    }

    public void setReportResultModel(String reportResultModel) {
        this.reportResultModel = reportResultModel;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getReportDesc() {
        return reportDesc;
    }

    public void setReportDesc(String reportDesc) {
        this.reportDesc = reportDesc;
    }

}
