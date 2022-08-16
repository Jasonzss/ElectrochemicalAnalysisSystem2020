package com.bluedot.Adapt;



import java.lang.reflect.InvocationTargetException;

public class Adapt extends Thread {
    private Data data;
    //继续执行线程
//    public synchronized void resume(){
//        notify();
//    }

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
