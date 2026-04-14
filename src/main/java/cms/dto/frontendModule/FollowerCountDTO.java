package cms.dto.frontendModule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 粉丝总数
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FollowerCountDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 2098181585208150813L;
    /** 粉丝总数 */
    private Long followerCount;
}
