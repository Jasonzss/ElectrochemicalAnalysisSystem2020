package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.Term;
import com.bluedot.mapper.bean.TermType;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.Permission;
import com.bluedot.pojo.entity.User;
import com.bluedot.pojo.vo.CommonResult;
import com.bluedot.utils.EmailUtil;
import com.bluedot.utils.ImageUtil;
import com.bluedot.utils.ReflectUtil;
import com.bluedot.utils.constants.SessionConstants;
import org.apache.commons.fileupload.FileItem;

import java.awt.image.BufferedImage;
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

    /**
     * 负责在UserService中个根据父类属性分析调用哪些方法来解决请求
     */
    @Override
    protected void doService() {
        String userEmail = (String) session.getAttribute("userEmail");
        List<Map<String,Object>> userList = null;
        User user = null;

        if (paramList.get("user") instanceof List){
            userList = (List<Map<String, Object>>) paramList.get("user");
        }else {
            ReflectUtil.invokeSetters(paramList,user);
        }

        String methodName;
        switch (operation) {
            case "delete":
                if (user == null && userList == null){
                    methodName = "logout";
                } else {
                    if (user != null) {
                        methodName = "deletePersonalUser";
                    } else {
                        methodName = "deleteUser";
                    }
                }
                break;
            case "update":
                if (userEmail.equals(paramList.get("userEmail"))) {
                    methodName = "updatePersonalUser";
                } else {
                    methodName = "updateUser";
                }
                break;
            case "select":
                if (paramList.get("pageNo") == null && paramList.get("pageSize") == null) {
                    if (paramList.get("password") != null) {
                        methodName = "login";
                    } else {
                        methodName = "getPersonalUser";
                    }
                } else {
                    methodName = "listUsers";
                }
                break;
            case "login":
                if (paramList.size() == 0){
                    methodName = "getLoginImg";
                }else if (paramList.get("authCode") != null){
                    methodName = "insertUser";
                }else if (paramList.get("imgAuthCode") != null){
                    methodName = "login";
                }else if (paramList.get("userEmail") != null){
                    methodName = "sendAuthEmail";
                }else {
                    throw new UserException(CommonErrorCode.E_5001);
                }
                break;
            default:
                throw new UserException(CommonErrorCode.E_5001);
        }

        invokeMethod(methodName,this);
    }

    /**
     * 管理员权限的修改用户信息
     */
    private void updateUser(){
        //判断是否修改了图片
        if (paramList.containsKey("userImg")){
            FileItem userImg = (FileItem) paramList.get("userImg");
            //将图片转换为二进制数组
            Byte[][] bytes = ImageUtil.imgToBinary(userImg);
            paramList.put("userImg",bytes);
        }
        
        User user = new User();
        ReflectUtil.invokeSetters(paramList,user);

        //执行更新逻辑
        entityInfo.addEntity(user);
        update();
    }

    /**
     * 个人权限修改用户信息
     */
    private void updatePersonalUser(){
        // 先将paramList中的参数取出

        // 判断是否有密码
        if (paramList.containsKey("userPassword")){
            if (!session.getAttribute("passwordAuth").equals(true) && !session.getAttribute("emailAuth").equals(true)) {
                throw new UserException(CommonErrorCode.E_1007);
            }
        }

        // 判断是否有用户状态
        if (paramList.containsKey("userStatus")){
            throw new UserException(CommonErrorCode.E_1006);
        }

        // 判断是否有图片
        if (paramList.containsKey("userImg")){
            // 将图片转换为数组放入
            paramList.put("userImg", ImageUtil.imgToBinary((FileItem) paramList.get("userImg")));
        }

        // 封装User实体
        User user = new User();
        ReflectUtil.invokeSetters(paramList, user);

        // 执行修改逻辑
        entityInfo.addEntity(user);
        update();
    }

    /**
     * 注册新用户
     */
    private void insertUser(){
        String authCode = (String) paramList.get("authCode");
        //判断邮箱验证码是否正确
        if (authCode.equalsIgnoreCase((String) session.getAttribute(SessionConstants.AUTH_CODE))){
            //验证码不正确
            throw new UserException(CommonErrorCode.E_1004);
        }

        //判断邮箱是否可用
        getPersonalUser();
        User user = (User) commonResult.getData();
        if (user != null){
            //邮箱已注册，无法再注册
            throw new UserException(CommonErrorCode.E_1002);
        }

        //执行插入操作
        ReflectUtil.invokeSetters(paramList,user);
        entityInfo.addEntity(user);
        insert();
    }

    /**
     * 管理员权限的删除用户
     */
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
    }

    /**
     * 个人权限的删除用户
     */
    private void deletePersonalUser(){
        //包装需要删除的实体类
        User user = new User();
        user.setUserEmail((String) paramList.get("userEmail"));
        entityInfo.addEntity(user);

        //执行删除逻辑
        delete();
    }

    /**
     * 个人权限的查询用户
     */
    private void getPersonalUser(){
        String userEmail = (String) paramList.get("userEmail");

        // 封装Condition
        Condition condition = new Condition();
        // 判断搜索用户的各项属性
        condition.addAndConditionWithView(new Term("user","userEmail",userEmail,TermType.EQUAL));

        // 执行修改逻辑
        entityInfo.setCondition(condition);
        select();

        //重新封装数据
        List<User> userList = (List<User>) commonResult.getData();
        User user = userList.get(0);
        commonResult.setData(user);
    }

    /**
     * 管理员权限的查询用户
     */
    private void listUsers(){
        // 封装Condition
        Condition condition = new Condition();
        condition.setStartIndex((Long) paramList.get("startIndex"));
        condition.setSize((Integer) paramList.get("pageSize"));

        // 判断搜索用户的各项属性
        if (paramList.size() != 0){
            List<Term> list = new ArrayList<>();
            if (paramList.containsKey("userName")){
                condition.addOrConditionWithView(new Term("user","userName",paramList.get("userName"), TermType.LIKE));
            }
            if (paramList.containsKey("roleId")){
                condition.addOrConditionWithView(new Term("user_role","roleId",paramList.get("roleId"),TermType.EQUAL));
            }
            if (paramList.containsKey("userEmail")){
                condition.addOrConditionWithView(new Term("user", "userEmail", paramList.get("userEmail"), TermType.EQUAL));
            }
        }

        // 执行修改逻辑
        entityInfo.setCondition(condition);
        select();
    }

    /**
     * 登录
     */
    private void login(){
        //获取登录的参数
        String authCode = (String) session.getAttribute("imgAuthCode");

        //判断图片验证码是否正确
        if (!authCode.equalsIgnoreCase((String) paramList.get("imgAuthCode"))){
            throw new UserException(CommonErrorCode.E_1004);
        }

        //session中移除图片验证码
        session.removeAttribute("imgAuthCode");

        //根据邮箱查询用户
        getPersonalUser();

        //是否存在此用户
        User user = (User) commonResult.getData();
        if (user == null){
            throw new UserException(CommonErrorCode.E_1008);
        }

        //判断密码是否正确
        if (!user.getUserPassword().equals(paramList.get("userPassword"))){
            throw new UserException(CommonErrorCode.E_1008);
        }

        //登录通过
        //查询权限列表放入session
        //编写查询逻辑
//        SELECT permission_name FROM user_role
//          LEFT JOIN role_permission ON user_role.role_id=role_permission.`role_id`
//          LEFT JOIN permission ON role_permission.`permission_id` = permission.`permission_id`
//        WHERE user_role.user_email = "2418972236@qq.com"
        Condition condition = new Condition();
        condition.addAndConditionWithView(new Term("user_role", "user_email", paramList.get("userEmail"), TermType.EQUAL));
        condition.addViewCondition("role_id","role_permission");
        condition.addViewCondition("permission_id","permission");
        condition.addFields("permission_name");

        //执行查询逻辑
        entityInfo.setCondition(condition);
//        entityInfo.setEntityName("Permission");
        select();

        //得到查询结果
        List<String> permissionList = new ArrayList<>();
        List<Permission> list = (List<Permission>) commonResult.getData();
        list.forEach((p) -> {
            permissionList.add(p.getPermissionName());
        });
        //将权限名集合放入session中
        session.setAttribute(SessionConstants.PERMISSION_LIST,permissionList);

        //将userEmail放入session
        session.setAttribute(SessionConstants.USER_EMAIL,paramList.get("userEmail"));

        //返回token回前端
        //?
    }

    /**
     * 发送邮箱验证码，包括注册验证码，找回密码验证码
     */
    private void sendAuthEmail(){
        getPersonalUser();
        User user = (User) commonResult.getData();
        EmailUtil emailUtil = new EmailUtil((String) paramList.get("userEmail"));

        if(user == null){
            //邮箱未注册,发送注册验证码
            emailUtil.sendEmail(EmailUtil.MessageType.SIGN_IN);
        }else {
            //邮箱已注册，发送找回密码验证码
            emailUtil.sendEmail(EmailUtil.MessageType.FIND_PASSWORD);
        }

        //返回消息
        commonResult = CommonResult.successResult("消息已发送，请及时接收",true);
    }

    /**
     * 用户账号登出
     */
    private void logout(){
        //移除session和token

    }

    /**
     * 获得登录的图片验证码
     */
    private void getLoginImg(){
        //创建随机六位由数字字母组成的字符串
        String imgAuthCode = EmailUtil.makeCode(6);
        //创建对应的图片文件
        BufferedImage authImg = ImageUtil.createAuthImg(imgAuthCode);
        //将验证码记录到session中
        session.setAttribute("imgAuthCode",imgAuthCode);

        //将图片包装返回前端
        commonResult.setData(authImg);

        //设置60秒后从session里移除验证码

    }
}
