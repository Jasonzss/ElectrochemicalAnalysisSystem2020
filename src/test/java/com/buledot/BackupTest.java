package com.buledot;

import com.bluedot.service.BackupService;
import com.google.protobuf.ServiceException;
import org.junit.Test;

import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BackupTest {
    private static String hostIP = "120.79.71.79";
    private static String userName = "root";
    private static String password = "aa520411";
    //sql文件存储的路径
    private String savePath = "D:/MysqlFile";

    //数据库名
    private static String databaseName = "electrochemical_analysis_system";
    private static final int BUFFER = 8192;

    private String sqlFilePath;
    @Test
    public void test() throws ServiceException, IOException, InterruptedException {
        // mysql -h端口号 -u用户 -p密码 数据库 < d:/test.sql 恢复到数据库中
        File datafile = new File("D:\\MysqlFile\\DataBase20220902165842.sql");
        if( !datafile.exists() ){
            throw new ServiceException("文件不存在。");
        }
        //拼接cmd命令
        Process exec = Runtime.getRuntime().exec("cmd /c mysql -h"+"localhost"+" -u "+"root"+" -p"+"root"+" "+databaseName+" < "+datafile);
        if( exec.waitFor() == 0){
            System.out.println("数据库还原成功，还原的文件为："+datafile);
        }
    }
    @Test
    public void handBackup(){
        try{

            //sql文件存储名
            String fileName = "DataBase"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".sql";
            dataBakExec(fileName);
            System.out.println("备份的sql文件保存路径为:"+this.sqlFilePath);
//            map.put("sqlFilePath",this.sqlFilePath);
//
//            map.put("logMessage","完成数据库备份");
//            map.put("code",200);
//            map.put("message","备份成功,路径为："+sqlFilePath);
        }catch (Exception e){
//            map.put("code",500);
//            map.put("message","备份失败");
        }
    }
    /**
     * 数据备份
     * @return
     */
    private void dataBakExec(String fileName)
    {

        File saveFile = new File(savePath);
        // 如果目录不存在
        if (!saveFile.exists()) {
            // 创建文件夹
            saveFile.mkdirs();
        }
        if(!savePath.endsWith(File.separator)){
            savePath = savePath + File.separator;
        }

        PrintWriter printWriter = null;
        BufferedReader bufferedReader = null;
        try {
            printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(savePath + fileName), "utf8"));
            this.sqlFilePath= savePath + fileName;
            //导出指定数据库的结构和数据              E:/JAVA/mysql/mysql-8.0.30-winx64/bin/mysqldump -h
            Process process = Runtime.getRuntime().exec("cmd /c mysqldump -h" + hostIP + " -u" + userName + " -p" + password + " --set-charset=UTF8 " + databaseName);

            //导出指定数据库指定表的结构和数据
            //Process process = Runtime.getRuntime().exec("E:/software/mysql8-winx64/mysql-8.0.21-winx64/bin/mysqldump -h" + hostIP + " -u" + userName + " -p" + password + " --set-charset=UTF8 " + databaseName +" book ");
            //导出指定数据库指定表的结构
            //Process process = Runtime.getRuntime().exec("C:/Program Files/MySQL/MySQL Server 5.6/bin/mysqldump -h" + hostIP + " -u" + userName + " -p" + password + " --set-charset=UTF8 " + databaseName + " book -d");
            //导出指定数据库指定表符合条件的结构和数据
            //Process process = Runtime.getRuntime().exec("C:/Program Files/MySQL/MySQL Server 5.6/bin/mysqldump -h" + hostIP + " -u" + userName + " -p" + password + " --set-charset=UTF8 " + databaseName +" book "+" --where=\" price> 100" + "\" ");
            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream(), "utf8");
            bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while((line = bufferedReader.readLine())!= null){
                printWriter.println(line);
            }
            printWriter.flush();
            //0 表示线程正常终止。
            if(process.waitFor() == 0){
                System.out.println("备份数据成功");

            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (printWriter != null) {
                    printWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
