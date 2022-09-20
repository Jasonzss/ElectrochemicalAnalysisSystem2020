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
        for (int i = 0; i < data.length; i++) {
            Optional<Double> minOptional = Arrays.stream(data[i]).min(new Comparator<Double>() {
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
            Optional<Double> maxOptional = Arrays.stream(data[i]).max(new Comparator<Double>() {
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
            Double d = max - min;
            for (int j = 0; j < data[i].length; j++) {
                data[i][j] = (data[i][j] - min) / d;
            }
        }
        return data;
    }

}
