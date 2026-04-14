package cms.model.membershipCard;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

/**
 * 会员卡赠送项Entity
 *
 */
@Getter
@Setter
@MappedSuperclass
public class MembershipCardGiftItemEntity implements Serializable{
	@Serial
    private static final long serialVersionUID = -5763871320407326485L;
	
	/** Id  格式: 会员卡赠送任务Id- 用户Id **/
	@Id @Column(length=65)
	protected String id;
	/** 任务类型 10:长期  20:一次性 **/
	protected Integer type = 10;
	/** 会员卡赠送任务Id **/
	protected Long membershipCardGiftTaskId;
	
	/** 账号 **/
	@Transient
	protected String account;
	/** 呢称 **/
	@Transient
	protected String nickname;
	/** 头像路径 **/
	@Transient
	protected String avatarPath;
	/** 头像名称 **/
	@Transient
	protected String avatarName;
	
	/** 用户名称 **/
	@Column(length=30)
	protected String userName;
	/** 赠送时间 **/
    @Column(columnDefinition = "DATETIME")
    protected LocalDateTime postTime;
	
	/** 赠送时长 **/
	protected Integer duration;
	/** 赠送时长时长单位 10.小时 20.日 30.月 40.年 **/
	protected Integer unit;
	
	/** 限制条件 restrictionGroupList值的json格式字符 **/
	@Lob
    protected String restriction;
	/** 限制条件组集合 **/
	@Transient
    protected RestrictionGroup restrictionGroup = new RestrictionGroup();
	

}
