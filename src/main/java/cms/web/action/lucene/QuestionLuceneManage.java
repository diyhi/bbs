package cms.web.action.lucene;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import cms.bean.QueryResult;
import cms.bean.question.AppendQuestionItem;
import cms.bean.question.Question;
import cms.bean.question.QuestionTagAssociation;
import cms.utils.HtmlEscape;
import cms.web.action.TextFilterManage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.BytesRef;
import org.springframework.stereotype.Component;
import org.wltea.analyzer.lucene.IKAnalyzer;


/**
 * 问题全文索引
 *
 */
@Component("questionLuceneManage")
public class QuestionLuceneManage {
	private static final Logger logger = LogManager.getLogger(QuestionLuceneManage.class);
	
	@Resource TextFilterManage textFilterManage;
	
	/**
	 * 添加索引
	 * @param questionList 问题集合
	 */
	public void addIndex(List<Question> questionList){
		
		if(questionList != null && questionList.size() >0){
			IndexWriter iw = QuestionLuceneInit.INSTANCE.getIndexWriter();
			try {
				// 默认IKAnalyzer()-false:实现最细粒度切分算法,true:分词器采用智能切分
				Analyzer analyzer_title = new IKAnalyzer(); 
				Analyzer analyzer_content = new IKAnalyzer(); 
				SplitAnalyzer analyzer_questionTag = new SplitAnalyzer(',');//设定自定义分词器  
				
				for(Question question : questionList){
					
					//用Document的add()方法增加一个属性进索引库,接收一个Field对象.
					Document doc= new Document();
					//Field.Store.YES:索引文件本来只存储索引数据,设置为YES之后会把文本内容也存储在索引文件中,如文档标题
					//Field.Store.NO:原文内容不存储在索引文件中,适合内容较大的情况
				    //如果Field.Store是YES，那么就可以在搜索结果中从索引数据直接提取该域的值；
				    //如果Field.Store是NO，那么就无法在搜索结果中从索引数据直接提取该域的值，会返回null。
					
					//FieldType ft = new FieldType();   
			        //ft.setStored(true); // 设置是否进行存储   
			        //ft.setIndexed(true); // 设置是否能够索引到   
			        //ft.setTokenized(true);// 设置是否进行分词分析  
					
					
					//Id
					doc.add(new LongPoint("id",question.getId()));
					doc.add(new StoredField("id", question.getId()));//要存储值，必须添加一个同名的StoredField
					
					
					//标题
					Field title = new TextField("title",question.getTitle(),Field.Store.YES);
					title.setTokenStream(analyzer_title.tokenStream("title",question.getTitle()));
					doc.add(title); 	
					doc.add(new SortedDocValuesField("title", new BytesRef(question.getTitle())));//排序

					String questionContent = question.getContent();
					if(question.getAppendQuestionItemList() != null && question.getAppendQuestionItemList().size() >0){
						for(AppendQuestionItem appendQuestionItem : question.getAppendQuestionItemList()){
							questionContent += appendQuestionItem.getContent();
						}
					}
					questionContent = textFilterManage.filterText(questionContent.trim());
					//内容
					Field content = new TextField("content",questionContent,Field.Store.YES);
					content.setTokenStream(analyzer_content.tokenStream("content",questionContent));
					doc.add(content); 	
				//	doc.add(new SortedDocValuesField("content", new BytesRef(productInfo.getContent())));//排序
				
					
					String questionTagIdGroup = "";
					for(QuestionTagAssociation questionTagAssociation :question.getQuestionTagAssociationList()){
						questionTagIdGroup += ","+questionTagAssociation.getQuestionTagId();
					}
					
					//删除第一个逗号
					String _questionTagIdGroup = org.apache.commons.lang3.StringUtils.difference(",", questionTagIdGroup);
					
					if(_questionTagIdGroup != null && !"".equals(_questionTagIdGroup.trim())){
						//问题标签Id组
						Field questionTagIdGroup_field = new TextField("questionTagIdGroup", _questionTagIdGroup, Field.Store.YES);
						questionTagIdGroup_field.setTokenStream(analyzer_questionTag.tokenStream("questionTagIdGroup", _questionTagIdGroup));
						doc.add(questionTagIdGroup_field);
					}
					
					//发表时间
					doc.add(new LongPoint("postTime",question.getPostTime().getTime())); 
					doc.add(new NumericDocValuesField("postTime",question.getPostTime().getTime()));//要排序，必须添加一个同名的NumericDocValuesField
				
					//用户名称
					doc.add(new StringField("userName",question.getUserName(),Field.Store.YES)); 
						
					//是否可见
					doc.add(new IntPoint("status",question.getStatus())); 
					
					
					//索引模块 多索引目录查询时用来区分模块 null:话题 20：问题
					doc.add(new IntPoint("indexModule",20)); 
					doc.add(new StoredField("indexModule", 20));//要存储值，必须添加一个同名的StoredField
					
				    iw.addDocument(QuestionLuceneInit.INSTANCE.getConfig().build(doc));
				}
				
				iw.commit();//提交数据    

			} catch (CorruptIndexException e) {
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("添加索引",e);
		        }
			} catch (LockObtainFailedException e) {
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("添加索引",e);
		        }
			} catch (IOException e) {
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("添加索引",e);
		        }
			}/**finally{
				if(taxoWriter != null){
					try {
						taxoWriter.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(iw != null){
					try {
						iw.close();
					} catch (CorruptIndexException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}**/
			
		}
	}
	
	/**
	 * 根据条件查询索引
	 * @param firstIndex 开始索引
	 * @param maxResult 需要获取的记录数
	 * @param keyword 关键词
	 * @param userName 用户名称
	 * @param minPostTime 最小发表时间
	 * @param maxPostTime 最大发表时间
	 * @param status 状态
	 * @param sortCondition 排序条件
	 * @param isHide 是否显示隐藏标签内容
	 * @return
	 */
	public QueryResult<Question> findIndexByCondition(int firstIndex, int maxResult,
			String keyword,Long tagId, String userName,
			Date minPostTime, Date maxPostTime,Integer status,int sortCondition){
		QueryResult<Question> qr = new QueryResult<Question>();
		//存储符合条件的记录   
	    List<Question> questionList = new ArrayList<Question>();  
	    SplitAnalyzer customAnalyzer = new SplitAnalyzer(',');//设定自定义分词器  
		Analyzer analyzer_keyword = new IKAnalyzer(); 
		
		IndexSearcher searcher =  QuestionLuceneInit.INSTANCE.getSearcher();
		if(searcher != null){
			try {
				
				//要搜索的字段  
				//	String[] fieldName = {"name","sellprice","createdate"}; //"path"字段不查询
				// BooleanClause.Occur[]数组,它表示多个条件之间的关系,     
				// BooleanClause.Occur.MUST表示 and,     
				// BooleanClause.Occur.MUST_NOT表示not,     
				// BooleanClause.Occur.SHOULD表示or. 
				BooleanQuery.Builder query = new BooleanQuery.Builder();//组合查询
				
				if(keyword != null && !"".equals(keyword.trim())){
					BooleanClause.Occur[] clauses = { BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD }; 
					Query keyword_parser = MultiFieldQueryParser.parse(new String[] {QueryParser.escape(keyword), QueryParser.escape(keyword)}, new String[] {"title", "content"}, clauses,analyzer_keyword);
					
					query.add(keyword_parser,BooleanClause.Occur.MUST);
				}
				

		        //按标签查询
		        if(tagId != null && tagId >0L){
	     	        QueryParser parser_tagId = new QueryParser("questionTagIdGroup",customAnalyzer);   
	         		Query query_tagId = parser_tagId.parse(tagId.toString());
	         		query.add(query_tagId, BooleanClause.Occur.MUST);
			    	
			    }
		        
		        //按用户查询
		        if(userName != null && !"".equals(userName.trim())){
		        	TermQuery userName_query = new TermQuery(new Term("userName", userName.trim()));
					query.add(userName_query,BooleanClause.Occur.MUST);
		        }
		        
		        //发表时间
			    if(minPostTime != null || maxPostTime != null){
			    	Long _minPostTime = null;
				    Long _maxPostTime = null;
				    if(minPostTime != null){
				    	_minPostTime = minPostTime.getTime();
				    }else{
				    	_minPostTime = 0L;
				    }
				    if(maxPostTime != null){
				    	_maxPostTime = maxPostTime .getTime();
				    }else{
				    	_maxPostTime = Long.MAX_VALUE-1;
				    }
				    // 范围查询，包含边界
				    Query createDate_query = LongPoint.newRangeQuery("postTime", _minPostTime , _maxPostTime);//不包含边界写法Math.addExact(_minCreateDate, 1), Math.addExact(_maxCreateDate, -1));
				    query.add(createDate_query,BooleanClause.Occur.MUST);
				    
			    }
			    
			    
		        if(status != null){
		        	//是否可见 精确查询
			        Query status_query = IntPoint.newExactQuery("status", status);
			        query.add(status_query,BooleanClause.Occur.MUST);
		        }

		        //分页起始索引  
		        int startIndex = firstIndex <= 1? 0 : (firstIndex-1) * maxResult;
				//分页结束索引
				int endIndex = startIndex+maxResult;
				
				//排序   
				SortField[] sortFields = new SortField[2]; 
				//type对应的值分别为： 
				//SortField.Type.SCORE 按相关度(积分)排序 
				//SortField.Type.DOC 按文档排序 
				//SortField.Type.AUTO 域的值为int、long、float都有效 
				//SortField.Type.STRING 域按STRING排序 
				//SortField.Type.FLOAT 
				//SortField.Type.LONG 
				//SortField.Type.DOUBLE 
				//SortField.Type.SHORT 
				//SortField.Type.CUSTOM 通过比较器排序 
				//SortField.Type.BYTE
				sortFields[0] = new SortField("title",SortField.Type.SCORE);   
				if(sortCondition == 1){
					sortFields[1] = new SortField("postTime",SortField.Type.LONG,true);//false升序，true降序 //发布时间排序   新-->旧
				}else if(sortCondition == 2){
					sortFields[1] = new SortField("postTime",SortField.Type.LONG,false);//false升序，true降序//发布时间排序  旧-->新
				}else{
					//和1相同
					sortFields[1] = new SortField("postTime",SortField.Type.LONG,true);//false升序，true降序 //发布时间排序   新-->旧
				}
				Sort sort = new Sort(sortFields);  

				
				 //高亮设置    
				SimpleHTMLFormatter simpleHtmlFormatter = new SimpleHTMLFormatter("<B>","</B>");//设定高亮显示的格式，也就是对高亮显示的词组加上前缀后缀   
				
				Highlighter highlighter = new Highlighter(simpleHtmlFormatter,new QueryScorer(query.build()));   
				highlighter.setTextFragmenter(new SimpleFragmenter(190));//设置每次返回的字符数.想必大家在使用搜索引擎的时候也没有一并把全部数据展示出来吧，当然这里也是设定只展示部分数据   
				
				
				
				//搜索相似度最高的5条记录
				TopDocs topDocs = searcher.search(query.build() ,endIndex,sort); 
				ScoreDoc[] scoreDocs = topDocs.scoreDocs;  
				
			    //查询所得记录 
			    for (int i = startIndex;i < endIndex && i<topDocs.totalHits; i++) {   
			    	Question question = new Question();
					Document targetDoc = searcher.doc(scoreDocs[i].doc); //根据文档编号取出相应的文档   
					question.setId(Long.parseLong(targetDoc.get("id")));
					
					String _title = targetDoc.get("title");
					if (_title != null && !"".equals(_title)) {   
						//执行转义
						_title = HtmlEscape.escape(_title);
		                TokenStream tokenStream = analyzer_keyword.tokenStream("title",new StringReader(_title));    
		                String highLightText = highlighter.getBestFragment(tokenStream, _title);//高亮显示  
		                if(highLightText != null && !"".equals(highLightText)){
		                	question.setTitle(highLightText);
		                }else{
		                	question.setTitle(_title);
		                }
		            }
					
					String _content = targetDoc.get("content");
					if (_content != null && !"".equals(_content)) { 					
		                TokenStream tokenStream = analyzer_keyword.tokenStream("content",new StringReader(_content));    
		                String highLightText = highlighter.getBestFragment(tokenStream, _content);//高亮显示  
		                if(highLightText != null && !"".equals(highLightText)){
		                	question.setContent(highLightText);
		                }else{
		                	if(_content.length() >190){
		                		question.setContent(_content.substring(0, 190));
		                	}else{
		                		question.setContent(_content);
		                	}
		                	
		                }
		            }
					
					questionList.add(question);
					
				} 
			    //把查询结果设进去
				qr.setResultlist(questionList);
				qr.setTotalrecord(topDocs.totalHits);
				
				
				
				
			
			} catch (CorruptIndexException e) {
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据条件查询索引",e);
		        }
			} catch (IOException e) {
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据条件查询索引",e);
		        }
			}catch (ParseException e) {
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据条件查询索引",e);
		        }
			}catch (InvalidTokenOffsetsException e) {
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据条件查询索引",e);
		        }
			}finally{ 
				//关闭资源   
				QuestionLuceneInit.INSTANCE.closeSearcher(searcher);
			}
		}
		return qr;
	}
	
	
	/**
	 * 查询相似问题
	 * @param maxResult 需要获取的记录数
	 * @param questionId 问题Id
	 * @param status 状态
	 * @return
	 */
	public List<Question> findLikeQuestion(int maxResult,Long questionId,Integer status){
		
		//存储符合条件的记录   
	    List<Question> questionList = new ArrayList<Question>();  
		IndexSearcher searcher =  QuestionLuceneInit.INSTANCE.getSearcher();

		if(searcher != null){
			//相似查询结果有可能包含自身，先查多一个
			maxResult++;	
			try {
				int docNum = -1;  
				 //精确查询
				Query questionId_query = LongPoint.newExactQuery("id", questionId);
				TopDocs topDocs = searcher.search(questionId_query,1); 
				if(topDocs != null){
					ScoreDoc[] scoreDocs = topDocs.scoreDocs;  
					if(scoreDocs != null && scoreDocs.length >0){
						docNum = scoreDocs[0].doc;
					}
				}
				if(docNum >=0){
					IndexReader reader = searcher.getIndexReader();
					
					MoreLikeThis mlt = new MoreLikeThis(reader); // 相似搜索组件
					mlt.setFieldNames(new String[] { "title"}); // 找“标题”相似的
					mlt.setAnalyzer(new IKAnalyzer());  
					mlt.setMinTermFreq(1); //最小词频 默认值是2，
					mlt.setMinDocFreq(1); // 最小文档频率 默认值是5
					
					
					BooleanQuery.Builder query = new BooleanQuery.Builder();//组合查询
					
					
					Query like_query = mlt.like(docNum);
					query.add(like_query,BooleanClause.Occur.MUST);
					
					if(status != null){
			        	//是否可见 精确查询
				        Query status_query = IntPoint.newExactQuery("status", status);
				        query.add(status_query,BooleanClause.Occur.MUST);
			        }
					
					
					TopDocs docs = searcher.search(query.build(), maxResult);
					if (docs.totalHits > 0){
						for (int i = 0; i < docs.scoreDocs.length; i++) {
							if (docs.scoreDocs[i].doc == docNum) { //排除自身
								continue;
							}
							if(questionList.size() == maxResult-1){//如果已获取足够所需的记录数
								break;
							}
							
							Question question = new Question();
							Document targetDoc = searcher.doc(docs.scoreDocs[i].doc); //根据文档编号取出相应的文档   
							question.setId(Long.parseLong(targetDoc.get("id")));

							String title = targetDoc.get("title");
							question.setTitle(title);
							questionList.add(question);		
						}
					}	
				}
			} catch (CorruptIndexException e) {
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据条件查询索引",e);
		        }
			} catch (IOException e) {
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据条件查询索引",e);
		        }
			}finally{ 
				//关闭资源   
				QuestionLuceneInit.INSTANCE.closeSearcher(searcher);
			}
		}
		
		
		return questionList;
	}

	/**
	 * 删除用户名称下的索引
	 * @param userName 用户名称
	 */
	public void deleteUserNameIndex(List<String> userNameList){
		IndexWriter iw = QuestionLuceneInit.INSTANCE.getIndexWriter();
		try { 
			// BooleanClause.Occur[]数组,它表示多个条件之间的关系,     
			// BooleanClause.Occur.MUST表示 and,     
			// BooleanClause.Occur.MUST_NOT表示not,     
			// BooleanClause.Occur.SHOULD表示or. 
			BooleanQuery.Builder query = new BooleanQuery.Builder();//组合查询
			for(String userName : userNameList){
				TermQuery userName_query = new TermQuery(new Term("userName", userName));//精确查询
				query.add(userName_query,BooleanClause.Occur.SHOULD);
			}

			iw.deleteDocuments(query.build());//删除指定ID的Document   
			iw.forceMergeDeletes();
			iw.commit();//提交   
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("删除用户名称下索引",e);
	        }
		} catch (LockObtainFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("删除用户名称下索引",e);
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("删除用户名称下索引",e);
	        }
		}
		
	}
	
	/**
	 * 删除索引
	 * @param questionIdList 问题Id集合
	 */
	public void deleteIndex(List<Long> questionIdList){
		IndexWriter iw = QuestionLuceneInit.INSTANCE.getIndexWriter();
		try {

			if(questionIdList != null && questionIdList.size() >0){

				//集合查询
				Query q  = LongPoint.newSetQuery("id", questionIdList);
				if(q != null){
					iw.deleteDocuments(q);//删除指定ID的Document 
					
					iw.forceMergeDeletes();
					iw.commit();//提交   
				}
				
			}
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("删除索引",e);
	        }
		} catch (LockObtainFailedException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("删除索引",e);
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("删除索引",e);
	        }
		}/**finally{
			if(iw != null){
				try {
					iw.close();
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}**/
	}

	/**
	 * 删除所有索引
	 */
	public void deleteAllIndex(){
		IndexWriter iw = QuestionLuceneInit.INSTANCE.getIndexWriter();
		try {
			iw.deleteAll();//标识删除全部 
			iw.forceMergeDeletes();  //强制删除    通过此方法可以强制从物理文件上删除文档，强制优化，del文件就没了，回收站清空
			iw.commit();//提交数据    
		
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("删除所有索引",e);
	        }
		} catch (LockObtainFailedException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("删除所有索引",e);
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("删除所有索引",e);
	        }
		}
	}
	


}
