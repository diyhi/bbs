package cms.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * httpClient返回数据
 *
 */
@Getter
@Setter
public class HttpResult implements Serializable{
	@Serial
    private static final long serialVersionUID = 383699323716307374L;
	
	/** HTTP协议状态码 **/
	private Integer code;
	/** 数据 **/
    private String data;
    
    public HttpResult(){}
    public HttpResult(Integer code, String data) {
        this.code = code;
        this.data = data;
    }

}
