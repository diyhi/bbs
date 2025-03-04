package cms.utils;


import org.springframework.lang.Nullable;
import org.springframework.web.util.HtmlUtils;


/**
 * HTML转义
 *
 */
public class HtmlEscape {
	
	/**
	 * 转义
	 * @param data
	 * @return
	 */
	public static String escape(String data){
		if(data == null){
			return "";
		}
		//  &：&amp; 
		//  " ：&quot; 
		//  < ：&lt; 
		//  > ：&gt; 
		data = HtmlUtils.htmlEscape(data);
		//  ' ：\' 
		//  " ：\" 
		//  \ ：\\ 
		//  走纸换页： \f 
		//  换行：\n 
		//  换栏符：\t 
		//  回车：\r 
		//  回退符：\b 
	//	data = JavaScriptUtils.javaScriptEscape(data);
		return data;
	}
	
	/**
	 * 转义符号 ( 小于号转为 &lt; 大于号转为&gt; )
	 * @param input
	 * @return
	 */
	public static String escapeSymbol(String input){
		StringBuilder escaped = new StringBuilder(input.length() * 2);
		for (int i = 0; i < input.length(); i++) {
			char character = input.charAt(i);
			String reference = convertToReference(character);
			if (reference != null) {
				escaped.append(reference);
			}
			else {
				escaped.append(character);
			}
		}
		return escaped.toString();
	}
	
	
	@Nullable
	private static String convertToReference(char character) {
		switch (character){
			case '<':
				return "&lt;";
			case '>':
				return "&gt;";
			/**
			case '"':
				return "&quot;";
			case '&':
				return "&amp;";
			case '\'':
				return "&#39;";**/
		}
		
		return null;
	}
}
