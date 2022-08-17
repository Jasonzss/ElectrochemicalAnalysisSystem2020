package com.bluedot.mapper;

import com.bluedot.mapper.bean.Configuration;
import com.bluedot.mapper.bean.MappedStatement;
import com.bluedot.mapper.dataSource.MyDataSource;
import com.bluedot.utils.ReflectUtil;
import com.bluedot.utils.StringUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/**
 * @author zlj
 * @version 1.0
 * @description: Mapper层执行器
 * @date 2022/8/16 16:37
 */
public class Executor {
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
                throw  new RuntimeException("连接失败");
            }
        } catch (SQLException e) {
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
                throw new RuntimeException(" sql is null.");
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
                List<E> result = new ArrayList<>();
                if (null == resultSet) {
                    return null;
                }
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

                while (resultSet.next()) {
                    String rowObjectType = mappedStatement.getReturnType();
                    E rowObject;
                    if (rowObjectType.endsWith("Long")) {
                        rowObject = (E) resultSet.getObject(1);
                    } else {
                        rowObject = (E) Class.forName(rowObjectType).newInstance();

                        for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {

                            String columnName = resultSetMetaData.getColumnLabel(i + 1);
                            Object columnValue = resultSet.getObject(i + 1);

                            if(columnValue!=null){

                                ReflectUtil.invokeSet(rowObject, columnName, columnValue);
                            }
                        }
                    }
                    result.add(rowObject);
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } catch (Exception e) {
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
                throw new RuntimeException(" sql is null.");
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

            Integer res = (Integer)  preparedStatement.executeUpdate();
            if (null != res) {
                return res;
            } else {
                throw new RuntimeException("更新数据错误");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            dataSource.returnConnection(connection);
        }
    }
}
