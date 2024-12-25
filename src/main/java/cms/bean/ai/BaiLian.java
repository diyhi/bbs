package cms.bean.ai;

import java.io.Serializable;

/**
 * 阿里云百炼大模型参数
 *
 */
public class BaiLian implements Serializable{
	private static final long serialVersionUID = -2282654527249181999L;
	
	/** API密钥(API-KEY) **/
	private String apiKey;
	/** 应用Id **/
	private String appId;
	/** 业务空间Id **/
	private String workspaceId;
	
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getWorkspaceId() {
		return workspaceId;
	}
	public void setWorkspaceId(String workspaceId) {
		this.workspaceId = workspaceId;
	}
}
