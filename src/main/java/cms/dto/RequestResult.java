package cms.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 返回请求结果
 * @author Gao
 *
 */
@Getter
@Setter
public class RequestResult implements Serializable{
	@Serial
    private static final long serialVersionUID = 903133110511739665L;
	
	/** 返回状态码  200:成功 500:错误 **/
	private int code = 200;
	 /** 返回信息描述 **/
    private String message = "成功";
	
	/** 返回数据 **/
    private Object data;
    /** 附加数据
    private Object appendData; **/
    
    public RequestResult() {}
    public RequestResult(ResultCode resultCode, Object data) {
    	this.code = resultCode.getCode();
		this.message = resultCode.getMessage();
		this.data = data;
	}
	public RequestResult(int code, String message, Object data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}
}
