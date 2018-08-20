package cms.bean;

import java.io.Serializable;

/**
 * 分页计算页码算法
 *
 */
public class WebTool implements Serializable{
	private static final long serialVersionUID = -7793160890024962667L;

	/**
	 * 
	 * @param viewpagecount 页码显示总数
	 * @param currenPage 当前页
	 * @param totalpage 总页数
	 * @return
	 */
  public static PageIndex getPageIndex(long viewpagecount, int currenPage, long totalpage){
		long startpage = currenPage-(viewpagecount%2==0? viewpagecount/2-1 : viewpagecount/2);
		long endpage = currenPage+viewpagecount/2;
		if(startpage<1){
			startpage = 1;
			if(totalpage>=viewpagecount) endpage = viewpagecount;
			else endpage = totalpage;
		}
		if(endpage>totalpage){
			endpage = totalpage;
			if((endpage-viewpagecount)>0) startpage = endpage-viewpagecount+1;
			else startpage = 1;
		}
		return new PageIndex(startpage, endpage);		
  }
}
