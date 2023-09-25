<!-- 基本设置 -->
<template id="editSystemSetting-template">
	<div>
		<div class="main" >
			<div class="data-form label-width-blank systemSettingModule" >
				<el-form label-width="270px"  @submit.native.prevent>
					<el-tabs v-model="activeTag" type="card" >
					    <el-tab-pane label="基本设置" name="10"></el-tab-pane>
					    <el-tab-pane label="话题编辑器" name="20"></el-tab-pane>
					    <el-tab-pane label="评论编辑器" name="30"></el-tab-pane>
					    <el-tab-pane label="问题编辑器" name="40"></el-tab-pane>
					    <el-tab-pane label="答案编辑器" name="50"></el-tab-pane>
					</el-tabs>
				
					<el-form-item label="站点名称" :required="true" :error="error.title" v-show="activeTag == 10">
						<el-input v-model.trim="title" maxlength="200" clearable="true" show-word-limit></el-input></el-col>
					</el-form-item>
					<el-form-item label="站点关键词(keywords)" :error="error.keywords" v-show="activeTag == 10">
						<el-input v-model.trim="keywords" maxlength="200" clearable="true" show-word-limit></el-input></el-col>
					</el-form-item>
					<el-form-item label="站点描述(description)" :error="error.description" v-show="activeTag == 10">
						<el-input v-model.trim="description" maxlength="200" clearable="true" show-word-limit></el-input></el-col>
					</el-form-item>
					<el-form-item label="关闭站点" :error="error.closeSite" v-show="activeTag == 10">
						<el-radio-group v-model="closeSite">
						    <el-radio-button :label="1">打开</el-radio-button>
						    <el-radio-button :label="2">只读模式</el-radio-button>
						    <el-radio-button :label="3">全站关闭</el-radio-button>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="关闭站点提示信息" v-show="activeTag == 10 && closeSite ==3" :error="error.closeSitePrompt">
						<el-input type="textarea" v-model="closeSitePrompt" :autosize="{minRows: 4}" placeholder="请输入内容" ></el-input>
					</el-form-item>
					<el-form-item label="支持访问设备" :error="error.supportAccessDevice" v-show="activeTag == 10">
						<el-radio-group v-model="supportAccessDevice">
						    <el-radio :label="1">自动识别终端</el-radio>
						    <el-radio :label="2">电脑端</el-radio>
						    <el-radio :label="3">移动端</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="支持编辑器" :error="error.supportEditor" v-show="activeTag == 10">
						<el-radio-group v-model="supportEditor">
						    <el-radio :label="10">仅富文本编辑器</el-radio>
						    <el-radio :label="20" disabled>仅Markdown编辑器</el-radio>
						    <el-radio :label="30" disabled>富文本编辑器优先</el-radio>
						    <el-radio :label="40" disabled>Markdown编辑器优先</el-radio>
						</el-radio-group>
						<div class="form-help" >仅支持前后端分离模板</div>
					</el-form-item>
					<el-form-item label="允许注册账号类型" :error="error.allowRegisterAccount" v-show="activeTag == 10">
						<el-checkbox v-model="allowRegisterAccountObject.local">本地账号密码用户</el-checkbox>
						<el-checkbox v-model="allowRegisterAccountObject.mobile">手机用户</el-checkbox>
						<el-checkbox v-model="allowRegisterAccountObject.weChat">微信用户</el-checkbox>
					</el-form-item>
					<el-form-item label="注册是否需要验证码" :error="error.registerCaptcha" v-show="activeTag == 10">
						<el-radio-group v-model="registerCaptcha">
						    <el-radio :label="true">需要</el-radio>
						    <el-radio :label="false">不需要</el-radio>
						</el-radio-group>
					</el-form-item>
					
					 
					<el-form-item label="登录密码每分钟连续错误" :error="error.login_submitQuantity" v-show="activeTag == 10">
						<el-row >
							<el-col :span="6"><el-input v-model.trim="login_submitQuantity" :required="true" maxlength="8" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
						<div class="form-help" >0为每次都出现验证码</div>
					</el-form-item>
					<el-form-item label="发表话题每分钟提交超过" :error="error.topic_submitQuantity" v-show="activeTag == 10">
						<el-row >
							<el-col :span="6"><el-input v-model.trim="topic_submitQuantity" :required="true" maxlength="8" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
						<div class="form-help" >0为每次都出现验证码</div>
					</el-form-item>
					<el-form-item label="发表评论每分钟提交超过" :error="error.comment_submitQuantity" v-show="activeTag == 10">
						<el-row >
							<el-col :span="6"><el-input v-model.trim="comment_submitQuantity" :required="true" maxlength="8" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
						<div class="form-help" >0为每次都出现验证码</div>
					</el-form-item>
					<el-form-item label="提交问题每分钟提交超过" :error="error.question_submitQuantity" v-show="activeTag == 10">
						<el-row >
							<el-col :span="6"><el-input v-model.trim="question_submitQuantity" :required="true" maxlength="8" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
						<div class="form-help" >0为每次都出现验证码</div>
					</el-form-item>
					<el-form-item label="提交答案每分钟提交超过" :error="error.answer_submitQuantity" v-show="activeTag == 10">
						<el-row >
							<el-col :span="6"><el-input v-model.trim="answer_submitQuantity" :required="true" maxlength="8" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
						<div class="form-help" >0为每次都出现验证码</div>
					</el-form-item>
					<el-form-item label="发表私信每分钟提交超过" :error="error.privateMessage_submitQuantity" v-show="activeTag == 10">
						<el-row >
							<el-col :span="6"><el-input v-model.trim="privateMessage_submitQuantity" :required="true" maxlength="8" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
						<div class="form-help" >0为每次都出现验证码</div>
					</el-form-item>
					<el-form-item label="举报每分钟提交超过" :error="error.report_submitQuantity" v-show="activeTag == 10">
						<el-row >
							<el-col :span="6"><el-input v-model.trim="report_submitQuantity" :required="true" maxlength="8" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
						<div class="form-help" >0为每次都出现验证码</div>
					</el-form-item>
					<el-form-item label="举报图片允许最大上传数量" :error="error.reportMaxImageUpload" v-show="activeTag == 10">
						<el-row >
							<el-col :span="6"><el-input v-model.trim="reportMaxImageUpload" :required="true" maxlength="8" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
						<div class="form-help" >0为不允许上传图片</div>
					</el-form-item>
					<el-form-item label="提交问题最多可选择标签数量" :error="error.maxQuestionTagQuantity" v-show="activeTag == 10">
						<el-row >
							<el-col :span="6"><el-input v-model.trim="maxQuestionTagQuantity" :required="true" maxlength="8" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
					</el-form-item>
					
					<el-form-item label="发表话题奖励积分" :error="error.topic_rewardPoint" v-show="activeTag == 10">
						<el-row >
							<el-col :span="6"><el-input v-model.trim="topic_rewardPoint" :required="true" maxlength="8" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
					</el-form-item>
					<el-form-item label="发表评论奖励积分" :error="error.comment_rewardPoint" v-show="activeTag == 10">
						<el-row >
							<el-col :span="6"><el-input v-model.trim="comment_rewardPoint" :required="true" maxlength="8" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
					</el-form-item>
					<el-form-item label="发表回复奖励积分" :error="error.reply_rewardPoint" v-show="activeTag == 10">
						<el-row >
							<el-col :span="6"><el-input v-model.trim="reply_rewardPoint" :required="true" maxlength="8" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
					</el-form-item>
					<el-form-item label="提交问题奖励积分" :error="error.question_rewardPoint" v-show="activeTag == 10">
						<el-row >
							<el-col :span="6"><el-input v-model.trim="question_rewardPoint" :required="true" maxlength="8" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
					</el-form-item>
					<el-form-item label="提交答案奖励积分" :error="error.answer_rewardPoint" v-show="activeTag == 10">
						<el-row >
							<el-col :span="6"><el-input v-model.trim="answer_rewardPoint" :required="true" maxlength="8" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
					</el-form-item>
					<el-form-item label="提交答案回复奖励积分" :error="error.answerReply_rewardPoint" v-show="activeTag == 10">
						<el-row >
							<el-col :span="6"><el-input v-model.trim="answerReply_rewardPoint" :required="true" maxlength="8" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
					</el-form-item>
					<el-form-item label="前台发表话题审核" :error="error.topic_review" v-show="activeTag == 10">
						<el-radio-group v-model="topic_review">
						    <el-radio :label="10">全部审核</el-radio>
						    <el-radio :label="30">特权会员免审核</el-radio>
						    <el-radio :label="50">无需审核</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="前台发表评论审核" :error="error.comment_review" v-show="activeTag == 10">
						<el-radio-group v-model="comment_review">
						    <el-radio :label="10">全部审核</el-radio>
						    <el-radio :label="30">特权会员免审核</el-radio>
						    <el-radio :label="50">无需审核</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="前台发表回复审核" :error="error.reply_review" v-show="activeTag == 10">
						<el-radio-group v-model="reply_review">
						    <el-radio :label="10">全部审核</el-radio>
						    <el-radio :label="30">特权会员免审核</el-radio>
						    <el-radio :label="50">无需审核</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="前台提交问题审核" :error="error.question_review" v-show="activeTag == 10">
						<el-radio-group v-model="question_review">
						    <el-radio :label="10">全部审核</el-radio>
						    <el-radio :label="30">特权会员免审核</el-radio>
						    <el-radio :label="50">无需审核</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="前台提交答案审核" :error="error.answer_review" v-show="activeTag == 10">
						<el-radio-group v-model="answer_review">
						    <el-radio :label="10">全部审核</el-radio>
						    <el-radio :label="30">特权会员免审核</el-radio>
						    <el-radio :label="50">无需审核</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="前台提交答案回复审核" :error="error.answerReply_review" v-show="activeTag == 10">
						<el-radio-group v-model="answerReply_review">
						    <el-radio :label="10">全部审核</el-radio>
						    <el-radio :label="30">特权会员免审核</el-radio>
						    <el-radio :label="50">无需审核</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="允许提交在线留言" :error="error.allowFeedback" v-show="activeTag == 10">
						<el-radio-group v-model="allowFeedback">
						    <el-radio :label="true">是</el-radio>
						    <el-radio :label="false">否</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="全局允许提交话题" :error="error.allowTopic" v-show="activeTag == 10">
						<el-radio-group v-model="allowTopic">
						    <el-radio :label="true">是</el-radio>
						    <el-radio :label="false">否</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="全局允许提交评论" :error="error.allowComment" v-show="activeTag == 10">
						<el-radio-group v-model="allowComment">
						    <el-radio :label="true">是</el-radio>
						    <el-radio :label="false">否</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="全局允许提交问题" :error="error.allowQuestion" v-show="activeTag == 10">
						<el-radio-group v-model="allowQuestion">
						    <el-radio :label="true">是</el-radio>
						    <el-radio :label="false">否</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="全局允许提交答案" :error="error.allowAnswer" v-show="activeTag == 10">
						<el-radio-group v-model="allowAnswer">
						    <el-radio :label="true">是</el-radio>
						    <el-radio :label="false">否</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="实名用户才允许提交话题" :error="error.realNameUserAllowTopic" v-show="activeTag == 10">
						<el-radio-group v-model="realNameUserAllowTopic">
						    <el-radio :label="true">是</el-radio>
						    <el-radio :label="false">否</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="实名用户才允许提交评论" :error="error.realNameUserAllowComment" v-show="activeTag == 10">
						<el-radio-group v-model="realNameUserAllowComment">
						    <el-radio :label="true">是</el-radio>
						    <el-radio :label="false">否</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="实名用户才允许提交问题" :error="error.realNameUserAllowQuestion" v-show="activeTag == 10">
						<el-radio-group v-model="realNameUserAllowQuestion">
						    <el-radio :label="true">是</el-radio>
						    <el-radio :label="false">否</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="实名用户才允许提交答案" :error="error.realNameUserAllowAnswer" v-show="activeTag == 10">
						<el-radio-group v-model="realNameUserAllowAnswer">
						    <el-radio :label="true">是</el-radio>
						    <el-radio :label="false">否</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="全局允许提交举报" :error="error.allowReport" v-show="activeTag == 10">
						<el-radio-group v-model="allowReport">
						    <el-radio :label="true">是</el-radio>
						    <el-radio :label="false">否</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="是否显示IP归属地" :error="error.showIpAddress" v-show="activeTag == 10">
						<el-radio-group v-model="showIpAddress">
						    <el-radio :label="true">是</el-radio>
						    <el-radio :label="false">否</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="话题热度因子加权" :error="error.topicHeatFactor" v-show="activeTag == 10">
						<el-row :gutter="12">
							<el-col :span="12"><el-input v-model.trim="topicHeatFactor" maxlength="100" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
						<div class="form-help" >以竖线符号分割各热度因子，因子的分数越高，在热度因子中占比重越大；如果不设参数，则因子的加权值默认为1；因子加权值“评论|点赞|浏览量”可以为0至9999之间的整数，“重力因子”可以为0.1至2之间的数。示例：评论=20|点赞=10|浏览量=1|重力因子=1.8    </div>
					</el-form-item>
					<el-form-item label="热门话题" :error="error.topicHotRecommendedTime" v-show="activeTag == 10">
						<div class="singleRowTable">
							<div class="leftCell">
								仅推荐发布
							</div>
							<div>
								<el-input v-model.trim="topicHotRecommendedTime" maxlength="8" clearable="true" show-word-limit></el-input>
							</div>
							<div class="rightCell">
								小时内的话题
							</div>
						</div>
						<div class="form-help" >空值为不限制</div>
					</el-form-item>
					<el-form-item label="解锁话题隐藏内容平台分成比例" :error="error.topicUnhidePlatformShareProportion" v-show="activeTag == 10">
						<el-row :gutter="12">
							<el-col :span="6"><el-input v-model.trim="topicUnhidePlatformShareProportion" maxlength="3" clearable="true" show-word-limit></el-input></el-col>
							<el-col :span="6">%</el-col>
						</el-row>
						<div class="form-help" >0至100之间的整数</div>
					</el-form-item> 
					
					
					<el-form-item label="问题悬赏积分下限" :error="error.questionRewardPointMin" v-show="activeTag == 10">
						<el-row >
							<el-col :span="6"><el-input v-model.trim="questionRewardPointMin" :required="true" maxlength="15" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
						<div class="form-help" >0至999999999999999之间的整数</div>
					</el-form-item>
					<el-form-item label="问题悬赏积分上限" :error="error.questionRewardPointMax" v-show="activeTag == 10">
						<el-row >
							<el-col :span="6"><el-input v-model.trim="questionRewardPointMax" :required="true" maxlength="15" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
						<div class="form-help" >空为无限制 0则不允许悬赏积分</div>
					</el-form-item>
					<el-form-item label="问题悬赏金额下限" :error="error.questionRewardAmountMin" v-show="activeTag == 10">
						<el-row >
							<el-col :span="6"><el-input v-model.trim="questionRewardAmountMin" :required="true" maxlength="12" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
						<div class="form-help" >0至99999999之间的金额</div>
					</el-form-item>
					<el-form-item label="问题悬赏金额上限" :error="error.questionRewardAmountMax" v-show="activeTag == 10">
						<el-row >
							<el-col :span="6"><el-input v-model.trim="questionRewardAmountMax" :required="true" maxlength="12" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
						<div class="form-help" >空为无限制 0则不允许悬赏金额</div>
					</el-form-item>
				
					<el-form-item label="悬赏问答平台分成比例" :error="error.questionRewardPlatformShareProportion" v-show="activeTag == 10">
						<el-row :gutter="12">
							<el-col :span="6"><el-input v-model.trim="questionRewardPlatformShareProportion" maxlength="3" clearable="true" show-word-limit></el-input></el-col>
							<el-col :span="6">%</el-col>
						</el-row>
						<div class="form-help" >0至100之间的整数</div>
					</el-form-item> 
					
					<el-form-item label="发红包总金额下限" :error="error.giveRedEnvelopeAmountMin" v-show="activeTag == 10">
						<el-row >
							<el-col :span="6"><el-input v-model.trim="giveRedEnvelopeAmountMin" :required="true" maxlength="12" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
						<div class="form-help" >0.01至99999999之间的金额</div>
					</el-form-item>
					<el-form-item label="发红包总金额上限" :error="error.giveRedEnvelopeAmountMax" v-show="activeTag == 10">
						<el-row >
							<el-col :span="6"><el-input v-model.trim="giveRedEnvelopeAmountMax" :required="true" maxlength="12" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
						<div class="form-help" >空为无限制 0则不允许发红包</div>
					</el-form-item>
					<el-form-item label="敏感词过滤" :error="error.allowFilterWord" v-show="activeTag == 10">
						<el-radio-group v-model="allowFilterWord">
						    <el-radio-button :label="true">打开</el-radio-button>
						    <el-radio-button :label="false">关闭</el-radio-button>
						</el-radio-group>
						<div class="form-help" >前台发表话题/评论/回复时过滤</div>
					</el-form-item>
					<el-form-item label="敏感词替换为" v-show="activeTag == 10 && allowFilterWord ==true" :error="error.filterWordReplace">
						<el-row >
							<el-col :span="6"><el-input v-model.trim="filterWordReplace" :required="true" maxlength="30" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
					</el-form-item>
					<el-form-item label="文件防盗链密钥" v-show="activeTag == 10" :error="error.fileSecureLinkSecret">
						<el-row >
							<el-col :span="10"><el-input v-model.trim="fileSecureLinkSecret" :required="true" maxlength="16" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
						<div class="form-help" >必须和Nginx的Secure Link模块密钥一致，并且为16个字符</div>
					</el-form-item>
					<el-form-item label="文件防盗链过期时间" :error="error.fileSecureLinkExpire" v-show="activeTag == 10">
						<el-row :gutter="12">
							<el-col :span="6"><el-input v-model.trim="fileSecureLinkExpire" maxlength="10" clearable="true" show-word-limit></el-input></el-col>
							<el-col :span="6">秒</el-col>
						</el-row>
					</el-form-item> 
					<el-form-item label="前台分页数量" :error="error.forestagePageNumber" v-show="activeTag == 10">
						<el-row >
							<el-col :span="6"><el-input v-model.trim="forestagePageNumber" :required="true" maxlength="8" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
						<div class="form-help" >空为默认20条</div>
					</el-form-item>
					<el-form-item label="后台分页数量" :error="error.backstagePageNumber" v-show="activeTag == 10">
						<el-row >
							<el-col :span="6"><el-input v-model.trim="backstagePageNumber" :required="true" maxlength="8" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
						<div class="form-help" >空为默认20条</div>
					</el-form-item>
					<el-form-item label="上传临时文件有效期" :error="error.temporaryFileValidPeriod" v-show="activeTag == 10">
						<div class="singleRowTable">
							<div class="leftCell">
								文件上传
							</div>
							<div>
								<el-input v-model.trim="temporaryFileValidPeriod" maxlength="8" clearable="true" show-word-limit></el-input>
							</div>
							<div class="rightCell">
								分钟内未提交表单由定时任务自动删除
							</div>
						</div>
					</el-form-item>
					<el-form-item label="每用户每24小时内发送短信最大限制次数" :error="error.userSentSmsCount" v-show="activeTag == 10">
						<el-row >
							<el-col :span="6"><el-input v-model.trim="userSentSmsCount" :required="true" maxlength="8" clearable="true" show-word-limit></el-input></el-col>
						</el-row>
						<div class="form-help" >空为无限制 &nbsp;短信发送最大数量受短信服务商限制</div>
					</el-form-item>
					
					
					<!-- 话题编辑器 -->
					<el-form-item label="字体" v-show="activeTag == 20">
						<span class="toolbar-icon-url icon-insertorderedlist"></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.fontname">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="文字大小" v-show="activeTag == 20">
						<span class="container-icon"><span class="toolbar-icon-url icon-fontsize"></span></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.fontsize">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="文字颜色" v-show="activeTag == 20">
						<span class="toolbar-icon-url icon-forecolor"></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.forecolor">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="文字背景" v-show="activeTag == 20">
						<span class="toolbar-icon-url icon-hilitecolor"></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.hilitecolor">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="粗体" v-show="activeTag == 20">
						<span class="toolbar-icon-url icon-bold"></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.bold">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="斜体" v-show="activeTag == 20">
						<span class="toolbar-icon-url icon-italic"></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.italic">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="下划线" v-show="activeTag == 20">
						<span class="toolbar-icon-url icon-underline"></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.underline">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="删除格式" v-show="activeTag == 20">
						<span class="toolbar-icon-url icon-removeformat"></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.removeformat">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="超级链接" v-show="activeTag == 20">
						<span class="toolbar-icon-url icon-link"></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.link">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="取消超级链接" v-show="activeTag == 20">
						<span class="toolbar-icon-url icon-unlink"></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.unlink">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="左对齐" v-show="activeTag == 20">
						<span class="toolbar-icon-url icon-justifyleft"></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.justifyleft">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="居中" v-show="activeTag == 20">
						<span class="toolbar-icon-url icon-justifycenter"></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.justifycenter">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="右对齐" v-show="activeTag == 20">
						<span class="toolbar-icon-url icon-justifyright"></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.justifyright">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="编号" v-show="activeTag == 20">
						<span class="toolbar-icon-url icon-insertorderedlist"></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.insertorderedlist">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="项目符号" v-show="activeTag == 20">
						<span class="toolbar-icon-url icon-insertunorderedlist"></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.insertunorderedlist">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="代码" v-show="activeTag == 20">
						<span class="toolbar-icon-url icon-code"></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.code">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="插入表情" v-show="activeTag == 20">
						<span class="toolbar-icon-url icon-emoticons"></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.emoticons">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="输入密码可见" v-show="activeTag == 20">
						<span class="icon-hide" style="width: 16px;height: 16px;position: relative;top: 10px;margin-left: -9px;margin-right: 9px;"></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.hidePassword">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="评论话题可见" v-show="activeTag == 20">
						<span class="icon-hide" style="width: 16px;height: 16px;position: relative;top: 10px;margin-left: -9px;margin-right: 9px;"></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.hideComment">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="达到等级可见" v-show="activeTag == 20">
						<span class="icon-hide" style="width: 16px;height: 16px;position: relative;top: 10px;margin-left: -9px;margin-right: 9px;"></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.hideGrade">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="积分购买可见" v-show="activeTag == 20">
						<span class="icon-hide" style="width: 16px;height: 16px;position: relative;top: 10px;margin-left: -9px;margin-right: 9px;"></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.hidePoint">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="余额购买可见" v-show="activeTag == 20">
						<span class="icon-hide" style="width: 16px;height: 16px;position: relative;top: 10px;margin-left: -9px;margin-right: 9px;"></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.hideAmount">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="全屏显示" v-show="activeTag == 20">
						<span class="toolbar-icon-url icon-fullscreen"></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.fullscreen">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="图片" v-show="activeTag == 20">
						<span class="toolbar-icon-url icon-image"></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.image">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="允许上传图片格式" :error="error.topicEditorTagObject.imageFormat" v-show="activeTag == 20 && topicEditorTagObject.image == true">
						<el-checkbox-group class="singColumnPlaceholder" v-model="topicEditorTagObject.imageFormat">
						    <el-checkbox :label="imageUploadFormat" v-for="imageUploadFormat in imageUploadFormatList"></el-checkbox>
						</el-checkbox-group>
					</el-form-item>
					<el-form-item label="允许上传图片大小" :error="error.topicEditorTagObject.imageSize" v-show="activeTag == 20 && topicEditorTagObject.image == true">
						<el-row :gutter="12" class="multipleColumnPlaceholder">
							<el-col :span="8"><el-input v-model.trim="topicEditorTagObject.imageSize" maxlength="10" clearable="true" show-word-limit></el-input></el-col>
							<el-col :span="6">K</el-col>
						</el-row>
					</el-form-item> 
					<el-form-item label="文件" v-show="activeTag == 20">
						<span class="toolbar-icon-url icon-insertfile"></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.file">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="允许上传文件格式" :error="error.topicEditorTagObject.fileFormat" v-show="activeTag == 20 && topicEditorTagObject.file == true">
						<el-checkbox-group class="singColumnPlaceholder" v-model="topicEditorTagObject.fileFormat">
						    <el-checkbox :label="fileUploadFormat" v-for="fileUploadFormat in fileUploadFormatList"></el-checkbox>
						</el-checkbox-group>
					</el-form-item>
					<el-form-item label="允许上传文件大小" :error="error.topicEditorTagObject.fileSize" v-show="activeTag == 20 && topicEditorTagObject.file == true">
						<el-row :gutter="12" class="multipleColumnPlaceholder">
							<el-col :span="8"><el-input v-model.trim="topicEditorTagObject.fileSize" maxlength="10" clearable="true" show-word-limit></el-input></el-col>
							<el-col :span="6">K</el-col>
						</el-row>
					</el-form-item> 
					<el-form-item label="嵌入视频" v-show="activeTag == 20">
						<span class="toolbar-icon-url icon-media"></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.embedVideo">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="上传视频" v-show="activeTag == 20">
						<span class="toolbar-icon-url icon-media"></span>
						<el-radio-group class="radioPosition"  v-model="topicEditorTagObject.uploadVideo">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="允许上传视频格式" :error="error.topicEditorTagObject.videoFormat" v-show="activeTag == 20 && topicEditorTagObject.uploadVideo == true">
						<el-checkbox-group class="singColumnPlaceholder" v-model="topicEditorTagObject.videoFormat">
						    <el-checkbox :label="videoUploadFormat" v-for="videoUploadFormat in videoUploadFormatList"></el-checkbox>
						</el-checkbox-group>
					</el-form-item>
					<el-form-item label="允许上传视频大小" :error="error.topicEditorTagObject.videoSize" v-show="activeTag == 20 && topicEditorTagObject.uploadVideo == true">
						<el-row :gutter="12" class="multipleColumnPlaceholder">
							<el-col :span="8"><el-input v-model.trim="topicEditorTagObject.videoSize" maxlength="10" clearable="true" show-word-limit></el-input></el-col>
							<el-col :span="6">K</el-col>
						</el-row>
					</el-form-item> 
					
					<!-- 评论编辑器 -->
					<el-form-item label="字体" v-show="activeTag == 30">
						<span class="toolbar-icon-url icon-insertorderedlist"></span>
						<el-radio-group class="radioPosition"  v-model="editorTagObject.fontname">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="文字大小" v-show="activeTag == 30">
						<span class="container-icon"><span class="toolbar-icon-url icon-fontsize"></span></span>
						<el-radio-group class="radioPosition"  v-model="editorTagObject.fontsize">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="文字颜色" v-show="activeTag == 30">
						<span class="toolbar-icon-url icon-forecolor"></span>
						<el-radio-group class="radioPosition"  v-model="editorTagObject.forecolor">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="文字背景" v-show="activeTag == 30">
						<span class="toolbar-icon-url icon-hilitecolor"></span>
						<el-radio-group class="radioPosition"  v-model="editorTagObject.hilitecolor">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="粗体" v-show="activeTag == 30">
						<span class="toolbar-icon-url icon-bold"></span>
						<el-radio-group class="radioPosition"  v-model="editorTagObject.bold">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="斜体" v-show="activeTag == 30">
						<span class="toolbar-icon-url icon-italic"></span>
						<el-radio-group class="radioPosition"  v-model="editorTagObject.italic">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="下划线" v-show="activeTag == 30">
						<span class="toolbar-icon-url icon-underline"></span>
						<el-radio-group class="radioPosition"  v-model="editorTagObject.underline">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="删除格式" v-show="activeTag == 30">
						<span class="toolbar-icon-url icon-removeformat"></span>
						<el-radio-group class="radioPosition"  v-model="editorTagObject.removeformat">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="超级链接" v-show="activeTag == 30">
						<span class="toolbar-icon-url icon-link"></span>
						<el-radio-group class="radioPosition"  v-model="editorTagObject.link">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="取消超级链接" v-show="activeTag == 30">
						<span class="toolbar-icon-url icon-unlink"></span>
						<el-radio-group class="radioPosition"  v-model="editorTagObject.unlink">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="左对齐" v-show="activeTag == 30">
						<span class="toolbar-icon-url icon-justifyleft"></span>
						<el-radio-group class="radioPosition"  v-model="editorTagObject.justifyleft">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="居中" v-show="activeTag == 30">
						<span class="toolbar-icon-url icon-justifycenter"></span>
						<el-radio-group class="radioPosition"  v-model="editorTagObject.justifycenter">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="右对齐" v-show="activeTag == 30">
						<span class="toolbar-icon-url icon-justifyright"></span>
						<el-radio-group class="radioPosition"  v-model="editorTagObject.justifyright">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="编号" v-show="activeTag == 30">
						<span class="toolbar-icon-url icon-insertorderedlist"></span>
						<el-radio-group class="radioPosition"  v-model="editorTagObject.insertorderedlist">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="项目符号" v-show="activeTag == 30">
						<span class="toolbar-icon-url icon-insertunorderedlist"></span>
						<el-radio-group class="radioPosition"  v-model="editorTagObject.insertunorderedlist">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="代码" v-show="activeTag == 30">
						<span class="toolbar-icon-url icon-code"></span>
						<el-radio-group class="radioPosition"  v-model="editorTagObject.code">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="插入表情" v-show="activeTag == 30">
						<span class="toolbar-icon-url icon-emoticons"></span>
						<el-radio-group class="radioPosition"  v-model="editorTagObject.emoticons">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="全屏显示" v-show="activeTag == 30">
						<span class="toolbar-icon-url icon-fullscreen"></span>
						<el-radio-group class="radioPosition"  v-model="editorTagObject.fullscreen">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="图片" v-show="activeTag == 30">
						<span class="toolbar-icon-url icon-image"></span>
						<el-radio-group class="radioPosition"  v-model="editorTagObject.image">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="允许上传图片格式" :error="error.editorTagObject.imageFormat" v-show="activeTag == 30 && editorTagObject.image == true">
						<el-checkbox-group class="singColumnPlaceholder" v-model="editorTagObject.imageFormat">
						    <el-checkbox :label="imageUploadFormat" v-for="imageUploadFormat in imageUploadFormatList"></el-checkbox>
						</el-checkbox-group>
					</el-form-item>
					<el-form-item label="允许上传图片大小" :error="error.editorTagObject.imageSize" v-show="activeTag == 30 && editorTagObject.image == true">
						<el-row :gutter="12" class="multipleColumnPlaceholder">
							<el-col :span="8"><el-input v-model.trim="editorTagObject.imageSize" maxlength="10" clearable="true" show-word-limit></el-input></el-col>
							<el-col :span="6">K</el-col>
						</el-row>
					</el-form-item> 
					
					<!-- 问题编辑器 -->
					<el-form-item label="字体" v-show="activeTag == 40">
						<span class="toolbar-icon-url icon-insertorderedlist"></span>
						<el-radio-group class="radioPosition"  v-model="questionEditorTagObject.fontname">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="文字大小" v-show="activeTag == 40">
						<span class="container-icon"><span class="toolbar-icon-url icon-fontsize"></span></span>
						<el-radio-group class="radioPosition"  v-model="questionEditorTagObject.fontsize">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="文字颜色" v-show="activeTag == 40">
						<span class="toolbar-icon-url icon-forecolor"></span>
						<el-radio-group class="radioPosition"  v-model="questionEditorTagObject.forecolor">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="文字背景" v-show="activeTag == 40">
						<span class="toolbar-icon-url icon-hilitecolor"></span>
						<el-radio-group class="radioPosition"  v-model="questionEditorTagObject.hilitecolor">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="粗体" v-show="activeTag == 40">
						<span class="toolbar-icon-url icon-bold"></span>
						<el-radio-group class="radioPosition"  v-model="questionEditorTagObject.bold">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="斜体" v-show="activeTag == 40">
						<span class="toolbar-icon-url icon-italic"></span>
						<el-radio-group class="radioPosition"  v-model="questionEditorTagObject.italic">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="下划线" v-show="activeTag == 40">
						<span class="toolbar-icon-url icon-underline"></span>
						<el-radio-group class="radioPosition"  v-model="questionEditorTagObject.underline">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="删除格式" v-show="activeTag == 40">
						<span class="toolbar-icon-url icon-removeformat"></span>
						<el-radio-group class="radioPosition"  v-model="questionEditorTagObject.removeformat">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="超级链接" v-show="activeTag == 40">
						<span class="toolbar-icon-url icon-link"></span>
						<el-radio-group class="radioPosition"  v-model="questionEditorTagObject.link">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="取消超级链接" v-show="activeTag == 40">
						<span class="toolbar-icon-url icon-unlink"></span>
						<el-radio-group class="radioPosition"  v-model="questionEditorTagObject.unlink">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="左对齐" v-show="activeTag == 40">
						<span class="toolbar-icon-url icon-justifyleft"></span>
						<el-radio-group class="radioPosition"  v-model="questionEditorTagObject.justifyleft">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="居中" v-show="activeTag == 40">
						<span class="toolbar-icon-url icon-justifycenter"></span>
						<el-radio-group class="radioPosition"  v-model="questionEditorTagObject.justifycenter">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="右对齐" v-show="activeTag == 40">
						<span class="toolbar-icon-url icon-justifyright"></span>
						<el-radio-group class="radioPosition"  v-model="questionEditorTagObject.justifyright">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="编号" v-show="activeTag == 40">
						<span class="toolbar-icon-url icon-insertorderedlist"></span>
						<el-radio-group class="radioPosition"  v-model="questionEditorTagObject.insertorderedlist">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="项目符号" v-show="activeTag == 40">
						<span class="toolbar-icon-url icon-insertunorderedlist"></span>
						<el-radio-group class="radioPosition"  v-model="questionEditorTagObject.insertunorderedlist">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="代码" v-show="activeTag == 40">
						<span class="toolbar-icon-url icon-code"></span>
						<el-radio-group class="radioPosition"  v-model="questionEditorTagObject.code">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="插入表情" v-show="activeTag == 40">
						<span class="toolbar-icon-url icon-emoticons"></span>
						<el-radio-group class="radioPosition"  v-model="questionEditorTagObject.emoticons">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="全屏显示" v-show="activeTag == 40">
						<span class="toolbar-icon-url icon-fullscreen"></span>
						<el-radio-group class="radioPosition"  v-model="questionEditorTagObject.fullscreen">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="图片" v-show="activeTag == 40">
						<span class="toolbar-icon-url icon-image"></span>
						<el-radio-group class="radioPosition"  v-model="questionEditorTagObject.image">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="允许上传图片格式" :error="error.questionEditorTagObject.imageFormat" v-show="activeTag == 40 && questionEditorTagObject.image == true">
						<el-checkbox-group class="singColumnPlaceholder" v-model="questionEditorTagObject.imageFormat">
						    <el-checkbox :label="imageUploadFormat" v-for="imageUploadFormat in imageUploadFormatList"></el-checkbox>
						</el-checkbox-group>
					</el-form-item>
					<el-form-item label="允许上传图片大小" :error="error.questionEditorTagObject.imageSize" v-show="activeTag == 40 && questionEditorTagObject.image == true">
						<el-row :gutter="12" class="multipleColumnPlaceholder">
							<el-col :span="8"><el-input v-model.trim="questionEditorTagObject.imageSize" maxlength="10" clearable="true" show-word-limit></el-input></el-col>
							<el-col :span="6">K</el-col>
						</el-row>
					</el-form-item> 
					
					<!-- 答案编辑器 -->
					<el-form-item label="字体" v-show="activeTag == 50">
						<span class="toolbar-icon-url icon-insertorderedlist"></span>
						<el-radio-group class="radioPosition"  v-model="answerEditorTagObject.fontname">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="文字大小" v-show="activeTag == 50">
						<span class="container-icon"><span class="toolbar-icon-url icon-fontsize"></span></span>
						<el-radio-group class="radioPosition"  v-model="answerEditorTagObject.fontsize">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="文字颜色" v-show="activeTag == 50">
						<span class="toolbar-icon-url icon-forecolor"></span>
						<el-radio-group class="radioPosition"  v-model="answerEditorTagObject.forecolor">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="文字背景" v-show="activeTag == 50">
						<span class="toolbar-icon-url icon-hilitecolor"></span>
						<el-radio-group class="radioPosition"  v-model="answerEditorTagObject.hilitecolor">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="粗体" v-show="activeTag == 50">
						<span class="toolbar-icon-url icon-bold"></span>
						<el-radio-group class="radioPosition"  v-model="answerEditorTagObject.bold">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="斜体" v-show="activeTag == 50">
						<span class="toolbar-icon-url icon-italic"></span>
						<el-radio-group class="radioPosition"  v-model="answerEditorTagObject.italic">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="下划线" v-show="activeTag == 50">
						<span class="toolbar-icon-url icon-underline"></span>
						<el-radio-group class="radioPosition"  v-model="answerEditorTagObject.underline">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="删除格式" v-show="activeTag == 50">
						<span class="toolbar-icon-url icon-removeformat"></span>
						<el-radio-group class="radioPosition"  v-model="answerEditorTagObject.removeformat">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="超级链接" v-show="activeTag == 50">
						<span class="toolbar-icon-url icon-link"></span>
						<el-radio-group class="radioPosition"  v-model="answerEditorTagObject.link">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="取消超级链接" v-show="activeTag == 50">
						<span class="toolbar-icon-url icon-unlink"></span>
						<el-radio-group class="radioPosition"  v-model="answerEditorTagObject.unlink">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="左对齐" v-show="activeTag == 50">
						<span class="toolbar-icon-url icon-justifyleft"></span>
						<el-radio-group class="radioPosition"  v-model="answerEditorTagObject.justifyleft">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="居中" v-show="activeTag == 50">
						<span class="toolbar-icon-url icon-justifycenter"></span>
						<el-radio-group class="radioPosition"  v-model="answerEditorTagObject.justifycenter">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="右对齐" v-show="activeTag == 50">
						<span class="toolbar-icon-url icon-justifyright"></span>
						<el-radio-group class="radioPosition"  v-model="answerEditorTagObject.justifyright">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="编号" v-show="activeTag == 50">
						<span class="toolbar-icon-url icon-insertorderedlist"></span>
						<el-radio-group class="radioPosition"  v-model="answerEditorTagObject.insertorderedlist">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="项目符号" v-show="activeTag == 50">
						<span class="toolbar-icon-url icon-insertunorderedlist"></span>
						<el-radio-group class="radioPosition"  v-model="answerEditorTagObject.insertunorderedlist">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="代码" v-show="activeTag == 50">
						<span class="toolbar-icon-url icon-code"></span>
						<el-radio-group class="radioPosition"  v-model="answerEditorTagObject.code">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="插入表情" v-show="activeTag == 50">
						<span class="toolbar-icon-url icon-emoticons"></span>
						<el-radio-group class="radioPosition"  v-model="answerEditorTagObject.emoticons">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="全屏显示" v-show="activeTag == 50">
						<span class="toolbar-icon-url icon-fullscreen"></span>
						<el-radio-group class="radioPosition"  v-model="answerEditorTagObject.fullscreen">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="图片" v-show="activeTag == 50">
						<span class="toolbar-icon-url icon-image"></span>
						<el-radio-group class="radioPosition"  v-model="answerEditorTagObject.image">
						    <el-radio :label="true">打开</el-radio>
						    <el-radio :label="false">关闭</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="允许上传图片格式" :error="error.answerEditorTagObject.imageFormat" v-show="activeTag == 50 && answerEditorTagObject.image == true">
						<el-checkbox-group class="singColumnPlaceholder" v-model="answerEditorTagObject.imageFormat">
						    <el-checkbox :label="imageUploadFormat" v-for="imageUploadFormat in imageUploadFormatList"></el-checkbox>
						</el-checkbox-group>
					</el-form-item>
					<el-form-item label="允许上传图片大小" :error="error.answerEditorTagObject.imageSize" v-show="activeTag == 50 && answerEditorTagObject.image == true">
						<el-row :gutter="12" class="multipleColumnPlaceholder">
							<el-col :span="8"><el-input v-model.trim="answerEditorTagObject.imageSize" maxlength="10" clearable="true" show-word-limit></el-input></el-col>
							<el-col :span="6">K</el-col>
						</el-row>
					</el-form-item> 
					
					<el-form-item>
					    <el-button type="primary" class="submitButton" @click="submitForm" :disabled="submitForm_disabled">提交</el-button>
					</el-form-item>
				</el-form>
				
				
			</div>
		</div>
	</div>
