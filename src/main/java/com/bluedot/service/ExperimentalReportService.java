package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.EntityInfo;
import com.bluedot.mapper.bean.Term;
import com.bluedot.mapper.bean.TermType;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.*;
import com.bluedot.pojo.vo.CommonResult;
import com.bluedot.utils.ImageUtil;
import com.bluedot.utils.ReflectUtil;
import org.apache.commons.fileupload.FileItem;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.*;

import static com.bluedot.pojo.vo.CommonResult.BUFFERED_IMAGE;
import static com.bluedot.pojo.vo.CommonResult.INPUT_STREAM_IMAGE;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/8/22 0:42
 * @created:
 */
public class ExperimentalReportService extends BaseService<Report>{
    public ExperimentalReportService(Data data) {
        super(data);
    }

    public ExperimentalReportService(HttpSession session, EntityInfo<?> entityInfo) {
        super(session, entityInfo);
    }

    @Override
    protected void doService() {

        String methodName = null;

        switch (operation){
            case "delete":
                if (isPersonal()){
                    methodName = "deletePersonalReport";
                }else {
                    methodName = "deleteReport";
                }
                break;
            case "update":
                if (isPersonal()){
                    methodName = "updatePersonalReport";
                }else {
                    methodName = "updateReport";
                }
                break;
            case "select":
                if (paramList.get("pageNo") != null || paramList.get("pageSize") != null){
                    if (isPersonal()){
                        methodName = "listPersonalReport";
                    }else {
                        methodName = "listReport";
                    }
                }else {
                    if (isPersonal()){
                        methodName = "getPersonalReportInfo";
                    }else {
                        methodName = "getReportInfo";
                    }
                }
                break;
            case "selectImage":
                if ("test".equals(paramList.get("imageType")) ) {
                    methodName = "getReportTestSetGraph";
                }else if ("train".equals(paramList.get("imageType")) ){
                    methodName = "getReportTrainingSetGraph";
                }
                break;
            default:
                throw new UserException(CommonErrorCode.E_5001);
        }
        invokeMethod(methodName,this);
    }

    /**
     * 判断数据是否是当前用户的数据
     */
    private boolean isPersonal(){
        String userEmail = (String) session.getAttribute("userEmail");
        // 处理的数据拥有者的userEmail
        String dataUserEmail = (String) paramList.get("userEmail");
        return userEmail.equals(dataUserEmail);
    }

    /**
     * 用户删除实验报告
     */
    private void deletePersonalReport(){
        paramList.put("userEmail",session.getAttribute("userEmail"));
        deleteReport();
    }

    /**
     * 管理员删除实验报告
     */
    private void deleteReport(){
        if (paramList.get("reportId") instanceof List){
            // list类型id，则删除多条数据
            List<Integer> listData = (List<Integer>)paramList.get("reportId");
            listData.forEach(data -> {
                Report report = new Report();
                report.setReportId(data);
                entityInfo.addEntity(report);
            });
        }else if (paramList.get("reportId") instanceof Integer){
            // int类型id，则删除一个
            Integer intData = (Integer) paramList.get("reportId");
            Report report = new Report();
            report.setReportId(intData);
            entityInfo.addEntity(report);
        }
        delete();
    }

    /**
     * 用户修改实验报告基本数据
     */
    private void updatePersonalReport(){
        updateReport();
    }

    /**
     * 修改实验报告基本数据
     */
    private void updateReport(){
        Report report = new Report();
        ReflectUtil.invokeSettersIncludeEntity(paramList,report);

        entityInfo.addEntity(report);
        update();
    }

    /**
     * 用户查询实验报告
     */
    private void listPersonalReport(){
        listReport();
    }

