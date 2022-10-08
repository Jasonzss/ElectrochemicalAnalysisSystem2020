/**
 * @Author SDJin
 * @CreationDate 2022/9/3 15:24
 * @Description ：数据预处理去均值
 */
public class Main {

    public Double[][] run(Double[][] data) {
        for(int i=0;i<data[0].length;i++){
            Double sum = Double.valueOf(0);
            for (int j = 0; j < data.length; j++) {
                sum += data[j][i];
            }
            sum /= data.length;
            for (int j = 0; j < data.length; j++) {
                data[j][i] -= sum;
            }
        }
        return data;
    }

}
