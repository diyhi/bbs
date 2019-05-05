package cms.web.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import cms.bean.setting.EditorTag;
import cms.bean.topic.HideTagType;
import cms.utils.Verification;
import cms.web.taglib.Configuration;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

/**
 * 文本过滤管理
 *
 */
@Component("textFilterManage")
public class TextFilterManage {

	/**
	 * 过滤参数
	 * @return
	 */
	private Whitelist filterParameter(EditorTag editorTag){
			
		
	//	Whitelist whitelist = Whitelist.basicWithImages();

		Whitelist whitelist = Whitelist.none();//只保留文本，其他所有的html内容均被删除
		
		if(editorTag == null){
			//插入模板
			whitelist.addTags("h3");
			whitelist.addTags("table","tbody","tr","td")
				.addAttributes("table","style")
				.addAttributes("table","cellspacing")
				.addAttributes("table","cellpadding")
				.addAttributes("table","border")
				.addAttributes("td","style");
		}
		//隐藏标签
		if(editorTag == null){
			whitelist.addTags("hide")
				.addAttributes("hide","class")
				.addAttributes("hide","hide-type")
				.addAttributes("hide","input-value");
		}
		
		if(editorTag == null || editorTag.isJustifyleft()){//左对齐
			//左对齐
			whitelist.addTags("p")
			.addAttributes("p", "align");
			
			//vue-html5-editor
			whitelist.addTags("div")
			.addAttributes("div", "style");
		}
		if(editorTag == null || editorTag.isJustifycenter()){//居中
			//居中
			whitelist.addTags("p")
			.addAttributes("p", "align");
			
			//vue-html5-editor
			whitelist.addTags("div")
			.addAttributes("div", "style");
		}
		if(editorTag == null || editorTag.isJustifyright()){//右对齐
			//右对齐
			whitelist.addTags("p")
			.addAttributes("p", "align");
			
			//vue-html5-editor
			whitelist.addTags("div")
			.addAttributes("div", "style");
		}
		if(editorTag == null || editorTag.isInsertorderedlist()){//编号
			//编号
			whitelist.addTags("ol","li","div","br")
			.addAttributes("div", "align");
		}
		if(editorTag == null || editorTag.isInsertunorderedlist()){//项目符号
			//项目符号
			whitelist.addTags("ul","li","div","br")
			.addAttributes("div", "align");
		}
		if(editorTag == null){
			//增加缩进
			whitelist.addTags("blockquote");
			//下标
			whitelist.addTags("sub");
			//上标
			whitelist.addTags("sup");
			//一键排版
			whitelist.addTags("p")//增加白名单标签
			.addAttributes("p","style");
			
		
			//段落
			whitelist.addTags("h1","h2","h3","h4","h5","h6")
			.addAttributes("h1","style","align")
			.addAttributes("h2","style","align")
			.addAttributes("h3","style","align")
			.addAttributes("h4","style","align")
			.addAttributes("h5","style","align")
			.addAttributes("h6","style","align");
			
			//代码
			whitelist.addTags("pre")
			.addAttributes("pre","class");
			
		}
		if(editorTag == null || editorTag.isFontname()){//字体
			//字体
			whitelist.addTags("span")//增加白名单标签
			.addAttributes("span","style");//给标签添加属性。Tag是属性名，keys对应的是一个个属性值。例如：addAttributes("a", "href", "class") 表示：给标签a添加href和class属性，即允许标签a包含href和class属性。如果想给每一个标签添加一组属性，使用:all。例如： addAttributes(":all", "class").即给每个标签添加class属性。
	//			.addProtocols("span", "style", "font-family");//第一个参数：标签 ;第二个参数：标签属性;第三个以后参数：标签属性参数
			
		
			
			//vue-html5-editor
			whitelist.addTags("font")
			.addAttributes("font","face");
			
			
		}
		if(editorTag == null || editorTag.isFontsize()){//文字大小
			//文字大小
			whitelist.addTags("span")
			.addAttributes("span","style");
			
			
	//		.addProtocols("span", "style", "font-size");
	//		.addProtocols("style", "font-size", "http", "https");
		}
		if(editorTag == null || editorTag.isForecolor()){//文字颜色
			//文字颜色
			whitelist.addTags("span")
			.addAttributes("span","style");
	
	
	//		.addProtocols("span", "style", "color");
			
			//vue-html5-editor
			whitelist.addTags("font")
			.addAttributes("font","color").addAttributes("font","style");
			
		}
		if(editorTag == null || editorTag.isHilitecolor()){//文字背景
			//文字背景
			whitelist.addTags("span")
			.addAttributes("span","style");
	//		style_attribute.add("background-color");
			
			
			//参数^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$
		}
		
		
		if(editorTag == null || editorTag.isBold()){//粗体
			//粗体
			whitelist.addTags("strong");
			//vue-html5-editor
			whitelist.addTags("b");
		}
		if(editorTag == null || editorTag.isItalic()){//斜体
			//斜体
			whitelist.addTags("em");
			
			//vue-html5-editor
			whitelist.addTags("i");
			
		}
		if(editorTag == null || editorTag.isUnderline()){//下划线
			//下划线
			whitelist.addTags("u");
		}
		if(editorTag == null){
			//删除线
			whitelist.addTags("s");
			
			//vue-html5-editor
			whitelist.addTags("strike");

		
			//表格边框颜色
			whitelist.addTags("table")
			.addAttributes("table", "bordercolor");
		
		
		
			//插入动态地图
			whitelist.addTags("iframe")
			.addAttributes("iframe", "style","src","frameborder")
			.addProtocols("iframe", "src", "http", "https");
			
			
			//插入横线
			whitelist.addTags("hr");
			
			//插入分页符
			whitelist.addTags("hr")
			.addAttributes("hr", "class","style");
		}
		if(editorTag == null || editorTag.isLink()){//超级链接
			//超级链接
			whitelist.addTags("a")
			.addAttributes("a", "href", "title","target")
			.addProtocols("a", "href", "ftp", "http", "https", "mailto")
			.addEnforcedAttribute("a", "rel", "nofollow");
		}
	
		//取消超级链接
		

		if(editorTag == null || editorTag.isEmoticons()){//插入表情
			//插入表情
			whitelist.addTags("img")
			.addAttributes("img", "align", "alt", "height", "src", "title", "width","border")
			.addProtocols("img", "src", "http", "https");
		}
		if(editorTag == null || editorTag.isImage()){//图片
			//图片
			whitelist.addTags("img")
			.addAttributes("img", "align", "alt", "height", "src", "title", "width")
			.addProtocols("img", "src", "http", "https");
		}
		if(editorTag == null){
			//flash 
			whitelist.addTags("embed")
			.addAttributes("embed", "type", "width","height", "quality", "src","autostart","loop")
			.addProtocols("embed", "src", "http", "https")
			.addEnforcedAttribute("embed", "allownetworking", "internal")
			.addEnforcedAttribute("embed", "allowscriptaccess", "none");
		}
		
		
		return whitelist;
	
	}
	
