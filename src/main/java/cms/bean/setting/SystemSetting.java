package cms.bean.setting;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
	
	/** 关闭站点 1.打开 2.只读模式 3.全站关闭**/
	private Integer closeSite = 1;
	/** 关闭站点提示信息 **/
	@Lob
	private String closeSitePrompt = "";
	
	/** 支持访问设备 1.自动识别终端 2.电脑端 3.移动端 **/
	private Integer supportAccessDevice = 1;
	
	/** 支持编辑器 10.仅富文本编辑器 20.仅Markdown编辑器  30.富文本编辑器优先 40.Markdown编辑器优先 **/
	private Integer supportEditor = 10;
	
	/** 是否允许注册
	private boolean allowRegister = true; **/
	/** 允许注册账号类型 json AllowRegisterAccount格式数据**/
	@Lob
	private String allowRegisterAccount;
	@Transient
	private AllowRegisterAccount allowRegisterAccountObject = new AllowRegisterAccount();

	/** 多语言切换 存储JSON的languageSwitchingCodeList结构 **/
	@Lob
	private String languageSwitching;
	@Transient
	private List<String> languageSwitchingCodeList = new ArrayList<String>();
	/** 多语言表单扩展 存储JSON的languageFormExtensionCodeList结构 **/
	@Lob
	private String languageFormExtension;
	@Transient
	private List<String> languageFormExtensionCodeList = new ArrayList<String>();
	
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
	/** 提交问题最多可选择标签数量 **/
	private Integer maxQuestionTagQuantity = 5;
	/** AI助手每分钟提交超过N次出现验证码 **/
	private Integer aiAssistant_submitQuantity = 5;
	/** 举报每分钟提交超过N次出现验证码 **/
	private Integer report_submitQuantity = 5;
	/** 举报图片允许最大上传数量   0为不允许上传图片 **/
	private Integer reportMaxImageUpload = 3;
	
	
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
	
	/** 允许提交举报 **/
	private boolean allowReport = true;
	
	/** 是否显示IP归属地 **/
	private boolean showIpAddress = false;
	
	/** 提交问题每分钟提交超过N次出现验证码 **/
	private Integer question_submitQuantity = 5;
	/** 提交答案每分钟提交超过N次出现验证码 **/
	private Integer answer_submitQuantity = 5;
	/** 提交问题奖励积分 **/
	private Long question_rewardPoint = 0L;
	/** 提交答案奖励积分 **/
	private Long answer_rewardPoint = 0L;
	/** 提交答案回复奖励积分 **/
	private Long answerReply_rewardPoint = 0L;
	
	/** 前台提交问题审核   10.全部审核 20.特权会员未触发敏感词免审核 30.特权会员免审核 40.触发敏感词需审核 50.无需审核 **/
	private Integer question_review = 10;
	/** 前台提交答案审核   10.全部审核 20.特权会员未触发敏感词免审核 30.特权会员免审核 40.触发敏感词需审核 50.无需审核 **/
	private Integer answer_review = 10;
	/** 前台提交答案回复审核   10.全部审核 20.特权会员未触发敏感词免审核 30.特权会员免审核 40.触发敏感词需审核 50.无需审核 **/
	private Integer answerReply_review = 10;
	
	/** 允许提交问题 **/
	private boolean allowQuestion = false;
	/** 允许提交答案 **/
	private boolean allowAnswer = false;
	
	/** 实名用户才允许提交问题 **/
	private boolean realNameUserAllowQuestion = false;
	/** 实名用户才允许提交答案 **/
	private boolean realNameUserAllowAnswer = false;

	/** 问题悬赏积分下限 **/
	private Long questionRewardPointMin = 0L;
	/** 问题悬赏积分上限 空为无限制 0则不允许悬赏积分 **/
	private Long questionRewardPointMax;
	
	/** 问题悬赏金额下限 **/
	@Column(nullable=false,precision=12, scale=2)
	private BigDecimal questionRewardAmountMin = new BigDecimal("0");
	/** 问题悬赏金额上限 空为无限制 0则不允许悬赏金额 **/
	@Column(precision=12, scale=2)
	private BigDecimal questionRewardAmountMax;
	
	/** 发红包金额下限 **/
	@Column(nullable=false,precision=12, scale=2)
	private BigDecimal giveRedEnvelopeAmountMin = new BigDecimal("0.01");
	/** 发红包金额上限 空为无限制 0则不允许发红包 **/
	@Column(precision=12, scale=2)
	private BigDecimal giveRedEnvelopeAmountMax;
	
	
	/** 解锁话题隐藏内容平台分成比例 默认0% **/
	private Integer topicUnhidePlatformShareProportion = 0;
	
	/** 悬赏问答平台分成比例 默认0% **/
	private Integer questionRewardPlatformShareProportion = 0;
	
	
	
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
	
	/** 话题热度因子加权    例如：评论=200|点赞=100|浏览量=1|重力因子=1.8    以竖线符号分割各热度因子，因子的分数越高，在热度因子中占比重越大；如果不设参数，则因子的加权值默认为1；因子加权值“评论|点赞|浏览量”可以为0至9999之间的整数，“重力因子”可以为0.1至2之间的数  **/
	private String topicHeatFactor;
	
	/** 热门话题仅推荐发布N小时内的话题  空值为不限制 **/
	private Integer topicHotRecommendedTime;
	/** 允许 @提及 用户最大数量  空值为不限制 **/
	private Integer allowMentionMaxNumber = 30;
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
	
	
	/** 问题编辑器标签  json **/
	@Lob
	private String questionEditorTag;
	@Transient
	private EditorTag questionEditorTagObject = new EditorTag();
	/** 答案编辑器标签  json **/
	@Lob
	private String answerEditorTag;
	@Transient
	private EditorTag answerEditorTagObject = new EditorTag();
	
	
	
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

	public String getAllowRegisterAccount() {
		return allowRegisterAccount;
	}

	public void setAllowRegisterAccount(String allowRegisterAccount) {
		this.allowRegisterAccount = allowRegisterAccount;
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

	public Long getQuestion_rewardPoint() {
		return question_rewardPoint;
	}

	public void setQuestion_rewardPoint(Long question_rewardPoint) {
		this.question_rewardPoint = question_rewardPoint;
	}

	public Long getAnswer_rewardPoint() {
		return answer_rewardPoint;
	}

	public void setAnswer_rewardPoint(Long answer_rewardPoint) {
		this.answer_rewardPoint = answer_rewardPoint;
	}

	public Long getAnswerReply_rewardPoint() {
		return answerReply_rewardPoint;
	}

	public void setAnswerReply_rewardPoint(Long answerReply_rewardPoint) {
		this.answerReply_rewardPoint = answerReply_rewardPoint;
	}

	public Integer getQuestion_review() {
		return question_review;
	}

	public void setQuestion_review(Integer question_review) {
		this.question_review = question_review;
	}

	public Integer getAnswer_review() {
		return answer_review;
	}

	public void setAnswer_review(Integer answer_review) {
		this.answer_review = answer_review;
	}

	public Integer getAnswerReply_review() {
		return answerReply_review;
	}

	public void setAnswerReply_review(Integer answerReply_review) {
		this.answerReply_review = answerReply_review;
	}

	public boolean isAllowQuestion() {
		return allowQuestion;
	}

	public void setAllowQuestion(boolean allowQuestion) {
		this.allowQuestion = allowQuestion;
	}

	public boolean isAllowAnswer() {
		return allowAnswer;
	}

	public void setAllowAnswer(boolean allowAnswer) {
		this.allowAnswer = allowAnswer;
	}

	public boolean isRealNameUserAllowQuestion() {
		return realNameUserAllowQuestion;
	}

	public void setRealNameUserAllowQuestion(boolean realNameUserAllowQuestion) {
		this.realNameUserAllowQuestion = realNameUserAllowQuestion;
	}

	public boolean isRealNameUserAllowAnswer() {
		return realNameUserAllowAnswer;
	}

	public void setRealNameUserAllowAnswer(boolean realNameUserAllowAnswer) {
		this.realNameUserAllowAnswer = realNameUserAllowAnswer;
	}

	public String getQuestionEditorTag() {
		return questionEditorTag;
	}

	public void setQuestionEditorTag(String questionEditorTag) {
		this.questionEditorTag = questionEditorTag;
	}

	public EditorTag getQuestionEditorTagObject() {
		return questionEditorTagObject;
	}

	public void setQuestionEditorTagObject(EditorTag questionEditorTagObject) {
		this.questionEditorTagObject = questionEditorTagObject;
	}

	public String getAnswerEditorTag() {
		return answerEditorTag;
	}

	public void setAnswerEditorTag(String answerEditorTag) {
		this.answerEditorTag = answerEditorTag;
	}

	public EditorTag getAnswerEditorTagObject() {
		return answerEditorTagObject;
	}

	public void setAnswerEditorTagObject(EditorTag answerEditorTagObject) {
		this.answerEditorTagObject = answerEditorTagObject;
	}

	public Integer getQuestion_submitQuantity() {
		return question_submitQuantity;
	}

	public void setQuestion_submitQuantity(Integer question_submitQuantity) {
		this.question_submitQuantity = question_submitQuantity;
	}

	public Integer getAnswer_submitQuantity() {
		return answer_submitQuantity;
	}

	public void setAnswer_submitQuantity(Integer answer_submitQuantity) {
		this.answer_submitQuantity = answer_submitQuantity;
	}

	public Integer getMaxQuestionTagQuantity() {
		return maxQuestionTagQuantity;
	}

	public void setMaxQuestionTagQuantity(Integer maxQuestionTagQuantity) {
		this.maxQuestionTagQuantity = maxQuestionTagQuantity;
	}

	public Integer getQuestionRewardPlatformShareProportion() {
		return questionRewardPlatformShareProportion;
	}

	public void setQuestionRewardPlatformShareProportion(Integer questionRewardPlatformShareProportion) {
		this.questionRewardPlatformShareProportion = questionRewardPlatformShareProportion;
	}

	public Long getQuestionRewardPointMin() {
		return questionRewardPointMin;
	}

	public void setQuestionRewardPointMin(Long questionRewardPointMin) {
		this.questionRewardPointMin = questionRewardPointMin;
	}

	public Long getQuestionRewardPointMax() {
		return questionRewardPointMax;
	}

	public void setQuestionRewardPointMax(Long questionRewardPointMax) {
		this.questionRewardPointMax = questionRewardPointMax;
	}

	public BigDecimal getQuestionRewardAmountMin() {
		return questionRewardAmountMin;
	}

	public void setQuestionRewardAmountMin(BigDecimal questionRewardAmountMin) {
		this.questionRewardAmountMin = questionRewardAmountMin;
	}

	public BigDecimal getQuestionRewardAmountMax() {
		return questionRewardAmountMax;
	}

	public void setQuestionRewardAmountMax(BigDecimal questionRewardAmountMax) {
		this.questionRewardAmountMax = questionRewardAmountMax;
	}

	public AllowRegisterAccount getAllowRegisterAccountObject() {
		return allowRegisterAccountObject;
	}

	public void setAllowRegisterAccountObject(AllowRegisterAccount allowRegisterAccountObject) {
		this.allowRegisterAccountObject = allowRegisterAccountObject;
	}

	public BigDecimal getGiveRedEnvelopeAmountMin() {
		return giveRedEnvelopeAmountMin;
	}

	public void setGiveRedEnvelopeAmountMin(BigDecimal giveRedEnvelopeAmountMin) {
		this.giveRedEnvelopeAmountMin = giveRedEnvelopeAmountMin;
	}

	public BigDecimal getGiveRedEnvelopeAmountMax() {
		return giveRedEnvelopeAmountMax;
	}

	public void setGiveRedEnvelopeAmountMax(BigDecimal giveRedEnvelopeAmountMax) {
		this.giveRedEnvelopeAmountMax = giveRedEnvelopeAmountMax;
	}

	public Integer getReport_submitQuantity() {
		return report_submitQuantity;
	}

	public void setReport_submitQuantity(Integer report_submitQuantity) {
		this.report_submitQuantity = report_submitQuantity;
	}

	public boolean isAllowReport() {
		return allowReport;
	}

	public void setAllowReport(boolean allowReport) {
		this.allowReport = allowReport;
	}

	public boolean isShowIpAddress() {
		return showIpAddress;
	}

	public void setShowIpAddress(boolean showIpAddress) {
		this.showIpAddress = showIpAddress;
	}

	public Integer getReportMaxImageUpload() {
		return reportMaxImageUpload;
	}

	public void setReportMaxImageUpload(Integer reportMaxImageUpload) {
		this.reportMaxImageUpload = reportMaxImageUpload;
	}

	public Integer getSupportEditor() {
		return supportEditor;
	}

	public void setSupportEditor(Integer supportEditor) {
		this.supportEditor = supportEditor;
	}

	public String getTopicHeatFactor() {
		return topicHeatFactor;
	}

	public void setTopicHeatFactor(String topicHeatFactor) {
		this.topicHeatFactor = topicHeatFactor;
	}

	public Integer getTopicHotRecommendedTime() {
		return topicHotRecommendedTime;
	}

	public void setTopicHotRecommendedTime(Integer topicHotRecommendedTime) {
		this.topicHotRecommendedTime = topicHotRecommendedTime;
	}

	public Integer getAllowMentionMaxNumber() {
		return allowMentionMaxNumber;
	}

	public void setAllowMentionMaxNumber(Integer allowMentionMaxNumber) {
		this.allowMentionMaxNumber = allowMentionMaxNumber;
	}

	public Integer getAiAssistant_submitQuantity() {
		return aiAssistant_submitQuantity;
	}

	public void setAiAssistant_submitQuantity(Integer aiAssistant_submitQuantity) {
		this.aiAssistant_submitQuantity = aiAssistant_submitQuantity;
	}

	public String getLanguageSwitching() {
		return languageSwitching;
	}

	public void setLanguageSwitching(String languageSwitching) {
		this.languageSwitching = languageSwitching;
	}

	public List<String> getLanguageSwitchingCodeList() {
		return languageSwitchingCodeList;
	}

	public void setLanguageSwitchingCodeList(List<String> languageSwitchingCodeList) {
		this.languageSwitchingCodeList = languageSwitchingCodeList;
	}

	public String getLanguageFormExtension() {
		return languageFormExtension;
	}

	public void setLanguageFormExtension(String languageFormExtension) {
		this.languageFormExtension = languageFormExtension;
	}

	public List<String> getLanguageFormExtensionCodeList() {
		return languageFormExtensionCodeList;
	}

	public void setLanguageFormExtensionCodeList(List<String> languageFormExtensionCodeList) {
		this.languageFormExtensionCodeList = languageFormExtensionCodeList;
	}




}
