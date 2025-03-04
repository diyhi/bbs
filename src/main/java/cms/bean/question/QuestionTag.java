package cms.bean.question;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
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
	/** 多语言扩展  存储JSON格式的multiLanguageExtensionMap属性值    key:字段-语言（例如：name-en_US） value:内容 **/
	@Lob
	private String multiLanguageExtension;
	/** 多语言扩展  key:字段-语言（例如：name-en_US） value:内容**/
	@Transient
	private Map<String,String> multiLanguageExtensionMap = new HashMap<String,String>();
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
	/** 图片 **/
	@Column(length=100)
	private String image;
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getMultiLanguageExtension() {
		return multiLanguageExtension;
	}

	public void setMultiLanguageExtension(String multiLanguageExtension) {
		this.multiLanguageExtension = multiLanguageExtension;
	}

	public Map<String, String> getMultiLanguageExtensionMap() {
		return multiLanguageExtensionMap;
	}

	public void setMultiLanguageExtensionMap(Map<String, String> multiLanguageExtensionMap) {
		this.multiLanguageExtensionMap = multiLanguageExtensionMap;
	}
	
	
}

