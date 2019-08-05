package cms.web.action.like;

import javax.annotation.Resource;

/**
 * 话题点赞配置
 *
 */
public class TopicLikeConfig {
	@Resource LikeManage likeManage;
	
	/** 分表数量 **/
	private Integer tableQuantity = 1;
	
	public Integer getTableQuantity() {
		return tableQuantity;
	}
	public void setTableQuantity(Integer tableQuantity) {
		this.tableQuantity = tableQuantity;
	}
	
	/**
	  * 根据话题点赞Id查询分配到表编号
	  * 根据话题点赞Id和话题点赞分表数量求余
	  * @param topicLikeId 话题点赞Id
	  * 注意：话题点赞Id要先判断最后4位是不是数字
	  * likeManage.verificationTopicLikeId(?)
	  * @return
	 */
	public Integer topicLikeIdRemainder(String topicLikeId){
	   int topicId = likeManage.getTopicLikeId(topicLikeId);
	   return topicId % this.getTableQuantity();
	} 
   /**
    * 根据话题Id查询分配到表编号
    * 根据话题Id和话题点赞分表数量求余(用户Id后四位)
    * @param topicId 话题Id
    * @return
    */
	public Integer topicIdRemainder(Long topicId){
	   	//选取得后N位话题Id
	   	String afterTopicId = String.format("%04d", topicId%10000);
	   	return Integer.parseInt(afterTopicId) % this.getTableQuantity();
	}
	 
}
