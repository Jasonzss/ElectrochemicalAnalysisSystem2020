package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.EntityInfo;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.UserRole;
import com.bluedot.pojo.vo.CommonResult;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

public class UserRoleService extends BaseService<UserRole>{

    public UserRoleService(Data data) {
        super(data);
    }

    protected UserRoleService(HttpSession session, EntityInfo<?> entityInfo) {
        super(session, entityInfo);
    }

    @Override
    protected void doService() {
        String methodName=null;
        switch (operation){
            case "delete":
                methodName="deleteUserRole";
                break;
            case "insert":
                methodName="insertUserRole";
                break;
            case "login":
                methodName="insertExperimenter";
                break;
            default:
                throw new UserException(CommonErrorCode.E_4001);
        }
        invokeMethod(methodName,this);
    }

    /**
     * 插入用户角色
     */
    private void insertUserRole(){
        ArrayList<Integer> roleIds = (ArrayList<Integer>) paramList.get("roleIds");
        ArrayList<UserRole> userRoleArrayList1 = new ArrayList<>();
        for (int roleId : roleIds) {
            UserRole userRole1 = new UserRole();
            userRole1.setRoleId(roleId);
            userRole1.setUserEmail((String) paramList.get("userEmail"));
            userRoleArrayList1.add(userRole1);
        }
        entityInfo.setEntity(userRoleArrayList1);
        insert();
    }

    /**
     * 给用户增加实验员角色
     */
    private void insertExperimenter(){
        UserRole userRole = new UserRole();
        userRole.setRoleId(0);
        userRole.setUserEmail((String) paramList.get("userEmail"));
        entityInfo.addEntity(userRole);
        insert();
    }

    /**
     * 删除用户角色
     */
    private void deleteUserRole(){
        UserRole userRole = new UserRole();
        userRole.setUserEmail((String) paramList.get("userEmail"));
        entityInfo.addEntity(userRole);
        delete();
    }
}
