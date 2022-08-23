package com.bluedot.utils;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
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
    //邮件题头
    private String title;
    //邮件内容
    private String content;

    /**
     * 构造方法，初始化发送邮件所需数据
     * @param userEmail 被发送邮件的用户邮箱
     */
    public EmailUtil(String userEmail,String title,String content){
        this.userEmail = userEmail;
        this.title = title;
        this.content = content;
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
            //发件人的邮箱用户名和授权码
            //定义整个应用程序所需的环境信息的Session对象
            Session session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    //发件人的邮箱用户名和授权码
                    return new PasswordAuthentication(from, password);
                }
            });

            //开启Session的debug模式，这样就可以查看到程序发送email的运行状态
            session.setDebug(true);

            //2.通过session得到transport对象
            Transport transport = session.getTransport();

            //3.使用邮箱的用户名和授权码连上邮件服务器
            transport.connect(host,username,password);

            //4.创建邮件
            MimeMessage mimeMessage = new MimeMessage(session);
            //发件人
            mimeMessage.setFrom(new InternetAddress(from));
            //收件人
            mimeMessage.setRecipient(Message.RecipientType.TO,new InternetAddress(userEmail));

            //编辑邮件
            //邮箱的标题
            mimeMessage.setSubject(title);
            mimeMessage.setContent(content,"text/html;charset=UTF-8");
            mimeMessage.saveChanges();

            //发送邮件
            transport.sendMessage(mimeMessage,mimeMessage.getAllRecipients());
            transport.close();
        } catch (Exception e) {
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
     * 判断邮箱的格式是否正确
     * @param email 要判断的邮箱
     * @return 邮箱格式是否正确
     */
    public static boolean isLegalEmail(String email){
        return false;
    }
}
