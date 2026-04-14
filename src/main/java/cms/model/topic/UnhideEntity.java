package cms.model.topic;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

/**
 * 取消隐藏 实体类的抽象基类,定义基本属性
 *
 */
@Getter
@Setter
@MappedSuperclass
public class UnhideEntity implements Serializable{
	@Serial
    private static final long serialVersionUID = -5556786175535354645L;
	
	/** ID (结构：取消隐藏的用户Id_隐藏标签类型_话题Id)**/
	@Id @Column(length=43)
	protected String id;
	/** 取消隐藏的用户名称 **/
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
	
	
	
	
	/** 消费积分 **/
	protected Long point;
	
	/** 消费金额  精度为12位，小数点位数为2位**/
    @Column(precision=19, scale=2)
    protected BigDecimal amount;
	
	/** 取消隐藏时间 **/
    @Column(columnDefinition = "DATETIME")
    protected LocalDateTime cancelTime = LocalDateTime.now();
	
	/** 隐藏标签类型 10:输入密码可见  40:积分购买可见  50:余额购买可见  **/
	protected Integer hideTagType;

	/** 发布话题的用户名称 **/
	@Column(length=30)
	protected String postUserName;
	
	/** 话题Id **/
	protected Long topicId;
	
	/** 话题标题 **/
	@Transient
	protected String topicTitle;

}
