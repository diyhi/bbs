package cms.component.follow;


import cms.utils.Verification;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 粉丝配置
 *
 */
@Component("followerConfig")
public class FollowerConfig {
	
	/** 分表数量 **/
	@Value("${bbs.sharding.followerConfig_tableQuantity}")
    @Getter
	private Integer tableQuantity = 1;

	
	/**
	  * 根据粉丝Id查询分配到表编号
	  * 根据粉丝Id和粉丝分表数量求余
	  * @param followerId 粉丝Id
	  * 注意：粉丝Id要验证
	  * followerManage.verificationFollowerId(?)
	  * @return
	 */
	public Integer followerIdRemainder(String followerId){
	   int userId = this.getFollowerAfterId(followerId);
	   return userId % this.getTableQuantity();
	} 
   /**
    * 根据用户Id查询分配到表编号
    * 根据用户Id和粉丝分表数量求余(用户Id后四位)
    * @param userId 用户Id
    * @return
    */
	public Integer userIdRemainder(Long userId){
	   	//选取得后N位用户Id
	   	String afterUserId = String.format("%04d", userId%10000);
	   	return Integer.parseInt(afterUserId) % this.getTableQuantity();
	}

    /**
     * 取得粉丝Id的用户Id(后N位)
     * @param followerId 关注Id
     * @return
     */
    public int getFollowerAfterId(String followerId){
        String[] idGroup = followerId.split("-");
        Long userId = Long.parseLong(idGroup[1]);
        String afterUserId = String.format("%04d", userId%10000);
        return Integer.parseInt(afterUserId);
    }

    /**
     * 生成粉丝Id
     * 粉丝Id由(对方用户Id-粉丝的用户Id)组成
     * @param userId 用户Id
     * @param friendUserId 对方用户Id
     * @return
     */
    public String createFollowerId(Long userId,Long friendUserId){
        return String.valueOf(friendUserId)+"-"+String.valueOf(userId);
    }
    /**
     * 校验粉丝Id
     * 粉丝Id要判断按横杆分割后是数字
     * @param followerId 粉丝Id
     * @return
     */
    public boolean verificationFollowerId(String followerId){
        if(followerId != null && !"".equals(followerId.trim())){
            String[] idGroup = followerId.split("-");
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
