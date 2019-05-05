package cms.bean.topic;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 隐藏标签名称
 *
 */
public class HideTagName {
	private static Map<Integer, String> map = new LinkedHashMap<Integer, String>();
	static {
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
