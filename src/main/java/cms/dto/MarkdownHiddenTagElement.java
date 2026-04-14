package cms.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;

/**
 * Markdown隐藏标签元素
 * @author Gao
 *
 */
@Getter
@Setter
public class MarkdownHiddenTagElement implements Serializable{
	@Serial
    private static final long serialVersionUID = 1427729336880180722L;

	/** 隐藏类型 **/
	private String hide_type;
	/** 隐藏标签输入值 **/
	private String input_value;
	/** 描述 **/
	private String description;
	
	
	public MarkdownHiddenTagElement() {}
	public MarkdownHiddenTagElement(String hide_type, String input_value, String description) {
		this.hide_type = hide_type;
		this.input_value = input_value;
		this.description = description;
	}
}
