package cms.bean.platformShare;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * 问答悬赏平台分成
 *
 */
@Entity
@Table(indexes = {@Index(name="questionRewardPlatformShare_1_idx", columnList="adoptionTime"),@Index(name="questionRewardPlatformShare_2_idx", columnList="questionId,answerUserName")})
public class QuestionRewardPlatformShare implements Serializable{
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
	@Temporal(TemporalType.TIMESTAMP)
	private Date adoptionTime = new Date();


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Long getQuestionId() {
		return questionId;
	}


	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}


	public String getQuestionTitle() {
		return questionTitle;
	}


	public void setQuestionTitle(String questionTitle) {
		this.questionTitle = questionTitle;
	}


	public boolean isStaff() {
		return staff;
	}


	public void setStaff(boolean staff) {
		this.staff = staff;
	}


	public String getPostUserName() {
		return postUserName;
	}


	public void setPostUserName(String postUserName) {
		this.postUserName = postUserName;
	}


	public String getAnswerUserName() {
		return answerUserName;
	}


	public void setAnswerUserName(String answerUserName) {
		this.answerUserName = answerUserName;
	}


	public Integer getPlatformShareProportion() {
		return platformShareProportion;
	}


	public void setPlatformShareProportion(Integer platformShareProportion) {
		this.platformShareProportion = platformShareProportion;
	}


	public String getAnswerUserShareRunningNumber() {
		return answerUserShareRunningNumber;
	}


	public void setAnswerUserShareRunningNumber(String answerUserShareRunningNumber) {
		this.answerUserShareRunningNumber = answerUserShareRunningNumber;
	}


	public BigDecimal getTotalAmount() {
		return totalAmount;
	}


	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}


	public BigDecimal getShareAmount() {
		return shareAmount;
	}


	public void setShareAmount(BigDecimal shareAmount) {
		this.shareAmount = shareAmount;
	}


	public Date getAdoptionTime() {
		return adoptionTime;
	}


	public void setAdoptionTime(Date adoptionTime) {
		this.adoptionTime = adoptionTime;
	}


	public String getPostNickname() {
		return postNickname;
	}


	public void setPostNickname(String postNickname) {
		this.postNickname = postNickname;
	}


	public String getPostAvatarPath() {
		return postAvatarPath;
	}


	public void setPostAvatarPath(String postAvatarPath) {
		this.postAvatarPath = postAvatarPath;
	}


	public String getPostAvatarName() {
		return postAvatarName;
	}


	public void setPostAvatarName(String postAvatarName) {
		this.postAvatarName = postAvatarName;
	}


	public String getAnswerNickname() {
		return answerNickname;
	}


	public void setAnswerNickname(String answerNickname) {
		this.answerNickname = answerNickname;
	}


	public String getAnswerAvatarPath() {
		return answerAvatarPath;
	}


	public void setAnswerAvatarPath(String answerAvatarPath) {
		this.answerAvatarPath = answerAvatarPath;
	}


	public String getAnswerAvatarName() {
		return answerAvatarName;
	}


	public void setAnswerAvatarName(String answerAvatarName) {
		this.answerAvatarName = answerAvatarName;
	}


	public String getPostAccount() {
		return postAccount;
	}


	public void setPostAccount(String postAccount) {
		this.postAccount = postAccount;
	}


	public String getAnswerAccount() {
		return answerAccount;
	}


	public void setAnswerAccount(String answerAccount) {
		this.answerAccount = answerAccount;
	}


	
	
	
}
