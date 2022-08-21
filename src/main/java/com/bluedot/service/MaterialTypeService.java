package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.Term;
import com.bluedot.mapper.bean.TermType;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.MaterialType;
import com.bluedot.pojo.entity.User;
import com.bluedot.utils.ReflectUtil;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/8/21 15:41
 * @created:
 */
public class MaterialTypeService extends BaseService<MaterialType> {


    public MaterialTypeService(Data data) {
        super(data);
    }

    @Override
    protected void doService() {
        String userEmail = (String) session.getAttribute("userEmail");
        boolean ifHasWritePermission = checkIfHasWritePermission();

        String methodName = null;
        switch (operation){
            case "insert":
                if (ifHasWritePermission){
                    methodName = "insertMaterialType";
                }else {
                    methodName = "insertPersonalMaterialType";
                }
                break;
            case "delete":
                methodName = "deleteMaterialType";
                break;
            case "update":

                break;
            case "select":

                break;
            default:
                throw new UserException(CommonErrorCode.E_9001);
        }

    }

    /**
     * 判断当前用户是否有权限增删改操作数据
     */
    private boolean checkIfHasWritePermission(){
        String userEmail = (String) session.getAttribute("userEmail");

        //添加条件，查询当前操作用户
        Condition condition = new Condition();
        condition.addAndConditionWithView(new Term("user","userEmail",userEmail, TermType.EQUAL));
        entityInfo.setCondition(condition);

        select();

        User user = (User) commonResult.getData();

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


}
