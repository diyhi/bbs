package cms.dto.frontendModule;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 图片广告
 */
@Getter
@Setter
public class ImageAd implements Serializable {
    @Serial
    private static final long serialVersionUID = 3268776432793931075L;

    /** 图片名称 **/
    private String name;
    /** 图片链接 **/
    private String link;
    /** 图片路径 **/
    private String path;
}
