package cms.service.frontend;

import cms.dto.CaptchaVerification;
import org.springframework.http.ResponseEntity;

/**
 * 验证码服务接口
 */
public interface CaptchaService {
    /**
     * 生成验证码
     * @param captchaKey 验证码Key
     * @return
     */
    public ResponseEntity<byte[]> generateCaptchaImage(String captchaKey);
    /**
     * 校验验证码
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @return
     */
    public CaptchaVerification verifyCaptcha(String captchaKey, String captchaValue);
}
