package cms.bean.ai;

import java.io.Serializable;

/**
 * 对话信息
 * @author Gao
 *
 */
public class ChatInfo implements Serializable{
	private static final long serialVersionUID = 7432882984799611317L;

	/** 系统生成的标识。请求ID **/
	private String requestId;
	
	/** 模型生成的回复内容(Markdown格式) **/
	private String text;
	
	/** 接口产品 10:阿里云百炼大模型  20.火山方舟大模型 **/
	private Integer interfaceProduct;
	
	
	
	/** 表示状态码，调用成功时为空值 **/
	private Integer statusCode;
	/** 表示错误码，调用成功时为空值 **/
	private String errorCode;
	/** 表示失败详细信息，成功忽略。 **/
	private String errorMessage;
	
	

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getInterfaceProduct() {
		return interfaceProduct;
	}

	public void setInterfaceProduct(Integer interfaceProduct) {
		this.interfaceProduct = interfaceProduct;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}



}
