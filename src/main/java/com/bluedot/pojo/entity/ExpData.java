package com.fuck.pojo.entity;

import java.sql.Timestamp;

/**
 * @Author Jason
 * @CreationDate 2022/07/29 - 055
 * @Description ：
 */
public class ExpData {
    private Integer expDataId;
    private User user;
    private MaterialType materialType;
    private String expMaterialName;
    private Double expMaterialSolubility;
    private Double[][] expOriginalPointData ;
    private Double[][] expNewestPointData ;
    private Double expOriginalCurrent;
    private Double expOriginalPotential;
    private Double expNewestCurrent;
    private Double expNewestPotential;
    private BufferSolution bufferSolution;
    private Double expPh;
    private String expDataDesc;
    private Timestamp expCreateTime;
    private Timestamp expLastUpdateTime;
    private Integer expDeleteStatus;

    public Integer getExpDataId() {
        return expDataId;
    }

    public void setExpDataId(Integer expDataId) {
        this.expDataId = expDataId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MaterialType getMaterialType() {
        return materialType;
    }

    public void setMaterialType(MaterialType materialType) {
        this.materialType = materialType;
    }

    public String getExpMaterialName() {
        return expMaterialName;
    }

    public void setExpMaterialName(String expMaterialName) {
        this.expMaterialName = expMaterialName;
    }

    public Double getExpMaterialSolubility() {
        return expMaterialSolubility;
    }

    public void setExpMaterialSolubility(Double expMaterialSolubility) {
        this.expMaterialSolubility = expMaterialSolubility;
    }

    public Double[][] getExpOriginalPointData() {
        return expOriginalPointData;
    }

    public void setExpOriginalPointData(Double[][] expOriginalPointData) {
        this.expOriginalPointData = expOriginalPointData;
    }

    public Double[][] getExpNewestPointData() {
        return expNewestPointData;
    }

    public void setExpNewestPointData(Double[][] expNewestPointData) {
        this.expNewestPointData = expNewestPointData;
    }

    public Double getExpOriginalCurrent() {
        return expOriginalCurrent;
    }

    public void setExpOriginalCurrent(Double expOriginalCurrent) {
        this.expOriginalCurrent = expOriginalCurrent;
    }

    public Double getExpOriginalPotential() {
        return expOriginalPotential;
    }

    public void setExpOriginalPotential(Double expOriginalPotential) {
        this.expOriginalPotential = expOriginalPotential;
    }

    public Double getExpNewestCurrent() {
        return expNewestCurrent;
    }

    public void setExpNewestCurrent(Double expNewestCurrent) {
        this.expNewestCurrent = expNewestCurrent;
    }

    public Double getExpNewestPotential() {
        return expNewestPotential;
    }

    public void setExpNewestPotential(Double expNewestPotential) {
        this.expNewestPotential = expNewestPotential;
    }

    public BufferSolution getBufferSolution() {
        return bufferSolution;
    }

    public void setBufferSolution(BufferSolution bufferSolution) {
        this.bufferSolution = bufferSolution;
    }

    public Double getExpPh() {
        return expPh;
    }

    public void setExpPh(Double expPh) {
        this.expPh = expPh;
    }

    public String getExpDataDesc() {
        return expDataDesc;
    }

    public void setExpDataDesc(String expDataDesc) {
        this.expDataDesc = expDataDesc;
    }

    public Timestamp getExpCreateTime() {
        return expCreateTime;
    }

    public void setExpCreateTime(Timestamp expCreateTime) {
        this.expCreateTime = expCreateTime;
    }

    public Timestamp getExpLastUpdateTime() {
        return expLastUpdateTime;
    }

    public void setExpLastUpdateTime(Timestamp expLastUpdateTime) {
        this.expLastUpdateTime = expLastUpdateTime;
    }

    public Integer getExpDeleteStatus() {
        return expDeleteStatus;
    }

    public void setExpDeleteStatus(Integer expDeleteStatus) {
        this.expDeleteStatus = expDeleteStatus;
    }
}
