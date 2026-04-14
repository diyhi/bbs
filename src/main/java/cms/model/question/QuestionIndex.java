package cms.model.question;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
/**
 * 问题索引变化
 *
 */
@Getter
@Setter
@Entity
public class QuestionIndex implements Serializable{
	@Serial
    private static final long serialVersionUID = 7363081942595857112L;
	
	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 数据Id   数据格式[1:添加 "问题Id"  2.修改 "问题Id"  3.删除 "问题Id"  4.删除 "用户名称" **/
	@Column(length=32)
	private String dataId;
	/** 索引状态  1:添加  2:修改  3:删除 4.删除用户发表的问题 **/
	private Integer indexState = 1;
	
	
	public QuestionIndex(){}
	public QuestionIndex(String dataId,Integer indexState) {
		this.dataId = dataId;
		this.indexState = indexState;
	}

}
