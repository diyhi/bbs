package cms.web.action.help;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.help.HelpType;
import cms.service.help.HelpTypeService;
import cms.service.setting.SettingService;
import cms.utils.RedirectPath;
import cms.web.action.SystemException;
import cms.web.action.fileSystem.FileManage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
	
	
	/**
	 * 帮助分类   添加界面显示
	 */
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(HelpType helpType,Long parentId,ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		if(parentId != null){//判断父类ID是否存在;
			HelpType pt = helpTypeService.findById(parentId);
			if(pt != null){
				model.addAttribute("parentName",pt.getName());//返回消息
				
				
				Map<Long,String> navigation = new LinkedHashMap<Long,String>();
				
				List<HelpType> allParentHelpTypeList = helpTypeService.findAllParentById(pt);
				for(HelpType p : allParentHelpTypeList){
					navigation.put(p.getId(), p.getName());
				}
				navigation.put(pt.getId(), pt.getName());
				model.addAttribute("navigation", navigation);//分类导航
			}

			
		}
		
		return "jsp/help/add_helpType";
	}
	
	/**
	 * 帮助分类  添加
	 */
	@RequestMapping(params="method=add", method=RequestMethod.POST)
	public String add(ModelMap model,HelpType formbean,BindingResult result,Long parentId
			) throws Exception {
		
		HelpType parentHelpType = null;
		if(parentId != null && parentId >0L){//判断父类ID是否存在;
			//取得父对象
			parentHelpType = helpTypeService.findById(parentId);
		}
		
		
		//数据校验
		this.validator.validate(formbean, result); 
		if (result.hasErrors()) { 
			if(parentId != null && parentId >0L){//判断父类ID是否存在;
				
				if(parentHelpType != null){
					model.addAttribute("parentName",parentHelpType.getName());//返回消息
					
					
					Map<Long,String> navigation = new LinkedHashMap<Long,String>();
					
					List<HelpType> allParentHelpTypeList = helpTypeService.findAllParentById(parentHelpType);
					for(HelpType p : allParentHelpTypeList){
						navigation.put(p.getId(), p.getName());
					}
					navigation.put(parentHelpType.getId(), parentHelpType.getName());
					model.addAttribute("navigation", navigation);//分类导航
				}
			}
			
			
			
			
			model.addAttribute("parentHelpType",parentHelpType);//返回消息
			return "jsp/help/add_helpType";
		} 
		HelpType type = new HelpType(); 
		type.setId(helpTypeManage.nextNumber());
		type.setName(formbean.getName());
		
		type.setSort(formbean.getSort());
		if(parentHelpType != null){
			if(parentHelpType.getParentIdGroup().length() >20){
				throw new SystemException("分类已达到最大层数,添加失败");
			}
			type.setParentId(formbean.getParentId());
			type.setParentIdGroup(parentHelpType.getParentIdGroup()+formbean.getParentId()+",");
		}
			
		helpTypeService.saveType(type);
		
		model.addAttribute("message","添加类别成功");//返回消息
		model.addAttribute("urladdress", RedirectPath.readUrl("control.helpType.list")+"?parentId="+type.getParentId());
		return "jsp/common/message";
	}
	
	
	/**
	 * 帮助分类   修改界面显示
	 */
	@RequestMapping(params="method=edit", method=RequestMethod.GET)
	public String editUI(ModelMap model,Long typeId,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		if(typeId != null){//判断父类ID是否存在;
			HelpType helpType = helpTypeService.findById(typeId);
			if(helpType != null){
				model.addAttribute("helpType",helpType);//返回消息
			}
			Map<Long,String> navigation = new LinkedHashMap<Long,String>();
			List<HelpType> allParentHelpTypeList = helpTypeService.findAllParentById(helpType);
			for(HelpType p : allParentHelpTypeList){
				navigation.put(p.getId(), p.getName());
			}
			model.addAttribute("navigation", navigation);//分类导航
		}
		return "jsp/help/edit_helpType";
	}
	/**
	 * 帮助分类   修改
	 */
	@RequestMapping(params="method=edit", method=RequestMethod.POST)
	public String edit(ModelMap model,HelpType formbean,BindingResult result,Long typeId,PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		HelpType helpType = null;
		if(typeId != null && typeId >0L){
			//取得对象
			helpType = helpTypeService.findById(typeId);
			if(helpType == null){
				throw new SystemException("分类不存在");
			}
		}
		
		//数据校验
		this.validator.validate(formbean, result); 
		if (result.hasErrors()) {  	
			Map<Long,String> navigation = new LinkedHashMap<Long,String>();
			List<HelpType> allParentHelpTypeList = helpTypeService.findAllParentById(helpType);
			for(HelpType p : allParentHelpTypeList){
				navigation.put(p.getId(), p.getName());
			}
			model.addAttribute("navigation", navigation);//分类导航
	
			return "jsp/help/edit_helpType";
		} 
		
		HelpType type = new HelpType(); 
		type.setId(typeId);
		type.setName(formbean.getName());
		type.setSort(formbean.getSort());
		
		helpTypeService.updateHelpType(type);
		
		model.addAttribute("message","修改类别成功");//返回消息
		model.addAttribute("urladdress", RedirectPath.readUrl("control.helpType.list")+"?parentId="+helpType.getParentId()+"&page="+pageForm.getPage());
		return "jsp/common/message";
	}
	/**
	 * 帮助分类   删除
	 */
	@RequestMapping(params="method=delete", method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String delete(ModelMap model,Long typeId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
	
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
				return"1";
				
			}
		}
		return"0";
	}
	
	/**
	 * 帮助分类   合并界面显示
	 */
	@RequestMapping(params="method=merger", method=RequestMethod.GET)
	public String mergerUI(ModelMap model,Long typeId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(typeId != null){//判断父类ID是否存在;
			HelpType helpType = helpTypeService.findById(typeId);
			if(helpType != null){
				model.addAttribute("helpType",helpType);//返回消息
				Map<Long,String> navigation = new LinkedHashMap<Long,String>();
				List<HelpType> parentHelpTypeList = helpTypeService.findAllParentById(helpType);
				for(HelpType p : parentHelpTypeList){
					navigation.put(p.getId(), p.getName());
				}
				model.addAttribute("navigation", navigation);//分类导航
			}
			
		}
		return "jsp/help/merger_helpType";
	}
	/**
	 * 帮助分类   合并
	 * @param typeId 主分类Id
	 * @param mergerTypeId 合并分类Id
	 */
	@RequestMapping(params="method=merger", method=RequestMethod.POST)
	public String merger(ModelMap model,Long typeId,Long mergerTypeId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		HelpType helpType = null;
		if(typeId != null && typeId >0L ){
			helpType = helpTypeService.findById(typeId);
			if(helpType != null){
				if(helpType.getChildNodeNumber().equals(0)){
					if(typeId.equals(mergerTypeId)){
						throw new SystemException("不能选择同一节点");
					}
					if(mergerTypeId != null && mergerTypeId >0L){
						HelpType merger_productType = helpTypeService.findById(mergerTypeId);
						if(merger_productType != null){

							if(merger_productType.getChildNodeNumber().equals(0)){
								
								helpTypeService.mergerHelpType(typeId,merger_productType);

							}else{
								throw new SystemException("请选择分类最后一个节点");
							}
						}else{
							throw new SystemException("请选择分类");
						}
					}else{
						throw new SystemException("请选择分类");
					}
				}else{
					throw new SystemException("分类最后一个节点才能合并");
				}
			}else{
				throw new SystemException("分类不存在");
			}
		}else{
			throw new SystemException("分类不存在");
		}
	
		model.addAttribute("message","合并分类成功");//返回消息
		model.addAttribute("urladdress", RedirectPath.readUrl("control.helpType.list")+"?parentId="+helpType.getParentId());
		return "jsp/common/message";
	}
	
	/**
	 * 帮助分类管理 分类选择分页显示
	 * @param pageForm
	 * @param parentId 父ID
	 * @param module 模块
	 * @param tableName div层对应的表名
	 */
	@RequestMapping(params="method=helpTypePageSelect", method=RequestMethod.GET)
	public String helpTypePageSelect(ModelMap model,PageForm pageForm,Long parentId,String module,String tableName,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
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
		model.addAttribute("pageView", pageView);
		model.addAttribute("tableName", tableName);
		
		
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
			model.addAttribute("navigation", navigation);
		}
		
		if(module != null && "forum".equals(module)){//版块模块使用
			return "jsp/help/ajax_helpTypePageSelect_forum";
		}else{
			return "jsp/help/ajax_helpTypePageSelect";
		}
	}
	
	
	/**
	 * 帮助分类管理 分类选择分页显示(移动功能使用)
	 * @param pageForm
	 * @param parentId 父ID
	 */
	@RequestMapping(params="method=helpTypePageSelect_move", method=RequestMethod.GET)
	public String productTypePageSelect_move(ModelMap model,PageForm pageForm,Long parentId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
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
		model.addAttribute("pageView", pageView);
	
		
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
			model.addAttribute("navigation", navigation);
		}

		return "jsp/help/ajax_helpTypePageSelect_move";
	}
}
