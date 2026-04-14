package cms.dto.thirdParty;


import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 第三方登录接口表单
 */
@Getter
@Setter
public class ThirdPartyLoginInterfaceRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -5168287831868451520L;

    /** Id  **/
    private Integer thirdPartyLoginInterfaceId;
    /** 名称 **/
    private String name;

    /** 接口产品  10.微信 50.其他开放平台 **/
    private Integer interfaceProduct;

    /** 是否选择  true:启用 false: 禁用 **/
    private boolean enable = true;

    /** 排序 **/
    private Integer sort = 1;


    private String weixin_op_appID;
    private String weixin_op_appSecret;
    private String weixin_oa_appID;
    private String weixin_oa_appSecret;

    private String other_appID;
    private String other_appSecret;

}
