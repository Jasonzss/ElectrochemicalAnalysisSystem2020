package com.buledot.utils;



import com.bluedot.mapper.BaseMapper;
import com.bluedot.mapper.MapperInit;
import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.EntityInfo;
import com.bluedot.mapper.bean.Term;
import com.bluedot.mapper.bean.TermType;
import com.bluedot.pojo.entity.ExpData;
import com.bluedot.pojo.entity.User;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MapperTest {

    @Test
    public void testPermission() throws SQLException, IOException, ClassNotFoundException {
        //模拟根据条件生成select语句
        Condition condition = new Condition();
        //查询涉及表
        List<String>  views = new ArrayList<>();
        views.add("`role_permission`");
        views.add("user_role");
        views.add("`permission`");
        //连接表条件
        List<String> viewCondition=new ArrayList<>();
        viewCondition.add("role_id");
        viewCondition.add("permission_id");
        //查询结果字段
        List<String> fields = new ArrayList<>();
        fields.add("role_permission.role_id");
        fields.add("permission.permission_name");
        //以and为连接符的查询条件
        List<Term> andCondition=new ArrayList<>();
        andCondition.add(new Term("user_role","user_email","2418972236@qq.com", TermType.EQUAL));
        condition.setViews(views);
        condition.setFields(fields);
        condition.setAndCondition(andCondition);
        condition.setViewCondition(viewCondition);

        new MapperInit("database.properties");
        EntityInfo<Object> entityInfo = new EntityInfo<>();
        condition.setReturnType("RolePermission");
        entityInfo.setCondition(condition);
        entityInfo.setOperation("select");
        BaseMapper baseMapper=new BaseMapper(entityInfo);
    }

    @Test
    public void testUpdate() throws SQLException, IOException, ClassNotFoundException {
        new MapperInit("database.properties");

        User user = new User();
        user.setUserEmail("1571864304@qq.com");
//        user.setUserPassword("111");
//        user.setUserStatus(0);
//        user.setUserSalt("654321");
//        user.setUserAge(1);
//        user.setUserName("da");

        User user1 = new User();
        user1.setUserEmail("1571864305@qq.com");
        user1.setUserPassword("111");
        user1.setUserStatus(1);
//        user.setUserSalt("654321");
//        user.setUserAge(1);
//        user.setUserName("da");
        ArrayList<User> userArrayList = new ArrayList<>();
        userArrayList.add(user);
//        userArrayList.add(user1);

        EntityInfo<User> userEntityInfo = new EntityInfo<>();
        userEntityInfo.setEntity(userArrayList);
        userEntityInfo.setOperation("insert");

        ExpData expData = new ExpData();
        expData.setExpDataId(6);
        expData.setExpDataDesc("看看看看");
        expData.setUser(user);

        ExpData expData1 = new ExpData();
        expData1.setExpDataId(5);
        expData1.setExpDataDesc("零零零零");
        expData1.setUser(user);

        ArrayList<ExpData> expDataArrayList=new ArrayList<>();
        expDataArrayList.add(expData);
//        expDataArrayList.add(expData1);

        EntityInfo<ExpData> expDataEntityInfo=new EntityInfo<>();
        expDataEntityInfo.setEntity(expDataArrayList);
        expDataEntityInfo.setOperation("insert");

        new BaseMapper(expDataEntityInfo);
    }

    @Test
    public void testTablesSelect() throws SQLException, IOException, ClassNotFoundException {
        //模拟根据条件生成select语句
        Condition condition = new Condition();
        //查询涉及表
        List<String>  views = new ArrayList<>();
        views.add("user_role");
        views.add("user");
        views.add("role");
        //连接表条件
        List<String> viewCondition=new ArrayList<>();
        viewCondition.add("user_email");
        viewCondition.add("role_id");
        //查询结果字段
        List<String> fields = new ArrayList<>();
        fields.add("user.user_email");
        fields.add("role.role_id");
        fields.add("role.role_name");
        //以and为连接符的查询条件
        List<Term> andCondition=new ArrayList<>();
        andCondition.add(new Term("question","qid","2", TermType.GREATER));
        List<Object> objects = new ArrayList<>();
        objects.add("数学");
        objects.add("英语");
        andCondition.add(new Term("question","type",objects,TermType.IN));
        //以or连接符的查询条件
        List<Term> orCondition=new ArrayList<>();
        orCondition.add(new Term("user","name","zhangSan", TermType.LIKE));
        orCondition.add(new Term("user","age","12", TermType.EQUAL));

        condition.setViews(views);
        condition.setFields(fields);
//        condition.setAndCondition(andCondition);
//        condition.setOrCondition(orCondition);
        condition.setViewCondition(viewCondition);
//        condition.setStartIndex(2L);
//        condition.setSize(2);
        //参数数组
//        List<Object> list = new ArrayList<>();
//        String s = BaseMapper.generateSelectSqL(condition, list);
//        System.out.println(s);
//        for (Object o : list) {
//            System.out.println(o);
//        }
        new MapperInit("database.properties");
        EntityInfo<Object> entityInfo = new EntityInfo<>();
        condition.setReturnType("UserRole");
        entityInfo.setCondition(condition);
        entityInfo.setOperation("select");
        BaseMapper baseMapper=new BaseMapper(entityInfo);
    }


    @org.junit.Test
    public void testSelect() throws SQLException, IOException, ClassNotFoundException {
        new MapperInit("database.properties");
        //模拟根据条件生成select语句
        Condition condition = new Condition();
        //查询涉及表
        List<String>  views = new ArrayList<>();
        views.add("question");
        views.add("user");
        //连接表条件
        List<String> viewCondition=new ArrayList<>();
        viewCondition.add("uid");
        //查询结果字段
        List<String> fields = new ArrayList<>();
        fields.add("questionName");
        fields.add("text");
        fields.add("username");
        //以and为连接符的查询条件
        List<Term> andCondition=new ArrayList<>();
        andCondition.add(new Term("question","qid","2", TermType.GREATER));
        List<Object> objects = new ArrayList<>();
        objects.add("数学");
        objects.add("英语");
        andCondition.add(new Term("question","type",objects,TermType.IN));
       //以or连接符的查询条件
        List<Term> orCondition=new ArrayList<>();
        orCondition.add(new Term("user","name","zhangSan", TermType.LIKE));
        orCondition.add(new Term("user","age","12", TermType.EQUAL));

//        condition.setViews(views);
//        condition.setFields(fields);
//        condition.setAndCondition(andCondition);
//        condition.setOrCondition(orCondition);
//        condition.setViewCondition(viewCondition);
//        condition.setStartIndex(2L);
//        condition.setSize(2);
        //参数数组
        List<Object> list = new ArrayList<>();
        String s = BaseMapper.generateSelectSqL(condition, list);
        System.out.println(s);
        for (Object o : list) {
            System.out.println(o);
        }
    }
}
