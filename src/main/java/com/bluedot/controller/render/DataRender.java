package com.bluedot.controller.render;

import com.bluedot.pojo.vo.CommonResult;

import javax.servlet.http.HttpServletResponse;
/**
 * @Author SDJin
 * @CreationDate 2022/08/16 - 11:54
 * @Description ：数据渲染器
 */
public interface DataRender {
    /**
     * 渲染请求处理结果
     * @param response 响应对象
     * @param commonResult 请求处理结果
     */
    void renderData(HttpServletResponse response, CommonResult commonResult);
}
