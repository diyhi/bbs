package cms.web.action.template;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.template.Column;
import cms.bean.template.Templates;
import cms.service.template.TemplateService;
import cms.utils.JsonUtils;

/**
 * 栏目管理
 *
 */

@Component("columnManage")
public class ColumnManage {
	@Resource TemplateService templateService;
	
	/**
	 * 栏目列表
	 * @param dirName 模板目录名称
	 * @return
	 */
	public List<Column> columnList(String dirName){
		List<Column> columnList = new ArrayList<Column>();

		Templates templates = templateService.findTemplatebyDirName(dirName);
		if(templates != null){
			String columnList_json = templates.getColumns();
			if(columnList_json != null && !"".equals(columnList_json)){
				columnList = JsonUtils.toGenericObject(columnList_json,new TypeReference<List<Column>>(){});
				
			}
		}
		return columnList;
	}
	
	/**
	 * 栏目列表 - 缓存
	 * @param dirName 模板目录名称
	 * @return
	 */
	public List<Column> columnList_cache(String dirName){
		List<Column> columnList = new ArrayList<Column>();

		Templates templates = templateService.findTemplatebyDirName_cache(dirName);
		if(templates != null){
			String columnList_json = templates.getColumns();
			if(columnList_json != null && !"".equals(columnList_json)){
				columnList = JsonUtils.toGenericObject(columnList_json,new TypeReference<List<Column>>(){});
				
			}
		}
		return columnList;
	}
	
	/**
	 * 添加栏目
	 * @param column
	 * @param dirName
	 * @return
	 */
	public Integer addColumn(Column column,String dirName){
		List<Column> columnList = columnList(dirName);
		TreeSet<Integer> columnIdList = this.columnIdList(columnList,new TreeSet<Integer>());
		//最大号码
		int maxId = 0;
		if(columnIdList != null && columnIdList.size() >0){
			maxId = columnIdList.last();
		}
		maxId++;
		column.setId(maxId);
		if(column.getLinkMode().equals(4)){
			column.setUrl("column/"+maxId);
		}
		
		
		if(column.getParentId() >0){//有父栏目
			this.addColumnIdList(columnList,column);
		}else{
			columnList.add(column);
		}
		
		//栏目排序
		this.columnSort(columnList);
		int i = templateService.updateColumn(JsonUtils.toJSONString(columnList), dirName);
		if(i >0){
			return maxId;
		}
		return -1;
	}
	
	/**
	 * 栏目排序
	 * @param columnList
	 * @return
	 */
	private List<Column> columnSort(List<Column> columnList){
		Collections.sort(columnList, new Comparator<Column>(){
			@Override
			public int compare(Column o1, Column o2) {
				int s1 = o1.getSort();
				int s2 = o2.getSort();
				if(s1 < s2){
        			return 1;
        		}else{
        			if(s1 == s2){
            			return 0;
            		}else{
            			return -1;
            		}
        		}  
			}   
		});
		if(columnList != null && columnList.size() >0){
			for(Column column : columnList){
				if(column.getChildColumn() != null && column.getChildColumn().size() >0){
					columnSort(column.getChildColumn());
				}
				
			}
		}
		
		return columnList;
	}
	
	/**
	 * 修改栏目
	 * @param column
	 * @param dirName
	 * @return
	 */
	public List<Column> updateColumn(Column column,String dirName){
		List<Column> columnList = this.columnList(dirName);
		List<Column> newColumnList = recursionUpdateColumn(columnList,column);
		//栏目排序
		this.columnSort(newColumnList);
		String newColumnList_json = JsonUtils.toJSONString(newColumnList);
		
		int i = templateService.updateColumn(newColumnList_json, dirName);
		if(i >0){
			return newColumnList;
		}
		return null;
	}
	
	/**
	 * 删除栏目
	 * @param column
	 * @param dirName
	 * @return
	 */
	public List<Column> deleteColumn(Integer columnId,String dirName){
		List<Column> columnList = this.columnList(dirName);
		List<Column> newColumnList = recursionDeleteColumn(columnList,columnId);
		String newColumnList_json = JsonUtils.toJSONString(newColumnList);
		int i = templateService.updateColumn(newColumnList_json, dirName);
		if(i >0){
			return newColumnList;
		}
		return null;
	}
	
