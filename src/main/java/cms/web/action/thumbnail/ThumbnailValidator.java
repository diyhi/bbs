package cms.web.action.thumbnail;


import java.io.File;

import javax.annotation.Resource;

import cms.bean.thumbnail.Thumbnail;
import cms.service.thumbnail.ThumbnailService;
import cms.utils.PathUtil;
import cms.web.action.FileManage;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * 缩略图校验器
 *
 */
@Component("thumbnailValidator")
public class ThumbnailValidator implements Validator{
	@Resource FileManage fileManage;
	@Resource ThumbnailService thumbnailService;
	
	public boolean supports(Class<?> clazz) {//该校验器支持的目标类 
		return clazz.equals(Thumbnail.class); 
	}
	
	public void validate(Object obj, Errors errors) {//对目标类对象进行校验，错误记录在errors中 
		Thumbnail thumbnail = (Thumbnail) obj;
		
		String width = "";//宽
		String high = "";//高
		if(thumbnail.getName() == null || "".equals(thumbnail.getName().trim())){
			errors.rejectValue("name","errors.required", new String[]{"名称不能为空"},"");
		}
		if(thumbnail.getWidth() != null){
			if(thumbnail.getWidth() <=0){
				errors.rejectValue("width","errors.required", new String[]{"宽必须大于0"},"");
			}else{
				width = thumbnail.getWidth().toString();
			}
		}else{
			errors.rejectValue("width","errors.required", new String[]{"宽不能为空"},"");
		}
		if(thumbnail.getHigh() != null){
			if(thumbnail.getHigh() <=0){
				errors.rejectValue("high","errors.required", new String[]{"高必须大于0"},"");
			}else{
				high = thumbnail.getHigh().toString();
			}	
		}else{
			errors.rejectValue("high","errors.required", new String[]{"高不能为空"},"");
		}
		if(!"".equals(width+high)){
			StringBuffer specificationGroup = new StringBuffer("");
			specificationGroup.append(thumbnail.getWidth() == null ?"" : thumbnail.getWidth());
			specificationGroup.append("x");
			specificationGroup.append(thumbnail.getHigh() == null ? "" : thumbnail.getHigh());
			
			Thumbnail t = thumbnailService.findThumbnailBySpecificationGroup(specificationGroup.toString());
			if(t != null){
				errors.rejectValue("specificationGroup","errors.required", new String[]{"当前规格已存在"},"");
			}
			
			File file = new File(PathUtil.path()+File.separator+"file"+File.separator+"topic"+File.separator+"thumbnailMarker"+File.separator+specificationGroup.toString()+".txt");
			if(file.exists()){
				errors.rejectValue("specificationGroup","errors.required", new String[]{"当前规格运行中，请稍后再添加"},"");
			}
		
		}
	}
}
