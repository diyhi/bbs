package cms.dto.riskControl;

/**
 * 贴子功能枚举
 * @author Gao
 *
 */
public enum PostFeatureEnum {
	/** 贴子设为待审核状态 **/
	POST_REVIEW_STATE("贴子设为待审核状态", 100), 
	/** 贴子设为待审核状态(特权用户免审核) **/
	PRIVILEGED_USERS_EXEMPT_REVIEW("贴子设为待审核状态(特权用户免审核)", 200),
	/** 删除贴子 **/
	DELETE_POST("删除贴子", 300);

	
	/**
	 * 
	 * @param name 名称
	 * @param code 编号
	 */
    private PostFeatureEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }
    /** 名称 **/
    private String name;
    /** 编号 **/
    private Integer code;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	
    
}
