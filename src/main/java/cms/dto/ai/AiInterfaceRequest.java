package cms.dto.ai;


import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI大模型表单
 */
@Getter
@Setter
public class AiInterfaceRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 7275033142476024937L;

    /** Id  **/
    private Integer aiInterfaceId;
    /** 名称 **/
    private String name;

    /** 接口产品 10:阿里云百炼大模型  20.火山方舟大模型 **/
    private Integer interfaceProduct;

    /** 是否选择  true:启用 false: 禁用 **/
    private boolean enable = true;



    private String baiLian_apiKey;
    private String baiLian_appId;
    private String baiLian_workspaceId;
}
