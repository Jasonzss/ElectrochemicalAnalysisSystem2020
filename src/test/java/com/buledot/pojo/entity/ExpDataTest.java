package com.buledot.pojo.entity;

import com.bluedot.pojo.entity.ExpData;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author Jason
 * @CreationDate 2022/09/02 - 10:44
 * @Description ：
 */
public class ExpDataTest {
    @Test
    public void test01(){
        ExpData expData = new ExpData();
        String a = "[12, 51, 55, 66]";
        Double[] doubles = expData.stringToDoubleArray(a);
        System.out.println(doubles[0]);
        System.out.println(doubles[1]);
        System.out.println(doubles[3]);
        System.out.println(Arrays.toString(expData.stringToDoubleArray(a)));

        List<Double> doubleList = new ArrayList<>();
        doubleList.add(12.5);
        doubleList.add(12.5);
        doubleList.add(12.5);
        doubleList.add(12.5);

        Double[] doubles1 = expData.stringToDoubleArray(doubleList.toString());
        System.out.println(doubles1[1]);
        System.out.println(doubles1[2]);
        System.out.println(doubles1[0]);
    }
}
