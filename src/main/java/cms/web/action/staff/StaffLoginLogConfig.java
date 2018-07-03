package cms.web.action.staff;

import javax.annotation.Resource;

/**
 * 员工登录日志配置
 *
 */
public class StaffLoginLogConfig {
	@Resource StaffLoginLogManage staffLoginLogManage;
	
	/** 分表数量 **/
	private Integer tableQuantity = 1;
	
	public Integer getTableQuantity() {
		return tableQuantity;
	}
	public void setTableQuantity(Integer tableQuantity) {
		this.tableQuantity = tableQuantity;
	}
	
	/**
	  * 根据员工登录日志Id查询分配到表编号
	  * 根据员工登录日志Id哈希值和员工登录日志日志分表数量求余
	  * @param staffLoginLogId 员工登录日志Id
	  * 注意：员工Id要先判断是否36位并且最后4位是数字
	  * staffLoginLogManage.verificationStaffLoginLogId(?)
	  * @return
	 */
	public Integer staffLoginLogIdRemainder(String staffLoginLogId){
	   int after_staffId = staffLoginLogManage.getStaffLoginLogUserId(staffLoginLogId);
	   return after_staffId % this.getTableQuantity();
	} 
   /**
    * 根据员工Id查询分配到表编号
    * 根据员工Id和员工登录日志分表数量求余(员工Id哈希值后四位)
    * @param userId 用户Id
    * @return
    */
	public Integer staffIdRemainder(String staffId){
	   	//选取得后N位员工Id,哈希值有可能负数，所以要计算绝对值
	   	String afterStaffId = String.format("%04d", Math.abs(staffId.hashCode()%10000));
	   	return Integer.parseInt(afterStaffId) % this.getTableQuantity();
	}
	 
}
