package cms.web.action.template;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.UUIDUtil;
import cms.web.action.SystemException;
import cms.web.action.fileSystem.localImpl.LocalFileManage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 栏目管理
 *
 */
@Controller
@RequestMapping("/control/column/manage") 
public class ColumnManageAction {
	@Resource ColumnManage columnManage;
	@Resource(name="templateServiceBean")
	private TemplateService templateService;//通过接口引用代理返回的对象
	@Resource LayoutManage layoutManage;
	@Resource LocalFileManage localFileManage;
	
	/**
	 * 栏目列表
	 * @param dirName 模板目录名称
	 */
	@RequestMapping(params="method=list",method=RequestMethod.GET)
	public String execute(@RequestParam("dirName") String dirName,PageForm pageForm,ModelMap model){
		
		if(dirName == null || "".equals(dirName.trim())){
			throw new SystemException("目录名称不能为空");
		}
		//根据模板目录名称查询模板
		if(dirName != null && !"".equals(dirName.trim())){
			Templates templates = templateService.findTemplatebyDirName(dirName);
			model.addAttribute("templates", templates);
		}
		return "jsp/template/columnList";
	}
	
	
	/**
	 * 查询栏目
	 * @param dirName 模板目录名称
	 */
	@RequestMapping(params="method=queryColumn",method=RequestMethod.GET)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String queryColumn(ModelMap model,String dirName,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		List<Column> columnList = columnManage.columnList(dirName);

		return JsonUtils.toJSONString(columnList);
	}
	
	
	/**
	 * 栏目管理 添加
	 * @param dirName 模板目录名称
	 */
	@RequestMapping(params="method=add",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String add(Integer parentId,String dirName,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
			
		//错误
		Map<String,String> error = new HashMap<String,String>();
		//返回值
		Map<String,Object> returnJson = new HashMap<String,Object>();
		
		
		Column column = new Column();
		if(parentId != null && parentId >0){
			column.setParentId(parentId);
		}
		//名称
		String name = request.getParameter("name");
		
		//排序
		String sort = request.getParameter("sort");
		//链接方式 
		String linkMode = request.getParameter("linkMode");
		//URL
		String url = request.getParameter("url");
		if(name != null && !"".equals(name.trim())){
			String name_utf8 = URLDecoder.decode(name, "UTF-8");//解码
			column.setName(name_utf8.trim());
		}else{
			error.put("name", "栏目名称不能为空");
		}
		if(sort != null && !"".equals(sort.trim())){
			if(StringUtils.isNumeric(sort)){
				if(sort.trim().length()>8){
					error.put("sort", "不能超过8位数字");
				}else{
					column.setSort(Integer.parseInt(sort.trim()));
				}
			}else{
				error.put("sort", "请填写数字");
			}
		}
		if(linkMode != null && !"".equals(linkMode)){
			column.setLinkMode(Integer.parseInt(linkMode));
		}
		
		if(column.getLinkMode().equals(2) || column.getLinkMode().equals(3)){//外部URL 和 内部URL 
			if(url != null && !"".equals(url.trim())){
				String url_utf8 = URLDecoder.decode(url, "UTF-8");//解码
				if(column.getLinkMode().equals(2) && !columnManage.validURL(url_utf8.trim())){
					error.put("url", "不是正确的网址");
				}
				if(column.getLinkMode().equals(3)){
					if(url_utf8.trim().matches("/.+?")){
						error.put("url", "不能以/开头");
					}
					if(url_utf8.trim().matches(".*/{2,}.*")){
						error.put("url", "左斜杆不能连续出现");
					}
				}
				
				column.setUrl(url_utf8.trim());
			}else{
				error.put("url", "请填写URL");
			}
		}else{
			column.setUrl("");
		}

		if(error.size() >0){//有错误
			returnJson.put("error", error);
			returnJson.put("success", false);
		}else{
			
			Integer maxId = columnManage.addColumn(column, dirName);
			if(column.getLinkMode().equals(4)){//空白页
				
				
				//添加布局文件
				Layout layout = new Layout();
				layout.setId(UUIDUtil.getUUID32());
				layout.setName(column.getName());
				layout.setType(7);//7.站点栏目详细页
				layout.setDirName(dirName);
				layout.setLayoutFile("column_"+maxId+".html");
				layout.setReferenceCode("column_"+maxId);
				layout.setSort(column.getSort());
				
				templateService.save(layout);
				
				//生成文件
				String pc_path = "WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"pc"+File.separator+"public"+File.separator;
				//创建文件并将注释写入模板文件
				FileUtil.writeStringToFile(pc_path+layout.getLayoutFile(),"<#-- "+column.getName()+" -->","utf-8",false);
				//生成文件
				String wap_path = "WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"wap"+File.separator+"public"+File.separator;
				//创建文件并将注释写入模板文件
				FileUtil.writeStringToFile(wap_path+layout.getLayoutFile(),"<#-- "+column.getName()+" -->","utf-8",false);
				
			}

			returnJson.put("success", true);
		}
		
		return JsonUtils.toJSONString(returnJson);
		
	}
	
	
	/**
	 * 栏目管理 修改
	 * @param dirName 模板目录名称
	 */
	@RequestMapping(params="method=edit",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String edit(String dirName,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//错误
		Map<String,String> error = new HashMap<String,String>();
		//返回值
		Map<String,Object> returnJson = new HashMap<String,Object>();
		
		
		String id = request.getParameter("columnId");
		
		//名称
		String name = request.getParameter("name");
		
		//排序
		String sort = request.getParameter("sort");
		//链接方式 
		String linkMode = request.getParameter("linkMode");
		//URL
		String url = request.getParameter("url");
		
		Column column = columnManage.queryColumnById(Integer.parseInt(id),dirName.trim());
		//旧链接方式
		Integer old_linkMode = column.getLinkMode();
		
		if(name != null && !"".equals(name.trim())){
			String name_utf8 = URLDecoder.decode(name, "UTF-8");//解码
			column.setName(name_utf8.trim());
		}else{
			error.put("name", "栏目名称不能为空");
		}
		if(sort != null && !"".equals(sort.trim())){
			if(StringUtils.isNumeric(sort.trim())){
				if(sort.trim().length()>8){
					error.put("sort", "不能超过8位数字");
				}else{
					column.setSort(Integer.parseInt(sort.trim()));
				}
			}else{
				error.put("sort", "请填写数字");
			}
		}
		if(linkMode != null && !"".equals(linkMode)){
			column.setLinkMode(Integer.parseInt(linkMode));
		}

		if(column.getLinkMode().equals(2) || column.getLinkMode().equals(3)){//外部URL 
			if(url != null && !"".equals(url.trim())){
				String url_utf8 = URLDecoder.decode(url, "UTF-8");//解码
				
				if(column.getLinkMode().equals(2) && !columnManage.validURL(url_utf8.trim())){
					error.put("url", "不是正确的网址");
				}
				if(column.getLinkMode().equals(3)){
					if(url_utf8.trim().matches("/.+?")){
						error.put("url", "不能以/开头");
					}
					if(url_utf8.trim().matches(".*/{2,}.*")){
						error.put("url", "左斜杆不能连续出现");
					}
				}
				
				column.setUrl(url_utf8.trim());
			}else{
				error.put("url", "请填写URL");
			}
		}else{
			column.setUrl("");
		}
		
		
		if(error.size() >0){
			returnJson.put("error", error);
			returnJson.put("success", false);
		}else{
			
			
			List<Column> newColumnList = columnManage.updateColumn(column, dirName);
			if(newColumnList != null){
				
				
				if(column.getLinkMode().equals(1) || column.getLinkMode().equals(2) ||column.getLinkMode().equals(3)){//无   外部URL  内部URL 
					//路径
					String pc_path = "WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"pc"+File.separator+"public"+File.separator;
					//删除空白页文件
					localFileManage.deleteFile(pc_path+"column_"+column.getId()+".html");
					//路径
					String wap_path = "WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"wap"+File.separator+"public"+File.separator;
					//删除空白页文件
					localFileManage.deleteFile(wap_path+"column_"+column.getId()+".html");
					
					//删除布局
					Layout layout = templateService.findLayoutByReferenceCode(dirName,7,"column_"+column.getId());
					if(layout != null){
						templateService.deleteLayoutByLayoutId(dirName, layout.getId());
					}
				}
				
				if(column.getLinkMode().equals(4) && old_linkMode.equals(4)){//空白页
					//修改布局名称
					templateService.updateLayoutName(column.getName(),column.getSort(),dirName,7,"column_"+column.getId());
					
				}

				if(column.getLinkMode().equals(4) && !old_linkMode.equals(4)){//空白页
					
					//添加布局文件
					Layout layout = new Layout();
					layout.setId(UUIDUtil.getUUID32());
					layout.setName(column.getName());
					layout.setType(7);
					layout.setDirName(dirName);
					layout.setLayoutFile("column_"+column.getId()+".html");
					layout.setReferenceCode("column_"+column.getId());
					layout.setSort(column.getSort());
					templateService.save(layout);
					//路径
					String pc_path = "WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"pc"+File.separator+"public"+File.separator;
					//创建文件并将注释写入模板文件
					FileUtil.writeStringToFile(pc_path+layout.getLayoutFile(),"<#-- "+column.getName()+" -->","utf-8",false);
					//路径
					String wap_path = "WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"wap"+File.separator+"public"+File.separator;
					//创建文件并将注释写入模板文件
					FileUtil.writeStringToFile(wap_path+layout.getLayoutFile(),"<#-- "+column.getName()+" -->","utf-8",false);
					
				}
				
				returnJson.put("success", true);
			}
			
		}

		return JsonUtils.toJSONString(returnJson);
	}
	/**
	 * 栏目管理  删除
	 * @param dirName 模板目录名称
	 * @param columnId 栏目Id
	 */
	@RequestMapping(params="method=delete",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String delete(ModelMap model,String dirName,Integer columnId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//返回值
		Map<String,Object> returnJson = new HashMap<String,Object>();
		
		Column column = columnManage.queryColumnById(columnId,dirName);
		if(column != null){
			List<Column> columnList = new ArrayList<Column>();
			columnList.add(column);
			
			TreeSet<Integer> columnIdList = columnManage.columnIdList(columnList,new TreeSet<Integer>());
			columnIdList.add(columnId);
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
				Layout layout = templateService.findLayoutByReferenceCode(dirName,7,"column_"+id);
				if(layout != null){
					//版块
					List<Forum> forumList = templateService.findForumByLayoutId(dirName,layout.getId());
					//删除布局版块上传文件
					layoutManage.deleteUploadFile(forumList);
					
		
					templateService.deleteLayoutByLayoutId(dirName, layout.getId());
				}
				
			}
			List<Column> newColumnList = columnManage.deleteColumn(columnId, dirName);
			returnJson.put("success", true);
		}

		return JsonUtils.toJSONString(returnJson);
	}
	
	
	

}
