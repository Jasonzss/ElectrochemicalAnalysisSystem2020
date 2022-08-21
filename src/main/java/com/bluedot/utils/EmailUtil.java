package com.bluedot.utils;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Random;

/**
 * @Author Jason
 * @CreationDate 2022/08/18 - 17:38
 * @Description ：
 */
public class EmailUtil extends Thread{
    //用于给用户发送邮件的邮箱
    private final String from = "2418972236@qq.com";
    //邮箱的用户名
    private final String username = "Jason";
    //邮箱的授权码
    private final String password = "llpxlpcybjnbeaic";
    //发送邮件的服务器地址
    private final String host = "smtp.qq.com";

    //接收邮件的用户邮箱
    private String userEmail;
    //定义整个应用程序所需的环境信息的Session对象
    private Session session;
    //发送的邮件
    private MimeMessage message;

    /**
     * 构造方法，初始化发送邮件所需数据
     * @param userEmail 被发送邮件的用户邮箱
     */
    public EmailUtil(String userEmail){

    }

    /**
     * 给用户调用的发送邮件方法
     * @param messageType 发送邮件的类型
     * @return 是否发送成功
     */
    public boolean sendEmail(MessageType messageType){
        return false;
    }

    /**
     * 判断邮箱的格式是否正确
     * @param email 要判断的邮箱
     * @return 邮箱格式是否正确
     */
    public static boolean isLegalEmail(String email){
        return false;
    }

    /**
     * 创建一个线程发送邮件
     */
    @Override
    public void run() {
        try {
            Properties properties = new Properties();
            properties.setProperty("mail.host",host);
            properties.setProperty("mail.transport.protocol","smtp");
            properties.setProperty("mail.smtp.auth","true");

            //关于qq邮箱，还需要设置SSL加密，加上一下代码即可
            MailSSLSocketFactory mailSSLSocketFactory = new MailSSLSocketFactory();
            mailSSLSocketFactory.setTrustAllHosts(true);
            
            properties.put("mail.smtp.ssl.enable","true");
            properties.put("mail.smtp.ssl.socketFactory",mailSSLSocketFactory);

            //1.创建定义整个应用程序所需的环境信息的Session对象
            session = Session.getDefaultInstance(properties,new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    //发件人的邮箱用户名和授权码
                    return new PasswordAuthentication(from,password);
                }
            });

            //开启Session的debug模式，这样就可以查看到程序发送email的运行状态
            session.setDebug(true);

            //2.通过session得到transport对象
            Transport transport = session.getTransport();

            //3.使用邮箱的用户名和授权码连上邮件服务器
            transport.connect(host,username,password);

            //创建邮件
            message = new MimeMessage(session);
            //发件人
            message.setFrom(new InternetAddress(from));
            //收件人
            message.setRecipient(Message.RecipientType.TO,new InternetAddress(userEmail));

            //发送邮件
            transport.sendMessage(message,message.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建一个注册邮件
     */
    public void createSignInMessage(){
        try {
            //邮件的标题
            message.setSubject(userEmail+"的Determinantor注册邮箱");

            //编辑邮件内容
            String authCode = makeCode(6);
            String info = "欢迎加入行列使，我们收到您的注册请求。<br><h2 style='color:green'>"+authCode+"</h2>是你的验证码，请勿告诉他人，验证码仅在60秒内生效。<br>如非本人操作申请请忽略。";
            message.setContent(info,"text/html;charset=UTF-8");
            message.saveChanges();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成指定长度的随机字母数字字符串
     */
    public static String makeCode(int length){
        Random random = new Random();
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomNum = random.nextInt(str.length());
            code.append(str.charAt(randomNum));
        }
        System.out.println(code);
        return code.toString();
    }

    /**
     * 发送邮件的类型
     */
    public enum MessageType{
        /**
         * 注册验证码邮箱
         */
        SIGN_IN,
        /**
         * 找回密码验证码邮箱
         */
        FIND_PASSWORD,
        /**
         * 算法审核通过邮件
         */
        ALGORITHM_PASS,
        /**
         * 解封申请通过邮件
         */
        UNFREEZE_SUCCESS,
        /**
         * 解封申请未通过邮件
         */
        UNFREEZE_DEFEAT
    }
}
