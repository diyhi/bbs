package cms.component.redEnvelope;



import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import cms.component.JsonComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.payment.PaymentComponent;
import cms.component.user.UserCacheManager;
import cms.dto.QueryResult;
import cms.model.payment.PaymentLog;
import cms.model.redEnvelope.GiveRedEnvelope;
import cms.model.redEnvelope.ReceiveRedEnvelope;
import cms.model.user.User;
import cms.repository.redEnvelope.RedEnvelopeRepository;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cglib.beans.BeanCopier;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import tools.jackson.core.type.TypeReference;

/**
 * 红包组件
 *
 */
@Component("redEnvelopeComponent")
public class RedEnvelopeComponent {
	private static final Logger logger = LogManager.getLogger(RedEnvelopeComponent.class);
	
    @Resource ReceiveRedEnvelopeConfig receiveRedEnvelopeConfig;
    @Resource RedEnvelopeCacheManager redEnvelopeCacheManager;
    @Resource RedEnvelopeRepository redEnvelopeRepository;
    @Resource PaymentComponent paymentComponent;
    @Resource UserCacheManager userCacheManager;
    @Resource FileComponent fileComponent;
    @Resource JsonComponent jsonComponent;

    
    
    /**
     * 生成收红包对象
     * @return
     */
    public Object createReceiveRedEnvelopeObject(ReceiveRedEnvelope receiveRedEnvelope){
    	//表编号
		int tableNumber = receiveRedEnvelopeConfig.receiveRedEnvelopeIdRemainder(receiveRedEnvelope.getId());
		if(tableNumber == 0){//默认对象
			return receiveRedEnvelope;
		}else{//带下划线对象
			Class<?> c;
			try {
				c = Class.forName("cms.model.redEnvelope.ReceiveRedEnvelope_"+tableNumber);
                //获取无参数构造函数
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
				
				BeanCopier copier = BeanCopier.create(ReceiveRedEnvelope.class,object.getClass(), false); 
			
				copier.copy(receiveRedEnvelope,object, null);
				return object;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成收红包对象",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成收红包对象",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成收红包对象",e);
		        }
			} catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成收红包对象",e);
                }
            } catch (NoSuchMethodException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成收红包对象",e);
                }
            }
		}
		return null;
    }
    
    
    
   /**
    * 生成红包金额
    * @param totalAmount 总金额
    * @param giveQuantity 发放数量
    * @return
    */
    public List<BigDecimal> createRedEnvelopeAmount(BigDecimal totalAmount,Integer giveQuantity){
    	
    	
        List<BigDecimal> amountList = new ArrayList<BigDecimal>();
        
        BigDecimal remainMoney = totalAmount;
        int remainSize = giveQuantity;
        
        while (remainSize != 0) {

        	if (remainSize == 1) {
            	remainSize--;
            	amountList.add(remainMoney.setScale(2, RoundingMode.DOWN));//在小数点后两位处截断
            	break;
            }

            BigDecimal random = BigDecimal.valueOf(Math.random());
            BigDecimal min   = BigDecimal.valueOf(0.01);//最小的红包金额

            BigDecimal halfRemainSize = BigDecimal.valueOf(remainSize).divide(new BigDecimal(2), RoundingMode.UP);
            BigDecimal max1 = remainMoney.divide(halfRemainSize, RoundingMode.DOWN);
            BigDecimal minRemainAmount = min.multiply(BigDecimal.valueOf(remainSize - 1)).setScale(2, RoundingMode.DOWN);
            BigDecimal max2 = remainMoney.subtract(minRemainAmount);
            BigDecimal max = (max1.compareTo(max2) < 0) ? max1 : max2;

            BigDecimal money = random.multiply(max).setScale(2, RoundingMode.DOWN);
            money = money.compareTo(min) < 0 ? min: money;

            remainSize--;
            remainMoney = remainMoney.subtract(money).setScale(2, RoundingMode.DOWN);
            amountList.add(money);
        }
    	
    	
    	
    	return amountList;
    }
    
   /**
    * 拆红包
    * @param receiveRedEnvelope 收红包Id
    * @param amount 红包金额
    * @param userId 用户Id
    * @param userName 用户名称
    */
    public Integer unwrapRedEnvelope(ReceiveRedEnvelope receiveRedEnvelope,BigDecimal amount,Long userId,String userName){
    	PaymentLog paymentLog = new PaymentLog();
		paymentLog.setPaymentRunningNumber(paymentComponent.createRunningNumber(userId));//支付流水号
		paymentLog.setPaymentModule(130);//支付模块 130.话题收红包
		paymentLog.setSourceParameterId(receiveRedEnvelope.getId());//参数Id 
		paymentLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
		paymentLog.setOperationUserName(userName);//操作用户名称
		paymentLog.setAmountState(1);//金额状态  1:账户存入  2:账户支出 
		paymentLog.setAmount(amount);//金额
		paymentLog.setInterfaceProduct(0);//接口产品
		paymentLog.setUserName(userName);//用户名称
		paymentLog.setTimes(receiveRedEnvelope.getReceiveTime());
		//金额日志
		Object paymentLogObject = paymentComponent.createPaymentLogObject(paymentLog);

		Integer j = redEnvelopeRepository.unwrapRedEnvelope(receiveRedEnvelope.getId(),amount,receiveRedEnvelope.getVersion(),userName,paymentLogObject);
		
		
    	//删除缓存
        redEnvelopeCacheManager.delete_cache_findByReceiveRedEnvelopeId(receiveRedEnvelope.getId());
    	
    	return j;
    }
    
    
   	/**
   	 * 查询用户领取到的红包金额
   	 * @param giveRedEnvelope 发红包
   	 * @param userId 用户Id
   	 */
    public BigDecimal queryReceiveRedEnvelopeAmount(GiveRedEnvelope giveRedEnvelope, Long userId){
    	if(giveRedEnvelope.getType().equals(20)){//20.公共随机红包(随机金额)
    		List<ReceiveRedEnvelope> receiveRedEnvelopeList = this.dateConvertReceiveRedEnvelope(giveRedEnvelope.getDistributionAmountGroup(),giveRedEnvelope.getGrabRedEnvelopeUserIdGroup());
    		
    		for(ReceiveRedEnvelope receiveRedEnvelope: receiveRedEnvelopeList){
    			if(receiveRedEnvelope.getReceiveUserId() != null && receiveRedEnvelope.getReceiveUserId().equals(userId)){
    				
    				return receiveRedEnvelope.getAmount();
    			}
    		}
    	}else if(giveRedEnvelope.getType().equals(30)){//30.公共定额红包(固定金额)
    		//抢到红包的用户Id组
    		String _grabRedEnvelopeUserIdGroup = StringUtils.substringBeforeLast(giveRedEnvelope.getGrabRedEnvelopeUserIdGroup(), ",");//删除最后一个逗号 从右往左截取到相等的字符,保留左边的
    		List<Long> grabRedEnvelopeUserIdGroupList = jsonComponent.toGenericObject(_grabRedEnvelopeUserIdGroup+"]", new TypeReference< List<Long> >(){});
    		if(grabRedEnvelopeUserIdGroupList != null && grabRedEnvelopeUserIdGroupList.size() >0){
				for(Long grabRedEnvelopeUserId : grabRedEnvelopeUserIdGroupList){
					if(grabRedEnvelopeUserId.equals(userId)){
		    			return giveRedEnvelope.getSingleAmount();
		    		}
				}
			}
    	}
    	return null;
    }
    
    /**
   	 * 数据转换为收红包对象
   	 * @param distributionAmountGroup 分配金额组
   	 * @param grabRedEnvelopeUserIdGroup 抢到红包的用户Id组
   	 */
    public List<ReceiveRedEnvelope> dateConvertReceiveRedEnvelope(String distributionAmountGroup,String grabRedEnvelopeUserIdGroup){
    	//金额对应的用户Id
		List<ReceiveRedEnvelope> receiveRedEnvelopeList = new ArrayList<ReceiveRedEnvelope>();
		//分配金额组
		List<BigDecimal> distributionAmountGroupList = jsonComponent.toGenericObject(distributionAmountGroup, new TypeReference< List<BigDecimal> >(){});
		
		//抢到红包的用户Id组
		String _grabRedEnvelopeUserIdGroup = StringUtils.substringBeforeLast(grabRedEnvelopeUserIdGroup, ",");//删除最后一个逗号 从右往左截取到相等的字符,保留左边的
		List<Long> grabRedEnvelopeUserIdGroupList = jsonComponent.toGenericObject(_grabRedEnvelopeUserIdGroup+"]", new TypeReference< List<Long> >(){});
		if(distributionAmountGroupList != null && distributionAmountGroupList.size() >0){
			for(int i =0; i<distributionAmountGroupList.size(); i++){
				ReceiveRedEnvelope receiveRedEnvelope = new ReceiveRedEnvelope();
				receiveRedEnvelope.setAmount(distributionAmountGroupList.get(i));
				
				if(grabRedEnvelopeUserIdGroupList != null && grabRedEnvelopeUserIdGroupList.size() >0 && grabRedEnvelopeUserIdGroupList.size() >i){
					Long grabRedEnvelopeUserId = grabRedEnvelopeUserIdGroupList.get(i);//抢到红包的用户Id
					receiveRedEnvelope.setReceiveUserId(grabRedEnvelopeUserId);
				}
				receiveRedEnvelopeList.add(receiveRedEnvelope);
			}
			
		}
    	return receiveRedEnvelopeList;
    }
    
    
    /**
     * 按条件查询收红包分页
     * @param giveRedEnvelope 发红包
     * @param includeNotReceive 是否包含未领取红包(仅20.公共随机红包(随机金额))
     * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @param sort 排序 true:正序 false:倒序
	 * @param isCancelAccountInfo 是否显示注销用户信息
     */
    public QueryResult<ReceiveRedEnvelope> queryReceiveRedEnvelopeByCondition(GiveRedEnvelope giveRedEnvelope, boolean includeNotReceive, int firstIndex, int maxResult, boolean sort, boolean isCancelAccountInfo){
    	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    	List<ReceiveRedEnvelope> receiveRedEnvelopeList = new ArrayList<ReceiveRedEnvelope>();
    	if(giveRedEnvelope.getType().equals(20)){//20.公共随机红包(随机金额)
    		if(includeNotReceive){//如果包含未领取红包
    			receiveRedEnvelopeList = this.dateConvertReceiveRedEnvelope(giveRedEnvelope.getDistributionAmountGroup(),giveRedEnvelope.getGrabRedEnvelopeUserIdGroup());
    		}else{
    			List<ReceiveRedEnvelope> returnReceiveRedEnvelopeList = this.dateConvertReceiveRedEnvelope(giveRedEnvelope.getDistributionAmountGroup(),giveRedEnvelope.getGrabRedEnvelopeUserIdGroup());
    			for(ReceiveRedEnvelope receiveRedEnvelope : returnReceiveRedEnvelopeList){
    				if(receiveRedEnvelope.getReceiveUserId() != null && receiveRedEnvelope.getReceiveUserId() > 0L){
    					receiveRedEnvelopeList.add(receiveRedEnvelope);
    				}
    			}
    			
    		}
    		
    	}else if(giveRedEnvelope.getType().equals(30)){//30.公共定额红包(固定金额)
    		//抢到红包的用户Id组
    		String _grabRedEnvelopeUserIdGroup = StringUtils.substringBeforeLast(giveRedEnvelope.getGrabRedEnvelopeUserIdGroup(), ",");//删除最后一个逗号 从右往左截取到相等的字符,保留左边的
    		List<Long> grabRedEnvelopeUserIdGroupList = jsonComponent.toGenericObject(_grabRedEnvelopeUserIdGroup+"]", new TypeReference< List<Long> >(){});
    		if(grabRedEnvelopeUserIdGroupList != null && grabRedEnvelopeUserIdGroupList.size() >0){
				for(Long grabRedEnvelopeUserId : grabRedEnvelopeUserIdGroupList){
					ReceiveRedEnvelope receiveRedEnvelope = new ReceiveRedEnvelope();
					receiveRedEnvelope.setAmount(giveRedEnvelope.getSingleAmount());
					receiveRedEnvelope.setReceiveUserId(grabRedEnvelopeUserId);
					receiveRedEnvelopeList.add(receiveRedEnvelope);
				}
			}
    	}
    	
    	if(!sort){//逆序排列
    		Collections.reverse(receiveRedEnvelopeList);
    	}
    	
    	
    	
    	QueryResult<ReceiveRedEnvelope> qr = new QueryResult<ReceiveRedEnvelope>();
    	
    	
    	List<ReceiveRedEnvelope> receiveRedEnvelopePage = new ArrayList<ReceiveRedEnvelope>();
    	
    	
    	int start = firstIndex;//索引起始
    	int end = firstIndex+maxResult;//索引结束
    	int size = receiveRedEnvelopeList.size();//总条数
    	
    	if(start > size){
    		start = size;
    	}
    	if(end > size){
    		end = size;
    	}
    	
    	for(int i=start; i<end; i++){
    		ReceiveRedEnvelope receiveRedEnvelope = receiveRedEnvelopeList.get(i);
    		if(receiveRedEnvelope != null){
    			receiveRedEnvelopePage.add(receiveRedEnvelope);
    		}
    	}
    	
    	for(ReceiveRedEnvelope receiveRedEnvelope : receiveRedEnvelopePage){
    		if(receiveRedEnvelope.getReceiveUserId() != null && receiveRedEnvelope.getReceiveUserId() >0L){
    			receiveRedEnvelope.setId(receiveRedEnvelopeConfig.createReceiveRedEnvelopeId(giveRedEnvelope.getId(),receiveRedEnvelope.getReceiveUserId()));
    			User user = userCacheManager.query_cache_findUserById(receiveRedEnvelope.getReceiveUserId());
    			
    			if(user != null){
    				receiveRedEnvelope.setReceiveUserName(user.getUserName());
    				if(isCancelAccountInfo || user.getCancelAccountTime().equals(-1L)){
    					receiveRedEnvelope.setReceiveAccount(user.getAccount());
            			receiveRedEnvelope.setReceiveNickname(user.getNickname());
            			
            			if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
            				receiveRedEnvelope.setReceiveAvatarPath(fileComponent.fileServerAddress(request)+user.getAvatarPath());
            				receiveRedEnvelope.setReceiveAvatarName(user.getAvatarName());
            			}
    				}
        			ReceiveRedEnvelope valid_receiveRedEnvelope = redEnvelopeRepository.findByReceiveRedEnvelopeId(receiveRedEnvelopeConfig.createReceiveRedEnvelopeId(giveRedEnvelope.getId(),user.getId()));
        			if(valid_receiveRedEnvelope != null){
        				receiveRedEnvelope.setReceiveTime(valid_receiveRedEnvelope.getReceiveTime());
        				
        				//如果红包还没拆，则执行拆红包
        				if(valid_receiveRedEnvelope.getAmount() != null && valid_receiveRedEnvelope.getAmount().compareTo(new BigDecimal("0")) ==0 && user != null){
        					this.unwrapRedEnvelope(valid_receiveRedEnvelope,receiveRedEnvelope.getAmount(),user.getId(),user.getUserName());
        				}
        			}
        		}

    		}
    	}
    	

    	qr.setResultlist(receiveRedEnvelopePage);
		
		
		qr.setTotalrecord(receiveRedEnvelopeList.size());
    	return qr;
    }
    
    
    
    
    
    /**
   	 * 返还红包未领取金额
   	 * @param giveRedEnvelope 发红包
   	 */
    public BigDecimal refundAmount(GiveRedEnvelope giveRedEnvelope){
    	//已领取总金额
    	BigDecimal receiveTotalAmount = new BigDecimal("0");
    	
    	
    	if(giveRedEnvelope.getType().equals(20)){//20.公共随机红包(随机金额)
    		List<ReceiveRedEnvelope> receiveRedEnvelopeList = this.dateConvertReceiveRedEnvelope(giveRedEnvelope.getDistributionAmountGroup(),giveRedEnvelope.getGrabRedEnvelopeUserIdGroup());
    		
    		for(ReceiveRedEnvelope receiveRedEnvelope: receiveRedEnvelopeList){
    			if(receiveRedEnvelope.getReceiveUserId() != null && receiveRedEnvelope.getReceiveUserId() >0L){
    				receiveTotalAmount = receiveTotalAmount.add(receiveRedEnvelope.getAmount());
    				
    			}
    		}
    	}else if(giveRedEnvelope.getType().equals(30)){//30.公共定额红包(固定金额)
    		//抢到红包的用户Id组
    		String _grabRedEnvelopeUserIdGroup = StringUtils.substringBeforeLast(giveRedEnvelope.getGrabRedEnvelopeUserIdGroup(), ",");//删除最后一个逗号 从右往左截取到相等的字符,保留左边的
    		List<Long> grabRedEnvelopeUserIdGroupList = jsonComponent.toGenericObject(_grabRedEnvelopeUserIdGroup+"]", new TypeReference< List<Long> >(){});
    		if(grabRedEnvelopeUserIdGroupList != null && grabRedEnvelopeUserIdGroupList.size() >0){
    			
    			receiveTotalAmount = giveRedEnvelope.getSingleAmount().multiply(new BigDecimal(String.valueOf(grabRedEnvelopeUserIdGroupList.size())));
    			
			}
    	}
    	
    	//待返还总金额
    	BigDecimal refundTotalAmount = giveRedEnvelope.getTotalAmount().subtract(receiveTotalAmount);
    	if(refundTotalAmount.compareTo(new BigDecimal("0")) <0){
    		return new BigDecimal("0");
    	}else{
    		return refundTotalAmount;
    	}
    	
    }
    

    
}
