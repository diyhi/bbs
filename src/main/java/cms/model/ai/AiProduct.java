package cms.model.ai;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI大模型产品
 * @author Gao
 *
 */
@Getter
@Setter
public class AiProduct implements Serializable{
	@Serial
    private static final long serialVersionUID = -8649233994892624474L;
	
	/** 接口产品 **/
	private Integer interfaceProduct;
	/** 名称 **/
	private String name;

}
