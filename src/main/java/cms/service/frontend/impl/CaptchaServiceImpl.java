package cms.service.frontend.impl;

import cms.component.CaptchaCacheManager;
import cms.component.setting.SettingCacheManager;
import cms.dto.CaptchaVerification;
import cms.service.frontend.CaptchaService;
import cms.utils.FileUtil;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 验证码服务
 */
@Service
public class CaptchaServiceImpl implements CaptchaService {

    @Resource DefaultKaptcha defaultKaptcha;
    @Resource SettingCacheManager settingCacheManager;
    @Resource CaptchaCacheManager captchaCacheManager;


    //不要i,l,o,z字符
    private static final char[] character = { '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a','b','c','d','e','f','g','h','j','k','m','n','p','q','r','s','t','u','v','w','x','y',
            'A','B','C','D','E','F','G','H','J','K','M','N','P','Q','R','S','T','U','V','W','X','Y'};

    /**
     * 生成验证码
     * @param captchaKey 验证码Key
     * @return
     */
    public ResponseEntity<byte[]> generateCaptchaImage(String captchaKey){
        if (captchaKey == null || captchaKey.trim().isEmpty()) {
            // 返回一个 204 No Content 的响应
            return ResponseEntity.noContent().build();
        }

        //删除后缀 .jpg
        String baseCaptchaKey = FileUtil.getBaseName(captchaKey.trim());

        //统计每分钟原来提交次数
        Integer quantity = settingCacheManager.getSubmitQuantity("captcha", baseCaptchaKey);
        //限制次数
        int rateLimit = 60;
        if(quantity != null && quantity > rateLimit){//如果每分钟提交超过60次，则不再生成验证码
            // 如果超过频率限制，返回空图片
            return ResponseEntity.noContent().build();
        }

        //使用指定的字符生成4位长度的随机字符串
        String capText = RandomStringUtils.secure().next(4, character);

        //根据key生成验证码
        captchaCacheManager.saveCaptcha(baseCaptchaKey.trim(),capText);

        try {
            // 创建验证码图片
            BufferedImage bi = defaultKaptcha.createImage(capText);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(bi, "jpg", out);
            byte[] imageBytes = out.toByteArray();


            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .cacheControl(CacheControl.noCache())
                    .body(imageBytes);

        } catch (IOException e) {
            // 捕获异常，返回一个 500 错误
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 校验验证码
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @return
     */
    public CaptchaVerification verifyCaptcha(String captchaKey,String captchaValue){
        if (captchaKey == null || captchaKey.trim().isEmpty()) {
            return new CaptchaVerification(false);
        }
        if(captchaValue == null || captchaValue.trim().isEmpty()){
            return new CaptchaVerification(false);
        }

        //增加验证码重试次数
        //统计每分钟原来提交次数
        Integer original = settingCacheManager.getSubmitQuantity("captcha", captchaKey.trim());
        if(original != null){
            settingCacheManager.addSubmitQuantity("captcha", captchaKey.trim(),original+1);//刷新每分钟原来提交次数
        }else{
            settingCacheManager.addSubmitQuantity("captcha", captchaKey.trim(),1);//刷新每分钟原来提交次数
        }

        String _captcha = captchaCacheManager.getCaptcha(captchaKey.trim());
        if(_captcha != null && _captcha.equalsIgnoreCase(captchaValue)){
            return new CaptchaVerification(true);
        }
        return new CaptchaVerification(false);
    }

}