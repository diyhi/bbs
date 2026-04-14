package cms.model.favorite;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


/**
 * 收藏夹
 *
 */
@Entity
@Table(name="favorites_0",indexes = {@Index(name="favorites_1_idx", columnList="userName,addtime")})
public class Favorites extends FavoritesEntity implements Serializable{

	@Serial
    private static final long serialVersionUID = 3870741499885154096L;
	


}
