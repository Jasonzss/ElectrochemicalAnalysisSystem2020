package com.bluedot.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;
import java.util.Arrays;

/**
 * @Author Jason
 * @CreationDate  2022/07/29 - 0:55
 * @Description ：
 */
public class Report {
    private Integer reportId;
    private String reportTitle;
    private String reportMaterialName;
    private Algorithm pretreatmentAlgorithm;
    private Algorithm reportDataModel;
    private String reportResultModel;
    private byte[] reportTrainingSetGraph;
    private byte[] reportTestSetGraph;
    /**
     * 加上注解，让对象在被转化成JSON时（为了传给前段）按规定格式转化（默认会有毫秒）
     * 还有时区默认是GMT，咱们是东八区。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp reportCreateTime;
    /**
     * 加上注解，让对象在被转化成JSON时（为了传给前段）按规定格式转化（默认会有毫秒）
     * 还有时区默认是GMT，咱们是东八区。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp reportLastUpdateTime;
    private User user;
    private String trainingSetData;
    private String testSetData;
    private Double rc2;
    private Double rmsec;
    private Double maec;
    private Double rp2;
    private Double rmsep;
    private Double maep;
    private Double rpd;
    private String reportDesc;

    public Report() {
        pretreatmentAlgorithm = new Algorithm();
        reportDataModel = new Algorithm();
        user = new User();
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

    public Algorithm getPretreatmentAlgorithm() {
        return pretreatmentAlgorithm;
    }

    public void setPretreatmentAlgorithm(Algorithm pretreatmentAlgorithm) {
        this.pretreatmentAlgorithm = pretreatmentAlgorithm;
    }

    public Algorithm getReportDataModel() {
        return reportDataModel;
    }

    public void setReportDataModel(Algorithm reportDataModel) {
        this.reportDataModel = reportDataModel;
    }

    public String getReportResultModel() {
        return reportResultModel;
    }

    public void setReportResultModel(String reportResultModel) {
        this.reportResultModel = reportResultModel;
    }

    public byte[] getReportTrainingSetGraph() {
        return reportTrainingSetGraph;
    }

    public void setReportTrainingSetGraph(byte[] reportTrainingSetGraph) {
        this.reportTrainingSetGraph = reportTrainingSetGraph;
    }

    public byte[] getReportTestSetGraph() {
        return reportTestSetGraph;
    }

    public void setReportTestSetGraph(byte[] reportTestSetGraph) {
        this.reportTestSetGraph = reportTestSetGraph;
    }

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTrainingSetData() {
        return trainingSetData;
    }

    public void setTrainingSetData(String trainingSetData) {
        this.trainingSetData = trainingSetData;
    }

    public String getTestSetData() {
        return testSetData;
    }

    public void setTestSetData(String testSetData) {
        this.testSetData = testSetData;
    }

    public Double getRc2() {
        return rc2;
    }

    public void setRc2(Double rc2) {
        this.rc2 = rc2;
    }

    public Double getRmsec() {
        return rmsec;
    }

    public void setRmsec(Double rmsec) {
        this.rmsec = rmsec;
    }

    public Double getMaec() {
        return maec;
    }

    public void setMaec(Double maec) {
        this.maec = maec;
    }

    public Double getRp2() {
        return rp2;
    }

    public void setRp2(Double rp2) {
        this.rp2 = rp2;
    }

    public Double getRmsep() {
        return rmsep;
    }

    public void setRmsep(Double rmsep) {
        this.rmsep = rmsep;
    }

    public Double getMaep() {
        return maep;
    }

    public void setMaep(Double maep) {
        this.maep = maep;
    }

    public Double getRpd() {
        return rpd;
    }

    public void setRpd(Double rpd) {
        this.rpd = rpd;
    }

    public String getReportDesc() {
        return reportDesc;
    }

    public void setReportDesc(String reportDesc) {
        this.reportDesc = reportDesc;
    }

    @Override
    public String toString() {
        return "Report{" +
                "reportId=" + reportId +
                ", reportTitle='" + reportTitle + '\'' +
                ", reportMaterialName='" + reportMaterialName + '\'' +
                ", pretreatmentAlgorithm=" + pretreatmentAlgorithm +
                ", reportDataModel=" + reportDataModel +
                ", reportResultModel='" + reportResultModel + '\'' +
                ", reportTrainingSetGraph=" + Arrays.toString(reportTrainingSetGraph) +
                ", reportTestSetGraph=" + Arrays.toString(reportTestSetGraph) +
                ", reportCreateTime=" + reportCreateTime +
                ", reportLastUpdateTime=" + reportLastUpdateTime +
                ", user=" + user +
                ", trainingSetData='" + trainingSetData + '\'' +
                ", testSetData='" + testSetData + '\'' +
                ", rc2=" + rc2 +
                ", rmsec=" + rmsec +
                ", maec=" + maec +
                ", rp2=" + rp2 +
                ", rmsep=" + rmsep +
                ", maep=" + maep +
                ", rpd=" + rpd +
                ", reportDesc='" + reportDesc + '\'' +
                '}';
    }
}
