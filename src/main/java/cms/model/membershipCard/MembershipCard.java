package cms.model.membershipCard;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import lombok.Getter;
import lombok.Setter;

/**
 * 会员卡
 *
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name="membershipCard_1_idx", columnList="createDate")})
public class MembershipCard implements Serializable{
	@Serial
    private static final long serialVersionUID = -4891301979499383115L;
	
	/** ID **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 名称**/
	@Column(length=190)
	private String name;
	/** 副标题 **/
	@Column(length=190)
	private String subtitle;
	/** 角色Id **/
	@Column(length=32)
	private String userRoleId;
	
	/** 最低销售价 精度为12位，小数点位数为2位**/
	@Column(precision=12, scale=2)
	private BigDecimal lowestPrice;
	/** 最高销售价 精度为12位，小数点位数为2位**/
	@Column(precision=12, scale=2)
	private BigDecimal highestPrice;
	
	/** 最低积分**/
	private Long lowestPoint;
	/** 最高积分**/
	private Long highestPoint;
	
	
	/** 创建时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createDate = LocalDateTime.now();
	
	/** 简介**/
	@Lob
	private String introduction;
	/** 状态 1:上架  2.下架 3.规格下架(本状态不存储) 11. 上架标记删除  12. 下架标记删除 **/
	private Integer state = 1;
	
	
	/** 排序 **/
	private Integer sort = 0;
	
	/** 规格集合 **/
	@Transient
	private List<Specification> specificationList = new ArrayList<Specification>();
	
	/** 说明标签集合 **/
	@Transient
	private List<String> descriptionTagList = new ArrayList<String>();
	/** 说明标签 JSON格式List<String>**/
	@Lob
	private String descriptionTagFormat;
	
	
	
	public void addSpecification(Specification specification) {
		this.specificationList.add(specification);
	}
	

}
