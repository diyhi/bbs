package cms.model.frontendModule;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 话题精华参数配置
 */
@Getter
@Setter
public class ConfigTopicFeaturedPage implements Serializable {
    @Serial
    private static final long serialVersionUID = -173806586701380833L;

    /** Id **/
    private String id;

    /** 排序 **/
    private Integer sort;

    /** 每页显示记录数 **/
    private Integer  maxResult;
    /** 页码显示总数 **/
    private Integer  pageCount;
}
