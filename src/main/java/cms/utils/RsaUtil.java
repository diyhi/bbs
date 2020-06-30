package cms.utils;

import java.io.File;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



/**
 * RSA工具
 *
 */
public class RsaUtil {
	private static final Logger logger = LogManager.getLogger(RsaUtil.class);

	/**
	 * 读取升级公钥文件
	 * @return
	 */
	public static String readPublicKeyFile(){
		String path = "WEB-INF"+File.separator+"classes"+File.separator+"upgradePublicKey.pem";
		try {
     		File file = new File(PathUtil.path()+File.separator+path);
     		if(file != null && file.exists()){
     			return FileUtils.readFileToString(file, "utf-8");
     		}
     		
 		} catch (IOException e) {
 			// TODO Auto-generated catch block
 		//	e.printStackTrace();
 			if (logger.isErrorEnabled()) {
	            logger.error("读文件",e);
	        }
 		}
 		return null;
		
	}
	
	
	/**
	 * 获得公钥
	 * @param publicKey_str 公钥字符
	 * @return
	 */
	public static PublicKey getPublicKey(String publicKey_str){
		try {
			//获取KeyFactory，指定RSA算法
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");

			byte[] publicKeyByte = Base64.decodeBase64(publicKey_str);
			
			//将BASE64解码后的字节数组，构造成X509EncodedKeySpec对象，生成公钥对象
			PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyByte));
			
			return publicKey;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
			    logger.error("算法不存在",e);
	    	}
		}catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
			    logger.error("密钥规范错误",e);
	    	}
		}
		
		return null;
	}
	
	
	/**
	 * 获得私钥
	 * @param privateKey 私钥
	 * @return
	 */
	public static PrivateKey getPrivateKey(String privateKey){
		
		try {
			//获取KeyFactory，指定RSA算法
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	
			byte[] privateKeyByte = Base64.decodeBase64(privateKey);

			//将BASE64解码后的字节数组，构造成PKCS8EncodedKeySpec对象，生成私钥对象
			PrivateKey privatekey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyByte));
			return privatekey;
			
		}catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
			    logger.error("算法不存在",e);
	    	}
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
			    logger.error("密钥规范错误",e);
	    	}
		}
		
		
		return null;
	}
	
	/**
	 * RSA私钥加密
	 * @param data 数据
	 * @param publicKey
	 * @return
	 */
	public static String encryptData(String data, PrivateKey privateKey) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
	        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
	        byte[] dataToEncrypt = data.getBytes("utf-8");
	        byte[] encryptedData = cipher.doFinal(dataToEncrypt);
	        String encryptString = Base64.encodeBase64String(encryptedData);
	        return encryptString;
	    } catch (Exception e) {
	   // 	e.printStackTrace();
	    	if (logger.isErrorEnabled()) {
			    logger.error("RSA私钥加密错误",e);
	    	}
	    }
		return null;
	}
	/**
	 * RSA公钥解密
	 * @param data 数据
	 * @param privateKey
	 * @return
	 */
	public static String decryptData(String data, PublicKey publicKey) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			byte[] descryptData = Base64.decodeBase64(data);
			byte[] descryptedData = cipher.doFinal(descryptData);
			String srcData = new String(descryptedData, "utf-8");
			return srcData;
		} catch (Exception e) {
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
			    logger.error("RSA公钥解密错误",e);
	    	}
		}
		return null;
	}
	
}
