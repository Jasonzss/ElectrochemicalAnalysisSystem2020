package com.bluedot.utils;

import com.bluedot.pojo.entity.ExpData;
import javafx.util.Pair;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/8/23 17:00
 * @created: Excel模板生成工具类
 */
public class ExcelUtil {

    /**
     * 标题信息 String(标题名) Pair(该标题名对应的子标题)
     * Pair(Integer,String) (该单元格的高度,子标题信息)
     */
    private static final List<Pair<String,List<Object>>> TITLE = new ArrayList<>();

    /**
     * 数据信息
     */
    private static final List<Pair<String,List<Object>>> VALUE_LIST = new ArrayList<>();

    private static final String BASE_INFO = "基本信息";
    private static final String ELECT_INFO = "电化学信息";
    private static final String POINT_INFO = "点位数据";

    /**
     * 将填有数据的HSSFWorkbook 写入响应中
     * @param response 响应
     * @param hssfWorkbook excel数据
     */
    public static void productExcelAndWriteToResponse(HttpServletResponse response, HSSFWorkbook hssfWorkbook){
        setResponse(response, hssfWorkbook);
    }


    /**
     * 将map数据生成excel并写入到响应流中
     * @param response 响应
     */
    public static void productExcelAndWriteToResponse(HttpServletResponse response,Map<String,ExpData> expDataMap){
        //创建HSSFWorkbook
        HSSFWorkbook workbook = ExcelUtil.productExpDataExcel(expDataMap);
        setResponse(response,workbook);

    }

    /**
     * 将expData数据生成excel并写入到响应流中
     * @param response 响应
     * @param sheetName 表名
     * @param expData 实验数据
     */
    public static void productExcelAndWriteToResponse(HttpServletResponse response, String sheetName, ExpData expData){
        //创建HSSFWorkbook
        HSSFWorkbook workbook = ExcelUtil.productExpDataExcel(sheetName,expData);
        setResponse(response,workbook);
    }

    /**
     * 生成包含单个sheet的excel
     * @param sheetName sheet名
     * @param expData 数据
     * @return 返回代表excel的HSSFWorkbook
     */
    public static HSSFWorkbook productExpDataExcel(String sheetName, ExpData expData){
        return getHSSFWorkbook(sheetName,expData,null);
    }

    /**
     * 生成包含多个sheet的excel
     * @param sheetsDataMap map数据
     * @return 返回代表excel的HSSFWorkbook
     */
    public static HSSFWorkbook productExpDataExcel(Map<String,ExpData> sheetsDataMap){
        HSSFWorkbook workbook = null;
        //遍历map，生成多个sheet
        Set<String> keySet = sheetsDataMap.keySet();
        for (String sheetName : keySet) {
            ExpData expData = sheetsDataMap.get(sheetName);
            workbook = getHSSFWorkbook(sheetName,expData,workbook);
        }
        return workbook;
    }

