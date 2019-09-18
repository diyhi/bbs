package cms.web.action.membershipCard;



import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import cms.bean.membershipCard.MembershipCard;
import cms.bean.membershipCard.Specification;
import cms.service.membershipCard.MembershipCardService;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 会员卡管理
 *
 */
@Component("membershipCardManage")
public class MembershipCardManage {
	 
    @Resource MembershipCardService membershipCardService;
   
    private AtomicInteger number = new AtomicInteger(new Random().nextInt(88888));//AtomicInteger 的最大值是2147483647，超过这个数字在递增的话就变成-2147483648
	private DateTime begin = new DateTime(2010,01,01,01,01,01,0);   
	/**
     * 取得下一个订单号
     * 订单号组成(2010年后的年月日时分秒+用户Id后四位+本机Id五位)
     */
    public Long nextNumber(Long userId){
    	//这里是atoNum到MAX_VALUE=99999的时候重新设成0
    	int MAX_VALUE = 99999;
    	number.compareAndSet(MAX_VALUE, 0);

    	DateTime end = new DateTime();  

		//计算区间毫秒数   
		Duration d = new Duration(begin, end);  
//		long hour = d.getStandardHours();//时
		long second = d.getStandardSeconds();//秒  分配的数字最大可以用到2300年
//		long minute = d.getStandardMinutes();//分
		
    	//userId%10000 取后4位
    	return Long.parseLong(second+""+(String.format("%04d", userId%10000))+""+(String.format("%05d", number.incrementAndGet())));
 
    }

    /**
	 * 查询缓存 查询会员卡
	 * @param membershipCardId 会员卡Id
	 * @return
	 */
	@Cacheable(value="membershipCardManage_cache_findById",key="#membershipCardId")
	public MembershipCard query_cache_findById(Long membershipCardId){
		return membershipCardService.findById(membershipCardId);
	}
	/**
	 * 删除缓存 会员卡
	 * @param membershipCardId 会员卡Id
	 * @return
	 */
	@CacheEvict(value="membershipCardManage_cache_findById",key="#membershipCardId")
	public void delete_cache_findById(Long membershipCardId){
	}
    
	/**
	 * 查询缓存 查询会员卡规格
	 * @param membershipCardId 会员卡Id
	 * @return
	 */
	@Cacheable(value="membershipCardManage_cache_findSpecificationByMembershipCardId",key="#membershipCardId")
	public List<Specification> query_cache_findSpecificationByMembershipCardId(Long membershipCardId){
		return membershipCardService.findSpecificationByMembershipCardId(membershipCardId);
	}
	/**
	 * 删除缓存 会员卡规格
	 * @param membershipCardId 会员卡Id
	 * @return
	 */
	@CacheEvict(value="membershipCardManage_cache_findSpecificationByMembershipCardId",key="#membershipCardId")
	public void delete_cache_findSpecificationByMembershipCardId(Long membershipCardId){
	}
    
}
