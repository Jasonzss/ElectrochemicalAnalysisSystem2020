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
import com.bluedot.pojo.entity.Report;
import com.bluedot.pojo.entity.User;
import com.bluedot.pojo.vo.CommonResult;
import com.bluedot.utils.AlgoUtil;
import com.bluedot.utils.ImageUtil;
import com.bluedot.utils.ModelUtil;
import com.bluedot.utils.PythonUtil;
import com.bluedot.utils.constants.OperationConstants;
import com.bluedot.utils.constants.SessionConstants;
import org.junit.Test;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Timestamp;
import java.util.*;

/**
 * @Author Jason
 * @CreationDate 2022/08/20 - 0:45
 * @Description ：
 */
public class ModelService extends BaseService<Report> {
    public ModelService(Data data) {
        super(data);
    }

    public ModelService(HttpSession session, EntityInfo<?> entityInfo) {
        super(session, entityInfo);
    }

    /**
     * 负责在ModelService中个根据父类属性分析调用哪些方法来解决请求
     */
    @Override
    protected void doService() {
        String methodName = null;

        if ("insert".equals(operation)) {
            methodName = "modeling";
        } else {
            throw new UserException(CommonErrorCode.E_5001);
        }

        invokeMethod(methodName,this);
    }

    @Override
    protected boolean check() {
        return true;
    }

    /**
     * 对用户选定的实验数据进行建模，并对建模进行一个测试，最终返回一份实验报告
     */
    private void modeling(){
        Report report = new Report();
        //获取前端数据
        List<Map<String,Object>> expDataMapList = (List<Map<String, Object>>) paramList.get("expData");
        int pretreatmentAlgorithmId = (int) paramList.get("pretreatmentAlgorithmId");
        int reportDataModelId = (int) paramList.get("reportDataModelId");
        String expMaterialName = (String) paramList.get("expMaterialName");
        String userEmail = (String) session.getAttribute(SessionConstants.USER_EMAIL);

        //1. 查询指定的实验数据
        if (expDataMapList.size() < 10){
            throw new UserException(CommonErrorCode.E_4006);
        }
        Condition condition = new Condition();
        //取出所有的id放入expDataIdList
        List<Integer> expDataIdList = new ArrayList<>();
        expDataMapList.forEach((l) -> {
            expDataIdList.add((Integer) l.get("expDataId"));
        });
        //编写condition
        condition.addAndConditionWithView(new Term("exp_data","exp_data_id",expDataIdList, TermType.IN));
        entityInfo.setCondition(condition);
        select();
        //得到查询到实验数据的expDataList
        List<ExpData> expDataList = (List<ExpData>) commonResult.getData();


        //2. 查询预处理算法
        condition = new Condition();
        condition.addAndConditionWithView(new Term("algorithm","algorithm_id",pretreatmentAlgorithmId,TermType.EQUAL));
        entityInfo.setCondition(condition);
        //得到查询到的预处理算法
        List<Algorithm> list = (List<Algorithm>) commonResult.getData();
        Algorithm pretreatmentAlgorithm = list.get(0);


        //3. 查询数据建模算法
        condition = new Condition();
        condition.addAndConditionWithView(new Term("algorithm","algorithm_id",reportDataModelId,TermType.EQUAL));
        entityInfo.setCondition(condition);
        //得到查询到的预处理算法
        list = (List<Algorithm>) commonResult.getData();
        Algorithm reportDataModel = list.get(0);
        //将算法id放入report
        report.setPretreatmentAlgorithmId(reportDataModelId);
        report.setReportDataModelId(reportDataModelId);


        //4. 查询当前用户
        User user = new User();
        user.setUserEmail(userEmail);
        //将当前用户的userEmail放入report中
        report.setUser(user);
        Map<String,Object> map = new HashMap<>();
        map.put("userEmail",userEmail);
        CommonResult commonResult = new UserService(session, entityInfo).doOtherService(map, OperationConstants.SELECT);
        user = (User) commonResult.getData();


        //将查询的数据放入report中
        //将expDataList中的所有电压电流取出来
        Double[][] data = new Double[expDataList.size()][2];
        for (int i = 0; i < expDataList.size(); i++) {
            ExpData expData = expDataList.get(i);
            if (expData.getExpNewestPotential() == null || expData.getExpNewestCurrent() == null){
                data[i][0] = expDataList.get(i).getExpOriginalPotential();
                data[i][1] = expDataList.get(i).getExpOriginalCurrent();
            }else {
                //如果存在最新数据则使用最新数据
                data[i][0] = expDataList.get(i).getExpNewestPotential();
                data[i][1] = expDataList.get(i).getExpNewestCurrent();
            }
        }


        //数据预处理
        Double[][] preprocessData = AlgoUtil.preprocess(pretreatmentAlgorithm, data);


        //划分数据集，得到测试集和训练集，并分类放到report
        Map<String, Double[][]> stringMap = AlgoUtil.divideDataSet(preprocessData);
        Double[][] testSet = stringMap.get("test");
        Double[][] trainSet = stringMap.get("train");
        report.setTestSetData(Arrays.deepToString(testSet));
        report.setTrainingSetData(Arrays.deepToString(trainSet));
        //获取测试集和训练集的真实电压数据
        Double[] trainExperimentalPotential = new Double[trainSet.length];
        Double[] testExperimentalPotential = new Double[testSet.length];
        for (int i = 0; i < testSet.length; i++) {
            testExperimentalPotential[i] = testSet[i][0];
        }
        for (int i = 0; i < trainSet.length; i++) {
            trainExperimentalPotential[i] = trainSet[i][0];
        }


        //进行数据建模
        Double[] modelData = AlgoUtil.modeling(reportDataModel, stringMap.get("train"));

        //分析得到结果建模，并将其放入report中
        String equation = generateEquation(modelData);
        report.setReportResultModel(equation);

        //将训练集和测试集的实际值带入方程算出训练集预测值和测试集预测值
        Double[][] trainingSetDataAsDoubles = report.getTrainingSetDataAsDoubles();
        Double[][] testSetDataAsDoubles = report.getTestSetDataAsDoubles();
        Double[] trainPrediction = new Double[trainingSetDataAsDoubles.length];
        Double[] testPrediction = new Double[testSetDataAsDoubles.length];
        for (int i = 0; i < trainPrediction.length; i++) {
            trainPrediction[i] = executeEquation(equation, trainingSetDataAsDoubles[i][1]);
        }
        for (int i = 0; i < trainPrediction.length; i++) {
            testPrediction[i] = executeEquation(equation, testSetDataAsDoubles[i][1]);
        }

        //分析模型，并将分析的七个数据放入report
        analysisReport(report,trainPrediction,testPrediction);

        //****************************************************************************************
        //TODO 根据测试集和训练集的点位画图，并把图放入report中
        setReportGraph(report, trainExperimentalPotential, trainPrediction, report.getTrainSetIndicator());
        setReportGraph(report, testExperimentalPotential, testPrediction, report.getTestSetIndicator());


        //******************************************************************
        //向report放入系统数据
        //设置用户输入的物质名称
        report.setReportMaterialName(expMaterialName);
        //设置默认的报告题头
        report.setReportTitle(user.getUserName()+"的实验报告");
        //设置此report生成的系统时间
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        report.setReportCreateTime(timestamp);
        report.setReportLastUpdateTime(timestamp);

        //先这么办吧，指定会存在并发问题：先插入，再查出来
        //将此report插入
        entityInfo.addEntity(report);
        insert();

        //返回reportId给前端
        //暂时使用这会有并发问题的写法吧
        condition = new Condition();
        condition.setReturnType("Integer");
        condition.addFields("MAX(report_id)");
        condition.addView("report");

        //执行查询逻辑，返回reportId
        entityInfo.setCondition(condition);
        select();
    }

