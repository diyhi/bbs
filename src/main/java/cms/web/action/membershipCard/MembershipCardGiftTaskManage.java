package cms.web.action.membershipCard;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import cms.bean.membershipCard.MembershipCardGiftItem;
import cms.bean.membershipCard.MembershipCardGiftTask;
import cms.bean.membershipCard.RestrictionGroup;
import cms.bean.user.User;
import cms.bean.user.UserRoleGroup;
import cms.service.membershipCard.MembershipCardGiftTaskService;
import cms.service.user.UserRoleService;
import cms.service.user.UserService;
import cms.utils.JsonUtils;
import cms.web.action.user.UserManage;
import cms.web.action.user.UserRoleManage;
import net.sf.cglib.beans.BeanCopier;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 会员卡赠送任务管理
 *
 */
@Component("membershipCardGiftTaskManage")
public class MembershipCardGiftTaskManage {
	private static final Logger logger = LogManager.getLogger(MembershipCardGiftTaskManage.class);
	
    @Resource MembershipCardGiftTaskService membershipCardGiftTaskService;
    @Resource UserManage userManage;
    @Resource MembershipCardGiftItemConfig membershipCardGiftItemConfig;
    @Resource UserRoleService userRoleService;
    @Resource UserRoleManage userRoleManage;
    @Lazy @Resource MembershipCardGiftTaskManage membershipCardGiftTaskManage;
    @Resource UserService userService;
    
    
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
    	
