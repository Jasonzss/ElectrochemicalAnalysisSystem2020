package com.buledot.service;

import com.bluedot.pojo.entity.ExpData;
import com.bluedot.pojo.entity.User;
import org.junit.Test;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Jason
 * @CreationDate 2022/09/25 - 1:29
 * @Description ：
 */
public class BaseServiceTest {
    @Test
    public void test(){
        List<Object> list = new ArrayList<>();
        list.add(new ExpData());
        list.add(new User());

        for (Object obj : list) {
            Field[] declaredFields = obj.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                //判断外键实体的主键
                System.out.println(field.getType().getTypeName());
                System.out.println(field.getType().getSimpleName());
                System.out.println(field.getName());
            }
            System.out.println("-------------------");
        }
    }

    @Test
    public void test01(){
        double b = 5.215333333333;
        double c = 5.333333333;
        double d = 0.000004333333;

        DecimalFormat a = new DecimalFormat("0.##");

        String frmStrb = a.format(b);
        String frmStrc = a.format(c);
        String frmStrd = a.format(d);

        System.out.println(frmStrb);
        System.out.println(frmStrc);
        System.out.println(frmStrd);
    }
}
