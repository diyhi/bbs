package cms.component.membershipCard;

import cms.model.membershipCard.MembershipCard;
import cms.model.membershipCard.Specification;
import cms.repository.membershipCard.MembershipCardRepository;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 员工缓存管理
 */
@Component("membershipCardCacheManager")
public class MembershipCardCacheManager {
    @Resource MembershipCardRepository membershipCardRepository;

    /**
     * 查询缓存 查询会员卡
     * @param membershipCardId 会员卡Id
     * @return
     */
    @Cacheable(value="membershipCardCacheManager_cache_findById",key="#membershipCardId")
    public MembershipCard query_cache_findById(Long membershipCardId){
        return membershipCardRepository.findById(membershipCardId);
    }
    /**
     * 删除缓存 会员卡
     * @param membershipCardId 会员卡Id
     * @return
     */
    @CacheEvict(value="membershipCardCacheManager_cache_findById",key="#membershipCardId")
    public void delete_cache_findById(Long membershipCardId){
    }

    /**
     * 查询缓存 查询会员卡规格
     * @param membershipCardId 会员卡Id
     * @return
     */
    @Cacheable(value="membershipCardCacheManager_cache_findSpecificationByMembershipCardId",key="#membershipCardId")
    public List<Specification> query_cache_findSpecificationByMembershipCardId(Long membershipCardId){
        return membershipCardRepository.findSpecificationByMembershipCardId(membershipCardId);
    }
    /**
     * 删除缓存 会员卡规格
     * @param membershipCardId 会员卡Id
     * @return
     */
    @CacheEvict(value="membershipCardCacheManager_cache_findSpecificationByMembershipCardId",key="#membershipCardId")
    public void delete_cache_findSpecificationByMembershipCardId(Long membershipCardId){
    }
}
