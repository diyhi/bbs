package cms.service.frontend.impl;

import cms.component.AccessSourceDeviceComponent;
import cms.component.payment.OnlinePaymentInterfaceComponent;
import cms.component.payment.PaymentComponent;
import cms.component.payment.impl.mobile.AlipayConfig_Mobile;
import cms.component.payment.impl.pc.AlipayConfig_PC;
import cms.component.user.UserCacheManager;
import cms.config.BusinessException;
import cms.dto.user.AccessUser;
import cms.model.payment.Bank;
import cms.model.payment.OnlinePaymentInterface;
import cms.model.payment.PaymentLog;
import cms.model.payment.PaymentVerificationLog;
import cms.model.setting.SystemSetting;
import cms.model.user.User;
import cms.repository.payment.PaymentRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.user.UserRepository;
import cms.service.frontend.PaymentClientService;
import cms.utils.AccessUserThreadLocal;
import cms.utils.WebUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 前台支付服务
 */
@Service
public class PaymentClientServiceImpl implements PaymentClientService {
    private static final Logger logger = LogManager.getLogger(PaymentClientServiceImpl.class);


    @Resource
    SettingRepository settingRepository;
    @Resource
    PaymentComponent paymentComponent;
    @Resource
    UserRepository userRepository;
    @Resource
    AccessSourceDeviceComponent accessSourceDeviceComponent;
    @Resource
    OnlinePaymentInterfaceComponent onlinePaymentInterfaceComponent;
    @Resource
    PaymentRepository paymentRepository;
    @Resource
    AlipayConfig_PC alipayConfig_PC;
    @Resource
    AlipayConfig_Mobile alipayConfig_Mobile;
    @Resource UserCacheManager userCacheManager;

    private final List<Integer> number = Arrays.asList(5);//1,3,5



    /**
     * 获取付款界面信息
     * @param paymentModule 支付模块 5.用户充值
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> getPaymentViewModel(Integer paymentModule, HttpServletRequest request){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值

        if(!number.contains(paymentModule)){
            throw new BusinessException(Map.of("message", "支付模块参数错误"));
        }

        String accessPath = accessSourceDeviceComponent.accessDevices(request);

        //显示所有支付接口
        List<OnlinePaymentInterface> onlinePaymentInterfaceList = paymentRepository.findAllEffectiveOnlinePaymentInterface_cache();
        //设置银行
        if(onlinePaymentInterfaceList != null && onlinePaymentInterfaceList.size() >0){
            Iterator<OnlinePaymentInterface> onlinePaymentInterface_iter = onlinePaymentInterfaceList.iterator();
            while (onlinePaymentInterface_iter.hasNext()) {
                OnlinePaymentInterface onlinePaymentInterface = onlinePaymentInterface_iter.next();
                if(accessPath.equals("pc")){
                    if(!onlinePaymentInterfaceComponent.isSupportEquipment(onlinePaymentInterface.getSupportEquipment(), 1)){
                        onlinePaymentInterface_iter.remove();
                        continue;
                    }
                }else if(accessPath.equals("wap")){
                    if(!onlinePaymentInterfaceComponent.isSupportEquipment(onlinePaymentInterface.getSupportEquipment(), 2)){
                        onlinePaymentInterface_iter.remove();
                        continue;
                    }
                }
                onlinePaymentInterface.setDynamicParameter(null);
                onlinePaymentInterface.setBankList(onlinePaymentInterfaceComponent.getBankList(onlinePaymentInterface.getInterfaceProduct()));

            }
        }
        returnValue.put("onlinePaymentInterfaceList",onlinePaymentInterfaceList);

        return returnValue;
    }

    /**
     * 支付校验
     * @param paymentModule 支付模块 5.用户充值
     * @param rechargeAmount 充值金额
     */
    public void paymentVerification(Integer paymentModule,String rechargeAmount){
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        User user = userRepository.findUserByUserName(accessUser.getUserName());//查询用户数据
        if(user == null){
            throw new BusinessException(Map.of("message", "用户不存在"));
        }
        
        if(rechargeAmount != null && !rechargeAmount.trim().isEmpty()){
            if(rechargeAmount.trim().length()>12){
                throw new BusinessException(Map.of("rechargeAmount", "不能超过12位数字"));
            }
            boolean rechargeAmountVerification = rechargeAmount.trim().matches("(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){1,2})?$");//金额
            if(!rechargeAmountVerification){
                throw new BusinessException(Map.of("rechargeAmount", "请填写金额"));
            }
        }
    }

