package com.buledot.mapper;

import com.bluedot.mapper.BaseMapper;
import com.bluedot.mapper.MapperInit;
import com.bluedot.mapper.bean.EntityInfo;
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

    @Test
    public void testInsert() throws SQLException, IOException, ClassNotFoundException {
        new MapperInit("database.properties");
        EntityInfo<User> entityInfo = new EntityInfo();
        entityInfo.setKey(1L);
        entityInfo.setOperation("insert");
        ArrayList<User> list = new ArrayList<>();
        User user = new User();
        user.setUserAge(12);
        user.setUserEmail("444444@qq.com");
//        user.setUserImg(new Byte[]{1, 2, 3});
        user.setUserName("lisi");
        user.setUserSalt("213");
        user.setUserPassword("123");
        user.setUserStatus(1);
        list.add(user);
        entityInfo.setEntity(list);
        BaseMapper baseMapper=new BaseMapper(entityInfo);

    }
    @Test
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

    @Test
    public void testUpdate() throws SQLException, IOException, ClassNotFoundException {
        new MapperInit("database.properties");
        EntityInfo<User> entityInfo=new EntityInfo();
        entityInfo.setKey(1L);
        entityInfo.setOperation("update");
        ArrayList<User> list=new ArrayList<>();
        User user = new User();
        user.setUserAge(14);
        user.setUserEmail("2386@qq.com");
        user.setUserName("lisi");
        user.setUserSalt("213");
        user.setUserPassword("123");
        user.setUserStatus(2);
        list.add(user);
        entityInfo.setEntity(list);
        BaseMapper baseMapper=new BaseMapper(entityInfo);

    }
}
