package com.bluedot.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Map;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/8/16 15:29
 * @created: token工具类
 */
public class JwtUtil {

    //签证：系统秘钥
    private static final String SIGN = "西八@#!#@!fuck!@#$%^^&*(*))_+{{}|{}{}<?:<<?>>+_+_:?OAJDKLSJAKLDJKJWOIQJOW102i309182903812903813";

    /**
     * 生成token
     * @param map 将放入token负载的信息
     * @return token字符串
     */
    public static String generateToken(Map<String,String> map){

        //获取创建token的builder
        JWTCreator.Builder builder = JWT.create();

        //将map中的信息放入token的负载中
        map.forEach(builder::withClaim);

        //token过期时间设置
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE,1000);

        //创建token
        return builder.withExpiresAt(instance.getTime())
                        .sign(Algorithm.HMAC256(SIGN));
    }


    /**
     * 验证token
     * @param token 需要验证的token
     * @return 返回验证是否成功的结果
     */
    public static boolean verify(String token){
        try {
            //使用算法和签证解密token
            JWT.require(Algorithm.HMAC256(SIGN)).build().verify(token);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("token验证失败，拒绝通行!");
            return false;
        }
        return true;
    }


    /**
     * 获取token负载中的信息
     * @param token token
     * @return 封装负载信息的map
     */
    public static Map<String,Claim> getTokenPlayLoadInfo(String token){

        // 解码token，获取到token解码后的对象
        DecodedJWT decodedJWT = JWT.decode(token);
        // 获取token负载信息
        return decodedJWT.getClaims();
    }





}