    /**
     * 付款
     * @param paymentModule 支付模块 5.用户充值
     * @param rechargeAmount 充值金额
     * @param paymentBank 支付银行 由 接口产品_银行简码 组成
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> payment(Integer paymentModule,String rechargeAmount, String paymentBank, HttpServletRequest request){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值

        String accessPath = accessSourceDeviceComponent.accessDevices(request);
        if(!number.contains(paymentModule)){
            throw new BusinessException(Map.of("message", "支付模块参数错误"));
        }

        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("message", "只读模式不允许提交数据"));
        }
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        User user = userRepository.findUserByUserName(accessUser.getUserName());//查询用户数据

        if(user == null){
            throw new BusinessException(Map.of("message", "用户不存在"));
        }

        if(rechargeAmount == null || rechargeAmount.trim().isEmpty()){
            throw new BusinessException(Map.of("message", "没有充值金额"));
        }

        if(rechargeAmount.trim().length()>12){
            throw new BusinessException(Map.of("rechargeAmount", "不能超过12位数字"));
        }
        boolean rechargeAmountVerification = rechargeAmount.trim().matches("(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){1,2})?$");//金额
        if(!rechargeAmountVerification){
            throw new BusinessException(Map.of("rechargeAmount", "请填写金额"));
        }

        BigDecimal amount = new BigDecimal(rechargeAmount.trim());

        //显示所有支付接口
        List<OnlinePaymentInterface> onlinePaymentInterfaceList = paymentRepository.findAllEffectiveOnlinePaymentInterface_cache();
        //设置银行
        if(onlinePaymentInterfaceList != null && onlinePaymentInterfaceList.size() >0){
            Iterator<OnlinePaymentInterface> onlinePaymentInterface_iter = onlinePaymentInterfaceList.iterator();
            while (onlinePaymentInterface_iter.hasNext()) {
                OnlinePaymentInterface onlinePaymentInterface = onlinePaymentInterface_iter.next();

                if(accessPath.equals("pc")){
                    if(!onlinePaymentInterfaceComponent.isSupportEquipment(onlinePaymentInterface.getSupportEquipment(), 1)){
                        onlinePaymentInterface_iter.remove();
                        continue;
                    }
                }else if(accessPath.equals("wap")){
                    if(!onlinePaymentInterfaceComponent.isSupportEquipment(onlinePaymentInterface.getSupportEquipment(), 2)){
                        onlinePaymentInterface_iter.remove();
                        continue;
                    }
                }
                onlinePaymentInterface.setDynamicParameter(null);
                onlinePaymentInterface.setBankList(onlinePaymentInterfaceComponent.getBankList(onlinePaymentInterface.getInterfaceProduct()));

            }
        }

        //添加支付校验日志
        PaymentVerificationLog paymentVerificationLog = new PaymentVerificationLog();

        if(paymentModule.equals(5)){//5.用户充值
            Integer interfaceProduct = null;//接口产品
            String code = null;//银行简码
            boolean flag = false;//提交的接口银行简码存在,标记要跳转网银支付

            if(amount.compareTo(new BigDecimal("0")) <=0){
                throw new BusinessException(Map.of("paymentBank", "没有充值金额"));
            }
            //如果'充值金额'大于0
            if(paymentBank != null && !paymentBank.trim().isEmpty()){
                String[] p = paymentBank.trim().split("_");
                if(p.length >=2){
                    if(p[0] != null && p[1] != null){
                        interfaceProduct = Integer.parseInt(p[0]);
                        code = p[1];
                    }

                }
            }
            if(interfaceProduct == null){
                throw new BusinessException(Map.of("paymentBank", "支付接口不存在"));
            }
            if(code.trim().isEmpty()){
                throw new BusinessException(Map.of("paymentBank", "请选择银行"));
            }

            if(onlinePaymentInterfaceList != null && onlinePaymentInterfaceList.size() >0){
                for(OnlinePaymentInterface onlinePaymentInterface : onlinePaymentInterfaceList){
                    if(onlinePaymentInterface.getInterfaceProduct().equals(interfaceProduct)){
                        List<Bank> bankList = onlinePaymentInterface.getBankList();
                        if(bankList != null && bankList.size() >0){
                            for(Bank bank : bankList){
                                if(code.equals(bank.getCode())){
                                    //回显需要
                                    bank.setSelected(true);//选中
                                    flag = true;
                                }
                            }
                        }

                    }
                }
            }
            if(!flag ){
                throw new BusinessException(Map.of("paymentBank", "银行选择错误"));
            }
            

            if(amount.compareTo(new BigDecimal("0")) >0){
                String paymentRequestId = paymentComponent.createRunningNumber(user.getId());
                paymentVerificationLog.setId(paymentRequestId);
                paymentVerificationLog.setParameterId(user.getId());
                paymentVerificationLog.setPaymentModule(5);
                paymentVerificationLog.setUserName(accessUser.getUserName());
                paymentVerificationLog.setTimes(LocalDateTime.now());
                paymentVerificationLog.setPaymentAmount(amount);
                paymentRepository.savePaymentVerificationLog(paymentVerificationLog);

                //调用在线支付API
                String createHtmlText = this.jumpPayAPI(interfaceProduct,paymentModule,paymentRequestId,user.getId(),"充值"+user.getId().toString(),String.valueOf(rechargeAmount),code,request);

                returnValue.put("callbackData", createHtmlText);
            }

        }
        return returnValue;
    }

    /**
     * 支付回调通知
     * @param interfaceProduct 接口产品
     * @param request 请求信息
     * @return
     */
    public String notify(Integer interfaceProduct,HttpServletRequest request){
        BigDecimal paymentAmount = new BigDecimal("0");

        if(interfaceProduct.equals(1)){//1.支付宝即时到账
            //获取支付宝POST过来反馈信息
            Map<String,String> params = new HashMap<String,String>();
            Map requestParams = request.getParameterMap();
            for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
                //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
                params.put(name, valueStr);
            }

            boolean signVerified = false; //调用SDK验证签名
            try {
                signVerified = AlipaySignature.rsaCheckV1(params, alipayConfig_PC.getAlipayPublicKey(interfaceProduct),alipayConfig_PC.CHARSET, alipayConfig_PC.SIGNTYPE);
            } catch (AlipayApiException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("支付回调通知错误",e);
                }
            }

