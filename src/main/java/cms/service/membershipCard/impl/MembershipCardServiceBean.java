package cms.service.membershipCard.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.QueryResult;
import cms.bean.membershipCard.MembershipCard;
import cms.bean.membershipCard.MembershipCardOrder;
import cms.bean.membershipCard.Specification;
import cms.bean.user.UserRoleGroup;
import cms.service.besa.DaoSupport;
import cms.service.membershipCard.MembershipCardService;
import cms.service.user.UserRoleService;
import cms.service.user.UserService;
import cms.web.action.SystemException;

/**
 * 会员卡实现
 *
 */
@Service
@Transactional
public class MembershipCardServiceBean extends DaoSupport<MembershipCard> implements MembershipCardService{
	@Resource UserRoleService userRoleService;
	@Resource UserService userService;
	
	/**
	 * 根据Id查询会员卡
	 * @param membershipCard 会员卡Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public MembershipCard findById(Long membershipCardId){
		Query query = em.createQuery("select o from MembershipCard o where o.id=?1")
		.setParameter(1, membershipCardId);
		List<MembershipCard> list = query.getResultList();
		for(MembershipCard p : list){
			return p;
		}
		return null;
	}
	/**
	 * 根据会员卡Id查询规格
	 * @param membershipCard 会员卡Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public List<Specification> findSpecificationByMembershipCardId(Long membershipCardId){
		Query query = em.createQuery("select o from Specification o where o.membershipCardId=?1 order by o.sort asc")
				.setParameter(1, membershipCardId);
		return query.getResultList();
	}
	/**
	 * 根据规格Id查询规格
	 * @param specificationId 规格Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Specification findSpecificationBySpecificationId(Long specificationId){
		Query query = em.createQuery("select o from Specification o where o.id=?1")
				.setParameter(1, specificationId);
		List<Specification> list = query.getResultList();
		for(Specification p : list){
			return p;
		}
		return null;
	}
	
	/**
	 * 查询所有会员卡
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public List<MembershipCard> findAllMembershipCard(){
		Query query = em.createQuery("select o from MembershipCard o order by o.sort desc");
		return query.getResultList();
	}

	/**
	 * 保存会员卡
	 * @param membershipCard 会员卡
	 */
	public void saveMembershipCard(MembershipCard membershipCard){
		this.save(membershipCard);
		if(membershipCard.getSpecificationList() != null && membershipCard.getSpecificationList().size() >0){
			for(Specification specification : membershipCard.getSpecificationList()){
				specification.setMembershipCardId(membershipCard.getId());
				this.save(specification);
			}
		}
		
		
	}
	

	/**
	 * 修改会员卡
	 * @param membershipCard 会员卡
	 * @param add_specificationList 添加规格集合
	 * @param update_specificationList 修改规格集合
	 * @param delete_specificationIdList 删除规格Id集合
	 * @return
	 */
	public Integer updateMembershipCard(MembershipCard membershipCard,List<Specification> add_specificationList,
			List<Specification> update_specificationList,List<Long> delete_specificationIdList){
		Query query = em.createQuery("update MembershipCard o set o.name=?1,o.subtitle=?2, o.lowestPrice=?3,o.highestPrice=?4,"
				+ " o.lowestPoint=?5,o.highestPoint=?6,o.introduction=?7,o.state=?8,o.userRoleId=?9,"
				+ " o.sort=?10,o.descriptionTagFormat=?11 where o.id=?12")
		.setParameter(1, membershipCard.getName())
		.setParameter(2, membershipCard.getSubtitle())
		.setParameter(3, membershipCard.getLowestPrice())
		.setParameter(4, membershipCard.getHighestPrice())
		.setParameter(5, membershipCard.getLowestPoint())
		.setParameter(6, membershipCard.getHighestPoint())
		.setParameter(7, membershipCard.getIntroduction())
		.setParameter(8, membershipCard.getState())
		.setParameter(9, membershipCard.getUserRoleId())
		.setParameter(10, membershipCard.getSort())
		.setParameter(11, membershipCard.getDescriptionTagFormat())
		.setParameter(12, membershipCard.getId());
		int i = query.executeUpdate();
		
		if(i >0){
				
			//删除规格
			if(delete_specificationIdList != null && delete_specificationIdList.size() >0){
				this.deleteSpecificationBySpecificationIdList(delete_specificationIdList);
			}
			
			//修改规格
			if(update_specificationList != null && update_specificationList.size() >0){
				for(Specification specification : update_specificationList){
					String stock_sql= "";
					String inventory_where_sql= "";
					if(specification.getStockStatus().equals(0)){//不变
						stock_sql= "o.stock=o.stock+?3,";
						
						
					}else if(specification.getStockStatus().equals(1)){//增加
						stock_sql= "o.stock=o.stock+?3,";
						
					}else if(specification.getStockStatus().equals(2)){//减少
						stock_sql= "o.stock=o.stock-?3,";
						inventory_where_sql = " and o.stock>=?12";
					}
					if(specification.getChangeStock() == null){
						specification.setChangeStock(0L);
					}
					Query query_style = em.createQuery("update Specification o set o.specificationName=?1,o.membershipCardId=?2," +
							stock_sql+"o.point=?4, o.marketPrice=?5, o.sellingPrice=?6, o.enable=?7,o.sort=?8,o.duration=?9,o.unit=?10 where o.id=?11"+inventory_where_sql)
					.setParameter(1, specification.getSpecificationName())//规格名称
					.setParameter(2, specification.getMembershipCardId())//会员卡Id
					.setParameter(3, specification.getChangeStock())//更改库存量
					.setParameter(4, specification.getPoint())//积分
					.setParameter(5, specification.getMarketPrice())//市场价
					.setParameter(6, specification.getSellingPrice())//销售价
					
					.setParameter(7, specification.isEnable())//是否启用
					.setParameter(8, specification.getSort())//排序
					.setParameter(9, specification.getDuration())//时长
					.setParameter(10, specification.getUnit())//时长单位
					.setParameter(11, specification.getId());
					if(!"".equals(inventory_where_sql)){
						query_style.setParameter(12, specification.getChangeStock());
					}
					int p = query_style.executeUpdate();
					
					
				}
			}
			//添加规格
			if(add_specificationList != null && add_specificationList.size() >0){
				for(Specification specification : add_specificationList){
					this.save(specification);
				}
			}
			
		}
	
		
		return i;
	}
	
