package cms.web.action.message;

import javax.annotation.Resource;

/**
 * 私信配置
 *
 */
public class PrivateMessageConfig {
	@Resource PrivateMessageManage privateMessageManage;
	
	/** 分表数量 **/
	private Integer tableQuantity = 1;
	
	public Integer getTableQuantity() {
		return tableQuantity;
	}
	public void setTableQuantity(Integer tableQuantity) {
		this.tableQuantity = tableQuantity;
	}
	
	/**
	  * 根据私信Id查询分配到表编号
	  * 根据私信Id和私信分表数量求余
	  * @param privateMessaged 私信Id
	  * 注意：私信Id要先判断是否36位并且最后4位是数字
	  * privateMessageManage.verificationPrivateMessageId(?)
	  * @return
	 */
	public Integer privateMessageIdRemainder(String privateMessageId){
	   int userId = privateMessageManage.getPrivateMessageId(privateMessageId);
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
	 
}
