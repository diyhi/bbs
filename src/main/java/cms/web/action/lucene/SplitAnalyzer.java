package cms.web.action.lucene;


import org.apache.lucene.analysis.Analyzer;

/**
 * 自定义单个char字符分词器 
 * @author GAO
 *
 */
public class SplitAnalyzer extends Analyzer{
	private char c;//按特定符号进行拆分   


    public SplitAnalyzer(char c) {   
        this.c=c;   
    }   
    @Override  
    protected TokenStreamComponents createComponents(String fieldName) {   
  
        return new TokenStreamComponents(new SpiltTokenizer(c));  
        
    }
    
  
}
