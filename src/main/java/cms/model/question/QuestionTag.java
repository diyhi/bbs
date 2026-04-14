package cms.model.question;

import lombok.Getter;
import lombok.Setter;

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



/**
 * 问题标签
 *
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name="questionTag_1_idx", columnList="sort")})
public class QuestionTag implements Serializable{
	@Serial
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


}

