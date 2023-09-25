package cms.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Hours;

/**
 * 热门计算工具
 * @author Gao
 *
 */
public class HotUtil {
	
	
	/**
	 * 解析话题热度因子
	 * @param topicHeatFactor 话题热度因子
	 * @return
	 */
	public static Map<String,Object> parseTopicHeatFactor(String topicHeatFactor) {
		Map<String,Object> heatMap = new HashMap<String,Object>();
		
		if(topicHeatFactor != null && !"".equals(topicHeatFactor.trim())){
			String[] factorGroup = topicHeatFactor.trim().split("\\|");
			if(factorGroup != null && factorGroup.length >0){
				for(String factor : factorGroup){
					
					String[] paramGroup = factor.trim().split("=");
					if(paramGroup != null && paramGroup.length==2){
						if(paramGroup[0].trim().equals("评论") && Verification.isPositiveIntegerZero(paramGroup[1].trim())){
							long number =  Long.parseLong(paramGroup[1].trim());
							if(number >=0L && number <=99999L){
								heatMap.put("评论", number);
							}
						}else if(paramGroup[0].trim().equals("点赞") && Verification.isPositiveIntegerZero(paramGroup[1].trim())){
							long number =  Long.parseLong(paramGroup[1].trim());
							if(number >=0L && number <=99999L){
								heatMap.put("点赞", number);
							}
						}else if(paramGroup[0].trim().equals("浏览量") && Verification.isPositiveIntegerZero(paramGroup[1].trim())){
							long number =  Long.parseLong(paramGroup[1].trim());
							if(number >=0L && number <=99999L){
								heatMap.put("浏览量", number);
							}
						}else if(paramGroup[0].trim().equals("重力因子") && Verification.isAmount(paramGroup[1].trim())){
							double number = Double.parseDouble(paramGroup[1].trim());
							if(number >=0.1d && number <=2d){
								heatMap.put("重力因子", number);
							}
						}
					}
				}
			}
		}
		return heatMap;
	}
	
	
	/**
	 * 计算当前时间距离发帖的时间多少小时
	 * @param postTime 发贴时间
	 * @return
	 */
	public static double computeDistanceTime(Date postTime) {
		DateTime currentTime = new DateTime();//当前时间
        DateTime dateTime = new DateTime(postTime);
        return Hours.hoursBetween(dateTime,currentTime).getHours();
	}
	
	
	/**
	 * hackerNews得分计算
	 * KaTeX公式表示：Score = \dfrac{P-1}{(T+2)^G} 
	 * 
	 * @param P 热度因子
	 * @param T 距离发帖的时间（单位为小时）
	 * @param G 重力因子(gravityth power)  默认是G=1.8
	 * @return 返回hackerNews得分
	 */
	public static double hackerNews(long P, double T,Double G) {
		if(G == null){
			G = 1.8; // 1.8;
		}
		double score = 0.0;
		if (P == 0) {
			return score;
		}
		if(T <= 0){
			return score;
		}
		double under = Math.pow((T + 2), G);
		score = (P - 1) / under;
		return score;
	}
}
