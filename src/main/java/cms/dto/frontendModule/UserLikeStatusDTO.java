package cms.dto.frontendModule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户点赞状态
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLikeStatusDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 8844514578930649936L;
    /** 是否点赞 **/
    private Boolean isLiked = false;
}
