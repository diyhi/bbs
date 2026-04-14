package cms.component.topic;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import cms.model.topic.Tag;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.stereotype.Component;


/**
 * 标签组件
 *
 */
@Component("tagComponent")
public class TagComponent {
	private final AtomicInteger number = new AtomicInteger(new Random().nextInt(88888));//AtomicInteger 的最大值是2147483647，超过这个数字在递增的话就变成-2147483648
	private final DateTime begin = new DateTime(2010,01,01,01,01,01,0);
	
	/**
     * 取得下一个Id
     * 商品分类Id组成(2010年后的年月日时分秒+本机Id五位)
     */
    public Long nextNumber(){
    	//这里是atoNum到MAX_VALUE=9999的时候重新设成0
    	int MAX_VALUE = 99999;
    	number.compareAndSet(MAX_VALUE, 0);
    	DateTime end = new DateTime();  
		//计算区间毫秒数   
		Duration d = new Duration(begin, end);  
		long second = d.getStandardSeconds();//秒	
    
    	return Long.parseLong(second+(String.format("%05d", number.incrementAndGet())));
    }
    
    /**
   	 * 标签放入标签中(递归)
   	 * @param new_tag 新标签
   	 * @param tagList 未存入子标签集合
   	 * @return
   	*/
   	public void childTag(Tag new_tag, List<Tag> tagList){
   		if(new_tag != null && new_tag.getChildNodeNumber() >0){
   			for(Tag tag : tagList){
   				if(tag.getParentId().equals(new_tag.getId())){
   					new_tag.addChildTag(tag);  
   			    	this.childTag(tag, tagList);
   			    	
   			    }   
   			}
   		}
   	}
   	/**
   	 * 话题标签排序(递归)
   	 * @param tagList 已存入子标签集合
   	 * @return
   	*/
   	public void tagSort(List<Tag> tagList){
   		if(tagList != null && tagList.size() >0){
   			this.tagItemSort(tagList);
   			for(Tag tag : tagList){
   				List<Tag> childTag = tag.getChildTag();
   				if(childTag != null && childTag.size() >0){
   					this.tagSort(childTag);
   				}
   			}
   		}
   	}
   	/**
   	 * 话题标签项排序
   	 * @param tagList
   	 */
   	private void tagItemSort(List<Tag> tagList){
   		Collections.sort(tagList, new Comparator<Tag>(){
   			@Override
   			public int compare(Tag o1, Tag o2) {
   				long s_1 = o1.getSort();
   				long s_2 = o2.getSort();
   				if(s_1 <s_2){
           			return 1;
           			
           		}else{
           			if(s_1 == s_2){
               			return 0;
               		}else{
               			return -1;
               		}
           		}  
   			}   
   		});
   	}
}
