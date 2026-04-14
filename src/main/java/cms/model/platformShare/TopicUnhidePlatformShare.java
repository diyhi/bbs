package cms.model.platformShare;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

/**
 * 解锁话题隐藏内容平台分成
 *
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name="topicUnhidePlatformShare_1_idx", columnList="unlockTime")})
public class TopicUnhidePlatformShare implements Serializable{
	@Serial
    private static final long serialVersionUID = -2776115664125447453L;

	/** ID **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 话题Id **/
	private Long topicId;
	/** 话题标题 **/
	@Transient
	private String topicTitle;
	
	/** 分成用户是否为员工 **/
	private boolean staff;
	
	/** 发布话题的用户名称 **/
	@Column(length=80)
	private String postUserName;
	/** 发布话题的用户账号 **/
	@Transient
	private String postAccount;
	/** 发布话题的用户呢称 **/
	@Transient
	private String postNickname;
	/** 发布话题的用户头像路径 **/
	@Transient
	private String postAvatarPath;
	/** 发布话题的用户头像名称 **/
	@Transient
	private String postAvatarName;
	
	
	
	/** 解锁话题的用户名称 **/ 
	@Column(length=30)
	private String unlockUserName;
	/** 解锁话题的用户账号 **/ 
	@Transient
	private String unlockAccount;
	/** 解锁话题的用户呢称 **/
	@Transient
	private String unlockNickname;
	/** 解锁话题的用户头像路径 **/
	@Transient
	private String unlockAvatarPath;
	/** 解锁话题的用户头像名称 **/
	@Transient
	private String unlockAvatarName;
	
	/** 平台分成比例 **/
	private Integer platformShareProportion;
	
	/** 发布话题的用户分成流水号 **/
	@Column(length=32)
	private String postUserShareRunningNumber;
	
	/** 总金额  精度为12位，小数点位数为2位 **/
	@Column(nullable=false,precision=12, scale=2)
	private BigDecimal totalAmount = new BigDecimal("0");
	
	/** 平台分成金额  精度为14位，小数点位数为4位 如果用户为员工，则分成全部归平台 **/
	@Column(nullable=false,precision=14, scale=4)
	private BigDecimal shareAmount = new BigDecimal("0");

	
	/** 解锁时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime unlockTime = LocalDateTime.now();

}
