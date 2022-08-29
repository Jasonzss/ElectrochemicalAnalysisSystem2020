package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.ErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.EntityInfo;
import com.bluedot.mapper.bean.PageInfo;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.User;
import com.bluedot.pojo.vo.CommonResult;
import com.bluedot.queue.enterQueue.Impl.ServiceMapperQueue;
import com.bluedot.queue.outQueue.impl.MapperServiceQueue;
import com.bluedot.queue.outQueue.impl.ServiceControllerQueue;
import com.bluedot.utils.constants.OperationConstants;

import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @Author Jason
 * @CreationDate 2022/08/15 - 11:52
 * @Description ：
 */
public abstract class BaseService<T> {
    protected HttpSession session;
    protected Map<String, Object> paramList;
    protected String operation;
    protected EntityInfo<T> entityInfo;
    protected CommonResult commonResult;

    /**
     * Service监听器调用具体Service时用的构造方法
     * @param data Controller层传进来的数据
     */
    public BaseService(Data data) {
        fillAttribute(data);
        if (check()){
            //检查通过
            doService();
        }else {
            //检查未通过
            commonResult = CommonResult.commonErrorCode(CommonErrorCode.E_5002);
        }

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
     * @param map
     * @param operation
     * @return
     */
    protected CommonResult doOtherService(Map<String,Object> map, String operation){
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
        paramList = data.getMap();
        session = data.getSession();
        operation = data.getOperation();
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
        commonResult = doMapper();
    }

    /**
     * 分页查询
     */
    protected void selectPage(){
        // 查询当前页的对应数据
        entityInfo.setOperation(OperationConstants.SELECT);
        doMapper();

        Condition condition = entityInfo.getCondition();
        // 设置pageInfo，并将查询到的数据填入
        PageInfo<T> pageInfo = new PageInfo<T>();
        pageInfo.setDataList((List<T>) commonResult.getData());
        pageInfo.setPageSize(condition.getSize());
        // 调用getCount查询数据总数量
        pageInfo.setTotalDataSize(getCount());
        pageInfo.setTotalPageSize((pageInfo.getTotalDataSize() + pageInfo.getTotalPageSize() - 1)/pageInfo.getTotalPageSize());
        pageInfo.setCurrentPageNo(Math.toIntExact(condition.getStartIndex() / condition.getSize() + 1));

        commonResult = CommonResult.successResult("分页查询",pageInfo);
    }

    private long getCount(){
        // 设置查询条件
        Condition condition = new Condition();
        condition.addFields("count(*)");
        condition.addView(entityInfo.getCondition().getViews().get(0));

        entityInfo.setCondition(condition);
        entityInfo.setOperation(OperationConstants.SELECT);
        CommonResult commonResult = doMapper();

        return (long) commonResult.getData();
    }

    protected void invokeMethod(String methodName,Object obj){
        List<String> permissionList = (List<String>) session.getAttribute("permissionList");
//        if ("login".equals(operation) || permissionList.contains(methodName)){
            //存在此权限，执行响应方法

            Method method = null;
            try {
                method = obj.getClass().getDeclaredMethod(methodName);
            } catch (NoSuchMethodException e) {
                commonResult = CommonResult.commonErrorCode(CommonErrorCode.E_6001);
                e.printStackTrace();
            }
            method.setAccessible(true);
            try {
                method.invoke(obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                commonResult = CommonResult.commonErrorCode(CommonErrorCode.E_6001);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                //处理抛出的UserException
                if (e.getTargetException() instanceof UserException){
                    //如果抛出的异常是UserException，则在此处理
                    commonResult = CommonResult.commonErrorCode(((UserException) e.getTargetException()).getErrorCode());
                }else {
                    //其他异常处理
                    commonResult = CommonResult.commonErrorCode(CommonErrorCode.E_6001);
                }
            }
//        }else {
//            // 没有权限，则设置枚举异常结果
//            commonResult = CommonResult.commonErrorCode(CommonErrorCode.E_3001);
//        }
    }

    private CommonResult doMapper(){
        //将待BaseMapper处理的数据加入到SMQueue中
        ServiceMapperQueue.getInstance().put(entityInfo);
        //每隔1秒判断MS队列中是否有处理结果
        while (!MapperServiceQueue.getInstance().getKeys().contains(entityInfo.getKey())){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //返回Mapper处理结果
        return MapperServiceQueue.getInstance().take(entityInfo.getKey());
    }
}
