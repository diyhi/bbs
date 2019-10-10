package cms.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * AES加密/解密
 *
 */
public class AES {
	 private static final Logger logger = LogManager.getLogger(AES.class);
	 
	 //默认的初始化向量值
	 private static final String IV_DEFAULT = "Q+\\~W4ER)b$=TYUI";
	 
	/**
	 * 加密
	 * @param data 数据
	 * @param key 密码
	 * @param iv 初始化向量
	 * @return
	 */
	public static String encrypt(String data,String key,String iv) { 
		if(iv == null || iv.length() != 16){//如果iv为空，则使用默认值
			iv = IV_DEFAULT;
		}
		
		
        try { 
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding"); 
            int blockSize = cipher.getBlockSize(); 
        
            byte[] dataBytes = data.getBytes(); 
            int plaintextLength = dataBytes.length; 
            if (plaintextLength % blockSize != 0) { 
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize)); 
            } 
    
            byte[] plaintext = new byte[plaintextLength]; 
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length); 
        
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES"); 
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes()); 
        
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec); 
            byte[] encrypted = cipher.doFinal(plaintext); 
        
            return Base64.encode(encrypted); 
    
        } catch (Exception e) { 
        	if (logger.isErrorEnabled()) {
	            logger.error("加密",e);
	        }
        } 
        return null; 
    } 
    
	/**
	 * 解密
	 * @param data 数据
	 * @param key 密码
	 * @param iv 初始化向量
	 * @return
	 */
    public static String decrypt(String data,String key,String iv) { 
    	if(iv == null || iv.length() != 16){//如果iv为空，则使用默认值
			iv = IV_DEFAULT;
		}
    	
        try { 
            String text = Base64.decode(data.getBytes()); 
               
            byte[] by = ConvertTobyte(text); 
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding"); 
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES"); 
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes()); 
        
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec); 
        
            byte[] original = cipher.doFinal(by); 
            String originalString = new String(original); 
            return originalString.trim(); 
        }catch (Exception e) {
        	if (logger.isErrorEnabled()) {
	            logger.error("解密",e);
	        }
        } 
        return null; 
    } 
       
    private static byte[] ConvertTobyte(String data) { 
        int maxLength = data.length(); 
        byte[] by = new byte[maxLength]; 
        char[] chars = data.toCharArray(); 
        for(int i = 0; i < chars.length; i++) { 
            by[i] = (byte) chars[i]; 
        } 
           
        return by; 
    } 

} 

