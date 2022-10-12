package cms.web.action.redEnvelope;



import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import cms.bean.QueryResult;
import cms.bean.payment.PaymentLog;
import cms.bean.redEnvelope.GiveRedEnvelope;
import cms.bean.redEnvelope.ReceiveRedEnvelope;
import cms.bean.user.User;
import cms.service.redEnvelope.RedEnvelopeService;
import cms.utils.JsonUtils;
import cms.utils.Verification;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.payment.PaymentManage;
import cms.web.action.user.UserManage;
import net.sf.cglib.beans.BeanCopier;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * 红包管理
 *
 */
@Component("redEnvelopeManage")
public class RedEnvelopeManage {
	private static final Logger logger = LogManager.getLogger(RedEnvelopeManage.class);
	
    @Resource ReceiveRedEnvelopeConfig receiveRedEnvelopeConfig;
    @Resource RedEnvelopeService redEnvelopeService;
    @Resource RedEnvelopeManage redEnvelopeManage;
    @Resource PaymentManage paymentManage;
    @Resource UserManage userManage;
    @Resource FileManage fileManage;
    
	/**
	 * 取得收红包Id的用户Id(后N位)
	 * @param receiveRedEnvelopeId 收红包Id
	 * @return
	 */
    public int getAfterUserId(String receiveRedEnvelopeId){
    	String[] idGroup = receiveRedEnvelopeId.split("_");
    	Long userId = Long.parseLong(idGroup[1]);
    	
    	//选取得后N位用户Id
    	String after_userId = String.format("%04d", Math.abs(userId)%10000);
    	return Integer.parseInt(after_userId);
    } 
    
    /**
     * 生成收红包Id
     * 收红包Id由发红包Id-领红包的用户Id组成
     * @param giveRedEnvelopeId 发红包Id
     * @param userId 用户Id
     * @return
     */
    public String createReceiveRedEnvelopeId(String giveRedEnvelopeId,Long userId){
    	return giveRedEnvelopeId+"_"+userId;
    }

