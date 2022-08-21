package com.bluedot.utils;



import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;


public class ReflectUtil {
    public static Object invokeGet(Object object, String fieldName) {
        Class<?> clazz = object.getClass();
        Method method;
        Object res = null;

        try {
            method = clazz.getDeclaredMethod("get" + StringUtil.firstCharToUpperCase(fieldName));
            res = method.invoke(object);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {}

        return res;
    }

    public static void invokeSet(Object obj, String columnName, Object value) {
        Class<?> clazz = obj.getClass();
        Method method;
        try {
            method = clazz.getDeclaredMethod("set" +  StringUtil.firstCharToUpperCase(columnName), value.getClass());
            method.setAccessible(true);
            method.invoke(obj, value);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {}
    }

    /**
     * 调用obj中的set方法注入map中对应的数值
     * @param map 装有属性名和对应的属性值
     * @param obj 执行set方法的对象
     */
    public static void invokeSetters(Map<String,Object> map, Object obj){
        map.forEach((k,v)->{
            invokeSet(obj,k,v);
        });
    }
}
