package cms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;

/**
 * HTML 内容校正结果
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CorrectionResult {
    /** 校正后的 HTML 内容 */
    private String correctedHtml;
    /** @提及的用户名称列表 */
    private LinkedHashSet<String> mentionUserNameList = new LinkedHashSet<>();
    /** 是否包含图片 */
    private boolean hasImage = false;
}
