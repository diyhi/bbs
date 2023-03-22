package cms.web.action.links;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.PageForm;
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.links.Links;
import cms.service.links.LinksService;
import cms.service.setting.SettingService;
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
 * 友情链接
 *
 */
@Controller
@RequestMapping("/control/links/manage") 
public class LinksManageAction {

	@Resource(name = "linksValidator") 
	private Validator validator; 
	@Resource LinksService linksService; 
	@Resource FileManage fileManage;
	@Resource TextFilterManage textFilterManage;
	@Resource SettingService settingService;
	@Resource MessageSource messageSource;
	
	/**
	 * 友情链接   添加界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(Links links,ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
	}
	
	/**
	 * 友情链接  添加
	 */
	@ResponseBody
	@RequestMapping(params="method=add", method=RequestMethod.POST)
	public String add(ModelMap model,Links formbean,BindingResult result,String imagePath,
			MultipartFile images,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<String,Object> error = new HashMap<String,Object>();
		

		Links links = new Links();
		
		Date date = new Date();
		links.setCreateDate(date);//创建时间
		String _imagePath = "";
		String _fileName = "";
		if (images == null || images.isEmpty()) { 
			if(imagePath != null && !"".equals(imagePath.trim())){
				imagePath = textFilterManage.deleteBindURL(request, imagePath);
				
				String fileName = FileUtil.getName(imagePath);
				
				//取得路径名称
				String pathName = FileUtil.getFullPath(imagePath);
				
				//旧路径必须为file/links/开头
				if(imagePath.substring(0, 11).equals("file/links/")){
					//新路径名称
					String newPathName = "file/links/";
					
					//如果新旧路径不一致
					if(!newPathName.equals(pathName)){
						
						//复制文件到新路径
						fileManage.copyFile(FileUtil.toRelativePath(imagePath), newPathName);
						//新建文件锁到新路径
						//生成锁文件名称
						String lockFileName =fileName;
						//添加文件锁
						fileManage.addLock("file"+File.separator+"links"+File.separator+"lock"+File.separator,lockFileName);
						
						
					}
				
					_imagePath = "file/links/";
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
			boolean authentication = FileUtil.validateFileSuffix(images.getOriginalFilename(),formatList);
			if(authentication){
				//取得文件后缀		
				String ext = FileUtil.getExtension(images.getOriginalFilename());
				//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
				String pathDir = "file"+File.separator+"links"+File.separator;
				//构建文件名称
				String fileName = UUIDUtil.getUUID32()+ "." + ext;
				_imagePath = "file/links/";
				_fileName = fileName; 
				  
				//生成文件保存目录
				FileUtil.createFolder(pathDir);
				 
				//生成锁文件名称
				String lockFileName = fileName;
				//添加文件锁
				fileManage.addLock("file"+File.separator+"links"+File.separator+"lock"+File.separator,lockFileName);
				
				//保存文件
				fileManage.writeFile(pathDir, fileName,images.getBytes());
				
			}else{
				error.put("images", "图片格式错误");
			}
        }
		links.setImage(_imagePath+_fileName);
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
			links.setName(formbean.getName());
			links.setWebsite(formbean.getWebsite());
			links.setSort(formbean.getSort());
			
			//写入数据库操作
			linksService.saveLinks(links);
			
			//删除图片锁
			if(_imagePath != null && !"".equals(_imagePath.trim())){	
				fileManage.deleteLock("file"+File.separator+"links"+File.separator+"lock"+File.separator,_fileName);		
			}
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	
	
	/**
	 * 友情链接   修改界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=edit", method=RequestMethod.GET)
	public String editUI(ModelMap model,Integer linksId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(linksId != null){//判断ID是否存在;
			Links links = linksService.findById(linksId);
			if(links != null){
				if(links.getImage() != null && !"".equals(links.getImage())){
					returnValue.put("imagePath",fileManage.fileServerAddress(request)+links.getImage());
				}
				returnValue.put("links",links);//返回消息
			}else{
				error.put("linksId", "友情链接不存在");
			}
		}else{
			error.put("linksId", "友情链接Id不能为空");
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	/**
	 * 友情链接   修改
	 */
	@ResponseBody
	@RequestMapping(params="method=edit", method=RequestMethod.POST)
	public String edit(ModelMap model,Links formbean,BindingResult result,Integer linksId,String imagePath,
			MultipartFile images,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,Object> error = new HashMap<String,Object>();
		
		Links links = new Links();
		links.setId(linksId);
		
		Links old_links = null;
		if(linksId != null && linksId >0){
			old_links = linksService.findById(linksId);
			if(old_links != null){
				String _imagePath = "";
				String _fileName = "";
				if(images ==null || images.isEmpty()){//如果图片已上传
					if(imagePath != null && !"".equals(imagePath.trim())){
						imagePath = textFilterManage.deleteBindURL(request, imagePath);
						
						//取得文件名称
						String fileName = FileUtil.getName(imagePath);

						//取得路径名称
						String pathName = FileUtil.getFullPath(imagePath);
						
						//旧路径必须为file/links/开头
						if(imagePath.substring(0, 11).equals("file/links/")){
							//新路径名称
							String newPathName = "file/links/";
							
							//如果新旧路径不一致
							if(!newPathName.equals(pathName)){
								
								//复制文件到新路径
								fileManage.copyFile(FileUtil.toRelativePath(imagePath), newPathName);
								//新建文件锁到新路径
								//生成锁文件名称
								String lockFileName = fileName;
								//添加文件锁
								fileManage.addLock("file"+File.separator+"links"+File.separator+"lock"+File.separator,lockFileName);
			
								
							}
							_imagePath = "file/links/";
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
					boolean authentication = FileUtil.validateFileSuffix(images.getOriginalFilename(),formatList);
					if(authentication){
						//取得文件后缀		
						String ext = FileUtil.getExtension(images.getOriginalFilename());
						//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
						String pathDir = "file"+File.separator+"links"+File.separator;
						//构建文件名称
						String fileName = UUIDUtil.getUUID32()+ "." + ext;
						_imagePath = "file/links/";
						_fileName = fileName;
						   
						//生成文件保存目录
						FileUtil.createFolder(pathDir);
						//生成锁文件名称
						String lockFileName = fileName;
						//添加文件锁
						fileManage.addLock("file"+File.separator+"links"+File.separator+"lock"+File.separator,lockFileName);
						  
						//保存文件
						fileManage.writeFile(pathDir, fileName,images.getBytes());
				   }else{
						error.put("images", "图片格式错误");
				   }
				}
			
				links.setImage(_imagePath+_fileName);
				
				
				//数据校验
				this.validator.validate(formbean, result); 
				if (result.hasErrors() || error.size() >0) {  
					List<FieldError> fieldErrorList = result.getFieldErrors();
					if(fieldErrorList != null && fieldErrorList.size() >0){
						for(FieldError fieldError : fieldErrorList){
							error.put(fieldError.getField(), messageSource.getMessage(fieldError, null));
						}
					}
				} 
				if(error.size() ==0){
					links.setName(formbean.getName());
					links.setWebsite(formbean.getWebsite());
					links.setSort(formbean.getSort());
					
					//写入数据库操作
					linksService.updateLinks(links);
					
					if(old_links.getImage() != null && !"".equals(old_links.getImage().trim())){
						if(!(_imagePath+_fileName).equals(old_links.getImage())){//如果图片有变化
							//删除旧图片
							//替换路径中的..号
							String oldPathFile = FileUtil.toRelativePath(old_links.getImage());
							//删除旧文件
							fileManage.deleteFile(FileUtil.toSystemPath(oldPathFile));
						}
					}
					
					//删除图片锁
					if(_imagePath != null && !"".equals(_imagePath.trim())){	
						fileManage.deleteLock("file"+File.separator+"links"+File.separator+"lock"+File.separator,_fileName);		
					}
				}
				
			}else{
				error.put("linksId", "友情链接不存在");
			}
		}else{
			error.put("linksId", "友情链接Id不能为空");
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}

	/**
	 * 友情链接   删除
	 * @param model
	 * @param linksId 友情链接Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=delete", method=RequestMethod.POST)
	public String delete(ModelMap model,Integer linksId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,Object> error = new HashMap<String,Object>();
		
		if(linksId != null && linksId >0){
			Links links = linksService.findById(linksId);
			if(links != null){
				int i = linksService.deleteLinks(linksId);
				if(links.getImage() != null && !"".equals(links.getImage().trim())){
					
					//删除旧图片
					//替换路径中的..号
					String oldPathFile = FileUtil.toRelativePath(links.getImage());
					//删除旧文件
					fileManage.deleteFile(FileUtil.toSystemPath(oldPathFile));
					
				}
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
			}else{
				error.put("linksId", "友情链接不存在");
			}
		}else{
			error.put("linksId", "友情链接Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	

	
}
