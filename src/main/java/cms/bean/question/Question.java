package cms.bean.question;

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
 * 问题
 *
 */
@Entity
@Table(indexes = {@Index(name="question_1_idx", columnList="userName,postTime"),@Index(name="question_4_idx", columnList="status,sort,lastAnswerTime"),@Index(name="question_5_idx", columnList="adoptionAnswerId,status,sort,lastAnswerTime"),@Index(name="question_6_idx", columnList="point,status,sort,lastAnswerTime"),@Index(name="question_7_idx", columnList="amount,status,sort,lastAnswerTime")})
public class Question implements Serializable{
	private static final long serialVersionUID = 8441186002971301170L;
	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 标题 **/
	@Column(length=190)
	private String title;

	/** 标签 **/
	@Transient
	private List<QuestionTagAssociation> questionTagAssociationList = new ArrayList<QuestionTagAssociation>();
	
	/** 问题内容 **/
	@Lob
	private String content;
	/** 是否使用Markdown **/
	private Boolean isMarkdown;
	/** Markdown内容 **/
	@Lob
	private String markdownContent;
	/** 内容摘要 **/
	@Lob
	private String summary;
	/** 追加内容 **/
	@Lob
	private String appendContent = "[";
	
	/** 追加内容集合 **/
	@Transient
	private List<AppendQuestionItem> appendQuestionItemList = new ArrayList<AppendQuestionItem>();
	
	
	
	/** 发表时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date postTime = new Date();
	/** 最后回答时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastAnswerTime;
	/** 最后修改时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdateTime;
	
	/** IP **/
	@Column(length=45)
	private String ip;
	/** IP归属地 **/
	@Transient
	private String ipAddress;
	
	/** 采纳的答案Id **/
	private Long adoptionAnswerId = 0L;
	
	/** 答案总数 **/
	private Long answerTotal = 0L;
	/** 允许回答 **/
	private boolean allow = true;
	
	/** 查看总数 **/
	private Long viewTotal = 0L;
	
	/** 用户名称 **/
	@Column(length=30)
	private String userName;
	/** 账号 **/
	@Transient
	private String account;
	/** 呢称 **/
	@Transient
	private String nickname;
	/** 头像路径 **/
	@Transient
	private String avatarPath;
	/** 头像名称 **/
	@Transient
	private String avatarName;
	/** 用户信息状态 -30.账号已注销(不显示数据) -20.账号已逻辑删除(不显示数据) -10.账号已禁用(不显示数据)  0.正常 10.账号已禁用(显示数据) 20.账号已逻辑删除(显示数据) **/
	@Transient
	private Integer userInfoStatus = 0;
	
	/** 用户角色名称集合 **/
	@Transient
	private List<String> userRoleNameList = new ArrayList<String>();
	
	/** 是否为员工 true:员工  false:会员 **/
	private Boolean isStaff = false;
	/** 排序  **/
	private Integer sort = 0;
	/** 状态 10.待审核 20.已发布 110.待审核删除 120.已发布删除 **/
	private Integer status = 10;
	
	/** 悬赏金额  **/
	@Column(nullable=false,precision=12, scale=2)
	private BigDecimal amount = new BigDecimal("0.00");
	/** 悬赏积分  **/
	private Long point = 0L;

	
	/**
	 * 添加标签
	 * @param questionTagAssociation
	 */
	public void addQuestionTagAssociation(QuestionTagAssociation questionTagAssociation) {
		this.getQuestionTagAssociationList().add(questionTagAssociation);
	}
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getAppendContent() {
		return appendContent;
	}
	public void setAppendContent(String appendContent) {
		this.appendContent = appendContent;
	}
	public Date getPostTime() {
		return postTime;
	}
	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}
	public Date getLastAnswerTime() {
		return lastAnswerTime;
	}
	public void setLastAnswerTime(Date lastAnswerTime) {
		this.lastAnswerTime = lastAnswerTime;
	}
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public Long getAnswerTotal() {
		return answerTotal;
	}
	public void setAnswerTotal(Long answerTotal) {
		this.answerTotal = answerTotal;
	}
	public boolean isAllow() {
		return allow;
	}
	public void setAllow(boolean allow) {
		this.allow = allow;
	}
	public Long getViewTotal() {
		return viewTotal;
	}
	public void setViewTotal(Long viewTotal) {
		this.viewTotal = viewTotal;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getAvatarPath() {
		return avatarPath;
	}
	public void setAvatarPath(String avatarPath) {
		this.avatarPath = avatarPath;
	}
	public String getAvatarName() {
		return avatarName;
	}
	public void setAvatarName(String avatarName) {
		this.avatarName = avatarName;
	}
	public List<String> getUserRoleNameList() {
		return userRoleNameList;
	}
	public void setUserRoleNameList(List<String> userRoleNameList) {
		this.userRoleNameList = userRoleNameList;
	}

	public Boolean getIsStaff() {
		return isStaff;
	}
	public void setIsStaff(Boolean isStaff) {
		this.isStaff = isStaff;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public List<QuestionTagAssociation> getQuestionTagAssociationList() {
		return questionTagAssociationList;
	}
	public void setQuestionTagAssociationList(List<QuestionTagAssociation> questionTagAssociationList) {
		this.questionTagAssociationList = questionTagAssociationList;
	}


	public Long getAdoptionAnswerId() {
		return adoptionAnswerId;
	}


	public void setAdoptionAnswerId(Long adoptionAnswerId) {
		this.adoptionAnswerId = adoptionAnswerId;
	}


	public List<AppendQuestionItem> getAppendQuestionItemList() {
		return appendQuestionItemList;
	}


	public void setAppendQuestionItemList(List<AppendQuestionItem> appendQuestionItemList) {
		this.appendQuestionItemList = appendQuestionItemList;
	}


	public BigDecimal getAmount() {
		return amount;
	}


	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}


	public Long getPoint() {
		return point;
	}


	public void setPoint(Long point) {
		this.point = point;
	}


	public String getAccount() {
		return account;
	}


	public void setAccount(String account) {
		this.account = account;
	}


	public Integer getUserInfoStatus() {
		return userInfoStatus;
	}


	public void setUserInfoStatus(Integer userInfoStatus) {
		this.userInfoStatus = userInfoStatus;
	}


	public Boolean getIsMarkdown() {
		return isMarkdown;
	}


	public void setIsMarkdown(Boolean isMarkdown) {
		this.isMarkdown = isMarkdown;
	}


	public String getMarkdownContent() {
		return markdownContent;
	}


	public void setMarkdownContent(String markdownContent) {
		this.markdownContent = markdownContent;
	}
	
	
}
