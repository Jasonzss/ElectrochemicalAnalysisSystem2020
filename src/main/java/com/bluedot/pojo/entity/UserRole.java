package com.bluedot.pojo.entity;

import java.util.ArrayList;

/**
 * @author zlj
 * @version 1.0
 * @description: TODO
 * @date 2022/8/21 13:49
 */
public class UserRole {
    private String userEmail;
    private Integer roleId;
    private User user;
    private ArrayList<Role> roleArrayList;

    public UserRole() {
    }

    public UserRole(User user, ArrayList<Role> roleArrayList) {
        this.user = user;
        this.roleArrayList = roleArrayList;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<Role> getRoleArrayList() {
        return roleArrayList;
    }

    public void setRoleArrayList(ArrayList<Role> roleArrayList) {
        this.roleArrayList = roleArrayList;
    }

    @Override
    public String toString() {
        return "UserRole{" +
                "userEmail='" + userEmail + '\'' +
                ", roleId=" + roleId +
                ", user=" + user +
                ", roleArrayList=" + roleArrayList +
                '}';
    }
}
