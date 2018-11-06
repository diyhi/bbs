package cms.service.user.impl;

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
import cms.bean.user.UserGrade;
import cms.bean.user.UserInputValue;
import cms.bean.user.UserLoginLog;
import cms.service.besa.DaoSupport;
import cms.service.message.PrivateMessageService;
import cms.service.message.RemindService;
import cms.service.message.SystemNotifyService;
import cms.service.user.UserGradeService;
import cms.service.user.UserService;
import cms.utils.ObjectConversion;
import cms.web.action.user.PointLogConfig;
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
	
	/**
	 * 根据条件分页查询用户名称
	 * @param jpql SQL
	 * @param params 参数值
	 * @param firstIndex 开始索引
	 * @param maxResult 需要获取的记录数
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<String> findUserNameByConditionPage(String jpql,List<Object> params,int firstIndex, int maxResult){
		
		
		
		int placeholder = 1;//占位符参数
		Query query =  em.createQuery("select o.userName from User o"+(jpql == null || "".equals(jpql.trim())? "" : " where "+jpql));
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
		
		return query.getResultList();
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
		String sql ="select t2.id, t2.userName, t2.email, t2.registrationDate, t2.point, t2.state from(select o.id from user o force index(user_idx) "+(param != null && !"".equals(param.trim()) ? " where "+param:" ")+orderBy+" limit ?,?)t1,user t2 where t1.id=t2.id";

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
				user.setEmail(ObjectConversion.conversion(object[2], ObjectConversion.STRING));
				user.setRegistrationDate(ObjectConversion.conversion(object[3], ObjectConversion.TIMESTAMP));
				user.setPoint(ObjectConversion.conversion(object[4], ObjectConversion.LONG));
				user.setState(ObjectConversion.conversion(object[5], ObjectConversion.INTEGER));
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
		String sql ="select t2.id,t2.userName,t2.email,t2.registrationDate,t2.point,t2.state from(select u.userId from  user o, userinputvalue u where "+customParam+param+" group by u.userId having count(u.userId)>=? "+orderBy+" limit ?,?)t1,user t2 where t1.userId=t2.id";

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
				user.setEmail(ObjectConversion.conversion(object[2], ObjectConversion.STRING));
				user.setRegistrationDate(ObjectConversion.conversion(object[3], ObjectConversion.TIMESTAMP));
				user.setPoint(ObjectConversion.conversion(object[4], ObjectConversion.LONG));
				user.setState(ObjectConversion.conversion(object[5], ObjectConversion.INTEGER));
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
	 * 保存用户
	 * @param user 用户
	 * @param userInputValueList 用户自定义注册功能项用户输入值
	 */
	public void saveUser(User user,List<UserInputValue> userInputValueList){
		this.save(user);

		if(userInputValueList != null && userInputValueList.size() >0){	
			for(UserInputValue userInputValue : userInputValueList){
				userInputValue.setUserId(user.getId());
				this.save(userInputValue);
			}
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
				" o.password=?1,o.securityDigest=?2, o.userVersion=o.userVersion+1 where o.id=?3 and o.userVersion=?4")
		.setParameter(1, user.getPassword())//密码
		.setParameter(2, user.getSecurityDigest())//安全摘要
		.setParameter(3, user.getId())//Id
		.setParameter(4, user.getUserVersion());//版本号
		
		
		
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
	 */
	public Integer updateUser(User user, List<UserInputValue> add_userInputValue,List<Long> delete_userInputValueIdList){
	
		Query query = em.createQuery("update User o set " +
				" o.password=?1,o.email=?2, o.issue=?3," +
				" o.answer=?4,  o.state=?5, " +
				" o.remarks=?6,o.mobile=?7,o.realNameAuthentication=?8,o.securityDigest=?9, o.userVersion=o.userVersion+1 where o.id=?10 and o.userVersion=?11")
		.setParameter(1, user.getPassword())//密码
		.setParameter(2, user.getEmail())//Email地址
		.setParameter(3, user.getIssue())//密码提示问题
		.setParameter(4, user.getAnswer())//密码提示答案
		.setParameter(5, user.getState())//用户状态
		.setParameter(6, user.getRemarks())//备注
		.setParameter(7, user.getMobile())//手机
		.setParameter(8, user.isRealNameAuthentication())//实名认证
		.setParameter(9, user.getSecurityDigest())//安全摘要
		.setParameter(10, user.getId())//Id
		.setParameter(11, user.getUserVersion());//版本号
		
		
		
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
}
