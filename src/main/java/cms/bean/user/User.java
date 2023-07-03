package cms.bean.user;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.joda.time.DateTime;


/**
 * 用户管理
 *
 */
@Entity
@Table(name="user",uniqueConstraints = {
		@UniqueConstraint(columnNames={"account"}),
		@UniqueConstraint(columnNames={"platformUserId"}
	)},indexes = {@Index(name="user_idx", columnList="state"),@Index(name="user_2_idx", columnList="userName")}
)//给user字段添加唯一性约束
public class User implements Serializable{
	private static final long serialVersionUID = 3692366870616346904L;
	
	/** ID **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 会员用户名 从5.6版开始，会员用户名由含有大小写的UUID生成，登录对接账号(account)字段 **/
	@Column(length=30)
	private String userName;
	
	/** 账号 在注册时不能含有横杆和冒号   注销账号后，字段值会加上双冒号和注册时间，例如 test::1640590418623  **/
	@Column(length=65)
	private String account;
	
	/** 注销账号时间  **/
	private Long cancelAccountTime = -1L;
	
	/** 呢称 **/
	@Column(length=50)
	private String nickname;
	/** 密码 密码结构: sha256(sha256(密码)+[盐值])  **/
	@Column(length=160)
	private String password;
	/** 盐值 **/
	@Column(length=80)
	private String salt;
	
	
	/** 安全摘要 需要用户重新登录时改变此值**/
	private Long securityDigest;
	
	/** 是否允许显示用户动态 **/
	private Boolean allowUserDynamic = true;
	
	/** 邮箱地址 **/
	@Column(length=60)
	private String email;
	/** 密码提示问题 **/
	@Column(length=50)
	private String issue;
	/** 密码提示答案 **/
	@Column(length=80)
	private String answer;
	
	
	/** 实名认证绑定手机 **/
	@Column(length=20)
	private String mobile;
	/** 是否实名认证 **/
	private boolean realNameAuthentication = false;

	/** 注册日期 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date registrationDate;
	/** 备注 **/
	@Lob 
	private String remarks;
	/** 当前积分**/
	private Long point = 0L;
	/** 当前预存款 **/
	@Column(precision=14, scale=4) 
	private BigDecimal deposit = new BigDecimal("0");
	
	/** 用户类型 10:本地账号密码用户 20: 手机用户 30: 邮箱用户 40:微信用户 80:其他用户**/
	private Integer type = 10;
	/** 平台用户Id   本地账号密码用户类型为'会员用户名';  手机用户类型为'手机号-mobile';  邮箱用户类型为'邮箱-email';  第三方用户类型的为'第三方用户Id-第三方平台标记',例如微信为'unionid-weixin'    注销后本字段值会加上双冒号和注册时间，例如 oORvU5oUs7AAAsBhw59G3jkUCQlk-weixin::1640850298000 **/
	@Column(length=90)
	private String platformUserId;
	
	/** 用户状态    1:启用   2:停用   11: 启用时删除   12: 停用时删除 **/
	private Integer state = 1;
	/** 用户版本号 **/
	private Integer userVersion = 0;
	
	/** 用户角色名称集合 **/
	@Transient
	private List<String> userRoleNameList = new ArrayList<String>();
	/** 当前等级ID  只用于统计显示,不写入数据库**/
	@Transient
	private Integer gradeId;
	/** 当前等级  只用于统计显示,不写入数据库**/
	@Transient
	private String gradeName;
	/** 头像路径 不写入数据库**/
	@Transient
	private String avatarPath;
	/** 头像名称 **/
	@Column(length=50)
	private String avatarName;
	/** IP归属地 **/
	@Transient
	private String ipAddress;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getIssue() {
		return issue;
	}
	public void setIssue(String issue) {
		this.issue = issue;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public Date getRegistrationDate() {
		return registrationDate;
	}
	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public Long getPoint() {
		return point;
	}
	public void setPoint(Long point) {
		this.point = point;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Integer getGradeId() {
		return gradeId;
	}
	public void setGradeId(Integer gradeId) {
		this.gradeId = gradeId;
	}
	public String getGradeName() {
		return gradeName;
	}
	public void setGradeName(String gradeName) {
		this.gradeName = gradeName;
	}
	public Integer getUserVersion() {
		return userVersion;
	}
	public void setUserVersion(Integer userVersion) {
		this.userVersion = userVersion;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public boolean isRealNameAuthentication() {
		return realNameAuthentication;
	}
	public void setRealNameAuthentication(boolean realNameAuthentication) {
		this.realNameAuthentication = realNameAuthentication;
	}
	public Long getSecurityDigest() {
		return securityDigest;
	}
	public void setSecurityDigest(Long securityDigest) {
		this.securityDigest = securityDigest;
	}
	public String getAvatarPath() {
		if(this.avatarPath == null || "".equals(this.avatarPath.trim())){
			if(this.getRegistrationDate() != null){
				DateTime dateTime = new DateTime(this.getRegistrationDate());     
				String date = dateTime.toString("yyyy-MM-dd");
				//头像路径
				//String _avatarPath = "file"+File.separator+"avatar"+File.separator + date +File.separator;
				String _avatarPath = "file/avatar/" + date +"/";
				this.avatarPath = _avatarPath;
			}
		}
		return avatarPath;	
	}
	public void setAvatarPath(String avatarPath) {
		this.avatarPath = avatarPath;
	}
	public String getAvatarName() {
		return avatarName;
	}
	public void setAvatarName(String avatarName) {
		this.avatarName = avatarName;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public Boolean getAllowUserDynamic() {
		return allowUserDynamic;
	}
	public void setAllowUserDynamic(Boolean allowUserDynamic) {
		this.allowUserDynamic = allowUserDynamic;
	}
	public List<String> getUserRoleNameList() {
		return userRoleNameList;
	}
	public void setUserRoleNameList(List<String> userRoleNameList) {
		this.userRoleNameList = userRoleNameList;
	}
	public BigDecimal getDeposit() {
		return deposit;
	}
	public void setDeposit(BigDecimal deposit) {
		this.deposit = deposit;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getPlatformUserId() {
		return platformUserId;
	}
	public void setPlatformUserId(String platformUserId) {
		this.platformUserId = platformUserId;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public Long getCancelAccountTime() {
		return cancelAccountTime;
	}
	public void setCancelAccountTime(Long cancelAccountTime) {
		this.cancelAccountTime = cancelAccountTime;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

}