    /**
     * 对实验报告中的模型进行评价，得到七个参数放入该Report中
     * @param report 待分析的实验报告
     * @param trainPrediction 训练集的预测电压
     * @param testPrediction 测试集的预测电压
     */
    private void analysisReport(Report report, Double[] trainPrediction, Double[] testPrediction){
        Double[][] trainingSetDataAsDoubles = report.getTrainingSetDataAsDoubles();
        Double[][] testSetDataAsDoubles = report.getTestSetDataAsDoubles();

        //将训练集和测试集的电压数据取出
        Double[] trainingPotentialSet = new Double[trainingSetDataAsDoubles.length];
        Double[] testPotentialSet = new Double[testSetDataAsDoubles.length];
        for (int i = 0; i < trainingPotentialSet.length; i++) {
            trainingPotentialSet[i] = trainingSetDataAsDoubles[i][0];
        }
        for (int i = 0; i < testPotentialSet.length; i++) {
            testPotentialSet[i] = testSetDataAsDoubles[i][0];
        }

        //计算七项参数
        report.setRc2(ModelUtil.getR2(trainingPotentialSet,trainPrediction));
        report.setRmsec(ModelUtil.getRMSE(trainingPotentialSet,trainPrediction));
        report.setMaec(ModelUtil.getMAE(trainingPotentialSet,trainPrediction));
        report.setRp2(ModelUtil.getR2(testPotentialSet,testPrediction));
        report.setRmsep(ModelUtil.getRMSE(testPotentialSet,testPrediction));
        report.setMaep(ModelUtil.getMAE(testPotentialSet,testPrediction));
        report.setRpd(ModelUtil.getPRD(testPotentialSet,testPrediction));
    }

