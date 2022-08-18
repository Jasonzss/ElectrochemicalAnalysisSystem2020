package com.bluedot.controller;

import com.bluedot.controller.handler.RequestHandler;
import com.bluedot.controller.handler.impl.MainRequestHandler;
import com.bluedot.controller.render.DataRender;
import com.bluedot.controller.render.impl.JsonDataRender;
import com.bluedot.pojo.vo.CommonResult;
import com.bluedot.utils.LogUtil;
import org.slf4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author SDJin
 * @CreationDate 2022/08/16 - 11:54
 * @Description ：
 */
@WebServlet(name = "DispatcherServlet",urlPatterns = "/*",loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {
    /**
     * 请求处理器
     */
    private RequestHandler requestHandler;
    /**
     * 结果数据渲染器
     */
    private DataRender dataRender;
    /**
     * 日志对象
     */
    private Logger log;

    @Override
    public void init(ServletConfig config) {
        //初始化日志对象
        log = LogUtil.getLogger();
        log.debug("初始化日志对象");
        //初始化请求处理器
        requestHandler = new MainRequestHandler();
        log.debug("初始化请求处理器：" + requestHandler);
        //初始化结果渲染器
        dataRender = new JsonDataRender();
        log.debug("初始化结果渲染器：" + dataRender);
        //初始化数据库连接池
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)  {
        //处理请求
        CommonResult commonResult = requestHandler.handlerRequest(req, resp);
        //渲染结果并返回给前端
        LogUtil.getLogger().debug("开始渲染结果数据--请求用户:{}",req.getSession().getAttribute("userEmail")==null?"游客":req.getSession().getAttribute("userEmail"));
        dataRender.renderData(resp, commonResult);
    }

    @Override
    public void destroy() {
        //关闭数据库连接池

        log.debug("关闭数据库连接池");

        //注销驱动

        log.debug("注销数据库驱动");

    }
}
