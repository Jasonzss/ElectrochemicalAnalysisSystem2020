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
import com.bluedot.utils.LogUtil;
import com.bluedot.utils.ReflectUtil;
import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;

import java.io.File;
import java.sql.Timestamp;
import java.util.*;

/**
 * @Project ElectrochemicalAnalysisSystem2020
 * @Package com.bluedot.service
 * @DateTime 2022/8/23 15:17
 * @Author FuZhichao
 **/
public class AlgorithmService extends BaseService<Algorithm> {
    /**
     * 当前登录用户的邮箱。
     */
    String sessionUserEmail;
    String table = "algorithm";

    Logger logger = LogUtil.getLogger();

    public AlgorithmService(Data data) {
        super(data);
    }


    /**
     * 根据参数分配方法
     */
    @Override
    protected void doService() {
        //从session获得useremail
        sessionUserEmail = (String) session.getAttribute("userEmail");
        //如果前端没有传来的useremail，表示此操作为管理员的操作
        Boolean isAdmin = paramList.get("userEmail") == null;
        //执行的方法名
        String method;

        switch (operation) {
            case "select":
                method = isAdmin ? "listAlgorithm" : "listPersonalAlgorithm";
                break;
            case "update":
                method = isAdmin ? "updateAlgorithm" :
                        "updatePersonalAlgorithm";
                break;
            case "delete":
                method = isAdmin ? "deleteAlgorithm" :
                        "deletePersonalAlgorithm";
                break;
            case "insert":
                method = "insertAlgorithm";
                break;
            default:
                throw new UserException(CommonErrorCode.E_5001);
        }
        System.out.println(method);
        invokeMethod(method, this);
    }


    private void doSelectPage(Condition condition) {
        entityInfo.setCondition(condition);
        selectPage();
    }

    private Condition getSameSelectCondition() {
        // 封装Condition
        Condition condition = new Condition();
        condition.setStartIndex((Long) paramList.get("startIndex"));
        condition.setSize((Integer) paramList.get("pageSize"));


        //拿到参数
        Object algorithmName = paramList.get("algorithmName");
        Object algorithmType = paramList.get("algorithmType");
        Object algorithmLanguage = paramList.get("algorithmLanguage");

        if (algorithmName instanceof String && !((String) algorithmName).isEmpty()) {
            condition.addAndConditionWithView(new Term(table, "algorithm_name"
                    , algorithmName, TermType.EQUAL));
        }
        if (algorithmType instanceof Integer) {
            condition.addAndConditionWithView(new Term(table, "algorithm_name"
                    , algorithmType, TermType.EQUAL));
        }
        if (algorithmLanguage instanceof Integer) {
            condition.addAndConditionWithView(new Term(table, "algorithm_name"
                    , algorithmLanguage, TermType.EQUAL));
        }


        return condition;
    }

    private void listAlgorithm() {
        doSelectPage(getSameSelectCondition());
    }

    private void listPersonalAlgorithm() {
        Condition condition = getSameSelectCondition();
        //确保删除的是自己的，所以添加一个useremail
        condition.addAndConditionWithView(new Term(table, "user_email",
                sessionUserEmail, TermType.EQUAL));
        doSelectPage(condition);

    }

    /**
     * 这个算法（算法名）是否存在
     */
    private Boolean isExists() {
        if (paramList.get("algorithmName") instanceof String) {
            Condition condition = new Condition();
            String algorithmName = (String) paramList.get("algorithmName");
            //where algorithmName = xxx
            condition.addAndConditionWithView(new Term(table, "algorithm_name"
                    , algorithmName, TermType.EQUAL));
            entityInfo.setCondition(condition);
            select();

            return commonResult.getData() != null;
        } else {
            throw new UserException(CommonErrorCode.E_5001);
        }
    }

