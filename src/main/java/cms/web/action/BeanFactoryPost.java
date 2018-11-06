package cms.web.action;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.TypedStringValue;

import cms.utils.CreateBean;
import cms.utils.ModifyClass;

/**
 * 后置处理器，可以修改bean的配置信息
 *
 */
public class BeanFactoryPost implements BeanFactoryPostProcessor{

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
			throws BeansException {

		//动态修改类
		ModifyClass.escapeXmlELResolver_getValue();
				
		
		BeanDefinition pointLog_bd = beanFactory.getBeanDefinition("pointLogConfig");
		if(pointLog_bd != null){
			MutablePropertyValues mutablePropertyValues = pointLog_bd.getPropertyValues();
			TypedStringValue typedStringValue = (TypedStringValue)mutablePropertyValues.getPropertyValue("tableQuantity").getValue();

			//积分日志分表数量
			Integer tableQuantity = Integer.parseInt(typedStringValue.getValue());
			if(tableQuantity >1){
				for(int i =1; i<tableQuantity; i++){
					
					//初始化积分日志bean
					CreateBean.createPointLogBean(i);
				}	
			}
		}
		
		
		BeanDefinition userLoginLog_bd = beanFactory.getBeanDefinition("userLoginLogConfig");
		if(userLoginLog_bd != null){
			MutablePropertyValues mutablePropertyValues = userLoginLog_bd.getPropertyValues();
			TypedStringValue typedStringValue = (TypedStringValue)mutablePropertyValues.getPropertyValue("tableQuantity").getValue();

			//用户登录日志分表数量
			Integer tableQuantity = Integer.parseInt(typedStringValue.getValue());
			if(tableQuantity >1){
				for(int i =1; i<tableQuantity; i++){
					
					//初始化登录日志bean
					CreateBean.createUserLoginLogBean(i);
				}	
			}
		}
		
		BeanDefinition staffLoginLog_bd = beanFactory.getBeanDefinition("staffLoginLogConfig");
		if(staffLoginLog_bd != null){
			MutablePropertyValues mutablePropertyValues = staffLoginLog_bd.getPropertyValues();
			TypedStringValue typedStringValue = (TypedStringValue)mutablePropertyValues.getPropertyValue("tableQuantity").getValue();

			//员工登录日志分表数量
			Integer tableQuantity = Integer.parseInt(typedStringValue.getValue());
			if(tableQuantity >1){
				for(int i =1; i<tableQuantity; i++){
					
					//初始化登录日志bean
					CreateBean.createStaffLoginLogBean(i);
				}	
			}
		}
		
		BeanDefinition privateMessage_bd = beanFactory.getBeanDefinition("privateMessageConfig");
		if(privateMessage_bd != null){
			MutablePropertyValues mutablePropertyValues = privateMessage_bd.getPropertyValues();
			TypedStringValue typedStringValue = (TypedStringValue)mutablePropertyValues.getPropertyValue("tableQuantity").getValue();

			//私信分表数量
			Integer tableQuantity = Integer.parseInt(typedStringValue.getValue());
			if(tableQuantity >1){
				for(int i =1; i<tableQuantity; i++){
					
					//初始化私信bean
					CreateBean.createPrivateMessageBean(i);
				}	
			}
		}
		
		BeanDefinition subscriptionSystemNotify_bd = beanFactory.getBeanDefinition("subscriptionSystemNotifyConfig");
		if(subscriptionSystemNotify_bd != null){
			MutablePropertyValues mutablePropertyValues = subscriptionSystemNotify_bd.getPropertyValues();
			TypedStringValue typedStringValue = (TypedStringValue)mutablePropertyValues.getPropertyValue("tableQuantity").getValue();

			//订阅系统通知分表数量
			Integer tableQuantity = Integer.parseInt(typedStringValue.getValue());
			if(tableQuantity >1){
				for(int i =1; i<tableQuantity; i++){
					
					//初始化订阅系统通知bean
					CreateBean.createSubscriptionSystemNotifyBean(i);
				}	
			}
		}
		
		BeanDefinition remind_bd = beanFactory.getBeanDefinition("remindConfig");
		if(remind_bd != null){
			MutablePropertyValues mutablePropertyValues = remind_bd.getPropertyValues();
			TypedStringValue typedStringValue = (TypedStringValue)mutablePropertyValues.getPropertyValue("tableQuantity").getValue();

			//提醒分表数量
			Integer tableQuantity = Integer.parseInt(typedStringValue.getValue());
			if(tableQuantity >1){
				for(int i =1; i<tableQuantity; i++){
					
					//初始化提醒bean
					CreateBean.createRemindBean(i);
				}	
			}
		}
	}
}
