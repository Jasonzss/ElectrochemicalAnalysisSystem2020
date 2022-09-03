package com.bluedot.utils;


/**
 * @Author SDJin
 * @CreationDate 2022/9/3 15:53
 * @Description ：
 */
public class ModelUtil {
    /**
     * 根据真实值和预测值求决定系数
     *
     * @param reality    真实值数组
     * @param prediction 预测值
     * @return 决定系数R2
     */
    public static Double getR2(Double[] reality, Double[] prediction) {
        if (reality.length != prediction.length) {
            throw new IndexOutOfBoundsException();
        }
        Double mean = getMean(reality);
        Double res = 1.0;
        for (int i = 0; i < reality.length; i++) {
            res -= Math.pow(prediction[i] - reality[i], 2) / Math.pow(mean - reality[i], 2);
        }
        return res;
    }

    /**
     * 根据真实值和预测值求均方根误差
     *
     * @param reality    真实值
     * @param prediction 预测值
     * @return 均方根误差RMSE
     */
    public static Double getRMSE(Double[] reality, Double[] prediction) {
        if (reality.length != prediction.length) {
            throw new IndexOutOfBoundsException();
        }
        Double res = 0.0;
        for (int i = 0; i < reality.length; i++) {
            res += Math.pow(reality[i] - prediction[i], 2);
        }
        res = Math.sqrt(res / reality.length);
        return res;
    }

    /**
     * 根据真实值和预测值求平均绝对误差
     *
     * @param reality    真实值
     * @param prediction 预测值
     * @return 平均绝对误差MAE
     */
    public static Double getMAE(Double[] reality, Double[] prediction) {
        if (reality.length != prediction.length) {
            throw new IndexOutOfBoundsException();
        }
        Double res = 0.0;
        for (int i = 0; i < reality.length; i++) {
            res += Math.abs(prediction[i] - reality[i]);
        }
        return res / reality.length;
    }

    /**
     * 根据真实值和预测值求预测偏差
     *
     * @param reality    真实值
     * @param prediction 预测值
     * @return 预测偏差PRD
     */
    public static Double getPRD(Double[] reality, Double[] prediction) {
        if (reality.length != prediction.length) {
            throw new IndexOutOfBoundsException();
        }
        //求标准差Stdev
        Double mean = getMean(reality);
        Double stdev = 0.0;
        for (int i = 0; i < reality.length; i++) {
            stdev += Math.pow(reality[i] - mean, 2);
        }
        stdev = Math.sqrt(stdev / reality.length);
        Double rmse = getRMSE(reality, prediction);
        //返回标准差与均方根误差的结果即为预测偏差
        return stdev / rmse;
    }


    /**
     * 求Double数组均值
     *
     * @param data
     * @return Double数组的均值
     */
    public static Double getMean(Double[] data) {
        Double mean = 0.0;
        for (int i = 0; i < data.length; i++) {
            mean += data[i];
        }
        return mean / data.length;
    }
}
