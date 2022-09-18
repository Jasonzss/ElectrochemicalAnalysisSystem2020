package com.bluedot.utils;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.pojo.entity.Algorithm;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @Project ElectrochemicalAnalysisSystem2020
 * @Package com.bluedot.utils
 * @DateTime 2022/8/17 16:34
 * @Author FuZhichao
 **/
public class AlgoUtil {
    /**
     * 运行时的错误
     */
    private static final CommonErrorCode E4001 = CommonErrorCode.E_4001;
    private static final CommonErrorCode E4002 = CommonErrorCode.E_4002;
    private static final CommonErrorCode E4003 = CommonErrorCode.E_4003;
    private static final CommonErrorCode E4004 = CommonErrorCode.E_4004;

    //获取资源文件目录的绝对路径
    public static final String RESPATH =
            Thread.currentThread().getContextClassLoader().getResource("").getPath().replaceAll( "%20"," ");

    //算法的类名
    public static final String ALGOCLASSNAME = "Main";

    //算法的入口（方法）
    public static final String ALGOENTRY = "run";

    //算法文件后缀名
    public static final String ALGOTXTSUFFIX = "java";
    //编译后的算法文件后缀名
    public static final String ALGOBINSUFFIX = "class";


    /**
     * @param algo 执行的算法
     * @param data
     *  例如想对数据(0.704, 0.708, 0.712, ...)降噪，
     *  就放到这样的[0.704, 0.708, 0.712]数组中
     * @return
     * 平滑处理算法的返回值为Double[]类型。
     */
    public static Double[] dataProcess(Algorithm algo, Double[] data) {
        if (algo == null || data == null) {
            //参数不能为空
            throw new UserException(E4001);
        }
        Object run = run(algo, data);
        if (run instanceof Double[]) {
            return (Double[]) run;
        } else {
            E4004.setMsg("方法返回值错误！方法返回值应为：Double[]\n");
            throw new UserException(E4004);
        }
    }

    /**
     * @param algo 执行的算法
     * @param data 用于预处理的数据
     * @return
     * 预处理算法的返回值为Double[][]类型
     * 例如data[0][0]表示预处理后的第0个观测值的第0个属性的值
     */
    public static Double[][] preprocess(Algorithm algo, Double[][] data) {
        if (algo == null || data == null) {
            //参数不能为空
            throw new UserException(E4001);
        }
        Object run = run(algo, data);
        if (run instanceof Double[][]) {
            return (Double[][]) run;
        } else {
            E4004.setMsg("方法返回值错误！方法返回值应为：Double[][]\n");
            throw new UserException(E4004);
        }
    }

    /**
     * @param data 需要划分的数据集
     * @return 返回一个Map，属性【train】表示分割好的训练集，属性【test】表示分割好的测试集
     */
    public static Map<String, Double[][]> divideDataSet(Double[][] data) {
        if (data == null || data.length == 0) {
            //参数不能为空
            throw new UserException(E4001);
        }
        //返回变量
        Map<String, Double[][]> ret = new HashMap<>(2);
        //训练集
        List<Double[]> train = new ArrayList<>();
        //测试集
        List<Double[]> test = new ArrayList<>();
        //得到0-data.length的随机序列集
        List<Integer> index = new ArrayList<>();
        for (int i = 0; i < data.length; i ++) {
            index.add(i);
        }
        Collections.shuffle(index);

        //将前80%作为训练集，后20%作为测试集。
        double ratio = 0.8;
        int splitPoint = (int) (data.length * ratio);

        Integer[] trainIndex = index.subList(0, splitPoint).toArray(new Integer[0]);
        Arrays.sort(trainIndex);
        Integer[] testIndex = index.subList(splitPoint, index.size()).toArray(new Integer[0]);
        Arrays.sort(testIndex);

        for (Integer i: trainIndex) {
            train.add(data[i]);
        }
        for (Integer i: testIndex) {
            test.add(data[i]);
        }

        ret.put("train", train.toArray(new Double[][]{}));
        ret.put("test", test.toArray(new Double[][]{}));

        return ret;
    }

    /**
     * @param algo 执行的算法
     * @param data 第0维表示需要预测的属性，之后的维数表示用于预测的属性
     * @return
     * 建模算法的返回值为Double[]类型，
     * 例如返回一个数组为[-1.0, 5]表示模型为y = -1.0 + 5x1，[-2, 8, -5]表示y = -2 + 8x1 - 5x2;
     */
    public static Double[] modeling(Algorithm algo, Double[][] data) {
        if (data == null) {
            //参数不能为空
            throw new UserException(E4001);
        }
        Object run = run(algo, data);
        if (run instanceof Double[]) {
            return (Double[]) run;
        } else {
            E4004.setMsg("方法返回值错误！方法返回值应为：Double[][]\n");
            throw new UserException(E4004);
        }
    }

