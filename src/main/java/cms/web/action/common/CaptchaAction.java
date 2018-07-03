package cms.web.action.common;

import java.awt.image.BufferedImage;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.code.kaptcha.Producer;

import cms.web.action.setting.SettingManage;

/**
 * 验证码
 *
 */
@Controller
public class CaptchaAction {
	@Resource CaptchaManage captchaManage;
	@Resource Producer captchaProducer;//验证码
	@Resource SettingManage settingManage;
	
	//不要i,l,o,z字符
	private static final char[] character = { '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'a','b','c','d','e','f','g','h','j','k','m','n','p','q','r','s','t','u','v','w','x','y',
		'A','B','C','D','E','F','G','H','J','K','M','N','P','Q','R','S','T','U','V','W','X','Y'};  
	
	/**
	 * 生成验证码
	 * @param model
	 * @param captchaKey 验证码Key
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/captcha/{captchaKey}", method=RequestMethod.GET)
	public String execute(ModelMap model,@PathVariable String captchaKey,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setDateHeader("Expires", 0);   
        // Set standard HTTP/1.1 no-cache headers.   
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");   
        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).   
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");   
        // Set standard HTTP/1.0 no-cache header.   
        response.setHeader("Pragma", "no-cache");   
        // return a jpeg   
        response.setContentType("image/jpeg"); 
        // create the text for the image  

        //使用指定的字符生成4位长度的随机字符串
        String capText = RandomStringUtils.random(4, character);
    //    String capText = RandomStringUtils.random(4, new char[]{'a','b','c','d','e','f'});


        if(captchaKey != null && !"".equals(captchaKey.trim())){
        	//统计每分钟原来提交次数
			int quantity = settingManage.submitQuantity_add("captcha", captchaKey.trim(), 0);
        	if(quantity >60){//如果每分钟提交超过60次，则不再生成验证码
        		capText = "";
        	}
        	
        	
        	//根据key删除验证码
            captchaManage.captcha_delete(captchaKey.trim());
            //根据key生成验证码
            captchaManage.captcha_generate(captchaKey.trim(),capText);
            //创建带有文本的图像
            BufferedImage bi = captchaProducer.createImage(capText);   
            ServletOutputStream out = response.getOutputStream();   
            //输出数据  
            ImageIO.write(bi, "jpg", out);   
            try {   
                out.flush();   
            } finally {   
                out.close();   
            } 
        }
    
        
          
        return null;   

	}
	/**
	 * 校验验证码
	 * @param captchaKey 验证码编号
	 * @param captchaValue 验证码值
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/checkCaptcha",method=RequestMethod.GET) 
	@ResponseBody//方式来做ajax,直接返回字符串
	public String verification(ModelMap model,String captchaKey,String captchaValue,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		if(captchaKey != null && !"".equals(captchaKey.trim()) && captchaValue != null && !"".equals(captchaValue.trim())){
			//增加验证码重试次数
			//统计每分钟原来提交次数
			int quantity = settingManage.submitQuantity_add("captcha", captchaKey.trim(), 0);
			//删除每分钟原来提交次数
			settingManage.submitQuantity_delete("captcha", captchaKey.trim());
			//刷新每分钟原来提交次数
			settingManage.submitQuantity_add("captcha", captchaKey.trim(), quantity+1);
			
			String _captcha = captchaManage.captcha_generate(captchaKey.trim(),"");
			if(_captcha != null && _captcha.equals(captchaValue)){
				return "true";
			}
		}
		return "false";
	}
}
