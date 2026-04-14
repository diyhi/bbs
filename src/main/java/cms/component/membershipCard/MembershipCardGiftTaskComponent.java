package cms.component.membershipCard;



import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import cms.component.JsonComponent;
import cms.component.user.UserCacheManager;
import cms.component.user.UserRoleCacheManager;
import cms.model.membershipCard.MembershipCardGiftItem;
import cms.model.membershipCard.MembershipCardGiftTask;
import cms.model.membershipCard.RestrictionGroup;
import cms.model.user.User;
import cms.model.user.UserRoleGroup;
import cms.repository.membershipCard.MembershipCardGiftTaskRepository;
import cms.repository.user.UserRepository;
import cms.repository.user.UserRoleRepository;
import jakarta.annotation.Resource;
import org.springframework.cglib.beans.BeanCopier;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 会员卡赠送任务组件
 *
 */
@Component("membershipCardGiftTaskComponent")
public class MembershipCardGiftTaskComponent {
	private static final Logger logger = LogManager.getLogger(MembershipCardGiftTaskComponent.class);

    @Resource MembershipCardGiftTaskCacheManager membershipCardGiftTaskCacheManager;
    @Resource MembershipCardGiftTaskRepository membershipCardGiftTaskRepository;
    @Resource UserCacheManager userCacheManager;

    @Resource MembershipCardGiftItemConfig membershipCardGiftItemConfig;
    @Resource UserRoleRepository userRoleRepository;
    @Resource UserRoleCacheManager userRoleCacheManager;
    @Resource UserRepository userRepository;
    @Resource JsonComponent jsonComponent;
    
    /**
     * 异步 触发会员卡赠送任务(长期任务类型)
     * @param userName 用户名称
     */
    @Async("taskExecutor")
    public void async_triggerMembershipCardGiftTask(String userName){
    	try {
    		this.triggerMembershipCardGiftTask(userName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("触发会员卡赠送任务(长期任务类型)异步执行异常",e);
	        }
		} 
    	
    }
    
