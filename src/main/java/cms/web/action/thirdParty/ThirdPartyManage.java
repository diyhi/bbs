package cms.web.action.thirdParty;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import cms.bean.HttpResult;
import cms.bean.thirdParty.ThirdPartyLoginInterface;
import cms.bean.thirdParty.WeChatConfig;
import cms.bean.thirdParty.WeiXinAccessToken;
import cms.bean.thirdParty.WeiXinOpenId;
import cms.bean.thirdParty.WeiXinUserInfo;
import cms.service.thirdParty.ThirdPartyLoginService;
import cms.utils.JsonUtils;
import cms.web.action.HttpRequestConfig;

/**
 * 第三方服务
 *
 */
@Component("thirdPartyManage")
public class ThirdPartyManage {
	private static final Logger logger = LogManager.getLogger(ThirdPartyManage.class);
	
	@Resource HttpRequestConfig httpRequestConfig;
	@Resource ThirdPartyLoginService thirdPartyLoginService;
	
	
	/**
	 * 设置支持设备
	 * @param pc 电脑端 true:支持  false:不支持
	 * @param wap 移动端 true:支持  false:不支持
	 * @param app 应用 true:支持  false:不支持
	 * @param weChat 微信浏览器 true:支持  false:不支持
	 */
	public String setSupportEquipment(boolean pc ,boolean wap ,boolean app,boolean weChat){
		String code = "";
		if(pc == true){
			code += "1";
		}else{
			code += "0";
		}
		if(wap == true){
			code += "1";
		}else{
			code += "0";
		}
		if(app == true){
			code += "1";
		}else{
			code += "0";
		}
		if(weChat == true){
			code += "1";
		}else{
			code += "0";
		}
		return code;
	}
	/**
	 * 判断支持设备
	 * @param supportEquipment 支持设备
	 * @param 当前设备 1:电脑端; 2:移动端 3:移动应用 4:微信浏览器
	 * @return true 支持  false:不支持
	 */
	public boolean isSupportEquipment(String supportEquipment,Integer currentDevice){
		boolean flag = false;
		String first = supportEquipment.substring(0,1);//第一位
		String second = supportEquipment.substring(1,2);//第二位
		String third = supportEquipment.substring(2,3);//第三位
		String four = supportEquipment.substring(3,4);//第四位
		if(currentDevice.equals(1)){
			if(first.equals("1")){
				flag = true;
			}
		}
		if(currentDevice.equals(2)){
			if(second.equals("1")){
				flag = true;
			}
		}
		if(currentDevice.equals(3)){
			if(third.equals("1")){
				flag = true;
			}
		}
		if(currentDevice.equals(4)){
			if(four.equals("1")){
				flag = true;
			}
		}
		return flag;
	}
	
	/**
    * 查询微信配置信息
    * @param code code作为换取access_token的票据，每次用户授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期
    */
	public WeChatConfig queryWeChatConfig(){
		List<ThirdPartyLoginInterface> thirdPartyLoginInterfaceList = thirdPartyLoginService.findAllValidThirdPartyLoginInterface_cache();
		if(thirdPartyLoginInterfaceList != null && thirdPartyLoginInterfaceList.size() >0){
			for(ThirdPartyLoginInterface thirdPartyLoginInterface : thirdPartyLoginInterfaceList){
				if(thirdPartyLoginInterface.getInterfaceProduct().equals(10)){//微信公众号
					if(thirdPartyLoginInterface.getDynamicParameter() != null && !"".equals(thirdPartyLoginInterface.getDynamicParameter().trim())){
						if(thirdPartyLoginInterface.getInterfaceProduct().equals(10)){//微信公众号
							WeChatConfig weChatConfig = JsonUtils.toObject(thirdPartyLoginInterface.getDynamicParameter(), WeChatConfig.class);
							return weChatConfig;
						}
					}
				}
			}
		}
		return null;
	}
	
	
	
