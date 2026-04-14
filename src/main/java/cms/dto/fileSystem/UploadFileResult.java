package cms.dto.fileSystem;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * SeaweedFS 上传文件完成后返回数据
 * @author Gao
 *
 */
@Getter
@Setter
public class UploadFileResult implements Serializable{
	@Serial
    private static final long serialVersionUID = -5455635891043547088L;
	
	/** 文件名称(传入类型是byte[]时为空) **/
	private String name;
	/** 文件大小 **/
	private Integer size;
	/** eTag消息头,html中用于标示URL对象是否改变 **/
	private String eTag;
	/** 错误 **/
	private String error;
}
