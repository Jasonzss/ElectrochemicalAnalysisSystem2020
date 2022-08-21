package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.Term;
import com.bluedot.mapper.bean.TermType;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.*;
import com.bluedot.utils.ReflectUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.Buffer;
import java.util.*;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/8/21 21:06
 * @created: 申请业务
 */
public class ApplicationService extends BaseService<Application>{
    public ApplicationService(Data data) {
        super(data);
    }

    @Override
    protected void doService() {

        String methodName = null;

        switch (operation){
            case "insert":
                methodName = "addApplication";
                break;
            case "delete":
                methodName = "deleteApplication";
                break;
            case "update":
                if (isPersonal()){
                    methodName = "updatePersonalApplication";
                }else {
                    methodName = "updateApplication";
                }
                break;
            case "select":
                methodName = "listApplicationPage";
                break;
            default:
                throw new UserException(CommonErrorCode.E_5001);
        }
    }

    /**
     * 判断数据是否是当前用户的数据
     */
    private boolean isPersonal(){
        String userEmail = (String) session.getAttribute("userEmail");
        // 处理的数据拥有者的userEmail
        String dataUserEmail = (String) paramList.get("userEmail");
        return userEmail.equals(dataUserEmail);
    }

    /**
     * 将map类型的数据，转换为json形式
     * @param applicationContent map类型数据
     * @return json格式字符串
     */
    private String convertMapToJson(Map<String,Object> applicationContent){
        String content = null;
        try {
            content = new ObjectMapper().writeValueAsString(applicationContent);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new UserException(CommonErrorCode.E_6001);
        }
        return content;
    }

    /**
     * 用户发送申请
     */
    @SuppressWarnings("unchecked")
    private void addApplication(){
        //申请内容是map数据，则转json
        if (paramList.get("applicationContent")!=null && paramList.get("applicationContent") instanceof Map){
            // 获取到请求参数中的申请内容，转换为json格式,并替换
            Map<String,Object> applicationContent = (Map<String, Object>) paramList.get("applicationContent");
            paramList.replace("applicationContent",convertMapToJson(applicationContent));
        }

        // 添加申请数据
        Application application = new Application();
        ReflectUtil.invokeSetters(paramList,application);

        entityInfo.addEntity(application);
        insert();
    }

    private void deleteApplication(){
        //参数解析
        Map<String,Object> intData = new HashMap<>();
        List<Map<String,Object>> listData = new ArrayList<>();
        if (paramList.get("applicationId") instanceof List){
            listData = (List<Map<String,Object>>)paramList.get("applicationId");
        }else {
            intData = (Map<String,Object>) paramList.get("applicationId");
        }

        //根据变量中是否有值来判断参数的类型，然后执行删除一个还是删除多个的操作
        if (!intData.isEmpty()){
            // 如果删除的参数是int类型的,则删除一个
            Application application = new Application();
            ReflectUtil.invokeSetters(intData,application);
            entityInfo.addEntity(application);
        }
        if (!listData.isEmpty()){
            // 如果删除的参数是list类型的，则删除多个
            listData.forEach(data -> {
                Application application = new Application();
                ReflectUtil.invokeSetters(data,application);
                entityInfo.addEntity(application);
            });
        }
    }

    /**
     * 用户修改申请内容信息
     */
    @SuppressWarnings("unchecked")
    private void updatePersonalApplication(){
        //申请内容是map数据，则转json
        if (paramList.get("applicationContent") != null || paramList.get("applicationContent") instanceof Map){
            // 获取到请求参数中的申请内容，转换为json格式,并替换
            Map<String,Object> applicationContent = (Map<String, Object>) paramList.get("applicationContent");
            String content = null;
            try {
                content = new ObjectMapper().writeValueAsString(applicationContent);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                throw new UserException(CommonErrorCode.E_6001);
            }
            paramList.replace("applicationContent",content);
        }


        Application application = new Application();
        ReflectUtil.invokeSetters(paramList,application);

        entityInfo.addEntity(application);
        update();
    }

    /**
     * 管理员审核申请
     */
    private void updateApplication(){
        Application application = new Application();
        ReflectUtil.invokeSetters(paramList,application);

        Integer applicationId = application.getApplicationId();

        // 拒绝理由为空，即同意申请，则执行申请内容中的操作
        if (application.getApplicationRejectReason() != null){
            Application selectedApplication = new Application();
            // 通过申请id查询到申请
            Condition condition = new Condition();
            condition.addAndConditionWithView(new Term("application","application_id",applicationId, TermType.EQUAL));

            entityInfo.addEntity(selectedApplication);
            select();

            // 获取查询到的申请内容
            selectedApplication = (Application) commonResult.getData();
            String applicationContent = selectedApplication.getApplicationContent();

            // 判断申请的类型,然后解析申请内容并执行
            Integer applicationType = selectedApplication.getApplicationType();
            ObjectMapper objectMapper = new ObjectMapper();

            switch (applicationType){
                case 0:
                    // 解封申请：根据userEmail来修改账号状态
                    User user = new User();
                    user.setUserEmail((String) paramList.get("userEmail"));
                    //修改账号状态 ?
                    update();
                    break;
                case 1:
                    MaterialType materialType = objectMapper.convertValue(applicationContent,MaterialType.class);
                    //添加物质类型 ?
                    insert();
                    break;
                case 2:
                    Algorithm algorithm = objectMapper.convertValue(applicationContent,Algorithm.class);
                    //测试 ?
                    //改变算法状态 ?
                    algorithm.setAlgorithmStatus(1);
                    update();
                    break;
                case 3:
                    BufferSolution bufferSolution = objectMapper.convertValue(applicationContent,BufferSolution.class);
                    //添加缓冲溶液 ?
                    insert();
                    break;
                default:
                    throw new UserException(CommonErrorCode.E_6001);
            }

        }

        // 完成审核操作，修改该申请的审核状态
        entityInfo.addEntity(application);
        update();
    }

    /**
     * 查询申请数据
     */
    private void listApplicationPage(){
        Condition condition = new Condition();

        if (paramList.containsKey("pageSize")){
            condition.setSize((Integer) paramList.get("pageSize"));
        }
        if (paramList.containsKey("pageNo")){
            condition.setStartIndex(((long)paramList.get("pageNo")-1)*(int)paramList.get("pageSize"));
        }
        if (paramList.containsKey("applicationType") || paramList.get("applicationTyoe") != null){
            condition.addAndConditionWithView(new Term("application","application_type",paramList.get("applicationType"),TermType.EQUAL));
        }
        if (paramList.containsKey("userEmail")){
            condition.addOrConditionWithView(new Term("application","user_email",paramList.get("userEmail"),TermType.EQUAL));
        }
        if (paramList.get("beginTime") != null){
            condition.addOrConditionWithView(new Term("application","application_time",paramList.get("beginTime"),TermType.GREATER));
        }
        if (paramList.get("endTime") != null){
            condition.addOrConditionWithView(new Term("application","application_time",paramList.get("endTime"),TermType.Less));
        }
        if (paramList.containsKey("applicationStatus") || paramList.get("applicationStatus") != null){
            condition.addOrConditionWithView(new Term("application","application_status",paramList.get("applicationStatus"),TermType.EQUAL));
        }

        entityInfo.setCondition(condition);
    }

}
