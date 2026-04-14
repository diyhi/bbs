package cms.utils;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

/**
 * Jasypt工具
 * @author Gao
 *
 */
public class JasyptUtil {
	
	
	public static PooledPBEStringEncryptor config(String salt){
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setConfig(cryptor(salt));
        return encryptor;
    }
	
	/**
     * Jasypt生成加密结果
     * @param salt 配置文件中设定的加密盐值
     * @param value 加密值
     * @return
     */
    public static String encypt(String salt,String value){
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setConfig(cryptor(salt));
        return encryptor.encrypt(value);
    }

    /**
     * 解密
     * @param salt 配置文件中设定的加密盐值
     * @param value 解密密文
     * @return
     */
    public static String decrypt(String salt,String value){
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setConfig(cryptor(salt));
        String result = encryptor.decrypt(value);
        return result;
    }

    private static SimpleStringPBEConfig cryptor(String salt){
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(salt);
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        return config;
    }
}
