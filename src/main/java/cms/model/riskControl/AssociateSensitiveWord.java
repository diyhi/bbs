package cms.model.riskControl;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 关联风控敏感词
 * @author Gao
 *
 */
@Getter
@Setter
public class AssociateSensitiveWord implements Serializable{
	@Serial
    private static final long serialVersionUID = 1940579080343615450L;
	
	/** 风控敏感词Id **/
	private Integer riskSensitiveWordsId;
	/** 风控敏感词名称 **/
	private String riskSensitiveWordsName;
	/** 是否有效(在显示时重新设置使用，存储不代表有效) **/
	private boolean isEffective;
	
}
