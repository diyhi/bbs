package cms.utils.threadLocal;

import java.util.List;
import java.util.Map;

import cms.bean.template.TemplateRunObject;

/**
 * 模板参数传递
 * map 参数 key:Run_Include_Instruction 引用代码;   key:LayoutFile 布局文件  value:List类型
 *
 */
public class TemplateThreadLocal {
	
	public static ThreadLocal<TemplateRunObject> holder = new ThreadLocal<TemplateRunObject>(); 
	
	
	
	/**
	 * 设置布局文件名称
	 * @param layoutFile 布局文件名称
	 */
	public static void setLayoutFile(String layoutFile){
		TemplateRunObject hr = holder.get();
		if(hr == null){
			hr = new TemplateRunObject();
		}
		hr.addLayoutFile(layoutFile);
		holder.set(hr);
	}
	/**
	 * 设置布局文件名称
	 * @param layoutFileList 布局文件名称集合
	 */
	public static void setLayoutFile(List<String> layoutFileList){
		TemplateRunObject hr = holder.get();
		if(hr == null){
			hr = new TemplateRunObject();
		}
		hr.addAllLayoutFile(layoutFileList);
		holder.set(hr);
	}
	/**
	 * 设置正在运行版块的引用代码
	 * @param referenceCode 正在运行版块的引用代码
	 */
	public static void setReferenceCode(String referenceCode){
		TemplateRunObject hr = holder.get();
		if(hr == null){
			hr = new TemplateRunObject();
		}
		hr.setReferenceCode(referenceCode);
		holder.set(hr);
	}
	/**
	 * 添加提交参数
	 * @param key 参数名
	 * @param value 参数值
	 */
	public static void addParameter(String key,Object value){
		TemplateRunObject hr = holder.get();
		if(hr == null){
			hr = new TemplateRunObject();
		}
		hr.addParameter(key, value);
		holder.set(hr);
	}
	/**
	 * 获取提交参数
	 * @param key 参数名
	 */
	public static Object getParameter(String key){
		TemplateRunObject hr = holder.get();
		if(hr != null){
			Map<String, Object> param = hr.getParameter();
			if(param != null && param.size() >0){
				return param.get(key);
			}
		}
		return null;
	}
	
	
	/**
	 * 添加提交参数
	 * @param key 参数名
	 * @param value 参数值
	 */
	public static void addParameter(Map<String,Object> parameter){
		TemplateRunObject hr = holder.get();
		if(hr == null){
			hr = new TemplateRunObject();
		}
		hr.addParameter(parameter);
		holder.set(hr);
	}
	
	/**
	 * 添加运行参数
	 * @param key 参数名
	 * @param value 参数值
	 */
	public static void addRuntimeParameter(String key,Object value){
		TemplateRunObject hr = holder.get();
		if(hr == null){
			hr = new TemplateRunObject();
		}
		hr.addRuntimeParameter(key, value);
		holder.set(hr);
	}
	/**
	 * 获取运行参数
	 * @param key 参数名
	 */
	public static Object getRuntimeParameter(String key){
		TemplateRunObject hr = holder.get();
		if(hr != null){
			Map<String, Object> param = hr.getRuntimeParameter();
			if(param != null && param.size() >0){
				return param.get(key);
			}
		}
		return null;
	}
	
	
	/**
	 * 设置'更多'版块 
	 * @param more_forum '更多'版块 
	
	public static void setMore_forum(Forum more_forum){
		TemplateRunObject hr = holder.get();
		if(hr == null){
			hr = new TemplateRunObject();
		}
		hr.setMore_forum(more_forum);
		holder.set(hr);
	} */
	/**
	 * 设置参数
	 * @param 模板运行对象
	*/
	public static void set(TemplateRunObject templateRunObject){
		holder.set(templateRunObject);
	} 
	public static TemplateRunObject get() {
		TemplateRunObject hr = holder.get();
		return hr;
	}
	/**
	 * cms.web.filter.TempletesInterceptor中删除
	 */
	public static void removeThreadLocal() {
		//一定要清理，否则就是线程不安全的，因为服务器都是有线程池的。 
		set(null); 
		holder.remove();
	}
}
