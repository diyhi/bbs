package cms.model.user;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

/**
 * 角色
 *
 */
@Getter
@Setter
@Entity
public class UserRole implements Serializable{
	@Serial
    private static final long serialVersionUID = -2873847246959858813L;
	
	
	@Id @Column(length=32)
	private String id;
	/** 名称 **/
	@Column(length=50)
	private String name;
	
	/** 多语言扩展  存储JSON格式的multiLanguageExtensionMap属性值    key:字段-语言（例如：name-en_US） value:内容 **/
	@Lob
	private String multiLanguageExtension;
	/** 多语言扩展  key:字段-语言（例如：name-en_US） value:内容**/
	@Transient
	private Map<String,String> multiLanguageExtensionMap = new HashMap<String,String>();
	
	/** 备注 **/
	@Lob
	private String remark;
	/** 排序 **/
	private Integer sort = 0;
	/** 是否默认角色 **/
	private Boolean defaultRole = false;
	
	/** 资源 List<UserResourceGroup>的JSON格式值**/
	@Lob
	private String userResourceFormat;
	

	/** 是否选中 **/
	@Transient
	private boolean selected =  false;
	/** 有效期结束**/
	@Transient
	private LocalDateTime validPeriodEnd;

}
