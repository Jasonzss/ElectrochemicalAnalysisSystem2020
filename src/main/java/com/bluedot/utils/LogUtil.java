package com.bluedot.utils;

import com.bluedot.mapper.bean.EntityInfo;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.User;
import com.bluedot.pojo.entity.UserLog;
import com.bluedot.service.LogService;
import com.bluedot.service.UserRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Jason
 * @CreationDate 2022/08/16 - 15:46
 * @Description ：
 */
public class LogUtil {
    public static Logger getLogger() {
        return LoggerFactory.getLogger(Thread.currentThread().getName());
    }
    public static void insertUserLog(String userLogLevel, String userLogDetail, Map userLogMap) {
        UserLog userLog = new UserLog();
        userLog.setUserIp((String) userLogMap.get("ip"));
        userLog.setUserLogClassMethodName((String) userLogMap.get("userLogClassMethodName"));
        userLog.setUserLogDetail(userLogDetail);
        userLog.setUserLogLevel(userLogLevel);
        userLog.setUserLogOperateTime(new Timestamp(System.currentTimeMillis()));
        userLog.setUserLogMethodReturnValue((String) userLogMap.get("userLogMethodReturnValue"));
        userLog.setUserLogParameter((String) userLogMap.get("userLogParameter"));
        User user = new User();
        user.setUserEmail((String) ((HttpSession) userLogMap.get("session")).getAttribute("userEmail"));
        userLog.setUser(user);
        HashMap<String,Object> map=new HashMap<>();
        map.put("userLog",userLog);
        new LogService((HttpSession) userLogMap.get("session"),(EntityInfo<?>)userLogMap.get("entityInfo")).doOtherService(map,"insert");

    }
}
