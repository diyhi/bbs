package cms.model.frontendModule;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 相似问题参数配置
 */
@Getter
@Setter
public class ConfigSimilarQuestion implements Serializable {
    @Serial
    private static final long serialVersionUID = -61574932708832657L;

    /** Id **/
    private String id;

    /** 显示记录数 **/
    private Integer maxResult;
}
