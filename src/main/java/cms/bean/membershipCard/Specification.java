package cms.bean.membershipCard;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * 规格
 *
 */
@Entity
@Table(indexes = {@Index(name="specification_1_idx", columnList="membershipCardId,sort")})
public class Specification implements Serializable{
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSpecificationName() {
		return specificationName;
	}

	public void setSpecificationName(String specificationName) {
		this.specificationName = specificationName;
	}

	public Long getMembershipCardId() {
		return membershipCardId;
	}

	public void setMembershipCardId(Long membershipCardId) {
		this.membershipCardId = membershipCardId;
	}

	public Long getStock() {
		return stock;
	}

	public void setStock(Long stock) {
		this.stock = stock;
	}

	public Long getStockOccupy() {
		return stockOccupy;
	}

	public void setStockOccupy(Long stockOccupy) {
		this.stockOccupy = stockOccupy;
	}

	public Integer getStockStatus() {
		return stockStatus;
	}

	public void setStockStatus(Integer stockStatus) {
		this.stockStatus = stockStatus;
	}

	public Long getChangeStock() {
		return changeStock;
	}

	public void setChangeStock(Long changeStock) {
		this.changeStock = changeStock;
	}

	public BigDecimal getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(BigDecimal marketPrice) {
		this.marketPrice = marketPrice;
	}

	public BigDecimal getSellingPrice() {
		return sellingPrice;
	}

	public void setSellingPrice(BigDecimal sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Long getPoint() {
		return point;
	}

	public void setPoint(Long point) {
		this.point = point;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Integer getUnit() {
		return unit;
	}

	public void setUnit(Integer unit) {
		this.unit = unit;
	}
	
	
	
}
