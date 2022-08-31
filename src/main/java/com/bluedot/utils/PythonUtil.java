package com.bluedot.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/8/26 10:47
 * @created: 调用python程序工具类
 */
public class PythonUtil {

    /**
     * 算法文件路径常量
     */
    private static final String BASE_PATH = "src/main/resources/algo/python/";


    /**
     * py执行命令
     */
    private static final String PYTHON_CMD = "venv/Scripts/python.exe";


    /**
     * 执行python算法文件
     * @param fileName 算法文件名
     * @param data 数组数据
     * @param type 返回数据类型
     * @return 算法执行的结果数据
     */
    @SuppressWarnings("unchecked")
    public static Object executePythonAlgorithFile(String fileName, Double[] data,ExecuteReturnType type){
        // 自定义map，将数据信息放入map中
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("data",data);

        // 将填充满数据信息的map转为json格式数据
        String jsonData;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            jsonData = objectMapper.writeValueAsString(dataMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        // 传入json格式数据并执行python程序,获取json数据结果
        Object result = executePython(fileName, jsonData, type);

        // 根据返回结果类型，转化结果
        if (result instanceof String){
            // 将json结果转成map返回
            try {
                return (Map<String,Object>)objectMapper.readValue((String) result,Map.class);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }else if (result instanceof byte[]){
            return result;
        }else {
            return null;
        }

    }

    /**
     * 执行python程序
     * @param jsonData 传递的json参数
     * @return 返回处理完后的json格式数据
     */
    private static Object executePython(String fileName, String jsonData, ExecuteReturnType type){

        //json数据编码:防止命令行传参数到python程序时出现双引号消去出现的json解析出错问题
        JsonStringEncoder encoder = new JsonStringEncoder();
        String encoderJsonData = new String(encoder.quoteAsString(jsonData));

        // 命令行参数: arg[0] python执行命令 , arg[1] base python程序路径 ,  arg[2] 算法文件路径
        String[] arg = new String[3];
        arg[0] = PYTHON_CMD;
        arg[1] = BASE_PATH + fileName;
        arg[2] = encoderJsonData;

        System.out.println("传入的python程序的参数:"+encoderJsonData);

        Object result = null;
        // 执行程序,从流中获取返回数据
        try {
            // 获取当前jvm的运行时环境,执行python程序
            Process process = Runtime.getRuntime().exec(arg);

            //如果是json类型返回结果
            if (ExecuteReturnType.JSON == type){
                // 返回的json结果
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String jsonResult = br.readLine();
                // 关闭流
                br.close();
                System.out.println("获取到python算法处理后的Json格式数据 : "+jsonResult);
                return jsonResult;
            }else if (ExecuteReturnType.PICTURE == type){
                //如果是图片类型返回结果
                InputStream inputStream = process.getInputStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = -1;
                while ((len = inputStream.read(buffer)) != -1){
                    byteArrayOutputStream.write(buffer,0,len);
                }
                //将输出流中数据转为字节数组
                byte[] byteResult = byteArrayOutputStream.toByteArray();

                System.out.println("获取到python算法生成的图片二进制数据 ：" + Arrays.toString(byteResult));

                byteArrayOutputStream.close();
                inputStream.close();

                return byteResult;
            }

            //使当前线程等待，直到该程序的进程结束，才返回调用
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return result;
    }


    public enum ExecuteReturnType{

        /**
         * json返回结果
         */
        JSON("json"),

        /**
         * 图片返回结果
         */
        PICTURE("picture");

        final String value;

        ExecuteReturnType(String value) {
            this.value = value;
        }


    }

}