</template>

<script>
//基本设置
export default({
	name: 'editSystemSetting',//组件名称，keep-alive缓存需要本参数
	template : '#editSystemSetting-template',
	inject:['reload'], 
	data : function data() {
		return {
			imageUploadFormatList :[],
			
			activeTag: "10",//选项卡
			
			title :'',
			keywords:'',
			description:'',
			closeSite:1,
			closeSitePrompt:'',
			supportAccessDevice:1,
			supportEditor:10,
			allowRegisterAccountObject:'',
			registerCaptcha:false,
			login_submitQuantity:'',
			topic_submitQuantity:'',
			comment_submitQuantity:'',
			question_submitQuantity:'',
			answer_submitQuantity:'',
			privateMessage_submitQuantity:'',
			report_submitQuantity:'',
			reportMaxImageUpload:'',
			maxQuestionTagQuantity:'',
			topic_rewardPoint:'',
			comment_rewardPoint:'',
			reply_rewardPoint:'',
			question_rewardPoint:'',
			answer_rewardPoint:'',
			answerReply_rewardPoint:'',
			topic_review:10,
			comment_review:10,
			reply_review:10,
			question_review:10,
			answer_review:10,
			answerReply_review:10,
			allowFeedback:false,
			allowTopic:false,
			allowComment:false,
			allowQuestion:false,
			allowAnswer:false,
			realNameUserAllowTopic:false,
			realNameUserAllowComment:false,
			realNameUserAllowQuestion:false,
			realNameUserAllowAnswer:false,
			allowReport:true,
			showIpAddress:false,
			topicHeatFactor:'',
			topicHotRecommendedTime:'',
			questionRewardPointMin:'',
			questionRewardPointMax:'',
			questionRewardAmountMin:'',
			questionRewardAmountMax:'',
			topicUnhidePlatformShareProportion:'',
			questionRewardPlatformShareProportion:'',
			giveRedEnvelopeAmountMin:'',
			giveRedEnvelopeAmountMax:'',
			allowFilterWord:false,
			filterWordReplace:'',
			fileSecureLinkSecret:'',
			fileSecureLinkExpire:'',
			forestagePageNumber:'',
			backstagePageNumber:'',
			temporaryFileValidPeriod:'',
			userSentSmsCount:'',
			
			topicEditorTagObject:'',//话题编辑器
			editorTagObject:'',//评论编辑器
			questionEditorTagObject:'',//问题编辑器
			answerEditorTagObject:'',//答案编辑器
			
			error : {
				title :'',
				keywords:'',
				description:'',
				closeSite :'',
				closeSitePrompt:'',
				supportAccessDevice :'',
				supportEditor :'',
				allowRegisterAccountObject:'',
				registerCaptcha :'',
				login_submitQuantity:'',
				topic_submitQuantity:'',
				comment_submitQuantity:'',
				question_submitQuantity:'',
				answer_submitQuantity:'',
				privateMessage_submitQuantity:'',
				report_submitQuantity:'',
				reportMaxImageUpload:'',
				maxQuestionTagQuantity:'',
				topic_rewardPoint:'',
				comment_rewardPoint:'',
				reply_rewardPoint:'',
				question_rewardPoint:'',
				answer_rewardPoint:'',
				answerReply_rewardPoint:'',
				topic_review :'',
				comment_review :'',
				reply_review :'',
				question_review :'',
				answer_review :'',
				answerReply_review :'',
				allowFeedback :'',
				allowTopic :'',
				allowComment :'',
				allowQuestion :'',
				allowAnswer :'',
				realNameUserAllowTopic :'',
				realNameUserAllowComment :'',
				realNameUserAllowQuestion :'',
				realNameUserAllowAnswer :'',
				allowReport :'',
				showIpAddress :'',
				topicHeatFactor:'',
				topicHotRecommendedTime:'',
				questionRewardPointMin:'',
				questionRewardPointMax:'',
				questionRewardAmountMin:'',
				questionRewardAmountMax:'',
				topicUnhidePlatformShareProportion:'',
				questionRewardPlatformShareProportion:'',
				giveRedEnvelopeAmountMin:'',
				giveRedEnvelopeAmountMax:'',
				allowFilterWord :'',
				filterWordReplace:'',
				fileSecureLinkSecret:'',
				fileSecureLinkExpire:'',
				forestagePageNumber:'',
				backstagePageNumber:'',
				temporaryFileValidPeriod:'',
				userSentSmsCount:'',
				
				topicEditorTagObject:{
					imageFormat:'',
					imageSize:'',
					fileFormat:'',
					fileSize:'',
					videoFormat:'',
					videoSize:'',
				},
				editorTagObject:{
					imageFormat:'',
					imageSize:'',
				},
				questionEditorTagObject:{
					imageFormat:'',
					imageSize:'',
				},
				answerEditorTagObject:{
					imageFormat:'',
					imageSize:'',
				},
				
			},
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	created : function created() {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		this.queryEditSystemSetting();
	},
	methods : {
		
				
		 //查询基本设置
	    queryEditSystemSetting: function(){
	        let _self = this;
	        
	        
			_self.$ajax.get('control/systemSetting/manage/edit', {
			    params: {
			    	
			    }
			})
			.then(function (response) {
				if(response == null){
					return;
				}
			    let result = response.data;
			    if(result){
			    	let returnValue = JSON.parse(result);
			    	
			    	if(returnValue.code === 200){//成功
			    		let mapData = returnValue.data;
			    		for(let key in mapData){
			    			let systemSetting = null;
			    			if(key == "systemSetting"){
			    				systemSetting = mapData[key];
			    			}else if(key == "imageUploadFormatList"){
			    				_self.imageUploadFormatList = mapData[key];
			    			}else if(key == "fileUploadFormatList"){
			    				_self.fileUploadFormatList = mapData[key];
			    			}else if(key == "videoUploadFormatList"){
			    				_self.videoUploadFormatList = mapData[key];
			    			}
			    		
			    			
			    			if(systemSetting != null){
			    				if(systemSetting.title != null){
			    					_self.title = systemSetting.title;
			    				}
			    				if(systemSetting.keywords != null){
			    					_self.keywords = systemSetting.keywords;
			    				}
			    				if(systemSetting.description != null){
			    					_self.description = systemSetting.description;
			    				}
			    				_self.closeSite = systemSetting.closeSite;
			    				if(systemSetting.closeSitePrompt != null){
			    					_self.closeSitePrompt = systemSetting.closeSitePrompt;
			    				}
			    				_self.supportAccessDevice = systemSetting.supportAccessDevice;
			    				_self.supportEditor = systemSetting.supportEditor;
			    				if(systemSetting.allowRegisterAccountObject != null){
			    					_self.allowRegisterAccountObject = systemSetting.allowRegisterAccountObject;
			    				}
			    				_self.registerCaptcha = systemSetting.registerCaptcha;
			    				if(systemSetting.login_submitQuantity != null){
			    					_self.login_submitQuantity = systemSetting.login_submitQuantity;
			    				}
			    				if(systemSetting.topic_submitQuantity != null){
			    					_self.topic_submitQuantity = systemSetting.topic_submitQuantity;
			    				}
			    				if(systemSetting.comment_submitQuantity != null){
			    					_self.comment_submitQuantity = systemSetting.comment_submitQuantity;
			    				}
			    				if(systemSetting.question_submitQuantity != null){
			    					_self.question_submitQuantity = systemSetting.question_submitQuantity;
			    				}
			    				if(systemSetting.answer_submitQuantity != null){
			    					_self.answer_submitQuantity = systemSetting.answer_submitQuantity;
			    				}
			    				if(systemSetting.privateMessage_submitQuantity != null){
			    					_self.privateMessage_submitQuantity = systemSetting.privateMessage_submitQuantity;
			    				}
			    				if(systemSetting.report_submitQuantity != null){
			    					_self.report_submitQuantity = systemSetting.report_submitQuantity;
			    				}
			    				if(systemSetting.reportMaxImageUpload != null){
			    					_self.reportMaxImageUpload = systemSetting.reportMaxImageUpload;
			    				}
			    				if(systemSetting.maxQuestionTagQuantity != null){
			    					_self.maxQuestionTagQuantity = systemSetting.maxQuestionTagQuantity;
			    				}
			    				if(systemSetting.topic_rewardPoint != null){
			    					_self.topic_rewardPoint = systemSetting.topic_rewardPoint;
			    				}
			    				if(systemSetting.comment_rewardPoint != null){
			    					_self.comment_rewardPoint = systemSetting.comment_rewardPoint;
			    				}
			    				if(systemSetting.reply_rewardPoint != null){
			    					_self.reply_rewardPoint = systemSetting.reply_rewardPoint;
			    				}
			    				if(systemSetting.question_rewardPoint != null){
			    					_self.question_rewardPoint = systemSetting.question_rewardPoint;
			    				}
			    				if(systemSetting.answer_rewardPoint != null){
			    					_self.answer_rewardPoint = systemSetting.answer_rewardPoint;
			    				}
			    				if(systemSetting.answerReply_rewardPoint != null){
			    					_self.answerReply_rewardPoint = systemSetting.answerReply_rewardPoint;
			    				}
			    				if(systemSetting.topic_review != null){
			    					_self.topic_review = systemSetting.topic_review;
			    				}
			    				if(systemSetting.comment_review != null){
			    					_self.comment_review = systemSetting.comment_review;
			    				}
			    				if(systemSetting.reply_review != null){
			    					_self.reply_review = systemSetting.reply_review;
			    				}
			    				if(systemSetting.question_review != null){
			    					_self.question_review = systemSetting.question_review;
			    				}
			    				if(systemSetting.answer_review != null){
			    					_self.answer_review = systemSetting.answer_review;
			    				}
			    				if(systemSetting.answerReply_review != null){
			    					_self.answerReply_review = systemSetting.answerReply_review;
			    				}
			    				_self.allowFeedback = systemSetting.allowFeedback;
			    				_self.allowTopic = systemSetting.allowTopic;
			    				_self.allowComment = systemSetting.allowComment;
			    				_self.allowQuestion = systemSetting.allowQuestion;
			    				_self.allowAnswer = systemSetting.allowAnswer;
			    				_self.realNameUserAllowTopic = systemSetting.realNameUserAllowTopic;
			    				_self.realNameUserAllowComment= systemSetting.realNameUserAllowComment;
			    				_self.realNameUserAllowQuestion= systemSetting.realNameUserAllowQuestion;
			    				_self.realNameUserAllowAnswer= systemSetting.realNameUserAllowAnswer;
			    				_self.allowReport = systemSetting.allowReport;
			    				_self.showIpAddress = systemSetting.showIpAddress;
			    				if(systemSetting.topicHeatFactor != null){
			    					_self.topicHeatFactor = systemSetting.topicHeatFactor;
			    				}
			    				if(systemSetting.topicHotRecommendedTime != null){
			    					_self.topicHotRecommendedTime = systemSetting.topicHotRecommendedTime;
			    				}
			    				if(systemSetting.questionRewardPointMin != null){
			    					_self.questionRewardPointMin = systemSetting.questionRewardPointMin;
			    				}
			    				if(systemSetting.questionRewardPointMax != null){
			    					_self.questionRewardPointMax = systemSetting.questionRewardPointMax;
			    				}
			    				if(systemSetting.questionRewardAmountMin != null){
			    					_self.questionRewardAmountMin = systemSetting.questionRewardAmountMin;
			    				}
			    				if(systemSetting.questionRewardAmountMax != null){
			    					_self.questionRewardAmountMax = systemSetting.questionRewardAmountMax;
			    				}
			    				if(systemSetting.topicUnhidePlatformShareProportion != null){
			    					_self.topicUnhidePlatformShareProportion = systemSetting.topicUnhidePlatformShareProportion;
			    				}
			    				if(systemSetting.questionRewardPlatformShareProportion != null){
			    					_self.questionRewardPlatformShareProportion = systemSetting.questionRewardPlatformShareProportion;
			    				}
			    				if(systemSetting.giveRedEnvelopeAmountMin != null){
			    					_self.giveRedEnvelopeAmountMin = systemSetting.giveRedEnvelopeAmountMin;
			    				}
			    				if(systemSetting.giveRedEnvelopeAmountMax != null){
			    					_self.giveRedEnvelopeAmountMax = systemSetting.giveRedEnvelopeAmountMax;
			    				}
			    				_self.allowFilterWord = systemSetting.allowFilterWord;
			    				if(systemSetting.filterWordReplace != null){
			    					_self.filterWordReplace = systemSetting.filterWordReplace;
			    				}
			    				if(systemSetting.fileSecureLinkSecret != null){
			    					_self.fileSecureLinkSecret = systemSetting.fileSecureLinkSecret;
			    				}
			    				if(systemSetting.fileSecureLinkExpire != null){
			    					_self.fileSecureLinkExpire = systemSetting.fileSecureLinkExpire;
			    				}
			    				if(systemSetting.forestagePageNumber != null){
			    					_self.forestagePageNumber = systemSetting.forestagePageNumber;
			    				}
			    				if(systemSetting.backstagePageNumber != null){
			    					_self.backstagePageNumber = systemSetting.backstagePageNumber;
			    				}
			    				if(systemSetting.temporaryFileValidPeriod != null){
			    					_self.temporaryFileValidPeriod = systemSetting.temporaryFileValidPeriod;
			    				}
			    				if(systemSetting.userSentSmsCount != null){
			    					_self.userSentSmsCount = systemSetting.userSentSmsCount;
			    				}
			    				if(systemSetting.topicEditorTagObject != null){//话题编辑器
			    					let topicEditorTagObject = systemSetting.topicEditorTagObject;
			    					let imageFormat_arr = [];
			    					if(_self.imageUploadFormatList != null && _self.imageUploadFormatList.length >0){
			    						A:for(let i=0; i<_self.imageUploadFormatList.length; i++){
			    							let imageUploadFormat = _self.imageUploadFormatList[i];
			    							
			    							if(topicEditorTagObject.imageFormat != null && topicEditorTagObject.imageFormat.length >0){
			    								for(let j=0; j<topicEditorTagObject.imageFormat.length; j++){
			    									let format = topicEditorTagObject.imageFormat[j];
			    									if(imageUploadFormat == format){
			    										imageFormat_arr.push(imageUploadFormat);
			    										continue A;
			    									}
			    								}
			    								
			    							}
			    							imageFormat_arr.push("-");//横杆代表空值
			    						}
			    						
			    					}
			    					let fileFormat_arr = [];
			    					if(_self.fileUploadFormatList != null && _self.fileUploadFormatList.length >0){
			    						A:for(let i=0; i<_self.fileUploadFormatList.length; i++){
			    							let fileUploadFormat = _self.fileUploadFormatList[i];
			    							
			    							if(topicEditorTagObject.fileFormat != null && topicEditorTagObject.fileFormat.length >0){
			    								for(let j=0; j<topicEditorTagObject.fileFormat.length; j++){
			    									let format = topicEditorTagObject.fileFormat[j];
			    									if(fileUploadFormat == format){
			    										fileFormat_arr.push(fileUploadFormat);
			    										continue A;
			    									}
			    								}
			    								
			    							}
			    							fileFormat_arr.push("-");//横杆代表空值
			    						}
			    						
			    					}
			    					let videoFormat_arr = [];
			    					if(_self.videoUploadFormatList != null && _self.videoUploadFormatList.length >0){
			    						A:for(let i=0; i<_self.videoUploadFormatList.length; i++){
			    							let videoUploadFormat = _self.videoUploadFormatList[i];
			    							
			    							if(topicEditorTagObject.videoFormat != null && topicEditorTagObject.videoFormat.length >0){
			    								for(let j=0; j<topicEditorTagObject.videoFormat.length; j++){
			    									let format = topicEditorTagObject.videoFormat[j];
			    									if(videoUploadFormat == format){
			    										videoFormat_arr.push(videoUploadFormat);
			    										continue A;
			    									}
			    								}
			    								
			    							}
			    							videoFormat_arr.push("-");//横杆代表空值
			    						}
			    						
			    					}
			    					topicEditorTagObject.imageFormat = imageFormat_arr;
			    					topicEditorTagObject.fileFormat = fileFormat_arr;
			    					topicEditorTagObject.videoFormat = videoFormat_arr;
			    					_self.topicEditorTagObject = topicEditorTagObject;
			    				}
			    				if(systemSetting.editorTagObject != null){//评论编辑器
			    					let editorTagObject = systemSetting.editorTagObject;
			    					let imageFormat_arr = [];
			    					if(_self.imageUploadFormatList != null && _self.imageUploadFormatList.length >0){
			    						A:for(let i=0; i<_self.imageUploadFormatList.length; i++){
			    							let imageUploadFormat = _self.imageUploadFormatList[i];
			    							
			    							if(editorTagObject.imageFormat != null && editorTagObject.imageFormat.length >0){
			    								for(let j=0; j<editorTagObject.imageFormat.length; j++){
			    									let format = editorTagObject.imageFormat[j];
			    									if(imageUploadFormat == format){
			    										imageFormat_arr.push(imageUploadFormat);
			    										continue A;
			    									}
			    								}
			    								
			    							}
			    							imageFormat_arr.push("-");//横杆代表空值
			    						}
			    						
			    					}
			    					editorTagObject.imageFormat = imageFormat_arr;
			    					_self.editorTagObject = editorTagObject;
			    				}
			    				if(systemSetting.questionEditorTagObject != null){//问题编辑器
			    					let questionEditorTagObject = systemSetting.questionEditorTagObject;
			    					let imageFormat_arr = [];
			    					if(_self.imageUploadFormatList != null && _self.imageUploadFormatList.length >0){
			    						A:for(let i=0; i<_self.imageUploadFormatList.length; i++){
			    							let imageUploadFormat = _self.imageUploadFormatList[i];
			    							
			    							if(questionEditorTagObject.imageFormat != null && questionEditorTagObject.imageFormat.length >0){
			    								for(let j=0; j<questionEditorTagObject.imageFormat.length; j++){
			    									let format = questionEditorTagObject.imageFormat[j];
			    									if(imageUploadFormat == format){
			    										imageFormat_arr.push(imageUploadFormat);
			    										continue A;
			    									}
			    								}
			    								
			    							}
			    							imageFormat_arr.push("-");//横杆代表空值
			    						}
			    						
			    					}
			    					questionEditorTagObject.imageFormat = imageFormat_arr;
			    					_self.questionEditorTagObject = questionEditorTagObject;
			    				}
			    				if(systemSetting.answerEditorTagObject != null){//答案编辑器
			    					let answerEditorTagObject = systemSetting.answerEditorTagObject;
			    					let imageFormat_arr = [];
			    					if(_self.imageUploadFormatList != null && _self.imageUploadFormatList.length >0){
			    						A:for(let i=0; i<_self.imageUploadFormatList.length; i++){
			    							let imageUploadFormat = _self.imageUploadFormatList[i];
			    							
			    							if(answerEditorTagObject.imageFormat != null && answerEditorTagObject.imageFormat.length >0){
			    								for(let j=0; j<answerEditorTagObject.imageFormat.length; j++){
			    									let format = answerEditorTagObject.imageFormat[j];
			    									if(imageUploadFormat == format){
			    										imageFormat_arr.push(imageUploadFormat);
			    										continue A;
			    									}
			    								}
			    								
			    							}
			    							imageFormat_arr.push("-");//横杆代表空值
			    						}
			    						
			    					}
			    					answerEditorTagObject.imageFormat = imageFormat_arr;
			    					_self.answerEditorTagObject = answerEditorTagObject;
			    				}
			    				
			    			}
			    		}
			    	}else if(returnValue.code === 500){//错误
			    		let errorMap = returnValue.data;
			    		for (let key in errorMap) {   
			    			_self.error[key] = errorMap[key];
			    	    }
			    	}
			    	
			    }
			    
			    
			})
			.catch(function (error) {
				console.log(error);
			});
		},
	
		//提交表单
		submitForm : function() {
			let _self = this;
			_self.submitForm_disabled = true;
			
	        //清除错误
			for (let key in _self.error) { 
				let obj = _self.error[key];
				
    			if(getType(obj) == 'object'){
    				Object.keys(obj).forEach(key => obj[key] = '');
    			}else{
    				_self.error[key] = "";
    			}
    	    }
	       
			let formData = new FormData();
			if(_self.title != null){
				formData.append('title', _self.title);
			}
			if(_self.keywords != null){
				formData.append('keywords', _self.keywords);
			}
			if(_self.description != null){
				formData.append('description', _self.description);
			}
			if(_self.closeSite != null){
				formData.append('closeSite', _self.closeSite);
			}
			if(_self.closeSitePrompt != null){
				formData.append('closeSitePrompt', _self.closeSitePrompt);
			}
			if(_self.supportAccessDevice != null){
				formData.append('supportAccessDevice', _self.supportAccessDevice);
			}
			if(_self.supportEditor != null){
				formData.append('supportEditor', _self.supportEditor);
			}
			if(_self.allowRegisterAccountObject != null){
				formData.append('allowRegisterAccountObject.local', _self.allowRegisterAccountObject.local);
				formData.append('allowRegisterAccountObject.mobile', _self.allowRegisterAccountObject.mobile);
				formData.append('allowRegisterAccountObject.weChat', _self.allowRegisterAccountObject.weChat);
				formData.append('allowRegisterAccountObject.other', _self.allowRegisterAccountObject.other);
			}
			
			if(_self.registerCaptcha != null){
				formData.append('registerCaptcha', _self.registerCaptcha);
			}
			if(_self.login_submitQuantity != null){
				formData.append('login_submitQuantity', _self.login_submitQuantity);
			}
			if(_self.topic_submitQuantity != null){
				formData.append('topic_submitQuantity', _self.topic_submitQuantity);
			}
			if(_self.comment_submitQuantity != null){
				formData.append('comment_submitQuantity', _self.comment_submitQuantity);
			}
			if(_self.question_submitQuantity != null){
				formData.append('question_submitQuantity', _self.question_submitQuantity);
			}
			if(_self.answer_submitQuantity != null){
				formData.append('answer_submitQuantity', _self.answer_submitQuantity);
			}
			if(_self.privateMessage_submitQuantity != null){
				formData.append('privateMessage_submitQuantity', _self.privateMessage_submitQuantity);
			}
			if(_self.report_submitQuantity != null){
				formData.append('report_submitQuantity', _self.report_submitQuantity);
			}
			if(_self.reportMaxImageUpload != null){
				formData.append('reportMaxImageUpload', _self.reportMaxImageUpload);
			}
			if(_self.maxQuestionTagQuantity != null){
				formData.append('maxQuestionTagQuantity', _self.maxQuestionTagQuantity);
			}
			if(_self.topic_rewardPoint != null){
				formData.append('topic_rewardPoint', _self.topic_rewardPoint);
			}
			if(_self.comment_rewardPoint != null){
				formData.append('comment_rewardPoint', _self.comment_rewardPoint);
			}
			if(_self.reply_rewardPoint != null){
				formData.append('reply_rewardPoint', _self.reply_rewardPoint);
			}
			if(_self.question_rewardPoint != null){
				formData.append('question_rewardPoint', _self.question_rewardPoint);
			}
			if(_self.answer_rewardPoint != null){
				formData.append('answer_rewardPoint', _self.answer_rewardPoint);
			}
			if(_self.answerReply_rewardPoint != null){
				formData.append('answerReply_rewardPoint', _self.answerReply_rewardPoint);
			}
			if(_self.topic_review != null){
				formData.append('topic_review', _self.topic_review);
			}
			if(_self.comment_review != null){
				formData.append('comment_review', _self.comment_review);
			}
			if(_self.reply_review != null){
				formData.append('reply_review', _self.reply_review);
			}
			if(_self.question_review != null){
				formData.append('question_review', _self.question_review);
			}
			if(_self.answer_review != null){
				formData.append('answer_review', _self.answer_review);
			}
			if(_self.answerReply_review != null){
				formData.append('answerReply_review', _self.answerReply_review);
			}
			if(_self.allowFeedback != null){
				formData.append('allowFeedback', _self.allowFeedback);
			}
			if(_self.allowTopic != null){
				formData.append('allowTopic', _self.allowTopic);
			}
			if(_self.allowComment != null){
				formData.append('allowComment', _self.allowComment);
			}
			if(_self.allowQuestion != null){
				formData.append('allowQuestion', _self.allowQuestion);
			}
			if(_self.allowAnswer != null){
				formData.append('allowAnswer', _self.allowAnswer);
			}
			if(_self.realNameUserAllowTopic != null){
				formData.append('realNameUserAllowTopic', _self.realNameUserAllowTopic);
			}
			if(_self.realNameUserAllowComment != null){
				formData.append('realNameUserAllowComment', _self.realNameUserAllowComment);
			}
			if(_self.realNameUserAllowQuestion != null){
				formData.append('realNameUserAllowQuestion', _self.realNameUserAllowQuestion);
			}
			if(_self.realNameUserAllowAnswer != null){
				formData.append('realNameUserAllowAnswer', _self.realNameUserAllowAnswer);
			}
			if(_self.allowReport != null){
				formData.append('allowReport', _self.allowReport);
			}
			if(_self.showIpAddress != null){
				formData.append('showIpAddress', _self.showIpAddress);
			}
			if(_self.topicHeatFactor != null){
				formData.append('topicHeatFactor', _self.topicHeatFactor);
			}
			if(_self.topicHotRecommendedTime != null){
				formData.append('topicHotRecommendedTime', _self.topicHotRecommendedTime);
			}
			if(_self.questionRewardPointMin != null){
				formData.append('questionRewardPointMin', _self.questionRewardPointMin);
			}
			if(_self.questionRewardPointMax != null){
				formData.append('questionRewardPointMax', _self.questionRewardPointMax);
			}
			if(_self.questionRewardAmountMin != null){
				formData.append('questionRewardAmountMin', _self.questionRewardAmountMin);
			}
			if(_self.questionRewardAmountMax != null){
				formData.append('questionRewardAmountMax', _self.questionRewardAmountMax);
			}
			
			if(_self.topicUnhidePlatformShareProportion != null){
				formData.append('topicUnhidePlatformShareProportion', _self.topicUnhidePlatformShareProportion);
			}
			if(_self.questionRewardPlatformShareProportion != null){
				formData.append('questionRewardPlatformShareProportion', _self.questionRewardPlatformShareProportion);
			}
			if(_self.giveRedEnvelopeAmountMin != null){
				formData.append('giveRedEnvelopeAmountMin', _self.giveRedEnvelopeAmountMin);
			}
			if(_self.giveRedEnvelopeAmountMax != null){
				formData.append('giveRedEnvelopeAmountMax', _self.giveRedEnvelopeAmountMax);
			}
			if(_self.allowFilterWord != null){
				formData.append('allowFilterWord', _self.allowFilterWord);
			}
			if(_self.filterWordReplace != null){
				formData.append('filterWordReplace', _self.filterWordReplace);
			}
			if(_self.fileSecureLinkSecret != null){
				formData.append('fileSecureLinkSecret', _self.fileSecureLinkSecret);
			}
			if(_self.fileSecureLinkExpire != null){
				formData.append('fileSecureLinkExpire', _self.fileSecureLinkExpire);
			}
			if(_self.forestagePageNumber != null){
				formData.append('forestagePageNumber', _self.forestagePageNumber);
			}
			if(_self.backstagePageNumber != null){
				formData.append('backstagePageNumber', _self.backstagePageNumber);
			}
			if(_self.temporaryFileValidPeriod != null){
				formData.append('temporaryFileValidPeriod', _self.temporaryFileValidPeriod);
			}
			if(_self.userSentSmsCount != null){
				formData.append('userSentSmsCount', _self.userSentSmsCount);
			}
			
			
			
			//话题编辑器
			if(_self.topicEditorTagObject.fontname != null){
				formData.append('topicEditorTagObject.fontname', _self.topicEditorTagObject.fontname);
			}
			if(_self.topicEditorTagObject.fontsize != null){
				formData.append('topicEditorTagObject.fontsize', _self.topicEditorTagObject.fontsize);
			}
			if(_self.topicEditorTagObject.forecolor != null){
				formData.append('topicEditorTagObject.forecolor', _self.topicEditorTagObject.forecolor);
			}
			if(_self.topicEditorTagObject.hilitecolor != null){
				formData.append('topicEditorTagObject.hilitecolor', _self.topicEditorTagObject.hilitecolor);
			}
			if(_self.topicEditorTagObject.bold != null){
				formData.append('topicEditorTagObject.bold', _self.topicEditorTagObject.bold);
			}
			if(_self.topicEditorTagObject.italic != null){
				formData.append('topicEditorTagObject.italic', _self.topicEditorTagObject.italic);
			}
			if(_self.topicEditorTagObject.underline != null){
				formData.append('topicEditorTagObject.underline', _self.topicEditorTagObject.underline);
			}
			if(_self.topicEditorTagObject.removeformat != null){
				formData.append('topicEditorTagObject.removeformat', _self.topicEditorTagObject.removeformat);
			}
			if(_self.topicEditorTagObject.link != null){
				formData.append('topicEditorTagObject.link', _self.topicEditorTagObject.link);
			}
			if(_self.topicEditorTagObject.unlink != null){
				formData.append('topicEditorTagObject.unlink', _self.topicEditorTagObject.unlink);
			}
			if(_self.topicEditorTagObject.justifyleft != null){
				formData.append('topicEditorTagObject.justifyleft', _self.topicEditorTagObject.justifyleft);
			}
			if(_self.topicEditorTagObject.justifycenter != null){
				formData.append('topicEditorTagObject.justifycenter', _self.topicEditorTagObject.justifycenter);
			}
			if(_self.topicEditorTagObject.justifyright != null){
				formData.append('topicEditorTagObject.justifyright', _self.topicEditorTagObject.justifyright);
			}
			if(_self.topicEditorTagObject.insertorderedlist != null){
				formData.append('topicEditorTagObject.insertorderedlist', _self.topicEditorTagObject.insertorderedlist);
			}
			if(_self.topicEditorTagObject.insertunorderedlist != null){
				formData.append('topicEditorTagObject.insertunorderedlist', _self.topicEditorTagObject.insertunorderedlist);
			}
			if(_self.topicEditorTagObject.code != null){
				formData.append('topicEditorTagObject.code', _self.topicEditorTagObject.code);
			}
			if(_self.topicEditorTagObject.emoticons != null){
				formData.append('topicEditorTagObject.emoticons', _self.topicEditorTagObject.emoticons);
			}
			if(_self.topicEditorTagObject.hidePassword != null){
				formData.append('topicEditorTagObject.hidePassword', _self.topicEditorTagObject.hidePassword);
			}
			if(_self.topicEditorTagObject.hideComment != null){
				formData.append('topicEditorTagObject.hideComment', _self.topicEditorTagObject.hideComment);
			}
			if(_self.topicEditorTagObject.hideGrade != null){
				formData.append('topicEditorTagObject.hideGrade', _self.topicEditorTagObject.hideGrade);
			}
			if(_self.topicEditorTagObject.hidePoint != null){
				formData.append('topicEditorTagObject.hidePoint', _self.topicEditorTagObject.hidePoint);
			}
			if(_self.topicEditorTagObject.hideAmount != null){
				formData.append('topicEditorTagObject.hideAmount', _self.topicEditorTagObject.hideAmount);
			}
			if(_self.topicEditorTagObject.fullscreen != null){
				formData.append('topicEditorTagObject.fullscreen', _self.topicEditorTagObject.fullscreen);
			}
			if(_self.topicEditorTagObject.image != null){
				formData.append('topicEditorTagObject.image', _self.topicEditorTagObject.image);
			}
			if(_self.topicEditorTagObject.imageFormat != null && _self.topicEditorTagObject.imageFormat.length >0){
				for(let i=0; i<_self.topicEditorTagObject.imageFormat.length; i++){
					let format = _self.topicEditorTagObject.imageFormat[i];
					if(format != null && format != "-"){//横杆由前面设置，代表空值
						formData.append('topicEditorTagObject.imageFormat', format);
					}
				}
			}
			if(_self.topicEditorTagObject.imageSize != null){
				formData.append('topicEditorTagObject.imageSize', _self.topicEditorTagObject.imageSize);
			}
			if(_self.topicEditorTagObject.file != null){
				formData.append('topicEditorTagObject.file', _self.topicEditorTagObject.file);
			}
			if(_self.topicEditorTagObject.fileFormat != null && _self.topicEditorTagObject.fileFormat.length >0){
				for(let i=0; i<_self.topicEditorTagObject.fileFormat.length; i++){
					let format = _self.topicEditorTagObject.fileFormat[i];
					if(format != null && format != "-"){//横杆由前面设置，代表空值
						formData.append('topicEditorTagObject.fileFormat', format);
					}
				}
			}
			if(_self.topicEditorTagObject.fileSize != null){
				formData.append('topicEditorTagObject.fileSize', _self.topicEditorTagObject.fileSize);
			}
			if(_self.topicEditorTagObject.embedVideo != null){
				formData.append('topicEditorTagObject.embedVideo', _self.topicEditorTagObject.embedVideo);
			}
			if(_self.topicEditorTagObject.uploadVideo != null){
				formData.append('topicEditorTagObject.uploadVideo', _self.topicEditorTagObject.uploadVideo);
			}
			if(_self.topicEditorTagObject.videoFormat != null && _self.topicEditorTagObject.videoFormat.length >0){
				for(let i=0; i<_self.topicEditorTagObject.videoFormat.length; i++){
					let format = _self.topicEditorTagObject.videoFormat[i];
					if(format != null && format != "-"){//横杆由前面设置，代表空值
						formData.append('topicEditorTagObject.videoFormat', format);
					}
				}
			}
			if(_self.topicEditorTagObject.videoSize != null){
				formData.append('topicEditorTagObject.videoSize', _self.topicEditorTagObject.videoSize);
			}
			
			
			
			//评论编辑器
			if(_self.editorTagObject.fontname != null){
				formData.append('editorTagObject.fontname', _self.editorTagObject.fontname);
			}
			if(_self.editorTagObject.fontsize != null){
				formData.append('editorTagObject.fontsize', _self.editorTagObject.fontsize);
			}
			if(_self.editorTagObject.forecolor != null){
				formData.append('editorTagObject.forecolor', _self.editorTagObject.forecolor);
			}
			if(_self.editorTagObject.hilitecolor != null){
				formData.append('editorTagObject.hilitecolor', _self.editorTagObject.hilitecolor);
			}
			if(_self.editorTagObject.bold != null){
				formData.append('editorTagObject.bold', _self.editorTagObject.bold);
			}
			if(_self.editorTagObject.italic != null){
				formData.append('editorTagObject.italic', _self.editorTagObject.italic);
			}
			if(_self.editorTagObject.underline != null){
				formData.append('editorTagObject.underline', _self.editorTagObject.underline);
			}
			if(_self.editorTagObject.removeformat != null){
				formData.append('editorTagObject.removeformat', _self.editorTagObject.removeformat);
			}
			if(_self.editorTagObject.link != null){
				formData.append('editorTagObject.link', _self.editorTagObject.link);
			}
			if(_self.editorTagObject.unlink != null){
				formData.append('editorTagObject.unlink', _self.editorTagObject.unlink);
			}
			if(_self.editorTagObject.justifyleft != null){
				formData.append('editorTagObject.justifyleft', _self.editorTagObject.justifyleft);
			}
			if(_self.editorTagObject.justifycenter != null){
				formData.append('editorTagObject.justifycenter', _self.editorTagObject.justifycenter);
			}
			if(_self.editorTagObject.justifyright != null){
				formData.append('editorTagObject.justifyright', _self.editorTagObject.justifyright);
			}
			if(_self.editorTagObject.insertorderedlist != null){
				formData.append('editorTagObject.insertorderedlist', _self.editorTagObject.insertorderedlist);
			}
			if(_self.editorTagObject.insertunorderedlist != null){
				formData.append('editorTagObject.insertunorderedlist', _self.editorTagObject.insertunorderedlist);
			}
			if(_self.editorTagObject.code != null){
				formData.append('editorTagObject.code', _self.editorTagObject.code);
			}
			if(_self.editorTagObject.emoticons != null){
				formData.append('editorTagObject.emoticons', _self.editorTagObject.emoticons);
			}
			if(_self.editorTagObject.fullscreen != null){
				formData.append('editorTagObject.fullscreen', _self.editorTagObject.fullscreen);
			}
			if(_self.editorTagObject.image != null){
				formData.append('editorTagObject.image', _self.editorTagObject.image);
			}
			if(_self.editorTagObject.imageFormat != null && _self.editorTagObject.imageFormat.length >0){
				for(let i=0; i<_self.editorTagObject.imageFormat.length; i++){
					let format = _self.editorTagObject.imageFormat[i];
					if(format != null && format != "-"){//横杆由前面设置，代表空值
						formData.append('editorTagObject.imageFormat', format);
					}
				}
			}
			if(_self.editorTagObject.imageSize != null){
				formData.append('editorTagObject.imageSize', _self.editorTagObject.imageSize);
			}
			
			
			//问题编辑器
			if(_self.questionEditorTagObject.fontname != null){
				formData.append('questionEditorTagObject.fontname', _self.questionEditorTagObject.fontname);
			}
			if(_self.questionEditorTagObject.fontsize != null){
				formData.append('questionEditorTagObject.fontsize', _self.questionEditorTagObject.fontsize);
			}
			if(_self.questionEditorTagObject.forecolor != null){
				formData.append('questionEditorTagObject.forecolor', _self.questionEditorTagObject.forecolor);
			}
			if(_self.questionEditorTagObject.hilitecolor != null){
				formData.append('questionEditorTagObject.hilitecolor', _self.questionEditorTagObject.hilitecolor);
			}
			if(_self.questionEditorTagObject.bold != null){
				formData.append('questionEditorTagObject.bold', _self.questionEditorTagObject.bold);
			}
			if(_self.questionEditorTagObject.italic != null){
				formData.append('questionEditorTagObject.italic', _self.questionEditorTagObject.italic);
			}
			if(_self.questionEditorTagObject.underline != null){
				formData.append('questionEditorTagObject.underline', _self.questionEditorTagObject.underline);
			}
			if(_self.questionEditorTagObject.removeformat != null){
				formData.append('questionEditorTagObject.removeformat', _self.questionEditorTagObject.removeformat);
			}
			if(_self.questionEditorTagObject.link != null){
				formData.append('questionEditorTagObject.link', _self.questionEditorTagObject.link);
			}
			if(_self.questionEditorTagObject.unlink != null){
				formData.append('questionEditorTagObject.unlink', _self.questionEditorTagObject.unlink);
			}
			if(_self.questionEditorTagObject.justifyleft != null){
				formData.append('questionEditorTagObject.justifyleft', _self.questionEditorTagObject.justifyleft);
			}
			if(_self.questionEditorTagObject.justifycenter != null){
				formData.append('questionEditorTagObject.justifycenter', _self.questionEditorTagObject.justifycenter);
			}
			if(_self.questionEditorTagObject.justifyright != null){
				formData.append('questionEditorTagObject.justifyright', _self.questionEditorTagObject.justifyright);
			}
			if(_self.questionEditorTagObject.insertorderedlist != null){
				formData.append('questionEditorTagObject.insertorderedlist', _self.questionEditorTagObject.insertorderedlist);
			}
			if(_self.questionEditorTagObject.insertunorderedlist != null){
				formData.append('questionEditorTagObject.insertunorderedlist', _self.questionEditorTagObject.insertunorderedlist);
			}
			if(_self.questionEditorTagObject.code != null){
				formData.append('questionEditorTagObject.code', _self.questionEditorTagObject.code);
			}
			if(_self.questionEditorTagObject.emoticons != null){
				formData.append('questionEditorTagObject.emoticons', _self.questionEditorTagObject.emoticons);
			}
			if(_self.questionEditorTagObject.fullscreen != null){
				formData.append('questionEditorTagObject.fullscreen', _self.questionEditorTagObject.fullscreen);
			}
			if(_self.questionEditorTagObject.image != null){
				formData.append('questionEditorTagObject.image', _self.questionEditorTagObject.image);
			}
			if(_self.questionEditorTagObject.imageFormat != null && _self.questionEditorTagObject.imageFormat.length >0){
				for(let i=0; i<_self.questionEditorTagObject.imageFormat.length; i++){
					let format = _self.questionEditorTagObject.imageFormat[i];
					if(format != null && format != "-"){//横杆由前面设置，代表空值
						formData.append('questionEditorTagObject.imageFormat', format);
					}
				}
			}
			if(_self.questionEditorTagObject.imageSize != null){
				formData.append('questionEditorTagObject.imageSize', _self.questionEditorTagObject.imageSize);
			}
			
			//答案编辑器
			if(_self.answerEditorTagObject.fontname != null){
				formData.append('answerEditorTagObject.fontname', _self.answerEditorTagObject.fontname);
			}
			if(_self.answerEditorTagObject.fontsize != null){
				formData.append('answerEditorTagObject.fontsize', _self.answerEditorTagObject.fontsize);
			}
			if(_self.answerEditorTagObject.forecolor != null){
				formData.append('answerEditorTagObject.forecolor', _self.answerEditorTagObject.forecolor);
			}
			if(_self.answerEditorTagObject.hilitecolor != null){
				formData.append('answerEditorTagObject.hilitecolor', _self.answerEditorTagObject.hilitecolor);
			}
			if(_self.answerEditorTagObject.bold != null){
				formData.append('answerEditorTagObject.bold', _self.answerEditorTagObject.bold);
			}
			if(_self.answerEditorTagObject.italic != null){
				formData.append('answerEditorTagObject.italic', _self.answerEditorTagObject.italic);
			}
			if(_self.answerEditorTagObject.underline != null){
				formData.append('answerEditorTagObject.underline', _self.answerEditorTagObject.underline);
			}
			if(_self.answerEditorTagObject.removeformat != null){
				formData.append('answerEditorTagObject.removeformat', _self.answerEditorTagObject.removeformat);
			}
			if(_self.answerEditorTagObject.link != null){
				formData.append('answerEditorTagObject.link', _self.answerEditorTagObject.link);
			}
			if(_self.answerEditorTagObject.unlink != null){
				formData.append('answerEditorTagObject.unlink', _self.answerEditorTagObject.unlink);
			}
			if(_self.answerEditorTagObject.justifyleft != null){
				formData.append('answerEditorTagObject.justifyleft', _self.answerEditorTagObject.justifyleft);
			}
			if(_self.answerEditorTagObject.justifycenter != null){
				formData.append('answerEditorTagObject.justifycenter', _self.answerEditorTagObject.justifycenter);
			}
			if(_self.answerEditorTagObject.justifyright != null){
				formData.append('answerEditorTagObject.justifyright', _self.answerEditorTagObject.justifyright);
			}
			if(_self.answerEditorTagObject.insertorderedlist != null){
				formData.append('answerEditorTagObject.insertorderedlist', _self.answerEditorTagObject.insertorderedlist);
			}
			if(_self.answerEditorTagObject.insertunorderedlist != null){
				formData.append('answerEditorTagObject.insertunorderedlist', _self.answerEditorTagObject.insertunorderedlist);
			}
			if(_self.answerEditorTagObject.code != null){
				formData.append('answerEditorTagObject.code', _self.answerEditorTagObject.code);
			}
			if(_self.answerEditorTagObject.emoticons != null){
				formData.append('answerEditorTagObject.emoticons', _self.answerEditorTagObject.emoticons);
			}
			if(_self.answerEditorTagObject.fullscreen != null){
				formData.append('answerEditorTagObject.fullscreen', _self.answerEditorTagObject.fullscreen);
			}
			if(_self.answerEditorTagObject.image != null){
				formData.append('answerEditorTagObject.image', _self.answerEditorTagObject.image);
			}
			if(_self.answerEditorTagObject.imageFormat != null && _self.answerEditorTagObject.imageFormat.length >0){
				for(let i=0; i<_self.answerEditorTagObject.imageFormat.length; i++){
					let format = _self.answerEditorTagObject.imageFormat[i];
					if(format != null && format != "-"){//横杆由前面设置，代表空值
						formData.append('answerEditorTagObject.imageFormat', format);
					}
				}
			}
			if(_self.answerEditorTagObject.imageSize != null){
				formData.append('answerEditorTagObject.imageSize', _self.answerEditorTagObject.imageSize);
			}
			
			
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/systemSetting/manage/edit',
		        data: formData
			})
			.then(function (response) {
				if(response == null){
					return;
				}
				
			    let result = response.data;
			    if(result){
			    	let returnValue = JSON.parse(result);
			    	if(returnValue.code === 200){//成功
			    		_self.$message.success("提交成功");
			    		
			    		//删除缓存
			    		_self.$store.commit('setCacheNumber');
			    		
			    		_self.queryEditSystemSetting();
			    	}else if(returnValue.code === 500){//错误
			    		
			    		let errorMap = returnValue.data;
			    		A:for (let key in errorMap) {   
			    			
			    			if(key.indexOf(".") != -1){//topicEditorTagObject.imageFormat
			    				let parameterName_first = key.split(".")[0];
			    				let parameterName_two = key.split(".")[1];
			    				//设置返回错误提示值到错误对象中
			    				for (let attribute_key in _self.error) {
			    					if(attribute_key == parameterName_first){
			    						let child = _self.error[attribute_key]
			    						for(let attribute_child_key in child){
			    							
			    							if(attribute_child_key == parameterName_two){
			    								child[attribute_child_key] = errorMap[key];
			    								continue A;
			    							}
			    							
			    						}
			    					}
			    				}
			    			}
			    			
			    			if(_self.error[key] == undefined){
			    				_self.$message({
									duration :0,
						            showClose: true,
						            message: errorMap[key],
						            type: 'error'
						        });
			    			}else{
			    				_self.error[key] = errorMap[key];
			    			}
			    	    }
			    		
			    	}
			    }
			    _self.submitForm_disabled = false;
			})
			.catch(function (error) {
				console.log(error);
			});
	    }
	}
});

</script>