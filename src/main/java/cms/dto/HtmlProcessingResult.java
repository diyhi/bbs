package cms.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Html内容处理结果
 */
@Getter
@Setter
public class HtmlProcessingResult {
    /** 过滤后的HTML内容 **/
    private String htmlContent;
    /** 上传的图片文件名称列表 **/
    private List<String> imageNameList;
    /** 是否包含图片 **/
    private boolean hasImage;
    /** 上传的音视频文件名称列表 **/
    private List<String> mediaNameList;
    /** 是否包含音视频 **/
    private boolean hasMedia;
    /** 上传的文件名称列表 **/
    private List<String> fileNameList;
    /** 是否包含文件 **/
    private boolean hasFile;
    /** 是否插入了地图 **/
    private boolean hasMap;
    /** 提及的用户名称列表 **/
    private LinkedHashSet<String> mentionUserNameList;
}
