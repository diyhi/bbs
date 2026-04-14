package cms.dto.frontendModule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 收藏状态
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoritedStatusDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1666042729545270776L;
    /** 是否已经收藏 **/
    private Boolean isFavorited = false;
}
