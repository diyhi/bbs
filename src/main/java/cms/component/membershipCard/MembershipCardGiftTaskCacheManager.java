package cms.component.membershipCard;

import cms.model.membershipCard.MembershipCardGiftItem;
import cms.repository.membershipCard.MembershipCardGiftTaskRepository;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 会员卡赠送任务缓存管理
 */
@Component("membershipCardGiftTaskCacheManager")
public class MembershipCardGiftTaskCacheManager {

    @Resource MembershipCardGiftTaskRepository membershipCardGiftTaskRepository;

    /**
     * 查询缓存 查询查询会员卡赠送项
     * @param membershipCardGiftItemId 查询会员卡赠送项Id
     * @return
     */
    @Cacheable(value="membershipCardGiftTaskCacheManager_cache_findMembershipCardGiftItemById",key="#membershipCardGiftItemId")
    public MembershipCardGiftItem query_cache_findMembershipCardGiftItemById(String membershipCardGiftItemId){
        return membershipCardGiftTaskRepository.findMembershipCardGiftItemById(membershipCardGiftItemId);
    }
    /**
     * 删除缓存 查询会员卡赠送项
     * @param membershipCardGiftItemId 查询会员卡赠送项Id
     * @return
     */
    @CacheEvict(value="membershipCardGiftTaskCacheManager_cache_findMembershipCardGiftItemById",key="#membershipCardGiftItemId")
    public void delete_cache_findMembershipCardGiftItemById(String membershipCardGiftItemId){
    }
}
