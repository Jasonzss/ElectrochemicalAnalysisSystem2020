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
public class AlgoUtil extends ClassLoader{
    /**
     * 运行时的错误
     */
    private static final CommonErrorCode e4001 = CommonErrorCode.E_4001;
    private static final CommonErrorCode e4002 = CommonErrorCode.E_4002;
    private static final CommonErrorCode e4003 = CommonErrorCode.E_4003;

    //获取资源文件目录的绝对路径
    private static final String resPath =
            Thread.currentThread().getContextClassLoader().getResource("").getPath();

    //算法的类名
    private static final String algoClassName = "Main";

    //算法的入口（方法）
    private static final String algoEntry = "run";

    //算法文件后缀名
    private static final String algoTxtSuffix = "java";
    //编译后的算法文件后缀名
    private static final String algoBinSuffix = "class";

    //class文件所在的根目录
    //为target/algo/算法id
    private String root;

    public void setRoot(String root) {
        this.root = root;
    }


    /**
     * @param algo 执行的算法
     * @param data
     *  例如想对数据((0.704, -2.882e-6), (0.708, -2.848e-6), (0.712, -2.818e-6), ...)降噪，
     *  就放到这样的[[0.704, -2.882e-6], [0.708, -2.848e-6], [0.712, -2.818e-6]]数组中
     *  不过这里只有对电流数据的平滑处理，所以这项目就只传只有电流的二维数组即可，例如[[0.704], [0.708], [0.712]]
     * @return
     * 平滑处理算法的返回值为Double[][]类型。
     * 例如data[0][0]表示平滑处理后的第0个观测值的第0个属性的值
     */
    public static Double[][] smooth(Algorithm algo, Double[][] data) {
        if (algo == null || data == null) {
            //参数不能为空
            throw new UserException(e4001);
        }
        Object run = run(algo, data);
        if (run instanceof Double[][]) {
            return (Double[][]) run;
        }
        return null;
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
            throw new UserException(e4001);
        }
        Object run = run(algo, data);
        if (run instanceof Double[][]) {
            return (Double[][]) run;
        }
        return null;
    }

    /**
     * @param data 需要划分的数据集
     * @return 返回一个Map，属性【train】表示分割好的训练集，属性【test】表示分割好的测试集
     */
    public static Map<String, Double[][]> divideDataSet(Double[][] data) {
        if (data == null || data.length == 0) {
            //参数不能为空
            throw new UserException(e4001);
        }
        //返回变量
        Map<String, Double[][]> ret = new HashMap<>();
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
            throw new UserException(e4001);
        }
        Object run = run(algo, data);
        if (run instanceof Double[]) {
            return (Double[]) run;
        }
        return null;
    }

    /**
     * 执行指定算法，返回算法得到的结果
     * @param algo 需要执行的算法
     * @param data 需要算法处理的数据
     * @return 得到的处理结果
     */
    private static Object run(Algorithm algo, Object ... data) {

        if (algo == null || data == null) {
            //参数不能为空
            throw new UserException(e4001);
        }

        //算法文件的父路径
        String algoParentPath = resPath + "algo/" + algo.getAlgorithmId();

        //算法文件所在位置
        String algoFilePath =
                algoParentPath + "/" + algoClassName + "." + algoTxtSuffix;

        //返回的结果
        Object ret = null;

        try {
            //编译文件，以utf-8编译
            runProcess("javac -encoding utf-8 " + algoFilePath);
            //创建自定义的类加载器
            AlgoUtil classLoader = new AlgoUtil();
            classLoader.setRoot(algoParentPath);

            //加载算法类
            Class<?> algoClass = Class.forName(algoClassName, true, classLoader);
            //获得方法并执行
            System.out.println(data.getClass().toGenericString());
            if (data.getClass().toString().contains("Object")) {
                //如果data是Object数组也就是传了多个参数才执行
                Class[] argClass = new Class[data.length];
                for (int i = 0; i < argClass.length; i ++) {
                    argClass[i] = data[i].getClass();
                }
                Method runMethod = algoClass.getDeclaredMethod(algoEntry, argClass);
                ret = runMethod.invoke(algoClass.newInstance(), data);

            }else {
                Method runMethod = algoClass.getDeclaredMethod(algoEntry,
                        data.getClass());
                ret = runMethod.invoke(algoClass.newInstance(), (Object) data);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.getCause().printStackTrace(pw);
            e4003.setMsg("语法错误：\n" + sw);
            pw.close();
            try {
                sw.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            throw new UserException(e4003);

        } catch (NoSuchMethodException e) {
            e4003.setMsg("没有" + e.getMessage() + "方法");
            throw new UserException(e4003);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } finally {
            //删除生成的class文件
            File algoClassFile =
                    new File(algoFilePath.replace("." + algoTxtSuffix,
                            "." + algoBinSuffix));
            if (algoClassFile.exists()) {
                algoClassFile.delete();
            }
        }

        return ret;
    }

    /**
     * 执行应用程序
     * 这里只是用于编译java文件
     * @param command 应用程序名
     * @throws Exception 抛出异常
     */
    private static void runProcess(String command) {
        Process pro = null;
        try {
            pro = Runtime.getRuntime().exec(command);
            pro.waitFor();

            //如果编译出错
            if (pro.exitValue() != 0) {
                //获取错误信息
                String errorMsg = getErrorMsg(pro.getErrorStream());
                String ableOutMsg = errorMsg.substring(errorMsg.indexOf("algo"));
                e4002.setMsg("编译错误：\n" + ableOutMsg);
                throw new UserException(e4002);
            }

        } catch (InterruptedException e) {
            e4002.setMsg("编译中断");
            throw new UserException(e4002);
        } catch (IOException e) {
            e4002.setMsg("找不到javac");
            throw new UserException(e4002);
        }


    }

    /**
     * 获得编译错误信息
     * @param ins 结果流
     */
    private static String getErrorMsg(InputStream ins) {
        String line = null;
        StringBuilder builder = new StringBuilder();

        BufferedReader in = null;
        try {
            in = new BufferedReader(
                    new InputStreamReader(ins, "GBK"));
            while ((line = in.readLine()) != null) {
                builder.append(line + "\n");
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return builder.toString();
    }

    /**
     * 自定义类加载过程，实现加载用户上传的算法文件
     * @param name 全限定类名
     * @return 创建的Class对象
     * @throws ClassNotFoundException
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classData = loadClassData(name);

        if (classData == null) {
            throw new ClassNotFoundException();
        } else {
            return defineClass(name, classData, 0, classData.length);
        }
    }


    /**
     * 得到class文件的二进制（字节）数据
     * @param className
     * @return
     */
    private byte[] loadClassData(String className) {
        String fileName = root + File.separatorChar +
                className.replace('.', File.separatorChar) + ".class";
        InputStream ins = null;
        ByteArrayOutputStream baos = null;
        try {
            ins = new FileInputStream(fileName);

            baos = new ByteArrayOutputStream();

            int bufferSize = 1024;

            byte[] buffer = new byte[bufferSize];

            int length = 0;

            while ((length = ins.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            try {
                if (baos != null) {
                    baos.close();
                }
                if (ins != null) {
                    ins.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return baos.toByteArray();
    }

}
