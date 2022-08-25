package com.bluedot.controller.render;

import com.bluedot.pojo.vo.CommonResult;
import com.bluedot.utils.DownloadUtils;
import com.bluedot.utils.LogUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
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
            response.getOutputStream().write((byte[]) map.get("file"));
            response.getOutputStream().close();
        } catch (IOException e) {
            LogUtil.getLogger().error(e.getMessage());
            e.printStackTrace();
        }
    }
}
