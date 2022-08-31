package com.bluedot.controller.filter;

import com.bluedot.controller.render.JsonDataRender;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.vo.CommonResult;
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

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
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

/**
 * @Author SDJin
 * @CreationDate 2022/8/20 16:05
 * @Description ：
 */
@WebFilter(urlPatterns = "/*", filterName = "数据过滤器")
public class DataFilter implements Filter {
    /**
     * 过滤器名
     */
    private String filterName;
    private final Logger log = LogUtil.getLogger();
    /**
     * 标识request 唯一id 并线程安全，自增
     */
    private final AtomicLong requestId = new AtomicLong(1);

    @Override
    public void init(FilterConfig filterConfig) {
        filterName = filterConfig.getFilterName();
        log.debug(filterName + "初始化");
    }

    /**
     * 封装请求
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        log.debug("开始封装请求数据--请求用户:{}", request.getSession().getAttribute("userEmail") == null ? "游客" : request.getSession().getAttribute("userEmail"));
        Data data = new Data();
        //将当前请求的session对象放入Data对象中
        data.setSession(request.getSession());
        //设置请求数据对应的key
        data.setKey(requestId.getAndIncrement());
        String contentType = request.getContentType();
        if (contentType != null && contentType.indexOf("application/json") != -1 || contentType.indexOf("multipart/form-data") != -1) {
            if (contentType.indexOf("application/json") != -1) {
                try {
                    //获取请求中的json数据
                    BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder json = new StringBuilder();
                    String inputStr;
                    while ((inputStr = streamReader.readLine()) != null) {
                        json.append(inputStr);
                    }
                    ObjectMapper objectMapper = JsonUtil.getObjectMapper();
                    //将请求中的json字符串转化为JsonNode类型的对象
                    JsonNode jsonNode = objectMapper.readValue(json.toString(), JsonNode.class);
                    //获取json中的viewName参数拼接为String类型ServiceName的放入Data对象中
                    JsonNode viewName = jsonNode.get("viewName");
                    data.setServiceName("com.bluedot.service." + viewName.asText() + "Service");
                    //获取json中的operation参数转化为String放入Data对象中
                    JsonNode operation = jsonNode.get("operation");
                    data.setOperation(operation.asText());
                    //获取json中的param参数转化为HashMap放入Data对象中
                    JsonNode param = jsonNode.get("param");
                    HashMap<String, Object> map = objectMapper.treeToValue(param, HashMap.class);
                    data.setMap(map);
                    request.setAttribute("Data", data);
                } catch (IOException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                    request.setAttribute("Data", null);
                }
            } else {
                HashMap<String, Object> map = new HashMap<>(10);
                //创建FileItem工厂
                FileItemFactory diskFileItemFactory = new DiskFileItemFactory();
                //创建ServletFileUpload对象
                ServletFileUpload upload = new ServletFileUpload(diskFileItemFactory);
                try {
                    //将request表单中所有输入项封装成一个FileItem数组
                    List<FileItem> fileItems = upload.parseRequest(request);
                    //遍历FileItem数组封装为Data对象
                    for (FileItem fileItem : fileItems) {
                        if (fileItem.isFormField()) {
                            if ("viewName".equals(fileItem.getFieldName())) {
                                data.setServiceName("com.bluedot.service." + fileItem.getString("utf-8") + "Service");
                            } else if ("operation".equals(fileItem.getFieldName())) {
                                data.setOperation(fileItem.getString("utf-8"));
                            } else {
                                map.put(fileItem.getFieldName(), fileItem.getString("utf-8"));
                            }
                        } else {
                            map.put("file", fileItem);
                        }
                    }
                    data.setMap(map);
                    request.setAttribute("Data", data);
                } catch (FileUploadException | UnsupportedEncodingException e) {
                    log.error(e.getMessage());
                    request.setAttribute("Data", null);
                }
            }
            try {
                if (request.getAttribute("Data") != null) {
                    filterChain.doFilter(request, response);
                } else {
                    log.error("请求数据格式错误---请求用户:{}", request.getSession().getAttribute("userEmail") == null ? "游客" : request.getSession().getAttribute("userEmail"));
                    JsonDataRender.renderData(response, CommonResult.errorResult(400, "数据格式错误"));
                }
            } catch (IOException | ServletException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        } else {
            log.error("请求ContentType错误---请求用户:{}", request.getSession().getAttribute("userEmail") == null ? "游客" : request.getSession().getAttribute("userEmail"));
            JsonDataRender.renderData(response, CommonResult.errorResult(400, "请求ContentType错误"));
        }
    }

    @Override
    public void destroy() {
        log.debug(filterName + "销毁");
    }
}
