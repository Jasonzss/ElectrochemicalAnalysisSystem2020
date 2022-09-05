package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.EntityInfo;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.Algorithm;
import com.bluedot.pojo.entity.ExpData;
import com.bluedot.pojo.vo.CommonResult;
import com.bluedot.utils.AlgoUtil;
import com.bluedot.utils.ReflectUtil;
import com.bluedot.utils.constants.SessionConstants;
import org.apache.commons.fileupload.FileItem;

import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Jason
 * @CreationDate 2022/08/20 - 0:41
 * @Description ：
 */
public class AnalysisService extends BaseService<ExpData> {
    public AnalysisService(Data data) {
        super(data);
    }

    public AnalysisService(HttpSession session, EntityInfo<?> entityInfo) {
        super(session, entityInfo);
    }

    /**
     * 负责在AnalysisService中个根据父类属性分析调用哪些方法来解决请求
     */
    @Override
    protected void doService() {
        String methodName = null;

        switch (operation){
            case "insert":
                methodName = "analysis";
                break;
            case "update":
                if (paramList.containsKey("algorithmId")){
                    methodName = "deal";
                }else {
                    methodName = "adjust";
                }
                break;
            default:
                throw new UserException(CommonErrorCode.E_5001);
        }

        invokeMethod(methodName,this);
    }

    @Override
    protected boolean check() {
        return true;
    }

    /**
     * 分析用户输入的数据，并将其和输入的数据保存到ExpData表与返回给前端
     */
    private void analysis(){
        //获取用户上传的文件
        FileItem fileItem = (FileItem) paramList.get("file");
        ExpData expData;

        try{
            // 读取用户上传的文件，获得相关分析数据
             expData = analysisFile(fileItem);
        }catch (NullPointerException e){
            throw new UserException(CommonErrorCode.E_1013);
        }

        //对expData中的原始电压电流进行波形分析
        Map<String, Double> waveAnalysisData = waveAnalysis(expData.getExpPotentialPointDataAsDouble(), expData.getExpOriginalCurrentPointDataAsDouble());
        expData.setExpOriginalPotential(waveAnalysisData.get("ep"));
        expData.setExpOriginalCurrent(waveAnalysisData.get("ip"));

        //设置expData的系统数据
        paramList.put("userEmail",session.getAttribute(SessionConstants.USER_EMAIL));
        //TODO expCreateTime时间该怎么搞
        paramList.put("expDeleteStatus",0);

        //将其他实验数据信息填充进expData
        ReflectUtil.invokeSettersIncludeEntity(paramList,expData);

        //将生成的expData储存到数据库
        entityInfo.addEntity(expData);
        insert();

        //TODO 如何将expData插入数据库后连带着expDataId一起返回给前端
    }

    /**
     * 读取用户上传的txt文件，将其中的电压电流文件读出，并设置默认的电流数量级
     * @param fileItem txt文件
     * @return 带有电压电流数量级数据的ExpData对象
     */
    private ExpData analysisFile(FileItem fileItem){
        ExpData expData = null;

        try {
            //创建字符输入流读取对象读取传入的文件
            InputStreamReader isr = new InputStreamReader(fileItem.getInputStream(),"GBK");
            BufferedReader br = new BufferedReader(isr);

            //先读取第一行
            String line = br.readLine();

            while (!"Potential/V, Current/A".equals(line)){
                //读取txt文件直到点位数据之前
                line = br.readLine();
            }
            String s = br.readLine();
            //第一行电流电压数据
            s = br.readLine();

            //获取数量级
            String orderOfMagnitudes = s.substring(s.indexOf("e"));

            //将所有的点位数据读取到ExpData中
            expData = new ExpData();
            List<Double> potentialList = new ArrayList<>();
            List<Double> currentList = new ArrayList<>();

            //读取每一行的电压电流数据
            while (s != null){
                String[] split = s.split(", ");
                //设置电压的数值
                potentialList.add(Double.valueOf(split[0]));

                //设置电流的数值
                String current = split[1];
                String currentNum = current.substring(0,current.indexOf("e"));
                //将当前数据的数量级和第一行的数量级进行比较
                int curr = Integer.parseInt(current.substring(current.indexOf("e")).substring(1));
                int first = Integer.parseInt(orderOfMagnitudes.substring(1));

                //修改所有数值的数量级与第一行数据的数量级一致，达到统一数量级的目的
                currentList.add(changeTheOrderOfMagnitude(Double.parseDouble(currentNum),curr-first));

                //读下一行
                s = br.readLine();
            }

            //将电压电流放入ExpData中
            expData.setExpPotentialPointData(potentialList.toString());
            expData.setExpOriginalCurrentPointData(currentList.toString());
            //将数量级放入ExpData中
            expData.setOrderOfMagnitudes(orderOfMagnitudes);

        } catch (IOException e) {
            e.printStackTrace();
            throw new UserException(CommonErrorCode.E_5003);
        }

        return expData;
    }

