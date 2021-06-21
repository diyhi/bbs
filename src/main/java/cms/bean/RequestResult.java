package cms.bean;

import java.io.Serializable;

/**
 * 返回请求结果
 * @author Gao
 *
 */
public class RequestResult implements Serializable{
	private static final long serialVersionUID = 903133110511739665L;
	
	/** 返回状态码  200:成功 500:错误 **/
	private int code = 200;
	 /** 返回信息描述 **/
    private String message = "成功";
	
	/** 返回数据 **/
    private Object data;

    
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

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
   

   

}
