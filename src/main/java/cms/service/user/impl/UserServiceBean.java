package cms.service.user.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.QueryResult;
import cms.bean.user.DisableUserName;
import cms.bean.user.PointLog;
import cms.bean.user.User;
import cms.bean.user.UserDynamic;
import cms.bean.user.UserGrade;
import cms.bean.user.UserInputValue;
import cms.bean.user.UserLoginLog;
import cms.bean.user.UserRoleGroup;
import cms.service.besa.DaoSupport;
import cms.service.favorite.FavoriteService;
import cms.service.follow.FollowService;
import cms.service.like.LikeService;
import cms.service.message.PrivateMessageService;
import cms.service.message.RemindService;
import cms.service.message.SystemNotifyService;
import cms.service.payment.PaymentService;
import cms.service.question.AnswerService;
import cms.service.question.QuestionService;
import cms.service.redEnvelope.RedEnvelopeService;
import cms.service.user.UserGradeService;
import cms.service.user.UserRoleService;
import cms.service.user.UserService;
import cms.utils.ObjectConversion;
import cms.web.action.user.PointLogConfig;
import cms.web.action.user.UserDynamicConfig;
import cms.web.action.user.UserLoginLogConfig;
import net.sf.cglib.beans.BeanCopier;
/**
 * 用户管理
 * @Repository 将加有Repository注解的使用JPA或者Hibernate原生API的方法所抛出的异常转化为Spring的DataAccessException中的异常
 *
 * UncategorizedDataAccessException为MYSQL唯一索引重复异常
 */
@Service
@Transactional
//@Repository
public class UserServiceBean extends DaoSupport<User> implements UserService {
	 private static final Logger logger = LogManager.getLogger(UserServiceBean.class);
	
	@Resource PointLogConfig pointLogConfig;
	@Resource UserGradeService userGradeService;

	
	@Resource UserLoginLogConfig userLoginLogConfig;

	@Resource PrivateMessageService privateMessageService;
	@Resource SystemNotifyService systemNotifyService;
	@Resource RemindService remindService;
	@Resource FavoriteService favoriteService;
	@Resource UserDynamicConfig userDynamicConfig;
	
	@Resource LikeService likeService;
	@Resource FollowService followService;
	@Resource UserRoleService userRoleService;
	@Resource PaymentService paymentService;
	@Resource QuestionService questionService;
	@Resource AnswerService answerService;
	@Resource RedEnvelopeService redEnvelopeService;
	
	
	/**
	 * 根据条件分页查询用户信息
	 * @param jpql SQL
	 * @param params 参数值
	 * @param firstIndex 开始索引
	 * @param maxResult 需要获取的记录数
	 * @return 返回值只包含id、userName、point、deposit、registrationDate 等字段值
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<User> findUserInfoByConditionPage(String jpql,List<Object> params,int firstIndex, int maxResult){
		
		List<User> userList = new ArrayList<User>();
		
		int placeholder = 1;//占位符参数
		Query query =  em.createQuery("select o.id, o.userName, o.point, o.deposit, o.registrationDate from User o"+(jpql == null || "".equals(jpql.trim())? "" : " where "+jpql));
		if(params != null && params.size() >0){
			for(Object obj : params){
				query.setParameter(placeholder,obj);
				placeholder++;
			}
		}
		//索引开始,即从哪条记录开始
		query.setFirstResult(firstIndex);
		//获取多少条数据
		query.setMaxResults(maxResult);
		
		
		List<Object[]> objectList = query.getResultList();
		
		if(objectList != null && objectList.size() >0){
			for(int o = 0; o<objectList.size(); o++){
				Object[] object = objectList.get(o);
				
				User user = new User();
				user.setId(ObjectConversion.conversion(object[0], ObjectConversion.LONG));
				user.setUserName(ObjectConversion.conversion(object[1], ObjectConversion.STRING));
				user.setPoint(ObjectConversion.conversion(object[2], ObjectConversion.LONG));
				user.setDeposit(ObjectConversion.conversion(object[3], ObjectConversion.BIGDECIMAL));
				user.setRegistrationDate(ObjectConversion.conversion(object[4], ObjectConversion.TIMESTAMP));
				
				userList.add(user);
			}
		}
		return userList;
	}
	
	
	/**
	 * 根据条件查询用户
	 * @param param 参数
	 * @param paramValue 参数值
	 * @param firstIndex 开始索引
	 * @param maxResult 需要获取的记录数
	 * @param sort 排序 true:升序 false:降序
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<User> findUserByCondition(String param,List<Object> paramValue,int firstIndex, int maxResult,boolean sort){
		QueryResult<User> qr = new QueryResult<User>();

		String orderBy  = "";	
		//排序
		if(sort == true){
			orderBy = " order by id asc ";
		}else{
			orderBy = " order by id desc ";
		}
		
		//mysql强制使用索引:force index(索引名或者主键PRI)
		String sql ="select t2.id, t2.userName,t2.nickname,t2.avatarName, t2.email, t2.registrationDate, t2.point, t2.state,t2.type,t2.platformUserId from(select o.id from user o force index(user_idx) "+(param != null && !"".equals(param.trim()) ? " where "+param:" ")+orderBy+" limit ?,?)t1,user t2 where t1.id=t2.id";

		Query query =  em.createNativeQuery(sql);
		int placeholder = 1;//占位符参数
		if(paramValue != null && paramValue.size() >0){
			for(Object obj : paramValue){
				query.setParameter(placeholder,obj);
				placeholder++;
			}
		}
		query.setParameter(placeholder,firstIndex);//索引开始,即从哪条记录开始
		placeholder++;
		query.setParameter(placeholder,maxResult);//获取多少条数据
		
		List<User> userList = new ArrayList<User>();
		
		List<Object[]> objectList = query.getResultList();
		
		if(objectList != null && objectList.size() >0){
			for(int o = 0; o<objectList.size(); o++){
				Object[] object = objectList.get(o);
				
				User user = new User();
				user.setId(ObjectConversion.conversion(object[0], ObjectConversion.LONG));
				user.setUserName(ObjectConversion.conversion(object[1], ObjectConversion.STRING));
				user.setNickname(ObjectConversion.conversion(object[2], ObjectConversion.STRING));
				user.setAvatarName(ObjectConversion.conversion(object[3], ObjectConversion.STRING));
				user.setEmail(ObjectConversion.conversion(object[4], ObjectConversion.STRING));
				user.setRegistrationDate(ObjectConversion.conversion(object[5], ObjectConversion.TIMESTAMP));
				user.setPoint(ObjectConversion.conversion(object[6], ObjectConversion.LONG));
				user.setState(ObjectConversion.conversion(object[7], ObjectConversion.INTEGER));
				user.setType(ObjectConversion.conversion(object[8], ObjectConversion.INTEGER));
				user.setPlatformUserId(ObjectConversion.conversion(object[9], ObjectConversion.STRING));
				userList.add(user);
			}
		}
		//把查询结果设进去
		qr.setResultlist(userList);
		
		
		String count_sql ="select COUNT(1) c from user o "+(param != null && !"".equals(param.trim()) ? " where "+param:" ");
		
		query = em.createNativeQuery(count_sql);
 
		placeholder = 1;//占位符参数
		if(paramValue != null && paramValue.size() >0){
			for(Object obj : paramValue){
				query.setParameter(placeholder,obj);
				placeholder++;
			}
		}
		Long lCount = 0L;
		for(Object o :query.getResultList()){
			lCount +=Long.parseLong(o.toString());
		}
		//获取总记录数
		qr.setTotalrecord(lCount);
		
		return qr;
	}
	
	/**
	 * 根据自定义条件查询用户
	 * @param param 参数
	 * @param paramValue 参数值
	 * @param customParam 用户自定义注册功能项参数
	 * @param firstIndex 开始索引
	 * @param maxResult 需要获取的记录数
	 * @param sort 排序 true:升序 false:降序
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<User> findUserByCustomCondition(String param,List<Object> paramValue,String customParam,int firstIndex, int maxResult,boolean sort){ 
		QueryResult<User> qr = new QueryResult<User>();

		String orderBy  = "";	
		//排序
		if(sort == true){
			orderBy = " order by o.id asc ";
		}else{
			orderBy = " order by o.id desc ";
		}
		
		//having count(*)=3
		//此处 count(*) = 3 表示的意思是
		//在查询的结果中，只查出按group 分组之后，每个组的有3条数据的结果集 


		//mysql强制使用索引:force index(索引名或者主键PRI)
		String sql ="select t2.id,t2.userName,t2.nickname,t2.email,t2.registrationDate,t2.point,t2.state,t2.type,t2.platformUserId from(select u.userId from  user o, userinputvalue u where "+customParam+param+" group by u.userId having count(u.userId)>=? "+orderBy+" limit ?,?)t1,user t2 where t1.userId=t2.id";

		Query query =  em.createNativeQuery(sql);

		int placeholder = 1;//占位符参数

		if(paramValue != null && paramValue.size() >0){
			for(Object obj : paramValue){
				query.setParameter(placeholder,obj);
				placeholder++;
			}
		}
		query.setParameter(placeholder,firstIndex);//索引开始,即从哪条记录开始
		placeholder++;
		query.setParameter(placeholder,maxResult);//获取多少条数据
		
		List<User> userList = new ArrayList<User>();
		List<Object[]> objectList = query.getResultList();
		if(objectList != null && objectList.size() >0){
			for(int o = 0; o<objectList.size(); o++){
				Object[] object = objectList.get(o);
				User user = new User();
				user.setId(ObjectConversion.conversion(object[0], ObjectConversion.LONG));
				user.setUserName(ObjectConversion.conversion(object[1], ObjectConversion.STRING));
				user.setNickname(ObjectConversion.conversion(object[2], ObjectConversion.STRING));
				user.setEmail(ObjectConversion.conversion(object[3], ObjectConversion.STRING));
				user.setRegistrationDate(ObjectConversion.conversion(object[4], ObjectConversion.TIMESTAMP));
				user.setPoint(ObjectConversion.conversion(object[5], ObjectConversion.LONG));
				user.setState(ObjectConversion.conversion(object[6], ObjectConversion.INTEGER));
				user.setType(ObjectConversion.conversion(object[7], ObjectConversion.INTEGER));
				user.setPlatformUserId(ObjectConversion.conversion(object[8], ObjectConversion.STRING));
				userList.add(user);
	
			}
		}
		//把查询结果设进去
		qr.setResultlist(userList);
		
		
		String count_sql ="select count(t2.id) c from (select u.userId from  user o, userinputvalue u where "+customParam+param+" group by u.userId having count(u.userId)>=?) t1,user t2 where t1.userId=t2.id";
		query = em.createNativeQuery(count_sql.toString());
		placeholder = 1;//占位符参数
		if(paramValue != null && paramValue.size() >0){
			for(Object obj : paramValue){
				query.setParameter(placeholder,obj);
				placeholder++;
			}
		}
		
		Long lCount = 0L;
		for(Object o :query.getResultList()){
			lCount +=Long.parseLong(o.toString());
		}
		//获取总记录数
		qr.setTotalrecord(lCount);
		
		return qr;
	}
	/**
	 * 根据用户名称查询用户状态
	 * @param userName 用户名称
	 
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public Integer findUserStateByUserName(String userName){
	
		Query query =  em.createQuery("select o.state from User o where o.userName=?1");
		//设置参数 
		query.setParameter(1, userName);
		List<Integer> stateList = query.getResultList();
		if(stateList != null && stateList.size() >0){
			for(Integer state : stateList){
				return state;
			}
		}
		
		return null;
	}*/
	
