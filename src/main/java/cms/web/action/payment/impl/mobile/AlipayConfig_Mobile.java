package cms.web.action.payment.impl.mobile;

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
 * 支付宝手机支付配置
 * @author Gao
 *
 */
@Component("alipayConfig_Mobile")
public class AlipayConfig_Mobile {
	
	@Resource PaymentService paymentService;
	
	// 请求网关地址
	public String URL = "https://openapi.alipay.com/gateway.do";
	//请求网关沙箱地址
//	public String URL = "https://openapi.alipaydev.com/gateway.do";
	// 编码
	public String CHARSET = "UTF-8";
	// 返回格式
	public String FORMAT = "json";
	// 支付宝公钥
	private String ALIPAY_PUBLIC_KEY = "";
	
	// 日志记录目录
	public String log_path = "/log";
	// RSA2
	public String SIGNTYPE = "RSA2";
	/** 客户端 **/
	private  AlipayClient client = null;
	/** 在线支付接口版本号 **/
	private Integer version = -1;
	
	
	
	/**
	 * 获取客户端
	 * 接口产品 4.支付宝手机网站(alipay.trade.wap.pay接口)
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
	 * 接口产品 4.支付宝手机网站(alipay.trade.wap.pay接口)
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
