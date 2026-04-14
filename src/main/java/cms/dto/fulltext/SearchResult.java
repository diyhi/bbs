package cms.dto.fulltext;

import cms.model.question.Question;
import cms.model.topic.Topic;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 搜索结果
 * @author Gao
 *
 */
@Getter
@Setter
public class SearchResult implements Serializable{
	@Serial
    private static final long serialVersionUID = 1492732938859028084L;
	/** 索引模块 10:话题 20：问题 **/
	private Integer indexModule;
	/** 话题 **/
	private Topic topic;
	/** 问题 **/
	private Question question;
}
