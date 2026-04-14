package cms.dto.frontendModule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户收藏总数
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteCountDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -6976574115933009750L;
    /** 用户收藏总数 **/
    private Long favoriteCount;
}
