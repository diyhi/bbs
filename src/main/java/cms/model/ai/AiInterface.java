package cms.model.ai;

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
 * AI大模型
 * @author Gao
 *
 */
@Getter
@Setter
@Entity
public class AiInterface implements Serializable{
	@Serial
    private static final long serialVersionUID = -7340690365799696749L;
	
	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/** 名称  **/
	@Column(length=100)
	private String name;
	/** 接口产品 10:阿里云百炼大模型  20.火山方舟大模型 **/
	private Integer interfaceProduct;
	/** 是否选择  true:启用 false: 禁用 **/
	private boolean enable = true;
	/** 接口动态参数 **/
	@Lob
	private String dynamicParameter;
	/** 版本 **/
	private Integer version = 0;

}
