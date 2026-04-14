package cms.model.setting;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

/**
 * 系统设置
 *
 */
@Entity
@Getter
@Setter
public class SystemSetting implements Serializable{
	@Serial
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


	/** 允许注册账号类型 存储JSON格式的registerAccountTypeCodeList结构 **/
	@Lob
	private String allowRegisterAccountType;
	@Transient
	private List<Integer> allowRegisterAccountTypeCodeList = new ArrayList<Integer>();
	
	/** 允许登录账号类型 存储JSON格式的loginAccountTypeCodeList结构 **/
	@Lob
	private String allowLoginAccountType;
	@Transient
	private List<Integer> allowLoginAccountTypeCodeList = new ArrayList<Integer>();
	
	
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
	
	/** 话题发起投票选项数量 空值为不限制  0为不允许发起投票  **/
	private Integer topicMaxVoteOptions = 8;
	/** 问答发起投票选项数量 空值为不限制  0为不允许发起投票  **/
	private Integer questionMaxVoteOptions = 8;
	
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


}
