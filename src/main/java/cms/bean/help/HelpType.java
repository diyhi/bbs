package cms.bean.help;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * 帮助分类
 *
 */
@Entity
@Table(indexes = {@Index(name="helpType_1_idx", columnList="parentId"), @Index(name="helpType_2_idx",columnList="parentIdGroup")})
public class HelpType implements Serializable{
	private static final long serialVersionUID = 2998010027677137673L;

	//GeneratedValue主键生成方式
	/** 类别ID **/
	@Id
	private Long id;

	/** 类别名称 **/
	@Column(length=200)
	private String name;
	/** 所属父类ID **/
	private Long parentId = 0L;
	/** 排序 **/
	private Integer sort = 0;
	/** 子节点数量 **/
	private Integer childNodeNumber = 0;
	
	/** 父Id组 **/
	@Column(length=200)
	private String parentIdGroup = ",0,";

	/** 帮助总数量(显示+回收站) **/
	private Long totalHelp = 0L;
	/** 帮助数量(显示) **/
	private Long helpQuantity = 0L;
	/** 已合并分类Id **/
	@Column(length=1000)
	private String mergerTypeId = ",";
	
	/** 图片 **/
	@Column(length=100)
	private String image;
	/** 描述 **/
	@Lob
	private String description;
	
	/** 子节点帮助分类 **/
	@Transient
	private List<HelpType> childHelpType = new ArrayList<HelpType>();
	
	
	public HelpType(){}
	public HelpType(String name) {
		this.name = name;
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
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public Integer getChildNodeNumber() {
		return childNodeNumber;
	}
	public void setChildNodeNumber(Integer childNodeNumber) {
		this.childNodeNumber = childNodeNumber;
	}
	public String getParentIdGroup() {
		return parentIdGroup;
	}
	public void setParentIdGroup(String parentIdGroup) {
		this.parentIdGroup = parentIdGroup;
	}
	public Long getTotalHelp() {
		return totalHelp;
	}
	public void setTotalHelp(Long totalHelp) {
		this.totalHelp = totalHelp;
	}
	public Long getHelpQuantity() {
		return helpQuantity;
	}
	public void setHelpQuantity(Long helpQuantity) {
		this.helpQuantity = helpQuantity;
	}
	public String getMergerTypeId() {
		return mergerTypeId;
	}
	public void setMergerTypeId(String mergerTypeId) {
		this.mergerTypeId = mergerTypeId;
	}
	public List<HelpType> getChildHelpType() {
		return childHelpType;
	}
	public void setChildHelpType(List<HelpType> childHelpType) {
		this.childHelpType = childHelpType;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	

	
}
