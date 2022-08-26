package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.Term;
import com.bluedot.mapper.bean.TermType;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.*;
import com.bluedot.utils.JsonUtil;
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
                //
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
                methodName = "listApplication";
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

    /**
     * 管理员删除申请
     */
    @SuppressWarnings("unchecked")
    private void deleteApplication(){
        if (paramList.get("applicationId") instanceof List){
            // 如果删除的参数是list类型的，则删除多个
            List<Integer> listData = (List<Integer>)paramList.get("applicationId");
            listData.forEach(data -> {
                Application application = new Application();
                application.setApplicationId(data);
                entityInfo.addEntity(application);
            });
        }else if (paramList.get("applicationId") instanceof Integer){
            // 如果删除的参数是int类型的,则删除一个
            Integer intData = (Integer) paramList.get("applicationId");
            Application application = new Application();
            application.setApplicationId(intData);
            entityInfo.addEntity(application);
        }
        delete();
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
            paramList.replace("applicationContent",convertMapToJson(applicationContent));
        }

        Application application = new Application();
        ReflectUtil.invokeSetters(paramList,application);

        entityInfo.addEntity(application);
        update();
    }

    /**
     * 管理员审核申请
     */
    @SuppressWarnings("unchecked,rawtypes")
    private void updateApplication(){
        Application application = new Application();
        ReflectUtil.invokeSetters(paramList,application);

        // 拒绝理由为空，即同意申请，则执行申请内容中的操作
        if (application.getApplicationRejectReason() != null && application.getApplicationStatus() == 1){
            // 通过申请id查询到申请
            Condition condition = new Condition();
            condition.addAndConditionWithView(new Term("application","application_id",application.getApplicationId(), TermType.EQUAL));

            entityInfo.setCondition(condition);
            select();

            // 获取查询到的申请内容
            Application selectedApplication = (Application) commonResult.getData();
            String applicationContent = selectedApplication.getApplicationContent();

            // 判断申请的类型,然后解析申请内容并执行
            Integer applicationType = selectedApplication.getApplicationType();
            ObjectMapper objectMapper = JsonUtil.getObjectMapper();

            switch (applicationType){
                case 0:
                    // 解封申请：根据userEmail来修改账号状态
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("userEmail",paramList.get("userEmail"));
                    userDataMap.put("userStatus",1);
                    new UserService(session,userDataMap,"insert",commonResult);
                    break;
                case 1:
                    Map materialTypeMap= objectMapper.convertValue(applicationContent,Map.class);
                    new MaterialTypeService(session,materialTypeMap,"insert",commonResult);
                    break;
                case 2:
                    Map algorithmMap = objectMapper.convertValue(applicationContent,Map.class);
                    algorithmMap.put("algorithmStatus",1);
                    //改变算法状态 ?????
                    break;
                case 3:
                    Map bufferSolutionMap = objectMapper.convertValue(applicationContent,Map.class);
                    new BufferSolutionService(session,bufferSolutionMap,"insert",commonResult);
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
    private void listApplication(){
        Condition condition = new Condition();

        // 分页查询
        if (paramList.containsKey("pageSize")){
            condition.setSize((Integer) paramList.get("pageSize"));
        }
        if (paramList.containsKey("pageNo")){
            condition.setStartIndex(((long)paramList.get("pageNo")-1)*(int)paramList.get("pageSize"));
        }

        // 查询申请的类型
        if (paramList.containsKey("applicationType") || paramList.get("applicationTyoe") != null){
            condition.addAndConditionWithView(new Term("application","application_type",paramList.get("applicationType"),TermType.EQUAL));
        }

        // 用户查找/管理员查找
        if (paramList.containsKey("userEmail")){
            condition.addAndConditionWithView(new Term("application","user_email",paramList.get("userEmail"),TermType.EQUAL));
        }

        // 可能添加的筛选条件:开始时间/截止时间/申请状态
        if (paramList.get("beginTime") != null){
            condition.addAndConditionWithView(new Term("application","application_time",paramList.get("beginTime"),TermType.GREATER));
        }
        if (paramList.get("endTime") != null){
            condition.addAndConditionWithView(new Term("application","application_time",paramList.get("endTime"),TermType.Less));
        }
        if (paramList.containsKey("applicationStatus") || paramList.get("applicationStatus") != null){
            condition.addAndConditionWithView(new Term("application","application_status",paramList.get("applicationStatus"),TermType.EQUAL));
        }

        entityInfo.setCondition(condition);
    }

}
