package cms.utils;

import cms.bean.statistic.AccessInfo;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;

/**
 * 浏览器UserAgent解析
 *
 */
public class UserAgentAnalysis {
	
	/**
	 * 解析
	 * String ua = request.getHeader("User-Agent");
	 * @param ua 请求头字段包含有关发起请求的用户代理的信息
	 */
	public static AccessInfo analysis(String ua){
		if(ua != null && !"".equals(ua.trim())){
			//转成UserAgent对象
			UserAgent userAgent = UserAgent.parseUserAgentString(ua);
			if(userAgent != null){
				AccessInfo accessInfo = new AccessInfo();
				//获取浏览器信息
				Browser browser = userAgent.getBrowser();
				if(browser != null){
					String browserName = browser.getName();// 浏览器名称
					accessInfo.setBrowserName(browserName);//浏览器名称
					
					if(browser.getGroup() != null){
						String group = browser.getGroup().getName();// 浏览器大类
						accessInfo.setGroup(group);//浏览器大类
					}
				}
				
				
				Version browserVersion = userAgent.getBrowserVersion();// 详细版本
				if(browserVersion != null){
					String version = browserVersion.getMajorVersion();// 浏览器主版本
					accessInfo.setBrowserVersion(browserVersion.getVersion());//详细版本
					accessInfo.setVersion(version);//浏览器主版本
					
				}
				
			//	System.out.println("浏览器名称 "+browserName);
			//	System.out.println("浏览器大类 "+group);
			//	System.out.println("详细版本 "+browserVersion);
			//	System.out.println("浏览器主版本 "+version);
			//	System.out.println("访问设备系统 "+userAgent.getOperatingSystem());// 访问设备系统
				//系统名称
			//	String system = userAgent.getOperatingSystem().getName();
			//	System.out.println("访问设备类型 "+userAgent.getOperatingSystem().getDeviceType());// 访问设备类型
			//	System.out.println("访问设备制造厂商 "+userAgent.getOperatingSystem().getManufacturer());// 访问设备制造厂商
				
				
				if(userAgent.getOperatingSystem() != null){
					accessInfo.setOperatingSystem(userAgent.getOperatingSystem().toString());//访问设备系统
					if(userAgent.getOperatingSystem().getDeviceType() != null && userAgent.getOperatingSystem().getDeviceType().getName() != null){
						if(userAgent.getOperatingSystem().getDeviceType().getName().equals("Computer")){
							accessInfo.setDeviceType("电脑");//访问设备类型
						}else if(userAgent.getOperatingSystem().getDeviceType().getName().equals("Mobile")){
							accessInfo.setDeviceType("手机");//访问设备类型
						}else if(userAgent.getOperatingSystem().getDeviceType().getName().equals("Tablet")){
							accessInfo.setDeviceType("平板");//访问设备类型
						}else if(userAgent.getOperatingSystem().getDeviceType().getName().equals("Game console")){
							accessInfo.setDeviceType("游戏机");//访问设备类型
						}else if(userAgent.getOperatingSystem().getDeviceType().getName().equals("Digital media receiver")){
							accessInfo.setDeviceType("数字媒体接收器");//访问设备类型
						}else if(userAgent.getOperatingSystem().getDeviceType().getName().equals("Wearable computer")){
							accessInfo.setDeviceType("可穿戴设备");//访问设备类型
						}else if(userAgent.getOperatingSystem().getDeviceType().getName().equals("Unknown")){
							accessInfo.setDeviceType("未知设备");//访问设备类型
						}
						
					}
					
					
				}
				
				

				return accessInfo;
			}
			
		}
		
		return null;
	}
}
