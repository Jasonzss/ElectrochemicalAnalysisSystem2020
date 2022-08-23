//package com.buledot.log;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.util.Properties;
//
///**
// * @author FireRain
// * @version 1.0
// * @date 2022/8/19 10:32
// * @created:
// */
//public class MyLogTest {
//
//    public static Connection getConnection() throws ClassNotFoundException, SQLException, IOException {
//        InputStream in = MyLogTest.class.getClassLoader().getResourceAsStream("database.properties");
//
//        Properties props = new Properties();
//        props.load(in);
//
//        String driver = (String) props.get("driver");
//        String url = (String) props.get("url");
//        String username = (String) props.get("username");
//        String password = (String) props.get("password");
//
//        Class.forName(driver);
//
//        return DriverManager.getConnection(url, username, password);
//    }
//
//    @org.junit.Test
//    public void test() throws SQLException, IOException, ClassNotFoundException {
//        Logger logger = LoggerFactory.getLogger(MyLogTest.class);
//        logger.error("ajsdlfkjasdf");
//        logger.debug("ajsdlfkjasdf");
//    }
//
//}