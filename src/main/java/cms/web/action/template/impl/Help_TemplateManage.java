package cms.web.action.template.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import cms.bean.DataView;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.help.Help;
import cms.bean.help.HelpType;
import cms.bean.template.Forum;
import cms.bean.template.Forum_HelpRelated_Help;
import cms.service.help.HelpService;
import cms.service.help.HelpTypeService;
import cms.service.setting.SettingService;
import cms.utils.JsonUtils;
import cms.utils.Verification;
import cms.web.action.fileSystem.FileManage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;


/**
 * 在线帮助 -- 模板方法实现
 *
 */
@Component("help_TemplateManage")
public class Help_TemplateManage {
	
	@Resource HelpService helpService;
	@Resource SettingService settingService;
	@Resource FileManage fileManage;
	@Resource HelpTypeService helpTypeService;
	/**
	 * 在线帮助列表 -- 单层
	 * @param forum
	 */
	public DataView<Help> help_monolayer(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		
		String formValueJSON = forum.getFormValue();//表单值
		if(formValueJSON != null && !"".equals(formValueJSON)){
			Forum_HelpRelated_Help forum_HelpRelated_Help = JsonUtils.toObject(formValueJSON,Forum_HelpRelated_Help.class);
			if(forum_HelpRelated_Help != null){
				int help_quantity = 10;//帮助展示数量
				Long helpTypeId = null;//在线帮助分类Id
				//每页显示记录数
				if(forum_HelpRelated_Help.getHelp_quantity() != null && forum_HelpRelated_Help.getHelp_quantity() >0){
					help_quantity = forum_HelpRelated_Help.getHelp_quantity();
				}
				
				//在线帮助分类Id
				if(forum_HelpRelated_Help.getHelp_helpTypeId() != null && forum_HelpRelated_Help.getHelp_helpTypeId() >0){
					helpTypeId = forum_HelpRelated_Help.getHelp_helpTypeId();
				}
				if(parameter != null && parameter.size() >0){
					for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
						if("helpTypeId".equals(paramIter.getKey())){
							if(Verification.isNumeric(paramIter.getValue().toString())){
								if(paramIter.getValue().toString().length() <=18){
									helpTypeId = Long.parseLong(paramIter.getValue().toString());	
								}
							}
							
						}
					}
					parameter.put("page", 1);//只显示第一页
				}
				
				DataView<Help> dataView = new DataView<Help>();
				dataView.setRecords(this.help_SQL_Page(forum_HelpRelated_Help,parameter,help_quantity,runtimeParameter).getRecords());
				StringBuffer moreUrl = new StringBuffer("");
				moreUrl.append("more/").append(forum.getId());
				moreUrl.append("-").append(forum_HelpRelated_Help.getHelp_id());
				moreUrl.append("/");
				moreUrl.append(helpTypeId == null ?"":helpTypeId);//在线帮助分类Id
				moreUrl.append("-");
				dataView.setMoreUrl(moreUrl.toString());
				return dataView;
			}
			
		}
		return null;
	}
	
	/**
	 * 在线帮助列表  -- 分页
	 * @param forum
	 */
	public PageView<Help> help_page(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		String formValueJSON = forum.getFormValue();//表单值
		if(formValueJSON != null && !"".equals(formValueJSON)){
			Forum_HelpRelated_Help forum_HelpRelated_Help = JsonUtils.toObject(formValueJSON,Forum_HelpRelated_Help.class);
			if(forum_HelpRelated_Help != null){
				int maxResult = settingService.findSystemSetting_cache().getForestagePageNumber();

				//每页显示记录数
				if(forum_HelpRelated_Help.getHelp_maxResult() != null && forum_HelpRelated_Help.getHelp_maxResult() >0){
					maxResult = forum_HelpRelated_Help.getHelp_maxResult();
				}
				
				return this.help_SQL_Page(forum_HelpRelated_Help,parameter,maxResult,runtimeParameter);
			}
			
		}
		return null;
	}
	
	
	
	
	/**
	 * 在线帮助SQL分页
	 * @param maxResult 每页显示记录数
	 */
	private PageView<Help> help_SQL_Page(Forum_HelpRelated_Help forum_HelpRelated_Help,Map<String,Object> parameter,int maxResult,Map<String,Object> runtimeParameter){
		int page = 1;//分页 当前页
		int pageCount=10;// 页码显示总数
		int sort = 1;//排序
		Long helpTypeId = null;//在线帮助分类Id

		//在线帮助分类Id
		if(forum_HelpRelated_Help.getHelp_helpTypeId() != null && forum_HelpRelated_Help.getHelp_helpTypeId() >0){
			helpTypeId = forum_HelpRelated_Help.getHelp_helpTypeId();
		}
		//排序
		if(forum_HelpRelated_Help.getHelp_sort() != null && forum_HelpRelated_Help.getHelp_sort() >0){
			sort = forum_HelpRelated_Help.getHelp_sort();
		}
		
		
		
		//页码显示总数
		if(forum_HelpRelated_Help.getHelp_pageCount() != null && forum_HelpRelated_Help.getHelp_pageCount() >0){
			pageCount = forum_HelpRelated_Help.getHelp_pageCount();
		}
		//获取参数
		if(parameter != null && parameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				if("page".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=9){
							page = Integer.parseInt(paramIter.getValue().toString());
						}
					}
				}else if("helpTypeId".equals(paramIter.getKey())){
					if(forum_HelpRelated_Help.isHelp_helpType_transferPrameter()){
						if(Verification.isNumeric(paramIter.getValue().toString())){
							if(paramIter.getValue().toString().length() <=18){
								helpTypeId = Long.parseLong(paramIter.getValue().toString());	
							}
						}
						
					}
				}
			}
		}
		
		String requestURI = "";
		String queryString = "";
		//获取运行时参数
		if(runtimeParameter != null && runtimeParameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : runtimeParameter.entrySet()) {
				if("requestURI".equals(paramIter.getKey())){
					requestURI = (String)paramIter.getValue();
				}else if("queryString".equals(paramIter.getKey())){
					queryString = (String)paramIter.getValue();
				}
			}
		}
		
		//调用分页算法代码
		PageView<Help> pageView = new PageView<Help>(maxResult,page,pageCount,requestURI,queryString);
		//当前页
		int firstIndex = (page-1)*pageView.getMaxresult();

		//执行查询
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();
		
		//帮助分类Id
		if(helpTypeId != null){
			jpql.append(" and o.helpTypeId=?"+ (params.size()+1));
			params.add(helpTypeId);//加上查询参数
			
		}
		jpql.append(" and o.visible=?"+ (params.size()+1));
		params.add(true);//设置o.visible=?1是否可见
		
		
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		//排行依据
		if(sort == 1){
			orderby.put("id", "desc");//发布时间排序   新-->旧
		}else if(sort == 2){
			orderby.put("id", "asc");//发布时间排序  旧-->新
		}	
		//删除第一个and
		String jpql_str = StringUtils.difference(" and", jpql.toString());
		QueryResult<Help> qr = helpService.getScrollData(Help.class,firstIndex, maxResult, jpql_str, params.toArray(),orderby);
		
		if(qr.getResultlist() != null && qr.getResultlist().size() >0){
			for(Help help: qr.getResultlist()){
				HelpType helpType = helpTypeService.findById(help.getHelpTypeId());
				if(helpType != null){
					help.setHelpTypeName(helpType.getName());
				}
				//处理富文本路径
				help.setContent(fileManage.processRichTextFilePath(help.getContent(),"help"));
			}
		}
		
		
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);
		return pageView;
	}
	
	/**
	 * 在线帮助列表  -- 集合
	 * @param forum 版块对象
	 * @param parameter 参数
	 */
	public List<Help> help_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){	
		List<Help> helpList = new ArrayList<Help>();
		
		Long helpTypeId = null;
		//获取参数
		if(parameter != null && parameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				if("helpTypeId".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=18){
							helpTypeId = Long.parseLong(paramIter.getValue().toString());	
						}
					}
				}
			}
		}
		if(helpTypeId != null && helpTypeId >0L){
			List<Help> _helpList = helpService.findByTypeId(helpTypeId);
			if(_helpList != null && _helpList.size() >0){
				helpList = _helpList;
			}
		}
		return helpList;
	}
	
	
	/**
	 * 在线帮助-- 推荐在线帮助 -- 集合
	 * @param forum 版块对象
	 * @param parameter 参数
	 */
	public List<Help> recommend_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){	
		List<Help> helpList = new ArrayList<Help>();
		
		String formValueJSON = forum.getFormValue();//表单值
		if(formValueJSON != null && !"".equals(formValueJSON)){
			Forum_HelpRelated_Help forum_HelpRelated_Help = JsonUtils.toObject(formValueJSON,Forum_HelpRelated_Help.class);
			if(forum_HelpRelated_Help != null){
				List<Help> recommendHelpList = forum_HelpRelated_Help.getHelp_recommendHelpList();
				if(recommendHelpList != null && recommendHelpList.size() >0){
					for(int i = 0; i< recommendHelpList.size(); i++){
						Help help = helpService.findById(recommendHelpList.get(i).getId());
						if(help != null){
							helpList.add(help);
						}
					}
				}
			}
			
		}
		return helpList;
	}
	
	/**
	 * 在线帮助-- 在线帮助分类 -- 集合
	 * @param forum 版块对象
	 * @param parameter 参数
	 */
	public List<HelpType> type_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){	
		List<HelpType> helpTypeList = new ArrayList<HelpType>();
		
		List<HelpType> allHelpType = helpTypeService.findAllHelpType();

		for(HelpType helpType : allHelpType){
			if(helpType.getChildNodeNumber() >0){//有子节点
				List<HelpType> childHelpType = this.queryType(allHelpType,helpType.getId());
				if(childHelpType != null && childHelpType.size() >0){
					helpType.setChildHelpType(childHelpType);
				}
			}
		}
		for(HelpType helpType : allHelpType){
			if(helpType.getParentId().equals(0L)){//加入所有父节点
				helpTypeList.add(helpType);
			}
			//排序
			this.helpTypeSort(helpTypeList);
		}
		
		return helpTypeList;
	}
	
	/**
	 * 根据父Id查询分类
	 * @param allHelpType
	 * @param parentId 父Id
	 * @return
	 */
	private List<HelpType> queryType(List<HelpType> allHelpType,Long parentId){
		List<HelpType> helpTypeList = new ArrayList<HelpType>();
		for(HelpType helpType : allHelpType){
			if(helpType.getParentId().equals(parentId)){//加入所有父节点
				helpTypeList.add(helpType);
			}
			//排序
			this.helpTypeSort(helpTypeList);
		}
		return helpTypeList;
	}
	/**
	 * 在线帮助分类排序
	 * @param helpTypeList
	 */
	private void helpTypeSort(List<HelpType> helpTypeList){
		//排序，防止更新时数据死锁，从小到大排序
        Collections.sort(helpTypeList, new Comparator<HelpType>(){        
			@Override
			public int compare(HelpType o1,
					HelpType o2) {
				 return o2.getSort().compareTo(o1.getSort());   
			} 
        });
	}
	
	
	/**
	 * 在线帮助-- 在线帮助导航 -- 集合
	 * @param forum 版块对象
	 * @param parameter 参数
	 */
	public Map<Long,String> navigation_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		Long helpTypeId = null;
		Map<Long,String> navigation = new LinkedHashMap<Long,String>();
		if(parameter != null && parameter.size() >0){
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				if("helpTypeId".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=18){
							helpTypeId = Long.parseLong(paramIter.getValue().toString());	
						}
					}
				}
			}
		}
		if(helpTypeId != null && helpTypeId > 0){
			HelpType helpType = helpTypeService.findById(helpTypeId);
			if(helpType != null){
				List<HelpType> parentHelpTypeList = helpTypeService.findAllParentById(helpType);
				for(HelpType p : parentHelpTypeList){
					navigation.put(p.getId(), p.getName());
				}
				navigation.put(helpType.getId(), helpType.getName());
			}
			
		}
		
		return navigation;
	}
	
	/**
	 * 在线帮助-- 在线帮助内容 -- 实体对象
	 * @param forum 版块对象
	 * @param parameter 参数
	 */
	public Help content_entityBean(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		
		Long helpId = null;
		if(parameter != null && parameter.size() >0){
			for(Map.Entry<String,Object> paramIter : parameter.entrySet()) {
				if("helpId".equals(paramIter.getKey())){
					if(Verification.isNumeric(paramIter.getValue().toString())){
						if(paramIter.getValue().toString().length() <=18){
							helpId = Long.parseLong(paramIter.getValue().toString());	
						}
					}
				}
			}
		}
		
		if(helpId != null && helpId > 0){
			Help help = helpService.findById(helpId);
			if(help != null){
				HelpType helpType = helpTypeService.findById(help.getHelpTypeId());
				if(helpType != null){
					help.setHelpTypeName(helpType.getName());
				}
				//处理富文本路径
				help.setContent(fileManage.processRichTextFilePath(help.getContent(),"help"));
				return help;
			}
			
		}
		return null;
	}
}
