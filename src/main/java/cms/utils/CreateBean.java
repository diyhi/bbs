package cms.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;

/**
 * 创建bean工具
 *
 */
public class CreateBean {
	private static final Logger logger = LogManager.getLogger(CreateBean.class);
	

	/**
	 * 创建积分日志bean
	 * @param number 表号
	 */
	public static void createPointLogBean(Integer tableNumber){
		
		//ClassPool：CtClass对象的容器   
	    ClassPool pool = ClassPool.getDefault(); 
	//    ClassClassPath classPath = new ClassClassPath(this.getClass());
	    ClassClassPath classPath = new ClassClassPath(CreateBean.class);
	    pool.insertClassPath(classPath);

	      
	    //通过ClassPool生成一个public新类HistoryOrder.java   
	    CtClass ctClass = pool.makeClass("cms.bean.user.PointLog_"+tableNumber); 

        try {
        	// 父类
        	ctClass.setSuperclass(pool.get("cms.bean.user.PointLogEntity"));
        	// 添加接口
			ctClass.addInterface(pool.get("java.io.Serializable"));
			
			// 添加属性   
			 ctClass.addField(CtField.make("private static final long serialVersionUID = 3001000000000000"+ tableNumber+"L;", ctClass));

			//写入注解(Annotation) 
			ClassFile cf = ctClass.getClassFile();  
	        ConstPool cp = cf.getConstPool();
 
	        //@Table(name="historyorder_0",indexes = {@Index(name="historyorder_idx", columnList="state,visible")})
	        AnnotationsAttribute attr = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);
	        
	        //@Entity注解
	        Annotation entity_a = new Annotation("javax.persistence.Entity", cp);
	  	  	attr.addAnnotation(entity_a);
	  	  	
	        //@Table注解
	        Annotation a = new Annotation("javax.persistence.Table", cp);
	        a.addMemberValue("name", new StringMemberValue("pointlog_"+tableNumber, cp));
	       
	        Annotation index_a = new Annotation("javax.persistence.Index", cp);
	        index_a.addMemberValue("name", new StringMemberValue("pointlog_idx", cp));
	        index_a.addMemberValue("columnList", new StringMemberValue("userName,times", cp));
	        AnnotationMemberValue annotationMemberValue = new AnnotationMemberValue(cp);
	        annotationMemberValue.setValue(index_a);

	        MemberValue[] vals = new MemberValue[]{annotationMemberValue};

	        ArrayMemberValue arrayMemberValue=new ArrayMemberValue(cp);//数组类型
	        arrayMemberValue.setValue(vals);
	      
	        a.addMemberValue("indexes", arrayMemberValue);//数组类型
	        attr.addAnnotation(a);
	        cf.addAttribute(attr);
	        cf.setVersionToJava5();

	        
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("创建积分日志bean",e);
	        }
		}   catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("创建积分日志bean",e);
	        }
		}   
       
        
      //把生成的class文件写入文件   
        byte[] byteArr;
        FileOutputStream fos = null;
		try {
			File file = new File(PathUtil.path()+File.separator+"WEB-INF"+File.separator+"classes"+File.separator+"cms"+File.separator+"bean"+File.separator+"user"+File.separator+"PointLog_"+tableNumber+".class");
			
			if(!file.exists()){
				byteArr = ctClass.toBytecode();
				//	fos = new FileOutputStream(new File("C://HistoryOrder_5.class"));
				fos = new FileOutputStream(file);
				fos.write(byteArr);
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("创建积分日志bean",e);
	        }
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("创建积分日志bean",e);
	        }
		} finally {
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
			            logger.error("创建积分日志bean",e);
			        }
				}  
			}
		}


	}
	
	
	/**
	 * 创建用户登录日志bean
	 * @param number 表号
	 */
	public static void createUserLoginLogBean(Integer tableNumber){
		
		//ClassPool：CtClass对象的容器   
	    ClassPool pool = ClassPool.getDefault(); 
	//    ClassClassPath classPath = new ClassClassPath(this.getClass());
	    ClassClassPath classPath = new ClassClassPath(CreateBean.class);
	    pool.insertClassPath(classPath);

	      
	    //通过ClassPool生成一个public新类UserLoginLog_?.java   
	    CtClass ctClass = pool.makeClass("cms.bean.user.UserLoginLog_"+tableNumber); 

        try {
        	// 父类
        	ctClass.setSuperclass(pool.get("cms.bean.user.UserLoginLogEntity"));
        	// 添加接口
			ctClass.addInterface(pool.get("java.io.Serializable"));
			
			// 添加属性   
			 ctClass.addField(CtField.make("private static final long serialVersionUID = 8001000000000000"+ tableNumber+"L;", ctClass));

			//写入注解(Annotation) 
			ClassFile cf = ctClass.getClassFile();  
	        ConstPool cp = cf.getConstPool();
 
	        //@Table(name="historyorder_0",indexes = {@Index(name="historyorder_idx", columnList="state,visible")})
	        AnnotationsAttribute attr = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);
	        
	        //@Entity注解
	        Annotation entity_a = new Annotation("javax.persistence.Entity", cp);
	  	  	attr.addAnnotation(entity_a);
	  	  	
	        //@Table注解
	        Annotation a = new Annotation("javax.persistence.Table", cp);
	        a.addMemberValue("name", new StringMemberValue("userloginlog_"+tableNumber, cp));
	       
	        Annotation index_a = new Annotation("javax.persistence.Index", cp);
	        index_a.addMemberValue("name", new StringMemberValue("userLoginLog_idx", cp));
	        index_a.addMemberValue("columnList", new StringMemberValue("userId,logonTime", cp));
	        AnnotationMemberValue annotationMemberValue = new AnnotationMemberValue(cp);
	        annotationMemberValue.setValue(index_a);

	        
	       
	        
	        MemberValue[] vals = new MemberValue[]{annotationMemberValue};

	        ArrayMemberValue arrayMemberValue=new ArrayMemberValue(cp);//数组类型
	        arrayMemberValue.setValue(vals);
	      
	        a.addMemberValue("indexes", arrayMemberValue);//数组类型
	        attr.addAnnotation(a);
	        cf.addAttribute(attr);
	        cf.setVersionToJava5();

	        
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("创建用户登录日志bean",e);
	        }
		}   catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("创建用户登录日志bean",e);
	        }
		}   
       
        
      //把生成的class文件写入文件   
        byte[] byteArr;
        FileOutputStream fos = null;
		try {
			File file = new File(PathUtil.path()+File.separator+"WEB-INF"+File.separator+"classes"+File.separator+"cms"+File.separator+"bean"+File.separator+"user"+File.separator+"UserLoginLog_"+tableNumber+".class");
			
			if(!file.exists()){
				byteArr = ctClass.toBytecode();
				//	fos = new FileOutputStream(new File("C://HistoryOrder_5.class"));
				fos = new FileOutputStream(file);
				fos.write(byteArr);
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("创建用户登录日志bean",e);
	        }
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("创建用户登录日志bean",e);
	        }
		} finally {
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
			            logger.error("创建用户登录日志bean",e);
			        }
				}  
			}
		} 


	}
	
	
	/**
	 * 创建员工登录日志bean
	 * @param number 表号
	 */
	public static void createStaffLoginLogBean(Integer tableNumber){
		
		//ClassPool：CtClass对象的容器   
	    ClassPool pool = ClassPool.getDefault(); 
	//    ClassClassPath classPath = new ClassClassPath(this.getClass());
	    ClassClassPath classPath = new ClassClassPath(CreateBean.class);
	    pool.insertClassPath(classPath);

	      
	    //通过ClassPool生成一个public新类UserLoginLog_?.java   
	    CtClass ctClass = pool.makeClass("cms.bean.staff.StaffLoginLog_"+tableNumber); 

        try {
        	// 父类
        	ctClass.setSuperclass(pool.get("cms.bean.staff.StaffLoginLogEntity"));
        	// 添加接口
			ctClass.addInterface(pool.get("java.io.Serializable"));
			
			// 添加属性   
			 ctClass.addField(CtField.make("private static final long serialVersionUID = 9001000000000000"+ tableNumber+"L;", ctClass));

			//写入注解(Annotation) 
			ClassFile cf = ctClass.getClassFile();  
	        ConstPool cp = cf.getConstPool();
 
	        //@Table(name="historyorder_0",indexes = {@Index(name="historyorder_idx", columnList="state,visible")})
	        AnnotationsAttribute attr = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);
	        
	        //@Entity注解
	        Annotation entity_a = new Annotation("javax.persistence.Entity", cp);
	  	  	attr.addAnnotation(entity_a);
	  	  	
	        //@Table注解
	        Annotation a = new Annotation("javax.persistence.Table", cp);
	        a.addMemberValue("name", new StringMemberValue("staffloginlog_"+tableNumber, cp));
	       
	        Annotation index_a = new Annotation("javax.persistence.Index", cp);
	        index_a.addMemberValue("name", new StringMemberValue("staffLoginLog_idx", cp));
	        index_a.addMemberValue("columnList", new StringMemberValue("staffId,logonTime", cp));
	        AnnotationMemberValue annotationMemberValue = new AnnotationMemberValue(cp);
	        annotationMemberValue.setValue(index_a);

	        
	       
	        
	        MemberValue[] vals = new MemberValue[]{annotationMemberValue};

	        ArrayMemberValue arrayMemberValue=new ArrayMemberValue(cp);//数组类型
	        arrayMemberValue.setValue(vals);
	      
	        a.addMemberValue("indexes", arrayMemberValue);//数组类型
	        attr.addAnnotation(a);
	        cf.addAttribute(attr);
	        cf.setVersionToJava5();

	        
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
	//		e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("创建员工登录日志bean",e);
	        }
		}   catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("创建员工登录日志bean",e);
	        }
		}   
       
        
      //把生成的class文件写入文件   
        byte[] byteArr;
        FileOutputStream fos = null;
		try {
			File file = new File(PathUtil.path()+File.separator+"WEB-INF"+File.separator+"classes"+File.separator+"cms"+File.separator+"bean"+File.separator+"staff"+File.separator+"StaffLoginLog_"+tableNumber+".class");
			
			if(!file.exists()){
				byteArr = ctClass.toBytecode();
				//	fos = new FileOutputStream(new File("C://HistoryOrder_5.class"));
				fos = new FileOutputStream(file);
				fos.write(byteArr);  
		         
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("创建员工登录日志bean",e);
	        }
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("创建员工登录日志bean",e);
	        }
		} finally {
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
			            logger.error("创建员工登录日志bean",e);
			        }
				}  
			}
		} 


	}
	
	
	/**
	 * 创建私信bean
	 * @param number 表号
	 */
	public static void createPrivateMessageBean(Integer tableNumber){
		
		//ClassPool：CtClass对象的容器   
	    ClassPool pool = ClassPool.getDefault(); 
	//    ClassClassPath classPath = new ClassClassPath(this.getClass());
	    ClassClassPath classPath = new ClassClassPath(CreateBean.class);
	    pool.insertClassPath(classPath);

	      
	    //通过ClassPool生成一个public新类HistoryOrder.java   
	    CtClass ctClass = pool.makeClass("cms.bean.message.PrivateMessage_"+tableNumber); 

        try {
        	// 父类
        	ctClass.setSuperclass(pool.get("cms.bean.message.PrivateMessageEntity"));
        	// 添加接口
			ctClass.addInterface(pool.get("java.io.Serializable"));
			
			// 添加属性   
			 ctClass.addField(CtField.make("private static final long serialVersionUID = 11000000000000000"+ tableNumber+"L;", ctClass));

			//写入注解(Annotation) 
			ClassFile cf = ctClass.getClassFile();  
	        ConstPool cp = cf.getConstPool();
 
	        //@Table(name="historyorder_0",indexes = {@Index(name="historyorder_idx", columnList="state,visible")})
	        AnnotationsAttribute attr = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);
	        
	        //@Entity注解
	        Annotation entity_a = new Annotation("javax.persistence.Entity", cp);
	  	  	attr.addAnnotation(entity_a);
	  	  	
	        //@Table注解
	        Annotation a = new Annotation("javax.persistence.Table", cp);
	        a.addMemberValue("name", new StringMemberValue("privatemessage_"+tableNumber, cp));
	       
	        Annotation index_a = new Annotation("javax.persistence.Index", cp);
	        index_a.addMemberValue("name", new StringMemberValue("privateMessage_1_idx", cp));
	        index_a.addMemberValue("columnList", new StringMemberValue("userId,status,sendTimeFormat", cp));
	        AnnotationMemberValue annotationMemberValue = new AnnotationMemberValue(cp);
	        annotationMemberValue.setValue(index_a);

	        
	        Annotation index_b = new Annotation("javax.persistence.Index", cp);
	        index_b.addMemberValue("name", new StringMemberValue("privateMessage_2_idx", cp));
	        index_b.addMemberValue("columnList", new StringMemberValue("userId,friendUserId,status,sendTimeFormat", cp));
	        AnnotationMemberValue annotationMemberValue_b = new AnnotationMemberValue(cp);
	        annotationMemberValue_b.setValue(index_b);
	        
	        MemberValue[] vals = new MemberValue[]{annotationMemberValue,annotationMemberValue_b};

	        ArrayMemberValue arrayMemberValue=new ArrayMemberValue(cp);//数组类型
	        arrayMemberValue.setValue(vals);
	      
	        a.addMemberValue("indexes", arrayMemberValue);//数组类型
	        attr.addAnnotation(a);
	        cf.addAttribute(attr);
	        cf.setVersionToJava5();

	        
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("创建私信bean",e);
	        }
		}   catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("创建私信bean",e);
	        }
		}   
       
        
      //把生成的class文件写入文件   
        byte[] byteArr;
        FileOutputStream fos = null;
		try {
			File file = new File(PathUtil.path()+File.separator+"WEB-INF"+File.separator+"classes"+File.separator+"cms"+File.separator+"bean"+File.separator+"message"+File.separator+"PrivateMessage_"+tableNumber+".class");
			
			if(!file.exists()){
				byteArr = ctClass.toBytecode();
				//	fos = new FileOutputStream(new File("C://HistoryOrder_5.class"));
				fos = new FileOutputStream(file);
				fos.write(byteArr); 
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("创建私信bean",e);
	        }
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("创建私信bean",e);
	        }
		} finally {
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
			            logger.error("创建私信bean",e);
			        }
				}  
			}
		} 


	}
	
	
	/**
	 * 创建订阅系统通知bean
	 * @param number 表号
	 */
	public static void createSubscriptionSystemNotifyBean(Integer tableNumber){
		
		//ClassPool：CtClass对象的容器   
	    ClassPool pool = ClassPool.getDefault(); 
	//    ClassClassPath classPath = new ClassClassPath(this.getClass());
	    ClassClassPath classPath = new ClassClassPath(CreateBean.class);
	    pool.insertClassPath(classPath);

	      
	    //通过ClassPool生成一个public新类HistoryOrder.java   
	    CtClass ctClass = pool.makeClass("cms.bean.message.SubscriptionSystemNotify_"+tableNumber); 

        try {
        	// 父类
        	ctClass.setSuperclass(pool.get("cms.bean.message.SubscriptionSystemNotifyEntity"));
        	// 添加接口
			ctClass.addInterface(pool.get("java.io.Serializable"));
			
			// 添加属性   
			 ctClass.addField(CtField.make("private static final long serialVersionUID = 12000000000000000"+ tableNumber+"L;", ctClass));

			//写入注解(Annotation) 
			ClassFile cf = ctClass.getClassFile();  
	        ConstPool cp = cf.getConstPool();
 
	        //@Table(name="historyorder_0",indexes = {@Index(name="historyorder_idx", columnList="state,visible")})
	        AnnotationsAttribute attr = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);
	        
	        //@Entity注解
	        Annotation entity_a = new Annotation("javax.persistence.Entity", cp);
	  	  	attr.addAnnotation(entity_a);
	  	  	
	        //@Table注解
	        Annotation a = new Annotation("javax.persistence.Table", cp);
	        a.addMemberValue("name", new StringMemberValue("subscriptionsystemnotify_"+tableNumber, cp));
	       
	        Annotation index_a = new Annotation("javax.persistence.Index", cp);
	        index_a.addMemberValue("name", new StringMemberValue("subscriptionSystemNotify_1_idx", cp));
	        index_a.addMemberValue("columnList", new StringMemberValue("systemNotifyId", cp));
	        AnnotationMemberValue annotationMemberValue = new AnnotationMemberValue(cp);
	        annotationMemberValue.setValue(index_a);

	
	        Annotation index_b = new Annotation("javax.persistence.Index", cp);
	        index_b.addMemberValue("name", new StringMemberValue("subscriptionSystemNotify_2_idx", cp));
	        index_b.addMemberValue("columnList", new StringMemberValue("userId,status,systemNotifyId", cp));
	        AnnotationMemberValue annotationMemberValue_b = new AnnotationMemberValue(cp);
	        annotationMemberValue_b.setValue(index_b);
	        
	        MemberValue[] vals = new MemberValue[]{annotationMemberValue,annotationMemberValue_b};

	        ArrayMemberValue arrayMemberValue=new ArrayMemberValue(cp);//数组类型
	        arrayMemberValue.setValue(vals);
	      
	        a.addMemberValue("indexes", arrayMemberValue);//数组类型
	        attr.addAnnotation(a);
	        cf.addAttribute(attr);
	        cf.setVersionToJava5();

	        
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("订阅系统通知bean",e);
	        }
		}   catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("订阅系统通知bean",e);
	        }
		}   
       
        
      //把生成的class文件写入文件   
        byte[] byteArr;
        FileOutputStream fos = null;
		try {
			File file = new File(PathUtil.path()+File.separator+"WEB-INF"+File.separator+"classes"+File.separator+"cms"+File.separator+"bean"+File.separator+"message"+File.separator+"SubscriptionSystemNotify_"+tableNumber+".class");
			
			if(!file.exists()){
				byteArr = ctClass.toBytecode();
				//	fos = new FileOutputStream(new File("C://HistoryOrder_5.class"));
				fos = new FileOutputStream(file);
				fos.write(byteArr); 
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("订阅系统通知bean",e);
	        }
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("订阅系统通知bean",e);
	        }
		} finally {
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
			            logger.error("订阅系统通知bean",e);
			        }
				}  
			}
		} 
	}
	
	/**
	 * 创建提醒bean
	 * @param number 表号
	 */
	public static void createRemindBean(Integer tableNumber){
		
		//ClassPool：CtClass对象的容器   
	    ClassPool pool = ClassPool.getDefault(); 
	//    ClassClassPath classPath = new ClassClassPath(this.getClass());
	    ClassClassPath classPath = new ClassClassPath(CreateBean.class);
	    pool.insertClassPath(classPath);

	      
	    //通过ClassPool生成一个public新类HistoryOrder.java   
	    CtClass ctClass = pool.makeClass("cms.bean.message.Remind_"+tableNumber); 

        try {
        	// 父类
        	ctClass.setSuperclass(pool.get("cms.bean.message.RemindEntity"));
        	// 添加接口
			ctClass.addInterface(pool.get("java.io.Serializable"));
			
			// 添加属性   
			 ctClass.addField(CtField.make("private static final long serialVersionUID = 13000000000000000"+ tableNumber+"L;", ctClass));

			//写入注解(Annotation) 
			ClassFile cf = ctClass.getClassFile();  
	        ConstPool cp = cf.getConstPool();
 
	        //@Table(name="historyorder_0",indexes = {@Index(name="historyorder_idx", columnList="state,visible")})
	        AnnotationsAttribute attr = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);
	        
	        //@Entity注解
	        Annotation entity_a = new Annotation("javax.persistence.Entity", cp);
	  	  	attr.addAnnotation(entity_a);
	  	  	
	        //@Table注解
	        Annotation a = new Annotation("javax.persistence.Table", cp);
	        a.addMemberValue("name", new StringMemberValue("remind_"+tableNumber, cp));
	       
	        Annotation index_a = new Annotation("javax.persistence.Index", cp);
	        index_a.addMemberValue("name", new StringMemberValue("remind_1_idx", cp));
	        index_a.addMemberValue("columnList", new StringMemberValue("receiverUserId,status,sendTimeFormat", cp));
	        AnnotationMemberValue annotationMemberValue = new AnnotationMemberValue(cp);
	        annotationMemberValue.setValue(index_a);

	        Annotation index_b = new Annotation("javax.persistence.Index", cp);
	        index_b.addMemberValue("name", new StringMemberValue("remind_2_idx", cp));
	        index_b.addMemberValue("columnList", new StringMemberValue("topicId", cp));
	        AnnotationMemberValue annotationMemberValue_b = new AnnotationMemberValue(cp);
	        annotationMemberValue_b.setValue(index_b);
	        
	        Annotation index_c = new Annotation("javax.persistence.Index", cp);
	        index_c.addMemberValue("name", new StringMemberValue("remind_3_idx", cp));
	        index_c.addMemberValue("columnList", new StringMemberValue("receiverUserId,typeCode,sendTimeFormat", cp));
	        AnnotationMemberValue annotationMemberValue_c = new AnnotationMemberValue(cp);
	        annotationMemberValue_c.setValue(index_c);
	        

	        MemberValue[] vals = new MemberValue[]{annotationMemberValue,annotationMemberValue_b,annotationMemberValue_c};

	        ArrayMemberValue arrayMemberValue=new ArrayMemberValue(cp);//数组类型
	        arrayMemberValue.setValue(vals);
	      
	        a.addMemberValue("indexes", arrayMemberValue);//数组类型
	        attr.addAnnotation(a);
	        cf.addAttribute(attr);
	        cf.setVersionToJava5();

	        
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("提醒bean",e);
	        }
		}   catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("提醒bean",e);
	        }
		}   
       
        
      //把生成的class文件写入文件   
        byte[] byteArr;
        FileOutputStream fos = null;
		try {
			File file = new File(PathUtil.path()+File.separator+"WEB-INF"+File.separator+"classes"+File.separator+"cms"+File.separator+"bean"+File.separator+"message"+File.separator+"Remind_"+tableNumber+".class");
			
			if(!file.exists()){
				byteArr = ctClass.toBytecode();
				//	fos = new FileOutputStream(new File("C://HistoryOrder_5.class"));
				fos = new FileOutputStream(file);
				fos.write(byteArr); 
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("提醒bean",e);
	        }
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("提醒bean",e);
	        }
		} finally {
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
			            logger.error("提醒bean",e);
			        }
				}  
			}
		} 
	}
	
	/**
	 * 创建收藏夹bean
	 * @param number 表号
	 */
	public static void createFavoritesBean(Integer tableNumber){
		
		//ClassPool：CtClass对象的容器   
	    ClassPool pool = ClassPool.getDefault(); 
	//    ClassClassPath classPath = new ClassClassPath(this.getClass());
	    ClassClassPath classPath = new ClassClassPath(CreateBean.class);
	    pool.insertClassPath(classPath);

	      
	    //通过ClassPool生成一个public新类HistoryOrder.java   
	    CtClass ctClass = pool.makeClass("cms.bean.favorite.Favorites_"+tableNumber); 

        try {
        	// 父类
        	ctClass.setSuperclass(pool.get("cms.bean.favorite.FavoritesEntity"));
        	// 添加接口
			ctClass.addInterface(pool.get("java.io.Serializable"));
			
			// 添加属性   
			 ctClass.addField(CtField.make("private static final long serialVersionUID = 14000000000000000"+ tableNumber+"L;", ctClass));

			//写入注解(Annotation) 
			ClassFile cf = ctClass.getClassFile();  
	        ConstPool cp = cf.getConstPool();
 
	        //@Table(name="historyorder_0",indexes = {@Index(name="historyorder_idx", columnList="state,visible")})
	        AnnotationsAttribute attr = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);
	        
	        //@Entity注解
	        Annotation entity_a = new Annotation("javax.persistence.Entity", cp);
	  	  	attr.addAnnotation(entity_a);
	  	  	
	        //@Table注解
	        Annotation a = new Annotation("javax.persistence.Table", cp);
	        a.addMemberValue("name", new StringMemberValue("favorites_"+tableNumber, cp));
	       
	        Annotation index_a = new Annotation("javax.persistence.Index", cp);
	        index_a.addMemberValue("name", new StringMemberValue("favorites_1_idx", cp));
	        index_a.addMemberValue("columnList", new StringMemberValue("userName,addtime", cp));
	        AnnotationMemberValue annotationMemberValue = new AnnotationMemberValue(cp);
	        annotationMemberValue.setValue(index_a);

	      

	        MemberValue[] vals = new MemberValue[]{annotationMemberValue};

	        ArrayMemberValue arrayMemberValue=new ArrayMemberValue(cp);//数组类型
	        arrayMemberValue.setValue(vals);
	      
	        a.addMemberValue("indexes", arrayMemberValue);//数组类型
	        attr.addAnnotation(a);
	        cf.addAttribute(attr);
	        cf.setVersionToJava5();

	        
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("收藏夹bean",e);
	        }
		}   catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("收藏夹bean",e);
	        }
		}   
       
        
      //把生成的class文件写入文件   
        byte[] byteArr;
        FileOutputStream fos = null;
		try {
			File file = new File(PathUtil.path()+File.separator+"WEB-INF"+File.separator+"classes"+File.separator+"cms"+File.separator+"bean"+File.separator+"favorite"+File.separator+"Favorites_"+tableNumber+".class");
			
			if(!file.exists()){
				byteArr = ctClass.toBytecode();
				//	fos = new FileOutputStream(new File("C://HistoryOrder_5.class"));
				fos = new FileOutputStream(file);
				fos.write(byteArr); 
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("收藏夹bean",e);
	        }
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("收藏夹bean",e);
	        }
		} finally {
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
			            logger.error("收藏夹bean",e);
			        }
				}  
			}
		} 
	}
	
	/**
	 * 创建话题收藏bean
	 * @param number 表号
	 */
	public static void createTopicFavoriteBean(Integer tableNumber){
		
		//ClassPool：CtClass对象的容器   
	    ClassPool pool = ClassPool.getDefault(); 
	//    ClassClassPath classPath = new ClassClassPath(this.getClass());
	    ClassClassPath classPath = new ClassClassPath(CreateBean.class);
	    pool.insertClassPath(classPath);

	      
	    //通过ClassPool生成一个public新类HistoryOrder.java   
	    CtClass ctClass = pool.makeClass("cms.bean.favorite.TopicFavorite_"+tableNumber); 

        try {
        	// 父类
        	ctClass.setSuperclass(pool.get("cms.bean.favorite.TopicFavoriteEntity"));
        	// 添加接口
			ctClass.addInterface(pool.get("java.io.Serializable"));
			
			// 添加属性   
			 ctClass.addField(CtField.make("private static final long serialVersionUID = 15000000000000000"+ tableNumber+"L;", ctClass));

			//写入注解(Annotation) 
			ClassFile cf = ctClass.getClassFile();  
	        ConstPool cp = cf.getConstPool();
 
	        //@Table(name="historyorder_0",indexes = {@Index(name="historyorder_idx", columnList="state,visible")})
	        AnnotationsAttribute attr = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);
	        
	        //@Entity注解
	        Annotation entity_a = new Annotation("javax.persistence.Entity", cp);
	  	  	attr.addAnnotation(entity_a);
	  	  	
	        //@Table注解
	        Annotation a = new Annotation("javax.persistence.Table", cp);
	        a.addMemberValue("name", new StringMemberValue("topicfavorite_"+tableNumber, cp));
	       
	        Annotation index_a = new Annotation("javax.persistence.Index", cp);
	        index_a.addMemberValue("name", new StringMemberValue("topicFavorite_1_idx", cp));
	        index_a.addMemberValue("columnList", new StringMemberValue("topicId,addtime", cp));
	        AnnotationMemberValue annotationMemberValue = new AnnotationMemberValue(cp);
	        annotationMemberValue.setValue(index_a);

	      

	        MemberValue[] vals = new MemberValue[]{annotationMemberValue};

	        ArrayMemberValue arrayMemberValue=new ArrayMemberValue(cp);//数组类型
	        arrayMemberValue.setValue(vals);
	      
	        a.addMemberValue("indexes", arrayMemberValue);//数组类型
	        attr.addAnnotation(a);
	        cf.addAttribute(attr);
	        cf.setVersionToJava5();

	        
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("话题收藏bean",e);
	        }
		}   catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("话题收藏bean",e);
	        }
		}   
       
        
      //把生成的class文件写入文件   
        byte[] byteArr;
        FileOutputStream fos = null;
		try {
			File file = new File(PathUtil.path()+File.separator+"WEB-INF"+File.separator+"classes"+File.separator+"cms"+File.separator+"bean"+File.separator+"favorite"+File.separator+"TopicFavorite_"+tableNumber+".class");
			
			if(!file.exists()){
				byteArr = ctClass.toBytecode();
				//	fos = new FileOutputStream(new File("C://HistoryOrder_5.class"));
				fos = new FileOutputStream(file);
				fos.write(byteArr); 
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("话题收藏bean",e);
	        }
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("话题收藏bean",e);
	        }
		} finally {
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
			            logger.error("话题收藏bean",e);
			        }
				}  
			}
		} 
	}
	
	/**
	 * 创建话题取消隐藏bean
	 * @param number 表号
	 */
	public static void createTopicUnhideBean(Integer tableNumber){
		
		//ClassPool：CtClass对象的容器   
	    ClassPool pool = ClassPool.getDefault(); 
	//    ClassClassPath classPath = new ClassClassPath(this.getClass());
	    ClassClassPath classPath = new ClassClassPath(CreateBean.class);
	    pool.insertClassPath(classPath);

	      
	    //通过ClassPool生成一个public新类HistoryOrder.java   
	    CtClass ctClass = pool.makeClass("cms.bean.topic.TopicUnhide_"+tableNumber); 

        try {
        	// 父类
        	ctClass.setSuperclass(pool.get("cms.bean.topic.UnhideEntity"));
        	// 添加接口
			ctClass.addInterface(pool.get("java.io.Serializable"));
			
			// 添加属性   
			ctClass.addField(CtField.make("private static final long serialVersionUID = 16000000000000000"+ tableNumber+"L;", ctClass));

			//写入注解(Annotation) 
			ClassFile cf = ctClass.getClassFile();  
	        ConstPool cp = cf.getConstPool();
 
	        //@Table(name="historyorder_0",indexes = {@Index(name="historyorder_idx", columnList="state,visible")})
	        AnnotationsAttribute attr = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);
	        
	        //@Entity注解
	        Annotation entity_a = new Annotation("javax.persistence.Entity", cp);
	  	  	attr.addAnnotation(entity_a);
	  	  	
	        //@Table注解
	        Annotation a = new Annotation("javax.persistence.Table", cp);
	        a.addMemberValue("name", new StringMemberValue("topicunhide_"+tableNumber, cp));
	       
	        Annotation index_a = new Annotation("javax.persistence.Index", cp);
	        index_a.addMemberValue("name", new StringMemberValue("topicunhide_1_idx", cp));
	        index_a.addMemberValue("columnList", new StringMemberValue("topicId,cancelTime", cp));
	        AnnotationMemberValue annotationMemberValue = new AnnotationMemberValue(cp);
	        annotationMemberValue.setValue(index_a);

	      

	        MemberValue[] vals = new MemberValue[]{annotationMemberValue};

	        ArrayMemberValue arrayMemberValue=new ArrayMemberValue(cp);//数组类型
	        arrayMemberValue.setValue(vals);
	      
	        a.addMemberValue("indexes", arrayMemberValue);//数组类型
	        attr.addAnnotation(a);
	        cf.addAttribute(attr);
	        cf.setVersionToJava5();

	        
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("话题取消隐藏bean",e);
	        }
		}   catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("话题取消隐藏bean",e);
	        }
		}   
       
        
      //把生成的class文件写入文件   
        byte[] byteArr;
        FileOutputStream fos = null;
		try {
			File file = new File(PathUtil.path()+File.separator+"WEB-INF"+File.separator+"classes"+File.separator+"cms"+File.separator+"bean"+File.separator+"topic"+File.separator+"TopicUnhide_"+tableNumber+".class");
			
			if(!file.exists()){
				byteArr = ctClass.toBytecode();
				//	fos = new FileOutputStream(new File("C://HistoryOrder_5.class"));
				fos = new FileOutputStream(file);
				fos.write(byteArr); 
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("话题取消隐藏bean",e);
	        }
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("话题取消隐藏bean",e);
	        }
		} finally {
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
			            logger.error("话题取消隐藏bean",e);
			        }
				}  
			}
		} 
	}
	
	/**
	 * 创建用户动态bean
	 * @param number 表号
	 */
	public static void createUserDynamicBean(Integer tableNumber){
		
		//ClassPool：CtClass对象的容器   
	    ClassPool pool = ClassPool.getDefault(); 
	//    ClassClassPath classPath = new ClassClassPath(this.getClass());
	    ClassClassPath classPath = new ClassClassPath(CreateBean.class);
	    pool.insertClassPath(classPath);

	      
	    //通过ClassPool生成一个public新类HistoryOrder.java   
	    CtClass ctClass = pool.makeClass("cms.bean.user.UserDynamic_"+tableNumber); 

        try {
        	// 父类
        	ctClass.setSuperclass(pool.get("cms.bean.user.UserDynamicEntity"));
        	// 添加接口
			ctClass.addInterface(pool.get("java.io.Serializable"));
			
			// 添加属性   
			ctClass.addField(CtField.make("private static final long serialVersionUID = 17000000000000000"+ tableNumber+"L;", ctClass));

			//写入注解(Annotation) 
			ClassFile cf = ctClass.getClassFile();  
	        ConstPool cp = cf.getConstPool();
 
	        //@Table(name="historyorder_0",indexes = {@Index(name="historyorder_idx", columnList="state,visible")})
	        AnnotationsAttribute attr = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);
	        
	        //@Entity注解
	        Annotation entity_a = new Annotation("javax.persistence.Entity", cp);
	  	  	attr.addAnnotation(entity_a);
	  	  	
	        //@Table注解
	        Annotation a = new Annotation("javax.persistence.Table", cp);
	        a.addMemberValue("name", new StringMemberValue("userDynamic_"+tableNumber, cp));
	       
	        Annotation index_a = new Annotation("javax.persistence.Index", cp);
	        index_a.addMemberValue("name", new StringMemberValue("userDynamic_1_idx", cp));
	        index_a.addMemberValue("columnList", new StringMemberValue("userName,status,postTime", cp));
	        AnnotationMemberValue annotationMemberValue = new AnnotationMemberValue(cp);
	        annotationMemberValue.setValue(index_a);
	        
	        Annotation index_b = new Annotation("javax.persistence.Index", cp);
	        index_b.addMemberValue("name", new StringMemberValue("userDynamic_2_idx", cp));
	        index_b.addMemberValue("columnList", new StringMemberValue("topicId,userName,module", cp));
	        AnnotationMemberValue annotationMemberValue_b = new AnnotationMemberValue(cp);
	        annotationMemberValue_b.setValue(index_b);
	        
	        Annotation index_c = new Annotation("javax.persistence.Index", cp);
	        index_c.addMemberValue("name", new StringMemberValue("userDynamic_3_idx", cp));
	        index_c.addMemberValue("columnList", new StringMemberValue("commentId,userName,module", cp));
	        AnnotationMemberValue annotationMemberValue_c = new AnnotationMemberValue(cp);
	        annotationMemberValue_c.setValue(index_c);
	        
	        Annotation index_d = new Annotation("javax.persistence.Index", cp);
	        index_d.addMemberValue("name", new StringMemberValue("userDynamic_4_idx", cp));
	        index_d.addMemberValue("columnList", new StringMemberValue("replyId,userName,module", cp));
	        AnnotationMemberValue annotationMemberValue_d = new AnnotationMemberValue(cp);
	        annotationMemberValue_d.setValue(index_d);
	        

	        MemberValue[] vals = new MemberValue[]{annotationMemberValue,annotationMemberValue_b,annotationMemberValue_c,annotationMemberValue_d};


	        ArrayMemberValue arrayMemberValue=new ArrayMemberValue(cp);//数组类型
	        arrayMemberValue.setValue(vals);
	      
	        a.addMemberValue("indexes", arrayMemberValue);//数组类型
	        attr.addAnnotation(a);
	        cf.addAttribute(attr);
	        cf.setVersionToJava5();

	        
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("创建用户动态bean",e);
	        }
		}   catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("创建用户动态bean",e);
	        }
		}   
       
        
      //把生成的class文件写入文件   
        byte[] byteArr;
        FileOutputStream fos = null;
		try {
			File file = new File(PathUtil.path()+File.separator+"WEB-INF"+File.separator+"classes"+File.separator+"cms"+File.separator+"bean"+File.separator+"user"+File.separator+"UserDynamic_"+tableNumber+".class");
			
			if(!file.exists()){
				byteArr = ctClass.toBytecode();
				//	fos = new FileOutputStream(new File("C://HistoryOrder_5.class"));
				fos = new FileOutputStream(file);
				fos.write(byteArr);
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("创建用户动态bean",e);
	        }
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("创建用户动态bean",e);
	        }
		} finally {
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
			            logger.error("创建用户动态bean",e);
			        }
				}  
			}
		}


	}
	
	/**
	 * 创建点赞bean
	 * @param number 表号
	 */
	public static void createLikeBean(Integer tableNumber){
		
		//ClassPool：CtClass对象的容器   
	    ClassPool pool = ClassPool.getDefault(); 
	//    ClassClassPath classPath = new ClassClassPath(this.getClass());
	    ClassClassPath classPath = new ClassClassPath(CreateBean.class);
	    pool.insertClassPath(classPath);

	      
	    //通过ClassPool生成一个public新类HistoryOrder.java   
	    CtClass ctClass = pool.makeClass("cms.bean.like.Like_"+tableNumber); 

        try {
        	// 父类
        	ctClass.setSuperclass(pool.get("cms.bean.like.LikeEntity"));
        	// 添加接口
			ctClass.addInterface(pool.get("java.io.Serializable"));
			
			// 添加属性   
			 ctClass.addField(CtField.make("private static final long serialVersionUID = 18000000000000000"+ tableNumber+"L;", ctClass));

			//写入注解(Annotation) 
			ClassFile cf = ctClass.getClassFile();  
	        ConstPool cp = cf.getConstPool();
 
	        //@Table(name="historyorder_0",indexes = {@Index(name="historyorder_idx", columnList="state,visible")})
	        AnnotationsAttribute attr = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);
	        
	        //@Entity注解
	        Annotation entity_a = new Annotation("javax.persistence.Entity", cp);
	  	  	attr.addAnnotation(entity_a);
	  	  	
	        //@Table注解
	        Annotation a = new Annotation("javax.persistence.Table", cp);
	        a.addMemberValue("name", new StringMemberValue("like_"+tableNumber, cp));
	       
	        Annotation index_a = new Annotation("javax.persistence.Index", cp);
	        index_a.addMemberValue("name", new StringMemberValue("like_1_idx", cp));
	        index_a.addMemberValue("columnList", new StringMemberValue("userName,addtime", cp));
	        AnnotationMemberValue annotationMemberValue = new AnnotationMemberValue(cp);
	        annotationMemberValue.setValue(index_a);

	      

	        MemberValue[] vals = new MemberValue[]{annotationMemberValue};

	        ArrayMemberValue arrayMemberValue=new ArrayMemberValue(cp);//数组类型
	        arrayMemberValue.setValue(vals);
	      
	        a.addMemberValue("indexes", arrayMemberValue);//数组类型
	        attr.addAnnotation(a);
	        cf.addAttribute(attr);
	        cf.setVersionToJava5();

	        
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("点赞bean",e);
	        }
		}   catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("点赞bean",e);
	        }
		}   
       
        
      //把生成的class文件写入文件   
        byte[] byteArr;
        FileOutputStream fos = null;
		try {
			File file = new File(PathUtil.path()+File.separator+"WEB-INF"+File.separator+"classes"+File.separator+"cms"+File.separator+"bean"+File.separator+"like"+File.separator+"Like_"+tableNumber+".class");
			
			if(!file.exists()){
				byteArr = ctClass.toBytecode();
				//	fos = new FileOutputStream(new File("C://HistoryOrder_5.class"));
				fos = new FileOutputStream(file);
				fos.write(byteArr); 
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("点赞bean",e);
	        }
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("点赞bean",e);
	        }
		} finally {
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
			            logger.error("点赞bean",e);
			        }
				}  
			}
		} 
	}
	
	/**
	 * 创建话题点赞bean
	 * @param number 表号
	 */
	public static void createTopicLikeBean(Integer tableNumber){
		
		//ClassPool：CtClass对象的容器   
	    ClassPool pool = ClassPool.getDefault(); 
	//    ClassClassPath classPath = new ClassClassPath(this.getClass());
	    ClassClassPath classPath = new ClassClassPath(CreateBean.class);
	    pool.insertClassPath(classPath);

	      
	    //通过ClassPool生成一个public新类HistoryOrder.java   
	    CtClass ctClass = pool.makeClass("cms.bean.like.TopicLike_"+tableNumber); 

        try {
        	// 父类
        	ctClass.setSuperclass(pool.get("cms.bean.like.TopicLikeEntity"));
        	// 添加接口
			ctClass.addInterface(pool.get("java.io.Serializable"));
			
			// 添加属性   
			 ctClass.addField(CtField.make("private static final long serialVersionUID = 19000000000000000"+ tableNumber+"L;", ctClass));

			//写入注解(Annotation) 
			ClassFile cf = ctClass.getClassFile();  
	        ConstPool cp = cf.getConstPool();
 
	        //@Table(name="historyorder_0",indexes = {@Index(name="historyorder_idx", columnList="state,visible")})
	        AnnotationsAttribute attr = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);
	        
	        //@Entity注解
	        Annotation entity_a = new Annotation("javax.persistence.Entity", cp);
	  	  	attr.addAnnotation(entity_a);
	  	  	
	        //@Table注解
	        Annotation a = new Annotation("javax.persistence.Table", cp);
	        a.addMemberValue("name", new StringMemberValue("topiclike_"+tableNumber, cp));
	       
	        Annotation index_a = new Annotation("javax.persistence.Index", cp);
	        index_a.addMemberValue("name", new StringMemberValue("topicLike_1_idx", cp));
	        index_a.addMemberValue("columnList", new StringMemberValue("topicId,addtime", cp));
	        AnnotationMemberValue annotationMemberValue = new AnnotationMemberValue(cp);
	        annotationMemberValue.setValue(index_a);

	      

	        MemberValue[] vals = new MemberValue[]{annotationMemberValue};

	        ArrayMemberValue arrayMemberValue=new ArrayMemberValue(cp);//数组类型
	        arrayMemberValue.setValue(vals);
	      
	        a.addMemberValue("indexes", arrayMemberValue);//数组类型
	        attr.addAnnotation(a);
	        cf.addAttribute(attr);
	        cf.setVersionToJava5();

	        
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("话题点赞bean",e);
	        }
		}   catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("话题点赞bean",e);
	        }
		}   
       
        
      //把生成的class文件写入文件   
        byte[] byteArr;
        FileOutputStream fos = null;
		try {
			File file = new File(PathUtil.path()+File.separator+"WEB-INF"+File.separator+"classes"+File.separator+"cms"+File.separator+"bean"+File.separator+"like"+File.separator+"TopicLike_"+tableNumber+".class");
			
			if(!file.exists()){
				byteArr = ctClass.toBytecode();
				//	fos = new FileOutputStream(new File("C://HistoryOrder_5.class"));
				fos = new FileOutputStream(file);
				fos.write(byteArr); 
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("话题点赞bean",e);
	        }
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("话题点赞bean",e);
	        }
		} finally {
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
			            logger.error("话题点赞bean",e);
			        }
				}  
			}
		} 
	}
	
	/**
	 * 创建关注bean
	 * @param number 表号
	 */
	public static void createFollowBean(Integer tableNumber){
		
		//ClassPool：CtClass对象的容器   
	    ClassPool pool = ClassPool.getDefault(); 
	//    ClassClassPath classPath = new ClassClassPath(this.getClass());
	    ClassClassPath classPath = new ClassClassPath(CreateBean.class);
	    pool.insertClassPath(classPath);

	      
	    //通过ClassPool生成一个public新类HistoryOrder.java   
	    CtClass ctClass = pool.makeClass("cms.bean.follow.Follow_"+tableNumber); 

        try {
        	// 父类
        	ctClass.setSuperclass(pool.get("cms.bean.follow.FollowEntity"));
        	// 添加接口
			ctClass.addInterface(pool.get("java.io.Serializable"));
			
			// 添加属性   
			 ctClass.addField(CtField.make("private static final long serialVersionUID = 20000000000000000"+ tableNumber+"L;", ctClass));

			//写入注解(Annotation) 
			ClassFile cf = ctClass.getClassFile();  
	        ConstPool cp = cf.getConstPool();
 
	        //@Table(name="historyorder_0",indexes = {@Index(name="historyorder_idx", columnList="state,visible")})
	        AnnotationsAttribute attr = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);
	        
	        //@Entity注解
	        Annotation entity_a = new Annotation("javax.persistence.Entity", cp);
	  	  	attr.addAnnotation(entity_a);
	  	  	
	        //@Table注解
	        Annotation a = new Annotation("javax.persistence.Table", cp);
	        a.addMemberValue("name", new StringMemberValue("follow_"+tableNumber, cp));
	       
	        Annotation index_a = new Annotation("javax.persistence.Index", cp);
	        index_a.addMemberValue("name", new StringMemberValue("follow_1_idx", cp));
	        index_a.addMemberValue("columnList", new StringMemberValue("userName,addtime", cp));
	        AnnotationMemberValue annotationMemberValue = new AnnotationMemberValue(cp);
	        annotationMemberValue.setValue(index_a);

	      

	        MemberValue[] vals = new MemberValue[]{annotationMemberValue};

	        ArrayMemberValue arrayMemberValue=new ArrayMemberValue(cp);//数组类型
	        arrayMemberValue.setValue(vals);
	      
	        a.addMemberValue("indexes", arrayMemberValue);//数组类型
	        attr.addAnnotation(a);
	        cf.addAttribute(attr);
	        cf.setVersionToJava5();

	        
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("关注bean",e);
	        }
		}   catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("关注bean",e);
	        }
		}   
       
        
      //把生成的class文件写入文件   
        byte[] byteArr;
        FileOutputStream fos = null;
		try {
			File file = new File(PathUtil.path()+File.separator+"WEB-INF"+File.separator+"classes"+File.separator+"cms"+File.separator+"bean"+File.separator+"follow"+File.separator+"Follow_"+tableNumber+".class");
			
			if(!file.exists()){
				byteArr = ctClass.toBytecode();
				//	fos = new FileOutputStream(new File("C://HistoryOrder_5.class"));
				fos = new FileOutputStream(file);
				fos.write(byteArr); 
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("关注bean",e);
	        }
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("关注bean",e);
	        }
		} finally {
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
			            logger.error("关注bean",e);
			        }
				}  
			}
		} 
	}
	
	/**
	 * 创建粉丝bean
	 * @param number 表号
	 */
	public static void createFollowerBean(Integer tableNumber){
		
		//ClassPool：CtClass对象的容器   
	    ClassPool pool = ClassPool.getDefault(); 
	//    ClassClassPath classPath = new ClassClassPath(this.getClass());
	    ClassClassPath classPath = new ClassClassPath(CreateBean.class);
	    pool.insertClassPath(classPath);

	      
	    //通过ClassPool生成一个public新类HistoryOrder.java   
	    CtClass ctClass = pool.makeClass("cms.bean.follow.Follower_"+tableNumber); 

        try {
        	// 父类
        	ctClass.setSuperclass(pool.get("cms.bean.follow.FollowerEntity"));
        	// 添加接口
			ctClass.addInterface(pool.get("java.io.Serializable"));
			
			// 添加属性   
			 ctClass.addField(CtField.make("private static final long serialVersionUID = 20000000000000000"+ tableNumber+"L;", ctClass));

			//写入注解(Annotation) 
			ClassFile cf = ctClass.getClassFile();  
	        ConstPool cp = cf.getConstPool();
 
	        //@Table(name="historyorder_0",indexes = {@Index(name="historyorder_idx", columnList="state,visible")})
	        AnnotationsAttribute attr = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);
	        
	        //@Entity注解
	        Annotation entity_a = new Annotation("javax.persistence.Entity", cp);
	  	  	attr.addAnnotation(entity_a);
	  	  	
	        //@Table注解
	        Annotation a = new Annotation("javax.persistence.Table", cp);
	        a.addMemberValue("name", new StringMemberValue("follower_"+tableNumber, cp));
	       
	        Annotation index_a = new Annotation("javax.persistence.Index", cp);
	        index_a.addMemberValue("name", new StringMemberValue("follower_1_idx", cp));
	        index_a.addMemberValue("columnList", new StringMemberValue("userName,addtime", cp));
	        AnnotationMemberValue annotationMemberValue = new AnnotationMemberValue(cp);
	        annotationMemberValue.setValue(index_a);

	      

	        MemberValue[] vals = new MemberValue[]{annotationMemberValue};

	        ArrayMemberValue arrayMemberValue=new ArrayMemberValue(cp);//数组类型
	        arrayMemberValue.setValue(vals);
	      
	        a.addMemberValue("indexes", arrayMemberValue);//数组类型
	        attr.addAnnotation(a);
	        cf.addAttribute(attr);
	        cf.setVersionToJava5();

	        
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("粉丝bean",e);
	        }
		}   catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("粉丝bean",e);
	        }
		}   
       
        
      //把生成的class文件写入文件   
        byte[] byteArr;
        FileOutputStream fos = null;
		try {
			File file = new File(PathUtil.path()+File.separator+"WEB-INF"+File.separator+"classes"+File.separator+"cms"+File.separator+"bean"+File.separator+"follow"+File.separator+"Follower_"+tableNumber+".class");
			
			if(!file.exists()){
				byteArr = ctClass.toBytecode();
				//	fos = new FileOutputStream(new File("C://HistoryOrder_5.class"));
				fos = new FileOutputStream(file);
				fos.write(byteArr); 
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("粉丝bean",e);
	        }
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("粉丝bean",e);
	        }
		} finally {
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
			            logger.error("粉丝bean",e);
			        }
				}  
			}
		} 
	}
}
