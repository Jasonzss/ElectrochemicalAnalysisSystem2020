package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.EntityInfo;
import com.bluedot.mapper.bean.Term;
import com.bluedot.mapper.bean.TermType;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.BufferSolution;
import com.bluedot.pojo.entity.BufferSolution;
import com.bluedot.pojo.vo.CommonResult;
import com.bluedot.utils.ReflectUtil;

import javax.servlet.http.HttpSession;
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

    public BufferSolutionService(HttpSession session, EntityInfo<?> entityInfo) {
        super(session, entityInfo);
    }

    @Override
    protected void doService() {

        String methodName;
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
                methodName = "listBufferSolution";
                break;
            default:
                throw new UserException(CommonErrorCode.E_5001);
        }
    }

    @Override
    protected boolean check() {
        return false;
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
        if (paramList.get("bufferSolutionId") instanceof List){
            // 如果删除的参数是list类型的，则删除多个
            List<Integer> listData = (List<Integer>)paramList.get("bufferSolutionId");
            listData.forEach(data -> {
                BufferSolution bufferSolution = new BufferSolution();
                bufferSolution.setBufferSolutionId(data);
                entityInfo.addEntity(bufferSolution);
            });
        }else if(paramList.get("bufferSolutionId") instanceof Integer){
            // 如果删除的参数是map类型的,则删除一个
            int intData = (Integer) paramList.get("bufferSolutionId");
            BufferSolution bufferSolution = new BufferSolution();
            bufferSolution.setBufferSolutionId(intData);
            entityInfo.addEntity(bufferSolution);
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
     * 查询缓冲溶液
     */
    private void listBufferSolution(){
        Condition condition = new Condition();
        if (paramList.containsKey("pageSize") && paramList.get("bufferSolutionName") != null){
            condition.setSize((Integer) paramList.get("pageSize"));
        }
        if (paramList.containsKey("pageNo") && paramList.get("bufferSolutionName") != null){
            condition.setStartIndex(((long)paramList.get("pageNo")-1)*(int)paramList.get("pageSize"));
        }
        if (paramList.containsKey("bufferSolutionName") && paramList.get("bufferSolutionName") != null){
            condition.addOrConditionWithView(new Term("buffer_solution","buffer_solution_id",paramList.get("bufferSolutionName"), TermType.EQUAL));
        }

        entityInfo.setCondition(condition);

        select();

    }
}
