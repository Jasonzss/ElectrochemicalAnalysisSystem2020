package algo.java;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

/**
 * @Author SDJin
 * @CreationDate 2022/8/31 17:06
 * @Description ：归一化算法
 */
public class Normalization {

    public Double[] run(Double[] data) {
        Optional<Double> minOptional = Arrays.stream(data).min(new Comparator<Double>() {
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
        Optional<Double> maxOptional = Arrays.stream(data).max(new Comparator<Double>() {
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
        for (int i = 0; i < data.length; i++) {
            data[i] = (data[i] - min) / d;
        }
        return data;
    }

}
