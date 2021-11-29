package cms.bean.membershipCard;

import java.io.Serializable;
import java.util.Date;

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
 * 会员卡赠送任务
 * @author Gao
 * 在赠送任务有效期范围内的活跃用户才能获取赠送
 */
@Entity
@Table(indexes = {@Index(name="membershipCardGiftTask_1_idx", columnList="expirationDate_start,expirationDate_end,type,enable")})
public class MembershipCardGiftTask implements Serializable{
	private static final long serialVersionUID = -4967838483890649996L;
	/** ID **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 名称**/
	@Column(length=190)
	private String name;
	/** 角色Id **/
	@Column(length=32)
	private String userRoleId;
	/** 用户角色名称 **/
	@Column(length=192)
	private String userRoleName;
	/** 任务类型 10:长期  20:一次性 **/
	private Integer type = 10;
	/** 任务创建时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate = new Date();
	
	/** 限制条件 restrictionGroupList值的json格式字符 **/
	@Lob
	private String restriction;
	/** 限制条件组集合 **/
	@Transient
	private RestrictionGroup restrictionGroup = new RestrictionGroup();
	
	
	
	/** 任务有效期范围起始(本参数只在'长期任务'中有效)  默认：1970-1-1 00:00:00 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date expirationDate_start;
	/** 任务有效期范围结束(本参数只在'长期任务'中有效)  默认：2999-1-1 00:00:00 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date expirationDate_end;
	
	/** 是否启用(本参数只在'长期任务'中有效)  true:启用 false:禁用 **/
	@Column(name="\"enable\"")
	private boolean enable = true;
	/** 赠送时长 **/
	private Integer duration;
	/** 赠送时长时长单位 10.小时 20.日 30.月 40.年 **/
	private Integer unit;
	/** 版本号 **/
	private Integer version = 0;
	
	
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
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getExpirationDate_start() {
		return expirationDate_start;
	}
	public void setExpirationDate_start(Date expirationDate_start) {
		this.expirationDate_start = expirationDate_start;
	}
	public Date getExpirationDate_end() {
		return expirationDate_end;
	}
	public void setExpirationDate_end(Date expirationDate_end) {
		this.expirationDate_end = expirationDate_end;
	}
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	public Integer getUnit() {
		return unit;
	}
	public void setUnit(Integer unit) {
		this.unit = unit;
	}
	public String getRestriction() {
		return restriction;
	}
	public void setRestriction(String restriction) {
		this.restriction = restriction;
	}
	public RestrictionGroup getRestrictionGroup() {
		return restrictionGroup;
	}
	public void setRestrictionGroup(RestrictionGroup restrictionGroup) {
		this.restrictionGroup = restrictionGroup;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public String getUserRoleId() {
		return userRoleId;
	}
	public void setUserRoleId(String userRoleId) {
		this.userRoleId = userRoleId;
	}
	public String getUserRoleName() {
		return userRoleName;
	}
	public void setUserRoleName(String userRoleName) {
		this.userRoleName = userRoleName;
	}
	
}
