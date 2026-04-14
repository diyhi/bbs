package cms.model.topic;

import java.io.Serial;
import java.io.Serializable;
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
import lombok.Getter;
import lombok.Setter;


/**
 * 评论
 *
 */
@Getter
@Setter
@Entity
@Table(name="comment",indexes = {@Index(name="comment_1_idx", columnList="topicId,status"),@Index(name="comment_2_idx", columnList="quoteIdGroup"),@Index(name="comment_3_idx", columnList="userName,isStaff")})
public class Comment implements Serializable{
	@Serial
    private static final long serialVersionUID = -7576354708273189356L;
	
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
	/** 话题Id **/
	private Long topicId;
	/** 话题标题 **/
	@Transient
	private String topicTitle;
	/** 状态 10.待审核 20.已发布 110.待审核用户删除 120.已发布用户删除 100010.待审核员工删除 100020.已发布员工删除 **/
	private Integer status = 10;
	
	/** 引用json数据 **/
	@Lob
	private String quote;
	/** 自定义引用集合 **/
	@Transient
	private List<Quote> quoteList = new ArrayList<Quote>();
	/** 引用Id组 倒序存放**/
	private String quoteIdGroup = ",";
	/** 引用修改Id (修改评论内容时，评论列表其它引用此评论的引用内容也显示更新后的内容) **/
	private String quoteUpdateId = ",";
	
	/** 评论内容 **/
	@Lob
	private String content;
	
	/** 是否使用Markdown **/
	private Boolean isMarkdown;
	/** Markdown内容 **/
	@Lob
	private String markdownContent;
	
	/** 评论时间 **/
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
	private List<Reply> replyList = new ArrayList<Reply>();

	/**
	 * 添加 回复
	 * @param reply 回复
	 */
	public void addReply(Reply reply){
		this.setTotalReply(this.getTotalReply()+1);
		this.replyList.add(reply);
	}
	
	

}
