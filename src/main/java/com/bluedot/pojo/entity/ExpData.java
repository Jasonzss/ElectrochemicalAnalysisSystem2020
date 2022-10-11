package com.bluedot.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author Jason
 * @CreationDate 2022/07/29 - 055
 * @Description ：
 */
public class ExpData {
    private Integer expDataId;  //系统提供
    private User user;  //系统提供
    private MaterialType materialType;  //用户选择
    private String expMaterialName; //用户选择或填写
    private Double expMaterialSolubility;   //用户填写
    private Double[] expPotentialPointData;   //用户上传文件读取
    private Double[] expOriginalCurrentPointData ;  //用户上传文件读取
    private Double[] expNewestCurrentPointData ;    //系统计算
    private Double expOriginalCurrent;  //系统计算
    private Double expOriginalPotential;    //系统计算
    private Double expNewestCurrent;    //系统计算
    private Double expNewestPotential;  //系统计算
    private BufferSolution bufferSolution;  //用户选择
    private Double expPh;   //用户填写
    private String expDataDesc; //用户填写
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp expCreateTime;    //系统提供
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp expLastUpdateTime;    //系统提供
    private Integer expDeleteStatus;    //系统提供

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

    public Double[] getExpPotentialPointDataAsDouble() {
        return expPotentialPointData;
    }
    public String getExpPotentialPointData() {
        return Arrays.toString(expPotentialPointData);
    }

    public void setExpPotentialPointData(Double[] expPotentialPointData) {
        this.expPotentialPointData = expPotentialPointData;
    }
    public void setExpPotentialPointData(String expPotentialPointData) {
        this.expPotentialPointData = stringToDoubleArray(expPotentialPointData);
    }

    public Double[] getExpOriginalCurrentPointDataAsDouble() {
        return expOriginalCurrentPointData;
    }
    public String getExpOriginalCurrentPointData() {
        return Arrays.toString(expOriginalCurrentPointData);
    }

    public void setExpOriginalCurrentPointData(Double[] expOriginalCurrentPointData) {
        this.expOriginalCurrentPointData = expOriginalCurrentPointData;
    }
    public void setExpOriginalCurrentPointData(String expOriginalCurrentPointData) {
        this.expOriginalCurrentPointData = stringToDoubleArray(expOriginalCurrentPointData);
    }

    public Double[] getExpNewestCurrentPointDataAsDouble() {
        return expNewestCurrentPointData;
    }
    public String getExpNewestCurrentPointData() {
        return Arrays.toString(expNewestCurrentPointData);
    }

    public void setExpNewestCurrentPointData(Double[] expNewestCurrentPointData) {
        this.expNewestCurrentPointData = expNewestCurrentPointData;
    }
    public void setExpNewestCurrentPointData(String expNewestCurrentPointData) {
        this.expNewestCurrentPointData = stringToDoubleArray(expNewestCurrentPointData);
    }

    public Double getExpOriginalCurrent() {
        return expOriginalCurrent;
    }
    public String getExpOriginalCurrentAsScientificNotation(){
        return simplifyScientificNotationDouble(expOriginalCurrent);
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
    public String getExpNewestCurrentAsScientificNotation(){
        return simplifyScientificNotationDouble(expNewestCurrent);
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

    public static Double[] stringToDoubleArray(String str){
        List<Double> list = new ArrayList<>();
        str = str.replace("[","");
        str = str.replace("]","");
        String[] array = str.split(",");
        Double[] doubles = new Double[array.length];

        for (int i = 0; i < array.length; i++) {
            doubles[i] = Double.parseDouble(array[i]);
        }

        return doubles;
    }

    @Override
    public String toString() {
        return "ExpData{" +
                "expDataId=" + expDataId +
                ", user=" + user +
                ", materialType=" + materialType +
                ", expMaterialName='" + expMaterialName + '\'' +
                ", expMaterialSolubility=" + expMaterialSolubility +
                ", expPotentialPointData=" + Arrays.toString(expPotentialPointData) +
                ", expOriginalCurrentPointData=" + Arrays.toString(expOriginalCurrentPointData) +
                ", expNewestCurrentPointData=" + Arrays.toString(expNewestCurrentPointData) +
                ", expOriginalCurrent=" + expOriginalCurrent +
                ", expOriginalPotential=" + expOriginalPotential +
                ", expNewestCurrent=" + expNewestCurrent +
                ", expNewestPotential=" + expNewestPotential +
                ", bufferSolution=" + bufferSolution +
                ", expPh=" + expPh +
                ", expDataDesc='" + expDataDesc + '\'' +
                ", expCreateTime=" + expCreateTime +
                ", expLastUpdateTime=" + expLastUpdateTime +
                ", expDeleteStatus=" + expDeleteStatus +
                '}';
    }

    /**
     * 将科学计数法的数字进行化简，小数点后面只保留三位有效数字
     * @param doubleNum 待处理的数字
     * @return 简化处理后的数字
     */
    public String simplifyScientificNotationDouble(Double doubleNum){
        if (doubleNum == null){
            return null;
        }

        String d = doubleNum.toString();
        if (!d.contains("E")){
            return d;
        }

        // 25.2E-7  25.2E5  0.01222E-7  0.0001E10
        // 8E2 8E-3  888888E10  77777E-10

        if (d.contains(".")){
            int dotIndex = d.indexOf('.');
            int eIndex = d.indexOf('E');

            if (eIndex - dotIndex > 3){
                return d.substring(0,dotIndex+4)+d.substring(eIndex);
            }
        }
        return d;
    }
}
