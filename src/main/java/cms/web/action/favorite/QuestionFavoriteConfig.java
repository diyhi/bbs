package cms.web.action.favorite;

import javax.annotation.Resource;

/**
 * 问题收藏配置
 *
 */
public class QuestionFavoriteConfig {
	@Resource FavoriteManage favoriteManage;
	
	/** 分表数量 **/
	private Integer tableQuantity = 1;
	
	public Integer getTableQuantity() {
		return tableQuantity;
	}
	public void setTableQuantity(Integer tableQuantity) {
		this.tableQuantity = tableQuantity;
	}
	
	/**
	  * 根据问题收藏Id查询分配到表编号
	  * 根据问题收藏Id和问题收藏分表数量求余
	  * @param questionFavoriteId 收藏夹Id
	  * 注意：问题收藏Id要先判断最后4位是不是数字
	  * favoriteManage.verificationQuestionFavoriteId(?)
	  * @return
	 */
	public Integer questionFavoriteIdRemainder(String questionFavoriteId){
	   int questionId = favoriteManage.getQuestionFavoriteId(questionFavoriteId);
	   return questionId % this.getTableQuantity();
	} 
   /**
    * 根据问题Id查询分配到表编号
    * 根据问题Id和问题收藏分表数量求余(用户Id后四位)
    * @param questionId 问题Id
    * @return
    */
	public Integer questionIdRemainder(Long questionId){
	   	//选取得后N位问题Id
	   	String afterQuestionId = String.format("%04d", questionId%10000);
	   	return Integer.parseInt(afterQuestionId) % this.getTableQuantity();
	}
	 
}
