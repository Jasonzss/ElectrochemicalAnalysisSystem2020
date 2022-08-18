package com.bluedot.controller.handler;

import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.vo.CommonResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * @Author SDJin
 * @CreationDate 2022/08/16 - 11:54
 * @Description ：
 */
public interface RequestHandler {
    /**
     * 处理请求
     * @param request 请求request对象
     * @param response 响应response对象
     * @return 请求处理结果CommonResult对象
     */
    CommonResult handlerRequest(HttpServletRequest request, HttpServletResponse response);
}
