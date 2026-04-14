package cms.utils;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;


/**
 * Referer比较
 *
 */
public class RefererCompare {
	
	/**
	 * 比较referer 和URI是否相同
	 * @param request 请求信息
	 * @param uri 统一资源标志符
	 */
	public static boolean compare(HttpServletRequest request,String uri){
		String referer = request.getHeader("referer");
		//取得URI
		String newReferer = Strings.CI.removeStart(referer,WebUtil.getUrl(request));//移除开始部分的相同的字符,不区分大小写
		newReferer = StringUtils.substringBefore(newReferer, ".");//截取到等于第二个参数的字符串为止
		newReferer = StringUtils.substringBefore(newReferer, "?");//截取到等于第二个参数的字符串为止
		if(uri.equals(newReferer)){//如果是登录页则跳转到首页
			return true;
		}
		return false;
	}
	
}
