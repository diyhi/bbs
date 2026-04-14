package cms.model.frontendModule;

import jakarta.persistence.Lob;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 图片广告参数配置
 */
@Getter
@Setter
public class ConfigImageAd implements Serializable {
    @Serial
    private static final long serialVersionUID = -8207327160420589479L;

    /** 版块---广告相关--轮播广告  Id **/
    private String id;

    /** 图片名称 **/
    private String name;

    /** 多语言扩展  key:字段-语言（例如：name-en_US） value:内容**/
    private Map<String,String> multiLanguageExtensionField = new HashMap<String,String>();

    /** 图片链接 **/
    private String link;

    /** 图片文件路径 **/
    private String imagePath;
}
