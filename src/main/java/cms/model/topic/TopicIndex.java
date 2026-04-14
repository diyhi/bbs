package cms.model.topic;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * 话题索引变化
 *
 */
@Getter
@Setter
@Entity
public class TopicIndex implements Serializable{
	@Serial
    private static final long serialVersionUID = 8745684693096984983L;
	
	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 数据Id   数据格式[1:添加 "话题Id"  2.修改 "话题Id"  3.删除 "话题Id"  4.删除 "用户名称" **/
	@Column(length=32)
	private String dataId;
	/** 索引状态  1:添加  2:修改  3:删除 4.删除用户发表的话题 **/
	private Integer indexState = 1;
	
	
	public TopicIndex(){}
	public TopicIndex(String dataId,Integer indexState) {
		this.dataId = dataId;
		this.indexState = indexState;
	}

}
