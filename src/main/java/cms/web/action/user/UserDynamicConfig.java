package cms.web.action.user;

import javax.annotation.Resource;

/**
 * 用户动态配置
 *
 */
public class UserDynamicConfig {
	@Resource UserDynamicManage userDynamicManage;
	
	
	/** 分表数量 **/
	private Integer tableQuantity = 1;
	
	public Integer getTableQuantity() {
		return tableQuantity;
	}
	public void setTableQuantity(Integer tableQuantity) {
		this.tableQuantity = tableQuantity;
	}
	
	
	 /**
	  * 根据用户动态Id查询分配到表编号
	  * 根据用户动态Id和用户动态分表数量求余
	  * @param userDynamicId 用户动态Id
	  * 注意：用户动态Id要先判断是否36位并且最后4位是数字
	  * userDynamicManage.verificationUserDynamicId(?)
	  * @return
	 */
    public Integer userDynamicIdRemainder(String userDynamicId){
    	int userId = userDynamicManage.getUserDynamicUserId(userDynamicId);
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
}