	/**
	 * 文本过滤标签，只保留<br>标签
	 * @param html
	 * @return
	 */
	public String filterTag_br(String html) {  
		if(StringUtils.isBlank(html)) return ""; 
		
		Whitelist whitelist = Whitelist.none();//只保留文本，其他所有的html内容均被删除
		whitelist.addTags("br");
		

	//	return doc.outputSettings().toString();
	     return Jsoup.clean(html, whitelist); 
	}
	
	
	/**
	 * 富文本过滤标签
	 * @param request
	 * @param html
	 * @return
	 */
	public String filterTag(HttpServletRequest request,String html) {  
		if(StringUtils.isBlank(html)) return ""; 
		Whitelist whitelist = this.filterParameter(null);
		
		
	//	return doc.outputSettings().toString();
	     return Jsoup.clean(html, Configuration.getUrl(request),whitelist); 
	//	return Jsoup.clean(html, whitelist); 
	}
	
	/**
	 * 富文本过滤标签
	 * @param request
	 * @param html 内容
	 * @param editorTag 自定义评论编辑器标签
	 * @return
	 */
	public String filterTag(HttpServletRequest request,String html,EditorTag editorTag) {  
		if(StringUtils.isBlank(html)) return ""; 
		Whitelist whitelist = this.filterParameter(editorTag);
		
		
	//	return doc.outputSettings().toString();
	    String content = Jsoup.clean(html, Configuration.getUrl(request),whitelist); 
	
		return content;
	}
	
	/**
	 * 过滤所有的标签，只返回文本
	 * @param html
	 * @return
	 */
	public String filterText(String html) {  
		if(StringUtils.isBlank(html)) return ""; 
		 return Jsoup.clean(html, Whitelist.none()); //只保留文本，其他所有的html内容均被删除
		 
	}
	/**
	 * 过滤标签并删除<hide>标签所有内容,只返回文本
	 * @param html
	 * @return
	 */
	public String filterHideText(String html) {  
		if(StringUtils.isBlank(html)) return ""; 
		String newHtml = this.deleteHiddenTag(html);
		if(StringUtils.isBlank(newHtml)) return ""; 
		
		return Jsoup.clean(newHtml, Whitelist.none()); //只保留文本，其他所有的html内容均被删除
		 
	}
	
