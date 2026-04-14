package cms.component.message;


import cms.utils.UUIDUtil;
import cms.utils.Verification;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 私信配置
 *
 */
@Component("privateMessageConfig")
public class PrivateMessageConfig {

	/** 分表数量 **/
	@Value("${bbs.sharding.privateMessageConfig_tableQuantity}")
    @Getter
    private Integer tableQuantity = 1;

	
	/**
	  * 根据私信Id查询分配到表编号
	  * 根据私信Id和私信分表数量求余
	  * @param privateMessageId 私信Id
	  * 注意：私信Id要先判断是否36位并且最后4位是数字
	  * privateMessageManage.verificationPrivateMessageId(?)
	  * @return
	 */
	public Integer privateMessageIdRemainder(String privateMessageId){
	   int userId = this.getPrivateMessageId(privateMessageId);
	   return userId % this.getTableQuantity();
	} 
   /**
    * 根据私信Id查询分配到表编号
    * 根据私信Id和私信分表数量求余(用户Id后四位)
    * @param userId 用户Id
    * @return
    */
	public Integer userIdRemainder(Long userId){
	   	//选取得后N位用户Id
	   	String afterUserId = String.format("%04d", userId%10000);
	   	return Integer.parseInt(afterUserId) % this.getTableQuantity();
	}


    /**
     * 取得私信Id的用户Id(后N位)
     * @param privateMessageId 私信Id
     * @return
     */
    public int getPrivateMessageId(String privateMessageId){
        String after_userId = privateMessageId.substring(privateMessageId.length()-4, privateMessageId.length());
        return Integer.parseInt(after_userId);
    }

    /**
     * 生成私信Id
     * 私信Id由32位uuid+1位用户Id后4位组成
     * @param userId 用户Id
     * @return
     */
    public String createPrivateMessageId(Long userId){
        //选取得后N位用户Id
        String afterUserId = String.format("%04d", userId%10000);
        String id = UUIDUtil.getUUID32()+afterUserId;
        return id;
    }
    /**
     * 校验私信Id
     * 私信Id要先判断是否36位并且最后4位是数字
     * @param privateMessageId 私信Id
     * @return
     */
    public boolean verificationPrivateMessageId(String privateMessageId){
        if(privateMessageId != null && !"".equals(privateMessageId.trim())){
            if(privateMessageId.length() == 36){
                String after_userId = privateMessageId.substring(privateMessageId.length()-4, privateMessageId.length());
                boolean verification = Verification.isPositiveIntegerZero(after_userId);//数字
                if(verification){
                    return true;
                }
            }
        }
        return false;
    }
	 
}
