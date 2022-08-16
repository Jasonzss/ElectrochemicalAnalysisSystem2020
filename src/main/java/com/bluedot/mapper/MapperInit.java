package com.bluedot.mapper;

import com.bluedot.mapper.info.ColumnInfo;
import com.bluedot.mapper.info.Configuration;
import com.bluedot.mapper.info.TableInfo;
import com.bluedot.utils.StringUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MapperInit {

    private String fileName;
    private  static Configuration configuration=new Configuration();

    public MapperInit(String fileName) throws IOException, SQLException, ClassNotFoundException {
        this.fileName=fileName;
        init();
    }
    private void init() throws IOException, ClassNotFoundException, SQLException {
        InputStream inputStream = MapperInit.class.getClassLoader().getResourceAsStream(fileName);
        configuration.load(inputStream);
//        loadSql(configuration.getProperty("mapper.location").replaceAll("\\.", "/"));
        loadTableInfo(configuration.getDataSource().getConnection());
    }

    private void loadTableInfo(Connection connection) throws SQLException {
        try {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            String database = configuration.getProperty("database");
            ResultSet tableResultSet = databaseMetaData.getTables(database, "%", "%", new String[]{"TABLE"});
            List<TableInfo> tableInfos = new ArrayList<>();
            Map<Class<?>, TableInfo> map = new HashMap<>();
            while (tableResultSet.next()) {
                String tableName = tableResultSet.getString("TABLE_NAME");
                TableInfo tableInfo = new TableInfo(tableName, new HashMap<>(10), new ArrayList<>(), new ArrayList<>());

                ResultSet columnResultSet = databaseMetaData.getColumns(database, "%", tableName, null);

                while (columnResultSet.next()) {
                    ColumnInfo columnInfo = new ColumnInfo(columnResultSet.getString("COLUMN_NAME"),
                            columnResultSet.getString("TYPE_NAME"), 0);
                    tableInfo.getColumnInfoMap().put(columnInfo.getName(), columnInfo);
                }

                ResultSet primaryKeyResultSet = databaseMetaData.getPrimaryKeys(database, "%", tableName);

                while (primaryKeyResultSet.next()) {

                    ColumnInfo primaryColumnInfo = tableInfo.getColumnInfoMap().get(primaryKeyResultSet.getString("COLUMN_NAME"));

                    primaryColumnInfo.setKeyType(1);

                    tableInfo.getPrimaryKeys().add(primaryColumnInfo);
                }

                ResultSet foreignKeyResultSet = databaseMetaData.getImportedKeys(database, null, tableName);
                while (foreignKeyResultSet.next()) {
                    ColumnInfo foreignColumnInfo = tableInfo.getColumnInfoMap().get(foreignKeyResultSet.getString("FKCOLUMN_NAME"));
                    tableInfo.getForeignKeys().add(foreignColumnInfo);
                }
                Class<?> clazz = Class.forName(configuration.getProperty("po.location") + "." + StringUtil.tableNameToClassName(tableName));
                map.put(clazz, tableInfo);
            }
            configuration.setClassToTableInfoMap(map);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            configuration.getDataSource().returnConnection(connection);
        }
    }

    public static Configuration getConfiguration() {
        return configuration;
    }
}
