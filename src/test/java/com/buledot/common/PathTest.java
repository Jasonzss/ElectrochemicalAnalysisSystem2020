package com.buledot.common;

import org.apache.poi.hssf.record.formula.functions.T;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/9/11 14:14
 * @created: 文件路径测试
 */
public class PathTest {

    /**
     * 测试获取当前类路径
     */
    @Test
    public void currentPathTest() throws IOException, ClassNotFoundException {
        String currentClassPath;

        String currentPath = PathTest.class.getName().replaceAll("[.]", "/");

        currentClassPath = currentPath + ".class";

        //通过当前线程获取到类加载器，使用类加载器获取指定路径的资源文件
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        String path = classLoader.getResource(currentClassPath).getPath();

        System.out.println(path);

        //通过类加载器加载某一个全类名类
        Class<?> clazz = classLoader.loadClass(PathTest.class.getName());

        System.out.println(clazz.getName());

    }

}
