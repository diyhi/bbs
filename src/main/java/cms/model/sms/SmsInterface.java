package cms.model.sms;

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
 * 短信接口
 *
 */
@Getter
@Setter
@Entity
public class SmsInterface implements Serializable{
	@Serial
    private static final long serialVersionUID = 8947489767344741648L;
	
	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/** 名称 **/
	@Column(length=100)
	private String name;
	
	/** 接口产品  1.阿里云短信  10.云片 **/
	private Integer interfaceProduct;

	/** 是否选择  true:启用 false: 禁用 **/
	private boolean enable = false;
	
	/** 短信接口动态参数 **/
	@Lob
	private String dynamicParameter;
	
	/** 短信发送服务 json格式 List<SendService> **/
	@Lob
	private String sendService;
	

	/** 排序 **/
	private Integer sort = 1;
	
	/** 版本 **/
	private Integer version = 0;

}
