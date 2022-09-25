package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.EntityInfo;
import com.bluedot.mapper.bean.Term;
import com.bluedot.mapper.bean.TermType;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.ExpData;
import com.bluedot.pojo.vo.CommonResult;
import com.bluedot.utils.ExcelUtil;
import com.bluedot.utils.ReflectUtil;
import com.bluedot.utils.constants.OperationConstants;
import com.bluedot.utils.constants.SessionConstants;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Jason
 * @CreationDate 2022/08/19 - 17:33
 * @Description ：
 */
public class ExperimentalDataService extends BaseService<ExpData>{

    public ExperimentalDataService(Data data) {
        super(data);
    }

    public ExperimentalDataService(HttpSession session, EntityInfo<?> entityInfo) {
        super(session, entityInfo);
    }

    /**
     * 负责在ExperimentalService中个根据父类属性分析调用哪些方法来解决请求
     */
    @Override
    protected void doService() {
        String userEmail = (String) session.getAttribute("userEmail");
        List<Map<String,Object>> expDataList = null;

        //判断操作的实验数据是个人管理还是管理员管理
        boolean isPersonal = true;
        if (paramList.get("expData") instanceof List){
            //操作多条数据，将多条数据取出
            expDataList = (List<Map<String, Object>>) paramList.get("expData");
            //判断每条数据是个人的还是他人的
            for (Map<String,Object> map:expDataList) {
                if (!map.containsKey("userEmail") || !map.get("userEmail").equals(userEmail)){
                    isPersonal = false;
                }
            }
        }

        if (paramList.containsKey("userEmail")){
            if (!paramList.containsKey("userEmail") || !paramList.get("userEmail").equals(userEmail)){
                isPersonal = false;
            }
        }
        if(operation.equals(OperationConstants.SELECT)){
            isPersonal = !paramList.containsKey("userEmail");
        }


        String methodName;
        switch (operation) {
            case "delete":
                if (isPersonal){
                    methodName = "deletePersonalExpData";
                }else {
                    methodName = "deleteExpData";
                }
                break;
            case "update":
                if (isPersonal){
                    methodName = "updatePersonalExpData";
                }else {
                    methodName = "updateExpData";
                }
                break;
            case "select":
                if (isPersonal){
                    methodName = "listPersonalExpData";
                }else {
                    methodName = "listExpData";
                }
                break;
            case "distinct":
                if (paramList.containsKey("expMaterialName")){
                    methodName = "listMaterialName";
                }else {
                    throw new UserException(CommonErrorCode.E_4001);
                }
                break;
            case "download":
                if (isPersonal){
                    methodName = "downloadPersonalExpData";
                }else {
                    methodName = "downloadExpData";
                }
                break;
            default:
                throw new UserException(CommonErrorCode.E_4001);
        }

        invokeMethod(methodName,this);


    }

    @Override
    protected boolean check() {
        return true;
    }

    /**
     * 查看实验员个人的实验数据
     */
    private void listPersonalExpData(){
        Condition condition = new Condition();

        //设置查询返回类型
        condition.setReturnType("ExpData");
        entityInfo.setCondition(setConditionParam(condition));

        //设置查询条件
        if (paramList.containsKey("pageSize") && paramList.containsKey("pageNo")){
            selectPage();
        }else if(paramList.containsKey("expData")){
            select();
        }
    }

    /**
     * 管理员权限的查看数据
     */
    private void listExpData(){
        Condition condition = new Condition();
        //设置查询返回类型
        condition.setReturnType("ExpData");
        entityInfo.setCondition(setConditionParam(condition));

        //设置查询条件
        if (paramList.containsKey("pageSize") && paramList.containsKey("pageNo")){
            selectPage();
        }else if(paramList.containsKey("expData")){
            select();
        }
    }

