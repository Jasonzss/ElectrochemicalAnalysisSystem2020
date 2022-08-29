package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.EntityInfo;
import com.bluedot.mapper.bean.Term;
import com.bluedot.mapper.bean.TermType;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.Application;
import com.bluedot.pojo.entity.Report;
import com.bluedot.utils.ReflectUtil;
import org.apache.commons.fileupload.FileItem;

import javax.servlet.http.HttpSession;
import java.util.List;

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
            case "insert":
                methodName = "createReport";
                break;
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
     * 保存实验报告
     */
    private void createReport(){
        // 将数据填充到实体类中
        Report report = new Report();
        ReflectUtil.invokeSetters(paramList,report);

        entityInfo.addEntity(report);
        insert();
    }

    /**
     * 用户删除实验报告
     */
    private void deletePersonalReport(){
        paramList.put("userEmail",session.getAttribute("userEmail"));
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
        Report report = new Report();
        ReflectUtil.invokeSetters(paramList,report);

        entityInfo.addEntity(report);
        update();
    }

    /**
     * 修改实验报告基本数据
     */
    private void updateReport(){
        Report report = new Report();
        ReflectUtil.invokeSetters(paramList,report);

        entityInfo.addEntity(report);
        update();
    }

    /**
     * 用户查询实验报告
     */
    private void listPersonalReport(){
        Condition condition = new Condition();

        // 分页
        if (paramList.containsKey("pageSize") && paramList.get("pageSize") != null){
            condition.setSize((Integer) paramList.get("pageSize"));
        }
        if (paramList.containsKey("pageNo") && paramList.get("pageNo") != null){
            condition.setStartIndex(((long)paramList.get("pageNo")-1)*(int)paramList.get("pageSize"));
        }

        // 筛选条件:标题/物质名称
        if (paramList.get("reportTitle") != null){
            condition.addAndConditionWithView(new Term("report","report_title",paramList.get("reportTitle"),TermType.EQUAL));
        }
        if (paramList.get("reportMaterialName") != null){
            condition.addAndConditionWithView(new Term("report","report_material_name",paramList.get("reportMaterialName"),TermType.EQUAL));
        }

        // 用户查询
        if (paramList.containsKey("userEmail")){
            condition.addAndConditionWithView(new Term("report","user_email",paramList.get("userEmail"),TermType.EQUAL));
        }

        entityInfo.setCondition(condition);
        select();
    }

    /**
     * 管理员查询实验报告
     */
    private void listReport(){
        Condition condition = new Condition();

        // 分页
        if (paramList.containsKey("pageSize") && paramList.get("pageSize") != null){
            condition.setSize((Integer) paramList.get("pageSize"));
        }
        if (paramList.containsKey("pageNo") && paramList.get("pageNo") != null){
            condition.setStartIndex(((long)paramList.get("pageNo")-1)*(int)paramList.get("pageSize"));
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
    }


    /**
     * 查询实验报告详情信息
     */
    private void getPersonalReportInfo(){
        Condition condition = new Condition();
        condition.addAndConditionWithView(new Term("report","report_id",paramList.get("reportId"), TermType.EQUAL));

        entityInfo.setCondition(condition);
        select();
    }

    /**
     * 查询实验报告详情信息
     */
    private void getReportInfo(){
        Condition condition = new Condition();
        condition.addAndConditionWithView(new Term("report","report_id",paramList.get("reportId"), TermType.EQUAL));

        entityInfo.setCondition(condition);
        select();
    }

}
