package cms.web.action.template;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.PageForm;
import cms.bean.template.Column;
import cms.bean.template.Forum;
import cms.bean.template.Layout;
import cms.bean.template.Templates;
import cms.service.template.TemplateService;
import cms.utils.Coding;
import cms.utils.DisablePath;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.PathUtil;
import cms.utils.RedirectPath;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import cms.web.action.SystemException;
import cms.web.action.fileSystem.localImpl.LocalFileManage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;



/**
 * 布局管理 
 *
 */
@Controller
@RequestMapping("/control/layout/manage") 
public class LayoutManageAction {
	//注入业务bean
	@Resource(name="templateServiceBean")
	private TemplateService templateService;//通过接口引用代理返回的对象
	@Resource TemplateManage templateManage;
	@Resource LayoutManage layoutManage;
	@Resource ColumnManage columnManage;
	@Resource LocalFileManage localFileManage;
	
	//?  匹配任何单字符
	//*  匹配0或者任意数量的字符
	//** 匹配0或者更多的目录
	private PathMatcher matcher = new AntPathMatcher(); 
	
	/**
	 * 模板管理 添加布局界面显示
	 */
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(ModelMap model,Layout layout,String dirName)throws Exception {
		
		//根据模板目录名称查询模板
		if(dirName != null && !"".equals(dirName.trim())){
			Templates templates = templateService.findTemplatebyDirName(dirName);
			model.addAttribute("templates", templates);
			
			List<Layout> default_layoutList = templateManage.newLayout(dirName);
			
			List<Layout> layoutList = templateService.findLayout(dirName, 1);
			if(layoutList != null && layoutList.size() >0){
				Iterator<Layout> default_layoutIter = default_layoutList.iterator();  
			    while (default_layoutIter.hasNext()) { 
			    	Layout default_layout = default_layoutIter.next();  

					for(Layout l : layoutList){
						if(default_layout.getLayoutFile().equals(l.getLayoutFile())){
							default_layoutIter.remove();
							break;
						}
					}
				}
			}
			
			
			model.addAttribute("default_layoutList", default_layoutList);
		}
		return "jsp/template/add_layout";
	}
	/**
	 * 模板管理 添加布局
	 */
	@RequestMapping(params="method=add", method=RequestMethod.POST)
	public String add(ModelMap model,Layout formbean,BindingResult result,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String,String> error = new HashMap<String,String>();
		
		Layout layout = new Layout();
		layout.setId(UUIDUtil.getUUID32());
		if(!formbean.getType().equals(1)){//默认页不验证
			if(formbean.getName() == null || "".equals(formbean.getName().trim())){
				error.put("name", "布局名称不能为空！");
			}
		}
		
		layout.setName(formbean.getName());
		layout.setType(formbean.getType());
		layout.setDirName(formbean.getDirName());
		
		//根据模板目录名称查询模板
		if(formbean.getDirName() != null && !"".equals(formbean.getDirName().trim())){
			Templates templates = templateService.findTemplatebyDirName(formbean.getDirName().trim());
			model.addAttribute("templates", templates);
		}
		List<Layout> default_layoutList = null;
		
		//取得路径名称
		String pathName = formbean.getDirName();
		if(layout.getType().equals(1)){//1.默认页
			
			
			default_layoutList = templateManage.newLayout(layout.getDirName());
			
			List<Layout> layoutList = templateService.findLayout(layout.getDirName(), 1);
			if(layoutList != null && layoutList.size() >0){
				Iterator<Layout> default_layoutIter = default_layoutList.iterator();  
			    while (default_layoutIter.hasNext()) { 
			    	Layout default_layout = default_layoutIter.next();  

					for(Layout l : layoutList){
						if(default_layout.getLayoutFile().equals(l.getLayoutFile())){
							default_layoutIter.remove();
							break;
						}
					}
				}
			}
			if(default_layoutList.size() > 0){
				if(formbean.getLayoutFile() != null && !"".equals(formbean.getLayoutFile().trim())){
					for(Layout default_layout :default_layoutList){
						if(default_layout.getLayoutFile().equals(formbean.getLayoutFile())){
							
							layout.setName(default_layout.getName());
							layout.setDirName(default_layout.getDirName());
							layout.setLayoutFile(default_layout.getLayoutFile());
							layout.setType(1);//默认页
							layout.setSort(default_layout.getSort());
							layout.setReferenceCode(default_layout.getReferenceCode());
							
							
							break;
						}
					}
					if(layout.getLayoutFile() == null || "".equals(layout.getLayoutFile().trim())){
						error.put("layoutFile", "默认页不存在");
					}
				}else{
					error.put("layoutFile", "请选择默认页");
				}
				
			}else{
				error.put("layoutFile", "默认页已全部添加");
			}
			
		}else if(formbean.getType().equals(3)){//更多
			layout.setForumData(formbean.getForumData());
			String prefix = "more_product_";//前缀
			if(formbean.getForumData().equals(1)){
				prefix = "more_product_";//商品'更多'前缀
			}else if(formbean.getForumData().equals(2)){
				prefix = "more_information_";//资讯'更多'前缀
			}else if(formbean.getForumData().equals(3)){
				prefix = "more_help_";//在线帮助'更多'前缀
			}
			
			
			List<String> layoutFile = new ArrayList<String>();//文件名称
			//取得'更多'最大值
			List<Layout> _layout = templateService.findLayoutByLayoutFile(formbean.getDirName(), prefix);
			for(Layout t :_layout){
				layoutFile.add(t.getLayoutFile());
			}
			if(layoutFile != null && layoutFile.size()>0){
				//取得文件编号
				Integer count = this.fileMaxNumber(layoutFile);
			    //生成布局文件名
				layout.setLayoutFile(prefix+count+".html");
			}else{
				//如果目录还没有文件就执行生成布局文件 
				layout.setLayoutFile(prefix+"1.html");
			}
		}else if(formbean.getType().equals(4)){//空白页
			
			
			
			//取得模板文件名
			if(formbean.getReferenceCode() != null && !"".equals(formbean.getReferenceCode().trim())){
				if(formbean.getReferenceCode().trim().matches("/.+?")){
					error.put("referenceCode", "不能以/开头！");
				}
				if(formbean.getReferenceCode().trim().matches(".+?/")){
					error.put("referenceCode", "不能以/结尾！");
				}
				if(formbean.getReferenceCode().trim().matches(".*/{2,}.*")){
					error.put("referenceCode", "左斜杆不能连续出现！");
				}
				if(!formbean.getReferenceCode().trim().matches("[\\d\\w_/]+")){
					error.put("referenceCode", "只能由数字、26个英文字母、下划线和或者左斜杆组成！");
				}
				
				//?  匹配任何单字符
				//*  匹配0或者任意数量的字符
				//** 匹配0或者更多的目录
				Set<String> pathSet = DisablePath.getPath();//禁止路径
				if(pathSet != null && pathSet.size() >0){
					for(String path : pathSet){
						boolean flag = matcher.match(path, formbean.getReferenceCode().trim().toLowerCase());  //参数一: ant匹配风格   参数二:输入URL
						if(flag){
							error.put("referenceCode", "当前URL禁止使用");
						}
					}
				}
				
				layout.setReferenceCode(formbean.getReferenceCode().trim());
				
				
				if(error.get("referenceCode") == null){
					List<Layout> _default_layoutList = templateManage.newLayout(formbean.getDirName());
					for(Layout l : _default_layoutList){
						if(l.getReferenceCode().equalsIgnoreCase(formbean.getReferenceCode().trim())){
							error.put("referenceCode", "URL为默认页，不能使用！");
							break;
						}
					}
					
					
					//取得空白页值
					List<Layout> layoutList = templateService.findLayout(formbean.getDirName(), 4);
					List<String> referenceCodeList = new ArrayList<String>();
					for(Layout l :layoutList){
						referenceCodeList.add(l.getReferenceCode().toLowerCase());//转为小写
					}
	
					if(referenceCodeList.contains(formbean.getReferenceCode().trim().toLowerCase())){
						error.put("referenceCode", "URL名称不能重复！");
					}else{
						//取得空白页最大值
						List<Layout> _layout = templateService.findLayoutByLayoutFile(formbean.getDirName(), "blank_");
						List<String> layoutFile = new ArrayList<String>();//文件名称
						for(Layout t :_layout){
							layoutFile.add(t.getLayoutFile());
						}
						if(layoutFile != null && layoutFile.size()>0){
							if(formbean.getReturnData().equals(0)){//返回html格式数据
								//取得文件编号
								Integer count = this.fileMaxNumber(layoutFile);
								//生成布局文件名
								layout.setLayoutFile("blank_"+count+".html");
							}else{//返回json格式数据
								layout.setReturnData(1);
							}	
						}else{
							if(formbean.getReturnData().equals(0)){//返回html格式数据
								//如果目录还没有文件就执行生成布局文件 
								 layout.setLayoutFile("blank_1.html");
							}else{//返回json格式数据
								layout.setReturnData(1);
							}
						}			
					}
				}				
			}else{
				error.put("referenceCode", "URL不能为空！");
			}
		}else if(formbean.getType().equals(5)){//公共页(生成新引用页)
			//取得公共页(生成新引用页)最大值
			List<Layout> _layout = templateService.findLayoutByLayoutFile(formbean.getDirName(), "newPublic_");
			List<String> layoutFile = new ArrayList<String>();//文件名称
			for(Layout t :_layout){
				layoutFile.add(t.getLayoutFile());
			}
			
			if(layoutFile != null && layoutFile.size()>0){
				//取得最大文件编号
				Integer count = this.fileMaxNumber(layoutFile);
				//生成公共页(生成新引用页)文件名
		        layout.setLayoutFile("newPublic_"+count+".html");
		        layout.setReferenceCode("newPublic_"+count);
				
			}else{//如果目录还没有文件就执行生成布局文件 
				 layout.setLayoutFile("newPublic_1.html");
			     layout.setReferenceCode("newPublic_1");	
			}
		}else if(formbean.getType().equals(6)){//公共页(引用版块值)
			//取得公共页引用版块最大值
			List<Layout> _layout = templateService.findLayoutByReferenceCode(formbean.getDirName(), "quote_");
			List<String> referenceCode = new ArrayList<String>();//文件名称
			for(Layout t :_layout){
				referenceCode.add(t.getReferenceCode());
			}
			if(referenceCode != null && referenceCode.size()>0){
				//取得文件编号
				Integer count = this.fileMaxNumber(referenceCode);
				layout.setReferenceCode("quote_"+count);
			}else{
				layout.setReferenceCode("quote_1");
			}
		}

		if(error.size() == 0){
			if(layout.getSort() != null && layout.getSort() <= 1){
				Integer maxLayoutSort = templateService.findMaxLayoutSortBydirName(layout.getDirName());
				if(maxLayoutSort != null){
					layout.setSort(maxLayoutSort+10);
				}
			}
			
			
			
			templateService.saveLayout(layout);
			//当前模板目录绝对路径
			String pc_path = "WEB-INF"+File.separator+"templates"+File.separator+pathName+File.separator+"pc"+File.separator+"public"+File.separator;
			//当前模板目录绝对路径
			String wap_path = "WEB-INF"+File.separator+"templates"+File.separator+pathName+File.separator+"wap"+File.separator+"public"+File.separator;
			
			if(layout.getType().equals(1)){//1.默认页
				//当前模板目录绝对路径
				String default_pc_path = "WEB-INF"+File.separator+"templates"+File.separator+pathName+File.separator+"pc"+File.separator;
				//当前模板目录绝对路径
				String default_wap_path = "WEB-INF"+File.separator+"templates"+File.separator+pathName+File.separator+"wap"+File.separator;
				
				//创建文件并将注释写入模板文件
				FileUtil.writeStringToFile(default_pc_path + layout.getLayoutFile(),"<#-- 默认"+layout.getName()+" -->","utf-8",false);
				//创建文件并将注释写入模板文件
				FileUtil.writeStringToFile(default_wap_path + layout.getLayoutFile(),"<#-- 默认"+layout.getName()+" -->","utf-8",false);
		
			}else if(layout.getType().equals(2)){//2.商品分类 
				//创建文件并将注释写入模板文件
				FileUtil.writeStringToFile(pc_path + layout.getLayoutFile(),"<#-- "+layout.getName()+" 商品分类 -->","utf-8",false);
				//创建文件并将注释写入模板文件
				FileUtil.writeStringToFile(wap_path + layout.getLayoutFile(),"<#-- "+layout.getName()+" 商品分类 -->","utf-8",false);
			
			}else if(formbean.getType().equals(3)){//更多
				//创建文件并将注释写入模板文件
				FileUtil.writeStringToFile(pc_path + layout.getLayoutFile(),"<#-- "+layout.getName()+" 更多 -->","utf-8",false);
				//创建文件并将注释写入模板文件
				FileUtil.writeStringToFile(wap_path + layout.getLayoutFile(),"<#-- "+layout.getName()+" 更多 -->","utf-8",false);
		
			}else if(layout.getType().equals(4)){//空白页
				if(layout.getLayoutFile() != null && !"".equals(layout.getLayoutFile().trim())){
					//创建文件并将注释写入模板文件
					FileUtil.writeStringToFile(pc_path+layout.getLayoutFile(),"<#-- "+layout.getName()+" 空白页 -->","utf-8",false);
					//创建文件并将注释写入模板文件
					FileUtil.writeStringToFile(wap_path+layout.getLayoutFile(),"<#-- "+layout.getName()+" 空白页 -->","utf-8",false);
				}
			}else if(formbean.getType().equals(5)){//公共页(生成新引用页)
				//创建文件并将注释写入模板文件
				FileUtil.writeStringToFile(pc_path+ layout.getLayoutFile(),"<#-- "+layout.getName()+" 公共页(生成新引用页) -->","utf-8",false);
				//创建文件并将注释写入模板文件
				FileUtil.writeStringToFile(wap_path+ layout.getLayoutFile(),"<#-- "+layout.getName()+" 公共页(生成新引用页) -->","utf-8",false);
		  		
			}	
		}else{
			model.addAttribute("error",error);
			model.addAttribute("layout",layout);
			if(layout.getType().equals(1)){//1.默认页
				model.addAttribute("default_layoutList",default_layoutList);
			}
			return "jsp/template/add_layout";
		}
		
		
		model.addAttribute("message","添加布局成功");//返回消息
		model.addAttribute("urladdress",RedirectPath.readUrl("control.layout.list")+"?dirName="+formbean.getDirName());//返回消息//返回转向地址
		return "jsp/common/message";
		
	}
	
