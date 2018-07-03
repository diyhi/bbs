package cms.bean;

import java.io.Serializable;

/**
 * httpClient返回数据
 *
 */
public class HttpResult implements Serializable{
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

    

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
