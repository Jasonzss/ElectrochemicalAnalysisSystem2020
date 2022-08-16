package com.bluedot.mapper;

import com.bluedot.mapper.callBack.MyCallback;
import com.bluedot.mapper.info.*;
import com.bluedot.mapper.queue.MSQueue;
import com.bluedot.utils.ReflectUtil;
import com.bluedot.utils.StringUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class BaseMapper {
    private Executor executor = new Executor(MapperInit.getConfiguration());
    private Object entity;
    private CommonResult commonResult;

    public BaseMapper(EntityInfo entityInfo) {
        entity = entityInfo.getEntity();
        doMapper(entityInfo);
    }

    private void doMapper(EntityInfo entityInfo) {
        Object object = null;
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
        commonResult = new CommonResult.Builder<>().setData(object).build();
        MSQueue.getInstance().put(entityInfo.getKey(), this.commonResult);
    }

    private Object select(Condition condition) {
        List<Object> parameters = new ArrayList<>();
        //生成sqL语句并得到填充后的参数数组
        String sql = generateSelectSqL(condition, parameters);
        MappedStatement mappedStatement = new MappedStatement();
        mappedStatement.setSql(sql);
        return this.executor.doQuery(mappedStatement,parameters);
//        MappedStatement mappedStatement = MapperInit.getConfiguration().getMappedStatement(sqlId);
//        return this.executor.doQuery(mappedStatement, parameter);
    }

    private <T> int insert(T type) {
        return (int) generateSqlTemplate(type, new MyCallback() {
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

    private <T> int delete(T type) {
        return (int) generateSqlTemplate(type, new MyCallback() {
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

    private <T> int update(T type) {
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
