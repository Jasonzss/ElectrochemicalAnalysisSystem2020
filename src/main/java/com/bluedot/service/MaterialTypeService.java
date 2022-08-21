package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.Term;
import com.bluedot.mapper.bean.TermType;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.Application;
import com.bluedot.pojo.entity.MaterialType;
import com.bluedot.pojo.entity.User;
import com.bluedot.utils.ReflectUtil;
import com.bluedot.utils.constants.SessionConstants;

import java.util.*;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/8/21 15:41
 * @created: 实验类型业务
 */
public class MaterialTypeService extends BaseService<MaterialType> {


    public MaterialTypeService(Data data) {
        super(data);
    }

    @Override
    protected void doService() {

        String methodName = null;
        switch (operation){
            case "insert":
                methodName = "insertMaterialType";
                break;
            case "delete":
                methodName = "deleteMaterialType";
                break;
            case "update":
                methodName = "updateMaterialType";
                break;
            case "select":
                if (paramList.get("pageNo")!=null || paramList.get("pageSize") != null){
                    methodName = "listMaterialTypePage";
                }else {
                    methodName = "listMaterialTypeNames";
                }
                break;
            default:
                throw new UserException(CommonErrorCode.E_5001);
        }

    }

    /**
     * 添加物质类型
     */
    private void insertMaterialType(){
        MaterialType materialType = new MaterialType();
        ReflectUtil.invokeSetters(paramList,materialType);

        // 将填充参数后的实体类放入entityInfo中
        entityInfo.addEntity(materialType);

        insert();
    }

    /**
     * 删除物质类型
     */
    @SuppressWarnings("unchecked")
    private void deleteMaterialType(){
        //参数解析
        Map<String,Object> intData = new HashMap<>();
        List<Map<String,Object>> listData = new ArrayList<>();
        if (paramList.get("materialTypeId") instanceof List){
            listData = (List<Map<String,Object>>)paramList.get("materialTypeId");
        }else {
            intData = (Map<String,Object>) paramList.get("materialTypeId");
        }

        //根据变量中是否有值来判断参数的类型，然后执行删除一个还是删除多个的操作
        if (!intData.isEmpty()){
            // 如果删除的参数是int类型的,则删除一个
            MaterialType materialType = new MaterialType();
            ReflectUtil.invokeSetters(intData,materialType);
            entityInfo.addEntity(materialType);
        }
        if (!listData.isEmpty()){
            // 如果删除的参数是list类型的，则删除多个
            listData.forEach(data -> {
                MaterialType materialType = new MaterialType();
                ReflectUtil.invokeSetters(data,materialType);
                entityInfo.addEntity(materialType);
            });
        }

        delete();
    }

    /**
     * 修改物质类型
     */
    private void updateMaterialType(){
        MaterialType materialType = new MaterialType();
        ReflectUtil.invokeSetters(paramList,materialType);

        // 将填充参数后的实体类放入entityInfo中
        entityInfo.addEntity(materialType);

        update();
    }

    /**
     * 分页所有的实验物质类型数据
     */
    private void listMaterialTypePage(){
        Condition condition = new Condition();
        if (paramList.containsKey("pageSize")){
            condition.setSize((Integer) paramList.get("pageSize"));
        }
        if (paramList.containsKey("pageNo")){
            condition.setStartIndex(((long)paramList.get("pageNo")-1)*(int)paramList.get("pageSize"));
        }
        if (paramList.containsKey("materialTypeName")){
            condition.addOrConditionWithView(new Term("material_type","material_type_id",paramList.get("materialTypeName"),TermType.EQUAL));
        }

        entityInfo.setCondition(condition);

        select();

    }

    /**
     * 查询所有的实验物质类型名数据
     */
    private void listMaterialTypeNames() {
        Condition condition = new Condition();
        condition.addView("material_type");

        entityInfo.setCondition(condition);

        select();

        List<MaterialType> materialTypes = (List<MaterialType>) commonResult.getData();
        List<String> materialTypeNames = new ArrayList<>();

        materialTypes.forEach(one -> {
            materialTypeNames.add(one.getMaterialTypeName());
        });

        commonResult.setData(materialTypeNames);
    }
}
