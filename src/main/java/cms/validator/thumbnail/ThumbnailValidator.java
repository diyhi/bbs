package cms.validator.thumbnail;


import cms.dto.thumbnail.ThumbnailRequest;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * 缩略图校验器
 *
 */
@Component("thumbnailValidator")
public class ThumbnailValidator implements Validator{
	
	public boolean supports(Class<?> clazz) {//该校验器支持的目标类 
		return clazz.equals(ThumbnailRequest.class);
	}
	
	public void validate(Object obj, Errors errors) {//对目标类对象进行校验，错误记录在errors中 
        ThumbnailRequest thumbnail = (ThumbnailRequest) obj;
		
		String width = "";//宽
		String high = "";//高
		if(thumbnail.getName() == null || thumbnail.getName().trim().isEmpty()){
			errors.rejectValue("name","errors.common", new String[]{"名称不能为空"},"");
		}
		if(thumbnail.getWidth() != null){
			if(thumbnail.getWidth() <=0){
				errors.rejectValue("width","errors.common", new String[]{"宽必须大于0"},"");
			}else{
				width = thumbnail.getWidth().toString();
			}
		}else{
			errors.rejectValue("width","errors.common", new String[]{"宽不能为空"},"");
		}
		if(thumbnail.getHigh() != null){
			if(thumbnail.getHigh() <=0){
				errors.rejectValue("high","errors.common", new String[]{"高必须大于0"},"");
			}else{
				high = thumbnail.getHigh().toString();
			}	
		}else{
			errors.rejectValue("high","errors.common", new String[]{"高不能为空"},"");
		}

	}
}
