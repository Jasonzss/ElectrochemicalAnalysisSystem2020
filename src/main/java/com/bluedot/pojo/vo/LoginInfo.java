package com.bluedot.pojo.vo;

import java.util.List;

/**
 * @Author Jason
 * @CreationDate 2022/10/03 - 20:26
 * @Description ：
 */
public class LoginInfo {
    private String token;
    private String userEmail;
    private List<Integer> roleIdList;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public List<Integer> getRoleIdList() {
        return roleIdList;
    }

    public void setRoleIdList(List<Integer> roleIdList) {
        this.roleIdList = roleIdList;
    }
}
