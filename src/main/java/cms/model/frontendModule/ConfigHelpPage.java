package cms.model.frontendModule;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 在线帮助分页参数配置
 */
@Getter
@Setter
public class ConfigHelpPage implements Serializable {
    @Serial
    private static final long serialVersionUID = 5376450372651447883L;

    /** Id **/
    private String id;

    /** 在线帮助展示数量 **/
    private Integer quantity;
    /** 排序 **/
    private Integer sort;

    /** 每页显示记录数 **/
    private Integer  maxResult;
    /** 页码显示总数 **/
    private Integer  pageCount;

    /** 分类Id **/
    private Long helpTypeId;
    /** 分类名称 **/
    private String helpTypeName;
    /** 是否传递分类参数 **/
    private boolean helpType_transferPrameter = false;

}