	/**
	 * 富文本过滤html
	 * @param request
	 * @param html 内容
	 * @param item 项目
	 * @return  [0]:过滤好的内容   [1]:上传图片文件名称   [2]:是否含有图片     [3]:上传Flash文件名称     [4]:是否含有Flash   [5]:上传音视频文件名称   [6]:是否含有音视频    [7]:上传文件名称  [8]:是否含有文件   [9]:是否插入地图
	 */
	public Object[] filterHtml(HttpServletRequest request,String html,String item,EditorTag editorTag) {  
		
		
	//	Whitelist whitelist = (Whitelist)object[1];
	
	//	Document doc = Jsoup.parseBodyFragment(html,Configuration.url());
		//是否含有图片
		boolean isImage = false;
		//是否含有Flash
		boolean isFlash = false;
		//是否含有音视频
		boolean isMedia = false;
		//是否含有文件
		boolean isFile = false;
		//是否插入地图
		boolean isMap = false;
		Document doc = Jsoup.parseBodyFragment(html);
	
		
		//样式标签
		Elements pngs = doc.select("*[style]");  
		for (Element element : pngs) {  
			//font-size:14px;font-family:NSimSun;
			 String styleValue = element.attr("style"); 
			 
			 if(styleValue != null && !"".equals(styleValue.trim())){
				 //样式允许使用标签
				 Set<String> styleAllowTagList = new HashSet<String>();
				 styleAllowTagList.add("background-color");
				 styleAllowTagList.add("color");
				 styleAllowTagList.add("font-size");//font-size: 12px;
				 styleAllowTagList.add("line-height");// line-height: 1.2;
				 styleAllowTagList.add("text-align");//  text-align: center; 
				 styleAllowTagList.add("text-indent");
				 styleAllowTagList.add("font-family");
				 styleAllowTagList.add("page-break-after");
				 
				 //表格
				 styleAllowTagList.add("border-spacing");//border-spacing: 0px;
				 styleAllowTagList.add("border-collapse");//border-collapse: collapse;
				 styleAllowTagList.add("width");//width: 100%;
				 styleAllowTagList.add("height");//height: 100%;
				 styleAllowTagList.add("max-width");//max-width: 100%;
				 styleAllowTagList.add("border");//border: 1px solid rgb(221, 221, 221);
				 styleAllowTagList.add("padding");//padding: 8px;
				 styleAllowTagList.add("vertical-align");//vertical-align: top;
				//有效样式标签
				 Map<String,String> effective_styleTagMap = new HashMap<String,String>();
				 
				 //输入样式标签 key:参数  value:值
				 Map<String,String> enter_styleTagMap = new LinkedHashMap<String,String>();
				 
				 //样式
				 List<String> style_array = this.splitSimpleString(styleValue, ';');
				 if(style_array != null && style_array.size() >0){
					 for(String style :style_array){
						 if(style != null && !"".equals(style.trim())){
							 //font-size:14px;
							 List<String> attribute = this.splitSimpleString(style, ':');
							 if(attribute != null && attribute.size() >1){	
								 //样式属性名称  font-size
								 String attribute_name = attribute.get(0).trim();
								 //样式属性值  14px
								 String attribute_value = attribute.get(1).trim();
								 enter_styleTagMap.put(attribute_name, attribute_value);
							 }
						 }
					 }
				 }
				 
				 if(enter_styleTagMap != null && enter_styleTagMap.size() >0){
					 for (Map.Entry<String,String> entry : enter_styleTagMap.entrySet()) {
						 String value = entry.getValue();
						 if(styleAllowTagList.contains(entry.getKey().toLowerCase().trim())){
							 if(StringUtils.countMatches(value, "(") >1){//查询字符串中指定字符串(参数二)出现的次数
								 
								//如果有两个以上括号,则替换为横杆
								 value = StringUtils.replaceIgnoreCase(value, "(", "-");
								 value = StringUtils.replaceIgnoreCase(value, ")", "-");
							 }
							 value = StringUtils.replace(value, "&#", "-");//替换字符串
							 
							 value = StringUtils.replace(value, "*", "-");//替换字符串
							 
							 value = StringUtils.replaceIgnoreCase(value, "expression", "-");//替换字符串，不区分大小写
							 value = StringUtils.replaceIgnoreCase(value, "javascript", "-");//替换字符串，不区分大小写
							 
							 effective_styleTagMap.put(entry.getKey(), value);
						 }
					 }
				 }
				 
				 StringBuffer style_value = new StringBuffer("");
				 for (Map.Entry<String,String> entry : effective_styleTagMap.entrySet()) {
					 style_value.append(entry.getKey()+": ").append(entry.getValue()).append("; ");
				 }
				 element.attr("style", style_value.toString());	 
			}
	    }
		
		//插入动态地图
		Elements iframe_pngs = doc.select("iframe[src]");  
		for (Element element : iframe_pngs) {  
			//<iframe style="width:560px;height:362px;" src="http://127.0.0.1:8080/shop/backstage/kindeditor/plugins/baidumap/index.html?center=121.473704%2C31.230393&zoom=11&width=558&height=360&markers=121.473704%2C31.230393&markerStyles=l%2CA" frameborder="0">
			 String mapUrl = element.attr("src"); 
			 if(mapUrl != null && !"".equals(mapUrl.trim())){
				 mapUrl = StringUtils.deleteWhitespace(mapUrl);//删除字符串中的空白字符 
				 
				 if (mapUrl.trim().startsWith(Configuration.getUrl(request)+"backstage/kindeditor/plugins/baidumap/index.html")) {  
					 //从左往右查到相等的字符开始，保留后边的，不包含等于的字符
					 mapUrl =StringUtils.substringAfter(mapUrl, Configuration.getUrl(request));  
	                 element.attr("src",  mapUrl);  
	                 isMap = true;
	             }else{
	            	 element.attr("src",  "");  
	             }
			 }
		}

		//上传图片文件名称
		List<String> imageNameList = new ArrayList<String>();
		//图片
		Elements image_elements = doc.select("img[src]");  
		for (Element element : image_elements) {
			isImage = true;
			//font-size:14px;font-family:NSimSun;
			 String imageUrl = element.attr("src"); 
			 if(imageUrl != null && !"".equals(imageUrl.trim())){
			//	 
				 
				 if (imageUrl.trim().startsWith(Configuration.getUrl(request)+"file/") ||
						 imageUrl.trim().startsWith(Configuration.getUrl(request)+"common/") ||
						 imageUrl.trim().startsWith(Configuration.getUrl(request)+"backstage/")) {  
					 //从左往右查到相等的字符开始，保留后边的，不包含等于的字符
					 imageUrl =StringUtils.substringAfter(imageUrl, Configuration.getUrl(request));  
	                 element.attr("src",  imageUrl);  
	                 if(StringUtils.startsWithIgnoreCase(imageUrl, "file/"+item+"/")){
	                	 
		            	 imageNameList.add(StringUtils.difference("file/"+item+"/", imageUrl));
		             }
	             }else{
	            	 element.attr("src",  imageUrl);  
	             }
			 }
			  
		}
		
		//上传Flash文件名称
		List<String> flashNameList = new ArrayList<String>();
		//上传音视频文件名称
		List<String> mediaNameList = new ArrayList<String>();
		Elements embed_pngs = doc.select("embed[src]");  
		for (Element element : embed_pngs) {  
			 String type = element.attr("type"); 
			 if("application/x-shockwave-flash".equalsIgnoreCase(type)){//flash
				 isFlash = true;
				//<embed src="http://127.0.0.1:8080/shop/file/information/1/2013-11-04/flash/6c72272190254f419478ddd2e2c774bc.swf" type="application/x-shockwave-flash" width="550" height="400" quality="high" />
				 String flashUrl = element.attr("src"); 
				 if(flashUrl != null && !"".equals(flashUrl.trim())){
					 
					 if (flashUrl.trim().startsWith(Configuration.getUrl(request)+"file/"+item+"/")) {  
						 //从左往右查到相等的字符开始，保留后边的，不包含等于的字符
						 flashUrl =StringUtils.substringAfter(flashUrl, Configuration.getUrl(request));  
		                 element.attr("src",  flashUrl);
		                 flashNameList.add(StringUtils.difference("file/"+item+"/", flashUrl));
		             }
				 } 
			 }else if("video/x-ms-asf-plugin".equalsIgnoreCase(type)){//音视频
				 isMedia = true;
				 String mediaUrl = element.attr("src"); 
				 if(mediaUrl != null && !"".equals(mediaUrl.trim())){
					 
					 if (mediaUrl.trim().startsWith(Configuration.getUrl(request)+"file/"+item+"/")) {  
						 //从左往右查到相等的字符开始，保留后边的，不包含等于的字符
						 mediaUrl =StringUtils.substringAfter(mediaUrl, Configuration.getUrl(request));  
		                 element.attr("src",  mediaUrl);
		                 mediaNameList.add(StringUtils.difference("file/"+item+"/", mediaUrl));
		             }
				 }
				 
			 }
		}
		//上传文件名称
		List<String> fileNameList = new ArrayList<String>();
		Elements file_pngs = doc.select("a[href]");  
		for (Element element : file_pngs) {  
			String fileUrl = element.attr("href");
			if(fileUrl != null && !"".equals(fileUrl.trim())){
				isFile = true;
				if (fileUrl.trim().startsWith(Configuration.getUrl(request)+"file/"+item+"/")) { 
					fileUrl =StringUtils.substringAfter(fileUrl, Configuration.getUrl(request));  
					element.attr("href",  fileUrl);
					fileNameList.add(StringUtils.difference("file/"+item+"/", fileUrl));
				}
			}
		}

	//	 doc.outputSettings().escapeMode(EscapeMode.xhtml);
	//     Document clean = new Cleaner(whitelist).clean(doc);
	//     return clean.body().html();//获取html 
		
		
	//	return doc.body().html();
		return new Object[]{doc.body().html(),imageNameList,isImage,flashNameList,isFlash,mediaNameList,isMedia,fileNameList,isFile,isMap};
	}
    
	
	
	
	
