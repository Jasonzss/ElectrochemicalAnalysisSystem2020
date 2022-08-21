package com.bluedot.service;

import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.Report;

/**
 * @Author Jason
 * @CreationDate 2022/08/20 - 0:45
 * @Description ：
 */
public class ModelService extends BaseService<Report> {
    public ModelService(Data data) {
        super(data);
    }

    /**
     * 负责在ModelService中个根据父类属性分析调用哪些方法来解决请求
     */
    @Override
    protected void doService() {

    }

    /**
     * 对用户选定的实验数据进行建模，并对建模进行一个测试，最终返回一份实验报告
     */
    private void modeling(){}
}
