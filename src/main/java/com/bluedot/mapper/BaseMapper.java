package com.bluedot.mapper;


import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.*;
import com.bluedot.mapper.callBack.MyCallback;
import com.bluedot.pojo.vo.CommonResult;
import com.bluedot.utils.LogUtil;
import com.bluedot.utils.ReflectUtil;
import com.bluedot.utils.StringUtil;
import com.bluedot.utils.constants.SessionConstants;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zlj
 * @version 1.0
 * @description: Mapper层核心类
 * @date 2022/8/16 16:15
 */
public class BaseMapper {
    private final Logger logger = LogUtil.getLogger();
    //mapper执行器
    private Executor executor = new Executor(MapperInit.getConfiguration());
    //mapper实体类属性
    private List entityList;
    //mapper层返回结果
    private CommonResult commonResult;
    //操作实体
    private EntityInfo entityInfo;
    /**
     * @description:构造器传入，属性赋值
     * @author: zlj
     * @date: 2022/8/16 16:15
     * @param: [entityInfo]
     * @return:
     **/
    public BaseMapper(EntityInfo entityInfo) {
        logger.debug("进入BaseMapper构造器");
        this.entityInfo=entityInfo;
        entityList = entityInfo.getEntity();
        doMapper();
    }
    /**
     * @description:调用方法
     * @author: zlj
     * @date: 2022/8/16 16:15
     * @param: [entityInfo]
     * @return:
     **/
    private void doMapper() {
        Object object = null;
//        根据查询类型，调用方法
        switch (entityInfo.getOperation()) {
            case "insert":
                object = insert(entityList);
                break;
            case "delete":
                object = delete(entityList);
                break;
            case "update":
                object = update(entityList);
                break;
            case "select":
                object = select(entityInfo.getCondition());
                break;
            default:
                throw new UserException(CommonErrorCode.E_8001);
        }
        //结果封装
        commonResult = new CommonResult();
        commonResult.setData(object);
        logger.debug("mapper层操作结果："+commonResult+",并将结果通过队列  返回给service层");
        //将结果通过队列  返回给service
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
        //生成sqL语句并得到填充后的参数数组
        String sql = null;
        try {
            sql = generateSelectSqL(condition, parameters);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        MappedStatement mappedStatement = new MappedStatement();
        mappedStatement.setSql(sql);
        logger.debug("自动生成的查询sql语句："+sql);
        String view=condition.getViews().get(0);
        if (view.startsWith("`")){
            view=view.substring(1,view.length()-1);
        }
        view=StringUtil.tableNameToClassName(view);
        mappedStatement.setView(view);
        mappedStatement.setReturnType(condition.getReturnType());
        return this.executor.doQuery(mappedStatement,parameters);
    }
    /**
     * @description:mapper层插入数据
     * @author: zlj
     * @date: 2022/8/16 16:15
     * @param: [entityInfo]
     * @return:
     **/
    private <T> int insert(List typeList) {
        return (int) generateSqlTemplate(typeList, new MyCallback() {
            //回调接口，insert类型sql语句生成模板
            @Override
            public void generateSqlExecutor(Field[] fields, TableInfo tableInfo, List<ColumnInfo> primaryKeys, StringBuilder sql, MappedStatement mappedStatement, List<Object> params) {
                Object entity = typeList.get(0);
                sql.append("insert into ").append(tableInfo.getTableName()).append("(");
                List<Class> classList= Arrays.asList(Byte.class,Short.class,Integer.class,Long.class,Float.class,Double.class,char.class,Boolean.class,String.class, Date.class, Timestamp.class);
                for (Field field : fields) {
                    field.setAccessible(true);
                    //是否是外键实体类
                    boolean flag=true;
                    for (Class clazz : classList) {
                        if (field.getType()==clazz){
                            flag=false;
                            break;
                        }
                    }
                    if (flag){
                        try {
                            Object foreignKeyEntity = field.get(entity);
                            if (foreignKeyEntity!=null){
                                Field[] foreignKeyEntityFileds = foreignKeyEntity.getClass().getDeclaredFields();
                                for (Field foreignKeyEntityFiled : foreignKeyEntityFileds) {
                                    foreignKeyEntityFiled.setAccessible(true);
                                    if (foreignKeyEntityFiled.get(foreignKeyEntity)!=null) {
                                        sql.append(StringUtil.humpToLine(foreignKeyEntityFiled.getName())).append(",");
                                        params.add(foreignKeyEntityFiled.get(foreignKeyEntity));
                                    }
                                }
                            }
                            continue;
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    String fieldName = field.getName();
                    Object fieldValue = ReflectUtil.invokeGet(entity, fieldName);
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
                if (typeList.size()>1) {
                    for (int i = 1; i <= typeList.size()-1; i++) {
                        sql.append("( ");
                        for (Field field : fields) {
                            field.setAccessible(true);
                            //是否是外键实体类
                            boolean flag=true;
                            for (Class clazz : classList) {
                                if (field.getType()==clazz){
                                    flag=false;
                                    break;
                                }
                            }
                            if (flag){
                                try {
                                    Object foreignKeyEntity = field.get(typeList.get(i));
                                    if (foreignKeyEntity!=null){
                                        Field[] foreignKeyEntityFileds = foreignKeyEntity.getClass().getDeclaredFields();
                                        for (Field foreignKeyEntityFiled : foreignKeyEntityFileds) {
                                            foreignKeyEntityFiled.setAccessible(true);
                                            if (foreignKeyEntityFiled.get(foreignKeyEntity)!=null) {
                                                sql.append("?,");
                                                params.add(foreignKeyEntityFiled.get(foreignKeyEntity));
                                            }
                                        }
                                    }
                                    continue;
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                            String fieldName = field.getName();
                            Object fieldValue = ReflectUtil.invokeGet(typeList.get(i), fieldName);
                            if (null != fieldValue) {
                                sql.append("?,");
                                params.add(fieldValue);
                            }
                        }
                        sql.setCharAt(sql.length() - 1, ' ');
                        sql.append("),");
                    }
                }
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
    private <T> int delete(List typeList) {
        return (int) generateSqlTemplate(typeList, new MyCallback() {
            //回调接口，delete类型sql语句生成模板
            @Override
            public void generateSqlExecutor(Field[] fields, TableInfo tableInfo, List<ColumnInfo> primaryKeys, StringBuilder sql, MappedStatement mappedStatement, List<Object> params) {

                sql.append("delete from ").append(tableInfo.getTableName()).append(" where ");
                for (ColumnInfo columnInfo : primaryKeys) {
                    if (ReflectUtil.invokeGet(typeList.get(0), StringUtil.lineToHump(columnInfo.getName()))!=null) {
                        sql.append(StringUtil.humpToLine(columnInfo.getName())).append(" in( ");
                        for (Object o : typeList) {
                            if (ReflectUtil.invokeGet(o, StringUtil.lineToHump(columnInfo.getName())) != null) {
                                sql.append(" ?,");
                                params.add(ReflectUtil.invokeGet(o, StringUtil.lineToHump(columnInfo.getName())));
                            }
                        }
                        sql.setCharAt(sql.length() - 1, ')');
                        sql.append(" ");
                    }
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
     *
     * @param typeList*/
    private  int update(List typeList) {
        //回调接口，update类型sql语句生成模板
        return (int) generateSqlTemplate(typeList, new MyCallback() {
            @Override
            public void generateSqlExecutor(Field[] fields, TableInfo tableInfo, List<ColumnInfo> primaryKeys, StringBuilder sql, MappedStatement mappedStatement, List<Object> params) {
                Object entity = typeList.get(0);
                List<Class> classList=Arrays.asList(Byte.class,Short.class,Integer.class,Long.class,Float.class,Double.class,char.class,Boolean.class,String.class,Date.class,Timestamp.class);
                if (typeList.size()>1){
                    sql.append("update ").append(tableInfo.getTableName()).append(" set ");
                    for (Field field : fields) {

                        field.setAccessible(true);
                        //是否是外键实体类
                        boolean flag=true;
                        for (Class clazz : classList) {
                            if (field.getType()==clazz){
                                flag=false;
                                break;
                            }
                        }
                        if (flag){
                            try {
                                Object foreignKeyEntity = field.get(entity);
                                if (foreignKeyEntity!=null){
                                    Field[] foreignKeyEntityFileds = foreignKeyEntity.getClass().getDeclaredFields();
                                    for (Field foreignKeyEntityFiled : foreignKeyEntityFileds) {
                                        String filedName = foreignKeyEntityFiled.getName();
                                        foreignKeyEntityFiled.setAccessible(true);
                                        if (foreignKeyEntityFiled.get(foreignKeyEntity)!=null) {
                                            for (ColumnInfo columnInfo : primaryKeys) {
                                                String columnName=StringUtil.humpToLine(foreignKeyEntityFiled.getName());
                                                sql.append(columnName);
                                                sql.append("=(case ").append(columnInfo.getName());

                                                for (Object o : typeList) {
                                                    foreignKeyEntity = field.get(o);
                                                    if (ReflectUtil.invokeGet(foreignKeyEntity, filedName)!=null){
                                                        sql.append(" when ? then ? ");
                                                        params.add(ReflectUtil.invokeGet(o, StringUtil.lineToHump(columnInfo.getName())));
                                                        params.add(ReflectUtil.invokeGet(foreignKeyEntity, filedName));
                                                    }
                                                }
                                                sql.append(" end),");

                                            }
                                        }
                                    }
                                }
                                continue;
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }


                        String fieldName = field.getName();
                        Object fieldValue = ReflectUtil.invokeGet(entity, fieldName);
                        if (null != fieldValue) {

                            for (ColumnInfo columnInfo : primaryKeys) {
                                if (ReflectUtil.invokeGet(entity, StringUtil.lineToHump(columnInfo.getName()))!=null&&!fieldName.equals(StringUtil.lineToHump(columnInfo.getName()))) {

                                    sql.append(StringUtil.humpToLine(fieldName));
                                    sql.append("=(case ").append(columnInfo.getName());
                                    for (Object o : typeList) {
                                        if (ReflectUtil.invokeGet(o, fieldName)!=null){
                                            sql.append(" when ? then ? ");
                                            params.add(ReflectUtil.invokeGet(o, StringUtil.lineToHump(columnInfo.getName())));
                                            params.add(ReflectUtil.invokeGet(o, fieldName));
                                        }
                                    }
                                    sql.append(" end),");
                                }
                            }
                        }
                    }
                    sql.setCharAt(sql.length() - 1, ' ');
                    sql.append("where ");
                    for (ColumnInfo columnInfo : primaryKeys) {
                        if (ReflectUtil.invokeGet(entity, StringUtil.lineToHump(columnInfo.getName()))!=null){
                            sql.append(StringUtil.humpToLine(columnInfo.getName())).append(" in( ");
                            for (Object o : typeList) {
                                sql.append(" ? ,");
                                params.add(ReflectUtil.invokeGet(o, StringUtil.lineToHump(columnInfo.getName())));
                            }
                            sql.setCharAt(sql.length() - 1, ')');
                            sql.append(" ");
                        }
                    }
                }
                else {
                    sql.append("update ").append(tableInfo.getTableName()).append(" set ");
                    for (Field field : fields) {

                        field.setAccessible(true);
                        //是否是外键实体类
                        boolean flag=true;
                        for (Class clazz : classList) {
                            if (field.getType()==clazz){
                                flag=false;
                                break;
                            }
                        }
                        if (flag){
                            try {
                                Object foreignKeyEntity = field.get(entity);
                                if (foreignKeyEntity!=null){
                                    Field[] foreignKeyEntityFileds = foreignKeyEntity.getClass().getDeclaredFields();
                                    for (Field foreignKeyEntityFiled : foreignKeyEntityFileds) {
                                        String filedName = foreignKeyEntityFiled.getName();
                                        foreignKeyEntityFiled.setAccessible(true);
                                        if (foreignKeyEntityFiled.get(foreignKeyEntity)!=null) {
                                            for (ColumnInfo columnInfo : primaryKeys) {
                                                String columnName=StringUtil.humpToLine(foreignKeyEntityFiled.getName());
                                                sql.append(columnName).append("=?,");
                                                params.add(foreignKeyEntityFiled.get(foreignKeyEntity));

                                            }
                                        }
                                    }
                                }
                                continue;
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        String fieldName = field.getName();
                        Object fieldValue = ReflectUtil.invokeGet(entity, fieldName);
                        if (null != fieldValue) {

                            for (ColumnInfo columnInfo : primaryKeys) {
                                if (!fieldName.equals(StringUtil.lineToHump(columnInfo.getName()))) {

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

                        params.add(ReflectUtil.invokeGet(entity, StringUtil.lineToHump(columnInfo.getName())));
                    }
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
    private <T> Object generateSqlTemplate(List typeList, MyCallback callback) {
        Object entity = ((ArrayList) typeList).get(0);
        Class<?> clazz = entity.getClass();
        Field[] fields = clazz.getDeclaredFields();
        TableInfo tableInfo = MapperInit.getConfiguration().getClassToTableInfoMap().get(clazz);
        List<ColumnInfo> primaryKeys = tableInfo.getPrimaryKeys();
        List<Object> params = new ArrayList<>();
        MappedStatement mappedStatement = new MappedStatement();
        StringBuilder sql = new StringBuilder();
        callback.generateSqlExecutor(fields, tableInfo, primaryKeys, sql, mappedStatement, params);
        sql.setCharAt(sql.length() - 1, ' ');
        logger.debug(entityInfo.getOperation()+"生成的sql:"+sql.toString());
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

        if (!condition.getFields().contains("count(*)")  ){
            String view=condition.getViews().get(0);
            if (view.startsWith("`")){
                view=view.substring(1,view.length()-1);
            }
            view=StringUtil.tableNameToClassName(view);
            String entity = Configuration.getProperty(SessionConstants.ENTITY_PACKAGENAME)+"."+view;
            TableInfo tableInfo = null;
            try {
                tableInfo = MapperInit.getConfiguration().getClassToTableInfoMap().get(Class.forName(entity));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            //表主键名
            String primaryName = tableInfo.getPrimaryKeys().get(0).getName();
            if (!condition.getFields().contains(tableInfo.getTableName()+"."+primaryName)){
                List<String> fields = condition.getFields();
                boolean flag=true;
                for (String field : fields) {
                    if (field.indexOf("GROUP_CONCAT")!=-1) {
                        flag=false;
                    }
                }
                if (flag){
                    fields.add(tableInfo.getTableName()+"."+primaryName);
                }
            }
        }
        StringBuffer select = new StringBuffer("select ");
        List<String> views = condition.getViews();
        if (condition.getFields().size() == 0) {
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
                    else {list.add(orTerm.getValue());    }
                    select.append(orTerm.getViewName() + "." + orTerm.getFieldName());
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
                            for (int i = 0; i < size; i++) {
                                select.append("'?' ,");
                            }
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
                else if (andTerm.getTermType()!=TermType.GROUOBY){
                    list.add(andTerm.getValue());
                }
                if (andTerm.getTermType()!=TermType.GROUOBY){
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
                            for (int i = 0; i < size; i++) {
                                select.append(" ?  ,");
                            }
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
                else {
                    int and = select.indexOf("and");
                    select.delete(and,select.length()-1);
                    select.append(" GROUP BY "+andTerm.getViewName()+ "." + andTerm.getFieldName()+"     ");

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
                    else {
                        list.add(orTerm.getValue());
                    }
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
                            for (int i = 0; i < size; i++) {
                                select.append("'?' ,");
                            }
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
        if (condition.getStartIndex()!=null && condition.getStartIndex()!=null) {
            select.append(" limit ");
            select.append(condition.getStartIndex() + ",");
            select.append(condition.getStartIndex() + condition.getSize());
        }
        return select.toString();
    }
}