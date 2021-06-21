package cms.bean.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cms.bean.help.Help;

/**
 * 版块---在线帮助相关--在线帮助
 *
 */
public class Forum_HelpRelated_Help implements Serializable{
	private static final long serialVersionUID = 5331713665377268678L;

	/** 版块---在线帮助相关--在线帮助  Id **/
	private String help_id;
	
	/** 在线帮助展示数量 **/
	private Integer help_quantity;
	/** 排序 **/
	private Integer help_sort;
	/** 更多 **/
	private String help_more;
	/** 更多选中文本 **/
	private String help_moreValue;
	
	/** 每页显示记录数 **/
	private Integer  help_maxResult;
	/** 页码显示总数 **/
	private Integer  help_pageCount;
	
	/** 分类Id **/
	private Long help_helpTypeId;
	/** 分类名称 **/
	private String help_helpTypeName;
	/** 是否传递分类参数 **/
	private boolean help_helpType_transferPrameter = false;
	
	/** 选择推荐在线帮助 只存储Id,Name **/
	private List<Help> help_recommendHelpList = new ArrayList<Help>();

	public String getHelp_id() {
		return help_id;
	}

	public void setHelp_id(String help_id) {
		this.help_id = help_id;
	}

	public Integer getHelp_quantity() {
		return help_quantity;
	}

	public void setHelp_quantity(Integer help_quantity) {
		this.help_quantity = help_quantity;
	}

	public Integer getHelp_sort() {
		return help_sort;
	}

	public void setHelp_sort(Integer help_sort) {
		this.help_sort = help_sort;
	}

	public String getHelp_more() {
		return help_more;
	}

	public void setHelp_more(String help_more) {
		this.help_more = help_more;
	}

	public String getHelp_moreValue() {
		return help_moreValue;
	}

	public void setHelp_moreValue(String help_moreValue) {
		this.help_moreValue = help_moreValue;
	}

	public Integer getHelp_maxResult() {
		return help_maxResult;
	}

	public void setHelp_maxResult(Integer help_maxResult) {
		this.help_maxResult = help_maxResult;
	}

	public Integer getHelp_pageCount() {
		return help_pageCount;
	}

	public void setHelp_pageCount(Integer help_pageCount) {
		this.help_pageCount = help_pageCount;
	}

	public Long getHelp_helpTypeId() {
		return help_helpTypeId;
	}

	public void setHelp_helpTypeId(Long help_helpTypeId) {
		this.help_helpTypeId = help_helpTypeId;
	}

	public String getHelp_helpTypeName() {
		return help_helpTypeName;
	}

	public void setHelp_helpTypeName(String help_helpTypeName) {
		this.help_helpTypeName = help_helpTypeName;
	}

	public boolean isHelp_helpType_transferPrameter() {
		return help_helpType_transferPrameter;
	}

	public void setHelp_helpType_transferPrameter(
			boolean help_helpType_transferPrameter) {
		this.help_helpType_transferPrameter = help_helpType_transferPrameter;
	}

	public List<Help> getHelp_recommendHelpList() {
		return help_recommendHelpList;
	}

	public void setHelp_recommendHelpList(List<Help> help_recommendHelpList) {
		this.help_recommendHelpList = help_recommendHelpList;
	}

	
	
}
