package cms.dto;

import lombok.Getter;

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
    @Getter
	private final Integer code;
	/** 返回信息描述 **/
    @Getter
    private final String message ;
    private ResultCode(Integer code, String message) {
		this.code = code;
		this.message = message;
	}

	
}