    /**
     * 对实验数据查询方法的对应属性以对应的条件放入Condition中
     * @param condition 查询条件放入的Condition
     * @return 完成条件注入的Condition
     */
    private Condition setConditionParam(Condition condition){
        //设置查询条件
        if (paramList.containsKey("pageSize") && paramList.containsKey("pageNo")){
            //分页查询
            condition.setSize((Integer) paramList.get("pageSize"));
            condition.setStartIndex((Long.valueOf(((Integer)paramList.get("pageNo")).longValue())-1)*(int)paramList.get("pageSize"));

            //设置查询条件
            if (paramList.containsKey("userEmail")){
                condition.addAndConditionWithView(new Term("exp_data","user_email",paramList.get("userEmail"),TermType.EQUAL));
            }
            if (paramList.containsKey("expMaterialName")){
                condition.addAndConditionWithView(new Term("exp_data","exp_material_name",paramList.get("expMaterialName"), TermType.LIKE));
            }
            if (paramList.containsKey("materialTypeId")){
                condition.addAndConditionWithView(new Term("exp_data","material_type_id",paramList.get("materialTypeId"),TermType.EQUAL));
            }
            if (paramList.containsKey("expDeleteStatus")){
                condition.addAndConditionWithView(new Term("exp_data","exp_delete_status",paramList.get("expDeleteStatus"),TermType.EQUAL));
            }
            if (paramList.containsKey("expCreateTimeStart")){
                condition.addAndConditionWithView(new Term("exp_data","exp_create_time",paramList.get("expCreateTimeStart"),TermType.GREATER));
            }
            if (paramList.containsKey("expCreateTimeEnd")){
                condition.addAndConditionWithView(new Term("exp_data","exp_create_time",paramList.get("expCreateTimeEnd"),TermType.Less));
            }
            if (paramList.containsKey("expLastUpdateTimeStart")){
                condition.addAndConditionWithView(new Term("exp_data","exp_last_update_time",paramList.get("expLastUpdateTimeStart"),TermType.GREATER));
            }
            if (paramList.containsKey("expLastUpdateTimeEnd")){
                condition.addAndConditionWithView(new Term("exp_data","exp_last_update_time",paramList.get("expLastUpdateTimeEnd"),TermType.Less));
            }
        }else if(paramList.containsKey("expData")){
            //指定id查询
            List<Map<String, Object>> objectList = (List<Map<String, Object>>) paramList.get("expData");
            //将所有需要查询的expData的id放入list中
            List<Object> list = new ArrayList<>();
            objectList.forEach((l) -> {
                list.add(l.get("expDataId"));
            });
            condition.addAndConditionWithView(new Term("exp_data","exp_data_id",list,TermType.IN));
        }

        return condition;
    }

    /**
     * 列出实验数据表中所有的物质名称
     */
    private void listMaterialName(){
        Condition condition = new Condition();
        condition.addFields("DISTINCT(exp_material_name)");
        condition.addView("exp_data");
        condition.setReturnType("ExpData");

        if (paramList.containsKey("userEmail")){
            condition.addOrConditionWithView(new Term("exp_data","user_email",paramList.get("userEmail"),TermType.EQUAL));
        }

        entityInfo.setCondition(condition);
        select();
    }

    /**
     * 管理员权限的修改实验数据
     */
    private void updateExpData(){
        if(paramList.containsKey("expData")){
            //批量操作数据
            List<Map<String,Object>> expDataMapList = (List<Map<String, Object>>) paramList.get("expData");
            expDataMapList.forEach((map) -> {
                ExpData expData = new ExpData();
                ReflectUtil.invokeSettersIncludeEntity(map,expData);
                entityInfo.addEntity(expData);
            });
        }else {
            //操作单个数据
            ExpData expData = new ExpData();
            ReflectUtil.invokeSettersIncludeEntity(paramList,expData);
            entityInfo.addEntity(expData);
        }
        update();
        int data = (int) commonResult.getData();
        commonResult = CommonResult.successResult("修改实验数据"+data+"条",true);
    }

