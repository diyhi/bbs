package cms.web.taglib;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * Freemarker自定义方法
 * 实现response.encodeURL(url)功能
 */
public class EncodeURL implements TemplateMethodModelEx {
	
	private HttpServletResponse response;
		
	/**
	 * 带参数的构造函数
	 * @param response HttpServletResponse对象
	 */
	public EncodeURL(HttpServletResponse response){
		this.response=response;
	}

	/**
	 * 执行方法
	 * @param argList 方法参数列表
	 * @return Object 方法返回值
	 * @throws TemplateModelException
	 */
	public Object exec(List argList) throws TemplateModelException {
		if(argList.size()!=1){  //限定方法中必须且只能传递一个参数
			throw new TemplateModelException("参数不正确!");
		}
		//返回response.encodeURL执行结果
		return response.encodeURL(String.valueOf(argList.get(0)));
	}
}
