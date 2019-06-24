package cms.bean.user;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 积分日志Entity
 *
 */

@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class PointLogEntity implements Serializable{
	private static final long serialVersionUID = -286480031746793534L;
	
	/** Id  Id的后4位为用户Id的后4位**/
	@Id @Column(length=36)
	private String id;
	/** 模块 100.话题  200.评论  300.回复 400.积分购买隐藏话题 **/
	private Integer module;
	/** 参数Id    话题Id ,评论Id,回复Id **/
	private Long parameterId;
	
	/**操作用户类型  0:系统  1: 员工  2:会员 **/ 
	private Integer operationUserType = 0;
	/**操作用户名称 **/ 
	@Column(length=50)
	private String operationUserName;
	/** 用户名称 **/
	@Column(length=30)
	private String userName;
	/** 积分状态  1:账户存入  2:账户支出 **/
	private int pointState = 1;
	
	
	/** 积分 **/
	protected Long point = 0L;

	
	/** 时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date times = new Date();

	/** 备注 **/
	@Lob
	protected String remark = "";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public Integer getModule() {
		return module;
	}

	public void setModule(Integer module) {
		this.module = module;
	}

	public Long getParameterId() {
		return parameterId;
	}

	public void setParameterId(Long parameterId) {
		this.parameterId = parameterId;
	}

	public Integer getOperationUserType() {
		return operationUserType;
	}

	public void setOperationUserType(Integer operationUserType) {
		this.operationUserType = operationUserType;
	}

	public String getOperationUserName() {
		return operationUserName;
	}

	public void setOperationUserName(String operationUserName) {
		this.operationUserName = operationUserName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getPointState() {
		return pointState;
	}

	public void setPointState(int pointState) {
		this.pointState = pointState;
	}

	public Long getPoint() {
		return point;
	}

	public void setPoint(Long point) {
		this.point = point;
	}

	public Date getTimes() {
		return times;
	}

	public void setTimes(Date times) {
		this.times = times;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	
	
}
