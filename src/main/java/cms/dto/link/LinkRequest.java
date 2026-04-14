package cms.dto.link;


import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 友情链接表单
 */
@Getter
@Setter
public class LinkRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -4370242595820070493L;

    /** Id  **/
    private Integer linksId;
    /** 名称**/
    private String name;
    /** 网址 **/
    private String website;
    /** 排序 **/
    private Integer sort = 0;
    /** 图片 **/
    private String image;
    /** 图片路径 **/
    private String imagePath;
}
