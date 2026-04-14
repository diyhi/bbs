package cms.model.riskControl;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 风控禁止的功能
 * @author Gao
 *
 */
@Getter
@Setter
public class RiskControlBlockedFeatures implements Serializable{
	@Serial
    private static final long serialVersionUID = -6074916612773444369L;
	
	/** 名称 **/
    private String name;
    /** 编号 **/
    private Integer code;
    /** 选中 **/
	private Boolean selected = false;

}
