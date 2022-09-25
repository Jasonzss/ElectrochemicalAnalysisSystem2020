package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.ErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.*;
import com.bluedot.monitor.impl.ServiceMapperMonitor;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.vo.CommonResult;
import com.bluedot.queue.enterQueue.Impl.ServiceMapperQueue;
import com.bluedot.queue.outQueue.impl.MapperServiceQueue;
import com.bluedot.queue.outQueue.impl.ServiceControllerQueue;
import com.bluedot.utils.ReflectUtil;
import com.bluedot.utils.StringUtil;
import com.bluedot.utils.constants.OperationConstants;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.locks.LockSupport;

/**
 * @Author Jason
 * @CreationDate 2022/08/15 - 11:52
 * @Description ：
 */
public abstract class BaseService<T> {
    protected HttpSession session;
    protected Map<String, Object> paramList;
    protected String operation;
    protected String ip;
    protected EntityInfo<T> entityInfo;
    protected CommonResult commonResult;
    protected Map<String,Object> userLogMap=new HashMap<>();

    /**
     * Service监听器调用具体Service时用的构造方法
     * @param data Controller层传进来的数据
     */
    public BaseService(Data data) {
        fillAttribute(data);
        if (check()){
            //检查通过
            userLogMap.put("ip",ip);
            userLogMap.put("session",session);
            userLogMap.put("entityInfo",entityInfo);
            userLogMap.put("userLogParameter",paramList.toString());
            try{
                //尝试抓取UserException
                doService();
            }catch (UserException e){
                commonResult = CommonResult.commonErrorCode(e.getErrorCode());
            }
        }else {
            //检查未通过
            commonResult = CommonResult.commonErrorCode(CommonErrorCode.E_5002);
        }
        //将结果返回给Controller
        ServiceControllerQueue.getInstance().put(data.getKey(), commonResult);

    }

    /**
     * 各Service之间互相调用时的构造方法
     * @param session 调用者的Session
     * @param entityInfo 调用者的entityInfo
     */
    protected BaseService(HttpSession session, EntityInfo<?> entityInfo){
        this.session = session;
        this.entityInfo = new EntityInfo<>();
        this.entityInfo.setKey(entityInfo.getKey());
    }

    /**
     *
     * @param map 请求其他Service的参数
     * @param operation 对参数进行的操作
     * @return Mapper层操作数据的结果
     */
    public CommonResult doOtherService(Map<String, Object> map, String operation){
        this.paramList = map;
        this.operation = operation;
        if (check()){
            //检查通过
            doService();
        }else {
            //检查未通过
            commonResult = CommonResult.commonErrorCode(CommonErrorCode.E_5002);
        }
        return commonResult;
    }

    /**
     * 将data中的数据封装到baseService的属性中
     * @param data Controller层传进来的数据
     */
    private void fillAttribute(Data data){
        userLogMap.put("userLogClassMethodName",data.getServiceName()+".");
        paramList = data.getMap();
        session = data.getSession();
        operation = data.getOperation();
        ip=data.getIp();
        entityInfo = new EntityInfo<>();
        commonResult = new CommonResult();
        entityInfo.setKey(data.getKey());
    }

    /**
     * 负责在具体Service中分析调用哪些方法来解决请求
     */
    abstract protected void doService();

    /**
     * 负责对传进来的paramList数据进行检查
     */
    protected boolean check(){
        return true;
    }

    protected void update(){
        entityInfo.setOperation(OperationConstants.UPDATE);
        commonResult = doMapper();
    }

    protected void delete(){
        entityInfo.setOperation(OperationConstants.DELETE);
        commonResult = doMapper();
    }

    protected void insert(){
        entityInfo.setOperation(OperationConstants.INSERT);
        commonResult = doMapper();
    }

    protected void select(){
        entityInfo.setOperation(OperationConstants.SELECT);
        CommonResult commonResult = doMapper();
        List<?> data = (List<?>) commonResult.getData();
        //对每个数据进行实体属性的填充
        for (Object obj:data) {
            queryEntityField(obj);
        }
        //封装结果
        this.commonResult = CommonResult.successResult("",data);
    }

    /**
     * 分页查询
     */
    protected void selectPage(){
        // 查询当前页的对应数据
        entityInfo.setOperation(OperationConstants.SELECT);
        Integer size = entityInfo.getCondition().getSize();
        Long startIndex = entityInfo.getCondition().getStartIndex();
        CommonResult commonResult = doMapper();
        List<?> data = (List<?>) commonResult.getData();
        //对每个数据进行实体属性的填充
        data.forEach(this::queryEntityField);
        //封装结果
        commonResult = CommonResult.successResult("",data);

        Condition condition = entityInfo.getCondition();
        // 设置pageInfo，并将查询到的数据填入
        PageInfo pageInfo = new PageInfo();
        if (((ArrayList) commonResult.getData()).size() == 0){
            this.commonResult = CommonResult.commonErrorCode(CommonErrorCode.E_1009);
        }else {
            pageInfo.setDataList((List<Object>)commonResult.getData());
            pageInfo.setPageSize(size);
            // 调用getCount查询数据总数量
            pageInfo.setTotalDataSize(getCount(condition));
            pageInfo.setTotalPageSize((pageInfo.getTotalDataSize() + pageInfo.getPageSize() - 1) / pageInfo.getPageSize());
            pageInfo.setCurrentPageNo(Math.toIntExact(startIndex / size) + 1);

            this.commonResult = CommonResult.successResult("分页查询", pageInfo);
        }
    }

