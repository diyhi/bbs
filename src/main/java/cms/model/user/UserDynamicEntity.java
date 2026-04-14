package cms.model.user;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户动态Entity
 *
 */
@Getter
@Setter
@MappedSuperclass
public class UserDynamicEntity implements Serializable{
	@Serial
    private static final long serialVersionUID = 1121098848019423561L;
	
	/** Id  Id的后4位为用户Id的后4位**/
	@Id @Column(length=36)
	protected String id;
	/** 用户名称 **/
	@Column(length=30)
	protected String userName;
	/** 账号 **/
	@Transient
	protected String account;
	/** 呢称 **/
	@Transient
	protected String nickname;
	/** 头像路径 **/
	@Transient
	protected String avatarPath;
	/** 头像名称 **/
	@Transient
	protected String avatarName;
	
	
	/** 模块 100.话题  200.评论 300.引用评论 400.评论回复  500.问题 600.答案 700.答案回复 **/
	protected Integer module;
	
	/** 功能Id组 格式：,话题Id,评论Id,回复Id,  或者 ,问题Id,答案Id,答案回复Id   **/
	@Column(length=100)
	protected String functionIdGroup;
	
	
	/** 话题Id -1表示默认空值 **/
	protected Long topicId = -1L;
	/** 评论Id -1表示默认空值 **/
	protected Long commentId = -1L;	
	/** 引用评论Id -1表示默认空值 **/
	protected Long quoteCommentId = -1L;
	/** 回复Id -1表示默认空值 **/
	protected Long replyId = -1L;
	
	/** 是否使用Markdown **/
	@Transient
	protected Boolean isMarkdown;
	/** 话题标题 **/
	@Transient
	protected String topicTitle;
	/** 话题内容 **/
	@Transient
	protected String topicContent;
	/** 话题查看总数 **/
	@Transient
	protected Long topicViewTotal;
	/** 话题评论总数 **/
	@Transient
	protected Long topicCommentTotal = 0L;
	/** 话题允许查看的角色名称集合(默认角色除外) **/
	@Transient
	protected List<String> allowRoleViewList = new ArrayList<String>();
	
	/** key:内容含有隐藏标签类型  10.输入密码可见  20.评论话题可见  30.达到等级可见 40.积分购买可见 50.余额购买可见  value:当前用户是否已对隐藏内容解锁 **/
	@Transient
	protected LinkedHashMap<Integer,Boolean> hideTagTypeMap = new LinkedHashMap<Integer,Boolean>();
	
	/** 评论内容 **/
	@Transient
	protected String commentContent;
	/** 引用评论内容 **/
	@Transient
	protected String quoteCommentContent;
	/** 回复内容 **/
	@Transient
	protected String replyContent;
	
	
	/** 问题Id -1表示默认空值 **/
	protected Long questionId = -1L;
	/** 答案Id -1表示默认空值 **/
	protected Long answerId = -1L;
	/** 答案回复Id -1表示默认空值 **/
	protected Long answerReplyId = -1L;
	/** 问题标题 **/
	@Transient
	protected String questionTitle;
	/** 问题内容 **/
	@Transient
	protected String questionContent;
	/** 问题查看总数 **/
	@Transient
	protected Long questionViewTotal;
	/** 问题回答总数 **/
	@Transient
	protected Long questionAnswerTotal = 0L;
	/** 答案内容 **/
	@Transient
	protected String answerContent;
	/** 答案回复内容 **/
	@Transient
	protected String answerReplyContent;
	

	/** 发表时间 **/
    @Column(columnDefinition = "DATETIME")
    protected LocalDateTime postTime = LocalDateTime.now();
	
	/** 状态 10.待审核 20.已发布 110.待审核删除 120.已发布删除 100010.待审核员工删除 100020.已发布员工删除 **/
	protected Integer status = 10;


}
