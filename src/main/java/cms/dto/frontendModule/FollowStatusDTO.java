package cms.dto.frontendModule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 关注状态
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FollowStatusDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -602547809051144305L;
    /** 是否关注 **/
    private Boolean isFollowed = false;
}
