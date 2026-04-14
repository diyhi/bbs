package cms.dto.ai;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 对话信息
 * @author Gao
 *
 */
@Getter
@Setter
public class ChatInfo implements Serializable{
	@Serial
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
	

}
