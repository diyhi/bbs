package cms.service.payment.impl;


import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.model.payment.PaymentLog;
import cms.model.payment.PaymentVerificationLog;
import cms.model.user.User;
import cms.repository.payment.PaymentRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.user.UserRepository;
import cms.service.payment.PaymentLogService;
import cms.utils.Verification;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 支付日志服务
 */
@Service
public class PaymentLogServiceImpl implements PaymentLogService {

    @Resource
    PaymentRepository paymentRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    UserRepository userRepository;


    /**
     * 获取支付日志列表
     * @param page 页码
     * @param userName 用户名称
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public Map<String,Object> getPaymentLogList(int page, String userName,String fileServerAddress){
        if(userName == null || userName.isEmpty()){
            throw new BusinessException(Map.of("userName", "用户名称不能为空"));
        }

        Map<String,Object> returnValue = new HashMap<String,Object>();

        //调用分页算法代码
        PageView<PaymentLog> pageView = new PageView<PaymentLog>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();


        User user = userRepository.findUserByUserName(userName);
        if(user != null){
            QueryResult<PaymentLog> qr =  paymentRepository.findPaymentLogPage(user.getId(),user.getUserName(),firstIndex, pageView.getMaxresult());

            //将查询结果集传给分页List
            pageView.setQueryResult(qr);

            User currentUser = new User();
            currentUser.setId(user.getId());
            currentUser.setAccount(user.getAccount());
            currentUser.setNickname(user.getNickname());
            if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                currentUser.setAvatarPath(fileServerAddress+user.getAvatarPath());
                currentUser.setAvatarName(user.getAvatarName());
            }
            returnValue.put("currentUser", currentUser);

            returnValue.put("pageView", pageView);
        }
        return returnValue;
    }

    /**
     * 获取支付日志明细
     * @param paymentRunningNumber 支付流水号
     * @param id 用户Id
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public Map<String,Object> getPaymentLogDetails(String paymentRunningNumber,Long id,String fileServerAddress){
        if(paymentRunningNumber == null || paymentRunningNumber.trim().isEmpty()){
            throw new BusinessException(Map.of("paymentRunningNumber", "支付流水号不能为空"));
        }
        if(id == null || id <=0L){
            throw new BusinessException(Map.of("id", "用户Id不能为空"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        if(paymentRunningNumber.trim().length() >9 && Verification.isPositiveInteger(paymentRunningNumber)){
            PaymentLog paymentLog = paymentRepository.findPaymentLogByPaymentRunningNumber(paymentRunningNumber);
            if(paymentLog != null){
                returnValue.put("paymentLog", paymentLog);
            }
        }
        User user = userRepository.findUserById(id);
        if(user != null){
            User currentUser = new User();
            currentUser.setId(user.getId());
            currentUser.setAccount(user.getAccount());
            currentUser.setNickname(user.getNickname());
            if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                currentUser.setAvatarPath(fileServerAddress+user.getAvatarPath());
                currentUser.setAvatarName(user.getAvatarName());
            }
            returnValue.put("currentUser", currentUser);

        }
        return returnValue;
    }

    /**
     * 支付校验日志分页
     * @param page 页码
     * @param paymentModule 支付模块 1.订单支付 3.售后服务换货/返修支付 5.用户充值
     * @param parameterId 参数Id
     * @param userName 用户名称
     * @return
     */
    public PageView<PaymentVerificationLog> getPaymentVerificationLogPage(int page, Integer paymentModule, Long parameterId, String userName) {

        if(paymentModule == null || paymentModule <=0 || userName == null || userName.trim().isEmpty()) {
            throw new BusinessException(Map.of("paymentModule", "支付模块或用户名称不能为空"));
        }

        StringBuffer jpql = new StringBuffer("");

        List<Object> params = new ArrayList<Object>();
        //调用分页算法代码
        PageView<PaymentVerificationLog> pageView = new PageView<PaymentVerificationLog>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);

        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();


        jpql.append(" and o.parameterId=?").append((params.size()+1));
        params.add(parameterId);

        jpql.append(" and o.userName=?").append((params.size()+1));
        params.add(userName);

        jpql.append(" and o.paymentModule=?").append((params.size()+1));//and o.code=?1
        params.add(paymentModule);



        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
        orderby.put("id", "desc");

        //删除第一个and
        String sql = StringUtils.difference(" and", jpql.toString());

        QueryResult<PaymentVerificationLog> qr = paymentRepository.getScrollData(PaymentVerificationLog.class,firstindex, pageView.getMaxresult(),sql, params.toArray(),orderby);


        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        return pageView;
    }
}
