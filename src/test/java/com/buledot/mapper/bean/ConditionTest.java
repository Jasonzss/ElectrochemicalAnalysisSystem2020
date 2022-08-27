package com.buledot.mapper.bean;

import com.bluedot.mapper.bean.Condition;
import com.bluedot.pojo.entity.User;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Jason
 * @CreationDate 2022/08/26 - 22:27
 * @Description ：
 */
public class ConditionTest {
//    @Test
    public void setFieldsTest(){
        Condition condition = new Condition();
        List<String> list = new ArrayList<>();
        list.add("userImg");
        condition.setFieldsInEntityExcept(User.class,list);
        System.out.println(condition.toString());
    }
}
