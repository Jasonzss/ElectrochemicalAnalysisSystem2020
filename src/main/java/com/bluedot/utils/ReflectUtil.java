package com.bluedot.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @Author Jason
 * @CreationDate 2022/08/15 - 11:52
 * @Description ：
 */
public class ReflectUtil {
    /**
     * @param param get方法对应的参数
     * @param obj 执行get方法的对象
     * @return get方法对象
     */
    public static Method getGetter(String param, Object obj){
        String getterName = "get" + param.substring(0,1).toUpperCase() + param.substring(1);
        try {
            return obj.getClass().getDeclaredMethod(getterName);
        } catch (NoSuchMethodException e) {
            System.out.println("找不到此参数或参数没有对应的get方法");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param param set方法对应的参数
     * @param obj 执行set方法的对象
     * @return set方法对象
     */
    public static Method getSetter(String param, Object obj){
        String setterName = "set" + param.substring(0,1).toUpperCase() + param.substring(1);
        Method[] methods = obj.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (setterName.equals(method.getName())) {
                //返回对应set方法
                return method;
            }
        }

        return null;
    }

    /**
     * @param param get方法对应的参数
     * @param obj 执行get方法的对象
     * @return get方法对应的参数
     */
    public static Object invokeGetter(String param, Object obj){
        Method getter = getGetter(param, obj);

        if (getter != null){
            try {
                //调用对应的get方法
                return getter.invoke(obj);
            } catch (IllegalAccessException e) {
                System.out.println("此方法不公开");
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * @param paramName set方法对应的参数名
     * @param paramValue set方法对应参数的值
     * @param obj 执行set方法的对象
     */
    public static void invokeSetter(String paramName, Object paramValue, Object obj){
        Method setter = getSetter(paramName, obj);

        if (setter != null){
            try {
                //调用对应的set方法
                setter.invoke(obj, paramValue);
            } catch (IllegalAccessException e) {
                System.out.println("此方法不公开");
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 调用obj中的set方法，将map中对应的 < key,value > 注入到实体类的属性中
     * @param map 装有属性名和对应的属性值
     * @param obj 执行set方法的对象
     */
    public static Object invokeSetters(Map<String,Object> map, Object obj){
        map.forEach((k,v)-> invokeSetter(k,v,obj));
        return obj;
    }
}
