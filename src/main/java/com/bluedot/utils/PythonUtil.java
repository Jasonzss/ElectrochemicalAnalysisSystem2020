package com.bluedot.utils;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.pojo.entity.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
     * python模板文件路径
     */
    private static final String  TEMPLATE_FILE_PATH= "template/example.txt";

    /**
     * 插入模板开始标志
     */
    private static final String START_FLAG = "# 算法内容";

    /**
     * 静态日志管理
     */
    private static final Logger log = LogUtil.getLogger();


    public static byte[] createAlgorithmImage(){
        return null;
    }

    public static Double[] dataProcess(Algorithm algorithm, Double[] data){
        String fileName = algorithm.getAlgorithmId() + ".py";
        return (Double[]) executePythonAlgorithFile(fileName,data,ExecuteReturnType.JSON);
    }

    public static Double[][] preprocess(Algorithm algorithm, Double[][] data){
        String fileName = algorithm.getAlgorithmId() + ".py";
        return (Double[][]) executePythonAlgorithFile(fileName,data,ExecuteReturnType.JSON);
    }

    public static Map<String, Double[][]> divideDataSet(Double[][] data){
        return null;
    }

    public static Double[] modeling(Algorithm algorithm, Double[][] data){
        String fileName = algorithm.getAlgorithmId() + ".py";
        return (Double[]) executePythonAlgorithFile(fileName,data,ExecuteReturnType.JSON);
    }

    /**
     * 执行python算法文件
     * @param fileName 算法文件名
     * @param data 数组数据
     * @param type 返回数据类型
     * @return 算法执行的结果数据
     */
    @SuppressWarnings("unchecked")
    public static Object executePythonAlgorithFile(String fileName, Object data,ExecuteReturnType type){
        // 自定义map，将数据信息放入map中
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("data",data);

        // 将填充满数据信息的map转为json格式数据
        String jsonData;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            jsonData = objectMapper.writeValueAsString(dataMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new UserException(CommonErrorCode.E_9002);
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
                throw new UserException(CommonErrorCode.E_9002);
            }
        }else if (result instanceof byte[]){
            return result;
        }else {
            return null;
        }

    }


    /**
     * 上传python文件，并加入python模板文件中，生成真正的执行的python文件
     * @param fileItem 文件表单项
     */
    public static void uploadPythonFile(FileItem fileItem){
        InputStream inputStream;
        try {
            inputStream = fileItem.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String fieldName = fileItem.getFieldName();

        productPythonFile(fieldName,inputStream);
    }

    /**
     * 将输入流中的文件数据，转储到fileName中，生成python真正执行的程序文件
     * @param fileName 上传的python文件名
     * @param inputStream 上传的python文件输入流
     */
    private static void productPythonFile(String fileName, InputStream inputStream){
        // 上传文件的字符流
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        // 模板算法算法文件的字符流
        BufferedReader tempBr;
        // 最后生成的算法文件流
        BufferedOutputStream bos;

        try {
            tempBr = new BufferedReader(new InputStreamReader(new FileInputStream(BASE_PATH + TEMPLATE_FILE_PATH)));
            bos = new BufferedOutputStream(new FileOutputStream(BASE_PATH + fileName));

            String line;
            while ( (line = tempBr.readLine()) != null){
                if (START_FLAG.equals(line)){
                    // 开始插入到模板中
                    String fileLine;
                    while ((fileLine = br.readLine())!=null){
                        bos.write(fileLine.getBytes(StandardCharsets.UTF_8));
                    }
                }else {
                    bos.write(line.getBytes(StandardCharsets.UTF_8));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error("算法模板文件读取失败:"+BASE_PATH + TEMPLATE_FILE_PATH);
            log.error("上传生成算法文件失败:"+BASE_PATH + fileName);
            throw new UserException(CommonErrorCode.E_9001);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("文件输入输出流读写出现异常: " + BASE_PATH + TEMPLATE_FILE_PATH);
            throw new UserException(CommonErrorCode.E_9001);
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

                System.out.println("获取到python算法生成的图片二进制数据字节大小 ：" + byteResult.length);
                System.out.println("获取到python算法生成的图片二进制数据 ：" + Arrays.toString(byteResult));

                byteArrayOutputStream.close();
                inputStream.close();

                return byteResult;
            }

            //使当前线程等待，直到该程序的进程结束，才返回调用
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new UserException(CommonErrorCode.E_9002);
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
