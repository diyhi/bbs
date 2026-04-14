package cms.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * Zip 打包
 *
 */
@Getter
@Setter
public class ZipPack implements Serializable{
	@Serial
    private static final long serialVersionUID = -4816411879712943046L;

	/** 待压缩的目录或文件 **/
	private String source;
	
	/** 压缩内文件逻辑路径 **/
	private String entryPath;
	/** 是否为目录 **/
	private boolean isDirectory;
}
