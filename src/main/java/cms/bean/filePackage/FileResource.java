package cms.bean.filePackage;

import java.io.Serializable;

/**
 * 文件资源
 *
 */
public class FileResource implements Serializable{
	private static final long serialVersionUID = 6371746813102840407L;
	
	/** Id 由文件路径组成  分割符为左斜杆 **/
	private String id;
	/** 父Id 由文件路径组成  分割符为左斜杆 **/
	private String parentId;
	/** 文件名称 **/
	private String name;
	/** 是否叶子节点 **/
	private boolean leaf = true;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isLeaf() {
		return leaf;
	}
	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}
}