    private void doUpdate(String[] blackArr) {
        //赋值给实体类
        Algorithm algo = new Algorithm();
        // 覆盖修改时间
        algo.setAlgorithmUpdateTime(new Timestamp(System.currentTimeMillis()));
        ReflectUtil.invokeSettersIncludeEntity(paramList, algo);

        //判断字符串变量长度是否为0，是就抛出变量非法异常
        if (algo.getAlgorithmDesc() != null && algo.getAlgorithmDesc().isEmpty()) {
            throw new UserException(CommonErrorCode.E_5002);
        }
        if (algo.getAlgorithmName() != null && algo.getAlgorithmName().isEmpty()) {
            throw new UserException(CommonErrorCode.E_5002);
        }
        //判断是否包含不允许的修改项
        for (String s : blackArr) {
            if (paramList.containsKey(s)) {
                throw new UserException(CommonErrorCode.E_5001);
            }
        }
        // 判断是否已有同名的算法
        if (isExists()) {
            throw new UserException(CommonErrorCode.E_7001);
        }

        //添加实体类
        entityInfo.addEntity(algo);
        update();
    }

    private Boolean isOwner() {
        List<Integer> ids = new ArrayList<>();
        Object algorithmId = paramList.get("algorithmId");
        Object algorithm = paramList.get("algorithm");
        //获取id
        //update中就一个id
        if (algorithmId instanceof Integer) {
            ids.add((Integer) algorithmId);
        } else if (algorithm instanceof List) {
            for (Map<String, Object> map :
                    (List<Map<String, Object>>) algorithm) {
                if (map.get("algorithmId") instanceof Integer) {
                    ids.add((Integer) map.get("algorithmId"));
                }
            }
        }
        //从数据库里根据id查询
        Condition condition = new Condition();
        condition.addAndConditionWithView(new Term(table, "algorithm_id", ids
                , TermType.IN));
        entityInfo.setCondition(condition);
        select();

        if (commonResult.getData() instanceof List) {
            List<Algorithm> algos = (List<Algorithm>) commonResult.getData();
            for (Algorithm algo : algos) {
                if (!sessionUserEmail.equals(algo.getUser().getUserName())) {
                    return false;
                }
            }
        } else {
            throw new UserException(CommonErrorCode.E_7003);
        }


        return true;
    }
    private void updateAlgorithm() {
        String[] blackArr = {
                "algorithmCreateTime", "algorithmUpdateTime"
        };
        doUpdate(blackArr);
    }

    private void updatePersonalAlgorithm() {
        //不允许的修改项
        String[] blackArr = {
                "algorithmCreateTime", "algorithmUpdateTime", "algorithmStatus"
        };
        //判断数据是否是该用户的
        if (!isOwner()) {
            throw new UserException(CommonErrorCode.E_7004);
        }

        doUpdate(blackArr);

    }


    private void doDelete() {
        //更新中得判断字符串长度是不是0

        Object algorithmPara = paramList.get("algorithm");
        List<Map<String, Object>> list;
        if (algorithmPara instanceof List) {
            list = (List<Map<String, Object>>) algorithmPara;
            for (Map<String, Object> map : list) {
                if (map.containsKey("algorithmId")) {
                    Algorithm algorithm = new Algorithm();
                    Integer algorithmId = (Integer) map.get("algorithmId");
                    algorithm.setAlgorithmId(algorithmId);

                    entityInfo.addEntity(algorithm);

                    //删除文件
                    File file =
                            new File(AlgoUtil.RESPATH + "algo/java/" + algorithmId + ".java");
                    if (file.exists()) {
                        if (!file.delete()) {
                            logger.warn("算法文件删除失败！！");
                        }
                    }
                } else {
                    throw new UserException(CommonErrorCode.E_5001);
                }
            }
            delete();
        } else {
            throw new UserException(CommonErrorCode.E_5001);
        }
    }

    private void deleteAlgorithm() {
        doDelete();
    }