	/**
     * 查询微信公众号openid
     * @param code code作为换取access_token的票据，每次用户授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期
     */
    public WeiXinOpenId queryWeiXinOpenId(String code){
    	WeChatConfig weChatConfig = this.queryWeChatConfig();
    	if(weChatConfig == null){
    		return null;
    	}
    	
    	
		String appid = weChatConfig.getOa_appID();//公众号的唯一标识
		String secret = weChatConfig.getOa_appSecret();//公众号的appsecret
		
	
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?"
				+ "appid="+appid+"&secret="+secret+"&code="+code.trim()+"&grant_type=authorization_code";
		
		HttpResult httpResult;
		try {
			httpResult = httpRequestConfig.doPost(url, null);
			if(httpResult != null && httpResult.getCode() != null && httpResult.getCode().equals(200)){
				String data = httpResult.getData();
				if(data != null && !"".equals(data.trim())){
					Map<String,Object> content_map = JsonUtils.toObject(data.trim(), HashMap.class);
					if(content_map != null && content_map.size() >0){
						WeiXinOpenId weiXinOpenId = new WeiXinOpenId();
						for(Map.Entry<String, Object> entry : content_map.entrySet()) {
							if(entry.getKey().equalsIgnoreCase("openid")){
								weiXinOpenId.setOpenId(entry.getValue().toString());
							}else if(entry.getKey().equalsIgnoreCase("errcode")){
								weiXinOpenId.setErrorCode(entry.getValue().toString());
							}else if(entry.getKey().equalsIgnoreCase("errmsg")){
								weiXinOpenId.setErrorMessage(entry.getValue().toString());
							}
							
						}
						return weiXinOpenId;
					}
				}
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("获取微信openid异常",e);
	        }
		}
    	return null;
    }
    
    /**
     * 查询微信access_token
     * @param code code作为换取access_token的票据，每次用户授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期
     * @param appID 应用唯一标识
     * @param appSecret 应用密钥
     */
    private WeiXinAccessToken queryWeiXinAccessToken(String code,String appID,String appSecret){
    	
    	
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?"
				+ "appid="+appID+"&secret="+appSecret+"&code="+code.trim()+"&grant_type=authorization_code";
		
		HttpResult httpResult;
		try {
			httpResult = httpRequestConfig.doPost(url, null);

			if(httpResult != null && httpResult.getCode() != null && httpResult.getCode().equals(200)){
				String data = httpResult.getData();
				if(data != null && !"".equals(data.trim())){
					Map<String,Object> content_map = JsonUtils.toObject(data.trim(), HashMap.class);
					if(content_map != null && content_map.size() >0){
						WeiXinAccessToken weiXinAccessToken = new WeiXinAccessToken();
						for(Map.Entry<String, Object> entry : content_map.entrySet()) {
							if(entry.getKey().equalsIgnoreCase("openid")){
								weiXinAccessToken.setOpenId(entry.getValue().toString());
							}else if(entry.getKey().equalsIgnoreCase("unionid")){
								weiXinAccessToken.setUnionId(entry.getValue().toString());
							}else if(entry.getKey().equalsIgnoreCase("access_token")){
								weiXinAccessToken.setAccess_token(entry.getValue().toString());
							}else if(entry.getKey().equalsIgnoreCase("expires_in")){
								weiXinAccessToken.setExpires_in(entry.getValue().toString());
							}else if(entry.getKey().equalsIgnoreCase("refresh_token")){
								weiXinAccessToken.setRefresh_token(entry.getValue().toString());
							}else if(entry.getKey().equalsIgnoreCase("scope")){
								weiXinAccessToken.setScope(entry.getValue().toString());
							}else if(entry.getKey().equalsIgnoreCase("errcode")){
								weiXinAccessToken.setErrorCode(entry.getValue().toString());
							}else if(entry.getKey().equalsIgnoreCase("errmsg")){
								weiXinAccessToken.setErrorMessage(entry.getValue().toString());
							}
							
						}
						return weiXinAccessToken;
					}
				}
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("获取微信access_token异常",e);
	        }
		}
    	return null;
    }
    
    
    