	/**
	 * 递归修改栏目
	 * @param columnList
	 * @param newColumn
	 * @return
	 */
	private List<Column> recursionUpdateColumn(List<Column> columnList,Column newColumn){
		if(columnList != null && columnList.size() >0){
			for(Column column : columnList){
				if(column.getId().equals(newColumn.getId())){
					column.setName(newColumn.getName());
					column.setSort(newColumn.getSort());
					column.setLinkMode(newColumn.getLinkMode());
					column.setUrl(newColumn.getUrl());
					if(column.getLinkMode().equals(4)){//空白页
						//栏目空白页访问路径
						column.setUrl("column/"+column.getId());
					}
					
					break;
				}
				
				if(column.getChildColumn() != null && column.getChildColumn().size() >0){
					recursionUpdateColumn(column.getChildColumn(),newColumn);
				}
				
			}
		}
		return columnList;
	}
	/**
	 * 递归删除栏目
	 * @param columnList
	 * @param columnId
	 * @return
	 */
	private List<Column> recursionDeleteColumn(List<Column> columnList,Integer columnId){
		if(columnList != null && columnList.size() >0){
			for(Column column : columnList){
				if(column.getId().equals(columnId)){
					columnList.remove(column);
					break;
				}
				
				if(column.getChildColumn() != null && column.getChildColumn().size() >0){
					recursionDeleteColumn(column.getChildColumn(),columnId);
				}
				
			}
		}
		return columnList;
		
		
	}
	
	/**
	 * 根据Id查询栏目
	 * @param columnId 栏目Id
	 * @return
	 */
	public Column queryColumnById(Integer columnId,String dirName){
		List<Column> columnList = this.columnList(dirName);
		if(columnList != null && columnList.size() >0){
			Column column = getColumn(columnList,columnId);
			return column;
		}
		return null;
	}
	
	
	
	/**
	 * 递归获取所有栏目Id
	 * @param columnList
	 * @param id
	 * @return
	 */
	public TreeSet<Integer> columnIdList(List<Column> columnList,TreeSet<Integer> id){
		if(columnList != null && columnList.size() >0){
			for(Column column : columnList){
				id.add(column.getId());
				if(column.getChildColumn() != null && column.getChildColumn().size() >0){
					columnIdList(column.getChildColumn(),id);
				}
				
			}
		}
		return id;
	}
	/**
	 * 递归添加到栏目
	 * @param columnList
	 * @param new_column 新子栏目
	 * @return
	 */
	private List<Column> addColumnIdList(List<Column> columnList,Column new_column){
		if(columnList != null && columnList.size() >0){
			for(Column column : columnList){
				if(column.getId().equals(new_column.getParentId())){
					column.addColumn(new_column);
					break;
				}
				
				if(column.getChildColumn() != null && column.getChildColumn().size() >0){
					addColumnIdList(column.getChildColumn(),new_column);
				}
				
			}
		}
		return columnList;
	}
	
	/**
	 * 递归获取栏目
	 * @param columnList
	 * @param columnId
	 * @return
	 */
	private Column getColumn(List<Column> columnList,Integer columnId){
		Column returnColumn = null;
		if(columnList != null && columnList.size() >0){
			
			for(Column column : columnList){
				if(column.getId().equals(columnId)){
					returnColumn = column;
				}
				if(returnColumn != null){
					break;
				}
				if(column.getChildColumn() != null && column.getChildColumn().size() >0){
					returnColumn = getColumn(column.getChildColumn(),columnId);
				}
				
			}
		}
		return returnColumn;
	}
	
	
	/**
	 * 判断URL地址是否合法
	 * @param url
	 * 1：该正则表达式匹配的字符串必须以http://、https://、ftp://开头；
	 * 2：该正则表达式能匹配URL或者IP地址；（如：http://www.baidu.com 或者 http://192.168.1.1）
	 * 3：该正则表达式能匹配到URL的末尾，即能匹配到子URL；（如能匹配：http://www.baidu.com/s?wd=a&rsv_spt=1&issp=1&rsv_bp=0&ie=utf-8&tn=baiduhome_pg&inputT=1236）
	 * 4：该正则表达式能够匹配端口号；
	 * @return
	 */
	public boolean validURL(String urlString){
	//	String regex = "((http|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&\\%_\\./-~-]*)?" ;
		String regex = "((http|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&\\%_\\./-~-\u4E00-\u9FFF]*)?" ;
		
		Pattern patt = Pattern. compile(regex);
		Matcher matcher = patt.matcher(urlString);
		boolean isMatch = matcher.matches();
		if (isMatch) {
		    return true;
		}else {
			return false;
		}
	}
	
}
