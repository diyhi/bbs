package cms.model.frontendModule;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 话题参数配置
 */
@Getter
@Setter
public class ConfigTopicPage implements Serializable {
    @Serial
    private static final long serialVersionUID = -1164115319076340262L;

    /** Id **/
    private String id;

    /** 话题展示数量 **/
    private Integer quantity;
    /** 排序 **/
    private Integer sort;

    /** 每页显示记录数 **/
    private Integer  maxResult;
    /** 页码显示总数 **/
    private Integer  pageCount;

    /** 标签Id **/
    private Long tagId;
    /** 标签名称 **/
    private String tagName;
    /** 是否传递标签参数 **/
    private boolean tag_transferPrameter = false;
}
