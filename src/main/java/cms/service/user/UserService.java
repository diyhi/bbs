package cms.service.user;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import cms.bean.QueryResult;
import cms.bean.user.DisableUserName;
import cms.bean.user.PointLog;
import cms.bean.user.User;
import cms.bean.user.UserDynamic;
import cms.bean.user.UserInputValue;
import cms.bean.user.UserLoginLog;
import cms.bean.user.UserRoleGroup;
import cms.service.besa.DAO;
/**
 * 用户管理接口
 *
 */
public interface UserService extends DAO<User> {

	/**
	 * 根据条件分页查询用户名称和Id
	 * @param jpql SQL
	 * @param params 参数值
	 * @param firstIndex 开始索引
	 * @param maxResult 需要获取的记录数
	 * @return 返回值只包含id和userName字段值
	 */
	public List<User> findUserInfoByConditionPage(String jpql,List<Object> params,int firstIndex, int maxResult);
	/**
	 * 根据条件查询所有用户
	 * @param param 参数
	 * @param paramValue 参数值
	 * @param firstIndex 开始索引
	 * @param maxResult 需要获取的记录数
	 * @param sort 排序 true:升序 false:降序
	 */
	public QueryResult<User> findUserByCondition(String param,List<Object> paramValue,int firstIndex, int maxResult,boolean sort);
	/**
	 * 根据自定义条件查询用户
	 * @param param 参数
	 * @param paramValue 参数值
	 * @param customParam 用户自定义注册功能项参数
	 * @param firstIndex 开始索引
	 * @param maxResult 需要获取的记录数
	 * @param sort 排序 true:升序 false:降序
	 */
	public QueryResult<User> findUserByCustomCondition(String param,List<Object> paramValue,String customParam,int firstIndex, int maxResult,boolean sort);

	/**
	 * 根据用户Id查询当前用户
	 * @param id 用户Id
	 * @return
	 */
	public User findUserById(Long id);
	/**
	 * 根据用户Id集合查询当前用户
	 * @param userIdList 用户Id集合
	 * @return
	 */
	public List<User> findUserByUserIdList(List<Long> userIdList);
	/**
	 * 根据用户名查询出当前用户
	 * @param userName 用户名称
	 * @return
	*/
	public User findUserByUserName(String userName);
	/**
	 * 根据呢称查询当前用户
	 * @param nickname 呢称
	 * @return
	 */
	public User findUserByNickname(String nickname);
	/**
	 * 根据账号查询当前用户
	 * @param account 账号
	 * @return
	 */
	public User findUserByAccount(String account);
	/**
	 * 根据平台用户Id查询当前用户
	 * @param platformUserId 平台用户Id
	 * @return
	 */
	public User findUserByPlatformUserId(String platformUserId);
	/**
	 * 保存用户
	 * @param user 用户
	 * @param userInputValueList 用户自定义注册功能项用户输入值
	 * @param userRoleGroupList 用户角色组集合
	 */
	public void saveUser(User user,List<UserInputValue> userInputValueList, List<UserRoleGroup> userRoleGroupList);
	/**
	 * 前台修改用户
	 * @param user 用户
	 * @param add_userInputValue 添加注册功能项用户输入值集合
	 * @param delete_userInputValueIdList 删除注册功能项用户输入值Id集合
	 */
	public Integer updateUser2(User user, List<UserInputValue> add_userInputValue,List<Long> delete_userInputValueIdList);
	/**
	 * 修改用户
	 * @param user 用户
	 * @param add_userInputValue 添加注册功能项用户输入值集合
	 * @param delete_userInputValueIdList 删除注册功能项用户输入值Id集合
	 * @param userRoleGroupList 用户角色组集合
	*/
	public Integer updateUser(User user, List<UserInputValue> add_userInputValue,List<Long> delete_userInputValueIdList,List<UserRoleGroup> userRoleGroupList);
	/**
	 * 修改密码
	 * @param userName 用户名称
	 * @param password 密码
	 * @param securityDigest 安全摘要
	 * @param userVersion 版本
	 * @return
	 */
	public Integer updatePassword(String userName,String password,Long securityDigest,Integer userVersion);
	/**
	 * 修改用户手机
	 * @param userName 用户名称
	 * @param mobile 手机号
	 * @param realNameAuthentication 是否实名认证
	 */
	public Integer updateUserMobile(String userName,String mobile,Boolean realNameAuthentication);
	/** 修改用户手机
	 * @param userName 用户名称
	 * @param mobile 手机号
	 * @param realNameAuthentication 是否实名认证
	 * @param platformUserId 平台用户Id
	 */
	public Integer updateUserMobile(String userName,String mobile,Boolean realNameAuthentication,String platformUserId);
	/**
	 * 修改安全摘要
	 * @param userName 用户名称
	 * @param securityDigest 安全摘要
	 */
	public Integer updateUserSecurityDigest(String userName,Long securityDigest);
	/**
	 * 修改用户头像
	 * @param userName 用户名称
	 * @param avatarName 头像名称
	 */
	public Integer updateUserAvatar(String userName,String avatarName);
	/**
	 * 注销账号
	 * @param userName 用户名称
	 * @param appendContent 追加内容
	 * @param cancelAccountTime 注销账号时间
	 * @param securityDigest 安全摘要
	 */
	public Integer cancelAccount(String userName,String appendContent,Long cancelAccountTime,Long securityDigest);
	/**
	 * 标记删除
	 * @param idList Id集合
	 * @return
	 */
	public Integer markDelete(List<Long> idList);
	/**
	 * 删除
	 * @param idList Id集合
	 * @param userNameList 用户名称集合
	 * @return
	 */
	public Integer delete(List<Long> idList,List<String> userNameList);
	/**
	 * 还原用户
	 * @param userList 用户集合
	 * @return
	 */
	public Integer reductionUser(List<User> userList);
	/**
	 * 在线充值
	 * @param paymentRunningNumber 支付流水号
	 * @param userName 用户名称
	 * @param deposit 预存款
	 * @param paymentLog 支付日志
	 * @return
	 */
	public Integer onlineRecharge(String paymentRunningNumber,String userName,BigDecimal deposit,Object paymentLog);
	/**
	 * 增加用户预存款
	 * @param userName 用户名称
	 * @param deposit 预存款
	 * @param paymentLog 支付日志
	 * @return
	 */
	public Integer addUserDeposit(String userName,BigDecimal deposit,Object paymentLog);
	/**
	 * 减少用户预存款
	 * @param userName 用户名称
	 * @param deposit 预存款
	 * @param paymentLog 支付日志
	 * @return
	 */
	public Integer subtractUserDeposit(String userName,BigDecimal deposit,Object paymentLog);
	
