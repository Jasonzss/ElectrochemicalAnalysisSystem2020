package com.bluedot.mapper.dataSource.impl;

import com.bluedot.mapper.bean.Configuration;
import com.bluedot.mapper.dataSource.MyDataSource;
import com.bluedot.utils.LogUtil;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.concurrent.*;


public class MyDataSourceImpl implements MyDataSource {
    private Logger logger=LogUtil.getLogger();
    private static  String DRIVER;
    private static  String URL;
    private static  String USERNAME;
    private static  String PASSWORD;
    private static int initCount = 5;
    private static int minCount = 5;
    private static int maxCount = 30;
    private static int createdCount;
    private static int increasingCount = 2;
    private static int maxWaitingTime = 500;
    private static int maxIdleTime = 20000;
    private LinkedList<Connection> conns = new LinkedList<>();
    private static final Object MONITOR = new Object();
//    private ExecutorService returnConnectionThreadPool = Executors.newFixedThreadPool(maxCount);
    private ExecutorService returnConnectionThreadPool = new ThreadPoolExecutor(maxCount, maxCount,
                                      0L,TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>());



    static {
        DRIVER = Configuration.getProperty("jdbc.driver");
        URL = Configuration.getProperty("jdbc.url");
        USERNAME = Configuration.getProperty("jdbc.username");
        PASSWORD = Configuration.getProperty("jdbc.password");
//        initCount = Integer.parseInt(Configuration.getProperty("jdbc.initCount"));
//        minCount = Integer.parseInt(Configuration.getProperty("jdbc.minCount"));
//        maxCount = Integer.parseInt(Configuration.getProperty("jdbc.maxCount"));
//        increasingCount = Integer.parseInt(Configuration.getProperty("jdbc.increasingCount"));
//        maxWaitingTime = Integer.parseInt(Configuration.getProperty("jdbc.maxWaitingTime"));
//        maxIdleTime = Integer.parseInt(Configuration.getProperty("jdbc.maxIdleTime"));
    }

    private static volatile MyDataSourceImpl instance;

    private MyDataSourceImpl() {
        init();
    }

    public static MyDataSourceImpl getInstance() {
        if (null == instance) {
            synchronized (MyDataSourceImpl.class) {
                if (null == instance) {
                    instance = new MyDataSourceImpl();
                }
            }
        }
        return instance;
    }

    public static Connection getDataBaseConnection(){
        return MyDataSourceImpl.getInstance().getConnection();
    }
    private void init() {

        for (int i = 0; i < initCount; i++) {
            boolean flag = conns.add(createConnection());
            if (flag) {
                createdCount++;
            }
        }
    }

    private Connection createConnection() {
        try {
            Class.forName(DRIVER);
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            logger.debug("创建数据库连接，连接池容量---》："+conns.size()+"-----createdCount为 "+createdCount);
            return connection;

        } catch (Exception e) {
            throw new RuntimeException("数据库连接失败：" + e.getMessage());
        }
    }

    private synchronized void autoAdd() {
        //增长步长默认为2
        if (createdCount == maxCount) {
            logger.error("连接池中连接已达最大数量,无法再次创建连接");
        }
        //临界时判断增长个数
        for (int i = 0; i < increasingCount; i++) {
            if (createdCount == maxCount) {
                break;
            }
            conns.add(createConnection());
            createdCount++;
        }

    }

    private void autoReduce(Connection conn) {
        synchronized (MONITOR) {
            if (createdCount > minCount && conns.contains(conn)) {
                try {
                    conns.remove(conn);
                    logger.debug("关闭数据库连接："+conn);
                    conn.close();
                    createdCount--;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
            }
        }

    }


    @Override
    public Connection getConnection() {
        synchronized (MONITOR) {
            if (conns.size() > 0) {
                return conns.poll();
            }
            if (createdCount < maxCount) {
                autoAdd();
                return getConnection();
            }
            try {
                MONITOR.wait(maxWaitingTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return conns.size() > 0 ? getConnection() : null;
        }

    }


    @Override
    public void returnConnection(Connection conn) {
        returnConnection(conn,"");
    }

    @Override
    public void returnConnection(Connection conn,String message) {
        synchronized (MONITOR) {
            conns.offer(conn);
            MONITOR.notify();
            Runnable closeConnectionTask = () -> {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                autoReduce(conn);
            };
            returnConnectionThreadPool.execute(closeConnectionTask);
        }
    }

    @Override
    public int getIdleCount() {
        return conns.size();
    }

    @Override
    public int getCreatedCount() {
        return createdCount;
    }

    public void close() {

        for (Connection conn : conns) {
            try {
                conn.close();
            } catch (SQLException throwables) {
                throw new RuntimeException(throwables);
            }
        }
    }

}
