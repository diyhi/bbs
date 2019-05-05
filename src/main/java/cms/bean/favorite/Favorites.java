package cms.bean.favorite;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;


/**
 * 收藏夹
 *
 */
@Entity
@Table(name="favorites_0",indexes = {@Index(name="favorites_1_idx", columnList="userName,addtime")})
public class Favorites extends FavoritesEntity implements Serializable{

	private static final long serialVersionUID = 3870741499885154096L;
	


}
