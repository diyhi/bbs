package cms.bean;

/**
 * 状态码
 * @author Gao
 *
 */
public enum ResultCode {
	
	/** 成功 **/
	SUCCESS(200,"成功"),
	/** 失败 **/
	FAILURE(500,"失败");
	
	/** 返回状态码 **/
	private Integer code;
	 /** 返回信息描述 **/
    private String message ;
    private ResultCode(Integer code, String message) {
		this.code = code;
		this.message = message;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
