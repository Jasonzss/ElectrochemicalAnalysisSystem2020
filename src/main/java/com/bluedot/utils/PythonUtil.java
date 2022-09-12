package com.bluedot.utils;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.pojo.entity.Algorithm;
import com.bluedot.pojo.entity.ExpData;
import com.bluedot.pojo.entity.Report;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/8/26 10:47
 * @created: 调用python程序工具类
 */
@SuppressWarnings("unchecked")
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
     * 图片的默认生成路径
     */
    private static final String IMAGE_PATH = "src/main/resources/image/";

    /**
     * 插入模板开始标志
     */
    private static final String START_FLAG = "# 算法内容";

    /**
     * 静态日志管理
     */
    private static final Logger log = LogUtil.getLogger();

    /**
     * 执行python算法文件
     * @param fileName 算法文件名
     * @param data 参数数据
     * @return 算法执行的结果数据
     */
    public static Map<String,Object> executePythonAlgorithFile(String fileName,Object data){
        return executePythonAlgorithFile(fileName,data,"");
    }

    /**
     * 执行python算法文件
     * @param fileName 算法文件名
     * @param data 参数数据
     * @param imageName 产生文件路径
     * @return 算法执行的结果数据
     */
    public static Map<String,Object> executePythonAlgorithFile(String fileName, Object data, String imageName){
        // 自定义map，将数据信息放入map中
        HashMap<String, Object> param = new HashMap<>();
        param.put("data",data);

        if (!"".equals(imageName)) {
            param.put("path",IMAGE_PATH + imageName);
        }
        return executePython(fileName, param);
    }

    /**
     * 执行python程序
     * @param param 传递的json参数
     * @return 返回处理完后的json格式数据
     */
    private static Map<String,Object> executePython(String fileName, Map<String,Object> param){

        try {
            // 将填充满数据信息的map转为json格式数据
            String jsonData;
            ObjectMapper objectMapper = new ObjectMapper();
            jsonData = objectMapper.writeValueAsString(param);

            //json数据编码:防止命令行传参数到python程序时出现双引号消去出现的json解析出错问题
            JsonStringEncoder encoder = new JsonStringEncoder();
            String encoderJsonData = new String(encoder.quoteAsString(jsonData));

            // 命令行参数: arg[0] python执行命令 , arg[1] base python程序路径 ,  arg[2] 算法文件路径
            String[] arg = new String[3];
            arg[0] = PYTHON_CMD;
            arg[1] = BASE_PATH + fileName;
            arg[2] = encoderJsonData;

            System.out.println("传入的python程序的参数:" + encoderJsonData);

            // 执行程序,从流中获取返回数据
            // 获取当前jvm的运行时环境,执行python程序
            Process process = Runtime.getRuntime().exec(arg);

            // 返回的json结果
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String jsonResult = br.readLine();
            // 关闭流
            br.close();
            System.out.println("获取到python算法处理后的Json格式数据 : " + jsonResult);

            //使当前线程等待，直到该程序的进程结束，才返回调用
            process.waitFor();

            return objectMapper.readValue(jsonResult, Map.class);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new UserException(CommonErrorCode.E_10002);
        }

    }

    /**
     * 将输入流中的文件数据，转储到fileName中，生成python真正执行的程序文件
     * @param fileName 上传的python文件名
     * @param inputStream 上传的python文件输入流
     */
    public static void uploadPythonFile(String fileName, InputStream inputStream){
        // 上传文件的字符流
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        // 模板算法算法文件的字符流
        BufferedReader tempBr;
        // 最后生成的算法文件流
        BufferedWriter bos;

        try {
            tempBr = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(BASE_PATH + TEMPLATE_FILE_PATH))));
            bos = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(BASE_PATH + fileName))));

            String line;
            while ( (line = tempBr.readLine()) != null){
                if (START_FLAG.equals(line)){
                    // 开始插入到模板中
                    String fileLine;
                    while ((fileLine = br.readLine())!=null){
                        bos.write(fileLine);
                        bos.newLine();
                    }
                }else {
                    bos.write(line);
                    bos.newLine();
                }
            }
            bos.flush();
            bos.close();
            tempBr.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("算法模板文件读取失败: "+BASE_PATH + TEMPLATE_FILE_PATH);
            log.error("上传生成算法文件失败: "+BASE_PATH + fileName);
            throw new UserException(CommonErrorCode.E_10001);
        }
    }

}