            if(signVerified){
                //商户订单号
                String out_trade_no = request.getParameter("out_trade_no");

                //支付宝交易号
                String trade_no = request.getParameter("trade_no");

                //交易状态
                String trade_status = request.getParameter("trade_status");

                //交易金额
                String total_amount = request.getParameter("total_amount");
                if(total_amount != null && !total_amount.isEmpty()){
                    paymentAmount = new BigDecimal(total_amount);
                }


                if(trade_status.equals("TRADE_FINISHED")){
                    //判断该笔订单是否在商户网站中已经做过处理
                    //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    //如果有做过处理，不执行商户的业务程序

                    //注意：
                    //退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
                }else if (trade_status.equals("TRADE_SUCCESS")){

                    //付款完成后，支付宝系统发送该交易状态通知
                    String remark = "在线付款金额："+paymentAmount+"元；支付宝交易号："+trade_no +" 系统支付流水号："+out_trade_no;
                    this.systemPayment(out_trade_no,interfaceProduct,paymentAmount,remark,trade_no);


                    //判断该笔订单是否在商户网站中已经做过处理
                    //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    //如果有做过处理，不执行商户的业务程序

                    //注意：
                    //付款完成后，支付宝系统发送该交易状态通知
                }
                return "success";

            }
        }else if(interfaceProduct.equals(4)){//4.支付宝手机网站(alipay.trade.wap.pay接口)
            //获取支付宝POST过来反馈信息
            Map<String,String> params = new HashMap<String,String>();
            Map requestParams = request.getParameterMap();
            for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
                //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
                params.put(name, valueStr);
            }

            //商户订单号
            String out_trade_no = request.getParameter("out_trade_no");

            //支付宝交易号
            String trade_no = request.getParameter("trade_no");
            //交易状态(同步没有这个参数)
            String trade_status = request.getParameter("trade_status");

            //交易金额
            String total_amount = request.getParameter("total_amount");
            if(total_amount != null && !total_amount.isEmpty()){
                paymentAmount = new BigDecimal(total_amount);
            }
            if(params != null && params.size() >0){

                //计算得出通知验证结果
                boolean verify_result = false;
                try {
                    verify_result = AlipaySignature.rsaCheckV1(params, alipayConfig_Mobile.getAlipayPublicKey(interfaceProduct), alipayConfig_Mobile.CHARSET, "RSA2");
                } catch (AlipayApiException e) {
                    if (logger.isErrorEnabled()) {
                        logger.error("支付回调通知错误",e);
                    }
                }
                if(verify_result){//验证成功

                    if(trade_status.equals("TRADE_FINISHED")){
                        //判断该笔订单是否在商户网站中已经做过处理
                        //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                        //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                        //如果有做过处理，不执行商户的业务程序

                        //注意：
                        //如果签约的是可退款协议，退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
                        //如果没有签约可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
                    } else if (trade_status.equals("TRADE_SUCCESS")){
                        //判断该笔订单是否在商户网站中已经做过处理
                        //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                        //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                        //如果有做过处理，不执行商户的业务程序

                        //注意：
                        //如果签约的是可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。

                        String remark = "在线付款金额："+paymentAmount+"元；支付宝交易号："+trade_no+" 系统支付流水号："+out_trade_no ;
                        this.systemPayment(out_trade_no,interfaceProduct,paymentAmount,remark,trade_no);

                    }
                    return "success";
                }
            }
        }
        return "fail";
    }


    /**
     * 支付完成通知
     * @param interfaceProduct 接口产品 0:账户支付
     * @param request 请求信息
     * @return
     */
    public void paySuccess(Integer interfaceProduct,HttpServletRequest request){
        BigDecimal paymentAmount = new BigDecimal("0");

        PaymentVerificationLog paymentVerificationLog = null;

        if(interfaceProduct.equals(1)){//1.支付宝即时到账
            //获取支付宝GET过来反馈信息
            Map<String,String> params = new HashMap<String,String>();
            Map requestParams = request.getParameterMap();
            for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
                //	valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
                params.put(name, valueStr);
            }

            boolean signVerified = false; //调用SDK验证签名
            try {
                signVerified = AlipaySignature.rsaCheckV1(params, alipayConfig_PC.getAlipayPublicKey(interfaceProduct), alipayConfig_PC.CHARSET, alipayConfig_PC.SIGNTYPE);
            } catch (AlipayApiException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("支付完成通知错误",e);
                }
            }
            if(signVerified){
                //商户订单号
                String out_trade_no = request.getParameter("out_trade_no");

                //支付宝交易号
                String trade_no = request.getParameter("trade_no");


                //付款金额
                String total_amount = request.getParameter("total_amount");
                if(total_amount != null && !total_amount.isEmpty()){
                    paymentAmount = new BigDecimal(total_amount);
                }

                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //如果有做过处理，不执行商户的业务程序
                String remark = "在线付款金额："+paymentAmount+"元；支付宝交易号："+trade_no+" 系统支付流水号："+out_trade_no ;
                paymentVerificationLog = this.systemPayment(out_trade_no,interfaceProduct,paymentAmount,remark,trade_no);


            }
        }else if(interfaceProduct.equals(4)){//4.支付宝手机网站(alipay.trade.wap.pay)
            //获取支付宝GET过来反馈信息
            Map<String,String> params = new HashMap<String,String>();
            Map requestParams = request.getParameterMap();
            for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
                //	valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
                params.put(name, valueStr);
            }

            //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)

            //商户订单号
            String out_trade_no = request.getParameter("out_trade_no");

            //支付宝交易号
            String trade_no = request.getParameter("trade_no");

            //交易金额
            String total_amount = request.getParameter("total_amount");
            if(total_amount != null && !total_amount.isEmpty()){
                paymentAmount = new BigDecimal(total_amount);
            }
            //计算得出通知验证结果
            boolean verify_result = false;
            try {
                verify_result = AlipaySignature.rsaCheckV1(params, alipayConfig_Mobile.getAlipayPublicKey(interfaceProduct), alipayConfig_Mobile.CHARSET, "RSA2");
            } catch (AlipayApiException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("支付完成通知错误",e);
                }
            }

            if(verify_result){//验证成功

                String remark = "在线付款金额："+paymentAmount+"元；支付宝交易号："+trade_no+" 系统支付流水号："+out_trade_no ;

                paymentVerificationLog = this.systemPayment(out_trade_no,interfaceProduct,paymentAmount,remark,trade_no);



            }

        }

    }

    /**
     * 系统付款
     * @param paymentRunningNumber 支付流水号
     * @param interfaceProduct 接口产品
     * @param paymentAmount 支付金额
     * @param remark 备注
     * @param tradeNo 交易号
     * @return
     */
    private PaymentVerificationLog systemPayment(String paymentRunningNumber,Integer interfaceProduct,BigDecimal paymentAmount,String remark,String tradeNo){
        //校验支付校验日志有数据存在
        PaymentVerificationLog paymentVerificationLog = paymentRepository.findPaymentVerificationLogById(paymentRunningNumber);
        if(paymentVerificationLog != null){

            //如果发起支付金额与通知时获取的金额不一致
            if(paymentAmount.compareTo(paymentVerificationLog.getPaymentAmount()) != 0){
                return null;
            }
            if(paymentVerificationLog.getPaymentModule().equals(5)){//用户充值
                //给用户充值并写入支付日志

                PaymentLog paymentLog = new PaymentLog();
                paymentLog.setPaymentRunningNumber(paymentRunningNumber);//支付流水号
                paymentLog.setPaymentModule(5);//支付模块 5.用户充值
                paymentLog.setSourceParameterId(String.valueOf(paymentVerificationLog.getParameterId()));//参数Id
                paymentLog.setOperationUserType(2);//用户类型  0:系统  1: 员工  2:会员
                paymentLog.setOperationUserName(paymentVerificationLog.getUserName());//操作用户名称
                paymentLog.setAmountState(1);//金额状态  1:账户存入  2:账户支出
                paymentLog.setAmount(paymentAmount);//金额
                paymentLog.setInterfaceProduct(interfaceProduct);//接口产品
                paymentLog.setTradeNo(tradeNo);//交易号
                paymentLog.setUserName(paymentVerificationLog.getUserName());//用户名称
                Object new_paymentLog = paymentComponent.createPaymentLogObject(paymentLog);

                userRepository.onlineRecharge(paymentRunningNumber,paymentVerificationLog.getUserName(),paymentAmount,new_paymentLog);

                User user = userCacheManager.query_cache_findUserByUserName(paymentVerificationLog.getUserName());
                if(user != null){
                    //删除缓存
                    userCacheManager.delete_cache_findUserById(user.getId());
                    userCacheManager.delete_cache_findUserByUserName(user.getUserName());
                }
            }

        }
        return paymentVerificationLog;
    }

    /**
     * 跳转支付API
     * @param interfaceProduct 接口产口
     * @param paymentModule 支付模块
     * @param paymentRunningNumber 支付流水号
     * @param parameterId 参数Id    订单Id 售后服务服务Id 用户Id
     * @param orderName 订单名称
     * @param total 付款金额
     * @param code 银行简码
     * @param request 请求信息
     * @return
     */
    private String jumpPayAPI(Integer interfaceProduct,Integer paymentModule,String paymentRunningNumber,Long parameterId,String orderName,String total,String code,
                              HttpServletRequest request){
        //支付接口生成的html,只有Ajax方式提交数据才返回
        String createHtmlText = null;

        if(interfaceProduct.equals(1)){//1.支付宝即时到账
            String domain = WebUtil.getOriginDomain(request);

            if(domain == null || domain.trim().isEmpty()){
                domain = WebUtil.getUrl(request);
            }

            // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
            String notify_url = WebUtil.getUrl(request)+"notify/"+interfaceProduct;

            // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
            String return_url = domain +"paymentCompleted/"+interfaceProduct+"/"+paymentModule+"/"+parameterId;

            AlipayClient client = alipayConfig_PC.getAlipayClient(interfaceProduct);

            if(client != null){
                //设置请求参数
                AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
                // 设置同步地址
                alipayRequest.setReturnUrl(return_url);
                // 设置异步通知地址
                alipayRequest.setNotifyUrl(notify_url);

                //商户订单号，商户网站订单系统中唯一订单号，必填
                String out_trade_no = paymentRunningNumber;
                //付款金额，必填
                String total_amount = total;
                //订单名称，必填
                String subject = orderName;
                //商品描述，可空
                //	String body = new String(request.getParameter("WIDbody").getBytes("ISO-8859-1"),"UTF-8");

                alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                        + "\"total_amount\":\""+ total_amount +"\","
                        + "\"subject\":\""+ subject +"\","
                        + "\"timeout_express\":\"15m\"," //可空 该笔订单允许的最晚付款时间，逾期将关闭交易
                        //			+ "\"body\":\""+ body +"\","
                        + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
                //建立请求
                try {
                    createHtmlText = client.pageExecute(alipayRequest).getBody();
                } catch (AlipayApiException e) {
                    if (logger.isErrorEnabled()) {
                        logger.error("调用支付宝SDK生成表单错误",e);
                    }
                }
            }
        }else if(interfaceProduct.equals(4)){//4.支付宝手机网站(alipay.trade.wap.pay)
            String domain = WebUtil.getOriginDomain(request);

            if(domain == null || domain.trim().isEmpty()){
                domain = WebUtil.getUrl(request);
            }

            // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
            String notify_url = WebUtil.getUrl(request)+"notify/"+interfaceProduct;
            // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
            String return_url = domain+"paymentCompleted/"+interfaceProduct+"/"+paymentModule+"/"+parameterId;

            // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
            //调用RSA签名方式
            AlipayClient client = alipayConfig_Mobile.getAlipayClient(interfaceProduct);
            if(client != null){
                AlipayTradeWapPayRequest alipay_request=new AlipayTradeWapPayRequest();

                // 封装请求支付信息
                AlipayTradeWapPayModel model=new AlipayTradeWapPayModel();
                model.setOutTradeNo(paymentRunningNumber);// 商户订单号，商户网站订单系统中唯一订单号，必填
                model.setSubject(orderName);// 订单名称，必填
                model.setTotalAmount(total);// 付款金额，必填
                //  model.setBody(body);// 商品描述，可空
                model.setTimeoutExpress("15m");// 超时时间 可空  该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。注：若为空，则默认为15d。
                model.setProductCode("QUICK_WAP_PAY");// 销售产品码 必填
                alipay_request.setBizModel(model);
                // 设置异步通知地址
                alipay_request.setNotifyUrl(notify_url);
                // 设置同步地址
                alipay_request.setReturnUrl(return_url);

                // 调用SDK生成表单
                try {
                    createHtmlText = client.pageExecute(alipay_request).getBody();
                } catch (AlipayApiException e) {
                    if (logger.isErrorEnabled()) {
                        logger.error("调用支付宝SDK生成表单错误",e);
                    }
                }
            }
        }

        return createHtmlText;
    }
}
