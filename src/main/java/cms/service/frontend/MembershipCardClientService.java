package cms.service.frontend;

import cms.dto.PageView;
import cms.model.membershipCard.MembershipCard;
import cms.model.membershipCard.MembershipCardOrder;

import java.util.List;

/**
 * 前台会员卡服务接口
 */
public interface MembershipCardClientService {
    /**
     * 获取会员卡列表
     * @return
     */
    public List<MembershipCard> getMembershipCardList();
    /**
     * 获取会员卡明细
     * @param membershipCardId 会员卡Id
     */
    public MembershipCard getMemberCardDetail(Long membershipCardId);
    /**
     * 购买会员卡
     * @param specificationId 规格Id
     * @return
     */
    public void addMembershipCard(Long specificationId);
    /**
     * 会员卡订单列表
     * @param page 页码
     * @return
     */
    public PageView<MembershipCardOrder> getMembershipCardOrderList(int page);
}
