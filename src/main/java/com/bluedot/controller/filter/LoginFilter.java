package com.bluedot.controller.filter;

import com.bluedot.controller.render.JsonDataRender;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.vo.CommonResult;
import com.bluedot.utils.JwtUtil;
import com.bluedot.utils.LogUtil;
import org.slf4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @Author SDJin
 * @CreationDate 2022/8/19 17:51
 * @Description ：
 */
@WebFilter(urlPatterns = "/*",filterName = "登录过滤器",initParams = {
        @WebInitParam(name = "userService",value = "login,insert")
})
public class LoginFilter implements Filter {
    /**
     * 过滤器名
     */
    private String filterName;
    private final Logger log = LogUtil.getLogger();
    /**
     * 白名单方法集合
     */
    private final HashMap<String, HashSet<String>> map = new HashMap<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.debug(filterName + "初始化");
        filterName = filterConfig.getFilterName();
        //初始化白名单方法集合
        Enumeration<String> initParameterNames = filterConfig.getInitParameterNames();
        while (initParameterNames.hasMoreElements()) {
            String name = initParameterNames.nextElement();
            String initParameter = filterConfig.getInitParameter(name);
            List<String> strings = Arrays.asList(initParameter.split(","));
            HashSet<String> set = new HashSet<>(strings);
            map.put(name, set);
        }
    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        Data data = (Data) request.getAttribute("Data");
        String name = data.getServiceName();
        name=name.substring(name.lastIndexOf('.')+1,name.length());
        if (map.containsKey(name) && map.get(name).contains(data.getOperation())) {
            try {
                filterChain.doFilter(request, response);
            } catch (IOException | ServletException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        } else {
            //请求方法不在白名单中，需进行登录验证token
            String token = request.getHeader("Authorization");
            if (token != null && JwtUtil.verify(token)) {
                try {
                    //验证成功则放行
                    filterChain.doFilter(request, response);
                } catch (IOException | ServletException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            } else {
                //token验证失败
                log.error("用户未登录");
                JsonDataRender.renderData(response, CommonResult.errorResult(401, "用户未登录，请登录后重试！"));
            }
        }

    }
    @Override
    public void destroy() {
        log.debug(filterName + "销毁");
    }
}