    /**
     * 转换数字的数量级，例如
     * 0.125e7 -> 1.25e6
     * 12.5e-7 -> 1.25e-6
     * -0.125e7 -> -1.25e6
     * -12.5e-7 -> -1.25e-6
     * @param number 被转换的数字
     * @param order 数字当前的数量级变化阶数 = 当前阶数-目标阶数，小数点左右移动一般为order左加右减
     * @return 转换后的数字
     */
    private double changeTheOrderOfMagnitude(double number, int order){
        if (order < 0){
            for (int i = 0; i < -order; i++) {
                number /= 10;
            }
        }else if (order > 0){
            for (int i = 0; i < order; i++) {
                number *= 10;
            }
        }

        return number;
    }

    /**
     * 波形分析，根据给出的电压电流点位数据确定一条曲线，对此曲线进行波形分析
     * @param potentialPoint 电压点位数据
     * @param currentPoint 电流点位数据
     * @return 两个key，一个ep（单位为V），一个ip（单位为A）
     */
    private Map<String,Double> waveAnalysis(Double[] potentialPoint, Double[] currentPoint){
        return null;
    }

    /**
     * 选择算法处理波形图点位数据
     */
    private void deal(){
        //获取点位数据
        List<Double> expPotentialPointData = (List<Double>) paramList.get("expPotentialPointData");
        List<Double> expOriginalCurrentPointData = (List<Double>) paramList.get("expOriginalCurrentPointData");

        if (expOriginalCurrentPointData == null || expPotentialPointData == null){
            throw new UserException(CommonErrorCode.E_1014);
        }

        //查询算法
        Algorithm algorithm = new Algorithm();
        Map<String,Object> map = new HashMap<>();

        //根据algorithmId查询对应算法
        map.put("algorithmId",paramList.get("algorithmId"));
        CommonResult commonResult = new AlgorithmService(session, entityInfo).doOtherService(map, "select");
        Map<String, Object> data = (Map<String, Object>) commonResult.getData();

        //将查询到的数据放入算法实体类中
        ReflectUtil.invokeSettersIncludeEntity(data,algorithm);

        //对数据进行分析，得到新的电流点位数据
        Double[] expNewestCurrentPointData = AlgoUtil.dataProcess(algorithm, expOriginalCurrentPointData.toArray(new Double[0]));

        //对处理后的点位数据进行波形分析
        Map<String, Double> waveAnalysisData = waveAnalysis(expPotentialPointData.toArray(new Double[0]), expNewestCurrentPointData);

        //将分析后的数据返回前端，用户自行决定是否保存
        Map<String,Object> analysisData = new HashMap<>();
        analysisData.put("expNewestPotential",waveAnalysisData.get("ep"));
        analysisData.put("expNewestCurrent",waveAnalysisData.get("ip"));
        analysisData.put("expNewestCurrentPointData",expNewestCurrentPointData);

        this.commonResult = CommonResult.successResult("处理成功",analysisData);
    }

    /**
     * 用户调整前端的波形图点位数据，根据修改后的点位数据给出新的分析结果返回
     */
    private void adjust(){
        //获取点位数据
        List<Double> expPotentialPointData = (List<Double>) paramList.get("expPotentialPointData");
        List<Double> expNewestCurrentPointData = (List<Double>) paramList.get("expNewestCurrentPointData");

        Map<String, Double> waveAnalysisData;
        try{
            //对手动修改后的点位数据进行波形分析
            waveAnalysisData = waveAnalysis(expPotentialPointData.toArray(new Double[0]), expNewestCurrentPointData.toArray(new Double[0]));
        }catch (NullPointerException e){
            e.printStackTrace();
            throw new UserException(CommonErrorCode.E_1014);
        }

        //将分析后的数据返回前端，用户自行决定是否保存
        Map<String,Object> analysisData = new HashMap<>();
        analysisData.put("expNewestPotential",waveAnalysisData.get("ep"));
        analysisData.put("expNewestCurrent",waveAnalysisData.get("ip"));
        analysisData.put("expNewestCurrentPointData",expNewestCurrentPointData);

        this.commonResult = CommonResult.successResult("处理成功",analysisData);
    }
}
