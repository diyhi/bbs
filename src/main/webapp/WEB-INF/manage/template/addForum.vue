<!-- 添加版块 -->
<template id="addForum-template">
	<div>
		<div class="main" >
			<div class="nav-breadcrumb">
				<el-breadcrumb separator-class="el-icon-arrow-right">
					<el-breadcrumb-item :to="{ path: '/admin/control/template/list' }">模板列表</el-breadcrumb-item>
					<el-breadcrumb-item :to="{ path: '/admin/control/layout/list',query:{ dirName : $route.query.dirName} }">布局列表</el-breadcrumb-item>
					<el-breadcrumb-item :to="{ path: '/admin/control/forum/list',query:{ layoutId : $route.query.layoutId,dirName : $route.query.dirName} }">版块列表</el-breadcrumb-item>
					<el-breadcrumb-item>{{templates.name}} [{{templates.dirName}}] [{{layout.name}}]</el-breadcrumb-item>
					<el-breadcrumb-item>添加版块</el-breadcrumb-item>
				</el-breadcrumb>
			</div>
			<div class="data-form label-width-blank forumModule" >
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="版块类型" :required="true" :error="error.forumType">
						<el-select v-model="forumType" @change="selectedForumType" placeholder="选择类型">
							<el-option v-for="item in forumTypeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
						</el-select>
						<el-select class="placeholder" v-model="forumChildType" placeholder="选择子类型">
							<el-option v-for="item in forumChildTypeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
						</el-select>
					</el-form-item>
					<el-form-item label="版块标题" :required="true" :error="error.name">
						<el-row><el-col :span="18"><el-input v-model.trim="name" maxlength="30" clearable="true" show-word-limit></el-input></el-col></el-row>
					</el-form-item>
					<el-form-item label="版块模板" :required="true" :error="error.module">
						<el-select v-model="module" @change="selectedModule" placeholder="选择模板" style="width: 100%">
							<el-option v-for="item in moduleOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
						</el-select>
					</el-form-item>
					<el-form-item label="调用方式" :required="true" :error="error.invokeMethod">
						<el-radio-group v-model="invokeMethod" :disabled="(layout.type == 4 && layout.returnData == 1) || layout.type == 6">
						    <el-radio :label="1">引用代码</el-radio>
						    <el-radio :label="2">调用对象</el-radio>
						</el-radio-group>
						<div class="form-help" v-if="(layout.type == 4 && layout.returnData == 1) || layout.type == 6">空白页json方式返回数据或公共页(引用版块值) 这两种布局方式不能选择'调用对象'</div>
					</el-form-item>
					

					
					<!-- 话题部分--话题列表  分页-->
					<div v-if="forumType == '话题' && forumChildType == '话题列表' && displayType == '分页'">
						<el-form-item label="选择标签" :error="error.page_Forum_TopicRelated_Topic.page_topic_tagId">
							<el-select v-model="page_Forum_TopicRelated_Topic.tagIdGroup"  v-show="page_Forum_TopicRelated_Topic.page_topic_tag_transferPrameter == false" @change="selectedTopicTag" @focus="loadTopicTag" multiple no-match-text="还没有标签" placeholder="请选择">
								<el-option v-for="item in page_Forum_TopicRelated_Topic.tagOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
							</el-select>
							<el-switch class="placeholder" v-model="page_Forum_TopicRelated_Topic.page_topic_tag_transferPrameter" active-text="传递标签参数">
						</el-form-item>
						<el-form-item label="排序" :required="true" :error="error.page_Forum_TopicRelated_Topic.page_topic_sort">
							<el-select v-model="page_Forum_TopicRelated_Topic.page_topic_sort" placeholder="请选择">
								<el-option v-for="item in page_Forum_TopicRelated_Topic.sortOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
							</el-select>
						</el-form-item>
						<el-form-item label="每页显示记录数" :error="error.page_Forum_TopicRelated_Topic.page_topic_maxResult" >
							<el-row><el-col :span="8"><el-input v-model.trim="page_Forum_TopicRelated_Topic.page_topic_maxResult" maxlength="9" clearable="true" show-word-limit></el-input></el-col></el-row>
						</el-form-item>
						<el-form-item label="页码显示总数" :error="error.page_Forum_TopicRelated_Topic.page_topic_pageCount" >
							<el-row><el-col :span="8"><el-input v-model.trim="page_Forum_TopicRelated_Topic.page_topic_pageCount" maxlength="9" clearable="true" show-word-limit></el-input></el-col></el-row>
						</el-form-item>
					</div>
					<!-- 评论部分--评论列表  分页-->
					<div v-if="forumType == '话题' && forumChildType == '评论列表' && displayType == '分页'">
						<el-form-item label="排序" :required="true" :error="error.page_Forum_CommentRelated_Comment.page_comment_sort">
							<el-select v-model="page_Forum_CommentRelated_Comment.page_comment_sort" placeholder="请选择">
								<el-option v-for="item in page_Forum_CommentRelated_Comment.sortOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
							</el-select>
						</el-form-item>
						<el-form-item label="每页显示记录数" :error="error.page_Forum_CommentRelated_Comment.page_comment_maxResult" >
							<el-row><el-col :span="8"><el-input v-model.trim="page_Forum_CommentRelated_Comment.page_comment_maxResult" maxlength="9" clearable="true" show-word-limit></el-input></el-col></el-row>
						</el-form-item>
						<el-form-item label="页码显示总数" :error="error.page_Forum_CommentRelated_Comment.page_comment_pageCount" >
							<el-row><el-col :span="8"><el-input v-model.trim="page_Forum_CommentRelated_Comment.page_comment_pageCount" maxlength="9" clearable="true" show-word-limit></el-input></el-col></el-row>
						</el-form-item>
					</div>
					<!-- 话题部分--相似话题  集合-->
					<div v-if="forumType == '话题' && forumChildType == '相似话题' && displayType == '集合'">
						<el-form-item label="显示记录数" :error="error.collection_Forum_TopicRelated_LikeTopic.collection_likeTopic_maxResult" >
							<el-row><el-col :span="8"><el-input v-model.trim="collection_Forum_TopicRelated_LikeTopic.collection_likeTopic_maxResult" maxlength="9" clearable="true" show-word-limit></el-input></el-col></el-row>
						</el-form-item>
					</div>
					<!-- 话题部分--热门话题  集合-->
					<div v-if="forumType == '话题' && forumChildType == '热门话题' && displayType == '集合'">
						<el-form-item label="显示记录数" :error="error.collection_Forum_TopicRelated_HotTopic.collection_hotTopic_maxResult" >
							<el-row><el-col :span="8"><el-input v-model.trim="collection_Forum_TopicRelated_HotTopic.collection_hotTopic_maxResult" maxlength="9" clearable="true" show-word-limit></el-input></el-col></el-row>
						</el-form-item>
					</div>



					<!-- 问题部分--问题列表  分页-->
					<div v-if="forumType == '问答' && forumChildType == '问题列表' && displayType == '分页'">
						<el-form-item label="选择标签" :error="error.page_Forum_QuestionRelated_Question.page_question_tagId">
							<el-select ref="page_question_tag_ref" v-model="page_Forum_QuestionRelated_Question.tagIdGroup" v-show="page_Forum_QuestionRelated_Question.page_question_tag_transferPrameter == false" @focus="loadQuestionTag" multiple placeholder="请选择">
								<el-option v-for="item in page_Forum_QuestionRelated_Question.tagOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
							</el-select>
							<el-switch class="placeholder" v-model="page_Forum_QuestionRelated_Question.page_question_tag_transferPrameter" active-text="传递标签参数">
						</el-form-item>
						<el-form-item label="过滤条件" :error="error.page_Forum_QuestionRelated_Question.page_question_filterCondition">
							<el-select v-model="page_Forum_QuestionRelated_Question.page_question_filterCondition" v-show="page_Forum_QuestionRelated_Question.page_question_filterCondition_transferPrameter == false" placeholder="请选择">
								<el-option v-for="item in page_Forum_QuestionRelated_Question.filterConditionOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
							</el-select>
							<el-switch class="placeholder" v-model="page_Forum_QuestionRelated_Question.page_question_filterCondition_transferPrameter" active-text="传递过滤参数">
						</el-form-item>
						<div class="form-help" >选择'传递过滤参数'时接收参数字段为filterCondition，全部：值为空或10 &nbsp; 未解决：20 &nbsp; 已解决：30 &nbsp; 积分悬赏：40 &nbsp; 现金悬赏：50</div>
					
						<el-form-item label="排序" :error="error.page_Forum_QuestionRelated_Question.page_question_sort">
							<el-select v-model="page_Forum_QuestionRelated_Question.page_question_sort" placeholder="请选择">
								<el-option v-for="item in page_Forum_QuestionRelated_Question.sortOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
							</el-select>
						</el-form-item>
						<el-form-item label="每页显示记录数" :error="error.page_Forum_QuestionRelated_Question.page_question_maxResult" >
							<el-row><el-col :span="8"><el-input v-model.trim="page_Forum_QuestionRelated_Question.page_question_maxResult" maxlength="9" clearable="true" show-word-limit></el-input></el-col></el-row>
						</el-form-item>
						<el-form-item label="页码显示总数" :error="error.page_Forum_QuestionRelated_Question.page_question_pageCount" >
							<el-row><el-col :span="8"><el-input v-model.trim="page_Forum_QuestionRelated_Question.page_question_pageCount" maxlength="9" clearable="true" show-word-limit></el-input></el-col></el-row>
						</el-form-item>
						
						
						<el-dialog title="选择标签" v-model="page_Forum_QuestionRelated_Question.question_tag_form">
							<div class="dialog-nav-breadcrumb">
								<el-breadcrumb separator-class="el-icon-arrow-right">
									<el-breadcrumb-item @click="queryQuestionTag(1,'')">全部标签</el-breadcrumb-item>
									<el-breadcrumb-item v-for="(value, key) in page_Forum_QuestionRelated_Question.navigation" @click="queryQuestionTag(1,key)">{{value}}</el-breadcrumb-item>
									
								</el-breadcrumb>
							</div>
							<div class="dialog-data-table" >
								<el-table :data="page_Forum_QuestionRelated_Question.tableData" @cell-click="cellExpandRow_questionTag" :show-header="false" tooltip-effect="dark" style="width: 100%" @selection-change="handleSelectionChange" stripe empty-text="没有内容">
									<el-table-column label="选择" align="right" width="50">
										<template #default="scope">
											<el-radio v-model="page_Forum_QuestionRelated_Question.page_question_tagId" v-if="scope.row.childNodeNumber ==0" :label="page_Forum_QuestionRelated_Question.tagIdList[scope.$index]" >&nbsp;</el-radio>
								    	</template>
									</el-table-column>
									<el-table-column label="标签名称">
										<template #default="scope">
											<i class="icon icon-folder el-icon-folder" v-if="scope.row.childNodeNumber >0"></i>
											<i class="icon icon-file el-icon-document" v-if="scope.row.childNodeNumber ==0"></i>{{scope.row.name}}
								    	</template>
									</el-table-column>
								</el-table>
								<div class="pagination-wrapper" v-if="page_Forum_QuestionRelated_Question.isShowPage">
									<el-pagination background  @current-change="questionTagPage" :current-page="page_Forum_QuestionRelated_Question.currentpage"  :page-size="page_Forum_QuestionRelated_Question.maxresult" layout="total, prev, pager, next,jumper" :total="page_Forum_QuestionRelated_Question.totalrecord"></el-pagination>
								</div>
							</div>
						</el-dialog>
					</div>
					<!-- 答案部分--答案列表  分页-->
					<div v-if="forumType == '问答' && forumChildType == '答案列表' && displayType == '分页'">
						<el-form-item label="排序" :required="true" :error="error.page_Forum_AnswerRelated_Answer.page_answer_sort">
							<el-select v-model="page_Forum_AnswerRelated_Answer.page_answer_sort" placeholder="请选择">
								<el-option v-for="item in page_Forum_AnswerRelated_Answer.sortOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
							</el-select>
						</el-form-item>
						<el-form-item label="每页显示记录数" :error="error.page_Forum_AnswerRelated_Answer.page_answer_maxResult" >
							<el-row><el-col :span="8"><el-input v-model.trim="page_Forum_AnswerRelated_Answer.page_answer_maxResult" maxlength="9" clearable="true" show-word-limit></el-input></el-col></el-row>
						</el-form-item>
						<el-form-item label="页码显示总数" :error="error.page_Forum_AnswerRelated_Answer.page_answer_pageCount" >
							<el-row><el-col :span="8"><el-input v-model.trim="page_Forum_AnswerRelated_Answer.page_answer_pageCount" maxlength="9" clearable="true" show-word-limit></el-input></el-col></el-row>
						</el-form-item>
					</div>
					<!-- 问题部分--相似问题  集合-->
					<div v-if="forumType == '问答' && forumChildType == '相似问题' && displayType == '集合'">
						<el-form-item label="显示记录数" :error="error.collection_Forum_QuestionRelated_LikeQuestion.collection_likeQuestion_maxResult" >
							<el-row><el-col :span="8"><el-input v-model.trim="collection_Forum_QuestionRelated_LikeQuestion.collection_likeQuestion_maxResult" maxlength="9" clearable="true" show-word-limit></el-input></el-col></el-row>
						</el-form-item>
					</div>
					<!-- 红包部分--领取红包用户列表 分页 -->
					<div v-if="forumType == '红包' && forumChildType == '领取红包用户列表' && displayType == '分页'">
						<el-form-item label="排序" :required="true" :error="error.page_Forum_RedEnvelopeRelated_ReceiveRedEnvelopeUser.page_receiveRedEnvelopeUser_sort">
							<el-select v-model="page_Forum_RedEnvelopeRelated_ReceiveRedEnvelopeUser.page_receiveRedEnvelopeUser_sort" placeholder="请选择">
								<el-option v-for="item in page_Forum_RedEnvelopeRelated_ReceiveRedEnvelopeUser.sortOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
							</el-select>
						</el-form-item>
						<el-form-item label="显示记录数" :error="error.page_Forum_RedEnvelopeRelated_ReceiveRedEnvelopeUser.page_receiveRedEnvelopeUser_maxResult" >
							<el-row><el-col :span="8"><el-input v-model.trim="page_Forum_RedEnvelopeRelated_ReceiveRedEnvelopeUser.page_receiveRedEnvelopeUser_maxResult" maxlength="9" clearable="true" show-word-limit></el-input></el-col></el-row>
						</el-form-item>
					</div>
					<!-- 广告部分--图片广告 集合 -->
					<div v-if="forumType == '广告' && forumChildType == '图片广告' && displayType == '集合'" class="table-container">
						<el-table ref="collection_Forum_AdvertisingRelated_imageList_table" :data="collection_Forum_AdvertisingRelated_imageList.tableList" row-key="key" tooltip-effect="dark" :show-header="false" stripe style="width: 100%" empty-text="没有项">
							<el-table-column label="表单" >
								<template #default="scope">	
									<el-form-item label="图片名称" :required="true" :error="error.collection_Forum_AdvertisingRelated_imageList.collection_image_name.get(scope.$index)">
										<el-input v-model.trim="collection_Forum_AdvertisingRelated_imageList.collection_image_name[scope.$index]" maxlength="40" clearable="true" show-word-limit></el-input>
									</el-form-item>
									<el-form-item label="图片链接" :error="error.collection_Forum_AdvertisingRelated_imageList.collection_image_link.get(scope.$index)">
										<el-input v-model.trim="collection_Forum_AdvertisingRelated_imageList.collection_image_link[scope.$index]" maxlength="200" clearable="true" show-word-limit></el-input>
									</el-form-item>
									<el-form-item label="图片" :error="error.collection_Forum_AdvertisingRelated_imageList.collection_image_imagePath.get(scope.$index)" >
										<!-- 使用v-for 循环时, 使用ref总会获取到的是最后的元素, 必须使用函数, 手动赋值, 不能用push, 会在更新的时候造成bug, 元素会重复 -->
										<el-upload action="#" :ref="el => collection_Forum_AdvertisingRelated_imageList.uploadImg_elementNodes[scope.$index]=el" list-type="picture-card" :auto-upload="false" :on-change="advertising_imageList_handleChange" :accept="'image/*'">
										    <template #default>
										    	<i class="el-icon-plus"></i>
										    </template>
										    <template #file="{file}">
										    	<div>
										        <img class="el-upload-list__item-thumbnail" :src="file.url" alt="" />
										        <span class="el-upload-list__item-actions">
										        	<span class="el-upload-list__item-preview" @click="advertising_imageList_openImagePreview(file)">
										            	<i class="el-icon-zoom-in"></i>
										        	</span>
										        	<span v-if="!disabled" class="el-upload-list__item-delete" @click="advertising_imageList_handleImageRemove(file,scope.$index)">
										            	<i class="el-icon-delete"></i>
										        	</span>
										        </span>
										    	</div>
										    </template>
										</el-upload>
									</el-form-item>
						    	</template>
							</el-table-column>
							<el-table-column label="操作" align="right" width="200">
								<template #default="scope">	
									<el-button icon="el-icon-top" circle @click.prevent="collection_Forum_AdvertisingRelated_imageList_moveUp(scope.row,scope.$index)" title="上移"></el-button>
									<el-button icon="el-icon-bottom" circle @click.prevent="collection_Forum_AdvertisingRelated_imageList_moveDown(scope.row,scope.$index)" title="下移"></el-button>
									<el-button icon="el-icon-delete" circle @click.prevent="collection_Forum_AdvertisingRelated_imageList_removeItem(scope.row,scope.$index)" title="删除"></el-button>
						    	</template>
							</el-table-column>
							
						</el-table>
						<el-form-item>
							<div class="item-button"><el-button icon="el-icon-plus" @click="advertising_imageList_addItem">添加项</el-button></div>
							<!-- 插入透明1像素图片占位-->
							<el-image ref="collection_Forum_AdvertisingRelated_imageList_imageViewer" style="width: 0px;height: 0px;" src="data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7" :preview-src-list="[collection_Forum_AdvertisingRelated_imageList.localImageUrl]" hide-on-click-modal />					
						</el-form-item>
						
					</div>
					<!-- 在线帮助部分--在线帮助列表  单层-->
					<div v-if="forumType == '在线帮助' && forumChildType == '在线帮助列表' && displayType == '单层'">
						<el-form-item label="选择分类" :error="error.monolayer_Forum_HelpRelated_Help.monolayer_help_helpTypeId">
							<el-select ref="monolayer_helpType_ref" v-model="monolayer_Forum_HelpRelated_Help.helpTypeIdGroup" v-if="monolayer_Forum_HelpRelated_Help.monolayer_help_helpType_transferPrameter == false" @focus="load_monolayer_helpType" multiple placeholder="请选择">
								<el-option v-for="item in monolayer_Forum_HelpRelated_Help.helpTypeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
							</el-select>
							<el-switch class="placeholder" v-model="monolayer_Forum_HelpRelated_Help.monolayer_help_helpType_transferPrameter" active-text="传递分类参数">
						</el-form-item>
						<el-form-item label="展示数量" :error="error.monolayer_Forum_HelpRelated_Help.monolayer_help_quantity" >
							<el-row><el-col :span="8"><el-input v-model.trim="monolayer_Forum_HelpRelated_Help.monolayer_help_quantity" maxlength="9" clearable="true" show-word-limit></el-input></el-col></el-row>
						</el-form-item>
						<el-form-item label="更多" :error="error.monolayer_Forum_HelpRelated_Help.monolayer_help_more">
							<el-select v-model="monolayer_Forum_HelpRelated_Help.monolayer_help_more" placeholder="请选择">
								<el-option v-for="item in monolayer_Forum_HelpRelated_Help.moreOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
							</el-select>
						</el-form-item>
						<el-form-item label="'更多'每页显示记录数" :error="error.monolayer_Forum_HelpRelated_Help.monolayer_help_maxResult" >
							<el-row><el-col :span="8"><el-input v-model.trim="monolayer_Forum_HelpRelated_Help.monolayer_help_maxResult" maxlength="9" clearable="true" show-word-limit></el-input></el-col></el-row>
						</el-form-item>
						<el-form-item label="'更多'页码显示总数" :error="error.monolayer_Forum_HelpRelated_Help.monolayer_help_pageCount" >
							<el-row><el-col :span="8"><el-input v-model.trim="monolayer_Forum_HelpRelated_Help.monolayer_help_pageCount" maxlength="9" clearable="true" show-word-limit></el-input></el-col></el-row>
						</el-form-item>
						<el-form-item label="排序" :error="error.monolayer_Forum_HelpRelated_Help.monolayer_help_sort">
							<el-select v-model="monolayer_Forum_HelpRelated_Help.monolayer_help_sort" placeholder="请选择">
								<el-option v-for="item in monolayer_Forum_HelpRelated_Help.sortOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
							</el-select>
						</el-form-item>
						
						<el-dialog title="选择分类" v-model="monolayer_Forum_HelpRelated_Help.helpType_form">
							<div class="dialog-nav-breadcrumb">
								<el-breadcrumb separator-class="el-icon-arrow-right">
									<el-breadcrumb-item @click="query_monolayer_helpType(1,'')">全部标签</el-breadcrumb-item>
									<el-breadcrumb-item v-for="(value, key) in monolayer_Forum_HelpRelated_Help.navigation" @click="query_monolayer_helpType(1,key)">{{value}}</el-breadcrumb-item>
									
								</el-breadcrumb>
							</div>
							<div class="dialog-data-table" >
								<el-table :data="monolayer_Forum_HelpRelated_Help.tableData" @cell-click="cellExpandRow_monolayer_helpType" :show-header="false" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
									<el-table-column label="选择" align="right" width="50">
										<template #default="scope">
											<el-radio v-model="monolayer_Forum_HelpRelated_Help.monolayer_help_helpTypeId" v-if="scope.row.childNodeNumber ==0" :label="monolayer_Forum_HelpRelated_Help.helpTypeIdList[scope.$index]" >&nbsp;</el-radio>
								    	</template>
									</el-table-column>
									<el-table-column label="分类名称">
										<template #default="scope">
											<i class="icon icon-folder el-icon-folder" v-if="scope.row.childNodeNumber >0"></i>
											<i class="icon icon-file el-icon-document" v-if="scope.row.childNodeNumber ==0"></i>{{scope.row.name}}
								    	</template>
									</el-table-column>
								</el-table>
								<div class="pagination-wrapper" v-if="monolayer_Forum_HelpRelated_Help.isShowPage">
									<el-pagination background  @current-change="monolayer_helpTypePage" :current-page="monolayer_Forum_HelpRelated_Help.currentpage"  :page-size="monolayer_Forum_HelpRelated_Help.maxresult" layout="total, prev, pager, next,jumper" :total="monolayer_Forum_HelpRelated_Help.totalrecord"></el-pagination>
								</div>
							</div>
						</el-dialog>
					</div>
					<!-- 在线帮助部分--在线帮助列表  分页-->
					<div v-if="forumType == '在线帮助' && forumChildType == '在线帮助列表' && displayType == '分页'">
						<el-form-item label="选择分类" :error="error.page_Forum_HelpRelated_Help.page_help_helpTypeId">
							<el-select ref="page_helpType_ref" v-model="page_Forum_HelpRelated_Help.helpTypeIdGroup" v-if="page_Forum_HelpRelated_Help.page_help_helpType_transferPrameter == false" @focus="load_page_helpType" multiple placeholder="请选择">
								<el-option v-for="item in page_Forum_HelpRelated_Help.helpTypeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
							</el-select>
							<el-switch class="placeholder" v-model="page_Forum_HelpRelated_Help.page_help_helpType_transferPrameter" active-text="传递分类参数">
						</el-form-item>
						<el-form-item label="排序" :error="error.page_Forum_HelpRelated_Help.page_help_sort">
							<el-select v-model="page_Forum_HelpRelated_Help.page_help_sort" placeholder="请选择">
								<el-option v-for="item in page_Forum_HelpRelated_Help.sortOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
							</el-select>
						</el-form-item>
						<el-form-item label="每页显示记录数" :error="error.page_Forum_HelpRelated_Help.page_help_maxResult" >
							<el-row><el-col :span="8"><el-input v-model.trim="page_Forum_HelpRelated_Help.page_help_maxResult" maxlength="9" clearable="true" show-word-limit></el-input></el-col></el-row>
						</el-form-item>
						<el-form-item label="页码显示总数" :error="error.page_Forum_HelpRelated_Help.page_help_pageCount" >
							<el-row><el-col :span="8"><el-input v-model.trim="page_Forum_HelpRelated_Help.page_help_pageCount" maxlength="9" clearable="true" show-word-limit></el-input></el-col></el-row>
						</el-form-item>
						
						
						
						<el-dialog title="选择分类" v-model="page_Forum_HelpRelated_Help.helpType_form">
							<div class="dialog-nav-breadcrumb">
								<el-breadcrumb separator-class="el-icon-arrow-right">
									<el-breadcrumb-item @click="query_page_helpType(1,'')">全部标签</el-breadcrumb-item>
									<el-breadcrumb-item v-for="(value, key) in page_Forum_HelpRelated_Help.navigation" @click="query_page_helpType(1,key)">{{value}}</el-breadcrumb-item>
									
								</el-breadcrumb>
							</div>
							<div class="dialog-data-table" >
								<el-table :data="page_Forum_HelpRelated_Help.tableData" @cell-click="cellExpandRow_page_helpType" :show-header="false" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
									<el-table-column label="选择" align="right" width="50">
										<template #default="scope">
											<el-radio v-model="page_Forum_HelpRelated_Help.page_help_helpTypeId" v-if="scope.row.childNodeNumber ==0" :label="page_Forum_HelpRelated_Help.helpTypeIdList[scope.$index]" >&nbsp;</el-radio>
								    	</template>
									</el-table-column>
									<el-table-column label="分类名称">
										<template #default="scope">
											<i class="icon icon-folder el-icon-folder" v-if="scope.row.childNodeNumber >0"></i>
											<i class="icon icon-file el-icon-document" v-if="scope.row.childNodeNumber ==0"></i>{{scope.row.name}}
								    	</template>
									</el-table-column>
								</el-table>
								<div class="pagination-wrapper" v-if="page_Forum_HelpRelated_Help.isShowPage">
									<el-pagination background  @current-change="page_helpTypePage" :current-page="page_Forum_HelpRelated_Help.currentpage"  :page-size="page_Forum_HelpRelated_Help.maxresult" layout="total, prev, pager, next,jumper" :total="page_Forum_HelpRelated_Help.totalrecord"></el-pagination>
								</div>
							</div>
						</el-dialog>
					</div>
					
					<!-- 在线帮助部分--推荐在线帮助  集合-->
					<div v-if="forumType == '在线帮助' && forumChildType == '推荐在线帮助' && displayType == '集合'" class="table-item-container">
						<el-table ref="collection_Forum_HelpRelated_RecommendHelp_table" :data="collection_Forum_HelpRelated_RecommendHelp.tableList" row-key="key" tooltip-effect="dark" :show-header="false" stripe style="width: 100%" empty-text="没有项">
							<el-table-column label="索引" align="center" width="50">
								<template #default="scope">	
									{{scope.$index+1}}
						    	</template>
							</el-table-column>
							<el-table-column label="帮助名称" >
								<template #default="scope">	
									{{scope.row.name}}
						    	</template>
							</el-table-column>
							<el-table-column label="操作" align="right" width="200">
								<template #default="scope">	
									<el-button icon="el-icon-top" circle @click.prevent="collection_Forum_HelpRelated_RecommendHelp_moveUp(scope.row,scope.$index)" title="上移"></el-button>
									<el-button icon="el-icon-bottom" circle @click.prevent="collection_Forum_HelpRelated_RecommendHelp_moveDown(scope.row,scope.$index)" title="下移"></el-button>
									<el-button icon="el-icon-delete" circle @click.prevent="collection_Forum_HelpRelated_RecommendHelp_removeItem(scope.row,scope.$index)" title="删除"></el-button>
						    	</template>
							</el-table-column>
							
						</el-table>
						<el-form-item>
							<div class="item-button"><el-button icon="el-icon-plus" @click="collection_Forum_HelpRelated_RecommendHelp_loadHelp">添加项</el-button></div>
						</el-form-item>
					
						<el-dialog title="选择帮助" v-model="collection_Forum_HelpRelated_RecommendHelp.help_form">
							<div class="dialog-nav-search">
								<el-form :inline="true" label-width="auto" @submit.native.prevent   >
									<el-form-item :error="error.collection_Forum_HelpRelated_RecommendHelp.keyword" >
										<el-tooltip content="不限制请留空" placement="top">
											<el-input v-model.trim="collection_Forum_HelpRelated_RecommendHelp.keyword" @keyup.enter.native="collection_Forum_HelpRelated_RecommendHelp_helpPage(1)" maxlength="50" placeholder="关键词"></el-input>
										</el-tooltip>
									</el-form-item>
									<el-form-item >
									    <el-button type="primary" class="submitButton" @click="collection_Forum_HelpRelated_RecommendHelp_helpPage(1)" >搜索</el-button>
									</el-form-item>
									
								</el-form>
							</div>
							<div class="dialog-data-table" >
								<el-table :data="collection_Forum_HelpRelated_RecommendHelp.tableData" @cell-click="cellExpandRow_collection_Forum_HelpRelated_RecommendHelp" :show-header="false" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
									<el-table-column label="选择" align="right" width="50">
										<template #default="scope">
											<el-checkbox v-model="collection_Forum_HelpRelated_RecommendHelp.helpIdGroup[scope.$index]" @click.native.prevent="radioClickEvent($event)">&nbsp;</el-checkbox>
								    	</template>
									</el-table-column>
									<el-table-column label="帮助名称">
										<template #default="scope">
											{{scope.row.name}}
								    	</template>
									</el-table-column>
								</el-table>
								<div class="pagination-wrapper" v-if="collection_Forum_HelpRelated_RecommendHelp.isShowPage">
									<el-pagination background  @current-change="collection_Forum_HelpRelated_RecommendHelp_helpPage" :current-page="collection_Forum_HelpRelated_RecommendHelp.currentpage"  :page-size="collection_Forum_HelpRelated_RecommendHelp.maxresult" layout="total, prev, pager, next,jumper" :total="collection_Forum_HelpRelated_RecommendHelp.totalrecord"></el-pagination>
								</div>
							</div>
						</el-dialog>
					</div>
					
					<!-- 自定义版块 -- 用户自定义HTML--实体对象 -->
					<div v-show="forumType == '自定义版块' && forumChildType == '用户自定义HTML' && displayType == '实体对象'" >
						<el-form-item label="内容" :error="error.entityBean_Forum_CustomForumRelated_CustomHTML.htmlContent" >
							<textarea ref="entityBean_customForum_htmlContent" style="width:99%;height:400px;visibility:hidden;"></textarea>
						</el-form-item>
					</div>
					
					<!-- 系统--热门搜索词  集合-->
					<div v-show="forumType == '系统' && forumChildType == '热门搜索词' && displayType == '集合'" >
						<el-form-item v-for="(formOption, index) in collection_Forum_SystemRelated_SearchWord.formOptions" label="搜索词"  :prop="'collection_Forum_SystemRelated_SearchWord.formOptions.' + index + '.value'">
							<el-row :gutter="10">
								<el-col :span="14">
									<el-input v-model.trim="formOption.value" maxlength="40"></el-input>
								</el-col>
								<el-col :span="6">
									<el-button icon="el-icon-top" circle @click.prevent="collection_Forum_SystemRelated_SearchWord_moveUp(formOption)" title="上移"></el-button>
									<el-button icon="el-icon-bottom" circle @click.prevent="collection_Forum_SystemRelated_SearchWord_moveDown(formOption)" title="下移"></el-button>
									<el-button icon="el-icon-delete" circle @click.prevent="collection_Forum_SystemRelated_SearchWord_removeItem(formOption)" title="删除"></el-button>
								</el-col>
							</el-row>
							
						</el-form-item>
						<el-form-item>
							<div class="item-button"><el-button icon="el-icon-plus" @click="collection_Forum_SystemRelated_SearchWord_addItem">添加项</el-button></div>
						</el-form-item>
					</div>
					
					<el-form-item>
					    <el-button type="primary" class="submitButton" @click="submitForm" :disabled="submitForm_disabled">提交</el-button>
					</el-form-item>
				</el-form>
				
				
			</div>
		</div>
	</div>
