package cms.web.action.lucene;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import cms.utils.PathUtil;

/**
 * 问题Lucene初始化
 *
 */
public enum QuestionLuceneInit {
	// 唯一实例
	INSTANCE;
	
	private final Logger logger = LogManager.getLogger(QuestionLuceneInit.class);
	
    /** 
     * 索引目录
     */  
    private Directory directory;  
    
    
    private FacetsConfig config = new FacetsConfig();
    
    private SearcherManager searcherManager = null;
    /**
     * 写索引
     */
    private IndexWriter writer = null;
    //加载 
    private QuestionLuceneInit() {  
        try {  
 
        	String indexPath = PathUtil.path()+File.separator+"WEB-INF"+File.separator+"data"+File.separator+"questionIndex"+File.separator;
        	
        	directory = FSDirectory.open(Paths.get(indexPath));  
   	
        	//写入空版本号防止读索引时错误
        	this.createIndexWriter();
        	writer.commit();
        	
        	SearcherFactory searcherFactory = new SearcherFactory();
        	searcherManager = new SearcherManager(directory,searcherFactory);
        	
        	
        	/**
        	IndexWriterConfig iwc = new IndexWriterConfig(null);  
        	IndexWriter indexWriter = new IndexWriter(directory, iwc);  
			TrackingIndexWriter trackWriter = new TrackingIndexWriter(indexWriter);  
			searcherManager = new SearcherManager(indexWriter, true, new SearcherFactory())//true 表示在内存中删除，false可能删可能不删，设为false性能会更好一些 
   
        	ControlledRealTimeReopenThread<IndexSearcher> thread =   
        		     new ControlledRealTimeReopenThread<IndexSearcher>(trackWriter, searcherManager, 5.0, 0.025) ; 参数三：目标最大刷新间隔/秒  参数四：目标最小刷新间隔/秒
        	thread.setDaemon(true); ////设为后台进程 
        	thread.setName("后台刷新服务");  
        	thread.start();//启动线程
        	**/
        	
        } catch (IOException e) {  
       //     e.printStackTrace();  
        	if (logger.isErrorEnabled()) {
	            logger.error("加载",e);
	        }
        } finally{
        	this.closeIndexWriter();//关闭IndexWriter
        	
        	/**
        	try {
        		if(writer != null){
        			writer.close();
        		}
        		if(taxoWriter != null){
					taxoWriter.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}**/
        }
    }
    
    
    /**
     * 是否允许创建IndexWriter  (索引运行过程中，不能执行创建)
     */
    public boolean allowCreateIndexWriter(){	
    	
    	//判断是打开索引
		try {
			if (directory instanceof FSDirectory && ((FSDirectory) directory).checkPendingDeletions()) {
				return false;
				//throw new IllegalArgumentException("Directory " + dir + " still has pending deleted files; cannot initialize IndexWriter");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("是否允许创建IndexWriter  (索引运行过程中，不能执行创建)",e);
	        }
		}
    	return true;
    }
    /**
     * 生成IndexWriter
     */
    public void createIndexWriter(){
    	
    	IndexWriterConfig iwc=new IndexWriterConfig(null);
    	iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);//创建索引模式：CREATE:覆盖模式； APPEND:追加模式；   CREATE_OR_APPEND:每次都追加更新
    	try {
			writer = new IndexWriter(directory, iwc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("生成IndexWriter",e);
	        }
		}
    }
    /**
     * 关闭IndexWriter
     */
    public void closeIndexWriter(){
    	if(writer != null){
			try {
				writer.close();
				writer = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("关闭IndexWriter",e);
		        }
			}
		}
    }
    /**
     * 获取IndexWriter
     * @return
     */
    public IndexWriter getIndexWriter(){
    	return writer;
    }
    
    
    
    /** 
     * 获取索引目录
     * @return 
     */  
    public Directory getDirectory() { 
    	return directory;
    }
    
    public FacetsConfig getConfig() { 
    	return config;
    }
   
    /**
     * 获取索引对象
     * @return
     */
    public IndexSearcher getSearcher() {
    	IndexSearcher s = null;
    	try {
			s = searcherManager.acquire();//获取当前已打开的最新IndexSearcher
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("获取索引对象",e);
	        }
		}
    	return s;
    } 
    

    /**
     * 刷新索引(由本地定时器执行)QuestionIndexManage.refreshIndex();
     */
    public void refreshSearcher() {
    	try {
    		searcherManager.maybeRefresh();//尝试打开新的IndexReader，本质调用的是DirectoryReader.openIfChanged
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("刷新索引(由本地定时器执行)QuestionIndexManage.refreshIndex()",e);
	        }
		}
    }
    /**
     * 关闭索引对象
     * @param indexSearcher
     */
    public void closeSearcher(IndexSearcher indexSearcher){            
    	if(indexSearcher != null) {              
    		try {
    			searcherManager.release(indexSearcher);//释放不用的引用，本质调的是IndexReader.close()，当一个IndexReader内部的引用计数为0时，会关闭自己释放资源。
			} catch (IOException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("关闭索引对象",e);
		        }
			}         
    	}
    }
}