    /**
     * 查询微信用户基本信息
     * @param code code作为换取access_token的票据，每次用户授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期
     * @param appID 应用唯一标识
     * @param appSecret 应用密钥
     */
    public WeiXinUserInfo queryWeiXinUserInfo(String code,String appID,String appSecret){
    	
    	
    	
    	WeiXinAccessToken weiXinAccessToken = this.queryWeiXinAccessToken(code,appID,appSecret);

    	if(weiXinAccessToken != null){
    		if(weiXinAccessToken.getErrorCode() == null || "".equals(weiXinAccessToken.getErrorCode().trim())){
    			if(weiXinAccessToken.getUnionId() != null && !"".equals(weiXinAccessToken.getUnionId().trim())){
    				WeiXinUserInfo weiXinUserInfo = new WeiXinUserInfo();
    				weiXinUserInfo.setOpenId(weiXinAccessToken.getOpenId());
    				weiXinUserInfo.setUnionId(weiXinAccessToken.getUnionId());
    				weiXinUserInfo.setErrorCode(weiXinAccessToken.getErrorCode());
    				weiXinUserInfo.setErrorMessage(weiXinAccessToken.getErrorMessage());
    				return weiXinUserInfo;
    			}else{
    				String url = "https://api.weixin.qq.com/sns/userinfo?"
        					+ "access_token="+weiXinAccessToken.getAccess_token()+"&openid="+weiXinAccessToken.getOpenId()+"&lang=zh_CN";

        			HttpResult httpResult;
        			try {
        				httpResult = httpRequestConfig.doPost(url, null);
        				if(httpResult != null && httpResult.getCode() != null && httpResult.getCode().equals(200)){
        					String data = httpResult.getData();

        					if(data != null && !"".equals(data.trim())){
        						Map<String,Object> content_map = JsonUtils.toObject(data.trim(), HashMap.class);
        						if(content_map != null && content_map.size() >0){
        							WeiXinUserInfo weiXinUserInfo = new WeiXinUserInfo();
        							for(Map.Entry<String, Object> entry : content_map.entrySet()) {
        								if(entry.getKey().equalsIgnoreCase("openid")){
        									weiXinUserInfo.setOpenId(entry.getValue().toString());
        								}else if(entry.getKey().equalsIgnoreCase("unionid")){
        									weiXinUserInfo.setUnionId(entry.getValue().toString());
        								}else if(entry.getKey().equalsIgnoreCase("nickname")){
        									weiXinUserInfo.setNickname(entry.getValue().toString());
        								}else if(entry.getKey().equalsIgnoreCase("errcode")){
        									weiXinUserInfo.setErrorCode(entry.getValue().toString());
        								}else if(entry.getKey().equalsIgnoreCase("errmsg")){
        									weiXinUserInfo.setErrorMessage(entry.getValue().toString());
        								}
        								
        							}
        							
        							return weiXinUserInfo;
        						}
        					}
        				}	
        			} catch (IOException e) {
        				// TODO Auto-generated catch block
        				//e.printStackTrace();
        				if (logger.isErrorEnabled()) {
        		            logger.error("查询微信用户基本信息异常",e);
        		        }
        			}	
    			}
    		}else{
    			WeiXinUserInfo weiXinUserInfo = new WeiXinUserInfo();
    			weiXinUserInfo.setErrorCode(weiXinAccessToken.getErrorCode());
    			weiXinUserInfo.setErrorMessage(weiXinAccessToken.getErrorMessage());
    			return weiXinUserInfo;
    		}
    		
    		
    	}
    	
    	
		
    	return null;
    	
    }
    
    
    /**
	 * 添加微信第三方用户的唯一标识票据
	 * @param weiXinCode 微信第三方用户的唯一标识票据
	 * @param weiXinOpenId 微信返回的openid
	 */
	@CachePut(value="thirdPartyManage_cache_weiXinOpenId",key="#weiXinCode")
    public WeiXinOpenId addWeiXinOpenId(String weiXinCode, WeiXinOpenId weiXinOpenId) {
		return weiXinOpenId;
    }
	/**
     * 根据微信第三方用户的唯一标识票据获取微信openid
     * @param weiXinCode 微信第三方用户的唯一标识票据
     * @return
     */
	@Cacheable(value="thirdPartyManage_cache_weiXinOpenId",key="#weiXinCode")
    public WeiXinOpenId getWeiXinOpenId(String weiXinCode) {
    	return null;
    }
    
	/**
	 * 删除微信第三方用户的唯一标识票据
	 * @param weiXinCode 微信第三方用户的唯一标识票据
	 * @return
	 */
	@CacheEvict(value="thirdPartyManage_cache_weiXinOpenId",key="#weiXinCode")
	public void deleteWeiXinOpenId(String weiXinCode){
	}

}
