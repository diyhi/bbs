package cms.bean.template;

import java.io.Serializable;
import java.util.Date;

/**
 * 资源
 *
 */
public class Resource implements Serializable{
	private static final long serialVersionUID = -2544240534141258378L;
	
	/** Id 由文件路径组成  分割符为左斜杆 **/
	private String id;
	/** 文件名称 **/
	private String name;
	/** 是否叶子节点 **/
	private boolean leaf = true;
	
	/** 最后修改时间 **/
	private Date lastModified;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	


}
