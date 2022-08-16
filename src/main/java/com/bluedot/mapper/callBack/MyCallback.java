package com.bluedot.mapper.callBack;

import com.bluedot.mapper.info.ColumnInfo;
import com.bluedot.mapper.info.MappedStatement;
import com.bluedot.mapper.info.TableInfo;

import javax.security.auth.callback.Callback;
import java.lang.reflect.Field;
import java.util.List;

public interface MyCallback extends Callback {
    default void generateSqlExecutor(Field[] fields, TableInfo tableInfo, List<ColumnInfo> primaryKeys, StringBuilder sql, MappedStatement mappedStatement, List<Object> params) {
    }
}
