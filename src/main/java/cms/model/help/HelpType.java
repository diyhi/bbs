package cms.model.help;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
 * 帮助分类
 *
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name="helpType_1_idx", columnList="parentId"), @Index(name="helpType_2_idx",columnList="parentIdGroup")})
public class HelpType implements Serializable{
	@Serial
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

}
