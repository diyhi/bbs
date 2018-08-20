package cms.web.action.data;

import java.util.List;

import cms.bean.data.TableProperty;

/**
 * 计算线程数
 *
 */
public class  Threads {
	public static Integer count = 0;//线程数
	public static List<TableProperty> tablePropertyList = null;//数据库属性
	/**
	 * 线程数计算
	 */
	public static synchronized int calculation(Integer i){
		if(i == 1){
			count++;
		}else if(i == 0){
			count = 0;
		}else{
			count--;
		}
		
		return count;
	}

}
