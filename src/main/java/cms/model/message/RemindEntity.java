package cms.model.message;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

/**
 * 提醒Entity
 *
 */
@Getter
@Setter
@MappedSuperclass
public class RemindEntity implements Serializable{

	@Serial
    private static final long serialVersionUID = -8678342490584706957L;
	
	
	/** Id  Id的后4位为接收提醒用户Id的后4位 **/
	@Id @Column(length=36)
	protected String id;
	/** 接收提醒用户Id **/
	protected Long receiverUserId;
	
	
	/** 提醒发送用户Id **/
	protected Long senderUserId;
	/** 提醒发送用户名称 **/
	@Transient
	protected String senderUserName;
	/** 提醒发送账号 **/
	@Transient
	protected String senderAccount;
	/** 提醒发送用户呢称 **/
	@Transient
	protected String senderNickname;
	/** 提醒发送用户头像路径 **/
	@Transient
	protected String senderAvatarPath;
	/** 提醒发送用户头像名称 **/
	@Transient
	protected String senderAvatarName;
	
	
	/** 提醒类型代码编号  10:别人评论了我的话题  20:别人回复了我的话题 30:别人引用了我的评论 40:别人回复了我的评论 50:别人回复了我回复过的评论  55:别人回复了我的评论回复  60:别人解锁了我的话题 70.别人点赞了我的话题 80.别人关注了我 90.我关注的人发表了话题 100.我关注的人发表了评论 110.我关注的人发表了回复 120:别人回答了我的问题  130:别人回复了我的问题 140:别人回复了我的答案 150:别人回复了我回复过的答案 160:别人回复了我的答案回复   170:我关注的人提了问题  180.我关注的人回答了问题 190.我关注的人发表了答案回复  200:别人在话题提到我  210:别人在评论提到我 220:别人在评论回复提到我  230:别人在问题提到我 240:别人在答案提到我 250:别人在答案回复提到我 260.别人点赞了我的评论  270.别人点赞了我的评论回复  280.别人点赞了我的问题  290.别人点赞了我的答案  300.别人点赞了我的答案回复 **/
	protected Integer typeCode;

	/** 关联数据
	@Lob
	protected String associativeData;**/
	/** 关联数据模块
	@Transient
	protected Object associativeDataModule;**/
	
	/** 提醒状态 10:未读  20:已读  110:未读删除  120:已读删除 **/ 
	protected Integer status = 10;
	
	/** 发送时间格式化 **/
	protected Long sendTimeFormat;
	
	/** 阅读时间格式化 **/
	protected Long readTimeFormat;
	

	/** 发送时间 **/
	@Transient
	protected LocalDateTime sendTime;
	/** 阅读时间 **/
	@Transient
	protected LocalDateTime readTime;
	
	
	/** 话题Id **/
	protected Long topicId = -1L;
	/** 话题标题 **/
	@Transient
	protected String topicTitle;
	/** 我的话题评论Id **/
	protected Long topicCommentId;
	/** 我的话题回复Id **/
	protected Long topicReplyId;
	
	/** 对方的话题评论Id **/
	protected Long friendTopicCommentId;
	/** 对方的话题回复Id **/
	protected Long friendTopicReplyId;
	
	
	/** 问题Id **/
	protected Long questionId = -1L;
	/** 问题标题 **/
	@Transient
	protected String questionTitle;
	/** 我的问题答案Id **/
	protected Long questionAnswerId;
	/** 我的问题回复Id **/
	protected Long questionReplyId;
	
	/** 对方的问题答案Id **/
	protected Long friendQuestionAnswerId;
	/** 对方的问题回复Id **/
	protected Long friendQuestionReplyId;
	
	/** 内容摘要 **/
	@Transient
	protected String summary;
	

	
}
