package cms.utils;

import java.util.HashMap;
import java.util.Map;

import cms.component.JsonComponent;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.web.util.UriComponentsBuilder;


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
        JsonComponent jsonComponent = (JsonComponent)SpringConfigTool.getContext().getBean("jsonComponent");

		Map<String,String> parameterMap = new HashMap<String,String>();
		parameterMap.put("link", link);
		parameterMap.put("fileName", fileName);
		parameterMap.put("tagId", String.valueOf(tagId));
		String parameter_json = jsonComponent.toJSONString(parameterMap);
		
		String ciphertext = AES.encrypt(parameter_json, secret, null);


        return UriComponentsBuilder.fromUriString("fileDownload")
        .queryParam("jump", Base64.encodeBase64URL(ciphertext))
        .build().toString();
	}
	
	/**
	 * 生成视频M3U8重定向链接
	 * @param path 路径
	 * @param tagId 标签Id  -1表示管理后台打开链接，不校验权限
	 * @param secret 密钥 16位字符
	 * @return
	 */
	public static String createVideoPlaylistRedirectLink(String path,Long tagId,String secret){
        JsonComponent jsonComponent = (JsonComponent)SpringConfigTool.getContext().getBean("jsonComponent");
        Map<String,String> parameterMap = new HashMap<String,String>();
		parameterMap.put("path", path);
		parameterMap.put("tagId", String.valueOf(tagId));
		String parameter_json = jsonComponent.toJSONString(parameterMap);
		
		String ciphertext = AES.encrypt(parameter_json, secret, null);


        return UriComponentsBuilder.fromUriString("dynamicPlaylist")
        .queryParam("jump", Base64.encodeBase64URL(ciphertext))
        .build().toString();
	}
	
	/**
	 * 生成视频重定向链接
	 * @param link 链接
	 * @param tagId 标签Id  -1表示管理后台打开链接，不校验权限
	 * @param secret 密钥 16位字符
	 * @return
	 */
	public static String createVideoRedirectLink(String link,Long tagId,String secret){
        JsonComponent jsonComponent = (JsonComponent)SpringConfigTool.getContext().getBean("jsonComponent");
        Map<String,String> parameterMap = new HashMap<String,String>();
		parameterMap.put("link", link);
		parameterMap.put("tagId", String.valueOf(tagId));
		String parameter_json = jsonComponent.toJSONString(parameterMap);
		
		String ciphertext = AES.encrypt(parameter_json, secret, null);


        return UriComponentsBuilder.fromUriString("videoRedirect")
        .queryParam("jump", Base64.encodeBase64URL(ciphertext))
        .build().toString();
	}
	
	/**
	 * 生成安全链接
	 * @param link 链接 例如 http://127.0.0.1/bbs-bucket/file/topic/2020-10-11/file/6197f199f6234f2791416282d94a805cb1.rar
	 * @param fileName 文件名称
	 * @param secret 密钥
	 * @param expires 有效时间 单位/秒
	 * @return
	 */
	public static String createSecureLink(String link,String fileName,String secret,Long expires){
		String uri = UriComponentsBuilder.fromUriString(link).build().getPath();
		String time = String.valueOf((System.currentTimeMillis() / 1000) + expires);
		String md5 = org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(DigestUtils.md5(secret +  uri + time));

        return UriComponentsBuilder.fromUriString(link)
	    .replaceQueryParam("md5", md5)
	    .replaceQueryParam("expires", time)
	    .replaceQueryParam("fileName", fileName)
	    .build().toString();
	}
	
	/**
	 * 本地存储生成安全链接(nginx获取URI包括虚拟目录)
	 * @param contextPath 虚拟目录 /api
	 * @param link 链接 例如 file/help/37424583350170/2022-03-17/media/32062d460cd9488296b1338aa33ff7a6.mp4
	 * @param fileName 文件名称
	 * @param secret 密钥
	 * @param expires 有效时间 单位/秒
	 * @return 返回结果不包含虚拟目录
	 */
	public static String createSecureLink(String contextPath,String link,String fileName,String secret,Long expires){
		String uri = UriComponentsBuilder.fromUriString(link).build().getPath();
		String time = String.valueOf((System.currentTimeMillis() / 1000) + expires);
		
		String contextPathParam = "";//   /api/
		if(contextPath != null && !contextPath.trim().isEmpty()){
			contextPathParam = contextPath+"/";
		}else{
			//判断开始部分是否与二参数相同
			if(!Strings.CS.startsWith(uri, "/")){
				contextPathParam = "/";
			}
		}
		
		//   1234567890123456/file/help/37471022242333/2022-03-17/media/9e889a18cb9f45d3aaf3838445ddd9ff.mp41647534852
		String md5 = org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(DigestUtils.md5(secret +  contextPathParam+uri + time));


        return UriComponentsBuilder.fromUriString(link)
	    .replaceQueryParam("md5", md5)
	    .replaceQueryParam("expires", time)
	    .replaceQueryParam("fileName", fileName)
	    .build().toString();
	}
	
	/**
	 * 生成安全URI
	 * @param link 链接 例如 http://127.0.0.1/bbs-bucket/file/topic/2020-10-11/file/6197f199f6234f2791416282d94a805cb1.rar
	 * @param secret 密钥
	 * @param expires 有效时间 单位/秒
	 * @return
	 */
	public static String createSecureLink(String link,String secret,Long expires){
		String uri = UriComponentsBuilder.fromUriString(link).build().getPath();
		String time = String.valueOf((System.currentTimeMillis() / 1000) + expires);
		String md5 = org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(DigestUtils.md5(secret +  uri + time));

        return UriComponentsBuilder.fromUriString(link)
	    .replaceQueryParam("md5", md5)
	    .replaceQueryParam("expires", time)
	    .build().toString();
	}
}