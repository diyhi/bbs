package cms.web.action.question;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.stereotype.Component;

import cms.bean.question.QuestionTag;

/**
 * 问题标签
 *
 */
@Component("questionTagManage")
public class QuestionTagManage {
	private AtomicInteger number = new AtomicInteger(new Random().nextInt(88888));//AtomicInteger 的最大值是2147483647，超过这个数字在递增的话就变成-2147483648
	private DateTime begin = new DateTime(2010,01,01,01,01,01,0);   
	
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
	 * @param new_questionTag 新标签
	 * @param questionTagList 未存入子标签集合
	 * @return
	*/
	public void childQuestionTag(QuestionTag new_questionTag,List<QuestionTag> questionTagList){
		if(new_questionTag != null && new_questionTag.getChildNodeNumber() >0){
			for(QuestionTag questionTag : questionTagList){
				if(questionTag.getParentId().equals(new_questionTag.getId())){
					new_questionTag.addChildTag(questionTag);  
			    	this.childQuestionTag(questionTag, questionTagList);
			    	
			    }   
			}
		}
	}
	/**
	 * 问题标签排序(递归)
	 * @param questionTagList 已存入子标签集合
	 * @return
	*/
	public void questionTagSort(List<QuestionTag> questionTagList){
		if(questionTagList != null && questionTagList.size() >0){
			this.questionTagItemSort(questionTagList);
			for(QuestionTag questionTag : questionTagList){
				List<QuestionTag> childQuestionTag = questionTag.getChildTag();
				if(childQuestionTag != null && childQuestionTag.size() >0){
					this.questionTagSort(childQuestionTag);
				}
			}
		}
	}
	/**
	 * 问题标签项排序
	 * @param questionTagList
	 */
	private void questionTagItemSort(List<QuestionTag> questionTagList){
		Collections.sort(questionTagList, new Comparator<QuestionTag>(){
			@Override
			public int compare(QuestionTag o1, QuestionTag o2) {
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