    /**
     * 查询目标分页查询的总数
     * @return 数据总数量
     */
    private long getCount(Condition condition){
        // 设置查询条件
        List<String> fieldList = new ArrayList<>();
        fieldList.add("count(*)");
        condition.setFields(fieldList);
        condition.addView(entityInfo.getCondition().getViews().get(0));
        condition.setReturnType("Long");

        //进行查询逻辑
        entityInfo.setCondition(condition);
        entityInfo.setOperation(OperationConstants.SELECT);
        CommonResult commonResult = doMapper();

        //返回结果
        ArrayList list= (ArrayList) commonResult.getData();
        return (long)list.get(0);
    }

    /**
     * 查询实体类的实体属性
     * @param obj 实体类
     */
    private void queryEntityField(Object obj){
        if (!obj.getClass().getTypeName().contains("pojo.entity")){
            //如果不是实体类直接返回
            return;
        }

        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            //判断外键实体的主键
            String fullClassName = field.getType().getName();
            String simpleName = field.getType().getSimpleName();
            if (fullClassName.contains("pojo.entity")){
                //对此实体进行查询
                Field[] foreignKeyEntityFields = field.getType().getDeclaredFields();
                Field key = null;
                int i = 0;
                while (i < foreignKeyEntityFields.length){
                    if (foreignKeyEntityFields[i] != null){
                        key = foreignKeyEntityFields[i];
                        break;
                    }
                    i++;
                }
                //外键实体没有任何属性，不用查了
                if (key == null){
                    return;
                }

                //生成查询condition
                Condition condition = new Condition();
                condition.setReturnType(simpleName);
                Object fieldAttribute = ReflectUtil.invokeGet(obj, field.getName());
                condition.setFieldsWithoutClasses(field.getType(), byte[].class);
                condition.addAndConditionWithView(new Term(StringUtil.humpToLine(simpleName), StringUtil.humpToLine(key.getName()), ReflectUtil.invokeGet(fieldAttribute, key.getName()), TermType.EQUAL));

                //执行查询
                entityInfo.setOperation(OperationConstants.SELECT);
                entityInfo.setCondition(condition);
                CommonResult commonResult = doMapper();
                List<?> data = (List<?>) commonResult.getData();
                if (data.size() == 0){
                    //数据库没有此数据时不包装
                    return;
                }
                Object o = data.get(0);

                //将查询到的封装到obj中
                ReflectUtil.invokeSetByTypeAndName(obj,simpleName,o);
            }
        }
    }

    /**
     * 反射调用目标Service成员方法
     * @param methodName 方法名
     * @param obj 目标Service
     */
    protected void invokeMethod(String methodName,Object obj){
        //判断是否存在Session
        if (!OperationConstants.LOGIN.equals(operation) && session.getAttribute("permissionList") == null){
            commonResult = CommonResult.commonErrorCode(CommonErrorCode.E_1011);
            return;
        }

        //从Session中获取权限列表
        List<String> permissionList = (List<String>) session.getAttribute("permissionList");

        if (OperationConstants.LOGIN.equals(operation) || permissionList.contains(methodName)){
            //存在此权限，执行响应方法
            Method method = null;
            try {
                method = obj.getClass().getDeclaredMethod(methodName);
                method.setAccessible(true);
            } catch (NoSuchMethodException e) {
                commonResult = CommonResult.commonErrorCode(CommonErrorCode.E_6001);
                e.printStackTrace();
            }
            try {
                //反射调用目标方法
                method.invoke(obj);
            } catch (IllegalAccessException e) {
                commonResult = CommonResult.commonErrorCode(CommonErrorCode.E_6001);
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                //处理抛出的UserException
                if (e.getTargetException() instanceof UserException){
                    //如果抛出的异常是UserException，则在此处理
                    UserException userException = (UserException) e.getTargetException();
                    ErrorCode errorCode = userException.getErrorCode();
                    commonResult = CommonResult.commonErrorCode(errorCode);
                }else {
                    //其他异常处理
                    commonResult = CommonResult.commonErrorCode(CommonErrorCode.E_6001);
                    e.printStackTrace();
                }
            }
        }else {
            // 没有权限，则设置枚举异常结果
            commonResult = CommonResult.commonErrorCode(CommonErrorCode.E_3001);
        }
    }

    /**
     * 将数据放入前往Mapper层的队列中，并等待获取返回的数据操作结果
     * @return mapper层的数据操作结果
     */
    private CommonResult doMapper(){
        //将待BaseMapper处理的数据加入到SMQueue中
        ServiceMapperQueue.getInstance().put(entityInfo);
        //将当前线程加入到ServiceMapperMonitor中等待唤醒
        ServiceMapperMonitor.getInstance().addThread(entityInfo.getKey(),Thread.currentThread());
        //当前线程暂停执行
        LockSupport.park();
        //返回Mapper处理结果
        CommonResult commonResult=MapperServiceQueue.getInstance().take(entityInfo.getKey());
        userLogMap.put("userLogMethodReturnValue",commonResult.getData().toString());
        return commonResult;
    }
}
