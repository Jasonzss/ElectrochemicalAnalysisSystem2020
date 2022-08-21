package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.Term;
import com.bluedot.mapper.bean.TermType;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.ExpData;
import com.bluedot.utils.ReflectUtil;

import java.util.ArrayList;
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
            expDataList = (List<Map<String, Object>>) paramList.get("expData");
            for (Map<String,Object> map:expDataList) {
                if (map.get("userEmail").equals(userEmail)){
                    isPersonal = false;
                }
            }
        }
        if (paramList.containsKey("userEmail")){
            if (!paramList.get("userEmail").equals(userEmail)){
                isPersonal = false;
            }
        }


        String methodName;
        switch (operation) {
            case "delete":
                if (isPersonal){
                    methodName = "deletePersonaExpData";
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
                    methodName = "listPersonalExpData";
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

    /**
     * 查看实验员个人的实验数据
     */
    private void listPersonalExpData(){
        Condition condition = new Condition();

        if (paramList.containsKey("pageSize")){
            condition.setSize((Integer) paramList.get("pageSize"));
        }
        if (paramList.containsKey("pageNo")){
            condition.setStartIndex(((long)paramList.get("pageNo")-1)*(int)paramList.get("pageSize"));
        }
        if (paramList.containsKey("expMaterialName")){
            condition.addOrConditionWithView(new Term("expData","expMaterialName",paramList.get("expMaterialName"), TermType.EQUAL));
        }
        if (paramList.containsKey("materialTypeId")){
            condition.addOrConditionWithView(new Term("expData","materialTypeId",paramList.get("materialType"),TermType.EQUAL));
        }
        if (paramList.containsKey("expDeleteStatus")){
            condition.addAndConditionWithView(new Term("expData","expDeleteStatus",paramList.get("expDeleteStatus"),TermType.EQUAL));
        }
        if (paramList.containsKey("userEmail")){
            condition.addAndConditionWithView(new Term("expData","userEmail",paramList.get("userEmail"),TermType.EQUAL));
        }

        entityInfo.setCondition(condition);
//        entityInfo.setEntityName("expData");
        select();
    }

    /**
     * 管理员权限的查看数据
     */
    private void listExpData(){
        Condition condition = new Condition();
        if (paramList.containsKey("pageSize")){
            condition.setSize((Integer) paramList.get("pageSize"));
        }
        if (paramList.containsKey("pageNo")){
            condition.setStartIndex(((long)paramList.get("pageNo")-1)*(int)paramList.get("pageSize"));
        }
        if (paramList.containsKey("expMaterialName")){
            condition.addOrConditionWithView(new Term("expData","expMaterialName",paramList.get("expMaterialName"), TermType.LIKE));
        }
        if (paramList.containsKey("materialTypeId")){
            condition.addOrConditionWithView(new Term("expData","materialTypeId",paramList.get("materialType"),TermType.EQUAL));
        }
        if (paramList.containsKey("expDeleteStatus")){
            condition.addAndConditionWithView(new Term("expData","expDeleteStatus",paramList.get("expDeleteStatus"),TermType.EQUAL));
        }

        entityInfo.setCondition(condition);
//        entityInfo.setEntityName("expData");
        select();
    }

    /**
     * 列出实验数据表中所有的物质名称
     */
    private void listMaterialName(){
        Condition condition = new Condition();
        condition.addFields("distinct(expMaterialName)");

        if (paramList.containsKey("userEmail")){
            condition.addAndConditionWithView(new Term("expData","userEmail",paramList.get("userEmail"),TermType.EQUAL));
        }

        entityInfo.setCondition(condition);
        select();
    }

    /**
     * 管理员权限的修改实验数据
     */
    private void updateExpData(){
        ExpData expData = new ExpData();
        ReflectUtil.invokeSetters(paramList,expData);
        entityInfo.addEntity(expData);
        update();
    }

    /**
     * 修改个人的实验数据
     */
    private void updatePersonalExpData(){
        ExpData expData = new ExpData();
        ReflectUtil.invokeSetters(paramList,expData);
        entityInfo.addEntity(expData);
        update();
    }

    /**
     * 删除个人的实验数据
     */
    private void deletePersonaExpData(){
        //获取删除的实验数据
        List<Map<String,Object>> data = new ArrayList<>();
        if (paramList.get("expData") instanceof List){
            data = (List<Map<String, Object>>) paramList.get("expData");
        }

        for(Map<String,Object> map:data){
            ExpData expData = new ExpData();
            ReflectUtil.invokeSetters(paramList,expData);
            entityInfo.addEntity(expData);
        }

        delete();
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
            ReflectUtil.invokeSetters(paramList,expData);
            entityInfo.addEntity(expData);
        }

        delete();
    }

    /**
     * 下载实验数据，下载格式为 ？
     */
    private void downloadExpData(){

    }

    /**
     * 下载实验数据，下载格式为 ？
     */
    private void downloadPersonalExpData(){

    }
}
