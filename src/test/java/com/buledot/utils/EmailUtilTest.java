package com.buledot.utils;

import com.bluedot.utils.EmailUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Jason
 * @CreationDate 2022/08/23 - 18:18
 * @Description ：
 */
public class EmailUtilTest {
    @Test
    public void test(){
        List<String> emails = new ArrayList<>();
        String userEmail = "2418972236@qq.com";
        //测试一
//        String title;
//        String content;
//        String authCode = EmailUtil.makeCode(6);
//        title = userEmail+" 的注册邮件";
//        content = "【蓝点电化学分析系统】 您正在使用邮箱【"+userEmail+"】进行用户注册。<br><h2 style='color:green'>"+authCode+"</h2>是你的验证码，请勿告诉他人，验证码仅在60秒内生效。<br>如非本人操作申请请忽略。";

//        EmailUtil emailUtil = new EmailUtil(userEmail, title, content);
//        emailUtil.run();

        //测试二
//        emails.add("2418972236@qq.com");
//        emails.add("1542062901@qq.com");
//
//        List<EmailUtil> emailUtils = new ArrayList<>();
//
//        emails.forEach((e) -> {
//            String authCode = EmailUtil.makeCode(6);
//            String title = e+" 的注册邮件";
//            String content = "【蓝点电化学分析系统】 您正在使用邮箱【"+e+"】进行用户注册。<br><h2 style='color:green'>"+authCode+"</h2>是你的验证码，请勿告诉他人，验证码仅在60秒内生效。<br>如非本人操作申请请忽略。";
//            emailUtils.add(new EmailUtil(e, title, content));
//        });
//
//        emailUtils.get(0).run();
//        emailUtils.get(1).run();
    }
}
