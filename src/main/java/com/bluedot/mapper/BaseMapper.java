package com.bluedot.mapper;


import com.bluedot.mapper.bean.*;
import com.bluedot.mapper.callBack.MyCallback;
import com.bluedot.pojo.vo.CommonResult;
import com.bluedot.utils.ReflectUtil;
import com.bluedot.utils.StringUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zlj
 * @version 1.0
 * @description: Mapper层核心类
 * @date 2022/8/16 16:15
 */
public class BaseMapper {
    //mapper执行器
    private Executor executor = new Executor(MapperInit.getConfiguration());
    //mapper实体类属性
    private Object entity;
    //mapper层返回结果
    private CommonResult commonResult;
    /**
     * @description:构造器传入，属性赋值
     * @author: zlj
     * @date: 2022/8/16 16:15
     * @param: [entityInfo]
     * @return:
     **/
    public BaseMapper(EntityInfo entityInfo) {
        entity = entityInfo.getEntity();
        doMapper(entityInfo);
    }
    /**
     * @description:调用方法
     * @author: zlj
     * @date: 2022/8/16 16:15
     * @param: [entityInfo]
     * @return:
     **/
    private void doMapper(EntityInfo entityInfo) {
        Object object = null;
//        根据查询类型，调用方法
        switch (entityInfo.getOperation()) {
            case "insert":
                object = insert(entity);
                break;
            case "delete":
                object = delete(entity);
                break;
            case "update":
                object = update(entity);
                break;
            case "select":
                object = select(entityInfo.getCondition());
                break;
        }
        //结果封装
        commonResult = new CommonResult.Builder<>().data(object).build();
        //将结果通过队列  返回给service层
        com.bluedot.queue.outQueue.impl.MapperServiceQueue.getInstance().put(entityInfo.getKey(), this.commonResult);
    }
    /**
     * @description:mapper层查询数据
     * @author: zlj
     * @date: 2022/8/16 16:15
     * @param: [entityInfo]
     * @return:
     **/
    private Object select(Condition condition) {
        List<Object> parameters = new ArrayList<>();
        //根据查询条件，动态生成sql
        String sql = generateSelectSqL(condition, parameters);
        MappedStatement mappedStatement = new MappedStatement();
        mappedStatement.setSql(sql);
        return this.executor.doQuery(mappedStatement,parameters);
    }
    /**
     * @description:mapper层插入数据
     * @author: zlj
     * @date: 2022/8/16 16:15
     * @param: [entityInfo]
     * @return:
     **/
    private <T> int insert(T type) {
        return (int) generateSqlTemplate(type, new MyCallback() {
           //回调接口，insert类型sql语句生成模板
            @Override
            public void generateSqlExecutor(Field[] fields, TableInfo tableInfo, List<ColumnInfo> primaryKeys, StringBuilder sql, MappedStatement mappedStatement, List<Object> params) {
                sql.append("insert into ").append(tableInfo.getTableName()).append("(");
                for (Field field : fields) {
                    String fieldName = field.getName();
                    Object fieldValue = ReflectUtil.invokeGet(type, fieldName);
                    if (null != fieldValue) {
                        sql.append(StringUtil.humpToLine(fieldName)).append(",");
                        params.add(fieldValue);
                    }
                }
                sql.setCharAt(sql.length() - 1, ')');
                sql.append(" values(");
                for (int i = 0; i < params.size(); i++) {
                    sql.append("?,");
                }
                sql.setCharAt(sql.length() - 1, ' ');
                sql.append("),");
            }
        });

    }
    /**
     * @description:mapper层删除数据
     * @author: zlj
     * @date: 2022/8/16 16:15
     * @param: [entityInfo]
     * @return:
     **/
    private <T> int delete(T type) {
        return (int) generateSqlTemplate(type, new MyCallback() {
            //回调接口，delete类型sql语句生成模板
            @Override
            public void generateSqlExecutor(Field[] fields, TableInfo tableInfo, List<ColumnInfo> primaryKeys, StringBuilder sql, MappedStatement mappedStatement, List<Object> params) {
                sql.append("delete from ").append(tableInfo.getTableName()).append(" where ");
                for (ColumnInfo columnInfo : primaryKeys) {
                    sql.append(StringUtil.humpToLine(columnInfo.getName())).append("=?,");
                    params.add(ReflectUtil.invokeGet(type, StringUtil.lineToHump(columnInfo.getName())));
                }
            }
        });
    }
    /**
     * @description:mapper层更新数据
     * @author: zlj
     * @date: 2022/8/16 16:15
     * @param: [entityInfo]
     * @return:
     **/
    private <T> int update(T type) {
        //回调接口，update类型sql语句生成模板
        return (int) generateSqlTemplate(type, new MyCallback() {
            @Override
            public void generateSqlExecutor(Field[] fields, TableInfo tableInfo, List<ColumnInfo> primaryKeys, StringBuilder sql, MappedStatement mappedStatement, List<Object> params) {
                sql.append("update").append(tableInfo.getTableName()).append(" set ");
                for (Field field : fields) {
                    String fieldName = field.getName();
                    Object fieldValue = ReflectUtil.invokeGet(type, fieldName);
                    if (null != fieldValue) {

                        for (ColumnInfo columnInfo : primaryKeys) {
                            if (!fieldName.equals(columnInfo.getName())) {

                                sql.append(StringUtil.humpToLine(fieldName)).append("=?,");

                                params.add(fieldValue);
                            }
                        }
                    }
                }

                sql.setCharAt(sql.length() - 1, ' ');

                sql.append("where ");
                for (ColumnInfo columnInfo : primaryKeys) {

                    sql.append(StringUtil.humpToLine(columnInfo.getName())).append("=?,");

                    params.add(ReflectUtil.invokeGet(type, StringUtil.lineToHump(columnInfo.getName())));
                }

            }
        });

    }
    /**
     * @description:mapper层增删改sql语句动态生成模板方法
     * @author: zlj
     * @date: 2022/8/16 16:15
     * @param: [entityInfo]
     * @return:
     **/
    private <T> Object generateSqlTemplate(T type, MyCallback callback) {
        Class<?> clazz = type.getClass();
        Field[] fields = clazz.getDeclaredFields();
        TableInfo tableInfo = MapperInit.getConfiguration().getClassToTableInfoMap().get(clazz);
        List<ColumnInfo> primaryKeys = tableInfo.getPrimaryKeys();
        List<Object> params = new ArrayList<>();
        MappedStatement mappedStatement = new MappedStatement();
        StringBuilder sql = new StringBuilder();
        callback.generateSqlExecutor(fields, tableInfo, primaryKeys, sql, mappedStatement, params);
        sql.setCharAt(sql.length() - 1, ' ');
        mappedStatement.setSql(sql.toString());
        return this.executor.doUpdate(mappedStatement, params.toArray());
    }
    /**
     * @description:mapper层查询sql语句生成模板
     * @author: zlj
     * @date: 2022/8/16 16:15
     * @param: [condition, list]
     * @return: java.lang.String
     **/
    public static String generateSelectSqL(Condition condition, List<Object> list) {
        StringBuffer select = new StringBuffer("select ");
        List<String> views = condition.getViews();
        if (condition.getFields() == null) {
            select.append("*");
        } else {
            for (String field : condition.getFields()) {
                select.append(field + ",");
            }
            select.delete(select.length() - 1, select.length());
        }
        select.append(" from " + condition.getViews().get(0));
        //连表条件
        if (views.size() > 1) {
            List<String> viewCondition = condition.getViewCondition();
            for (int i = 1; i < views.size(); i++) {
                String field = viewCondition.get(i - 1);
                select.append(" left join " + views.get(i) + " on ");
                select.append(views.get(0) + "." + field + "=" + views.get(i) + "." + field);
            }
        }
        List<Term> andTerms = condition.getAndCondition();
        //无and条件
        if (andTerms == null || andTerms.size() == 0) {
            List<Term> orTerms = condition.getOrCondition();
            //or条件
            if (orTerms != null && orTerms.size() != 0) {
                select.append(" where ");
                for (Term orTerm : orTerms) {
                    if(orTerm.getValue() instanceof List){
                        List<Object> value = (List<Object>) orTerm.getValue();
                        for (Object o : value) {
                            list.add(o);
                        }
                    }
                    else list.add(orTerm.getValue());                    select.append(orTerm.getViewName() + "." + orTerm.getFieldName());
                    switch (orTerm.getTermType()) {
                        case EQUAL:
                            select.append("= ? or ");
                            break;
                        case LIKE:
                            select.append("like %?% or ");
                            break;
                        case IN: {
                            select.append("in (");
                            List<Object> value = (List<Object>) orTerm.getValue();
                            int size = value.size();
                            for (int i = 0; i < size; i++)
                                select.append("'?' ,");
                            select.delete(select.length() - 1, select.length());
                            select.append(") or ");
                            break;
                        }
                        case GREATER:
                            select.append("> ? or ");
                            break;
                        case Less:
                            select.append("< ? or ");
                            break;
                    }

                }
                select.delete(select.length() - 3, select.length());
            }
        } else {
            select.append(" where ");
            //有and条件
            for (Term andTerm : andTerms) {
                if(andTerm.getValue() instanceof List){
                    List<Object> value = (List<Object>) andTerm.getValue();
                    for (Object o : value) {
                        list.add(o);
                    }
                }
                else list.add(andTerm.getValue());
                select.append(andTerm.getViewName() + "." + andTerm.getFieldName() + " ");
                switch (andTerm.getTermType()) {
                    case EQUAL:
                        select.append("= ? and ");
                        break;
                    case LIKE:
                        select.append("like %?% and ");
                        break;
                    case IN: {
                        select.append("in (");
                        List<Object> value = (List<Object>) andTerm.getValue();
                        int size = value.size();
                        for (int i = 0; i < size; i++)
                            select.append(" '?' ,");
                        select.delete(select.length() - 1, select.length());
                        select.append(") and ");
                        break;
                    }
                    case GREATER:
                        select.append("> ? and ");
                        break;
                    case Less:
                        select.append("< ? and ");
                        break;
                }
            }
            select.delete(select.length() - 4, select.length() - 1);
            //or条件
            List<Term> orTerms = condition.getOrCondition();
            if (orTerms != null && orTerms.size() != 0) {
                for (Term orTerm : orTerms) {
                    if(orTerm.getValue() instanceof List){
                        List<Object> value = (List<Object>) orTerm.getValue();
                        for (Object o : value) {
                            list.add(o);
                        }
                    }
                    else list.add(orTerm.getValue());
                    select.append("or " + orTerm.getViewName() + "." + orTerm.getFieldName()+" ");
                    switch (orTerm.getTermType()) {
                        case EQUAL:
                            select.append("= ? ");
                            break;
                        case LIKE:
                            select.append("like '%?%' ");
                            break;
                        case IN: {
                            select.append("in (");
                            List<Object> value = (List<Object>) orTerm.getValue();
                            int size = value.size();
                            for (int i = 0; i < size; i++)
                                select.append("'?' ,");
                            select.delete(select.length() - 1, select.length());
                            select.append(") ");
                            break;
                        }
                        case GREATER:
                            select.append("> ? ");
                            break;
                        case Less:
                            select.append("< ? ");
                            break;
                    }

                }
            }
        }
        //limit条件
        if (condition.getStartIndex().longValue() != -1 && condition.getStartIndex().intValue() != -1) {
            select.append("limit ");
            select.append(condition.getStartIndex() + ",");
            select.append(condition.getStartIndex() + condition.getSize());
        }
        return select.toString();
    }
}
