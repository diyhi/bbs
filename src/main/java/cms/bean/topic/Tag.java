package cms.bean.topic;

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
 * 标签
 *
 */
@Entity
@Table(indexes = {@Index(name="tag_1_idx", columnList="sort")})
public class Tag implements Serializable{
	private static final long serialVersionUID = -7672182879689718269L;

	/** id **/
	@Id
	private Long id;

	/** 标签名称 **/
	@Column(length=190)
	private String name;
	/** 排序 **/
	private Integer sort = 0;
	
	/** 标签下的话题允许查看的角色名称集合(默认角色除外) **/
	@Transient
	private List<String> allowRoleViewList = new ArrayList<String>();
	
	
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
	public List<String> getAllowRoleViewList() {
		return allowRoleViewList;
	}
	public void setAllowRoleViewList(List<String> allowRoleViewList) {
		this.allowRoleViewList = allowRoleViewList;
	}

	
	
	
	
	
}
