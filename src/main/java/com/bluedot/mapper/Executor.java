package com.bluedot.mapper;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.Configuration;
import com.bluedot.mapper.bean.MappedStatement;
import com.bluedot.mapper.bean.TableInfo;
import com.bluedot.mapper.dataSource.MyDataSource;
import com.bluedot.utils.LogUtil;
import com.bluedot.utils.ReflectUtil;
import com.bluedot.utils.StringUtil;
import com.bluedot.utils.constants.SessionConstants;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * @author zlj
 * @version 1.0
 * @description: Mapper层执行器
 * @date 2022/8/16 16:37
 */
public class Executor {
    private final Logger logger = LogUtil.getLogger();
    //数据库连接池
    private MyDataSource dataSource;
    //数据库连接
    private final Connection connection;
    //是否自动提交
    private boolean ifAutoCommit=true;

    public Executor(Configuration configuration) {
        dataSource = configuration.getDataSource();
        try {
            //获取数据库连接
            connection=dataSource.getConnection();
            if(connection==null){
                logger.error("Executor:获取数据库连接为null");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * @description:操作数据库查询数据
     * @author: zlj
     * @date: 2022/8/16 16:37
     * @param: [mappedStatement, parameter]
     * @return: java.util.List<E>
     **/
    public <E> List<E> doQuery(MappedStatement mappedStatement, Object parameter) {
        try {
            connection.setAutoCommit(ifAutoCommit);
            String sql = mappedStatement.getSql();
            PreparedStatement preparedStatement;
            if (StringUtil.isNotEmpty(sql)) {
                preparedStatement =  connection.prepareStatement(sql);
            } else {
                throw new UserException(CommonErrorCode.E_8002);
            }

            try {
                if (null != parameter) {
                    if (parameter.getClass().isArray()) {
                        Object[] params = (Object[]) parameter;
                        for (int i = 0; i < params.length; i++) {
                            preparedStatement.setObject(i + 1, params[i]);
                        }
                    } else if (parameter instanceof Collection){
                        List list= (List) parameter;
                        for (int i = 0; i < list.size(); i++) {
                            preparedStatement.setObject(i + 1, list.get(i));
                        }
                    }
                    else  {
                        preparedStatement.setObject(1, parameter);
                    }
                }
            } catch (SQLException throwable) {throwable.printStackTrace();}

            ResultSet resultSet =  preparedStatement.executeQuery();
            try {
                String entity = Configuration.getProperty(SessionConstants.ENTITY_PACKAGENAME)+"."+mappedStatement.getView();
                TableInfo tableInfo = MapperInit.getConfiguration().getClassToTableInfoMap().get(Class.forName(entity));
                //表主键名
                String primaryName = tableInfo.getPrimaryKeys().get(0).getName();
                logger.debug("获取主表主键名"+primaryName);
                Map<Object,Object> map=new HashMap();
                //查询结果
                List<E> result = new ArrayList<>();
                if (null == resultSet) {
                    logger.warn("查询结果为空");
                    return null;
                }
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                //封装数据
                while (resultSet.next()) {
                    //行数据主键值
                    Object primaryValue =null;
                    if (sql.indexOf("count(*)") ==-1 && sql.indexOf("GROUP")==-1){
                        primaryValue = resultSet.getObject(primaryName);
                    }
                    //每一行结果
                    E rowObject = null;
                    //返回值类型vo
                    String rowObjectType = mappedStatement.getReturnType();
                    if (rowObjectType.endsWith("Long")) {
                        rowObject = (E) resultSet.getObject(1);
                        logger.debug("每一行查询结果："+rowObject);
                        result.add(rowObject);
                        return result;
                    }
                    if (mappedStatement.getView().equals(mappedStatement.getReturnType()) && sql.indexOf("count(*)") ==-1&& map.containsKey(primaryValue)) {
                        rowObject = (E) map.get(primaryValue);
                    } else {
                        rowObject = (E) Class.forName(Configuration.getProperty(SessionConstants.ENTITY_PACKAGENAME)+"."+rowObjectType).newInstance();
                    }
                    Object listEntity = null;

                    Field[] fields = rowObject.getClass().getDeclaredFields();
                    Map<String, Object> foreignKeyEntityMap = new HashMap<>();
                    List foreignKeyEntityList = new ArrayList();

                    List<Class> classList=Arrays.asList(Byte.class,Short.class,Integer.class,Long.class,Float.class,Double.class,char.class,Boolean.class,String.class,Date.class,Timestamp.class);
                    //遍历返回值实体类 所有成员变量
                    for (Field field : fields) {
                        //是否是外键实体类
                        boolean flag=true;
                        for (Class clazz : classList) {
                            field.setAccessible(true);
                            if (field.getType()==clazz){
                                flag=false;
                                break;
                            }
                        }
                        //不是外键实体类，跳过
                        if (flag==false){
                            continue;
                        }
                        if(field.getType()==byte[].class){
                            String fieldName = StringUtil.humpToLine(field.getName());
                            //判断查询结果集中是否存在此列
                            try{
                                if (resultSet.findColumn(fieldName) > 0){
                                    //存在此列
                                    byte[] bytes = resultSet.getBytes(fieldName);
                                    field.setAccessible(true);
                                    field.set(rowObject,bytes);
                                }
                            }catch (SQLException ignored){
                                //无事发生，忽略对实体类中的此属性赋值
                            }
                            continue;
                        }
                        if (field.getType()==List.class || field.getType()==ArrayList.class) {
                            if (field.get(rowObject) == null) {
                                String fieldName = field.getName();
                                List foreignKeyList=new ArrayList();
                                ReflectUtil.invokeSetAttribute(rowObject,fieldName,foreignKeyList);
                            }
                            Type genericType = field.getGenericType();
                            if (genericType instanceof ParameterizedType) {
                                ParameterizedType pt = (ParameterizedType) genericType;
                                // 得到泛型里的class类型对象
                                Class<?> actualTypeArgument = (Class<?>) pt.getActualTypeArguments()[0];
                                listEntity = actualTypeArgument.newInstance();
                            }
                            List foreignKeyList = (List) field.get(rowObject);
                            foreignKeyList.add(listEntity);

                            foreignKeyEntityList.add(listEntity);
                        }
                        else {
                            if(field.get(rowObject)==null){
                                String name = field.getName();
                                Object foreignKeyEntity = field.getType().newInstance();
                                ReflectUtil.invokeSet(rowObject, name, foreignKeyEntity);
                                foreignKeyEntityMap.put(name, foreignKeyEntity);
                            }
                        }
                    }
                    //封装数据
                        for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {

                            String columnName = resultSetMetaData.getColumnLabel(i + 1);
                            Object columnValue = resultSet.getObject(i + 1);

                            String columnClassName = StringUtil.lineToHump(columnName);
                            //基本类型封装
                            Field[] declaredFields = rowObject.getClass().getDeclaredFields();
                            for (Field declaredField : declaredFields) {
                                if (declaredField.getName().equals(columnClassName)&& columnValue != null){
                                    ReflectUtil.invokeSet(rowObject, columnClassName, columnValue);
                                }
                            }

                            for (Object o : foreignKeyEntityList) {
                                Field[] declaredFields1 = o.getClass().getDeclaredFields();
                                for (Field declaredField1 : declaredFields1) {
                                    if (declaredField1.getName().equals(columnClassName)&& columnValue != null){
                                        ReflectUtil.invokeSet(o, columnClassName, columnValue);
                                    }
                                }
                            }
                            for (Object value : foreignKeyEntityMap.values()) {
                                Field[] declaredFields1 = value.getClass().getDeclaredFields();
                                for (Field field : declaredFields1) {
                                    if (field.getName().equals(columnClassName)&& columnValue != null){
                                        ReflectUtil.invokeSet(value, columnClassName, columnValue);
                                    }
                                }
                            }
                        }

                    if (sql.indexOf("count(*)") ==-1&&mappedStatement.getView().equals(mappedStatement.getReturnType()) && !map.containsKey(primaryValue)) {
                        map.put(primaryValue, rowObject);
                    }
                    if (!result.contains(rowObject)) {
                        result.add(rowObject);
                    }

                }
                logger.debug("查询结果:"+result.toString());
                return result;
            } catch (Exception exc) {
                logger.error("封装查询结果，出现异常:"+exc.getMessage());
                exc.printStackTrace();
                return null;
            }
        } catch (Exception e) {
            logger.error("执行查询sql，出现异常:"+e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }finally {
            dataSource.returnConnection(connection);
        }
    }

    /**
     * @description:操作数据库增删改数据
     * @author: zlj
     * @date: 2022/8/16 16:37
     * @param: [mappedStatement, parameter]
     * @return: int
     **/
    public int doUpdate(MappedStatement mappedStatement, Object parameter) {
        try {
            connection.setAutoCommit(ifAutoCommit);
            String sql = mappedStatement.getSql();
            PreparedStatement preparedStatement;
            if (StringUtil.isNotEmpty(sql)) {
                preparedStatement =  connection.prepareStatement(sql);
            } else {
                throw new UserException(CommonErrorCode.E_8002);
            }

                if (null != parameter) {
                    if (parameter.getClass().isArray()) {
                        Object[] params = (Object[]) parameter;
                        for (int i = 0; i < params.length; i++) {
                            preparedStatement.setObject(i + 1, params[i]);
                        }
                    } else if (parameter instanceof Collection){
                        List list= (List) parameter;
                        for (int i = 0; i < list.size(); i++) {
                            preparedStatement.setObject(i + 1, list.get(i));
                        }
                    }
                    else  {
                        preparedStatement.setObject(1, parameter);
                    }
                }

            Integer res = (Integer)  preparedStatement.executeUpdate();
                logger.debug("更新数据库结果行数:"+res);
                return res;

        } catch (Exception e) {
            logger.error("更新数据异常："+e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }finally {
            logger.debug("更新数据库完成，归还数据库连接");
            dataSource.returnConnection(connection);
        }
    }
}
