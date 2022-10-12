package cms.web.action.help;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.help.HelpType;
import cms.service.help.HelpTypeService;
import cms.service.setting.SettingService;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.UUIDUtil;
import cms.web.action.TextFilterManage;
import cms.web.action.fileSystem.FileManage;

import org.apache.commons.lang3.StringUtils;
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
 * 帮助分类
 *
 */
@Controller
@RequestMapping("/control/helpType/manage") 
public class HelpTypeManageAction {

	@Resource(name = "helpTypeValidator") 
	private Validator validator; 
	@Resource HelpTypeService helpTypeService; 
	@Resource HelpTypeManage helpTypeManage;
	@Resource FileManage fileManage;
	
	@Resource SettingService settingService;
	@Resource MessageSource messageSource;
	@Resource TextFilterManage textFilterManage;
	
	/**
	 * 帮助分类   添加界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(HelpType helpType,Long parentId,ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(parentId != null && parentId >0L){//判断父类ID是否存在;
			HelpType pt = helpTypeService.findById(parentId);
			if(pt != null){
				returnValue.put("parentHelpType",pt);//返回消息
				
				Map<Long,String> navigation = new LinkedHashMap<Long,String>();
				
				List<HelpType> allParentHelpTypeList = helpTypeService.findAllParentById(pt);
				for(HelpType p : allParentHelpTypeList){
					navigation.put(p.getId(), p.getName());
				}
				navigation.put(pt.getId(), pt.getName());
				returnValue.put("navigation", navigation);//分类导航
			}else{
				error.put("parentId", "父类不存在");
			}

			
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	
	/**
	 * 帮助分类  添加
	 */
	@ResponseBody
	@RequestMapping(params="method=add", method=RequestMethod.POST)
	public String add(ModelMap model,HelpType formbean,BindingResult result,Long parentId,String imagePath,MultipartFile images,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		
		HelpType parentHelpType = null;
		if(parentId != null && parentId >0L){//判断父类ID是否存在;
			//取得父对象
			parentHelpType = helpTypeService.findById(parentId);
			if(parentHelpType == null){
				error.put("parentId", "父类不存在");
			}
		}
		HelpType type = new HelpType(); 
		
		String _imagePath = "";
		String _fileName = "";
		if (images == null || images.isEmpty()) { 
			if(imagePath != null && !"".equals(imagePath.trim())){
				imagePath = textFilterManage.deleteBindURL(request, imagePath);
				
				String fileName = FileUtil.getName(imagePath);
				
				//取得路径名称
				String pathName = FileUtil.getFullPath(imagePath);
				
				//旧路径必须为file/helpType/开头
				if(imagePath.substring(0, 14).equals("file/helpType/")){
					//新路径名称
					String newPathName = "file/helpType/";
					
					//如果新旧路径不一致
					if(!newPathName.equals(pathName)){
						
						//复制文件到新路径
						fileManage.copyFile(FileUtil.toRelativePath(imagePath), newPathName);
						//新建文件锁到新路径
						//生成锁文件名称
						String lockFileName =fileName;
						//添加文件锁
						fileManage.addLock("file"+File.separator+"helpType"+File.separator+"lock"+File.separator,lockFileName);
						
						
					}
				
					_imagePath = "file/helpType/";
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
				String pathDir = "file"+File.separator+"helpType"+File.separator;
				//构建文件名称
				String fileName = UUIDUtil.getUUID32()+ "." + ext;
				_imagePath = "file/helpType/";
				_fileName = fileName; 
				  
				//生成文件保存目录
				FileUtil.createFolder(pathDir);
				 
				//生成锁文件名称
				String lockFileName = fileName;
				//添加文件锁
				fileManage.addLock("file"+File.separator+"helpType"+File.separator+"lock"+File.separator,lockFileName);
				
				//保存文件
				fileManage.writeFile(pathDir, fileName,images.getBytes());
				
			}else{
				error.put("images", "图片格式错误");
			}
        }
		type.setImage(_imagePath+_fileName);
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
			
			type.setId(helpTypeManage.nextNumber());
			type.setName(formbean.getName());
			type.setDescription(formbean.getDescription());
			
			type.setSort(formbean.getSort());
			if(parentHelpType != null){
				if(parentHelpType.getParentIdGroup().length() >180){
					error.put("helpType", "分类已达到最大层数,添加失败");
				}
				type.setParentId(formbean.getParentId());
				type.setParentIdGroup(parentHelpType.getParentIdGroup()+formbean.getParentId()+",");
			}
				
			
			
			
		}
		if(error.size() ==0){
			helpTypeService.saveType(type);
			//删除图片锁
			if(_imagePath != null && !"".equals(_imagePath.trim())){	
				fileManage.deleteLock("file"+File.separator+"helpType"+File.separator+"lock"+File.separator,_fileName);		
			}
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	
	/**
	 * 帮助分类   修改界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=edit", method=RequestMethod.GET)
	public String editUI(ModelMap model,Long typeId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(typeId != null){//判断父类ID是否存在;
			HelpType helpType = helpTypeService.findById(typeId);
			if(helpType != null){
				if(helpType.getImage() != null && !"".equals(helpType.getImage())){
					returnValue.put("imagePath",fileManage.fileServerAddress(request)+helpType.getImage());
				}
				returnValue.put("helpType",helpType);//返回消息
			}
			Map<Long,String> navigation = new LinkedHashMap<Long,String>();
			List<HelpType> allParentHelpTypeList = helpTypeService.findAllParentById(helpType);
			for(HelpType p : allParentHelpTypeList){
				navigation.put(p.getId(), p.getName());
			}
			returnValue.put("navigation", navigation);//分类导航
		}else{
			error.put("typeId", "分类Id不能为空");
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	/**
	 * 帮助分类   修改
	 */
	@ResponseBody
	@RequestMapping(params="method=edit", method=RequestMethod.POST)
	public String edit(ModelMap model,HelpType formbean,BindingResult result,Long typeId,String imagePath,
			MultipartFile images,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
				
		HelpType helpType = null;
		String _imagePath = "";
		String _fileName = "";
		
		if(typeId != null && typeId >0L){
			//取得对象
			helpType = helpTypeService.findById(typeId);
			if(helpType != null){
				
				if(images ==null || images.isEmpty()){//如果图片已上传
					if(imagePath != null && !"".equals(imagePath.trim())){
						imagePath = textFilterManage.deleteBindURL(request, imagePath);
						
						//取得文件名称
						String fileName = FileUtil.getName(imagePath);

						//取得路径名称
						String pathName = FileUtil.getFullPath(imagePath);
						
						//旧路径必须为file/helpType/开头
						if(imagePath.substring(0, 14).equals("file/helpType/")){
							//新路径名称
							String newPathName = "file/helpType/";
							
							//如果新旧路径不一致
							if(!newPathName.equals(pathName)){
								
								//复制文件到新路径
								fileManage.copyFile(FileUtil.toRelativePath(imagePath), newPathName);
								//新建文件锁到新路径
								//生成锁文件名称
								String lockFileName = fileName;
								//添加文件锁
								fileManage.addLock("file"+File.separator+"helpType"+File.separator+"lock"+File.separator,lockFileName);
			
								
							}
							_imagePath = "file/helpType/";
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
						String pathDir = "file"+File.separator+"helpType"+File.separator;
						//构建文件名称
						String fileName = UUIDUtil.getUUID32()+ "." + ext;
						_imagePath = "file/helpType/";
						_fileName = fileName;
						   
						//生成文件保存目录
						FileUtil.createFolder(pathDir);
						//生成锁文件名称
						String lockFileName = fileName;
						//添加文件锁
						fileManage.addLock("file"+File.separator+"helpType"+File.separator+"lock"+File.separator,lockFileName);
						  
						//保存文件
						fileManage.writeFile(pathDir, fileName,images.getBytes());
				   }else{
						error.put("images", "图片格式错误");
				   }
				}
			
				
				

			}else{
				error.put("typeId", "分类不存在");
			}
		}else{
			error.put("typeId", "分类Id不能为空");
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
		
		
		if(error.size() ==0){
			HelpType type = new HelpType(); 
			type.setId(typeId);
			type.setName(formbean.getName());
			type.setSort(formbean.getSort());
			type.setImage(_imagePath+_fileName);
			type.setDescription(formbean.getDescription());
			helpTypeService.updateHelpType(type);
			
			if(helpType.getImage() != null && !"".equals(helpType.getImage().trim())){
				if(!(_imagePath+_fileName).equals(helpType.getImage())){//如果图片有变化
					//删除旧图片
					//替换路径中的..号
					String oldPathFile = FileUtil.toRelativePath(helpType.getImage());
					//删除旧文件
					fileManage.deleteFile(FileUtil.toSystemPath(oldPathFile));
				}
			}
			
			//删除图片锁
			if(_imagePath != null && !"".equals(_imagePath.trim())){	
				fileManage.deleteLock("file"+File.separator+"helpType"+File.separator+"lock"+File.separator,_fileName);		
			}
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	/**
	 * 帮助分类   删除
	 */
	@ResponseBody
	@RequestMapping(params="method=delete", method=RequestMethod.POST)
	public String delete(ModelMap model,Long typeId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		
		if(typeId != null && typeId >0L){
			HelpType helpType = helpTypeService.findById(typeId);
			if(helpType != null){
				//删除目录组
				StringBuffer delete_dirGroup = new StringBuffer("");
				
				delete_dirGroup.append(","+helpType.getId()).append(helpType.getMergerTypeId());
				
				String idGroup = helpType.getParentIdGroup()+helpType.getId()+",";
				
				//读取当前id下所有分类
				List<HelpType> productTypeList = helpTypeService.findChildHelpTypeByIdGroup(idGroup);
				for(HelpType it : productTypeList){
					
					delete_dirGroup.append(","+it.getId()).append(it.getMergerTypeId());
					
					if(it.getImage() != null && !"".equals(it.getImage().trim())){
						
						//删除旧图片
						//替换路径中的..号
						String oldPathFile = FileUtil.toRelativePath(it.getImage());
						//删除旧文件
						fileManage.deleteFile(FileUtil.toSystemPath(oldPathFile));
						
					}
					
				}
				if(helpType.getImage() != null && !"".equals(helpType.getImage().trim())){
					
					//删除旧图片
					//替换路径中的..号
					String oldPathFile = FileUtil.toRelativePath(helpType.getImage());
					//删除旧文件
					fileManage.deleteFile(FileUtil.toSystemPath(oldPathFile));
					
				}
				
				String[] old_typeId_array = delete_dirGroup.toString().split(",");
				if(old_typeId_array != null && old_typeId_array.length >0){
					for(String old_typeId :old_typeId_array){
						if(old_typeId != null && !"".equals(old_typeId)){
							
							//清空目录
							Boolean state_ = fileManage.removeDirectory("file"+File.separator+"help"+File.separator+old_typeId+File.separator);
							if(state_ != null && state_ == false){
								//创建删除失败目录文件
								fileManage.failedStateFile("file"+File.separator+"help"+File.separator+"lock"+File.separator+"#"+old_typeId);
							}
							
							
						}
					}
				}
				
				int i = helpTypeService.deleteHelpType(helpType);
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
			}else{
				error.put("typeId", "分类不存在");
			}
		}else{
			error.put("typeId", "分类Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 帮助分类   合并界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=merger", method=RequestMethod.GET)
	public String mergerUI(ModelMap model,Long typeId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		if(typeId != null){//判断父类ID是否存在;
			HelpType helpType = helpTypeService.findById(typeId);
			if(helpType != null){
				returnValue.put("helpType",helpType);//返回消息
				Map<Long,String> navigation = new LinkedHashMap<Long,String>();
				List<HelpType> parentHelpTypeList = helpTypeService.findAllParentById(helpType);
				for(HelpType p : parentHelpTypeList){
					navigation.put(p.getId(), p.getName());
				}
				returnValue.put("navigation", navigation);//分类导航
			}else{
				error.put("typeId", "分类不存在");
			}
			
		}else{
			error.put("typeId", "分类Id不能为空");
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	/**
	 * 帮助分类   合并
	 * @param typeId 主分类Id
	 * @param mergerTypeId 合并分类Id
	 */
	@ResponseBody
	@RequestMapping(params="method=merger", method=RequestMethod.POST)
	public String merger(ModelMap model,Long typeId,Long mergerTypeId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		
		HelpType helpType = null;
		if(typeId != null && typeId >0L ){
			helpType = helpTypeService.findById(typeId);
			if(helpType != null){
				if(helpType.getChildNodeNumber().equals(0)){
					if(typeId.equals(mergerTypeId)){
						error.put("typeId", "不能选择同一节点");
					}
					if(mergerTypeId != null && mergerTypeId >0L){
						HelpType merger_productType = helpTypeService.findById(mergerTypeId);
						if(merger_productType != null){

							if(merger_productType.getChildNodeNumber().equals(0)){
								if(error.size() ==0){
									helpTypeService.mergerHelpType(typeId,merger_productType);
									return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
								}
								

							}else{
								error.put("typeId", "请选择分类最后一级节点");
							}
						}else{
							error.put("typeId", "请选择分类");
						}
					}else{
						error.put("typeId", "合并Id不存在");
					}
				}else{
					error.put("typeId", "分类最后一级节点才能合并");
				}
			}else{
				error.put("typeId", "分类不存在");
			}
		}else{
			error.put("typeId", "分类Id不能为空");
		}
	
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 帮助分类管理 分类选择分页显示
	 * @param pageForm
	 * @param parentId 父ID
	 */
	@ResponseBody
	@RequestMapping(params="method=helpTypePageSelect", method=RequestMethod.GET)
	public String helpTypePageSelect(ModelMap model,PageForm pageForm,Long parentId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();
	
		//如果所属父类有值
		if(parentId != null && parentId >0L){
			jpql.append(" and o.parentId=?"+ (params.size()+1));//所属父类的ID;(params.size()+1)是为了和下面的条件参数兼容
			params.add(parentId);//设置o.parentId=?2参数
		}else{//如果没有父类
			jpql.append(" and o.parentId=?"+ (params.size()+1));
			params.add(0L);
		}
		
		PageView<HelpType> pageView = new PageView<HelpType>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("sort", "desc");//根据sort字段降序排序
		
		//删除第一个and
		String jpql_str = StringUtils.difference(" and", jpql.toString());
		//调用分页算法类
		QueryResult<HelpType> qr = helpTypeService.getScrollData(HelpType.class, firstindex, pageView.getMaxresult(), jpql_str, params.toArray(),orderby);

		pageView.setQueryResult(qr);
		returnValue.put("pageView", pageView);
		
		
		//分类导航
		if(parentId != null && parentId >0L){
			Map<Long,String> navigation = new LinkedHashMap<Long,String>();
			HelpType helpType = helpTypeService.findById(parentId);
			if(helpType != null){
				List<HelpType> allParentHelpTypeList = helpTypeService.findAllParentById(helpType);
				for(HelpType p : allParentHelpTypeList){
					navigation.put(p.getId(), p.getName());
				}
				navigation.put(helpType.getId(), helpType.getName());
			}
			returnValue.put("navigation", navigation);
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	
	
	/**
	 * 帮助分类管理 分类选择分页显示(移动功能使用)
	 * @param pageForm
	 * @param parentId 父ID
	 */
	@ResponseBody
	@RequestMapping(params="method=helpTypePageSelect_move", method=RequestMethod.GET)
	public String productTypePageSelect_move(ModelMap model,PageForm pageForm,Long parentId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();
		
		//如果所属父类有值
		if(parentId != null && parentId >0L){
			jpql.append(" and o.parentId=?"+ (params.size()+1));//所属父类的ID;(params.size()+1)是为了和下面的条件参数兼容
			params.add(parentId);//设置o.parentId=?2参数
		}else{//如果没有父类
			jpql.append(" and o.parentId=?"+ (params.size()+1));
			params.add(0L);
		}
		
		PageView<HelpType> pageView = new PageView<HelpType>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("sort", "desc");//根据sort字段降序排序
		
		//删除第一个and
		String jpql_str = StringUtils.difference(" and", jpql.toString());
		//调用分页算法类
		QueryResult<HelpType> qr = helpTypeService.getScrollData(HelpType.class, firstindex, pageView.getMaxresult(), jpql_str, params.toArray(),orderby);

		pageView.setQueryResult(qr);
		returnValue.put("pageView", pageView);
	
		
		//分类导航
		if(parentId != null && parentId >0L){
			Map<Long,String> navigation = new LinkedHashMap<Long,String>();
			HelpType helpType = helpTypeService.findById(parentId);
			if(helpType != null){
				List<HelpType> allParentHelpTypeList = helpTypeService.findAllParentById(helpType);
				for(HelpType p : allParentHelpTypeList){
					navigation.put(p.getId(), p.getName());
				}
				navigation.put(helpType.getId(), helpType.getName());
			}
			returnValue.put("navigation", navigation);
		}

		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
}
