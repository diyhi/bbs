package cms.service.frontend.impl;

import cms.component.JsonComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.membershipCard.MembershipCardCacheManager;
import cms.component.membershipCard.MembershipCardComponent;
import cms.component.payment.PaymentComponent;
import cms.component.user.PointComponent;
import cms.component.user.PointLogConfig;
import cms.component.user.UserCacheManager;
import cms.component.user.UserRoleCacheManager;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.dto.user.AccessUser;
import cms.model.membershipCard.MembershipCard;
import cms.model.membershipCard.MembershipCardOrder;
import cms.model.membershipCard.Specification;
import cms.model.payment.PaymentLog;
import cms.model.setting.SystemSetting;
import cms.model.user.PointLog;
import cms.model.user.User;
import cms.model.user.UserRole;
import cms.model.user.UserRoleGroup;
import cms.repository.membershipCard.MembershipCardRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.user.UserRepository;
import cms.repository.user.UserRoleRepository;
import cms.service.frontend.MembershipCardClientService;
import cms.utils.AccessUserThreadLocal;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 前台会员卡服务
 */
@Service
public class MembershipCardClientServiceImpl implements MembershipCardClientService {
    @Resource
    FileComponent fileComponent;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    SettingRepository settingRepository;
    @Resource
    MembershipCardRepository membershipCardRepository;
    @Resource MembershipCardCacheManager membershipCardCacheManager;
    @Resource
    UserRoleRepository userRoleRepository;
    @Resource
    UserRepository userRepository;
    @Resource UserRoleCacheManager userRoleCacheManager;
    @Resource
    PointComponent pointComponent;
    @Resource
    MembershipCardComponent membershipCardComponent;
    @Resource
    PointLogConfig pointLogConfig;
    @Resource
    PaymentComponent paymentComponent;
    @Resource
    UserCacheManager userCacheManager;
    /**
     * 获取会员卡列表
     * @return
     */
    public List<MembershipCard> getMembershipCardList(){
        List<MembershipCard> new_membershipCardList = new ArrayList<MembershipCard>();

        List<MembershipCard> membershipCardList = membershipCardRepository.findAllMembershipCard();
        if(membershipCardList != null && membershipCardList.size() >0){
            for(MembershipCard membershipCard : membershipCardList){
                if(membershipCard.getState().equals(1)){//状态 1:上架
                    String descriptionTagFormat = membershipCard.getDescriptionTagFormat();
                    if(descriptionTagFormat != null && !descriptionTagFormat.trim().isEmpty()){
                        List<String> descriptionTagList = jsonComponent.toGenericObject(descriptionTagFormat.trim(), new TypeReference< List<String> >(){});
                        membershipCard.setDescriptionTagList(descriptionTagList);
                    }

                    new_membershipCardList.add(membershipCard);
                }
            }
        }
        return new_membershipCardList;
    }

    /**
     * 获取会员卡明细
     * @param membershipCardId 会员卡Id
     */
    public MembershipCard getMemberCardDetail(Long membershipCardId){
        if(membershipCardId != null){
            MembershipCard membershipCard = membershipCardCacheManager.query_cache_findById(membershipCardId);

            if(membershipCard != null){
                if(membershipCard.getIntroduction() != null && !membershipCard.getIntroduction().trim().isEmpty()){
                    //处理富文本路径
                    membershipCard.setIntroduction(fileComponent.processRichTextFilePath(membershipCard.getIntroduction(),"membershipCard"));
                }

                String descriptionTagFormat = membershipCard.getDescriptionTagFormat();
                if(descriptionTagFormat != null && !descriptionTagFormat.trim().isEmpty()){
                    List<String> descriptionTagList = jsonComponent.toGenericObject(descriptionTagFormat.trim(), new TypeReference< List<String> >(){});
                    membershipCard.setDescriptionTagList(descriptionTagList);
                }


                List<Specification> specificationList = membershipCardCacheManager.query_cache_findSpecificationByMembershipCardId(membershipCardId);
                if(specificationList != null && specificationList.size() >0){

                    for(Specification specification : specificationList){
                        specification.setStockOccupy(0L);//占用库存量(已出售库存量)不显示
                        if(specification.isEnable()){//启用
                            membershipCard.addSpecification(specification);
                        }
                    }

                    return membershipCard;
                }
            }


        }
        return null;
    }

