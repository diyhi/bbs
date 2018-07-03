package cms.utils;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Druid连接池工具
 *
 */
public class DruidTool {
	private static final Logger logger = LogManager.getLogger(DruidTool.class);
	
	/**
	 * 生成密钥
	 * @param str
	 */
	public static Map<String,String> generateRsaKey(){
		Map<String,String> rsaKey = new LinkedHashMap<String,String>();
		try {
			String[] arr = com.alibaba.druid.filter.config.ConfigTools.genKeyPair(512);
			rsaKey.put("privateKey", arr[0]);//私钥
			rsaKey.put("publicKey", arr[1]);//公钥
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("生成密钥错误",e);
	        }
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("生成密钥错误",e);
	        }
		}
		return rsaKey;
	}
	
	
	
	/**
	 * 加密字符串
	 * @param privateKey 私钥
	 * @param originalText 原文
	 */
	public static String encryptString(String privateKey,String originalText)throws Exception{
		//密文
		String ciphertext = com.alibaba.druid.filter.config.ConfigTools.encrypt(privateKey, originalText);
		
		return ciphertext;
       
	}
	/**
	 * 解密字符串
	 * @param privateKey 公钥
	 * @param cipherText 密文
	 */
	public static String decryptString(String publicKey,String cipherText)throws Exception{
		//原文
		String originalText = com.alibaba.druid.filter.config.ConfigTools.decrypt(publicKey, cipherText);
		
		return originalText; 
	}
}
