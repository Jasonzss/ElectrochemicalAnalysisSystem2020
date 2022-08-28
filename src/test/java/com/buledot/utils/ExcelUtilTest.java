//package com.buledot.utils;
//
//import com.bluedot.pojo.entity.ExpData;
//import com.bluedot.utils.ExcelUtil;
//import org.junit.Test;
//
//import java.util.HashMap;
//
///**
// * @author FireRain
// * @version 1.0
// * @date 2022/8/26 9:25
// * @created:
// */
//public class ExcelUtilTest {
//
//    @Test
//    public void test(){
//        //sheet名
//        String sheetName = "电化学分析系统数据表";
//
//        //内容
//        ExpData expData = new ExpData();
//        expData.setExpDataDesc("描述1");
//        expData.setExpPh(2.5);
//        expData.setExpOriginalPointData(new Double[][]{{2.5,2.2},{1.2,212.3}});
//        expData.setExpNewestPointData(new Double[][]{{2.5,2.2},{1.2,212.3}});
//
//        ExpData expData2 = new ExpData();
//        expData2.setExpDataDesc("描述2");
//        expData2.setExpPh(2.8);
//        expData2.setExpOriginalPointData(new Double[][]{{1.2,212.3}});
//        expData2.setExpNewestPointData(new Double[][]{{1.2,212.3}});
//
//        ExpData expData3 = new ExpData();
//        expData3.setExpDataDesc("描述3");
//        expData3.setExpPh(2.6);
//        expData3.setExpOriginalPointData(new Double[][]{{2.5,2.2}});
//        expData3.setExpNewestPointData(new Double[][]{{1.2,212.3}});
//
//        HashMap<String, ExpData> map = new HashMap<>();
//        map.put("表1",expData);
//        map.put("表2",expData2);
//        map.put("表3",expData3);
////        ExcelUtil.productExcelAndWriteToResponse(resp,map);
//    }
//
//}
