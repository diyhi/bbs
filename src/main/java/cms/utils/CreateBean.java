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
	        a.addMemberValue("name", new StringMemberValue("userLoginLog_"+tableNumber, cp));
	       
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
	        a.addMemberValue("name", new StringMemberValue("staffLoginLog_"+tableNumber, cp));
	       
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
	
	
	
	
}
