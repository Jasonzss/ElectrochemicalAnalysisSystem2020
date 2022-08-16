package com.bluedot.mapper;



import com.bluedot.mapper.info.Condition;
import com.bluedot.mapper.info.Term;
import com.bluedot.mapper.info.TermType;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MapperTest {

    @org.junit.Test
    public  void testSelect() throws SQLException, IOException, ClassNotFoundException {
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

        condition.setViews(views);
        condition.setFields(fields);
        condition.setAndCondition(andCondition);
        condition.setOrCondition(orCondition);
        condition.setViewCondition(viewCondition);
        condition.setStartIndex(2L);
        condition.setSize(2);
        //参数数组
        List<Object> list = new ArrayList<>();
        String s = BaseMapper.generateSelectSqL(condition, list);
        System.out.println(s);
        for (Object o : list) {
            System.out.println(o);
        }
    }
}