	/**
	 * 修改分类路径
	 * @param html内容
	 * @param item 项目
	 * @param new_typeId 新分类Id
	 * @return 上传旧文件路径和新文件路径
	 */
	public Object[] updateTypePath(String html,String item,Long new_typeId){
		Map<String,String> path = new HashMap<String,String>();//key:旧文件路径+文件名  value:新文件路径
		
		if(!StringUtils.isBlank(html)){
			Document doc = Jsoup.parseBodyFragment(html);
			//图片
			Elements image_elements = doc.select("img[src]");  
			for (Element element : image_elements) {
				 String imageUrl = element.attr("src"); 
				 if(StringUtils.startsWithIgnoreCase(imageUrl, "file/"+item+"/")){
					 
					 String[] old_typeId_list = imageUrl.split("/");
					 String old_typeId = "";
					 if(old_typeId_list.length >=2){
						 old_typeId = old_typeId_list[2];
						
					 }
					 String new_typeId_str = "";
					 if(new_typeId == null){
						 new_typeId_str = "null";
					 }else{
						 new_typeId_str = new_typeId.toString();
					 }
					
					 if(!old_typeId.equals(new_typeId_str)){
						 //替换指定的字符，只替换第一次出现的
						 String url = StringUtils.replaceOnce(imageUrl, "/"+old_typeId+"/", "/"+new_typeId_str+"/");
						 element.attr("src",  url);
						 path.put(imageUrl, StringUtils.substringBeforeLast(url, "/"));
					 }
	             }
			}
			
			Elements embed_pngs = doc.select("embed[src]");  
			for (Element element : embed_pngs) {  
				 String type = element.attr("type"); 
				 if("application/x-shockwave-flash".equalsIgnoreCase(type)){//flash
					//<embed src="http://127.0.0.1:8080/shop/file/information/1/2013-11-04/flash/6c72272190254f419478ddd2e2c774bc.swf" type="application/x-shockwave-flash" width="550" height="400" quality="high" />
					 String flashUrl = element.attr("src"); 
					 if(flashUrl != null && !"".equals(flashUrl.trim())){
						 if(StringUtils.startsWithIgnoreCase(flashUrl, "file/"+item+"/")){
							 String[] old_typeId_list = flashUrl.split("/");
							 String old_typeId = "";
							 if(old_typeId_list.length >=2){
								 old_typeId = old_typeId_list[2];
								
							 }
							 String new_typeId_str = "";
							 if(new_typeId == null){
								 new_typeId_str = "null";
							 }else{
								 new_typeId_str = new_typeId.toString();
							 }
							 
							
							 if(!old_typeId.equals(new_typeId_str)){
								 //替换指定的字符，只替换第一次出现的
								 String url = StringUtils.replaceOnce(flashUrl, "/"+old_typeId+"/", "/"+new_typeId_str+"/");
								 element.attr("src",  url);
								 path.put(flashUrl, StringUtils.substringBeforeLast(url, "/"));//返回最后一个指定字符串之前的所有字符
							 }
						 }
					 } 
				 }else if("video/x-ms-asf-plugin".equalsIgnoreCase(type)){//音视频
					 String mediaUrl = element.attr("src"); 
					 if(mediaUrl != null && !"".equals(mediaUrl.trim())){
						 if(StringUtils.startsWithIgnoreCase(mediaUrl, "file/"+item+"/")){
							 String[] old_typeId_list = mediaUrl.split("/");
							 String old_typeId = "";
							 if(old_typeId_list.length >=2){
								 old_typeId = old_typeId_list[2];
								
							 }
							 String new_typeId_str = "";
							 if(new_typeId == null){
								 new_typeId_str = "null";
							 }else{
								 new_typeId_str = new_typeId.toString();
							 }
							 
							
							 if(!old_typeId.equals(new_typeId_str)){
								 //替换指定的字符，只替换第一次出现的
								 String url = StringUtils.replaceOnce(mediaUrl, "/"+old_typeId+"/", "/"+new_typeId_str+"/");
								 element.attr("src",  url);
								 path.put(mediaUrl, StringUtils.substringBeforeLast(url, "/"));//返回最后一个指定字符串之前的所有字符
							 }
						 }
					 }
				 }
			}
			Elements file_pngs = doc.select("a[href]");  
			for (Element element : file_pngs) {  
				String fileUrl = element.attr("href");
				if(fileUrl != null && !"".equals(fileUrl.trim())){
					if(StringUtils.startsWithIgnoreCase(fileUrl, "file/"+item+"/")){
						 String[] old_typeId_list = fileUrl.split("/");
						 String old_typeId = "";
						 if(old_typeId_list.length >=2){
							 old_typeId = old_typeId_list[2];
							
						 }
						 String new_typeId_str = "";
						 if(new_typeId == null){
							 new_typeId_str = "null";
						 }else{
							 new_typeId_str = new_typeId.toString();
						 }
						 
						
						 if(!old_typeId.equals(new_typeId_str)){
							 //替换指定的字符，只替换第一次出现的
							 String url = StringUtils.replaceOnce(fileUrl, "/"+old_typeId+"/", "/"+new_typeId_str+"/");
							 element.attr("href",  url);
							 path.put(fileUrl, StringUtils.substringBeforeLast(url, "/"));//返回最后一个指定字符串之前的所有字符
						 }
					}
				}
			}
			html = doc.body().html();
		}
		
		return new Object[]{html,path};
		
		
	}
	
