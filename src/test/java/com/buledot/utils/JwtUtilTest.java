package com.buledot.utils;

import com.auth0.jwt.interfaces.Claim;
import com.bluedot.pojo.vo.CommonResult;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.bluedot.utils.JwtUtil.*;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/8/16 15:22
 * @created:
 */
public class JwtUtilTest {

    @Test
    public void test(){

        //放入token负载的自定义信息
        HashMap<String, String> map = new HashMap<>();
        map.put("id","2");
        map.put("email","xxx@qq.com");
        map.put("identity","管理员");
        String token = generateToken(map);
        System.out.println("生成token:"+token);

        boolean verify = verify(token);
        if (verify){
            System.out.println("token验证成功");
        }else {
            System.out.println("token验证失败");
        }

        Map<String, Claim> info = getTokenPlayLoadInfo(token);
        info.forEach((k,v) ->{
            System.out.print("key:"+k+",");
            System.out.println("value:"+v);
        });


        System.out.println("前端收到信息:"+ CommonResult.<String>builder()
                .code(200)
                .msg("登录成功")
                .data(token)
                .build()
                .mapValue());

        //前端设置，并每次请求携带
        //"Authorization":"Bearer "+token

    }

}
