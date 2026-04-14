package cms.model.frontendModule;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 自定义HTML参数配置
 */
@Getter
@Setter
public class ConfigCustomHtml implements Serializable {
    @Serial
    private static final long serialVersionUID = -5622916332697129666L;

    /** 自定义HTML Id **/
    private String id;

    /** 自定义HTML内容 **/
    private String content;
}
