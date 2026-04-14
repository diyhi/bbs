package cms.config;

import java.util.List;
import freemarker.cache.TemplateLoader;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

/**
 * 模板转义
 * @author Gao
 *
 */

public class MyFreeMarkerConfigurer extends FreeMarkerConfigurer{
	@Override  
    protected TemplateLoader getAggregateTemplateLoader(List<TemplateLoader> templateLoaders) {  
        return new HtmlTemplateLoader(super.getAggregateTemplateLoader(templateLoaders));  
  
    }
}
