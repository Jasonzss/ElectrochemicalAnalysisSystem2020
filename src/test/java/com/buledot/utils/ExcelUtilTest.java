package com.buledot.utils;

import com.bluedot.pojo.entity.BufferSolution;
import com.bluedot.pojo.entity.ExpData;
import com.bluedot.pojo.entity.MaterialType;
import com.bluedot.pojo.entity.User;
import com.bluedot.utils.ExcelUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/8/26 9:25
 * @created:
 */
public class ExcelUtilTest {

    @Test
    public void test(){
        //sheet名
        String sheetName = "电化学分析系统数据表";

        //内容
        ExpData expData = new ExpData();
        expData.setExpDataDesc("描述1");
        MaterialType materialType = new MaterialType();
        materialType.setMaterialTypeName("化学药品");
        expData.setMaterialType(materialType);
        expData.setExpMaterialName("农药");
        User user = new User();
        user.setUserEmail("2538506575@qq.com");
        expData.setUser(user);
        BufferSolution bufferSolution = new BufferSolution();
        bufferSolution.setBufferSolutionName("敌敌畏");
        expData.setBufferSolution(bufferSolution);
        expData.setExpCreateTime(new Timestamp(System.currentTimeMillis()));
        expData.setExpLastUpdateTime(new Timestamp(System.currentTimeMillis()));
        expData.setExpPh(2.5);
        expData.setExpPotentialPointData(new Double[]{2.5,1.2,212.3});
        expData.setExpNewestCurrentPointData(new Double[]{2.532,1.2});
        expData.setExpOriginalCurrentPointData(new Double[]{2.3,1.3});

//        ExpData expData2 = new ExpData();
//        expData2.setExpDataDesc("描述2");
//        expData2.setExpPh(2.8);
//        expData2.setExpPotentialPointData(new Double[]{2.5,1.2,212.3});
//        expData2.setExpNewestCurrentPointData(new Double[]{2.3,1.3});
//        expData2.setExpOriginalCurrentPointData(new Double[]{2.532,1.2});
//
//        ExpData expData3 = new ExpData();
//        expData3.setExpDataDesc("描述3");
//        expData3.setExpPh(2.6);
//        expData3.setExpPotentialPointData(new Double[]{2.5,1.2,212.3});
//        expData3.setExpNewestCurrentPointData(new Double[]{2.532,1.2});
//        expData3.setExpOriginalCurrentPointData(new Double[]{2.3,1.3});
//
//        ExpData expData4 = new ExpData();
//        expData4.setExpDataDesc("描述4");
//        expData4.setExpPh(2.6);
////        expData4.setExpPotentialPointData(new Double[]{2.3});
////        expData4.setExpNewestCurrentPointData(new Double[]{2.2});
////        expData4.setExpOriginalCurrentPointData(new Double[]{1.1});


        HashMap<String, ExpData> map = new HashMap<>();
        map.put("表1",expData);
//        map.put("表2",expData2);
//        map.put("表3",expData3);
//        map.put("表4",expData4);
//        ExcelUtil.productExcelAndWriteToResponse(resp,map);

        HSSFWorkbook workbook = ExcelUtil.productExpDataExcel(map);

        try {
            OutputStream outputStream = Files.newOutputStream(Paths.get("test.xls"));
            workbook.write(outputStream);
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
