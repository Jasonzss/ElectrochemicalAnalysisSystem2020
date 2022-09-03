package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.MapperInit;
import com.bluedot.mapper.bean.EntityInfo;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.Backup;
import com.bluedot.pojo.vo.CommonResult;
import com.bluedot.utils.constants.MapperConstants;
import com.google.protobuf.ServiceException;
import org.junit.Test;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @author zlj
 * @version 1.0
 * @description: TODO
 * @date 2022/8/24 11:11
 */
public class BackupService extends BaseService<Backup>{
    static {
        Properties pro=new Properties();
        InputStream inputStream = BackupService.class.getClassLoader().getResourceAsStream(MapperConstants.PROPERTIES);
        try {
            pro.load(inputStream);
            dataBaseIP=pro.getProperty(MapperConstants.DATABASE_IP,"localhost");
            dataBasePort=pro.getProperty(MapperConstants.DATABASE_PORT,"3306");
            databaseName=pro.getProperty(MapperConstants.DATABASE_NAME);
            userName=pro.getProperty(MapperConstants.JDBC_USERNAME,"root");
            password=pro.getProperty(MapperConstants.JDBC_PASSWORD,"root");
                    savePath=pro.getProperty(MapperConstants.BACKUP_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String dataBaseIP ;
    private static String dataBasePort ;
    private static String userName ;
    private static String password ;
    private static String databaseName;
    //sql文件存储目录
    private static String savePath;
    //sql文件备份路径
    private static String sqlFilePath;


    public BackupService(Data data) {
        super(data);
    }

    @Override
    protected void doService() {
        String methodName=null;
        switch (operation){
            case "insert":
                if (paramList.containsKey("timecycle")){
                    methodName="autoBackup";
                }else {
                    methodName="handBackup";
                }
                break;
            case "select":
                if (paramList.containsKey("startTime") || paramList.containsKey("endTime")){
                    methodName="getBackup";
                }else {
                    methodName="listBackup";
                }
                break;
            case "delete":
                if (paramList.containsKey("renewDate") && paramList.containsKey("bcakupDateId")){
                    methodName="recoverBackup";
                }
                else if (paramList.containsKey("renewDates") && paramList.containsKey("bcakupDateIds")){
                    methodName="recoverBackups";
                }
                else if (paramList.containsKey("bcakupDateIds")){
                    methodName="deleteBackups";
                }
                else if (paramList.containsKey("bcakupDateId")){
                    methodName="deleteBackup";
                }else {
                    throw new UserException(CommonErrorCode.E_5001);
                }
                break;
            default:
                throw new UserException(CommonErrorCode.E_5002);
        }
        invokeMethod(methodName,this);
    }

    @Override
    protected boolean check() {
        return false;
    }

    /**
     * 手动备份设置
     */
    @Test
    private void handBackup(){
            String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            System.out.println(time);
            //sql文件名
            String fileName = databaseName+"_"+time+".sql";
            dataBakExec(fileName);
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
            sqlFilePath= savePath + fileName;
            Process process = Runtime.getRuntime().exec("cmd /c mysqldump -h" + dataBaseIP + " -u" + userName + " -p" + password + " --set-charset=UTF8 " + databaseName);
            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream(), "utf8");
            bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while((line = bufferedReader.readLine())!= null){
                printWriter.println(line);
            }
            printWriter.flush();
            if(process.waitFor() == 0){
                Backup backup=new Backup();
                backup.setBackupTime(new Timestamp(System.currentTimeMillis()));
                backup.setBackupDataFileName(fileName);
                entityInfo.addEntity(backup);
                insert();
                commonResult=CommonResult.successResult("备份成功",backup);
            }else {
                commonResult=CommonResult.errorResult(500,"备份不成功");
            }
        }catch (Exception e) {
            commonResult=CommonResult.errorResult(500,"备份报错");
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
    /**
     * 自动备份
     */
    private void autoBackup(){

    }
    /**
     * 查询所有备份文件
     */
    private void listBackup(){

    }
    /**
     * 查询目标备份文件
     */
    private void getBackup(){

    }
    /**
     * 删除所有备份文件
     */
    private void deleteBackups(){

    }
    /**
     * 删除目标备份文件
     */
    private void deleteBackup(){

    }
    /**
     * 还原多个备份文件
     */
    private void recoverBackups(){

    }
    /**
     * 还原单个备份文件
     */
    private void recoverBackup(){
        String fileName = (String) paramList.get("fileName");
        if (fileName==null) {
            commonResult=CommonResult.errorResult(400,"请求参数：备份文件名为空");
        }else {
            savePath+=fileName;
            File datafile = new File(savePath);
            if( !datafile.exists() ){
                throw new UserException(CommonErrorCode.E_9001);
            }
            //拼接cmd命令
            Process exec = null;
            try {
                exec = Runtime.getRuntime().exec("cmd /c mysql -h"+dataBaseIP+" -u "+userName+" -p"+password+" "+databaseName+" < "+datafile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if( exec.waitFor() == 0){
                    Backup backup=new Backup();
                    backup.setBackupDataFileName(fileName);
                    entityInfo.addEntity(backup);
                    delete();
                    commonResult=CommonResult.successResult("还原数据成功",backup);
                }
            } catch (InterruptedException e) {
                commonResult=CommonResult.errorResult(500,"还原数据报错");
                e.printStackTrace();
            }
        }
    }
}
