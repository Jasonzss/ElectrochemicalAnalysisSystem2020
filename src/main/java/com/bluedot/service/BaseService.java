package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.EntityInfo;
import com.bluedot.mapper.bean.PageInfo;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.vo.CommonResult;
import com.bluedot.queue.enterQueue.Impl.ServiceMapperQueue;
import com.bluedot.queue.outQueue.impl.MapperServiceQueue;
import com.bluedot.queue.outQueue.impl.ServiceControllerQueue;

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
        doService();
        ServiceControllerQueue.getInstance().put(data.getKey(), commonResult);
    }

    /**
     * Service之间互相调用使用到的构造方法
     * @param session 调用者自身的Session
     * @param map 调用者自身的paramList
     * @param operation 调用者调用某个Service时想让他执行的操作
     * @param commonResult 调用者自身的CommonResult
     */
    public BaseService(HttpSession session,Map<String,Object> map,String operation,CommonResult commonResult){
        paramList = map;
        this.session = session;
        this.operation = operation;
        entityInfo = new EntityInfo<>();

        doService();
        commonResult = this.commonResult;
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
    }

    /**
     * 负责在具体Service中分析调用哪些方法来解决请求
     */
    abstract protected void doService();

    protected void update(){
        entityInfo.setKey(1L);
        entityInfo.setOperation("update");
        commonResult = doMapper();
    }

    protected void delete(){
        entityInfo.setKey(1L);
        entityInfo.setOperation("delete");
        commonResult = doMapper();
    }

    protected void insert(){
        entityInfo.setKey(1L);
        entityInfo.setOperation("insert");
        commonResult = doMapper();
    }

    protected void select(){
        entityInfo.setKey(1L);
        entityInfo.setOperation("select");
        commonResult = doMapper();
    }

    /**
     * 分页查询
     */
    protected void selectPage(){
        // 查询当前页的对应数据
        entityInfo.setKey(1L);
        entityInfo.setOperation("select");
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

        entityInfo.setKey(1L);
        entityInfo.setCondition(condition);
        entityInfo.setOperation("select");
        CommonResult commonResult = doMapper();

        return (long) commonResult.getData();
    }

    protected void invokeMethod(String methodName,Object obj){
        List<String> permissionList = (List<String>) session.getAttribute("permissionList");
        if ("login".equals(operation) || permissionList.contains(methodName)){
            //存在此权限，执行响应方法
            try {
                Method method = obj.getClass().getDeclaredMethod(methodName);
                method.invoke(obj);
            } catch (NoSuchMethodException e) {
                System.out.println(1);
                throw new UserException(CommonErrorCode.E_5001);
            } catch (IllegalAccessException e) {
                System.out.println(2);
                throw new UserException(CommonErrorCode.E_5001);
            } catch (InvocationTargetException e) {
                System.out.println(3);
                throw new UserException(CommonErrorCode.E_5001);
            }
        }else {
            // 没有权限，则设置枚举异常结果
            commonResult = CommonResult.commonErrorCode(CommonErrorCode.E_3001);
        }
    }

    private CommonResult doMapper(){
        //将待BaseMapper处理的数据加入到SMQueue中
        ServiceMapperQueue.getInstance().put(entityInfo);
        //每隔1秒判断MS队列中是否有处理结果
        while (!MapperServiceQueue.getInstance().getKeys().contains(entityInfo.getKey())){
            try {
                System.out.println("正在睡觉");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //返回Mapper处理结果
        return MapperServiceQueue.getInstance().take(entityInfo.getKey());
    }
}
