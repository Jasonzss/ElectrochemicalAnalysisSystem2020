
/**
 * @Author SDJin
 * @CreationDate 2022/8/31 21:22
 * @Description ：数据预处理标准化
 */
public class Main {
    
    public Double[][] run(Double[][] data) {
        int dimension = data[0].length;
        int length = data.length;
        for(int i=0;i<dimension;i++){
            Double[] d=  new Double[length];
            Double mean = 0.0;
            //求均值
            for(int j=0;j<length;j++){
                d[j]=data[j][i];
                mean = mean + data[j][i];
            }
            mean /= length;
            //求标准差
            Double dev = getStandardDeviation(d, mean);
            for (int j = 0; j < length; j++) {
                data[j][i]=(data[j][i]-mean)/dev;
            }
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
