package cms.model.payment;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Transient;

/**
 * 在线支付接口
 * @author Gao
 *
 */
@Getter
@Setter
@Entity
public class OnlinePaymentInterface implements Serializable{

	@Serial
    private static final long serialVersionUID = -8713258827061915758L;
	
	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/** 名称 **/
	@Column(length=100)
	private String name;
	
	/** 接口产品  1.支付宝即时到账   4. 支付宝手机网站 **/
	private Integer interfaceProduct;

	/** 支持设备  第一位:电脑端; 第二位:移动网页端 第三位:移动应用 **/
	@Column(length=5)
	private String supportEquipment;
	
	
	/** 是否选择  true:启用 false: 禁用 **/
	private boolean enable = true;
	
	/** 支付接口动态参数 **/
	@Lob
	private String dynamicParameter;
	
	/** 银行  **/
	@Transient
	private List<Bank> bankList = new ArrayList<Bank>();
	
	/** 排序 **/
	private Integer sort = 1;
	
	/** 版本 **/
	private Integer version = 0;

}