	/**
	 * 删除会员卡
	 * @param membershipCardId 会员卡Id
	 */
	public Integer deleteMembershipCard(Long membershipCardId){
		int i = 0;
		Query delete = em.createQuery("delete from MembershipCard o where o.id=?1")
		.setParameter(1,membershipCardId);
		i = delete.executeUpdate();
		
		if(i>0){
			
			Query delete2 = em.createQuery("delete from Specification o where o.membershipCardId=?1")
					.setParameter(1,membershipCardId);
			i += delete2.executeUpdate();
			
		}
		
		return i;
	}
	
	
	
	
	/**
	 * 根据规格Id删除规格
	 * @param specificationIdList 会员卡Id集合
	 */
	private Integer deleteSpecificationBySpecificationIdList(List<Long> specificationIdList){
		Query delete = em.createQuery("delete from Specification o where o.id in(:id)")
		.setParameter("id",specificationIdList);
		int i = delete.executeUpdate();
		
		return i;
	}
	
	
	/**----------------------------------------------------- 会员卡订单 -----------------------------------------------------**/
	
	/**
	 * 根据用户名称查询会员卡订单分页
	 * @param userName 用户名称
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<MembershipCardOrder> findMembershipCardOrderByUserName(String userName,int firstIndex, int maxResult){
		QueryResult<MembershipCardOrder> qr = new QueryResult<MembershipCardOrder>();
		
		Query query = em.createQuery("select o from MembershipCardOrder o where o.userName=?1 ORDER BY o.createDate desc");
			query.setParameter(1, userName);
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<MembershipCardOrder> membershipCardOrderList= query.getResultList();
			qr.setResultlist(membershipCardOrderList);
			
			query = em.createQuery("select count(o) from MembershipCardOrder o where o.userName=?1");
			query.setParameter(1, userName);
			qr.setTotalrecord((Long)query.getSingleResult());
	
		
		return qr;
	}
	
	/**
	 * 保存会员卡订单
	 * @param membershipCardOrder 会员卡订单
	 * @param add_userRoleGroup 增加用户角色组
	 * @param update_userRoleGroup 修改用户角色组
	 * @param pointLog 积分支付日志
	 * @param paymentLog 预存款支付日志
	 */
	public void saveMembershipCardOrder(MembershipCardOrder membershipCardOrder,UserRoleGroup add_userRoleGroup,UserRoleGroup update_userRoleGroup,
			Object pointLog,Object paymentLog){
		this.save(membershipCardOrder);
		
		if(add_userRoleGroup != null){//角色组不存在时添加
			List<UserRoleGroup> userRoleGroupList = new ArrayList<UserRoleGroup>();
			userRoleGroupList.add(add_userRoleGroup);
			userRoleService.saveUserRoleGroup(userRoleGroupList);
		}
		
		if(update_userRoleGroup != null){//角色组存在时更新
			int i = userRoleService.updateUserRoleGroup(update_userRoleGroup.getUserRoleId(), update_userRoleGroup.getUserName(), update_userRoleGroup.getValidPeriodEnd());
			if(i==0){
				throw new SystemException("角色组更新错误");
			}
		}
		
		
		//扣除用户金额
		if(membershipCardOrder.getPaymentAmount().compareTo(new BigDecimal("0")) >0){
			int i = userService.subtractUserDeposit(membershipCardOrder.getUserName(),membershipCardOrder.getPaymentAmount(),paymentLog);
			if(i==0){
				throw new SystemException("扣除用户金额错误");
			}
		}

		//扣除用户积分
		if(membershipCardOrder.getPaymentPoint() >0L){
			int i = userService.subtractUserPoint(membershipCardOrder.getUserName(),membershipCardOrder.getPaymentPoint(),pointLog);
			if(i==0){
				throw new SystemException("扣除用户金额错误");
			}
		}
		
		
		//扣减库存
		int j = this.reduceSpecificationStock(Long.parseLong(""+membershipCardOrder.getQuantity()),membershipCardOrder.getSpecificationId());
		if(j ==0){
			throw new SystemException("扣减库存错误");
			
		}
		
	}
	
	
	/**
	 * 减少会员卡库存,增加占用库存
	 * @param stock 库存量
	 * @param specificationId 规格Id
	 * @return
	 */
	private Integer reduceSpecificationStock(Long stock, Long specificationId){
		Query query = em.createQuery("update Specification o set o.stock=o.stock-?1,o.stockOccupy=o.stockOccupy+?2 where o.id=?3 and o.stock >=?4 and o.stock >0")
		.setParameter(1, stock)
		.setParameter(2, stock)
		.setParameter(3, specificationId)
		.setParameter(4, stock);
		int i = query.executeUpdate();

		return i;
	}
	
}
