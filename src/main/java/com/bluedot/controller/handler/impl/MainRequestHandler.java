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
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
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
    private  final ServiceControllerQueue serviceControllerQueue;
    private final ControllerMonitor monitor;
    /**
     * 标识request 唯一id 并线程安全，自增
     */
    private AtomicLong requestId = new AtomicLong(1);

    public MainRequestHandler() {
        this.log = LogUtil.getLogger();
        this.controllerServiceQueue = ControllerServiceQueue.getInstance();
        this.serviceControllerQueue = ServiceControllerQueue.getInstance();
        this.monitor = ControllerMonitor.getInstance();
    }

    @Override
    public CommonResult handlerRequest(HttpServletRequest request, HttpServletResponse response) {
        //根据request对象将请求参数封装为Data对象
        Data data = getDataFromRequest(request);
        if(data == null){
            log.debug("请求数据格式错误---请求用户:{}",request.getSession().getAttribute("userEmail"));
            return  CommonResult.errorResult(500,"数据格式错误");
        }
        log.debug("开始处理请求---请求id:{}",data.getKey());
        //将Data对象放入ControllerServiceQueue中等待处理
        controllerServiceQueue.put(data);
        //将当前线程放入ControllerMonitor中等待唤醒
        monitor.addThread(data.getKey(), Thread.currentThread());
        //线程暂停
        LockSupport.park();
        //线程被唤醒后从ServiceControllerQueue队列中根据key取出处理后的数据并返回
        log.debug("处理请求结束---请求id:{}",data.getKey());
        return serviceControllerQueue.take(data.getKey());
    }

    /**
     * @param request 请求request对象
     * @return 分装了请求参数的Data对象
     */
    private Data getDataFromRequest(HttpServletRequest request)  {
        log.debug("开始封装请求数据--请求用户:{}",request.getSession().getAttribute("userEmail")==null?"游客":request.getSession().getAttribute("userEmail"));
        Data data = new Data();
        //将当前请求的session对象放入Data对象中
        data.setSession(request.getSession());
        //设置请求数据对应的key
        data.setKey(requestId.getAndIncrement());
        BufferedReader streamReader = null;
        StringBuilder json;
        try {
            //获取请求中的json数据
            streamReader = new BufferedReader( new InputStreamReader(request.getInputStream(), "UTF-8"));
             json = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                json.append(inputStr);
            }
            ObjectMapper objectMapper = JsonUtil.getObjectMapper();
            //将请求中的json字符串转化为JsonNode类型的对象
            JsonNode jsonNode = objectMapper.readValue(json.toString(), JsonNode.class);
            //获取json中的viewName参数拼接为String类型ServiceName的放入Data对象中
            JsonNode viewName = jsonNode.get("viewName");
            data.setServiceName("com.bluedot.service."+viewName.asText()+"Service");
            //获取json中的operation参数转化为String放入Data对象中
            JsonNode operation = jsonNode.get("operation");
            data.setOperation(operation.asText());
            //获取json中的param参数转化为HashMap放入Data对象中
            JsonNode param = jsonNode.get("param");
            HashMap<String,Object> map = objectMapper.treeToValue(param, HashMap.class);
            data.setMap(map);
            return data;
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