    /**
     * 校验收红包Id
     * @param receiveRedEnvelopeId 收红包Id
     * @return
     */
    public boolean verificationReceiveRedEnvelopeId(String receiveRedEnvelopeId){
    	if(receiveRedEnvelopeId != null && !"".equals(receiveRedEnvelopeId.trim())){
    		String[] idGroup = receiveRedEnvelopeId.split("_");
    		for(String id : idGroup){
    			boolean verification = Verification.isPositiveIntegerZero(id);//数字
    			if(!verification){
    				return false;
    			}
    			return true;
    		}	
			
    	}
    	return false;
    }
    
    
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
				c = Class.forName("cms.bean.redEnvelope.ReceiveRedEnvelope_"+tableNumber);
				Object object = c.newInstance();
				
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
            	amountList.add(remainMoney.setScale(2, BigDecimal.ROUND_DOWN));//在小数点后两位处截断
            	break;
            }

            BigDecimal random = BigDecimal.valueOf(Math.random());
            BigDecimal min   = BigDecimal.valueOf(0.01);//最小的红包金额

            BigDecimal halfRemainSize = BigDecimal.valueOf(remainSize).divide(new BigDecimal(2), BigDecimal.ROUND_UP);
            BigDecimal max1 = remainMoney.divide(halfRemainSize, BigDecimal.ROUND_DOWN);
            BigDecimal minRemainAmount = min.multiply(BigDecimal.valueOf(remainSize - 1)).setScale(2, BigDecimal.ROUND_DOWN);
            BigDecimal max2 = remainMoney.subtract(minRemainAmount);
            BigDecimal max = (max1.compareTo(max2) < 0) ? max1 : max2;

            BigDecimal money = random.multiply(max).setScale(2, BigDecimal.ROUND_DOWN);
            money = money.compareTo(min) < 0 ? min: money;

            remainSize--;
            remainMoney = remainMoney.subtract(money).setScale(2, BigDecimal.ROUND_DOWN);
            amountList.add(money);
        }
    	
    	
    	
    	return amountList;
    }
    
   /**
    * 拆红包
    * @param receiveRedEnvelope 收红包Id
    * @param amount 红包金额
    * @param receiveTime 收取时间
    */
    public Integer unwrapRedEnvelope(ReceiveRedEnvelope receiveRedEnvelope,BigDecimal amount,Long userId,String userName){
    	PaymentLog paymentLog = new PaymentLog();
		paymentLog.setPaymentRunningNumber(paymentManage.createRunningNumber(userId));//支付流水号
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
		Object paymentLogObject = paymentManage.createPaymentLogObject(paymentLog);

		Integer j = redEnvelopeService.unwrapRedEnvelope(receiveRedEnvelope.getId(),amount,receiveRedEnvelope.getVersion(),userName,paymentLogObject);
		
		
    	//删除缓存
    	redEnvelopeManage.delete_cache_findByReceiveRedEnvelopeId(receiveRedEnvelope.getId());
    	
    	return j;
    }
    
    
   	/**
   	 * 查询用户领取到的红包金额
   	 * @param giveRedEnvelope 发红包
   	 * @param userId 用户Id
   	 */
    public BigDecimal queryReceiveRedEnvelopeAmount(GiveRedEnvelope giveRedEnvelope,Long userId){
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
    		List<Long> grabRedEnvelopeUserIdGroupList = JsonUtils.toGenericObject(_grabRedEnvelopeUserIdGroup+"]", new TypeReference< List<Long> >(){});
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
		List<BigDecimal> distributionAmountGroupList = JsonUtils.toGenericObject(distributionAmountGroup, new TypeReference< List<BigDecimal> >(){});
		
		//抢到红包的用户Id组
		String _grabRedEnvelopeUserIdGroup = StringUtils.substringBeforeLast(grabRedEnvelopeUserIdGroup, ",");//删除最后一个逗号 从右往左截取到相等的字符,保留左边的
		List<Long> grabRedEnvelopeUserIdGroupList = JsonUtils.toGenericObject(_grabRedEnvelopeUserIdGroup+"]", new TypeReference< List<Long> >(){});
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
    public QueryResult<ReceiveRedEnvelope> queryReceiveRedEnvelopeByCondition(GiveRedEnvelope giveRedEnvelope,boolean includeNotReceive,int firstIndex, int maxResult,boolean sort,boolean isCancelAccountInfo){
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
    		List<Long> grabRedEnvelopeUserIdGroupList = JsonUtils.toGenericObject(_grabRedEnvelopeUserIdGroup+"]", new TypeReference< List<Long> >(){});
    		if(grabRedEnvelopeUserIdGroupList != null && grabRedEnvelopeUserIdGroupList.size() >0){
				for(Long grabRedEnvelopeUserId : grabRedEnvelopeUserIdGroupList){
					ReceiveRedEnvelope receiveRedEnvelope = new ReceiveRedEnvelope();
					receiveRedEnvelope.setAmount(giveRedEnvelope.getSingleAmount());
					receiveRedEnvelope.setReceiveUserId(grabRedEnvelopeUserId);
					receiveRedEnvelopeList.add(receiveRedEnvelope);
				}
			}
    	}
    	
    	if(sort == false){//逆序排列
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
    			receiveRedEnvelope.setId(this.createReceiveRedEnvelopeId(giveRedEnvelope.getId(),receiveRedEnvelope.getReceiveUserId()));
    			User user = userManage.query_cache_findUserById(receiveRedEnvelope.getReceiveUserId());
    			
    			if(user != null){
    				receiveRedEnvelope.setReceiveUserName(user.getUserName());
    				if(isCancelAccountInfo || user.getCancelAccountTime().equals(-1L)){
    					receiveRedEnvelope.setReceiveAccount(user.getAccount());
            			receiveRedEnvelope.setReceiveNickname(user.getNickname());
            			
            			if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
            				receiveRedEnvelope.setReceiveAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
            				receiveRedEnvelope.setReceiveAvatarName(user.getAvatarName());
            			}
    				}
        			ReceiveRedEnvelope valid_receiveRedEnvelope = redEnvelopeService.findByReceiveRedEnvelopeId(this.createReceiveRedEnvelopeId(giveRedEnvelope.getId(),user.getId()));
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
    		List<Long> grabRedEnvelopeUserIdGroupList = JsonUtils.toGenericObject(_grabRedEnvelopeUserIdGroup+"]", new TypeReference< List<Long> >(){});
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
    
    /**
	 * 查询缓存 根据Id查询发红包
	 * @param giveRedEnvelopeId 发红包Id
	 * @return
	 */
	@Cacheable(value="redEnvelopeManage_cache_findById",key="#giveRedEnvelopeId")
	public GiveRedEnvelope query_cache_findById(String giveRedEnvelopeId){
		return redEnvelopeService.findById(giveRedEnvelopeId);
	}
	/**
	 * 删除缓存 根据Id查询发红包
	 * @param giveRedEnvelopeId 发红包Id
	 * @return
	 */
	@CacheEvict(value="redEnvelopeManage_cache_findById",key="#giveRedEnvelopeId")
	public void delete_cache_findById(String giveRedEnvelopeId){
	}
    
	/**
	 * 查询缓存 根据Id查询收红包
	 * @param receiveRedEnvelopeId 收红包Id
	 * @return
	 */
	@Cacheable(value="redEnvelopeManage_cache_findByReceiveRedEnvelopeId",key="#receiveRedEnvelopeId")
	public ReceiveRedEnvelope query_cache_findByReceiveRedEnvelopeId(String receiveRedEnvelopeId){
		return redEnvelopeService.findByReceiveRedEnvelopeId(receiveRedEnvelopeId);
	}
	/**
	 * 删除缓存 根据Id查询收红包
	 * @param giveRedEnvelopeId 收红包Id
	 * @return
	 */
	@CacheEvict(value="redEnvelopeManage_cache_findByReceiveRedEnvelopeId",key="#receiveRedEnvelopeId")
	public void delete_cache_findByReceiveRedEnvelopeId(String receiveRedEnvelopeId){
	}
	
	
    
}
