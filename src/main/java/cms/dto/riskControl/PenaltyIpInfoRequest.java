package cms.dto.riskControl;


import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 处罚IP信息表单
 */
@Getter
@Setter
public class PenaltyIpInfoRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -5980959166335906158L;
    /** IP **/
    private String ip;
    /** 封禁结束时间 **/
    private String banEndTime;
    /** 备注 **/
    private String remark;
}
