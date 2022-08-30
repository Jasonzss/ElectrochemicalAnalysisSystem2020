package com.bluedot.pojo.entity;

import java.util.ArrayList;

/**
 * @author zlj
 * @version 1.0
 * @description: TODO
 * @date 2022/8/21 13:57
 */
public class RolePermission {
    private Integer roleId;
    private Integer permissionId;
    private Role role;
    private ArrayList<Permission> permissionArrayList;

    public RolePermission() {
    }

    public RolePermission(Integer roleId, Integer permissionId, Role role, ArrayList<Permission> permissionArrayList) {
        this.roleId = roleId;
        this.permissionId = permissionId;
        this.role = role;
        this.permissionArrayList = permissionArrayList;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public ArrayList<Permission> getPermissionArrayList() {
        return permissionArrayList;
    }

    public void setPermissionArrayList(ArrayList<Permission> permissionArrayList) {
        this.permissionArrayList = permissionArrayList;
    }

    @Override
    public String toString() {
        return "RolePermission{" +
                "roleId=" + roleId +
                ", permissionId=" + permissionId +
                ", role=" + role +
                ", permissionArrayList=" + permissionArrayList +
                '}';
    }
}
