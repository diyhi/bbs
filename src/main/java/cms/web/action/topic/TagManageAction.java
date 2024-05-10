package cms.web.action.topic;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.topic.Tag;
import cms.service.setting.SettingService;
import cms.service.topic.TagService;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.UUIDUtil;
import cms.web.action.TextFilterManage;
import cms.web.action.fileSystem.FileManage;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * 标签管理
 *
 */
@Controller
@RequestMapping("/control/tag/manage") 
public class TagManageAction {

	@Resource(name = "tagValidator") 
	private Validator validator; 
	@Resource TagService tagService; 
	@Resource TagManage tagManage;
	
	@Resource FileManage fileManage;
	@Resource SettingService settingService;
	@Resource MessageSource messageSource;
	@Resource TextFilterManage textFilterManage;
	
	/**
	 * 标签   添加界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(Tag tag,ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
	}
	
	/**
	 * 标签  添加
	 */
	@RequestMapping(params="method=add", method=RequestMethod.POST)
	@ResponseBody
	public String add(ModelMap model,Tag formbean,BindingResult result,String imagePath,MultipartFile images,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		Tag tag = new Tag(); 
		String _imagePath = "";
		String _fileName = "";
		if (images == null || images.isEmpty()) { 
			if(imagePath != null && !"".equals(imagePath.trim())){
				imagePath = textFilterManage.deleteBindURL(request, imagePath);
				
				String fileName = FileUtil.getName(imagePath);
				
				//取得路径名称
				String pathName = FileUtil.getFullPath(imagePath);
				
				//旧路径必须为file/topicTag/开头
				if(imagePath.substring(0, 14).equals("file/topicTag/")){
					//新路径名称
					String newPathName = "file/topicTag/";
					
					//如果新旧路径不一致
					if(!newPathName.equals(pathName)){
						
						//复制文件到新路径
						fileManage.copyFile(FileUtil.toRelativePath(imagePath), newPathName);
						//新建文件锁到新路径
						//生成锁文件名称
						String lockFileName =fileName;
						//添加文件锁
						fileManage.addLock("file"+File.separator+"topicTag"+File.separator+"lock"+File.separator,lockFileName);
						
						
					}
				
					_imagePath = "file/topicTag/";
					_fileName = fileName;
				}
				
					  
			}
        }else{
        	//验证文件类型
			List<String> formatList = new ArrayList<String>();
			formatList.add("gif");
			formatList.add("jpg");
			formatList.add("jpeg");
			formatList.add("bmp");
			formatList.add("png");
			formatList.add("svg");
			boolean authentication = FileUtil.validateFileSuffix(images.getOriginalFilename(),formatList);
			if(authentication){
				//取得文件后缀		
				String ext = FileUtil.getExtension(images.getOriginalFilename());
				//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
				String pathDir = "file"+File.separator+"topicTag"+File.separator;
				//构建文件名称
				String fileName = UUIDUtil.getUUID32()+ "." + ext;
				_imagePath = "file/topicTag/";
				_fileName = fileName; 
				  
				//生成文件保存目录
				FileUtil.createFolder(pathDir);
				 
				//生成锁文件名称
				String lockFileName = fileName;
				//添加文件锁
				fileManage.addLock("file"+File.separator+"topicTag"+File.separator+"lock"+File.separator,lockFileName);
				
				//保存文件
				fileManage.writeFile(pathDir, fileName,images.getBytes());
				
			}else{
				error.put("images", "图片格式错误");
			}
        }
		tag.setImage(_imagePath+_fileName);
		
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
			
			tag.setId(tagManage.nextNumber());
			tag.setName(formbean.getName());
			
			tag.setSort(formbean.getSort());
				
			tagService.saveTag(tag);
			
			//删除图片锁
			if(_imagePath != null && !"".equals(_imagePath.trim())){	
				fileManage.deleteLock("file"+File.separator+"topicTag"+File.separator+"lock"+File.separator,_fileName);		
			}
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	
	
	/**
	 * 标签   修改界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=edit", method=RequestMethod.GET)
	public String editUI(ModelMap model,Long tagId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(tagId != null){//判断ID是否存在;
			Tag tag = tagService.findById(tagId);
			if(tag != null){
				
				if(tag.getImage() != null && !"".equals(tag.getImage())){
					returnValue.put("imagePath",fileManage.fileServerAddress(request)+tag.getImage());
				}
				returnValue.put("tag",tag);
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
			}else{
				error.put("tag", "标签不存在");
			}
		}else{
			error.put("tag", "标签Id不能为空");
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		
	}
	/**
	 * 标签   修改
	 */
	@ResponseBody
	@RequestMapping(params="method=edit", method=RequestMethod.POST)
	public String edit(ModelMap model,Tag formbean,BindingResult result,Long tagId,String imagePath,
			MultipartFile images,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		
		Tag tag = null;
		String _imagePath = "";
		String _fileName = "";
		
		if(tagId != null && tagId >0L){
			//取得对象
			tag = tagService.findById(tagId);
			if(tag != null){
				if(images ==null || images.isEmpty()){//如果图片已上传
					if(imagePath != null && !"".equals(imagePath.trim())){
						imagePath = textFilterManage.deleteBindURL(request, imagePath);
						
						//取得文件名称
						String fileName = FileUtil.getName(imagePath);

						//取得路径名称
						String pathName = FileUtil.getFullPath(imagePath);
						
						//旧路径必须为file/topicTag/开头
						if(imagePath.substring(0, 14).equals("file/topicTag/")){
							//新路径名称
							String newPathName = "file/topicTag/";
							
							//如果新旧路径不一致
							if(!newPathName.equals(pathName)){
								
								//复制文件到新路径
								fileManage.copyFile(FileUtil.toRelativePath(imagePath), newPathName);
								//新建文件锁到新路径
								//生成锁文件名称
								String lockFileName = fileName;
								//添加文件锁
								fileManage.addLock("file"+File.separator+"topicTag"+File.separator+"lock"+File.separator,lockFileName);
			
								
							}
							_imagePath = "file/topicTag/";
							_fileName = fileName;
						}
					}
				}
				
				if(images !=null && !images.isEmpty()){	
					//验证文件类型
					List<String> formatList = new ArrayList<String>();
					formatList.add("gif");
					formatList.add("jpg");
					formatList.add("jpeg");
					formatList.add("bmp");
					formatList.add("png");
					formatList.add("svg");
					boolean authentication = FileUtil.validateFileSuffix(images.getOriginalFilename(),formatList);
					if(authentication){
						//取得文件后缀		
						String ext = FileUtil.getExtension(images.getOriginalFilename());
						//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
						String pathDir = "file"+File.separator+"topicTag"+File.separator;
						//构建文件名称
						String fileName = UUIDUtil.getUUID32()+ "." + ext;
						_imagePath = "file/topicTag/";
						_fileName = fileName;
						   
						//生成文件保存目录
						FileUtil.createFolder(pathDir);
						//生成锁文件名称
						String lockFileName = fileName;
						//添加文件锁
						fileManage.addLock("file"+File.separator+"topicTag"+File.separator+"lock"+File.separator,lockFileName);
						  
						//保存文件
						fileManage.writeFile(pathDir, fileName,images.getBytes());
				   }else{
						error.put("images", "图片格式错误");
				   }
				}
			}else{
				error.put("tag", "标签不存在");
			}
		}else{
			error.put("tag", "标签Id不能为空");
		}
		
		
		
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
		if(error.size() == 0){
			Tag new_tag = new Tag(); 
			new_tag.setId(tagId);
			new_tag.setImage(_imagePath+_fileName);
			new_tag.setName(formbean.getName());
			new_tag.setSort(formbean.getSort());
			
			tagService.updateTag(new_tag);
			
			if(tag.getImage() != null && !"".equals(tag.getImage().trim())){
				if(!(_imagePath+_fileName).equals(tag.getImage())){//如果图片有变化
					//删除旧图片
					//替换路径中的..号
					String oldPathFile = FileUtil.toRelativePath(tag.getImage());
					//删除旧文件
					fileManage.deleteFile(FileUtil.toSystemPath(oldPathFile));
				}
			}
			
			//删除图片锁
			if(_imagePath != null && !"".equals(_imagePath.trim())){	
				fileManage.deleteLock("file"+File.separator+"topicTag"+File.separator+"lock"+File.separator,_fileName);		
			}
		}
		
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	/**
	 * 标签   删除
	 * @param model
	 * @param tagId 标签Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=delete", method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String delete(ModelMap model,Long tagId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		if(tagId != null && tagId >0L){
			Tag tag = tagService.findById(tagId);
			if(tag != null){
				
			
				if(tag.getImage() != null && !"".equals(tag.getImage().trim())){
					
					//删除旧图片
					//替换路径中的..号
					String oldPathFile = FileUtil.toRelativePath(tag.getImage());
					//删除旧文件
					fileManage.deleteFile(FileUtil.toSystemPath(oldPathFile));
					
				}
				int i = tagService.deleteTag(tagId);
				if(i >0){
					return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
				}
			}else{
				error.put("tagId", " 标签不存在");
			}
		}else{
			error.put("tagId", " 标签Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	
	
	
	/**
	 * 标签 查询所有标签
	 */
	@ResponseBody
	@RequestMapping(params="method=allTag", method=RequestMethod.GET)
	public String queryAllTag(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
	
		List<Tag> tagList = tagService.findAllTag();
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,tagList));
	}

}
