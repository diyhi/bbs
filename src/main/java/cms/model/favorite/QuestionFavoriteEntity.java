package cms.model.favorite;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

/**
 * 问题收藏 实体类的抽象基类,定义基本属性
 *
 */
@Getter
@Setter
@MappedSuperclass
public class QuestionFavoriteEntity implements Serializable{
	@Serial
    private static final long serialVersionUID = -4141083671828837197L;

	/** ID 格式(问题Id_用户Id)**/
	@Id @Column(length=40)
	protected String id;
	/** 问题收藏的用户名称 **/
	@Column(length=30)
	protected String userName;
	
	/** 发布问题的用户名称 **/
	@Column(length=30)
	protected String postUserName;
	/** 加入时间 **/
    @Column(columnDefinition = "DATETIME")
    protected LocalDateTime addtime = LocalDateTime.now();

	/** 问题Id **/
	protected Long questionId;
	
	/** 收藏夹Id **/
	@Column(length=36)
	protected String favoriteId;


	/** 问题标题 **/
	@Transient
	protected String questionTitle;


}
