/**
 * @Author SDJin
 * @CreationDate 2022/8/31 21:22
 * @Description ：标准化
 */
public class Main {
    
    public Double[] run(Double[] data) {
        //求均值
        Double mean = 0.0;
        for (Double datum : data) {
            mean = mean + datum;
        }
        mean /= data.length;
        //求标准差
        Double dev = getStandardDeviation(data, mean);
        for (int i = 0; i < data.length; i++) {
            data[i]=(data[i]-mean)/dev;
        }
        return data;
    }

    /**
     * 求标准差
     * @param data 样本数据
     * @param mean 样本数据均值
     * @return 返回样本数据的标准差
     */
    Double getStandardDeviation(Double[] data, Double mean) {
        //求方差
        Double variance = 0.0;
        for (Double datum : data) {
            variance = variance + (Math.pow((datum - mean), 2));
        }
        variance = variance / data.length;
        //返回方差的开方
        return Math.sqrt(variance);
    }


}
