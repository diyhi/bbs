package cms.model.question;

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
 * 答案回复
 *
 */
@Getter
@Setter
@Entity
@Table(name="answerreply",indexes = {@Index(name="answerReply_1_idx", columnList="answerId,status"),@Index(name="answerReply_2_idx", columnList="questionId"),@Index(name="answerReply_3_idx", columnList="userName,isStaff")})
public class AnswerReply implements Serializable{
	@Serial
    private static final long serialVersionUID = 4931492011929337590L;
	
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
	
	
	/** 对方回复Id **/
	private Long friendReplyId;
	/** 对方回复Id组 **/
	@Lob
	private String friendReplyIdGroup = ",0,";
	/** 对方是否是员工   true:员工   false:会员 **/
	private Boolean isFriendStaff = false;
	/** 对方用户名称 **/
	@Column(length=30)
	private String friendUserName;
	/** 对方账号 **/
	@Transient
	private String friendAccount;
	/** 对方呢称 **/
	@Transient
	private String friendNickname;
	/** 对方头像路径 **/
	@Transient
	private String friendAvatarPath;
	/** 对方头像名称 **/
	@Transient
	private String friendAvatarName;
	
	/** 用户信息状态 -30.账号已注销(不显示数据) -20.账号已逻辑删除(不显示数据) -10.账号已禁用(不显示数据)  0.正常 10.账号已禁用(显示数据) 20.账号已逻辑删除(显示数据) **/
	@Transient
	private Integer userInfoStatus = 0;
	/** 用户角色名称集合 **/
	@Transient
	private List<String> userRoleNameList = new ArrayList<String>();
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
	/** 回复内容 **/
	@Lob
	private String content;
	/** 答案Id **/
	private Long answerId;
	/** 回复时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime postTime = LocalDateTime.now();
	/** 最后修改时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime lastUpdateTime;
	
	/** 状态 10.待审核 20.已发布 110.待审核用户删除 120.已发布用户删除 100010.待审核员工删除 100020.已发布员工删除 **/
	private Integer status = 10;


}
