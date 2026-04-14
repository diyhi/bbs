package cms.model.question;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
 * 问题
 *
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name="question_1_idx", columnList="userName,postTime"),@Index(name="question_4_idx", columnList="status,sort,lastAnswerTime"),@Index(name="question_5_idx", columnList="adoptionAnswerId,status,sort,lastAnswerTime"),@Index(name="question_6_idx", columnList="point,status,sort,lastAnswerTime"),@Index(name="question_7_idx", columnList="amount,status,sort,lastAnswerTime")})
public class Question implements Serializable{
	@Serial
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
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime postTime = LocalDateTime.now();
	/** 最后回答时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime lastAnswerTime;
	/** 最后修改时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime lastUpdateTime;
	
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
	
	/** 投票主题Id **/
	@Column(length=32)
	private String voteThemeId;
	
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
	 * @param questionTagAssociation 标签
	 */
	public void addQuestionTagAssociation(QuestionTagAssociation questionTagAssociation) {
		this.getQuestionTagAssociationList().add(questionTagAssociation);
	}
	
	

}
