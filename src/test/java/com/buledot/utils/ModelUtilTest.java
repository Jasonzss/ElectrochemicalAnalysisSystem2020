package com.buledot.utils;

import com.bluedot.utils.ModelUtil;
import org.junit.Test;

/**
 * @Author SDJin
 * @CreationDate 2022/9/3 16:54
 * @Description ：
 */
public class ModelUtilTest {
    @Test
    public void testMean() {
        Double[] data = new Double[]{1.1, 2.0, 3.0};
        System.out.println(ModelUtil.getMean(data));
    }
    @Test
    public void testR2(){
        Double[] reality = new Double[]{1.0, 2.1, 3.0};
        Double[] prediction = new Double[]{1.1, 2.1, 2.9};
        System.out.println(ModelUtil.getR2(reality, prediction));
    }
    @Test
    public void testRMSEE() {
        Double[] reality = new Double[]{1.0, 2.0, 3.0};
        Double[] prediction = new Double[]{1.1, 2.1, 2.9};
        System.out.println(ModelUtil.getRMSE(reality, prediction));
    }

    @Test
    public void testMAE() {
        Double[] reality = new Double[]{1.0, 2.0, 3.0};
        Double[] prediction = new Double[]{1.1, 2.1, 2.9};
        System.out.println(ModelUtil.getMAE(reality, prediction));
    }
    @Test
    public void testPRD(){
        Double[] reality = new Double[]{1.0, 2.0, 3.0};
        Double[] prediction = new Double[]{1.1, 2.1, 2.9};
        System.out.println(ModelUtil.getPRD(reality, prediction));
    }
}
