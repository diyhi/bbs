package cms.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * Markdown隐藏标签属性值
 * @author Gao
 *
 */
@Getter
@Setter
public class MarkdownHiddenTagAttributeValue implements Serializable{
	@Serial
    private static final long serialVersionUID = 778833097169293279L;
	
	/** 密码输入值 **/
	private String password_inputValue = "";
	/** 达到等级输入值 **/
	private String grade_inputValue = "";
	/** 积分购买输入值 **/
	private String point_inputValue = "";
	/** 余额购买输入值 **/
	private String amount_inputValue = "";
	

}
