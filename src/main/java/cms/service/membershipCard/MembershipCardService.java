package cms.service.membershipCard;

import java.util.List;

import cms.bean.QueryResult;
import cms.bean.membershipCard.MembershipCard;
import cms.bean.membershipCard.MembershipCardOrder;
import cms.bean.membershipCard.Specification;
import cms.bean.user.UserRoleGroup;
import cms.service.besa.DAO;

/**
 * 会员卡接口
 *
 */
public interface MembershipCardService extends DAO<MembershipCard>{
	/**
	 * 根据Id查询会员卡
	 * @param membershipCard 会员卡Id
	 * @return
	 */
	public MembershipCard findById(Long membershipCardId);
	/**
	 * 根据会员卡Id查询规格
	 * @param membershipCard 会员卡Id
	 * @return
	 */
	public List<Specification> findSpecificationByMembershipCardId(Long membershipCardId);
	/**
	 * 根据规格Id查询规格
	 * @param specificationId 规格Id
	 * @return
	 */
	public Specification findSpecificationBySpecificationId(Long specificationId);
	/**
	 * 查询所有会员卡
	 * @return
	 */
	public List<MembershipCard> findAllMembershipCard();
	/**
	 * 保存会员卡
	 * @param membershipCard 会员卡
	 */
	public void saveMembershipCard(MembershipCard membershipCard);


	/**
	 * 修改会员卡
	 * @param membershipCard 会员卡
	 * @param add_specificationList 添加规格集合
	 * @param update_specificationList 修改规格集合
	 * @param delete_specificationIdList 删除规格Id集合
	 * @return
	 */
	public Integer updateMembershipCard(MembershipCard membershipCard,List<Specification> add_specificationList,
			List<Specification> update_specificationList,List<Long> delete_specificationIdList);
	
	/**
	 * 删除会员卡
	 * @param membershipCardId 会员卡Id
	 */
	public Integer deleteMembershipCard(Long membershipCardId);
	/**
	 * 根据用户名称查询会员卡订单分页
	 * @param userName 用户名称
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @return
	 */
	public QueryResult<MembershipCardOrder> findMembershipCardOrderByUserName(String userName,int firstIndex, int maxResult);
	/**
	 * 保存会员卡订单
	 * @param membershipCardOrder 会员卡订单
	 * @param add_userRoleGroup 增加用户角色组
	 * @param update_userRoleGroup 修改用户角色组
	 * @param pointLog 积分支付日志
	 * @param paymentLog 预存款支付日志
	 */
	public void saveMembershipCardOrder(MembershipCardOrder membershipCardOrder,UserRoleGroup add_userRoleGroup,UserRoleGroup update_userRoleGroup,
			Object pointLog,Object paymentLog);
}
