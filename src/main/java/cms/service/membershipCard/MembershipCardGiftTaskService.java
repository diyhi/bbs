package cms.service.membershipCard;

import java.util.List;

import cms.bean.QueryResult;
import cms.bean.membershipCard.MembershipCardGiftItem;
import cms.bean.membershipCard.MembershipCardGiftTask;
import cms.bean.user.UserRoleGroup;
import cms.service.besa.DAO;

/**
 * 会员卡赠送任务接口
 *
 */
public interface MembershipCardGiftTaskService extends DAO<MembershipCardGiftTask>{
	/**
	 * 根据Id查询会员卡赠送任务
	 * @param membershipCardGiftTaskId 会员卡赠送任务Id
	 * @return
	 */
	public MembershipCardGiftTask findById(Long membershipCardGiftTaskId);
	/**
	 * 查询启用的长期会员卡赠送任务
	 * @return
	 */
	public List<MembershipCardGiftTask> findEnableMembershipCardGiftTask();
	/**
	 * 保存会员卡赠送任务
	 * @param membershipCardGiftTask 会员卡赠送任务
	 */
	public void saveMembershipCardGiftTask(MembershipCardGiftTask membershipCardGiftTask);


	/**
	 * 修改会员卡赠送任务
	 * @param membershipCardGiftTask 会员卡赠送任务
	 * @return
	 */
	public Integer updateMembershipCardGiftTask(MembershipCardGiftTask membershipCardGiftTask);
	
	/**
	 * 删除会员卡赠送任务
	 * @param membershipCardGiftTaskId 会员卡赠送任务Id
	 */
	public Integer deleteMembershipCardGiftTask(Long membershipCardGiftTaskId);
	/**
	 * 保存会员卡赠送项
	 * @param membershipCardGiftItem 会员卡赠送项
	 * @param add_userRoleGroup 增加用户角色组
	 * @param update_userRoleGroup 修改用户角色组
	 * @return
	 */
	public void saveMembershipCardGiftItem(Object membershipCardGiftItem,UserRoleGroup add_userRoleGroup,UserRoleGroup update_userRoleGroup);
	/**
	 * 根据会员卡赠送项Id查询会员卡赠送项
	 * @param membershipCardGiftItemId
	 */
	public MembershipCardGiftItem findMembershipCardGiftItemById(String membershipCardGiftItemId);
	
	/**
	 * 会员卡赠送项分页
	 * @param membershipCardGiftTaskId 会员卡赠送任务Id
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 */
	public QueryResult<MembershipCardGiftItem> findMembershipCardGiftItemPage(Long membershipCardGiftTaskId,int firstIndex, int maxResult);
	/**
	 * 根据用户名称删除会员卡赠送项
	 * @param userNameList 用户名称集合
	 */
	public Integer deleteMembershipCardGiftItemByUserName(List<String> userNameList);
}
