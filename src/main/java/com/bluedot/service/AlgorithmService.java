package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.Condition;
import com.bluedot.mapper.bean.Term;
import com.bluedot.mapper.bean.TermType;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.Algorithm;
import com.bluedot.pojo.entity.User;
import com.bluedot.utils.AlgoUtil;
import com.bluedot.utils.ReflectUtil;
import org.apache.commons.fileupload.FileItem;

import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Project ElectrochemicalAnalysisSystem2020
 * @Package com.bluedot.service
 * @DateTime 2022/8/23 15:17
 * @Author FuZhichao
 **/
public class AlgorithmService extends BaseService<Algorithm> {
    //当前登录用户的邮箱。
    String sessionUseremail;
    String table = "algorithm";

    public AlgorithmService(Data data) {
        super(data);
    }


    /**
     * 根据参数分配方法
     */
    @Override
    protected void doService() {
        //从session获得useremail
        sessionUseremail = String.valueOf(session.getAttribute("userEmail"));
        //前端传来的username，可能为空
        String paraUseremail = String.valueOf(paramList.get("userEmail"));
        //如果paraUseremail为空，表示此操作为管理员的操作
        Boolean isAdmin = paraUseremail == null ? true : false;
        //执行的方法名
        String method;

        switch (operation) {
            case "select":
                method = isAdmin ? "listAlgorithm" : "listPersonalAlgorithm";
                break;
            case "update":
                method = isAdmin ? "updateAlgorithm" : "updatePersonalAlgorithm";
                break;
            case "delete":
                method = isAdmin ? "deleteAlgorithm" : "deletePersonalAlgorithm";
                break;
            case "insert":
                method = "insertAlgorithm";
                break;
            default:
                throw new UserException(CommonErrorCode.E_5001);
        }

        invokeMethod(method, this);
    }



    private void listAlgorithm() {
        // 封装Condition
        Condition condition = new Condition();
        condition.setStartIndex((Long) paramList.get("startIndex"));
        condition.setSize((Integer) paramList.get("pageSize"));


        //拿到参数
        String algorithmName = (String) paramList.get("algorithmName");
        Integer algorithmType = (Integer) paramList.get("algorithmType");
        Integer algorithmLanguage = (Integer) paramList.get("algorithmLanguage");


        //添加查询表的条件
        if (algorithmName != null || !algorithmName.isEmpty()) {
            condition.addAndConditionWithView(new Term(table, "algorithm_name", algorithmName, TermType.EQUAL));
        }
        if (algorithmType != null) {
            condition.addAndConditionWithView(new Term(table, "algorithm_type", algorithmType, TermType.EQUAL));
        }
        if (algorithmLanguage != null) {
            condition.addAndConditionWithView(new Term(table, "algorithm_language", algorithmLanguage, TermType.EQUAL));
        }


        entityInfo.setCondition(condition);
        selectPage();
    }

    private void listPersonalAlgorithm() {
        // 封装Condition
        Condition condition = new Condition();
        condition.setStartIndex((Long) paramList.get("startIndex"));
        condition.setSize((Integer) paramList.get("pageSize"));


        //拿到参数
        String algorithmName = (String) paramList.get("algorithmName");
        Integer algorithmType = (Integer) paramList.get("algorithmType");
        Integer algorithmLanguage = (Integer) paramList.get("algorithmLanguage");


        //添加查询表的条件
        if (algorithmName != null || !algorithmName.isEmpty()) {
            condition.addAndConditionWithView(new Term(table, "algorithm_name", algorithmName, TermType.EQUAL));
        }
        if (algorithmType != null) {
            condition.addAndConditionWithView(new Term(table, "algorithm_type", algorithmType, TermType.EQUAL));
        }
        if (algorithmLanguage != null) {
            condition.addAndConditionWithView(new Term(table, "algorithm_language", algorithmLanguage, TermType.EQUAL));
        }

        //确保删除的是自己的，所以添加一个useremail
        condition.addAndConditionWithView(new Term(table, "user_email", sessionUseremail, TermType.EQUAL));

        entityInfo.setCondition(condition);
        selectPage();

    }

