package cms.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * sha加密算法
 *
 */
public class SHA {
	private static final Logger logger = LogManager.getLogger(SHA.class);
	
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
   
	/**
	 * sha256加密
	 * @param file 文件
	 * @return
	 */
	public static String sha256Hex(File file) {
		String sha1Hex = "";
		try (FileInputStream is = new FileInputStream(file)){
			sha1Hex = org.apache.commons.codec.digest.DigestUtils.sha1Hex(is);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("文件不存在异常",e);
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("读文件IO异常",e);
	        }
		}
		return sha1Hex;
    } 
}
