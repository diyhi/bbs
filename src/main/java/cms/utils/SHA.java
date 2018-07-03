package cms.utils;


/**
 * sha加密算法
 *
 */
public class SHA {
	/**
	 * sha1加密
	 * @param data 数据
	 * @return
	 */
	public static String sha1Hex(String data) { 
		String sha1Hex = org.apache.commons.codec.digest.DigestUtils.sha1Hex(data);
		return sha1Hex;
    } 
	/**
	 * sha256加密
	 * @param data 数据
	 * @return
	 */
	public static String sha256Hex(String data) { 
		String sha256Hex = org.apache.commons.codec.digest.DigestUtils.sha256Hex(data);  
		return sha256Hex;
    } 
   
}
