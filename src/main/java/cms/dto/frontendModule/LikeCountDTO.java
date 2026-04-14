package cms.dto.frontendModule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 点赞总数
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeCountDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 2331255272850729053L;
    /** 点赞总数 **/
    private Long totalCount;
}
