package com.bluedot.service;

import com.bluedot.exception.CommonErrorCode;
import com.bluedot.exception.UserException;
import com.bluedot.mapper.bean.*;
import com.bluedot.pojo.Dto.Data;
import com.bluedot.pojo.entity.Algorithm;
import com.bluedot.pojo.entity.Application;
import com.bluedot.pojo.entity.User;
import com.bluedot.utils.AlgoUtil;
import com.bluedot.utils.LogUtil;
import com.bluedot.utils.ReflectUtil;
import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.lang.reflect.Field;
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
    private String sessionUserEmail;
    private final String table = "algorithm";

    private Logger logger = null;

    private final String ID_FIELD_STR = "algorithmId";
    private final String ID_COL_STR = "algorithm_id";
    private final String NAME_FIELD_STR = "algorithmName";
    private final String NAME_COL_STR = "algorithm_name";
    private final String TYPE_FIELD_STR = "algorithmType";
    private final String TYPE_COL_STR = "algorithm_type";
    private final String LANGUAGE_FIELD_STR = "algorithmLanguage";
    private final String LANGUAGE_COL_STR = "algorithm_language";
    private final String DESC_FIELD_STR = "algorithmDesc";
    private final String STATUS_FIELD_STR = "algorithmStatus";
    private final String CREATE_TIME_FIELD_STR = "algorithmCreateTime";
    private final String UPDATE_TIME_FIELD_STR = "algorithmUpdateTime";


    public AlgorithmService(Data data) {
        super(data);
    }

    public AlgorithmService(HttpSession session, EntityInfo<?> entityInfo) {
        super(session, entityInfo);
    }


    /**
     * 根据参数分配方法
     */
    @Override
    protected void doService() {
        //因为是通过调用父类方法，所以不能初始化属性，在这初始化
        logger = LogUtil.getLogger();
        String userEmailKey = "userEmail";
        //从session获得useremail
        sessionUserEmail = (String) session.getAttribute(userEmailKey);
        if (sessionUserEmail == null) {
            throw new UserException(CommonErrorCode.E_1011);
        }
        //如果前端没有传来的useremail，表示此操作为管理员的操作

        Boolean isAdmin = paramList.get(userEmailKey) == null;
        //如果不是管理员，传过来的用户邮箱还不一样，那就是有鬼
        if (!isAdmin) {
            if (!Objects.equals(sessionUserEmail, paramList.get(userEmailKey))) {
                throw new UserException(CommonErrorCode.E_5002);
            }
        }
        //执行的方法名
        String method = null;

        switch (operation) {
            case "select": {
                if (paramList.size() == 1) {
                    if (paramList.get(TYPE_FIELD_STR) instanceof Integer) {
                        method = "listAllAlgorithmByType";
                    }else if (paramList.get(ID_FIELD_STR) instanceof Integer) {
                        method = "selectAlgorithmById";
                    }
                }else {
                    method = isAdmin ? "listAlgorithm" : "listPersonalAlgorithm";
                }
                break;
            }
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
        logger.debug("执行方法：" + method);
        invokeMethod(method, this);
    }


    private void doSelectPage(Condition condition) {
        entityInfo.setCondition(condition);
        selectPage();
    }

    private Condition getSameSelectCondition() {
        // 封装Condition
        Condition condition = new Condition();
        condition.addView(table);
        String returnTypeStr = "Algorithm";
        condition.setReturnType(returnTypeStr);

        condition.setStartIndex(Long.valueOf((Integer) paramList.get("pageNo")));
        condition.setSize((Integer) paramList.get("pageSize"));


        //拿到参数
        Object algorithmName = paramList.get(NAME_FIELD_STR);
        Object algorithmType = paramList.get(TYPE_FIELD_STR);
        Object algorithmLanguage = paramList.get(LANGUAGE_FIELD_STR);

        if (algorithmName instanceof String && !((String) algorithmName).isEmpty()) {
            condition.addAndConditionWithView(new Term(table, NAME_COL_STR
                    , algorithmName, TermType.LIKE));
        }
        if (algorithmType instanceof Integer) {
            condition.addAndConditionWithView(new Term(table, TYPE_COL_STR
                    , algorithmType, TermType.EQUAL));
        }
        if (algorithmLanguage instanceof Integer) {
            condition.addAndConditionWithView(new Term(table, LANGUAGE_COL_STR
                    , algorithmLanguage, TermType.EQUAL));
        }


        return condition;
    }

    private void listAlgorithm() {
        doSelectPage(getSameSelectCondition());
        transformListResult();
    }

    private void listPersonalAlgorithm() {
        Condition condition = getSameSelectCondition();
        //确保删除的是自己的，所以添加一个useremail
        condition.addAndConditionWithView(new Term(table, "user_email",
                sessionUserEmail, TermType.EQUAL));
        doSelectPage(condition);
        transformListResult();
    }

    /**
     * 根据id查出算法，供其他模块使用
     */
    private void selectAlgorithmById() {
        if (paramList.get(ID_FIELD_STR) instanceof Integer) {
            Condition condition = new Condition();
            condition.addView(table);
            String returnTypeStr = "Algorithm";
            condition.setReturnType(returnTypeStr);
            condition.addAndConditionWithView(new Term(table, ID_COL_STR, paramList.get(ID_FIELD_STR), TermType.EQUAL));

            entityInfo.setCondition(condition);
            select();
            transformListResult();
        }else {
            throw new UserException(CommonErrorCode.E_5001);
        }
    }
    private void listAllAlgorithmByType() {
        if (!(paramList.get(TYPE_FIELD_STR) instanceof Integer)) {
            throw new UserException(CommonErrorCode.E_5001);
        }

        Condition condition = new Condition();
        condition.addView(table);
        String returnTypeStr = "Algorithm";
        condition.setReturnType(returnTypeStr);
        condition.addAndConditionWithView(new Term(table, TYPE_COL_STR, paramList.get(TYPE_FIELD_STR), TermType.EQUAL));

        entityInfo.setCondition(condition);
        select();
        transformListResult();
    }
    /**
     * 算法查询结果的渲染，对algorithmType，algorithmStatus，algorithmLanguage的转化（数字转文字）
     */
    private void transformListResult() {
        List<Map<String, Object>> list = new ArrayList<>();
        String[] needTrans = new String[]{TYPE_FIELD_STR, STATUS_FIELD_STR, LANGUAGE_FIELD_STR};
        List<Object> algos = null;
        if (commonResult.getData() instanceof List) {
            algos = (List<Object>) commonResult.getData();
        }else if (commonResult.getData() instanceof PageInfo) {
            //TODO 等PageInfo修改完后需要修改
            algos = (List<Object>) ((PageInfo) commonResult.getData()).getDataList().get(0);
        }else {
            throw new UserException(CommonErrorCode.E_6001);
        }

        Field[] fields = Algorithm.class.getDeclaredFields();
        //遍历查询的每一个实体类对象
        for (Object algo: algos) {
            Map<String, Object> each = new HashMap<>(Algorithm.class.getFields().length);
            //遍历每一个属性
            for (Field field: fields) {
                try {
                    //属性名
                    String name = field.getName();
                    //属性值
                    field.setAccessible(true);
                    Object value = field.get(algo);
                    //如果有需要转换的，就转换
                    if (Arrays.asList(needTrans).contains(name) && value != null) {
                        value = getTransformResult(name, (Integer) value);
                    }
                    each.put(name, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            list.add(each);

        }

        commonResult.setData(list);

    }

    private String getTransformResult(String key, Integer num) {
        Map<String, Map<Integer, String>> transformRule = getTransformRule();
        return transformRule.get(key).get(num);
    }

    private Map<String, Map<Integer, String>> getTransformRule() {
        Map<String, Map<Integer, String>> ret = new HashMap<>(3);

        Map<Integer, String> algorithmTypeRule = new HashMap<>(3);
        algorithmTypeRule.put(0, "预处理算法");
        algorithmTypeRule.put(1, "数据处理算法");
        algorithmTypeRule.put(2, "数据模型算法");
        ret.put(TYPE_FIELD_STR, algorithmTypeRule);

        Map<Integer, String> algorithmStatusRule = new HashMap<>(3);
        algorithmStatusRule.put(0, "私有");
        algorithmStatusRule.put(1, "审核中");
        algorithmStatusRule.put(2, "公开");
        ret.put(STATUS_FIELD_STR, algorithmStatusRule);

        Map<Integer, String> algorithmLanguageRule = new HashMap<>(3);
        algorithmLanguageRule.put(0, "Java");
        algorithmLanguageRule.put(1, "Matlab");
        algorithmLanguageRule.put(2, "Python");
        ret.put(LANGUAGE_FIELD_STR, algorithmLanguageRule);

        return ret;
    }

    /**
     * 这个算法（算法名）是否存在
     */
    private Boolean isExists() {
        if (paramList.get(NAME_FIELD_STR) instanceof String) {
            Condition condition = new Condition();
            condition.addView(table);
            String returnTypeStr = "Algorithm";
            condition.setReturnType(returnTypeStr);
            String algorithmName = (String) paramList.get(NAME_FIELD_STR);
            //where algorithmName = xxx
            condition.addAndConditionWithView(new Term(table, NAME_COL_STR
                    , algorithmName, TermType.EQUAL));
            entityInfo.setCondition(condition);
            select();
            Object data = commonResult.getData();
            if (data instanceof List) {
                if (((List<Algorithm>) data).size() != 0) {
                    return ((List<Algorithm>) data).get(0).getAlgorithmId() != paramList.get(ID_FIELD_STR);
                }
            }
        }

        //如果没传algorithmName，那就意味着是发送申请来修改Status，那就不会冲突，直接修改就行了
        return false;
    }

    private void doUpdate(String[] blackArr) {
        //赋值给实体类
        Algorithm algo = new Algorithm();
        // 覆盖修改时间
        algo.setAlgorithmUpdateTime(new Timestamp(System.currentTimeMillis()));
        ReflectUtil.invokeSettersIncludeEntity(paramList, algo);

        //判断是否有algorithmId字段，没有就抛出异常
        if (algo.getAlgorithmId() == null) {
            throw new UserException(CommonErrorCode.E_5001);
        }
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
        Object algorithmId = paramList.get(ID_FIELD_STR);
        Object algorithm = paramList.get("algorithm");
        //获取id
        //update中就一个id
        if (algorithmId instanceof Integer) {
            ids.add((Integer) algorithmId);
        } else if (algorithm instanceof List) {
            for (Map<String, Object> map :
                    (List<Map<String, Object>>) algorithm) {
                if (map.get(ID_FIELD_STR) instanceof Integer) {
                    ids.add((Integer) map.get(ID_FIELD_STR));
                } else {
                    //没有id就到这
                    throw new UserException(CommonErrorCode.E_5001);
                }
            }
        }else {
            //没有id就会到这
            throw new UserException(CommonErrorCode.E_5001);
        }
        //从数据库里根据id查询
        Condition condition = new Condition();
        condition.addView(table);
        String returnTypeStr = "Algorithm";
        condition.setReturnType(returnTypeStr);
        condition.addAndConditionWithView(new Term(table, ID_COL_STR, ids
                , TermType.IN));
        entityInfo.setCondition(condition);
        select();

        if (commonResult.getData() instanceof List) {
            List<Algorithm> algos = (List<Algorithm>) commonResult.getData();
            for (Algorithm algo : algos) {
                if (!sessionUserEmail.equals(algo.getUser().getUserEmail())) {
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
                CREATE_TIME_FIELD_STR, UPDATE_TIME_FIELD_STR
        };
        doUpdate(blackArr);
    }

    private void updatePersonalAlgorithm() {
        //不允许的修改项
        String[] blackArr = {
                CREATE_TIME_FIELD_STR, UPDATE_TIME_FIELD_STR, STATUS_FIELD_STR
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
                NAME_FIELD_STR, LANGUAGE_FIELD_STR, TYPE_FIELD_STR,
                DESC_FIELD_STR
        };
        Algorithm algo = new Algorithm();
        Map<String, Object> para = new HashMap<>(required.length);
        for (String s : required) {
            if (!paramList.containsKey(s)) {
                throw new UserException(CommonErrorCode.E_5001);
            }
            try {
                para.put(s, Integer.valueOf((String) paramList.get(s)));
            } catch (NumberFormatException e) {
                para.put(s, paramList.get(s));
            }

        }
        //判断是否已有同名的算法
        if (isExists()) {
            throw new UserException(CommonErrorCode.E_7001);
        }
        //初始化一些基础的值
        ReflectUtil.invokeSettersIncludeEntity(para, algo);
        //测试文件
        FileItem item = (FileItem) paramList.get("file");
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
        //在测试方法中会设定一个id，所以在这里我们需要设置id为空，以防出意外
        algo.setAlgorithmId(null);
        entityInfo.addEntity(algo);
        insert();
        //添加完查看这条数据项的id
        Condition condition = new Condition();
        condition.addView(table);
        String returnTypeStr = "Algorithm";
        condition.setReturnType(returnTypeStr);
        //因为算法名唯一，所以通过查算法名可以得到id
        condition.addAndConditionWithView(new Term(table, NAME_COL_STR,
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
            logger.warn("上传的算法写入失败");
            e.printStackTrace();
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
                System.out.println(e.getErrorCode().getMsg());
                //TODO 待完善获取数据，需要改为false
                return true;
            }
        } catch (Exception e) {
            logger.warn("用户上传算法写入到本地失败！");
            e.printStackTrace();
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
