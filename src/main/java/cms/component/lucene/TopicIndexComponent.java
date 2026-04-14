package cms.component.lucene;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import cms.model.topic.Topic;
import cms.model.topic.TopicIndex;
import cms.repository.topic.TopicIndexRepository;
import cms.repository.topic.TopicRepository;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;


/**
 * 话题全文索引组件
 *
 */
@Component("topicIndexComponent")
public class TopicIndexComponent {
	private static final Logger logger = LogManager.getLogger(TopicIndexComponent.class);
	
	@Resource TopicRepository topicRepository;
	@Resource TopicIndexRepository topicIndexRepository;
	@Resource TopicLuceneComponent topicLuceneComponent;
    @Resource TopicIndexCacheManager topicIndexCacheManager;
	
	/**
	 * 更新话题索引(由定时器触发)
	 */
	public void updateTopicIndex(){
		int firstindex = 0;//起始页
		int maxresult = 100;// 每页显示记录数

		try {

			while(true){			
				//查询话题索引
				List<TopicIndex> topicIndexList = topicIndexRepository.findTopicIndex(firstindex, maxresult);
				
				
				if(topicIndexList == null || topicIndexList.size() == 0){
					break;
				}
				//添加话题Id集合
				Set<Long> add_topicIdList = new LinkedHashSet<Long>();
				//修改话题Id
				Set<Long> update_topicIdList = new LinkedHashSet<Long>();
				//删除话题Id
				Set<Long> delete_topicIdList = new LinkedHashSet<Long>();
				//删除用户名称
				Set<String> delete_userNameList = new LinkedHashSet<String>();
				
				List<Long> indexIdList = new ArrayList<Long>();
				for(TopicIndex p : topicIndexList){
					indexIdList.add(p.getId());
					if(p.getIndexState().equals(1)){//1:添加
						add_topicIdList.add(Long.parseLong(p.getDataId()));
						
					}else if(p.getIndexState().equals(2)){//2:修改
						update_topicIdList.add(Long.parseLong(p.getDataId()));
					}else if(p.getIndexState().equals(3)){//3:删除 
						Long id = Long.parseLong(p.getDataId());
						delete_topicIdList.add(id);
						
						//删除添加和修改Id
						add_topicIdList.remove(id);
						update_topicIdList.remove(id);
					}else if(p.getIndexState().equals(4)){//4:删除用户发表的话题
						delete_userNameList.add(p.getDataId());
					}
				}

				//删除话题索引变化标记
				topicIndexRepository.deleteTopicIndex(indexIdList);
				
				
				//查询话题Id集合
				Set<Long> topicIdList = new LinkedHashSet<Long>();
				topicIdList.addAll(add_topicIdList);
				topicIdList.addAll(update_topicIdList);
				
				//删除话题Id索引集合
				Set<Long> topicIdIndexList = new LinkedHashSet<Long>();
				topicIdIndexList.addAll(update_topicIdList);
				topicIdIndexList.addAll(delete_topicIdList);
				
				
				if(topicIdIndexList.size() >0){
					//删除当前Id索引
					topicLuceneComponent.deleteIndex(new ArrayList<Long>(topicIdIndexList));
				}
				
				//根据用户名称删除话题集合
				if(delete_userNameList.size() >0){
					//删除用户名称下的索引
					topicLuceneComponent.deleteUserNameIndex(new ArrayList<String>(delete_userNameList));
				}

				if(topicIdList.size() >0){
					//根据话题Id集合查询话题
					List<Topic> topicList = topicRepository.findTopicByTopicIdList(new ArrayList<Long>(topicIdList));
					if(topicList != null && topicList.size() >0){
						//写入索引
						topicLuceneComponent.addIndex(topicList);
						
					}
				}

				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("更新话题索引(由定时器触发)",e);
	        }
		}
	}
	/**
	 * 添加全部话题索引
	 */
	public void addAllTopicIndex(){
		long count = 0;
		int page = 1;//分页 当前页
		int maxresult = 200;// 每页显示记录数

        topicIndexCacheManager.taskRunMark_delete();
        topicIndexCacheManager.taskRunMark_add(count);


        //删除所有话题索引变化标记
        topicIndexRepository.deleteAllIndex();

        try {

            topicLuceneComponent.deleteAllIndex();//删除所有索引


            while(true){
                count++;
                topicIndexCacheManager.taskRunMark_delete();
                topicIndexCacheManager.taskRunMark_add(count);

                //当前页
                int firstindex = (page-1)*maxresult;
                //查询话题
                List<Topic> topicList = topicRepository.findTopicByPage(firstindex, maxresult);

                if(topicList == null || topicList.size() == 0){
                    break;
                }



                //写入索引
                topicLuceneComponent.addIndex(topicList);
                page++;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
        //	e.printStackTrace();
            if (logger.isErrorEnabled()) {
                logger.error("添加全部话题索引",e);
            }
        }

        topicIndexCacheManager.taskRunMark_delete();
	}


	
	

}
