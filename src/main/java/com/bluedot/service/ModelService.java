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
import com.bluedot.utils.AlgoUtil;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        //获取前端数据
        List<Map<String,Object>> expDataMapList = (List<Map<String, Object>>) paramList.get("expData");
        int pretreatmentAlgorithmId = (int) paramList.get("pretreatmentAlgorithmId");
        int reportDataModelId = (int) paramList.get("reportDataModelId");
        String expMaterialName = (String) paramList.get("expMaterialName");

        //查询指定的实验数据
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
        //得到查询到的expDataList
        List<ExpData> expDataList = (List<ExpData>) commonResult.getData();

        //查询预处理算法
        condition = new Condition();
        condition.addAndConditionWithView(new Term("algorithm","algorithm_id",pretreatmentAlgorithmId,TermType.EQUAL));
        entityInfo.setCondition(condition);
        //得到查询到的预处理算法
        List<Algorithm> list = (List<Algorithm>) commonResult.getData();
        Algorithm pretreatmentAlgorithm = list.get(0);

        //查询数据建模算法
        condition = new Condition();
        condition.addAndConditionWithView(new Term("algorithm","algorithm_id",reportDataModelId,TermType.EQUAL));
        entityInfo.setCondition(condition);
        //得到查询到的预处理算法
        list = (List<Algorithm>) commonResult.getData();
        Algorithm reportDataModel = list.get(0);

        //将查询的数据放入report中
        Report report = new Report();

        //TODO 进行预处理
//        AlgoUtil.preprocess()

        //划分数据集，得到测试集和训练集
//        AlgoUtil.divideDataSet()

        //进行数据建模
//        AlgoUtil.modeling()
    }
}
