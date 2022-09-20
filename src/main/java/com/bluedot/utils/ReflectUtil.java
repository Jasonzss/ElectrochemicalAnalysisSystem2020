package com.bluedot.utils;



import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;


public class ReflectUtil {
    /**
     * 反射调用目标对象的指定属性的get方法
     * @param object 目标对象
     * @param fieldName 指定属性名
     * @return 通过get方法得到的指定属性
     */
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

    /**
     * 通过反射调用目标对象的指定属性的set方法，为其注入值
     * @param obj 目标对象
     * @param columnName 指定属性名
     * @param value 为属性注入的值
     */
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
     * 通过方法名匹配，反射调用目标对象的指定属性的set方法，为其注入值。
     * 如果注入值与参数值的类型不一致，且注入值为String类型，则会尝试将String转成目标属性类型
     * @param obj 目标对象
     * @param columnName 指定属性名
     * @param value 为属性注入的值
     */
    public static void invokeSetByTypeAndName(Object obj, String columnName, Object value) {
        Class<?> clazz = obj.getClass();
        Method setter = null;
        //获得所有方法
        Method[] methods = clazz.getDeclaredMethods();
        Map<String, Method> methodMap = new HashMap<>();
        for (Method item : methods) {
            methodMap.put(item.getName(), item);
        }

        String setName = "set" +  StringUtil.firstCharToUpperCase(columnName);

        //先尝试byType
        try {
            setter = clazz.getDeclaredMethod("set" +  StringUtil.firstCharToUpperCase(columnName), value.getClass());
        } catch (NoSuchMethodException ignored) { }

        if (setter == null){
            //尝试byName
            if (methodMap.containsKey(setName)){
                //有此方法名
                setter = methodMap.get(setName);
                Class<?>[] parameterTypes = setter.getParameterTypes();
                Class<?> parameterType = parameterTypes[0];

                if (parameterType != value.getClass()){
                    //参数类型不一致
                    if (Double.class.equals(parameterType)) {
                        value = Double.valueOf(String.valueOf(value));
                    } else if (Integer.class.equals(parameterType)) {
                        value = Integer.valueOf(String.valueOf(value));
                    } else if (Long.class.equals(parameterType)) {
                        value = Long.valueOf(String.valueOf(value));
                    } else if (Boolean.class.equals(parameterType)) {
                        value = Boolean.valueOf(String.valueOf(value));
                    } else if (Float.class.equals(parameterType)) {
                        value = Float.valueOf(String.valueOf(value));
                    } else if (Byte.class.equals(parameterType)) {
                        value = Byte.valueOf(String.valueOf(value));
                    }
                }
            }
        }

        if (setter != null){
            //参数类型一致
            try {
                setter.invoke(obj, value);
            } catch (IllegalAccessException | InvocationTargetException ignored) {}
        }
    }

    /**
     * 调用obj中的set方法注入map中对应的数值
     * @param map 装有属性名和对应的属性值
     * @param obj 执行set方法的对象
     */
    public static void invokeSetters(Map<String,Object> map, Object obj){
        map.forEach((k,v)-> invokeSet(obj,k,v));
    }

    /**
     * 调用obj中的set方法注入map中对应的数值，set方法使用invokeSetByName匹配
     * @param map 装有属性名和对应的属性值
     * @param obj 执行set方法的对象
     */
    public static void invokeSettersByTypeAndName(Map<String,Object> map, Object obj){
        map.forEach((k,v)-> invokeSetByTypeAndName(obj,k,v));
    }

    /**
     * 调用obj中的set方法注入map中对应的数值，如果目标属性为属性实体的属性，则将目标属性注入到该实体的属性中
     * 比如ExpData中存在User实体类，需要向ExpData中注入userEmail属性，则userEmail会被注入到ExpData中的User的userEmail属性中
     * @param map 装有属性名和对应的属性值
     * @param obj 执行set方法的对象
     */
    public static void invokeSettersIncludeEntity(Map<String,Object> map, Object obj){
        List<?> classList= Arrays.asList(Byte.class,Short.class,Integer.class,Long.class,Float.class,Double.class,Character.class,Boolean.class,String.class, Date.class, Timestamp.class, Double[][].class, Double[].class, Byte[].class, byte[].class);
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


    /**
     * 调用obj中的set方法注入map中对应的数值，如果目标属性为属性实体的属性，则将目标属性注入到该实体的属性中
     * set方法的匹配使用先byType再byName的方式
     * 比如ExpData中存在User实体类，需要向ExpData中注入userEmail属性，则userEmail会被注入到ExpData中的User的userEmail属性中
     * @param map 装有属性名和对应的属性值
     * @param obj 执行set方法的对象
     */
    public static void invokeSettersIncludeEntityByTypeAndName(Map<String,Object> map, Object obj){
        List<?> classList= Arrays.asList(Byte.class,Short.class,Integer.class,Long.class,Float.class,Double.class,Character.class,Boolean.class,String.class, Date.class, Timestamp.class, Double[][].class, Double[].class, Byte[].class, byte[].class);
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
                invokeSetByTypeAndName(obj,k,v);
            }else {
                //不存在，挨个在非基本属性中找
                syntheticList.forEach((e)->{
                    //获得obj中的非基本对象
                    Object o = invokeGet(obj, e);
                    //往非基本对象中注入属性值
                    invokeSettersByTypeAndName(map,o);
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
