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
import cms.bean.links.Links;
import cms.service.links.LinksService;
import cms.service.setting.SettingService;
import cms.utils.RedirectPath;
import cms.utils.UUIDUtil;
import cms.web.action.FileManage;
import cms.web.action.SystemException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	@Resource SettingService settingService;
	
	
	/**
	 * 友情链接   添加界面显示
	 */
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(Links links,ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		return "jsp/links/add_links";
	}
	
	/**
	 * 友情链接  添加
	 */
	@RequestMapping(params="method=add", method=RequestMethod.POST)
	public String add(ModelMap model,Links formbean,BindingResult result,String imagePath,
			@RequestParam()MultipartFile images,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<String,String> error = new HashMap<String,String>();
		

		Links links = new Links();
		
		Date date = new Date();
		links.setCreateDate(date);//创建时间
		String _imagePath = "";
		String _fileName = "";
		if (images == null || images.isEmpty()) { 
			if(imagePath != null && !"".equals(imagePath.trim())){
				
				String fileName = fileManage.getName(imagePath);
				
				//取得路径名称
				String pathName = fileManage.getFullPath(imagePath);
				
				//旧路径必须为file/links/开头
				if(imagePath.substring(0, 11).equals("file/links/")){
					//新路径名称
					String newPathName = "file/links/";
					
					//如果新旧路径不一致
					if(!newPathName.equals(pathName)){
						
						//复制文件到新路径
						fileManage.copyFile(imagePath, newPathName);
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
			boolean authentication = fileManage.validateFileSuffix(images.getOriginalFilename(),formatList);
			if(authentication){
				//取得文件后缀		
				String ext = fileManage.getExtension(images.getOriginalFilename());
				//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
				String pathDir = "file"+File.separator+"links"+File.separator;
				//构建文件名称
				String fileName = UUIDUtil.getUUID32()+ "." + ext;
				_imagePath = "file/links/";
				_fileName = fileName; 
				  
				//生成文件保存目录
				fileManage.createFolder(pathDir);
				 
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
			model.addAttribute("imagePath",_imagePath+_fileName);
			model.addAttribute("error",error);
			return "jsp/links/add_links";
		} 
		
		
		links.setName(formbean.getName());
		links.setWebsite(formbean.getWebsite());
		links.setSort(formbean.getSort());
		
		//写入数据库操作
		linksService.saveLinks(links);
		
		//删除图片锁
		if(_imagePath != null && !"".equals(_imagePath.trim())){	
			fileManage.deleteLock("file"+File.separator+"links"+File.separator+"lock"+File.separator,_fileName);		
		}
		
		model.addAttribute("message","添加友情链接成功");//返回消息
		model.addAttribute("urladdress", RedirectPath.readUrl("control.links.list"));
		return "jsp/common/message";
	}
	
	
	/**
	 * 友情链接   修改界面显示
	 */
	@RequestMapping(params="method=edit", method=RequestMethod.GET)
	public String editUI(ModelMap model,Integer linksId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		if(linksId != null){//判断ID是否存在;
			Links links = linksService.findById(linksId);
			if(links != null){
				if(links.getImage() != null && !"".equals(links.getImage())){
					model.addAttribute("imagePath",links.getImage());
				}
				model.addAttribute("links",links);//返回消息
			}
		}
		return "jsp/links/edit_links";
	}
	/**
	 * 友情链接   修改
	 */
	@RequestMapping(params="method=edit", method=RequestMethod.POST)
	public String edit(ModelMap model,Links formbean,BindingResult result,Integer linksId,String imagePath,
			@RequestParam()MultipartFile images,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,String> error = new HashMap<String,String>();
		
		Links links = new Links();
		links.setId(linksId);
		
		Links old_links = null;
		if(linksId != null && linksId >0){
			old_links = linksService.findById(linksId);
			if(old_links != null){
				
			}else{
				throw new SystemException("友情链接不存在！");
			}
		}else{
			throw new SystemException("参数错误！");
		}
		String _imagePath = "";
		String _fileName = "";
		if (images == null || images.isEmpty()) { 
			if(imagePath != null && !"".equals(imagePath.trim())){
				//取得文件名称
				String fileName = fileManage.getName(imagePath);

				//取得路径名称
				String pathName = fileManage.getFullPath(imagePath);
				
				//旧路径必须为file/links/开头
				if(imagePath.substring(0, 11).equals("file/links/")){
					//新路径名称
					String newPathName = "file/links/";
					
					//如果新旧路径不一致
					if(!newPathName.equals(pathName)){
						
						//复制文件到新路径
						fileManage.copyFile(imagePath, newPathName);
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
        }else{
        	//验证文件类型
			List<String> formatList = new ArrayList<String>();
			formatList.add("gif");
			formatList.add("jpg");
			formatList.add("jpeg");
			formatList.add("bmp");
			formatList.add("png");
			boolean authentication = fileManage.validateFileSuffix(images.getOriginalFilename(),formatList);
			if(authentication){
				//取得文件后缀		
				String ext = fileManage.getExtension(images.getOriginalFilename());
				//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
				String pathDir = "file"+File.separator+"links"+File.separator;
				//构建文件名称
				String fileName = UUIDUtil.getUUID32()+ "." + ext;
				_imagePath = "file/links/";
				_fileName = fileName;
				   
				//生成文件保存目录
				fileManage.createFolder(pathDir);
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
			model.addAttribute("imagePath",_imagePath+_fileName);
			model.addAttribute("error",error);
			return "jsp/links/edit_links";
		} 
		
		links.setName(formbean.getName());
		links.setWebsite(formbean.getWebsite());
		links.setSort(formbean.getSort());
		
		//写入数据库操作
		linksService.updateLinks(links);
		
		if(old_links.getImage() != null && !"".equals(old_links.getImage().trim())){
			if(!(_imagePath+_fileName).equals(old_links.getImage())){//如果图片有变化
				//删除旧图片
				//替换路径中的..号
				String oldPathFile = fileManage.toRelativePath(old_links.getImage());
				//删除旧文件
				fileManage.deleteFile(fileManage.toSystemPath(oldPathFile));
			}
		}
		
		//删除图片锁
		if(_imagePath != null && !"".equals(_imagePath.trim())){	
			fileManage.deleteLock("file"+File.separator+"links"+File.separator+"lock"+File.separator,_fileName);		
		}
		
		model.addAttribute("message","修改友情链接成功");//返回消息
		model.addAttribute("urladdress", RedirectPath.readUrl("control.links.list")+"?page="+pageForm.getPage());//返回转向地址
		return "jsp/common/message";
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
	@RequestMapping(params="method=delete", method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String delete(ModelMap model,Integer linksId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
	
		
		if(linksId != null && linksId >0){
			Links links = linksService.findById(linksId);
			if(links != null){
				int i = linksService.deleteLinks(linksId);
				if(links.getImage() != null && !"".equals(links.getImage().trim())){
					
					//删除旧图片
					//替换路径中的..号
					String oldPathFile = fileManage.toRelativePath(links.getImage());
					//删除旧文件
					fileManage.deleteFile(fileManage.toSystemPath(oldPathFile));
					
				}
				return "1";
			}
		}
		return "0";
	}
	

	
}
