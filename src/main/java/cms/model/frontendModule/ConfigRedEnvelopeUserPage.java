package cms.model.frontendModule;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 领取红包用户分页参数配置
 */
@Getter
@Setter
public class ConfigRedEnvelopeUserPage implements Serializable {
    @Serial
    private static final long serialVersionUID = -5488243972273404086L;

    /** 版块---红包相关--领取红包用户列表  Id **/
    private String id;

    /** 显示记录数 **/
    private Integer maxResult;
    /** 排序 10.领取时间新--旧 20..领取时间旧--新 **/
    private Integer sort;
}
