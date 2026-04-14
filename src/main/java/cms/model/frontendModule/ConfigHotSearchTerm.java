package cms.model.frontendModule;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 热门搜索词参数配置
 */
@Getter
@Setter
public class ConfigHotSearchTerm implements Serializable {
    @Serial
    private static final long serialVersionUID = 580088556569899404L;

    /** 热门搜索词Id **/
    private String id;

    /** 热门搜索词集合 **/
    private List<String> searchTermList = new ArrayList<String>();
}
