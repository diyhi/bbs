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
 * 问答悬赏平台分成
 *
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name="questionRewardPlatformShare_1_idx", columnList="adoptionTime"),@Index(name="questionRewardPlatformShare_2_idx", columnList="questionId,answerUserName")})
public class QuestionRewardPlatformShare implements Serializable{
	@Serial
    private static final long serialVersionUID = 2537184637626480652L;
	
	/** ID **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 问题Id **/
	private Long questionId;
	/** 问题标题 **/
	@Transient
	private String questionTitle;
	
	/** 分成用户是否为员工 **/
	private boolean staff;
	
	/** 提问题的用户名称 **/
	@Column(length=80)
	private String postUserName;
	/** 提问题的用户账号 **/
	@Transient
	private String postAccount;
	/** 提问题的用户呢称 **/
	@Transient
	private String postNickname;
	/** 提问题的用户头像路径 **/
	@Transient
	private String postAvatarPath;
	/** 提问题的用户头像名称 **/
	@Transient
	private String postAvatarName;

	/** 回答问题的用户名称 **/ 
	@Column(length=30)
	private String answerUserName;
	/** 回答问题的用户账号 **/
	@Transient
	private String answerAccount;
	/** 回答问题的用户呢称 **/
	@Transient
	private String answerNickname;
	/** 回答问题的用户头像路径 **/
	@Transient
	private String answerAvatarPath;
	/** 回答问题的用户头像名称 **/
	@Transient
	private String answerAvatarName;
	
	/** 平台分成比例 **/
	private Integer platformShareProportion;
	
	/** 回答问题的用户分成流水号 **/
	@Column(length=32)
	private String answerUserShareRunningNumber;
	
	/** 总金额  精度为12位，小数点位数为2位 **/
	@Column(nullable=false,precision=12, scale=2)
	private BigDecimal totalAmount = new BigDecimal("0");
	
	/** 平台分成金额  精度为14位，小数点位数为4位 如果用户为员工，则分成全部归平台 **/
	@Column(nullable=false,precision=14, scale=4)
	private BigDecimal shareAmount = new BigDecimal("0");

	
	/** 采纳时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime adoptionTime = LocalDateTime.now();



}
