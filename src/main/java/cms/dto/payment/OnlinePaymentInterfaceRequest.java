package cms.dto.payment;


import cms.model.payment.Bank;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 在线支付接口表单
 */
@Getter
@Setter
public class OnlinePaymentInterfaceRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 7239981017951387210L;

    /** Id  **/
    private Integer onlinePaymentInterfaceId;
    /** 名称 **/
    private String name;

    /** 接口产品  1.支付宝即时到账   4. 支付宝手机网站 **/
    private Integer interfaceProduct;

    /** 支持设备  第一位:电脑端; 第二位:移动网页端 第三位:移动应用 **/
    private String supportEquipment;

    /** 是否选择  true:启用 false: 禁用 **/
    private boolean enable = true;

    /** 排序 **/
    private Integer sort = 1;


    private String direct_app_id;
    private String direct_rsa_private_key;
    private String direct_alipay_public_key;

    private String mobile_app_id;
    private String mobile_rsa_private_key;
    private String mobile_alipay_public_key;
}
