package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.EntityInfo;
import com.bluedot.mapper.bean.Term;
import com.bluedot.mapper.bean.TermType;
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
        ExpData expData = new ExpData();
        paramList.put("userEmail",session.getAttribute(SessionConstants.USER_EMAIL));
        //TODO expCreateTime时间该怎么搞
        paramList.put("expDeleteStatus",0);

        //将其他实验数据信息填充进expData
        ReflectUtil.invokeSettersIncludeEntityByTypeAndName(paramList,expData);

        //获取用户上传的文件
        FileItem fileItem = (FileItem) paramList.get("file");

        try{
            // 读取用户上传的文件，获得相关分析数据
            analysisFile(expData, fileItem);
        }catch (NullPointerException e){
            throw new UserException(CommonErrorCode.E_1013);
        }

        //对expData中的原始电压电流进行波形分析
        Double[] waveAnalysisData = waveFormAnalysis(expData.getExpPotentialPointDataAsDouble(), expData.getExpOriginalCurrentPointDataAsDouble());
        expData.setExpOriginalPotential(waveAnalysisData[0]);
        expData.setExpOriginalCurrent(waveAnalysisData[1]);

        //设置expData的系统数据
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        expData.setExpCreateTime(timestamp);
        expData.setExpLastUpdateTime(timestamp);

        //将生成的expData储存到数据库
        entityInfo.addEntity(expData);
        insert();

        //返回expDataId给前端
        //暂时使用这会有并发问题的写法吧
        Condition condition = new Condition();
        List<String> list = new ArrayList<>();
        condition.setReturnType("Integer");
        condition.addFields("MAX(exp_data_id)");
        condition.addView("exp_data");

        //执行查询逻辑
        entityInfo.setCondition(condition);
        select();

        //查询新增的实验数据
        List<Integer> data = (List<Integer>) commonResult.getData();
        if (data.size() == 0){
            throw new UserException(CommonErrorCode.E_6001);
        }
        int expDataId = data.get(0);
        condition = new Condition();
        condition.setReturnType("ExpData");
        condition.addAndConditionWithView(new Term("exp_data","exp_data_id",expDataId, TermType.EQUAL));

        //查询后包装返回前端
        entityInfo.setCondition(condition);
        select();
        List<ExpData> expDataList = (List<ExpData>) commonResult.getData();
        commonResult = CommonResult.successResult("",expDataList.get(0));
    }

    /**
     * 读取用户上传的txt文件，将其中的电压电流文件读出，并设置默认的电流数量级
     * @param expData 分析出来的数据放入此expData中
     * @param fileItem txt文件
     */
    private void analysisFile(ExpData expData, FileItem fileItem){
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

            //将所有的点位数据读取到ExpData中
            List<Double> potentialList = new ArrayList<>();
            List<Double> currentList = new ArrayList<>();

            //读取每一行的电压电流数据
            while (s != null){
                String[] split = s.split(", ");
                //设置电压的数值
                potentialList.add(Double.valueOf(split[0]));

                //设置电流的数值
                currentList.add(Double.valueOf(split[1]));

                //读下一行
                s = br.readLine();
            }

            //将电压电流放入ExpData中
            expData.setExpPotentialPointData(potentialList.toString());
            expData.setExpOriginalCurrentPointData(currentList.toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new UserException(CommonErrorCode.E_5003);
        }
    }

    /**
     * 波形分析，根据给出的电压电流点位数据确定一条曲线，对此曲线进行波形分析
     * @param current 电位数据
     * @param potential 电流数据
     * @return Double[0]为结果电位，Double[1]为结果电流
     */
    public static Double[] waveFormAnalysis(Double[] potential, Double[] current) {
        if (potential.length != current.length) {
            throw new IndexOutOfBoundsException();
        }
        Double[] res = new Double[2];
        int peak = potential.length / 2;
        int left = 0, right = potential.length - 1;
        if (current[0] > 0) {
            while ((peak + 1) <= potential.length - 1 && (peak - 1) >= 0 && !(current[peak] >= current[peak - 1] && current[peak] >= current[peak + 1])) {
                if ((peak + 1) <= potential.length - 1 && (peak - 1) >= 0 && current[peak] > current[peak + 1] && current[peak] < current[peak - 1]) {
                    peak--;
                } else if ((peak + 1) <= potential.length - 1 && (peak - 1) >= 0 && current[peak] > current[peak - 1] && current[peak] < current[peak + 1]) {
                    peak++;
                }
            }
            while (left < peak) {
                if (current[left] > current[left + 1]) {
                    left++;
                } else {
                    break;
                }
            }
            while (peak < right) {
                if (current[right] > current[right - 1]) {
                    right--;
                } else {
                    break;
                }
            }
        } else {
            while ((peak + 1) <= potential.length - 1 && (peak - 1) >= 0 && !(current[peak] <= current[peak - 1] && current[peak] <= current[peak + 1])) {
                if ((peak + 1) <= potential.length - 1 && (peak - 1) >= 0 && current[peak] > current[peak - 1] && current[peak] < current[peak + 1]) {
                    peak--;
                } else if ((peak + 1) <= potential.length - 1 && (peak - 1) >= 0 && current[peak] < current[peak - 1] && current[peak] > current[peak + 1]) {
                    peak++;
                }
            }
            while (left < peak) {
                if (current[left] < current[left + 1]) {
                    left++;
                } else {
                    break;
                }
            }
            while (peak < right) {
                if (current[right] < current[right - 1]) {
                    right--;
                } else {
                    break;
                }
            }
        }

        double k = (current[right] - current[left]) / (potential[right] - potential[left]);
        double b = current[left] - k * potential[left];
        double y = k * potential[peak] + b;
        res[0] = potential[peak];
        res[1] = current[peak] - y;
        return res;
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
        List<Map<String, Object>> data = (List<Map<String, Object>>) commonResult.getData();
        Map<String, Object> algoMap = data.get(0);
        if (algoMap.size() <= 0){
            throw new UserException(CommonErrorCode.E_1019);
        }

        //将查询到的数据放入算法实体类中
        ReflectUtil.invokeSettersIncludeEntity(algoMap ,algorithm);

        //对数据进行分析，得到新的电流点位数据
        Double[] expNewestCurrentPointData = AlgoUtil.dataProcess(algorithm, expOriginalCurrentPointData.toArray(new Double[0]));

        //对处理后的点位数据进行波形分析
        Double[] waveAnalysisData = waveFormAnalysis(expPotentialPointData.toArray(new Double[0]), expNewestCurrentPointData);

        //将分析后的数据返回前端，用户自行决定是否保存
        Map<String,Object> analysisData = new HashMap<>();
        analysisData.put("expNewestPotential",waveAnalysisData[0]);
        analysisData.put("expNewestCurrent",waveAnalysisData[1]);
        analysisData.put("expNewestCurrentPointData",expNewestCurrentPointData);

        this.commonResult = CommonResult.successResult("处理成功",analysisData);
    }

    /**
     * 用户调整前端的波形图点位数据，根据修改后的点位数据给出新的分析结果返回
     */
    private void adjust(){
        //获取修改后的点位数据
        List<Double> expPotentialPointData = (List<Double>) paramList.get("expPotentialPointData");
        List<Double> expNewestCurrentPointData = (List<Double>) paramList.get("expNewestCurrentPointData");

        Double[] waveAnalysisData;
        try{
            //对手动修改后的点位数据进行波形分析
            waveAnalysisData = waveFormAnalysis(expPotentialPointData.toArray(new Double[0]), expNewestCurrentPointData.toArray(new Double[0]));
        }catch (NullPointerException e){
            e.printStackTrace();
            throw new UserException(CommonErrorCode.E_1014);
        }

        //将分析后的数据返回前端，用户自行决定是否保存
        Map<String,Object> analysisData = new HashMap<>();
        analysisData.put("expNewestPotential",waveAnalysisData[0]);
        analysisData.put("expNewestCurrent",waveAnalysisData[1]);
        analysisData.put("expNewestCurrentPointData",expNewestCurrentPointData);

        this.commonResult = CommonResult.successResult("处理成功",analysisData);
    }
}
