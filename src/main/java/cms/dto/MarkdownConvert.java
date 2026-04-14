package cms.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * Markdown转换
 * @author Gao
 *
 */
@Getter
@Setter
public class MarkdownConvert implements Serializable{
	@Serial
    private static final long serialVersionUID = 2183730791208837873L;
	
	/** Markdown格式化文本 **/
	public String formatterText;
	/** Markdown转html文本 **/
	public String html;
	
	
	public MarkdownConvert() {}
	public MarkdownConvert(String formatterText, String html) {
		this.formatterText = formatterText;
		this.html = html;
	}
}
