package com.bluedot.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Jason
 * @CreationDate  2022/07/29 - 0:55
 * @Description ：
 */
public class Report {
    private Integer reportId;   //数据库自增
    private String reportTitle;     //用户输入
    private String reportMaterialName;  //用户输入

    private Integer pretreatmentAlgorithmId;    //用户选择

    private Integer reportDataModelId;  //用户选择

    private String reportResultModel;   //系统计算
    private byte[] reportTrainingSetGraph;  //系统画
    private byte[] reportTestSetGraph;      //系统画
    /**
     * 加上注解，让对象在被转化成JSON时（为了传给前段）按规定格式转化（默认会有毫秒）
     * 还有时区默认是GMT，咱们是东八区。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp reportCreateTime;     //系统设置
    /**
     * 加上注解，让对象在被转化成JSON时（为了传给前段）按规定格式转化（默认会有毫秒）
     * 还有时区默认是GMT，咱们是东八区。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp reportLastUpdateTime; //系统设置
    private User user;  //当前操作用户
    private String trainingSetData;     //系统设置
    private String testSetData;     //系统设置
    private Double rc2;     //系统计算
    private Double rmsec;   //系统计算
    private Double maec;    //系统计算
    private Double rp2;     //系统计算
    private Double rmsep;   //系统计算
    private Double maep;    //系统计算
    private Double rpd;     //系统计算
    private String reportDesc;  //用户输入



    public Integer getPretreatmentAlgorithmId() {
        return pretreatmentAlgorithmId;
    }

    public void setPretreatmentAlgorithmId(Integer pretreatmentAlgorithmId) {
        this.pretreatmentAlgorithmId = pretreatmentAlgorithmId;
    }

    public Integer getReportDataModelId() {
        return reportDataModelId;
    }

    public void setReportDataModelId(Integer reportDataModelId) {
        this.reportDataModelId = reportDataModelId;
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
    public Double[][] getTrainingSetDataAsDoubles() {
        return stringToDoublesArray(this.trainingSetData);
    }

    public void setTrainingSetData(String trainingSetData) {
        this.trainingSetData = trainingSetData;
    }
    public void setTrainingSetDataAsDouble(Double[][] trainingSetData) {
        this.trainingSetData = Arrays.deepToString(trainingSetData);
    }

    public String getTestSetData() {
        return testSetData;
    }
    public Double[][] getTestSetDataAsDoubles() {
        return stringToDoublesArray(this.testSetData);
    }

    public void setTestSetData(String testSetData) {
        this.testSetData = testSetData;
    }
    public void setTestSetDataAsDouble(Double[][] testSetData) {
        this.testSetData = Arrays.deepToString(testSetData);
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

    /**
     *  以map形式返回测试集的指标
     */
    public Map<String,String> getTestSetIndicator(){
        Map<String,String> map = new HashMap<>();
        map.put("rp2",this.rp2.toString());
        map.put("rmsep",this.rmsep.toString());
        map.put("maep",this.maep.toString());
        map.put("rpd",this.rpd.toString());
        return map;
    }

    /**
     * 以map形式返回测试集的指标
     */
    public Map<String,String> getTrainSetIndicator(){
        Map<String,String> map = new HashMap<>();
        map.put("rc2",this.rc2.toString());
        map.put("rmsec",this.rmsec.toString());
        map.put("maec",this.maec.toString());
        return map;
    }

    /**
     * 将字符串转换成二维Double数组
     * @param str 被转换的字符串
     * @return 转换后的Double二维数组
     */
    public static Double[][] stringToDoublesArray(String str){
        // str = [[25.1, 25.2], [25.3, 25.4], [25.5, 25.6]]
        System.out.println(str);
        str = str.replace("[[","");
        str = str.replace("]]","");
        String[] array = str.split("], \\[");
        Double[][] doubles = new Double[array.length][3];

        for (int i = 0; i < array.length; i++) {
            String[] split = array[i].split(", ");
            doubles[i][0] = Double.parseDouble(split[0]);
            doubles[i][1] = Double.parseDouble(split[1]);
        }

        return doubles;
    }
}
