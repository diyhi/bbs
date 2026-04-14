package cms.dto.topic;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 隐藏标签名称
 *
 */
public class HideTagName {
	private static  final Map<Integer, String> map = new LinkedHashMap<Integer, String>();
	static {//修改本内容要同同步修改messages_en_US.properties，messages_zh_CN.properties，TopicClientServiceImpl.java 的解析隐藏标签后面的内容 String placeholderContent = languageComponent.getMessage("topicClientServiceImpl_3"+entry.getKey(),null);
		map.put(HideTagType.PASSWORD.getName(), "输入密码可见");
		map.put(HideTagType.COMMENT.getName(), "评论话题可见");
		map.put(HideTagType.GRADE.getName(), "达到等级可见");
		map.put(HideTagType.POINT.getName(), "积分购买可见");
		map.put(HideTagType.AMOUNT.getName(), "余额购买可见");
	}
	
	public static String getKey(Integer key) {
		
		return map.get(key);
	}
	
}
