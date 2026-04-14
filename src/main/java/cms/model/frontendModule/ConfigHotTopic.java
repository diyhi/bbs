package cms.model.frontendModule;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 热门话题参数配置
 */
@Getter
@Setter
public class ConfigHotTopic implements Serializable {
    @Serial
    private static final long serialVersionUID = -6872251226358547812L;

    /** Id **/
    private String id;

    /** 显示记录数 **/
    private Integer maxResult;
}
