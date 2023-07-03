package cms.bean.report;

import java.io.Serializable;
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

import cms.bean.topic.ImageInfo;

/**
 * 举报
 *
 */
@Entity
@Table(name="report",indexes = {@Index(name="report_1_idx", columnList="parameterId,module,status"),@Index(name="report_2_idx", columnList="userName,status"),@Index(name="report_3_idx", columnList="parameterId,userName")})
public class Report implements Serializable{
	private static final long serialVersionUID = -3923487496295992861L;
	
	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/** 用户Id **/
	@Transient
	private Long userId;
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
	
	/** 处理举报的员工账号 **/
	@Column(length=30)
	private String staffAccount;
	
	/** 举报分类Id **/
	@Column(length=36)
	private String reportTypeId;
	/** 举报分类名称 **/
	@Transient
	private String reportTypeName;
	
	/** 参数Id (话题Id；  评论Id；  评论回复Id；  问题Id；  答案Id；  答案回复Id) **/
	@Column(length=65)
	private String parameterId;
	
	/** 扩展参数Id (模块值10：空；  模块值20：话题Id； 模块值30：话题Id-评论Id；  模块值40：空；  模块值50：问题Id；  模块值60：问题Id-答案Id) **/
	@Column(length=130)
	private String extraParameterId;
	
	/** 模块  10:话题  20:评论  30:评论回复  40:问题  50:答案   60:答案回复  **/
	private Integer module;
	
	/** 举报理由 **/
	@Lob
	private String reason;
	
	/** 处理结果 **/
	@Lob
	private String processResult;
	
	/** 备注(本属性值不在前台显示) **/
	@Lob
	private String remark;
	
	/** 图片 List<ImageInfo>json格式**/
	@Lob
	private String imageData;
	
	/** 图片集合 **/
	@Transient
	private List<ImageInfo> imageInfoList = new ArrayList<ImageInfo>();
	
	/** 举报时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date postTime = new Date();
	
	/** IP **/
	@Column(length=45)
	private String ip;
	/** IP归属地 **/
	@Transient
	private String ipAddress;
	
	/** 处理完成时间   处理状态(40:投诉失败  50:投诉成功)为处理完成状态 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date processCompleteTime;
	
	/** 处理状态  10:待处理  20:需补充理由  30:已补充理由  40:投诉失败  50:投诉成功   1010:待处理【用户删除】  1020:需补充理由【用户删除】  1030:已补充理由【用户删除】  1040:投诉失败【用户删除】  1050:投诉成功【用户删除】     100010:待处理【员工删除】  100020:需补充理由【员工删除】  100030:已补充理由【员工删除】  100040:投诉失败【员工删除】  100050:投诉成功【员工删除】  **/
	private Integer status = 10;
	
	/** 版本号 **/
	private Integer version = 0;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getReportTypeId() {
		return reportTypeId;
	}

	public void setReportTypeId(String reportTypeId) {
		this.reportTypeId = reportTypeId;
	}

	public String getReportTypeName() {
		return reportTypeName;
	}

	public void setReportTypeName(String reportTypeName) {
		this.reportTypeName = reportTypeName;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getProcessResult() {
		return processResult;
	}

	public void setProcessResult(String processResult) {
		this.processResult = processResult;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getImageData() {
		return imageData;
	}

	public void setImageData(String imageData) {
		this.imageData = imageData;
	}

	public List<ImageInfo> getImageInfoList() {
		return imageInfoList;
	}

	public void setImageInfoList(List<ImageInfo> imageInfoList) {
		this.imageInfoList = imageInfoList;
	}

	public Date getPostTime() {
		return postTime;
	}

	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}

	public Date getProcessCompleteTime() {
		return processCompleteTime;
	}

	public void setProcessCompleteTime(Date processCompleteTime) {
		this.processCompleteTime = processCompleteTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getParameterId() {
		return parameterId;
	}

	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}

	public Integer getModule() {
		return module;
	}

	public void setModule(Integer module) {
		this.module = module;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAvatarPath() {
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

	public String getExtraParameterId() {
		return extraParameterId;
	}

	public void setExtraParameterId(String extraParameterId) {
		this.extraParameterId = extraParameterId;
	}

	public String getStaffAccount() {
		return staffAccount;
	}

	public void setStaffAccount(String staffAccount) {
		this.staffAccount = staffAccount;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	
	
}
