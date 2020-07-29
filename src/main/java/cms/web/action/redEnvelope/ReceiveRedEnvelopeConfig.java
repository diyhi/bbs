package cms.web.action.redEnvelope;

import javax.annotation.Resource;

/**
 * 收红包配置
 *
 */
public class ReceiveRedEnvelopeConfig {
	@Resource RedEnvelopeManage redEnvelopeManage;
	
	/** 分表数量 **/
	private Integer tableQuantity = 1;
	
	public Integer getTableQuantity() {
		return tableQuantity;
	}
	public void setTableQuantity(Integer tableQuantity) {
		this.tableQuantity = tableQuantity;
	}
	
	/**
	  * 根据收红包Id查询分配到表编号
	  * 根据收红包Id和收红包分表数量求余
	  * @param receiveRedEnvelopeId 收红包Id
	  * 注意：收红包Id要先判断最后4位是不是数字
	  * redEnvelopeManage.verificationReceiveRedEnvelopeId(?)
	  * @return
	 */
	public Integer receiveRedEnvelopeIdRemainder(String receiveRedEnvelopeId){
	   int afterUserId = redEnvelopeManage.getAfterUserId(receiveRedEnvelopeId);
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
	 
}