    /**
     * 这个算法（算法名）是否存在
     */
    private Boolean isExists() {
        if (paramList.get("algorithmName") instanceof String) {
            Condition condition = new Condition();
            String algorithmName = (String) paramList.get("algorithmName");
            //where algorithmName = xxx
            condition.addAndConditionWithView(new Term(table, "algorithm_name", algorithmName, TermType.EQUAL));
            entityInfo.setCondition(condition);
            select();

            if (commonResult.getData() != null) {
                return false;
            }else {
                return true;
            }
        }else {
            throw new UserException(CommonErrorCode.E_5001);
        }
    }

    private void updateAlgorithm() {
        if (!check()) {
            throw new UserException(CommonErrorCode.E_5002);
        }
        //不允许的修改项
        String[] blackArr = {
                "algorithmFileName", "algorithmCreateTime", "algorithmUpdateTime"
        };
        //判断是否包含不允许的修改项
        for (int i = 0; i < blackArr.length; i ++) {
            if (paramList.containsKey(blackArr[i])) {
                throw new UserException(CommonErrorCode.E_5001);
            }
        }
        // 判断是否已有同名的算法
        if (isExists()) {
            throw new UserException(CommonErrorCode.E_7001);
        }
        //赋值给实体类
        Algorithm algo = new Algorithm();
        // 覆盖修改时间
        algo.setAlgorithmUpdateTime(new Timestamp(System.currentTimeMillis()));
        ReflectUtil.invokeSettersIncludeEntity(paramList, algo);
        //添加实体类
        entityInfo.addEntity(algo);
        update();

    }



    private void updatePersonalAlgorithm() {
        if (!check()) {
            throw new UserException(CommonErrorCode.E_5002);
        }
        //不允许的修改项
        String[] blackArr = {
                "algorithmFileName", "algorithmCreateTime", "algorithmUpdateTime", "algorithmStatus"
        };
        //判断是否包含不允许的修改项
        for (int i = 0; i < blackArr.length; i ++) {
            if (paramList.containsKey(blackArr[i])) {
                throw new UserException(CommonErrorCode.E_5001);
            }
        }
        // 判断是否已有同名的算法
        if (isExists()) {
            throw new UserException(CommonErrorCode.E_7001);
        }
        //赋值给实体类
        Algorithm algo = new Algorithm();
        // 覆盖修改时间
        algo.setAlgorithmUpdateTime(new Timestamp(System.currentTimeMillis()));
        ReflectUtil.invokeSettersIncludeEntity(paramList, algo);
        //添加实体类
        entityInfo.addEntity(algo);
        //设置更新参数，确保是自己的
        Condition condition = new Condition();
        condition.addAndConditionWithView(new Term(table, "user_email", sessionUseremail, TermType.EQUAL));
        update();
    }

    private void deleteAlgorithm() {
        Object algorithmPara = paramList.get("algorithm");
        List<Map<String, Object>> list = null;
        if (algorithmPara instanceof List) {
            list = (List<Map<String, Object>>) algorithmPara;
            for (Map<String, Object> map: list) {
                if (map.containsKey("algorithmId")) {
                    Algorithm algorithm = new Algorithm();
                    Integer algorithmId = (Integer) map.get("algorithmId");
                    algorithm.setAlgorithmId(algorithmId);
                    entityInfo.addEntity(algorithm);

                    //删除文件
                    File file = new File(AlgoUtil.RESPATH + "algo/src/" + algorithmId + ".java");
                    if (file.exists()) {
                        file.delete();
                    }
                } else {
                    throw new UserException(CommonErrorCode.E_5001);
                }

            }
            delete();


        }else {
            throw new UserException(CommonErrorCode.E_5001);
        }

    }

    private void deletePersonalAlgorithm() {
        Object algorithmPara = paramList.get("algorithm");
        List<Map<String, Object>> list = null;
        if (algorithmPara instanceof List) {
            list = (List<Map<String, Object>>) algorithmPara;
            for (Map<String, Object> map: list) {
                if (map.containsKey("algorithmId")) {
                    Algorithm algorithm = new Algorithm();
                    Integer algorithmId = (Integer) map.get("algorithmId");
                    algorithm.setAlgorithmId(algorithmId);
                    entityInfo.addEntity(algorithm);

                    //删除文件
                    File file = new File(AlgoUtil.RESPATH + "algo/src/" + algorithmId + ".java");
                    if (file.exists()) {
                        file.delete();
                    }
                } else {
                    throw new UserException(CommonErrorCode.E_5001);
                }

            }
        }
        //添加删除参数
        Condition condition = new Condition();
        condition.addAndConditionWithView(new Term(table, "user_email", sessionUseremail, TermType.EQUAL));
        entityInfo.setCondition(condition);

        delete();
    }

