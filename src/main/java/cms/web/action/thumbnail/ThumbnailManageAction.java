package cms.web.action.thumbnail;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.thumbnail.Thumbnail;
import cms.service.thumbnail.ThumbnailService;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 缩略图管理
 *
 */
@Controller
@RequestMapping("/control/thumbnail/manage") 
public class ThumbnailManageAction {
	@Resource ThumbnailService thumbnailService;
	
	@Resource(name = "thumbnailValidator") 
	private Validator validator; 
	@Resource MessageSource messageSource;
	
	@Resource ThumbnailManage thumbnailManage;
	
	/**
	 * 缩略图管理 添加界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(ModelMap model,Thumbnail thumbnail) throws Exception {
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
	}
	/**
	 * 缩略图管理 添加
	 */
	@ResponseBody
	@RequestMapping(params="method=add", method=RequestMethod.POST)
	public String add(ModelMap model,Thumbnail formbean,BindingResult result,
			HttpServletRequest request) throws Exception {
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		//数据校验
		this.validator.validate(formbean, result); 
		if (result.hasErrors()) {  
			List<FieldError> fieldErrorList = result.getFieldErrors();
			if(fieldErrorList != null && fieldErrorList.size() >0){
				for(FieldError fieldError : fieldErrorList){
					error.put(fieldError.getField(), messageSource.getMessage(fieldError, null));
				}
			}
		}
		if(error.size() ==0){
			StringBuffer specificationGroup = new StringBuffer("");
			
			Thumbnail thumbnail = new Thumbnail();
			thumbnail.setName(formbean.getName());
			thumbnail.setWidth(formbean.getWidth());
			thumbnail.setHigh(formbean.getHigh());
			
			specificationGroup.append(formbean.getWidth() == null ?"" : formbean.getWidth());
			specificationGroup.append("x");
			specificationGroup.append(formbean.getHigh() == null ? "" : formbean.getHigh());
			thumbnail.setSpecificationGroup(specificationGroup.toString());
			
			//添加样式增加缩略图标记
			FileUtil.writeStringToFile("file"+File.separator+"topic"+File.separator+"thumbnailMarker"+File.separator+specificationGroup.toString()+".txt","+","utf-8", false);
					
			thumbnailService.saveThumbnail(thumbnail);
		}
		
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	
	/**
	 * 缩略图 删除
	 * @param thumbnailId 缩略图Id
	 */
	@ResponseBody
	@RequestMapping(params="method=delete",method=RequestMethod.POST)
	public String delete(ModelMap model,Integer thumbnailId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
				
		if(thumbnailId != null && thumbnailId >0){
			Thumbnail thumbnail = thumbnailService.findByThumbnailId(thumbnailId);
			if(thumbnail != null){
				//添加样式删除缩略图标记
				FileUtil.writeStringToFile("file"+File.separator+"topic"+File.separator+"thumbnailMarker"+File.separator+thumbnail.getSpecificationGroup()+".txt","-","utf-8", false);
				
				int i = thumbnailService.deleteThumbnail(thumbnailId);
				if(i >0){
					return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
				}else{
					error.put("thumbnailId", "缩略图删除错误");
				}
			}else{
				error.put("thumbnailId", "缩略图不存在");
			}
		}else{
			error.put("thumbnailId", "缩略图Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
}
