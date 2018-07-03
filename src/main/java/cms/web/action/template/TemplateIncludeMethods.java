package cms.web.action.template;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import cms.utils.threadLocal.TemplateThreadLocal;
import freemarker.core.Environment;
import freemarker.core.ParseException;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 自定义引入指令Include
 *
 */
public class TemplateIncludeMethods implements TemplateDirectiveModel {
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
		
		String modulePath = "";//模块路径      templates/bbb/Forum/ProductRelated_Product_Multilayer_Default.html
        String enc = "UTF-8";
        boolean parse = true;
        
        if (!params.isEmpty()) {
			Iterator paramIter = params.entrySet().iterator();
			while (paramIter.hasNext()) {
				Map.Entry ent = (Map.Entry) paramIter.next();
				if(ent.getKey() != null && !"".equals(ent.getKey().toString().trim())){
					if("action".equals(ent.getKey())){
							String action = ent.getValue().toString().trim();
							if(action != null && !"".equals(action)){
								String[] action_arr = action.split("\\|");//按|号分割
								referenceCode = action_arr[0];
								modulePath = action_arr[1];
							}
					}else{
						//将引入参数添加到ThreadLocal
						if(ent.getValue() != null && !"".equals(ent.getValue().toString().trim())){
							TemplateThreadLocal.addParameter(ent.getKey().toString(), ent.getValue().toString().trim());
						}
						
					}
				}
			}
		}
        
      //将当前正在运行的引入指令设进模板参数里面
        TemplateThreadLocal.setReferenceCode(referenceCode);
        
        Template includedTemplate;
        try {
            includedTemplate = env.getTemplateForInclusion(modulePath, enc, parse);
        }
        catch (ParseException pe) {
            String msg = "错误解析包含的模板 "
                        + modulePath  + "\n" + pe.getMessage();
            throw new TemplateException(msg, pe, env);
        }
        catch (IOException ioe) {
            String msg = "包含的文件读取错误 "
                        + modulePath;
            throw new TemplateException(msg, ioe, env);
        }
        env.include(includedTemplate);

	}
}
