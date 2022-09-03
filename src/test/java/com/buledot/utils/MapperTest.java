package com.buledot.utils;



import com.bluedot.mapper.BaseMapper;
import com.bluedot.mapper.MapperInit;
import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.EntityInfo;
import com.bluedot.mapper.bean.Term;
import com.bluedot.mapper.bean.TermType;
import com.bluedot.pojo.entity.Backup;
import com.bluedot.pojo.entity.ExpData;
import com.bluedot.pojo.entity.User;
import com.google.protobuf.ServiceException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MapperTest {
    @Test
    public void insert() throws SQLException, IOException, ClassNotFoundException {
        new MapperInit("database.properties");
        User user = new User();
        user.setUserEmail("1571764306@qq.com");

        byte[] bytes = {1,26,66,22,  6,2,1,2,1,2,1,2,1,2,1,2,1,21,2,1,1,2,1,2,3};

        user.setUserImg(bytes);
        EntityInfo<User> userEntityInfo = new EntityInfo<>();
        userEntityInfo.setOperation("update");
        userEntityInfo.addEntity(user);
        new BaseMapper(userEntityInfo);
    }

    @Test
    public void   testBackup() throws ServiceException, InterruptedException, IOException {
//        Properties props = Resources.getResourceAsProperties("jdbc.properties");
//        String url = props.getProperty("jdbc.url");
////		String driver = props.getProperty("jdbc.driverClassName");
//        String username = props.getProperty("jdbc.username");
//        String password = props.getProperty("jdbc.password");
////      获取 地址及数据库名称
//        String[] arr = url.split("\\/");
//        String port = arr[2].split("\\:")[0];
//        String database = arr[3].split("\\?")[0];
        // mysqldump -h 地址 -u用户 -p密码 数据库 > d:/test.sql --备份D盘
        File file = new File("D:\\test\\");
        if ( !file.exists() ){
            file.mkdir();
        }
        File datafile = new File(file+File.separator+"test3.sql");
        if( datafile.exists() ){
            throw new ServiceException("文件名已存在，");
        }
        //拼接cmd命令
        Process exec = Runtime.getRuntime().exec("cmd /c mysqldump -h"+"3306"+" -u "+"root"+" -p"+"root"+" "+"test"+" > "+datafile);
        if( exec.waitFor() == 0){
            System.out.println("数据库备份成功,备份路径为："+datafile);
        }


    }
    @Test
    public void BackupDatabase() {

// TODO Auto-generated method stub

        java.sql.PreparedStatement ps = null;

        java.sql.Connection cn = null;

        ResultSet rs = null;

        try {

// Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");

            Class.forName("com.mysql.jdbc.Driver").newInstance();

            cn = DriverManager.getConnection(

                    "jdbc:mysql://120.79.71.79:3306/electrochemical_analysis_system?user=root&password=aa520411&useUnicode=true&characterEncoding=UTF8");

            ps = cn.prepareStatement("backup database learn to disk=‘d:/12345.bak‘");

            int i= ps.executeUpdate();

            System.out.println(i);

            if(i>=1) {

                System.out.println("数据库备份：成功");

            }else {

                System.out.println("数据库备份：失败");

            }

        } catch (Exception e) {

// TODO Auto-generated catch block

            e.printStackTrace();

        } finally {

            try {

                ps.close();

                cn.close();

            } catch (SQLException e) {

                e.printStackTrace();

            }

        }

    }


    @Test
    public void test3() throws SQLException, IOException, ClassNotFoundException {
        new MapperInit("database.properties");

        // 封装Condition
        Condition condition = new Condition();
        condition.setReturnType("UserRole");
        List<String> views = new ArrayList<>();
        views.add("`role`");
        views.add("`user_role`");

        condition.setViews(views);
        List<String> viewCondition=new ArrayList<>();
        viewCondition.add("role_id");

        condition.setViewCondition(viewCondition);
        List<String> fields = new ArrayList<>();
        fields.add("user_role.user_email");
        fields.add("GROUP_CONCAT(role.role_name) as role_name");
        condition.setFields(fields);

        //以and为连接符的查询条件
        List<Term> andCondition=new ArrayList<>();
        List<String> list=new ArrayList<>();
        list.add("2418972236@qq.com");
        list.add("123@qq.com");


        andCondition.add(new Term("`user_role`","user_email",list,TermType.IN));
        andCondition.add(new Term("`user_role`","user_email",null,TermType.GROUOBY));
        condition.setAndCondition(andCondition);
        EntityInfo<Object> entityInfo = new EntityInfo<>();
        entityInfo.setCondition(condition);
        entityInfo.setOperation("select");
        BaseMapper baseMapper=new BaseMapper(entityInfo);
    }

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

    @Test
    public void listUsersTest() throws SQLException, IOException, ClassNotFoundException {
        new MapperInit("database.properties");
        Condition condition = new Condition();
        // 封装Condition
        condition.setStartIndex(Long.valueOf(0));
        condition.setSize(Integer.valueOf(8));
        condition.setReturnType("User");
        condition.addView("user");

        condition.addAndConditionWithView(new Term("user","user_name","Ja",TermType.LIKE));
        EntityInfo<Object> entityInfo = new EntityInfo<>();
        entityInfo.setCondition(condition);
        entityInfo.setOperation("select");
        new BaseMapper(entityInfo);
    }
}
