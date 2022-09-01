package com.buledot.utils;

import com.bluedot.pojo.entity.ExpData;
import com.bluedot.pojo.entity.User;
import com.bluedot.utils.ReflectUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Jason
 * @CreationDate 2022/08/20 - 17:24
 * @Description ：
 */
public class ReflectUtilTest {
    @Test
    public void test(){
        ExpData expData = new ExpData();
        Map<String,Object> map = new HashMap<>();
        map.put("expDataId",1);
        map.put("userEmail","2418@qq.com");

        ReflectUtil.invokeSettersIncludeEntity(map,expData);
        System.out.println(expData.toString());
    }

    @Test
    public void test01(){
        User user = new User();
        Map<String,Object> map = new HashMap<>();
        map.put("userEmail","123@qq.com");
        map.put("userTel","10086");
        ReflectUtil.invokeSettersIncludeEntity(map,user);
        System.out.println(user.toString());
    }
}
