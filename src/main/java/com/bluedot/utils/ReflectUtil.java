package com.bluedot.utils;



import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;


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

    /**
     * 调用obj中的set方法注入map中对应的数值，如果目标属性为属性实体的属性，则将目标属性注入到该实体的属性中
     * 比如ExpData中存在User实体类，需要向ExpData中注入userEmail属性，则userEmail会被注入到ExpData中的User的userEmail属性中
     * @param map 装有属性名和对应的属性值
     * @param obj 执行set方法的对象
     */
    public static void invokeSettersIncludeEntity(Map<String,Object> map, Object obj){
        List<Class> classList= Arrays.asList(Byte.class,Short.class,Integer.class,Long.class,Float.class,Double.class,Character.class,Boolean.class,String.class, Date.class, Timestamp.class, Double[][].class, Double[].class, Byte[].class, byte[].class);
        List<String> syntheticList = new ArrayList<>();
        List<String> primitiveList = new ArrayList<>();

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field f:fields) {
            //判断当前属性是否为基本属性
            if (classList.contains(f.getType())){
                //基本类型
                primitiveList.add(f.getName());
            }else {
                //非基本属性，先反射使用无参构造创建一个对象
                //调用set方法将对象放入obj的属性中
                try {
                    invokeSet(obj,f.getName(),f.getType().newInstance());
                    syntheticList.add(f.getName());
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        map.forEach((k,v) -> {
            //判断当前属性在obj基本属性中是否存在
            if (primitiveList.contains(k)){
                //存在，注入
                invokeSet(obj,k,v);
            }else {
                //不存在，挨个在非基本属性中找
                syntheticList.forEach((e)->{
                    //获得obj中的非基本对象
                    Object o = invokeGet(obj, e);
                    //往非基本对象中注入属性值
                    invokeSetters(map,o);
                });
            }
        });
    }

    public static void invokeSetAttribute(Object obj, String columnName, Object value) {
        Class<?> clazz = obj.getClass();
        Method method;
        try {
            method = clazz.getDeclaredMethod("set" + StringUtil.firstCharToUpperCase(columnName), value.getClass());
            method.setAccessible(true);
            method.invoke(obj, value);
        } catch (Exception e) {
            throw new RuntimeException("[" + Thread.currentThread().getName() + "]" +
                    "com.xxbb.smybatis.utils.ReflectUtils" + "--->" + "value=" +
                    value.getClass());
        }

    }
}
