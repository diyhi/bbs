package cms.repository.membershipCard.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import cms.component.membershipCard.MembershipCardGiftItemConfig;
import cms.config.BusinessException;
import cms.dto.QueryResult;
import cms.model.membershipCard.MembershipCardGiftItem;
import cms.model.membershipCard.MembershipCardGiftTask;
import cms.model.user.UserRoleGroup;
import cms.repository.besa.DaoSupport;
import cms.repository.membershipCard.MembershipCardGiftTaskRepository;
import cms.repository.user.UserRoleRepository;
import jakarta.annotation.Resource;
import jakarta.persistence.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import org.springframework.cglib.beans.BeanCopier;

/**
 * 会员卡赠送任务管理接口实现类
 *
 */
@Repository
@Transactional
public class MembershipCardGiftTaskRepositoryImpl extends DaoSupport<MembershipCardGiftTask> implements MembershipCardGiftTaskRepository {
	private static final Logger logger = LogManager.getLogger(MembershipCardGiftTaskRepositoryImpl.class);
	
	@Resource UserRoleRepository userRoleRepository;
	@Resource MembershipCardGiftItemConfig membershipCardGiftItemConfig;
	
	/**
	 * 根据Id查询会员卡赠送任务
	 * @param membershipCardGiftTaskId 会员卡赠送任务Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public MembershipCardGiftTask findById(Long membershipCardGiftTaskId){
		Query query = em.createQuery("select o from MembershipCardGiftTask o where o.id=?1")
		.setParameter(1, membershipCardGiftTaskId);
		List<MembershipCardGiftTask> list = query.getResultList();
		for(MembershipCardGiftTask p : list){
			return p;
		}
		return null;
	}
	
	/**
	 * 查询启用的长期会员卡赠送任务
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	@Cacheable(value="membershipCardGiftTaskRepositoryImpl_cache",key="'default'")
	public List<MembershipCardGiftTask> findEnableMembershipCardGiftTask(){
        LocalDateTime currentTime = LocalDateTime.now();
		Query query = em.createQuery("select o from MembershipCardGiftTask o where o.expirationDate_start<=?1 and o.expirationDate_end>=?2 and o.type=?3 and o.enable=?4")
				.setParameter(1, currentTime)
				.setParameter(2, currentTime)
				.setParameter(3, 10)
				.setParameter(4, true);
		return query.getResultList();
	}
	
	
	/**
	 * 保存会员卡赠送任务
	 * @param membershipCardGiftTask 会员卡赠送任务
	 */
	@CacheEvict(value="membershipCardGiftTaskRepositoryImpl_cache",allEntries=true)
	public void saveMembershipCardGiftTask(MembershipCardGiftTask membershipCardGiftTask){
		this.save(membershipCardGiftTask);
	}
	

	/**
	 * 修改会员卡赠送任务
	 * @param membershipCardGiftTask 会员卡赠送任务
	 * @return
	 */
	@CacheEvict(value="membershipCardGiftTaskRepositoryImpl_cache",allEntries=true)
	public Integer updateMembershipCardGiftTask(MembershipCardGiftTask membershipCardGiftTask){
		
		Query query = em.createQuery("update MembershipCardGiftTask o set o.name=?1,o.userRoleId=?2, o.userRoleName=?3,o.restriction=?4,"
				+ " o.expirationDate_start=?5,o.expirationDate_end=?6,o.enable=?7,o.duration=?8,o.unit=?9,"
				+ " o.version=o.version+1 where o.id=?10")
		.setParameter(1, membershipCardGiftTask.getName())
		.setParameter(2, membershipCardGiftTask.getUserRoleId())
		.setParameter(3, membershipCardGiftTask.getUserRoleName())
		.setParameter(4, membershipCardGiftTask.getRestriction())
		.setParameter(5, membershipCardGiftTask.getExpirationDate_start())
		.setParameter(6, membershipCardGiftTask.getExpirationDate_end())
		.setParameter(7, membershipCardGiftTask.isEnable())
		.setParameter(8, membershipCardGiftTask.getDuration())
		.setParameter(9, membershipCardGiftTask.getUnit())
		.setParameter(10, membershipCardGiftTask.getId());
		return  query.executeUpdate();
	}
	