    private void deletePersonalAlgorithm() {
        //删前判断是否是自己的
        if (isOwner()) {
            doDelete();
        } else {
            throw new UserException(CommonErrorCode.E_7004);
        }
    }

    private void insertAlgorithm() {
        String[] required = {
                "algorithmName", "algorithmLanguage", "algorithmType",
                "algorithmDesc"
        };
        Algorithm algo = new Algorithm();
        Map<String, Object> para = new HashMap<>(required.length);
        for (String s : required) {
            if (!paramList.containsKey(s)) {
                throw new UserException(CommonErrorCode.E_5001);
            }
            para.put(s, paramList.get(s));
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
        user.setUserEmail(sessionUserEmail);
        algo.setUser(user);
        entityInfo.addEntity(algo);
        insert();
        //添加完查看这条数据项的id
        Condition condition = new Condition();
        //因为算法名唯一，所以通过查算法名可以得到id
        condition.addAndConditionWithView(new Term(table, "algorithm_name",
                algo.getAlgorithmName(), TermType.EQUAL));
        entityInfo.setCondition(condition);
        select();
        Integer id = null;
        Object data = commonResult.getData();
        if (data instanceof List) {
            //得到该算法名的id
            id = ((List<Algorithm>) data).get(0).getAlgorithmId();
        }
        //添加文件
        File file = new File(AlgoUtil.RESPATH + "algo/java/" + id + ".java");
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
        File tempFile =
                new File(AlgoUtil.RESPATH + "algo/java/" + algo.getAlgorithmId() + ".java");
        try {
            //要是有算法在测试就会占用这个id，所以看看这个文件在不在，存在就让id-1，继续判断直到有个可以用的
            while (tempFile.exists()) {
                algo.setAlgorithmId(algo.getAlgorithmId() - 1);
            }
            tempFile =
                    new File(AlgoUtil.RESPATH + "algo/java/" + algo.getAlgorithmId() + ".java");
            //将上传的文件写入到临时文件
            item.write(tempFile);
            //根据算法类型穿不同的测试数据来测试
            try {
                switch (algo.getAlgorithmType()) {
                    case 0:
                        AlgoUtil.preprocess(algo, (Double[][]) getData(algo));
                        break;
                    case 1:
                        AlgoUtil.dataProcess(algo, (Double[]) getData(algo));
                        break;
                    case 2:
                        AlgoUtil.modeling(algo, (Double[][]) getData(algo));
                        break;
                    default:
                        throw new UserException(CommonErrorCode.E_5001);
                }
                //只要在AlgoUtil执行方法的过程中没报错，得到结果就算是测试通过了
                return true;
            } catch (UserException e) {
                return false;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (!tempFile.delete()) {
                logger.warn("文件删除失败");
            }
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
        Integer id = algo.getAlgorithmId();
        int idMin = 1;
        String desc = algo.getAlgorithmDesc();
        int descMaxLen = 50;
        String name = algo.getAlgorithmName();
        int nameMaxLen = 20;
        Integer language = algo.getAlgorithmLanguage();
        Integer[] languageRange = new Integer[]{0, 1, 2};
        Integer status = algo.getAlgorithmStatus();
        Integer[] statusRange = new Integer[]{0, 1, 2};
        Integer type = algo.getAlgorithmType();
        Integer[] typeRange = new Integer[]{0, 1, 2};
        if (id != null && id < idMin) {
            return false;
        }
        if (desc != null && desc.length() > descMaxLen) {
            return false;
        }
        if (name != null && name.length() > nameMaxLen) {
            return false;
        }
        if (language != null) {
            if (!Arrays.asList(languageRange).contains(language)) {
                return false;
            }
        }
        if (status != null) {
            if (!Arrays.asList(statusRange).contains(status)) {
                return false;
            }
        }
        if (type != null) {
            if (!Arrays.asList(typeRange).contains(type)) {
                return false;
            }
        }
        return true;
    }

}
