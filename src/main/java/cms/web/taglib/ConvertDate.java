package cms.web.taglib;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 转换时间(long类型时间转为其它时间格式)
 *
 */
public class ConvertDate extends TagSupport{
	private static final long serialVersionUID = 1894049171269471211L;


	private static final Logger logger = LogManager.getLogger(ConvertDate.class);
	
	
	private String value;//传入值
	private String pattern;// yyyy-MM-dd HH:mm:ss
	private String var;//返回值
	
    public void setValue(String parameter) throws JspException {   
        this.value = parameter;
  
    }   
    public void setPattern(String parameter) throws JspException {   
        this.pattern = parameter;
    } 
    public void setVar(String parameter) throws JspException {   
        this.var = parameter;
  
    } 
    public int doEndTag() throws JspException{  
    	String vv = ""+value;   
        long time = Long.valueOf(vv);   
        Calendar c = Calendar.getInstance();   
        c.setTimeInMillis(time);   
        SimpleDateFormat dateformat =new SimpleDateFormat(pattern);   
        String s = dateformat.format(c.getTime());   
        try {   
        	if(var != null && !"".equals(var.trim())){
        		pageContext.setAttribute(var.trim(), s);
        	}else{
        		pageContext.getOut().write(s);   
        	} 
        } catch (IOException e) {   
         //   e.printStackTrace();   
        	if (logger.isErrorEnabled()) {
	            logger.error("转换时间(long类型时间转为其它时间格式)",e);
	        }
        }	
        return super.doStartTag();  
    }   


}
