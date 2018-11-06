package cms.service.topic.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import cms.bean.topic.Topic;
import cms.service.besa.DaoSupport;
import cms.service.message.RemindService;
import cms.service.topic.TopicService;
import cms.utils.ObjectConversion;

/**
 * 话题
 *
 */
@Service
@Transactional
public class TopicServiceBean extends DaoSupport<Topic> implements TopicService{
	
	@Resource RemindService remindService;
	
	
	/**
	 * 根据Id查询话题
	 * @param topicId 话题Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Topic findById(Long topicId){
		Query query = em.createQuery("select o from Topic o where o.id=?1")
		.setParameter(1, topicId);
		List<Topic> list = query.getResultList();
		for(Topic p : list){
			return p;
		}
		return null;
	}
	/**
	 * 根据Id集合查询话题
	 * @param topicIdList 话题Id集合
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public List<Topic> findByIdList(List<Long> topicIdList){
		Query query = em.createQuery("select o from Topic o where o.id in(:topicIdList)")
		.setParameter("topicIdList", topicIdList);
		return query.getResultList();
	}
	
	/**
	 * 根据Id集合查询话题标题
	 * @param topicIdList 话题Id集合
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public List<Topic> findTitleByIdList(List<Long> topicIdList){
		String sql ="select o.id, o.title from topic o where o.id in(:topicIdList)";
		 
		Query query = em.createNativeQuery(sql);
		query.setParameter("topicIdList", topicIdList);

		List<Topic> topicList = new ArrayList<Topic>();
		List<Object[]> objectList = query.getResultList();
		if(objectList != null && objectList.size() >0){
			for(int o = 0; o<objectList.size(); o++){
				Object[] object = objectList.get(o);
				Topic topic = new Topic();
				topic.setId(ObjectConversion.conversion(object[0], ObjectConversion.LONG));
				topic.setTitle(ObjectConversion.conversion(object[1], ObjectConversion.STRING));
				topicList.add(topic);
			}
		}
		
		return topicList;
		
	}
	
	/**
	 * 根据话题Id集合查询话题
	 * @param topicIdList 话题Id集合
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<Topic> findTopicByTopicIdList(List<Long> topicIdList){
		List<Topic> topicList = new ArrayList<Topic>();
		
		Query query = em.createQuery("select o.id,o.title,o.tagId,o.content," +
				"o.postTime,o.userName,o.isStaff,o.status " +
				" from Topic o where o.id in(:topicId)");
		query.setParameter("topicId", topicIdList);	
		
		
		List<Object[]> objectList = query.getResultList();
		if(objectList != null && objectList.size() >0){
			for(int i = 0; i<objectList.size(); i++){
				Object[] obj = (Object[])objectList.get(i);
				Long id = (Long)obj[0];
				String title = (String)obj[1];
				Long tagId = (Long)obj[2];
				String content = (String)obj[3];
				Date postTime = (Date)obj[4];
				String userName = (String)obj[5];
				Boolean isStaff = (Boolean)obj[6];
				Integer status = (Integer)obj[7];

				Topic topic = new Topic();
				topic.setId(id);
				topic.setTitle(title);
				topic.setTagId(tagId);
				topic.setContent(content);
				topic.setPostTime(postTime);
				topic.setUserName(userName);
				topic.setIsStaff(isStaff);
				topic.setStatus(status);
				topicList.add(topic);
				
			}
		}
		return topicList;
	}
	
	/**
	 * 分页查询话题内容
	 * @param firstIndex
	 * @param maxResult
	 * @param userName 用户名称
	 * @param isStaff 是否为员工
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public Map<Long,String> findTopicContentByPage(int firstIndex, int maxResult,String userName,boolean isStaff){
		Map<Long,String> topicContentList = new HashMap<Long,String>();//key:话题Id  value:话题内容
		
		String sql = "select o.id,o.content from Topic o where o.userName=?1 and o.isStaff=?2";
		Query query = em.createQuery(sql);	
		query.setParameter(1, userName);
		query.setParameter(2, isStaff);
		//索引开始,即从哪条记录开始
		query.setFirstResult(firstIndex);
		//获取多少条数据
		query.setMaxResults(maxResult);
		
		List<Object[]> objectList = query.getResultList();
		if(objectList != null && objectList.size() >0){
			for(int i = 0; i<objectList.size(); i++){
				Object[] obj = (Object[])objectList.get(i);
				Long id = (Long)obj[0];
				String content = (String)obj[1];
				topicContentList.put(id, content);
			}
		}
		
		return topicContentList;
	}
	
	
	/**
	 * 分页查询话题
	 * @param firstIndex 开始索引
	 * @param maxResult 需要获取的记录数
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<Topic> findTopicByPage(int firstIndex, int maxResult){
		List<Topic> topicList = new ArrayList<Topic>();
		
		String sql = "select o.id,o.title,o.tagId,o.content," +
				"o.postTime,o.userName,o.isStaff,o.status " +
				" from Topic o ";

		Query query = em.createQuery(sql);	
		
		//索引开始,即从哪条记录开始
		query.setFirstResult(firstIndex);
		//获取多少条数据
		query.setMaxResults(maxResult);
			
		List<Object[]> objectList = query.getResultList();
		if(objectList != null && objectList.size() >0){
			for(int i = 0; i<objectList.size(); i++){
				Object[] obj = (Object[])objectList.get(i);
				Long id = (Long)obj[0];
				String title = (String)obj[1];
				Long tagId = (Long)obj[2];
				String content = (String)obj[3];
				Date postTime = (Date)obj[4];
				String userName = (String)obj[5];
				Boolean isStaff = (Boolean)obj[6];
				Integer status = (Integer)obj[7];

				Topic topic = new Topic();
				topic.setId(id);
				topic.setTitle(title);
				topic.setTagId(tagId);
				topic.setContent(content);
				topic.setPostTime(postTime);
				topic.setUserName(userName);
				topic.setIsStaff(isStaff);
				topic.setStatus(status);
				topicList.add(topic);
				
			}
		}
		return topicList;
	}
	
	
	/**
	 * 保存话题
	 * @param topic
	 */
	public void saveTopic(Topic topic){
		this.save(topic);
	}
	/**
	 * 修改话题
	 * @param topic
	 * @return
	 */
	public Integer updateTopic(Topic topic){
		Query query = em.createQuery("update Topic o set o.title=?1, o.content=?2,o.summary=?3,o.tagId=?4,o.allow=?5,o.image=?6,o.status=?7,o.sort=?8 where o.id=?9")
		.setParameter(1, topic.getTitle())
		.setParameter(2, topic.getContent())
		.setParameter(3, topic.getSummary())
		.setParameter(4, topic.getTagId())
		.setParameter(5, topic.isAllow())
		.setParameter(6, topic.getImage())
		.setParameter(7, topic.getStatus())
		.setParameter(8, topic.getSort())
		.setParameter(9, topic.getId());
		int i = query.executeUpdate();
		return i;
	}
	