	/**
	 * 根据用户Id查询当前用户
	 * @param id 用户Id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public User findUserById(Long id){
		Query query =  em.createQuery("select o from User o where o.id=?1");
		//设置参数 
		query.setParameter(1, id);
		List<User> userList = query.getResultList();
		for(User user : userList){
			List<UserGrade> userGradeList = userGradeService.findAllGrade();//取得用户所有等级
			if(userGradeList != null && userGradeList.size() >0){
				for(UserGrade userGrade : userGradeList){
					if(user.getPoint() >= userGrade.getNeedPoint()){
						user.setGradeId(userGrade.getId());
						user.setGradeName(userGrade.getName());//将等级值设进等级参数里
						break;
					}
				} 
			}
			return user;
		}
		return null;
		
	}
	
	
	
	
	/**
	 * 根据用户Id集合查询当前用户
	 * @param userIdList 用户Id集合
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<User> findUserByUserIdList(List<Long> userIdList){
		Query query =  em.createQuery("select o from User o where o.id in(:id)");
		//设置参数 
		query.setParameter("id", userIdList);
		return query.getResultList();
		
	}
	/**
	 * 根据用户名查询当前用户
	 * @param userName 用户名称
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public User findUserByUserName(String userName){
		Query query =  em.createQuery("select o from User o where o.userName=?1");
		//设置参数 
		query.setParameter(1, userName);
		List<User> userList = query.getResultList();
		for(User user : userList){	
			return user;
		}
		return null;
		
	}
	/**
	 * 根据呢称查询当前用户
	 * @param nickname 呢称
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public User findUserByNickname(String nickname){
		Query query =  em.createQuery("select o from User o where o.nickname=?1");
		//设置参数 
		query.setParameter(1, nickname);
		List<User> userList = query.getResultList();
		for(User user : userList){	
			return user;
		}
		return null;
		
	}
	
	/**
	 * 根据平台用户Id查询当前用户
	 * @param platformUserId 平台用户Id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public User findUserByPlatformUserId(String platformUserId){
		Query query =  em.createQuery("select o from User o where o.platformUserId=?1");
		//设置参数 
		query.setParameter(1, platformUserId);
		List<User> userList = query.getResultList();
		for(User user : userList){
			List<UserGrade> userGradeList = userGradeService.findAllGrade();//取得用户所有等级
			if(userGradeList != null && userGradeList.size() >0){
				for(UserGrade userGrade : userGradeList){
					if(user.getPoint() >= userGrade.getNeedPoint()){
						user.setGradeId(userGrade.getId());
						user.setGradeName(userGrade.getName());//将等级值设进等级参数里
						break;
					}
				} 
			}
			return user;
		}
		return null;
		
	}
	
	
	/**
	 * 保存用户
	 * @param user 用户
	 * @param userInputValueList 用户自定义注册功能项用户输入值
	 * @param userRoleGroupList 用户角色组集合
	 */
	public void saveUser(User user,List<UserInputValue> userInputValueList, List<UserRoleGroup> userRoleGroupList){
		this.save(user);

		if(userInputValueList != null && userInputValueList.size() >0){	
			for(UserInputValue userInputValue : userInputValueList){
				userInputValue.setUserId(user.getId());
				this.save(userInputValue);
			}
		}
		if(userRoleGroupList != null && userRoleGroupList.size() >0){
			userRoleService.saveUserRoleGroup(userRoleGroupList);
		}
	}
	
