package cms.web.action.message;


import java.util.Date;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import cms.bean.message.SystemNotify;
import cms.service.message.SystemNotifyService;
/**
 * 系统通知管理
 *
 */
@Component("systemNotifyManage")
public class SystemNotifyManage {

	@Resource SystemNotifyService systemNotifyService;
	
	/**
	 * 查询缓存 根据用户Id查询最早的未读系统通知Id
	 * @param userId 用户Id
	 * @return
	 */
	@Cacheable(value="systemNotifyManage_cache_findMinUnreadSystemNotifyIdByUserId",key="#userId")
	public Long query_cache_findMinUnreadSystemNotifyIdByUserId(Long userId){
		return systemNotifyService.findMinUnreadSystemNotifyIdByUserId(userId);
	}
	/**
	 * 删除缓存 根据用户Id查询最早的未读系统通知Id
	 * @param userId 用户Id
	 * @return
	 */
	@CacheEvict(value="systemNotifyManage_cache_findMinUnreadSystemNotifyIdByUserId",key="#userId")
	public void delete_cache_findMinUnreadSystemNotifyIdByUserId(Long userId){
	}
	
	
	/**
	 * 查询缓存 根据用户Id查询最大的已读系统通知Id 
	 * @param userId 用户Id
	 * @return
	 */
	@Cacheable(value="systemNotifyManage_cache_findMaxReadSystemNotifyIdByUserId",key="#userId")
	public Long query_cache_findMaxReadSystemNotifyIdByUserId(Long userId){
		return systemNotifyService.findMaxReadSystemNotifyIdByUserId(userId);
	}
	/**
	 * 删除缓存 根据用户Id查询最大的已读系统通知Id 
	 * @param userId 用户Id
	 * @return
	 */
	@CacheEvict(value="systemNotifyManage_cache_findMaxReadSystemNotifyIdByUserId",key="#userId")
	public void delete_cache_findMaxReadSystemNotifyIdByUserId(Long userId){
	}
	
	
	
	
	/**
	 * 查询缓存 根据起始系统通知Id查询系统通知数量
	 * @param start_systemNotifyId 起始系统通知Id
	 * @return
	 */
	@Cacheable(value="systemNotifyManage_cache_findSystemNotifyCountBySystemNotifyId",key="#start_systemNotifyId")
	public Long query_cache_findSystemNotifyCountBySystemNotifyId(Long start_systemNotifyId){
		return systemNotifyService.findSystemNotifyCountBySystemNotifyId(start_systemNotifyId);
	}
	/**
	 * 删除缓存 根据起始系统通知Id查询系统通知数量
	 * @return
	 */
	@CacheEvict(value="systemNotifyManage_cache_findSystemNotifyCountBySystemNotifyId",allEntries=true)
	public void delete_cache_findSystemNotifyCountBySystemNotifyId(){
	}
	
	
	
	/**
	 * 查询缓存 根据起始系统通知发送时间查询系统通知数量
	 * @param start_sendTime 起始系统通知发送时间
	 * @return
	 */
	@Cacheable(value="systemNotifyManage_cache_findSystemNotifyCountBySendTime",key="#start_sendTime.getTime()")
	public Long query_cache_findSystemNotifyCountBySendTime(Date start_sendTime){
		return systemNotifyService.findSystemNotifyCountBySendTime(start_sendTime);
	}
	/**
	 * 删除缓存 根据起始系统通知发送时间查询系统通知数量
	 * @return
	 */
	@CacheEvict(value="systemNotifyManage_cache_findSystemNotifyCountBySendTime",allEntries=true)
	public void delete_cache_findSystemNotifyCountBySendTime(){
	}
	
	
	
	
	/**
	 * 查询缓存 根据Id查询系统通知
	 * @param systemNotifyId 系统通知Id
	 * @return
	 */
	@Cacheable(value="systemNotifyManage_cache_findById",key="#systemNotifyId")
	public SystemNotify query_cache_findById(Long systemNotifyId){
		return systemNotifyService.findById(systemNotifyId);
	}
	/**
	 * 删除缓存 根据Id查询系统通知
	 * @param systemNotifyId 系统通知Id
	 * @return
	 */
	@CacheEvict(value="systemNotifyManage_cache_findById",key="#systemNotifyId")
	public void delete_cache_findById(Long systemNotifyId){
	}
	

}
