package cms.component.redEnvelope;


import cms.utils.Verification;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 收红包配置
 *
 */
@Component("receiveRedEnvelopeConfig")
public class ReceiveRedEnvelopeConfig {
	
	/** 分表数量 **/
	@Value("${bbs.sharding.receiveRedEnvelopeConfig_tableQuantity}")
    @Getter
	private Integer tableQuantity = 1;
	
	/**
	  * 根据收红包Id查询分配到表编号
	  * 根据收红包Id和收红包分表数量求余
	  * @param receiveRedEnvelopeId 收红包Id
	  * 注意：收红包Id要先判断最后4位是不是数字
	  * redEnvelopeManage.verificationReceiveRedEnvelopeId(?)
	  * @return
	 */
	public Integer receiveRedEnvelopeIdRemainder(String receiveRedEnvelopeId){
	   int afterUserId = this.getAfterUserId(receiveRedEnvelopeId);
	   return afterUserId % this.getTableQuantity();
	} 
   /**
    * 根据用户Id查询分配到表编号
    * 根据用户Id和收红包分表数量求余(用户Id后四位)
    * @param userId 用户Id
    * @return
    */
	public Integer userIdRemainder(Long userId){
	   	//选取得后N位用户Id
	   	String afterUserId = String.format("%04d", userId%10000);
	   	return Integer.parseInt(afterUserId) % this.getTableQuantity();
	}

    /**
     * 取得收红包Id的用户Id(后N位)
     * @param receiveRedEnvelopeId 收红包Id
     * @return
     */
    public int getAfterUserId(String receiveRedEnvelopeId){
        String[] idGroup = receiveRedEnvelopeId.split("_");
        Long userId = Long.parseLong(idGroup[1]);

        //选取得后N位用户Id
        String after_userId = String.format("%04d", Math.abs(userId)%10000);
        return Integer.parseInt(after_userId);
    }

    /**
     * 生成收红包Id
     * 收红包Id由发红包Id-领红包的用户Id组成
     * @param giveRedEnvelopeId 发红包Id
     * @param userId 用户Id
     * @return
     */
    public String createReceiveRedEnvelopeId(String giveRedEnvelopeId,Long userId){
        return giveRedEnvelopeId+"_"+userId;
    }

    /**
     * 校验收红包Id
     * @param receiveRedEnvelopeId 收红包Id
     * @return
     */
    public boolean verificationReceiveRedEnvelopeId(String receiveRedEnvelopeId){
        if(receiveRedEnvelopeId != null && !"".equals(receiveRedEnvelopeId.trim())){
            String[] idGroup = receiveRedEnvelopeId.split("_");
            for(String id : idGroup){
                boolean verification = Verification.isPositiveIntegerZero(id);//数字
                if(!verification){
                    return false;
                }
                return true;
            }

        }
        return false;
    }
}