	/**
	 * 前台修改用户
	 * @param user 用户
	 * @param add_userInputValue 添加注册功能项用户输入值集合
	 * @param delete_userInputValueIdList 删除注册功能项用户输入值Id集合
	 */
	public Integer updateUser2(User user, List<UserInputValue> add_userInputValue,List<Long> delete_userInputValueIdList){
	
		Query query = em.createQuery("update User o set " +
				" o.nickname=?1, o.allowUserDynamic=?2, o.password=?3,o.securityDigest=?4, o.userVersion=o.userVersion+1 where o.id=?5 and o.userVersion=?6")
		.setParameter(1, user.getNickname())//呢称
		.setParameter(2, user.getAllowUserDynamic())//允许显示用户动态 
		.setParameter(3, user.getPassword())//密码
		.setParameter(4, user.getSecurityDigest())//安全摘要
		.setParameter(5, user.getId())//Id
		.setParameter(6, user.getUserVersion());//版本号
		
		
		
		int i = query.executeUpdate();
		
		if(add_userInputValue != null && add_userInputValue.size() >0){	
			for(UserInputValue userInputValue : add_userInputValue){
				userInputValue.setUserId(user.getId());
				this.save(userInputValue);
			}
		}
		
		if(delete_userInputValueIdList != null && delete_userInputValueIdList.size() >0){
			Query delete = em.createQuery("delete from UserInputValue o where o.id in(:id)")
					.setParameter("id", delete_userInputValueIdList);
					delete.executeUpdate();
		}
		
		
		return i;
	}
	
	/**
	 * 修改用户
	 * @param user 用户
	 * @param add_userInputValue 添加注册功能项用户输入值集合
	 * @param delete_userInputValueIdList 删除注册功能项用户输入值Id集合
	 * @param userRoleGroupList 用户角色组集合
	 */
	public Integer updateUser(User user, List<UserInputValue> add_userInputValue,List<Long> delete_userInputValueIdList, List<UserRoleGroup> userRoleGroupList){
	
		Query query = em.createQuery("update User o set " +
				" o.nickname=?1, o.allowUserDynamic=?2, o.password=?3,o.email=?4, o.issue=?5," +
				" o.answer=?6,  o.state=?7, " +
				" o.remarks=?8,o.mobile=?9,o.realNameAuthentication=?10,o.securityDigest=?11,o.platformUserId=?12, o.userVersion=o.userVersion+1 where o.id=?13 and o.userVersion=?14")
		.setParameter(1, user.getNickname())//呢称
		.setParameter(2, user.getAllowUserDynamic())//允许显示用户动态 
		.setParameter(3, user.getPassword())//密码
		.setParameter(4, user.getEmail())//Email地址
		.setParameter(5, user.getIssue())//密码提示问题
		.setParameter(6, user.getAnswer())//密码提示答案
		.setParameter(7, user.getState())//用户状态
		.setParameter(8, user.getRemarks())//备注
		.setParameter(9, user.getMobile())//手机
		.setParameter(10, user.isRealNameAuthentication())//实名认证
		.setParameter(11, user.getSecurityDigest())//安全摘要
		.setParameter(12, user.getPlatformUserId())//平台用户Id
		.setParameter(13, user.getId())//Id
		.setParameter(14, user.getUserVersion());//版本号
		
		
		
		int i = query.executeUpdate();
		
		if(add_userInputValue != null && add_userInputValue.size() >0){	
			for(UserInputValue userInputValue : add_userInputValue){
				userInputValue.setUserId(user.getId());
				this.save(userInputValue);
			}
		}
		
		if(delete_userInputValueIdList != null && delete_userInputValueIdList.size() >0){
			Query delete = em.createQuery("delete from UserInputValue o where o.id in(:id)")
					.setParameter("id", delete_userInputValueIdList);
					delete.executeUpdate();
		}
		
		userRoleService.updateUserRoleGroup(user.getUserName(), userRoleGroupList);
		
		
		return i;
	}

	/**
	 * 修改密码
	 * @param userName 用户名称
	 * @param password 密码
	 * @param securityDigest 安全摘要
	 * @param userVersion 版本
	 * @return
	 */
	public Integer updatePassword(String userName,String password,Long securityDigest,Integer userVersion){
		Query query = em.createQuery("update User o set o.password=?1,o.securityDigest=?2,o.userVersion=o.userVersion+1 where o.userName=?3 and o.userVersion=?4")
		.setParameter(1, password)
		.setParameter(2, securityDigest)
		.setParameter(3, userName)
		.setParameter(4, userVersion);
		
		return query.executeUpdate();
		
	}
	
