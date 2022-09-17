package com.buledot.utils;

import com.bluedot.pojo.entity.ExpData;
import com.bluedot.utils.ExcelUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;

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
        expData.setExpPh(2.5);
        expData.setExpPotentialPointData(new Double[]{2.5,1.2,212.3});
        expData.setExpNewestCurrentPointData(new Double[]{2.532,1.2});
        expData.setExpOriginalCurrentPointData(new Double[]{2.3,1.3});

        ExpData expData2 = new ExpData();
        expData2.setExpDataDesc("描述2");
        expData2.setExpPh(2.8);
        expData2.setExpPotentialPointData(new Double[]{2.5,1.2,212.3});
        expData2.setExpNewestCurrentPointData(new Double[]{2.3,1.3});
        expData2.setExpOriginalCurrentPointData(new Double[]{2.532,1.2});

        ExpData expData3 = new ExpData();
        expData3.setExpDataDesc("描述3");
        expData3.setExpPh(2.6);
        expData3.setExpPotentialPointData(new Double[]{2.5,1.2,212.3});
        expData3.setExpNewestCurrentPointData(new Double[]{2.532,1.2});
        expData3.setExpOriginalCurrentPointData(new Double[]{2.3,1.3});

        ExpData expData4 = new ExpData();
        expData4.setExpDataDesc("描述4");
        expData4.setExpPh(2.6);
//        expData4.setExpPotentialPointData(new Double[]{2.3});
//        expData4.setExpNewestCurrentPointData(new Double[]{2.2});
//        expData4.setExpOriginalCurrentPointData(new Double[]{1.1});

        System.out.println(expData4);


        HashMap<String, ExpData> map = new HashMap<>();
        map.put("表1",expData);
        map.put("表2",expData2);
        map.put("表3",expData3);
        map.put("表4",expData4);
//        ExcelUtil.productExcelAndWriteToResponse(resp,map);

        HSSFWorkbook workbook = ExcelUtil.productExpDataExcel(map);
        System.out.println(workbook);
    }

}
