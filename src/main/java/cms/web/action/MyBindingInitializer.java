package cms.web.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;

/**
 * 注册框架级的自定义属性编辑器
 *
 */
public class MyBindingInitializer implements WebBindingInitializer{ 
	
	public void initBinder(WebDataBinder binder) {  
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");   
        dateFormat.setLenient(false);   
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));   
     //   binder.registerCustomEditor(String.class, new StringTrimmerEditor(false)); 

    }  
	/**
	private static final int HTTP_STATUS_500 = 500; 
	private static final int BUF_SIZE = 4096; 
	@InitBinder 
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
        dateFormat.setLenient(false); 
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false)); 
        binder.registerCustomEditor(BigDecimal.class, new CustomNumberEditor(BigDecimal.class, false)); 
        binder.registerCustomEditor(Integer.class, null, 
        	new CustomNumberEditor(Integer.class, null, true)); 
       
	   binder.registerCustomEditor(Float.class, new CustomNumberEditor( 
	     Float.class, true)); 
	   binder.registerCustomEditor(Double.class, new CustomNumberEditor( 
	     Double.class, true)); 
	   binder.registerCustomEditor(BigInteger.class, new CustomNumberEditor( 
	     BigInteger.class, true)); 
	} **/
}
