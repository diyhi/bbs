package cms.dto.thumbnail;


import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 缩略图表单
 */
@Getter
@Setter
public class ThumbnailRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 3797525300128668176L;

    /** Id **/
    private Integer id;
    /** 缩略图名称 **/
    private String name;
    /** 宽 **/
    private Integer width;
    /** 高 **/
    private Integer high;
}
