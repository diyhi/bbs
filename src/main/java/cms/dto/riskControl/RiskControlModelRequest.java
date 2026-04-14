package cms.dto.riskControl;


import cms.model.riskControl.Penalty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 风控模型表单
 */
@Getter
@Setter
public class RiskControlModelRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -4122350306666451517L;

    /** Id  **/
    private Integer riskControlModelId;
    /** 名称 **/
    private String name;

    /** 类型 **/
    private Integer type;
    /** 风控规则 **/
    private String rules;

    /** 是否选择  true:启用 false: 禁用 **/
    private boolean enable = true;

    /** 排序 **/
    private Integer sort = 1;
    /** 备注 **/
    private String remark;

    /** 风控处罚 **/
    private Penalty penalty = new Penalty();

    /** 关联风控敏感词Id **/
    private Integer[] associateSensitiveWordId;
    /** 风控禁止的功能编号 **/
    private Integer riskControlBlockedFeaturesCode[];
    /** 风控执行贴子的功能编号 **/
    private Integer riskControlPostFeaturesCode;
}