    	List<MembershipCardGiftTask> membershipCardGiftTaskList = membershipCardGiftTaskService.findEnableMembershipCardGiftTask();
    	if(membershipCardGiftTaskList != null && membershipCardGiftTaskList.size() >0){
    		User user = userManage.query_cache_findUserByUserName(userName);
    		if(user == null){
    			return;
    		}
    		
    		for(MembershipCardGiftTask membershipCardGiftTask : membershipCardGiftTaskList){
    			if(!membershipCardGiftTask.isEnable()){
    				continue;
    			}
    			if(membershipCardGiftTask.getExpirationDate_start() != null){
					//当前时间
		            DateTime defaultTime = new DateTime();
		            
		            if(defaultTime.toLocalDateTime().isBefore(new DateTime(membershipCardGiftTask.getExpirationDate_start()).toLocalDateTime())){
		            	continue;
		            }
				}
				if(membershipCardGiftTask.getExpirationDate_end() != null){
					//当前时间
		            DateTime defaultTime = new DateTime();
		            
					if(defaultTime.toLocalDateTime().isAfter(new DateTime(membershipCardGiftTask.getExpirationDate_end()).toLocalDateTime())){
						continue;
		            }
				}
    			if(membershipCardGiftTask.getRestriction() != null && !"".equals(membershipCardGiftTask.getRestriction().trim())){
					RestrictionGroup restrictionGroup= JsonUtils.toObject(membershipCardGiftTask.getRestriction(), RestrictionGroup.class);
					if(restrictionGroup != null){
						membershipCardGiftTask.setRestrictionGroup(restrictionGroup);
					}
				}
    			
    			
				if(membershipCardGiftTask.getRestrictionGroup().getRegistrationTime_start() != null){
					//注册时间
		            DateTime defaultTime = new DateTime(user.getRegistrationDate());
		            
		            if(defaultTime.toLocalDateTime().isBefore(new DateTime(membershipCardGiftTask.getRestrictionGroup().getRegistrationTime_start()).toLocalDateTime())){
		            	continue;
		            }
				}
				if(membershipCardGiftTask.getRestrictionGroup().getRegistrationTime_end() != null){
					//注册时间
		            DateTime defaultTime = new DateTime(user.getRegistrationDate());
		            
					if(defaultTime.toLocalDateTime().isAfter(new DateTime(membershipCardGiftTask.getRestrictionGroup().getRegistrationTime_end()).toLocalDateTime())){
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
			List<User> userList = userService.findUserInfoByConditionPage(param,paramValue,firstindex, maxresult);
			
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
     * @param membershipCardGiftTask
     * @param user
     */
    private void executionGift(MembershipCardGiftTask membershipCardGiftTask,User user){
    	String membershipCardGiftItemId = this.createMembershipCardGiftItemId(membershipCardGiftTask.getId(),user.getId());
    	MembershipCardGiftItem old_membershipCardGiftItem = membershipCardGiftTaskManage.query_cache_findMembershipCardGiftItemById(membershipCardGiftItemId);
		if(old_membershipCardGiftItem != null){//如果当前用户已经赠送过
			return;
		}
    	
    	
    	UserRoleGroup add_userRoleGroup = null;
		UserRoleGroup update_userRoleGroup = null;
		
		UserRoleGroup userRoleGroup = userRoleService.findRoleGroupByUserRoleId(membershipCardGiftTask.getUserRoleId(), user.getUserName());
		if(userRoleGroup == null){
			add_userRoleGroup = new UserRoleGroup();
			add_userRoleGroup.setUserName(user.getUserName());
			add_userRoleGroup.setUserRoleId(membershipCardGiftTask.getUserRoleId());
			DateTime dateTime = new DateTime(new Date());
			
			if(membershipCardGiftTask.getUnit().equals(10)){//时长单位 10.小时
				dateTime = dateTime.plusHours(membershipCardGiftTask.getDuration());// 增加小时
			}else if(membershipCardGiftTask.getUnit().equals(20)){//20.日
				dateTime = dateTime.plusDays(membershipCardGiftTask.getDuration()); // 增加天 
			}else if(membershipCardGiftTask.getUnit().equals(30)){//30.月
				dateTime = dateTime.plusMonths(membershipCardGiftTask.getDuration()); // 增加月
			}else if(membershipCardGiftTask.getUnit().equals(40)){//40.年
				dateTime = dateTime.plusYears(membershipCardGiftTask.getDuration()); // 增加年
			}
			add_userRoleGroup.setValidPeriodEnd(dateTime.toDate());
			
		}else{
			update_userRoleGroup = new UserRoleGroup();
			update_userRoleGroup.setUserName(user.getUserName());
			update_userRoleGroup.setUserRoleId(membershipCardGiftTask.getUserRoleId());
			DateTime dateTime = new DateTime(userRoleGroup.getValidPeriodEnd());
		
			
			//和系统时间比  
			if(dateTime.isAfterNow()){//如果在系统时间之后
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
				dateTime = new DateTime();
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
			
			update_userRoleGroup.setValidPeriodEnd(dateTime.toDate());
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
			membershipCardGiftTaskService.saveMembershipCardGiftItem(this.createMembershipCardGiftItemObject(membershipCardGiftItem), add_userRoleGroup, update_userRoleGroup);
			
			//删除缓存
			userRoleManage.delete_cache_findRoleGroupByUserName(membershipCardGiftItem.getUserName());
			membershipCardGiftTaskManage.delete_cache_findMembershipCardGiftItemById(membershipCardGiftItemId);
			
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
				c = Class.forName("cms.bean.membershipCard.MembershipCardGiftItem_"+tableNumber);
				Object object = c.newInstance();
				
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
			}	
		}
		return null;
    }
   

    /**
	 * 查询缓存 查询查询会员卡赠送项
	 * @param membershipCardGiftItemId 查询会员卡赠送项Id
	 * @return
	 */
	@Cacheable(value="membershipCardGiftTaskManage_cache_findMembershipCardGiftItemById",key="#membershipCardGiftItemId")
	public MembershipCardGiftItem query_cache_findMembershipCardGiftItemById(String membershipCardGiftItemId){
		return membershipCardGiftTaskService.findMembershipCardGiftItemById(membershipCardGiftItemId);
	}
	/**
	 * 删除缓存 查询会员卡赠送项
	 * @param membershipCardGiftItemId 查询会员卡赠送项Id
	 * @return
	 */
	@CacheEvict(value="membershipCardGiftTaskManage_cache_findMembershipCardGiftItemById",key="#membershipCardGiftItemId")
	public void delete_cache_findMembershipCardGiftItemById(String membershipCardGiftItemId){
	}
    
}
