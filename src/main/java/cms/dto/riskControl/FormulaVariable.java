package cms.dto.riskControl;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 公式变量
 * @author Gao
 *
 */
@Getter
@Setter
public class FormulaVariable implements Serializable{
	@Serial
    private static final long serialVersionUID = -4554322383141794504L;

	/** 类型  10：发贴(发贴包含'发话题'、'修改话题'、'发评论'、'修改评论'、'发评论回复'、'修改评论回复'、'发问题'、追加问题'、'发答案'、'修改答案'、'发答案回复'、'修改答案回复'等操作)   20：注册 **/
	private Integer type;
	/** 名称 **/
	private String name;
	/** 支持变量 key:变量字段  value:备注 **/
	private Map<String,String> variableMap = new LinkedHashMap<String,String>();
	
	public void addVariable(String key, String value) {
		variableMap.put(key, value);
	}
}
