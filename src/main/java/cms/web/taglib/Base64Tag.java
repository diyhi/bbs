package cms.web.taglib;

import java.util.List;

import cms.utils.Base64;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * Freemarker自定义函数
 * Base64 标签
 * 使用方法 ${encodeBase64URL("http://www.a.com/news/showNews.sf?id=123456")}  
 * 
 *
 */
public class Base64Tag implements TemplateMethodModelEx{

	/**
	 * paramList 参数集合
	 */
	@Override
	public Object exec(List paramList) throws TemplateModelException {
		if(paramList.size()!=1){  //限定方法中必须且只能传递一个参数
			throw new TemplateModelException("参数不正确!");
		}
		return Base64.encodeBase64URL(String.valueOf(paramList.get(0)));
	}


}