	/**
	 * 删除会员卡赠送任务
	 * @param membershipCardGiftTaskId 会员卡赠送任务Id
	 */
	@CacheEvict(value="membershipCardGiftTaskRepositoryImpl_cache",allEntries=true)
	public Integer deleteMembershipCardGiftTask(Long membershipCardGiftTaskId){
		Query delete = em.createQuery("delete from MembershipCardGiftTask o where o.id=?1")
		.setParameter(1,membershipCardGiftTaskId);
		return delete.executeUpdate();
	}
	
	
	/**-------------------------------------------------------------------------------------------------------------**/
	
	/**
	 * 保存会员卡赠送项
	 * @param membershipCardGiftItem 会员卡赠送项
	 * @param add_userRoleGroup 增加用户角色组
	 * @param update_userRoleGroup 修改用户角色组
	 * @return
	 */
	public void saveMembershipCardGiftItem(Object membershipCardGiftItem,UserRoleGroup add_userRoleGroup,UserRoleGroup update_userRoleGroup){
		this.save(membershipCardGiftItem);
		
		if(add_userRoleGroup != null){//角色组不存在时添加
			List<UserRoleGroup> userRoleGroupList = new ArrayList<UserRoleGroup>();
			userRoleGroupList.add(add_userRoleGroup);
			userRoleRepository.saveUserRoleGroup(userRoleGroupList);
		}
		
		if(update_userRoleGroup != null){//角色组存在时更新
			int i = userRoleRepository.updateUserRoleGroup(update_userRoleGroup.getUserRoleId(), update_userRoleGroup.getUserName(), update_userRoleGroup.getValidPeriodEnd());
			if(i==0){
				throw new BusinessException("角色组更新错误");
			}
		}
	}
	
	
	/**
	 * 根据会员卡赠送项Id查询会员卡赠送项
	 * @param membershipCardGiftItemId 会员卡赠送项Id
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public MembershipCardGiftItem findMembershipCardGiftItemById(String membershipCardGiftItemId){
		
		Query query  = null;
		//表编号
		int tableNumber = membershipCardGiftItemConfig.membershipCardGiftItemRemainder(membershipCardGiftItemId);
		if(tableNumber == 0){//默认对象
			query = em.createQuery("select o from MembershipCardGiftItem o where o.id=?1")
			.setParameter(1, membershipCardGiftItemId);
			
			List<MembershipCardGiftItem> membershipCardGiftItemList= query.getResultList();
			if(membershipCardGiftItemList != null && membershipCardGiftItemList.size() >0){
				for(MembershipCardGiftItem membershipCardGiftItem : membershipCardGiftItemList){
					return membershipCardGiftItem;
				}
			}
		}else{//带下划线对象
			query = em.createQuery("select o from MembershipCardGiftItem_"+tableNumber+" o where o.id=?1")
			.setParameter(1, membershipCardGiftItemId);
			
			List<?> membershipCardGiftItem_List= query.getResultList();
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.model.membershipCard.MembershipCardGiftItem_"+tableNumber);
                //获取无参数构造函数
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),MembershipCardGiftItem.class, false); 
				for(int j = 0;j< membershipCardGiftItem_List.size(); j++) {  
					Object obj = membershipCardGiftItem_List.get(j);
					MembershipCardGiftItem membershipCardGiftItem = new MembershipCardGiftItem();
					copier.copy(obj,membershipCardGiftItem, null);
					return membershipCardGiftItem;
				}
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据会员卡赠送项Id查询会员卡赠送项",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据会员卡赠送项Id查询会员卡赠送项",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据会员卡赠送项Id查询会员卡赠送项",e);
		        }
			} catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("根据会员卡赠送项Id查询会员卡赠送项",e);
                }
            } catch (NoSuchMethodException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("根据会员卡赠送项Id查询会员卡赠送项",e);
                }
            }
		}
		
		return null;
	}
	
	
	/**
	 * 会员卡赠送项分页
	 * @param membershipCardGiftTaskId 会员卡赠送任务Id
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<MembershipCardGiftItem> findMembershipCardGiftItemPage(Long membershipCardGiftTaskId, int firstIndex, int maxResult){
		
		QueryResult<MembershipCardGiftItem> qr = new QueryResult<MembershipCardGiftItem>();
		Query query  = null;
		
		
		//表编号
		int tableNumber = membershipCardGiftItemConfig.membershipCardGiftTaskIdRemainder(membershipCardGiftTaskId);
		if(tableNumber == 0){//默认对象为HistoryOrder
			query = em.createQuery("select o from MembershipCardGiftItem o where o.membershipCardGiftTaskId=?1")
			.setParameter(1, membershipCardGiftTaskId);
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<MembershipCardGiftItem> membershipCardGiftItemList= query.getResultList();
			qr.setResultlist(membershipCardGiftItemList);
			
			query = em.createQuery("select count(o) from MembershipCardGiftItem  o where o.membershipCardGiftTaskId=?1")
					.setParameter(1, membershipCardGiftTaskId);
			qr.setTotalrecord((Long)query.getSingleResult());
		}else{//带下划线对象
			query = em.createQuery("select o from MembershipCardGiftItem_"+tableNumber+" o where o.membershipCardGiftTaskId=?1")
			.setParameter(1, membershipCardGiftTaskId);
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<?> membershipCardGiftItem_List= query.getResultList();
			
			
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.model.membershipCard.MembershipCardGiftItem_"+tableNumber);
                //获取无参数构造函数
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),MembershipCardGiftItem.class, false); 
				List<MembershipCardGiftItem> membershipCardGiftItemList= new ArrayList<MembershipCardGiftItem>();
				for(int j = 0;j< membershipCardGiftItem_List.size(); j++) {  
					Object obj = membershipCardGiftItem_List.get(j);
					MembershipCardGiftItem membershipCardGiftItem = new MembershipCardGiftItem();
					copier.copy(obj,membershipCardGiftItem, null);
					membershipCardGiftItemList.add(membershipCardGiftItem);
				}
				qr.setResultlist(membershipCardGiftItemList);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("会员卡赠送项分页",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("会员卡赠送项分页",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("会员卡赠送项分页",e);
		        }
			} catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("会员卡赠送项分页",e);
                }
            } catch (NoSuchMethodException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("会员卡赠送项分页",e);
                }
            }
			
			query = em.createQuery("select count(o) from MembershipCardGiftItem_"+tableNumber+" o where o.membershipCardGiftTaskId=?1")
					.setParameter(1, membershipCardGiftTaskId);
			qr.setTotalrecord((Long)query.getSingleResult());
		}
		
		return qr;
	}
	
	/**
	 * 根据用户名称删除会员卡赠送项
	 * @param userNameList 用户名称集合
	 */
	public Integer deleteMembershipCardGiftItemByUserName(List<String> userNameList){
		int j = 0;
		int tableNumber = membershipCardGiftItemConfig.getTableQuantity();
		for(int i = 0; i<tableNumber; i++){
			if(i == 0){//默认对象
				//删除
				Query query = em.createQuery("delete from MembershipCardGiftItem o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
			}else{//带下划线对象
				Query query = em.createQuery("delete from MembershipCardGiftItem_"+i+" o where o.userName in(:userName)")
						.setParameter("userName", userNameList);
				j += query.executeUpdate();
			}
		}
		return j;
	}
}
