package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.ErrorException;
import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.Term;
import com.bluedot.mapper.bean.TermType;
import com.bluedot.pojo.dto.Data;
import com.bluedot.pojo.entity.User;
import com.bluedot.pojo.vo.CommonResult;
import com.bluedot.utils.EmailUtil;
import com.bluedot.utils.ImageUtil;
import com.bluedot.utils.ReflectUtil;
import com.bluedot.utils.constants.SessionConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author Jason
 * @CreationDate 2022/08/16 - 21:45
 * @Description ：
 */
public class UserService extends BaseService<User> {

    public UserService(Data data) {
        super(data);
    }

    @Override
    protected void doService() {
        String userEmail = (String) session.getAttribute("userEmail");
        Map<String,Object> map = (Map<String, Object>) paramList.get("user");

        String methodName = null;
        switch (operation) {
            case "insert":
                methodName = "insertUser";
                break;
            case "delete":
                if (userEmail.equals(map.get("userEmail"))) {
                    methodName = "deletePersonalUser";
                } else {
                    methodName = "deleteUser";
                }
                break;
            case "update":
                if (userEmail.equals(map.get("userEmail"))) {
                    methodName = "updatePersonalUser";
                } else {
                    methodName = "updateUser";
                }
                break;
            case "select":
                if (userEmail.equals(map.get("userEmail"))) {
                    if (map.get("password") != null) {
                        methodName = "login";
                    } else {
                        methodName = "getPersonalUser";
                    }
                } else {
                    methodName = "listUsers";
                }
                break;
            case "other":
                if (map.get("userEmail") != null) {
                    methodName = "sendAuthEmail";
                }
                break;
            default:
                System.out.println("错误的请求参数！");
                commonResult = CommonResult.errorResult(200, "错误的请求参数");
                return;
        }

        invokeMethod(methodName,this);
    }

    private void updateUser(){
        // 先将paramList中的参数取出
        List<Map<String,Object>> objectList = (List<Map<String, Object>>) paramList.get("user");
        List<User> userList = new ArrayList<>();


        // 遍历每个map对象，将其封装到指定对象中
        objectList.forEach(map->{
            if (map.containsKey("userImg")){
                // 将图片转换为数组放入
                map.put("userImg", ImageUtil.imgToBinary((File) map.get("userImg")));
            }

            // 封装User实体
            User user = new User();
            ReflectUtil.invokeSetters(map, user);

            // 执行修改逻辑
            update();
        });

    }

    private void updatePersonalUser(){
        // 先将paramList中的参数取出
        List<Map<String,Object>> userList = (List<Map<String,Object>>) paramList.get("user");
        Map<String,Object> map = userList.get(0);

        // 判断是否有密码
        if (map.containsKey("userPassword")){
            if (!session.getAttribute("passwordAuth").equals(true) && !session.getAttribute("emailAuth").equals(true)) {
                commonResult = CommonResult.errorResult(200,"禁止未经验证的修改密码");
                return;
            }
        }

        try {
            // 判断是否有用户状态
            if (map.containsKey("userStatus")){
                commonResult = CommonResult.errorResult(200,"用户状态无法自行修改");
                return;
            }
        } catch (Exception e) {
            throw new ErrorException(CommonErrorCode.E_3001);
        }

        // 判断是否有图片
        if (map.containsKey("userImg")){
            // 将图片转换为数组放入
            map.put("userImg", ImageUtil.imgToBinary((File) map.get("userImg")));
        }

        // 封装User实体
        User user = new User();
        ReflectUtil.invokeSetters(map, user);

        // 执行修改逻辑
        entityInfo.addEntity(user);
        update();
    }

