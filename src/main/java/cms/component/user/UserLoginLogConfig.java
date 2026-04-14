package cms.component.user;


import cms.utils.UUIDUtil;
import cms.utils.Verification;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 用户登录日志配置
 *
 */
@Component("userLoginLogConfig")
public class UserLoginLogConfig {
	
	/** 分表数量 **/
	@Value("${bbs.sharding.userLoginLogConfig_tableQuantity}")
    @Getter
	private Integer tableQuantity = 1;

	
	/**
	  * 根据用户登录日志Id查询分配到表编号
	  * 根据用户登录日志Id和用户登录日志日志分表数量求余
	  * @param userLoginLogId 用户登录日志Id
	  * 注意：用户登录日志Id要先判断是否36位并且最后4位是数字
	  * userLoginLogManage.verificationUserLoginLogId(?)
	  * @return
	 */
	public Integer userLoginLogIdRemainder(String userLoginLogId){
	   int userId = this.getUserLoginLogUserId(userLoginLogId);
	   return userId % this.getTableQuantity();
	} 
   /**
    * 根据用户Id查询分配到表编号
    * 根据用户Id和用户登录日志分表数量求余(用户Id后四位)
    * @param userId 用户Id
    * @return
    */
	public Integer userIdRemainder(Long userId){
	   	//选取得后N位用户Id
	   	String afterUserId = String.format("%04d", userId%10000);
	   	return Integer.parseInt(afterUserId) % this.getTableQuantity();
	}

    /**
     * 取得用户登录日志Id的用户Id(后N位)
     * @param userLoginLogId 用户登录日志Id
     * @return
     */
    public int getUserLoginLogUserId(String userLoginLogId){
        String after_userId = userLoginLogId.substring(userLoginLogId.length()-4, userLoginLogId.length());
        return Integer.parseInt(after_userId);
    }

    /**
     * 生成用户登录日志Id
     * 积分Id由32位uuid+1位用户Id后4位组成
     * @param userId 用户Id
     * @return
     */
    public String createUserLoginLogId(Long userId){
        //选取得后N位用户Id
        String afterUserId = String.format("%04d", userId%10000);
        String id = UUIDUtil.getUUID32()+afterUserId;
        return id;
    }
    /**
     * 校验用户登录日志Id
     * 用户登录日志Id要先判断是否36位并且最后4位是数字
     * @param userLoginLogId 用户登录日志Id
     * @return
     */
    public boolean verificationUserLoginLogId(String userLoginLogId){
        if(userLoginLogId != null && !"".equals(userLoginLogId.trim())){
            if(userLoginLogId.length() == 36){
                String after_userId = userLoginLogId.substring(userLoginLogId.length()-4, userLoginLogId.length());
                boolean verification = Verification.isPositiveIntegerZero(after_userId);//数字
                if(verification){
                    return true;
                }
            }
        }
        return false;
    }
}
