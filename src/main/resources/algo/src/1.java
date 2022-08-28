import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Project ElectrochemicalAnalysisSystem2020
 * @DateTime 2022/8/21 16:54
 * @Author FuZhichao
 * @Description 平滑算法中的滑动平均法
 **/
public class Main {
    public Double[] run(Double[] data) {
        //默认设置窗口windowSize为5，
        // 若是数据量小于(windowSize + 1)/2，则设置windowSize为数据量-1再乘以2再+1
        int windowSize = 5;
        if (data.length < (windowSize + 1) / 2) {
            windowSize = (data.length - 1) * 2 + 1;
        }
        int n = (windowSize - 1) / 2;
        //在数据项前后各加n项
        List<Double> list = new ArrayList<>(Arrays.asList(data));
        for (int i = 1; i <= n && i < data.length; i ++) {
            //添加首
            list.add(0, data[i]);
            //添加尾
            list.add(list.size(), data[data.length - 1 - i]);
        }

        //计算均值
        Double[] ret = new Double[data.length];
        for (int i = 0; i < ret.length; i ++) {
            double sum = 0;
            for (int j = i; j < i + windowSize; j ++) {
                sum += list.get(j);
            }
            ret[i] = sum / windowSize;
        }

        return ret;
    }
}