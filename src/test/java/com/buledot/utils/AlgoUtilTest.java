package com.buledot.utils;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.pojo.entity.Algorithm;
import com.bluedot.utils.AlgoUtil;
import org.junit.Test;

/**
 * @Author SDJin
 * @CreationDate 2022/8/27 15:48
 * @Description ：
 */
public class AlgoUtilTest {
    @Test
    public  void testDataProcess(){
        Algorithm algorithm = new Algorithm();
        algorithm.setAlgorithmId(1);
        algorithm.setAlgorithmFileName("1378799690@qq.com_1");
        Double[] data=new Double[]{1.0,2.0,3.0,4.2,5.1,3.9,3.1,1.7};
        Double[] doubles = AlgoUtil.dataProcess(algorithm, data);
        for (Double aDouble : doubles) {
            System.out.println(aDouble);
        }
    }
    @Test
    public void testFiltering(){
        Algorithm algorithm = new Algorithm();
        algorithm.setAlgorithmId(1);
        algorithm.setAlgorithmFileName("Filtering");
        Double[] a=new Double[]{-2.882e-6
                , -2.848e-6
                , -2.818e-6
                ,-2.793e-6
                ,-2.775e-6
                ,-2.760e-6
                ,-2.748e-6
                ,-2.741e-6
                ,-2.736e-6
                ,-2.735e-6
                ,-2.735e-6
                ,-2.739e-6
                ,-2.746e-6
                ,-2.753e-6
                ,-2.762e-6
                ,-2.771e-6
                ,-2.781e-6
                ,-2.795e-6
                ,-2.811e-6
                ,-2.827e-6
                ,-2.848e-6
                ,-2.872e-6
                ,-2.900e-6
                ,-2.931e-6
                ,-2.968e-6
                ,-3.010e-6
                ,-3.061e-6
                , -3.118e-6
                , -3.186e-6
                , -3.263e-6
                , -3.354e-6
                , -3.457e-6
                , -3.579e-6
                , -3.719e-6
                , -3.886e-6
                , -4.077e-6
                , -4.293e-6
                , -4.530e-6
                , -4.790e-6
                , -5.066e-6
                , -5.360e-6
                , -5.663e-6
                , -5.972e-6
                , -6.268e-6
                , -6.541e-6
                , -6.775e-6
                , -6.975e-6
                , -7.120e-6
                , -7.207e-6
                , -7.227e-6
                , -7.180e-6
                , -7.069e-6
                , -6.911e-6
                , -6.701e-6
                , -6.453e-6
                , -6.184e-6
                , -5.910e-6
                , -5.641e-6
                , -5.387e-6
                , -5.152e-6
                , -4.944e-6
                , -4.763e-6
                , -4.613e-6
                , -4.487e-6
                , -4.383e-6
                , -4.298e-6
                , -4.228e-6
                , -4.179e-6
                , -4.145e-6
                , -4.118e-6
                , -4.091e-6
                , -4.072e-6
                , -4.060e-6
                , -4.062e-6
                , -4.075e-6
                , -4.094e-6
                , -4.109e-6
                , -4.133e-6
                , -4.164e-6
                , -4.195e-6
                , -4.235e-6
                , -4.282e-6
                , -4.326e-6
                , -4.386e-6
                , -4.439e-6
                , -4.500e-6
                , -4.553e-6
                , -4.605e-6
                , -4.663e-6
                , -4.743e-6
                , -4.819e-6
                , -4.912e-6
                ,-4.989e-6
                , -5.070e-6
                , -5.158e-6
                , -5.250e-6
                , -5.346e-6
                , -5.445e-6
                , -5.548e-6
                , -5.654e-6};
        Double[] doubles = AlgoUtil.dataProcess(algorithm, a);
        for (Double aDouble : doubles) {
            System.out.println(aDouble);
        }
    }
}
