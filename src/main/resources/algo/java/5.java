/**
 * @Author SDJin
 * @CreationDate 2022/9/3 15:24
 * @Description ：去均值
 */
public class Main {

    public Double[] run(Double[] data) {
        Double sum = Double.valueOf(0);
        for (int i = 0; i < data.length; i++) {
            sum += data[i];
        }
        sum /= data.length;
        for (int i = 0; i < data.length; i++) {
            data[i] -= sum;
        }
        return data;
    }
}
