package com.buledot.mapper;

import com.bluedot.mapper.BaseMapper;
import com.bluedot.mapper.MapperInit;
import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.EntityInfo;
import com.bluedot.mapper.bean.Term;
import com.bluedot.mapper.bean.TermType;
import com.bluedot.pojo.entity.RolePermission;
import com.bluedot.pojo.entity.User;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @Author SDJin
 * @CreationDate 2022/8/23 18:21
 * @Description ：
 */
public class BaseMapperTest {

//    @Test
    public void testInsert() throws SQLException, IOException, ClassNotFoundException {
        new MapperInit("database.properties");
        EntityInfo<User> entityInfo=new EntityInfo();
        entityInfo.setKey(1L);
        entityInfo.setOperation("insert");
        ArrayList<User> list=new ArrayList<>();
        User user = new User();
        user.setUserAge(12);
        user.setUserEmail("2418972236@qq.com");
//        user.setUserImg(new Byte[]{1,2,3});
        user.setUserName("lisi");
        user.setUserSalt("213");
        user.setUserPassword("123");
        user.setUserStatus(1);
        list.add(user);
        entityInfo.setEntity(list);
        BaseMapper baseMapper=new BaseMapper(entityInfo);

    }
//    @Test
    public void testDelete() throws SQLException, IOException, ClassNotFoundException {
        new MapperInit("database.properties");
        EntityInfo<User> entityInfo=new EntityInfo();
        entityInfo.setKey(1L);
        entityInfo.setOperation("delete");
        ArrayList<User> list=new ArrayList<>();
        User user = new User();
//        user.setUserAge(12);
        user.setUserEmail("2418972236@qq.com");
//        user.setUserImg(new Byte[]{1,2,3});
//        user.setUserName("lisi");
//        user.setUserSalt("213");
//        user.setUserPassword("123");
//        user.setUserStatus(1);
        list.add(user);
        entityInfo.setEntity(list);
        BaseMapper baseMapper=new BaseMapper(entityInfo);
    }

//    @Test
    public void testUpdate() throws SQLException, IOException, ClassNotFoundException {
        new MapperInit("database.properties");
        EntityInfo<User> entityInfo=new EntityInfo();
        entityInfo.setKey(1L);
        entityInfo.setOperation("update");
        ArrayList<User> list=new ArrayList<>();
        User user = new User();
        user.setUserAge(14);
        user.setUserEmail("2386@qq.com");
//        user.setUserImg(new Byte[]{1,2,3});
        user.setUserName("lisi");
        user.setUserSalt("213");
        user.setUserPassword("123");
        user.setUserStatus(2);
        list.add(user);
        entityInfo.setEntity(list);
        BaseMapper baseMapper=new BaseMapper(entityInfo);

    }

//    @Test
    public void testUser() throws SQLException, IOException, ClassNotFoundException {
        new MapperInit("database.properties");
        // 封装Condition
        Condition condition = new Condition();
        condition.addFields("user_img");
        condition.setReturnType("User");
        // 判断搜索用户的各项属性
        condition.addAndConditionWithView(new Term("user","user_email","2538506575@qq.com", TermType.EQUAL));
        EntityInfo<User> entityInfo=new EntityInfo();
        entityInfo.setCondition(condition);
        entityInfo.setOperation("select");
        new BaseMapper(entityInfo);
    }

//    @Test
    public void testPermission() throws SQLException, IOException, ClassNotFoundException {
        new MapperInit("database.properties");

        Condition condition = new Condition();
        condition.addViewCondition("role_id","role_permission");
        condition.addViewCondition("permission_id","permission");
        condition.addViewCondition("role_id","user_role");
        condition.addAndConditionWithView(new Term("user_role", "user_email","2418972236@qq.com", TermType.EQUAL));
        condition.addFields("permission_name");
        condition.setReturnType("Permission");

        //执行查询逻辑
        EntityInfo<RolePermission> entityInfo = new EntityInfo<>();
        entityInfo.setCondition(condition);
        entityInfo.setOperation("select");
        new BaseMapper(entityInfo);
    }

//    @Test
    public void testExpSelect() throws SQLException, IOException, ClassNotFoundException {
        new MapperInit("database.properties");
        Condition condition = new Condition();
        condition.addAndConditionWithView(new Term("exp_data","exp_data_id",6,TermType.EQUAL));
        condition.setReturnType("ExpData");
        //执行查询逻辑
        EntityInfo<RolePermission> entityInfo = new EntityInfo<>();
        entityInfo.setCondition(condition);
        entityInfo.setOperation("select");
        new BaseMapper(entityInfo);
    }
}
