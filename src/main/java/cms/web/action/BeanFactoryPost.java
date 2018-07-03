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
			MutablePropertyValues pointLog_mutablePropertyValues = pointLog_bd.getPropertyValues();
			TypedStringValue pointLog_typedStringValue = (TypedStringValue)pointLog_mutablePropertyValues.getPropertyValue("tableQuantity").getValue();

			//积分日志分表数量
			Integer pointLog_tableQuantity = Integer.parseInt(pointLog_typedStringValue.getValue());
			if(pointLog_tableQuantity >1){
				for(int i =1; i<pointLog_tableQuantity; i++){
					
					//初始化积分日志bean
					CreateBean.createPointLogBean(i);
				}	
			}
		}
		
		
		BeanDefinition userLoginLog_bd = beanFactory.getBeanDefinition("userLoginLogConfig");
		if(userLoginLog_bd != null){
			MutablePropertyValues userLoginLog_mutablePropertyValues = userLoginLog_bd.getPropertyValues();
			TypedStringValue userLoginLog_typedStringValue = (TypedStringValue)userLoginLog_mutablePropertyValues.getPropertyValue("tableQuantity").getValue();

			//用户登录日志分表数量
			Integer userLoginLog_tableQuantity = Integer.parseInt(userLoginLog_typedStringValue.getValue());
			if(userLoginLog_tableQuantity >1){
				for(int i =1; i<userLoginLog_tableQuantity; i++){
					
					//初始化登录日志bean
					CreateBean.createUserLoginLogBean(i);
				}	
			}
		}
		
		BeanDefinition staffLoginLog_bd = beanFactory.getBeanDefinition("staffLoginLogConfig");
		if(staffLoginLog_bd != null){
			MutablePropertyValues staffLoginLog_mutablePropertyValues = staffLoginLog_bd.getPropertyValues();
			TypedStringValue staffLoginLog_typedStringValue = (TypedStringValue)staffLoginLog_mutablePropertyValues.getPropertyValue("tableQuantity").getValue();

			//员工登录日志分表数量
			Integer staffLoginLog_tableQuantity = Integer.parseInt(staffLoginLog_typedStringValue.getValue());
			if(staffLoginLog_tableQuantity >1){
				for(int i =1; i<staffLoginLog_tableQuantity; i++){
					
					//初始化登录日志bean
					CreateBean.createStaffLoginLogBean(i);
				}	
			}
		}
		
	}
}
