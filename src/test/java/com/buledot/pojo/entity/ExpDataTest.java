package com.buledot.pojo.entity;

import com.bluedot.pojo.entity.ExpData;
import org.junit.Test;

import java.util.Arrays;

/**
 * @Author Jason
 * @CreationDate 2022/09/02 - 10:44
 * @Description ：
 */
public class ExpDataTest {
    @Test
    public void test01(){
        ExpData expData = new ExpData();
        String a = "[\"12\",\"51\",\"55\",\"66\"]";
        System.out.println(Arrays.toString(expData.stringToDoubleArray(a)));
    }
}
