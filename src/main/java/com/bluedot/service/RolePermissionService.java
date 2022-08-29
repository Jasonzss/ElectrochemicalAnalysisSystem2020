package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.EntityInfo;
import com.bluedot.mapper.bean.Term;
import com.bluedot.mapper.bean.TermType;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.Role;
import com.bluedot.pojo.entity.RolePermission;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

public class RolePermissionService extends BaseService<RolePermission>{
    public RolePermissionService(Data data) {
        super(data);
    }

    protected RolePermissionService(HttpSession session, EntityInfo<?> entityInfo) {
        super(session, entityInfo);
    }

    @Override
    protected void doService() {
        String methodName=null;
        switch (operation){
            case "delete":
                methodName="deleteRolePermission";
                break;
            case "insert":
                methodName="insertRolePermission";
                break;
            default:
                throw new UserException(CommonErrorCode.E_4001);
        }
        invokeMethod(methodName,this);
    }

    /**
     * 修改角色权限
     */
    private void updateRolePermissions(){
        int[] permissionIds = (int[])paramList.get("permissionIds");
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId((Integer) paramList.get("roleId"));
        entityInfo.addEntity(rolePermission);
        delete();

        ArrayList<RolePermission> rolePermissionArrayList = new ArrayList<>();
        for (int permissionId : permissionIds) {
            RolePermission rolePermission1 = new RolePermission();
            rolePermission1.setRoleId((Integer) paramList.get("roleId"));
            rolePermission1.setPermissionId(permissionId);
            rolePermissionArrayList.add(rolePermission1);
        }
        entityInfo.setEntity(rolePermissionArrayList);
        insert();
    }

    private void insertRolePermission(){
        List rolePermissionArrayList= (List) paramList.get("rolePermissionArrayList");
        entityInfo.setEntity(rolePermissionArrayList);
        insert();
    }
    private void deleteRolePermission(){
        int[] permissionIds = (int[])paramList.get("permissionIds");
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId((Integer) paramList.get("roleId"));
        entityInfo.addEntity(rolePermission);
        delete();
    }
}
