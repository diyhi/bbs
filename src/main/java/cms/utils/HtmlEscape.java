package cms.utils;


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
	

}