    private void insertUser(){
        //获取新增用户的信息
        Map<String,Object> userMap = (Map<String, Object>) paramList.get("user");
        String authCode = (String) paramList.get("authCode");

        //判断邮箱验证码是否正确
        if (authCode.equalsIgnoreCase((String) session.getAttribute(SessionConstants.AUTH_CODE))){
            //验证码不正确
            throw new UserException(CommonErrorCode.E_1004);
        }

        //判断邮箱是否可用

        //执行插入操作

        //返回逻辑

    }

    private void deleteUser(){
        //获取需要删除的用户信息
        List<Map<String,Object>> userMapList = (List<Map<String, Object>>) paramList.get("user");

        //包装需要删除的实体类
        for (Map<String,Object> map:userMapList) {
            User user = new User();
            user.setUserEmail((String) map.get("userEmail"));
            entityInfo.addEntity(user);
        }

        //执行删除逻辑
        delete();

        //返回逻辑

    }

    private void deletePersonalUser(){
        //获取需要删除的用户信息
        Map<String,Object> userMap = (Map<String, Object>) paramList.get("user");

        //包装需要删除的实体类

            User user = new User();
            user.setUserEmail((String) userMap.get("userEmail"));
            entityInfo.addEntity(user);


        //执行删除逻辑
        delete();

        //返回逻辑

    }

    private void getPersonalUser(){
        // 先将paramList中的参数取出
        Map<String,Object> map = (Map<String, Object>) paramList.get("user");

        // 封装Condition
        Condition condition = new Condition();
        // 判断搜索用户的各项属性
        condition.addAndConditionWithView(new Term("user","userEmail",map.get("userEmail"),TermType.EQUAL));

        // 执行修改逻辑
        entityInfo.setCondition(condition);
        select();
    }

    private void listUsers(){
        // 先将paramList中的参数取出
        Map<String,Object> map = (Map<String, Object>) paramList.get("user");

        // 封装Condition
        Condition condition = new Condition();
        condition.setStartIndex((Long) paramList.get("startIndex"));
        condition.setSize((Integer) paramList.get("pageSize"));

        // 判断搜索用户的各项属性
        if (map.size() != 0){
            List<Term> list = new ArrayList<>();
            if (map.containsKey("userName")){
                condition.addOrConditionWithView(new Term("user","userName",map.get("userName"), TermType.LIKE));
            }
            if (map.containsKey("roleId")){
                condition.addOrConditionWithView(new Term("user_role","roleId",map.get("roleId"),TermType.EQUAL));
            }
            if (map.containsKey("userEmail")){
                condition.addOrConditionWithView(new Term("user", "userEmail", map.get("userEmail"), TermType.EQUAL));
            }
        }

        // 执行修改逻辑
        entityInfo.setCondition(condition);
        select();
    }

    private void login(){
        //获取登录的参数

        //判断图片验证码是否正确

        //session中移除图片验证码

        //根据邮箱查询用户

        //是否存在此用户

        //判断密码是否正确

        //登录通过

        //查询权限列表放入session

        //将userEmail放入session

        //返回token回前端
    }

    private void sendAuthEmail(){
        getPersonalUser();
        List<User> userList = (List<User>) commonResult.getData();
        User user = userList.get(0);
        if(user == null){
            //邮箱未注册
            throw new UserException(CommonErrorCode.E_1005);
        }

        
    }

    private void isAvailableEmail(){
        // 先将paramList中的参数取出
        Map<String,Object> userMap = (Map<String, Object>) paramList.get("user");
        //判断邮箱是否合法
        if (!EmailUtil.isLegalEmail((String) userMap.get("userEmail"))){
            throw new UserException(CommonErrorCode.E_1003);
        }

        //查询用户
        getPersonalUser();
        //获得查询结果
        List<User> user = (List<User>) commonResult.getData();

        //判断用户是否注册
        if (user != null){
            throw new UserException(CommonErrorCode.E_1002);
        }

        //此邮箱为可用邮箱
        commonResult = CommonResult.successResult("此邮箱可用",true);
    }

    private void logout(){
        //移除session和token
    }
}
