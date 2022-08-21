package com.bluedot.pojo.entity;

/**
 * @Author Jason
 * @CreationDate 2022/08/19 - 16:34
 * @Description ：代替物理表中的角色权限中间表
 */
public class RolePermission {
    private Role role;
    private Permission[] permissions;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Permission[] getPermissions() {
        return permissions;
    }

    public void setPermissions(Permission[] permissions) {
        this.permissions = permissions;
    }
}
