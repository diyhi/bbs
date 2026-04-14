package cms.model.user;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import jakarta.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


/**
 * 用户管理
 *
 */
@Getter
@Setter
@Entity
@Table(name="user",uniqueConstraints = {
		@UniqueConstraint(columnNames={"account"}),
		@UniqueConstraint(columnNames={"platformUserId"}
	)},indexes = {@Index(name="user_idx", columnList="state"),@Index(name="user_2_idx", columnList="userName"),@Index(name="user_3_idx", columnList="nickname"),@Index(name="user_4_idx", columnList="mobile"),@Index(name="user_5_idx", columnList="email")}
)//给user字段添加唯一性约束
public class User implements Serializable{
	@Serial
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
	@Column(length=100)
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
    @Column(columnDefinition = "DATETIME")
    protected LocalDateTime registrationDate;
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
    @Getter(AccessLevel.NONE)
	private String avatarPath;
	/** 头像名称 **/
	@Column(length=50)
	private String avatarName;
	/** IP归属地 **/
	@Transient
	private String ipAddress;
	
    //头像路径动态生成
	public String getAvatarPath() {
		if(this.avatarPath == null || this.avatarPath.trim().isEmpty()){
			if(this.getRegistrationDate() != null){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                String date = this.getRegistrationDate().format(formatter);

				//头像路径
                this.avatarPath = "file/avatar/" + date +"/";
			}
		}
		return avatarPath;	
	}
}
