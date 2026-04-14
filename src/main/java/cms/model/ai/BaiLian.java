package cms.model.ai;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 阿里云百炼大模型参数
 *
 */
@Getter
@Setter
public class BaiLian implements Serializable{
	@Serial
    private static final long serialVersionUID = -2282654527249181999L;
	
	/** API密钥(API-KEY) **/
	private String apiKey;
	/** 应用Id **/
	private String appId;
	/** 业务空间Id **/
	private String workspaceId;

}
