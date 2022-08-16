package com.bluedot.pojo.entity;

/**
 * @Author Jason
 * @CreationDate 2022/07/26 - 11:13
 * @Description ：
 */
public class User {
    private String userEmail;
    private String userPassword;
    private String userSalt;
    private Integer userStatus;
    private String userName;
    private Integer userSex;
    private Integer userAge;
    private Long userTel;
    private Byte[] userImg;
    private Role[] roles;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserSalt() {
        return userSalt;
    }

    public void setUserSalt(String userSalt) {
        this.userSalt = userSalt;
    }

    public Integer getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Integer userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getUserSex() {
        return userSex;
    }

    public void setUserSex(Integer userSex) {
        this.userSex = userSex;
    }

    public Integer getUserAge() {
        return userAge;
    }

    public void setUserAge(Integer userAge) {
        this.userAge = userAge;
    }

    public Long getUserTel() {
        return userTel;
    }

    public void setUserTel(Long userTel) {
        this.userTel = userTel;
    }

    public Byte[] getUserImg() {
        return userImg;
    }

    public void setUserImg(Byte[] userImg) {
        this.userImg = userImg;
    }

    public Role[] getRoles() {
        return roles;
    }

    public void setRoles(Role[] roles) {
        this.roles = roles;
    }
}