    private void insertAlgorithm() {
        if (!check()) {
            throw new UserException(CommonErrorCode.E_5002);
        }

        String[] required = {
                "algorithmName", "algorithmLanguage", "algorithmType", "algorithmDesc"
        };
        Algorithm algo = new Algorithm();
        Map<String, Object> para = new HashMap<>(required.length);
        for (int i = 0; i < required.length; i++) {
            if (!paramList.containsKey(required[i])) {
                throw new UserException(CommonErrorCode.E_5001);
            }
            para.put(required[i], paramList.get(required[i]));
        }
        //判断是否已有同名的算法
        if (isExists()) {
            throw new UserException(CommonErrorCode.E_7001);
        }
        //初始化一些基础的值
        ReflectUtil.invokeSettersIncludeEntity(para, algo);
        //测试文件
        FileItem item = (FileItem) paramList.get("algorithmFile");
        if (!test(algo, item)) {
            throw new UserException(CommonErrorCode.E_7002);
        }
        //测试通过后，向数据库添加这条信息
        //添加一些附加消息
        algo.setAlgorithmCreateTime(new Timestamp(System.currentTimeMillis()));
        algo.setAlgorithmUpdateTime(new Timestamp(System.currentTimeMillis()));
        //默认私有
        algo.setAlgorithmStatus(0);
        User user = new User();
        user.setUserEmail(sessionUseremail);
        algo.setUser(user);
        entityInfo.addEntity(algo);
        insert();
        //添加完查看这条数据项的id
        Condition condition = new Condition();
        //因为算法名唯一，所以通过查算法名可以得到id
        condition.addAndConditionWithView(new Term(table, "algorithm_name", algo.getAlgorithmName(), TermType.EQUAL));
        entityInfo.setCondition(condition);
        select();
        Integer id = null;
        Object data = commonResult.getData();
        if (data instanceof Algorithm) {
            id = ((Algorithm) data).getAlgorithmId();
        }else if (data instanceof List) {
            id = ((List<Algorithm>) data).get(0).getAlgorithmId();
        }else if (data instanceof Algorithm[]) {
            id = ((Algorithm) data).getAlgorithmId();
        }
        //添加文件
        File file = new File(AlgoUtil.RESPATH + "algo/src/" + id + ".java");
        try {
            item.write(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Boolean test(Algorithm algo, FileItem item) {
        //测试用类
        algo.setAlgorithmId(-1);
        //创建一个临时文件去测试
        File tempFile = null;
        try {
            tempFile = new File(AlgoUtil.RESPATH + "algo/src/" + algo.getAlgorithmId() + ".java");
            //要是有算法在测试就会占用这个id，所以看看这个文件在不在，存在就让id-1，继续判断直到有个可以用的
            while (tempFile.exists()) {
                algo.setAlgorithmId(algo.getAlgorithmId() - 1);
            }
            tempFile = new File(AlgoUtil.RESPATH + "algo/src/" + algo.getAlgorithmId() + ".java");
            //将上传的文件写入到临时文件
            item.write(tempFile);
            //根据算法类型穿不同的测试数据来测试
            Object ret = null;
            try {
                switch (algo.getAlgorithmType()) {
                    case 0: ret = AlgoUtil.preprocess(algo, (Double[][]) getData(algo));break;
                    case 1: ret = AlgoUtil.dataProcess(algo, (Double[]) getData(algo));break;
                    case 2: ret = AlgoUtil.modeling(algo, (Double[][]) getData(algo));break;
                    default: throw new UserException(CommonErrorCode.E_5001);
                }
                //只要在AlgoUtil执行方法的过程中没报错，得到结果就算是测试通过了
                return true;
            } catch (UserException e) {
                return false;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            tempFile.delete();
        }
    }
    //根据算法类型返回不同的数据用于测试

    private Object getData(Algorithm algo) {
        return null;
    }


    @Override
    protected boolean check() {
        Algorithm algo = new Algorithm();
        ReflectUtil.invokeSettersIncludeEntity(paramList, algo);
        if (algo.getAlgorithmDesc() != null && algo.getAlgorithmDesc().length() > 50) {
            return false;
        }else if (algo.getAlgorithmName() != null && algo.getAlgorithmName().length() > 50) {
            return false;
        }
        return true;
    }

}
