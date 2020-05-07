package cms.bean.thirdParty;

import java.io.Serializable;

/**
 * 微信配置信息
 * @author Gao
 *
 */
public class WeChatConfig implements Serializable{
	private static final long serialVersionUID = -5692566131895731007L;
	
	/** 开放平台 - 应用唯一标识 **/
	private String op_appID="";
	/** 开放平台 - 应用密钥 **/
	private String op_appSecret="";
	
	/** 公众号 - 应用唯一标识 **/
	private String oa_appID="";
	/** 公众号 - 应用密钥 **/
	private String oa_appSecret="";
	
	/**
	 * 开放平台 - 应用唯一标识
	 * @return
	 */
	public String getOp_appID() {
		return op_appID;
	}
	public void setOp_appID(String op_appID) {
		this.op_appID = op_appID;
	}
	/**
	 * 开放平台 - 应用密钥
	 * @return
	 */
	public String getOp_appSecret() {
		return op_appSecret;
	}
	public void setOp_appSecret(String op_appSecret) {
		this.op_appSecret = op_appSecret;
	}
	/**
	 * 公众号 - 应用唯一标识
	 * @return
	 */
	public String getOa_appID() {
		return oa_appID;
	}
	public void setOa_appID(String oa_appID) {
		this.oa_appID = oa_appID;
	}
	/**
	 * 公众号 - 应用密钥
	 * @return
	 */
	public String getOa_appSecret() {
		return oa_appSecret;
	}
	public void setOa_appSecret(String oa_appSecret) {
		this.oa_appSecret = oa_appSecret;
	}
	
	
	
}
