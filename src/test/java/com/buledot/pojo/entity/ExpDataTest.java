package com.buledot.pojo.entity;

import com.bluedot.pojo.entity.ExpData;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author Jason
 * @CreationDate 2022/09/02 - 10:44
 * @Description ：
 */
public class ExpDataTest {
    @Test
    public void test01(){
        ExpData expData = new ExpData();
        String a = "[12, 51, 55, 66]";
        Double[] doubles = expData.stringToDoubleArray(a);
        System.out.println(doubles[0]);
        System.out.println(doubles[1]);
        System.out.println(doubles[3]);
        System.out.println(Arrays.toString(expData.stringToDoubleArray(a)));
        expData.setExpOriginalCurrentPointData(a);
        System.out.println(expData);

        List<Double> doubleList = new ArrayList<>();
        doubleList.add(12.5);
        doubleList.add(12.5);
        doubleList.add(12.5);
        doubleList.add(12.5);

        Double[] doubles1 = expData.stringToDoubleArray(doubleList.toString());
        System.out.println(doubles1[1]);
        System.out.println(doubles1[2]);
        System.out.println(doubles1[0]);
    }

    @Test
    public void test02(){
        String a = "[0.704, 0.704, 0.704, 0.708, 0.712, 0.716, 0.72, 0.724, 0.728, 0.732, 0.736, 0.74, 0.744, 0.748, 0.752, 0.756, 0.76, 0.764, 0.768, 0.772, 0.776, 0.78, 0.784, 0.788, 0.792, 0.796, 0.8, 0.804, 0.808, 0.812, 0.816, 0.82, 0.824, 0.828, 0.832, 0.836, 0.84, 0.844, 0.848, 0.852, 0.856, 0.86, 0.864, 0.868, 0.872, 0.876, 0.88, 0.884, 0.888, 0.892, 0.896, 0.9, 0.904, 0.908, 0.912, 0.916, 0.92, 0.924, 0.928, 0.932, 0.936, 0.94, 0.944, 0.948, 0.952, 0.956, 0.96, 0.964, 0.968, 0.972, 0.976, 0.98, 0.984, 0.988, 0.992, 0.996, 1.0, 1.004, 1.008, 1.012, 1.016, 1.02, 1.024, 1.028, 1.032, 1.036, 1.04, 1.044, 1.048, 1.052, 1.056, 1.06, 1.064, 1.068, 1.072, 1.076, 1.08, 1.084, 1.088, 1.092, 1.096, 1.1]";
        String b = "[-2.882E-6, -2.882E-6, -2.882E-6, -2.848E-6, -2.818E-6, -2.793E-6, -2.775E-6, -2.76E-6, -2.748E-6, -2.741E-6, -2.736E-6, -2.735E-6, -2.735E-6, -2.739E-6, -2.746E-6, -2.753E-6, -2.762E-6, -2.771E-6, -2.781E-6, -2.795E-6, -2.811E-6, -2.827E-6, -2.848E-6, -2.872E-6, -2.9E-6, -2.931E-6, -2.968E-6, -3.01E-6, -3.061E-6, -3.118E-6, -3.186E-6, -3.263E-6, -3.354E-6, -3.457E-6, -3.579E-6, -3.719E-6, -3.886E-6, -4.077E-6, -4.293E-6, -4.53E-6, -4.79E-6, -5.066E-6, -5.36E-6, -5.663E-6, -5.972E-6, -6.268E-6, -6.541E-6, -6.775E-6, -6.975E-6, -7.12E-6, -7.207E-6, -7.227E-6, -7.18E-6, -7.069E-6, -6.911E-6, -6.701E-6, -6.453E-6, -6.184E-6, -5.91E-6, -5.641E-6, -5.387E-6, -5.152E-6, -4.944E-6, -4.763E-6, -4.613E-6, -4.487E-6, -4.383E-6, -4.298E-6, -4.228E-6, -4.179E-6, -4.145E-6, -4.118E-6, -4.091E-6, -4.072E-6, -4.06E-6, -4.062E-6, -4.075E-6, -4.094E-6, -4.109E-6, -4.133E-6, -4.164E-6, -4.195E-6, -4.235E-6, -4.282E-6, -4.326E-6, -4.386E-6, -4.439E-6, -4.5E-6, -4.553E-6, -4.605E-6, -4.663E-6, -4.743E-6, -4.819E-6, -4.912E-6, -4.989E-6, -5.07E-6, -5.158E-6, -5.25E-6, -5.346E-6, -5.445E-6, -5.548E-6, -5.654E-6]";
        ExpData expData = new ExpData();

        expData.setExpPotentialPointData(a);
        expData.setExpOriginalCurrentPointData(b);

        System.out.println(expData.toString());
    }
}
