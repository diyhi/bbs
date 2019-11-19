package cms.web.action.lucene;


import org.apache.lucene.analysis.util.CharTokenizer;
/**
 * 按字符分词
 * @author GAO
 *
 */
public class SpiltTokenizer extends CharTokenizer {
	char c;   
    public SpiltTokenizer(char c) {   
        super();   
        // TODO Auto-generated constructor stub   
        this.c=c;   
    }   
  
    @Override  
    protected boolean isTokenChar(int arg0) {   
        return arg0==c?false:true ;   
    }   

}
