package cms.web.action.template.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.template.Advert;
import cms.bean.template.Forum;
import cms.bean.template.Forum_AdvertisingRelated_Image;
import cms.utils.JsonUtils;

/**
 * 广告 -- 模板方法实现
 *
 */
@Component("advertising_TemplateManage")
public class Advertising_TemplateManage {
	/**
	 * 推荐广告
	 * @param forum
	 * @param parameter
	 * @return
	 */
	public List<Advert> recommend_collection_image(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		List<Advert> advertList = new ArrayList<Advert>();
		
		String formValueJSON = forum.getFormValue();//表单值
		if(formValueJSON != null && !"".equals(formValueJSON)){
			
			
			List<Forum_AdvertisingRelated_Image> forum_AdvertisingRelated_ImageList = JsonUtils.toGenericObject(formValueJSON, new TypeReference< List<Forum_AdvertisingRelated_Image> >(){});
			if(forum_AdvertisingRelated_ImageList != null && forum_AdvertisingRelated_ImageList.size() >0){
				for(Forum_AdvertisingRelated_Image forum_AdvertisingRelated_Image : forum_AdvertisingRelated_ImageList){
					Advert advert = new Advert();
					advert.setName(forum_AdvertisingRelated_Image.getImage_name());
					advert.setLink(forum_AdvertisingRelated_Image.getImage_link());
					
					advert.setPath(forum_AdvertisingRelated_Image.getImage_filePath()+forum.getDirName()+"/"+forum_AdvertisingRelated_Image.getImage_fileName());
					advertList.add(advert);
				}
				
				
			}
		}
		return advertList;
	}
}
