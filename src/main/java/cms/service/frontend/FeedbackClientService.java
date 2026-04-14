package cms.service.frontend;

import java.util.Map;

/**
 * 前台在线留言服务接口
 */
public interface FeedbackClientService {

    /**
     * 获取添加在线留言界面信息
     * @return
     */
    public Map<String,Object> getAddFeedbackViewModel();


    /**
     * 添加在线留言
     * @param name 名称
     * @param contact 联系方式
     * @param content 内容
     * @param ip IP地址
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @return
     */
    public Map<String,Object> addFeedback(String name, String contact, String content,String ip,
                                          String captchaKey, String captchaValue);
}
