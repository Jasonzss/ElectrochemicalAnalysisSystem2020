package com.bluedot.controller.filter;


import com.bluedot.utils.LogUtil;
import org.slf4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author SDJin
 * @CreationDate 2022/8/19 17:51
 * @Description ：
 */
@WebFilter(filterName = "字符编码过滤器",initParams ={
        @WebInitParam(name = "encoding",value = "utf-8")
},urlPatterns = "/*")
public class CharacterEncodingFilter implements Filter {
    /**
     * 字符编码
     */
    private String characterEncoding;
    /**
     * 过滤器名
     */
    private String filterName;
    /**
     * 日志对象
     */
    private final Logger log = LogUtil.getLogger();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        filterName = filterConfig.getFilterName();
        characterEncoding = filterConfig.getInitParameter("encoding");
        log.debug(filterName+"过滤器初始化");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {;
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        HttpServletResponse response= (HttpServletResponse) servletResponse;
        //设置请求跨域
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, HEAD, PUT,PATCH, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Origin, X-Requested-With, Content-Type, Accept");
//        response.setHeader("Access-Control-Allow-Credentials", "true");
        log.info("请求设置允许跨域");
        //设置字符编码
        request.setCharacterEncoding(characterEncoding);
        response.setCharacterEncoding(characterEncoding);
        log.debug("过滤器设置请求编号");
        filterChain.doFilter(request,response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
        log.debug(filterName+"过滤器销毁");
    }
}
