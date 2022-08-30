package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.EntityInfo;
import com.bluedot.mapper.bean.Term;
import com.bluedot.mapper.bean.TermType;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.User;
import com.bluedot.pojo.entity.UserLog;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;


public class LogService extends BaseService<UserLog>{


    public LogService(Data data) {
        super(data);
    }

    public LogService(HttpSession session, EntityInfo<?> entityInfo) {
        super(session, entityInfo);
    }

    @Override
    protected void doService() {
        String methodName=null;
        switch (operation){
            case "insert":
                methodName="insertUserLog";
                break;
            case "select":
                if (paramList.containsKey("userLog")&&!paramList.containsKey("startTime")&&!paramList.containsKey("endTime")){
                    methodName="listUserLog";
                }else if(paramList.containsKey("systemLog")&&!paramList.containsKey("startTime")&&!paramList.containsKey("endTime")){
                    methodName="listSystemLog";
                }else if(paramList.containsKey("userLog")||paramList.containsKey("userEmail")||paramList.containsKey("ulogLevel")){
                    methodName="getUserLog";
                }else if(paramList.containsKey("systemLog")||paramList.containsKey("slogLevel")){
                    methodName="getSystemLog";
                }else {
                    throw new UserException(CommonErrorCode.E_4001);
                }
                break;
            default:
                throw new UserException(CommonErrorCode.E_4001);
        }
        userLogMap.put("userLogClassMethodName",userLogMap.get("userLogClassMethodName")+methodName);
        invokeMethod(methodName,this);
    }

    @Override
    protected boolean check() {
        return true;
    }

    /**
     * 插入用户日志
     */
    private void insertUserLog(){
        UserLog userLog= (UserLog) paramList.get("userLog");

        entityInfo.addEntity(userLog);
        insert();
    }

    /**
     * 查询所有用户日志
     */
    private void listUserLog(){
        Long pageNo = Long.valueOf(((Integer)paramList.get("pageNo")).longValue());
        Integer pageSize = (Integer) paramList.get("pageSize");

        // 封装Condition
        Condition condition = new Condition();
        condition.setStartIndex((pageNo-1)*pageSize);
        condition.setSize(pageSize);
        condition.setReturnType("UserLog");
        List<String> views = new ArrayList<>();
        views.add("`user_log`");
        List<String> fields = new ArrayList<>();
        fields.add("*");
        condition.setViews(views);
        condition.setFields(fields);
        entityInfo.setCondition(condition);
        selectPage();
    }
    /**
     * 查询目标用户日志
     */
    private void getUserLog(){
        Long pageNo = Long.valueOf(((Integer)paramList.get("pageNo")).longValue());
        Integer pageSize = (Integer) paramList.get("pageSize");
        // 封装Condition
        Condition condition = new Condition();
        condition.setStartIndex((pageNo-1)*pageSize);
        condition.setSize(pageSize);
        condition.setReturnType("UserLog");
        List<String> views = new ArrayList<>();
        views.add("`user_log`");
        List<String> fields = new ArrayList<>();
        fields.add("*");
        List<Term> andCondition=new ArrayList<>();

        condition.setViews(views);
        condition.setFields(fields);
        condition.setAndCondition(andCondition);
        entityInfo.setCondition(condition);
        if (paramList.containsKey("userEmail")){
            andCondition.add(new Term("user_log","user_email",paramList.get("userEmail"), TermType.EQUAL));
        }
        if (paramList.containsKey("ulogLevel")){
            andCondition.add(new Term("user_log","user_log_level",paramList.get("ulogLevel"), TermType.EQUAL));
        }
        if (paramList.containsKey("startTime")){
            andCondition.add(new Term("user_log","user_log_operate_time",paramList.get("startTime"), TermType.GREATER));
        }
        if (paramList.containsKey("endTime")){
            andCondition.add(new Term("user_log","user_log_operate_time",paramList.get("endTime"), TermType.Less));
        }

        selectPage();
    }
    /**
     * 查询所有系统日志
     */
    private void listSystemLog(){
        Long pageNo = Long.valueOf(((Integer)paramList.get("pageNo")).longValue());
        Integer pageSize = (Integer) paramList.get("pageSize");

        // 封装Condition
        Condition condition = new Condition();
        condition.setStartIndex((pageNo-1)*pageSize);
        condition.setSize(pageSize);
        condition.setReturnType("SystemLog");
        List<String> views = new ArrayList<>();
        views.add("`system_log`");
        condition.setViews(views);
        List<String> fields = new ArrayList<>();
        fields.add("system_log.system_log_id");
        fields.add("system_log.system_log_level");
        fields.add("system_log.system_log_details");
        fields.add("system_log.system_log_time");
        condition.setFields(fields);
        entityInfo.setCondition(condition);
        selectPage();
    }
    /**
     * 查询目标系统日志
     */
    private void getSystemLog(){
        Long pageNo = Long.valueOf(((Integer)paramList.get("pageNo")).longValue());
        Integer pageSize = (Integer) paramList.get("pageSize");
        // 封装Condition
        Condition condition = new Condition();
        condition.setStartIndex((pageNo-1)*pageSize);
        condition.setSize(pageSize);
        condition.setReturnType("SystemLog");
        //表名
        List<String> views = new ArrayList<>();
        views.add("`system_log`");
        List<String> fields = new ArrayList<>();
        fields.add("*");
        List<Term> andCondition=new ArrayList<>();

        condition.setViews(views);
        condition.setFields(fields);
        condition.setAndCondition(andCondition);
        entityInfo.setCondition(condition);
        if (paramList.containsKey("slogLevel")){
            andCondition.add(new Term("system_log","system_log_level",paramList.get("slogLevel"), TermType.EQUAL));
        }
        if (paramList.containsKey("startTime")){
            andCondition.add(new Term("system_log","system_log_time",paramList.get("startTime"), TermType.GREATER));
        }
        if (paramList.containsKey("endTime")){
            andCondition.add(new Term("system_log","system_log_time",paramList.get("endTime"), TermType.Less));
        }

        selectPage();
    }
}
