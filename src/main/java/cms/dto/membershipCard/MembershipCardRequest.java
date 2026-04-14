package cms.dto.membershipCard;


import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 会员卡表单
 */
@Getter
@Setter
public class MembershipCardRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -8728061434630336710L;
    /** Id  **/
    private Long membershipCardId;

    /** 名称**/
    private String name;
    /** 副标题 **/
    private String subtitle;
    /** 角色Id **/
    private String userRoleId;

    /** 最低销售价 精度为12位，小数点位数为2位**/
    private BigDecimal lowestPrice;
    /** 最高销售价 精度为12位，小数点位数为2位**/
    private BigDecimal highestPrice;

    /** 最低积分**/
    private Long lowestPoint;
    /** 最高积分**/
    private Long highestPoint;

    /** 状态 1:上架  2.下架 3.规格下架(本状态不存储) 11. 上架标记删除  12. 下架标记删除 **/
    private Integer state;
    /** 排序 **/
    private Integer sort;
    /** 简介**/
    private String introduction;


    /** 规格表格参数行 **/
    private Integer[] specificationRowTable;
    /** 规格Id **/
    private Long[] specificationId;
    /** 规格名称 **/
    private String[] specificationName;
    /** 是否启用 **/
    private Boolean[] enable;
    /** 库存量 **/
    private String[] stock;
    /** 更改库存状态 **/
    private Integer[] stockStatus;
    /** 更改库存 **/
    private String[] changeStock;
    /** 积分 **/
    private String[] point;
    /** 市场价 **/
    private String[] marketPrice;
    /** 销售价 **/
    private String[] sellingPrice;
    /** 说明标签 **/
    private String[] descriptionTag;
    /** 时长 **/
    private String[] duration;
    /** 时长单位 **/
    private Integer[] unit;

}
