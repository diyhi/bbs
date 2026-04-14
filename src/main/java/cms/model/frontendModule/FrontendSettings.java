package cms.model.frontendModule;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 前台设置
 */
@Getter
@Setter
@Entity
public class FrontendSettings implements Serializable {
    @Serial
    private static final long serialVersionUID = -4427660958131720830L;
    /** Id **/
    @Id
    private Integer id;
    /** 站点栏目 Section格式JSON数据 **/
    @Lob
    private String sectionData;

}