    /**
     * 修改个人的实验数据
     */
    private void updatePersonalExpData(){
        if(paramList.containsKey("expData")){
            //批量操作数据
            List<Map<String,Object>> expDataMapList = (List<Map<String, Object>>) paramList.get("expData");
            expDataMapList.forEach((map) -> {
                ExpData expData = new ExpData();
                ReflectUtil.invokeSettersIncludeEntity(map,expData);
                entityInfo.addEntity(expData);
            });
        }else {
            //操作单个数据
            ExpData expData = new ExpData();
            ReflectUtil.invokeSettersIncludeEntity(paramList,expData);
            entityInfo.addEntity(expData);
        }
        update();
        int data = (int) commonResult.getData();
        commonResult = CommonResult.successResult("修改实验数据"+data+"条",true);
    }

    /**
     * 删除个人的实验数据
     */
    private void deletePersonalExpData(){
        //获取删除的实验数据
        List<Map<String,Object>> data = new ArrayList<>();
        if (paramList.get("expData") instanceof List){
            data = (List<Map<String, Object>>) paramList.get("expData");
        }

        for(Map<String,Object> map:data){
            map.put("userEmail",session.getAttribute("userEmail"));
            ExpData expData = new ExpData();
            ReflectUtil.invokeSettersIncludeEntity(map,expData);
            entityInfo.addEntity(expData);
        }

        delete();
        int deleteNum = (int) commonResult.getData();
        commonResult = CommonResult.successResult("修改实验数据"+deleteNum+"条",true);
    }

    /**
     * 管理员权限的删除数据
     */
    private void deleteExpData(){
        //获取删除的实验数据
        List<Map<String,Object>> data = new ArrayList<>();
        if (paramList.get("expData") instanceof List){
            data = (List<Map<String, Object>>) paramList.get("expData");
        }

        for(Map<String,Object> map:data){
            ExpData expData = new ExpData();
            ReflectUtil.invokeSettersIncludeEntity(map,expData);
            entityInfo.addEntity(expData);
        }

        delete();
        int deleteNum = (int) commonResult.getData();
        commonResult = CommonResult.successResult("修改实验数据"+deleteNum+"条",true);
    }

    /**
     * 下载实验数据，下载格式为 excel
     */
    private void downloadExpData(){
        try{
            //查询用户所需数据
            listExpData();

        }catch (Exception e){
            if (e instanceof UserException){
                throw e;
            }else {
                throw new UserException(CommonErrorCode.E_6001);
            }
        }
        List<ExpData> expDataList = (List<ExpData>) commonResult.getData();
        Map<String,ExpData> expDataMap = new HashMap<>();
        for (int i = 0; i < expDataList.size(); i++) {
            expDataMap.put("sheet"+(i+1),expDataList.get(i));
        }

        //生成excel对象
        HSSFWorkbook hssfWorkbook = ExcelUtil.productExpDataExcel(expDataMap);

        commonResult.setFileData("实验数据.xls",hssfWorkbook);
        commonResult.setRespContentType(CommonResult.EXCEL);
    }

    /**
     * 下载实验数据，下载格式为 excel
     */
    private void downloadPersonalExpData(){
        try{
            //查询用户所需数据
            listPersonalExpData();
        }catch (Exception e){
            if (e instanceof UserException){
                throw e;
            }else {
                throw new UserException(CommonErrorCode.E_6001);
            }
        }

        List<ExpData> expDataList = (List<ExpData>) commonResult.getData();
        Map<String,ExpData> expDataMap = new HashMap<>();
        for (int i = 0; i < expDataList.size(); i++) {
            expDataMap.put("sheet"+(i+1),expDataList.get(i));
        }

        //生成excel对象
        HSSFWorkbook hssfWorkbook = ExcelUtil.productExpDataExcel(expDataMap);

        commonResult.setFileData("实验数据.xls",hssfWorkbook);
        commonResult.setRespContentType(CommonResult.EXCEL);
    }
}
