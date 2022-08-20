package com.bluedot.controller.render;

import com.bluedot.pojo.vo.CommonResult;
import com.bluedot.utils.JsonUtil;
import com.bluedot.utils.LogUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author SDJin
 * @CreationDate 2022/08/16 - 11:54
 * @Description ：
 */
public class JsonDataRender {
    public static void renderData(HttpServletResponse response, CommonResult commonResult) {
        try {
            //将结果数据转化为Json格式
            String json = JsonUtil.getObjectMapper().writeValueAsString(commonResult.mapValue());
            //设置响应头
            response.setContentType("application/json");
            //将json数据写入响应字符输出流
            response.getWriter().write(json);
            response.getWriter().flush();
            response.getWriter().close();
        } catch (IOException e) {
            LogUtil.getLogger().error(e.getMessage());
            e.printStackTrace();
        }
    }
}
