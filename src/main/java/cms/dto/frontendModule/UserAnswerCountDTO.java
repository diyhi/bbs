package cms.dto.frontendModule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户回答总数
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswerCountDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4384674856637440546L;
    /** 用户回答总数 **/
    private Long answerCount;
}
