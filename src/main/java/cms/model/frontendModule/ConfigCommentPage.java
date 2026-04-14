package cms.model.frontendModule;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 评论参数配置
 */
@Getter
@Setter
public class ConfigCommentPage implements Serializable {
    @Serial
    private static final long serialVersionUID = -6454496411517845039L;

    /** Id **/
    private String id;

    /** 每页显示记录数 **/
    private Integer maxResult;
    /** 页码显示总数 **/
    private Integer pageCount;

    /** 排序 **/
    private Integer sort;
}
