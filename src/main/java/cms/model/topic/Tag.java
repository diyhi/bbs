package cms.model.topic;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;


/**
 * 标签
 *
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name="tag_1_idx", columnList="sort")})
public class Tag implements Serializable{
	@Serial
    private static final long serialVersionUID = -7672182879689718269L;

	/** id **/
	@Id
	private Long id;

	/** 标签名称 **/
	@Column(length=190)
	private String name;
	
	
	/** 所属父标签Id **/
	private Long parentId = 0L;
	/** 父Id组  顺序存放**/
	@Column(length=190)
	private String parentIdGroup = ",0,";
	/** 子节点数量 **/
	private Integer childNodeNumber = 0;
	
	/** 多语言扩展  存储JSON格式的multiLanguageExtensionMap属性值    key:字段-语言（例如：name-en_US） value:内容 **/
	@Lob
	private String multiLanguageExtension;
	/** 多语言扩展  key:字段-语言（例如：name-en_US） value:内容**/
	@Transient
	private Map<String,String> multiLanguageExtensionMap = new HashMap<String,String>();
	
	/** 排序 **/
	private Integer sort = 0;
	/** 图片 **/
	@Column(length=100)
	private String image;
	/** 标签下的话题允许查看的角色名称集合(默认角色除外) **/
	@Transient
	private List<String> allowRoleViewList = new ArrayList<String>();
	
	
	/** 子标签 **/
	@Transient
	private List<Tag> childTag = new ArrayList<Tag>();
	
	/** 添加子标签 **/
	public void addChildTag(Tag tag){
		this.getChildTag().add(tag);
	}

}
