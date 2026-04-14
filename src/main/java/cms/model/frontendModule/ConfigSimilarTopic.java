package cms.model.frontendModule;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 相似话题参数配置
 */
@Getter
@Setter
public class ConfigSimilarTopic implements Serializable {
    @Serial
    private static final long serialVersionUID = 6158171071047805301L;

    /** 版块---话题相关--相似话题  Id **/
    private String id;

    /** 显示记录数 **/
    private Integer maxResult;
}
