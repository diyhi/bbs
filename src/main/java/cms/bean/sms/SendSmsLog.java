package cms.bean.sms;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 短信发送错误日志
 *
 */
@Entity
public class SendSmsLog implements Serializable{
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
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate = new Date();	
	
	/** 阿里大于 -- 请求ID
	@Column(length=60)
	private String alidayu_requestId; **/
	/** 阿里大于 -- 发送回执ID,可根据该ID查询具体的发送状态 
	@Column(length=60)
	private String alidayu_bizId;**/
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getInterfaceProduct() {
		return interfaceProduct;
	}
	public void setInterfaceProduct(Integer interfaceProduct) {
		this.interfaceProduct = interfaceProduct;
	}
	public Integer getServiceId() {
		return serviceId;
	}
	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}
	public String getPlatformUserId() {
		return platformUserId;
	}
	public void setPlatformUserId(String platformUserId) {
		this.platformUserId = platformUserId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	
}
