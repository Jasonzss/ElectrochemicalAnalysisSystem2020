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
import com.bluedot.pojo.vo.RoleWithCountNum;
import com.bluedot.utils.LogUtil;
import com.bluedot.utils.constants.OperationConstants;


import java.util.*;
import java.util.stream.Collectors;

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
        Integer pageNo = (Integer) paramList.get("pageNo");
        Integer pageSize = (Integer) paramList.get("pageSize");

        Map<String,Object> map = new HashMap<>();
        map.put("pageSize",pageSize);
        map.put("pageNo",pageNo);
        CommonResult userList = new UserService(session, entityInfo).doOtherService(map, OperationConstants.SELECT);
        PageInfo userPageInfo = (PageInfo) userList.getData();
        List<Object> dataList = userPageInfo.getDataList();
        List<User> users = new ArrayList<>();
        dataList.forEach((data) -> {
            users.add((User) data);
        });
        List<String> userEmailList = users.stream().map(User::getUserEmail).collect(Collectors.toList());


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

        condition.addAndConditionWithView(new Term("`user_role`","user_email",userEmailList,TermType.IN));
        entityInfo.setCondition(condition);
        select();
        // 设置pageInfo，并将查询到的数据填入
        List<UserRole> userRoleList = (List<UserRole>) this.commonResult.getData();

        //获得查询到的user
        Map<String, User> userMap = users.stream().collect(Collectors.toMap(User::getUserEmail, user -> user));
        userPageInfo.setDataList(null);
        //将user放入pageInfo中
        userRoleList.forEach((userRole -> {
            userRole.setUser(userMap.getOrDefault(userRole.getUserEmail(),userRole.getUser()));
        }));

        //将数据放入pageInfo
        userRoleList.forEach((userPageInfo::addData));

        this.commonResult = CommonResult.successResult("",userPageInfo);
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

        List<Role> roles = (List<Role>) commonResult.getData();
        List<RoleWithCountNum> roleWithCountNumList = new ArrayList<>();

        for (int i = 0; i < roles.size(); i++) {
            condition = new Condition();
            condition.setReturnType("Long");
            condition.addFields("count(*)");
            condition.addView("user_role");
            condition.addAndConditionWithView(new Term("user_role","role_id",roles.get(i).getRoleId(),TermType.EQUAL));
            entityInfo.setCondition(condition);
            select();

            RoleWithCountNum roleWithCountNum = new RoleWithCountNum();

            ArrayList list= (ArrayList) commonResult.getData();
            roleWithCountNum.setCount((Long)list.get(0));
            roleWithCountNum.setRole(roles.get(i));

            roleWithCountNumList.add(roleWithCountNum);
        }

        commonResult = CommonResult.successResult("",roleWithCountNumList);
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
