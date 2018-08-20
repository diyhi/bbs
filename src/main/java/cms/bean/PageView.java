package cms.bean;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.queryString.util.MultiMap;
import org.queryString.util.UrlEncoded;

import cms.web.taglib.Configuration;


/**
 * 通用页面分页显示类
 *
 */
public class PageView<T> implements Serializable{
	private static final long serialVersionUID = 8989558282198897558L;

	private static final Logger logger = LogManager.getLogger(PageView.class);
	
	/** 显示记录集,即分页数据 **/
	private List<T> records;
	/** 页码开始索引和结束索引 **/
	private PageIndex pageindex;
	/** 总页数 **/
	private long totalpage = 1;
	/** 每页显示记录数 **/
	private int maxresult = 12;
	/** 当前页 **/
	private int currentpage = 1;
	/** 总记录数 **/
	private long totalrecord;
	/** 页码显示总数 **/
	private long pagecount = 10;
	/** 要获取记录的开始索引 **/
	public int getFirstResult() {
		return (this.currentpage-1)*this.maxresult;
	}
	/** URI **/
	public String requestURI;
	/** URL参数 **/
	public String queryString;
	/** 上一页URL **/
	public String getOnUrl(){
		String uri = "";
		//删除系统虚拟路径
		if(this.getRequestURI()!= null && !"".equals(this.getRequestURI())){
			//删除第一个
			uri = StringUtils.difference(Configuration.getPath()+"/", this.getRequestURI());
		}

		String queryString = this.queryString();
		if(queryString != null && !"".equals(queryString.trim())){
			return uri+"?"+queryString+"&page="+(this.getCurrentpage()-1);
		}
		return uri+"?page="+(this.getCurrentpage()-1);
		
		
	}
	/** 下一页URL **/
	public String getNextUrl(){
		String uri = "";
		//删除系统虚拟路径
		if(this.getRequestURI()!= null && !"".equals(this.getRequestURI())){
			//删除第一个
			uri = StringUtils.difference(Configuration.getPath()+"/", this.getRequestURI());

		}
		String queryString = this.queryString();
		if(queryString != null && !"".equals(queryString.trim())){
			return uri+"?"+queryString+"&page="+(this.getCurrentpage()+1);
		}else{
			return uri+"?page="+(this.getCurrentpage()+1);
		}
	}
	
	
	
	
	/**
	 * 通过构造函数强制new这两个对象时一定要对这两个函数赋值,因为"每页显示记录数"和"当前页"不能为空值
	 * @param maxresult 每页显示记录数
	 * @param currentpage 当前页
	 * @param pagecount 页码显示总数
	 */
	public PageView(int maxresult, int currentpage, int pagecount) {
		super();
		this.maxresult = maxresult;
		this.currentpage = currentpage;
		this.pagecount = pagecount;
	}
	/**
	 * 通过构造函数强制new这两个对象时一定要对这两个函数赋值,因为"每页显示记录数"和"当前页"不能为空值
	 * @param maxresult 每页显示记录数
	 * @param currentpage 当前页
	 * @param pagecount 页码显示总数
	 * @param queryString URL参数
	 */
	public PageView(int maxresult, int currentpage, int pagecount,String requestURI,String queryString) {
		super();
		this.maxresult = maxresult;
		this.currentpage = currentpage;
		this.pagecount = pagecount;
		this.requestURI = requestURI;
		this.queryString = queryString;
	}
	/**
	 * 设置查询结果
	 * @param qr
	 */
	public void setQueryResult(QueryResult qr){
		setTotalrecord(qr.getTotalrecord());
		setRecords(qr.getResultlist());
	}
	
	
	private String queryString(){
		//删除page参数的URL参数
		String queryString = "";
		
		if(this.getQueryString() != null && !"".equals(this.getQueryString())){
       		MultiMap<String> values = new MultiMap<String>(); 
       		
	       	UrlEncoded.decodeTo(this.getQueryString(), values, "UTF-8");
	       	Iterator iter = values.entrySet().iterator();  
	       	while(iter.hasNext()){  
	       		Map.Entry e = (Map.Entry)iter.next();
	       		if("page".equalsIgnoreCase(e.getKey().toString())){
       				continue;
       			}
	       		if(e.getValue() instanceof List){
	       			
	       			if(e.getValue() != null){
	       				List<String> valueList = (List)e.getValue();
	       				
		       			for(String parameters : valueList){
		       				try {
								queryString+= "&"+e.getKey()+"="+URLEncoder.encode( parameters, "UTF-8");
							} catch (UnsupportedEncodingException e1) {
							//	e1.printStackTrace();
								if (logger.isErrorEnabled()) {
						            logger.error("URL集合参数编码错误",e1);
						        }
							}
		       			}
	       			}
       				
	       		}
	       		if(e.getValue() instanceof String){      				
       				String value = e.getValue().toString().trim();
   					if(value != null && !"".equals(value)){
   						try {
							queryString+= "&"+e.getKey()+"="+URLEncoder.encode(value, "UTF-8");
						} catch (UnsupportedEncodingException e1) {
						//	e1.printStackTrace();
							if (logger.isErrorEnabled()) {
					            logger.error("URL参数编码错误",e1);
					        }
						}
   					}
	       		}
	       	}
	       	//删除第一个&
   			queryString = StringUtils.difference("&", queryString);
	    }
		return queryString;
	}
	
	
	
	public List<T> getRecords() {
		return records;
	}
	public void setRecords(List<T> records) {
		this.records = records;
	}
	public PageIndex getPageindex() {
		return pageindex;
	}
	public long getTotalpage() {
		return totalpage;
	}
	public void setTotalpage(long totalpage) {
		this.totalpage = totalpage;
		this.pageindex = WebTool.getPageIndex(pagecount, currentpage, totalpage);
	}
	public int getMaxresult() {
		return maxresult;
	}
	
	public int getCurrentpage() {
		return currentpage;
	}
	
	public long getTotalrecord() {
		return totalrecord;
	}
	public void setTotalrecord(long totalrecord) {
		this.totalrecord = totalrecord;
		setTotalpage( this.totalrecord%this.maxresult==0? this.totalrecord/this.maxresult : this.totalrecord/this.maxresult+1);
	}
	public long getPagecount() {
		return pagecount;
	}
	public void setPagecount(long pagecount) {
		this.pagecount = pagecount;
	}
	public String getQueryString() {
		return queryString;
	}
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	public String getRequestURI() {
		return requestURI;
	}
	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}
	
	
	
}