    /**
     * 设置响应
     */
    private static void setResponse(HttpServletResponse response,HSSFWorkbook workbook){
        //excel文件名
        String fileName = "电化学分析系统数据表" + System.currentTimeMillis() + ".xls";
        //响应到客户端
        try {
            //以附件的形式响应给浏览器
            //设置文件数据在传输过程中的编码格式
            fileName = new String(fileName.getBytes(), StandardCharsets.ISO_8859_1);
            response.setContentType("application/octet-stream;charset=ISO-8859-1");
            response.setHeader("Content-Disposition", "attachment;filename="+ fileName);
            //缓存方面的东西:不缓存浏览器本地
            //public:表示响应可能是任何缓存的，即使它只是通常是非缓存或可缓存的仅在非共享缓存中。
            //no-cache:表示响应不可能是任何缓存的
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            //将workbook写入response操作
            OutputStream os = response.getOutputStream();
            workbook.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出excel
     * @param sheetName sheet名称
     * @param content 内容
     * @return 填充后的excel
     */
    private static HSSFWorkbook getHSSFWorkbook(String sheetName, ExpData content, HSSFWorkbook workbook){

        if (workbook == null){
            workbook = new HSSFWorkbook();
            //初始化模板
            initTemplate();
        }

        // 在workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = workbook.createSheet(sheetName);
        sheet.setDefaultRowHeightInPoints((short) (256*20));

        // 主标题行生成
        HSSFRow firstRow = sheet.createRow(0);
        HSSFCellStyle firstRowStyle = parentTitleStyle(workbook);

        // 子标题行生成
        HSSFRow secondRow = sheet.createRow(1);
        HSSFCellStyle secondRowStyle = childTitleStyle(workbook);

        //内容行生成
        HSSFRow thirdRow = sheet.createRow(2);
        HSSFCellStyle contentStyle = contentStyle(workbook);

        // 计数:列
        int num = 0;
        // 标题生成
        for (Pair<String, List<Object>> currentTitle : TITLE) {
            // 创建主标题单元格
            HSSFCell firstRowCell = firstRow.createCell(num);
            // 设置主标题内容
            firstRowCell.setCellValue(currentTitle.getKey());
            // 设置主标题样式
            firstRowCell.setCellStyle(firstRowStyle);

            // 子标题
            List<Object> currentChildTitles = currentTitle.getValue();
            int size = currentChildTitles.size();
            for (int j = 0; j < size; j++) {
                // 创建子标题单元格
                HSSFCell secondRowCell = secondRow.createCell(num + j);
                // 设置子标题内容
                secondRowCell.setCellValue((String) currentChildTitles.get(j));
                // 设置子标题样式
                secondRowCell.setCellStyle(secondRowStyle);
                // 设置子标题宽度
                sheet.setColumnWidth(secondRowCell.getColumnIndex(),256*23);
            }

            // 合并主标题单元格
            sheet.addMergedRegion(new CellRangeAddress(0, 0, num, num + size - 1));
            num += size;
        }


        //数据解析
        resolveContent(content);

        //计数:内容列
        int valueNum = 0;
        //遍历填充数据
        for (int i = 0; i < VALUE_LIST.size(); i++) {
            Pair<String, List<Object>> listPair = VALUE_LIST.get(i);
            List<Object> values = listPair.getValue();
            if (i <= 1){
                //基本信息和电化学信息
                for (int j = 0; j < values.size(); j++) {
                    //创建单元格
                    HSSFCell thirdRowCell = thirdRow.createCell(valueNum+j);
                    //根据值的类型，强转放入数据
                    Object value = values.get(j);
                    if (value instanceof Date) {
                        thirdRowCell.setCellValue((Date) value);
                    }else if (value instanceof String){
                        thirdRowCell.setCellValue((String) value);
                    }else if (value instanceof Boolean){
                        thirdRowCell.setCellValue((Boolean) value);
                    }else if (value instanceof Calendar){
                        thirdRowCell.setCellValue((Calendar) value);
                    }else {
                        thirdRowCell.setCellValue(String.valueOf(value));
                    }
                    thirdRowCell.setCellStyle(contentStyle);
                }
            }else {
                //电位数据信息
                //临时存储行
                List<HSSFRow> contentRows = new ArrayList<>();
                for (int j = 0; j < values.size(); j++) {
                    //单点位值数组
                    Double[] value = (Double[]) values.get(j);
                    int finalValueNum = valueNum;
                    int finalJ = j;
                    // 若点位数据值不为空，生成对应的单元格
                    Optional.ofNullable(value).ifPresent(tag -> {
                        //填充点位数据
                        HSSFRow contentRow;
                        //遍历创建列单元格
                        for (int m = 0; m < value.length; m++) {
                            if (m == 0) {
                                //只有一行，默认第三行
                                contentRow = thirdRow;
                            } else if(finalJ == 0){
                                //只在第一次使用时创建一次行，后面沿用该行，避免覆盖当前行
                                //如果不止一行数据，创建新的行
                                contentRow = sheet.createRow(2 + m);
                                contentRows.add(contentRow);
                            } else {
                                //获取行
                                contentRow = contentRows.get(m-1);
                            }
                            //创建列单元格
                            HSSFCell contentRowCell = contentRow.createCell(finalValueNum + finalJ);
                            //填充值
                            contentRowCell.setCellValue(value[m]);
                            //设置单元格样式
                            contentRowCell.setCellStyle(contentStyle);
                        }
                    });

                }
            }
            valueNum += values.size();
        }

        VALUE_LIST.clear();

        return workbook;
    }


    /**
     * 初始化excel模板数据
     */
    private static void initTemplate(){
        // 基本信息
        List<Object> firstColTitle = new ArrayList<>();

        firstColTitle.add("实验数据id");
        firstColTitle.add("物质类型名");
        firstColTitle.add("物质名称");
        firstColTitle.add("创建时间");
        firstColTitle.add("最后修改时间");
        firstColTitle.add("用户邮箱");
        firstColTitle.add("实验数据说明");

        TITLE.add(new Pair<>(BASE_INFO,firstColTitle));

        // 电化学信息
        List<Object> secondColTitle = new ArrayList<>();

        secondColTitle.add("缓冲溶液名称");
        secondColTitle.add("原始电流");
        secondColTitle.add("原始电位");
        secondColTitle.add("最新电流");
        secondColTitle.add("最新电位");
        secondColTitle.add("PH");

        TITLE.add(new Pair<>(ELECT_INFO,secondColTitle));

        // 点位数据
        List<Object> thirdColTitle = new ArrayList<>();

        thirdColTitle.add("电位");
        thirdColTitle.add("原始电流");
//        thirdColTitle.add("最新电位");
        thirdColTitle.add("最新电流");

        TITLE.add(new Pair<>(POINT_INFO,thirdColTitle));

    }

    /**
     * 主标题样式
     * @return 样式
     */
    private static HSSFCellStyle parentTitleStyle(HSSFWorkbook workbook){
        // 创建样式
        HSSFCellStyle style = workbook.createCellStyle();
        // 横向居中
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 纵向居中
        style.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER);

        // 字体设置
        HSSFFont font = workbook.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setColor(HSSFColor.PINK.index);
        font.setFontHeightInPoints((short) 30);
        font.setFontName("宋体");

        style.setFont(font);

        return style;
    }

    /**
     * 子标题样式
     * @return 样式
     */
    private static HSSFCellStyle childTitleStyle(HSSFWorkbook workbook){
        // 创建样式
        HSSFCellStyle style = workbook.createCellStyle();
        // 横向居中
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 纵向居中
        style.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER);

        // 字体设置
        HSSFFont font = workbook.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setColor(HSSFColor.GREEN.index);
        font.setFontHeightInPoints((short) 15);
        font.setFontName("黑体");

        style.setFont(font);

        return style;
    }

