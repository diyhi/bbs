package cms.dto.filePackage;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件资源
 *
 */
@Getter
@Setter
public class FileResource implements Serializable{
	@Serial
    private static final long serialVersionUID = 6371746813102840407L;
	
	/** Id 由文件路径组成  分割符为左斜杆 **/
	private String id;
	/** 父Id 由文件路径组成  分割符为左斜杆 **/
	private String parentId;
	/** 文件名称 **/
	private String name;
	/** 是否叶子节点 **/
	private boolean leaf = true;

}
