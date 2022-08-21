package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.Term;
import com.bluedot.mapper.bean.TermType;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.BufferSolution;
import com.bluedot.pojo.entity.BufferSolution;
import com.bluedot.utils.ReflectUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/8/21 20:29
 * @created: 缓冲溶液业务
 */
public class BufferSolutionService extends BaseService<BufferSolution> {
    public BufferSolutionService(Data data) {
        super(data);
    }

    @Override
    protected void doService() {
        String methodName = null;
        switch (operation){
            case "insert":
                methodName = "insertBufferSolution";
                break;
            case "delete":
                methodName = "deleteBufferSolution";
                break;
            case "update":
                methodName = "updateBufferSolution";
                break;
            case "select":
                if (paramList.get("pageNo")!=null || paramList.get("pageSize") != null){
                    methodName = "listMaterialBufferSolution";
                }else {
                    methodName = "listBufferSolutionNames";
                }
                break;
            default:
                throw new UserException(CommonErrorCode.E_5001);
        }
    }

    /**
     * 添加缓冲溶液
     */
    private void insertBufferSolution(){
        BufferSolution bufferSolution = new BufferSolution();
        ReflectUtil.invokeSetters(paramList,bufferSolution);

        // 将填充参数后的实体类放入entityInfo中
        entityInfo.addEntity(bufferSolution);

        insert();
    }

    /**
     * 删除缓冲溶液
     */
    @SuppressWarnings("unchecked")
    private void deleteBufferSolution(){
        //参数解析
        Map<String,Object> intData = new HashMap<>();
        List<Map<String,Object>> listData = new ArrayList<>();
        if (paramList.get("bufferSolutionId") instanceof List){
            listData = (List<Map<String,Object>>)paramList.get("bufferSolutionId");
        }else {
            intData = (Map<String,Object>) paramList.get("bufferSolutionId");
        }

        //根据变量中是否有值来判断参数的类型，然后执行删除一个还是删除多个的操作
        if (!intData.isEmpty()){
            // 如果删除的参数是int类型的,则删除一个
            BufferSolution bufferSolution = new BufferSolution();
            ReflectUtil.invokeSetters(intData,bufferSolution);
            entityInfo.addEntity(bufferSolution);
        }
        if (!listData.isEmpty()){
            // 如果删除的参数是list类型的，则删除多个
            listData.forEach(data -> {
                BufferSolution bufferSolution = new BufferSolution();
                ReflectUtil.invokeSetters(data,bufferSolution);
                entityInfo.addEntity(bufferSolution);
            });
        }

        delete();
    }

    /**
     * 修改缓冲溶液
     */
    private void updateBufferSolution(){
        BufferSolution bufferSolution = new BufferSolution();
        ReflectUtil.invokeSetters(paramList,bufferSolution);

        // 将填充参数后的实体类放入entityInfo中
        entityInfo.addEntity(bufferSolution);

        update();
    }

    /**
     * 分页所有的缓冲溶液数据
     */
    private void listMaterialBufferSolution(){
        Condition condition = new Condition();
        if (paramList.containsKey("pageSize")){
            condition.setSize((Integer) paramList.get("pageSize"));
        }
        if (paramList.containsKey("pageNo")){
            condition.setStartIndex(((long)paramList.get("pageNo")-1)*(int)paramList.get("pageSize"));
        }
        if (paramList.containsKey("bufferSolutionName")){
            condition.addOrConditionWithView(new Term("buffer_solution","buffer_solution_id",paramList.get("bufferSolutionName"), TermType.EQUAL));
        }

        entityInfo.setCondition(condition);

        select();

    }

    /**
     * 查询所有的缓冲溶液名称
     */
    private void listBufferSolutionNames() {
        Condition condition = new Condition();
        condition.addView("buffer_solution");

        entityInfo.setCondition(condition);

        select();

        List<BufferSolution> bufferSolutions = (List<BufferSolution>) commonResult.getData();
        List<String> bufferSolutionNames = new ArrayList<>();

        bufferSolutions.forEach(one -> {
            bufferSolutionNames.add(one.getBufferSolutionName());
        });

        commonResult.setData(bufferSolutionNames);
    }
}
