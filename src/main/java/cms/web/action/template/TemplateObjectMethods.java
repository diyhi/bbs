package cms.web.action.template;


import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import cms.bean.template.Forum;
import cms.bean.template.TemplateRunObject;
import cms.service.template.TemplateService;
import cms.utils.threadLocal.TemplateThreadLocal;
import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 模板调用对象自定义函数
 * 使用方法 
 *
 */
public class TemplateObjectMethods implements TemplateDirectiveModel{
	@Resource TemplateService templateService;
	@Resource TemplateMain templateMain;

	
	/**
	 * @param env
	 * @param params 参数
	 * @param loopVars 循环变量
	 * @param body 指令内容体
	 */
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {

		String referenceCode = "";//版块引用代码   ProductRelated_Product_3
		String run_referenceCode = "";//正在运行的版块引用代码
		String modulePath = "";//模块路径      templates/bbb/Forum/ProductRelated_Product_Multilayer_Default.html
        String enc = "UTF-8";
        boolean parse = true;
        
        TemplateRunObject templateRunObject = TemplateThreadLocal.get();
        Map<String,Object> parameter = new HashMap<String,Object>();//提交参数
        Map<String,Object> runtimeParameter = new HashMap<String,Object>();//运行时参数
		
        
        //取得<@object parameter...></@object>的参数
        if (!params.isEmpty()) {
			Iterator paramIter = params.entrySet().iterator();
			while (paramIter.hasNext()) {
				Map.Entry ent = (Map.Entry) paramIter.next();
				if("action".equals(ent.getKey())){
					String action = ent.getValue().toString().trim();
					if(action != null && !"".equals(action)){
						String[] action_arr = action.split("\\|");//按|号分割
						referenceCode = action_arr[0];
						modulePath = action_arr[1];
					}
				}else{
					if(ent.getValue() != null && !"".equals(ent.getValue().toString().trim())){
						parameter.put(ent.getKey().toString(), ent.getValue().toString().trim());
					}
				}
			}
		}
        
        //取得当前正在运行的引入指令
	    if(templateRunObject != null){
	    	 if(templateRunObject.getRuntimeParameter() != null && templateRunObject.getRuntimeParameter().size() >0){
	    		 
	    		 runtimeParameter.putAll(templateRunObject.getRuntimeParameter());
	    	 }	  
	     }

        String templatesDir = templateService.findTemplateDir_cache();
        if(referenceCode != null && !"".equals(referenceCode)){
		    //模板路径名称(env.getTemplate().getName()方法已过时)
        	String templatePathName = env.getCurrentTemplate().getName();
		   
		    //模板文件名称  StringUtils.lastIndexOfIgnoreCase从后往前查，不区分大小写
			String name = templatePathName.substring(StringUtils.lastIndexOfIgnoreCase(templatePathName, "/")+1,templatePathName.length());//含有后缀
			Forum forum = templateService.findForum_cache(templatesDir,referenceCode);
	    	if(forum != null){
	    		StringBuffer modulePrefix = new StringBuffer("");//版块模板前缀     ProductRelated_Product_Page
	    		String[] module_str = forum.getModule().split("_");
	    		int i = 0;
	    		for(String str :module_str){
	    			modulePrefix.append(str);
	    			if(i<2){
	    				modulePrefix.append("_");
	    			}else{
	    				break;
	    			}
	    			i++;
	    		}
	    		
	    		if(name.equals(forum.getLayoutFile()) && !forum.getLayoutType().equals(6) && forum.getInvokeMethod().equals(2)){	
	    			env.setVariable(modulePrefix.toString(), getBeansWrapper().wrap(templateMain.templateObject(forum,parameter,runtimeParameter)));//引用代码为key  如:Product_Show_1 
			    	if(body != null){
			    		 body.render(env.getOut()); 
			    	}
		    	
		    	}
		    }
		
		}
	}
	
	private static BeansWrapper getBeansWrapper(){
		//创建builder:
	    BeansWrapperBuilder builder = new BeansWrapperBuilder(Configuration.getVersion());
	    // 设置所需的beanswrapper属性
	    //builder.setUseModelCache(true);是否启用缓存
	    //builder.setExposeFields(true);//是否启用返回类的公共实例。
	    builder.setSimpleMapWrapper(true);//模板能使用Map方法
	    // Get the singleton:
	    BeansWrapper beansWrapper = builder.build();
		
		return beansWrapper;
	} 
}
