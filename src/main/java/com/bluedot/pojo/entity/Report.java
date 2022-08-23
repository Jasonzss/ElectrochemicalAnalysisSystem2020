package com.bluedot.pojo.entity;

import java.util.Arrays;
import java.util.Date;

/**
 * @Author Jason
 * @CreationDate 2022/07/29 - 0:55
 * @Description ：
 */
public class Report {
    private Integer reportId;
    private String reportTitle;
    private String reportMaterialName;
    private Algorithm pretreatmentAlgorithm;
    private Algorithm reportDataModel;
    private String reportResultModel;
    private Byte[] reportTrainingSetGraph;
    private Byte[] reportTestSetGraph;
    private Date reportCreateTime;
    private Date reportLastUpdateTime;
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

    public Byte[] getReportTrainingSetGraph() {
        return reportTrainingSetGraph;
    }

    public void setReportTrainingSetGraph(Byte[] reportTrainingSetGraph) {
        this.reportTrainingSetGraph = reportTrainingSetGraph;
    }

    public Byte[] getReportTestSetGraph() {
        return reportTestSetGraph;
    }

    public void setReportTestSetGraph(Byte[] reportTestSetGraph) {
        this.reportTestSetGraph = reportTestSetGraph;
    }

    public Date getReportCreateTime() {
        return reportCreateTime;
    }

    public void setReportCreateTime(Date reportCreateTime) {
        this.reportCreateTime = reportCreateTime;
    }

    public Date getReportLastUpdateTime() {
        return reportLastUpdateTime;
    }

    public void setReportLastUpdateTime(Date reportLastUpdateTime) {
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
