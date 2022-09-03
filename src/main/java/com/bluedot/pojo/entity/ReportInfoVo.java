package com.bluedot.pojo.entity;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/8/29 17:05
 * @created: 实验报告详情vo
 */
public class ReportInfoVo {

    private String trainingSetData;
    private String testSetData;
    private Double rc2;
    private Double rmsec;
    private Double maec;
    private Double rp2;
    private Double rmsep;
    private Double maep;
    private Double rpd;

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
}
