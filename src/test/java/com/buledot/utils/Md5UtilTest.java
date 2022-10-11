package com.buledot.utils;

import com.bluedot.utils.Md5Util;
import org.junit.Test;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/9/3 17:14
 * @created: Md5Util测试
 */
public class Md5UtilTest {

    @Test
    public void test(){
        String saltMd5 = Md5Util.transformToSaltMd5("abc", "123");
        System.out.println(saltMd5);

        boolean b = Md5Util.verifySaltMd5("abc", "123", saltMd5);
        System.out.println(b);
    }

    @Test
    public void test1(){
        System.out.println(Md5Util.transformToSaltMd5("123456","qPY)6o"));
        System.out.println(Md5Util.verifySaltMd5("1234", "qPY)6o", "a9088749468789eb8dd0cb5547e35bc7"));
    }
}
