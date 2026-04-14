package cms.component.user;

import cms.utils.UUIDUtil;
import cms.utils.Verification;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 用户动态配置
 *
 */
@Component("userDynamicConfig")
public class UserDynamicConfig {
	
	/** 分表数量 **/
	@Value("${bbs.sharding.userDynamicConfig_tableQuantity}")
    @Getter
	private Integer tableQuantity = 1;
	
	 /**
	  * 根据用户动态Id查询分配到表编号
	  * 根据用户动态Id和用户动态分表数量求余
	  * @param userDynamicId 用户动态Id
	  * 注意：用户动态Id要先判断是否36位并且最后4位是数字
	  * userDynamicManage.verificationUserDynamicId(?)
	  * @return
	 */
    public Integer userDynamicIdRemainder(String userDynamicId){
    	int userId = this.getUserDynamicUserId(userDynamicId);
    	return userId % this.getTableQuantity();
    } 
    /**
     * 根据用户Id查询分配到表编号
     * 根据用户Id和用户动态分表数量求余(用户Id后四位)
     * @param userId 用户Id
     * @return
     */
    public Integer userIdRemainder(Long userId){
    	//选取得后N位用户Id
    	String afterUserId = String.format("%04d", userId%10000);
    	return Integer.parseInt(afterUserId) % this.getTableQuantity();
    }

    /**
     * 取得用户动态Id的用户Id(后N位)
     * @param userDynamicId 用户动态Id
     * @return
     */
    public int getUserDynamicUserId(String userDynamicId){
        String after_userId = userDynamicId.substring(userDynamicId.length()-4, userDynamicId.length());
        return Integer.parseInt(after_userId);
    }

    /**
     * 生成用户动态Id
     * 用户动态Id由32位uuid+1位用户Id后4位组成
     * @param userId 用户Id
     * @return
     */
    public String createUserDynamicId(Long userId){
        //选取得后N位用户Id
        String afterUserId = String.format("%04d", userId%10000);
        String id = UUIDUtil.getUUID32()+afterUserId;
        return id;
    }
    /**
     * 校验用户动态Id
     * 用户动态Id要先判断是否36位并且最后4位是数字
     * @param userDynamicId 用户动态Id
     * @return
     */
    public boolean verificationUserDynamicId(String userDynamicId){
        if(userDynamicId != null && !"".equals(userDynamicId.trim())){
            if(userDynamicId.length() == 36){
                String after_userId = userDynamicId.substring(userDynamicId.length()-4, userDynamicId.length());
                boolean verification = Verification.isPositiveIntegerZero(after_userId);//数字
                if(verification){
                    return true;
                }
            }
        }
        return false;
    }
}
