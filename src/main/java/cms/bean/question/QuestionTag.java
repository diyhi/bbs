package cms.bean.question;

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
 * 问题标签
 *
 */
@Entity
@Table(indexes = {@Index(name="questionTag_1_idx", columnList="sort")})
public class QuestionTag implements Serializable{
	private static final long serialVersionUID = -811884294168718523L;

	/** Id **/
	@Id
	private Long id;

	/** 标签名称 **/
	@Column(length=190)
	private String name;
	/** 排序 **/
	private Integer sort = 0;
	/** 所属父类ID **/
	private Long parentId = 0L;
	/** 父Id组  顺序存放**/
	@Column(length=190)
	private String parentIdGroup = ",0,";
	
	/** 子节点数量 **/
	private Integer childNodeNumber = 0;
	
	/** 子标签 **/
	@Transient
	private List<QuestionTag> childTag = new ArrayList<QuestionTag>();
	
	/** 添加子标签 **/
	public void addChildTag(QuestionTag questionTag){
		this.getChildTag().add(questionTag);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
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

	public List<QuestionTag> getChildTag() {
		return childTag;
	}

	public void setChildTag(List<QuestionTag> childTag) {
		this.childTag = childTag;
	}
	
	
}

