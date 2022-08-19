package com.bluedot.adapt;



import com.bluedot.pojo.Dto.Data;

import java.lang.reflect.InvocationTargetException;
/**
 * @Author SDJin
 * @CreationDate 2022/08/16 - 11:54
 * @Description ：
 */
public class Adapt extends Thread {
    private Data data;


    public Adapt(Data data) {
        this.data=data;
    }

    @Override
    public void run() {
        String serViceName = data.getServiceName();
        try {
            Class<?> aClass = Class.forName(serViceName);
            aClass.getConstructor(Data.class).newInstance(data);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
