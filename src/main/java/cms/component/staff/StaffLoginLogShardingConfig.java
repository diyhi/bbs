package cms.component.staff;


import cms.utils.UUIDUtil;
import cms.utils.Verification;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 员工登录日志配置
 *
 */
@Component("staffLoginLogShardingConfig")
public class StaffLoginLogShardingConfig {
	
	/** 分表数量 **/
	@Value("${bbs.sharding.staffLoginLogConfig_tableQuantity}")
    @Getter
    private Integer tableQuantity = 1;

	/**
	  * 根据员工登录日志Id查询分配到表编号
	  * 根据员工登录日志Id哈希值和员工登录日志日志分表数量求余
	  * @param staffLoginLogId 员工登录日志Id
	  * 注意：员工Id要先判断是否36位并且最后4位是数字
	  * staffLoginLogManage.verificationStaffLoginLogId(?)
	  * @return
	 */
	public Integer staffLoginLogIdRemainder(String staffLoginLogId){
	   int after_staffId = this.getStaffLoginLogUserId(staffLoginLogId);
	   return after_staffId % tableQuantity;
	} 
   /**
    * 根据员工Id查询分配到表编号
    * 根据员工Id和员工登录日志分表数量求余(员工Id哈希值后四位)
    * @param staffId 员工Id
    * @return
    */
	public Integer staffIdRemainder(String staffId){
	   	//选取得后N位员工Id,哈希值有可能负数，所以要计算绝对值
	   	String afterStaffId = String.format("%04d", Math.abs(staffId.hashCode()%10000));
	   	return Integer.parseInt(afterStaffId) % tableQuantity;
	}


    /**
     * 取得员工登录日志Id的员工Id哈希值(后N位)
     * @param staffLoginLogId 员工登录日志Id
     * @return
     */
    public int getStaffLoginLogUserId(String staffLoginLogId){
        String after_staffId = staffLoginLogId.substring(staffLoginLogId.length()-4, staffLoginLogId.length());
        return Integer.parseInt(after_staffId);
    }

    /**
     * 生成员工登录日志Id
     * 登录日志Id由32位uuid+员工Id哈希值后4位组成
     * @param staffId 员工Id
     * @return
     */
    public String createStaffLoginLogId(String staffId){
        //员工Id哈希值后N位,哈希值有可能负数，所以要计算绝对值
        String afterStaffId = String.format("%04d", Math.abs(staffId.hashCode()%10000));
        return UUIDUtil.getUUID32()+afterStaffId;
    }


    /**
     * 校验员工登录日志Id
     * 员工登录日志Id要先判断是否36位并且最后4位是数字
     * @param staffLoginLogId 员工登录日志Id
     * @return
     */
    public boolean verificationStaffLoginLogId(String staffLoginLogId){
        if(staffLoginLogId != null && !staffLoginLogId.trim().isEmpty()){
            if(staffLoginLogId.length() == 36){
                String after_staffId = staffLoginLogId.substring(staffLoginLogId.length()-4, staffLoginLogId.length());
                boolean verification = Verification.isPositiveIntegerZero(after_staffId);//数字
                if(verification){
                    return true;
                }
            }
        }
        return false;
    }
}
