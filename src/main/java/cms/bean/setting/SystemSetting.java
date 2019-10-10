package cms.bean.setting;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;

/**
 * 系统设置
 *
 */
@Entity
public class SystemSetting implements Serializable{
	private static final long serialVersionUID = -4895623453512343431L;

	/** Id **/
	@Id
	private Integer id;
	
	/** 站点名称 **/
	private String title = "";
	/** 站点关键词(keywords) **/
	private String keywords = "";
	/** 站点描述(description) **/
	private String description= "";
	
	/** 关闭站点 1.打开 3.全站关闭**/
	private Integer closeSite = 1;
	/** 关闭站点提示信息 **/
	@Lob
	private String closeSitePrompt = "";
	
	/** 支持访问设备 1.自动识别终端 2.电脑端 3.移动端 **/
	private Integer supportAccessDevice = 1;
	
	/** 是否允许注册 **/
	private boolean allowRegister = true;
	
	/** 注册是否需要验证码 **/
	private boolean registerCaptcha = true;

	
	/** 登录密码连续每分钟错误N次出现验证码 0为每次都要 **/
	private Integer login_submitQuantity = 0;
	/** 发表话题每分钟提交超过N次出现验证码 **/
	private Integer topic_submitQuantity = 5;
	/** 发表评论每分钟提交超过N次出现验证码 **/
	private Integer comment_submitQuantity = 5;
	/** 发表私信每分钟提交超过N次出现验证码 **/
	private Integer privateMessage_submitQuantity = 5;
	
	/** 发表话题奖励积分 **/
	private Long topic_rewardPoint = 0L;
	/** 发表评论奖励积分 **/
	private Long comment_rewardPoint = 0L;
	/** 发表回复奖励积分 **/
	private Long reply_rewardPoint = 0L;
	
	/** 前台发表话题审核   10.全部审核 20.特权会员未触发敏感词免审核 30.特权会员免审核 40.触发敏感词需审核 50.无需审核 **/
	private Integer topic_review = 10;
	/** 前台发表评论审核   10.全部审核 20.特权会员未触发敏感词免审核 30.特权会员免审核 40.触发敏感词需审核 50.无需审核 **/
	private Integer comment_review = 10;
	/** 前台发表回复审核   10.全部审核 20.特权会员未触发敏感词免审核 30.特权会员免审核 40.触发敏感词需审核 50.无需审核 **/
	private Integer reply_review = 10;
	
	/** 允许提交在线留言 **/
	private boolean allowFeedback = false;
	
	/** 允许提交话题 **/
	private boolean allowTopic = false;
	/** 允许提交评论 **/
	private boolean allowComment = false;
	
	/** 实名用户才允许提交话题 **/
	private boolean realNameUserAllowTopic = false;
	/** 实名用户才允许提交评论 **/
	private boolean realNameUserAllowComment = false;

	
	/** 解锁话题隐藏内容平台分成比例 默认0% **/
	private Integer topicUnhidePlatformShareProportion = 0;
	
	
	/** 文件防盗链密钥 **/
	@Column(length=190)
	private String fileSecureLinkSecret;
	/** 文件防盗链过期时间 单位:秒 **/
	private Long fileSecureLinkExpire = 300L;
	
	/** 上传临时文件有效期 单位:分钟 **/
	private Integer temporaryFileValidPeriod = 60;
	
	/** 过滤敏感词 **/
	private boolean allowFilterWord = false;
	/** 敏感词替换 **/
	private String filterWordReplace;
	
	/** 前台分页数量 **/
	private Integer forestagePageNumber = 20;
	
	/** 后台分页数量 **/
	private Integer backstagePageNumber = 20;
	

	/** 每用户每24小时内发送短信次数 **/
	private Integer userSentSmsCount = 10;
	/** 话题编辑器标签  json **/
	@Lob
	private String topicEditorTag;
	@Transient
	private EditorTag topicEditorTagObject = new EditorTag();
	
	/** 评论编辑器标签  json **/
	@Lob
	private String editorTag;
	@Transient
	private EditorTag editorTagObject = new EditorTag();
	
	/** 版本 **/
	private Long version = 0L;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getCloseSite() {
		return closeSite;
	}

	public void setCloseSite(Integer closeSite) {
		this.closeSite = closeSite;
	}

	public String getCloseSitePrompt() {
		return closeSitePrompt;
	}

	public void setCloseSitePrompt(String closeSitePrompt) {
		this.closeSitePrompt = closeSitePrompt;
	}

	public Integer getSupportAccessDevice() {
		return supportAccessDevice;
	}

	public void setSupportAccessDevice(Integer supportAccessDevice) {
		this.supportAccessDevice = supportAccessDevice;
	}

	public boolean isAllowRegister() {
		return allowRegister;
	}

	public void setAllowRegister(boolean allowRegister) {
		this.allowRegister = allowRegister;
	}

	public boolean isRegisterCaptcha() {
		return registerCaptcha;
	}

	public void setRegisterCaptcha(boolean registerCaptcha) {
		this.registerCaptcha = registerCaptcha;
	}

	public Integer getLogin_submitQuantity() {
		return login_submitQuantity;
	}

	public void setLogin_submitQuantity(Integer login_submitQuantity) {
		this.login_submitQuantity = login_submitQuantity;
	}

	public Integer getTemporaryFileValidPeriod() {
		return temporaryFileValidPeriod;
	}

	public void setTemporaryFileValidPeriod(Integer temporaryFileValidPeriod) {
		this.temporaryFileValidPeriod = temporaryFileValidPeriod;
	}