	/**
	 * 读取上传文件路径名称
	 * @param html
	 * @param item 项目
	 * @return
	 */
	public Object[] readPathName(String html,String item) {
		//上传图片文件名称
		List<String> imageNameList = new ArrayList<String>();
		//上传Flash
		List<String> flashNameList = new ArrayList<String>();
		//上传影音
		List<String> mediaNameList = new ArrayList<String>();
		//上传文件
		List<String> fileNameList = new ArrayList<String>();
		
		
		
		
		if(!StringUtils.isBlank(html)){
			Document doc = Jsoup.parseBodyFragment(html);
			
			
			
			
			//图片
			Elements image_elements = doc.select("img[src]");  
			for (Element element : image_elements) {
				 String imageUrl = element.attr("src"); 
				 if(StringUtils.startsWithIgnoreCase(imageUrl, "file/"+item+"/")){
					 imageNameList.add(imageUrl);
	             }
			}
			Elements embed_pngs = doc.select("embed[src]");  
			for (Element element : embed_pngs) {  
				 String type = element.attr("type"); 
				 if("application/x-shockwave-flash".equalsIgnoreCase(type)){//flash
					//<embed src="http://127.0.0.1:8080/shop/file/information/1/2013-11-04/flash/6c72272190254f419478ddd2e2c774bc.swf" type="application/x-shockwave-flash" width="550" height="400" quality="high" />
					 String flashUrl = element.attr("src"); 
					 if(flashUrl != null && !"".equals(flashUrl.trim())){
						 if(StringUtils.startsWithIgnoreCase(flashUrl, "file/"+item+"/")){
							 flashNameList.add(flashUrl);
						 }
					 } 
				 }else if("video/x-ms-asf-plugin".equalsIgnoreCase(type)){//音视频
					 String mediaUrl = element.attr("src"); 
					 if(mediaUrl != null && !"".equals(mediaUrl.trim())){
						 if(StringUtils.startsWithIgnoreCase(mediaUrl, "file/"+item+"/")){
							 mediaNameList.add(mediaUrl);
						 }
					 }
				 }
			}
			Elements file_pngs = doc.select("a[href]");  
			for (Element element : file_pngs) {  
				String fileUrl = element.attr("href");
				if(fileUrl != null && !"".equals(fileUrl.trim())){
					
					if(StringUtils.startsWithIgnoreCase(fileUrl, "file/"+item+"/")){
						fileNameList.add(fileUrl);
					}
				}
			}
		}
		return new Object[]{imageNameList,flashNameList,mediaNameList,fileNameList};
	}
	

