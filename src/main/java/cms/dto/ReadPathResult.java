package cms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * HTML内容中文件路径的读取结果
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReadPathResult {
    private List<String> imageNameList = new ArrayList<>();
    private List<String> mediaNameList = new ArrayList<>();
    private List<String> fileNameList = new ArrayList<>();
}
