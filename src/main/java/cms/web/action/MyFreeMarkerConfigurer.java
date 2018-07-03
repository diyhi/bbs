package cms.web.action;

import java.util.List;

import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.cache.TemplateLoader;

/**
 * FreeMarker模板配置
 *
 */
public class MyFreeMarkerConfigurer extends FreeMarkerConfigurer{
	
	@Override  
    protected TemplateLoader getAggregateTemplateLoader(List<TemplateLoader> templateLoaders) {  
  
        return new HtmlTemplateLoader(super.getAggregateTemplateLoader(templateLoaders));  
  
    }  
}
