package com.buledot.utils;

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
        Map<String,Object> map = new HashMap<>();
        map.put("userEmail","123@qq.com");
        map.put("woc","woc");
        User user = new User();
        ReflectUtil.invokeSetters(map, user);
        System.out.println(user.getUserEmail());
    }
}
