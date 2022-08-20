package com.bluedot.controller.handler.impl;

import com.bluedot.controller.handler.RequestHandler;
import com.bluedot.monitor.impl.ControllerMonitor;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.queue.enterQueue.Impl.ControllerServiceQueue;
import com.bluedot.pojo.vo.CommonResult;
import com.bluedot.queue.outQueue.impl.ServiceControllerQueue;
import com.bluedot.utils.JsonUtil;
import com.bluedot.utils.LogUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

/**
 * @Author SDJin
 * @CreationDate 2022/08/17 - 11:54
 * @Description
 */
public class MainRequestHandler implements RequestHandler {

    private final Logger log;
    private final ControllerServiceQueue controllerServiceQueue;
    private final ServiceControllerQueue serviceControllerQueue;
    private final ControllerMonitor monitor;

    public MainRequestHandler() {
        this.log = LogUtil.getLogger();
        this.controllerServiceQueue = ControllerServiceQueue.getInstance();
        this.serviceControllerQueue = ServiceControllerQueue.getInstance();
        this.monitor = ControllerMonitor.getInstance();
    }

    @Override
    public CommonResult handlerRequest(HttpServletRequest request, HttpServletResponse response) {
        //根据request对象将请求参数封装为Data对象
        Data data = (Data) request.getAttribute("Data");
        System.out.println(data);
        log.debug("开始处理请求---请求id:{}", data.getKey());
        //将Data对象放入ControllerServiceQueue中等待处理
        controllerServiceQueue.put(data);
        //将当前线程放入ControllerMonitor中等待唤醒
        monitor.addThread(data.getKey(), Thread.currentThread());
        //线程暂停
        LockSupport.park();
        //线程被唤醒后从ServiceControllerQueue队列中根据key取出处理后的数据并返回
        log.debug("处理请求结束---请求id:{}", data.getKey());
        return serviceControllerQueue.take(data.getKey());
    }


}
