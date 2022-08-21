package com.bluedot.pojo.entity;

/**
 * @Author Jason
 * @CreationDate 2022/08/19 - 16:24
 * @Description ：代替物理表中的用户角色中间表
 */
public class UserRole {
    private User user;
    private Role[] roles;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role[] getRoles() {
        return roles;
    }

    public void setRoles(Role[] roles) {
        this.roles = roles;
    }
}
