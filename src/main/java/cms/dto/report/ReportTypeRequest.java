package cms.dto.report;


import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 举报分类表单
 */
@Getter
@Setter
public class ReportTypeRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1901489357641810164L;

    /** Id  **/
    private String reportTypeId;
    /** 名称 **/
    private String name;
    /** 父Id  **/
    private String parentId;

    /** 是否需要说明理由  true:启用 false: 禁用 **/
    private Boolean giveReason = true;

    /** 排序 **/
    private Integer sort = 1;

}
