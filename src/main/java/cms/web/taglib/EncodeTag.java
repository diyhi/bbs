package cms.web.taglib;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;

/**
 * 自定义URL编码
 *
 */
public class EncodeTag extends TagSupport {
	private static final long serialVersionUID = 398268058655426615L;

	private static Logger logger = LogManager.getLogger(EncodeTag.class);
	
	private Object value;//自定义标签的key属性   
	  
    public void setValue(Object key) throws JspException {   
    	
        this.value=ExpressionEvaluatorManager.evaluate("value", key== null ? "":key.toString(), Object.class, this, pageContext);   
  
    }   
    public int doEndTag() {   
        try {// 使用JspWriter获得JSP的输出对象   
        	JspWriter jspWriterOutput = pageContext.getOut();   
			jspWriterOutput.write(URLEncoder.encode(value.toString(),"utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("自定义URL编码",e);
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("自定义URL编码",e);
	        }
		}   
        return EVAL_PAGE;   
    }   

}
