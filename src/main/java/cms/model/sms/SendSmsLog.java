package cms.model.sms;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * 短信发送错误日志
 *
 */
@Getter
@Setter
@Entity
public class SendSmsLog implements Serializable{
	@Serial
    private static final long serialVersionUID = 657712788941395847L;

	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/** 接口产品 1.阿里大于  10.云片 **/
	private Integer interfaceProduct;
	/** 服务Id 1.绑定手机  10.营销广告  **/
	private Integer serviceId;
	/** 会员用户名
	@Column(length=30)
	private String userName; **/
	
	/** 平台用户Id   本地账号密码用户类型为'会员用户名';  手机用户类型为'手机号-mobile';  邮箱用户类型为'邮箱-email';  第三方用户类型的为'第三方用户Id-第三方平台标记',例如微信为'unionid-weixin' **/
	@Column(length=90)
	private String platformUserId;
	
	/** 手机 **/
	@Column(length=20)
	private String mobile;
	/** 状态码 **/
	@Column(name="\"code\"",length=60)
	private String code;
	/** 状态码描述 **/
	@Column(length=200)
	private String message;
	/** 发送时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createDate = LocalDateTime.now();
	

}