    /**
     * 根据数据建模得到的Double数组生成一个数学方程式
     * @param param 数据建模生成模型参数数组
     * @return 数学方程式
     */
    private String generateEquation(Double[] param){
        String subscripts = "₀₁₂₃₄₅₆₇₈₉";
        StringBuilder equation = new StringBuilder("y = ");
        equation.append(param[0]);
        for (int i = 1; i < param.length; i++) {
            if (param[i] > 0){
                equation.append("+");
            }
            equation.append(param[i]);
            equation.append("x");
            equation.append(subscripts.charAt(i));
        }
        return String.valueOf(equation);
    }

    /**
     * 根据数据建模得到的Double数组生成一个数学方程式
     * @param param 数据建模生成模型参数数组
     * @return 数学方程式
     */
    private String generateEquationWithKAndB(Double[] param){
        Double temp = param[0];
        param[0] = param[1];
        param[1] = temp;
        return generateEquation(param);
    }

    /**
     * 解析String类型的多元一次方程式，并将参数放入方程中进行计算
     * @param equation 方程式
     * @param args 多元的参数
     * @return 计算结果
     */
    private double executeEquation(String equation, double ...args){
        String subscripts = "₀₁₂₃₄₅₆₇₈₉";
        String exe = equation;
        int paramNum = 0;
        double result;

        //移除所有下标，并计算方程中有多少个参数
        for (int i = exe.length()-1; i >= 0; i--) {
            if (subscripts.contains(exe.substring(i,i+1))){
                exe = exe.substring(0,i) + exe.substring(i+1);
                paramNum++;
            }
        }

        //判断方程所需参数和填入参数个数是否一致
        if (paramNum != args.length){
            throw new UserException(CommonErrorCode.E_6001);
        }

        //初始化result
        int sum;
        int minus;
        int firstNumPosition;
        if (exe.charAt(4) == '-'){
            String temp = exe.substring(5);
            sum = temp.indexOf("+") + 5;
            minus = temp.indexOf("-") + 5;
        }else {
            sum = exe.indexOf("+");
            minus = exe.indexOf("-");
        }

        //得到等号右侧除了常量值外的第一个子式子的开头位置firstNumPosition
        if (sum < 5 && minus < 5){
            return Double.parseDouble(exe.substring(4));
        }else if (sum < 5){
            firstNumPosition = minus;
        }else if (minus < 5){
            firstNumPosition = sum;
        }else {
            firstNumPosition = Math.min(sum,minus);
        }

        result = Double.parseDouble(exe.substring(4,firstNumPosition));

        //获取方程中每个子式子的数值
        LinkedList<String> calculator = new LinkedList<>();
        LinkedList<Double> preNum = new LinkedList<>();
        int lastCalculator = 0;
        for (int i = firstNumPosition; i < exe.length(); i++) {
            if (exe.charAt(i) == '+'){
                calculator.add("+");
                lastCalculator = i;
            }else if (exe.charAt(i) == '-'){
                calculator.add("-");
                lastCalculator = i;
            }else if (exe.charAt(i) == 'x'){
                preNum.add(Double.valueOf(exe.substring(lastCalculator+1,i)));
            }
        }

        //开始计算每个子式子的值
        List<Double> subResult = new ArrayList<>();
        for (int i = 0; i < calculator.size(); i++) {
            double num = preNum.get(i) * args[i];
            if ("-".equals(calculator.get(i))){
                num = -num;
            }
            subResult.add(num);
        }

        //计算总结果
        for (Double sub : subResult) {
            result += sub;
        }

        return result;
    }

    /**
     * 根据测试集和训练集的点位画图，并把图放入report中
     * @param report 被操作实验报告
     * @param experimental 实验数据原本的实验（真实）电压
     * @param prediction 实验数据经过建模方程得到的预测电压
     * @param param 判断模型的指标
     */
    private void setReportGraph(Report report, Double[] experimental, Double[] prediction, Map<String,String> param){
        Map<String,Object> trainPaintParam = new HashMap<>();
        trainPaintParam.put("x",experimental);
        trainPaintParam.put("y",prediction);
        trainPaintParam.put("equation",generateEquationWithKAndB(ModelUtil.getFiParameters(experimental,prediction)));
        trainPaintParam.put("para",param);
        PythonUtil.executePythonAlgorithFile("paintReportGraph.py",trainPaintParam,"fig_"+report.getReportId()+".jpg");
        //获取生成的图片的流，并把文件删除

        File file = new File("src/main/resources/image/fig_"+report.getReportId()+".jpg");
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = ImageUtil.inputStreamToBytes(fis);
            report.setReportTrainingSetGraph(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            file.delete();
        }
    }
}
