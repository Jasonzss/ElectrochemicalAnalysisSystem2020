package com.buledot.utils;

import com.auth0.jwt.interfaces.Claim;
import com.bluedot.mapper.dataSource.MyDataSource;
import com.bluedot.mapper.dataSource.impl.MyDataSourceImpl;
import com.bluedot.pojo.vo.CommonResult;
import com.mysql.cj.jdbc.MysqlDataSourceFactory;
import org.junit.Test;

import java.sql.Connection;
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
    public void test() throws InterruptedException {
        Connection connection = MyDataSourceImpl.getInstance().getConnection();
        System.out.println(connection);
        //放入token负载的自定义信息
        HashMap<String, String> map = new HashMap<>();
        map.put("id","2");
        map.put("email","xxx@qq.com");
        map.put("identity","管理员");
        HashMap<String, String> map2 = new HashMap<>();
        map.put("id","2");
        map.put("email","xxx@qq.com");
        map.put("identity","管理员");
        String token = generateToken(map);
        String token2 = generateToken(map2);
        System.out.println("生成token1:"+token);
        System.out.println("生成token1:"+token2);
        boolean verify = verify(token);
        System.out.println(verify);
        Thread.sleep(2000);
        boolean verify1 = verify(token2);
        System.out.println(verify1);
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


//        System.out.println("前端收到信息:"+ CommonResult.<String>builder()
//                .code(200)
//                .msg("登录成功")
//                .data(token)
//                .build()
//                .mapValue());


        //前端设置，并每次请求携带
        //"Authorization":"Bearer "+token

    }

}
