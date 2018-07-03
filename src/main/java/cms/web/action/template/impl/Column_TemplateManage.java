package cms.web.action.template.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import cms.bean.template.Column;
import cms.bean.template.Forum;
import cms.web.action.template.ColumnManage;

import org.springframework.stereotype.Component;

/**
 * 站点栏目 -- 模板方法实现
 *
 */
@Component("column_TemplateManage")
public class Column_TemplateManage {
	
	@Resource ColumnManage columnManage;
	/**
	 * 站点栏目列表 -- 集合
	 * @param forum
	 */
	public List<Column> column_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		List<Column> columnList = columnManage.columnList_cache(forum.getDirName());
		return columnList;
	}
}
