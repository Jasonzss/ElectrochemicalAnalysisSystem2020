package com.bluedot.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;

/**
 * @Author SDJin
 * @CreationDate 2022/08/16 - 11:54
 * @Description ：
 */
public class DownloadUtils {

    public static String getFileName(String agent, String filename) throws UnsupportedEncodingException {
        if (agent.contains("MSIE")) {
            // IE浏览器
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+", " ");
        } else if (agent.contains("Firefox")) {
            // 火狐浏览器
            Base64.Encoder encoder = Base64.getEncoder();
            filename = "=?utf-8?B?" +encoder.encodeToString(filename.getBytes("utf-8")) + "?=";
        } else {
            // 其它浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        }
        return filename;
    }
}