    /**
     * 内容样式
     * @return 样式
     */
    private static HSSFCellStyle contentStyle(HSSFWorkbook workbook){
        // 创建样式
        HSSFCellStyle style = workbook.createCellStyle();
        // 横向居中
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 纵向居中
        style.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER);

        // 字体设置
        HSSFFont font = workbook.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        font.setColor(HSSFColor.GREY_40_PERCENT.index);
        font.setFontHeightInPoints((short) 12);
        font.setFontName("宋体");

        style.setFont(font);

        return style;
    }


    /**
     * 将content信息转化为需要的形式
     */
    private static void resolveContent(ExpData data){
        // 基本信息
        List<Object> baseInfoList = new ArrayList<>();
        baseInfoList.add(data.getExpDataId());
        baseInfoList.add(data.getMaterialType().getMaterialTypeName());
        baseInfoList.add(data.getExpMaterialName());
        baseInfoList.add(data.getExpCreateTime());
        baseInfoList.add(data.getExpLastUpdateTime());
        baseInfoList.add(data.getUser().getUserName());
        baseInfoList.add(data.getExpDataDesc());
        VALUE_LIST.add(0,new Pair<>(BASE_INFO,baseInfoList));
        //电化学信息
        List<Object> electInfo = new ArrayList<>();
        electInfo.add(data.getBufferSolution().getBufferSolutionName());
        electInfo.add(data.getExpOriginalCurrent());
        electInfo.add(data.getExpOriginalPotential());
        electInfo.add(data.getExpNewestCurrent());
        electInfo.add(data.getExpNewestPotential());
        electInfo.add(data.getExpPh());
        VALUE_LIST.add(1,new Pair<>(ELECT_INFO,electInfo));
        //点位数据
        List<Object> pointInfoList = new ArrayList<>();
        //double[][]点位数据分解
        Double[] originalCurrentPointData = data.getExpOriginalCurrentPointDataAsDouble();
        Double[] newestCurrentPointData = data.getExpNewestCurrentPointDataAsDouble();
        Double[] voltagePointData = data.getExpPotentialPointDataAsDouble();
//        Double[][] originalPointData = data.getExpOriginalPointData();
//        Double[][] newestPointData = data.getExpNewestPointData();
//        Pair<Double[], Double[]> originalData = resolvePointData(originalPointData);
//        Pair<Double[], Double[]> newestData = resolvePointData(newestPointData);
//        pointInfoList.add(originalData.getKey());
//        pointInfoList.add(originalData.getValue());
//        pointInfoList.add(newestData.getKey());
//        pointInfoList.add(newestData.getValue());
        pointInfoList.add(voltagePointData);
        pointInfoList.add(originalCurrentPointData);
        pointInfoList.add(newestCurrentPointData);
        VALUE_LIST.add(2,new Pair<>(POINT_INFO,pointInfoList));
    }

//    /**
//     * 将点位数据中的电流和电位分开
//     * @param pointData 点位数据
//     * @return 分开后的点位数据
//     */
//    private static Pair<Double[], Double[]> resolvePointData(Double[][] pointData){
//        int length = pointData.length;
//        int size = pointData[0].length;
//        Double[] potential = new Double[length];
//        Double[] current = new Double[length];
//        for (int i = 0; i < length; i++) {
//            for (int j = 0; j < size; j++) {
//                potential[i] = pointData[i][0];
//                current[i] = pointData[i][1];
//            }
//        }
//        return new Pair<>(potential, current);
//    }

}
