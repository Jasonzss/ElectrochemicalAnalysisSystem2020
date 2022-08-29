package com.bluedot.controller.render;

import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.vo.CommonResult;
import com.bluedot.utils.DownloadUtils;
import com.bluedot.utils.LogUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @Author SDJin
 * @CreationDate 2022/8/23 16:44
 * @Description ：
 */
public class FileDataRender {
    public static void renderData(HttpServletResponse response, HttpServletRequest request, CommonResult commonResult) {
        try {
            //根据文件格式设置对应的ContentType
            response.setContentType(commonResult.getRespContentType());
            HashMap<String, Object> map = (HashMap<String, Object>) commonResult.getData();
            String name = DownloadUtils.getFileName(request.getHeader("user-agent"), (String) map.get("fileName"));
            //设置下载文件的文件名
            response.setHeader("content-disposition", "attachment;filename=" + name);
            //将文件以字节流输出
            switch (commonResult.getRespContentType()) {
                case CommonResult.EXCEL:
                    ((HSSFWorkbook) map.get("file")).write(response.getOutputStream());
                    break;
                case CommonResult.BUFFERED_IMAGE:
                    ImageIO.write(((BufferedImage) map.get("file")), "png", response.getOutputStream());
                    break;
                case CommonResult.INPUT_STREAM_IMAGE:
                    InputStream inputStream = (InputStream) map.get("file");
                    byte[] buffer = new byte[1024];
                    int len = -1;
                    while ((len = inputStream.read(buffer)) != -1){
                        response.getOutputStream().write(buffer,0,len);
                    }
//                    response.getOutputStream().write(((InputStream) map.get("file")).read());
                    break;
                case CommonResult.FILE:
                    response.getOutputStream().write(new FileInputStream((File) map.get("file")).read());
                    break;
                default:
                    LogUtil.getLogger().error("请求结果CommonResult中ContentType与数据类型不匹配----请求id:{}",((Data)request.getAttribute("Data")).getKey());
            }
            response.getOutputStream().close();
        } catch (IOException e) {
            LogUtil.getLogger().error(e.getMessage());
            e.printStackTrace();
        }
    }
}
