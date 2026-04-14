package cms.service.membershipCard.impl;


import cms.component.JsonComponent;
import cms.component.TextFilterComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.membershipCard.MembershipCardCacheManager;
import cms.component.membershipCard.MembershipCardComponent;
import cms.component.user.UserCacheManager;
import cms.config.BusinessException;
import cms.dto.HtmlProcessingResult;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.dto.ReadPathResult;
import cms.dto.membershipCard.MembershipCardRequest;
import cms.model.membershipCard.MembershipCard;
import cms.model.membershipCard.MembershipCardOrder;
import cms.model.membershipCard.Specification;
import cms.model.user.User;
import cms.model.user.UserRole;
import cms.repository.membershipCard.MembershipCardRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.user.UserRepository;
import cms.repository.user.UserRoleRepository;
import cms.service.membershipCard.MembershipCardService;
import cms.utils.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 会员卡服务
 */
@Service
public class MembershipCardServiceImpl implements MembershipCardService {
    private static final Logger logger = LogManager.getLogger(MembershipCardServiceImpl.class);

    @Resource
    MembershipCardRepository membershipCardRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    MembershipCardComponent membershipCardComponent;
    @Resource
    JsonComponent jsonComponent;
    @Resource UserCacheManager userCacheManager;
    @Resource
    UserRepository userRepository;
    @Resource
    UserRoleRepository userRoleRepository;
    @Resource MembershipCardCacheManager membershipCardCacheManager;
    @Resource
    TextFilterComponent textFilterComponent;
    @Resource
    FileComponent fileComponent;


    /**
     * 获取会员卡列表
     * @param page 页码
     */
    public PageView<MembershipCard> getMembershipCardList(int page){
        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();


        PageView<MembershipCard> pageView = new PageView<MembershipCard>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据id字段降序排序


        //删除第一个and
        String jpql_str = StringUtils.difference(" and", jpql.toString());

        //调用分页算法类
        QueryResult<MembershipCard> qr = membershipCardRepository.getScrollData(MembershipCard.class, firstindex, pageView.getMaxresult(), jpql_str, params.toArray(),orderby);

        pageView.setQueryResult(qr);
        return pageView;
    }

