package cms.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * 动态修改类
 *
 */
public class ModifyClass {
	private static final Logger logger = LogManager.getLogger(ModifyClass.class);
	
	
	/**
	 * 修改webappenhance.jar的com.github.pukkaone.jsp.EscapeXmlELResolver类getValue方法
	 */
	public static void escapeXmlELResolver_getValue(){
		ClassPool cp = ClassPool.getDefault();
		CtClass ctClass = null;
		ClassClassPath classPath = new ClassClassPath(ModifyClass.class);
		cp.insertClassPath(classPath);
	    try {
	    	//获得类文件名
			ctClass = cp.get("com.github.pukkaone.jsp.EscapeXmlELResolver");
			//获得要修改的方法名
		    CtMethod ctMethod = ctClass.getDeclaredMethod("getValue");
			//原方法体 env.getOut().write(escapedExpression.evalAndCoerceToString(env));
		//	ctMethod.setBody("{try{Integer i = null;"
		//            + "System.out.println(\"this is a new method\");");
		//	ctMethod.setBody("{int i =0;System.out.println(\"this is a new method\");}");
			//($$)表示所有的参数
		//	ctMethod.setBody("{$1.getOut().write(escapedExpression.evalAndCoerceToString($1));}");
		    
		    //在指定的位置插入代码
		    ctMethod.insertAt(88, " value = cms.utils.HtmlEscape.escape((String) value);int i=0;if(i==0){return value;}");
	
		    
		    
			/**
			//把生成的class文件写入文件   
	        byte[] byteArr;
	        FileOutputStream fos;
			try {
				
					byteArr = ctClass.toBytecode();
					fos = new FileOutputStream(new File("C://1.class"));
					fos.write(byteArr);  
			        fos.close();  
				
				
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CannotCompileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} **/
			
		    //加载此类
			ctClass.toClass();
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("修改webappenhance.jar的com.github.pukkaone.jsp.EscapeXmlELResolver类getValue方法不存在",e);
	        }
		}catch (CannotCompileException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("修改webappenhance.jar的com.github.pukkaone.jsp.EscapeXmlELResolver类getValue方法无法编译",e);
	        }
		}finally {
			if (ctClass != null) {
				ctClass.detach();// ClassPool默认不会回收，需要手动清理
            }                          
        }
	}
	
	


}
