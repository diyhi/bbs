package cms.model.question;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


/**
 * 答案
 *
 */
@Getter
@Setter
@Entity
@Table(name="answer",indexes = {@Index(name="answer_1_idx", columnList="questionId,status,adoption"),@Index(name="answer_2_idx", columnList="userName,isStaff")})
public class Answer implements Serializable{
	@Serial
    private static final long serialVersionUID = -7929411590314542710L;
	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 是否是员工   true:员工   false:会员 **/
	private Boolean isStaff = false;
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
	
	/** IP **/
	@Column(length=45)
	private String ip;
	/** IP归属地 **/
	@Transient
	private String ipAddress;
	/** 问题Id **/
	private Long questionId;
	/** 问题标题 **/
	@Transient
	private String questionTitle;
	/** 状态 10.待审核 20.已发布 110.待审核用户删除 120.已发布用户删除 100010.待审核员工删除 100020.已发布员工删除 **/
	private Integer status = 10;
	/** 答案是否采纳 **/
	private Boolean adoption = false;
	
	/** 答案内容 **/
	@Lob
	private String content;
	/** 是否使用Markdown **/
	private Boolean isMarkdown;
	/** Markdown内容 **/
	@Lob
	private String markdownContent;
	/** 回答时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime postTime = LocalDateTime.now();
	/** 最后修改时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime lastUpdateTime;
	
	/** 用户角色名称集合 **/
	@Transient
	private List<String> userRoleNameList = new ArrayList<String>();
	/** 总回复数 **/
	@Transient
	private Integer totalReply = 0;
	/** 回复集合 **/
	@Transient
	private List<AnswerReply> answerReplyList = new ArrayList<AnswerReply>();

	/**
	 * 添加 回复
	 * @param answerReply 答案回复
	 */
	public void addAnswerReply(AnswerReply answerReply){
		this.setTotalReply(this.getTotalReply()+1);
		this.answerReplyList.add(answerReply);
	}


}
