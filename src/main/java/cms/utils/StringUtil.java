package cms.utils;
import java.util.List;
import java.util.StringTokenizer;       
import org.apache.commons.lang3.StringUtils;    
/**
 * 字符串工具类
 *
 */
public class StringUtil {
	/**            
	 * 解析一个带 token 分隔符的字符串，这个方法的效率比直接调用String的split()方法快大约1倍   
	 * @param tokenedStr            
	 * @param token           
	 * @return String[]            
	 */           
	public static String[] splitString(String tokenedStr, String token) {       
		String[] ids = null;                  
		if (tokenedStr != null) {                           
			StringTokenizer st = new StringTokenizer(tokenedStr, token);      
			final int arraySize = st.countTokens();                        
			if (arraySize > 0) {                                 
				ids = new String[arraySize];                                 
				int counter = 0;                                 
				while (st.hasMoreTokens()) {                          
					ids[counter++] = st.nextToken();                      
					}                       
				}                   
			}                  
		return ids;           
		}             
	/**            
	 * 把字符串数组组合成一个以指定分隔符分隔的字符串。                        
	 * @param strs 字符串数组           
	 * @param seperator 分隔符           
	 * @return            
	 */          
	public static String mergeString(String[] strs, String seperator) {     
		StringBuilder sb = new StringBuilder();                 
		mergeString(strs, seperator, sb);                  
		return sb.toString();          
	}  
	/**            
	 * 把List数组组合成一个以指定分隔符分隔的字符串。                        
	 * @param list List数组     
	 * @param seperator 分隔符           
	 * @return            
	 */ 
	public static String mergeString(List list, String seperator) {   
		StringBuilder sb = new StringBuilder();         
		for (int i = 0; i < list.size(); i++) {
			if (i != 0 && list.get(i).toString()!= null && !"".equals(list.get(i).toString().trim())) {                              
				sb.append(seperator);                        
			}
			if(list.get(i).toString()!= null && !"".equals(list.get(i).toString().trim())){
				sb.append(list.get(i));            
			}
			
		}              
		return sb.toString();          
	}
	/**           
	 *  把字符串数组组合成一个以指定分隔符分隔的字符串，并追加到给定的<code>StringBuilder</code>                 
	 * @param strs 字符串数组         
	 * @param seperator 分隔符           
	 * @return            
	 **/          
	public static void mergeString(String[] strs, String seperator, StringBuilder sb) {  
		for (int i = 0; i < strs.length; i++) {                         
			if (i != 0) {                              
				sb.append(seperator);                        
				}                           
			sb.append(strs[i]);            
			}         
		}                                    
    
    /**
     * 按字节长度截取字符串
     * @param str 要截取的字符串
     * @param length 截取长度
     * @return
     * @throws Exception
     */
    public static String bSubstring(String str, int length) throws Exception{
        byte[] bytes = str.getBytes("Unicode");
        int n = 0; // 表示当前的字节数
        int i = 2; // 要截取的字节数，从第3个字节开始
        for (; i < bytes.length && n < length; i++){
            // 奇数位置，如3、5、7等，为UCS2编码中两个字节的第二个字节
            if (i % 2 == 1){
                n++; // 在UCS2第二个字节时n加1
            }else{
                // 当UCS2编码的第一个字节不等于0时，该UCS2字符为汉字，一个汉字算两个字节
                if (bytes[i] != 0){
                    n++;
                }
            }
        }
        // 如果i为奇数时，处理成偶数
        if (i % 2 == 1){
            // 该UCS2字符是汉字时，去掉这个截一半的汉字
            if (bytes[i - 1] != 0)
                i = i - 1;
            // 该UCS2字符是字母或数字，则保留该字符
            else
                i = i + 1;
        }
        return new String(bytes, 0, i, "Unicode");
    }
    
    /**
     * 清除前后空格&nbsp;
     * @param html
     * @return
    
    public static String clearSpace(String html){
    	return html.replaceAll("^&nbsp;|&nbsp;$","");
    } */
    /**
     * 替换空格&nbsp;
     * @param html
     * @return
     */
    public static String replaceSpace(String html){
    	return html.toLowerCase().replaceAll("(?i)&nbsp;","");
    	
    }
   
    /** 
     * 转义like语句中的 
     * <code>'_'</code><code>'%'</code> 
     * 将<code>'?'</code>转成sql的<code>'/_'</code> 
     * 将<code>'%'</code>转成sql的<code>'/%'</code> 
     * <p> 
     *   例如搜索<code>?aa*bb?c_d%f</code>将转化成<br/> 
     *   <code>_aa%bb_c/_d/%f</code> 
     * </p> 
     * @param likeStr 
     * @return 
     * @author <a href="http://jdkcn.com" mce_href="http://jdkcn.com">somebody</a> 
     */  
    public static String escapeSQLLike(String likeStr) {  
        String str = StringUtils.replace(likeStr, "_", "/_");  
        str = StringUtils.replace(str, "%",    "/%");  
        str = StringUtils.replace(str, "?", "_");  
        str = StringUtils.replace(str, "*", "%");  
        return str;  
    }  

    
    
}
