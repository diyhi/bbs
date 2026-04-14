package cms.model.thirdParty;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

/**
 * 第三方登录接口
 * @author Gao
 *
 */
@Getter
@Setter
@Entity
public class ThirdPartyLoginInterface implements Serializable{
	@Serial
    private static final long serialVersionUID = 345178962574816009L;
	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/** 名称 **/
	@Column(length=100)
	private String name;
	
	/** 接口产品  10.微信 50.其他开放平台 **/
	private Integer interfaceProduct;

	/** 支持设备  第一位:电脑端; 第二位:移动网页端 第三位:移动应用 第四位:微信浏览器 **/
	@Column(length=10)
	private String supportEquipment;
	
	
	/** 是否选择  true:启用 false: 禁用 **/
	private boolean enable = true;
	
	/** 接口动态参数 **/
	@Lob
	private String dynamicParameter;

	/** 排序 **/
	private Integer sort = 1;
	
	/** 版本 **/
	private Integer version = 0;

}
