package cms.web.taglib;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 当前时间
 *
 */
public class CurrentDate extends TagSupport{
	private static final long serialVersionUID = -3401092178077716710L;


	private static final Logger logger = LogManager.getLogger(CurrentDate.class);
	
	
	private String var;//返回值
	 public void setVar(String parameter) throws JspException {   
	        this.var = parameter;
	  
	    } 
	    public int doEndTag() throws JspException{   
	 
	        SimpleDateFormat dateformat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
	        String s = dateformat.format(new Date());   
	        try {   
	        	if(var != null && !"".equals(var.trim())){
	        		pageContext.setAttribute(var.trim(), s);
	        	}else{
	        		pageContext.getOut().write(s);   
	        	} 
	        } catch (IOException e) {   
	         //   e.printStackTrace();   
	        	if (logger.isErrorEnabled()) {
		            logger.error("当前时间",e);
		        }
	        }	
	        return super.doStartTag();  
	    }   

}