	/**
	 * 模板管理 布局修改 界面显示
	 * @param layoutId 布局Id
	 * @param layoutName 布局名称
	 * @param templeteName 模板名称
	 * @param dirName 模板目录
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=editLayout", method=RequestMethod.GET)
	public String editLayoutUI(ModelMap model,String layoutId,String dirName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		//根据模板目录名称查询模板
		if(dirName != null && !"".equals(dirName.trim())){
			Templates templates = templateService.findTemplatebyDirName(dirName);
			model.addAttribute("templates", templates);
		}
		
		if(layoutId != null && !"".equals(layoutId.trim())){
			Layout layout = templateService.findLayoutByLayoutId(layoutId);
			if(layout != null){
				
				model.addAttribute("layout",layout);
				
			}else{
				throw new SystemException("布局不存在！");
			}
		}else{
			throw new SystemException("参数不能为空！");
		}
		return "jsp/template/edit_layout";
	}
	
	/**
	 * 模板管理 布局修改 
	 * @param layoutId 布局Id
	 * @param name 布局名称
	 * @param templeteName 模板名称
	 * @param dirName 模板目录
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=editLayout", method=RequestMethod.POST)
	public String editLayout(ModelMap model,String layoutId,Layout formbean,BindingResult result,
			PageForm pageForm,String templeteName,String dirName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if(layoutId != null && !"".equals(layoutId.trim())){
			Layout layout = templateService.findLayoutByLayoutId(layoutId);
			if(layout != null){
				
				//根据模板目录名称查询模板
				if(layout.getDirName() != null && !"".equals(layout.getDirName().trim())){
					Templates templates = templateService.findTemplatebyDirName(layout.getDirName().trim());
					model.addAttribute("templates", templates);
				}
				
				
				Map<String,String> error = new HashMap<String,String>();
	
				if(formbean.getName() == null || "".equals(formbean.getName().trim())){
					error.put("name", "布局名称不能为空！");
				}
				layout.setName(formbean.getName().trim());
				
				if(layout.getType().equals(4)){//空白页
					layout.setReferenceCode(formbean.getReferenceCode().trim());
					
					//取得模板文件名
					if(formbean.getReferenceCode() != null && !"".equals(formbean.getReferenceCode().trim())){
						if(formbean.getReferenceCode().trim().matches("/.+?")){
							error.put("referenceCode", "不能以/开头！");
						}
						if(formbean.getReferenceCode().trim().matches(".+?/")){
							error.put("referenceCode", "不能以/结尾！");
						}
						if(formbean.getReferenceCode().trim().matches(".*/{2,}.*")){
							error.put("referenceCode", "左斜杆不能连续出现！");
						}
						if(!formbean.getReferenceCode().trim().matches("[\\d\\w_/]+")){
							error.put("referenceCode", "只能由数字、26个英文字母、下划线和或者左斜杆组成！");
						}
						//?  匹配任何单字符
						//*  匹配0或者任意数量的字符
						//** 匹配0或者更多的目录
						Set<String> pathSet = DisablePath.getPath();//禁止路径
						if(pathSet != null && pathSet.size() >0){
							for(String path : pathSet){
								boolean flag = matcher.match(path, formbean.getReferenceCode().trim().toLowerCase());  //参数一: ant匹配风格   参数二:输入URL
								if(flag){
									error.put("referenceCode", "当前URL禁止使用");
								}
							}
						}
						
						
						
						if(error.get("referenceCode") == null){
							List<Layout> _default_layoutList = templateManage.newLayout(formbean.getDirName());
							for(Layout l : _default_layoutList){
								if(l.getReferenceCode().equalsIgnoreCase(formbean.getReferenceCode().trim())){
									error.put("referenceCode", "URL为默认页，不能使用！");
									break;
								}
							}
							
							//取得空白页值
							List<Layout> layoutList = templateService.findLayout(formbean.getDirName(), 4);
							List<String> referenceCodeList = new ArrayList<String>();
							for(Layout l :layoutList){
								if(!l.getId().equals(layout.getId())){
									referenceCodeList.add(l.getReferenceCode().toLowerCase());//转为小写
								}
					
							}
			
							if(referenceCodeList.contains(formbean.getReferenceCode().trim().toLowerCase())){
								error.put("referenceCode", "URL名称不能重复！");		
							}
						}				
					}else{
						error.put("referenceCode", "URL不能为空！");
					}
					
				}
				
				
				if(error.size() == 0){
					templateService.updateLayoutById(layout);
					
					if(layout.getType().equals(7)){//站点栏目详细页
						
						
						Column column = columnManage.queryColumnById(Integer.parseInt(layout.getReferenceCode().split("_")[1]),layout.getDirName());
						column.setName(layout.getName());
						List<Column> newColumnList = columnManage.updateColumn(column, layout.getDirName());
					}
				}else{
					model.addAttribute("error",error);
					model.addAttribute("layout",layout);
					return "jsp/template/edit_layout";
				}
			}else{
				throw new SystemException("布局不存在！");
			}
		}else{
			throw new SystemException("参数不能为空！");
		}
		
		model.addAttribute("message","布局修改成功");//返回消息
		model.addAttribute("urladdress",RedirectPath.readUrl("control.layout.list")+"?dirName="+dirName+"&page="+pageForm.getPage());//返回消息//返回转向地址
		return "jsp/common/message";
	}
	
	
	
	/**
	 * 模板管理 布局删除
	 * @param layoutId 布局Id
	 * @param layoutName 布局名称
	 * @param templeteName 模板名称
	 * @param dirName 模板目录
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=deleteLayout", method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String deleteLayout(ModelMap model,String layoutId, String dirName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if(layoutId != null && !"".equals(layoutId.trim())){
			Layout layout = templateService.findLayoutByLayoutId(layoutId);
			if(layout != null){
				//版块
				List<Forum> forumList = templateService.findForumByLayoutId(dirName,layoutId);

				templateService.deleteLayoutByLayoutId(layout.getDirName(),layout.getId());
				
				
				//删除布局版块上传文件
				layoutManage.deleteUploadFile(forumList);
				
				if(layout.getLayoutFile() != null && !"".equals(layout.getLayoutFile().trim())){
					String pc_path = "WEB-INF"+File.separator+"templates"+File.separator+layout.getDirName()+File.separator+"pc"+File.separator+"public"+File.separator;
					//删除模板文件
					localFileManage.deleteFile(pc_path+layout.getLayoutFile());
					String wap_path = "WEB-INF"+File.separator+"templates"+File.separator+layout.getDirName()+File.separator+"wap"+File.separator+"public"+File.separator;
					//删除模板文件
					localFileManage.deleteFile(wap_path+layout.getLayoutFile());
				}
				if(layout.getType().equals(1)){//默认页
					String pc_path = "WEB-INF"+File.separator+"templates"+File.separator+layout.getDirName()+File.separator+"pc"+File.separator;
					//删除模板文件
					localFileManage.deleteFile(pc_path+layout.getLayoutFile());
					String wap_path = "WEB-INF"+File.separator+"templates"+File.separator+layout.getDirName()+File.separator+"wap"+File.separator;
					//删除模板文件
					localFileManage.deleteFile(wap_path+layout.getLayoutFile());
				}else if(layout.getType().equals(7)){//站点栏目详细页
					String columnId_str = layout.getReferenceCode().split("_")[1];
					Column column = columnManage.queryColumnById(Integer.parseInt(columnId_str),dirName);
					if(column != null){
						List<Column> columnList = new ArrayList<Column>();
						columnList.add(column);
						
						TreeSet<Integer> columnIdList = columnManage.columnIdList(columnList,new TreeSet<Integer>());
						for(Integer id : columnIdList){
							//路径
							String pc_path = "WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"pc"+File.separator+"public"+File.separator;
							//删除空白页文件
							localFileManage.deleteFile(pc_path+"column_"+id+".html");
							//路径
							String wap_path = "WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"wap"+File.separator+"public"+File.separator;
							//删除空白页文件
							localFileManage.deleteFile(wap_path+"column_"+id+".html");
							//删除布局
							Layout _layout = templateService.findLayoutByReferenceCode(dirName,7,"column_"+id);
							if(_layout != null){
								templateService.deleteLayoutByLayoutId(dirName, _layout.getId());
							}
							
						}
						
					}
					List<Column> newColumnList = columnManage.deleteColumn(Integer.parseInt(columnId_str), dirName);
				}
				
				return "1";
			}
		}
		return "0";
	}
	
	
	
	
	
	/**
	 * Ajax校验URL名称
	 */
	@RequestMapping(params="method=checkUrlName", method=RequestMethod.GET)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String checkUrlName(Layout formbean,String layoutId,
				HttpServletRequest request, HttpServletResponse response)
				throws Exception {
		
		Map<String,String> error = new HashMap<String,String>();

		//取得模板文件名
		if(formbean.getReferenceCode() != null && !"".equals(formbean.getReferenceCode().trim())){
			if(formbean.getReferenceCode().trim().matches("/.+?")){
				error.put("referenceCode", "不能以/开头！");
			}
			if(formbean.getReferenceCode().trim().matches(".+?/")){
				error.put("referenceCode", "不能以/结尾！");
			}
			if(formbean.getReferenceCode().trim().matches(".*/{2,}.*")){
				error.put("referenceCode", "左斜杆不能连续出现！");
			}
			if(!formbean.getReferenceCode().trim().matches("[\\d\\w_/]+")){
				error.put("referenceCode", "只能由数字、26个英文字母、下划线和或者左斜杆组成！");
			}
			
			//?  匹配任何单字符
			//*  匹配0或者任意数量的字符
			//** 匹配0或者更多的目录
			Set<String> pathSet = DisablePath.getPath();//禁止路径
			if(pathSet != null && pathSet.size() >0){
				for(String path : pathSet){
					boolean flag = matcher.match(path, formbean.getReferenceCode().trim().toLowerCase());  //参数一: ant匹配风格   参数二:输入URL
					if(flag){
						error.put("referenceCode", "当前URL禁止使用");
					}
				}
			}
				
			if(error.get("referenceCode") == null){
				List<Layout> _default_layoutList = templateManage.newLayout(formbean.getDirName());
				for(Layout l : _default_layoutList){
					if(l.getReferenceCode().equalsIgnoreCase(formbean.getReferenceCode().trim())){
						error.put("referenceCode", "URL为默认页，不能使用！");
						break;
					}
				}
				//取得空白页值
				List<Layout> layoutList = templateService.findLayout(formbean.getDirName(), 4);
				List<String> referenceCodeList = new ArrayList<String>();
				for(Layout l :layoutList){
					if(!l.getId().equals(layoutId)){
						referenceCodeList.add(l.getReferenceCode().toLowerCase());//转为小写
					}
				}

				if(referenceCodeList.contains(formbean.getReferenceCode().trim().toLowerCase())){
					error.put("referenceCode", "URL名称不能重复！");
				}
				
			}				
		}else{
			error.put("referenceCode", "URL不能为空！");
		}
		
		if(error.size() >0){
			for(Map.Entry<String, String> entry : error.entrySet()){ 
				return entry.getValue();
			} 
		}
		return "";
	}
	/**
	 * * AJAX读取'更多'
	 * 返回JSON Map格式
	 * @param dirName 目录名称
	 */
	@RequestMapping(params="method=ajax_more", method=RequestMethod.GET)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String more(String dirName,String forumChildType,
				HttpServletRequest request, HttpServletResponse response)
				throws Exception {
		
		if(forumChildType != null && !"".equals(forumChildType) ){
			String forumChildType_decode= java.net.URLDecoder.decode(forumChildType, "UTF-8"); 
			//key:布局文件名称  value: 布局文件名称+布局名称
			Map<String,String> more = layoutManage.queryMore(dirName, forumChildType_decode);
			
			return JsonUtils.toJSONString(more);
		}

		return "";
	}
	
	/**
	 * 取得文件编号
	 * @param fileNameList xxx开头的文件名集合
	 * @return
	 */
	private Integer fileMaxNumber(List<String> fileNameList){
		List<Integer> numberList = new ArrayList<Integer>();
		for(int i = 0; i<fileNameList.size();i++){
			String fileName = (String)fileNameList.get(i);//取得newPublic_开头的文件
			
			if(fileName!= null && !"".equals(fileName.trim())){
				String[] fileName_arr = fileName.split("_");		
				StringBuffer sb = new StringBuffer(fileName_arr[fileName_arr.length-1]);

				if(sb != null && !"".equals(sb.toString())){
					if(sb.length() >5){
						//文件名后5个字符
						String after = sb.substring(sb.length()-5, sb.length());
						if(after != null && ".html".equals(after)){
							sb.delete(sb.length()-5, sb.length());//删除.html
						}
					}
					boolean verification = Verification.isPositiveInteger(sb.toString().trim());//正整数
					if(verification){
						int number = Integer.parseInt(sb.toString().trim()); //取得newPublic_开头的文件的编号,如:newPublic_1
						numberList.add(number);
					}	
				}	
			}
		}
		if(numberList != null && numberList.size() >0){
			//将ColumnDetailsView_开头的文件排序,取最大值
			//初始化数组   ,冒泡排序法从低到高选择排序 
			Integer[] lastArray = (Integer []) numberList.toArray(new Integer[0]);//li.toArray返回的是Object类型数组,这句是将Object数组转为Integer数组
			//外层循环lastArray.length-1次循环   
	        for(int i=lastArray.length-1; i>0; i--) {   
	            //外层循环i次循环   
	            for(int j=0; j<i; j++) {   
	                //当前面的数据大于后面的数据，把两个数据进行交换   
	                if(lastArray[j] > lastArray[j+1]) {   
	                    int tempInt = lastArray[j];   
	                    lastArray[j] = lastArray[j+1];   
	                    lastArray[j+1] = tempInt;   
	                }   
	            }   
	        }
	        //取得排序后的最大值+1,生成新的值
	        Integer count = Integer.parseInt(lastArray[lastArray.length-1].toString())+1;
			return count;
		}else{
			return 1;
		}
	}
	
	/**
	 * 模板管理 布局代码编辑 界面显示
	 * @param layoutId 布局Id
	 * @param dirName 模板目录
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=editLayoutCode", method=RequestMethod.GET)
	public String editLayoutCodeUI(String layoutId,String dirName,PageForm pageForm,
			ModelMap model,HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		if(layoutId != null && !"".equals(layoutId.trim())){
			Layout layout = templateService.findLayoutByLayoutId(layoutId);
			if(layout != null){
				String pc_path = "";
				String wap_path = "";
				if(layout.getType().equals(1)){//如果是默认页
					pc_path = PathUtil.path()+File.separator+"WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"pc"+File.separator+layout.getLayoutFile();
					wap_path = PathUtil.path()+File.separator+"WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"wap"+File.separator+layout.getLayoutFile();
					
				}else{
					pc_path = PathUtil.path()+File.separator+"WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"pc"+File.separator+"public"+File.separator+layout.getLayoutFile();
					wap_path = PathUtil.path()+File.separator+"WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"wap"+File.separator+"public"+File.separator+layout.getLayoutFile();
					
				}
				StringBuffer pc_html = new StringBuffer();
				StringBuffer wap_html = new StringBuffer();
				File pc_f = new File(pc_path); 
				if(pc_f.exists()){
					//调用文件编码判断类
					String coding = Coding.detection(pc_f);
					InputStreamReader read = new InputStreamReader (new FileInputStream(pc_f),coding); 
					BufferedReader br = new BufferedReader(read);
					String row;
					while((row = br.readLine())!=null){	
						pc_html.append(row).append("\n");
					}
					
				}else{
					model.addAttribute("layout_error", "电脑版找不到指定的文件");
				}	
				
				File wap_f = new File(wap_path); 
				if(wap_f.exists()){
					//调用文件编码判断类
					String coding = Coding.detection(wap_f);
					InputStreamReader read = new InputStreamReader (new FileInputStream(wap_f),coding); 
					BufferedReader br = new BufferedReader(read);
					String row;
					while((row = br.readLine())!=null){		
						wap_html.append(row).append("\n");
					}
					
				}else{
					model.addAttribute("layout_error", "移动版找不到指定的文件");
				}
				
				//最后一个左斜杆后的值 例:user/orderList 保留orderList   index返回空值
				String lastValue = StringUtils.substringAfterLast(layout.getReferenceCode(), "/");//从右往左截取到相等的字符
				if(lastValue == null || "".equals(lastValue.trim())){
					lastValue = layout.getReferenceCode();
				}
				//默认页
				if(layout.getType().equals(1)){
					lastValue = StringUtils.substringBeforeLast(layout.getLayoutFile(), ".html");
				}
				
				//默认更多  因为下一处理步骤会删除最后一个下划线之后的字符,所以本参数值加上一个下划线。如more_product改成more_product_
				if("more".equals(layout.getReferenceCode())){
					//从右往左截取到相等的字符
					lastValue = StringUtils.substringBeforeLast(layout.getLayoutFile(), ".html");
					
					lastValue +="_";
				}
				//布局更多
				if(layout.getType().equals(3)){
					//从右往左截取到相等的字符
					lastValue = StringUtils.substringBeforeLast(layout.getLayoutFile(), ".html");
				}
				
				
				
				//显示示例程序
				String example = templateManage.readExample(lastValue);
				
				//显示公共API
				String common = templateManage.readExample("common");
				
				//根据模板目录名称查询模板
				if(dirName != null && !"".equals(dirName.trim())){
					Templates templates = templateService.findTemplatebyDirName(dirName);
					model.addAttribute("templates", templates);
				}
				model.addAttribute("example", example);
				model.addAttribute("common", common);
				model.addAttribute("layout", layout);
				model.addAttribute("pc_html", pc_html.toString());
				model.addAttribute("wap_html", wap_html.toString());
				
			}
		}
		

		return "jsp/template/edit_layoutCode";
	}
	
	/**
	 * 模板管理 布局代码编辑 修改
	 * @param model
	 * @param pc_code 电脑版
	 * @param wap_code 移动版
	 * @param layoutId
	 * @param pageForm
	 * @param dirName
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=editLayoutCode", method=RequestMethod.POST)
	public String editLayoutCode(ModelMap model,String pc_code,String wap_code,String layoutId, PageForm pageForm,String dirName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if(layoutId != null && !"".equals(layoutId.trim())){
			Layout layout = templateService.findLayoutByLayoutId(layoutId);
			if(layout != null){
				String pc_path = "";
				String wap_path = "";
				if(layout.getType().equals(1)){//如果是默认页
					pc_path = "WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"pc"+File.separator+layout.getLayoutFile();
					wap_path = "WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"wap"+File.separator+layout.getLayoutFile();
					
				}else{
					pc_path = "WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"pc"+File.separator+"public"+File.separator+layout.getLayoutFile();
					wap_path = "WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"wap"+File.separator+"public"+File.separator+layout.getLayoutFile();
				}
				FileUtil.writeStringToFile(pc_path,pc_code,"utf-8", false);
				
				
				FileUtil.writeStringToFile(wap_path,wap_code,"utf-8", false);
			}else{
				throw new SystemException("布局不存在！");
			}
		}else{
			throw new SystemException("参数不能为空！");
		}
		
		//在Chrome浏览器下和home.html页的下面两行js代码有冲突，会报错误manage.htm?method=editLayoutCode&layoutId=440b1b2f202d4de38f450226083ca174&dirName=default&page=:31 The XSS Auditor refused to execute a script in 'http://bbs.diyhi.com/control/layout/manage.htm?method=editLayoutCode&layoutId=440b1b2f202d4de38f450226083ca174&dirName=default&page=' because its source code was found within the request. The auditor was enabled as the server did not send an 'X-XSS-Protection' header.
		//html += 		'<input type="button" value="提交" class="button" onclick="topicUnhide(10,'+random+','+topicId+');">';
		//html += 		'<input type="button" value="立即购买" class="button" onclick="topicUnhide(40,'+random+','+topicId+');">';
		//解决办法是设置Header头文件为X-XSS-Protection:0
		//	response.addHeader("X-XSS-Protection", "0");
		
		
		model.addAttribute("message","布局代码编辑成功");//返回消息
		model.addAttribute("urladdress",RedirectPath.readUrl("control.layout.list")+"?&dirName="+dirName+"&page="+pageForm.getPage());//返回消息//返回转向地址
		return "jsp/common/message";
	}
}
