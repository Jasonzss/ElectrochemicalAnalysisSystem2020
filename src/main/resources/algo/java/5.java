/**
 * @Author SDJin
 * @CreationDate 2022/9/3 15:24
 * @Description ：数据预处理去均值
 */
public class Main {

    public Double[][] run(Double[][] data) {
        for(int i=0;i<data.length;i++){
            Double sum = Double.valueOf(0);
            for (int j = 0; j < data[i].length; j++) {
                sum += data[i][j];
            }
            sum /= data[i].length;
            for (int j = 0; j < data[i].length; j++) {
                data[i][j] -= sum;
            }
        }
        return data;
    }

}