</template>

<script>
//添加版块
export default({
	name: 'addForum',//组件名称，keep-alive缓存需要本参数
	template : '#addForum-template',
	inject:['reload'], 
	data : function data() {
		return {
			layoutId :'',
			layout: '',
			templates: '',
		
			dirName: '',
			forumType: '',//版块类型
			forumChildType: '',//版块子类型
			forumCodeNodeList:[],//版块代码节点列表选项
			forumTypeOptions: [],//版块类型选项
	        forumChildTypeOptions:[],//版块子类型选项
	        moduleOptions:[],//模板选项
	        
	        name: '',//版块标题
	        module: '',//版块模板
	        invokeMethod: 1,//调用方式
			displayType :'',//模板显示类型
			
			
			page_Forum_TopicRelated_Topic :{//话题部分--话题列表  分页
				isAllowLoadTagGroup:true,//是否允许加载标签组	
				tagIdGroup :[],//标签Id组
				tagOptions:[],
				
				sortOptions: [{//排序
						value: 1,
						label: '按发表时间 新 -> 旧'
					}, {
						value: 2,
						label: '按发表时间 旧 -> 新'
		        	}, {
			        	value: 3,
			        	label: '按回复时间 新 -> 旧'
			        }, {
			        	value: 4,
			        	label: '按回复时间 旧 -> 新'
			        }],
			        
				page_topic_tagId: '',
				page_topic_tagName :'',//标签名称
				page_topic_tag_transferPrameter: false,
				page_topic_sort: 1,
				page_topic_maxResult: '',
				page_topic_pageCount: '',
			},
			
			page_Forum_CommentRelated_Comment :{//话题部分--评论列表  分页
				sortOptions: [{//排序
						value: 1,
						label: '按发布时间 新 -> 旧'
					}, {
						value: 2,
						label: '按发布时间 旧 -> 新'
		        	}],
				page_comment_sort: 1,
				page_comment_maxResult: '',
				page_comment_pageCount: '',
			},
			collection_Forum_TopicRelated_LikeTopic :{//话题部分--相似话题  集合
				collection_likeTopic_maxResult: '',
			},
			collection_Forum_TopicRelated_HotTopic :{//话题部分--热门话题  集合
				collection_hotTopic_maxResult: '',
			},
			
			page_Forum_QuestionRelated_Question :{//问题部分--问题列表  分页	
				tagIdGroup :[],//标签Id组
				tagOptions:[],
				tableData: [],//表格内容
				parentId : '',//父Id
				tagIdList: [],//可选择Id集合
				totalrecord : 0, //总记录数
			    currentpage : 1, //当前页码
				totalpage : 1, //总页数
				maxresult: 12, //每页显示记录数
				isShowPage:false,//是否显示分页 maxresult没返回结果前就显示会导致分页栏显示页码错误
				question_tag_form:false,//是否显示问题标签表单
				navigation: '',
				sortOptions: [{//排序
						value: 1,
						label: '按发表时间 新 -> 旧'
					}, {
						value: 2,
						label: '按发表时间 旧 -> 新'
		        	}, {
			        	value: 3,
			        	label: '按回答时间 新 -> 旧'
			        }, {
			        	value: 4,
			        	label: '按回答时间 旧 -> 新'
			        }],
				     
				filterConditionOptions: [{//过滤条件
						value: 10,
						label: '全部'
					}, {
						value: 20,
						label: '未解决'
		        	}, {
			        	value: 30,
			        	label: '已解决'
			        }, {
			        	value: 40,
			        	label: '积分悬赏'
			        }, {
			        	value: 50,
			        	label: '现金悬赏'
			        }],
				page_question_tagId: '',
				page_question_tag_transferPrameter: false,
				page_question_filterCondition: 10,
				page_question_filterCondition_transferPrameter: false,
				page_question_sort: 1,
				page_question_maxResult: '',
				page_question_pageCount: '',
			},
			page_Forum_AnswerRelated_Answer :{//答案部分--答案列表  分页
				sortOptions: [{//排序
						value: 1,
						label: '按回答时间 新 -> 旧'
					}, {
						value: 2,
						label: '按回答时间 旧 -> 新'
		        	}],
				page_answer_sort: 1,
				page_answer_maxResult: '',
				page_answer_pageCount: '',
			},
			collection_Forum_QuestionRelated_LikeQuestion :{//问题部分--相似问题  集合
				collection_likeQuestion_maxResult: '',
			},
			page_Forum_RedEnvelopeRelated_ReceiveRedEnvelopeUser :{//红包部分--领取红包用户列表 分页
				sortOptions: [{//排序
						value: 10,
						label: '按领取时间 新 -> 旧'
					}, {
						value: 20,
						label: '按领取时间 旧 -> 新'
		        	}],
				page_receiveRedEnvelopeUser_sort: 10,
				page_receiveRedEnvelopeUser_maxResult: '',
			},
			collection_Forum_AdvertisingRelated_imageList :{//广告部分--图片广告 集合
				tableList:[],
				uploadImg_elementNodes:[],//上传图片Element节点集合
				localImageUrl:'',//本地预览图
				collection_image_name: [],
				collection_image_link: [],
				collection_image_path: [],
				
			},
			monolayer_Forum_HelpRelated_Help :{//在线帮助部分--在线帮助列表  单层
				helpTypeIdGroup :[],//分类Id组
				helpTypeOptions:[],
				tableData: [],//表格内容
				parentId : '',//父Id
				helpTypeIdList: [],//可选择Id集合
				totalrecord : 0, //总记录数
			    currentpage : 1, //当前页码
				totalpage : 1, //总页数
				maxresult: 12, //每页显示记录数
				isShowPage:false,//是否显示分页 maxresult没返回结果前就显示会导致分页栏显示页码错误
				helpType_form:false,//是否显示问题标签表单
				navigation: '',
				
				isAllowLoadHelpTypeMore:true,
				moreOptions:[],
				monolayer_help_helpType_transferPrameter:false,
				sortOptions: [{//排序
						value: 1,
						label: '按发布时间 新 -> 旧'
					}, {
						value: 2,
						label: '按发布时间 旧 -> 新'
		        	}],
				
				
				monolayer_help_helpTypeId: '',
				monolayer_help_quantity: '',
				monolayer_help_more: '',
				monolayer_help_maxResult: '',
				monolayer_help_pageCount: '',
				monolayer_help_sort : 1,
			},
			page_Forum_HelpRelated_Help :{//在线帮助部分--在线帮助列表  分页
				helpTypeIdGroup :[],//分类Id组
				helpTypeOptions:[],
				tableData: [],//表格内容
				parentId : '',//父Id
				helpTypeIdList: [],//可选择Id集合
				totalrecord : 0, //总记录数
			    currentpage : 1, //当前页码
				totalpage : 1, //总页数
				maxresult: 12, //每页显示记录数
				isShowPage:false,//是否显示分页 maxresult没返回结果前就显示会导致分页栏显示页码错误
				helpType_form:false,//是否显示帮助分类表单
				navigation: '',
				
				page_help_helpType_transferPrameter:false,
				sortOptions: [{//排序
						value: 1,
						label: '按发布时间 新 -> 旧'
					}, {
						value: 2,
						label: '按发布时间 旧 -> 新'
		        	}],
				page_help_helpTypeId: '',
				page_help_maxResult: '',
				page_help_pageCount: '',
				page_help_sort : 1,
			},
			collection_Forum_HelpRelated_RecommendHelp:{//在线帮助部分--推荐在线帮助  集合
				help_form:false,//是否显示帮助表单
				
				keyword: '',//关键词
				tableList :[],
				helpIdGroup :[],//帮助Id组
				tableData: [],//表格内容
				totalrecord : 0, //总记录数
			    currentpage : 1, //当前页码
				totalpage : 1, //总页数
				maxresult: 12, //每页显示记录数
				isShowPage:false,//是否显示分页 maxresult没返回结果前就显示会导致分页栏显示页码错误
			},
			entityBean_Forum_CustomForumRelated_CustomHTML:{//自定义版块 -- 用户自定义HTML--实体对象
				editor: '',//编辑器
				editorCreateParameObject: '',
			},
			collection_Forum_SystemRelated_SearchWord:{//系统--热门搜索词  集合
			    formOptions: [{
			    	key: '',
					value: ''
	            }],
			},
			
			
			error : {
				name: '',//版块标题
				forumType :'',
				module: '',//版块模板
	        	invokeMethod: '',//调用方式
	        	
	        	page_Forum_TopicRelated_Topic :{//话题部分--话题列表  分页
					page_topic_tagId: '',//话题部分--话题列表  分页
					page_topic_tag_transferPrameter: '',
					page_topic_sort: '',
					page_topic_maxResult: '',
					page_topic_pageCount: '',
				},
				page_Forum_CommentRelated_Comment :{//话题部分--评论列表  分页
					page_comment_sort: '',
					page_comment_maxResult: '',
					page_comment_pageCount: '',
				},
				collection_Forum_TopicRelated_LikeTopic :{//话题部分--相似话题  集合
					collection_likeTopic_maxResult: '',
				},
				collection_Forum_TopicRelated_HotTopic :{//话题部分--热门话题  集合
					collection_hotTopic_maxResult: '',
				},
				
				page_Forum_QuestionRelated_Question :{//问题部分--问题列表  分页
					page_question_tagId: '',
					page_question_tagName :'',//标签名称
					page_question_tag_transferPrameter: '',
					page_question_sort: '',
					page_question_maxResult: '',
					page_question_pageCount: '',
				},
				page_Forum_AnswerRelated_Answer :{//答案部分--答案列表  分页
					page_answer_sort: '',
					page_answer_maxResult: '',
					page_answer_pageCount: '',
				},
				collection_Forum_QuestionRelated_LikeQuestion :{//问题部分--相似问题  集合
					collection_likeQuestion_maxResult: '',
				},
				page_Forum_RedEnvelopeRelated_ReceiveRedEnvelopeUser :{//红包部分--领取红包用户列表 分页
					page_receiveRedEnvelopeUser_sort: '',
					page_receiveRedEnvelopeUser_maxResult: '',
				},
				collection_Forum_AdvertisingRelated_imageList :{//广告部分--图片广告 集合
					collection_image_name: new Map(),
					collection_image_link: new Map(),
					collection_image_imagePath: new Map(),
				},
				monolayer_Forum_HelpRelated_Help :{//在线帮助部分--在线帮助列表  单层
					monolayer_help_helpTypeId: '',
					monolayer_help_quantity: '',
					monolayer_help_more: '',
					monolayer_help_maxResult: '',
					monolayer_help_pageCount: '',
					monolayer_help_sort: '',
				},
				page_Forum_HelpRelated_Help :{//在线帮助部分--在线帮助列表  分页
					page_help_helpTypeId: '',
					page_help_maxResult: '',
					page_help_pageCount: '',
					page_help_sort: '',
				},
				collection_Forum_HelpRelated_RecommendHelp:{//在线帮助部分--推荐在线帮助  集合
					keyword: '',//关键词
				},
				entityBean_Forum_CustomForumRelated_CustomHTML:{//自定义版块 -- 用户自定义HTML--实体对象
					htmlContent: '',
				},
				collection_Forum_SystemRelated_SearchWord:{//系统--热门搜索词  集合

				},
			},

			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	//keep-alive 进入时
	activated: function () {
		if (Object.keys(this.entityBean_Forum_CustomForumRelated_CustomHTML.editorCreateParameObject).length != 0) {//不等于空
			//创建富文本编辑器
			this.entityBean_Forum_CustomForumRelated_CustomHTML.editor = createEditor(
				this.entityBean_Forum_CustomForumRelated_CustomHTML.editorCreateParameObject.ref, 
				this.entityBean_Forum_CustomForumRelated_CustomHTML.editorCreateParameObject.availableTag, 
				this.entityBean_Forum_CustomForumRelated_CustomHTML.editorCreateParameObject.uploadPath, 
				this.entityBean_Forum_CustomForumRelated_CustomHTML.editorCreateParameObject.userGradeList
			);
		}
		
	},
	// keep-alive 离开时
	deactivated : function () {
		if(this.entityBean_Forum_CustomForumRelated_CustomHTML.editor != ''){
			this.entityBean_Forum_CustomForumRelated_CustomHTML.editor.remove();
		}
	},
	
	watch: {
		//监听版块子类型值变化
		forumChildType: {
	　　　　handler(newValue, oldValue) {
				this.queryForumTemplateFileName(newValue);
				if(newValue == "图片广告"){
					if(this.collection_Forum_AdvertisingRelated_imageList.collection_image_name.length ==0){
						this.advertising_imageList_addItem();
					}
				}
	　　　　}
	　　},
		module: {
	　　　　handler(newValue, oldValue) {
				this.$nextTick(function(){//渲染结束再监听
					if(this.forumChildType == '在线帮助列表' && this.displayType == '单层' && this.monolayer_Forum_HelpRelated_Help.moreOptions.length ==0){
						this.load_monolayer_helpType_more();
					}
					if(this.forumChildType == '用户自定义HTML' && this.displayType == '实体对象'){
						if(this.entityBean_Forum_CustomForumRelated_CustomHTML.editor == ''){
							this.entityBean_Forum_CustomForumRelated_CustomHTML_editor();
						}
					}
				});
	　　　　}
	　　},
	
	},
	created : function created() {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		if(this.$route.query.dirName != undefined && this.$route.query.dirName != ''){
			this.dirName = this.$route.query.dirName;
		}
		if(this.$route.query.layoutId != undefined && this.$route.query.layoutId != ''){
			this.layoutId = this.$route.query.layoutId;
		}
		
		this.queryAddForumUI();
	},
	methods : {
		//空的点击事件  不写上本方法点击cellExpandRow时会运行两次
	  	radioClickEvent : function(event) {
	  		
	  	},
		//选中版块类型
	    selectedForumType: function(val) { 
	    	this.forumType = val;
	    	//清空
			this.forumChildTypeOptions = [];
	    		
			if(this.forumCodeNodeList != null && this.forumCodeNodeList.length >0){
				for(let i=0; i<this.forumCodeNodeList.length; i++){
					let forumCodeNode = this.forumCodeNodeList[i];
							
		        	if(forumCodeNode.nodeName == val){
		        		if(forumCodeNode.childNode != null && forumCodeNode.childNode.length >0){
	 						for(let j=0; j<forumCodeNode.childNode.length; j++){
	 							let childForumCodeNode = forumCodeNode.childNode[j];
	 							this.forumChildTypeOptions.push({
					        		value: childForumCodeNode.nodeName,
					        		label: childForumCodeNode.nodeName
						        });
						        
						        if(j == 0){//默认选择第一项
		  							this.forumChildType = childForumCodeNode.nodeName;
		  						}
	 						}
	 					}
		       		}
  				}
	        }
	    },
		//选中模板
	    selectedModule: function(val) { 
			let stringArray = val.split("_");
			let displayType = "单层";//模板显示类型参数 默认是单层
			if(stringArray != null && stringArray.length >1){	
				if(stringArray[2] == "monolayer"){//单层
					displayType = "单层";
				}else if(stringArray[2] == "multilayer"){//多层
					displayType = "多层";
				}else if(stringArray[2] == "page"){//分页
					displayType = "分页";
				}else if(stringArray[2] == "entityBean"){//实体对象
					displayType = "实体对象";
				}else if(stringArray[2] == "collection"){//集合
					displayType = "集合";
				}
			}
	        this.displayType = displayType;
		},
		//选中话题标签
	    selectedTopicTag: function(val) {  
	        //因为只允许选中一个，所以将已选择的清空
	    	if (Object.keys(this.page_Forum_TopicRelated_Topic.tagIdGroup).length >1) {
	        	this.page_Forum_TopicRelated_Topic.tagIdGroup.shift();//从Array 头部移除元素
	        }
	        
	        this.page_Forum_TopicRelated_Topic.page_topic_tagId = val[0];
	        
	        for(let option in this.page_Forum_TopicRelated_Topic.tagOptions){
	        	if(this.page_Forum_TopicRelated_Topic.tagOptions[option].value == this.page_Forum_TopicRelated_Topic.page_topic_tagId){
	        		this.page_Forum_TopicRelated_Topic.page_topic_tagName = this.page_Forum_TopicRelated_Topic.tagOptions[option].label;
	        		break;
	        	}
	        }
	    },
	   
	    
		//加载话题标签
	    loadTopicTag: function() { 
	    	let _self = this;
	    	if(!_self.page_Forum_TopicRelated_Topic.isAllowLoadTagGroup){
	        	return;
	        }
			_self.$ajax.get('control/tag/manage', {
			    params: {
			    	method : 'allTag'
			    }
			})
			.then(function (response) {
				_self.page_Forum_TopicRelated_Topic.isAllowLoadTagGroup = false;
				if(response == null){
					return;
				}
			    let result = response.data;
			    if(result){
			    	let returnValue = JSON.parse(result);
			    	if(returnValue.code === 200){//成功
			    		_self.page_Forum_TopicRelated_Topic.tagOptions = [];
			    		
			    		let tagList = returnValue.data;
			    		if(tagList != null && tagList.length >0){
			    			for(let i=0; i<tagList.length; i++){
			    				let tag = tagList[i];
				    			let obj =new Object();
				    	    	obj.value = tag.id;
				    	    	obj.label = tag.name;
				    	    	_self.page_Forum_TopicRelated_Topic.tagOptions.push(obj);
			    			}
			    		}
			    	}else if(returnValue.code === 500){//错误
			    		
			    		
			    	}
			    }
			})
			.catch(function (error) {
				console.log(error);
			});
		},
		
	
	    
	    //点击单元格选择
		cellExpandRow_questionTag : function(row,column,event,cell) {
			if(column.label=="选择"){
				if(row.childNodeNumber ==0){
					this.page_Forum_QuestionRelated_Question.tagIdGroup.push(row.id);
					
			        this.page_Forum_QuestionRelated_Question.page_question_tagId = row.id;
			        
					let obj =new Object();
					obj.value = row.id;
					obj.label = row.name;
					this.page_Forum_QuestionRelated_Question.tagOptions.length = 0;//清空
					this.page_Forum_QuestionRelated_Question.tagOptions.push(obj);
					
					
					//因为只允许选中一个，所以将已选择的清空
			    	if (Object.keys(this.page_Forum_QuestionRelated_Question.tagIdGroup).length >1) {
			        	this.page_Forum_QuestionRelated_Question.tagIdGroup.shift();//从Array 头部移除元素
			        }
			        
			        this.page_Forum_QuestionRelated_Question.question_tag_form = false;
				}
			}else if(column.label=="标签名称"){
				if(row.childNodeNumber >0){
					this.page_Forum_QuestionRelated_Question.parentId = row.id;
					this.queryQuestionTag(1,row.id);
				}
			}
		},
	    
	   
	    
		//加载问题标签
	    loadQuestionTag: function() { 
	    	
	    	this.page_Forum_QuestionRelated_Question.question_tag_form = true;
	       	this.$refs.page_question_tag_ref.blur();//使Select选择器失去焦点，并隐藏下拉框
	       	
	       	if(this.page_Forum_QuestionRelated_Question.tagIdGroup.length ==0){
	       		this.page_Forum_QuestionRelated_Question.page_question_tagId = "";
	       		this.page_Forum_QuestionRelated_Question.tagOptions.length = 0;//清空	
	       	}
	       	
	       	
	       	//清空数据
			this.page_Forum_QuestionRelated_Question.totalrecord = 0;//服务器返回的long类型已转为String类型
   			this.page_Forum_QuestionRelated_Question.currentpage = 1;
			this.page_Forum_QuestionRelated_Question.totalpage = 1;//服务器返回的long类型已转为String类型
			this.page_Forum_QuestionRelated_Question.maxresult = 12;
			this.page_Forum_QuestionRelated_Question.isShowPage = false;//显示分页栏
			this.page_Forum_QuestionRelated_Question.parentId = '';
	       	
	        this.queryQuestionTag(1,'');
		},
		//问题标签分页
		questionTagPage : function(page) {
			
			this.queryQuestionTag(page, this.page_Forum_QuestionRelated_Question.parentId);
		},
		//查询问题标签
		queryQuestionTag : function(page,parentId) {
			let _self = this;
			
			_self.page_Forum_QuestionRelated_Question.tableData = [];
			_self.page_Forum_QuestionRelated_Question.tagIdList = [];
			_self.page_Forum_QuestionRelated_Question.navigation = '';
			
			_self.page_Forum_QuestionRelated_Question.parentId = parentId;
			_self.$ajax.get('control/questionTag/manage', {
			    params: {
			    	method : 'questionTagPage',
			    	parentId : parentId,
			    	page : page
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
			    			if(key == "pageView"){
			    				let tagView = mapData[key];
					    		let tagList = tagView.records;
					    		if(tagList != null && tagList.length >0){
					    			for(let i = 0; i<tagList.length; i++){
					    				let tag = tagList[i];
					    				_self.page_Forum_QuestionRelated_Question.tagIdList.push(tag.id);
					    			
					    			}
					    		
					    		
					    			_self.page_Forum_QuestionRelated_Question.tableData = tagList;
					 
					    			_self.page_Forum_QuestionRelated_Question.totalrecord = parseInt(tagView.totalrecord);//服务器返回的long类型已转为String类型
					    			_self.page_Forum_QuestionRelated_Question.currentpage = tagView.currentpage;
									_self.page_Forum_QuestionRelated_Question.totalpage = parseInt(tagView.totalpage);//服务器返回的long类型已转为String类型
									_self.page_Forum_QuestionRelated_Question.maxresult = tagView.maxresult;
									_self.page_Forum_QuestionRelated_Question.isShowPage = true;//显示分页栏
					    		}
			    			}else if(key == "navigation"){
			    				_self.page_Forum_QuestionRelated_Question.navigation = mapData[key];
			    			}
			    		}
			    	}else if(returnValue.code === 500){//错误
			    		
			    		
			    	}
			    }
			})
			.catch(function (error) {
				console.log(error);
			});
		
		
		},
		
		
		
		//广告部分--图片广告 集合 添加项
	    advertising_imageList_addItem: function() {
	    	this.collection_Forum_AdvertisingRelated_imageList.collection_image_name.push("");
	    	this.collection_Forum_AdvertisingRelated_imageList.collection_image_link.push("");
	    	this.collection_Forum_AdvertisingRelated_imageList.collection_image_path.push("");
	    
	        this.collection_Forum_AdvertisingRelated_imageList.tableList.push({
	        	key: Math.random().toString().slice(2),//随机数
	        });
		},
		
		//处理上传图片
		advertising_imageList_handleChange(file, fileList) {
			if (fileList.length > 1) {
	      		fileList.splice(0, 1);
			}
    	},
		//处理删除图片
		advertising_imageList_handleImageRemove(file,tableIndex) {
		//	let fileList = this.$refs.uploadImg.uploadFiles;
			let fileList = this.collection_Forum_AdvertisingRelated_imageList.uploadImg_elementNodes[tableIndex].uploadFiles;
		
			let index = fileList.findIndex( fileItem => {
				return fileItem.uid === file.uid
			});
			fileList.splice(index, 1);
	    },
	    //打开图片预览
		advertising_imageList_openImagePreview(file) {
			
	        this.collection_Forum_AdvertisingRelated_imageList.localImageUrl = file.url;
	        this.$refs.collection_Forum_AdvertisingRelated_imageList_imageViewer.showViewer = true;
		},
		//上移
		collection_Forum_AdvertisingRelated_imageList_moveUp : function(row,index) {
	        if (index >0) {
	        	let upData = this.collection_Forum_AdvertisingRelated_imageList.tableList[index - 1];
	        	this.collection_Forum_AdvertisingRelated_imageList.tableList.splice(index - 1, 1);
                this.collection_Forum_AdvertisingRelated_imageList.tableList.splice(index, 0, upData);
                
                let uploadImg_elementNodes_upData = this.collection_Forum_AdvertisingRelated_imageList.uploadImg_elementNodes[index - 1];
	        	this.collection_Forum_AdvertisingRelated_imageList.uploadImg_elementNodes.splice(index - 1, 1);
                this.collection_Forum_AdvertisingRelated_imageList.uploadImg_elementNodes.splice(index, 0, uploadImg_elementNodes_upData);
                
                let image_name_upData = this.collection_Forum_AdvertisingRelated_imageList.collection_image_name[index - 1];
	        	this.collection_Forum_AdvertisingRelated_imageList.collection_image_name.splice(index - 1, 1);
                this.collection_Forum_AdvertisingRelated_imageList.collection_image_name.splice(index, 0, image_name_upData);
                
                let image_link_upData = this.collection_Forum_AdvertisingRelated_imageList.collection_image_link[index - 1];
	        	this.collection_Forum_AdvertisingRelated_imageList.collection_image_link.splice(index - 1, 1);
                this.collection_Forum_AdvertisingRelated_imageList.collection_image_link.splice(index, 0, image_link_upData);
                
                let image_path_upData = this.collection_Forum_AdvertisingRelated_imageList.collection_image_path[index - 1];
	        	this.collection_Forum_AdvertisingRelated_imageList.collection_image_path.splice(index - 1, 1);
                this.collection_Forum_AdvertisingRelated_imageList.collection_image_path.splice(index, 0, image_path_upData);
                
                //错误信息移动
                let up_image_name_error = this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_name.get(index - 1);
                let current_image_name_error = this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_name.get(index);
                this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_name.delete(index - 1);
                this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_name.delete(index);
                if(up_image_name_error != null){
                	this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_name.set(index,up_image_name_error);
                }
                if(current_image_name_error != null){
                	this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_name.set(index - 1,current_image_name_error);
                }
                
                let up_image_link_error = this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_link.get(index - 1);
                let current_image_link_error = this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_link.get(index);
                this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_link.delete(index - 1);
                this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_link.delete(index);
                if(up_image_link_error != null){
                	this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_link.set(index,up_image_link_error);
                }
                if(current_image_link_error != null){
                	this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_link.set(index - 1,current_image_link_error);
                }
               
                let up_image_imagePath_error = this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_imagePath.get(index - 1);
                let current_image_imagePath_error = this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_imagePath.get(index);
                this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_imagePath.delete(index - 1);
                this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_imagePath.delete(index);
                if(up_image_imagePath_error != null){
                	this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_imagePath.set(index,up_image_imagePath_error);
                }
                if(current_image_imagePath_error != null){
                	this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_imagePath.set(index - 1,current_image_imagePath_error);
                }
	        }
		
		},
		//下移
		collection_Forum_AdvertisingRelated_imageList_moveDown : function(row,index) {
	        if ((index + 1) < this.collection_Forum_AdvertisingRelated_imageList.tableList.length) {
	        	let downData = this.collection_Forum_AdvertisingRelated_imageList.tableList[index + 1];
                this.collection_Forum_AdvertisingRelated_imageList.tableList.splice(index + 1, 1);
                this.collection_Forum_AdvertisingRelated_imageList.tableList.splice(index, 0, downData);
                
                let uploadImg_elementNodes_downData = this.collection_Forum_AdvertisingRelated_imageList.uploadImg_elementNodes[index + 1];
                this.collection_Forum_AdvertisingRelated_imageList.uploadImg_elementNodes.splice(index + 1, 1);
                this.collection_Forum_AdvertisingRelated_imageList.uploadImg_elementNodes.splice(index, 0, uploadImg_elementNodes_downData);
   
                let image_name_downData = this.collection_Forum_AdvertisingRelated_imageList.collection_image_name[index + 1];
                this.collection_Forum_AdvertisingRelated_imageList.collection_image_name.splice(index + 1, 1);
                this.collection_Forum_AdvertisingRelated_imageList.collection_image_name.splice(index, 0, image_name_downData);
                
                let image_link_downData = this.collection_Forum_AdvertisingRelated_imageList.collection_image_link[index + 1];
                this.collection_Forum_AdvertisingRelated_imageList.collection_image_link.splice(index + 1, 1);
                this.collection_Forum_AdvertisingRelated_imageList.collection_image_link.splice(index, 0, image_link_downData);
                
                let image_path_downData = this.collection_Forum_AdvertisingRelated_imageList.collection_image_path[index + 1];
                this.collection_Forum_AdvertisingRelated_imageList.collection_image_path.splice(index + 1, 1);
                this.collection_Forum_AdvertisingRelated_imageList.collection_image_path.splice(index, 0, image_path_downData);
                
                //错误信息移动
                let down_image_name_error = this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_name.get(index + 1);
                let current_image_name_error = this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_name.get(index);
                this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_name.delete(index + 1);
                this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_name.delete(index);
                if(down_image_name_error != null){
                	this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_name.set(index,down_image_name_error);
                }
                if(current_image_name_error != null){
                	this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_name.set(index + 1,current_image_name_error);
                	
                }
                
                let down_image_link_error = this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_link.get(index + 1);
                let current_image_link_error = this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_link.get(index);
                this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_link.delete(index + 1);
                this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_link.delete(index);
                if(down_image_link_error != null){
                	this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_link.set(index,down_image_link_error);
                }
                if(current_image_link_error != null){
                	this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_link.set(index + 1,current_image_link_error);
                	
                }
                
                let down_image_imagePath_error = this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_imagePath.get(index + 1);
                let current_image_imagePath_error = this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_imagePath.get(index);
                this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_imagePath.delete(index + 1);
                this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_imagePath.delete(index);
                if(down_image_imagePath_error != null){
                	this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_imagePath.set(index,down_image_imagePath_error);
                }
                if(current_image_imagePath_error != null){
                	this.error.collection_Forum_AdvertisingRelated_imageList.collection_image_imagePath.set(index + 1,current_image_imagePath_error);
                	
                }
                
                
	        }
		
		},
		//删除
		collection_Forum_AdvertisingRelated_imageList_removeItem : function(row,index) {
			this.collection_Forum_AdvertisingRelated_imageList.tableList.splice(index, 1);
			
			this.collection_Forum_AdvertisingRelated_imageList.uploadImg_elementNodes.splice(index, 1);//上传图片Element节点集合
			this.collection_Forum_AdvertisingRelated_imageList.localImageUrl = '';//本地预览图
			this.collection_Forum_AdvertisingRelated_imageList.collection_image_name.splice(index, 1);
			this.collection_Forum_AdvertisingRelated_imageList.collection_image_link.splice(index, 1);
			this.collection_Forum_AdvertisingRelated_imageList.collection_image_path.splice(index, 1);
			
		},

		//在线帮助列表 -- 单层 ---点击单元格选择
		cellExpandRow_monolayer_helpType : function(row,column,event,cell) {
			if(column.label=="选择"){
				if(row.childNodeNumber ==0){
					this.monolayer_Forum_HelpRelated_Help.helpTypeIdGroup.push(row.id);
					
			        this.monolayer_Forum_HelpRelated_Help.monolayer_help_helpTypeId = row.id;
			        
					let obj =new Object();
					obj.value = row.id;
					obj.label = row.name;
					this.monolayer_Forum_HelpRelated_Help.helpTypeOptions.length = 0;//清空
					this.monolayer_Forum_HelpRelated_Help.helpTypeOptions.push(obj);
					
					
					//因为只允许选中一个，所以将已选择的清空
			    	if (Object.keys(this.monolayer_Forum_HelpRelated_Help.helpTypeIdGroup).length >1) {
			        	this.monolayer_Forum_HelpRelated_Help.helpTypeIdGroup.shift();//从Array 头部移除元素
			        }
			        
			        this.monolayer_Forum_HelpRelated_Help.helpType_form = false;
				}
			}else if(column.label=="分类名称"){
				if(row.childNodeNumber >0){
					this.monolayer_Forum_HelpRelated_Help.parentId = row.id;
					this.query_monolayer_helpType(1,row.id);
				}
			}
		},
		
		
		//加载帮助分类 -- 单层
	    load_monolayer_helpType: function() { 
	    	
	    	this.monolayer_Forum_HelpRelated_Help.helpType_form = true;
	       	this.$refs.monolayer_helpType_ref.blur();//使Select选择器失去焦点，并隐藏下拉框
	      
	       	if(this.monolayer_Forum_HelpRelated_Help.helpTypeIdGroup.length ==0){
	       		this.monolayer_Forum_HelpRelated_Help.monolayer_help_helpTypeId = "";
	       		this.monolayer_Forum_HelpRelated_Help.helpTypeOptions.length = 0;//清空	
	       	}
	       	
	       	
	       	//清空数据
			this.monolayer_Forum_HelpRelated_Help.totalrecord = 0;//服务器返回的long类型已转为String类型
   			this.monolayer_Forum_HelpRelated_Help.currentpage = 1;
			this.monolayer_Forum_HelpRelated_Help.totalpage = 1;//服务器返回的long类型已转为String类型
			this.monolayer_Forum_HelpRelated_Help.maxresult = 12;
			this.monolayer_Forum_HelpRelated_Help.isShowPage = false;//显示分页栏
			this.monolayer_Forum_HelpRelated_Help.parentId = '';
	       	
	        this.query_monolayer_helpType(1,'');
		},
		//帮助分类分页 -- 单层
		monolayer_helpTypePage : function(page) {
			
			this.query_monolayer_helpType(page, this.monolayer_Forum_HelpRelated_Help.parentId);
		},
		//查询帮助分类 -- 单层
		query_monolayer_helpType : function(page,parentId) {
			let _self = this;
			
			_self.monolayer_Forum_HelpRelated_Help.tableData = [];
			_self.monolayer_Forum_HelpRelated_Help.helpTypeIdList = [];
			_self.monolayer_Forum_HelpRelated_Help.navigation = '';
			
			_self.monolayer_Forum_HelpRelated_Help.parentId = parentId;
			_self.$ajax.get('control/helpType/manage', {
			    params: {
			    	method : 'helpTypePageSelect',
			    	parentId : parentId,
			    	page : page
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
			    			if(key == "pageView"){
			    				let typeView = mapData[key];
					    		let typeList = typeView.records;
					    		if(typeList != null && typeList.length >0){
					    			for(let i = 0; i<typeList.length; i++){
					    				let type = typeList[i];
					    				_self.monolayer_Forum_HelpRelated_Help.helpTypeIdList.push(type.id);
					    			
					    			}
					    		
					    		
					    			_self.monolayer_Forum_HelpRelated_Help.tableData = typeList;
					 
					    			_self.monolayer_Forum_HelpRelated_Help.totalrecord = parseInt(typeView.totalrecord);//服务器返回的long类型已转为String类型
					    			_self.monolayer_Forum_HelpRelated_Help.currentpage = typeView.currentpage;
									_self.monolayer_Forum_HelpRelated_Help.totalpage = parseInt(typeView.totalpage);//服务器返回的long类型已转为String类型
									_self.monolayer_Forum_HelpRelated_Help.maxresult = typeView.maxresult;
									_self.monolayer_Forum_HelpRelated_Help.isShowPage = true;//显示分页栏
					    		}
			    			}else if(key == "navigation"){
			    				_self.monolayer_Forum_HelpRelated_Help.navigation = mapData[key];
			    			}
			    		}
			    	}else if(returnValue.code === 500){//错误
			    		
			    		
			    	}
			    }
			})
			.catch(function (error) {
				console.log(error);
			});
		
		
		},
		//加载帮助分类更多 -- 单层
	    load_monolayer_helpType_more: function() { 
	    	let _self = this;
	    	if(!_self.monolayer_Forum_HelpRelated_Help.isAllowLoadHelpTypeMore){
	        	return;
	        }
	        
			_self.$ajax.get('control/layout/manage', {
			    params: {
			    	method : 'ajax_more',
			    	dirName:_self.dirName,
			    	forumChildType:_self.forumChildType,
			    }
			})
			.then(function (response) {
				_self.monolayer_Forum_HelpRelated_Help.isAllowLoadHelpTypeMore = false;
				if(response == null){
					return;
				}
			    let result = response.data;
			    if(result){
			    	let returnValue = JSON.parse(result);
			    	if(returnValue.code === 200){//成功
			    		_self.monolayer_Forum_HelpRelated_Help.moreOptions = [];
			    		let first = "";
			    		let moreMap = returnValue.data;
			    		for(let key in moreMap){
			    			let obj =new Object();
			    	    	obj.value = key;
			    	    	obj.label = moreMap[key];
			    	    	_self.monolayer_Forum_HelpRelated_Help.moreOptions.push(obj);
			    			if(first == ""){
			    				first = key;
			    			}
			    		}
			    		
			    		//默认选中第一项
			    		_self.monolayer_Forum_HelpRelated_Help.monolayer_help_more = first;
			    		
			    		
			    	}else if(returnValue.code === 500){//错误
			    		
			    		
			    	}
			    }
			})
			.catch(function (error) {
				console.log(error);
			});
		},
		
		
		
		//在线帮助列表 -- 分页 ---点击单元格选择
		cellExpandRow_page_helpType : function(row,column,event,cell) {
			if(column.label=="选择"){
				if(row.childNodeNumber ==0){
					this.page_Forum_HelpRelated_Help.helpTypeIdGroup.push(row.id);
					
			        this.page_Forum_HelpRelated_Help.page_help_helpTypeId = row.id;
			        
					let obj =new Object();
					obj.value = row.id;
					obj.label = row.name;
					this.page_Forum_HelpRelated_Help.helpTypeOptions.length = 0;//清空
					this.page_Forum_HelpRelated_Help.helpTypeOptions.push(obj);
					
					
					//因为只允许选中一个，所以将已选择的清空
			    	if (Object.keys(this.page_Forum_HelpRelated_Help.helpTypeIdGroup).length >1) {
			        	this.page_Forum_HelpRelated_Help.helpTypeIdGroup.shift();//从Array 头部移除元素
			        }
			        
			        this.page_Forum_HelpRelated_Help.helpType_form = false;
				}
			}else if(column.label=="分类名称"){
				if(row.childNodeNumber >0){
					this.page_Forum_HelpRelated_Help.parentId = row.id;
					this.query_page_helpType(1,row.id);
				}
			}
		},
		
		
		//加载帮助分类 -- 分页
	    load_page_helpType: function() { 
	    	
	    	this.page_Forum_HelpRelated_Help.helpType_form = true;
	       	this.$refs.page_helpType_ref.blur();//使Select选择器失去焦点，并隐藏下拉框
	      
	       	if(this.page_Forum_HelpRelated_Help.helpTypeIdGroup.length ==0){
	       		this.page_Forum_HelpRelated_Help.page_help_helpTypeId = "";
	       		this.page_Forum_HelpRelated_Help.helpTypeOptions.length = 0;//清空	
	       	}
	       	
	       	
	       	//清空数据
			this.page_Forum_HelpRelated_Help.totalrecord = 0;//服务器返回的long类型已转为String类型
   			this.page_Forum_HelpRelated_Help.currentpage = 1;
			this.page_Forum_HelpRelated_Help.totalpage = 1;//服务器返回的long类型已转为String类型
			this.page_Forum_HelpRelated_Help.maxresult = 12;
			this.page_Forum_HelpRelated_Help.isShowPage = false;//显示分页栏
			this.page_Forum_HelpRelated_Help.parentId = '';
	       	
	        this.query_page_helpType(1,'');
		},
		//帮助分类分页 -- 分页
		page_helpTypePage : function(page) {
			
			this.query_page_helpType(page, this.page_Forum_HelpRelated_Help.parentId);
		},
		//查询帮助分类 -- 分页
		query_page_helpType : function(page,parentId) {
			let _self = this;
			
			_self.page_Forum_HelpRelated_Help.tableData = [];
			_self.page_Forum_HelpRelated_Help.helpTypeIdList = [];
			_self.page_Forum_HelpRelated_Help.navigation = '';
			
			_self.monolayer_Forum_HelpRelated_Help.parentId = parentId;
			_self.$ajax.get('control/helpType/manage', {
			    params: {
			    	method : 'helpTypePageSelect',
			    	parentId : parentId,
			    	page : page
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
			    			if(key == "pageView"){
			    				let typeView = mapData[key];
					    		let typeList = typeView.records;
					    		if(typeList != null && typeList.length >0){
					    			for(let i = 0; i<typeList.length; i++){
					    				let type = typeList[i];
					    				_self.page_Forum_HelpRelated_Help.helpTypeIdList.push(type.id);
					    			
					    			}
					    		
					    		
					    			_self.page_Forum_HelpRelated_Help.tableData = typeList;
					 
					    			_self.page_Forum_HelpRelated_Help.totalrecord = parseInt(typeView.totalrecord);//服务器返回的long类型已转为String类型
					    			_self.page_Forum_HelpRelated_Help.currentpage = typeView.currentpage;
									_self.page_Forum_HelpRelated_Help.totalpage = parseInt(typeView.totalpage);//服务器返回的long类型已转为String类型
									_self.page_Forum_HelpRelated_Help.maxresult = typeView.maxresult;
									_self.page_Forum_HelpRelated_Help.isShowPage = true;//显示分页栏
					    		}
			    			}else if(key == "navigation"){
			    				_self.page_Forum_HelpRelated_Help.navigation = mapData[key];
			    			}
			    		}
			    	}else if(returnValue.code === 500){//错误
			    		
			    		
			    	}
			    }
			})
			.catch(function (error) {
				console.log(error);
			});
		
		
		},
		
		
		
		//推荐在线帮助 - 集合 --点击单元格选择
		cellExpandRow_collection_Forum_HelpRelated_RecommendHelp : function(row,column,event,cell) {
			if(column.label=="选择"){
			
				
			//ref="collection_Forum_HelpRelated_RecommendHelp_table" :data="collection_Forum_HelpRelated_RecommendHelp.tableList"
				
				
				for(let i =0; i< this.collection_Forum_HelpRelated_RecommendHelp.tableData.length; i++){
					let search_help = this.collection_Forum_HelpRelated_RecommendHelp.tableData[i];
					if(row.id == search_help.id){
						if(this.collection_Forum_HelpRelated_RecommendHelp.helpIdGroup[i] == true){//原来已选中
							this.collection_Forum_HelpRelated_RecommendHelp.helpIdGroup[i] = false;
							for(let j =0; j< this.collection_Forum_HelpRelated_RecommendHelp.tableList.length; j++){
								let help = this.collection_Forum_HelpRelated_RecommendHelp.tableList[j];
								if(row.id == help.id){
									this.collection_Forum_HelpRelated_RecommendHelp.tableList.splice(j, 1);
									break;
								}
							}
						}else{
							this.collection_Forum_HelpRelated_RecommendHelp.helpIdGroup[i] = true;
							this.collection_Forum_HelpRelated_RecommendHelp.tableList.push(row);
						}
					
						
					}
				}
			}
		},
		//推荐在线帮助 - 集合 -- 加载 
	    collection_Forum_HelpRelated_RecommendHelp_loadHelp: function() { 	
	    	this.collection_Forum_HelpRelated_RecommendHelp.help_form = true;
   	
	       	this.collection_Forum_HelpRelated_RecommendHelp.helpIdGroup.length = 0;//清空
			this.collection_Forum_HelpRelated_RecommendHelp.keyword = '';//关键词

	       	//清空数据
			this.collection_Forum_HelpRelated_RecommendHelp.totalrecord = 0;//服务器返回的long类型已转为String类型
   			this.collection_Forum_HelpRelated_RecommendHelp.currentpage = 1;
			this.collection_Forum_HelpRelated_RecommendHelp.totalpage = 1;//服务器返回的long类型已转为String类型
			this.collection_Forum_HelpRelated_RecommendHelp.maxresult = 12;
			this.collection_Forum_HelpRelated_RecommendHelp.isShowPage = false;//显示分页栏
	       	
	        this.search_collection_help(1,'');
		},
		//推荐在线帮助 -- 分页
		collection_Forum_HelpRelated_RecommendHelp_helpPage : function(page) {
			
			this.search_collection_help(page, this.collection_Forum_HelpRelated_RecommendHelp.keyword);
		},
		
		//推荐在线帮助 - 集合 搜索分类
		search_collection_help : function(page,keyword) {
			let _self = this;
			
			_self.collection_Forum_HelpRelated_RecommendHelp.tableData = [];
			_self.collection_Forum_HelpRelated_RecommendHelp.helpIdGroup = [];
			
			_self.$ajax.get('control/help/manage', {
			    params: {
			    	method : 'ajax_searchHelpPage',
			    	keyword : keyword,
			    	page : page
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
			    		let pageView = returnValue.data;
			    		let list = pageView.records;
			    		if(list != null && list.length >0){
			    			
			    			A:for(let i =0; i<list.length; i++){
			    				let help = list[i];
			    				
			    				for(let j =0; j<_self.collection_Forum_HelpRelated_RecommendHelp.tableList.length; j++){
			    					let item = _self.collection_Forum_HelpRelated_RecommendHelp.tableList[j];
			    					if(item.id == help.id){
			    						_self.collection_Forum_HelpRelated_RecommendHelp.helpIdGroup.push(true);
			    						continue A;
			    					}
			    				}
			    				_self.collection_Forum_HelpRelated_RecommendHelp.helpIdGroup.push(false);
			    			}
			    			
			    			_self.collection_Forum_HelpRelated_RecommendHelp.tableData = list;
			 
			    			_self.collection_Forum_HelpRelated_RecommendHelp.totalrecord = parseInt(pageView.totalrecord);//服务器返回的long类型已转为String类型
			    			_self.collection_Forum_HelpRelated_RecommendHelp.currentpage = pageView.currentpage;
							_self.collection_Forum_HelpRelated_RecommendHelp.totalpage = parseInt(pageView.totalpage);//服务器返回的long类型已转为String类型
							_self.collection_Forum_HelpRelated_RecommendHelp.maxresult = pageView.maxresult;
							_self.collection_Forum_HelpRelated_RecommendHelp.isShowPage = true;//显示分页栏
			    		}
			    	}else if(returnValue.code === 500){//错误
			    		
			    		
			    	}
			    }
			})
			.catch(function (error) {
				console.log(error);
			});
		
		
		},
		//推荐在线帮助 - 集合 -- 上移
		collection_Forum_HelpRelated_RecommendHelp_moveUp : function(row,index) {
	        if (index >0) {
	        	let upData = this.collection_Forum_HelpRelated_RecommendHelp.tableList[index - 1];
	        	this.collection_Forum_HelpRelated_RecommendHelp.tableList.splice(index - 1, 1);
                this.collection_Forum_HelpRelated_RecommendHelp.tableList.splice(index, 0, upData);
	        }
		
		},
		//推荐在线帮助 - 集合 -- 下移
		collection_Forum_HelpRelated_RecommendHelp_moveDown : function(row,index) {
	        if ((index + 1) < this.collection_Forum_HelpRelated_RecommendHelp.tableList.length) {
	        	let downData = this.collection_Forum_HelpRelated_RecommendHelp.tableList[index + 1];
                this.collection_Forum_HelpRelated_RecommendHelp.tableList.splice(index + 1, 1);
                this.collection_Forum_HelpRelated_RecommendHelp.tableList.splice(index, 0, downData); 
	        }
		
		},
		//推荐在线帮助 - 集合 -- 删除
		collection_Forum_HelpRelated_RecommendHelp_removeItem : function(row,index) {
			this.collection_Forum_HelpRelated_RecommendHelp.tableList.splice(index, 1);
		},
		
		//自定义版块 -- 用户自定义HTML -- 实体对象 -- 编辑器
		entityBean_Forum_CustomForumRelated_CustomHTML_editor : function() {
			let _self = this;
		
			let availableTag =  ['source', '|', 'preview', 'template',  
		        '|', 'justifyleft', 'justifycenter', 'justifyright',
		        'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent', 'subscript',
		        'superscript', 'clearhtml', 'quickformat', 'selectall', '|', 
		        'formatblock', 'fontname', 'fontsize','fullscreen', '/', 'forecolor', 'hilitecolor', 'bold',
		        'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', '|', 'image', 'multiimage',
		         'media','embedVideo','uploadVideo', 'insertfile','emoticons','baidumap', 'table', 'hr',   'pagebreak',
		         'link', 'unlink'];
   			let uploadPath = "control/forum/manage?method=upload&layoutId="+_self.layoutId;
    		//创建富文本编辑器
			_self.entityBean_Forum_CustomForumRelated_CustomHTML.editor = createEditor(_self.$refs.entityBean_customForum_htmlContent, availableTag, uploadPath, null);
			_self.entityBean_Forum_CustomForumRelated_CustomHTML.editorCreateParameObject = {
    				ref:_self.$refs.entityBean_customForum_htmlContent,
    				availableTag:availableTag,
    				uploadPath:uploadPath,
    				userGradeList:null
    		}
		},
		
		//系统--热门搜索词  集合 -- 添加项
	    collection_Forum_SystemRelated_SearchWord_addItem() {
	        this.collection_Forum_SystemRelated_SearchWord.formOptions.push({
	        	key: '',
	        	value: ''
	        });
		},
		//系统--热门搜索词  集合 -- 删除项
	    collection_Forum_SystemRelated_SearchWord_removeItem(formOption) {
	        let index = this.collection_Forum_SystemRelated_SearchWord.formOptions.indexOf(formOption);
	        if (index !== -1) {
	        	this.collection_Forum_SystemRelated_SearchWord.formOptions.splice(index, 1);
	        }
		},
		//系统--热门搜索词  集合 -- 上移
	    collection_Forum_SystemRelated_SearchWord_moveUp(formOption) {
	        let index = this.collection_Forum_SystemRelated_SearchWord.formOptions.indexOf(formOption);
	        if (index >0) {
	        	let upData = this.collection_Forum_SystemRelated_SearchWord.formOptions[index - 1];
	        	this.collection_Forum_SystemRelated_SearchWord.formOptions.splice(index - 1, 1);
                this.collection_Forum_SystemRelated_SearchWord.formOptions.splice(index, 0, upData);
	        }
		},
		//系统--热门搜索词  集合 -- 下移
		collection_Forum_SystemRelated_SearchWord_moveDown(formOption) {
	        let index = this.collection_Forum_SystemRelated_SearchWord.formOptions.indexOf(formOption);
	        if ((index + 1) < this.collection_Forum_SystemRelated_SearchWord.formOptions.length) {
	        	let downData = this.collection_Forum_SystemRelated_SearchWord.formOptions[index + 1];
                this.collection_Forum_SystemRelated_SearchWord.formOptions.splice(index + 1, 1);
                this.collection_Forum_SystemRelated_SearchWord.formOptions.splice(index, 0, downData);
	        }
		},

		
		//查询添加界面
		queryAddForumUI : function() {
			let _self = this;
		
			_self.$ajax.get('control/forum/manage', {
			    params: {
			    	method : 'add',
			    	layoutId:  _self.layoutId,
			    	dirName: _self.dirName,
			    },
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
			    			if(key == "layout"){
			    				_self.layout = mapData[key];
			    			}else if(key == "templates"){
			    				_self.templates = mapData[key];
			    			}else if(key == "forumCodeNodeList"){
			    				let forumCodeNodeList = mapData[key];
			    				if(forumCodeNodeList != null && forumCodeNodeList.length >0){
			    					//清空
			    					_self.forumTypeOptions = [];
			    					_self.forumChildTypeOptions = [];
			    				
			    					for(let i=0; i<forumCodeNodeList.length; i++){
			    						let forumCodeNode = forumCodeNodeList[i];
			    						
			    						_self.forumTypeOptions.push({//版块类型
								        	value: forumCodeNode.nodeName,
								        	label: forumCodeNode.nodeName
								        });
								        
								       
								        if(i == 0){//默认选择第一项
								        	_self.forumType = forumCodeNode.nodeName;
			 									
								        	if(forumCodeNode.childNode != null && forumCodeNode.childNode.length >0){
				    							for(let j=0; j<forumCodeNode.childNode.length; j++){
				    								let childForumCodeNode = forumCodeNode.childNode[j];
				    								_self.forumChildTypeOptions.push({
											        	value: childForumCodeNode.nodeName,
											        	label: childForumCodeNode.nodeName
											        });
											        
											        if(j == 0){//默认选择第一项
					    							 	_self.forumChildType = childForumCodeNode.nodeName;
					    							}
				    							}
				    						}
								        }
			    					}
			    				}
			    				_self.forumCodeNodeList = forumCodeNodeList;
			    			}
			    		}
			    	}else if(returnValue.code === 500){//错误
			    		let errorMap = returnValue.data;
			    		for (let key in errorMap) {   
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
			    
			    
			})
			.catch(function (error) {
				console.log(error);
			});
		},
	
		//查询版块模板文件名
		queryForumTemplateFileName : function(childNodeName) {
			let _self = this;
			_self.moduleOptions = [];
			_self.module = "";
			_self.displayType = "";
			
			_self.$ajax.get('control/forum/manage', {
			    params: {
			    	method : 'forumTemplateFileNameUI',
			    	childNodeName: childNodeName,
			    	dirName: _self.dirName,
			    },
			})
			.then(function (response) {
				if(response == null){
					return;
				}
			    let result = response.data;
			    if(result){
			    	let returnValue = JSON.parse(result);
			    	if(returnValue.code === 200){//成功
			    		let forumCodeNodeList = returnValue.data;
			    		if(forumCodeNodeList != null && forumCodeNodeList.length >0){
			    			for(let i=0; i<forumCodeNodeList.length; i++){
			    				let forumCodeNode = forumCodeNodeList[i];
			    				
			    				
			    				let stringArray = forumCodeNode.nodeName.split("_");
								//var childType = "";
								let displayType = "单层";//模板显示类型参数 默认是单层
								if(stringArray != null && stringArray.length >1){
								//	childType = stringArray[1].toLowerCase(); //子版块类型参数  转为小写	
									if(stringArray[2] == "monolayer"){//单层
										displayType = "单层";
									}else if(stringArray[2] == "multilayer"){//多层
										displayType = "多层";
									}else if(stringArray[2] == "page"){//分页
										displayType = "分页";
									}else if(stringArray[2] == "entityBean"){//实体对象
										displayType = "实体对象";
									}else if(stringArray[2] == "collection"){//集合
										displayType = "集合";
									}
								}
			    				
			    				
			    				_self.moduleOptions.push({
						        	value: forumCodeNode.nodeName,
						        	label: forumCodeNode.nodeName+" ("+displayType+")"
						        });
						        
						        if(i == 0){//默认选择第一项
						        	_self.displayType = displayType;
					    			_self.module = forumCodeNode.nodeName;
					    		}
			    			}
			    		}
			    	}else if(returnValue.code === 500){//错误
			    		let errorMap = returnValue.data;
			    		for (let key in errorMap) {   
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
				let value = _self.error[key];
				if(getType(value) == 'object'){//如果为Object类型
				//	Object.keys(value).forEach(key => value[key]= '');
					Object.keys(value).forEach(
						function(key){
							if(getType(value[key]) == 'map'){
								value[key].clear();
							}else{
								value[key]= '';
							}
						}
					);
				}else{
    				_self.error[key] = "";
    			}
    	    }
    	    
			let formData = new FormData();
			
			if(_self.layoutId != null){
				formData.append('layoutId', _self.layoutId);
			}
			if(_self.dirName != null){
				formData.append('dirName', _self.dirName);
			}
			if(_self.forumType != null){
				formData.append('forumType', _self.forumType);
			}
			if(_self.forumChildType != null){
				formData.append('forumChildType', _self.forumChildType);
			}
			if(_self.name != null){
				formData.append('name', _self.name);
			}
			if(_self.module != null){
				formData.append('module', _self.module);
			}
			if(_self.invokeMethod != null){
				formData.append('invokeMethod', _self.invokeMethod);
			}
			
			if(_self.forumType == '话题' && _self.forumChildType == '话题列表' && _self.displayType == '分页'){
				if( _self.page_Forum_TopicRelated_Topic.page_topic_tagId != undefined){
					formData.append('page_topic_tagId', _self.page_Forum_TopicRelated_Topic.page_topic_tagId);
				}
				
				formData.append('page_topic_tag_transferPrameter', _self.page_Forum_TopicRelated_Topic.page_topic_tag_transferPrameter);
				formData.append('page_topic_sort', _self.page_Forum_TopicRelated_Topic.page_topic_sort);
				formData.append('page_topic_maxResult', _self.page_Forum_TopicRelated_Topic.page_topic_maxResult);
				formData.append('page_topic_pageCount', _self.page_Forum_TopicRelated_Topic.page_topic_pageCount);
			}
			
			if(_self.forumType == '话题' && _self.forumChildType == '评论列表' && _self.displayType == '分页'){
				formData.append('page_comment_sort', _self.page_Forum_CommentRelated_Comment.page_comment_sort);
				formData.append('page_comment_maxResult', _self.page_Forum_CommentRelated_Comment.page_comment_maxResult);
				formData.append('page_comment_pageCount', _self.page_Forum_CommentRelated_Comment.page_comment_pageCount);
			}
			if(_self.forumType == '话题' && _self.forumChildType == '相似话题' && _self.displayType == '集合'){
				formData.append('collection_likeTopic_maxResult', _self.collection_Forum_TopicRelated_LikeTopic.collection_likeTopic_maxResult);
			}
			if(_self.forumType == '话题' && _self.forumChildType == '热门话题' && _self.displayType == '集合'){
				formData.append('collection_hotTopic_maxResult', _self.collection_Forum_TopicRelated_HotTopic.collection_hotTopic_maxResult);
			}
			
			if(_self.forumType == '问答' && _self.forumChildType == '问题列表' && _self.displayType == '分页'){
				if(_self.page_Forum_QuestionRelated_Question.tagIdGroup != null && _self.page_Forum_QuestionRelated_Question.tagIdGroup.length >0){
					for(let i=0; i<_self.page_Forum_QuestionRelated_Question.tagIdGroup.length; i++){
						let tagId = _self.page_Forum_QuestionRelated_Question.tagIdGroup[i];
						formData.append('page_question_tagId', tagId);
						break;
					}
				}
				formData.append('page_question_tag_transferPrameter', _self.page_Forum_QuestionRelated_Question.page_question_tag_transferPrameter);
				formData.append('page_question_sort', _self.page_Forum_QuestionRelated_Question.page_question_sort);
				formData.append('page_question_filterCondition', _self.page_Forum_QuestionRelated_Question.page_question_filterCondition);
				formData.append('page_question_filterCondition_transferPrameter', _self.page_Forum_QuestionRelated_Question.page_question_filterCondition_transferPrameter);
				formData.append('page_question_sort', _self.page_Forum_QuestionRelated_Question.page_question_sort);
				formData.append('page_question_maxResult', _self.page_Forum_QuestionRelated_Question.page_question_maxResult);
				formData.append('page_question_pageCount', _self.page_Forum_QuestionRelated_Question.page_question_pageCount);
			}
			if(_self.forumType == '问答' && _self.forumChildType == '答案列表' && _self.displayType == '分页'){
				formData.append('page_answer_sort', _self.page_Forum_AnswerRelated_Answer.page_answer_sort);
				formData.append('page_answer_maxResult', _self.page_Forum_AnswerRelated_Answer.page_answer_maxResult);
				formData.append('page_answer_pageCount', _self.page_Forum_AnswerRelated_Answer.page_answer_pageCount);
			}
			if(_self.forumType == '问答' && _self.forumChildType == '相似问题' && _self.displayType == '集合'){
				formData.append('collection_likeQuestion_maxResult', _self.collection_Forum_QuestionRelated_LikeQuestion.collection_likeQuestion_maxResult);
			}
			if(_self.forumType == '红包' && _self.forumChildType == '领取红包用户列表' && _self.displayType == '分页'){
				formData.append('page_receiveRedEnvelopeUser_sort', _self.page_Forum_RedEnvelopeRelated_ReceiveRedEnvelopeUser.page_receiveRedEnvelopeUser_sort);
				formData.append('page_receiveRedEnvelopeUser_maxResult', _self.page_Forum_RedEnvelopeRelated_ReceiveRedEnvelopeUser.page_receiveRedEnvelopeUser_maxResult);
			}
			if(_self.forumType == '广告' && _self.forumChildType == '图片广告' && _self.displayType == '集合'){
				for(let i=0; i<_self.collection_Forum_AdvertisingRelated_imageList.tableList.length; i++){
					let table = _self.collection_Forum_AdvertisingRelated_imageList.tableList[i];

					let image_name = _self.collection_Forum_AdvertisingRelated_imageList.collection_image_name[i];
					let image_link = _self.collection_Forum_AdvertisingRelated_imageList.collection_image_link[i];
					formData.append('advertisingRelated_Image_Count', i);
					formData.append('collection_image_name', image_name);
					formData.append('collection_image_link', image_link);
					formData.append('collection_image_path', '');
					let flag = false;
					let fileList = _self.collection_Forum_AdvertisingRelated_imageList.uploadImg_elementNodes[i].uploadFiles;
					if(fileList != null && fileList.length >0){
						for(let i=0; i<fileList.length; i++){
							let file = fileList[i];
							flag = true;
							formData.append('collection_image_uploadImage', file.raw);
							break;
						}
					}
					if(!flag){
						formData.append('collection_image_uploadImage', new File([], '', {}));//空文件占位
					
					}

				}
			}
			if(_self.forumType == '在线帮助' && _self.forumChildType == '在线帮助列表' && _self.displayType == '单层'){
				if(_self.monolayer_Forum_HelpRelated_Help.helpTypeIdGroup != null && _self.monolayer_Forum_HelpRelated_Help.helpTypeIdGroup.length >0){
					for(let i=0; i<_self.monolayer_Forum_HelpRelated_Help.helpTypeIdGroup.length; i++){
						let helpTypeId = _self.monolayer_Forum_HelpRelated_Help.helpTypeIdGroup[i];
						formData.append('monolayer_help_helpTypeId', helpTypeId);
						break;
					}
				}
				formData.append('monolayer_help_helpType_transferPrameter', _self.monolayer_Forum_HelpRelated_Help.monolayer_help_helpType_transferPrameter);
				formData.append('monolayer_help_quantity', _self.monolayer_Forum_HelpRelated_Help.monolayer_help_quantity);
				formData.append('monolayer_help_more', _self.monolayer_Forum_HelpRelated_Help.monolayer_help_more);
				formData.append('monolayer_help_maxResult', _self.monolayer_Forum_HelpRelated_Help.monolayer_help_maxResult);
				formData.append('monolayer_help_pageCount', _self.monolayer_Forum_HelpRelated_Help.monolayer_help_pageCount);
				formData.append('monolayer_help_sort', _self.monolayer_Forum_HelpRelated_Help.monolayer_help_sort);
			}
			if(_self.forumType == '在线帮助' && _self.forumChildType == '在线帮助列表' && _self.displayType == '分页'){
				if(_self.page_Forum_HelpRelated_Help.helpTypeIdGroup != null && _self.page_Forum_HelpRelated_Help.helpTypeIdGroup.length >0){
					for(let i=0; i<_self.page_Forum_HelpRelated_Help.helpTypeIdGroup.length; i++){
						let helpTypeId = _self.page_Forum_HelpRelated_Help.helpTypeIdGroup[i];
						formData.append('page_help_helpTypeId', helpTypeId);
						break;
					}
				}
				formData.append('page_help_helpType_transferPrameter', _self.page_Forum_HelpRelated_Help.page_help_helpType_transferPrameter);
				formData.append('page_help_maxResult', _self.page_Forum_HelpRelated_Help.page_help_maxResult);
				formData.append('page_help_pageCount', _self.page_Forum_HelpRelated_Help.page_help_pageCount);
				formData.append('page_help_sort', _self.page_Forum_HelpRelated_Help.page_help_sort);
			}
			if(_self.forumType == '在线帮助' && _self.forumChildType == '推荐在线帮助' && _self.displayType == '集合'){
				if(_self.collection_Forum_HelpRelated_RecommendHelp.tableList != null && _self.collection_Forum_HelpRelated_RecommendHelp.tableList.length >0){	
					for(let i=0; i<_self.collection_Forum_HelpRelated_RecommendHelp.tableList.length; i++){
						let help = _self.collection_Forum_HelpRelated_RecommendHelp.tableList[i];
						formData.append('collection_recommendHelp_helpId', help.id);
					}
				}
			}
			if(_self.forumType == '自定义版块' && _self.forumChildType == '用户自定义HTML' && _self.displayType == '实体对象'){
				if(_self.$refs.entityBean_customForum_htmlContent.value != null && _self.$refs.entityBean_customForum_htmlContent.value !=''){
					formData.append('entityBean_customForum_htmlContent', _self.$refs.entityBean_customForum_htmlContent.value);
				}
			}
			if(_self.forumType == '系统' && _self.forumChildType == '热门搜索词' && _self.displayType == '集合'){
				if(_self.collection_Forum_SystemRelated_SearchWord.formOptions != null && _self.collection_Forum_SystemRelated_SearchWord.formOptions.length >0){
					for(let i=0; i<_self.collection_Forum_SystemRelated_SearchWord.formOptions.length; i++){
						let formOption = _self.collection_Forum_SystemRelated_SearchWord.formOptions[i];
						formData.append('collection_searchWord', formOption.value);
						
					}
				}
			}


			_self.$ajax({
		        method: 'post',
		        url: 'control/forum/manage?method=add',
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
			    		_self.$router.push({
							path : '/admin/control/forum/list',
							query:{ 
								layoutId : _self.layoutId,
								dirName : _self.dirName,
								sourcePage:_self.$route.query.sourcePage,
								page :_self.$route.query.page,
							}
						});
			    	}else if(returnValue.code === 500){//错误
			    		let errorMap = returnValue.data;
			    		A:for (let key in errorMap) {
			    			for (let errorKey in _self.error) { 
								let errorValue = _self.error[errorKey];

								if(getType(errorValue) == 'object'){//如果为Object类型		
									let parameterName = (key.lastIndexOf("_") != -1 ? key.substring(0,key.lastIndexOf("_")) : '');//删除最后一个序号 collection_image_name_0
									if(parameterName != "" && getType(errorValue[parameterName]) == 'map'){
										errorValue[parameterName].set(parseInt(key.substring(key.lastIndexOf("_")+1,key.length)),errorMap[key]);
										continue A;
									}
									
									
									if(errorValue[key] != undefined){
										errorValue[key] = errorMap[key];
										continue A;
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