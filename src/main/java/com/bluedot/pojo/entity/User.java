package com.bluedot.pojo.entity;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;

import java.util.Arrays;

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
    private byte[] userImg;

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

    public void setUserStatus(String userStatus){
        this.userStatus = Integer.valueOf(userStatus);
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

    public void setUserSex(String userSex){
        if ("男".equals(userSex)){
            this.userSex = new Integer(1);
        }else if ("女".equals(userSex)){
            this.userSex = new Integer(0);
        }else {
            throw new UserException(CommonErrorCode.E_5002);
        }
    }

    public Integer getUserAge() {
        return userAge;
    }

    public void setUserAge(Integer userAge) {
        this.userAge = userAge;
    }

    public void setUserAge(String userAge){
        this.userAge = Integer.valueOf(userAge);
    }

    public Long getUserTel() {
        return userTel;
    }

    public void setUserTel(Long userTel) {
        this.userTel = userTel;
    }

    public byte[] getUserImg() {
        return userImg;
    }

    public void setUserImg(byte[] userImg) {
        this.userImg = userImg;
    }

    @Override
    public String toString() {
        return "User{" +
                "userEmail='" + userEmail + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userSalt='" + userSalt + '\'' +
                ", userStatus=" + userStatus +
                ", userName='" + userName + '\'' +
                ", userSex=" + userSex +
                ", userAge=" + userAge +
                ", userTel=" + userTel +
                ", userImg=" + Arrays.toString(userImg) +
                '}';
    }
}
