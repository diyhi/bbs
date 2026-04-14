package cms.dto.frontendModule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 举报表单
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 7826290682122459591L;

    /** 举报分类Id **/
    private String reportTypeId;
    /** 理由 **/
    private String reason;
    /** 参数Id **/
    private String parameterId;
    /** 模块 **/
    private Integer module;
    /** 验证码Key **/
    private String captchaKey;
    /** 验证码值 **/
    private String captchaValue;
}
