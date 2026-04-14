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
	 * @param data 数据
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
	 * @param input 输入数据
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
	
	
	/**
	 * Lucene的QueryParser.escape(keyword)转义方法
	 * Returns a String where those characters that QueryParser
	 * expects to be escaped are escaped by a preceding <code>\</code>.
	 */
	public static String lucene_escape(String s) {
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < s.length(); i++) {
	      char c = s.charAt(i);
	      // These characters are part of the query syntax and must be escaped
	      if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':'
	        || c == '^' || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~'
	        || c == '*' || c == '?' || c == '|' || c == '&' || c == '/') {
	        sb.append('\\');
	      }
	      sb.append(c);
	    }
	    return sb.toString();
	}
}