	/**
	 * 修改上传文件路径目录
	 * @param html
	 * @param oldDirectoryName 旧目录名称
	 * @param newDirectoryName 新目录名称
	 * @return
	 */
	public String updatePathName(String html,String oldDirectoryName,String newDirectoryName) {
		if(!StringUtils.isBlank(html)){
			Document doc = Jsoup.parseBodyFragment(html);
			String oldPath = "file/template/"+oldDirectoryName+"/";
			//图片
			Elements image_elements = doc.select("img[src]");  
			for (Element element : image_elements) {
				 String imageUrl = element.attr("src"); 
				 
				 if(StringUtils.startsWithIgnoreCase(imageUrl, oldPath)){
					 element.attr("src",  "file/template/"+newDirectoryName+"/"+imageUrl.substring(oldPath.length(), imageUrl.length()));
	             }
			}
			
			Elements embed_pngs = doc.select("embed[src]");  
			for (Element element : embed_pngs) {  
				 String type = element.attr("type"); 
				 if("application/x-shockwave-flash".equalsIgnoreCase(type)){//flash
					//<embed src="http://127.0.0.1:8080/shop/file/information/1/2013-11-04/flash/6c72272190254f419478ddd2e2c774bc.swf" type="application/x-shockwave-flash" width="550" height="400" quality="high" />
					 String flashUrl = element.attr("src"); 
					 if(flashUrl != null && !"".equals(flashUrl.trim())){
						 if(StringUtils.startsWithIgnoreCase(flashUrl, oldPath)){
							 element.attr("src",  "file/template/"+newDirectoryName+"/"+flashUrl.substring(oldPath.length(), flashUrl.length()));
						 }
					 } 
				 }else if("video/x-ms-asf-plugin".equalsIgnoreCase(type)){//音视频
					 String mediaUrl = element.attr("src"); 
					 if(mediaUrl != null && !"".equals(mediaUrl.trim())){
						 if(StringUtils.startsWithIgnoreCase(mediaUrl, oldPath)){
							 element.attr("src",  "file/template/"+newDirectoryName+"/"+mediaUrl.substring(oldPath.length(), mediaUrl.length()));
						 }
					 }
				 }
			}
			Elements file_pngs = doc.select("a[href]");  
			for (Element element : file_pngs) {  
				String fileUrl = element.attr("href");
				if(fileUrl != null && !"".equals(fileUrl.trim())){
					
					if(StringUtils.startsWithIgnoreCase(fileUrl, oldPath)){
						 element.attr("href",  "file/template/"+newDirectoryName+"/"+fileUrl.substring(oldPath.length(), fileUrl.length()));
					}
				}
			}
			
			html = doc.body().html();
		}
		
		
		return html;
	}
	
	
	/**
	 * 读取上传图片路径名称
	 * @param html
	 * @param item 项目
	 * @return
	 */
	public List<String> readImageName(String html,String item) {
		//上传图片文件名称
		List<String> imageNameList = new ArrayList<String>();
		if(!StringUtils.isBlank(html)){
			Document doc = Jsoup.parseBodyFragment(html);

			//图片
			Elements image_elements = doc.select("img[src]");  
			for (Element element : image_elements) {
				 String imageUrl = element.attr("src"); 
				 if(StringUtils.startsWithIgnoreCase(imageUrl, "file/"+item+"/")){
					 
					 imageNameList.add(imageUrl);
	             }
			}
		}
		return imageNameList;
	}
	
	/** 
	 * 简易字符串分割
	 * 本方法用来根据指定字符，将某字符串以此为分割，拆分成多个子字符串。 
	 * 对于分割字符串功能，在 Java 6.0 以内，都只提供了支持正则表达式的 
	 * {@link String#split(String) split} 方法。此方法为追求通用，即便是简单的 
	 * 分割，也会基于正则表达式来进行。即便是编译过的正则表达式，其性能也无法与简单 
	 * 的字符相等判断相提并论。
	 * 本方法不涉及正则表达式，通过遍历原字符串对应的字符数组来寻找符合分割字符的 
	 * 字符，然后通过 {@link String#substring(int, int)} 来获取每一个分割字符之间 
	 * 的子字符串，存入一个 {@link LinkedList} 中。这是一个功能简单但高效的方法。 
	 * 如果规模比较大，拟考虑先通过一次循环，取得原字符串中分割字符的数量，以此制作 
	 * 定长的 {@link ArrayList} 。 
	 * 本方法尤其适用于常见的由半角逗号结合在一起的字符串的分割。
	 * 在编写之初，本方法曾采取将字符串的字符数组分段处理，通过系统字符串复制来形成 
	 * 一个个子字符串。后经考证，{@link String#substring(int, int)} 是一个很高效的 
	 * 方法，遂改。效率提高了一倍。 
	 * 本方法使用示例如下： 
	 * 
	 * String source = "a b c d e f g h i j k l m n o p q r s t u v w x y z"; 
	 * List<String> secs = StringTool.splitSimpleString(source, ' ');
	 * 此示例中，{@link String} source 为原字符串。{@link List} secs 为删除空格后 
	 * 的结果。 
	 * @see     String#split(String) 
	 * @see     String#substring(int, int) 
	 * @param   source  待被处理的字符串，即本方法的“原字符串” 
	 * @param   gap     分割字符 
	 * @return      从指定原字符按分割字符拆分成的子字符串列表 
	 * @exception   NullPointerException    当传入参数 source 为空时 
	 */ 
	private List<String> splitSimpleString(String source, char gap){  
	    List<String> result = new LinkedList<String>();  
	    char[] sourceChars = source.toCharArray();  
	    String section = null;  
	    int startIndex = 0;  
	    for (int index = -1; ++index != sourceChars.length; )  
	    {  
	        if (sourceChars[index] != gap) continue;  
	        section = source.substring(startIndex, index);  
	        result.add(section);  
	        startIndex = index + 1;  
	    }  
	    section = source.substring(startIndex, sourceChars.length);  
	    result.add(section);  
	    return result;  
	}  
	
	
	