    /**
     * 获取会员卡订单列表
     * @param page 页码
     * @param fileServerAddress 文件服务器随机地址
     */
    public PageView<MembershipCardOrder> getMembershipCardOrderList(int page,String fileServerAddress){
        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();

        PageView<MembershipCardOrder> pageView = new PageView<MembershipCardOrder>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("createDate", "desc");//根据id字段降序排序


        //删除第一个and
        String jpql_str = StringUtils.difference(" and", jpql.toString());

        //调用分页算法类
        QueryResult<MembershipCardOrder> qr = membershipCardRepository.getScrollData(MembershipCardOrder.class, firstindex, pageView.getMaxresult(), jpql_str, params.toArray(),orderby);

        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(MembershipCardOrder t :qr.getResultlist()){

                User user = userCacheManager.query_cache_findUserByUserName(t.getUserName());
                if(user != null){
                    t.setAccount(user.getAccount());
                    t.setNickname(user.getNickname());
                    if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                        t.setAvatarPath(fileServerAddress+user.getAvatarPath());
                        t.setAvatarName(user.getAvatarName());
                    }
                }
            }
        }


        pageView.setQueryResult(qr);
        return pageView;
    }

    /**
     * 获取用户会员卡订单列表
     * @param page 页码
     * @param userName 用户名称
     */
    public Map<String,Object> getUserMembershipCardOrderList(int page,String userName){
        if(userName == null || userName.trim().isEmpty()){
            throw new BusinessException(Map.of("userName", "用户名称不能为空"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();

        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();
        jpql.append(" and o.userName=?"+ (params.size()+1));
        params.add(userName.trim());

        PageView<MembershipCardOrder> pageView = new PageView<MembershipCardOrder>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("createDate", "desc");//根据id字段降序排序


        //删除第一个and
        String jpql_str = StringUtils.difference(" and", jpql.toString());

        //调用分页算法类
        QueryResult<MembershipCardOrder> qr = membershipCardRepository.getScrollData(MembershipCardOrder.class, firstindex, pageView.getMaxresult(), jpql_str, params.toArray(),orderby);

        pageView.setQueryResult(qr);


        User user = userRepository.findUserByUserName(userName);
        if(user != null){
            returnValue.put("currentUser", user);
        }
        returnValue.put("pageView", pageView);
        return returnValue;
    }



    /**
     * 获取添加会员卡界面信息
     * @return
     */
    public Map<String,Object> getAddMembershipCardViewModel(){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        //查询所有角色
        List<UserRole> userRoleList = userRoleRepository.findAllRole();
        if(userRoleList != null && userRoleList.size() >0){
            Iterator<UserRole> iterator = userRoleList.iterator();
            while (iterator.hasNext()) {
                UserRole userRole = iterator.next();
                if(userRole.getDefaultRole()){//如果是默认角色
                    iterator.remove();
                }
            }
            for(UserRole userRole : userRoleList){
                userRole.setSelected(true);//默认选中第一个
                break;
            }
        }

        returnValue.put("userRoleList", userRoleList);
        return returnValue;
    }

    /**
     * 添加会员卡
     * @param membershipCardRequest 会员卡表单
     * @param request 请求信息
     */
    public void addMembershipCard(MembershipCardRequest membershipCardRequest, HttpServletRequest request){
        Map<String, String> errors = new HashMap<String, String>();
        MembershipCard membershipCard = new MembershipCard();

        validateInput(membershipCardRequest,errors);


        //添加规格
        if(membershipCardRequest.getSpecificationRowTable()!= null && membershipCardRequest.getSpecificationRowTable().length>0){
            for (int i = 0; i < membershipCardRequest.getSpecificationRowTable().length; i++) {//循环全部提交的规格表项
                Specification specification = new Specification();

                //规格名称
                if(membershipCardRequest.getSpecificationName() != null && membershipCardRequest.getSpecificationName().length>0 && membershipCardRequest.getSpecificationName()[i] != null && membershipCardRequest.getSpecificationName()[i].trim().length() >0){
                    if(membershipCardRequest.getSpecificationName()[i].trim().length() >50){
                        errors.put("specificationName_"+i, "规格名称超过50个字符");
                    }else{
                        specification.setSpecificationName(membershipCardRequest.getSpecificationName()[i].trim());
                    }
                }else{
                    errors.put("specificationName_"+i, "请填写规格名称");
                }
                //是否启用
                if(membershipCardRequest.getEnable() != null && membershipCardRequest.getEnable().length>0 && membershipCardRequest.getEnable()[i] != null){
                    specification.setEnable(membershipCardRequest.getEnable()[i]);
                }else{
                    errors.put("enable_"+i, "请填写库存");
                }


                if(membershipCardRequest.getStock() != null && membershipCardRequest.getStock().length>0 && membershipCardRequest.getStock()[i] != null && !membershipCardRequest.getStock()[i].trim().isEmpty()){
                    if(!Verification.isPositiveIntegerZero(membershipCardRequest.getStock()[i].trim())){
                        errors.put("stock_"+i, "库存必须为数字");
                    }else{
                        if(membershipCardRequest.getStock()[i].trim().length() >17){
                            errors.put("stock_"+i, "数字最大为17位");
                        }else{
                            long _stock = Long.parseLong(membershipCardRequest.getStock()[i].trim());//库存量
                            specification.setStock(_stock);
                        }

                    }
                }else{
                    errors.put("stock_"+i, "请填写库存");
                }

                if(membershipCardRequest.getPoint() != null && membershipCardRequest.getPoint().length>0 && membershipCardRequest.getPoint()[i] != null && !membershipCardRequest.getPoint()[i].trim().isEmpty()){
                    if(!Verification.isPositiveIntegerZero(membershipCardRequest.getPoint()[i].trim())){
                        errors.put("point_"+i, "积分必须为整数");
                    }else{
                        if(membershipCardRequest.getPoint()[i].trim().length() >17){
                            errors.put("point_"+i, "数字最大为17位");
                        }else{
                            long _point = Long.parseLong(membershipCardRequest.getPoint()[i].trim());//积分
                            specification.setPoint(_point);
                        }

                    }
                }



                //市场价
                if(membershipCardRequest.getMarketPrice() != null && membershipCardRequest.getMarketPrice().length>0 && membershipCardRequest.getMarketPrice()[i] != null && !membershipCardRequest.getMarketPrice()[i].trim().isEmpty()){
                    if(!Verification.isAmount(membershipCardRequest.getMarketPrice()[i].trim())){
                        errors.put("marketPrice_"+i, "市场价必须为金额类型");
                    }else{
                        if(membershipCardRequest.getMarketPrice()[i].trim().length() >10){
                            errors.put("marketPrice_"+i, "数字最大为10位");
                        }else{
                            specification.setMarketPrice(new BigDecimal(membershipCardRequest.getMarketPrice()[i].trim()));
                        }

                    }
                }
                //销售价
                if(membershipCardRequest.getSellingPrice() != null && membershipCardRequest.getSellingPrice().length>0 && membershipCardRequest.getSellingPrice()[i] != null && !membershipCardRequest.getSellingPrice()[i].trim().isEmpty()){
                    if(!Verification.isAmount(membershipCardRequest.getSellingPrice()[i].trim())){
                        errors.put("sellingPrice_"+i, "销售价必须为金额类型");
                    }else{
                        if(membershipCardRequest.getSellingPrice()[i].trim().length() >10){
                            errors.put("sellingPrice_"+i, "数字最大为10位");
                        }else{
                            specification.setSellingPrice(new BigDecimal(membershipCardRequest.getSellingPrice()[i].trim()));
                        }

                    }
                }
                if((specification.getPoint() ==null || specification.getPoint() <= 0) &&
                        (specification.getSellingPrice() == null || specification.getSellingPrice().compareTo(new BigDecimal("0"))<=0)){
                    errors.put("sellingPrice_"+i, "积分和销售价必须填一个");

                }

                if(membershipCardRequest.getDuration() != null && membershipCardRequest.getDuration().length >0 && membershipCardRequest.getDuration()[i] != null && !membershipCardRequest.getDuration()[i].trim().isEmpty()){
                    if(!Verification.isPositiveIntegerZero(membershipCardRequest.getDuration()[i].trim())){
                        errors.put("duration_"+i, "时长必须为整数");
                    }else{
                        if(membershipCardRequest.getDuration()[i].trim().length() >8){
                            errors.put("duration_"+i, "数字最大为8位");
                        }else{
                            int _duration = Integer.parseInt(membershipCardRequest.getDuration()[i].trim());//时长
                            specification.setDuration(_duration);
                        }

                    }
                }else{
                    errors.put("duration_"+i, "时长不能为空");
                }

                //时长单位
                if(membershipCardRequest.getUnit() != null && membershipCardRequest.getUnit().length >0 && membershipCardRequest.getUnit()[i] != null){
                    specification.setUnit(membershipCardRequest.getUnit()[i]);
                }else{
                    errors.put("unit_"+i, "时长单位不能为空");
                }

                //排序
                specification.setSort(i);


                membershipCard.addSpecification(specification);
            }
        }

        if(membershipCardRequest.getDescriptionTag() != null && membershipCardRequest.getDescriptionTag().length >0){
            List<String> descriptionTagList = new ArrayList<String>();
            for(String tag : membershipCardRequest.getDescriptionTag()){
                if(tag != null && !tag.trim().isEmpty()){
                    descriptionTagList.add(tag.trim());
                }

            }
            membershipCard.setDescriptionTagList(descriptionTagList);
            membershipCard.setDescriptionTagFormat(jsonComponent.toJSONString(descriptionTagList));
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }
        HtmlProcessingResult htmlProcessingResult = null;
        if(membershipCardRequest.getIntroduction() != null && !membershipCardRequest.getIntroduction().trim().isEmpty()){
            //过滤标签
            String introduction = textFilterComponent.filterTag(request,membershipCardRequest.getIntroduction());
           htmlProcessingResult = textFilterComponent.filterHtml(request,introduction,"membershipCard",null);
            String value = htmlProcessingResult.getHtmlContent();

            membershipCard.setIntroduction(value);//简介
        }

        //设置最低价和最高价
        if(membershipCard.getSpecificationList() != null && membershipCard.getSpecificationList().size() >0){
            if(membershipCard.getSpecificationList().size() == 1){
                membershipCard.setLowestPrice(membershipCard.getSpecificationList().get(0).getSellingPrice());
                membershipCard.setHighestPrice(membershipCard.getSpecificationList().get(0).getSellingPrice());
            }else{
                List<BigDecimal> priceList = new ArrayList<BigDecimal>();
                for(Specification specification :membershipCard.getSpecificationList()){
                    if(specification.getSellingPrice() != null){
                        priceList.add(specification.getSellingPrice());
                    }
                }
                if(priceList.size() >0){
                    //排序 默认从低到高
                    Collections.sort(priceList);

                    membershipCard.setLowestPrice(priceList.get(0));
                    membershipCard.setHighestPrice(priceList.get(priceList.size()-1));
                }

            }
        }

        //设置最低积分和最高积分
        if(membershipCard.getSpecificationList() != null && membershipCard.getSpecificationList().size() >0){
            if(membershipCard.getSpecificationList().size() == 1){
                membershipCard.setLowestPoint(membershipCard.getSpecificationList().get(0).getPoint());
                membershipCard.setHighestPoint(membershipCard.getSpecificationList().get(0).getPoint());
            }else{
                List<Long> pointList = new ArrayList<Long>();
                for(Specification specification :membershipCard.getSpecificationList()){
                    if(specification.getPoint() != null){
                        pointList.add(specification.getPoint());
                    }
                }
                if(pointList.size() >0){
                    //排序 默认从低到高
                    Collections.sort(pointList);

                    membershipCard.setLowestPoint(pointList.get(0));
                    membershipCard.setHighestPoint(pointList.get(pointList.size()-1));
                }
            }
        }


        membershipCard.setName(membershipCardRequest.getName().trim());//名称
        membershipCard.setSubtitle(membershipCardRequest.getSubtitle().trim());
        membershipCard.setUserRoleId(membershipCardRequest.getUserRoleId().trim());

        membershipCard.setState(membershipCardRequest.getState());//是否上架   1:上架   2:下架
        membershipCard.setSort(membershipCardRequest.getSort());//排序
        membershipCard.setCreateDate(LocalDateTime.now());//创建时间

        membershipCardRepository.saveMembershipCard(membershipCard);

        //清除缓存
        membershipCardCacheManager.delete_cache_findById(membershipCard.getId());
        membershipCardCacheManager.delete_cache_findSpecificationByMembershipCardId(membershipCard.getId());

        //删除图片锁
        if(htmlProcessingResult != null && htmlProcessingResult.getImageNameList() != null && !htmlProcessingResult.getImageNameList().isEmpty()){
            for(String imageName :htmlProcessingResult.getImageNameList()){

                if(imageName != null && !imageName.trim().isEmpty()){
                    fileComponent.deleteLock("file"+ File.separator+"membershipCard"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));

                }
            }
        }
        //音视频
        if(htmlProcessingResult != null && htmlProcessingResult.getMediaNameList() != null && !htmlProcessingResult.getMediaNameList().isEmpty()){
            for(String mediaName :htmlProcessingResult.getMediaNameList()){
                if(mediaName != null && !mediaName.trim().isEmpty()){
                    fileComponent.deleteLock("file"+File.separator+"membershipCard"+File.separator+"lock"+File.separator,mediaName.replaceAll("/","_"));

                }
            }
        }
        //文件
        if(htmlProcessingResult != null && htmlProcessingResult.getFileNameList() != null && !htmlProcessingResult.getFileNameList().isEmpty()){
            for(String fileName : htmlProcessingResult.getFileNameList()){
                if(fileName != null && !fileName.trim().isEmpty()){
                    fileComponent.deleteLock("file"+File.separator+"membershipCard"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));

                }
            }
        }
    }

    /**
     * 校验表单字段
     * @param membershipCardRequest 会员卡表单
     * @param errors 错误信息
     */
    private void validateInput(MembershipCardRequest membershipCardRequest, Map<String, String> errors) {
        if(membershipCardRequest.getName() != null && !membershipCardRequest.getName().trim().isEmpty()){
            if(membershipCardRequest.getName().trim().length() >150){
                errors.put("name", "不能大于150个字符");
            }
        }else{
            errors.put("name", "名称不能为空");
        }
        if(membershipCardRequest.getSubtitle() != null && !membershipCardRequest.getSubtitle().trim().isEmpty()){
            if(membershipCardRequest.getSubtitle().trim().length() >150){
                errors.put("subtitle", "不能大于150个字符");
            }
        }

        if(membershipCardRequest.getUserRoleId() == null || membershipCardRequest.getUserRoleId().trim().isEmpty()){
            errors.put("userRoleId", "角色不能为空");
        }

    }


    /**
     * 获取修改会员卡界面信息
     * @param membershipCardId 会员卡Id
     * @return
     */
    public Map<String,Object> getEditMembershipCardViewModel(Long membershipCardId){
        if(membershipCardId == null || membershipCardId <=0L){
            throw new BusinessException(Map.of("membershipCardId", "会员卡Id不能为空"));
        }
        MembershipCard membershipCard = membershipCardRepository.findById(membershipCardId);
        if(membershipCard == null){
            throw new BusinessException(Map.of("membershipCard", "会员卡不存在"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        String descriptionTagFormat = membershipCard.getDescriptionTagFormat();
        if(descriptionTagFormat != null && !descriptionTagFormat.trim().isEmpty()){
            List<String> descriptionTagList = jsonComponent.toGenericObject(descriptionTagFormat.trim(), new TypeReference< List<String> >(){});
            membershipCard.setDescriptionTagList(descriptionTagList);
        }
        if(membershipCard.getIntroduction() != null && !membershipCard.getIntroduction().trim().isEmpty()){
            //处理富文本路径
            membershipCard.setIntroduction(fileComponent.processRichTextFilePath(membershipCard.getIntroduction(),"membershipCard"));
        }
        returnValue.put("membershipCard",membershipCard);
        List<Specification> specificationList = membershipCardRepository.findSpecificationByMembershipCardId(membershipCardId);
        for (Specification specification : specificationList) {

            //库存状态
            specification.setStockStatus(1);



        }

        //查询所有角色
        List<UserRole> userRoleList = userRoleRepository.findAllRole();
        if(userRoleList != null && userRoleList.size() >0){
            Iterator<UserRole> iterator = userRoleList.iterator();
            while (iterator.hasNext()) {
                UserRole userRole = iterator.next();
                if(membershipCard.getUserRoleId() != null && userRole.getId().equals(membershipCard.getUserRoleId())){
                    userRole.setSelected(true);
                }
                if(userRole.getDefaultRole()){//如果是默认角色
                    iterator.remove();
                }
            }

        }
        returnValue.put("userRoleList", userRoleList);
        //规格
        returnValue.put("specificationList",specificationList);
        return returnValue;
    }
    /**
     * 修改会员卡
     * @param membershipCardRequest 会员卡表单
     * @param request 请求信息
     */
    public void editMembershipCard(MembershipCardRequest membershipCardRequest, HttpServletRequest request){
        if(membershipCardRequest.getMembershipCardId() == null || membershipCardRequest.getMembershipCardId() <=0L){
            throw new BusinessException(Map.of("membershipCardId", "会员卡Id不能为空"));
        }
        MembershipCard old_membershipCard = membershipCardRepository.findById(membershipCardRequest.getMembershipCardId());
        if(old_membershipCard == null){
            throw new BusinessException(Map.of("membershipCardId", "会员卡不存在"));
        }
        //旧规格
        List<Specification> old_specificationList = membershipCardRepository.findSpecificationByMembershipCardId(membershipCardRequest.getMembershipCardId());
        String old_introduction = old_membershipCard.getIntroduction();
        Map<String, String> errors = new HashMap<String, String>();
        MembershipCard membershipCard = new MembershipCard();

        validateInput(membershipCardRequest,errors);


        //添加规格
        if(membershipCardRequest.getSpecificationRowTable()!= null && membershipCardRequest.getSpecificationRowTable().length>0){
            for (int i = 0; i < membershipCardRequest.getSpecificationRowTable().length; i++) {//循环全部提交的规格表项
                Specification specification = new Specification();

                specification.setMembershipCardId(membershipCardRequest.getMembershipCardId());
                if(membershipCardRequest.getSpecificationId() != null && membershipCardRequest.getSpecificationId().length >0 && membershipCardRequest.getSpecificationId()[i] != null && membershipCardRequest.getSpecificationId()[i] >0){
                    specification.setId(membershipCardRequest.getSpecificationId()[i]);
                }

                //规格名称
                if(membershipCardRequest.getSpecificationName() != null && membershipCardRequest.getSpecificationName().length>0 && membershipCardRequest.getSpecificationName()[i] != null && membershipCardRequest.getSpecificationName()[i].trim().length() >0){
                    if(membershipCardRequest.getSpecificationName()[i].trim().length() >50){
                        errors.put("specificationName_"+i, "规格名称超过50个字符");
                    }else{
                        specification.setSpecificationName(membershipCardRequest.getSpecificationName()[i].trim());
                    }
                }else{
                    errors.put("specificationName_"+i, "请填写规格名称");
                }
                //是否启用
                if(membershipCardRequest.getEnable() != null && membershipCardRequest.getEnable().length>0 && membershipCardRequest.getEnable()[i] != null){
                    specification.setEnable(membershipCardRequest.getEnable()[i]);
                }else{
                    errors.put("enable_"+i, "请填写库存");
                }

                //更改库存状态
                if(membershipCardRequest.getStockStatus() != null && membershipCardRequest.getStockStatus().length >0 && membershipCardRequest.getStockStatus()[i] != null){
                    specification.setStockStatus(membershipCardRequest.getStockStatus()[i]);
                }

                if(specification.getStockStatus().equals(0)){// 0:不变  1:增加   2:减少
                    if(membershipCardRequest.getStock() != null && membershipCardRequest.getStock().length>0 && membershipCardRequest.getStock()[i] != null && !membershipCardRequest.getStock()[i].trim().isEmpty()){
                        if(!Verification.isPositiveIntegerZero(membershipCardRequest.getStock()[i].trim())){
                            errors.put("stock_"+i, "库存必须为数字");
                        }else{
                            if(membershipCardRequest.getStock()[i].trim().length() >17){
                                errors.put("stock_"+i, "数字最大为17位");
                            }else{
                                long _stock = Long.parseLong(membershipCardRequest.getStock()[i].trim());//库存量
                                specification.setStock(_stock);
                            }

                        }
                    }else{
                        errors.put("stock_"+i, "请填写库存");
                    }
                }else{
                    //解决回显库存问题
                    if(old_specificationList != null && old_specificationList.size() >0){
                        for(Specification old_specification :old_specificationList){
                            if(old_specification.getId().equals(specification.getId())){
                                specification.setStock(old_specification.getStock());
                                break;
                            }
                        }
                    }

                    //更改库存
                    if(membershipCardRequest.getChangeStock() != null && membershipCardRequest.getChangeStock().length >0 && membershipCardRequest.getChangeStock()[i] != null && !membershipCardRequest.getChangeStock()[i].trim().isEmpty()){
                        if(!Verification.isPositiveIntegerZero(membershipCardRequest.getChangeStock()[i].trim())){
                            errors.put("stock_"+i, "库存必须为数字");
                        }else{
                            if(membershipCardRequest.getChangeStock()[i].trim().length() >17){
                                errors.put("stock_"+i, "数字最大为17位");
                            }else{
                                specification.setChangeStock(Long.parseLong(membershipCardRequest.getChangeStock()[i].trim()));

                                if(specification.getStockStatus().equals(1)){// 0:不变  1:增加
                                    if((specification.getStock()+specification.getChangeStock()) > 99999999999999999L){
                                        errors.put("stock_"+i, "总库存数量不能超过17位数字");
                                    }
                                }
                                if(specification.getStockStatus().equals(2) && specification.getChangeStock() > specification.getStock()){
                                    errors.put("stock_"+i, "减少库存量不能大于已存在库存量");
                                }
                            }
                        }
                    }
                }

                if(membershipCardRequest.getPoint() != null && membershipCardRequest.getPoint().length>0 && membershipCardRequest.getPoint()[i] != null && !membershipCardRequest.getPoint()[i].trim().isEmpty()){
                    if(!Verification.isPositiveIntegerZero(membershipCardRequest.getPoint()[i].trim())){
                        errors.put("point_"+i, "积分必须为整数");
                    }else{
                        if(membershipCardRequest.getPoint()[i].trim().length() >17){
                            errors.put("point_"+i, "数字最大为17位");
                        }else{
                            long _point = Long.parseLong(membershipCardRequest.getPoint()[i].trim());//积分
                            specification.setPoint(_point);
                        }

                    }
                }



                //市场价
                if(membershipCardRequest.getMarketPrice() != null && membershipCardRequest.getMarketPrice().length>0 && membershipCardRequest.getMarketPrice()[i] != null && !membershipCardRequest.getMarketPrice()[i].trim().isEmpty()){
                    if(!Verification.isAmount(membershipCardRequest.getMarketPrice()[i].trim())){
                        errors.put("marketPrice_"+i, "市场价必须为金额类型");
                    }else{
                        if(membershipCardRequest.getMarketPrice()[i].trim().length() >10){
                            errors.put("marketPrice_"+i, "数字最大为10位");
                        }else{
                            specification.setMarketPrice(new BigDecimal(membershipCardRequest.getMarketPrice()[i].trim()));
                        }

                    }
                }
                //销售价
                if(membershipCardRequest.getSellingPrice() != null && membershipCardRequest.getSellingPrice().length>0 && membershipCardRequest.getSellingPrice()[i] != null && !membershipCardRequest.getSellingPrice()[i].trim().isEmpty()){
                    if(!Verification.isAmount(membershipCardRequest.getSellingPrice()[i].trim())){
                        errors.put("sellingPrice_"+i, "销售价必须为金额类型");
                    }else{
                        if(membershipCardRequest.getSellingPrice()[i].trim().length() >10){
                            errors.put("sellingPrice_"+i, "数字最大为10位");
                        }else{
                            specification.setSellingPrice(new BigDecimal(membershipCardRequest.getSellingPrice()[i].trim()));
                        }

                    }
                }
                if((specification.getPoint() ==null || specification.getPoint() <= 0) &&
                        (specification.getSellingPrice() == null || specification.getSellingPrice().compareTo(new BigDecimal("0"))<=0)){
                    errors.put("sellingPrice_"+i, "积分和销售价必须填一个");

                }

                if(membershipCardRequest.getDuration() != null && membershipCardRequest.getDuration().length >0 && membershipCardRequest.getDuration()[i] != null && !membershipCardRequest.getDuration()[i].trim().isEmpty()){
                    if(!Verification.isPositiveIntegerZero(membershipCardRequest.getDuration()[i].trim())){
                        errors.put("duration_"+i, "时长必须为整数");
                    }else{
                        if(membershipCardRequest.getDuration()[i].trim().length() >8){
                            errors.put("duration_"+i, "数字最大为8位");
                        }else{
                            int _duration = Integer.parseInt(membershipCardRequest.getDuration()[i].trim());//时长
                            specification.setDuration(_duration);
                        }

                    }
                }else{
                    errors.put("duration_"+i, "时长不能为空");
                }

                //时长单位
                if(membershipCardRequest.getUnit() != null && membershipCardRequest.getUnit().length >0 && membershipCardRequest.getUnit()[i] != null){
                    specification.setUnit(membershipCardRequest.getUnit()[i]);
                }else{
                    errors.put("unit_"+i, "时长单位不能为空");
                }

                //排序
                specification.setSort(i);


                membershipCard.addSpecification(specification);
            }
        }

        if(membershipCardRequest.getDescriptionTag() != null && membershipCardRequest.getDescriptionTag().length >0){
            List<String> descriptionTagList = new ArrayList<String>();
            for(String tag : membershipCardRequest.getDescriptionTag()){
                if(tag != null && !tag.trim().isEmpty()){
                    descriptionTagList.add(tag.trim());
                }

            }
            membershipCard.setDescriptionTagList(descriptionTagList);
            membershipCard.setDescriptionTagFormat(jsonComponent.toJSONString(descriptionTagList));
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        List<String> imageNameList = new ArrayList<>();//上传图片文件名称
        List<String> mediaNameList = new ArrayList<>();//上传音视频文件名称
        List<String> fileNameList = new ArrayList<>();//上传文件名称
        if(membershipCardRequest.getIntroduction() != null && !membershipCardRequest.getIntroduction().trim().isEmpty()){
            //过滤标签
            String introduction = textFilterComponent.filterTag(request,membershipCardRequest.getIntroduction());
            HtmlProcessingResult htmlProcessingResult = textFilterComponent.filterHtml(request,introduction,"membershipCard",null);
            String value = htmlProcessingResult.getHtmlContent();
            imageNameList = htmlProcessingResult.getImageNameList();
            mediaNameList = htmlProcessingResult.getMediaNameList();
            fileNameList = htmlProcessingResult.getFileNameList();
            membershipCard.setIntroduction(value);//简介
        }

        //设置最低价和最高价
        if(membershipCard.getSpecificationList() != null && membershipCard.getSpecificationList().size() >0){
            if(membershipCard.getSpecificationList().size() == 1){
                membershipCard.setLowestPrice(membershipCard.getSpecificationList().get(0).getSellingPrice());
                membershipCard.setHighestPrice(membershipCard.getSpecificationList().get(0).getSellingPrice());
            }else{
                List<BigDecimal> priceList = new ArrayList<BigDecimal>();
                for(Specification specification :membershipCard.getSpecificationList()){
                    if(specification.getSellingPrice() != null){
                        priceList.add(specification.getSellingPrice());
                    }
                }
                if(priceList.size() >0){
                    //排序 默认从低到高
                    Collections.sort(priceList);

                    membershipCard.setLowestPrice(priceList.get(0));
                    membershipCard.setHighestPrice(priceList.get(priceList.size()-1));
                }

            }
        }

        //设置最低积分和最高积分
        if(membershipCard.getSpecificationList() != null && membershipCard.getSpecificationList().size() >0){
            if(membershipCard.getSpecificationList().size() == 1){
                membershipCard.setLowestPoint(membershipCard.getSpecificationList().get(0).getPoint());
                membershipCard.setHighestPoint(membershipCard.getSpecificationList().get(0).getPoint());
            }else{
                List<Long> pointList = new ArrayList<Long>();
                for(Specification specification :membershipCard.getSpecificationList()){
                    if(specification.getPoint() != null){
                        pointList.add(specification.getPoint());
                    }
                }
                if(pointList.size() >0){
                    //排序 默认从低到高
                    Collections.sort(pointList);

                    membershipCard.setLowestPoint(pointList.get(0));
                    membershipCard.setHighestPoint(pointList.get(pointList.size()-1));
                }
            }
        }



        List<Specification> add_specificationList = new ArrayList<Specification>();//添加规格表
        List<Specification> update_specificationList = new ArrayList<Specification>();//修改规格表
        List<Long> delete_specificationIdList = new ArrayList<Long>();//删除规格表Id
        if(old_specificationList != null && old_specificationList.size() >0){//删除相同Id的旧规格
            for(Specification ps : old_specificationList){
                delete_specificationIdList.add(ps.getId());
            }
        }

        //比较规格表
        List<Specification> new_productStyleList = membershipCard.getSpecificationList();//新规格
        if(new_productStyleList != null && new_productStyleList.size() >0){
            for(Specification new_specification : new_productStyleList){
                if(new_specification.getId() != null && new_specification.getId() >0L){
                    if(old_specificationList != null && !old_specificationList.contains(new_specification)){
                        update_specificationList.add(new_specification);
                    }else{
                        //如果有修改库存
                        if(new_specification.getChangeStock() != null && new_specification.getChangeStock() >0L){
                            update_specificationList.add(new_specification);
                        }
                    }

                    //删除相同Id的旧规格
                    if( delete_specificationIdList.contains(new_specification.getId())){
                        delete_specificationIdList.remove(new_specification.getId());
                    }
                }else{
                    add_specificationList.add(new_specification);
                }
            }
        }

        membershipCard.setName(membershipCardRequest.getName().trim());//名称
        membershipCard.setSubtitle(membershipCardRequest.getSubtitle().trim());
        membershipCard.setUserRoleId(membershipCardRequest.getUserRoleId().trim());
        membershipCard.setState(membershipCardRequest.getState());//是否上架   1:上架   2:下架
        membershipCard.setSort(membershipCardRequest.getSort());//排序
        membershipCard.setId(old_membershipCard.getId());//Id

        membershipCardRepository.updateMembershipCard(membershipCard, add_specificationList, update_specificationList, delete_specificationIdList);

        //清除缓存
        membershipCardCacheManager.delete_cache_findById(membershipCardRequest.getMembershipCardId());
        membershipCardCacheManager.delete_cache_findSpecificationByMembershipCardId(membershipCardRequest.getMembershipCardId());


        List<String> oldPathFileList = new ArrayList<String>();//旧路径文件

        ReadPathResult readPathResult = textFilterComponent.readPathName(old_introduction,"membershipCard");

        //旧图片
        List<String> old_imageNameList = readPathResult.getImageNameList();
        if(old_imageNameList != null && old_imageNameList.size() >0){

            Iterator<String> iter = old_imageNameList.iterator();
            while (iter.hasNext()) {
                String imageName = iter.next();
                for(String new_imageName : imageNameList){
                    if(imageName.equals("file/membershipCard/"+new_imageName)){
                        iter.remove();
                        break;
                    }
                }
            }
            if(old_imageNameList.size() >0){
                for(String imageName : old_imageNameList){
                    oldPathFileList.add(FileUtil.toSystemPath(imageName));
                }

            }
        }
        //旧影音
        List<String> old_mediaNameList = readPathResult.getMediaNameList();
        if(old_mediaNameList != null && old_mediaNameList.size() >0){
            Iterator<String> iter = old_mediaNameList.iterator();
            while (iter.hasNext()) {
                String mediaName = iter.next();
                for(String new_mediaName : mediaNameList){
                    if(mediaName.equals("file/membershipCard/"+new_mediaName)){
                        iter.remove();
                        break;
                    }
                }
            }
            if(old_mediaNameList.size() >0){
                for(String mediaName : old_mediaNameList){
                    oldPathFileList.add(FileUtil.toSystemPath(mediaName));
                }

            }
        }

        //旧文件
        List<String> old_fileNameList = readPathResult.getFileNameList();
        if(old_fileNameList != null && old_fileNameList.size() >0){
            Iterator<String> iter = old_fileNameList.iterator();
            while (iter.hasNext()) {
                String fileName = iter.next();
                for(String new_fileName : fileNameList){
                    if(fileName.equals("file/membershipCard/"+new_fileName)){
                        iter.remove();
                        break;
                    }
                }
            }
            if(old_fileNameList.size() >0){
                for(String fileName : old_fileNameList){
                    oldPathFileList.add(FileUtil.toSystemPath(fileName));
                }

            }
        }


        //删除旧路径文件
        if(oldPathFileList.size() >0){
            for(String oldPathFile :oldPathFileList){
                //替换路径中的..号
                oldPathFile = FileUtil.toRelativePath(oldPathFile);

                //删除旧路径文件
                Boolean state = fileComponent.deleteFile(oldPathFile);
                if(state != null && !state){
                    //替换指定的字符，只替换第一次出现的
                    oldPathFile = Strings.CS.replaceOnce(oldPathFile, "file"+File.separator+"membershipCard"+File.separator, "");
                    oldPathFile = Strings.CS.replace(oldPathFile, File.separator, "_");//替换所有出现过的字符

                    //创建删除失败文件
                    fileComponent.failedStateFile("file"+File.separator+"membershipCard"+File.separator+"lock"+File.separator+oldPathFile);

                }
            }
        }

        //删除图片锁
        if(imageNameList != null && imageNameList.size() >0){
            for(String imageName :imageNameList){
                if(imageName != null && !imageName.trim().isEmpty()){
                    fileComponent.deleteLock("file"+File.separator+"membershipCard"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
                }
            }
        }
        //删除音视频锁
        if(mediaNameList != null && mediaNameList.size() >0){
            for(String mediaName :mediaNameList){
                if(mediaName != null && !mediaName.trim().isEmpty()){
                    fileComponent.deleteLock("file"+File.separator+"membershipCard"+File.separator+"lock"+File.separator,mediaName.replaceAll("/","_"));

                }
            }
        }
        //删除文件锁
        if(fileNameList != null && fileNameList.size() >0){
            for(String fileName :fileNameList){
                if(fileName != null && !fileName.trim().isEmpty()){
                    fileComponent.deleteLock("file"+File.separator+"membershipCard"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));

                }
            }
        }
    }

    /**
     * 上传文件
     * @param dir 上传类型，分别为image、media、file
     * @param fileName 文件名称 预签名时有值
     * @param fileServerAddress 文件服务器地址
     * @param file 文件
     * @return
     */
    public Map<String, Object> uploadFile(String dir, String fileName, String fileServerAddress, MultipartFile file) {

        Map<String,Object> returnValue = new HashMap<String,Object>();
        if (dir == null) {
            returnValue.put("error", 1);
            returnValue.put("message", "参数不能为空");
            return returnValue;
        }
        DateTime dateTime = new DateTime();
        String date = dateTime.toString("yyyy-MM-dd");
        int fileSystem = fileComponent.getFileSystem();
        if (fileSystem == 10 || fileSystem == 20 || fileSystem == 30) { // SeaweedFS, MinIO, 阿里云OSS
            return handleCloudUpload(dir, fileName, date, returnValue);
        } else { // 本地系统
            return handleLocalUpload(dir, file, date, fileServerAddress, returnValue);
        }
    }

    /**
     * 处理云存储系统（如SeaweedFS, MinIO, OSS）的文件上传
     * @param dir 上传类型，分别为image、media、file
     * @param fileName 文件名称 预签名时有值
     * @param date 日期
     * @param result 返回内容
     * @return
     */
    private Map<String, Object> handleCloudUpload(String dir, String fileName, String date, Map<String, Object> result){
        if (fileName == null || fileName.trim().isEmpty()) {
            result.put("error", 1);
            result.put("message", "名称不能为空");
            return result;
        }

        List<String> formatList = getAllowFormats(dir,result);
        if (formatList == null || formatList.isEmpty()) {
            return result;
        }

        if(!FileUtil.validateFileSuffix(fileName.trim(),formatList)){
            result.put("error", 1);
            result.put("message", "该文件格式不允许上传");
            return result;
        }


        //文件锁目录
        String lockPathDir = "file"+File.separator+"membershipCard"+File.separator+"lock"+File.separator;
        //取得文件后缀
        String suffix = FileUtil.getExtension(fileName.trim()).toLowerCase();
        //构建文件名称
        String newFileName = UUIDUtil.getUUID32()+ "." + suffix;


        //生成锁文件
        try {
            fileComponent.addLock(lockPathDir,date +"_"+dir+"_"+newFileName);
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("生成锁文件错误",e);
            }
        }
        String presigne = fileComponent.createPresigned("file/membershipCard/"+date+"/"+dir+"/"+newFileName,null);//创建预签名


        //返回预签名URL
        result.put("error", 0);//0成功  1错误
        result.put("url", presigne);
        result.put("title", HtmlEscape.escape(fileName));//旧文件名称
        return result;
    }

    /**
     * 根据文件类型获取允许上传的格式列表。
     */
    private List<String> getAllowFormats(String dir,Map<String, Object> result) {
        List<String> formatList = new ArrayList<>();
        switch (dir) {
            case "image":
                formatList = CommentedProperties.readRichTextAllowImageUploadFormat();
                break;
            case "media":
                formatList = CommentedProperties.readRichTextAllowVideoUploadFormat();
                break;
            case "file":
                formatList = CommentedProperties.readRichTextAllowFileUploadFormat();
                break;
            default:
                result.put("error", 1);
                result.put("message", "缺少dir参数");
        }
        return formatList;
    }

    /**
     * 处理本地文件系统上传
     * @param dir dir 上传类型，分别为image、media、file
     * @param file 文件
     * @param date 日期
     * @param fileServerAddress 文件服务器地址
     * @param result 返回内容
     * @return
     */
    private Map<String, Object> handleLocalUpload(String dir, MultipartFile file, String date, String fileServerAddress, Map<String, Object> result){
        if (file == null || file.isEmpty()) {
            result.put("error", 1);
            result.put("message", "文件不能为空");
            return result;
        }

        //当前文件名称
        String sourceFileName = file.getOriginalFilename();
        String suffix = FileUtil.getExtension(sourceFileName).toLowerCase();
        List<String> formatList = getAllowFormats(dir,result);
        if (formatList == null || formatList.isEmpty()) {
            return result;
        }
        //验证文件类型
        if(!FileUtil.validateFileSuffix(sourceFileName.trim(),formatList)){
            result.put("error", 1);
            result.put("message", "该文件格式不允许上传");
            return result;
        }

        //文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
        String pathDir = "file"+File.separator+"membershipCard"+File.separator + date +File.separator +dir+ File.separator;
        //文件锁目录
        String lockPathDir = "file"+File.separator+"membershipCard"+File.separator+"lock"+File.separator;

        //构建文件名称
        String newFileName = UUIDUtil.getUUID32()+ "." + suffix;

        //生成文件保存目录
        fileComponent.createFolder(pathDir);
        //生成锁文件保存目录
        fileComponent.createFolder(lockPathDir);

        try {
            //生成锁文件
            fileComponent.addLock(lockPathDir,date +"_"+dir+"_"+newFileName);
            //保存文件
            fileComponent.writeFile(pathDir, newFileName,file.getBytes());
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("生成锁文件错误",e);
            }
        }

        //上传成功
        result.put("error", 0);//0成功  1错误
        result.put("url", fileServerAddress+"file/membershipCard/"+date+"/"+dir+"/"+newFileName);
        result.put("title", HtmlEscape.escape(file.getOriginalFilename()));//旧文件名称
        return result;
    }

    /**
     * 删除会员卡
     * @param membershipCardId 会员卡Id
     */
    public void deleteMembershipCard(Long membershipCardId){
        if(membershipCardId == null || membershipCardId <=0){
            throw new BusinessException(Map.of("membershipCardId", "会员卡Id不能为空"));
        }

        MembershipCard membershipCard = membershipCardRepository.findById(membershipCardId);
        if(membershipCard == null){
            throw new BusinessException(Map.of("membershipCard", "会员卡不存在"));
        }

        int i = membershipCardRepository.deleteMembershipCard(membershipCardId);

        //清除缓存
        membershipCardCacheManager.delete_cache_findById(membershipCardId);
        membershipCardCacheManager.delete_cache_findSpecificationByMembershipCardId(membershipCardId);

        ReadPathResult readPathResult = textFilterComponent.readPathName(membershipCard.getIntroduction(),"membershipCard");

        List<String> filePathList = new ArrayList<String>();

        //删除图片
        List<String> imageNameList = readPathResult.getImageNameList();
        for(String imageName :imageNameList){
            filePathList.add(FileUtil.toSystemPath(imageName));

        }
        //删除影音
        List<String> mediaNameList = readPathResult.getMediaNameList();
        for(String mediaName :mediaNameList){
            filePathList.add(FileUtil.toSystemPath(mediaName));
        }
        //删除文件
        List<String> fileNameList = readPathResult.getFileNameList();
        for(String fileName :fileNameList){
            filePathList.add(FileUtil.toSystemPath(fileName));
        }
        for(String filePath :filePathList){
            //替换路径中的..号
            filePath = FileUtil.toRelativePath(filePath);

            //删除旧路径文件
            Boolean state = fileComponent.deleteFile(filePath);
            if(state != null && !state){
                //替换指定的字符，只替换第一次出现的
                filePath = Strings.CS.replaceOnce(filePath, "file"+File.separator+"membershipCard"+File.separator, "");
                //创建删除失败文件
                fileComponent.failedStateFile("file"+File.separator+"membershipCard"+File.separator+"lock"+File.separator+FileUtil.toUnderline(filePath));
            }
        }
    }


}
