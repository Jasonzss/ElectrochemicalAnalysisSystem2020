package com.bluedot.pojo;

import com.bluedot.utils.LogUtil;
import com.bluedot.utils.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/8/17 10:51
 * @created: pojo类
 */
public class Pojo {

    /**
     * 通过map的key和实体类的属性名匹配的原则注入值
     * @param map 将要填充到实体类的map
     */
    public Pojo(Map<String,Object> map){
        try {
            Class<? extends Pojo> clazz = this.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                String fieldName = field.getName();
                if (map.containsKey(fieldName)){
                    Object value = map.get(fieldName);
                    Method method = clazz.getMethod("set" + StringUtil.firstCharToUpperCase(fieldName),value.getClass());
                    method.setAccessible(true);
                    method.invoke(this,value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getLogger().error("map参数值注入pojo类出错!错误信息:"+ e.getMessage());
        }
    }

}
