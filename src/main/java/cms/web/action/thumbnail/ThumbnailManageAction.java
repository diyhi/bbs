package cms.web.action.thumbnail;

import java.io.File;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.thumbnail.Thumbnail;
import cms.service.thumbnail.ThumbnailService;
import cms.utils.RedirectPath;
import cms.web.action.FileManage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
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
	@Resource FileManage fileManage;
	
	@Resource(name = "thumbnailValidator") 
	private Validator validator; 
	
	@Resource ThumbnailManage thumbnailManage;
	
	/**
	 * 缩略图管理 添加界面显示
	 */
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(ModelMap model,Thumbnail thumbnail) throws Exception {
		return "jsp/thumbnail/add_thumbnail";
	}
	/**
	 * 缩略图管理 添加
	 */
	@RequestMapping(params="method=add", method=RequestMethod.POST)
	public String add(ModelMap model,Thumbnail formbean,BindingResult result,
			HttpServletRequest request) throws Exception {
		//数据校验
		this.validator.validate(formbean, result); 
		if (result.hasErrors()) { 
			model.addAttribute("thumbnail",formbean);//返回消息
			return "jsp/thumbnail/add_thumbnail";
		} 
		
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
		fileManage.writeStringToFile("file"+File.separator+"topic"+File.separator+"thumbnailMarker"+File.separator+specificationGroup.toString()+".txt","+","utf-8", false);
				
		thumbnailService.saveThumbnail(thumbnail);
		
		
		
		
		model.addAttribute("message", "添加任务成功");//返回消息
		model.addAttribute("urladdress", RedirectPath.readUrl("control.thumbnail.list"));
		return "jsp/common/message";
	}
	
	/**
	 * 缩略图 删除
	 * @param thumbnailId 缩略图Id
	 */
	@RequestMapping(params="method=delete",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String delete(ModelMap model,Integer thumbnailId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(thumbnailId != null && thumbnailId >0){
			Thumbnail thumbnail = thumbnailService.findByThumbnailId(thumbnailId);
			if(thumbnail != null){
				//添加样式删除缩略图标记
				fileManage.writeStringToFile("file"+File.separator+"topic"+File.separator+"thumbnailMarker"+File.separator+thumbnail.getSpecificationGroup()+".txt","-","utf-8", false);
				
				int i = thumbnailService.deleteThumbnail(thumbnailId);
				if(i >0){
					
					
					return "1";
				}
			}	
		}
		return "0";
	}
}
