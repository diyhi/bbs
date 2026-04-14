package cms.component.message;


import cms.utils.UUIDUtil;
import cms.utils.Verification;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 提醒配置
 *
 */
@Component("remindConfig")
public class RemindConfig {
	
	/** 分表数量 **/
	@Value("${bbs.sharding.remindConfig_tableQuantity}")
    @Getter
	private Integer tableQuantity = 1;

	
	/**
	  * 根据提醒Id查询分配到表编号
	  * 根据提醒Id和提醒分表数量求余
	  * @param remindId 提醒Id
	  * 注意：提醒Id要先判断是否36位并且最后4位是数字
	  * remindManage.verificationRemindId(?)
	  * @return
	 */
	public Integer remindIdRemainder(String remindId){
	   int userId = this.getRemindId(remindId);
	   return userId % this.getTableQuantity();
	} 
   /**
    * 根据提醒Id查询分配到表编号
    * 根据提醒Id和提醒分表数量求余(用户Id后四位)
    * @param userId 用户Id
    * @return
    */
	public Integer userIdRemainder(Long userId){
	   	//选取得后N位用户Id
	   	String afterUserId = String.format("%04d", userId%10000);
	   	return Integer.parseInt(afterUserId) % this.getTableQuantity();
	}

    /**
     * 取得提醒Id的用户Id(后N位)
     * @param remindId 提醒Id
     * @return
     */
    public int getRemindId(String remindId){
        String after_userId = remindId.substring(remindId.length()-4, remindId.length());
        return Integer.parseInt(after_userId);
    }

    /**
     * 生成提醒Id
     * 提醒Id由32位uuid+1位用户Id后4位组成
     * @param userId 用户Id
     * @return
     */
    public String createRemindId(Long userId){
        //选取得后N位用户Id
        String afterUserId = String.format("%04d", userId%10000);
        String id = UUIDUtil.getUUID32()+afterUserId;
        return id;
    }
    /**
     * 校验提醒Id
     * 提醒Id要先判断是否36位并且最后4位是数字
     * @param remindId 提醒Id
     * @return
     */
    public boolean verificationRemindId(String remindId){
        if(remindId != null && !"".equals(remindId.trim())){
            if(remindId.length() == 36){
                String after_userId = remindId.substring(remindId.length()-4, remindId.length());
                boolean verification = Verification.isPositiveIntegerZero(after_userId);//数字
                if(verification){
                    return true;
                }
            }
        }
        return false;
    }
}
