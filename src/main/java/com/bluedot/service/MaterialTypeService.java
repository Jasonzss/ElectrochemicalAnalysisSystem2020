package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.EntityInfo;
import com.bluedot.mapper.bean.Term;
import com.bluedot.mapper.bean.TermType;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.Application;
import com.bluedot.pojo.entity.MaterialType;
import com.bluedot.pojo.entity.User;
import com.bluedot.pojo.vo.CommonResult;
import com.bluedot.utils.ReflectUtil;
import com.bluedot.utils.constants.SessionConstants;

import javax.servlet.http.HttpSession;
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

    public MaterialTypeService(HttpSession session, EntityInfo<?> entityInfo) {
        super(session, entityInfo);
    }

    @Override
    protected void doService() {

        String methodName;
        switch (operation) {
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
                methodName = "listMaterialType";
                break;
            default:
                throw new UserException(CommonErrorCode.E_5001);
        }
        invokeMethod(methodName,this);
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
        if (paramList.get("materialTypeId") instanceof List){
            List<Integer> listData = (List<Integer>)paramList.get("materialTypeId");
            // 如果删除的参数是list类型的，则删除多个
            listData.forEach(data -> {
                MaterialType materialType = new MaterialType();
                materialType.setMaterialTypeId(data);
                entityInfo.addEntity(materialType);
            });
        }else {
            Integer intData = (Integer) paramList.get("materialTypeId");
            // 如果删除的参数是int类型的,则删除一个
            MaterialType materialType = new MaterialType();
            materialType.setMaterialTypeId(intData);
            entityInfo.addEntity(materialType);
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
     * 查询物质类型
     */
    private void listMaterialType(){
        Condition condition = new Condition();
        condition.addView("material_type");
        condition.setReturnType("MaterialType");

        if (paramList.containsKey("pageSize")){
            condition.setSize((Integer) paramList.get("pageSize"));
        }
        if (paramList.containsKey("pageNo")){
            condition.setStartIndex((long)((int)paramList.get("pageNo")-1)*(int)paramList.get("pageSize"));
        }
        if (paramList.containsKey("materialTypeName")){
            condition.addOrConditionWithView(new Term("material_type","material_type_name",paramList.get("materialTypeName"),TermType.EQUAL));
        }

        entityInfo.setCondition(condition);

        select();

    }
}
