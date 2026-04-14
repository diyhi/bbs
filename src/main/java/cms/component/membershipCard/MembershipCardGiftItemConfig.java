package cms.component.membershipCard;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 会员卡赠送项配置
 * @author Gao
 *
 */
@Component("membershipCardGiftItemConfig")
public class MembershipCardGiftItemConfig {
	
	/** 分表数量 **/
	@Value("${bbs.sharding.membershipCardGiftItemConfig_tableQuantity}")
    @Getter
	private Integer tableQuantity = 1;

	
	/**
	  * 根据会员卡赠送项查询分配到表编号
	  * 根据会员卡赠送项用户Id后4位和会员卡赠送项分表数量求余
	  * @param membershipCardGiftItemId 会员卡赠送项Id
	  * @return
	 */
   public Integer membershipCardGiftItemRemainder(String membershipCardGiftItemId){
	   //选取得后N位会员卡赠送任务Id
	   String afterId = String.format("%04d", Math.abs(this.getMembershipCardGiftTaskId(membershipCardGiftItemId))%10000);
	   return Integer.parseInt(afterId) % this.getTableQuantity();
   } 
   /**
    * 根据会员卡赠送任务Id查询分配到表编号
    * 根据会员卡赠送任务Id和会员卡赠送项分表数量求余
    * @param membershipCardGiftTaskId 会员卡赠送任务Id
    * @return
    */
   public Integer membershipCardGiftTaskIdRemainder(Long membershipCardGiftTaskId){
	   //选取得后N位会员卡赠送任务Id
	   String afterId = String.format("%04d", Math.abs(membershipCardGiftTaskId)%10000);
	   
	   return Integer.parseInt(afterId) % this.getTableQuantity();
   }
   
   /**
	 * 取得会员卡赠送项的会员卡赠送任务Id
	 * @param membershipCardGiftItemId 会员卡赠送项Id
	 * @return
	 */
   private long getMembershipCardGiftTaskId(String membershipCardGiftItemId){
	   return Long.parseLong(membershipCardGiftItemId.split("-")[0]);
   }
}
