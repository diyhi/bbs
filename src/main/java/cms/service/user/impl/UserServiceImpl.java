package cms.service.user.impl;


import cms.component.JsonComponent;
import cms.component.TextFilterComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.membershipCard.MembershipCardGiftTaskCacheManager;
import cms.component.membershipCard.MembershipCardGiftTaskComponent;
import cms.component.payment.PaymentComponent;
import cms.component.question.QuestionCacheManager;
import cms.component.question.QuestionComponent;
import cms.component.topic.TopicComponent;
import cms.component.user.*;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.dto.payment.PaymentRequest;
import cms.model.payment.PaymentLog;
import cms.model.payment.PaymentVerificationLog;
import cms.model.question.*;
import cms.model.riskControl.PenaltyUserInfo;
import cms.model.riskControl.RiskControlModel;
import cms.model.topic.*;
import cms.model.user.*;
import cms.repository.payment.PaymentRepository;
import cms.repository.question.AnswerRepository;
import cms.repository.question.QuestionIndexRepository;
import cms.repository.question.QuestionRepository;
import cms.repository.question.QuestionTagRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.topic.CommentRepository;
import cms.repository.topic.TagRepository;
import cms.repository.topic.TopicIndexRepository;
import cms.repository.topic.TopicRepository;
import cms.repository.user.UserCustomRepository;
import cms.repository.user.UserGradeRepository;
import cms.repository.user.UserRepository;
import cms.repository.user.UserRoleRepository;
import cms.service.user.UserService;
import cms.utils.FileUtil;
import cms.utils.SHA;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import jakarta.annotation.Resource;
import jakarta.persistence.*;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 用户服务
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    @Resource
    UserRepository userRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    UserGradeRepository userGradeRepository;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    UserComponent userComponent;
    @Resource
    UserCustomRepository userCustomRepository;
    @Resource
    UserRoleRepository userRoleRepository;
    @Resource
    TopicComponent topicComponent;
    @Resource
    QuestionComponent questionComponent;
    @Resource
    TopicIndexRepository topicIndexRepository;
    @Resource
    QuestionIndexRepository questionIndexRepository;
    @Resource
    FileComponent fileComponent;
    @Resource UserCacheManager userCacheManager;
    @Resource UserRoleCacheManager userRoleCacheManager;
    @Resource
    PaymentRepository paymentRepository;
    @Resource
    PaymentComponent paymentComponent;
    @Resource
    PointLogConfig pointLogConfig;
    @Resource PointComponent pointComponent;
    @Resource
    MembershipCardGiftTaskComponent membershipCardGiftTaskComponent;
    @Resource
    TopicRepository topicRepository;
    @Resource
    TagRepository tagRepository;
    @Resource
    CommentRepository commentRepository;
    @Resource
    TextFilterComponent textFilterComponent;
    @Resource
    QuestionRepository questionRepository;
    @Resource
    QuestionTagRepository questionTagRepository;
    @Resource
    AnswerRepository answerRepository;


    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // 日期时间格式化
    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final List<Integer> paymentType = Arrays.asList(1,2, 3);
    @Autowired
    private MembershipCardGiftTaskCacheManager membershipCardGiftTaskCacheManager;
    @Autowired
    private QuestionCacheManager questionCacheManager;

    //积分请求信息
    private record PointQuery(
            Integer startPoint,//起始积分
            Integer endPoint//结束积分
    ) {
    }

    //注册日期请求信息
    private record RegistrationDateQuery(
            LocalDateTime startRegistrationDate,//起始注册日期
            LocalDateTime endRegistrationDate//结束注册日期
    ) {
    }

    /**
     * 获取用户列表
     *
     * @param page              页码
     * @param visible           null或true:正常页面  false:回收站
     * @param fileServerAddress 文件服务器随机地址
     */
    public PageView<User> getUserList(int page, Boolean visible, String fileServerAddress) {
        //调用分页算法代码
        PageView<User> pageView = new PageView<User>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(), page, 10);

        //当前页
        int firstIndex = (page - 1) * pageView.getMaxresult();

        String param = "";//sql参数
        List<Object> paramValue = new ArrayList<Object>();//sql参数值


        if (visible != null && !visible) {//回收站
            param = " o.state>? ";
            paramValue.add(2);
        } else {//正常页面
            param = " o.state<=? ";
            paramValue.add(2);
        }

        QueryResult<User> qr = userRepository.findUserByCondition(param, paramValue, firstIndex, pageView.getMaxresult(), false);

        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        List<UserGrade> userGradeList = userGradeRepository.findAllGrade();
        if (userGradeList != null && userGradeList.size() > 0) {
            for (User user : pageView.getRecords()) {//取得所有用户
                for (UserGrade userGrade : userGradeList) {//取得所有等级
                    if (user.getPoint() >= userGrade.getNeedPoint()) {
                        user.setGradeName(userGrade.getName());//将等级值设进等级参数里
                        break;
                    }
                }

            }
        }

        if (pageView.getRecords() != null && pageView.getRecords().size() > 0) {
            for (User user : pageView.getRecords()) {//取得所有用户
                if (user.getType() > 10) {
                    user.setPlatformUserId(userComponent.platformUserIdToThirdPartyUserId(user.getPlatformUserId()));
                }

            }
            //仅显示指定字段
            List<User> userViewList = new ArrayList<User>();
            for (User user : pageView.getRecords()) {//取得所有用户
                User userView = new User();

                userView.setId(user.getId());
                userView.setUserName(user.getUserName());
                userView.setAccount(user.getAccount());
                userView.setNickname(user.getNickname());
                userView.setCancelAccountTime(user.getCancelAccountTime());
                userView.setAllowUserDynamic(user.getAllowUserDynamic());
                userView.setEmail(user.getEmail());
                userView.setIssue(user.getIssue());
                userView.setMobile(user.getMobile());
                userView.setId(user.getId());
                userView.setRealNameAuthentication(user.isRealNameAuthentication());
                userView.setRegistrationDate(user.getRegistrationDate());
                userView.setRemarks(user.getRemarks());
                userView.setPoint(user.getPoint());
                userView.setDeposit(user.getDeposit());
                userView.setType(user.getType());
                userView.setPlatformUserId(user.getPlatformUserId());
                userView.setState(user.getState());
                userView.setUserVersion(user.getUserVersion());
                userView.setUserRoleNameList(user.getUserRoleNameList());
                userView.setGradeId(user.getGradeId());
                userView.setGradeName(user.getGradeName());
                userView.setAvatarPath(fileServerAddress + user.getAvatarPath());
                userView.setAvatarName(user.getAvatarName());
                userViewList.add(userView);
            }
            pageView.setRecords(userViewList);
        }
        return pageView;
    }

    /**
     * 搜索用户列表
     *
     * @param page                   页码
     * @param searchType             查询类型
     * @param userTypeCode           查询用户类型
     * @param keyword                关键词
     * @param start_point            起始积分
     * @param end_point              结束积分
     * @param start_registrationDate 起始注册日期
     * @param end_registrationDate   结束注册日期
     * @param fileServerAddress      文件服务器随机地址
     * @param request                请求信息
     * @return
     */
    public Map<String, Object> searchUset(int page, Integer searchType, Integer userTypeCode, String keyword,
                                          String start_point, String end_point,
                                          String start_registrationDate, String end_registrationDate, String fileServerAddress, HttpServletRequest request) {

        Map<String, String> errors = new HashMap<String, String>();
        Map<String, Object> returnValue = new HashMap<String, Object>();
        if (searchType == null) {//如果查询类型为空，则默认为用户名查询
            searchType = 1;
        }
        PointQuery pointQuery = null;
        RegistrationDateQuery registrationDateQuery = null;
        /**
         //校验搜索类型
         if (searchType.equals(1)) {//用户
         validateSearchType(userTypeCode, keyword, errors);
         }**/

        if (searchType.equals(2)) {//筛选条件
            //积分范围
            pointQuery = validateAndConvertPoint(start_point, end_point, errors);

            //注册日期范围
            registrationDateQuery = validateAndConvertRegistrationDate(start_registrationDate, end_registrationDate, errors);

        }

        //处理用户自定义字段
        List<UserCustom> userCustomList = processUserCustomFields(request, errors);

        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }


        //调用分页算法代码
        PageView<User> pageView = new PageView<User>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(), page, 10);
        //当前页
        int firstIndex = (page - 1) * pageView.getMaxresult();

        if (searchType.equals(1)) {//用户名
            if (keyword != null && !keyword.trim().isEmpty()) {
                if (userTypeCode.equals(10)) {//账号
                    User user = userRepository.findUserByAccount(keyword.trim());

                    if (user != null) {
                        QueryResult<User> qr = new QueryResult<User>();
                        List<User> userList = new ArrayList<User>();
                        userList.add(user);
                        qr.setResultlist(userList);
                        qr.setTotalrecord(1L);
                        pageView.setQueryResult(qr);
                    }
                } else if (userTypeCode.equals(20)) {//呢称
                    User user = userRepository.findUserByNickname(keyword.trim());

                    if (user != null) {
                        QueryResult<User> qr = new QueryResult<User>();
                        List<User> userList = new ArrayList<User>();
                        userList.add(user);
                        qr.setResultlist(userList);
                        qr.setTotalrecord(1L);
                        pageView.setQueryResult(qr);
                    }
                } else if (userTypeCode.equals(30)) {//用户名
                    User user = userRepository.findUserByUserName(keyword.trim());

                    if (user != null) {
                        QueryResult<User> qr = new QueryResult<User>();
                        List<User> userList = new ArrayList<User>();
                        userList.add(user);
                        qr.setResultlist(userList);
                        qr.setTotalrecord(1L);
                        pageView.setQueryResult(qr);
                    }
                } else if (userTypeCode.equals(40)) {//手机号

                    StringBuffer jpql = new StringBuffer("");
                    //存放参数值
                    List<Object> params = new ArrayList<Object>();

                    jpql.append(" and o.mobile=?" + (params.size() + 1));
                    params.add(keyword.trim());
                    //删除第一个and
                    String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

                    //排序
                    LinkedHashMap<String, String> orderby = new LinkedHashMap<String, String>();

                    orderby.put("id", "desc");//根据id字段降序排序
                    //调用分页算法类
                    QueryResult<User> qr = userRepository.getScrollData(User.class, firstIndex, pageView.getMaxresult(), _jpql, params.toArray(), orderby);

                    //将查询结果集传给分页List
                    pageView.setQueryResult(qr);

                } else if (userTypeCode.equals(50)) {//邮箱
                    StringBuffer jpql = new StringBuffer("");
                    //存放参数值
                    List<Object> params = new ArrayList<Object>();

                    jpql.append(" and o.email=?" + (params.size() + 1));
                    params.add(keyword.trim());
                    //删除第一个and
                    String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

                    //排序
                    LinkedHashMap<String, String> orderby = new LinkedHashMap<String, String>();

                    orderby.put("id", "desc");//根据id字段降序排序
                    //调用分页算法类
                    QueryResult<User> qr = userRepository.getScrollData(User.class, firstIndex, pageView.getMaxresult(), _jpql, params.toArray(), orderby);

                    //将查询结果集传给分页List
                    pageView.setQueryResult(qr);
                }
            }
        } else if (searchType.equals(2)) {//筛选条件
            //用户自定义注册功能项用户输入值集合
            List<UserInputValue> all_userInputValueList = new ArrayList<UserInputValue>();

            if (userCustomList != null && userCustomList.size() > 0) {
                for (UserCustom userCustom : userCustomList) {
                    all_userInputValueList.addAll(userCustom.getUserInputValueList());
                }
            }
            if (all_userInputValueList.size() > 0) {//含有自定义项
                String customParam = "";//自定义参数
                String param = "";//sql参数
                List<Object> paramValue = new ArrayList<Object>();//sql参数值
                Integer customParamGroupCount = 0;//用户自定义注册功能项参数组数量

                for (UserCustom userCustom : userCustomList) {
                    if (userCustom.getChooseType().equals(2)) {//单选按钮
                        List<UserInputValue> userInputValueList = userCustom.getUserInputValueList();
                        if (userInputValueList != null && userInputValueList.size() > 0) {
                            customParamGroupCount++;
                            customParam += " or ( u.userId =o.id and u.options in(";
                            String parameters = "";

                            for (UserInputValue userInputValue : userInputValueList) {
                                parameters += ",?";
                                paramValue.add(userInputValue.getOptions());

                            }
                            //删除第一个逗号
                            parameters = StringUtils.difference(",", parameters);
                            customParam += parameters;
                            customParam += "))";
                        }

                    } else if (userCustom.getChooseType().equals(3)) {//多选按钮
                        List<UserInputValue> userInputValueList = userCustom.getUserInputValueList();
                        if (userInputValueList != null && userInputValueList.size() > 0) {
                            customParamGroupCount++;
                            customParam += " or ( u.userId =o.id and u.options in(";
                            String parameters = "";

                            for (UserInputValue userInputValue : userInputValueList) {
                                parameters += ",?";
                                paramValue.add(userInputValue.getOptions());

                            }
                            //删除第一个逗号
                            parameters = StringUtils.difference(",", parameters);
                            customParam += parameters;
                            customParam += "))";
                        }
                    } else if (userCustom.getChooseType().equals(4)) {//下拉列表

                        List<UserInputValue> userInputValueList = userCustom.getUserInputValueList();
                        if (userInputValueList != null && userInputValueList.size() > 0) {
                            customParamGroupCount++;
                            customParam += " or ( u.userId =o.id and u.options in(";
                            String parameters = "";

                            for (UserInputValue userInputValue : userInputValueList) {
                                parameters += ",?";
                                paramValue.add(userInputValue.getOptions());

                            }
                            //删除第一个逗号
                            parameters = StringUtils.difference(",", parameters);
                            customParam += parameters;
                            customParam += "))";
                        }
                    }
                }

                if (pointQuery != null && pointQuery.startPoint != null) {//起始积分
                    param += " and o.point >= ? ";
                    paramValue.add(pointQuery.startPoint);
                }
                if (pointQuery != null && pointQuery.endPoint != null) {//结束积分
                    param += " and o.point <= ? ";
                    paramValue.add(pointQuery.endPoint);
                }

                if (registrationDateQuery != null && registrationDateQuery.startRegistrationDate != null) {//起始时间
                    param += " and o.registrationDate >= ? ";
                    paramValue.add(registrationDateQuery.startRegistrationDate);
                }
                if (registrationDateQuery != null && registrationDateQuery.endRegistrationDate != null) {//结束时间
                    param += " and o.registrationDate <= ? ";
                    paramValue.add(registrationDateQuery.endRegistrationDate);
                }
                paramValue.add(customParamGroupCount);
                //删除第一个or
                customParam = StringUtils.difference(" or", customParam);

                QueryResult<User> qr = userRepository.findUserByCustomCondition(param, paramValue, customParam, firstIndex, pageView.getMaxresult(), false);
                //将查询结果集传给分页List
                pageView.setQueryResult(qr);
            } else {
                String param = "";//sql参数
                List<Object> paramValue = new ArrayList<Object>();//sql参数值
                if (pointQuery != null && pointQuery.startPoint != null) {//起始积分
                    param += " and o.point >= ? ";
                    paramValue.add(pointQuery.startPoint);
                }
                if (pointQuery != null && pointQuery.endPoint != null) {//结束积分
                    param += " and o.point <= ? ";
                    paramValue.add(pointQuery.endPoint);
                }

                if (registrationDateQuery != null && registrationDateQuery.startRegistrationDate != null) {//起始时间
                    param += " and o.registrationDate >= ? ";
                    paramValue.add(registrationDateQuery.startRegistrationDate);
                }
                if (registrationDateQuery != null && registrationDateQuery.endRegistrationDate != null) {//结束时间
                    param += " and o.registrationDate <= ? ";
                    paramValue.add(registrationDateQuery.endRegistrationDate);
                }

                //删除第一个and
                param = StringUtils.difference(" and", param);
                QueryResult<User> qr = userRepository.findUserByCondition(param, paramValue, firstIndex, pageView.getMaxresult(), false);
                //将查询结果集传给分页List
                pageView.setQueryResult(qr);
            }
        }

        if (pageView.getRecords() != null && pageView.getRecords().size() > 0) {
            //仅显示指定字段
            List<User> userViewList = new ArrayList<User>();
            List<UserGrade> userGradeList = userGradeRepository.findAllGrade();
            for (User user : pageView.getRecords()) {//取得所有用户
                if (user.getType() > 10) {
                    user.setPlatformUserId(userComponent.platformUserIdToThirdPartyUserId(user.getPlatformUserId()));
                }

                if (userGradeList != null && userGradeList.size() > 0) {
                    for (UserGrade userGrade : userGradeList) {//取得所有等级
                        if (user.getPoint() >= userGrade.getNeedPoint()) {
                            user.setGradeName(userGrade.getName());//将等级值设进等级参数里
                            break;
                        }
                    }
                }
                if (user.getAvatarPath() != null && !"".contentEquals(user.getAvatarPath().trim())) {
                    user.setAvatarPath(fileServerAddress + user.getAvatarPath());
                }
                User userView = new User();

                userView.setId(user.getId());
                userView.setUserName(user.getUserName());
                userView.setAccount(user.getAccount());
                userView.setNickname(user.getNickname());
                userView.setCancelAccountTime(user.getCancelAccountTime());
                userView.setAllowUserDynamic(user.getAllowUserDynamic());
                userView.setEmail(user.getEmail());
                userView.setIssue(user.getIssue());
                userView.setMobile(user.getMobile());
                userView.setId(user.getId());
                userView.setRealNameAuthentication(user.isRealNameAuthentication());
                userView.setRegistrationDate(user.getRegistrationDate());
                userView.setRemarks(user.getRemarks());
                userView.setPoint(user.getPoint());
                userView.setDeposit(user.getDeposit());
                userView.setType(user.getType());
                userView.setPlatformUserId(user.getPlatformUserId());
                userView.setState(user.getState());
                userView.setUserVersion(user.getUserVersion());
                userView.setUserRoleNameList(user.getUserRoleNameList());
                userView.setGradeId(user.getGradeId());
                userView.setGradeName(user.getGradeName());
                userView.setAvatarPath(user.getAvatarPath());
                userView.setAvatarName(user.getAvatarName());
                userViewList.add(userView);
            }
            pageView.setRecords(userViewList);
        }

        returnValue.put("userCustomList", userCustomList);
        returnValue.put("pageView", pageView);


        return returnValue;
    }



    /**
     * 查询用户
     *
     * @param keyword           关键字
     * @param fileServerAddress 文件服务器随机地址
     */
    public User queryUser(String keyword, String fileServerAddress) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new BusinessException(Map.of("user", "查询关键字不能为空"));
        }

        User u1 = userRepository.findUserByAccount(keyword.trim());
        if (u1 != null && u1.getState().equals(1) && u1.getCancelAccountTime().equals(-1L)) {


            //仅显示指定的字段
            User viewUser = new User();
            viewUser.setId(u1.getId());//Id
            viewUser.setUserName(u1.getUserName());//会员用户名
            viewUser.setAccount(u1.getAccount());//账号
            viewUser.setNickname(u1.getNickname());//呢称
            viewUser.setState(u1.getState());//用户状态
            viewUser.setPoint(u1.getPoint());//当前积分
            viewUser.setGradeId(u1.getGradeId());//等级Id
            viewUser.setGradeName(u1.getGradeName());//等级名称
            viewUser.setAvatarPath(fileServerAddress + u1.getAvatarPath());//头像路径
            viewUser.setAvatarName(u1.getAvatarName());//头像名称
            return viewUser;
        } else {
            User u2 = userRepository.findUserByNickname(keyword.trim());
            if (u2 != null && u2.getState().equals(1) && u2.getCancelAccountTime().equals(-1L)) {
                //仅显示指定的字段
                User viewUser = new User();
                viewUser.setId(u2.getId());//Id
                viewUser.setUserName(u2.getUserName());//会员用户名
                viewUser.setAccount(u2.getAccount());//账号
                viewUser.setNickname(u2.getNickname());//呢称
                viewUser.setState(u2.getState());//用户状态
                viewUser.setPoint(u2.getPoint());//当前积分
                viewUser.setGradeId(u2.getGradeId());//等级Id
                viewUser.setGradeName(u2.getGradeName());//等级名称
                viewUser.setAvatarPath(fileServerAddress + u2.getAvatarPath());//头像路径
                viewUser.setAvatarName(u2.getAvatarName());//头像名称
                return viewUser;
            }
        }
        return null;
    }

    /**
     * 获取用户明细
     *
     * @param id                用户Id
     * @param fileServerAddress 文件服务器随机地址
     */
    public Map<String, Object> getUserDetails(Long id, String fileServerAddress) {
        if (id == null) {
            throw new BusinessException(Map.of("id", "用户Id不存在"));
        }
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new BusinessException(Map.of("id", "用户不存在"));
        }
        Map<String, Object> returnValue = new HashMap<String, Object>();
        //仅显示指定的字段
        User viewUser = new User();

        viewUser.setId(user.getId());//Id
        viewUser.setUserName(user.getUserName());//会员用户名
        viewUser.setAccount(user.getAccount());//账号
        viewUser.setCancelAccountTime(user.getCancelAccountTime());//注销账号时间
        viewUser.setNickname(user.getNickname());//呢称
        viewUser.setAllowUserDynamic(user.getAllowUserDynamic());//是否允许显示用户动态
        viewUser.setEmail(user.getEmail());//邮箱地址
        viewUser.setIssue(user.getIssue());//密码提示问题
        viewUser.setMobile(user.getMobile());//实名认证绑定手机
        viewUser.setRealNameAuthentication(user.isRealNameAuthentication());//是否实名认证
        viewUser.setRegistrationDate(user.getRegistrationDate());//注册日期
        viewUser.setRemarks(user.getRemarks());//备注
        viewUser.setPoint(user.getPoint());//当前积分
        viewUser.setDeposit(user.getDeposit());//当前预存款
        viewUser.setType(user.getType());//用户类型
        viewUser.setPlatformUserId(user.getPlatformUserId());//平台用户Id
        viewUser.setState(user.getState());//用户状态
        viewUser.setUserVersion(user.getUserVersion());//用户版本号
        viewUser.setGradeId(user.getGradeId());//等级Id
        viewUser.setAvatarPath(fileServerAddress + user.getAvatarPath());//头像路径
        viewUser.setAvatarName(user.getAvatarName());//头像名称

        if (user.getType() > 10) {
            user.setPlatformUserId(userComponent.platformUserIdToThirdPartyUserId(user.getPlatformUserId()));
        }


        List<UserGrade> userGradeList = userGradeRepository.findAllGrade();
        if (userGradeList != null && !userGradeList.isEmpty()) {
            for (UserGrade userGrade : userGradeList) {//取得所有等级
                if (user.getPoint() >= userGrade.getNeedPoint()) {
                    user.setGradeName(userGrade.getName());//将等级值设进等级参数里
                    break;
                }
            }
        }

        //有效的用户角色
        List<UserRole> validUserRoleList = new ArrayList<UserRole>();

        //查询所有角色
        List<UserRole> userRoleList = userRoleRepository.findAllRole();
        if (userRoleList != null && !userRoleList.isEmpty()) {
            List<UserRoleGroup> userRoleGroupList = userRoleRepository.findRoleGroupByUserName(user.getUserName());


            for (UserRole userRole : userRoleList) {
                if (userRole.getDefaultRole()) {//如果是默认角色
                    continue;
                } else {
                    //默认时间  年,月,日,时,分,秒,毫秒
                    LocalDateTime defaultTime = LocalDateTime.of(2999, 1, 1, 0, 0);// 2999年1月1日0点0分
                    userRole.setValidPeriodEnd(defaultTime);
                }

                if (userRoleGroupList != null && !userRoleGroupList.isEmpty()) {
                    for (UserRoleGroup userRoleGroup : userRoleGroupList) {
                        if (userRole.getId().equals(userRoleGroup.getUserRoleId())) {
                            UserRole validUserRole = new UserRole();
                            validUserRole.setId(userRole.getId());
                            validUserRole.setName(userRole.getName());
                            validUserRole.setValidPeriodEnd(userRoleGroup.getValidPeriodEnd());
                            validUserRoleList.add(validUserRole);
                        }
                    }
                }
            }
        }


        List<UserCustom> userCustomList = userCustomRepository.findAllUserCustom();
        if (userCustomList != null && !userCustomList.isEmpty()) {
            Iterator<UserCustom> it = userCustomList.iterator();
            while (it.hasNext()) {
                UserCustom userCustom = it.next();
                if (!userCustom.isVisible()) {//如果不显示
                    it.remove();
                    continue;
                }
                if (userCustom.getValue() != null && !userCustom.getValue().trim().isEmpty()) {
                    LinkedHashMap<String, String> itemValue = jsonComponent.toGenericObject(userCustom.getValue(), new TypeReference<LinkedHashMap<String, String>>() {
                    });
                    userCustom.setItemValue(itemValue);
                }

            }

            List<UserInputValue> userInputValueList = userCustomRepository.findUserInputValueByUserName(user.getId());
            if (userInputValueList != null && !userInputValueList.isEmpty()) {
                for (UserCustom userCustom : userCustomList) {
                    for (UserInputValue userInputValue : userInputValueList) {
                        if (userCustom.getId().equals(userInputValue.getUserCustomId())) {
                            userCustom.addUserInputValue(userInputValue);
                        }
                    }
                }
            }
        }



        returnValue.put("userRoleList", validUserRoleList);
        returnValue.put("userCustomList", userCustomList);
        returnValue.put("user", viewUser);
        return returnValue;
    }


    /**
     * 获取添加用户界面信息
     *
     * @return
     */
    public Map<String, Object> getAddUserViewModel() {
        Map<String, Object> returnValue = new HashMap<String, Object>();

        List<UserCustom> userCustomList = userCustomRepository.findAllUserCustom();
        if (userCustomList != null && !userCustomList.isEmpty()) {
            Iterator<UserCustom> it = userCustomList.iterator();
            while (it.hasNext()) {
                UserCustom userCustom = it.next();
                if (!userCustom.isVisible()) {//如果不显示
                    it.remove();
                    continue;
                }
                if (userCustom.getValue() != null && !userCustom.getValue().trim().isEmpty()) {
                    LinkedHashMap<String, String> itemValue = jsonComponent.toGenericObject(userCustom.getValue(), new TypeReference<LinkedHashMap<String, String>>() {
                    });
                    userCustom.setItemValue(itemValue);
                }

            }
        }


        //查询所有角色
        List<UserRole> userRoleList = userRoleRepository.findAllRole();
        if (userRoleList != null && !userRoleList.isEmpty()) {
            for (UserRole userRole : userRoleList) {
                if (userRole.getDefaultRole()) {//如果是默认角色
                    userRole.setSelected(true);
                } else {
                    //默认时间  年,月,日,时,分,秒,毫秒
                    LocalDateTime defaultTime = LocalDateTime.of(2999, 1, 1, 0, 0);// 2999年1月1日0点0分
                    userRole.setValidPeriodEnd(defaultTime);
                }
            }
        }

        returnValue.put("userCustomList", userCustomList);
        returnValue.put("userRoleList", userRoleList);
        return returnValue;
    }

    /**
     * 添加用户
     * @param userForm    用户表单
     * @param userRolesId 角色Id
     * @param request     请求信息
     */
    public void addUser(User userForm, String[] userRolesId, HttpServletRequest request) {
        //用户自定义注册功能项参数
        List<UserCustom> userCustomList = userCustomRepository.findAllUserCustom();

        Map<String, String> errors = new HashMap<String, String>();
        processAndValidateCustomFields(userCustomList, request, errors);

        List<UserRoleGroup> userRoleGroupList = new ArrayList<UserRoleGroup>();
        List<UserRole> userRoleList = userRoleRepository.findAllRole();
        processAndValidateUserRoles(userRolesId, userRoleList,userRoleGroupList, request, errors);

        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        User user = new User();

        if(userForm.getType().equals(10)){//10:本地账号密码用户
            user.setAccount(userForm.getAccount().trim());
            user.setUserName(UUIDUtil.getUUID22());
            user.setIssue(userForm.getIssue().trim());
            //密码提示答案由  密码提示答案原文sha256  进行sha256组成
            user.setAnswer(SHA.sha256Hex(SHA.sha256Hex(userForm.getAnswer().trim())));
            user.setPlatformUserId(user.getUserName());
        }else if(userForm.getType().equals(20)){//20: 手机用户
            String id = UUIDUtil.getUUID22();
            user.setUserName(id);//会员用户名
            user.setAccount(userComponent.queryUserIdentifier(20)+"-"+id);//用户名和账号可以用不相同的UUID
            user.setPlatformUserId(userComponent.thirdPartyUserIdToPlatformUserId(userForm.getMobile().trim(),20));
        }else if(userForm.getType().equals(30)){//30: 邮箱用户
            String id = UUIDUtil.getUUID22();
            user.setUserName(id);//会员用户名
            user.setAccount(userComponent.queryUserIdentifier(30)+"-"+id);//用户名和账号可以用不相同的UUID
            user.setPlatformUserId(userComponent.thirdPartyUserIdToPlatformUserId(userForm.getEmail().trim(),30));
        }



        user.setSalt(UUIDUtil.getUUID32());

        if(userForm.getNickname() != null && !userForm.getNickname().trim().isEmpty()){
            user.setNickname(userForm.getNickname().trim());
        }

        //密码
        user.setPassword(SHA.sha256Hex(SHA.sha256Hex(userForm.getPassword().trim())+"["+user.getSalt()+"]"));
        user.setEmail(userForm.getEmail().trim());


        user.setRegistrationDate(LocalDateTime.now());
        user.setRemarks(userForm.getRemarks());
        user.setState(userForm.getState());

        user.setMobile(userForm.getMobile().trim());
        user.setRealNameAuthentication(userForm.isRealNameAuthentication());
        //允许显示用户动态
        user.setAllowUserDynamic(userForm.getAllowUserDynamic());
        user.setSecurityDigest(new Date().getTime());
        user.setType(userForm.getType());

        //用户自定义注册功能项用户输入值集合
        List<UserInputValue> all_userInputValueList = new ArrayList<UserInputValue>();

        if(userCustomList != null && !userCustomList.isEmpty()){
            for(UserCustom userCustom : userCustomList){
                all_userInputValueList.addAll(userCustom.getUserInputValueList());
            }
        }

        if(!userRoleGroupList.isEmpty()){
            for(UserRoleGroup userRoleGroup : userRoleGroupList){
                userRoleGroup.setUserName(user.getUserName());
            }
        }


        try {
            userRepository.saveUser(user,all_userInputValueList,userRoleGroupList);
        } catch (Exception e) {
            throw new BusinessException(Map.of("user", "添加用户错误"));
            //	e.printStackTrace();
        }
        //删除缓存
        if(user.getId() != null){
            userCacheManager.delete_cache_findUserById(user.getId());
        }
        userCacheManager.delete_cache_findUserByUserName(user.getUserName());
        userRoleCacheManager.delete_cache_findRoleGroupByUserName(user.getUserName());

    }


    /**
     * 获取修改用户界面信息
     * @param id 用户Id
     * @return
     */
    public Map<String, Object> getEditUserViewModel(Long id) {
        if (id == null) {
            throw new BusinessException(Map.of("id", "用户Id不存在"));
        }
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new BusinessException(Map.of("id", "用户不存在"));
        }
        Map<String, Object> returnValue = new HashMap<String, Object>();


        User userView = new User();

        userView.setId(user.getId());
        userView.setUserName(user.getUserName());
        userView.setAccount(user.getAccount());
        userView.setNickname(user.getNickname());
        userView.setCancelAccountTime(user.getCancelAccountTime());
        userView.setAllowUserDynamic(user.getAllowUserDynamic());
        userView.setEmail(user.getEmail());
        userView.setIssue(user.getIssue());
        userView.setMobile(user.getMobile());
        userView.setId(user.getId());
        userView.setRealNameAuthentication(user.isRealNameAuthentication());
        userView.setRegistrationDate(user.getRegistrationDate());
        userView.setRemarks(user.getRemarks());
        userView.setPoint(user.getPoint());
        userView.setDeposit(user.getDeposit());
        userView.setType(user.getType());
        userView.setPlatformUserId(user.getPlatformUserId());
        userView.setState(user.getState());
        userView.setUserVersion(user.getUserVersion());
        userView.setUserRoleNameList(user.getUserRoleNameList());
        userView.setGradeId(user.getGradeId());
        userView.setGradeName(user.getGradeName());
        //userView.setAvatarPath(fileServerAddress + user.getAvatarPath());
        userView.setAvatarName(user.getAvatarName());


        List<UserCustom> userCustomList = userCustomRepository.findAllUserCustom();
        if(userCustomList != null && !userCustomList.isEmpty()){
            Iterator <UserCustom> it = userCustomList.iterator();
            while(it.hasNext()){
                UserCustom userCustom = it.next();
                if(!userCustom.isVisible()){//如果不显示
                    it.remove();
                    continue;
                }
                if(userCustom.getValue() != null && !userCustom.getValue().trim().isEmpty()){
                    LinkedHashMap<String,String> itemValue = jsonComponent.toGenericObject(userCustom.getValue(), new TypeReference<LinkedHashMap<String,String>>(){});
                    userCustom.setItemValue(itemValue);
                }

            }
            List<UserInputValue> userInputValueList= userCustomRepository.findUserInputValueByUserName(user.getId());
            if(userInputValueList != null && !userInputValueList.isEmpty()){
                for(UserCustom userCustom : userCustomList){
                    for(UserInputValue userInputValue : userInputValueList){
                        if(userCustom.getId().equals(userInputValue.getUserCustomId())){
                            userCustom.addUserInputValue(userInputValue);
                        }
                    }
                }
            }
        }


        //查询所有角色
        List<UserRole> userRoleList = userRoleRepository.findAllRole();
        if(userRoleList != null && !userRoleList.isEmpty()){
            List<UserRoleGroup> userRoleGroupList = userRoleRepository.findRoleGroupByUserName(user.getUserName());


            for(UserRole userRole : userRoleList){
                if(userRole.getDefaultRole()){//如果是默认角色
                    userRole.setSelected(true);
                }else{

                    //默认时间  年,月,日,时,分,秒,毫秒
                    LocalDateTime defaultTime = LocalDateTime.of(2999, 1, 1, 0, 0);// 2999年1月1日0点0分
                    userRole.setValidPeriodEnd(defaultTime);
                }

                if(userRoleGroupList != null && !userRoleGroupList.isEmpty()){
                    for(UserRoleGroup userRoleGroup : userRoleGroupList){
                        if(userRole.getId().equals(userRoleGroup.getUserRoleId())){
                            userRole.setSelected(true);
                            userRole.setValidPeriodEnd(userRoleGroup.getValidPeriodEnd());
                        }
                    }
                }
            }
        }
        returnValue.put("userRoleList", userRoleList);
        returnValue.put("userCustomList", userCustomList);
        returnValue.put("user",userView);

        return returnValue;
    }

    /**
     * 修改用户
     * @param userForm    用户表单
     * @param userRolesId 角色Id
     * @param request     请求信息
     */
    public void editUser(User userForm, String[] userRolesId, HttpServletRequest request){
        if (userForm.getId() == null) {
            throw new BusinessException(Map.of("id", "用户Id不存在"));
        }
        User oldUser = userRepository.findUserById(userForm.getId() );
        if (oldUser == null) {
            throw new BusinessException(Map.of("id", "用户不存在"));
        }
        if(!oldUser.getUserVersion().equals(userForm.getUserVersion())){
            throw new BusinessException(Map.of("user", "当前数据不是最新"));
        }
        Map<String, String> errors = new HashMap<String, String>();
        User newUser = new User();

        processAndValidateUserFields(userForm, newUser,oldUser,errors);

        List<UserCustom> userCustomList = userCustomRepository.findAllUserCustom();

        processAndValidateCustomFields(userCustomList, request, errors);

        List<UserRoleGroup> userRoleGroupList = new ArrayList<UserRoleGroup>();
        List<UserRole> userRoleList = userRoleRepository.findAllRole();
        processAndValidateUserRoles(userRolesId, userRoleList,userRoleGroupList, request, errors);

        for(UserRoleGroup userRoleGroup: userRoleGroupList){
            userRoleGroup.setUserName(oldUser.getUserName());
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        newUser.setId(oldUser.getId());
        newUser.setUserName(oldUser.getUserName());
        //备注
        newUser.setRemarks(userForm.getRemarks());
        newUser.setUserVersion(userForm.getUserVersion());


        List<UserInputValue> userInputValueList= userCustomRepository.findUserInputValueByUserName(oldUser.getId());

        //添加注册功能项用户输入值集合
        List<UserInputValue> add_userInputValue = new ArrayList<UserInputValue>();
        //删除注册功能项用户输入值Id集合
        List<Long> delete_userInputValueIdList = new ArrayList<Long>();
        if(userCustomList != null && !userCustomList.isEmpty()){
            for(UserCustom userCustom : userCustomList){
                List<UserInputValue> new_userInputValueList = userCustom.getUserInputValueList();
                if(new_userInputValueList != null && !new_userInputValueList.isEmpty()){
                    A:for(UserInputValue new_userInputValue : new_userInputValueList){
                        if(userInputValueList != null && !userInputValueList.isEmpty()){
                            for(UserInputValue old_userInputValue : userInputValueList){
                                if(new_userInputValue.getUserCustomId().equals(old_userInputValue.getUserCustomId())){
                                    if(new_userInputValue.getOptions().equals("-1")){

                                        if(new_userInputValue.getContent() == null){
                                            if(old_userInputValue.getContent() == null){
                                                userInputValueList.remove(old_userInputValue);
                                                continue A;
                                            }
                                        }else{
                                            if(new_userInputValue.getContent().equals(old_userInputValue.getContent())){
                                                userInputValueList.remove(old_userInputValue);
                                                continue A;
                                            }
                                        }

                                    }else{
                                        if(new_userInputValue.getOptions().equals(old_userInputValue.getOptions())){
                                            userInputValueList.remove(old_userInputValue);
                                            continue A;
                                        }
                                    }
                                }
                            }
                        }
                        add_userInputValue.add(new_userInputValue);
                    }
                }
            }
        }
        if(userInputValueList != null && !userInputValueList.isEmpty()){
            for(UserInputValue old_userInputValue : userInputValueList){
                delete_userInputValueIdList.add(old_userInputValue.getId());
            }
        }

        userRepository.updateUser(newUser,add_userInputValue,delete_userInputValueIdList,userRoleGroupList);

        userCacheManager.delete_userState(newUser.getUserName());

        //删除缓存
        userCacheManager.delete_cache_findUserById(oldUser.getId());
        userCacheManager.delete_cache_findUserByUserName(oldUser.getUserName());
        userRoleCacheManager.delete_cache_findRoleGroupByUserName(oldUser.getUserName());
    }

    /**
     * 删除用户
     * @param userId 用户Id集合
     */
    public void deleteUser(Long[] userId){
        if(userId == null || userId.length ==0){
            throw new BusinessException(Map.of("userId", "请选择用户"));
        }

        List<Long> idList = Arrays.stream(userId)
                .filter(Objects::nonNull) // 过滤掉所有 null 元素
                .toList();

        if (idList.isEmpty()) {
            throw new BusinessException(Map.of("userId", "用户Id不能为空"));
        }

        List<User> userList = userRepository.findUserByUserIdList(idList);
        if(userList != null && !userList.isEmpty()){
            List<Long> softDelete_userIdList = new ArrayList<Long>();//逻辑删除用户Id集合
            List<User> softDelete_userList = new ArrayList<User>();//逻辑删除用户集合
            List<Long> physicalDelete_userIdList = new ArrayList<Long>();//物理删除用户Id集合
            List<String> physicalDelete_userNameList = new ArrayList<String>();//物理删除用户名称集合
            List<User> physicalDelete_userList = new ArrayList<User>();//物理删除用户集合

            for(User user : userList){
                if(user.getState() <10){
                    softDelete_userIdList.add(user.getId());
                    softDelete_userList.add(user);
                }else{
                    physicalDelete_userIdList.add(user.getId());
                    physicalDelete_userNameList.add(user.getUserName());
                    physicalDelete_userList.add(user);
                }
            }


            if(!softDelete_userIdList.isEmpty()){//逻辑删除
                int i = userRepository.markDelete(softDelete_userIdList);
                //删除缓存用户状态
                for(User user : softDelete_userList){
                    userCacheManager.delete_userState(user.getUserName());
                    //删除缓存
                    userCacheManager.delete_cache_findUserById(user.getId());
                    userCacheManager.delete_cache_findUserByUserName(user.getUserName());
                    userRoleCacheManager.delete_cache_findRoleGroupByUserName(user.getUserName());
                }
            }
            if(!physicalDelete_userNameList.isEmpty()){//物理删除
                for(User user : physicalDelete_userList){
                    //删除用户话题文件
                    topicComponent.deleteTopicFile(user.getUserName(), false);

                    //删除评论文件
                    topicComponent.deleteCommentFile(user.getUserName(), false);

                    //删除用户问题文件
                    questionComponent.deleteQuestionFile(user.getUserName(), false);

                    //删除答案文件
                    questionComponent.deleteAnswerFile(user.getUserName(), false);

                    if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                        String date = user.getRegistrationDate().format(formatter);
                        String pathFile = "file"+ File.separator+"avatar"+File.separator + date +File.separator  +user.getAvatarName();
                        //删除头像
                        fileComponent.deleteFile(pathFile);

                        String pathFile_100 = "file"+File.separator+"avatar"+File.separator + date +File.separator +"100x100" +File.separator+user.getAvatarName();
                        //删除头像100*100
                        fileComponent.deleteFile(pathFile_100);
                    }
                }



                int i = userRepository.delete(physicalDelete_userIdList,physicalDelete_userNameList);

                for(User user : physicalDelete_userList){
                    //添加删除索引标记
                    topicIndexRepository.addTopicIndex(new TopicIndex(user.getUserName(),4));
                    questionIndexRepository.addQuestionIndex(new QuestionIndex(user.getUserName(),4));

                    //删除缓存用户状态
                    userCacheManager.delete_userState(user.getUserName());
                    //删除缓存
                    userCacheManager.delete_cache_findUserById(user.getId());
                    userCacheManager.delete_cache_findUserByUserName(user.getUserName());
                    userRoleCacheManager.delete_cache_findRoleGroupByUserName(user.getUserName());

                }

            }

        }

    }

    /**
     * 还原用户
     * @param userId 用户Id集合
     */
    public void reductionUser(Long[] userId){
        if(userId == null || userId.length ==0){
            throw new BusinessException(Map.of("userId", "请选择用户"));
        }
        List<Long> idList = Arrays.stream(userId)
                .filter(Objects::nonNull) // 过滤掉所有 null 元素
                .toList();

        if (idList.isEmpty()) {
            throw new BusinessException(Map.of("userId", "用户Id不能为空"));
        }
        List<User> userList = userRepository.findUserByUserIdList(idList);
        if(userList == null || userList.isEmpty()){
            throw new BusinessException(Map.of("userId", "用户不存在"));
        }

        for(User user :userList){
            if(user.getState().equals(11)){ //1:正常用户   2:禁止用户   11: 正常用户删除   12: 禁止用户删除
                user.setState(1);
            }else if(user.getState().equals(12)){
                user.setState(2);
            }

        }
        userRepository.reductionUser(userList);

        //删除缓存用户状态
        for(User user :userList){
            userCacheManager.delete_userState(user.getUserName());
            //删除缓存
            userCacheManager.delete_cache_findUserById(user.getId());
            userCacheManager.delete_cache_findUserByUserName(user.getUserName());
            userRoleCacheManager.delete_cache_findRoleGroupByUserName(user.getUserName());
        }

    }

    /**
     * 更新头像
     * @param file 文件
     * @param id 用户Id
     * @param width 图片宽
     * @param height 图片高
     * @param x X轴
     * @param y Y轴
     */
    public  void updateAvatar(MultipartFile file,Integer width, Integer height, Integer x, Integer y, Long id){
        Map<String,String> errors = new HashMap<String,String>();//错误
        if(id ==null){
            throw new BusinessException(Map.of("userId", "用户Id不能为空"));
        }
        User user = userRepository.findUserById(id);
        if(user == null){
            throw new BusinessException(Map.of("userId", "用户不存在"));
        }
        if(file ==null || file.isEmpty()){
            throw new BusinessException(Map.of("file", "文件不能为空"));
        }


        // 文件大小和类型验证
        long imageSize = 5 * 1024L; // 5MB
        if (file.getSize() / 1024 > imageSize) {
            throw new BusinessException(Map.of("file", "文件超出允许上传大小"));
        }

        List<String> allowedFormats = Arrays.asList("gif", "jpg", "jpeg", "bmp", "png","webp");
        String originalFileName = file.getOriginalFilename();
        if (!FileUtil.validateFileSuffix(originalFileName, allowedFormats)) {
            throw new BusinessException(Map.of("file", "当前文件类型不允许上传"));
        }

        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("读取上传文件字节失败", e);
            }
            throw new BusinessException(Map.of("file", "文件读取失败"));
        }

        int srcWidth;
        int srcHeight;
        int maxWidth = 200;
        int maxHeight = 200;
        try (InputStream is = new ByteArrayInputStream(fileBytes)) {
            BufferedImage bufferImage = ImageIO.read(is);
            if (bufferImage == null) {
                throw new BusinessException(Map.of("file", "无法读取图片文件"));
            }

            srcWidth = bufferImage.getWidth();
            srcHeight = bufferImage.getHeight();


            // 图像尺寸验证
            if ("blob".equalsIgnoreCase(originalFileName)) {
                if (srcWidth > maxWidth || srcHeight > maxHeight) {
                    throw new BusinessException(Map.of("file", "超出最大尺寸 (" + maxWidth + "x" + maxHeight + ")"));
                }
            }
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("解析图片尺寸失败", e);
            }
            throw new BusinessException(Map.of("file", "图片处理失败"));
        }
        // 裁剪坐标验证
        if (width != null && (width <= 0 || width.toString().length() >= 8))
            errors.put("width", "宽度必须大于0且少于8位");
        if (height != null && (height <= 0 || height.toString().length() >= 8))
            errors.put("height", "高度必须大于0且少于8位");
        if (x != null && (x < 0 || x.toString().length() >= 8)) errors.put("x", "X轴必须大于或等于0且少于8位");
        if (y != null && (y < 0 || y.toString().length() >= 8)) errors.put("y", "Y轴必须大于或等于0且少于8位");

        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        //保存文件并生成缩略图
        String suffix = FileUtil.getExtension(originalFileName).toLowerCase();
        // 如果是 blob，默认使用 png
        if (suffix.isEmpty() || "blob".equalsIgnoreCase(suffix)) {
            suffix = "png";
        }
        String newFileName = UUIDUtil.getUUID32() + "." + suffix;
        String date = user.getRegistrationDate().format(formatter);
        String pathDir = "file"+File.separator+"avatar"+File.separator + date +File.separator ;
        String pathDir_100 = "file"+File.separator+"avatar"+File.separator + date +File.separator +"100x100" +File.separator;

        fileComponent.createFolder(pathDir);
        fileComponent.createFolder(pathDir_100);

        // 原图在 200x200 范围内
        if (srcWidth <= 200 && srcHeight <= 200) {
            // 保存原图到 pathDir (作为 200x200 级别的备份)
            fileComponent.writeFile(pathDir, newFileName, fileBytes);

            //判断是否需要缩小到 100x100
            if (srcWidth <= 100 && srcHeight <= 100) {
                // 图片很小，直接拷贝字节
                fileComponent.writeFile(pathDir_100, newFileName, fileBytes);
            } else {
                // 图片在 100-200 之间，等比例缩放生成 100x100 缩略图
                fileComponent.createImage(new ByteArrayInputStream(fileBytes), pathDir_100 + newFileName, suffix, 100, 100);
            }
        }else {//原图大于 200x200，需要裁剪并生成两套缩略图

            if (x != null && y != null && width != null && height != null) {
                // 根据裁剪坐标生成 200x200 缩略图
                fileComponent.createImage(new ByteArrayInputStream(fileBytes), pathDir + newFileName, suffix, x, y, width, height, 200, 200);
                // 根据裁剪坐标生成 100x100 缩略图
                fileComponent.createImage(new ByteArrayInputStream(fileBytes), pathDir_100 + newFileName, suffix, x, y, width, height, 100, 100);
            } else {
                // 如果没有裁剪参数
                fileComponent.writeFile(pathDir, newFileName, fileBytes);// 保存原始文件
                fileComponent.createImage(new ByteArrayInputStream(fileBytes), pathDir_100 + newFileName, suffix, 100, 100);
            }
        }

        userRepository.updateUserAvatar(user.getUserName(), newFileName);
        //删除缓存
        userCacheManager.delete_cache_findUserById(user.getId());
        userCacheManager.delete_cache_findUserByUserName(user.getUserName());

        //删除旧头像
        if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
            String oldPathFile = pathDir + user.getAvatarName();
            //删除旧头像
            fileComponent.deleteFile(oldPathFile);
            String oldPathFile_100 = pathDir_100 + user.getAvatarName();
            //删除旧头像100*100
            fileComponent.deleteFile(oldPathFile_100);
        }
    }

    /**
     * 付款
     * @param paymentRequest 付款表单
     */
    public void payment(PaymentRequest paymentRequest){
        if(paymentRequest.getId() ==null){
            throw new BusinessException(Map.of("userId", "用户Id不能为空"));
        }
        User user = userRepository.findUserById(paymentRequest.getId());
        if(user == null){
            throw new BusinessException(Map.of("userId", "用户不存在"));
        }

        if(!paymentType.contains(paymentRequest.getType())){
            throw new BusinessException(Map.of("type", "支付类型错误"));
        }


        String staffName = "";//员工名称
        Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(obj instanceof UserDetails){
            staffName =((UserDetails)obj).getUsername();
        }
        Map<String, String> errors = new HashMap<String, String>();
        if(paymentRequest.getType().equals(1)){//1:支付流水号

            if(paymentRequest.getPaymentRunningNumberAmount() == null){
                errors.put("paymentRunningNumberAmount", "请填写金额");
            }else{
                if(!Verification.hasAtMostTwoDecimalPlaces(paymentRequest.getPaymentRunningNumberAmount())){
                    errors.put("paymentRunningNumberAmount", "最多只能有两位小数");
                }
                if(paymentRequest.getPaymentRunningNumberAmount().compareTo(new BigDecimal("0")) <=0){
                    errors.put("paymentRunningNumberAmount", "金额必须大于0");
                }
                if(paymentRequest.getPaymentRunningNumberAmount().compareTo(new BigDecimal("999999999999")) >0){
                    errors.put("paymentRunningNumberAmount", "金额不能超过12位数字");
                }
            }

            if(paymentRequest.getPaymentRunningNumber() == null || paymentRequest.getPaymentRunningNumber().trim().isEmpty()){
                errors.put("paymentRunningNumber", "流水号不能为空");
            }else{
                if(paymentRequest.getPaymentRunningNumber().trim().length() >64){
                    errors.put("paymentRunningNumber", "流水号不能超过64位");
                }
            }
            if (!errors.isEmpty()) {
                throw new BusinessException(errors);
            }


            PaymentVerificationLog paymentVerificationLog = paymentRepository.findPaymentVerificationLogById(paymentRequest.getPaymentRunningNumber().trim());
            if(paymentVerificationLog == null) {
                throw new BusinessException(Map.of("paymentRunningNumber", "流水号不存在"));
            }
            if(!paymentVerificationLog.getPaymentModule().equals(5)) {//5.用户充值
                throw new BusinessException(Map.of("paymentRunningNumber", "流水号不属于充值模块"));
            }
            if(!user.getUserName().equals(paymentVerificationLog.getUserName())) {
                throw new BusinessException(Map.of("paymentRunningNumber", "流水号不属于当前用户"));
            }



            PaymentLog paymentLog = new PaymentLog();
            paymentLog.setPaymentRunningNumber(paymentVerificationLog.getId());//支付流水号
            paymentLog.setPaymentModule(paymentVerificationLog.getPaymentModule());//支付模块 5.用户充值
            paymentLog.setSourceParameterId(String.valueOf(paymentVerificationLog.getParameterId()));//参数Id
            paymentLog.setOperationUserType(1);//操作用户类型  0:系统  1: 员工  2:会员
            paymentLog.setOperationUserName(staffName);

            paymentLog.setAmount(paymentRequest.getPaymentRunningNumberAmount());//金额
            paymentLog.setInterfaceProduct(-1);//接口产品
            paymentLog.setTradeNo("");//交易号
            paymentLog.setUserName(paymentVerificationLog.getUserName());//用户名称
            paymentLog.setRemark(paymentRequest.getPaymentRunningNumber_remark());//备注
            paymentLog.setAmountState(1);//金额状态  1:账户存入  2:账户支出
            Object new_paymentLog = paymentComponent.createPaymentLogObject(paymentLog);

            userRepository.onlineRecharge(paymentRequest.getPaymentRunningNumber().trim(),paymentVerificationLog.getUserName(),paymentRequest.getPaymentRunningNumberAmount(),new_paymentLog);

            //删除缓存
            userCacheManager.delete_cache_findUserById(user.getId());
            userCacheManager.delete_cache_findUserByUserName(user.getUserName());

        }else if(paymentRequest.getType().equals(2)){//2:预存款

            if(paymentRequest.getDeposit() == null){
                errors.put("deposit", "请填写金额");
            }else{
                if(!Verification.hasAtMostTwoDecimalPlaces(paymentRequest.getDeposit())){
                    errors.put("deposit", "最多只能有两位小数");
                }
                if(paymentRequest.getDeposit().compareTo(new BigDecimal("0")) <=0){
                    errors.put("deposit", "金额必须大于0");
                }
                if(paymentRequest.getDeposit().compareTo(new BigDecimal("999999999999")) >0){
                    errors.put("deposit", "金额不能超过12位数字");
                }
            }

            if (!errors.isEmpty()) {
                throw new BusinessException(errors);
            }

            String deposit_symbol ="-";//减少
            if("+".equals(paymentRequest.getDeposit_symbol())){//增加
                deposit_symbol = "+";
            }


            PaymentLog paymentLog = new PaymentLog();
            paymentLog.setPaymentRunningNumber(paymentComponent.createRunningNumber(user.getId()));//支付流水号
            paymentLog.setPaymentModule(5);//支付模块 5.用户充值
            paymentLog.setSourceParameterId(String.valueOf(user.getId()));//参数Id
            paymentLog.setOperationUserType(1);//操作用户类型  0:系统  1: 员工  2:会员
            paymentLog.setOperationUserName(staffName);

            paymentLog.setAmount(paymentRequest.getDeposit());//金额
            paymentLog.setInterfaceProduct(-1);//接口产品
            paymentLog.setTradeNo("");//交易号
            paymentLog.setUserName(user.getUserName());//用户名称
            paymentLog.setRemark(paymentRequest.getDeposit_remark());//备注

            if("+".equals(deposit_symbol)){//增加预存款
                paymentLog.setAmountState(1);//金额状态  1:账户存入  2:账户支出
                Object new_paymentLog = paymentComponent.createPaymentLogObject(paymentLog);
                userRepository.addUserDeposit(user.getUserName(),paymentRequest.getDeposit(),new_paymentLog);
            }else{//减少预存款
                paymentLog.setAmountState(2);//金额状态  1:账户存入  2:账户支出
                Object new_paymentLog = paymentComponent.createPaymentLogObject(paymentLog);
                userRepository.subtractUserDeposit(user.getUserName(),paymentRequest.getDeposit(),new_paymentLog);
            }
            //删除缓存
            userCacheManager.delete_cache_findUserById(user.getId());
            userCacheManager.delete_cache_findUserByUserName(user.getUserName());

        }else if(paymentRequest.getType().equals(3)){//3:积分
            if(paymentRequest.getPoint() == null){
                errors.put("point", "请填写正整数");
            }else{
                if(paymentRequest.getPoint() <0L){
                    errors.put("point", "不能小于0");
                }
                if(paymentRequest.getPoint() >9999999999L){
                    errors.put("point", "不能超过10位数");
                }
            }

            if (!errors.isEmpty()) {
                throw new BusinessException(errors);
            }

            String point_symbol ="-";//减少
            if("+".equals(paymentRequest.getPoint_symbol())){//增加
                point_symbol = "+";
            }

            PointLog pointLog = new PointLog();
            pointLog.setId(pointLogConfig.createPointLogId(user.getId()));
            pointLog.setModule(600);//模块  600.账户充值
            pointLog.setParameterId(user.getId());//参数Id
            pointLog.setOperationUserType(1);//操作用户类型  0:系统  1: 员工  2:会员
            pointLog.setOperationUserName(staffName);//操作用户名称

            pointLog.setPoint(paymentRequest.getPoint());//积分
            pointLog.setUserName(user.getUserName());//用户名称
            pointLog.setRemark(paymentRequest.getPoint_remark());


            if("+".equals(point_symbol)){//增加积分
                pointLog.setPointState(1);//积分状态 1:账户存入
                Object new_pointLog = pointComponent.createPointLogObject(pointLog);
                userRepository.addUserPoint(user.getUserName(),paymentRequest.getPoint(),new_pointLog);
            }else{//减少积分
                pointLog.setPointState(2);//积分状态  1:账户存入  2:账户支出
                Object new_pointLog = pointComponent.createPointLogObject(pointLog);
                userRepository.subtractUserPoint(user.getUserName(),paymentRequest.getPoint(),new_pointLog);
            }

            //删除缓存
            userCacheManager.delete_cache_findUserById(user.getId());
            userCacheManager.delete_cache_findUserByUserName(user.getUserName());

            //异步执行会员卡赠送任务(长期任务类型)
            membershipCardGiftTaskComponent.async_triggerMembershipCardGiftTask(user.getUserName());

        }
    }

    /**
     * 注销账号
     * @param id 用户Id
     */
    public void cancelAccount(Long id){
        if(id ==null){
            throw new BusinessException(Map.of("userId", "用户Id不能为空"));
        }
        User user = userRepository.findUserById(id);
        if(user == null){
            throw new BusinessException(Map.of("userId", "用户不存在"));
        }

        if(user.getCancelAccountTime() != -1L){
            throw new BusinessException(Map.of("account", "不能重复注销"));

        }

        userRepository.cancelAccount(user.getUserName(),"::"+String.valueOf(user.getRegistrationDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()),new Date().getTime(),new Date().getTime());

        //删除缓存用户状态
        userCacheManager.delete_userState(user.getUserName());
        //删除缓存
        userCacheManager.delete_cache_findUserById(user.getId());
        userCacheManager.delete_cache_findUserByUserName(user.getUserName());
        userRoleCacheManager.delete_cache_findRoleGroupByUserName(user.getUserName());

    }



    /**
     * 获取用户发表的话题
     * @param page 页码
     * @param userName 用户名称
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public PageView<Topic> getUserTopic(int page, String userName,String fileServerAddress){
        if(userName == null || userName.trim().isEmpty()){
            throw new BusinessException(Map.of("userName", "用户名称不能为空"));
        }
        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();

        jpql.append(" and o.userName=?"+ (params.size()+1));
        params.add(userName.trim());

        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

        PageView<Topic> pageView = new PageView<Topic>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据id字段降序排序


        //调用分页算法类
        QueryResult<Topic> qr = topicRepository.getScrollData(Topic.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            List<Tag> tagList = tagRepository.findAllTag();
            if(tagList != null && tagList.size() >0){
                for(Topic topic : qr.getResultlist()){
                    for(Tag tag : tagList){
                        if(topic.getTagId().equals(tag.getId())){
                            topic.setTagName(tag.getName());
                            break;
                        }
                    }

                }
            }

            User user = null;
            for(Topic topic : qr.getResultlist()){
                if(user == null){
                    user = userCacheManager.query_cache_findUserByUserName(topic.getUserName());
                }
                if(user != null){
                    topic.setAccount(user.getAccount());
                    topic.setNickname(user.getNickname());
                    if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                        topic.setAvatarPath(fileServerAddress+user.getAvatarPath());
                        topic.setAvatarName(user.getAvatarName());
                    }
                }
            }

        }

        pageView.setQueryResult(qr);
        return pageView;
    }


    /**
     * 获取用户发表的评论
     * @param page 页码
     * @param userName 用户名称
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public PageView<Comment> getUserComment(int page, String userName, String fileServerAddress){
        if(userName == null || userName.trim().isEmpty()){
            throw new BusinessException(Map.of("userName", "用户名称不能为空"));
        }
        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();

        jpql.append(" and o.userName=?"+ (params.size()+1));
        params.add(userName.trim());

        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

        PageView<Comment> pageView = new PageView<Comment>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据id字段降序排序


        //调用分页算法类
        QueryResult<Comment> qr = commentRepository.getScrollData(Comment.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            List<Long> topicIdList = new ArrayList<Long>();
            for(Comment o :qr.getResultlist()){
                o.setContent(textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(o.getContent())));
                if(!topicIdList.contains(o.getTopicId())){
                    topicIdList.add(o.getTopicId());
                }
            }
            List<Topic> topicList = topicRepository.findTitleByIdList(topicIdList);
            if(topicList != null && topicList.size() >0){
                for(Comment o :qr.getResultlist()){
                    for(Topic topic : topicList){
                        if(topic.getId().equals(o.getTopicId())){
                            o.setTopicTitle(topic.getTitle());
                            break;
                        }
                    }

                }
            }
            User user = null;
            for(Comment comment : qr.getResultlist()){
                if(user == null){
                    user = userCacheManager.query_cache_findUserByUserName(comment.getUserName());
                }
                if(user != null){
                    comment.setAccount(user.getAccount());
                    comment.setNickname(user.getNickname());
                    if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                        comment.setAvatarPath(fileServerAddress+user.getAvatarPath());
                        comment.setAvatarName(user.getAvatarName());
                    }
                }
            }

        }

        pageView.setQueryResult(qr);
        return pageView;
    }

    /**
     * 获取用户发表的评论回复
     * @param page 页码
     * @param userName 用户名称
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public PageView<Reply> getUserCommentReply(int page, String userName, String fileServerAddress){
        if(userName == null || userName.trim().isEmpty()){
            throw new BusinessException(Map.of("userName", "用户名称不能为空"));
        }

        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();

        jpql.append(" and o.userName=?"+ (params.size()+1));
        params.add(userName.trim());

        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

        PageView<Reply> pageView = new PageView<Reply>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据id字段降序排序


        //调用分页算法类
        QueryResult<Reply> qr = commentRepository.getScrollData(Reply.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            List<Long> topicIdList = new ArrayList<Long>();
            for(Reply o :qr.getResultlist()){

                o.setContent(textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(o.getContent())));
                if(!topicIdList.contains(o.getTopicId())){
                    topicIdList.add(o.getTopicId());
                }
            }
            List<Topic> topicList = topicRepository.findTitleByIdList(topicIdList);
            if(topicList != null && topicList.size() >0){
                for(Reply o :qr.getResultlist()){
                    for(Topic topic : topicList){
                        if(topic.getId().equals(o.getTopicId())){
                            o.setTopicTitle(topic.getTitle());
                            break;
                        }
                    }

                }
            }
            User user = null;
            for(Reply reply : qr.getResultlist()){
                if(user == null){
                    user = userCacheManager.query_cache_findUserByUserName(reply.getUserName());
                }
                if(user != null){
                    reply.setAccount(user.getAccount());
                    reply.setNickname(user.getNickname());
                    if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                        reply.setAvatarPath(fileServerAddress+user.getAvatarPath());
                        reply.setAvatarName(user.getAvatarName());
                    }
                }
            }
        }

        pageView.setQueryResult(qr);
        return pageView;
    }


    /**
     * 获取用户发表的问题
     * @param page 页码
     * @param userName 用户名称
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public PageView<Question> getUserQuestion(int page, String userName, String fileServerAddress){
        if(userName == null || userName.trim().isEmpty()){
            throw new BusinessException(Map.of("userName", "用户名称不能为空"));
        }

        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();

        jpql.append(" and o.userName=?"+ (params.size()+1));
        params.add(userName.trim());

        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

        PageView<Question> pageView = new PageView<Question>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据id字段降序排序

        //调用分页算法类
        QueryResult<Question> qr = questionRepository.getScrollData(Question.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            List<QuestionTag> questionTagList = questionTagRepository.findAllQuestionTag();

            if(questionTagList != null && questionTagList.size() >0){
                for(Question question : qr.getResultlist()){
                    List<QuestionTagAssociation> questionTagAssociationList = questionCacheManager.query_cache_findQuestionTagAssociationByQuestionId(question.getId());
                    if(questionTagAssociationList != null && questionTagAssociationList.size() >0){
                        for(QuestionTag questionTag : questionTagList){
                            for(QuestionTagAssociation questionTagAssociation : questionTagAssociationList){
                                if(questionTagAssociation.getQuestionTagId().equals(questionTag.getId())){
                                    questionTagAssociation.setQuestionTagName(questionTag.getName());
                                    question.addQuestionTagAssociation(questionTagAssociation);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            User user = null;
            for(Question question : qr.getResultlist()){
                if(user == null){
                    user = userCacheManager.query_cache_findUserByUserName(question.getUserName());
                }
                if(user != null){
                    question.setAccount(user.getAccount());
                    question.setNickname(user.getNickname());
                    if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                        question.setAvatarPath(fileServerAddress+user.getAvatarPath());
                        question.setAvatarName(user.getAvatarName());
                    }
                }
            }
        }

        pageView.setQueryResult(qr);
        return pageView;
    }

    /**
     * 获取用户发表的答案
     * @param page 页码
     * @param userName 用户名称
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public PageView<Answer> getUserAnswer(int page, String userName, String fileServerAddress) {
        if (userName == null || userName.trim().isEmpty()) {
            throw new BusinessException(Map.of("userName", "用户名称不能为空"));
        }

        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();

        jpql.append(" and o.userName=?"+ (params.size()+1));
        params.add(userName.trim());

        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

        PageView<Answer> pageView = new PageView<Answer>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据id字段降序排序


        //调用分页算法类
        QueryResult<Answer> qr = answerRepository.getScrollData(Answer.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            List<Long> questionIdList = new ArrayList<Long>();
            for(Answer o :qr.getResultlist()){
                o.setContent(textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(o.getContent())));
                if(!questionIdList.contains(o.getQuestionId())){
                    questionIdList.add(o.getQuestionId());
                }
            }
            List<Question> questionList = questionRepository.findTitleByIdList(questionIdList);
            if(questionList != null && questionList.size() >0){
                for(Answer o :qr.getResultlist()){
                    for(Question question : questionList){
                        if(question.getId().equals(o.getQuestionId())){
                            o.setQuestionTitle(question.getTitle());
                            break;
                        }
                    }

                }
            }
            User user = null;
            for(Answer answer : qr.getResultlist()){
                if(user == null){
                    user = userCacheManager.query_cache_findUserByUserName(answer.getUserName());
                }
                if(user != null){
                    answer.setAccount(user.getAccount());
                    answer.setNickname(user.getNickname());
                    if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                        answer.setAvatarPath(fileServerAddress+user.getAvatarPath());
                        answer.setAvatarName(user.getAvatarName());
                    }
                }
            }
        }

        pageView.setQueryResult(qr);
        return pageView;
    }

    /**
     * 获取用户发表的答案回复
     * @param page 页码
     * @param userName 用户名称
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public PageView<AnswerReply> getUserAnswerReply(int page, String userName, String fileServerAddress) {
        if (userName == null || userName.trim().isEmpty()) {
            throw new BusinessException(Map.of("userName", "用户名称不能为空"));
        }

        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();

        jpql.append(" and o.userName=?"+ (params.size()+1));
        params.add(userName.trim());

        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

        PageView<AnswerReply> pageView = new PageView<AnswerReply>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据id字段降序排序


        //调用分页算法类
        QueryResult<AnswerReply> qr = answerRepository.getScrollData(AnswerReply.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            List<Long> questionIdList = new ArrayList<Long>();
            for(AnswerReply o :qr.getResultlist()){

                o.setContent(textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(o.getContent())));
                if(!questionIdList.contains(o.getQuestionId())){
                    questionIdList.add(o.getQuestionId());
                }
            }
            List<Question> questionList = questionRepository.findTitleByIdList(questionIdList);
            if(questionList != null && questionList.size() >0){
                for(AnswerReply o :qr.getResultlist()){
                    for(Question question : questionList){
                        if(question.getId().equals(o.getQuestionId())){
                            o.setQuestionTitle(question.getTitle());
                            break;
                        }
                    }

                }
            }
            User user = null;
            for(AnswerReply answerReply : qr.getResultlist()){
                if(user == null){
                    user = userCacheManager.query_cache_findUserByUserName(answerReply.getUserName());
                }
                if(user != null){
                    answerReply.setAccount(user.getAccount());
                    answerReply.setNickname(user.getNickname());
                    if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                        answerReply.setAvatarPath(fileServerAddress+user.getAvatarPath());
                        answerReply.setAvatarName(user.getAvatarName());
                    }
                }
            }
        }

        pageView.setQueryResult(qr);
        return pageView;
    }


    /**
     * 校验搜索类型
     *
     * @param userTypeCode 查询用户类型
     * @param keyword      关键词
     * @param errors       错误集合
     */
    private void validateSearchType(Integer userTypeCode, String keyword, Map<String, String> errors) {
        if (userTypeCode.equals(10)) {//账号
            if (keyword == null || keyword.trim().isEmpty()) {
                errors.put("keyword", "请填写账号");
            }
        } else if (userTypeCode.equals(20)) {//呢称
            if (keyword == null || keyword.trim().isEmpty()) {
                errors.put("keyword", "请填写呢称");
            }
        } else if (userTypeCode.equals(30)) {//用户名
            if (keyword == null || keyword.trim().isEmpty()) {
                errors.put("keyword", "请填写用户名");
            }
        } else if (userTypeCode.equals(40)) {//手机号
            if (keyword == null || keyword.trim().isEmpty()) {
                errors.put("keyword", "请填写手机号");
            }
        } else if (userTypeCode.equals(50)) {//邮箱
            if (keyword == null || keyword.trim().isEmpty()) {
                errors.put("keyword", "请填写邮箱");
            }
        } else {
            errors.put("userTypeCode", "请选择用户类型");
        }
    }



    /**
     * 校验积分范围
     *
     * @param start_point 起始积分
     * @param end_point   结束积分
     * @param errors      错误集合
     */
    private PointQuery validateAndConvertPoint(String start_point, String end_point, Map<String, String> errors) {
        Integer startPoint = null;//起始积分
        Integer endPoint = null;//结束积分
        //积分
        if (start_point != null && !start_point.trim().isEmpty()) {
            boolean start_point_verification = Verification.isPositiveInteger(start_point.trim());//正整数
            if (start_point_verification) {
                startPoint = Integer.parseInt(start_point.trim());
            } else {
                errors.put("start_point", "请填写正整数");
            }
        }
        if (end_point != null && !end_point.trim().isEmpty()) {
            boolean end_point_verification = Verification.isPositiveInteger(end_point.trim());//正整数
            if (end_point_verification) {
                endPoint = Integer.parseInt(end_point.trim());
            } else {
                errors.put("end_point", "请填写正整数");
            }
        }
        if (startPoint != null && endPoint != null) {
            if (startPoint > endPoint) {
                errors.put("start_point", "起始积分不能大于结束积分");
            }
        }
        return new PointQuery(startPoint, endPoint);
    }

    /**
     * 校验注册时间范围
     *
     * @param start_registrationDate 起始注册日期
     * @param end_registrationDate   结束注册日期
     * @param errors                 错误集合
     */
    private RegistrationDateQuery validateAndConvertRegistrationDate(String start_registrationDate, String end_registrationDate, Map<String, String> errors) {
        LocalDateTime startRegistrationDate = null;//起始注册日期
        LocalDateTime endRegistrationDate = null;//结束注册日期

        if (start_registrationDate != null && !start_registrationDate.trim().isEmpty()) {
            boolean start_registrationDateVerification = Verification.isTime_minute(start_registrationDate.trim());
            if (start_registrationDateVerification) {
                startRegistrationDate = LocalDateTime.parse(start_registrationDate.trim(), DATE_TIME_FORMATTER);
            } else {
                errors.put("start_registrationDate", "请填写正确的日期");
            }
        }
        if (end_registrationDate != null && !end_registrationDate.trim().isEmpty()) {
            boolean end_registrationDateVerification = Verification.isTime_minute(end_registrationDate.trim());
            if (end_registrationDateVerification) {
                endRegistrationDate = LocalDateTime.parse(end_registrationDate.trim(), DATE_TIME_FORMATTER);
            } else {
                errors.put("end_registrationDate", "请填写正确的日期");
            }
        }
        //比较时间
        if (errors.isEmpty()) {
            if (startRegistrationDate != null && endRegistrationDate != null && startRegistrationDate.isAfter(endRegistrationDate)) {
                errors.put("start_registrationDate", "起始时间不能比结束时间大");
            }
        }
        return new RegistrationDateQuery(startRegistrationDate, endRegistrationDate);
    }

    /**
     * 处理自定义字段
     *
     * @param request 请求信息
     * @param errors  错误集合
     */
    private List<UserCustom> processUserCustomFields(HttpServletRequest request, Map<String, String> errors) {
        //自定义参数
        List<UserCustom> userCustomList = userCustomRepository.findAllUserCustom();
        if (userCustomList != null && !userCustomList.isEmpty()) {
            Iterator<UserCustom> it = userCustomList.iterator();
            while (it.hasNext()) {
                UserCustom userCustom = it.next();
                if (!userCustom.isVisible()) {//如果不显示
                    it.remove();
                    continue;
                }
                if (!userCustom.isSearch()) {//如果不可搜索
                    it.remove();
                    continue;
                }
                if (userCustom.getChooseType().equals(1) || (userCustom.getChooseType().equals(5))) {//1.输入框  5.文本域不可搜索
                    it.remove();
                    continue;
                }
                if (userCustom.getValue() != null && !userCustom.getValue().trim().isEmpty()) {
                    LinkedHashMap<String, String> itemValue = jsonComponent.toGenericObject(userCustom.getValue(), new TypeReference<LinkedHashMap<String, String>>() {
                    });
                    userCustom.setItemValue(itemValue);
                }

            }
        }

        if (userCustomList != null && !userCustomList.isEmpty()) {
            for (UserCustom userCustom : userCustomList) {
                //用户自定义注册功能项用户输入值集合
                List<UserInputValue> userInputValueList = new ArrayList<UserInputValue>();


                if (userCustom.getValue() != null && !userCustom.getValue().trim().isEmpty()) {
                    LinkedHashMap<String, String> itemValue = jsonComponent.toGenericObject(userCustom.getValue(), new TypeReference<LinkedHashMap<String, String>>() {
                    });
                    userCustom.setItemValue(itemValue);
                }
                /**
                 if(userCustom.getChooseType().equals(1)){//1.输入框
                 String userCustom_value = request.getParameter("userCustom_"+userCustom.getId());

                 if(userCustom_value != null && !userCustom_value.trim().isEmpty()){
                 UserInputValue userInputValue = new UserInputValue();
                 userInputValue.setUserCustomId(userCustom.getId());
                 userInputValue.setContent(userCustom_value.trim());
                 userInputValueList.add(userInputValue);
                 userCustom.setUserInputValueList(userInputValueList);

                 if(userCustom_value.trim().length() > userCustom.getMaxlength()){
                 errors.put("userCustom_"+userCustom.getId(), "长度超过"+userCustom_value.length()+"个字符");
                 }

                 int fieldFilter = userCustom.getFieldFilter();//字段过滤方式    0.无  1.只允许输入数字  2.只允许输入字母  3.只允许输入数字和字母  4.只允许输入汉字  5.正则表达式过滤
                 switch(fieldFilter){
                 case 1 : //输入数字
                 if(!Verification.isPositiveIntegerZero(userCustom_value.trim())){
                 errors.put("userCustom_"+userCustom.getId(), "只允许输入数字");
                 }
                 break;
                 case 2 : //输入字母
                 if(!Verification.isLetter(userCustom_value.trim())){
                 errors.put("userCustom_"+userCustom.getId(), "只允许输入字母");
                 }
                 break;
                 case 3 : //只能输入数字和字母
                 if(!Verification.isNumericLetters(userCustom_value.trim())){
                 errors.put("userCustom_"+userCustom.getId(), "只允许输入数字和字母");
                 }
                 break;
                 case 4 : //只能输入汉字
                 if(!Verification.isChineseCharacter(userCustom_value.trim())){
                 errors.put("userCustom_"+userCustom.getId(), "只允许输入汉字");
                 }
                 break;
                 case 5 : //正则表达式过滤
                 if(!userCustom_value.trim().matches(userCustom.getRegular())){
                 errors.put("userCustom_"+userCustom.getId(), "输入错误");
                 }
                 break;
                 //	default:
                 }
                 }

                 }else**/
                if (userCustom.getChooseType().equals(2)) {//2.单选按钮
                    String userCustom_value = request.getParameter("userCustom_" + userCustom.getId());

                    if (userCustom_value != null && !userCustom_value.trim().isEmpty()) {

                        String itemValue = userCustom.getItemValue().get(userCustom_value.trim());
                        if (itemValue != null) {
                            UserInputValue userInputValue = new UserInputValue();
                            userInputValue.setUserCustomId(userCustom.getId());
                            userInputValue.setOptions(userCustom_value.trim());
                            userInputValueList.add(userInputValue);
                            userCustom.setUserInputValueList(userInputValueList);
                        }

                    }
                } else if (userCustom.getChooseType().equals(3)) {//3.多选按钮
                    String[] userCustom_value_arr = request.getParameterValues("userCustom_" + userCustom.getId());

                    if (userCustom_value_arr != null && userCustom_value_arr.length > 0) {
                        for (String userCustom_value : userCustom_value_arr) {

                            if (userCustom_value != null && !userCustom_value.trim().isEmpty()) {

                                String itemValue = userCustom.getItemValue().get(userCustom_value.trim());
                                if (itemValue != null) {
                                    UserInputValue userInputValue = new UserInputValue();
                                    userInputValue.setUserCustomId(userCustom.getId());
                                    userInputValue.setOptions(userCustom_value.trim());
                                    userInputValueList.add(userInputValue);
                                }


                            }
                        }
                    }
                    userCustom.setUserInputValueList(userInputValueList);

                } else if (userCustom.getChooseType().equals(4)) {//4.下拉列表
                    String[] userCustom_value_arr = request.getParameterValues("userCustom_" + userCustom.getId());

                    if (userCustom_value_arr != null && userCustom_value_arr.length > 0) {
                        for (String userCustom_value : userCustom_value_arr) {

                            if (userCustom_value != null && !userCustom_value.trim().isEmpty()) {

                                String itemValue = userCustom.getItemValue().get(userCustom_value.trim());
                                if (itemValue != null) {
                                    UserInputValue userInputValue = new UserInputValue();
                                    userInputValue.setUserCustomId(userCustom.getId());
                                    userInputValue.setOptions(userCustom_value.trim());
                                    userInputValueList.add(userInputValue);
                                }


                            }
                        }
                    }
                    userCustom.setUserInputValueList(userInputValueList);
                }
                /**
                 else if(userCustom.getChooseType().equals(5)){// 5.文本域
                 String userCustom_value = request.getParameter("userCustom_"+userCustom.getId());

                 if(userCustom_value != null && !userCustom_value.trim().isEmpty()){
                 UserInputValue userInputValue = new UserInputValue();
                 userInputValue.setUserCustomId(userCustom.getId());
                 userInputValue.setContent(userCustom_value);
                 userInputValueList.add(userInputValue);
                 userCustom.setUserInputValueList(userInputValueList);
                 }
                 }**/

            }
        }
        return userCustomList;
    }



    /**
     * 处理自定义字段的解析和校验
     * @param userCustomList 用户自定义注册功能项集合
     * @param request        请求信息
     * @param errors         错误集合
     */
    private void processAndValidateCustomFields(List<UserCustom> userCustomList, HttpServletRequest request, Map<String, String> errors) {
        if (userCustomList != null && !userCustomList.isEmpty()) {
            for (UserCustom userCustom : userCustomList) {
                //用户自定义注册功能项用户输入值集合
                List<UserInputValue> userInputValueList = new ArrayList<UserInputValue>();

                if (userCustom.isVisible()) {//显示
                    if (userCustom.getValue() != null && !userCustom.getValue().trim().isEmpty()) {
                        LinkedHashMap<String, String> itemValue = jsonComponent.toGenericObject(userCustom.getValue(), new TypeReference<LinkedHashMap<String, String>>() {
                        });
                        userCustom.setItemValue(itemValue);
                    }
                    if (userCustom.getChooseType().equals(1)) {//1.输入框
                        String userCustom_value = request.getParameter("userCustom_" + userCustom.getId());

                        if (userCustom_value != null && !userCustom_value.trim().isEmpty()) {
                            UserInputValue userInputValue = new UserInputValue();
                            userInputValue.setUserCustomId(userCustom.getId());
                            userInputValue.setContent(userCustom_value.trim());
                            userInputValueList.add(userInputValue);

                            if (userCustom.getMaxlength() != null && userCustom_value.length() > userCustom.getMaxlength()) {
                                errors.put("userCustom_" + userCustom.getId(), "长度超过" + userCustom_value.length() + "个字符");
                            }

                            int fieldFilter = userCustom.getFieldFilter();//字段过滤方式    0.无  1.只允许输入数字  2.只允许输入字母  3.只允许输入数字和字母  4.只允许输入汉字  5.正则表达式过滤
                            switch (fieldFilter) {
                                case 1: //输入数字
                                    if (!Verification.isPositiveIntegerZero(userCustom_value.trim())) {
                                        errors.put("userCustom_" + userCustom.getId(), "只允许输入数字");
                                    }
                                    break;
                                case 2: //输入字母
                                    if (!Verification.isLetter(userCustom_value.trim())) {
                                        errors.put("userCustom_" + userCustom.getId(), "只允许输入字母");
                                    }
                                    break;
                                case 3: //只能输入数字和字母
                                    if (!Verification.isNumericLetters(userCustom_value.trim())) {
                                        errors.put("userCustom_" + userCustom.getId(), "只允许输入数字和字母");
                                    }
                                    break;
                                case 4: //只能输入汉字
                                    if (!Verification.isChineseCharacter(userCustom_value.trim())) {
                                        errors.put("userCustom_" + userCustom.getId(), "只允许输入汉字");
                                    }
                                    break;
                                case 5: //正则表达式过滤
                                    if (!userCustom_value.trim().matches(userCustom.getRegular())) {
                                        errors.put("userCustom_" + userCustom.getId(), "输入错误");
                                    }
                                    break;
                                //	default:
                            }
                        } else {
                            if (userCustom.isRequired()) {//是否必填
                                errors.put("userCustom_" + userCustom.getId(), "必填项");
                            }

                        }
                        userCustom.setUserInputValueList(userInputValueList);
                    } else if (userCustom.getChooseType().equals(2)) {//2.单选按钮
                        String userCustom_value = request.getParameter("userCustom_" + userCustom.getId());

                        if (userCustom_value != null && !userCustom_value.trim().isEmpty()) {

                            String itemValue = userCustom.getItemValue().get(userCustom_value.trim());
                            if (itemValue != null) {
                                UserInputValue userInputValue = new UserInputValue();
                                userInputValue.setUserCustomId(userCustom.getId());
                                userInputValue.setOptions(userCustom_value.trim());
                                userInputValueList.add(userInputValue);

                            } else {
                                if (userCustom.isRequired()) {//是否必填
                                    errors.put("userCustom_" + userCustom.getId(), "必填项");
                                }
                            }

                        } else {
                            if (userCustom.isRequired()) {//是否必填
                                errors.put("userCustom_" + userCustom.getId(), "必填项");
                            }
                        }
                        userCustom.setUserInputValueList(userInputValueList);

                    } else if (userCustom.getChooseType().equals(3)) {//3.多选按钮
                        String[] userCustom_value_arr = request.getParameterValues("userCustom_" + userCustom.getId());

                        if (userCustom_value_arr != null && userCustom_value_arr.length > 0) {
                            for (String userCustom_value : userCustom_value_arr) {

                                if (userCustom_value != null && !userCustom_value.trim().isEmpty()) {

                                    String itemValue = userCustom.getItemValue().get(userCustom_value.trim());
                                    if (itemValue != null) {
                                        UserInputValue userInputValue = new UserInputValue();
                                        userInputValue.setUserCustomId(userCustom.getId());
                                        userInputValue.setOptions(userCustom_value.trim());
                                        userInputValueList.add(userInputValue);
                                    }


                                }
                            }
                        } else {
                            if (userCustom.isRequired()) {//是否必填
                                errors.put("userCustom_" + userCustom.getId(), "必填项");
                            }
                        }
                        if (userInputValueList.isEmpty()) {
                            if (userCustom.isRequired()) {//是否必填
                                errors.put("userCustom_" + userCustom.getId(), "必填项");
                            }
                        }
                        userCustom.setUserInputValueList(userInputValueList);

                    } else if (userCustom.getChooseType().equals(4)) {//4.下拉列表
                        String[] userCustom_value_arr = request.getParameterValues("userCustom_" + userCustom.getId());

                        if (userCustom_value_arr != null && userCustom_value_arr.length > 0) {
                            for (String userCustom_value : userCustom_value_arr) {

                                if (userCustom_value != null && !userCustom_value.trim().isEmpty()) {

                                    String itemValue = userCustom.getItemValue().get(userCustom_value.trim());
                                    if (itemValue != null) {
                                        UserInputValue userInputValue = new UserInputValue();
                                        userInputValue.setUserCustomId(userCustom.getId());
                                        userInputValue.setOptions(userCustom_value.trim());
                                        userInputValueList.add(userInputValue);
                                    }


                                }
                            }
                        } else {
                            if (userCustom.isRequired()) {//是否必填
                                errors.put("userCustom_" + userCustom.getId(), "必填项");
                            }
                        }
                        if (userInputValueList.isEmpty()) {
                            if (userCustom.isRequired()) {//是否必填
                                errors.put("userCustom_" + userCustom.getId(), "必填项");
                            }
                        }
                        userCustom.setUserInputValueList(userInputValueList);
                    } else if (userCustom.getChooseType().equals(5)) {// 5.文本域
                        String userCustom_value = request.getParameter("userCustom_" + userCustom.getId());

                        if (userCustom_value != null && !userCustom_value.trim().isEmpty()) {
                            UserInputValue userInputValue = new UserInputValue();
                            userInputValue.setUserCustomId(userCustom.getId());
                            userInputValue.setContent(userCustom_value);
                            userInputValueList.add(userInputValue);

                        } else {
                            if (userCustom.isRequired()) {//是否必填
                                errors.put("userCustom_" + userCustom.getId(), "必填项");
                            }
                        }
                        userCustom.setUserInputValueList(userInputValueList);
                    }
                }
            }
        }
    }

    /**
     * 处理角色分配并校验角色过期日期
     *
     * @param userRolesId  角色Id
     * @param userRoleList 角色集合
     * @param userRoleGroupList 用户角色组
     * @param request      请求信息
     * @param errors       错误集合
     */
    private void processAndValidateUserRoles(String[] userRolesId, List<UserRole> userRoleList, List<UserRoleGroup> userRoleGroupList,HttpServletRequest request, Map<String, String> errors) {
        if(userRolesId != null && userRolesId.length >0) {

            if (userRoleList != null && !userRoleList.isEmpty()) {
                for (String rolesId : userRolesId) {
                    if (rolesId != null && !rolesId.trim().isEmpty()) {
                        for (UserRole userRole : userRoleList) {
                            userRole.setSelected(true);//错误回显需要
                            if (!userRole.getDefaultRole() && userRole.getId().equals(rolesId.trim())) {//默认角色不保存
                                //默认时间  年,月,日,时,分,秒,毫秒
                                LocalDateTime validPeriodEnd = LocalDateTime.of(2999, 1, 1, 0, 0);// 2999年1月1日0点0分

                                String validPeriodEnd_str = request.getParameter("validPeriodEnd_" + userRole.getId());

                                if (validPeriodEnd_str != null && !validPeriodEnd_str.trim().isEmpty()) {
                                    boolean verification = Verification.isTime_minute(validPeriodEnd_str.trim());
                                    if (verification) {
                                        validPeriodEnd = LocalDateTime.parse(validPeriodEnd_str.trim(), DATE_TIME_FORMATTER);
                                    } else {
                                        validPeriodEnd = null;
                                        errors.put("validPeriodEnd_" + userRole.getId(), "请填写正确的日期");
                                    }
                                }

                                userRole.setValidPeriodEnd(validPeriodEnd);//错误回显需要
                                UserRoleGroup userRoleGroup = new UserRoleGroup();
                                userRoleGroup.setUserRoleId(userRole.getId());
                                userRoleGroup.setValidPeriodEnd(validPeriodEnd);
                                userRoleGroupList.add(userRoleGroup);
                            }
                        }
                    }
                }


            }
        }
    }

    /**
     * 处理用户字段的解析和校验
     * @param userForm       用户表单
     * @param newUser       新用户信息
     * @param oldUser       旧用户信息
     * @param errors         错误集合
     */
    private void processAndValidateUserFields(User userForm, User newUser,User oldUser,Map<String, String> errors) {
        if(userForm.getNickname() != null && !userForm.getNickname().trim().isEmpty()){
            if(userForm.getNickname().length()>15){
                errors.put("nickname", "呢称不能超过15个字符");
            }


            User u = userRepository.findUserByNickname(userForm.getNickname().trim());
            if(u != null){
                if(oldUser.getNickname() == null || oldUser.getNickname().isEmpty() || !userForm.getNickname().trim().equals(oldUser.getNickname())){
                    errors.put("nickname", "该呢称已存在");
                }

            }
            newUser.setNickname(userForm.getNickname().trim());
        }else{
            newUser.setNickname(null);
        }
        if(oldUser.getType() <=30){//本地账户才允许设置的参数
            if(userForm.getPassword() != null && !userForm.getPassword().trim().isEmpty()){//密码
                if(userForm.getPassword().length()>30){
                    errors.put("password", "密码不能超过30个字符");
                }
                //密码
                newUser.setPassword(SHA.sha256Hex(SHA.sha256Hex(userForm.getPassword().trim())+"["+oldUser.getSalt()+"]"));
                newUser.setSecurityDigest(new Date().getTime());
            }else{
                newUser.setPassword(oldUser.getPassword());
                newUser.setSecurityDigest(oldUser.getSecurityDigest());
            }

            if(oldUser.getType().equals(10)){
                if(userForm.getIssue() != null && !userForm.getIssue().trim().isEmpty()){//密码提示问题
                    if(userForm.getIssue().length()>50){
                        errors.put("issue", "密码提示问题不能超过50个字符");
                    }
                    newUser.setIssue(userForm.getIssue().trim());
                }else{
                    errors.put("issue", "密码提示问题不能为空");
                }
                if(userForm.getAnswer() != null && !userForm.getAnswer().trim().isEmpty()){//密码提示答案
                    if(userForm.getAnswer().length()>50){
                        errors.put("answer", "密码提示答案不能超过50个字符");
                    }
                    //密码提示答案由  密码提示答案原文sha256  进行sha256组成
                    newUser.setAnswer(SHA.sha256Hex(SHA.sha256Hex(userForm.getAnswer().trim())));
                }else{
                    newUser.setAnswer(oldUser.getAnswer());
                }

            }
        }else{
            newUser.setPassword(oldUser.getPassword());
            if(oldUser.getSecurityDigest() != null){
                newUser.setSecurityDigest(oldUser.getSecurityDigest());
            }else{
                newUser.setSecurityDigest(new Date().getTime());
            }

            newUser.setIssue(oldUser.getIssue());
            newUser.setAnswer(oldUser.getAnswer());
        }

        //平台用户Id
        newUser.setPlatformUserId(oldUser.getPlatformUserId());
        if(oldUser.getCancelAccountTime().equals(-1L)){//未注销的用户
            if(oldUser.getType().equals(10)){//10:本地账号密码用户
                //手机
                if(userForm.getMobile() != null && !userForm.getMobile().trim().isEmpty()){
                    if(userForm.getMobile().trim().length() >18){
                        errors.put("mobile", "手机号码超长");
                    }else{
                        boolean mobile_verification = Verification.isPositiveInteger(Strings.CS.removeStart(userForm.getMobile().trim(), "+"));//正整数
                        if(!mobile_verification){
                            errors.put("mobile", "手机号码不正确");
                        }else{
                            newUser.setMobile(userForm.getMobile().trim());
                        }
                    }
                }
                if(userForm.getEmail() != null && !userForm.getEmail().trim().isEmpty()){//邮箱
                    if(!Verification.isEmail(userForm.getEmail().trim())){
                        errors.put("email", "Email地址不正确");
                    }
                    if(userForm.getEmail().trim().length()>90){
                        errors.put("email", "Email地址不能超过90个字符");
                    }
                    newUser.setEmail(userForm.getEmail().trim());
                }
            }else if(oldUser.getType().equals(20)){//20: 手机用户
                //手机
                if(userForm.getMobile() != null && !userForm.getMobile().trim().isEmpty()){
                    if(userForm.getMobile().trim().length() >18){
                        errors.put("mobile", "手机号码超长");
                    }else{
                        boolean mobile_verification = Verification.isPositiveInteger(Strings.CS.removeStart(userForm.getMobile().trim(), "+"));//正整数
                        if(!mobile_verification){
                            errors.put("mobile", "手机号码不正确");
                        }else{

                            if(!oldUser.getMobile().equals(userForm.getMobile().trim())){
                                String platformUserId = userComponent.thirdPartyUserIdToPlatformUserId(userForm.getMobile().trim(),20);
                                User mobile_user = userRepository.findUserByPlatformUserId(platformUserId);

                                if(mobile_user != null){
                                    errors.put("mobile", "手机号码已注册");

                                }
                            }

                            newUser.setPlatformUserId(userComponent.thirdPartyUserIdToPlatformUserId(userForm.getMobile().trim(),20));
                            newUser.setMobile(userForm.getMobile().trim());
                        }
                    }
                }else{
                    errors.put("mobile", "手机号码不能为空");
                }
                if(userForm.getEmail() != null && !userForm.getEmail().trim().isEmpty()){//邮箱
                    if(!Verification.isEmail(userForm.getEmail().trim())){
                        errors.put("email", "Email地址不正确");
                    }
                    if(userForm.getEmail().trim().length()>90){
                        errors.put("email", "Email地址不能超过90个字符");
                    }
                    newUser.setEmail(userForm.getEmail().trim());
                }
            }else if(oldUser.getType().equals(30)){//30: 邮箱用户
                if(userForm.getEmail() != null && !userForm.getEmail().trim().isEmpty()){//邮箱
                    if(userForm.getEmail().trim().length()>90){
                        errors.put("email", "Email地址不能超过90个字符");
                    }else{
                        if(Verification.isEmail(userForm.getEmail().trim())){

                            if(!oldUser.getEmail().equals(userForm.getEmail().trim())){
                                String platformUserId = userComponent.thirdPartyUserIdToPlatformUserId(userForm.getEmail().trim(),30);
                                User email_user = userRepository.findUserByPlatformUserId(platformUserId);

                                if(email_user != null){
                                    errors.put("email", "Email地址已注册");

                                }
                            }

                            newUser.setPlatformUserId(userComponent.thirdPartyUserIdToPlatformUserId(userForm.getEmail().trim(),30));
                            newUser.setEmail(userForm.getEmail().trim());

                        }else{
                            errors.put("email", "Email地址不正确");
                        }
                    }
                }else{
                    errors.put("email", "Email地址不能为空");
                }
                //手机
                if(userForm.getMobile() != null && !userForm.getMobile().trim().isEmpty()){
                    if(userForm.getMobile().trim().length() >18){
                        errors.put("mobile", "手机号码超长");
                    }else{
                        boolean mobile_verification = Verification.isPositiveInteger(Strings.CS.removeStart(userForm.getMobile().trim(), "+"));//正整数
                        if(!mobile_verification){
                            errors.put("mobile", "手机号码不正确");
                        }else{
                            newUser.setMobile(userForm.getMobile().trim());
                        }
                    }
                }
            }
        }else{//已注销
            newUser.setMobile(oldUser.getMobile().trim());
            newUser.setEmail(oldUser.getEmail().trim());
        }

        //实名认证
        newUser.setRealNameAuthentication(userForm.isRealNameAuthentication());
        //允许显示用户动态
        newUser.setAllowUserDynamic(userForm.getAllowUserDynamic());

        //用户状态
        if(userForm.getState() == null){
            errors.put("state", "用户状态不能为空");
        }else{
            if(userForm.getState() >2 || userForm.getState() <1){
                errors.put("state", "用户状态错误");
            }
            newUser.setState(userForm.getState());
        }
    }

}