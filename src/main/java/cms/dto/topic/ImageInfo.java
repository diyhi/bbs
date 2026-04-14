package cms.dto.topic;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 图片信息
 *
 */
@Getter
@Setter
public class ImageInfo implements Serializable{
	@Serial
    private static final long serialVersionUID = 5884756296465895791L;
	/** 图片路径 **/
	private String path;
	/** 图片名称 **/
	private String name;
	
	
	public ImageInfo() {}
	public ImageInfo(String path, String name) {
		this.path = path;
		this.name = name;
	}

}
