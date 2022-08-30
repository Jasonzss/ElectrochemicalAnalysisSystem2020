package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.PageInfo;
import com.bluedot.mapper.bean.Term;
import com.bluedot.mapper.bean.TermType;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.*;
import com.bluedot.pojo.vo.CommonResult;
import com.bluedot.utils.LogUtil;


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
        userLogMap.put("userLogClassMethodName",userLogMap.get("userLogClassMethodName")+methodName);
        invokeMethod(methodName,this);

    }

    @Override
    protected boolean check() {
        return true;
    }

    /**
     * 查询所有用户的角色
     */
    private void listUserRoles(){

        Long pageNo = Long.valueOf(((Integer)paramList.get("pageNo")).longValue());
        Integer pageSize = (Integer) paramList.get("pageSize");

        Condition condition1 = new Condition();
        condition1.setReturnType("User");
        condition1.addView("user");
        condition1.addFields("user_email");
        entityInfo.setCondition(condition1);
        select();
        ArrayList<User> arrayList= (ArrayList<User>) commonResult.getData();
        int start= (int) ((pageNo-1)*pageSize);
        int end=start+pageSize;
        end= end>arrayList.size() ? arrayList.size():end;
        List<String> list=new ArrayList<>();
        for (int i = start; i < end; i++) {
            list.add(arrayList.get(i).getUserEmail());
        }

        // 封装Condition
        Condition condition = new Condition();
        condition.setReturnType("UserRole");
        List<String> views = new ArrayList<>();
        views.add("`user_role`");
        views.add("`user`");
        views.add("`role`");
        condition.setViews(views);
        List<String> viewCondition=new ArrayList<>();
        viewCondition.add("user_email");
        viewCondition.add("role_id");
        condition.setViewCondition(viewCondition);
        List<String> fields = new ArrayList<>();
        fields.add("user.user_email");
        fields.add("role.*");
        condition.setFields(fields);

        condition.addAndConditionWithView(new Term("`user_role`","user_email",list,TermType.IN));
        entityInfo.setCondition(condition);
        select();
        // 设置pageInfo，并将查询到的数据填入
        PageInfo<UserRole> pageInfo = new PageInfo<UserRole>();
        pageInfo.setDataList((List<UserRole>) commonResult.getData());
        pageInfo.setPageSize(pageSize);

        pageInfo.setTotalDataSize((long) arrayList.size());
        pageInfo.setTotalPageSize(pageInfo.getTotalDataSize() /pageInfo.getPageSize());
        pageInfo.setCurrentPageNo(Math.toIntExact(pageNo));

        commonResult = CommonResult.successResult("分页查询",pageInfo);
        LogUtil.insertUserLog("error","详情",userLogMap);
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
        condition.setViews(views);
        List<String> fields = new ArrayList<>();
        fields.add("role.role_id");
        fields.add("role.role_name");
        fields.add("role.role_desc");
        condition.setFields(fields);
        entityInfo.setCondition(condition);
        select();
    }
    /**
     * 查询所有角色的权限
     */
    private void listRolePermissions(){
        // 封装Condition
        Condition condition = new Condition();

        condition.setReturnType("RolePermission");
        List<String> views = new ArrayList<>();
        views.add("`role_permission`");
        views.add("`role`");
        views.add("`permission`");
        condition.setViews(views);
        List<String> viewCondition=new ArrayList<>();
        viewCondition.add("role_id");
        viewCondition.add("permission_id");
        condition.setViewCondition(viewCondition);
        List<String> fields = new ArrayList<>();
        fields.add("role.role_name");
        fields.add("permission.permission_name");
        fields.add("permission.permission_id");
        condition.setFields(fields);
        entityInfo.setCondition(condition);
        select();
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
        condition.setViews(views);
        List<String> fields = new ArrayList<>();
        fields.add("permission.permission_id");
        fields.add("permission.permission_name");
        condition.setFields(fields);
        entityInfo.setCondition(condition);
        select();
    }

    /**
     * 新增角色
     */
    private void insertRole(){
        String roleName = (String) paramList.get("roleName");
        String roleDesc = (String) paramList.get("roleDesc");
        Role role=new Role();
        role.setRoleName(roleName);
        role.setRoleDesc(roleDesc);
        paramList.put("role",role);
        new RoleService(session,entityInfo).doOtherService(paramList,"insert");

        // 封装Condition
        Condition condition = new Condition();
        condition.setReturnType("Role");
        List<String> views = new ArrayList<>();
        views.add("`role`");
        List<String> fields = new ArrayList<>();
        fields.add("role.role_id");
        List<Term> andCondition=new ArrayList<>();
        andCondition.add(new Term("role","role_name",roleName, TermType.EQUAL));
        condition.setViews(views);
        condition.setFields(fields);
        condition.setAndCondition(andCondition);
        entityInfo.setCondition(condition);
        select();
        ArrayList<Role> roleArrayList= (ArrayList<Role>) commonResult.getData();
        Role role1= roleArrayList.get(roleArrayList.size()-1);
        Integer roleId = role1.getRoleId();

        ArrayList<Integer> permissionIds = (ArrayList<Integer>) paramList.get("permissionIds");
        ArrayList<RolePermission> rolePermissionArrayList = new ArrayList<>();
        for (int permissionId : permissionIds) {
            RolePermission rolePermission1 = new RolePermission();
            rolePermission1.setRoleId(roleId);
            rolePermission1.setPermissionId(permissionId);
            rolePermissionArrayList.add(rolePermission1);
        }

        paramList.put("rolePermissionArrayList",rolePermissionArrayList);

        new RolePermissionService(session,entityInfo).doOtherService(paramList,"insert");
        LogUtil.insertUserLog("error","详情",userLogMap);
    }
    /**
     * 修改角色权限
     */
    private void updateRolePermissions(){
        RolePermissionService rolePermissionService = new RolePermissionService(session, entityInfo);
        rolePermissionService.doOtherService(paramList,"delete");
        commonResult=rolePermissionService.doOtherService(paramList,"insert");
        commonResult= CommonResult.successResult("修改角色权限成功",commonResult.getData());
    }
    /**
     * 修改用户角色
     */
    private void updateUserRoles(){
        UserRoleService userRoleService = new UserRoleService(session, entityInfo);
        userRoleService.doOtherService(paramList,"delete");
        commonResult=userRoleService.doOtherService(paramList,"insert");
        commonResult= CommonResult.successResult("修改用户角色成功",commonResult.getData());
    }
}
