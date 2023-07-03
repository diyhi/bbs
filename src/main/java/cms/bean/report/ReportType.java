package cms.bean.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * 举报分类
 *
 */
@Entity
@Table(indexes = {@Index(name="reportType_1_idx", columnList="sort")})
public class ReportType implements Serializable{
	private static final long serialVersionUID = -6736014509164078586L;

	/** ID **/
	@Id @Column(length=36)
	private String id;
	
	/** 分类名称 **/
	@Column(length=190)
	private String name;
	
	/** 所属父类Id 根节点为0 **/
	@Column(length=36)
	private String parentId;
	/** 子分类**/
	@Transient
	private List<ReportType> childType = new ArrayList<ReportType>();
	
	/** 排序 **/
	private Integer sort = 0;
	
	/** 是否需要说明理由 **/
	private Boolean giveReason = false;
	
	
	/** 父Id组  顺序存放**/
	@Column(length=190)
	private String parentIdGroup = ",0,";
	
	/** 子节点数量 **/
	private Integer childNodeNumber = 0;
	
	
	/** 添加子分类  **/
	public void addChildType(ReportType reportType){
		this.getChildType().add(reportType);
	}
	
	
	
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

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public List<ReportType> getChildType() {
		return childType;
	}

	public void setChildType(List<ReportType> childType) {
		this.childType = childType;
	}

	public Boolean getGiveReason() {
		return giveReason;
	}

	public void setGiveReason(Boolean giveReason) {
		this.giveReason = giveReason;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}



	public String getParentIdGroup() {
		return parentIdGroup;
	}



	public void setParentIdGroup(String parentIdGroup) {
		this.parentIdGroup = parentIdGroup;
	}



	public Integer getChildNodeNumber() {
		return childNodeNumber;
	}



	public void setChildNodeNumber(Integer childNodeNumber) {
		this.childNodeNumber = childNodeNumber;
	}
	
	
}
