package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.EntityInfo;
import com.bluedot.mapper.bean.Term;
import com.bluedot.mapper.bean.TermType;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.Permission;
import com.bluedot.pojo.entity.User;
import com.bluedot.pojo.vo.CommonResult;
import com.bluedot.utils.*;
import com.bluedot.utils.constants.SessionConstants;
import org.apache.commons.fileupload.FileItem;

import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author Jason
 * @CreationDate 2022/08/16 - 21:45
 * @Description ：
 */
public class UserService extends BaseService<User> {

    public UserService(Data data) {
        super(data);
    }

    public UserService(HttpSession session, EntityInfo<?> entityInfo) {
        super(session, entityInfo);
    }

    /**
     * 负责在UserService中个根据父类属性分析调用哪些方法来解决请求
     */
    @Override
    protected void doService() {
        String userEmail = (String) session.getAttribute(SessionConstants.USER_EMAIL);
        List<Map<String,Object>> userList = null;

        if (paramList.get("user") instanceof List){
            userList = (List<Map<String, Object>>) paramList.get("user");
        }

        String methodName;
        switch (operation) {
            case "delete":
                if (paramList.size() == 0 && userList == null){
                    methodName = "logout";
                } else {
                    if (userList != null) {
                        methodName = "deleteUser";
                    } else {
                        methodName = "deletePersonalUser";
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
                    if (paramList.get("userPassword") != null) {
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

    @Override
    protected boolean check() {
        return true;
    }

    /**
     * 管理员权限的修改用户信息
     */
    private void updateUser(){
        //判断是否修改了图片
        if (paramList.containsKey("userImg")){
            FileItem userImg = (FileItem) paramList.get("userImg");
            //将图片转换为二进制数组
            byte[] bytes = ImageUtil.imgToByteArray(userImg);
            paramList.put("userImg",bytes);
        }

        User user = new User();
        ReflectUtil.invokeSettersIncludeEntity(paramList,user);

        //执行更新逻辑
        entityInfo.addEntity(user);
        update();
    }

    /**
     * 个人权限修改用户信息
     */
    private void updatePersonalUser(){

        // 判断是否有密码
        if (paramList.containsKey("userPassword")){
            //判断是邮箱验证码修改密码还是密码验证修改
            if (session.getAttribute(SessionConstants.AUTH_CODE) != null) {
                //邮箱验证修改密码
                //获取验证码
                String authCode = (String) session.getAttribute(SessionConstants.AUTH_CODE);
                //移除验证码
                session.removeAttribute(SessionConstants.AUTH_CODE);
                //判断验证码是否正确
                if (!authCode.equalsIgnoreCase((String) paramList.get("authCode"))){
                    throw new UserException(CommonErrorCode.E_1004);
                }
            } else if (paramList.containsKey("oldPassword")){
                //旧密码验证修改密码
                //判断旧密码是否正确
                getPersonalUser();
                User user = (User) commonResult.getData();
                String userPassword = user.getUserPassword();
                if (!userPassword.equals(paramList.get("oldPassword"))){
                    throw new UserException(CommonErrorCode.E_1004);
                }
            } else {
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
            paramList.put("userImg", ImageUtil.imgToByteArray((FileItem) paramList.get("userImg")));
        }

        // 封装User实体
        User user = new User();
        ReflectUtil.invokeSettersIncludeEntity(paramList, user);

        // 执行修改逻辑
        entityInfo.addEntity(user);
        update();
    }

    /**
     * 注册新用户
     */
    private void insertUser(){
        String authCode = (String) session.getAttribute(SessionConstants.AUTH_CODE);
        //移除验证码
        session.removeAttribute(SessionConstants.AUTH_CODE);
        //判断邮箱验证码是否正确
        if (authCode.equalsIgnoreCase((String) paramList.get("authCode"))){
            //验证码不正确
            throw new UserException(CommonErrorCode.E_1004);
        }

        //判断邮箱是否可用
        getPersonalUser();
        User user = (User) commonResult.getData();
        if (user != null){
            //邮箱已注册，无法再注册
            throw new UserException(CommonErrorCode.E_1002);
        }else {
            //邮箱未注册，进行新用户的注册
            user = new User();
        }

        //执行插入操作
        ReflectUtil.invokeSettersIncludeEntity(paramList,user);
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
        condition.setReturnType("User");
        // 判断搜索用户的各项属性
        condition.addAndConditionWithView(new Term("user","user_email",userEmail,TermType.EQUAL));

        //判断是否查询图片
        if (paramList.get("userImg") == null){
            //查询图片以外的信息
            List<String> list = new ArrayList<>();
            list.add("userImg");
            condition.setFieldsInEntityExcept(User.class,list);

            // 执行查询逻辑
            entityInfo.setCondition(condition);
            select();

            //重新封装数据
            List<User> userList = (List<User>) commonResult.getData();
            if (userList.size() != 0){
                commonResult = CommonResult.successResult("用户信息",userList.get(0));
            }else {
                throw new UserException(CommonErrorCode.E_1005);
            }
        }else {
            //查询图片
            condition.addFields("user_img");

            // 执行查询逻辑
            entityInfo.setCondition(condition);
            select();

            //获取查询的图片数据
            List<User> userList = (List<User>) commonResult.getData();
            if (userList.size() != 0){
                User user = userList.get(0);
                byte[] userImg = user.getUserImg();

                commonResult = CommonResult.successResult("",null);
                commonResult.setRespContentType(CommonResult.INPUT_STREAM_IMAGE);
                if (userImg == null){
                    try {
                        //用户未设置头像，返回默认头像
                        URL url = getClass().getClassLoader().getResource("image/userImg.jpg");
                        File file = new File(url.toExternalForm().substring(6).replaceAll( "%20"," "));
                        FileInputStream fileInputStream = new FileInputStream(file);
                        commonResult.setFileData("userImg",fileInputStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }else {
                    //用户设置了头像，返回用户头像
                    commonResult.setFileData("userImg",new ByteArrayInputStream(userImg));
                }
            }else {
                throw new UserException(CommonErrorCode.E_1005);
            }
        }
    }

    /**
     * 管理员权限的查询用户
     */
    private void listUsers(){
        Long pageNo = Long.valueOf(((Integer)paramList.get("pageNo")).longValue());
        Integer pageSize = (Integer) paramList.get("pageSize");

        // 封装Condition
        Condition condition = new Condition();
        condition.setStartIndex((pageNo-1)*pageSize);
        condition.setSize(pageSize);
        condition.setReturnType("User");
        condition.addView("user");

        // 判断搜索用户的各项属性
        if (paramList.size() != 0){
            if (paramList.containsKey("userName")){
                condition.addOrConditionWithView(new Term("user","user_name",paramList.get("userName"), TermType.LIKE));
            }
            if (paramList.containsKey("roleId")){
                condition.addOrConditionWithView(new Term("user_role","role_id",paramList.get("roleId"),TermType.EQUAL));
                condition.addViewCondition("user_email","user_role");
            }
            if (paramList.containsKey("userEmail")){
                condition.addOrConditionWithView(new Term("user", "user_email", paramList.get("userEmail"), TermType.EQUAL));
            }
        }

        //设置不查询图片
        List<String> list = new ArrayList<>();
        list.add("userImg");
        condition.setFieldsInEntityExcept(User.class,list);

        // 执行修改逻辑
        entityInfo.setCondition(condition);
        selectPage();
    }

    /**
     * 登录
     */
    private void login(){
        //获取登录的参数
        String authCode = (String) session.getAttribute(SessionConstants.IMG_AUTH_CODE);
        if (authCode == null){
            throw new UserException(CommonErrorCode.E_1010);
        }
        System.out.println(authCode);

        //判断图片验证码是否正确
        if (!authCode.equalsIgnoreCase((String) paramList.get("imgAuthCode"))){
            throw new UserException(CommonErrorCode.E_1004);
        }

        //session中移除图片验证码
        session.removeAttribute(SessionConstants.IMG_AUTH_CODE);

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
        //select permission.permission_name from role_permission
        //  left join permission on permission.permission_id = role_permission.permission_id
        //  left join user_role on user_role.role_id = role_permission.role_id
        //where user_role.user_email = '2418972236@qq.com';
        Condition condition = new Condition();
        condition.addViewCondition("role_id","role_permission");
        condition.addViewCondition("permission_id","permission");
        condition.addViewCondition("role_id","user_role");
        condition.addAndConditionWithView(new Term("user_role", "user_email", paramList.get("userEmail"), TermType.EQUAL));
        condition.addFields("permission_name");
        condition.setReturnType("Permission");

        //执行查询逻辑
        entityInfo.setCondition(condition);
        select();

        //得到查询结果
        List<String> permissionList = new ArrayList<>();
        List<Permission> list = (List<Permission>) commonResult.getData();
        list.forEach((p) -> permissionList.add(p.getPermissionName()));
        //将权限名集合放入session中
        session.setAttribute(SessionConstants.PERMISSION_LIST,permissionList);

        //将userEmail放入session
        session.setAttribute(SessionConstants.USER_EMAIL,paramList.get("userEmail"));

        //返回token回前端
        Map<String,String> map = new HashMap<>();
        map.put("userEmail",user.getUserEmail());
        String tokenStr = JwtUtil.generateToken(map);
        commonResult = CommonResult.successResult("登录成功",tokenStr);
    }

    /**
     * 发送邮箱验证码，包括注册验证码，找回密码验证码
     */
    private void sendAuthEmail(){
        String userEmail = (String) paramList.get("userEmail");
        String title;
        String content;
        String authCode = EmailUtil.makeCode(6);
        User user = null;

        //查询此邮箱对应的用户
        try{
            getPersonalUser();
            user = (User) commonResult.getData();
        }catch (UserException e){
            if (e.getErrorCode() != CommonErrorCode.E_1005){
                //当抛出的异常为1005以外的其他异常才解决，1005异常不做处理
                throw new UserException(e.getErrorCode());
            }
        }

        //判断此邮箱是否已经注册
        if(user == null){
            //邮箱未注册,发送注册验证码
            title = userEmail+" 的注册邮件";
            content = "【蓝点电化学分析系统】 您正在使用邮箱【"+userEmail+"】进行用户注册。<br><h2 style='color:green'>"+authCode+"</h2>是你的验证码，请勿告诉他人，验证码仅在60秒内生效。<br>如非本人操作申请请忽略。";
        }else {
            //邮箱已注册，发送找回密码验证码
            title = user.getUserName()+" 的找回密码邮件";
            content = "【蓝点电化学分析系统】 您正在找回邮箱用户【"+userEmail+"】的密码。<br><h2 style='color:green'>"+authCode+"</h2>是你的验证码，请勿告诉他人，验证码仅在60秒内生效。<br>如非本人操作申请请忽略。";
        }

        //发送邮件
        EmailUtil emailUtil = new EmailUtil(userEmail,title,content);
        emailUtil.start();
        //将验证码放入session中
        session.setAttribute(SessionConstants.AUTH_CODE,authCode);

        //设置定时任务60s后删除验证码
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                session.removeAttribute(SessionConstants.AUTH_CODE);
            }
        };
        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(5,new ThreadPoolExecutor.CallerRunsPolicy());
        //定时一分钟
        long delay  = 60 * 1000L;
        executor.schedule(task, delay, TimeUnit.MILLISECONDS);
        executor.shutdown();

        //返回消息
        commonResult = CommonResult.successResult("验证码已发送，请及时接收",true);
    }

    /**
     * 用户账号登出
     */
    private void logout(){
        //移除session
        session.invalidate();
        commonResult = CommonResult.successResult("登出成功",true);
    }

    /**
     * 获得登录的图片验证码
     */
    private void getLoginImg(){
        //创建随机六位由数字字母组成的字符串
        String imgAuthCode = EmailUtil.makeCode(6);
        //创建对应的图片文件
        BufferedImage authImg = ImageUtil.createAuthImage(imgAuthCode);
        //将验证码记录到session中
        session.setAttribute(SessionConstants.IMG_AUTH_CODE,imgAuthCode);

        //将图片包装返回前端
        commonResult.setFileData("authImg.png",authImg);
        commonResult.setRespContentType(CommonResult.BUFFERED_IMAGE);

        //设置60秒后从session里移除验证码
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                session.removeAttribute(SessionConstants.IMG_AUTH_CODE);
            }
        };
        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(5,new ThreadPoolExecutor.CallerRunsPolicy());
        //定时一分钟
        long delay  = 60 * 1000L;
        executor.schedule(task, delay, TimeUnit.MILLISECONDS);
        executor.shutdown();
    }
}
