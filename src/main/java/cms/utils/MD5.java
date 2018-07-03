package cms.utils;

/**
 * MD5加密
 *
 */
public class MD5 {
	/**  
    * MD5 加密  
    */   
   public static String getMD5(String str) {   
	   String md5Hex = org.apache.commons.codec.digest.DigestUtils.md5Hex(str);   
	   return md5Hex;
   }   
   
   
}
