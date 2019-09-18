package cms.web.action.payment.impl.pc;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import cms.bean.payment.Alipay;
import cms.bean.payment.OnlinePaymentInterface;
import cms.service.payment.PaymentService;
import cms.utils.JsonUtils;
/**
 * 支付宝电脑版配置
 * @author Gao
 *
 */

@Component("alipayConfig_PC")
public class AlipayConfig_PC {
	@Resource PaymentService paymentService;
	
	// 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public String ALIPAY_PUBLIC_KEY = "";

	// 签名方式
	public String SIGNTYPE = "RSA2";
	// 返回格式
	public String FORMAT = "json";
	// 编码
	public String CHARSET = "utf-8";
	
	// 支付宝网关
	public String URL = "https://openapi.alipay.com/gateway.do";
	//请求网关沙箱地址
//	public String URL = "https://openapi.alipaydev.com/gateway.do";
	/** 客户端 **/
	private AlipayClient client = null;
	/** 在线支付接口版本号 **/
	private Integer version = -1;
	
	/**
	 * 获取客户端
	 * 接口产品 1.支付宝电脑版
	 * @return
	 */
	public AlipayClient getAlipayClient(Integer interfaceProduct){
		List<OnlinePaymentInterface> onlinePaymentInterfaceList = paymentService.findAllEffectiveOnlinePaymentInterface_cache();
		for(OnlinePaymentInterface onlinePaymentInterface : onlinePaymentInterfaceList){
			//接口产品 
			if(onlinePaymentInterface.getInterfaceProduct().equals(interfaceProduct)){
				if(!onlinePaymentInterface.getVersion().equals(version)){
					this.update(interfaceProduct,onlinePaymentInterface.getVersion());
				}
				
				return client;
			}
		}
		
		return null;
	}
	
	/**
	 * 获取支付宝公钥
	 * 接口产品 1.支付宝电脑版
	 * @return
	 */
	public String getAlipayPublicKey(Integer interfaceProduct){
		List<OnlinePaymentInterface> onlinePaymentInterfaceList = paymentService.findAllEffectiveOnlinePaymentInterface_cache();
		for(OnlinePaymentInterface onlinePaymentInterface : onlinePaymentInterfaceList){
			//接口产品 
			if(onlinePaymentInterface.getInterfaceProduct().equals(interfaceProduct)){
				if(!onlinePaymentInterface.getVersion().equals(version)){
					this.update(interfaceProduct,onlinePaymentInterface.getVersion());
				}
				return this.ALIPAY_PUBLIC_KEY;
			}
		}
		
		return "";
		
	}
	
	/**
	 * 更新数据
	 */
	private synchronized void update(Integer interfaceProduct,Integer version){
		if(version.equals(this.version)){
			return;
		}
		List<OnlinePaymentInterface> onlinePaymentInterfaceList = paymentService.findAllEffectiveOnlinePaymentInterface_cache();
		for(OnlinePaymentInterface onlinePaymentInterface : onlinePaymentInterfaceList){
			//接口产品 
			if(onlinePaymentInterface.getInterfaceProduct().equals(interfaceProduct)){
				if(onlinePaymentInterface.getDynamicParameter() != null && !"".equals(onlinePaymentInterface.getDynamicParameter().trim())){
					Alipay alipay = JsonUtils.toObject(onlinePaymentInterface.getDynamicParameter(), Alipay.class);
					
					this.version = onlinePaymentInterface.getVersion();
					this.ALIPAY_PUBLIC_KEY = alipay.getAlipay_public_key();
					this.client = new DefaultAlipayClient(this.URL, alipay.getApp_id(), alipay.getRsa_private_key(), this.FORMAT, this.CHARSET, alipay.getAlipay_public_key(),this.SIGNTYPE);
					
				}
			}
		}
		
	}
}
