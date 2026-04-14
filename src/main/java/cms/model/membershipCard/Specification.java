package cms.model.membershipCard;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;


/**
 * 规格
 *
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name="specification_1_idx", columnList="membershipCardId,sort")})
public class Specification implements Serializable{
	@Serial
    private static final long serialVersionUID = 7157973122318114404L;

	/** ID **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 规格名称 **/
	@Column(length=100)
	private String specificationName;
	
	/** 会员卡Id  **/
	private Long membershipCardId;
	
	
	
	/** 可用库存量 **/
	private Long stock = 0L;
	/** 占用库存量(已出售库存量) **/
	private Long stockOccupy = 0L;
	
	/** 增/减 库存状态   0:不变  1:增加   2:减少**/
	@Transient
	private Integer stockStatus =0;
	/** 更改库存 **/
	@Transient
	private Long changeStock;
	
	/** 支付积分**/
	private Long point;
	
	/** 市场价 精度为12位，小数点位数为2位**/
	@Column(precision=12, scale=2)
	private BigDecimal marketPrice;
	/** 销售价 精度为12位，小数点位数为2位**/
	@Column(precision=12, scale=2)
	private BigDecimal sellingPrice;
	
	/** 时长 **/
	private Integer duration;
	/** 时长单位 10.小时 20.日 30.月 40.年 **/
	private Integer unit;
	
	
	/** 是否启用  true:启用 false:禁用 **/
	private boolean enable = true;
	
	/** 排序 **/
	private Integer sort = 0;

}
