package cms.web.action.forumCode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import cms.bean.forumCode.ForumCodeFile;
import cms.bean.forumCode.ForumCodeNode;
import cms.service.template.TemplateService;
import cms.utils.PathUtil;
import cms.web.action.FileManage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * 版块代码辅助
 *
 */
@Component("forumCodeManage")
public class ForumCodeManage {
	private static final Logger logger = LogManager.getLogger(ForumCodeManage.class);
	
	
	@Resource TemplateService templateService;//通过接口引用代理返回的对象
	@Resource FileManage fileManage;
	/**
	 * 版块代码节点
	 * @param dirName 目录
	 * 单层monolayer  多层multilayer 分页page
	 * @return
	 */
	public List<ForumCodeNode> forumCodeNodeList(String dirName){
		List<ForumCodeNode> forumCodeNodeList = new ArrayList<ForumCodeNode>();

		//话题
		List<ForumCodeNode> childNode =  new ArrayList<ForumCodeNode>();//添加二级节点
		childNode.add(new ForumCodeNode(1001,"话题列表","topicRelated_topic_",Arrays.asList(new String[]{"分页"})));
		childNode.add(new ForumCodeNode(1002,"话题内容","topicRelated_topicContent_",Arrays.asList(new String[]{"实体对象"})));
		childNode.add(new ForumCodeNode(1003,"评论列表","topicRelated_comment_",Arrays.asList(new String[]{"分页"})));
		childNode.add(new ForumCodeNode(1004,"标签列表","topicRelated_tag_",Arrays.asList(new String[]{"集合"})));
		childNode.add(new ForumCodeNode(1005,"添加话题","topicRelated_addTopic_",Arrays.asList(new String[]{"集合"})));
		childNode.add(new ForumCodeNode(1006,"添加评论","topicRelated_addComment_",Arrays.asList(new String[]{"集合"})));
		childNode.add(new ForumCodeNode(1007,"引用评论","topicRelated_quoteComment_",Arrays.asList(new String[]{"集合"})));
		childNode.add(new ForumCodeNode(1008,"回复评论","topicRelated_replyComment_",Arrays.asList(new String[]{"集合"})));
		childNode.add(new ForumCodeNode(1009,"相似话题","topicRelated_likeTopic_",Arrays.asList(new String[]{"集合"})));
		childNode.add(new ForumCodeNode(1010,"话题取消隐藏","topicRelated_topicUnhide_",Arrays.asList(new String[]{"集合"})));
		
		forumCodeNodeList.add(new ForumCodeNode(100,"话题",childNode));//添加一级节点
		
		//收藏夹
		List<ForumCodeNode> childNode13 =  new ArrayList<ForumCodeNode>();//添加二级节点	
		childNode13.add(new ForumCodeNode(13001,"加入收藏夹","favoriteRelated_addFavorite_",Arrays.asList(new String[]{"集合"})));
		childNode13.add(new ForumCodeNode(13002,"话题会员收藏总数","favoriteRelated_favoriteCount_",Arrays.asList(new String[]{"实体对象"})));
		childNode13.add(new ForumCodeNode(13003,"用户是否已经收藏话题","favoriteRelated_alreadyCollected_",Arrays.asList(new String[]{"实体对象"})));
		forumCodeNodeList.add(new ForumCodeNode(130,"收藏夹",childNode13));//添加一级节点
		
		//点赞
		List<ForumCodeNode> childNode14 =  new ArrayList<ForumCodeNode>();//添加二级节点	
		childNode14.add(new ForumCodeNode(14001,"给话题点赞","likeRelated_addLike_",Arrays.asList(new String[]{"集合"})));
		childNode14.add(new ForumCodeNode(14002,"话题点赞总数","likeRelated_likeCount_",Arrays.asList(new String[]{"实体对象"})));
		childNode14.add(new ForumCodeNode(14003,"用户是否已经点赞该话题","likeRelated_alreadyLiked_",Arrays.asList(new String[]{"实体对象"})));
		forumCodeNodeList.add(new ForumCodeNode(140,"点赞",childNode14));//添加一级节点
		
		//关注
		List<ForumCodeNode> childNode15 =  new ArrayList<ForumCodeNode>();//添加二级节点	
		childNode15.add(new ForumCodeNode(15001,"关注用户","followRelated_addFollow_",Arrays.asList(new String[]{"集合"})));
		childNode15.add(new ForumCodeNode(15002,"粉丝总数","followRelated_followerCount_",Arrays.asList(new String[]{"实体对象"})));
		childNode15.add(new ForumCodeNode(15003,"是否已经关注该用户","followRelated_following_",Arrays.asList(new String[]{"实体对象"})));
		forumCodeNodeList.add(new ForumCodeNode(150,"关注",childNode15));//添加一级节点
		
		//会员卡
		List<ForumCodeNode> childNode20 =  new ArrayList<ForumCodeNode>();//添加二级节点	
		childNode20.add(new ForumCodeNode(20001,"会员卡列表","membershipCardRelated_membershipCard_",Arrays.asList(new String[]{"集合"})));
		childNode20.add(new ForumCodeNode(20002,"会员卡内容","membershipCardRelated_membershipCardContent_",Arrays.asList(new String[]{"实体对象"})));
		childNode20.add(new ForumCodeNode(20003,"购买会员卡","membershipCardRelated_buyMembershipCard_",Arrays.asList(new String[]{"集合"})));
		forumCodeNodeList.add(new ForumCodeNode(200,"会员卡",childNode20));//添加一级节点
		
		//在线留言
		List<ForumCodeNode> childNode3 =  new ArrayList<ForumCodeNode>();//添加二级节点	
		childNode3.add(new ForumCodeNode(3001,"添加在线留言","feedbackRelated_addFeedback_",Arrays.asList(new String[]{"集合"})));
		forumCodeNodeList.add(new ForumCodeNode(103,"在线留言",childNode3));//添加一级节点
		
		//友情链接
		List<ForumCodeNode> childNode4 =  new ArrayList<ForumCodeNode>();//添加二级节点
		childNode4.add(new ForumCodeNode(4001,"友情链接列表","linksRelated_links_",Arrays.asList(new String[]{"集合"})));
		forumCodeNodeList.add(new ForumCodeNode(104,"友情链接",childNode4));//添加一级节点
		
		//广告相关
		List<ForumCodeNode> childNode9 =  new ArrayList<ForumCodeNode>();//添加二级节点
		childNode9.add(new ForumCodeNode(9001,"图片广告","advertRelated_image_",Arrays.asList(new String[]{"集合"})));
		forumCodeNodeList.add(new ForumCodeNode(109,"广告",childNode9));//添加一级节点
		
		
		
		//在线帮助
		List<ForumCodeNode> childNode11 =  new ArrayList<ForumCodeNode>();//添加二级节点
		childNode11.add(new ForumCodeNode(11001,"在线帮助列表","helpRelated_help_",Arrays.asList(new String[]{"单层","分页","集合"})));
		childNode11.add(new ForumCodeNode(11002,"推荐在线帮助","helpRelated_recommendHelp_",Arrays.asList(new String[]{"集合"})));
		childNode11.add(new ForumCodeNode(11003,"在线帮助分类","helpRelated_helpType_",Arrays.asList(new String[]{"集合"})));
		childNode11.add(new ForumCodeNode(11004,"在线帮助导航","helpRelated_helpNavigation_",Arrays.asList(new String[]{"集合"})));
		childNode11.add(new ForumCodeNode(11005,"在线帮助内容","helpRelated_helpContent_",Arrays.asList(new String[]{"实体对象"})));
		forumCodeNodeList.add(new ForumCodeNode(111,"在线帮助",childNode11));//添加一级节点
		
		
		//站点栏目
		List<ForumCodeNode> childNode16 =  new ArrayList<ForumCodeNode>();//添加二级节点
		childNode16.add(new ForumCodeNode(16001,"站点栏目列表","columnRelated_column_",Arrays.asList(new String[]{"集合"})));
		forumCodeNodeList.add(new ForumCodeNode(116,"站点栏目",childNode16));//添加一级节点
		
		//
		List<ForumCodeNode> childNode17 =  new ArrayList<ForumCodeNode>();//添加二级节点
		childNode17.add(new ForumCodeNode(17001,"用户自定义HTML","customForumRelated_customHTML_",Arrays.asList(new String[]{"实体对象"})));
		forumCodeNodeList.add(new ForumCodeNode(117,"自定义版块",childNode17));//添加一级节点
		
		
		//系统
		List<ForumCodeNode> childNode18 =  new ArrayList<ForumCodeNode>();//添加二级节点
		childNode18.add(new ForumCodeNode(18001,"热门搜索词","systemRelated_searchWord_",Arrays.asList(new String[]{"集合"})));
		forumCodeNodeList.add(new ForumCodeNode(118,"系统",childNode18));//添加一级节点
		
		List<ForumCodeFile> forumCodeFileList = this.readForumCodeFile(dirName);
		
		int count = 0;//计数
		for(int i = 0; i<forumCodeNodeList.size(); i++){
			//二级节点
			List<ForumCodeNode> childNodes = forumCodeNodeList.get(i).getChildNode();
			
			for(int j = 0; j< childNodes.size();j++){
				ForumCodeNode two_ForumCodeNode = childNodes.get(j);
				String prefix = childNodes.get(j).getPrefix();
				if(!"".equals(prefix)){
					for(ForumCodeFile forumCodeFile : forumCodeFileList){
						String regular = "^"+prefix+".*";//正则
						if(Pattern.matches(regular, forumCodeFile.getFileName()+".html")){
							count++;
							//版块代码文件
							ForumCodeNode forumCodeNode = new ForumCodeNode();
							forumCodeNode.setNodeId(Integer.parseInt("99000"+count));
							forumCodeNode.setNodeName(forumCodeFile.getFileName());
							forumCodeNode.setRemark(forumCodeFile.getRemark());
							forumCodeNode.setPc_lastTime(forumCodeFile.getPc_lastTime());
							forumCodeNode.setWap_lastTime(forumCodeFile.getWap_lastTime());
							two_ForumCodeNode.addChildNode(forumCodeNode);
						}
					}
				}
			}
		}
		return forumCodeNodeList;
	}
	