	/**
	 * 增加用户积分
	 * @param userName 用户名称
	 * @param point 积分
	 * @param paymentLog 积分日志
	 * @return
	 */
	public Integer addUserPoint(String userName,Long point,Object pointLog);
	/**
	 * 减少用户积分
	 * @param userName 用户名称
	 * @param point 积分
	 * @param pointLog 积分日志
	 * @return
	 */
	public Integer subtractUserPoint(String userName,Long point,Object pointLog);
	
	
	/**
	 * 积分日志分页
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 */
	public QueryResult<PointLog> findPointLogPage(Long userId,String userName,int firstIndex, int maxResult);
	/**
	 * 根据积分Id查询积分日志
	 * @param pointLogId 积分Id
	 */
	public PointLog findPointLogById(String pointLogId);
	/**
	 * 保存积分日志
	 * 先由pointManage.createPointLogObject();方法生成对象再保存
	 * @param pointLog 积分日志
	 */
	public void savePointLog(Object pointLog);
	
	
	/**
	 * 保存用户登录日志
	 * 先由userLoginLogManage.createUserLoginLogObject();方法生成对象再保存
	 * @param userLoginLog 用户登录日志
	 */
	public void saveUserLoginLog(Object userLoginLog);
	
	/**
	 * 根据用户登录日志Id查询用户登录日志
	 * @param userLoginLogId 用户登录日志Id
	 */
	public UserLoginLog findUserLoginLogById(String userLoginLogId);
	
	/**
	 * 用户登录日志分页
	 * @param userId 用户Id
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 */
	public QueryResult<UserLoginLog> findUserLoginLogPage(Long userId,int firstIndex, int maxResult);

	/**
	 * 查询最近一条登录日志
	 * @param userId 用户Id
	 */
	public UserLoginLog findFirstUserLoginLog(Long userId);
	
	/**
	 * 删除用户登录日志
	 * @param endTime 结束时间
	 */
	public void deleteUserLoginLog(Date endTime);
	
	/**
	 * 根据Id查询禁止的用户名称
	 * @param id
	 * @return
	 */
	public DisableUserName findDisableUserNameById(Integer id);
	/**
	 * 查询所有禁止的用户名称
	 * @return
	 */
	public List<DisableUserName> findAllDisableUserName();
	/**
	 * 查询所有禁止的用户名称 缓存
	 */
	public List<DisableUserName> findAllDisableUserName_cache();
	/**
	 * 保存禁止的用户名称
	 * @param disableUserName 禁止的用户名称
	 */
	public void saveDisableUserName(DisableUserName disableUserName);
	/**
	 * 修改禁止的用户名称
	 * @param userName 用户名称
	 * @param password 密码
	 * @param userVersion 版本
	 * @return
	 */
	public void updateDisableUserName(DisableUserName disableUserName);
	/**
	 * 删除禁止的用户名称
	 * @param id 
	 * @return
	 */
	public Integer deleteDisableUserName(Integer id);
	
