import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

/**
 * @Author SDJin
 * @CreationDate 2022/8/31 17:06
 * @Description ：数据处理归一化算法
 */
public class Main {

    public Double[][] run(Double[][] data) {
        int dimension = data[0].length;
        int length = data.length;
        for (int i = 0; i < dimension; i++) {
           Double[] d=  new Double[length];
           for(int j=0;j<length;j++){
               d[j]=data[j][i];
           }
            Optional<Double> minOptional = Arrays.stream(d).min(new Comparator<Double>() {
                @Override
                public int compare(Double o1, Double o2) {
                    if (o1 > o2) {
                        return 1;
                    } else if (o1 < o2) {
                        return -1;
                    } else {
                        return 0;
                    }

                }
            });
            Optional<Double> maxOptional = Arrays.stream(d).max(new Comparator<Double>() {
                @Override
                public int compare(Double o1, Double o2) {
                    if (o1 > o2) {
                        return 1;
                    } else if (o1 < o2) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
            Double min = minOptional.get();
            Double max = maxOptional.get();
            Double drc = max - min;
            for (int j = 0; j < length; j++) {
                data[j][i] = (data[j][i] - min) / drc;
            }
        }
        return data;
    }

}
