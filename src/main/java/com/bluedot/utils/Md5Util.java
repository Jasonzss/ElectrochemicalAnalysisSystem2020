package com.bluedot.utils;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/9/3 16:33
 * @created: md5加密工具类
 */
public class Md5Util {


    /**
     * 将明文字符串转化为md5加密字符串
     * @param origin 原始字符串
     * @param salt 加盐字符串
     * @return 加密后的字符串
     */
    public static String transformToSaltMd5(String origin,String salt){
        try {
            // 获取Md5算法加密对象
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");

            // 加盐密码
            String source = origin + salt;

            // 将获取到的明文密码的字节数组,进行算法加密,并获取到加密结果
            byte[] digest = messageDigest.digest(source.getBytes());

            // 创建BigInteger对象,bigInteger的第一个参数(1表示正数),第二个参数表示字节数据
            BigInteger bigInteger = new BigInteger(1,digest);

            // 按照16进制将bigInteger转为大写字符串
            return bigInteger.toString(16).toLowerCase();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new UserException(CommonErrorCode.E_6001);
        }
    }

    /**
     * 不加盐的Md5，原生Md5
     * @param origin 密码
     * @return 原生Md5加密字符串
     */
    public static String transformToMd5(String origin){
        return transformToSaltMd5(origin,null);
    }


    /**
     * 生成n位随机盐
     */
    public static String getRandomSalt(int n){
        char[] chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz~!@#$^%&*()_+{}:<>?".toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            char c = chars[new Random().nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }


    /**
     * 验证明文密码加盐后是否和数据库中密码匹配
     * @param origin  原来的密码
     * @param salt    盐
     * @param saltMd5 存储在数据库中，加盐后的密码
     * @return 密码是否正确
     */
    public static boolean verifySaltMd5(String origin,String salt,String saltMd5){
        return saltMd5.equals(transformToSaltMd5(origin,salt));
    }

}
