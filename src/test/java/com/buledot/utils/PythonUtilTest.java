package com.buledot.utils;

import com.bluedot.utils.PythonUtil;
import org.junit.Test;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/8/31 19:23
 * @created:
 */
public class PythonUtilTest {


    /**
     * json类型返回值测试
     */
    @Test
    public void jsonResultTest(){
        Double[] data = new Double[]{0.001, 0.002, 0.003, 0.004, 0.005, 0.0123, 0.0089810,
                0.001, 0.002, 0.003, 0.004, 0.005, 0.0123, 0.0089810,
                0.001, 0.002, 0.003, 0.004, 0.005, 0.0123, 0.0089810,
                0.011, 0.012312, 0.012342, 0.01557, 0.02888, 0.0075686, 0.00980};
        Object value = PythonUtil.executePythonAlgorithmFile("3.py", data);
        System.out.println(value);
    }

    /**
     * 图片类型返回测试
     */
    @Test
    public void imageResultTest(){
        Double[] data = new Double[]{0.001, 0.002, 0.003, 0.004, 0.005, 0.0123, 0.0089810,
                0.001, 0.002, 0.003, 0.004, 0.005, 0.0123, 0.0089810,
                0.001, 0.002, 0.003, 0.004, 0.005, 0.0123, 0.0089810,
                0.011, 0.012312, 0.012342, 0.01557, 0.02888, 0.0075686, 0.00980};
        String path = "images/imageResultTest";

        Object result = PythonUtil.executePythonAlgorithmFile("2.py", data, path);
        System.out.println(result);

    }


    @Test
    public void paintReportTest(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("expermental",new Double[]{1.2,3.3,3.3,4.4,5.5});
        map.put("predicted",new Double[]{1.123,2.123,3.12312,4.3123,5.1231});
        map.put("equation","Y=3x+7");

        HashMap<String, Object> param = new HashMap<>();
        param.put("rc2",1.2);
        param.put("rmsec",1.2);
        param.put("maec",1.2);
        param.put("rp2",1.2);
        param.put("rmsep",1.2);
        param.put("maep",1.2);
        param.put("rpd",1.2);

        map.put("param",param);

        String path = "line.png";

        Object result = PythonUtil.executePythonAlgorithmFile("paintReportGraph.py", map, path);
        System.out.println(result);

    }

    @Test
    public void fileImageTest(){
        try {
            FileInputStream fis = new FileInputStream("images/pythonImages/women.jpg");
            BufferedInputStream bis = new BufferedInputStream(fis);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = bis.read(buffer)) != -1){
                bos.write(buffer,0,len);
            }
            byte[] result = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream("images/pythonImages/womenTestResult2.png");
            fos.write(result);
            fos.flush();

            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 二进制数组和图片之间的相互转换：通过Base64Encoder和Base64Decoder
     * 流程： 图片文件 ----> 二进制数据 --> 字符串 ----> 图片文件
     */
    @Test
    public void base64ImageTest(){
        // 将文件转为字节数组
        try {
            FileInputStream fis = new FileInputStream("images/pythonImages/women.jpg");
            BufferedInputStream bis = new BufferedInputStream(fis);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = bis.read(buffer)) != -1){
                bos.write(buffer,0,len);
            }
            byte[] result = bos.toByteArray();
            // 将数组转为字符串
            BASE64Encoder encoder = new BASE64Encoder();
            String value = encoder.encode(result);
            System.out.println(value);

            fis.close();
            bos.close();

            // 将数组转为图片
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytes = decoder.decodeBuffer(value);
            FileOutputStream fos = new FileOutputStream("images/pythonImages/women.jpg");
            fos.write(bytes,0,bytes.length);
            fos.flush();

            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 上传文件测试
     */
    @Test
    public void uploadPythonFileTest() throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("src/main/resources/algo/python/template/main1.txt");
        PythonUtil.uploadPythonFile("line.py",fileInputStream);
    }

    @Test
    public void modelTest() throws SQLException, IOException, ClassNotFoundException {
        Double[][] d = new Double[10][2];
        for (int i = 0; i < d.length; i++) {
            d[i][1] = 1.0;
            d[i][0] = 0.9;
        }

        Object o = PythonUtil.executePythonAlgorithmFile("4.py", d);
        System.out.println(o);
    }

    /**
     * python程序异常处理
     */
    @Test
    public void pythonProcessExceptionTest(){
        Double[][] data = new Double[10][2];
        for (int i = 0; i < data.length; i++) {
            data[i][0] = 1.9;
            data[i][1] = 1.0;
        }
        Object result = PythonUtil.executePythonAlgorithmFile("4.py",data);
        System.out.println(result);
    }
}
