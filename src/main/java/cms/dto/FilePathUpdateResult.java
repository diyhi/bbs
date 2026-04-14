package cms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件路径修改结果
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilePathUpdateResult {
    /** 更新后的HTML内容 */
    private String htmlContent;
    /** 旧路径 -> 新路径 的文件映射 */
    private Map<String, String> oldToNewPathMap = new HashMap<>();
}
