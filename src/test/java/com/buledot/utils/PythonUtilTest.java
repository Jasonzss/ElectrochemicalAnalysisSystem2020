package com.buledot.utils;

import com.bluedot.utils.PythonUtil;
import org.junit.Test;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/8/31 19:23
 * @created:
 */
public class PythonUtilTest {

    @Test
    public void test(){
        Double[] data = new Double[]{0.001, 0.002, 0.003, 0.004, 0.005, 0.0123, 0.0089810,
                0.001, 0.002, 0.003, 0.004, 0.005, 0.0123, 0.0089810,
                0.001, 0.002, 0.003, 0.004, 0.005, 0.0123, 0.0089810,
                0.011, 0.012312, 0.012342, 0.01557, 0.02888, 0.0075686, 0.00980};
//        Map<String, Object> map = (Map<String, Object>) executePythonAlgorithFile("2.py", data,ExecuteReturnType.JSON.value);
//        map.forEach((k,v)->{
//            System.out.println("key::"+k);
//            System.out.println("value::"+v);
//        });
        Object o = PythonUtil.executePythonAlgorithFile("2.py", data, PythonUtil.ExecuteReturnType.PICTURE);
        System.out.println(o);
    }
}
