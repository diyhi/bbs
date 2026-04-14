package cms.model.frontendModule;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 问题参数配置
 */
@Getter
@Setter
public class ConfigQuestionPage implements Serializable {
    @Serial
    private static final long serialVersionUID = -4550789258334527836L;

    /** Id **/
    private String id;

    /** 问题展示数量 **/
    private Integer quantity;
    /** 排序 **/
    private Integer sort;

    /** 每页显示记录数 **/
    private Integer maxResult;
    /** 页码显示总数 **/
    private Integer pageCount;

    /** 标签Id **/
    private Long tagId;
    /** 标签名称 **/
    private String tagName;
    /** 是否传递标签参数 **/
    private boolean tag_transferPrameter = false;


    /** 过滤条件  **/
    private Integer filterCondition;
    /** 是否传递过滤参数 **/
    private boolean filterCondition_transferPrameter = false;
}
