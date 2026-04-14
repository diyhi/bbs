package cms.dto.frontendModule;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 自定义HTML
 *
 */
@Getter
@Setter
public class CustomHTML implements Serializable{
    @Serial
    private static final long serialVersionUID = 194847547200592133L;

    /** 版块标题 **/
	private String forumTitle;
	/** 自定义HTML内容 **/
	private String content;
}