	/**
	 * 根据二级节点查询版块代码文件
	 * @param dirName 目录
	 * @param childNodeName 二级节点名称
	 * @return
	 */
	public List<ForumCodeNode> getForumCodeNode(String dirName,String childNodeName){
		List<ForumCodeNode> forumCodeNodeList = this.forumCodeNodeList(dirName);
		for(int i = 0; i<forumCodeNodeList.size(); i++){
			//二级节点
			List<ForumCodeNode> childNodes = forumCodeNodeList.get(i).getChildNode();
			for(int j = 0; j< childNodes.size();j++){
				ForumCodeNode two_ForumCodeNode = childNodes.get(j);
				if(two_ForumCodeNode.getNodeName().equals(childNodeName)){
					return two_ForumCodeNode.getChildNode();
				}
			}
		}
		return null;
	}

	/**
	 * 读取版块代码文件信息
	 * @param dirName 目录
	 * @return
	 */
	public List<ForumCodeFile> readForumCodeFile(String dirName){
		
		List<ForumCodeFile> forumCodeFileList = new ArrayList<ForumCodeFile>();
		
		String pc_path = PathUtil.path()+File.separator+"WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"pc"+File.separator+"forum"+File.separator;
		String wap_path = PathUtil.path()+File.separator+"WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator+"wap"+File.separator+"forum"+File.separator;
				
		
		
		File pc_file=new File(pc_path);
		File[] pc_files=pc_file.listFiles();
		if(pc_files != null && pc_files.length >0){
			for(int i=0;i<pc_files.length;i++){
				if(pc_files[i].isFile()&&(!pc_files[i].isHidden())){//判断是否是文件并不能是隐藏文件
					String value = this.forumCodeRemark(pc_file+File.separator+pc_files[i].getName());
					
			        String fileName = pc_files[i].getName();//文件名带后缀
			        String suffix = fileManage.getExtension(fileName);//文件后缀
			        if(suffix != null && "html".equals(suffix)){
			        	ForumCodeFile forumCodeFile = new ForumCodeFile();
		        		
		        		fileName = fileName.substring(0,fileName.length() - 5);//删除后缀
		        		
		        		forumCodeFile.setFileName(fileName);
		        		forumCodeFile.setRemark(value);
		        		forumCodeFile.setPc_lastTime(new Date(pc_files[i].lastModified()));
		        		forumCodeFileList.add(forumCodeFile);
			        }
			    }
			 }
		}
		
		File wap_file=new File(wap_path);
		File[] wap_files=wap_file.listFiles();
		if(wap_files != null && wap_files.length >0){
			for(int i=0;i<wap_files.length;i++){
				if(wap_files[i].isFile()&&(!wap_files[i].isHidden())){//判断是否是文件并不能是隐藏文件
					
			        String fileName = wap_files[i].getName();//文件名带后缀
			        String suffix = fileManage.getExtension(fileName);//文件后缀
			        if(suffix != null && "html".equals(suffix)){
		        		fileName = fileName.substring(0,fileName.length() - 5);//删除后缀
		        		for(ForumCodeFile forumCodeFile : forumCodeFileList){
		        			if(forumCodeFile.getFileName().equals(fileName)){
		        				forumCodeFile.setWap_lastTime(new Date(wap_files[i].lastModified()));
		        				break;
		        			}
		        		}
		        		
			        }
			    }
			 }
		}
		
		
		 return forumCodeFileList;
	}