	public Integer getForestagePageNumber() {
		return forestagePageNumber;
	}

	public void setForestagePageNumber(Integer forestagePageNumber) {
		this.forestagePageNumber = forestagePageNumber;
	}

	public Integer getBackstagePageNumber() {
		return backstagePageNumber;
	}

	public void setBackstagePageNumber(Integer backstagePageNumber) {
		this.backstagePageNumber = backstagePageNumber;
	}

	public Integer getUserSentSmsCount() {
		return userSentSmsCount;
	}

	public void setUserSentSmsCount(Integer userSentSmsCount) {
		this.userSentSmsCount = userSentSmsCount;
	}

	public String getEditorTag() {
		return editorTag;
	}

	public void setEditorTag(String editorTag) {
		this.editorTag = editorTag;
	}

	public EditorTag getEditorTagObject() {
		return editorTagObject;
	}

	public void setEditorTagObject(EditorTag editorTagObject) {
		this.editorTagObject = editorTagObject;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Integer getTopic_submitQuantity() {
		return topic_submitQuantity;
	}

	public void setTopic_submitQuantity(Integer topic_submitQuantity) {
		this.topic_submitQuantity = topic_submitQuantity;
	}

	public Integer getComment_submitQuantity() {
		return comment_submitQuantity;
	}

	public void setComment_submitQuantity(Integer comment_submitQuantity) {
		this.comment_submitQuantity = comment_submitQuantity;
	}

	public boolean isRealNameUserAllowTopic() {
		return realNameUserAllowTopic;
	}

	public void setRealNameUserAllowTopic(boolean realNameUserAllowTopic) {
		this.realNameUserAllowTopic = realNameUserAllowTopic;
	}

	public boolean isRealNameUserAllowComment() {
		return realNameUserAllowComment;
	}

	public void setRealNameUserAllowComment(boolean realNameUserAllowComment) {
		this.realNameUserAllowComment = realNameUserAllowComment;
	}

	public Long getTopic_rewardPoint() {
		return topic_rewardPoint;
	}

	public void setTopic_rewardPoint(Long topic_rewardPoint) {
		this.topic_rewardPoint = topic_rewardPoint;
	}

	public Long getComment_rewardPoint() {
		return comment_rewardPoint;
	}

	public void setComment_rewardPoint(Long comment_rewardPoint) {
		this.comment_rewardPoint = comment_rewardPoint;
	}

	public Long getReply_rewardPoint() {
		return reply_rewardPoint;
	}

	public void setReply_rewardPoint(Long reply_rewardPoint) {
		this.reply_rewardPoint = reply_rewardPoint;
	}

	public boolean isAllowTopic() {
		return allowTopic;
	}

	public void setAllowTopic(boolean allowTopic) {
		this.allowTopic = allowTopic;
	}


	public boolean isAllowFeedback() {
		return allowFeedback;
	}

	public void setAllowFeedback(boolean allowFeedback) {
		this.allowFeedback = allowFeedback;
	}

	public boolean isAllowComment() {
		return allowComment;
	}

	public void setAllowComment(boolean allowComment) {
		this.allowComment = allowComment;
	}

	public boolean isAllowFilterWord() {
		return allowFilterWord;
	}

	public void setAllowFilterWord(boolean allowFilterWord) {
		this.allowFilterWord = allowFilterWord;
	}

	public String getFilterWordReplace() {
		return filterWordReplace;
	}

	public void setFilterWordReplace(String filterWordReplace) {
		this.filterWordReplace = filterWordReplace;
	}

	public Integer getPrivateMessage_submitQuantity() {
		return privateMessage_submitQuantity;
	}

	public void setPrivateMessage_submitQuantity(Integer privateMessage_submitQuantity) {
		this.privateMessage_submitQuantity = privateMessage_submitQuantity;
	}

	public String getTopicEditorTag() {
		return topicEditorTag;
	}

	public void setTopicEditorTag(String topicEditorTag) {
		this.topicEditorTag = topicEditorTag;
	}

	public EditorTag getTopicEditorTagObject() {
		return topicEditorTagObject;
	}

	public void setTopicEditorTagObject(EditorTag topicEditorTagObject) {
		this.topicEditorTagObject = topicEditorTagObject;
	}

	public Integer getTopic_review() {
		return topic_review;
	}

	public void setTopic_review(Integer topic_review) {
		this.topic_review = topic_review;
	}

	public Integer getComment_review() {
		return comment_review;
	}

	public void setComment_review(Integer comment_review) {
		this.comment_review = comment_review;
	}

	public Integer getReply_review() {
		return reply_review;
	}

	public void setReply_review(Integer reply_review) {
		this.reply_review = reply_review;
	}

	public Integer getTopicUnhidePlatformShareProportion() {
		return topicUnhidePlatformShareProportion;
	}

	public void setTopicUnhidePlatformShareProportion(Integer topicUnhidePlatformShareProportion) {
		this.topicUnhidePlatformShareProportion = topicUnhidePlatformShareProportion;
	}

	public String getFileSecureLinkSecret() {
		return fileSecureLinkSecret;
	}

	public void setFileSecureLinkSecret(String fileSecureLinkSecret) {
		this.fileSecureLinkSecret = fileSecureLinkSecret;
	}

	public Long getFileSecureLinkExpire() {
		return fileSecureLinkExpire;
	}

	public void setFileSecureLinkExpire(Long fileSecureLinkExpire) {
		this.fileSecureLinkExpire = fileSecureLinkExpire;
	}




}
