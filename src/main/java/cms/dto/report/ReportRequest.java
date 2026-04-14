package cms.dto.report;


import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 举报表单
 */
@Getter
@Setter
public class ReportRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 2059315752356543175L;
    /** Id  **/
    private Long reportId;
    /** 举报分类Id  **/
    private String reportTypeId;
    /** 状态 **/
    private Integer status;
    /** 理由 **/
    private String reason;

    /** 处理结果 **/
    private String processResult;

    /** 备注 **/
    private String remark;
    /** 图片路径 **/
    private String[] imagePath;
    /** 版本 **/
    private Integer version;

}
