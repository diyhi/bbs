package cms.bean.template;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 布局管理
 *
 */
@Entity
public class Layout implements Serializable{
	private static final long serialVersionUID = -5795714672149544654L;
	
	/** ID;采用uuid生成 **/
	@Id @Column(length=36)
	private String id;	
	/** 布局名称 **/
	@Column(length=100)
	private String name;
	/** 布局类型     1.默认页     3.更多  4.空白页  5.公共页(生成新引用页)  6.公共页(引用版块值)  7.站点栏目详细页 **/
	private Integer type = 4;
	/** 布局文件**/ 
	@Column(length=40)
	private String layoutFile;
	/** 模板目录名称 **/
	@Column(length=40)
	private String dirName;
	
	/** 生成模板引用代码 **/
	@Column(length=100)
	private String referenceCode;
	/** 访问需要登录 **/
	private boolean accessRequireLogin = false;
	
	
	/** 空白页返回数据     0:html   1: json**/
	private Integer returnData = 0;
	/** 版块数据   -1:无数据   1:商品   2: 资讯    ‘更多’功能的返回数据  **/
	private Integer forumData = -1;
	/** 排序  **/
	private Integer sort = 1;
	
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
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getLayoutFile() {
		return layoutFile;
	}
	public void setLayoutFile(String layoutFile) {
		this.layoutFile = layoutFile;
	}
	public String getDirName() {
		return dirName;
	}
	public void setDirName(String dirName) {
		this.dirName = dirName;
	}
	public String getReferenceCode() {
		return referenceCode;
	}
	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}
	public Integer getReturnData() {
		return returnData;
	}
	public void setReturnData(Integer returnData) {
		this.returnData = returnData;
	}
	public Integer getForumData() {
		return forumData;
	}
	public void setForumData(Integer forumData) {
		this.forumData = forumData;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public boolean isAccessRequireLogin() {
		return accessRequireLogin;
	}
	public void setAccessRequireLogin(boolean accessRequireLogin) {
		this.accessRequireLogin = accessRequireLogin;
	}

	
	
}
