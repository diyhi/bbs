package cms.model.membershipCard;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * 会员卡赠送任务
 * @author Gao
 * 在赠送任务有效期范围内的活跃用户才能获取赠送
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name="membershipCardGiftTask_1_idx", columnList="expirationDate_start,expirationDate_end,type,enable")})
public class MembershipCardGiftTask implements Serializable{
	@Serial
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
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createDate = LocalDateTime.now();
	
	/** 限制条件 restrictionGroupList值的json格式字符 **/
	@Lob
	private String restriction;
	/** 限制条件组集合 **/
	@Transient
	private RestrictionGroup restrictionGroup = new RestrictionGroup();
	
	
	
	/** 任务有效期范围起始(本参数只在'长期任务'中有效)  默认：1970-1-1 00:00:00 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime expirationDate_start;
	/** 任务有效期范围结束(本参数只在'长期任务'中有效)  默认：2999-1-1 00:00:00 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime expirationDate_end;
	
	/** 是否启用(本参数只在'长期任务'中有效)  true:启用 false:禁用 **/
	@Column(name="\"enable\"")
	private boolean enable = true;
	/** 赠送时长 **/
	private Integer duration;
	/** 赠送时长单位 10.小时 20.日 30.月 40.年 **/
	private Integer unit;
	/** 版本号 **/
	private Integer version = 0;
	

}
