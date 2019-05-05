package cms.web.action.topic;

import javax.annotation.Resource;

/**
 * 话题取消隐藏配置
 *
 */
public class TopicUnhideConfig {
	@Resource TopicManage topicManage;
	
	/** 分表数量 **/
	private Integer tableQuantity = 1;
	
	public Integer getTableQuantity() {
		return tableQuantity;
	}
	public void setTableQuantity(Integer tableQuantity) {
		this.tableQuantity = tableQuantity;
	}
	
	/**
	  * 根据话题取消隐藏Id查询分配到表编号
	  * 根据'话题取消隐藏Id'和'话题取消隐藏'分表数量求余
	  * @param topicUnhideId 话题取消隐藏Id
	  * 校验话题取消隐藏Id使用方法: topicManage.verificationTopicUnhideId(?)
	  * @return
	 */
	public Integer topicUnhideIdRemainder(String topicUnhideId){
	   int topicId = topicManage.analysisTopicId(topicUnhideId);
	   return topicId % this.getTableQuantity();
	} 
  /**
   * 根据话题Id查询分配到表编号
   * 根据话题Id和话题'话题取消隐藏'分表数量求余(用户Id后四位)
   * @param topicId 话题Id
   * @return
   */
	public Integer topicIdRemainder(Long topicId){
	   	//选取得后N位话题Id
	   	String afterTopicId = String.format("%04d", topicId%10000);
	   	return Integer.parseInt(afterTopicId) % this.getTableQuantity();
	}
	 
}
