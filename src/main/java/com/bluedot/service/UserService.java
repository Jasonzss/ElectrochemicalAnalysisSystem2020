package com.bluedot.service;

import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.Term;
import com.bluedot.mapper.bean.TermType;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.User;
import com.bluedot.pojo.vo.CommonResult;
import com.bluedot.utils.ImageUtil;
import com.bluedot.utils.ReflectUtil;

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
        List<String> permissionList = (List<String>) session.getAttribute("permissionList");
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

        // 判断是否有用户状态
        if (map.containsKey("userStatus")){
            commonResult = CommonResult.errorResult(200,"用户状态无法自行修改");
            return;
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
        entityInfo.setEntity(user);
        update();
    }

    private void insertUser(){

    }

    private void deleteUser(){

    }

    private void deletePersonalUser(){

    }

    private void getPersonalUser(){
        // 先将paramList中的参数取出
        Map<String,Object> map = (Map<String, Object>) paramList.get("user");

        // 封装Condition
        Condition condition = new Condition();

        // 判断搜索用户的各项属性
        Term term = new Term("user", "userEmail", map.get("userEmail"), TermType.EQUAL);
        List<Term> list = new ArrayList<>();
        list.add(term);
        condition.setAndCondition(list);

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
                Term term = new Term("user","userName",map.get("userName"), TermType.LIKE);
                list.add(term);
            }
            if (map.containsKey("roleId")){
                Term term = new Term("user_role","roleId",map.get("roleId"),TermType.EQUAL);
                list.add(term);
            }
            if (map.containsKey("userEmail")){
                Term term = new Term("user", "userEmail", map.get("userEmail"), TermType.EQUAL);
                list.add(term);
            }
            // 封装进Condition
            condition.setOrCondition(list);
        }

        // 执行修改逻辑
        entityInfo.setCondition(condition);
        select();
    }

    private void login(){

    }

    private void sendAuthEmail(){

    }

    private void isAvailableEmail(){

    }
}
