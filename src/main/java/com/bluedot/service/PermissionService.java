package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.Term;
import com.bluedot.mapper.bean.TermType;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.Permission;
import com.bluedot.pojo.entity.Role;
import com.bluedot.pojo.entity.RolePermission;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author zlj
 * @version 1.0
 * @description:
 * @date 2022/8/23 22:13
 */
public class PermissionService extends BaseService<Permission>{


    public PermissionService(Data data) {
        super(data);
    }
    /**
     * 负责在PermissionService中个根据父类属性分析调用哪些方法来解决请求
     */
    @Override
    protected void doService() {
        String methodName=null;
        switch (operation){
            case "select":
                if (paramList.containsKey("user")){
                    methodName="listUserRoles";
                }else if(paramList.containsKey("role")){
                    methodName="listRolePermissions";
                }else if(paramList.containsKey("roles")){
                    methodName="listRoles";
                }else if(paramList.containsKey("permission")){
                    methodName="listPermissions";
                }else {
                    throw new UserException(CommonErrorCode.E_4001);
                }
                break;
            case "insert":
                if (paramList.containsKey("roleName")&&paramList.containsKey("roleDesc")&&paramList.containsKey("permissionIds")){
                    methodName="insertRole";
                }else {
                    throw new UserException(CommonErrorCode.E_4001);
                }
                break;
            case "update":
                if (paramList.containsKey("userEmail")&&paramList.containsKey("roleIds")){
                    methodName="updateUserRoles";
                }else if(paramList.containsKey("roleId")&&paramList.containsKey("permissionIds")){
                    methodName="updateRolePermissions";
                }else {
                    throw new UserException(CommonErrorCode.E_4001);
                }
                break;
            default:
                throw new UserException(CommonErrorCode.E_4001);
        }
        invokeMethod(methodName,this);

    }

    @Override
    protected boolean check() {
        return false;
    }

    /**
     * 查询所有用户的角色
     */
    private void listUserRoles(){
        Long pageNo = (Long) paramList.get("pageNo");
        Integer pageSize = (Integer) paramList.get("pageSize");

        // 封装Condition
        Condition condition = new Condition();
        condition.setStartIndex((pageNo-1)*pageSize);
        condition.setSize(pageSize);
        condition.setReturnType("UserRole");
        List<String> views = new ArrayList<>();
        views.add("`user_role`");
        views.add("`user`");
        views.add("`role`");
        List<String> viewCondition=new ArrayList<>();
        viewCondition.add("user_email");
        viewCondition.add("role_id");
        List<String> fields = new ArrayList<>();
        fields.add("user.user_email");
        fields.add("role.role_id");
        fields.add("role.role_name");
        fields.add("role.role_desc");
        entityInfo.setCondition(condition);
        selectPage();
    }
    /**
     * 查询所有角色
     */
    private void listRoles(){
        // 封装Condition
        Condition condition = new Condition();
        condition.setReturnType("Role");
        List<String> views = new ArrayList<>();
        views.add("`role`");
        List<String> fields = new ArrayList<>();
        fields.add("role.role_id");
        fields.add("role.role_name");
        fields.add("role.role_desc");
        entityInfo.setCondition(condition);
        selectPage();
    }
    /**
     * 查询所有角色的权限
     */
    private void listRolePermissions(){
        Long pageNo = (Long) paramList.get("pageNo");
        Integer pageSize = (Integer) paramList.get("pageSize");

        // 封装Condition
        Condition condition = new Condition();
        condition.setStartIndex((pageNo-1)*pageSize);
        condition.setSize(pageSize);
        condition.setReturnType("RolePermission");
        List<String> views = new ArrayList<>();
        views.add("`role_permission`");
        views.add("`role`");
        views.add("`permission`");
        List<String> viewCondition=new ArrayList<>();
        viewCondition.add("role_id");
        viewCondition.add("permission_id");
        List<String> fields = new ArrayList<>();
        fields.add("role.role_name");
        fields.add("permission.permission_name");
        fields.add("permission.permission_id");
        entityInfo.setCondition(condition);
        selectPage();
    }
    /**
     * 查询所有权限
     */
    private void listPermissions(){
        // 封装Condition
        Condition condition = new Condition();
        condition.setReturnType("Permission");
        List<String> views = new ArrayList<>();
        views.add("`permission`");
        List<String> fields = new ArrayList<>();
        fields.add("permission.permission_id");
        fields.add("permission.permission_name");
        fields.add("permission.methodName");
        entityInfo.setCondition(condition);
        selectPage();
    }

    /**
     * 新增角色
     */
    private void insertRole(){
        int[] permissionIds = (int[])paramList.get("permissionIds");
        String roleName = (String) paramList.get("roleName");
        String roleDesc = (String) paramList.get("roleDesc");
        Role role=new Role();
        role.setRoleName(roleName);
        role.setRoleDesc(roleDesc);
        insert();
        // 封装Condition
        Condition condition = new Condition();
        condition.setReturnType("Role");
        List<String> views = new ArrayList<>();
        views.add("`role`");
        List<String> fields = new ArrayList<>();
        fields.add("role.role_id");
        List<Term> andCondition=new ArrayList<>();
        andCondition.add(new Term("role","role_name",roleName, TermType.EQUAL));
        entityInfo.setCondition(condition);
        select();
        Role role1= (Role) commonResult.getData();
        Integer roleId = role1.getRoleId();

        ArrayList<RolePermission> rolePermissionArrayList = new ArrayList<>();
        for (int permissionId : permissionIds) {
            RolePermission rolePermission1 = new RolePermission();
            rolePermission1.setRoleId(roleId);
            rolePermission1.setPermissionId(permissionId);
            rolePermissionArrayList.add(rolePermission1);
        }
        HashMap<String,Object> map=new HashMap<>();
        map.put("rolePermissionArrayList",rolePermissionArrayList);

        new RolePermissionService(session,entityInfo).doOtherService(map,"insert");
    }
    /**
     * 修改角色权限
     */
    private void updateRolePermissions(){
        RolePermissionService rolePermissionService = new RolePermissionService(session, entityInfo);
        rolePermissionService.doOtherService(paramList,"delete");
        rolePermissionService.doOtherService(paramList,"insert");
    }
}
