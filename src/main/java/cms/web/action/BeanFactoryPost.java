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
		
		BeanDefinition favorites_bd = beanFactory.getBeanDefinition("favoritesConfig");
		if(favorites_bd != null){
			MutablePropertyValues mutablePropertyValues = favorites_bd.getPropertyValues();
			TypedStringValue typedStringValue = (TypedStringValue)mutablePropertyValues.getPropertyValue("tableQuantity").getValue();

			//收藏夹分表数量
			Integer tableQuantity = Integer.parseInt(typedStringValue.getValue());
			if(tableQuantity >1){
				for(int i =1; i<tableQuantity; i++){
					
					//初始化收藏夹bean
					CreateBean.createFavoritesBean(i);
				}	
			}
		}
		
		BeanDefinition topicFavorite_bd = beanFactory.getBeanDefinition("topicFavoriteConfig");
		if(topicFavorite_bd != null){
			MutablePropertyValues mutablePropertyValues = topicFavorite_bd.getPropertyValues();
			TypedStringValue typedStringValue = (TypedStringValue)mutablePropertyValues.getPropertyValue("tableQuantity").getValue();

			//话题收藏分表数量
			Integer tableQuantity = Integer.parseInt(typedStringValue.getValue());
			if(tableQuantity >1){
				for(int i =1; i<tableQuantity; i++){
					
					//初始化话题收藏bean
					CreateBean.createTopicFavoriteBean(i);
				}	
			}
		}
		
		BeanDefinition topicUnhide_bd = beanFactory.getBeanDefinition("topicUnhideConfig");
		if(topicUnhide_bd != null){
			MutablePropertyValues mutablePropertyValues = topicUnhide_bd.getPropertyValues();
			TypedStringValue typedStringValue = (TypedStringValue)mutablePropertyValues.getPropertyValue("tableQuantity").getValue();

			//话题取消隐藏分表数量
			Integer tableQuantity = Integer.parseInt(typedStringValue.getValue());
			if(tableQuantity >1){
				for(int i =1; i<tableQuantity; i++){
					
					//初始化话题取消隐藏bean
					CreateBean.createTopicUnhideBean(i);
				}	
			}
		}
		
		BeanDefinition userDynamic_bd = beanFactory.getBeanDefinition("userDynamicConfig");
		if(userDynamic_bd != null){
			MutablePropertyValues mutablePropertyValues = userDynamic_bd.getPropertyValues();
			TypedStringValue typedStringValue = (TypedStringValue)mutablePropertyValues.getPropertyValue("tableQuantity").getValue();

			//用户动态分表数量
			Integer tableQuantity = Integer.parseInt(typedStringValue.getValue());
			if(tableQuantity >1){
				for(int i =1; i<tableQuantity; i++){
					
					//初始化用户动态bean
					CreateBean.createUserDynamicBean(i);
				}	
			}
		}
		
		BeanDefinition like_bd = beanFactory.getBeanDefinition("likeConfig");
		if(like_bd != null){
			MutablePropertyValues mutablePropertyValues = like_bd.getPropertyValues();
			TypedStringValue typedStringValue = (TypedStringValue)mutablePropertyValues.getPropertyValue("tableQuantity").getValue();

			//点赞分表数量
			Integer tableQuantity = Integer.parseInt(typedStringValue.getValue());
			if(tableQuantity >1){
				for(int i =1; i<tableQuantity; i++){
					
					//初始化点赞bean
					CreateBean.createLikeBean(i);
				}	
			}
		}
		
		BeanDefinition topicLike_bd = beanFactory.getBeanDefinition("topicLikeConfig");
		if(topicLike_bd != null){
			MutablePropertyValues mutablePropertyValues = topicLike_bd.getPropertyValues();
			TypedStringValue typedStringValue = (TypedStringValue)mutablePropertyValues.getPropertyValue("tableQuantity").getValue();

			//话题点赞分表数量
			Integer tableQuantity = Integer.parseInt(typedStringValue.getValue());
			if(tableQuantity >1){
				for(int i =1; i<tableQuantity; i++){
					
					//初始化话题点赞bean
					CreateBean.createTopicLikeBean(i);
				}	
			}
		}
		
		BeanDefinition follow_bd = beanFactory.getBeanDefinition("followConfig");
		if(follow_bd != null){
			MutablePropertyValues mutablePropertyValues = follow_bd.getPropertyValues();
			TypedStringValue typedStringValue = (TypedStringValue)mutablePropertyValues.getPropertyValue("tableQuantity").getValue();

			//关注分表数量
			Integer tableQuantity = Integer.parseInt(typedStringValue.getValue());
			if(tableQuantity >1){
				for(int i =1; i<tableQuantity; i++){
					
					//初始化关注bean
					CreateBean.createFollowBean(i);
				}	
			}
		}
		
		BeanDefinition follower_bd = beanFactory.getBeanDefinition("followerConfig");
		if(follower_bd != null){
			MutablePropertyValues mutablePropertyValues = follower_bd.getPropertyValues();
			TypedStringValue typedStringValue = (TypedStringValue)mutablePropertyValues.getPropertyValue("tableQuantity").getValue();

			//粉丝分表数量
			Integer tableQuantity = Integer.parseInt(typedStringValue.getValue());
			if(tableQuantity >1){
				for(int i =1; i<tableQuantity; i++){
					
					//初始化粉丝bean
					CreateBean.createFollowerBean(i);
				}	
			}
		}
		
		BeanDefinition paymentLog_bd = beanFactory.getBeanDefinition("paymentLogConfig");
		if(paymentLog_bd != null){
			MutablePropertyValues mutablePropertyValues = paymentLog_bd.getPropertyValues();
			TypedStringValue typedStringValue = (TypedStringValue)mutablePropertyValues.getPropertyValue("tableQuantity").getValue();

			//支付日志分表数量
			Integer tableQuantity = Integer.parseInt(typedStringValue.getValue());
			if(tableQuantity >1){
				for(int i =1; i<tableQuantity; i++){
					
					//初始化支付日志bean
					CreateBean.createPaymentLogBean(i);
				}	
			}
		}
		
		
		BeanDefinition questionFavorite_bd = beanFactory.getBeanDefinition("questionFavoriteConfig");
		if(questionFavorite_bd != null){
			MutablePropertyValues mutablePropertyValues = questionFavorite_bd.getPropertyValues();
			TypedStringValue typedStringValue = (TypedStringValue)mutablePropertyValues.getPropertyValue("tableQuantity").getValue();

			//问题收藏分表数量
			Integer tableQuantity = Integer.parseInt(typedStringValue.getValue());
			if(tableQuantity >1){
				for(int i =1; i<tableQuantity; i++){
					
					//初始化问题收藏bean
					CreateBean.createQuestionFavoriteBean(i);
				}	
			}
		}
		BeanDefinition receiveRedEnvelope_bd = beanFactory.getBeanDefinition("receiveRedEnvelopeConfig");
		if(receiveRedEnvelope_bd != null){
			MutablePropertyValues mutablePropertyValues = receiveRedEnvelope_bd.getPropertyValues();
			TypedStringValue typedStringValue = (TypedStringValue)mutablePropertyValues.getPropertyValue("tableQuantity").getValue();

			//收红包分表数量
			Integer tableQuantity = Integer.parseInt(typedStringValue.getValue());
			if(tableQuantity >1){
				for(int i =1; i<tableQuantity; i++){
					
					//初始化收红包bean
					CreateBean.createReceiveRedEnvelopeBean(i);
				}	
			}
		}
		BeanDefinition membershipCardGiftItem_bd = beanFactory.getBeanDefinition("membershipCardGiftItemConfig");
		if(membershipCardGiftItem_bd != null){
			MutablePropertyValues mutablePropertyValues = membershipCardGiftItem_bd.getPropertyValues();
			TypedStringValue typedStringValue = (TypedStringValue)mutablePropertyValues.getPropertyValue("tableQuantity").getValue();

			//会员卡赠送项分表数量
			Integer tableQuantity = Integer.parseInt(typedStringValue.getValue());
			if(tableQuantity >1){
				for(int i =1; i<tableQuantity; i++){
					
					//初始化会员卡赠送项bean
					CreateBean.createMembershipCardGiftItem(i);
				}	
			}
		}
	}
}