    /**
     * 购买会员卡
     * @param specificationId 规格Id
     * @return
     */
    public void addMembershipCard(Long specificationId){
        Map<String,String> errors = new HashMap<String,String>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("membershipCard", "只读模式不允许提交数据"));
        }

        if(specificationId == null){
            throw new BusinessException(Map.of("specification", "规格Id不能为空"));
        }
        Specification specification = membershipCardRepository.findSpecificationBySpecificationId(specificationId);

        if(specification == null){
            throw new BusinessException(Map.of("specification", "规格不存在"));
        }

        MembershipCard membershipCard = membershipCardRepository.findById(specification.getMembershipCardId());
        if(membershipCard != null){
            if(membershipCard.getState() >1){
                errors.put("membershipCard", "会员卡已下架");
            }
        }else{
            errors.put("membershipCard", "会员卡不存在");
        }

        UserRole userRole = userRoleRepository.findRoleById(membershipCard.getUserRoleId());
        if(userRole == null){
            errors.put("role", "角色不存在");
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        //购买数量
        int quantity =1;
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        MembershipCardOrder membershipCardOrder = new MembershipCardOrder();

        if(specification.getSellingPrice() != null){
            BigDecimal price = specification.getSellingPrice().multiply(new BigDecimal(String.valueOf(quantity)));//应付款 = 规格销售价*购买数量
            membershipCardOrder.setAccountPayable(price);//应付款
            membershipCardOrder.setPaymentAmount(price);//已支付金额
        }
        if(specification.getPoint() != null){
            Long point = specification.getPoint() * quantity;//应付积分 = 规格积分*购买数量
            membershipCardOrder.setAccountPoint(point);//应付积分
            membershipCardOrder.setPaymentPoint(point);//已支付积分
        }

        //规格库存
        if(specification.getStock() < quantity){
            errors.put("stock", "库存不足");
        }
        User user = userRepository.findUserByUserName(accessUser.getUserName());
        if(membershipCardOrder.getPaymentPoint() >0L){
            if(user.getPoint() < membershipCardOrder.getPaymentPoint()){
                errors.put("point", "积分不足");
            }

        }
        //扣除用户金额
        if(membershipCardOrder.getPaymentAmount().compareTo(new BigDecimal("0")) >0){
            if(user.getDeposit().compareTo(membershipCardOrder.getPaymentAmount()) < 0){
                errors.put("deposit", "预存款不足");
            }
        }

        if(!specification.isEnable()){//规格禁用
            errors.put("specification", "此规格已下架");

        }
        if(membershipCard.getState() >1){
            errors.put("membershipCard", "此会员卡已下架");
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        UserRoleGroup add_userRoleGroup = null;
        UserRoleGroup update_userRoleGroup = null;

        LocalDateTime currentTime = LocalDateTime.now();

        UserRoleGroup userRoleGroup = userRoleRepository.findRoleGroupByUserRoleId(userRole.getId(), accessUser.getUserName());
        if(userRoleGroup == null){
            add_userRoleGroup = new UserRoleGroup();
            add_userRoleGroup.setUserName(accessUser.getUserName());
            add_userRoleGroup.setUserRoleId(userRole.getId());
            LocalDateTime dateTime = LocalDateTime.now();

            if(specification.getUnit().equals(10)){//时长单位 10.小时
                dateTime = dateTime.plusHours(specification.getDuration());// 增加小时
            }else if(specification.getUnit().equals(20)){//20.日
                dateTime = dateTime.plusDays(specification.getDuration()); // 增加天
            }else if(specification.getUnit().equals(30)){//30.月
                dateTime = dateTime.plusMonths(specification.getDuration()); // 增加月
            }else if(specification.getUnit().equals(40)){//40.年
                dateTime = dateTime.plusYears(specification.getDuration()); // 增加年
            }
            add_userRoleGroup.setValidPeriodEnd(dateTime);


        }else{
            update_userRoleGroup = new UserRoleGroup();
            update_userRoleGroup.setUserName(accessUser.getUserName());
            update_userRoleGroup.setUserRoleId(userRole.getId());
            LocalDateTime dateTime = userRoleGroup.getValidPeriodEnd();


            //和系统时间比
            if(dateTime.isAfter(currentTime)){//如果在系统时间之后
                if(specification.getUnit().equals(10)){//时长单位 10.小时
                    dateTime = dateTime.plusHours(specification.getDuration());// 增加小时
                }else if(specification.getUnit().equals(20)){//20.日
                    dateTime = dateTime.plusDays(specification.getDuration()); // 增加天
                }else if(specification.getUnit().equals(30)){//30.月
                    dateTime = dateTime.plusMonths(specification.getDuration()); // 增加月
                }else if(specification.getUnit().equals(40)){//40.年
                    dateTime = dateTime.plusYears(specification.getDuration()); // 增加年
                }
            }else{//如果已过期,则有效期从当前时间算起
                dateTime = LocalDateTime.now();
                if(specification.getUnit().equals(10)){//时长单位 10.小时
                    dateTime = dateTime.plusHours(specification.getDuration());// 增加小时
                }else if(specification.getUnit().equals(20)){//20.日
                    dateTime = dateTime.plusDays(specification.getDuration()); // 增加天
                }else if(specification.getUnit().equals(30)){//30.月
                    dateTime = dateTime.plusMonths(specification.getDuration()); // 增加月
                }else if(specification.getUnit().equals(40)){//40.年
                    dateTime = dateTime.plusYears(specification.getDuration()); // 增加年
                }
            }

            update_userRoleGroup.setValidPeriodEnd(dateTime);
        }




        membershipCardOrder.setOrderId(membershipCardComponent.nextNumber(accessUser.getUserId()));//订单号
        membershipCardOrder.setUserName(accessUser.getUserName());//用户账号
        membershipCardOrder.setCreateDate(currentTime);//订单创建时间

        membershipCardOrder.setUserRoleId(membershipCard.getUserRoleId());//购买的会员卡用户角色Id
        membershipCardOrder.setRoleName(userRole.getName());//购买的会员卡用户角色名称
        membershipCardOrder.setMembershipCardId(membershipCard.getId());//购买的会员卡Id
        membershipCardOrder.setSpecificationId(specification.getId());//购买的会员卡规格Id
        membershipCardOrder.setSpecificationName(specification.getSpecificationName());//购买的会员卡规格名称
        membershipCardOrder.setQuantity(quantity);//购买的会员卡数量
        membershipCardOrder.setDuration(specification.getDuration());//购买的会员卡有效期时长
        membershipCardOrder.setUnit(specification.getUnit());//购买的会员卡时长单位 10.小时 20.日 30.月 40.年

        //扣除用户积分
        Object new_pointLog = null;
        if(membershipCardOrder.getPaymentPoint() >0L){
            PointLog pointLog = new PointLog();
            pointLog.setId(pointLogConfig.createPointLogId(accessUser.getUserId()));
            pointLog.setModule(500);//模块 500.会员卡支付
            pointLog.setParameterId(membershipCardOrder.getOrderId());//参数Id
            pointLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
            pointLog.setOperationUserName(accessUser.getUserName());//操作用户名称

            pointLog.setPoint(membershipCardOrder.getPaymentPoint());//积分
            pointLog.setUserName(accessUser.getUserName());//用户名称
            pointLog.setRemark("");
            pointLog.setPointState(2);//积分状态  1:账户存入  2:账户支出
            new_pointLog = pointComponent.createPointLogObject(pointLog);
        }

        Object new_paymentLog = null;
        //扣除用户金额
        if(membershipCardOrder.getPaymentAmount().compareTo(new BigDecimal("0")) >0){


            PaymentLog paymentLog = new PaymentLog();
            paymentLog.setPaymentRunningNumber(paymentComponent.createRunningNumber(accessUser.getUserId()));//支付流水号
            paymentLog.setPaymentModule(1);//支付模块 1.订单支付
            paymentLog.setSourceParameterId(String.valueOf(membershipCardOrder.getOrderId()));//参数Id
            paymentLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
            paymentLog.setOperationUserName(accessUser.getUserName());

            paymentLog.setAmount(membershipCardOrder.getPaymentAmount());//金额
            paymentLog.setInterfaceProduct(-1);//接口产品
            paymentLog.setTradeNo("");//交易号
            paymentLog.setUserName(accessUser.getUserName());//用户名称
            paymentLog.setRemark("");//备注


            paymentLog.setAmountState(2);//金额状态  1:账户存入  2:账户支出
            new_paymentLog = paymentComponent.createPaymentLogObject(paymentLog);

        }

        try {
            //保存会员卡
            membershipCardRepository.saveMembershipCardOrder(membershipCardOrder,add_userRoleGroup,update_userRoleGroup,new_pointLog,new_paymentLog);

            //删除规格缓存
            membershipCardCacheManager.delete_cache_findSpecificationByMembershipCardId(membershipCard.getId());
            userRoleCacheManager.delete_cache_findRoleGroupByUserName(membershipCardOrder.getUserName());

            userCacheManager.delete_cache_findUserById(accessUser.getUserId());
            userCacheManager.delete_cache_findUserByUserName(accessUser.getUserName());

        }catch (org.springframework.orm.jpa.JpaSystemException e) {
            throw new BusinessException(Map.of("membershipCard", "创建会员卡订单错误"));

        }

    }

    /**
     * 会员卡订单列表
     * @param page 页码
     * @return
     */
    public PageView<MembershipCardOrder> getMembershipCardOrderList(int page){
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        //调用分页算法代码
        PageView<MembershipCardOrder> pageView = new PageView<MembershipCardOrder>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();

        QueryResult<MembershipCardOrder> qr =  membershipCardRepository.findMembershipCardOrderByUserName(accessUser.getUserName(),firstIndex, pageView.getMaxresult());

        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        return pageView;
    }
}
