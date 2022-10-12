package cms.web.action.upgrade.impl;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import cms.bean.QueryResult;
import cms.bean.data.TableInfoObject;
import cms.bean.upgrade.UpgradeLog;
import cms.bean.upgrade.UpgradeSystem;
import cms.bean.user.DisableUserName;
import cms.bean.user.User;
import cms.service.data.impl.MySqlDataServiceBean;
import cms.service.upgrade.UpgradeService;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.SpringConfigTool;
import cms.web.action.fileSystem.localImpl.LocalFileManage;
import cms.web.action.upgrade.UpgradeManage;
/**
 * 5.7升级到5.8版本执行程序
 *
 */
public class Upgrade5_7to5_8 {
	private static final Logger logger = LogManager.getLogger(Upgrade5_7to5_8.class);
	
	/**
	 * 运行
	 * @param upgradeId 升级Id
	 */
    public static void run(String upgradeId){
    	UpgradeService upgradeService = (UpgradeService)SpringConfigTool.getContext().getBean("upgradeServiceBean");
    	UpgradeManage upgradeManage = (UpgradeManage)SpringConfigTool.getContext().getBean("upgradeManage");
    	LocalFileManage localFileManage = (LocalFileManage)SpringConfigTool.getContext().getBean("localFileManage");
    	for(int i =0; i< 100; i++){
    		upgradeManage.taskRunMark_delete();
			upgradeManage.taskRunMark_add(1L);
    		
    		UpgradeSystem upgradeSystem = upgradeService.findUpgradeSystemById(upgradeId);
    		if(upgradeSystem == null || upgradeSystem.getRunningStatus().equals(9999)){
    			break;
    		}
    		if(upgradeSystem.getRunningStatus()>=100 && upgradeSystem.getRunningStatus()<200){
    			updateSQL_templates_verifyCSRF(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改模板表字段(templates)字段成功",1))+",");
    			
    			
    			insertSQL_templates(upgradeService);
				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表templates插入SQL成功",1))+",");
				
				
				insertSQL_forum(upgradeService);
				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表forum插入SQL成功",1))+",");
				
				insertSQL_layout(upgradeService);
				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表layout插入SQL成功",1))+",");
				
				
    			//更改运行状态
				upgradeService.updateRunningStatus(upgradeId ,200,JsonUtils.toJSONString(new UpgradeLog(new Date(),"升级流程结束",1))+",");

    		}
    		
    		
    		if(upgradeSystem.getRunningStatus()>=200 && upgradeSystem.getRunningStatus()<9999){
    			//更改运行状态
				upgradeService.updateRunningStatus(upgradeId ,9999,JsonUtils.toJSONString(new UpgradeLog(new Date(),"升级完成",1))+",");
				//写入当前BBS版本
				FileUtil.writeStringToFile("WEB-INF"+File.separator+"data"+File.separator+"systemVersion.txt",upgradeSystem.getId(),"utf-8",false);
				
				//临时目录路径
				String temp_path = "WEB-INF"+File.separator+"data"+File.separator+"temp"+File.separator+"upgrade"+File.separator;
				//删除临时文件夹
				localFileManage.removeDirectory(temp_path+upgradeSystem.getUpdatePackageFirstDirectory()+File.separator);
				
    		}
    		
    		
    		
    		
    	}
    	upgradeManage.taskRunMark_delete();
    }

    
    /**
  	 * 插入升级SQL
  	 * @param upgradeService
  	 */
    private static void insertSQL_templates(UpgradeService upgradeService){
    	String sql = "INSERT INTO `templates` (`id`,`columns`,`dirName`,`introduction`,`name`,`thumbnailSuffix`,`uses`,`verifyCSRF`) VALUES (NULL,'[{\"id\":1,\"name\":\"首页\",\"parentId\":0,\"childColumn\":[],\"sort\":100,\"linkMode\":3,\"url\":\"index\"},{\"id\":2,\"name\":\"问答\",\"parentId\":0,\"childColumn\":[],\"sort\":75,\"linkMode\":3,\"url\":\"askList\"},{\"id\":3,\"name\":\"会员卡\",\"parentId\":0,\"childColumn\":[],\"sort\":50,\"linkMode\":3,\"url\":\"membershipCardList\"},{\"id\":4,\"name\":\"帮助中心\",\"parentId\":0,\"childColumn\":[],\"sort\":30,\"linkMode\":3,\"url\":\"help\"}]','api','前后端分离模板，本模板仅提供API接口,前端代码需要另外部署','前后端分离模板','jpg',b'0',b'0')";
    			upgradeService.insertNativeSQL(sql);
    }
    /**
   	 * 插入升级SQL
   	 * @param upgradeService
   	 */
    private static void insertSQL_forum(UpgradeService upgradeService){
    	
       	String sql = "INSERT INTO `forum` (`id`,`dirName`,`displayType`,`formValue`,`forumChildType`,`forumType`,`invokeMethod`,`layoutFile`,`layoutId`,`layoutType`,`module`,`name`,`queryMode`,`referenceCode`) VALUES (NULL,'api','collection',NULL,'站点栏目列表','站点栏目',1,NULL,'139c10a2b22b43f38aa2b8680be5cf2c',4,'columnRelated_column_collection_default','站点栏目',0,'columnRelated_column_1'),(NULL,'api','entityBean',NULL,'粉丝总数','关注',1,NULL,'9283ad4a07a343828c145200f1fd272c',4,'followRelated_followerCount_entityBean_default','粉丝总数',0,'followRelated_followerCount_1'),(NULL,'api','entityBean',NULL,'是否已经关注该用户','关注',1,NULL,'9b78793395bd415fa6349d889226089c',4,'followRelated_following_entityBean_default','查询是否已关注该用户',0,'followRelated_following_1'),(NULL,'api','collection',NULL,'第三方登录','系统',1,NULL,'f119cd0e918742838000f096ec42358d',4,'systemRelated_thirdPartyLogin_collection_default','第三方登录',0,'systemRelated_thirdPartyLogin_1'),(NULL,'api','entityBean',NULL,'话题内容','话题',1,NULL,'afd68d68b7e4405ca555fa1fafb3681e',4,'topicRelated_topicContent_entityBean_default','话题内容',0,'topicRelated_topicContent_1'),(NULL,'api','entityBean',NULL,'问题内容','问答',1,NULL,'babfe2e086b34463933149b07932e265',4,'questionRelated_questionContent_entityBean_default','查询问题内容',0,'questionRelated_questionContent_1'),(NULL,'api','entityBean','{\"html_id\":\"d314eabc722044feae325de61d9199ac\",\"html_content\":\"欢迎您注册成为本站用户！<br />\\r\\n请仔细阅读下面的协议，只有接受协议才能继续进行注册。 <br />\\r\\n1．服务条款的确认和接纳<br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp; 本站用户服务的所有权和运作权归本站拥有。"
       			+ "本站所提供的服务将按照有关章程、服务条款和操作规则严格执行。用户通过注册程序点击“我同意” 按钮，即表示用户与本站达成协议并接受所有的服务条款。<br />\\r\\n2． 本站服务简介<br />\\r\\n&nbsp; &nbsp; &nbsp; 本站通过国际互联网为用户提供新闻及文章浏览、图片浏览、软件下载、网上留言和BBS论坛等服务。<br />\\r\\n&nbsp; &nbsp; &nbsp; 用户必须： <br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;1)购置设备，包括个人电脑一台、调制解调器一个及配备上网装置。 <br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;2)个人上网和支付与此服务有关的电话费用、网络费用。<br />\\r\\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &emsp;用户同意： <br />\\r\\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &emsp;1)提供及时、详尽及准确的个人资料。 <br />\\r\\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &emsp;2)不断更新注册资料，符合及时、详尽、准确的要求。所有原始键入的资料将引用为注册资料。 <br />\\r\\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &emsp;3)用户同意遵守《中华人民共和国保守国家秘密法》、《中华人民共和国计算机信息系统安全保护条例》、《计算机软件保护条例》等有关计算机及互联网规定的法律和法规、实施办法。在任何情况下，本站合理地认为用户的行为可能违反上述法律、法规，本站可以在任何时候，不经事先通知终止向该用户提供服务。用户应了解国际互联网的无国界性，应特别注意遵守当地所有有关的法律和法规。<br />\\r\\n3． 服务条款的修改<br />\\r\\n&nbsp; &nbsp; &nbsp; 本站会不定时地修改服务条款，服务条款一旦发生变动，将会在相关页面上提示修改内容。如果您同意改动，则再一次点击“我同意”按钮。 如果您不接受，则及时取消您的用户使用服务资格。<br />\\r\\n4． 服务修订<br />\\r\\n&nbsp; &nbsp; &nbsp; 本站保留随时修改或中断服务而不需知照用户的权利。本站行使修改或中断服务的权利，不需对用户或第三方负责。<br />\\r\\n5． 用户隐私制"
       			+ "度<br />\\r\\n&nbsp; &nbsp; &nbsp; 尊重用户个人隐私是本站的基本政策。本站不会公开、编辑或透露用户的注册信息，除非有法律许可要求，或本站在诚信的基础上认为透露这些信息在以下三种情况是必要的： <br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 1)遵守有关法律规定，遵从合法服务程序。 <br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 2)保持维护本站的商标所有权。 <br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 3)在紧急情况下竭力维护用户个人和社会大众的隐私安全。 <br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 4)符合其他相关的要求。 <br />\\r\\n6．用户的帐号，密码和安全性<br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp;一旦注册成功成为本站用户，您将得到一个密码和帐号。如果您不保管好自己的帐号和密码安全，将对因此产生的后果负全部责任。另外，每个用户都要对其帐户中的所有活动和事件负全责。您可随时根据指示改变您的密码，也可以结束旧的帐户重开一个新帐户。用户同意若发现任何非法使用用户帐号或安全漏洞的情况，立即通知本站。<br />\\r\\n7． 免责条款<br />\\r\\n&nbsp; &nbsp; &nbsp; 用户明确同意网站服务的使用由用户个人承担风险。 &emsp;&emsp; <br />\\r\\n&nbsp; &nbsp; &nbsp; 本站不作任何类型的担保，不担保服务一定能满足用户的要求，也不担保服务不会受中断，对服务的及时性，安全性，出错发生都不作担保。用户理解并接受：任何通过本站服务取得的信息资料的可靠性取决于用户自己，用户自己承担所有风险和责任。 <br />\\r\\n8．有限责任<br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp;本站对任何直接、间接、偶然、特殊及继起的损害不负责任。<br />\\r\\n9． 不提供零售和商业性服务 <br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp;用户使用网站服务的权利是个人的。用户只能是一个单独的个体而不能是一个公司或实体商业性组织。用户承诺不经本站同意，不能利用网站服务进行销售或其他商业用途。<br />\\r\\n10．用户责任 <br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp;用户单独承担传输内容的责任。用户必须遵循： <br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp;1)从中国境内向外传输技术性资料时必须符合中国有关法规。 <br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp;2)使用网站服务不作非法用途。 <br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp;3)不干"
       			+ "扰或混乱网络服务。 <br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp;4)不在论坛BBS或留言簿发表任何与政治相关的信息。 <br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp;5)遵守所有使用网站服务的网络协议、规定、程序和惯例。<br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp;6)不得利用本站危害国家安全、泄露国家秘密，不得侵犯国家社会集体的和公民的合法权益。<br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp;7)不得利用本站制作、复制和传播下列信息： <br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;1、煽动抗拒、破坏宪法和法律、行政法规实施的；<br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;2、煽动颠覆国家政权，推翻社会主义制度的；<br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;3、煽动分裂国家、破坏国家统一的；<br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;4、煽动民族仇恨、民族歧视，破坏民族团结的；<br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;5、捏造或者歪曲事实，散布谣言，扰乱社会秩序的；<br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;6、宣扬封建迷信、淫秽、色情、赌博、暴力、凶杀、恐怖、教唆犯罪的；<br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;7、公然侮辱他人或者捏造事实诽谤他人的，或者进行其他恶意攻击的；<br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;8、损害国家机关信誉的；<br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;9、其他违反宪法和法律行政法规的；<br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;10、进行商业广告行为的。<br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp;用户不能传输任何教唆他人构成犯罪行为的资料；不能传输长国内不利条件和涉及国家安全的资料；不能传输任何不符合当地法规、国家法律和国际法律的资料。未经许可而非法进入其它电脑系统是禁止的。若用户的行为不符合以上的条款，本站将取消用户服务帐号。<br />\\r\\n11．网站内容的所有权<br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp;本站定义的内容包括：文字、软件、声音、相片、录象、图表；在广告中全部内容；电子邮件的全部内容；本站为用户提供的商业信息。所有这些内容受版权、商标、标签和其它财产所有权法律的保护。所以，用户只能在本站和广告商授权下才能使用这些内容，而不能擅自复制、篡改这些内容、或创造与内容有关的派生产品。<br />\\r\\n12．附"
       			+ "加信息服务<br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp;用户在享用本站提供的免费服务的同时，同意接受本站提供的各类附加信息服务。<br />\\r\\n13．解释权<br />\\r\\n&nbsp; &nbsp; &nbsp; &nbsp;本注册协议的解释权归本站所有。如果其中有任何条款与国家的有关法律相抵触，则以国家法律的明文规定为准。\"}','用户自定义HTML','自定义版块',1,NULL,'fd72dab269d94795ac112b59364017ed',4,'customForumRelated_customHTML_entityBean_default','服务条款',0,'customForumRelated_customHTML_1'),(NULL,'api','collection',NULL,'标签列表','话题',1,NULL,'a4fe0c6f3c2a4b1eb776065c702781b3',4,'topicRelated_tag_collection_default','全部标签',0,'topicRelated_tag_1'),(NULL,'api','page','{\"topic_id\":\"8ccee10d032141a28336d5860f7db116\",\"topic_quantity\":null,\"topic_sort\":1,\"topic_more\":null,\"topic_moreValue\":null,\"topic_maxResult\":30,\"topic_pageCount\":null,\"topic_tagId\":null,\"topic_tagName\":null,\"topic_tag_transferPrameter\":true,\"topic_recommendTopicList\":{}}','话题列表','话题',1,NULL,'eb22678d375740a78e11c5cbcb24be0d',4,'topicRelated_topic_page_default','话题分页',0,'topicRelated_topic_1'),(NULL,'api','collection',NULL,'友情链接列表','友情链接',1,NULL,'8c25652471eb476b8f293f7bb20632ca',4,'linksRelated_links_collection_default','友情链接列表',0,'linksRelated_links_1'),(NULL,'api','collection',NULL,'添加话题','话题',1,NULL,'e8126fd3b06645b1963d0dc7a6f96e65',4,'topicRelated_addTopic_collection_default','发表话题',0,'topicRelated_addTopic_1'),(NULL,'api','entityBean',NULL,'话题会员收藏总数','收藏夹',1,NULL,'a920fd623ff74d7b97f25378c3567f0c',4,'favoriteRelated_favoriteCount_entityBean_default','话题会员收藏总数',0,'favoriteRelated_favoriteCount_1'),(NULL,'api','entityBean',NULL,'用户是否已经收藏话题','收藏夹',1,NULL,'c22f985f3d0a436ba0b5c6479a121f5b',4,'favoriteRelated_alreadyCollected_entityBean_default','用户是否已收藏话题',0,'favoriteRelated_alreadyCollected_1'),(NULL,'api','entityBean',NULL,'话题点赞总数','点赞',1,NULL,'baecca44ee9b4b3bb6f6cada63b70389',4,'likeRelated_likeCount_entityBean_default','话题点赞总数',0,'likeRelated_likeCount_1'),(NULL,'api','entityBean',NULL,'用户是否已经点赞该话题','点赞',1,NULL,'f1e60813417c4c12840613da6b01726c',4,'likeRelated_alreadyLiked_entityBean_default','用户是否已点赞该话题',0,'likeRelated_alreadyLiked_1'),"
       			+ "(NULL,'api','collection',NULL,'修改话题','话题',1,NULL,'4ddaf163935e4e3cad6b3901cfb38d19',4,'topicRelated_editTopic_collection_default','修改话题',0,'topicRelated_editTopic_1'),(NULL,'api','page','{\"comment_id\":\"f949c71b2a914b12b147d8254a528eec\",\"comment_maxResult\":30,\"comment_pageCount\":null,\"comment_sort\":2}','评论列表','话题',1,NULL,'fbbbf2ec6fbe43f79b489b8cce26f2a6',4,'topicRelated_comment_page_default','评论列表',0,'topicRelated_comment_1'),(NULL,'api','collection',NULL,'添加评论','话题',1,NULL,'9954217a647445648bf46666c826bed4',4,'topicRelated_addComment_collection_default','添加评论',0,'topicRelated_addComment_1'),(NULL,'api','collection',NULL,'修改评论','话题',1,NULL,'4888f0f582df41c68c7bc98deba72711',4,'topicRelated_editComment_collection_default','修改评论',0,'topicRelated_editComment_1'),(NULL,'api','collection',NULL,'引用评论','话题',1,NULL,'6f06fcbed64144d4a0a87d68e5e1ac5a',4,'topicRelated_quoteComment_collection_default','引用评论',0,'topicRelated_quoteComment_1'),(NULL,'api','collection',NULL,'回复评论','话题',1,NULL,'fb7526585c4d4ffa9e19a5bbfcadd730',4,'topicRelated_replyComment_collection_default','回复评论',0,'topicRelated_replyComment_1'),(NULL,'api','collection',NULL,'修改评论回复','话题',1,NULL,'b3ce59d4049a4a609a70480681b3d119',4,'topicRelated_editReply_collection_default','修改评论回复',0,'topicRelated_editReply_1'),(NULL,'api','entityBean',NULL,'回答总数','问答',1,NULL,'ea282240f21c46688de3e70a6a9e1925',4,'questionRelated_answerCount_entityBean_default','回答总数',0,'questionRelated_answerCount_1'),(NULL,'api','entityBean',NULL,'关注总数','关注',1,NULL,'80b64ce95e2b41848c8dfe13e76f6c0d',4,'followRelated_followCount_entityBean_default','关注总数',0,'followRelated_followCount_1'),(NULL,'api','entityBean',NULL,'发红包内容','红包',1,NULL,'d738e25ac96e4e74812cdf63a639352e',4,'redEnvelopeRelated_giveRedEnvelopeContent_entityBean_default','发红包内容',0,'redEnvelopeRelated_giveRedEnvelopeContent_1'),"
       			+ "(NULL,'api','page','{\"receiveRedEnvelopeUser_id\":\"4a3b6942052a48618b783f1434a51d2b\",\"receiveRedEnvelopeUser_maxResult\":13,\"receiveRedEnvelopeUser_sort\":10}','领取红包用户列表','红包',1,NULL,'c5d387ee29764c2a829278a17c629e17',4,'redEnvelopeRelated_receiveRedEnvelopeUser_page_default','领取红包用户列表',0,'redEnvelopeRelated_receiveRedEnvelopeUser_1'),(NULL,'api','collection','{\"likeTopic_id\":\"53b696d99f9846f3949632b426238ec4\",\"likeTopic_maxResult\":15}','相似话题','话题',1,NULL,'bfb54b63a24c4c9c91775991d0f53dcc',4,'topicRelated_likeTopic_collection_default','相似话题',0,'topicRelated_likeTopic_1'),(NULL,'api','collection',NULL,'问题标签列表','问答',1,NULL,'2eb34391373643b59430301b390d87f8',4,'questionRelated_questionTag_collection_default','问题标签',0,'questionRelated_questionTag_1'),(NULL,'api','page','{\"question_id\":\"d3fdb4ae0d114a0d90180200eb78b626\",\"question_quantity\":null,\"question_sort\":1,\"question_more\":null,\"question_moreValue\":null,\"question_maxResult\":30,\"question_pageCount\":null,\"question_tagId\":null,\"question_tagName\":null,\"question_tag_transferPrameter\":true,\"question_filterCondition\":null,\"question_filterCondition_transferPrameter\":true,\"question_recommendQuestionList\":{}}','问题列表','问答',1,NULL,'f7676ca40900427e821f2999185d1ca0',4,'questionRelated_question_page_default','问题列表',0,'questionRelated_question_1'),(NULL,'api','collection',NULL,'添加问题','问答',1,NULL,'f7db737de89b410d8416f271247a3cad',4,'questionRelated_addQuestion_collection_default','添加问题',0,'questionRelated_addQuestion_1'),(NULL,'api','collection',NULL,'追加问题','问答',1,NULL,'d8a30fd190ad4fd1a89a5d8f9c7df573',4,'questionRelated_appendQuestion_collection_default','追加问题',0,'questionRelated_appendQuestion_1'),(NULL,'api','entityBean',NULL,'用户是否已经收藏问题','收藏夹',1,NULL,'2286b0ba46bc4ca29d1528b9832d115b',4,'favoriteRelated_alreadyFavoriteQuestion_entityBean_default','用户是否已经收藏该问题',0,'favoriteRelated_alreadyFavoriteQuestion_1'),"
       			+ "(NULL,'api','entityBean',NULL,'问题会员收藏总数','收藏夹',1,NULL,'b017258bf1b244ed8a86d24eda1ad3bf',4,'favoriteRelated_questionFavoriteCount_entityBean_default','问题会员收藏总数',0,'favoriteRelated_questionFavoriteCount_1'),(NULL,'api','collection',NULL,'添加答案','问答',1,NULL,'9a4c7ba616db4c1489a5ab3b3587ed12',4,'questionRelated_addAnswer_collection_default','添加答案',0,'questionRelated_addAnswer_1'),(NULL,'api','page','{\"answer_id\":\"afc349e6f5d9418a95b8ad0e379b97c4\",\"answer_maxResult\":30,\"answer_pageCount\":null,\"answer_sort\":2}','答案列表','问答',1,NULL,'46e93b23a0fe49eebaafe1c8c4705238',4,'questionRelated_answer_page_default','答案列表',0,'questionRelated_answer_1'),(NULL,'api','collection',NULL,'回复答案','问答',1,NULL,'a8e08924952e4501ae1c19766487bfb3',4,'questionRelated_replyAnswer_collection_default','回复答案',0,'questionRelated_replyAnswer_1'),(NULL,'api','collection',NULL,'修改答案','问答',1,NULL,'890eb44b2c2d4b9db90955951a0123a4',4,'questionRelated_editAnswer_collection_default','修改答案',0,'questionRelated_editAnswer_1'),(NULL,'api','collection',NULL,'修改答案回复','问答',1,NULL,'6ab3b63990bd43bd92c16b2373a09c8a',4,'questionRelated_editReply_collection_default','修改答案回复',0,'questionRelated_editReply_1'),(NULL,'api','collection','{\"likeQuestion_id\":\"886271ee491f47db8bf169e4101ade02\",\"likeQuestion_maxResult\":15}','相似问题','问答',1,NULL,'b3211fc921684203a0b5e5960e140274',4,'questionRelated_likeQuestion_collection_default','相似问题',0,'questionRelated_likeQuestion_1'),(NULL,'api','collection',NULL,'会员卡列表','会员卡',1,NULL,'ba307b042784455aa64cf886d0fce20e',4,'membershipCardRelated_membershipCard_collection_default','会员卡列表',0,'membershipCardRelated_membershipCard_1'),(NULL,'api','entityBean',NULL,'会员卡内容','会员卡',1,NULL,'27a1ac573f9546ddb811417129d6ab52',4,'membershipCardRelated_membershipCardContent_entityBean_default','会员卡内容',0,'membershipCardRelated_membershipCardContent_1'),(NULL,'api','collection',NULL,'在线帮助分类','在线帮助',1,NULL,'554557f4f99f484eb678ba20fa451073',4,'helpRelated_helpType_collection_default','在线帮助分类',0,'helpRelated_helpType_1'),"
       			+ "(NULL,'api','collection',NULL,'在线帮助导航','在线帮助',1,NULL,'6b34109ea214408d9a215fab47b47016',4,'helpRelated_helpNavigation_collection_default','在线帮助导航',0,'helpRelated_helpNavigation_1'),(NULL,'api','collection',NULL,'在线帮助列表','在线帮助',1,NULL,'030d51bc3c7a4f85a9a8b27f59b8dc91',4,'helpRelated_help_collection_default','在线帮助列表',0,'helpRelated_help_1'),(NULL,'api','entityBean',NULL,'在线帮助内容','在线帮助',1,NULL,'aca9f3a710f14f61934c49c257d35002',4,'helpRelated_helpContent_entityBean_default','在线帮助内容',0,'helpRelated_helpContent_1'),(NULL,'api','collection',NULL,'添加在线留言','在线留言',1,NULL,'4c0ff429df524417999b1bdcd2e34549',4,'feedbackRelated_addFeedback_collection_default','添加在线留言',0,'feedbackRelated_addFeedback_1');";
       	upgradeService.insertNativeSQL(sql);
       	
       	
    
    }
   	
   /**
  	 * 插入升级SQL
  	 * 将单引号替换为两个单引号
  	 * @param upgradeService
  	 */
    private static void insertSQL_layout(UpgradeService upgradeService){
    	String sql = "INSERT INTO `layout` (`id`,`dirName`,`forumData`,`layoutFile`,`name`,`referenceCode`,`returnData`,`sort`,`type`,`accessRequireLogin`) VALUES ('0146a94f857542fa8868706d43712cf0','api',-1,'followerList.html','粉丝列表','user/control/followerList',0,1300,1,b'0'),('0237f9d88e6d42b88339832be56da0b2','api',-1,'giveRedEnvelopeList.html','发红包列表','user/control/giveRedEnvelopeList',0,2200,1,b'0'),('030d51bc3c7a4f85a9a8b27f59b8dc91','api',-1,NULL,'查询帮助列表','queryHelpList',1,2840,4,b'0'),('03d87267e62e45348c5be7b6a7d12907','api',-1,'paymentCompleted.html','付款完成页面','paymentCompleted',0,1600,1,b'0'),('0539cfc710694d57abbd55f40836d853','api',-1,'payment.html','付款页面','user/control/payment',0,1500,1,b'0'),('05c03df92de74379bd46f800a9861625','api',-1,'userDynamicList.html','用户动态列表','user/control/userDynamicList',0,900,1,b'0'),('0d062401879945608b9abdd30670db64','api',-1,'replyList.html','我的回复','user/control/replyList',0,180,1,b'0'),('0de585bdbffa4862bcd3342c6361b928','api',-1,'userLoginLogList.html','用户登录日志列表','user/control/userLoginLogList',0,310,1,b'0'),('139c10a2b22b43f38aa2b8680be5cf2c','api',-1,NULL,'栏目列表','columnList',1,2410,4,b'0'),('1768830e9027429ba66d7ac824128fd7','api',-1,'findPassWord_step1.html','找回密码第一步','findPassWord/step1',0,136,1,b'0'),('1a03c7d8ad84478ea63187f1fbd8ebc7','api',-1,'register.html','注册页','register',0,130,1,b'0'),('1a22c5bcf0db4a229c3d3db8e1b96e53','api',-1,'updatePhoneBinding_step2.html','更换绑定手机第二步','user/control/updatePhoneBinding/step2',0,277,1,b'0'),('1aae84f9973743078b7da1914d4117cf','api',-1,'systemNotifyList.html','系统通知列表','user/control/systemNotifyList',0,500,1,b'0'),('2286b0ba46bc4ca29d1528b9832d115b','api',-1,NULL,'查询用户是否已经收藏该问题','queryAlreadyFavoriteQuestion',1,2720,4,b'0'),('24475e3169834022a2e9da4d55a30614','api',-1,'balance.html','余额','user/control/balance',0,1400,1,b'0'),('27a1ac573f9546ddb811417129d6ab52','api',-1,NULL,'查询会员卡','queryMembershipCard',1,2810,4,b'0'),('27ce9c5cba6b477882cddf32d3c4046e','api',-1,'privateMessageList.html','私信列表','user/control/privateMessageList',0,350,1,b'0'),"
    			+ "('29381b0502b24fb5838f52a96a7b9c7a','api',-1,'home.html','用户中心页','user/control/home',0,170,1,b'0'),('2eb34391373643b59430301b390d87f8','api',-1,NULL,'查询所有问题标签','queryAllQuestionTag',1,2680,4,b'0'),('3736da6867314018be676c56a28a582a','api',-1,'index.html','首页','index',0,10,1,b'0'),('3e03f55f72804dde9a9874d94f15d7d5','api',-1,'answerList.html','我的答案','user/control/answerList',0,1900,1,b'0'),('417ea9d80bf3488aa312cc1bb8c6a3f9','api',-1,'topicFavoriteList.html','话题收藏列表','user/control/topicFavoriteList',0,700,1,b'0'),('46e93b23a0fe49eebaafe1c8c4705238','api',-1,NULL,'查询答案列表','queryAnswerList',1,2750,4,b'0'),('4888f0f582df41c68c7bc98deba72711','api',-1,NULL,'查询修改评论表单','user/queryEditComment',1,2590,4,b'0'),('4908dc43828d484d80cc1235b7f1c572','api',-1,'topicLikeList.html','话题点赞列表','user/control/topicLikeList',0,1100,1,b'0'),('4c0ff429df524417999b1bdcd2e34549','api',-1,NULL,'查询添加在线留言表单','queryAddFeedback',1,2860,4,b'0'),('4dd50167d54b43f48089e1fc433baff7','api',3,'more_help.html','在线帮助''更多''','more',0,60,1,b'0'),('4ddaf163935e4e3cad6b3901cfb38d19','api',-1,NULL,'修改话题','user/editTopic',1,2560,4,b'0'),('5174250d9377410085e2b66f9e6866f2','api',-1,'search.html','话题搜索页','search',0,30,1,b'0'),('51ff5c4c03634e1f9776916ff28e3f64','api',-1,'privateMessageChatList.html','私信对话列表','user/control/privateMessageChatList',0,400,1,b'0'),('554557f4f99f484eb678ba20fa451073','api',-1,NULL,'查询帮助分类','queryHelpTypeList',1,2820,4,b'0'),('57557f26652742b4bd2abe84ba24a054','api',-1,'addPrivateMessage.html','添加私信','user/control/addPrivateMessage',0,450,1,b'0'),('59963311443649299e1d86ae80c8e33c','api',-1,'answerReplyList.html','我的答案回复','user/control/answerReplyList',0,2000,1,b'0'),('5bb7f175bfb848dbb1fc8f1b0094a4f6','api',-1,'commentList.html','我的评论','user/control/commentList',0,180,1,b'0'),('68fabb2c63e942a48c0c8ff7c2dd99c3','api',-1,'questionFavoriteList.html','问题收藏列表','user/control/questionFavoriteList',0,2100,1,b'0'),('6ab3b63990bd43bd92c16b2373a09c8a','api',-1,NULL,'查询修改答案回复表单','user/queryEditAnswerReply',1,2780,4,b'0'),"
    			+ "('6b34109ea214408d9a215fab47b47016','api',-1,NULL,'查询帮助导航','queryHelpNavigation',1,2830,4,b'0'),('6f06fcbed64144d4a0a87d68e5e1ac5a','api',-1,NULL,'查询引用评论表单','user/queryQuoteComment',1,2600,4,b'0'),('704793a98bc241e7869a5e72c6fa3600','api',-1,'remindList.html','提醒列表','user/control/remindList',0,550,1,b'0'),('723dcb4988ab4d1eb1114f01c79f4402','api',-1,'realNameAuthentication.html','实名认证','user/control/realNameAuthentication',0,274,1,b'0'),('7c4b56d66c0e4b69ba3d8750ebff87f8','api',-1,'topicUnhideList.html','话题取消隐藏用户列表','user/control/topicUnhideList',0,800,1,b'0'),('80b64ce95e2b41848c8dfe13e76f6c0d','api',-1,NULL,'查询关注总数','queryFollowCount',1,2640,4,b'0'),('837ba035b57d4ff19358c0a10710313d','api',-1,'updatePhoneBinding_step1.html','更换绑定手机第一步','user/control/updatePhoneBinding/step1',0,276,1,b'0'),('890eb44b2c2d4b9db90955951a0123a4','api',-1,NULL,'查询修改答案表单','user/queryEditAnswer',1,2770,4,b'0'),('8c25652471eb476b8f293f7bb20632ca','api',-1,NULL,'查询友情链接','queryLink',1,2500,4,b'0'),('917e2578460641838e8b93355744b748','api',-1,'likeList.html','点赞列表','user/control/likeList',0,1000,1,b'0'),('9283ad4a07a343828c145200f1fd272c','api',-1,NULL,'查询粉丝总数','queryFollowerCount',1,2420,4,b'0'),('9954217a647445648bf46666c826bed4','api',-1,NULL,'查询添加评论表单','queryAddComment',1,2580,4,b'0'),('9a4c7ba616db4c1489a5ab3b3587ed12','api',-1,NULL,'查询添加答案表单','user/queryAddAnswer',1,2740,4,b'0'),('9b78793395bd415fa6349d889226089c','api',-1,NULL,'查询是否已经关注该用户','queryFollowing',1,2430,4,b'0'),('a4fe0c6f3c2a4b1eb776065c702781b3','api',-1,NULL,'全部标签','allTag',1,2480,4,b'0'),('a8e08924952e4501ae1c19766487bfb3','api',-1,NULL,'查询添加答案回复表单','user/queryAddAnswerReply',1,2760,4,b'0'),('a920fd623ff74d7b97f25378c3567f0c','api',-1,NULL,'查询话题会员收藏总数','queryFavoriteCount',1,2520,4,b'0'),('aca9f3a710f14f61934c49c257d35002','api',-1,NULL,'查询帮助','queryHelp',1,2850,4,b'0'),('afd68d68b7e4405ca555fa1fafb3681e','api',-1,NULL,'查询话题内容','thread',1,2450,4,b'0'),"
    			+ "('b017258bf1b244ed8a86d24eda1ad3bf','api',-1,NULL,'查询问题用户收藏总数','queryQuestionFavoriteCount',1,2730,4,b'0'),('b3211fc921684203a0b5e5960e140274','api',-1,NULL,'查询相似问题','queryLikeQuestion',1,2790,4,b'0'),('b3ce59d4049a4a609a70480681b3d119','api',-1,NULL,'查询话题修改回复表单','user/queryEditReply',1,2620,4,b'0'),('b7fb6d10945342d9be7366f0fe79426d','api',-1,'receiveRedEnvelopeList.html','收红包列表','user/control/receiveRedEnvelopeList',0,2400,1,b'0'),('b8aea98e976b45b0933c2e6c79e1b5aa','api',-1,'point.html','积分','user/control/point',0,220,1,b'0'),('b9070f6f929c4a7999d5c8aa2ac29b45','api',-1,'editUser.html','修改会员','user/control/editUser',0,270,1,b'0'),('b9cb01a67efc459b8ef3ba5c88e5ab6e','api',-1,'membershipCardOrderList.html','会员卡订单列表','user/control/membershipCardOrderList',0,1700,1,b'0'),('ba307b042784455aa64cf886d0fce20e','api',-1,NULL,'查询会员卡列表','queryMembershipCardList',1,2800,4,b'0'),('babfe2e086b34463933149b07932e265','api',-1,NULL,'查询问题内容','question',1,2460,4,b'0'),('baecca44ee9b4b3bb6f6cada63b70389','api',-1,NULL,'查询话题点赞总数','queryLikeCount',1,2540,4,b'0'),('bfb54b63a24c4c9c91775991d0f53dcc','api',-1,NULL,'查询相似话题','queryLikeTopic',1,2670,4,b'0'),('bfe17faef78d4971b14e598541ba3414','api',-1,'login.html','登录页','login',0,160,1,b'0'),('c22f985f3d0a436ba0b5c6479a121f5b','api',-1,NULL,'查询用户是否已收藏该话题','queryAlreadyCollected',1,2530,4,b'0'),('c53be46778cb48eb8971a18a4ee2680f','api',-1,'favoriteList.html','收藏夹列表','user/control/favoriteList',0,600,1,b'0'),('c5d387ee29764c2a829278a17c629e17','api',-1,NULL,'查询领取红包用户','queryReceiveRedEnvelopeUser',1,2660,4,b'0'),('c686755a48ca4ca191d7380545a9f56a','api',-1,'findPassWord_step2.html','找回密码第二步','findPassWord/step2',0,137,1,b'0'),('c9a85b0657264a5aac06cf7e6555d5ff','api',-1,'topicList.html','我的话题','user/control/topicList',0,180,1,b'0'),('cef07a74185940109969afa9f3899899','api',-1,'message.html','默认消息页','message',0,150,1,b'0'),('d5bad6d68fc24cafbbe2b8920c9d289c','api',-1,'questionList.html','我的问题','user/control/questionList',0,1800,1,b'0'),"
    			+ "('d738e25ac96e4e74812cdf63a639352e','api',-1,NULL,'查询发红包内容','queryGiveRedEnvelopeContent',1,2650,4,b'0'),('d8a30fd190ad4fd1a89a5d8f9c7df573','api',-1,NULL,'查询追加问题表单','user/queryAppendQuestion',1,2710,4,b'0'),('dbda9575f6e0415fa1bfee47f3301293','api',-1,'phoneBinding.html','绑定手机','user/control/phoneBinding',0,275,1,b'0'),('de0e6f72232547798f687a5ac54092d4','api',-1,'agreement.html','用户协议','agreement',0,135,1,b'0'),('e8126fd3b06645b1963d0dc7a6f96e65','api',-1,NULL,'查询添加话题表单','user/queryAddTopic',1,2510,4,b'0'),('ea282240f21c46688de3e70a6a9e1925','api',-1,NULL,'查询回答总数','queryAnswerCount',1,2630,4,b'0'),('eb22678d375740a78e11c5cbcb24be0d','api',-1,NULL,'话题列表','queryTopicList',1,2490,4,b'0'),('f119cd0e918742838000f096ec42358d','api',-1,NULL,'第三方登录','queryThirdPartyLogin',1,2440,4,b'0'),('f1e60813417c4c12840613da6b01726c','api',-1,NULL,'查询用户是否已经点赞该话题','queryAlreadyLiked',1,2550,4,b'0'),('f7676ca40900427e821f2999185d1ca0','api',-1,NULL,'问题列表','queryQuestionList',1,2690,4,b'0'),('f7db737de89b410d8416f271247a3cad','api',-1,NULL,'查询提问题表单','user/queryAddQuestion',1,2700,4,b'0'),('f7efe75775064ae496f9008e220ec3f5','api',-1,'redEnvelopeAmountDistributionList.html','发红包金额分配列表','user/control/redEnvelopeAmountDistributionList',0,2300,1,b'0'),('fad5de351ed7453d95f53e5b5efdf0dc','api',-1,'jump.html','默认跳转页','jump',0,140,1,b'0'),('fb7526585c4d4ffa9e19a5bbfcadd730','api',-1,NULL,'查询话题添加回复表单','user/queryAddReply',1,2610,4,b'0'),('fbbbf2ec6fbe43f79b489b8cce26f2a6','api',-1,NULL,'评论列表','queryCommentList',1,2570,4,b'0'),('fd72dab269d94795ac112b59364017ed','api',-1,NULL,'服务条款','queryTermsService',1,2470,4,b'0'),('ffd38d63c405486f800d29cc43f804db','api',-1,'followList.html','关注列表','user/control/followList',0,1200,1,b'0');";
        upgradeService.insertNativeSQL(sql);
        
        
        
    }  
    
    /**
   	 * 修改模板表字段,设置verifyCSRF字段默认值
   	 * @param upgradeService
   	 */
    private static void updateSQL_templates_verifyCSRF(UpgradeService upgradeService){
    	String sql = "UPDATE templates SET verifyCSRF=true;";
       	upgradeService.insertNativeSQL(sql);
    }

    
}
