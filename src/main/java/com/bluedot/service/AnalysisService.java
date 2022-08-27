package com.bluedot.service;

import com.bluedot.mapper.bean.EntityInfo;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.ExpData;

import javax.servlet.http.HttpSession;

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

    }

    @Override
    protected boolean check() {
        return true;
    }

    /**
     * 分析用户输入的数据，并将其和输入的数据保存到ExpData表与返回给前端
     */
    private void analysis(){}

    /**
     * 选择算法处理波形图点位数据
     */
    private void deal(){}

    /**
     * 用户调整前端的波形图点位数据，根据修改后的点位数据给出新的分析结果返回
     */
    private void adjust(){}
}
