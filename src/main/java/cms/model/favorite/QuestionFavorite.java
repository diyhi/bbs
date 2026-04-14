package cms.model.favorite;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


/**
 * 问题收藏
 *
 */
@Entity
@Table(name="questionfavorite_0",indexes = {@Index(name="questionFavorite_1_idx", columnList="questionId,addtime")})
public class QuestionFavorite extends QuestionFavoriteEntity implements Serializable{
	@Serial
    private static final long serialVersionUID = 3934126208840648082L;


}
