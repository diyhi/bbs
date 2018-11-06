package cms.web.action.message;

import javax.annotation.Resource;

/**
 * 提醒配置
 *
 */
public class RemindConfig {
	@Resource RemindManage remindManage;
	
	/** 分表数量 **/
	private Integer tableQuantity = 1;
	
	public Integer getTableQuantity() {
		return tableQuantity;
	}
	public void setTableQuantity(Integer tableQuantity) {
		this.tableQuantity = tableQuantity;
	}
	
	/**
	  * 根据提醒Id查询分配到表编号
	  * 根据提醒Id和提醒分表数量求余
	  * @param remindId 提醒Id
	  * 注意：提醒Id要先判断是否36位并且最后4位是数字
	  * remindManage.verificationRemindId(?)
	  * @return
	 */
	public Integer remindIdRemainder(String remindId){
	   int userId = remindManage.getRemindId(remindId);
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
	 
}
