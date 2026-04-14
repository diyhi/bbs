package cms.model.frontendModule;

import cms.model.help.Help;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 推荐在线帮助参数配置
 */
@Getter
@Setter
public class ConfigRecommendedHelp implements Serializable {
    @Serial
    private static final long serialVersionUID = -2946557711273223116L;

    /** Id **/
    private String id;

    /** 选择推荐在线帮助 只存储Id,Name **/
    private List<Help> recommendHelpList = new ArrayList<Help>();
}
