package cms.web.action.payment;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import cms.bean.payment.Bank;
import cms.service.payment.PaymentService;


import org.springframework.stereotype.Component;

/**
 * 在线支付接口管理
 * @author gao
 *
 */
@Component("onlinePaymentInterfaceManage")
public class OnlinePaymentInterfaceManage{
	@Resource PaymentService paymentService;
	
	
	
	/**
	 * 读取银行
	 * @param interfaceProduct 接口产品  1.支付宝即时到账  4.支付宝手机网站
	 * @return
	 */
	public List<Bank> getBankList(Integer interfaceProduct){
		List<Bank> bankList = new ArrayList<Bank>();
		
		if(interfaceProduct.equals(1)){//1.支付宝即时到账
			bankList.add(new Bank("ALIPAY","支付宝","ALIPAY.gif"));
	//	}
	//	if(interfaceProduct.equals(3)){//3.支付宝手机网站(alipay.wap.create.direct.pay.by.user接口)
	//		bankList.add(new Bank("ALIPAY-WAP","支付宝","ALIPAY-WAP.gif"));
		}if(interfaceProduct.equals(4)){//4.支付宝手机网站(alipay.trade.wap.pay接口)
			bankList.add(new Bank("ALIPAY-WAP","支付宝","ALIPAY-WAP.gif"));
		}
		
		
		
		
		/**
		if(interfaceProduct.equals(2)){//2.支付宝纯网关
			bankList.add(new Bank("CMB-DEBIT","招商银行","CMB-DEBIT.gif"));
			bankList.add(new Bank("CCB-DEBIT","中国建设银行","CCB-DEBIT.gif"));
			bankList.add(new Bank("ICBC-DEBIT","中国工商银行","ICBC-DEBIT.gif"));
			bankList.add(new Bank("COMM-DEBIT","交通银行","COMM-DEBIT.gif"));
			bankList.add(new Bank("GDB-DEBIT","广发银行","GDB-DEBIT.gif"));
			bankList.add(new Bank("BOC-DEBIT","中国银行","BOC-DEBIT.gif"));
			bankList.add(new Bank("CEB-DEBIT","中国光大银行","CEB-DEBIT.gif"));
			bankList.add(new Bank("SPDB-DEBIT","上海浦东发展银行","SPDB-DEBIT.gif"));
			bankList.add(new Bank("PSBC-DEBIT","中国邮政储蓄银行","PSBC-DEBIT.gif"));
			bankList.add(new Bank("BJBANK","北京银行","BJBANK.gif"));
			bankList.add(new Bank("SHRCB","上海农商银行","SHRCB.gif"));
			bankList.add(new Bank("WZCBB2C-DEBIT","温州银行","WZCBB2C-DEBIT.gif"));
			bankList.add(new Bank("CMBC","中国民生银行","CMBC.gif"));
			bankList.add(new Bank("BJRCB","北京农村商业银行","BJRCB.gif"));
			bankList.add(new Bank("SPA-DEBIT","平安银行","SPA-DEBIT.gif"));
			bankList.add(new Bank("CITIC-DEBIT","中信银行","CITIC-DEBIT.gif"));
			
			
		}**/
		
		return bankList;
	}
	
	/**
	 * 设置支持设备
	 * @param pc 电脑端 true:支持  false:不支持
	 * @param wap 移动端 true:支持  false:不支持
	 * @param app 应用 true:支持  false:不支持
	 */
	public String setSupportEquipment(boolean pc ,boolean wap ,boolean app){
		String code = "";
		if(pc == true){
			code += "1";
		}else{
			code += "0";
		}
		if(wap == true){
			code += "1";
		}else{
			code += "0";
		}
		if(app == true){
			code += "1";
		}else{
			code += "0";
		}
		return code;
	}
	/**
	 * 判断支持设备
	 * @param supportEquipment 支持设备
	 * @param 当前设备 1:电脑端; 2:移动端 3:移动应用
	 * @return true 支持  false:不支持
	 */
	public boolean isSupportEquipment(String supportEquipment,Integer currentDevice){
		boolean flag = false;
		String first = supportEquipment.substring(0,1);//第一位
		String second = supportEquipment.substring(1,2);//第二位
		String third = supportEquipment.substring(2,3);//第三位
		
		if(currentDevice.equals(1)){
			if(first.equals("1")){
				flag = true;
			}
		}
		if(currentDevice.equals(2)){
			if(second.equals("1")){
				flag = true;
			}
		}
		if(currentDevice.equals(3)){
			if(third.equals("1")){
				flag = true;
			}
		}
		return flag;
	}
	
	
	
}

