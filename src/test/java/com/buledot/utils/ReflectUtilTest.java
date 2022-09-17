package com.buledot.utils;

import com.bluedot.pojo.entity.ExpData;
import com.bluedot.pojo.entity.MaterialType;
import com.bluedot.pojo.entity.User;
import com.bluedot.utils.ReflectUtil;
import org.apache.commons.math3.analysis.function.Exp;
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
    public void test03(){
        ExpData expData = new ExpData();
        ReflectUtil.invokeSet(expData,"expDataId","15");
        System.out.println(expData.toString());
    }

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
        ReflectUtil.invokeSettersIncludeEntityByTypeAndName(map,user);
        System.out.println(user.toString());
    }

    @Test
    public void test02(){
        Map<String,Object> map = new HashMap<>();
        map.put("userEmail","123@qq.com");
        map.put("wadw",151);
        map.put("adwdwd",151);
        map.put("userTel","10086");
        map.put("materialTypeId","12");
        map.put("bufferSolutionId","1515");
        map.put("dawda",151);

        ExpData expData = new ExpData();
        ReflectUtil.invokeSettersIncludeEntityByTypeAndName(map,expData);
        System.out.println(expData.toString());
    }
}
