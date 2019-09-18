package cms.bean.membershipCard;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * 会员卡
 *
 */
@Entity
@Table(indexes = {@Index(name="membershipCard_1_idx", columnList="createDate")})
public class MembershipCard implements Serializable{
	private static final long serialVersionUID = -4891301979499383115L;
	
	/** ID **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 名称**/
	@Column(length=190)
	private String name;
	/** 副标题 **/
	@Column(length=190)
	private String subtitle;
	/** 角色Id **/
	@Column(length=32)
	private String userRoleId;
	
	/** 最低销售价 精度为12位，小数点位数为2位**/
	@Column(precision=12, scale=2)
	private BigDecimal lowestPrice;
	/** 最高销售价 精度为12位，小数点位数为2位**/
	@Column(precision=12, scale=2)
	private BigDecimal highestPrice;
	
	/** 最低积分**/
	private Long lowestPoint;
	/** 最高积分**/
	private Long highestPoint;
	
	
	/** 创建时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate = new Date();
	
	/** 简介**/
	@Lob
	private String introduction;
	/** 状态 1:上架  2.下架 3.规格下架(本状态不存储) 11. 上架标记删除  12. 下架标记删除 **/
	private Integer state = 1;
	
	
	/** 排序 **/
	private Integer sort = 0;
	
	/** 规格集合 **/
	@Transient
	private List<Specification> specificationList = new ArrayList<Specification>();
	
	/** 说明标签集合 **/
	@Transient
	private List<String> descriptionTagList = new ArrayList<String>();
	/** 说明标签 JSON格式List<String>**/
	@Lob
	private String descriptionTagFormat;
	
	
	
	public void addSpecification(Specification specification) {
		this.specificationList.add(specification);
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
	public String getSubtitle() {
		return subtitle;
	}
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	public BigDecimal getLowestPrice() {
		return lowestPrice;
	}
	public void setLowestPrice(BigDecimal lowestPrice) {
		this.lowestPrice = lowestPrice;
	}
	public BigDecimal getHighestPrice() {
		return highestPrice;
	}
	public void setHighestPrice(BigDecimal highestPrice) {
		this.highestPrice = highestPrice;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getIntroduction() {
		return introduction;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public List<Specification> getSpecificationList() {
		return specificationList;
	}

	public void setSpecificationList(List<Specification> specificationList) {
		this.specificationList = specificationList;
	}

	public Long getLowestPoint() {
		return lowestPoint;
	}

	public void setLowestPoint(Long lowestPoint) {
		this.lowestPoint = lowestPoint;
	}

	public Long getHighestPoint() {
		return highestPoint;
	}

	public void setHighestPoint(Long highestPoint) {
		this.highestPoint = highestPoint;
	}

	public List<String> getDescriptionTagList() {
		return descriptionTagList;
	}

	public void setDescriptionTagList(List<String> descriptionTagList) {
		this.descriptionTagList = descriptionTagList;
	}

	public String getDescriptionTagFormat() {
		return descriptionTagFormat;
	}

	public void setDescriptionTagFormat(String descriptionTagFormat) {
		this.descriptionTagFormat = descriptionTagFormat;
	}

	public String getUserRoleId() {
		return userRoleId;
	}

	public void setUserRoleId(String userRoleId) {
		this.userRoleId = userRoleId;
	}
	
	
	
	
}