	/**
	 * 读取版块代码备注
	 * @param path 文件路径
	 * @return
	 */
	public String forumCodeRemark(String path){
		String tempString = "";
		BufferedReader reader = null;
        try {
        	reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8")); 
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
               break;
            }
            reader.close();
        } catch (IOException e) {
         //   e.printStackTrace();
        	if (logger.isErrorEnabled()) {
	            logger.error("读取版块代码备注",e);
	        }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                	if (logger.isErrorEnabled()) {
        	            logger.error("读取版块代码备注",e1);
        	        }
                }
            }
        }
        String value = "";
        int start=0;//注释开始
        int end=0; //注释结束
        if(tempString != null && !"".equals(tempString.trim())){
        	if((start=tempString.indexOf("<#--")) != -1 && (end=tempString.indexOf("-->")) != -1){
            	//输出中间文字   
            	value = tempString.substring(start+4,end);
            }
        }

		return value;
	}
	
	/**  
     * 读取模板文件内容  
     * @param filePath 路径
     * @param content 内容
     * @return  
     */  
    public String read(String filePath,String content) {   
        BufferedReader br = null;   
        String line = null;   
        StringBuffer buf = new StringBuffer();   
        int count = 0;
        try {   
            // 根据文件路径创建缓冲输入流    
        	br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));  
            // 循环读取文件的每一行, 对需要修改的行进行修改, 放入缓冲对象中   
            while ((line = br.readLine()) != null) {   
            	count++;
        	   	if(count == 1){//读取第一行
        		   	if(line != null && !"".equals(line)){
        			   	if((line.indexOf("<#--")) != -1 && (line.indexOf("-->")) != -1){
        				   	buf.append( "<#--"+content+"-->");
        			   	}else{
        			   		buf.append( "<#--"+content+"-->");
        			   		buf.append(System.getProperty("line.separator")); //换行
        			   		buf.append(line);  
        			   	}
        		   	}
        	   	}else {   // 如果不用修改, 则按原来的内容回写   
        	   		buf.append(line);   
               	}   
               	buf.append(System.getProperty("line.separator"));   
            }   
            if(count == 0){//如果文件没有任何内容
            	buf.append( "<#--"+content+"-->");
            }
        } catch (Exception e) {   
        //    e.printStackTrace(); 
        	if (logger.isErrorEnabled()) {
	            logger.error("读取模板文件内容",e);
	        }
        } finally {   
            // 关闭流   
            if (br != null) {   
                try {   
                    br.close(); 
                    br = null;     
                } catch (IOException e) {  
                    if (logger.isErrorEnabled()) {
        	            logger.error("读取模板文件内容",e);
        	        }
                }   
            }   
        }   
           
        return buf.toString();   
    }   
       
     

}