    /**
     * 触发会员卡赠送任务(长期任务类型)
     * @param userName 用户名称
     */
    private void triggerMembershipCardGiftTask(String userName){
    	
    	List<MembershipCardGiftTask> membershipCardGiftTaskList = membershipCardGiftTaskRepository.findEnableMembershipCardGiftTask();
    	if(membershipCardGiftTaskList != null && membershipCardGiftTaskList.size() >0){
    		User user = userCacheManager.query_cache_findUserByUserName(userName);
    		if(user == null){
    			return;
    		}
    		
    		for(MembershipCardGiftTask membershipCardGiftTask : membershipCardGiftTaskList){
    			if(!membershipCardGiftTask.isEnable()){
    				continue;
    			}
    			if(membershipCardGiftTask.getExpirationDate_start() != null){
					//当前时间
                    LocalDateTime defaultTime = LocalDateTime.now();
		            
		            if(defaultTime.isBefore(membershipCardGiftTask.getExpirationDate_start())){
		            	continue;
		            }
				}
				if(membershipCardGiftTask.getExpirationDate_end() != null){
					//当前时间
                    LocalDateTime defaultTime = LocalDateTime.now();
		            
					if(defaultTime.isAfter(membershipCardGiftTask.getExpirationDate_end())){
						continue;
		            }
				}
    			if(membershipCardGiftTask.getRestriction() != null && !membershipCardGiftTask.getRestriction().trim().isEmpty()){
					RestrictionGroup restrictionGroup= jsonComponent.toObject(membershipCardGiftTask.getRestriction(), RestrictionGroup.class);
					if(restrictionGroup != null){
						membershipCardGiftTask.setRestrictionGroup(restrictionGroup);
					}
				}
    			
    			
				if(membershipCardGiftTask.getRestrictionGroup().getRegistrationTime_start() != null){
					//注册时间
		            if(user.getRegistrationDate().isBefore(membershipCardGiftTask.getRestrictionGroup().getRegistrationTime_start())){
		            	continue;
		            }
				}
				if(membershipCardGiftTask.getRestrictionGroup().getRegistrationTime_end() != null){
					//注册时间
					if(user.getRegistrationDate().isAfter(membershipCardGiftTask.getRestrictionGroup().getRegistrationTime_end())){
						continue;
		            }
				}
				
				if(membershipCardGiftTask.getRestrictionGroup().getTotalPoint() != null){
					if(user.getPoint() < membershipCardGiftTask.getRestrictionGroup().getTotalPoint()){
						continue;
					}
					
				}
				
				
				
				//执行赠送
				this.executionGift(membershipCardGiftTask,user);
				
				
    		}
    	}
    	
    	
    }
    
    
    /**
     * 异步 对符合条件的会员执行会员卡赠送任务(一次性任务类型)
     */
    @Async("taskExecutor")
    public void async_executionMembershipCardGiftTask(MembershipCardGiftTask membershipCardGiftTask){
    	try {
    		this.executionMembershipCardGiftTask(membershipCardGiftTask);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("对符合条件的会员执行会员卡赠送任务(一次性任务类型)异步执行异常",e);
	        }
		} 
    	
    }
    
    /**
     * 对符合条件的会员执行会员卡赠送任务(一次性任务类型)
     */
    private void executionMembershipCardGiftTask(MembershipCardGiftTask membershipCardGiftTask){
    	int page = 1;//分页 当前页
		int maxresult = 200;// 每页显示记录数

		String param = "";//sql参数
		List<Object> paramValue = new ArrayList<Object>();//sql参数值
		if(membershipCardGiftTask.getRestrictionGroup().getRegistrationTime_start() != null){//注册时间超始
			param += " and o.registrationDate >= ?"+ (paramValue.size()+1);
			paramValue.add(membershipCardGiftTask.getRestrictionGroup().getRegistrationTime_start());
		}
		if(membershipCardGiftTask.getRestrictionGroup().getRegistrationTime_end() != null){//注册时间结束
			param += " and o.registrationDate <= ?"+ (paramValue.size()+1);
			paramValue.add(membershipCardGiftTask.getRestrictionGroup().getRegistrationTime_end());
		}
		
		if(membershipCardGiftTask.getRestrictionGroup().getTotalPoint() != null){//积分
			param += " and o.point >= ?"+ (paramValue.size()+1);
			paramValue.add(membershipCardGiftTask.getRestrictionGroup().getTotalPoint());
		}
		//未注销账号的用户
		param += " and o.cancelAccountTime = ?"+ (paramValue.size()+1);
		paramValue.add(-1L);
				
		//删除第一个and
		param = StringUtils.difference(" and", param);
		
				
		while(true){
			//当前页
			int firstindex = (page-1)*maxresult;
			
			
			
			//查询用户
			List<User> userList = userRepository.findUserInfoByConditionPage(param,paramValue,firstindex, maxresult);
			
			if(userList == null || userList.size() == 0){
				break;
			}
			
			
			for(User user : userList){
				
				//执行赠送
				this.executionGift(membershipCardGiftTask,user);
				
				//删除缓存
				//userManage.delete_cache_findUserById(user.getId());
				//userManage.delete_cache_findUserByUserName(user.getUserName());
			}
			page++;
		}
    	
    }
    
    
    
    /**
     * 执行赠送
     * @param membershipCardGiftTask 会员卡赠送任务
     * @param user 用户
     */
    private void executionGift(MembershipCardGiftTask membershipCardGiftTask,User user){
    	String membershipCardGiftItemId = this.createMembershipCardGiftItemId(membershipCardGiftTask.getId(),user.getId());
    	MembershipCardGiftItem old_membershipCardGiftItem = membershipCardGiftTaskCacheManager.query_cache_findMembershipCardGiftItemById(membershipCardGiftItemId);
		if(old_membershipCardGiftItem != null){//如果当前用户已经赠送过
			return;
		}
    	
    	
    	UserRoleGroup add_userRoleGroup = null;
		UserRoleGroup update_userRoleGroup = null;
		
		UserRoleGroup userRoleGroup = userRoleRepository.findRoleGroupByUserRoleId(membershipCardGiftTask.getUserRoleId(), user.getUserName());
		if(userRoleGroup == null){
			add_userRoleGroup = new UserRoleGroup();
			add_userRoleGroup.setUserName(user.getUserName());
			add_userRoleGroup.setUserRoleId(membershipCardGiftTask.getUserRoleId());
            LocalDateTime dateTime = LocalDateTime.now();
			
			if(membershipCardGiftTask.getUnit().equals(10)){//时长单位 10.小时
				dateTime = dateTime.plusHours(membershipCardGiftTask.getDuration());// 增加小时
			}else if(membershipCardGiftTask.getUnit().equals(20)){//20.日
				dateTime = dateTime.plusDays(membershipCardGiftTask.getDuration()); // 增加天 
			}else if(membershipCardGiftTask.getUnit().equals(30)){//30.月
				dateTime = dateTime.plusMonths(membershipCardGiftTask.getDuration()); // 增加月
			}else if(membershipCardGiftTask.getUnit().equals(40)){//40.年
				dateTime = dateTime.plusYears(membershipCardGiftTask.getDuration()); // 增加年
			}

			add_userRoleGroup.setValidPeriodEnd(dateTime);

		}else{
			update_userRoleGroup = new UserRoleGroup();
			update_userRoleGroup.setUserName(user.getUserName());
			update_userRoleGroup.setUserRoleId(membershipCardGiftTask.getUserRoleId());
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime dateTime = userRoleGroup.getValidPeriodEnd();
			//和系统时间比  
			if(dateTime.isAfter(now)){//如果在系统时间之后
				if(membershipCardGiftTask.getUnit().equals(10)){//时长单位 10.小时
					dateTime = dateTime.plusHours(membershipCardGiftTask.getDuration());// 增加小时
				}else if(membershipCardGiftTask.getUnit().equals(20)){//20.日
					dateTime = dateTime.plusDays(membershipCardGiftTask.getDuration()); // 增加天 
				}else if(membershipCardGiftTask.getUnit().equals(30)){//30.月
					dateTime = dateTime.plusMonths(membershipCardGiftTask.getDuration()); // 增加月
				}else if(membershipCardGiftTask.getUnit().equals(40)){//40.年
					dateTime = dateTime.plusYears(membershipCardGiftTask.getDuration()); // 增加年
				}
			}else{//如果已过期,则有效期从当前时间算起
				dateTime = LocalDateTime.now();
				if(membershipCardGiftTask.getUnit().equals(10)){//时长单位 10.小时
					dateTime = dateTime.plusHours(membershipCardGiftTask.getDuration());// 增加小时
				}else if(membershipCardGiftTask.getUnit().equals(20)){//20.日
					dateTime = dateTime.plusDays(membershipCardGiftTask.getDuration()); // 增加天 
				}else if(membershipCardGiftTask.getUnit().equals(30)){//30.月
					dateTime = dateTime.plusMonths(membershipCardGiftTask.getDuration()); // 增加月
				}else if(membershipCardGiftTask.getUnit().equals(40)){//40.年
					dateTime = dateTime.plusYears(membershipCardGiftTask.getDuration()); // 增加年
				}
			}

			update_userRoleGroup.setValidPeriodEnd(dateTime);
		}
		
		
		MembershipCardGiftItem membershipCardGiftItem = new MembershipCardGiftItem();
		membershipCardGiftItem.setId(this.createMembershipCardGiftItemId(membershipCardGiftTask.getId(),user.getId()));
		membershipCardGiftItem.setType(membershipCardGiftTask.getType());
		membershipCardGiftItem.setMembershipCardGiftTaskId(membershipCardGiftTask.getId());
		membershipCardGiftItem.setUserName(user.getUserName());
		membershipCardGiftItem.setDuration(membershipCardGiftTask.getDuration());
		membershipCardGiftItem.setUnit(membershipCardGiftTask.getUnit());
		membershipCardGiftItem.setRestriction(membershipCardGiftTask.getRestriction());
		
		try {
			membershipCardGiftTaskRepository.saveMembershipCardGiftItem(this.createMembershipCardGiftItemObject(membershipCardGiftItem), add_userRoleGroup, update_userRoleGroup);
			
			//删除缓存
			userRoleCacheManager.delete_cache_findRoleGroupByUserName(membershipCardGiftItem.getUserName());
            membershipCardGiftTaskCacheManager.delete_cache_findMembershipCardGiftItemById(membershipCardGiftItemId);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("重复主键冲突",e);
	        }
		} 
		
		
		
    }
    
    
    
    /**
     * 生成会员卡赠送项Id
     * 格式: 会员卡赠送任务Id - 用户Id
     * @param membershipCardGiftTaskId 会员卡赠送任务Id
     * @param userId 用户Id
     * @return
     */
    public String createMembershipCardGiftItemId(Long membershipCardGiftTaskId,Long userId){
    	return membershipCardGiftTaskId+"-"+userId;
    }
    
    /**
     * 生成会员卡赠送项对象
     * @return
     */
    public Object createMembershipCardGiftItemObject(MembershipCardGiftItem membershipCardGiftItem){
    	//表编号
		int tableNumber = membershipCardGiftItemConfig.membershipCardGiftItemRemainder(membershipCardGiftItem.getId());
		if(tableNumber == 0){//默认对象为PaymentLog
			return membershipCardGiftItem;
		}else{//带下划线对象
			Class<?> c;
			try {
				c = Class.forName("cms.model.membershipCard.MembershipCardGiftItem_"+tableNumber);
                //获取无参数构造函数
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
				
				BeanCopier copier = BeanCopier.create(MembershipCardGiftItem.class,object.getClass(), false); 
			
				copier.copy(membershipCardGiftItem,object, null);
				return object;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成会员卡赠送项对象",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成会员卡赠送项对象",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成会员卡赠送项对象",e);
		        }
			} catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成会员卡赠送项对象",e);
                }
            } catch (NoSuchMethodException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成会员卡赠送项对象",e);
                }
            }
		}
		return null;
    }
   



}
