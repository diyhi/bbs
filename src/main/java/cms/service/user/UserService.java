package cms.service.user;

import java.util.Date;
import java.util.List;

import cms.bean.QueryResult;
import cms.bean.user.DisableUserName;
import cms.bean.user.PointLog;
import cms.bean.user.User;
import cms.bean.user.UserInputValue;
import cms.bean.user.UserLoginLog;
import cms.service.besa.DAO;
/**
 * 用户管理接口
 *
 */
public interface UserService extends DAO<User> {

	/**
	 * 根据条件分页查询用户名称
	 * @param jpql SQL
	 * @param params 参数值
	 * @param firstIndex 开始索引
	 * @param maxResult 需要获取的记录数
	 */
	public List<String> findUserNameByConditionPage(String jpql,List<Object> params,int firstIndex, int maxResult);
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
	 * 保存用户
	 * @param user 用户
	 * @param userInputValueList 用户自定义注册功能项用户输入值
	 */
	public void saveUser(User user,List<UserInputValue> userInputValueList);
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
	*/
	public Integer updateUser(User user, List<UserInputValue> add_userInputValue,List<Long> delete_userInputValueIdList);
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
}