	/**
	 * 修改用户手机
	 * @param userName 用户名称
	 * @param mobile 手机号
	 * @param realNameAuthentication 是否实名认证
	 */
	public Integer updateUserMobile(String userName,String mobile,Boolean realNameAuthentication){
		Query query = em.createQuery("update User o set " +
				" o.mobile=?1,o.realNameAuthentication=?2, o.userVersion=o.userVersion+1 where o.userName=?3")
		.setParameter(1, mobile)
		.setParameter(2, realNameAuthentication)
		.setParameter(3, userName);
		return query.executeUpdate();
	}
	/**
	 * 修改用户手机
	 * @param userName 用户名称
	 * @param mobile 手机号
	 * @param realNameAuthentication 是否实名认证
	 * @param platformUserId 平台用户Id
	 */
	public Integer updateUserMobile(String userName,String mobile,Boolean realNameAuthentication,String platformUserId){
		Query query = em.createQuery("update User o set " +
				" o.mobile=?1,o.realNameAuthentication=?2,o.platformUserId=?3, o.userVersion=o.userVersion+1 where o.userName=?4")
		.setParameter(1, mobile)
		.setParameter(2, realNameAuthentication)
		.setParameter(3, platformUserId)
		.setParameter(4, userName);
		return query.executeUpdate();
	}
	/**
	 * 修改安全摘要
	 * @param userName 用户名称
	 * @param securityDigest 安全摘要
	 */
	public Integer updateUserSecurityDigest(String userName,Long securityDigest){
		Query query = em.createQuery("update User o set " +
				" o.securityDigest=?1, o.userVersion=o.userVersion+1 where o.userName=?2")
		.setParameter(1, securityDigest)
		.setParameter(2, userName);
		return query.executeUpdate();
	}
	/**
	 * 修改用户头像
	 * @param userName 用户名称
	 * @param avatarName 头像名称
	 */
	public Integer updateUserAvatar(String userName,String avatarName){
		Query query = em.createQuery("update User o set " +
				" o.avatarName=?1, o.userVersion=o.userVersion+1 where o.userName=?2")
		.setParameter(1, avatarName)
		.setParameter(2, userName);
		return query.executeUpdate();
	}
	/**
	 * 标记删除
	 * @param idList 用户Id集合
	 * @return
	 */
	public Integer markDelete(List<Long> idList){
		Query query = em.createQuery("update User o set o.state=o.state+10,o.userVersion=o.userVersion+1 where o.id in(:id) and o.state < :state")
			.setParameter("id", idList)
		.setParameter("state", 10);
		return query.executeUpdate();
	}
	/**
	 * 删除
	 * @param idList 用户Id集合
	 * @param userNameList 用户名称集合
	 * @return
	 */
	public Integer delete(List<Long> idList,List<String> userNameList){
		
		Query delete = em.createQuery("delete from User o where o.id in(:id)")
			.setParameter("id", idList);
		int j = delete.executeUpdate();
		
		//用户自定义注册功能项用户输入值
		
		Query delete2 = em.createQuery("delete from UserInputValue o where o.userId in(:id)")
		.setParameter("id", idList);
		delete2.executeUpdate();
			
		int pointLog_tableNumber = pointLogConfig.getTableQuantity();
		for(int i = 0; i<pointLog_tableNumber; i++){
			if(i == 0){//默认对象
				//删除积分日志
				Query query_pointLog = em.createQuery("delete from PointLog o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				query_pointLog.executeUpdate();
			}else{//带下划线对象
				Query query_pointLog = em.createQuery("delete from PointLog_"+i+" o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				query_pointLog.executeUpdate();
			}
		}
		
		//删除登录日志
		this.deleteUserLoginLog(idList);
		
		//删除话题
		Query delete_topic = em.createQuery("delete from Topic o where o.userName in(:userName) and o.isStaff=:isStaff")
				.setParameter("userName", userNameList)
				.setParameter("isStaff", false);
		delete_topic.executeUpdate();
		
		//删除评论
		Query delete_comment = em.createQuery("delete from Comment o where o.userName in(:userName) and o.isStaff=:isStaff")
				.setParameter("userName", userNameList)
				.setParameter("isStaff", false);
		delete_comment.executeUpdate();
		
		//删除回复
		Query delete_reply = em.createQuery("delete from Reply o where o.userName in(:userName) and o.isStaff=:isStaff")
				.setParameter("userName", userNameList)
				.setParameter("isStaff", false);
		delete_reply.executeUpdate();
		
		//删除私信
		privateMessageService.deleteUserPrivateMessage(idList);
		//删除用户所有订阅系统通知
		systemNotifyService.deleteUserSubscriptionSystemNotify(idList);
		
		
		//删除用户提醒
		remindService.deleteRemindByUserId(idList);
		
		//删除收藏夹
		favoriteService.deleteFavoriteByUserName(userNameList);
		
		//删除点赞
		likeService.deleteLikeByUserName(userNameList);
		

		//删除关注
		followService.deleteFollowByUserName(userNameList);
		
		//根据话题/问题的用户名称删除收藏
		favoriteService.deleteFavoriteByPostUserName(userNameList);

		//删除用户动态
		this.deleteUserDynamic(userNameList);
		
		//删除用户角色组
		userRoleService.deleteUserRoleGroup(userNameList);
		
		//删除问题
		questionService.deleteQuestion(userNameList);
		
		//删除答案
		answerService.deleteAnswer(userNameList);
		
		//删除回复
		answerService.deleteAnswerReply(userNameList);
		//删除问题标签关联
		questionService.deleteQuestionTagAssociationByUserId(userNameList);
		
		//删除发红包
		redEnvelopeService.deleteGiveRedEnvelope(idList);
		
		//删除收红包
		redEnvelopeService.deleteReceiveRedEnvelope(idList);
		
		return j;
	}
	/**
	 * 还原用户
	 * @param userList 用户集合
	 * @return
	 */
	public Integer reductionUser(List<User> userList){
		int i = 0;
		if(userList != null && userList.size() >0){
			for(User user : userList){
				Query query = em.createQuery("update User o set o.state=?1,o.userVersion=o.userVersion+1 where o.id=?2")
				.setParameter(1, user.getState())
				.setParameter(2, user.getId());
				int j = query.executeUpdate();
				i += j;
			}
		}
		return i;
	}
	/**
	 * 在线充值
	 * @param paymentRunningNumber 支付流水号
	 * @param userName 用户名称
	 * @param deposit 预存款
	 * @param paymentLog 支付日志
	 * @return
	 */
	public Integer onlineRecharge(String paymentRunningNumber,String userName,BigDecimal deposit,Object paymentLog){
		int j = 0;
		//删除校验支付校验日志
		int i = paymentService.deletePaymentVerificationLogById(paymentRunningNumber);
		if(i >0){
			
			j = this.addUserDeposit(userName,deposit,paymentLog);
		}
		return j;
	}
	
	/**
	 * 增加用户预存款
	 * @param userName 用户名称
	 * @param deposit 预存款
	 * @param paymentLog 支付日志
	 * @return
	 */
	public Integer addUserDeposit(String userName,BigDecimal deposit,Object paymentLog){
		int i = 0;
		Query query = em.createQuery("update User o set o.deposit=o.deposit+?1,o.userVersion=o.userVersion+1 where o.userName=?2")
		.setParameter(1, deposit)
		.setParameter(2, userName);
		i = query.executeUpdate();
		if(i >0){
			//保存支付日志
			if(paymentLog != null){
				paymentService.savePaymentLog(paymentLog);
			}
		}
		
		return i;
	}
	/**
	 * 减少用户预存款
	 * @param userName 用户名称
	 * @param deposit 预存款
	 * @param paymentLog 支付日志
	 * @return
	 */
	public Integer subtractUserDeposit(String userName,BigDecimal deposit,Object paymentLog){
		int i = 0;
		Query query = em.createQuery("update User o set o.deposit=o.deposit-?1,o.userVersion=o.userVersion+1 where o.userName=?2 and o.deposit>=?3")
		.setParameter(1, deposit)
		.setParameter(2, userName)
		.setParameter(3, deposit);
		i = query.executeUpdate();
		if(i >0){
			//保存支付日志
			if(paymentLog != null){
				paymentService.savePaymentLog(paymentLog);
			}
		}
		
		return i;
	}
	
	
	
	/**
	 * 增加用户积分
	 * @param userName 用户名称
	 * @param point 积分
	 * @param paymentLog 积分日志
	 * @return
	 */
	public Integer addUserPoint(String userName,Long point,Object pointLog){
		int i = 0;
		Query query = em.createQuery("update User o set o.point=o.point+?1,o.userVersion=o.userVersion+1 where o.userName=?2")
		.setParameter(1, point)
		.setParameter(2, userName);
		i = query.executeUpdate();
		if(i >0){
			//保存支付日志
			if(pointLog != null){
				this.savePointLog(pointLog);
			}
		}
		
		return i;
	}
	
	
	/**
	 * 减少用户积分(积分必须大于0)
	 * @param userName 用户名称
	 * @param point 积分
	 * @param pointLog 积分日志
	 * @return
	 */
	public Integer subtractUserPoint(String userName,Long point,Object pointLog){
		int i = 0;
		Query query = em.createQuery("update User o set o.point=o.point-?1,o.userVersion=o.userVersion+1 where o.userName=?2 and o.point>=?3")
		.setParameter(1, point)
		.setParameter(2, userName)
		.setParameter(3, point);
		i = query.executeUpdate();
		if(i >0){
			//保存支付日志
			if(pointLog != null){
				this.savePointLog(pointLog);
			}
		}
		return i;
	}

	
	/**----------------------------------- 积分 -------------------------------------**/
	/**
	 * 保存积分日志
	 * 先由pointManage.createPointLogObject();方法生成对象再保存
	 * @param pointLog 积分日志
	 */
	public void savePointLog(Object pointLog){
		this.save(pointLog);	
	}
	
	/**
	 * 根据积分Id查询积分日志
	 * @param pointLogId 积分Id
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public PointLog findPointLogById(String pointLogId){
		
		Query query  = null;
		//表编号
		int tableNumber = pointLogConfig.pointLogIdRemainder(pointLogId);
		if(tableNumber == 0){//默认对象为HistoryOrder
			query = em.createQuery("select o from PointLog o where o.id=?1")
			.setParameter(1, pointLogId);
			
			List<PointLog> pointLogList= query.getResultList();
			if(pointLogList != null && pointLogList.size() >0){
				for(PointLog pointLog : pointLogList){
					return pointLog;
				}
			}
		}else{//带下划线对象
			query = em.createQuery("select o from PointLog_"+tableNumber+" o where o.id=?1")
			.setParameter(1, pointLogId);
			
			List<?> pointLog_List= query.getResultList();
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.user.PointLog_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),PointLog.class, false); 
				for(int j = 0;j< pointLog_List.size(); j++) {  
					Object obj = pointLog_List.get(j);
					PointLog pointLog = new PointLog();
					copier.copy(obj,pointLog, null);
					return pointLog;
				}
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据积分Id查询积分日志",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据积分Id查询积分日志",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据积分Id查询积分日志",e);
		        }
			}
		}
		
		return null;
	}
	
	/**
	 * 积分日志分页
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<PointLog> findPointLogPage(Long userId,String userName,int firstIndex, int maxResult){
		
		QueryResult<PointLog> qr = new QueryResult<PointLog>();
		Query query  = null;
		
		
		//表编号
		int tableNumber = pointLogConfig.userIdRemainder(userId);
		if(tableNumber == 0){//默认对象为HistoryOrder
			query = em.createQuery("select o from PointLog o where o.userName=?1 ORDER BY o.times desc")
			.setParameter(1, userName);
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<PointLog> pointLogList= query.getResultList();
			qr.setResultlist(pointLogList);
			
			query = em.createQuery("select count(o) from PointLog o where o.userName=?1")
					.setParameter(1, userName);
			qr.setTotalrecord((Long)query.getSingleResult());
		}else{//带下划线对象
			query = em.createQuery("select o from PointLog_"+tableNumber+" o where o.userName=?1 ORDER BY o.times desc")
			.setParameter(1, userName);
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<?> pointLog_List= query.getResultList();
			
			
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.user.PointLog_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),PointLog.class, false); 
				List<PointLog> pointLogList= new ArrayList<PointLog>();
				for(int j = 0;j< pointLog_List.size(); j++) {  
					Object obj = pointLog_List.get(j);
					PointLog pointLog = new PointLog();
					copier.copy(obj,pointLog, null);
					pointLogList.add(pointLog);
				}
				qr.setResultlist(pointLogList);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("积分日志分页",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("积分日志分页",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("积分日志分页",e);
		        }
			}
			
			query = em.createQuery("select count(o) from PointLog_"+tableNumber+" o where o.userName=?1")
					.setParameter(1, userName);
			qr.setTotalrecord((Long)query.getSingleResult());
		}
		
		return qr;
	}
	
	/**----------------------------------- 用户登录日志 -------------------------------------**/
	
	
	/**
	 * 保存用户登录日志
	 * 先由userLoginLogManage.createUserLoginLogObject();方法生成对象再保存
	 * @param userLoginLog 用户登录日志
	 */
	public void saveUserLoginLog(Object userLoginLog){
		this.save(userLoginLog);	
	}
	
	/**
	 * 根据用户登录日志Id查询用户登录日志
	 * @param userLoginLogId 用户登录日志Id
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public UserLoginLog findUserLoginLogById(String userLoginLogId){
		
		Query query  = null;
		//表编号
		int tableNumber = userLoginLogConfig.userLoginLogIdRemainder(userLoginLogId);
		if(tableNumber == 0){//默认对象为HistoryOrder
			query = em.createQuery("select o from UserLoginLog o where o.id=?1")
			.setParameter(1, userLoginLogId);
			
			List<UserLoginLog> userLoginLogList= query.getResultList();
			if(userLoginLogList != null && userLoginLogList.size() >0){
				for(UserLoginLog userLoginLog : userLoginLogList){
					return userLoginLog;
				}
			}
		}else{//带下划线对象
			query = em.createQuery("select o from UserLoginLog_"+tableNumber+" o where o.id=?1")
			.setParameter(1, userLoginLogId);
			
			List<?> userLoginLog_List= query.getResultList();
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.user.UserLoginLog_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),UserLoginLog.class, false); 
				for(int j = 0;j< userLoginLog_List.size(); j++) {  
					Object obj = userLoginLog_List.get(j);
					UserLoginLog userLoginLog = new UserLoginLog();
					copier.copy(obj,userLoginLog, null);
					return userLoginLog;
				}
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户登录日志Id查询用户登录日志",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户登录日志Id查询用户登录日志",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户登录日志Id查询用户登录日志",e);
		        }
			}
		}
		
		return null;
	}
	
	/**
	 * 用户登录日志分页
	 * @param userId 用户Id
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<UserLoginLog> findUserLoginLogPage(Long userId,int firstIndex, int maxResult){
		
		QueryResult<UserLoginLog> qr = new QueryResult<UserLoginLog>();
		Query query  = null;
		
		
		//表编号
		int tableNumber = userLoginLogConfig.userIdRemainder(userId);
		if(tableNumber == 0){//默认对象为HistoryOrder
			query = em.createQuery("select o from UserLoginLog o where o.userId=?1 ORDER BY o.logonTime desc")
			.setParameter(1, userId);
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<UserLoginLog> userLoginLogList= query.getResultList();
			qr.setResultlist(userLoginLogList);
			
			query = em.createQuery("select count(o) from UserLoginLog o where o.userId=?1")
					.setParameter(1, userId);
			qr.setTotalrecord((Long)query.getSingleResult());
		}else{//带下划线对象
			query = em.createQuery("select o from UserLoginLog_"+tableNumber+" o where o.userId=?1 ORDER BY o.logonTime desc")
			.setParameter(1, userId);
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<?> userLoginLog_List= query.getResultList();
			
			
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.user.UserLoginLog_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),UserLoginLog.class, false); 
				List<UserLoginLog> userLoginLogList= new ArrayList<UserLoginLog>();
				for(int j = 0;j< userLoginLog_List.size(); j++) {  
					Object obj = userLoginLog_List.get(j);
					UserLoginLog userLoginLog = new UserLoginLog();
					copier.copy(obj,userLoginLog, null);
					userLoginLogList.add(userLoginLog);
				}
				qr.setResultlist(userLoginLogList);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("用户登录日志分页",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("用户登录日志分页",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("用户登录日志分页",e);
		        }
			}
			
			query = em.createQuery("select count(o) from UserLoginLog_"+tableNumber+" o where o.userId=?1")
					.setParameter(1, userId);
			qr.setTotalrecord((Long)query.getSingleResult());
		}
		
		return qr;
	}
	
	/**
	 * 删除用户登录日志
	 * @param userIdList 用户Id集合
	 */
	private Integer deleteUserLoginLog(List<Long> userIdList){
		int j = 0;
		int userLoginLog_tableNumber = userLoginLogConfig.getTableQuantity();
		for(int i = 0; i<userLoginLog_tableNumber; i++){
			if(i == 0){//默认对象
				//删除积分日志
				Query query_userLoginLog = em.createQuery("delete from UserLoginLog o where o.userId in(:userId)")
						.setParameter("userId", userIdList);
				j += query_userLoginLog.executeUpdate();
			}else{//带下划线对象
				Query query_userLoginLog = em.createQuery("delete from UserLoginLog_"+i+" o where o.userId in(:userId)")
						.setParameter("userId", userIdList);
				j += query_userLoginLog.executeUpdate();
			}
		}
		return j;
	}
	
	/**
	 * 删除用户登录日志
	 * @param endTime 结束时间
	 */
	public void deleteUserLoginLog(Date endTime){
		
		//表数量
		int tableQuantity = userLoginLogConfig.getTableQuantity();
		for(int i =0; i<tableQuantity; i++){
			
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from UserLoginLog o where o.logonTime<:date")
					.setParameter("date", endTime);
				query.executeUpdate();
			}else{
				Query query = em.createQuery("delete from UserLoginLog_"+i+" o where o.logonTime<:date")
						.setParameter("date", endTime);
					query.executeUpdate();
			}
		}
		
	}
	
	/**----------------------------------- 注册禁止的用户名称 -------------------------------------**/
	/**
	 * 根据Id查询禁止的用户名称
	 * @param id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public DisableUserName findDisableUserNameById(Integer id){
		Query query =  em.createQuery("select o from DisableUserName o where o.id=?1");
		//设置参数 
		query.setParameter(1, id);
		List<DisableUserName> disableUserNameList = query.getResultList();
		for(DisableUserName disableUserName : disableUserNameList){
			return disableUserName;
		}
		return null;
		
	}
	/**
	 * 查询所有禁止的用户名称
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<DisableUserName> findAllDisableUserName(){
		Query query =  em.createQuery("select o from DisableUserName o");
		List<DisableUserName> disableUserNameList = query.getResultList();
		return disableUserNameList;
	}
	/**
	 * 查询所有禁止的用户名称 缓存
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	@Cacheable(value="userServiceBean_disableUserName_cache",key="'findAllDisableUserName_default'")
	public List<DisableUserName> findAllDisableUserName_cache(){
		return this.findAllDisableUserName();
	}
	/**
	 * 保存禁止的用户名称
	 * @param disableUserName 禁止的用户名称
	 */
	@CacheEvict(value="userServiceBean_disableUserName_cache",allEntries=true)
	public void saveDisableUserName(DisableUserName disableUserName){
		this.save(disableUserName);
	}
	/**
	 * 修改禁止的用户名称
	 * @param userName 用户名称
	 * @param password 密码
	 * @param userVersion 版本
	 * @return
	 */
	@CacheEvict(value="userServiceBean_disableUserName_cache",allEntries=true)
	public void updateDisableUserName(DisableUserName disableUserName){
		this.update(disableUserName);
		
	}
	/**
	 * 删除禁止的用户名称
	 * @param id 
	 * @return
	 */
	@CacheEvict(value="userServiceBean_disableUserName_cache",allEntries=true)
	public Integer deleteDisableUserName(Integer id){
		Query delete = em.createQuery("delete from DisableUserName o where o.id=?1")
				.setParameter(1, id);
		return	delete.executeUpdate();
	}
	
/**----------------------------------- 用户动态 -------------------------------------**/
	
	
	/**
	 * 保存用户动态
	 * 先由userDynamicManage.createUserDynamicObject();方法生成对象再保存
	 * @param userDynamic 用户动态
	 */
	public void saveUserDynamic(Object userDynamic){
		this.save(userDynamic);	
	}
	
	/**
	 * 修改话题状态
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param topicId 话题Id
	 * @param status 状态
	 */
	public Integer updateUserDynamicTopicStatus(Long userId,String userName,Long topicId,Integer status){
		int i = 0;
		
		//功能Id组
		String functionIdGroup = ","+topicId+",";
		
		//表编号
		int tableNumber = userDynamicConfig.userIdRemainder(userId);
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("update UserDynamic o set o.status=?1 where o.functionIdGroup like ?2 and o.userName=?3 and o.module=?4")
					.setParameter(1, status)
					.setParameter(2, functionIdGroup+"%")
					.setParameter(3, userName)
					.setParameter(4, 100);
			i += query.executeUpdate();
			
				
			
		}else{//带下划线对象
			Query query = em.createQuery("update UserDynamic_"+tableNumber+" o set o.status=?1 where o.functionIdGroup like ?2 and o.userName=?3 and o.module=?4")
					.setParameter(1, status)
					.setParameter(2, functionIdGroup+"%")
					.setParameter(3, userName)
					.setParameter(4, 100);
			i += query.executeUpdate();	
		}
		return i;
	}
	/**
	 * 修改评论状态
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param topicId 话题Id
	 * @param commentId 评论Id
	 * @param status 状态
	 */
	public Integer updateUserDynamicCommentStatus(Long userId,String userName,Long topicId,Long commentId,Integer status){
		int i = 0;
		//功能Id组
		String functionIdGroup = ","+topicId+","+commentId+",";
		
		//表编号
		int tableNumber = userDynamicConfig.userIdRemainder(userId);
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("update UserDynamic o set o.status=?1 where o.functionIdGroup like ?2 and o.userName=?3 and o.module>=?4 and o.module<=?5")
					.setParameter(1, status)
					.setParameter(2, functionIdGroup+"%")
					.setParameter(3, userName)
					.setParameter(4, 200)
					.setParameter(5, 300);
			i += query.executeUpdate();
			
		}else{//带下划线对象
			Query query = em.createQuery("update UserDynamic_"+tableNumber+" o set o.status=?1 where o.functionIdGroup like ?2 and o.userName=?3 and o.module>=?4 and o.module<=?5")
					.setParameter(1, status)
					.setParameter(2, functionIdGroup+"%")
					.setParameter(3, userName)
					.setParameter(4, 200)
					.setParameter(5, 300);
			i += query.executeUpdate();	
		}
		return i;
	}
	/**
	 * 修改回复状态
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param topicId 话题Id
	 * @param commentId 评论Id
	 * @param replyId 回复Id
	 * @param status 状态
	 */
	public Integer updateUserDynamicReplyStatus(Long userId,String userName,Long topicId,Long commentId,Long replyId,Integer status){
		int i = 0;
		//功能Id组
		String functionIdGroup = ","+topicId+","+commentId+","+replyId+",";
		
		//表编号
		int tableNumber = userDynamicConfig.userIdRemainder(userId);
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("update UserDynamic o set o.status=?1 where o.functionIdGroup like ?2 and o.userName=?3 and o.module=?4")
					.setParameter(1, status)
					.setParameter(2, functionIdGroup+"%")
					.setParameter(3, userName)
					.setParameter(4, 400);
			i += query.executeUpdate();
			
		}else{//带下划线对象
			Query query = em.createQuery("update UserDynamic_"+tableNumber+" o set o.status=?1 where o.functionIdGroup like ?2 and o.userName=?3 and o.module=?4")
					.setParameter(1, status)
					.setParameter(2, functionIdGroup+"%")
					.setParameter(3, userName)
					.setParameter(4, 400);
			i += query.executeUpdate();	
		}
		return i;
	}
	
	/**
	 * 根据话题Id软删除用户动态
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param topicId 话题Id
	 */
	public Integer softDeleteUserDynamicByTopicId(Long userId,String userName,Long topicId){
		int i = 0;
		
		//功能Id组
		String functionIdGroup = ","+topicId+",";
				
		//表编号
		int tableNumber = userDynamicConfig.userIdRemainder(userId);
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("update UserDynamic o set o.status=o.status+100 where o.functionIdGroup like ?1 and o.userName=?2 and o.module=?3 and o.status <100")
					.setParameter(1, functionIdGroup+"%")
					.setParameter(2, userName)
					.setParameter(3, 100);
			i += query.executeUpdate();
			
		}else{//带下划线对象
			Query query = em.createQuery("update UserDynamic_"+tableNumber+" o set o.status=o.status+100 where o.functionIdGroup like ?1 and o.userName=?2 and o.module=?3 and o.status <100")
					.setParameter(1, functionIdGroup+"%")
					.setParameter(2, userName)
					.setParameter(3, 100);
			i += query.executeUpdate();	
		}
		return i;
		
	}
	
	/**
	 * 根据话题Id还原用户动态
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param topicId 话题Id
	 */
	public Integer reductionUserDynamicByTopicId(Long userId,String userName,Long topicId){
		int i = 0;
		//功能Id组
		String functionIdGroup = ","+topicId+",";
		
		//表编号
		int tableNumber = userDynamicConfig.userIdRemainder(userId);
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("update UserDynamic o set o.status=o.status-100 where o.functionIdGroup like ?1 and o.userName=?2 and o.module=?3 and o.status >100")
					.setParameter(1, functionIdGroup+"%")
					.setParameter(2, userName)
					.setParameter(3, 100);
			i += query.executeUpdate();
			
		}else{//带下划线对象
			Query query = em.createQuery("update UserDynamic_"+tableNumber+" o set o.status=o.status-100 where o.functionIdGroup like ?1 and o.userName=?2 and o.module=?3 and o.status >100")
					.setParameter(1, functionIdGroup+"%")
					.setParameter(2, userName)
					.setParameter(3, 100);
			i += query.executeUpdate();	
		}
		return i;
		
	}
	
	
	
	
	/**
	 * 根据话题Id删除用户动态(话题下的评论和回复也同时删除)
	 * @param topicId 话题Id
	 */
	public Integer deleteUserDynamicByTopicId(Long topicId){
		int j = 0;
		
		//功能Id组
		String functionIdGroup = ","+topicId+",";
		
		//表数量
		int tableQuantity = userLoginLogConfig.getTableQuantity();
		for(int i =0; i<tableQuantity; i++){
			
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from UserDynamic o where o.functionIdGroup like ?1")
						.setParameter(1, functionIdGroup+"%");
				j += query.executeUpdate();
			}else{
				Query query = em.createQuery("delete from UserDynamic_"+i+" o where o.functionIdGroup like ?1")
						.setParameter(1, functionIdGroup+"%");
				j += query.executeUpdate();	
			}
		}
		return j;
	}
	
	/**
	 * 根据评论Id删除用户动态(评论下的回复也同时删除)
	 * @param topicId 话题Id
	 * @param commentId 评论Id
	 */
	public Integer deleteUserDynamicByCommentId(Long topicId,Long commentId){
		int j = 0;
		
		//功能Id组
		String functionIdGroup = ","+topicId+","+commentId+",";
		
		//表数量
		int tableQuantity = userLoginLogConfig.getTableQuantity();
		for(int i =0; i<tableQuantity; i++){
			
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from UserDynamic o where o.functionIdGroup like ?1")
						.setParameter(1, functionIdGroup+"%");
				j += query.executeUpdate();
			}else{
				Query query = em.createQuery("delete from UserDynamic_"+i+" o where o.functionIdGroup like ?1")
						.setParameter(1, functionIdGroup+"%");
				j += query.executeUpdate();	
			}
		}
		return j;
	}
	/**
	 * 根据回复Id删除用户动态
	 * @param userId 用户Id
	 * @param topicId 话题Id
	 * @param commentId 评论Id
	 * @param replyId 回复Id
	 */
	public Integer deleteUserDynamicByReplyId(Long userId,Long topicId,Long commentId,Long replyId){
		int i = 0;
		
		//功能Id组
		String functionIdGroup = ","+topicId+","+commentId+","+replyId+",";
		
		//表编号
		int tableNumber = userDynamicConfig.userIdRemainder(userId);
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("delete from UserDynamic o where o.functionIdGroup like ?1")
					.setParameter(1, functionIdGroup+"%");
			i += query.executeUpdate();

		}else{//带下划线对象
			Query query = em.createQuery("delete from UserDynamic_"+tableNumber+" o where o.functionIdGroup like ?1")
					.setParameter(1, functionIdGroup+"%");
			i += query.executeUpdate();	
		}
		return i;
	}
	
	/**
	 * 根据用户名称删除用户动态
	 * @param userNameList 用户名称集合
	 */
	private Integer deleteUserDynamic(List<String> userNameList){
		int j = 0;
		int tableNumber = userDynamicConfig.getTableQuantity();
		for(int i = 0; i<tableNumber; i++){
			if(i == 0){//默认对象
				//删除
				Query query = em.createQuery("delete from UserDynamic o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
			}else{//带下划线对象
				Query query = em.createQuery("delete from UserDynamic_"+i+" o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
			}
		}
		return j;
	}
	
	
	
	
	/**
	 * 修改问题状态
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param questionId 问题Id
	 * @param status 状态
	 */
	public Integer updateUserDynamicQuestionStatus(Long userId,String userName,Long questionId,Integer status){
		int i = 0;
		
		//功能Id组
		String functionIdGroup = ","+questionId+",";

		//表编号
		int tableNumber = userDynamicConfig.userIdRemainder(userId);
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("update UserDynamic o set o.status=?1 where o.functionIdGroup like ?2 and o.userName=?3 and o.module=?4")
					.setParameter(1, status)
					.setParameter(2, functionIdGroup+"%")
					.setParameter(3, userName)
					.setParameter(4, 500);
			i += query.executeUpdate();
			
		}else{//带下划线对象
			Query query = em.createQuery("update UserDynamic_"+tableNumber+" o set o.status=?1 where o.functionIdGroup like ?2 and o.userName=?3 and o.module=?4")
					.setParameter(1, status)
					.setParameter(2, functionIdGroup+"%")
					.setParameter(3, userName)
					.setParameter(4, 500);
			i += query.executeUpdate();	
		}
		return i;
	}
	/**
	 * 修改答案状态
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param questionId 问题Id
	 * @param answerId 答案Id
	 * @param status 状态
	 */
	public Integer updateUserDynamicAnswerStatus(Long userId,String userName,Long questionId,Long answerId,Integer status){
		int i = 0;
		//功能Id组
		String functionIdGroup = ","+questionId+","+answerId+",";
		
		//表编号
		int tableNumber = userDynamicConfig.userIdRemainder(userId);
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("update UserDynamic o set o.status=?1 where o.functionIdGroup like ?2 and o.userName=?3 and o.module=?4")
					.setParameter(1, status)
					.setParameter(2, functionIdGroup+"%")
					.setParameter(3, userName)
					.setParameter(4, 600);
			i += query.executeUpdate();
			
		}else{//带下划线对象
			Query query = em.createQuery("update UserDynamic_"+tableNumber+" o set o.status=?1 where o.functionIdGroup like ?2 and o.userName=?3 and o.module=?4")
					.setParameter(1, status)
					.setParameter(2, functionIdGroup+"%")
					.setParameter(3, userName)
					.setParameter(4, 600);
			i += query.executeUpdate();	
		}
		return i;
	}
	/**
	 * 修改答案回复状态
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param questionId 问题Id
	 * @param answerId 答案Id
	 * @param answerReplyId 答案回复Id
	 * @param status 状态
	 */
	public Integer updateUserDynamicAnswerReplyStatus(Long userId,String userName,Long questionId,Long answerId,Long answerReplyId,Integer status){
		int i = 0;
		//功能Id组
		String functionIdGroup = ","+questionId+","+answerId+","+answerReplyId+",";

		//表编号
		int tableNumber = userDynamicConfig.userIdRemainder(userId);
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("update UserDynamic o set o.status=?1 where o.functionIdGroup like ?2 and o.userName=?3 and o.module=?4")
					.setParameter(1, status)
					.setParameter(2, functionIdGroup+"%")
					.setParameter(3, userName)
					.setParameter(4, 700);
			i += query.executeUpdate();
			
		}else{//带下划线对象
			
			Query query = em.createQuery("update UserDynamic_"+tableNumber+" o set o.status=?1 where o.functionIdGroup like ?2 and o.userName=?3 and o.module=?4")
					.setParameter(1, status)
					.setParameter(2, functionIdGroup+"%")
					.setParameter(3, userName)
					.setParameter(4, 700);
			i += query.executeUpdate();	
		}
		return i;
	}
	
	/**
	 * 根据问题Id软删除用户动态
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param questionId 问题Id
	 */
	public Integer softDeleteUserDynamicByQuestionId(Long userId,String userName,Long questionId){
		int i = 0;
		
		//功能Id组
		String functionIdGroup = ","+questionId+",";
		
		//表编号
		int tableNumber = userDynamicConfig.userIdRemainder(userId);
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("update UserDynamic o set o.status=o.status+100 where o.functionIdGroup like ?1 and o.userName=?2 and o.module=?3 and o.status <100")
					.setParameter(1, functionIdGroup+"%")
					.setParameter(2, userName)
					.setParameter(3, 500);
			i += query.executeUpdate();
			
		}else{//带下划线对象
			Query query = em.createQuery("update UserDynamic_"+tableNumber+" o set o.status=o.status+100 where o.functionIdGroup like ?1 and o.userName=?2 and o.module=?3 and o.status <100")
					.setParameter(1, functionIdGroup+"%")
					.setParameter(2, userName)
					.setParameter(3, 500);
			i += query.executeUpdate();	
		}
		return i;
		
	}
	
	/**
	 * 根据问题Id还原用户动态
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param questionId 问题Id
	 */
	public Integer reductionUserDynamicByQuestionId(Long userId,String userName,Long questionId){
		int i = 0;
		
		//功能Id组
		String functionIdGroup = ","+questionId+",";
				
		//表编号
		int tableNumber = userDynamicConfig.userIdRemainder(userId);
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("update UserDynamic o set o.status=o.status-100 where o.functionIdGroup like ?1 and o.userName=?2 and o.module=?3 and o.status >100")
					.setParameter(1, functionIdGroup+"%")
					.setParameter(2, userName)
					.setParameter(3, 500);
			i += query.executeUpdate();
			
		}else{//带下划线对象
			Query query = em.createQuery("update UserDynamic_"+tableNumber+" o set o.status=o.status-100 where o.functionIdGroup like ?1 and o.userName=?2 and o.module=?3 and o.status >100")
					.setParameter(1, functionIdGroup+"%")
					.setParameter(2, userName)
					.setParameter(3, 500);
			i += query.executeUpdate();	
		}
		return i;
		
	}
	
	/**
	 * 根据问题Id删除用户动态(问题下的答案和回复也同时删除)
	 * @param questionId 问题Id
	 */
	public Integer deleteUserDynamicByQuestionId(Long questionId){
		int j = 0;
		
		//功能Id组
		String functionIdGroup = ","+questionId+",";
				
		//表数量
		int tableQuantity = userLoginLogConfig.getTableQuantity();
		for(int i =0; i<tableQuantity; i++){
			
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from UserDynamic o where o.functionIdGroup like ?1")
						.setParameter(1, functionIdGroup+"%");
				j += query.executeUpdate();
			}else{
				Query query = em.createQuery("delete from UserDynamic_"+i+" o where o.functionIdGroup like ?1")
						.setParameter(1, functionIdGroup+"%");
				j += query.executeUpdate();	
			}
		}
		return j;
	}
	
	/**
	 * 根据答案Id删除用户动态(答案下的回复也同时删除)
	 * @param questionId 问题Id
	 * @param answerId 答案Id
	 */
	public Integer deleteUserDynamicByAnswerId(Long questionId,Long answerId){
		int j = 0;
		
		//功能Id组
		String functionIdGroup = ","+questionId+","+answerId+",";
				
		//表数量
		int tableQuantity = userLoginLogConfig.getTableQuantity();
		for(int i =0; i<tableQuantity; i++){
			
			if(i == 0){//默认对象
				Query query = em.createQuery("delete from UserDynamic o where o.functionIdGroup like ?1")
						.setParameter(1, functionIdGroup+"%");
				j += query.executeUpdate();
			}else{
				Query query = em.createQuery("delete from UserDynamic_"+i+" o where o.functionIdGroup like ?1")
						.setParameter(1, functionIdGroup+"%");
				j += query.executeUpdate();	
			}
		}
		return j;
	}
	/**
	 * 根据答案回复Id删除用户动态
	 * @param userId 用户Id
	 * @param questionId 问题Id
	 * @param answerId 答案Id
	 * @param answerReplyId 答案回复Id
	 */
	public Integer deleteUserDynamicByAnswerReplyId(Long userId,Long questionId,Long answerId,Long answerReplyId){
		int i = 0;
		
		//功能Id组
		String functionIdGroup = ","+questionId+","+answerId+","+answerReplyId+",";
		
		//表编号
		int tableNumber = userDynamicConfig.userIdRemainder(userId);
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("delete from UserDynamic o where o.functionIdGroup like ?1")
					.setParameter(1, functionIdGroup+"%");
			i += query.executeUpdate();

		}else{//带下划线对象
			Query query = em.createQuery("delete from UserDynamic_"+tableNumber+" o where o.functionIdGroup like ?1")
					.setParameter(1, functionIdGroup+"%");
			i += query.executeUpdate();	
		}
		return i;
	}
	
	
	
	/**
	 * 用户动态分页
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<UserDynamic> findUserDynamicPage(Long userId,String userName,int firstIndex, int maxResult){
		
		QueryResult<UserDynamic> qr = new QueryResult<UserDynamic>();
		Query query  = null;
		
		
		//表编号
		int tableNumber = userDynamicConfig.userIdRemainder(userId);
		if(tableNumber == 0){//默认对象
			query = em.createQuery("select o from UserDynamic o where o.userName=?1 and o.status=?2 ORDER BY o.postTime desc")
			.setParameter(1, userName)
			.setParameter(2, 20);
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<UserDynamic> userDynamicList= query.getResultList();
			qr.setResultlist(userDynamicList);
			
			query = em.createQuery("select count(o) from UserDynamic o where o.userName=?1 and o.status=?2")
					.setParameter(1, userName)
					.setParameter(2, 20);
			qr.setTotalrecord((Long)query.getSingleResult());
		}else{//带下划线对象
			query = em.createQuery("select o from UserDynamic_"+tableNumber+" o where o.userName=?1 and o.status=?2 ORDER BY o.postTime desc")
			.setParameter(1, userName)
			.setParameter(2, 20);
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<?> userDynamic_List= query.getResultList();
			
			
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.user.UserDynamic_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),UserDynamic.class, false); 
				List<UserDynamic> userDynamicList= new ArrayList<UserDynamic>();
				for(int j = 0;j< userDynamic_List.size(); j++) {  
					Object obj = userDynamic_List.get(j);
					UserDynamic userDynamic = new UserDynamic();
					copier.copy(obj,userDynamic, null);
					userDynamicList.add(userDynamic);
				}
				qr.setResultlist(userDynamicList);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("用户动态分页",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("用户动态分页",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("用户动态分页",e);
		        }
			}
			
			query = em.createQuery("select count(o) from UserDynamic_"+tableNumber+" o where o.userName=?1 and o.status=?2")
					.setParameter(1, userName)
					.setParameter(2, 20);
			qr.setTotalrecord((Long)query.getSingleResult());
		}
		
		return qr;
	}
}
