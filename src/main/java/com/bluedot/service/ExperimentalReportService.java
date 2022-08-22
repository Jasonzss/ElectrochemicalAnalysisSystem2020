package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.Term;
import com.bluedot.mapper.bean.TermType;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.Application;
import com.bluedot.pojo.entity.Report;
import com.bluedot.utils.ReflectUtil;

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

    @Override
    protected void doService() {

        String methodName = null;

        switch (operation){
            case "insert":
                methodName = "saveReport";
                break;
            case "delete":
                methodName = "deleteReport";
                break;
            case "update":
                methodName = "updateReport";
                break;
            case "select":
                if (paramList.get("pageNo") != null || paramList.get("pageSize") != null){
                    methodName ="listReport";
                }else {
                    methodName = "getReportInfo";
                }
                break;
            case "download":
                methodName = "downloadReport";
                break;
            default:
                throw new UserException(CommonErrorCode.E_5001);
        }
    }

    private boolean isPersonal() {
        String userEmail = (String) session.getAttribute("userEmail");
        String requestUserEmail = (String) paramList.get("userEmail");
        return userEmail.equals(requestUserEmail);
    }

    /**
     * 保存实验报告
     */
    private void saveReport(){

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
     * 管理员修改实验报告
     */
    private void updateReport(){
        Report report = new Report();
        ReflectUtil.invokeSetters(paramList,report);

        entityInfo.addEntity(report);
        update();
    }


    /**
     * 查询实验报告
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
        if (paramList.containsKey("reportTitle") && paramList.get("reportTitle") != null){
            condition.addAndConditionWithView(new Term("report","report_title",paramList.get("reportTitle"),TermType.EQUAL));
        }
        if (paramList.containsKey("reportMaterialName") && paramList.get("reportMaterialName") != null){
            condition.addAndConditionWithView(new Term("report","report_material_name",paramList.get("reportMaterialName"),TermType.EQUAL));
        }

        // 用户查询/管理员查询
        if (paramList.containsKey("userEmail") && paramList.get("userEmail") != null){
            condition.addAndConditionWithView(new Term("report","user_email",paramList.get("userEmail"),TermType.EQUAL));
        }

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

    /**
     * 下载实验报告
     */
    private void downloadReport(){

    }

}
