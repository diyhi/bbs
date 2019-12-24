package cms.bean.favorite;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;


/**
 * 问题收藏
 *
 */
@Entity
@Table(name="questionfavorite_0",indexes = {@Index(name="questionFavorite_1_idx", columnList="questionId,addtime")})
public class QuestionFavorite extends QuestionFavoriteEntity implements Serializable{
	private static final long serialVersionUID = 3934126208840648082L;


}
