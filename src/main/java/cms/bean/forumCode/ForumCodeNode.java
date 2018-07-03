package cms.bean.forumCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 版块代码节点
 *
 */
public class ForumCodeNode implements Serializable{
	private static final long serialVersionUID = -1927961998029117334L;
	
	/** 节点Id **/
	private Integer nodeId;
	/** 节点名称 **/
	private String nodeName;
	
	/** 子节点 **/
	private List<ForumCodeNode> childNode = new ArrayList<ForumCodeNode>();
	
	/** 版块代码文件名称前缀 **/
	private String prefix;
	
	/** 版块代码文件 备注 **/
	private String remark;
	
	/** 电脑版最后修改时间 **/
	private Date pc_lastTime;
	/** 移动版最后修改时间 **/
	private Date wap_lastTime;
	
	/** 模板显示类型 （子节点使用）  单层/多层/分页/实体对象/集合 **/
	private List<String> displayType = new ArrayList<String>();
	
	
	public ForumCodeNode(){};
	public ForumCodeNode(Integer nodeId,String nodeName, List<ForumCodeNode> childNode) {
		this.nodeId = nodeId;
		this.nodeName = nodeName;
		this.childNode = childNode;
	}
	public ForumCodeNode(Integer nodeId,String nodeName, String prefix,
			List<String> displayType) {
		this.nodeId = nodeId;
		this.nodeName = nodeName;
		this.prefix = prefix;
		this.displayType = displayType;
	}
	
	
	//添加子节点
	public void addChildNode(ForumCodeNode forumCodeNode){
		this.childNode.add(forumCodeNode);
	}
	
	
	public Integer getNodeId() {
		return nodeId;
	}
	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public List<String> getDisplayType() {
		return displayType;
	}
	public void setDisplayType(List<String> displayType) {
		this.displayType = displayType;
	}
	public List<ForumCodeNode> getChildNode() {
		return childNode;
	}
	public void setChildNode(List<ForumCodeNode> childNode) {
		this.childNode = childNode;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public Date getPc_lastTime() {
		return pc_lastTime;
	}
	public void setPc_lastTime(Date pc_lastTime) {
		this.pc_lastTime = pc_lastTime;
	}
	public Date getWap_lastTime() {
		return wap_lastTime;
	}
	public void setWap_lastTime(Date wap_lastTime) {
		this.wap_lastTime = wap_lastTime;
	}
	
	
}
