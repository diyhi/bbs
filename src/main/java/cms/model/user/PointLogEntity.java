package cms.model.user;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

/**
 * 积分日志Entity
 *
 */
@Getter
@Setter
@MappedSuperclass
public class PointLogEntity implements Serializable{
	@Serial
    private static final long serialVersionUID = -286480031746793534L;
	
	/** Id  Id的后4位为用户Id的后4位**/
	@Id @Column(length=36)
	private String id;
	/** 模块 100.话题  200.评论  300.回复 400.积分解锁话题隐藏内容 500.会员卡订单支付 600.账户充值 700.问题 800.答案 900.答案回复 1000.悬赏积分 1100.采纳答案 1200.调整赏金 **/
	private Integer module;
	/** 参数Id    话题Id ,评论Id,回复Id,订单Id **/
	private Long parameterId;
	
	/**操作用户类型  0:系统  1: 员工  2:会员 **/ 
	private Integer operationUserType = 0;
	/**操作用户名称 **/ 
	@Column(length=50)
	private String operationUserName;
	/** 操作用户账号 **/
	@Transient
	private String operationAccount;
	/** 用户名称 **/
	@Column(length=30)
	private String userName;
	/** 积分状态  1:账户存入  2:账户支出 **/
	private int pointState = 1;
	
	
	/** 积分 **/
	protected Long point = 0L;

	
	/** 时间 **/
    @Column(columnDefinition = "DATETIME")
    protected LocalDateTime times = LocalDateTime.now();

	/** 备注 **/
	@Lob
	protected String remark = "";


}