	/**
	 * 校正隐藏标签(缺少参数或参数不正确的隐藏标签替换成<p>)
	 * @param html 富文本内容
	 * @return
	 */
	public String correctionHiddenTag(String html){
		if(!StringUtils.isBlank(html)){
			//隐藏类型
			List<Integer> hideTypeList = new ArrayList<Integer>();
			hideTypeList.add(HideTagType.PASSWORD.getName());//输入密码可见
			hideTypeList.add(HideTagType.COMMENT.getName());//评论话题可见
			hideTypeList.add(HideTagType.GRADE.getName());//达到等级可见
			hideTypeList.add(HideTagType.POINT.getName());//积分购买可见
			hideTypeList.add(HideTagType.AMOUNT.getName());//余额购买可见
			//密码输入值
			String password_inputValue = "";
			//达到等级输入值
			String grade_inputValue = "";
			//积分购买输入值
			String point_inputValue = "";
			//余额购买输入值
			String amount_inputValue = "";
			
			Document doc = Jsoup.parseBodyFragment(html);
			Elements elements = doc.select("hide");  
			for (Element element : elements) {
				//隐藏标签类型
				String hide_type = element.attr("hide-type"); 
				//隐藏标签输入值
				String input_value = element.attr("input-value"); 
				//是否有效标签
				boolean isValidTag = true;
				
				if(hide_type != null && !"".equals(hide_type.trim())){
					if(hide_type.trim().length()>8){//不能超过8位数字
						isValidTag = false;
					}else{
						boolean verification = Verification.isPositiveInteger(hide_type.trim());//正整数
						if(verification){
							if(!hideTypeList.contains(Integer.parseInt(hide_type.trim()))){//不是有效的隐藏类型
								isValidTag = false;
							}
							
						}else{//不是正整数
							isValidTag = false;
						}
					}
				}else{
					isValidTag = false;
				}
				if(isValidTag == false){
					//移除匹配的元素但保留他们的内容
					element.unwrap();  
					/**
					//替换当前标签为<p>标签
					element.removeAttr("class"); 
					element.removeAttr("hide-type");
					element.removeAttr("input-value");
					element.tagName("p");**/
					continue;
				}
				
				//处理输入值
				if(input_value != null && !"".equals(input_value.trim())){
					if(hide_type.trim().equals(HideTagType.PASSWORD.getName().toString())){//输入密码可见
						if(input_value.length() >50){//密码超出50个字符
							isValidTag = false;
						}else{
							if(password_inputValue == null || "".equals(password_inputValue.trim())){
								password_inputValue = input_value.trim();
							}else{
								//替换不是和第一个输入值相同的标签
								if(!password_inputValue.equals(input_value.trim())){
									element.attr("input-value",password_inputValue); 
								}
							}
							
							
						}
					}
					if(hide_type.trim().equals(HideTagType.GRADE.getName().toString())){//达到等级可见
						if(input_value.trim().length()>8){//不能超过8位数字
							isValidTag = false;
						}else{
							boolean verification = Verification.isPositiveInteger(input_value.trim());//正整数
							if(!verification){//不是正整数
								isValidTag = false;
							}else{
								if(grade_inputValue == null || "".equals(grade_inputValue.trim())){
									grade_inputValue = input_value.trim();
								}else{
									//替换不是和第一个输入值相同的标签
									if(!grade_inputValue.equals(input_value.trim())){
										element.attr("input-value",grade_inputValue); 
									}
								}
								
							}
						}
					}
					if(hide_type.trim().equals(HideTagType.POINT.getName().toString())){//积分购买可见
						if(input_value.trim().length()>8){//不能超过8位数字
							isValidTag = false;
						}else{
							boolean verification = Verification.isPositiveInteger(input_value.trim());//正整数
							if(!verification){//不是正整数
								isValidTag = false;
							}else{
								if(point_inputValue == null || "".equals(point_inputValue.trim())){
									point_inputValue = input_value.trim();
								}else{
									//替换不是和第一个输入值相同的标签
									if(!point_inputValue.equals(input_value.trim())){
										element.attr("input-value",point_inputValue); 
									}
								}
								
							}
						}
					}
					if(hide_type.trim().equals(HideTagType.AMOUNT.getName().toString())){//余额购买可见
						if(input_value.trim().length()>12){//不能超过12位数字
							isValidTag = false;
						}else{
							boolean verification = Verification.isAmount(input_value.trim());//金额
							if(verification){
								if(new BigDecimal(input_value.trim()).compareTo(new BigDecimal("0")) <=0){
									isValidTag = false;//不是金额
								}else{
									if(amount_inputValue == null || "".equals(amount_inputValue.trim())){
										amount_inputValue = input_value.trim();
									}else{
										//替换不是和第一个输入值相同的标签
										if(!amount_inputValue.equals(input_value.trim())){
											element.attr("input-value",amount_inputValue); 
										}
									}
									
								}
							}else{
								isValidTag = false;//不是金额
							}
						}
					}
				}else{
					if(!hide_type.trim().equals(HideTagType.COMMENT.getName().toString())){//如是不是评论话题可见
						isValidTag = false;
					}
					
				}
				if(hide_type.trim().equals(HideTagType.COMMENT.getName().toString())){//评论话题可见
					if(input_value != null && !"".equals(input_value.trim())){
						element.removeAttr("input-value");
					}
				}
				
				if(isValidTag == false){
					//移除匹配的元素但保留他们的内容
					element.unwrap();  
					/**
					//替换当前标签为<p>标签
					element.removeAttr("class"); 
					element.removeAttr("hide-type");
					element.removeAttr("input-value");
					element.tagName("p");**/
					continue;
				}
				//处理class值
				element.attr("class","inputValue_"+hide_type.trim()); 
				
			}
			html = doc.body().html();
		}
		return html;
	}
	
	
	/**
	 * 解析隐藏标签
	 * @param html 富文本内容
	 * @return 每种隐藏类型只取第一个值
	 */
	public Map<Integer,Object> analysisHiddenTag(String html){
		//隐藏标签输入值 key 隐藏标签类型 value:输入值
		Map<Integer,Object> inputValueMap = new HashMap<Integer,Object>();
		if(!StringUtils.isBlank(html)){
			Document doc = Jsoup.parseBodyFragment(html);
			Elements elements = doc.select("hide");  
			for (Element element : elements) {
				//隐藏标签类型
				String hide_type = element.attr("hide-type"); 
				//隐藏标签输入值
				String input_value = element.attr("input-value"); 
				
				if(hide_type != null && !"".equals(hide_type.trim())){
					if(hide_type.trim().equals(HideTagType.PASSWORD.getName().toString())){//输入密码可见
						if(input_value != null && !"".equals(input_value.trim())){
							if(inputValueMap.get(HideTagType.PASSWORD.getName()) == null){
								inputValueMap.put(HideTagType.PASSWORD.getName(), input_value.trim());
							}
						}
					}
					if(hide_type.trim().equals(HideTagType.COMMENT.getName().toString())){//评论话题可见
						if(inputValueMap.get(HideTagType.COMMENT.getName()) == null){
							inputValueMap.put(HideTagType.COMMENT.getName(), true);
						}
					}
					if(hide_type.trim().equals(HideTagType.GRADE.getName().toString())){//达到等级可见
						if(input_value != null && !"".equals(input_value.trim())){
							if(inputValueMap.get(HideTagType.GRADE.getName()) == null){
								inputValueMap.put(HideTagType.GRADE.getName(), Integer.parseInt(input_value.trim()));
							}
						}
					}
					if(hide_type.trim().equals(HideTagType.POINT.getName().toString())){//积分购买可见
						if(input_value != null && !"".equals(input_value.trim())){
							if(inputValueMap.get(HideTagType.POINT.getName()) == null){
								inputValueMap.put(HideTagType.POINT.getName(), Integer.parseInt(input_value.trim()));
							}
						}
					}
					if(hide_type.trim().equals(HideTagType.AMOUNT.getName().toString())){//余额购买可见
						if(input_value != null && !"".equals(input_value.trim())){
							if(inputValueMap.get(HideTagType.AMOUNT.getName()) == null){
								inputValueMap.put(HideTagType.AMOUNT.getName(), new BigDecimal(input_value.trim()));
							}
						}
					}
				}
			}
		}
		return inputValueMap;
	}
	
