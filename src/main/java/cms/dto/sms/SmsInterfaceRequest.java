package cms.dto.sms;


import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 短信接口表单
 */
@Getter
@Setter
public class SmsInterfaceRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -5215294394749341262L;

    /** Id  **/
    private Integer smsInterfaceId;
    /** 名称 **/
    private String name;

    /** 接口产品  1.阿里云短信  10.云片 **/
    private Integer interfaceProduct;

    /** 是否选择  true:启用 false: 禁用 **/
    private boolean enable = true;

    /** 排序 **/
    private Integer sort = 1;


    private String alidayu_accessKeyId;
    private String alidayu_accessKeySecret;
}