	/**
	 * 保存用户动态
	 * 先由userDynamicManage.createUserDynamicObject();方法生成对象再保存
	 * @param userDynamic 用户动态
	 */
	public void saveUserDynamic(Object userDynamic);
	/**
	 * 修改话题状态
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param topicId 话题Id
	 * @param status 状态
	 */
	public Integer updateUserDynamicTopicStatus(Long userId,String userName,Long topicId,Integer status);
	/**
	 * 修改评论状态
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param topicId 话题Id
	 * @param commentId 评论Id
	 * @param status 状态
	 */
	public Integer updateUserDynamicCommentStatus(Long userId,String userName,Long topicId,Long commentId,Integer status);
	/**
	 * 修改回复状态
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param topicId 话题Id
	 * @param commentId 评论Id
	 * @param replyId 回复Id
	 * @param status 状态
	 */
	public Integer updateUserDynamicReplyStatus(Long userId,String userName,Long topicId,Long commentId,Long replyId,Integer status);
	/**
	 * 根据话题Id软删除用户动态
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param topicId 话题Id
	 */
	public Integer softDeleteUserDynamicByTopicId(Long userId,String userName,Long topicId);
	/**
	 * 根据话题Id还原用户动态
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param topicId 话题Id
	 */
	public Integer reductionUserDynamicByTopicId(Long userId,String userName,Long topicId);
	/**
	 * 根据话题Id删除用户动态(话题下的评论和回复也同时删除)
	 * @param topicId 话题Id
	 */
	public Integer deleteUserDynamicByTopicId(Long topicId);
	/**
	 * 根据评论Id删除用户动态(评论下的回复也同时删除)
	 * @param topicId 话题Id
	 * @param commentId 评论Id
	 */
	public Integer deleteUserDynamicByCommentId(Long topicId,Long commentId);
	/**
	 * 根据回复Id删除用户动态
	 * @param userId 用户Id
	 * @param topicId 话题Id
	 * @param commentId 评论Id
	 * @param replyId 回复Id
	 */
	public Integer deleteUserDynamicByReplyId(Long userId,Long topicId,Long commentId,Long replyId);
	
	/**
	 * 修改问题状态
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param questionId 问题Id
	 * @param status 状态
	 */
	public Integer updateUserDynamicQuestionStatus(Long userId,String userName,Long questionId,Integer status);
	/**
	 * 修改答案状态
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param questionId 问题Id
	 * @param commentId 评论Id
	 * @param status 状态
	 */
	public Integer updateUserDynamicAnswerStatus(Long userId,String userName,Long questionId,Long answerId,Integer status);
	/**
	 * 修改答案回复状态
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param questionId 问题Id
	 * @param answerId 答案Id
	 * @param answerReplyId 答案回复Id
	 * @param status 状态
	 */
	public Integer updateUserDynamicAnswerReplyStatus(Long userId,String userName,Long questionId,Long answerId,Long answerReplyId,Integer status);
	/**
	 * 根据问题Id软删除用户动态
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param questionId 问题Id
	 */
	public Integer softDeleteUserDynamicByQuestionId(Long userId,String userName,Long questionId);
	
	/**
	 * 根据问题Id还原用户动态
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param questionId 问题Id
	 */
	public Integer reductionUserDynamicByQuestionId(Long userId,String userName,Long questionId);
	/**
	 * 根据问题Id删除用户动态(问题下的答案和回复也同时删除)
	 * @param questionId 问题Id
	 */
	public Integer deleteUserDynamicByQuestionId(Long questionId);
	
	/**
	 * 根据答案Id删除用户动态(答案下的回复也同时删除)
	 * @param questionId 问题Id
	 * @param answerId 答案Id
	 */
	public Integer deleteUserDynamicByAnswerId(Long questionId,Long answerId);
	/**
	 * 根据答案回复Id删除用户动态
	 * @param userId 用户Id
	 * @param questionId 问题Id
	 * @param answerId 答案Id
	 * @param answerReplyId 答案回复Id
	 */
	public Integer deleteUserDynamicByAnswerReplyId(Long userId,Long questionId,Long answerId,Long answerReplyId);
	/**
	 * 用户动态分页
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 */
	public QueryResult<UserDynamic> findUserDynamicPage(Long userId,String userName,int firstIndex, int maxResult);
}