	/**
	 * 处理隐藏标签
	 * @param html 富文本内容
	 * @param visibleTagList 允许可见的隐藏标签
	 * @return
	 */
	public String processHiddenTag(String html,List<Integer> visibleTagList){
		
		if(!StringUtils.isBlank(html)){
			Document doc = Jsoup.parseBodyFragment(html);
			Elements elements = doc.select("hide");  
			for (Element element : elements) {
				//隐藏标签类型
				String hide_type = element.attr("hide-type"); 
				//隐藏标签输入值
			//	String input_value = element.attr("input-value"); 
				
				if(visibleTagList.contains(Integer.parseInt(hide_type.trim()))){//如果允许可见
					/**
					//替换当前标签为<p>标签
					element.removeAttr("class"); 
					element.removeAttr("hide-type");
					element.removeAttr("input-value");
					element.tagName("p");**/
					//移除匹配的元素但保留他们的内容
					element.unwrap();  
					
				}else{
					if(hide_type.trim().equals(HideTagType.PASSWORD.getName().toString())){//输入密码可见
						element.removeAttr("input-value");
					}
					//清空元素的内容
					element.empty();	
				}
			}
			html = doc.body().html();
		}
		return html;
	}
	
	/**
	 * 删除隐藏标签(包括隐藏标签的内容和子标签)
	 * @param html 富文本内容
	 * @return
	 */
	public String deleteHiddenTag(String html){
		if(!StringUtils.isBlank(html)){
			Document doc = Jsoup.parseBodyFragment(html);
			Elements elements = doc.select("hide");  
			for (Element element : elements) {
				element.remove();
			}
			html = doc.body().html();
		}
		return html;
		
		
	}

	
	
}
