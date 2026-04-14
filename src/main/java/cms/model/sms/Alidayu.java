package cms.model.sms;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 阿里大于参数
 *
 */
@Getter
@Setter
public class Alidayu implements Serializable{
	@Serial
    private static final long serialVersionUID = 3019531034925238031L;

	private String accessKeyId="";
	
	private String accessKeySecret="";

	
}