    /**
     * 执行指定算法，返回算法得到的结果
     * @param algo 需要执行的算法
     * @param data 需要算法处理的数据
     * @return 得到的处理结果
     */
    private static Object run(Algorithm algo, Object data) {

        if (algo == null || data == null) {
            //参数不能为空
            throw new UserException(E4001);
        }

        //算法文件的目录
        String algoSrcPath = RESPATH + "algo/java";
        //算法的包名
        String algoPackage = "algo.java.cla.algo" + algo.getAlgorithmId();
        //算法的编译目录
        String algoTargetPath = RESPATH + algoPackage.replace(".", "/");
        //算法文件所在位置
        String algoFilePath =
                algoSrcPath + "/" + algo.getAlgorithmId() + "." + ALGOTXTSUFFIX;
        //算法编译文件位置
        String algoClassPath = algoTargetPath + "/" + ALGOCLASSNAME + "." + ALGOBINSUFFIX;

        //返回的结果
        Object ret;

        try {
            //编译文件
            long b = System.currentTimeMillis();
            compile(algoFilePath, algoPackage, algoTargetPath);
            long e = System.currentTimeMillis();
            System.out.println("编译耗时：" + (e - b) / 1000.0);

            //加载算法类
            Class<?> algoClass = Class.forName(algoPackage + "." + ALGOCLASSNAME);
            //获得方法并执行
            Method runMethod = algoClass.getDeclaredMethod(ALGOENTRY, data.getClass());
            ret = runMethod.invoke(algoClass.newInstance(), data);

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.getCause().printStackTrace(pw);
            E4003.setMsg("语法错误：\n" + sw);
            pw.close();
            try {
                sw.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            throw new UserException(E4003);

        } catch (NoSuchMethodException e) {
            E4003.setMsg("没有" + e.getMessage() + "方法");
            throw new UserException(E4003);
        } finally {
            //删除class文件
            File classFile = new File(algoClassPath);
            classFile.delete();
        }
        return ret;
    }

    private static void compile(String algoFilePath, String packageStr, String algoTargetPath) {
        //package语句的byte数组
        byte[] packageBytes = ("package " + packageStr + ";\n").getBytes();

        //源代码文件，在:classpath/algo/src/算法名.java下
        File src;
        //加了package语句的文件，在:classpath/target/algo[算法id]/Main.java下
        File temp = null;

        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            //在文件头加package语句
            src = new File(algoFilePath);
            temp = new File(algoTargetPath + "/" +ALGOCLASSNAME + "." + ALGOTXTSUFFIX);
            if (!temp.getParentFile().exists()) {
                temp.getParentFile().mkdirs();
            }else if (!temp.exists()) {
                temp.createNewFile();
            }

            fis = new FileInputStream(src);
            fos = new FileOutputStream(temp);
            //在temp里先写入package语句
            fos.write(packageBytes);
            //再将原始文件内容写入到temp
            //存放每次读取的内容
            byte[] bytes = new byte[2048];
            //不获取读到的长度，会把整个byte数组都写进去，但最后一次读这个byte数组没读完会有些不正常的值，导致出错。
            int len;
            while ((len = fis.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
            }

            fos.flush();
            //运行编译程序，以utf-8编译
            runProcess("javac -encoding utf-8 -cp \"" + RESPATH + "\" \"" + temp.getAbsolutePath() + "\"");


        } catch (FileNotFoundException e) {
            throw new UserException(CommonErrorCode.E_4005);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                //关闭资源
                if (fos != null) {
                    fos.close();
                }
                if (fis != null) {
                    fis.close();
                }
                //删除复制的源代码文件和编译文件
                temp.delete();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * 执行应用程序
     * 这里只是用于编译java文件
     * @param command 应用程序名
     */
    private static void runProcess(String command) {
        Process pro = null;
        InputStream errorStream = null;
        try {
            pro = Runtime.getRuntime().exec(command);
            pro.waitFor();

            //如果编译出错
            if (pro.exitValue() != 0) {
                errorStream = pro.getErrorStream();
                //获取错误信息
                String errorMsg = getErrorMsg(errorStream);
                if (errorMsg.contains("algo")) {
                    E4002.setMsg("编译错误：\n" + errorMsg.substring(errorMsg.indexOf("algo")));
                }else {
                    E4002.setMsg("编译错误：\n" + errorMsg.substring(errorMsg.indexOf(errorMsg)));
                }
                throw new UserException(E4002);
            }

        } catch (InterruptedException e) {
            E4002.setMsg("编译中断");
            throw new UserException(E4002);
        } catch (IOException e) {
            E4002.setMsg("找不到javac");
            throw new UserException(E4002);
        }


    }

    /**
     * 获得编译错误信息
     * @param ins 结果流
     */
    private static String getErrorMsg(InputStream ins) {
        String line;
        StringBuilder builder = new StringBuilder();

        BufferedReader in = null;
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(ins, "GBK");
            in = new BufferedReader(isr);
            while ((line = in.readLine()) != null) {
                builder.append(line + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (isr != null) {
                    isr.close();
                }
                if (in != null) {
                    in.close();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        return builder.toString();
    }

}
