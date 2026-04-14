package cms.model.staff;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * 资源表
 * @author Administrator
 *
 */
@Entity
@Getter
@Setter
public class SysResources implements Serializable {
	@Serial
    private static final long serialVersionUID = 9031856537579017249L;
	
	@Id @Column(length=32)
	private String id;
	/** 名称 **/
	private String name;
	/** 备注 **/
	private String remarks;
	/** URL **/
	private String url;
	/** 附加URL类型 1.GET 2.POST **/
	private Integer urlType;
	/** 附加URL所属父ID **/
	private String urlParentId;
	/** 优先级 **/ 
	private Integer priority;
	
	
	/** 模块 **/
	private String module;
}
