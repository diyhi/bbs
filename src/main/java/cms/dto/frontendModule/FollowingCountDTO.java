package cms.dto.frontendModule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 关注总数
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FollowingCountDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -7546406861441643507L;

    /** 主动关注对象总数 (关注了多少人) */
    private Long followingCount;
}