    /**
     * 管理员查询实验报告
     */
    private void listReport(){
        Condition condition = new Condition();
        condition.addView("report");
        condition.setReturnType("Report");

        condition.addFields("report_id");
        condition.addFields("report_title");
        condition.addFields("report_material_name");
        condition.addFields("pretreatment_algorithm_id");
        condition.addFields("report_data_model_id");
        condition.addFields("report_result_model");
        condition.addFields("user_email");
        condition.addFields("report_desc");
        condition.addFields("report_create_time");
        condition.addFields("report_last_update_time");

        // 分页
        if (paramList.containsKey("pageSize") && paramList.get("pageSize") != null){
            condition.setSize((Integer) paramList.get("pageSize"));
        }
        if (paramList.containsKey("pageNo") && paramList.get("pageNo") != null){
            condition.setStartIndex(((long)(int)paramList.get("pageNo")-1)*(int)paramList.get("pageSize"));
        }

        // 筛选条件:标题/物质名称
        if (paramList.get("reportTitle") != null){
            condition.addAndConditionWithView(new Term("report","report_title",paramList.get("reportTitle"),TermType.EQUAL));
        }
        if (paramList.get("reportMaterialName") != null){
            condition.addAndConditionWithView(new Term("report","report_material_name",paramList.get("reportMaterialName"),TermType.EQUAL));
        }

        // 用户筛选
        if (paramList.get("userEmail") != null){
            condition.addAndConditionWithView(new Term("report","user_email",paramList.get("userEmail"),TermType.EQUAL));
        }

        entityInfo.setCondition(condition);
        select();

        List<Report> reports = (List<Report>) commonResult.getData();
        List<ReportPageVo> reportPageVos = new ArrayList<>();
        for (Report report : reports) {
            Integer pretreatmentAlgorithmId = report.getPretreatmentAlgorithmId();
            Integer reportDataModelId = report.getReportDataModelId();


            Condition condition1 = new Condition();
            condition1.setReturnType("Algorithm");
            entityInfo.setCondition(condition1);

            //查reportDataModelId
            Map<String,Object> pretreatmentAlgorithmMap = new HashMap<>();
            pretreatmentAlgorithmMap.put("algorithmId",pretreatmentAlgorithmId);
            CommonResult commonResult1 = new AlgorithmService(session, entityInfo).doOtherService(pretreatmentAlgorithmMap,"select");
            String pretreatmentAlgorithmName = (String) ((List<Map<String,Object>>)commonResult1.getData()).get(0).get("algorithmName");

            //查pretreatmentAlgorithmId
            Map<String,Object> reportDataModelMap = new HashMap<>();
            reportDataModelMap.put("algorithmId",reportDataModelId);
            CommonResult commonResult2 = new AlgorithmService(session, entityInfo).doOtherService(reportDataModelMap,"select");
            String reportDataModelAlgorithmName = (String) ((List<Map<String,Object>>)commonResult2.getData()).get(0).get("algorithmName");


            ReportPageVo reportPageVo = new ReportPageVo();
            reportPageVo.setUserEmail(report.getUser().getUserEmail());
            reportPageVo.setReportDesc(report.getReportDesc());
            reportPageVo.setReportTitle(report.getReportTitle());
            reportPageVo.setReportCreateTime(report.getReportCreateTime());
            reportPageVo.setReportLastUpdateTime(report.getReportLastUpdateTime());
            reportPageVo.setReportResultModel(report.getReportResultModel());
            reportPageVo.setReportMaterialName(report.getReportMaterialName());
            reportPageVo.setReportId(report.getReportId());
            reportPageVo.setPretreatmentAlgorithmName(pretreatmentAlgorithmName);
            reportPageVo.setReportDataModelName(reportDataModelAlgorithmName);

            reportPageVos.add(reportPageVo);
        }

        commonResult.setData(reportPageVos);
    }


    /**
     * 查询实验报告详情信息
     */
    private void getPersonalReportInfo(){
        getReportInfo();
    }

    /**
     * 查询实验报告详情信息
     */
    private void getReportInfo(){
        Condition condition = new Condition();
        condition.addAndConditionWithView(new Term("report","report_id",paramList.get("reportId"), TermType.EQUAL));

        condition.setFieldsWithoutClasses(Report.class,byte[].class, User.class);
        condition.addFields("user_email");


        condition.setReturnType("Report");

        entityInfo.setCondition(condition);
        select();
    }


    /**
     * 获取实验报告测试集图片
     */
    private void getReportTestSetGraph(){
        Condition condition = new Condition();
        condition.addAndConditionWithView(new Term("report","report_id",paramList.get("reportId"), TermType.EQUAL));
        condition.addFields("report_test_set_graph");

        condition.setReturnType("Report");

        entityInfo.setCondition(condition);

        select();

        Report report = ((List<Report>)commonResult.getData()).get(0);
        byte[] reportTestSetGraph = report.getReportTestSetGraph();

        commonResult.setFileData("test.jpg",new ByteArrayInputStream(reportTestSetGraph));
        commonResult.setRespContentType(INPUT_STREAM_IMAGE);
    }

    /**
     * 获取实验报告训练集图片
     */
    private void getReportTrainingSetGraph(){
        Condition condition = new Condition();
        condition.addAndConditionWithView(new Term("report","report_id",paramList.get("reportId"), TermType.EQUAL));
        condition.addFields("report_training_set_graph");

        condition.setReturnType("Report");

        entityInfo.setCondition(condition);

        select();

        Report report = ((List<Report>)commonResult.getData()).get(0);
        byte[] reportTrainingSetGraph = report.getReportTrainingSetGraph();

        commonResult.setFileData("train.jpg",new ByteArrayInputStream(reportTrainingSetGraph));
        commonResult.setRespContentType(INPUT_STREAM_IMAGE);
    }


}
