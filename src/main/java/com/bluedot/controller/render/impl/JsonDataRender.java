package com.bluedot.controller.render.impl;

import com.bluedot.controller.render.DataRender;
import com.bluedot.pojo.vo.CommonResult;
import com.bluedot.utils.JsonUtil;
import com.bluedot.utils.LogUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author SDJin
 * @CreationDate 2022/08/16 - 11:54
 * @Description ：
 */
public class JsonDataRender implements DataRender {
    @Override
    public void renderData(HttpServletResponse response, CommonResult commonResult) {
        try {
            //将结果数据转化为Json格式
            String json = JsonUtil.getObjectMapper().writeValueAsString(commonResult);
            //设置响应头
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
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
