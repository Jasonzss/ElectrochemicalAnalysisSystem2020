package com.bluedot.controller;

import com.bluedot.controller.handler.RequestHandler;
import com.bluedot.controller.handler.impl.MainRequestHandler;
import com.bluedot.controller.render.FileDataRender;
import com.bluedot.controller.render.JsonDataRender;
import com.bluedot.mapper.MapperInit;
import com.bluedot.mapper.dataSource.impl.MyDataSourceImpl;
import com.bluedot.monitor.impl.ControllerMonitor;
import com.bluedot.monitor.impl.MapperMonitor;
import com.bluedot.monitor.impl.ServiceControllerMonitor;
import com.bluedot.monitor.impl.ServiceMapperMonitor;
import com.bluedot.pojo.vo.CommonResult;
import com.bluedot.utils.LogUtil;
import org.slf4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author SDJin
 * @CreationDate 2022/08/16 - 11:54
 * @Description ：
 */
@WebServlet(name = "DispatcherServlet", urlPatterns = "/*", loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {
    /**
     * 请求处理器
     */
    private RequestHandler requestHandler;
    /**
     * 日志对象
     */
    private Logger log;
    /**
     * 定时周期任务线程池
     */
    private static final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(4);

    @Override
    public void init(ServletConfig config) {
        //初始化日志对象
        log = LogUtil.getLogger();
        log.debug("初始化日志对象");
        //初始化数据库连接池
        try {
            log.debug("初始化数据库连接池");
            new MapperInit("database.properties");
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        //初始化请求处理器
        requestHandler = new MainRequestHandler();
        log.debug("初始化请求处理器：" + requestHandler);
        //初始化监听器
        executorService.scheduleAtFixedRate(ControllerMonitor.getInstance(), 3, 1, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(ServiceControllerMonitor.getInstance(), 0, 1, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(ServiceMapperMonitor.getInstance(), 2, 1, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(MapperMonitor.getInstance(), 1, 1, TimeUnit.SECONDS);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        //处理请求
        CommonResult commonResult = requestHandler.handlerRequest(req, resp);
        //渲染结果并返回给前端
        log.debug("开始渲染结果数据--请求用户:{}", req.getSession().getAttribute("userEmail") == null ? "游客" : req.getSession().getAttribute("userEmail"));
        //根据CommonResult中的RespHeadType使用对应的数据渲染器
        if (commonResult.getRespContentType().equals(CommonResult.JSON)){
            JsonDataRender.renderData(resp, commonResult);
        }else {
            FileDataRender.renderData(resp,req,commonResult);
        }
    }

    @Override
    public void destroy() {
        //关闭定时后期线程池
        executorService.shutdown();
        //关闭数据库连接池
        log.debug("关闭数据库连接池");
        MyDataSourceImpl.getInstance().close();
        //注销驱动
        log.debug("注销数据库驱动");
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        Driver driver = null;
        while (drivers.hasMoreElements()) {
            try {
                driver = drivers.nextElement();
                DriverManager.deregisterDriver(driver);
                log.debug("deregister success : driver {}", driver);
            } catch (SQLException e) {
                log.error("deregister failed : driver {}", driver);
            }
        }

    }
}
