package com.bluedot.pojo.entity;

import java.util.ArrayList;

/**
 * @author zlj
 * @version 1.0
 * @description: TODO
 * @date 2022/8/21 13:57
 */
public class RolePermission {
    private Role role;
    private ArrayList<Permission> permissionArrayList;

    public RolePermission() {
    }

    public RolePermission(Role role, ArrayList<Permission> permissionArrayList) {
        this.role = role;
        this.permissionArrayList = permissionArrayList;
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
                "role=" + role +
                ", permissionArrayList=" + permissionArrayList +
                '}';
    }
}
