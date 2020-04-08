package cms.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.util.UriComponentsBuilder;

import cms.web.taglib.Configuration;

/**
 * 防盗链处理
 * 结合nginx下的secure_link模块实现
 */
public class SecureLink {
	
	/**
	 * 生成文件下载重定向链接
	 * @param link 链接
	 * @param fileName 文件名称
	 * @param tagId 标签Id  -1表示管理后台打开链接，不校验权限
	 * @param secret 密钥 16位字符
	 * @return
	 */
	public static String createDownloadRedirectLink(String link,String fileName,Long tagId,String secret){
		Map<String,String> parameterMap = new HashMap<String,String>();
		parameterMap.put("link", link);
		parameterMap.put("fileName", fileName);
		parameterMap.put("tagId", String.valueOf(tagId));
		String parameter_json = JsonUtils.toJSONString(parameterMap);
		
		String ciphertext = AES.encrypt(parameter_json, secret, null);
		
		
		String newLink = UriComponentsBuilder.fromUriString("fileDowload")
        .queryParam("jump", Base64.encodeBase64URL(ciphertext))
        .build().toString();
		
		return newLink;
	}

	
	/**
	 * 生成视频重定向链接
	 * @param link 链接
	 * @param tagId 标签Id  -1表示管理后台打开链接，不校验权限
	 * @param secret 密钥 16位字符
	 * @return
	 */
	public static String createVideoRedirectLink(String link,Long tagId,String secret){
		Map<String,String> parameterMap = new HashMap<String,String>();
		parameterMap.put("link", link);
		parameterMap.put("tagId", String.valueOf(tagId));
		String parameter_json = JsonUtils.toJSONString(parameterMap);
		
		String ciphertext = AES.encrypt(parameter_json, secret, null);
		
		
		String newLink = UriComponentsBuilder.fromUriString("videoRedirect")
        .queryParam("jump", Base64.encodeBase64URL(ciphertext))
        .build().toString();
		
		return newLink;
	}
	
	/**
	 * 生成安全链接
	 * @param link 链接
	 * @param fileName 文件名称
	 * @param secret 密钥
	 * @param expires 有效时间 单位/秒
	 * @return
	 */
	public static String createSecureLink(String link,String fileName,String secret,Long expires){
		String time = String.valueOf((System.currentTimeMillis() / 1000) + expires);
		String md5 = org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(DigestUtils.md5(secret +  Configuration.getPath()+"/"+link + time));
		
		String newLink = UriComponentsBuilder.fromUriString(link)
        .replaceQueryParam("md5", md5)
        .replaceQueryParam("expires", time)
        .replaceQueryParam("fileName", fileName)
        .build().toString();
		
		return newLink;
	}
}
