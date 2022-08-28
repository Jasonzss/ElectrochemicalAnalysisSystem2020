
import biz.source_code.dsp.filter.FilterPassType;
import biz.source_code.dsp.filter.IirFilterCoefficients;
import biz.source_code.dsp.filter.IirFilterDesignExstrom;
import org.junit.Test;

import java.util.Arrays;

/**
 * @Author SDJin
 * @CreationDate 2022/8/27 23:13
 * @Description ：巴特沃斯滤波器
 */
public class Main {

    public Double[] run(Double[] signal) {
        IirFilterCoefficients iirFilterCoefficients;
        iirFilterCoefficients = IirFilterDesignExstrom.design(FilterPassType.lowpass, 10,
                10.0 / 50, 10.0 / 50);
        double[] a = iirFilterCoefficients.a;
        double[] b = iirFilterCoefficients.b;
        double[] in = new double[b.length];
        double[] out = new double[a.length - 1];
        double[] outData = new double[signal.length];
        for (int i = 0; i < signal.length; i++) {
            System.arraycopy(in, 0, in, 1, in.length - 1);
            in[0] = signal[i];
            //calculate y based on a and b coefficients
            //and in and out.
            float y = 0;
            for (int j = 0; j < b.length; j++) {
                y += b[j] * in[j];
            }
            for (int j = 0; j < a.length - 1; j++) {
                y -= a[j + 1] * out[j];
            }
            //shift the out array
            System.arraycopy(out, 0, out, 1, out.length - 1);
            out[0] = y;
            outData[i] = y;
        }
        return Arrays.stream(outData).boxed().toArray(Double[]::new);
    }
}