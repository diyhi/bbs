package cms.component.follow;

import cms.utils.Verification;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 关注配置
 *
 */
@Component("followConfig")
public class FollowConfig {
	
	/** 分表数量 **/
	@Value("${bbs.sharding.followConfig_tableQuantity}")
    @Getter
	private Integer tableQuantity = 1;
	
	/**
	  * 根据关注Id查询分配到表编号
	  * 根据关注Id和关注分表数量求余
	  * @param followId 关注Id
	  * 注意：关注Id要先验证
	  * followManage.verificationFollowId(?)
	  * @return
	 */
	public Integer followIdRemainder(String followId){
	   int userId = this.getFollowAfterId(followId);
	   return userId % this.getTableQuantity();
	} 
   /**
    * 根据用户Id查询分配到表编号
    * 根据用户Id和关注分表数量求余(用户Id后四位)
    * @param userId 用户Id
    * @return
    */
	public Integer userIdRemainder(Long userId){
	   	//选取得后N位用户Id
	   	String afterUserId = String.format("%04d", userId%10000);
	   	return Integer.parseInt(afterUserId) % this.getTableQuantity();
	}

    /**
     * 取得关注Id的用户Id(后N位)
     * @param followId 关注Id
     * @return
     */
    public int getFollowAfterId(String followId){
        String[] idGroup = followId.split("-");
        Long userId = Long.parseLong(idGroup[1]);
        String afterUserId = String.format("%04d", userId%10000);
        return Integer.parseInt(afterUserId);
    }

    /**
     * 生成关注Id
     * 关注Id由(对方用户Id-关注的用户Id)组成
     * @param userId 用户Id
     * @param friendUserId 对方用户Id
     * @return
     */
    public String createFollowId(Long userId,Long friendUserId){
        return String.valueOf(friendUserId)+"-"+String.valueOf(userId);
    }
    /**
     * 校验关注Id
     * 关注Id要判断按横杆分割后是数字
     * @param followId 关注Id
     * @return
     */
    public boolean verificationFollowId(String followId){
        if(followId != null && !"".equals(followId.trim())){
            String[] idGroup = followId.split("-");
            if(idGroup.length ==2){
                boolean verification_1 = Verification.isPositiveIntegerZero(idGroup[0]);//数字
                boolean verification_2 = Verification.isPositiveIntegerZero(idGroup[1]);//数字
                if(verification_1 && verification_2){
                    return true;
                }
            }
        }
        return false;
    }
	 
}
