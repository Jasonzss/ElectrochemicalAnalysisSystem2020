package com.bluedot.mapper.bean;

import java.util.List;
import java.util.Map;

public class TableInfo {
    private String tableName;
    private Map<String, ColumnInfo> columnInfoMap;
    private List<ColumnInfo> primaryKeys;
    private List<ColumnInfo> foreignKeys;

    public TableInfo() {
    }

    public TableInfo(String tableName, Map<String, ColumnInfo> columnInfoMap, List<ColumnInfo> primaryKeys, List<ColumnInfo> foreignKeys) {
        this.tableName = tableName;
        this.columnInfoMap = columnInfoMap;
        this.primaryKeys = primaryKeys;
        this.foreignKeys = foreignKeys;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, ColumnInfo> getColumnInfoMap() {
        return columnInfoMap;
    }

    public void setColumnInfoMap(Map<String, ColumnInfo> columnInfoMap) {
        this.columnInfoMap = columnInfoMap;
    }

    public List<ColumnInfo> getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(List<ColumnInfo> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public List<ColumnInfo> getForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(List<ColumnInfo> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }


}
