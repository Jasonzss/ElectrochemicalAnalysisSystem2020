//package com.buledot.utils;
//
////import biz.source_code.dsp.filter.FilterPassType;
////import biz.source_code.dsp.filter.IirFilterCoefficients;
////import biz.source_code.dsp.filter.IirFilterDesignExstrom;
//import org.junit.Test;
//
//import java.util.Arrays;
//
///**
// * @Author SDJin
// * @CreationDate 2022/8/27 23:13
// * @Description ：
// */
//public class AlgoTest {
//    public static void main(String[] args) {
//
//        double[] time = new double[150];
//        double[] valueA = new double[150];
//        for (int i = 0; i < 50 * 3; i++) {
//            time[i] = i / 50.0;
//            valueA[i] = Math.sin(2 * Math.PI * 5 * time[i]) + Math.sin(2 * Math.PI * 15 * time[i]);
//        }
//        for (double v : valueA) {
//            System.out.println(v);
//        }
//    }
//
//
//    @Test
//    public void test() {
//       Double[] a=new Double[]{-2.882e-6
//               , -2.848e-6
//               , -2.818e-6
//               ,-2.793e-6
//               ,-2.775e-6
//               ,-2.760e-6
//               ,-2.748e-6
//               ,-2.741e-6
//               ,-2.736e-6
//               ,-2.735e-6
//               ,-2.735e-6
//               ,-2.739e-6
//               ,-2.746e-6
//               ,-2.753e-6
//               ,-2.762e-6
//               ,-2.771e-6
//               ,-2.781e-6
//               ,-2.795e-6
//               ,-2.811e-6
//               ,-2.827e-6
//               ,-2.848e-6
//               ,-2.872e-6
//               ,-2.900e-6
//               ,-2.931e-6
//               ,-2.968e-6
//               ,-3.010e-6
//               ,-3.061e-6
//               , -3.118e-6
//               , -3.186e-6
//               , -3.263e-6
//               , -3.354e-6
//               , -3.457e-6
//               , -3.579e-6
//               , -3.719e-6
//               , -3.886e-6
//               , -4.077e-6
//               , -4.293e-6
//               , -4.530e-6
//               , -4.790e-6
//               , -5.066e-6
//               , -5.360e-6
//               , -5.663e-6
//               , -5.972e-6
//               , -6.268e-6
//               , -6.541e-6
//               , -6.775e-6
//               , -6.975e-6
//               , -7.120e-6
//               , -7.207e-6
//               , -7.227e-6
//               , -7.180e-6
//               , -7.069e-6
//               , -6.911e-6
//               , -6.701e-6
//               , -6.453e-6
//               , -6.184e-6
//               , -5.910e-6
//               , -5.641e-6
//               , -5.387e-6
//               , -5.152e-6
//               , -4.944e-6
//               , -4.763e-6
//               , -4.613e-6
//               , -4.487e-6
//               , -4.383e-6
//               , -4.298e-6
//               , -4.228e-6
//               , -4.179e-6
//               , -4.145e-6
//               , -4.118e-6
//               , -4.091e-6
//               , -4.072e-6
//               , -4.060e-6
//               , -4.062e-6
//               , -4.075e-6
//               , -4.094e-6
//               , -4.109e-6
//               , -4.133e-6
//               , -4.164e-6
//               , -4.195e-6
//               , -4.235e-6
//               , -4.282e-6
//               , -4.326e-6
//               , -4.386e-6
//               , -4.439e-6
//               , -4.500e-6
//               , -4.553e-6
//               , -4.605e-6
//               , -4.663e-6
//               , -4.743e-6
//               , -4.819e-6
//               , -4.912e-6
//               ,-4.989e-6
//               , -5.070e-6
//               , -5.158e-6
//               , -5.250e-6
//               , -5.346e-6
//               , -5.445e-6
//               , -5.548e-6
//               , -5.654e-6};
//        Double[] doubles = IIRFilter(a);
//        for (Double aDouble : doubles) {
//            System.out.println(aDouble);
//        }
//
//    }
//
//    public static synchronized Double[] IIRFilter(Double[] signal) {
//        IirFilterCoefficients iirFilterCoefficients;
//        iirFilterCoefficients = IirFilterDesignExstrom.design(FilterPassType.lowpass, 10,
//                10.0 / 50, 10.0 / 50);
//        double[] a = iirFilterCoefficients.a;
//        double[] b = iirFilterCoefficients.b;
//        double[] in = new double[b.length];
//        double[] out = new double[a.length - 1];
//        double[] outData = new double[signal.length];
//        for (int i = 0; i < signal.length; i++) {
//            System.arraycopy(in, 0, in, 1, in.length - 1);
//            in[0] = signal[i];
//            //calculate y based on a and b coefficients
//            //and in and out.
//            float y = 0;
//            for (int j = 0; j < b.length; j++) {
//                y += b[j] * in[j];
//            }
//            for (int j = 0; j < a.length - 1; j++) {
//                y -= a[j + 1] * out[j];
//            }
//            //shift the out array
//            System.arraycopy(out, 0, out, 1, out.length - 1);
//            out[0] = y;
//            outData[i] = y;
//        }
//        return Arrays.stream(outData).boxed().toArray(Double[]::new);
//    }
//}