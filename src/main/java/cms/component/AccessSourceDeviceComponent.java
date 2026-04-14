package cms.component;



import cms.model.setting.SystemSetting;
import cms.repository.setting.SettingRepository;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.stereotype.Component;


/**
 * 访问来源设备
 *
 */
@Component("accessSourceDeviceComponent")
public class AccessSourceDeviceComponent {
	
	@Resource SettingRepository settingRepository;

    // 单例实例
    private static final UserAgentAnalyzer userAgentAnalyzer =
            UserAgentAnalyzer.newBuilder()
                    // 只需要 DeviceClass 字段，这样可以大幅提高解析性能
                    .withField("DeviceClass")
                    .build();


	/**
	 * 访问设备
	 * @param request 请求信息
	 * @return pc:电脑 wap:移动设备
	 */
	public String accessDevices(HttpServletRequest request) {
		SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
		if(systemSetting != null && systemSetting.getSupportAccessDevice() != null){
			if(systemSetting.getSupportAccessDevice().equals(2)){
				return "pc";//电脑端
			}else if(systemSetting.getSupportAccessDevice().equals(3)){
				return "wap";//移动设备
			}
		}

        String userAgentText = request.getHeader("User-Agent");

        if (userAgentText == null || userAgentText.isEmpty()) {
            return "pc";//电脑
        } else {
            // 解析 User-Agent 字符串
            UserAgent userAgent = userAgentAnalyzer.parse(userAgentText);

            // 获取设备类型并进行比较
            String deviceClass = userAgent.getValue("DeviceClass");

            // yauaa 将桌面设备归类为 "Desktop"
            if("Desktop".equalsIgnoreCase(deviceClass)){
                return "pc";//电脑
            }else{
                return "wap";//移动设备
            }
        }
	}
	

}