	/**
	 * 还原话题
	 * @param topicList 话题集合
	 * @return
	 */
	public Integer reductionTopic(List<Topic> topicList){
		int i = 0;
		if(topicList != null && topicList.size() >0){
			for(Topic topic : topicList){
				Query query = em.createQuery("update Topic o set o.status=o.status-100 where o.id=?1 and o.status >100")
				.setParameter(1, topic.getId());
				int j = query.executeUpdate();
				i += j;
			}
		}
		return i;
	}
	
	/**
	 * 标记删除话题
	 * @param topicId 话题Id
	 * @return
	 */
	public Integer markDelete(Long topicId){
		int i = 0;
		Query query = em.createQuery("update Topic o set o.status=o.status+100 where o.id=?1")
		.setParameter(1, topicId);
		i= query.executeUpdate();
		return i;
		
	}
	
	
	/**
	 * 修改话题最后回复时间
	 * @param topicId 话题Id
	 * @param lastReplyTime 最后回复时间
	 * @return
	 */
	public Integer updateTopicReplyTime(Long topicId,Date lastReplyTime){
		int i = 0;
		Query query = em.createQuery("update Topic o set o.lastReplyTime=?1 where o.id=?2")
		.setParameter(1, lastReplyTime)
		.setParameter(2, topicId);
		i= query.executeUpdate();
		return i;
		
	}
	
	
	
	
	/**
	 * 删除话题
	 * @param topicId 话题Id
	 * @return
	 */
	public Integer deleteTopic(Long topicId){
		int i = 0;
		Query delete = em.createQuery("delete from Topic o where o.id=?1")
		.setParameter(1, topicId);
		i = delete.executeUpdate();
		
		//删除评论
		Query comment_delete = em.createQuery("delete from Comment o where o.topicId=?1");
		comment_delete.setParameter(1, topicId);
		comment_delete.executeUpdate();
		
		//删除评论回复
		Query delete_reply = em.createQuery("delete from Reply o where o.topicId=?1");
		delete_reply.setParameter(1, topicId);
		delete_reply.executeUpdate();
				
		//删除提醒
		remindService.deleteRemindByTopicId(topicId);
		
		return i;
	}

	/**
	 * 增加展示次数
	 * @param countMap key: 话题Id value:展示次数
	 * @return
	 */
	public int addViewCount(Map<Long,Long> countMap){
		int i = 0;
		for (Map.Entry<Long, Long> entry : countMap.entrySet()) { 
			Query query = em.createQuery("update Topic o set o.viewTotal=o.viewTotal+?1 where o.id=?2")
					.setParameter(1, entry.getValue())
					.setParameter(2, entry.getKey());
			i = i+query.executeUpdate();
		}
		return i;
	}
	/**
	 * 修改话题状态
	 * @param topicId 话题Id
	 * @param status 状态
	 * @return
	 */
	public int updateTopicStatus(Long topicId,Integer status){
		Query query = em.createQuery("update Topic o set o.status=?1 where o.id=?2")
				.setParameter(1, status)
				.setParameter(2, topicId);
		return query.executeUpdate();
	}
	
	/**
	 * 增加总评论数
	 * @param topicId 话题Id
	 * @param quantity 数量
	 * @return
	 */
	public int addCommentTotal(Long topicId,Long quantity){
		Query query = em.createQuery("update Topic o set o.commentTotal=o.commentTotal+?1 where o.id=?2")
				.setParameter(1, quantity)
				.setParameter(2, topicId);
		return query.executeUpdate();
	}
	/**
	 * 减少总评论数
	 * @param topicId 话题Id
	 * @param quantity 数量
	 * @return
	 */
	public int subtractCommentTotal(Long topicId,Long quantity){
		Query query = em.createQuery("update Topic o set o.commentTotal=o.commentTotal-?1 where o.id=?2")
				.setParameter(1, quantity)
				.setParameter(2, topicId);
		return query.executeUpdate();
	}
}
