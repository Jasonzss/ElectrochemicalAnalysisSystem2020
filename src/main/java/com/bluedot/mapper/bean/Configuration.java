package com.bluedot.mapper.bean;

import com.bluedot.mapper.dataSource.MyDataSource;
import com.bluedot.mapper.dataSource.impl.MyDataSourceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Configuration {

    private static Properties pros = new Properties();
    private static   Map<String, MappedStatement> mappedStatementMap = new HashMap<>();
    private static   Map<Class<?>, TableInfo> classToTableInfoMap = new HashMap<>();

    private  MyDataSource dataSource;


    public void  load(InputStream inputStream) throws IOException {
        pros.load(inputStream);
        dataSource = MyDataSourceImpl.getInstance();
    }
    public void addMappedStatement(String sqlId, MappedStatement mappedStatement) {
        this.mappedStatementMap.put(sqlId, mappedStatement);
    }

    public MappedStatement getMappedStatement(String sqlId) {
        return this.mappedStatementMap.get(sqlId);
    }

    public MyDataSource getDataSource() {
        return dataSource;
    }

    public static String getProperty(String key) {
        return getProperty(key, "");
    }
    public static String getProperty(String key, String defaultValue) {
        return pros.containsKey(key) ? pros.getProperty(key) : defaultValue;
    }

    public Map<Class<?>, TableInfo> getClassToTableInfoMap() {
        return classToTableInfoMap;
    }
    public void setClassToTableInfoMap(Map<Class<?>, TableInfo> classToTableInfoMap) {
        this.classToTableInfoMap = classToTableInfoMap;
    }
}
