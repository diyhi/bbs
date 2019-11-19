package cms.bean.question;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
/**
 * 问题索引变化
 *
 */
@Entity
public class QuestionIndex implements Serializable{
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
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDataId() {
		return dataId;
	}
	public void setDataId(String dataId) {
		this.dataId = dataId;
	}
	public Integer getIndexState() {
		return indexState;
	}
	public void setIndexState(Integer indexState) {
		this.indexState = indexState;
	}
	
}
