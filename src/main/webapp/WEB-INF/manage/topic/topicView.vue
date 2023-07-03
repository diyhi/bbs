<!-- 话题查看 -->
<template id="topicView-template">
	<div>
		<div class="main blankBackground" >
			<div class="navbar">
				
				<el-button type="primary" plain size="small"  @click="$router.push({path: sourceUrlObject.path, query:sourceUrlObject.query})">返回</el-button>
			
			</div>
			<div class="topicViewModule">
	        	<div class="topic-wrap">
					<div class="topicTag">
						<span class="tag">{{topic.tagName}}</span>
					</div>
					<div class="operat">
						<el-link class="item" href="javascript:void(0);" v-if="topic.status == 10" @click="auditTopic(topic.id);">审核</el-link>
						<el-link class="item" href="javascript:void(0);" @click="$router.push({path: '/admin/control/topicLike/list', query:{ visible:($route.query.visible != undefined ? $route.query.visible:''),topicView_beforeUrl:($route.query.topicView_beforeUrl != undefined ? $route.query.topicView_beforeUrl:''),topicId :topic.id, commentId:($route.query.commentId != undefined ? $route.query.commentId:''),topicPage:($route.query.page != undefined ? $route.query.page:'')}})">点赞用户</el-link>
						<el-link class="item" href="javascript:void(0);" v-if="topic.giveRedEnvelopeId != null && topic.giveRedEnvelopeId != ''" @click="$router.push({path: '/admin/control/redEnvelope/redEnvelopeAmountDistribution/list', query:{giveRedEnvelopeId:topic.giveRedEnvelopeId, visible:($route.query.visible != undefined ? $route.query.visible:''),topicView_beforeUrl:($route.query.topicView_beforeUrl != undefined ? $route.query.topicView_beforeUrl:''),topicId :topic.id, commentId:($route.query.commentId != undefined ? $route.query.commentId:''),topicPage:($route.query.page != undefined ? $route.query.page:'')}})">红包</el-link>
						<el-link class="item" href="javascript:void(0);" @click="$router.push({path: '/admin/control/topicFavorite/list', query:{ visible:($route.query.visible != undefined ? $route.query.visible:''),topicView_beforeUrl:($route.query.topicView_beforeUrl != undefined ? $route.query.topicView_beforeUrl:''),topicId :topic.id, commentId:($route.query.commentId != undefined ? $route.query.commentId:''),topicPage:($route.query.page != undefined ? $route.query.page:'')}})">收藏用户</el-link>
						<el-link class="item" href="javascript:void(0);" @click="$router.push({path: '/admin/control/topicReport/list', query:{ visible:($route.query.visible != undefined ? $route.query.visible:''),topicView_beforeUrl:($route.query.topicView_beforeUrl != undefined ? $route.query.topicView_beforeUrl:''),topicId :topic.id, commentId:($route.query.commentId != undefined ? $route.query.commentId:''),topicPage:($route.query.page != undefined ? $route.query.page:''), parameterId:topic.id,module:10}})">举报</el-link>
                    	<el-link class="item" href="javascript:void(0);" @click="$store.commit('setCacheNumber'); $router.push({path: '/admin/control/topic/topicUnhideList', query:{ visible:($route.query.visible != undefined ? $route.query.visible:''),topicView_beforeUrl:($route.query.topicView_beforeUrl != undefined ? $route.query.topicView_beforeUrl:''),topicId :topic.id, commentId:($route.query.commentId != undefined ? $route.query.commentId:''),topicPage:($route.query.page != undefined ? $route.query.page:'')}})">解锁隐藏内容用户</el-link>
						<el-link class="item" href="javascript:void(0);" @click="editTopicUI();">修改</el-link>
						<el-link class="item" href="javascript:void(0);" @click="deleteTopic(topic.id)">删除</el-link>
					</div>
					
					<div class="editTopic-post" v-show="editTopicFormView">
						<div class="editTopic">
							<el-form label-width="auto"  @submit.native.prevent>
								<el-form-item label="标题" :required="true" :error="error.title">
									<el-input v-model.trim="title" maxlength="100" clearable="true" show-word-limit></el-input>
								</el-form-item>
								<el-form-item label="标签" :required="true" :error="error.tagId">
									<el-select v-model="tagIdGroup" @focus="queryTagList" @change="selectedTag" no-match-text="还没有标签" placeholder="选择标签">
										<el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"></el-option>
									</el-select>
								</el-form-item>
								<el-form-item label="排序" :required="true" :error="error.sort">
									<el-input-number ref="sort_ref" v-model="sort" controls-position="right" :min="0" :max="999999999"></el-input-number>
									<div class="form-help" >数字越大越在前</div>
								</el-form-item>
								<el-form-item label="允许评论" :required="true" :error="error.allow">
									<el-switch v-model="allow" ></el-switch>
								</el-form-item>
								<el-form-item label="精华" :error="error.essence">
									<el-switch v-model="essence" ></el-switch>
								</el-form-item>
								<el-form-item label="状态" :required="true" :error="error.status">
									<el-radio-group v-model="status">
									    <el-radio :label="10">待审核</el-radio>
									    <el-radio :label="20">已发布</el-radio>
									</el-radio-group>
								</el-form-item>
								<el-form-item label="内容" :required="true">
									<textarea ref="topicContent" style="width:100%;height:300px;visibility:hidden;"></textarea>
								</el-form-item>
								<el-form-item>
								    <el-button class="submitButton" type="primary" @mousedown.native="editTopic" :disabled="editTopic_disabled">提交</el-button>
								    <el-button class="submitButton" type="primary" plain @mousedown.native="cancelEditTopic();">取消</el-button>
								</el-form-item>
							</el-form>
						</div>
					</div>
					
					<div class="head">
		                <div class="title">
		                	{{topic.title}}
		                	<span class="essence" v-if="topic.essence == true">精华</span>
		                	<span class="top" v-if="topic.sort >0">置顶</span>
		                </div>
		                <div class="topicInfo clearfix" >
		                	<div class="avatar">
		                		<el-popover effect="light" trigger="hover" placement="top">
						        	<template #default>
						        		<p v-if="topic.isStaff == false">呢称: {{topic.nickname}}</p>
							            <p>账号: {{topic.account}}</p>
							            <p v-if="topic.userRoleNameList != null && topic.userRoleNameList.length >0" >角色: 
							            	<span class="topicViewModule_topic-wrap_head_topicInfo_userRoleName" v-for="roleName in topic.userRoleNameList" >{{roleName}}</span>
							            </p> 
							            
							            
						        	</template>
						        	<template #reference v-if="topic.isStaff == false">
						          		<div class="avatar-wrapper" >
											<div class="avatar-badge" v-if="topic.avatarName == null || topic.avatarName == ''">
												<el-avatar shape="square" :size="64" icon="el-icon-user-solid"></el-avatar>
											</div>
											<div class="avatar-badge" v-if="topic.avatarName != null && topic.avatarName != ''">
												<el-avatar shape="square" :size="64" :src="topic.avatarPath+'100x100/'+topic.avatarName"></el-avatar>
											</div>
										</div>
						        	</template>
						        	
						        	<template #reference v-if="topic.isStaff == true">
						        		<div class="avatar-wrapper">
											<el-badge value="员工" type="warning" class="avatar-badge">
												<el-avatar shape="square" :size="64" icon="el-icon-user-solid"></el-avatar>
											</el-badge>
										</div>
						        	</template>
						        </el-popover>
		                	</div>
		                	<div class="userName" title="账号">
		                		{{topic.account}}
		                		<div class="nickname" title="呢称">
		                			{{topic.nickname}}
		                			<span class="topicViewModule_topic-wrap_head_topicInfo_userRoleName" v-if="topic.userRoleNameList != null && topic.userRoleNameList.length >0" v-for="roleName in topic.userRoleNameList" title="角色">{{roleName}}</span> 
		                		</div>
		                	</div>
		                	<div class="postTime" title="发表时间">{{topic.postTime}}</div>
		                	<div class="viewTotal-icon" title="查看总数"><i class="el-icon-view"></i></div>
		                	<div class="viewTotal" title="查看总数">{{topic.viewTotal}}次阅读</div>
		                	<div class="comment-icon" title="评论总数"><i class="el-icon-chat-dot-round"></i></div>
		                	<div class="comment" title="评论总数">{{topic.commentTotal}}个评论</div>
		                	
		                	<div class="ipAddress" title="IP地址">
		                		{{topic.ip}}&nbsp;{{topic.ipAddress}}
		                		
		                		<div class="statusTagInfo">
									<span class="orange-tag" v-if="topic.status ==10" title="话题状态">待审核</span>
									<span class="green-tag" v-if="topic.status ==20" title="话题状态">已发布</span>
									<span class="red-tag" v-if="topic.status ==110" title="话题状态">待审核员工删除</span>
									<span class="red-tag" v-if="topic.status ==120" title="话题状态">已发布员工删除</span>	
									<span v-if="topic.giveRedEnvelopeId != null && topic.giveRedEnvelopeId != ''" class="redEnvelope" title="发红包">红包 {{giveRedEnvelope.totalAmount}} 元</span>
							
		                		</div>
		                	</div>
		                </div>
					</div>
					<div class="main"  :class="$route.query.reportModule !=undefined && parseInt($route.query.reportModule) == 10 ? 'reportMark' : ''">
						<div v-if="topic.lastUpdateTime != null" class="lastUpdateTime" >最后修改时间：{{topic.lastUpdateTime}}</div>
						<div :ref="'topic_'+topic.id">
							<component v-bind:is ="topicComponent(topic.content)" v-bind="$props" /> 
						</div>
					</div>
					
				</div>
			</div>
			
			<div class="commentModule">
				<div class="commentList">
					<div class="item" :class="$route.query.reportModule !=undefined && parseInt($route.query.reportModule) == 20 && comment.id == $route.query.commentId ? 'reportMark' : ''" v-for="(comment,index) in commentList" :key="comment.id" :commentId="comment.id"  :ref="handleNodes">
						<div class="head">
							<div class="avatarBox">
		                		<el-popover effect="light" trigger="hover" placement="top">
						        	<template #default>
						        		<p v-if="comment.isStaff == false">呢称: {{comment.nickname}}</p>
							            <p>账号: {{comment.account}}</p>
							            <p v-if="comment.userRoleNameList != null && comment.userRoleNameList.length >0" >角色: 
							            	<span class="topicViewModule_topic-wrap_head_topicInfo_userRoleName" v-for="roleName in comment.userRoleNameList" >{{roleName}}</span>
							            </p> 
							            
							            
						        	</template>
						        	<template #reference v-if="comment.isStaff == false">
						          		<div class="avatar-wrapper" >
											<div class="avatar-badge" v-if="comment.avatarName == null || comment.avatarName == ''">
												<el-avatar shape="square" :size="64" icon="el-icon-user-solid"></el-avatar>
											</div>
											<div class="avatar-badge" v-if="comment.avatarName != null && comment.avatarName != ''">
												<el-avatar shape="square" :size="64" :src="comment.avatarPath+'100x100/'+comment.avatarName"></el-avatar>
											</div>
										</div>
						        	</template>
						        	
						        	<template #reference v-if="comment.isStaff == true">
						        		<div class="avatar-wrapper">
											<el-badge value="员工" type="warning" class="avatar-badge">
												<el-avatar shape="square" :size="64" icon="el-icon-user-solid"></el-avatar>
											</el-badge>
										</div>
						        	</template>
						        </el-popover>
		                	</div>
						
						
							<span class="info" >
						    	<h2 class="clearfix" >
						    		
						        	<div class="userName">
						        		{{comment.account}}
						        		<div class="nickname" >
						        			{{comment.nickname}}
						        			<i class="userRoleName" v-for="roleName in comment.userRoleNameList" >{{roleName}}</i>
										    <i class="master" v-if="comment.userName == topic.userName && comment.isStaff == topic.isStaff">作者</i>
						        		</div>
						        	</div>
						        	
						        </h2>
	                        	
	                          	<div class="time clearfix">{{comment.postTime}}</div>
	                        </span>
	                        <span class="floor">{{index+1}}楼</span>                     
	                    </div>
	                    <div class="main">
							<div class="quote"  v-if="comment.quoteList != null && comment.quoteList.length >0" >
								<component v-bind:is ="quoteDataComponent(quoteData.get(comment.id))" v-bind="$props" />
							</div>
							

							<p class="commentContent" :ref="'comment_'+comment.id">
								<component v-bind:is ="commentDataComponent(comment.content)" v-bind="$props" />
							</p> 
							
							
							<div class="clearfix"></div>
							<div class="replyList" v-if="comment.replyList.length >0">
								 <ul class="timeline box">
									<li class="timeline-item replyItem-container" v-for="(reply,index2) in comment.replyList" :key="reply.id"  :replyId="reply.id" :ref="handleReplyNodes">
										<div class="tail" v-if="line.get(reply.id)"></div>
                                    	<div class="node node--normal" v-if="dot.get(reply.id)"></div>
										<div class="replyItem">
											<div :class="$route.query.reportModule !=undefined && parseInt($route.query.reportModule) == 30 && reply.id == $route.query.replyId ? 'reply-reportMark' : ''">
                                        		<div class="reply-top" >
											    	<div class="avatarBox">
								                		<el-popover effect="light" trigger="hover" placement="top">
												        	<template #default>
												        		<p v-if="reply.isStaff == false">呢称: {{reply.nickname}}</p>
													            <p>账号: {{reply.account}}</p>
													            <p v-if="reply.userRoleNameList != null && reply.userRoleNameList.length >0" >角色: 
													            	<span class="topicViewModule_topic-wrap_head_topicInfo_userRoleName" v-for="roleName in reply.userRoleNameList" >{{roleName}}</span>
													            </p> 
													            
													            
												        	</template>
												        	<template #reference v-if="reply.isStaff == false">
												          		<div class="avatar-wrapper" >
																	<div class="avatar-badge" v-if="reply.avatarName == null || reply.avatarName == ''">
																		<el-avatar shape="square" :size="48" icon="el-icon-user-solid"></el-avatar>
																	</div>
																	<div class="avatar-badge" v-if="reply.avatarName != null && reply.avatarName != ''">
																		<el-avatar shape="square" :size="48" :src="reply.avatarPath+'100x100/'+reply.avatarName"></el-avatar>
																	</div>
																</div>
												        	</template>
												        	
												        	<template #reference v-if="reply.isStaff == true">
												        		<div class="avatar-wrapper">
																	<el-badge value="员工" type="warning" class="avatar-badge">
																		<el-avatar shape="square" :size="48" icon="el-icon-user-solid"></el-avatar>
																	</el-badge>
																</div>
												        	</template>
												        </el-popover>
								                	</div>
								                	<span class="info" >
												    	<h2 class="clearfix" >
												    		
												        	<div class="userName">
												        		{{reply.account}}
												        		<div class="nickname" >
												        			{{reply.nickname}}
												        			<i class="userRoleName" v-for="roleName in reply.userRoleNameList" >{{roleName}}</i>
																    <i class="master" v-if="reply.userName == topic.userName && reply.isStaff == topic.isStaff">作者</i>
												        		</div>
												        	</div>
												        	
												        </h2>
							                        	
							                          	<div class="time clearfix">{{reply.postTime}}</div>
							                        </span>
							                        <span class="friendInfo" v-if="reply.friendUserName != null && reply.friendUserName != ''">
	                                                    <span class="arrow">
	                                                    	<i class="el-icon-caret-right"></i>
	                                                    </span>
	                                                    <div class="friendAvatarBox">
	                                                        <el-popover effect="light" trigger="hover" placement="top">
	                                                            <template #default>
	                                                                <p v-if="reply.isFriendStaff == false">呢称: {{reply.friendNickname}}</p>
	                                                                <p>账号: {{reply.friendAccount}}</p>
	                                                            </template>
	                                                            <template #reference v-if="reply.isFriendStaff == false">
	                                                                <div class="avatar-wrapper" >
	                                                                    <div class="avatar-badge" v-if="reply.friendAvatarName == null || reply.friendAvatarName == ''">
	                                                                        <el-avatar shape="square" :size="48" icon="el-icon-user-solid"></el-avatar>
	                                                                    </div>
	                                                                    <div class="avatar-badge" v-if="reply.friendAvatarName != null && reply.friendAvatarName != ''">
	                                                                        <el-avatar shape="square" :size="48" :src="reply.friendAvatarPath+'100x100/'+reply.friendAvatarName"></el-avatar>
	                                                                    </div>
	                                                                </div>
	                                                            </template>
	                                                            
	                                                            <template #reference v-if="reply.isFriendStaff == true">
	                                                                <div class="avatar-wrapper">
	                                                                    <el-badge value="员工" type="warning" class="avatar-badge">
	                                                                        <el-avatar shape="square" :size="48" icon="el-icon-user-solid"></el-avatar>
	                                                                    </el-badge>
	                                                                </div>
	                                                            </template>
	                                                        </el-popover>
	                                                    </div>
	                                                
	                                                    <h2 class="namedInfo clearfix" >
	                                                        <div class="userName">
	                                                            {{reply.friendAccount}}
	                                                            <div class="nickname" >
	                                                                {{reply.friendNickname}}
	                                                                <i class="master" v-if="reply.friendUserName == topic.userName && reply.isFriendStaff == topic.isStaff">作者</i>
	                                                            </div>
	                                                        </div>
	                                                    </h2>
	                                                </span>
							                        
							                        <div class="operatInfo">
							                        	<span class="orange-tag" v-if="reply.status ==10" title="回复状态">待审核</span>
														<span class="green-tag" v-if="reply.status ==20" title="回复状态">已发布</span>
														<span class="red-tag" v-if="reply.status ==110" title="回复状态">待审核用户删除</span>
														<span class="red-tag" v-if="reply.status ==120" title="回复状态">已发布用户删除</span>
														<span class="red-tag" v-if="reply.status ==100010" title="回复状态">待审核员工删除</span>
														<span class="red-tag" v-if="reply.status ==100020" title="回复状态">已发布员工删除</span>
							                        	<el-link class="operat-btn" href="javascript:void(0);" v-if="reply.status ==10"  @click="auditReply(reply.id)">审核</el-link>
							                        	<el-link class="operat-btn" href="javascript:void(0);" @click="addReplyFriendUI(reply.id)">回复</el-link>
		                                                    
							                        	<el-link class="operat-btn" href="javascript:void(0);" @click="editReplyUI(reply)">修改</el-link>
							                        	<el-link class="operat-btn" href="javascript:void(0);" v-if="reply.status >100"  @click="recoveryReply(reply.id)">恢复</el-link>
							                        	<el-link class="operat-btn" href="javascript:void(0);" @click="$router.push({path: '/admin/control/topicReport/list', query:{ visible:($route.query.visible != undefined ? $route.query.visible:''),topicView_beforeUrl:($route.query.topicView_beforeUrl != undefined ? $route.query.topicView_beforeUrl:''),topicId :topic.id, commentId:($route.query.commentId != undefined ? $route.query.commentId:''),topicPage:($route.query.page != undefined ? $route.query.page:''), parameterId:reply.id,module:30}})">举报</el-link>
                    
							                        	<el-link class="operat-btn" href="javascript:void(0);" @click="deleteReply(reply.id)">删除</el-link>
							                        
							                        </div>
							                        
												</div>
												<div style="clear:both; height: 0; line-height: 0; font-size: 0;"></div>
												<div class="editCommentReply-formModule" v-show="editReplyFormView.get(reply.id)">
													<div class="editReply-wrap">
														<el-form label-width="100"  @submit.native.prevent>
															<el-form-item label="状态" :required="true" >
																<el-radio-group v-model="editReplyStatusField[index][index2]">
																    <el-radio :label="10">待审核</el-radio>
																    <el-radio :label="20">已发布</el-radio>
																</el-radio-group>
															</el-form-item>
															<el-form-item label="内容" :required="true">
																<el-input type="textarea" :autosize="{minRows: 5}" placeholder="请输入内容" v-model="editReplyContentField[index][index2]"></el-input>
															</el-form-item>
															<el-form-item>
															    <el-button class="submitButton" type="primary" @mousedown.native="editReply(reply.id)" :disabled="editReply_disabled.get(reply.id)">提交</el-button>
															    <el-button class="submitButton" type="primary" plain @mousedown.native="cancelEditReply(reply.id);">取消</el-button>
															</el-form-item>
														</el-form>
													</div>
												</div>
												
												<div class="replyContent" @click="clickReplyLevel(comment.id,reply.id)">{{reply.content}}</div>
												<!-- 回复对方 -->
		                                        <div class="addReplyFriend-post" v-show="addReplyFriendFormView.get(reply.id)">
		                                            <el-form @submit.native.prevent>
		                                                <el-form-item >
		                                                    <el-input type="textarea" :autosize="{minRows: 5}" placeholder="请输入内容" v-model="addReplyFriendContentField[reply.id]"></el-input>
		                                                </el-form-item>
		                                                <el-form-item>
		                                                    <el-button class="submitButton" size="large" type="primary" @mousedown.native="addReplyFriend(comment.id,reply.id)" :disabled="addReplyFriend_disabled.get(reply.id)">提交</el-button>
		                                                    <el-button class="submitButton" size="large" type="primary" plain @mousedown.native="cancelAddReplyFriend(reply.id);">取消</el-button>
		                                                </el-form-item>
		                                            </el-form>
		                                        </div>
											</div>
										</div>
									</li>
								</ul>
							</div>                  
						</div>
						
						<div class="addReply-post" v-show="addReplyFormView.get(comment.id)">
							<el-form @submit.native.prevent>
								<el-form-item >
									<el-input type="textarea" :autosize="{minRows: 5}" placeholder="请输入内容" v-model="addReplyContentField[index]"></el-input>
								</el-form-item>
								<el-form-item>
								    <el-button class="submitButton" type="primary" @mousedown.native="addReply(comment.id)" :disabled="addReply_disabled.get(comment.id)">提交</el-button>
								    <el-button class="submitButton" type="primary" plain @mousedown.native="cancelAddReply(comment.id);">取消</el-button>
								</el-form-item>
							</el-form>
						</div>
						<div class="quote-post" v-show="quoteFormView.get(comment.id)">
							<div class="quote-formModule">
								<div class="addQuote-wrap">
									<div class="head-tag"><i class="el-icon-eleme cms-quote-left-solid icon"></i></div>
									<textarea :ref="handleQuoteNodes" :commentId="comment.id" class="quote-textarea"  ></textarea>
									<div class="form-action">
										<div class="quoteSubmit">
											<el-button class="submitButton" type="primary" @mousedown.native="addQuote(comment.id);" :disabled="addQuote_disabled.get(comment.id)">提交</el-button>
											<el-button class="submitButton" type="primary" plain @mousedown.native="cancelQuote(comment.id);">取消</el-button>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="editComment-post" v-show="editCommentFormView.get(comment.id)">
							<div class="editComment-formModule">
								<div class="editComment-wrap">
									<div class="head-tag"><i class="cms-pencil-alt icon"></i></div>
									
									<el-form label-position="top" label-width="auto"  @submit.native.prevent>
										<el-form-item label="状态" >
											<el-radio-group v-model="commentStatusField[index]">
											    <el-radio :label="10">待审核</el-radio>
											    <el-radio :label="20">已发布</el-radio>
											</el-radio-group>
										</el-form-item>
									</el-form>
									
									
									<textarea :ref="handleEditCommentNodes" :commentId="comment.id" class="editComment-textarea" ></textarea>
										
									<div class="form-action">
										<div class="editCommentSubmit">
											<el-button class="submitButton" type="primary" @mousedown.native="editComment(comment.id);" :disabled="editComment_disabled.get(comment.id)">提交</el-button>
											<el-button class="submitButton" type="primary" plain @mousedown.native="cancelEditComment(comment.id);">取消</el-button>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="bottomInfo">
							<div class="toolbar">
							
								<span class="orange-tag" v-if="comment.status ==10" title="评论状态">待审核</span>
								<span class="green-tag" v-if="comment.status ==20" title="评论状态">已发布</span>
								<span class="red-tag" v-if="comment.status ==110" title="评论状态">待审核用户删除</span>
								<span class="red-tag" v-if="comment.status ==120" title="评论状态">已发布用户删除</span>
								<span class="red-tag" v-if="comment.status ==100010" title="评论状态">待审核员工删除</span>
								<span class="red-tag" v-if="comment.status ==100020" title="评论状态">已发布员工删除</span>
	
								<el-link class="operat-btn" href="javascript:void(0);"v-if="comment.status ==10" @click="auditComment(comment.id)">审核</el-link>
					            <el-link class="operat-btn" href="javascript:void(0);" @click="addReplyUI(comment.id)">回复</el-link>
					            <el-link class="operat-btn" href="javascript:void(0);" @click="addQuoteUI(comment.id)">引用</el-link>
					            <el-link class="operat-btn" href="javascript:void(0);" @click="editCommentUI(comment)">修改</el-link>
					            <el-link class="operat-btn" href="javascript:void(0);" v-if="comment.status >100" @click="recoveryComment(comment.id)">恢复</el-link>
					            <el-link class="operat-btn" href="javascript:void(0);" @click="$router.push({path: '/admin/control/topicReport/list', query:{ visible:($route.query.visible != undefined ? $route.query.visible:''),topicView_beforeUrl:($route.query.topicView_beforeUrl != undefined ? $route.query.topicView_beforeUrl:''),topicId :topic.id, commentId:($route.query.commentId != undefined ? $route.query.commentId:''),topicPage:($route.query.page != undefined ? $route.query.page:''), parameterId:comment.id,module:20}})">举报</el-link>
                   
					            <el-link class="operat-btn" href="javascript:void(0);" @click="deleteComment(comment.id)">删除</el-link>
					            
								
							
							
							
							</div>
						</div>
					</div>
					
					
				    <div class="pagination-wrapper" v-if="isShowPage">
						<el-pagination background  @current-change="page" :current-page="currentpage"  :page-size="maxresult" layout="total, prev, pager, next,jumper" :total="totalrecord"></el-pagination>
					</div>
				    
				    
				    <div class="addComment" >
						<el-form @submit.native.prevent>
							<el-form-item :error="error.content">
								<textarea ref="commentContent" style="width:100%;height:300px;visibility:hidden;"></textarea>
							</el-form-item>
							<el-form-item>
							    <el-button class="submitButton" type="primary" @mousedown.native="addCommentForm" :disabled="addCommentForm_disabled">提交</el-button>
							    
							</el-form-item>
						</el-form>
					</div>
				</div>
				
			</div>
			
			
			
		</div>
	</div>
</template>

<script>
//话题查看
export default({
	name: 'topicView',//组件名称，keep-alive缓存需要本参数
	template : '#topicView-template',
	inject:['reload'], 
	data : function () {
		return {
			topicId :'',//话题Id
			commentId :'',//评论Id
			topic :'',//话题
			giveRedEnvelope:'',//红包
			availableTag:'',//富文本编辑器允许使用的标签
			userName:'',//用户名称
			commentList:'',
			totalrecord : 0, //总记录数
		    currentpage : 1, //当前页码
			totalpage : 1, //总页数
			maxresult: 12, //每页显示记录数
			isShowPage:false,//是否显示分页 maxresult没返回结果前就显示会导致分页栏显示页码错误
			
			
			addCommentEditor :'',//添加评论编辑器
			addCommentEditorCreateParameObject :{},//添加评论编辑器的创建参数
			
			
			quoteFormView : new Map(),//评论引用表单  key:评论Id value:是否显示
			quoteEditorMap:new Map(),//引用评论富文本编辑器集合 key:评论Id value:富文本编辑器
			quoteEditorCreateParameMap:new Map(),//引用评论编辑器的创建参数 key:评论Id value:编辑器参数对象
			quoteData : new Map(),//引用数据 map格式 key:评论Id value:引用html数据
			
			editCommentFormView : new Map(),//修改评论表单  key:评论Id value:是否显示
			editCommentEditorMap:new Map(),//修改评论富文本编辑器集合 key:评论Id value:富文本编辑器
			editCommentEditorCreateParameMap:new Map(),//修改评论编辑器的创建参数 key:评论Id value:编辑器参数对象
			
			editCommentData : new Map(),//修改评论数据 map格式 key:评论Id value:修改html数据
			commentStatusField : [], //评论状态项绑定
			
			title :'',
			tagId :'',//标签Id
			sort : 0,
			allow : true,
			status :0,
			essence:false,
			isAllowLoadTagGroup:true,//是否允许加载标签组
			tagIdGroup : [], //标签Id组
			loading :false,//是否正在从远程获取数据
			options: [],//Select 选择器标签数据
			editTopicFormView:false,//修改话题表单是否显示
			editTopicEditor :'',//修改话题编辑器
			editTopicEditorCreateParameObject :{},//修改话题编辑器的创建参数
			
			
			error : {
				title :'',
				tagId :'',
				sort :'',
				content :'',
				essence :'',
			},
			
		    
		    playerIdList: [],//视频播放Id列表
		    playerObjectList: [],//视频播放对象集合
		    playerNodeList: [],//视频节点对象集合
		    playerHlsList: [],//视频播放流对象集合
		    
		    commentElementNodes:[],//评论列表项Element节点集合
		    quoteElementNodes:[],//引用评论项Element节点集合
		    editCommentElementNodes:[],//修改评论项Element节点集合
			replyElementNodes:[],//回复列表项Element节点集合
		    
		    addCommentForm_disabled:false,//提交评论按钮是否禁用
		    addQuote_disabled : new Map(),//提交引用按钮是否禁用 map格式 key:评论Id value:是否禁用
		    editComment_disabled : new Map(),//提交修改评论按钮是否禁用 map格式 key:评论Id value:是否禁用
		    editTopic_disabled:false,//提交修改话题按钮是否禁用
		    addReplyFriend_disabled: new Map(),//提交添加回复按钮是否禁用 map格式 key:一语论Id value:是否禁用
		    addReply_disabled: new Map(),//提交添加回复按钮是否禁用 map格式 key:一语论Id value:是否禁用
		    editReply_disabled: new Map(),//提交修改回复按钮是否禁用 map格式 key:回复Id value:是否禁用
		    
		    
		 
		    editReplyStatusField : [], //修改回复状态项绑定(二维数组)
		    editReplyContentField : [], //修改回复内容项绑定(二维数组)
			editReplyFormView : new Map(),//修改回复表单  key:回复Id value:是否显示
			
			
			addReplyContentField : [], //添加回复内容项绑定
			addReplyFormView : new Map(),//添加回复表单  key:评论Id value:是否显示
			
			addReplyFriendContentField : {}, //添加回复对方内容项绑定 key:回复Id value:内容 示例{回复Id-1 : 内容,回复Id-2 : 内容}
			addReplyFriendFormView : new Map(),//添加回复对方表单  key:回复Id value:是否显示
	        line : new Map(),//楼中楼的线  key:回复Id value:是否显示
	        dot : new Map(),//楼中楼的点  key:回复Id value:是否显示
			
			sourceUrlObject:{},//来源URL对象
			
		};
	},
	//keep-alive 进入时
	activated: function () {
		let _self = this;
		if (Object.keys(_self.addCommentEditorCreateParameObject).length != 0) {//不等于空
			//创建富文本编辑器
			_self.addCommentEditor = createEditor(
				_self.addCommentEditorCreateParameObject.ref, 
				_self.addCommentEditorCreateParameObject.availableTag, 
				_self.addCommentEditorCreateParameObject.uploadPath, 
				null
			);
		}
		
		
		//创建富文本编辑器
		_self.quoteEditorCreateParameMap.forEach(function(value,key){
			let editor = createEditor(
				value.ref, 
				value.availableTag, 
				value.uploadPath, 
				null
			);
		
			_self.quoteEditorMap.set(key,editor);
	　　});
	
		//创建富文本编辑器
		_self.editCommentEditorCreateParameMap.forEach(function(value,key){
			let editor = createEditor(
				value.ref, 
				value.availableTag, 
				value.uploadPath, 
				null
			);
		
			_self.editCommentEditorMap.set(key,editor);
	　　});
	
		if (Object.keys(_self.editTopicEditorCreateParameObject).length != 0) {//不等于空
			//创建富文本编辑器
			_self.editTopicEditor = createEditor(
				_self.editTopicEditorCreateParameObject.ref, 
				_self.editTopicEditorCreateParameObject.availableTag, 
				_self.editTopicEditorCreateParameObject.uploadPath, 
				_self.editTopicEditorCreateParameObject.userGradeList
			);
		}
		
		
	},
	
	// keep-alive 离开时
	deactivated : function () {
		if(this.addCommentEditor != ""){
			this.addCommentEditor.remove();
		}
		
		
		this.quoteEditorMap.forEach(function(value,key){
			value.remove();
	　　});
		this.quoteEditorMap.clear();
		
		this.editCommentEditorMap.forEach(function(value,key){
			value.remove();
	　　});
		this.editCommentEditorMap.clear();
		
		if(this.editTopicEditor != ""){
			this.editTopicEditor.remove();
		}
	},
	
	//生命周期钩子 -- 响应数据修改时运行
	updated : function () {
		let _self = this;
		_self.$nextTick(function() {
			if(_self.topicId != ''){
				let topicRefValue = _self.$refs['topic_'+_self.topicId];
				if(topicRefValue != undefined){
					_self.renderBindNode(topicRefValue); 
				}
            	
			}
			if(_self.commentList != null && _self.commentList.length > 0){
				for (let i = 0; i <_self.commentList.length; i++) {
					let comment = _self.commentList[i];
					let commentRefValue = _self.$refs['comment_'+comment.id];
					if(commentRefValue != undefined){
						_self.renderBindNode(commentRefValue); 
					}
					
				}
			}
		})
	},
	beforeRouteEnter (to, from, next) {
		//上级路由编码
		if(to.query.topicView_beforeUrl == undefined || to.query.topicView_beforeUrl==''){//前一个URL
			let parameterObj = new Object;
			parameterObj.path = from.path;
			let query = from.query;
			for(let q in query){
				query[q] = encodeURIComponent(query[q]);
			}
			
			parameterObj.query = query;
			//将请求参数转为base62
			let encrypt = delete_base62_equals(base62_encode(JSON.stringify(parameterObj)));
			
			
			let newFullPath = updateURLParameter(to.fullPath,'topicView_beforeUrl',encrypt);
			
			to.fullPath = newFullPath;
			
			let paramGroup = to.query;
			paramGroup.topicView_beforeUrl = encrypt;
			to.query = paramGroup;
		}
		next();
	},
	created : function () {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		if(this.$route.query.visible != undefined && this.$route.query.visible != ''){
			this.visible = this.$route.query.visible;
		}
		if(this.$route.query.topicId != undefined && this.$route.query.topicId != ''){
			this.topicId = this.$route.query.topicId;
		}
		if(this.$route.query.commentId != undefined && this.$route.query.commentId != ''){
			this.commentId = this.$route.query.commentId;
		}
		if(this.$route.query.page != undefined && this.$route.query.page != ''){
			this.currentpage = this.$route.query.page;
		}
		
		//上级路由解码
		if(this.$route.query.topicView_beforeUrl != undefined && this.$route.query.topicView_beforeUrl != ''){
			let decrypt = base62_decode(add_base62_equals(this.$route.query.topicView_beforeUrl));
			
			let decryptObject = JSON.parse(decrypt);
			
			let query = decryptObject.query;
			for(let q in query){
				query[q] = decodeURIComponent(query[q]);
			}
			this.sourceUrlObject = {
				path : decryptObject.path,
				query :query
			}
		}
		
		
		
		//将话题对象先设置空字符串,防止ajax未加载完成时显示undefined
		this.topic = {content :''};
		
		this.queryTopic();
	},
	computed: {
		
		//动态解析评论引用模板数据
		quoteDataComponent: function() {
			return function (html) {
				return {
					template: html , // use content as template for this component 不能有换行符，否则换行符后面的数据不显示
					props: this.$props, // re-use current props definitions
					
				};
			};	
		},
		//动态解析评论模板数据
		commentDataComponent: function() {
			return function (html) {
				return {
					template: "<div>"+ html +"</div>", // use content as template for this component 必须用<div>标签包裹，否则会有部分内容不显示
					props: this.$props, // re-use current props definitions
					
				};
			};	
		},
		//动态解析话题模板数据
		topicComponent: function() {
			return function (html) {
				return {
					template: "<div>"+ html +"</div>", // use content as template for this component 必须用<div>标签包裹，否则会有部分内容不显示
					
					data : function() {
						return {
						};
					},
					mounted :function () {
						this.resumePlayerNodeData();
					},
					props: this.$props, // re-use current props definitions
					methods: {
				        //恢复播放器节点数据(vue组件切换时会自动刷新数据，视频播放器框在组件生成数据内容之后插入，组件刷新数据时播放器框会消失，组件刷新后需要用之前的节点数据恢复)
				        resumePlayerNodeData : function(){
				        	let _self = this;
				        	_self.$nextTick(function() {
					        	if(_self.$parent.playerObjectList.length >0){
					        		for(let i=0; i< _self.$parent.playerNodeList.length; i++){
					        			let playerNode = _self.$parent.playerNodeList[i];
					        			let playerId = playerNode.getAttribute("id");
					        			let node = document.getElementById(playerId);
					        			if(node != null){
					        				node.parentNode.replaceChild(playerNode,node);
					        			}
					        			
					        		}
					        	}
				        	});
				        }
				    }
				};
			};	
		},
	},
	methods : {
		//ref节点处理
		handleNodes(el) {
			this.commentElementNodes.push(el);
		},
		//引用评论ref节点处理
		handleQuoteNodes(el) {
			this.quoteElementNodes.push(el);
		},
		//评论ref节点处理
		handleEditCommentNodes(el) {
			this.editCommentElementNodes.push(el);
		},
		//回复ref节点处理
		handleReplyNodes(el) {
			this.replyElementNodes.push(el);
		},
	
	
		//查询话题
		queryTopic : function() {
			let _self = this;
			
			let params = {};
			if(_self.$route.query.page != undefined && _self.$route.query.page != ''){
				params = {
			    	method : 'view',
			    	topicId :_self.topicId,
			    	commentId :_self.commentId,
			    	page :_self.currentpage,
			    }
			}else{
				params = {
			    	method : 'view',
			    	topicId :_self.topicId,
			    	commentId :_self.commentId,
			    	
			    }
			}
			_self.$ajax.get('control/topic/manage', {
			    params: params
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
			    			if(key == "topic"){//话题
			    				let topic = mapData[key];
			    				
			    				//清空播放器
								_self.clearVideoPlayer();
								
								
								
								//处理自定义标签
								let contentNode = document.createElement("div");
								contentNode.innerHTML = topic.content;
								
								_self.bindNode(contentNode);
								topic.content = escapeVueHtml(contentNode.innerHTML);
								
								
								_self.topic = topic;
								
								
								
								_self.$nextTick(function() {
									setTimeout(function() {
										_self.renderVideoPlayer();//渲染视频播放器
									}, 30);
									
									
								});
			    			}else if(key == "giveRedEnvelope"){//发红包
								let giveRedEnvelope = mapData[key];
			    				_self.giveRedEnvelope = giveRedEnvelope;
			    			
			    			
			    			}else if(key == "availableTag"){//评论富文本框支持标签
			    				_self.availableTag = mapData[key];
				    			
			    			}else if(key == "userName"){//用户名称
								let userName = mapData[key];
			    				_self.userName = userName;
			    			}else if(key == "pageView"){//评论分页列表
			    				let pageView = mapData[key];
			    				
			    				let commentList = pageView.records;
			    				
			    				//清空
								_self.commentElementNodes.length = 0;
								_self.quoteElementNodes.length = 0;
			    				_self.editCommentElementNodes.length = 0;
			    				_self.commentStatusField.length = 0;
			    				_self.editReplyStatusField.length = 0;
			    				_self.editReplyContentField.length = 0;
			    				_self.commentList = '';
			    				
			    				
			    				if(commentList != null && commentList.length > 0){
				    				for (let i = 0; i <commentList.length; i++) {
										let comment = commentList[i];
										
										
										//组装引用数据
										if(comment.quoteList != null && comment.quoteList.length >0){
											let quoteContent = "";
											for (let j = 0; j <comment.quoteList.length; j++) {
												let quote = comment.quoteList[j];
												let avatarHtml = "";
												
												avatarHtml += "<div class=\"avatar\">";
							                	avatarHtml += 		"<el-popover effect=\"light\" trigger=\"hover\" placement=\"top\">";
											    avatarHtml += 		   	"<template #default>";
												
												
												if(quote.isStaff == false){
											   	//	avatarHtml += 			"<p>呢称: "+quote.nickname+"</p>";
											   		avatarHtml += 			"<p>呢称: "+escapeHtml(quote.nickname)+"</p>";
												}   	
												avatarHtml += 				"<p>账号: "+quote.account+"</p>";
											    if(quote.userRoleNameList != null && quote.userRoleNameList.length >0){
											        avatarHtml += 			"<p>角色: ";
											        for (let k = 0; k <quote.userRoleNameList.length; k++) {
											        	let roleName = quote.userRoleNameList[k];
											        	avatarHtml += 			"<span class=\"topicViewModule_topic-wrap_head_topicInfo_userRoleName\" >"+escapeHtml(roleName)+"</span>";
											        }
											        avatarHtml += 			"</p>";
												}       
											    avatarHtml += 		   	 "</template>";
											    	
											    if(quote.isStaff == false){
											   		avatarHtml += 			"<template #reference>";
											   		avatarHtml += 				"<div class=\"avatar-wrapper\" >";
											   		if(quote.avatarName == null || quote.avatarName == ''){
											   			avatarHtml += 				"<div class=\"avatar-badge\" >";
											   			avatarHtml += 					"<el-avatar shape=\"square\" :size=\"40\" icon=\"el-icon-user-solid\"></el-avatar>";
											   			avatarHtml += 				"</div>";
											   		}
											   		if(quote.avatarName != null && quote.avatarName != ''){
											   			avatarHtml += 				"<div class=\"avatar-badge\" >";
											   			avatarHtml += 					"<el-avatar shape=\"square\" :size=\"40\" src=\""+quote.avatarPath+"100x100/"+quote.avatarName+"\"></el-avatar>";
											   			avatarHtml += 				"</div>";
											   		}
											   		avatarHtml += 				"</div>";
											   		avatarHtml += 			"</template>";
												}    
											    if(quote.isStaff == true){
											   		avatarHtml += 			"<template #reference>";
											   		avatarHtml += 				"<div class=\"avatar-wrapper\" >";   		
											   		avatarHtml += 					"<el-badge value=\"员工\" type=\"warning\" class=\"avatar-badge\">";
											   		avatarHtml += 						"<el-avatar shape=\"square\" :size=\"40\" icon=\"el-icon-user-solid\"></el-avatar>";
											   		avatarHtml += 					"</el-badge>";
											   		avatarHtml += 				"</div>";
											   		avatarHtml += 			"</template>";
												} 
											    avatarHtml += 		"</el-popover>";
							                	avatarHtml += "</div>";
												
												avatarHtml += "<span class=\"info\" >";
												avatarHtml += 		"<h2 class=\"clearfix\" >";
											    avatarHtml += 			"<div class=\"userName\">";
											    avatarHtml += 				""+quote.account+"";
											    avatarHtml += 				"<div class=\"nickname\" >";
											    
											    if(quote.isStaff == false){
											    	avatarHtml += 					""+escapeHtml(quote.nickname)+"";
											    }
											   
											    for (let k = 0; k <quote.userRoleNameList.length; k++) {
											        let roleName = quote.userRoleNameList[k];
											        avatarHtml += 				"<i class=\"userRoleName\" >"+escapeHtml(roleName)+"</i>";
											    
											      
											    }
											    if(quote.userName == _self.topic.userName && quote.isStaff == _self.topic.isStaff){
											    	avatarHtml += 				"<i class=\"master\">作者</i>";
											    
											    }
											    avatarHtml += 				"</div>";
											    avatarHtml += 			"</div>";
						                        avatarHtml += 		"</h2>";
												avatarHtml += "</span>";
												
												quoteContent = "<div class=\"quoteComment\">"+quoteContent+"<span class=\"userInfo\">"+avatarHtml+"</span><div class=\"content\">"+quote.content+"</div></div>";
			
			
											}
											
											_self.quoteData.set(comment.id, escapeVueHtml(quoteContent));
										}
										
										//定义回复数组
										let editReplyStatusField_reply_array = [];
										let editReplyContentField_reply_array = [];
										
										//回复
										if(comment.replyList != null && comment.replyList.length >0){
											for (let j = 0; j <comment.replyList.length; j++) {
												let reply = comment.replyList[j];
												_self.editReplyFormView.set(reply.id,false);
												
												editReplyStatusField_reply_array.push(reply.status);
												editReplyContentField_reply_array.push(reply.content);
			    								
												_self.addReplyFriendFormView.set(reply.id,false);
                                                Object.assign(_self.addReplyFriendContentField, {[reply.id] : ''}); 
											}
										}
										
										//二维数组示例
										//let a=[1,2];
										//let b=[]; 
										//b[0]=a;//把数组a作为b数组的元素传入b数组中	 
										//alert(b[0][1]);//2
										_self.editReplyStatusField[i] = editReplyStatusField_reply_array;//把数组editReplyStatusField_reply_array作为editReplyStatusField数组的元素传入editReplyStatusField数组中
										_self.editReplyContentField[i] = editReplyContentField_reply_array;
										
										
										
										
										
										//处理图片放大标签
										let contentNode = document.createElement("div");
										contentNode.innerHTML = comment.content;
										_self.bindNode(contentNode);
										comment.content = escapeVueHtml(contentNode.innerHTML);
				    					
				    					_self.quoteFormView.set(comment.id,false);
				    					
				    					
				    					_self.addQuote_disabled.set(comment.id,false);
				    					
				    					
				    					_self.commentStatusField.push(comment.status);
				    					
				    					
			    					}
			    					_self.commentList = commentList;
			    				
				    				_self.totalrecord = parseInt(pageView.totalrecord);//服务器返回的long类型已转为String类型
					    			_self.currentpage = pageView.currentpage;
									_self.totalpage = parseInt(pageView.totalpage);//服务器返回的long类型已转为String类型
									_self.maxresult = pageView.maxresult;
									_self.isShowPage = true;//显示分页栏
									
									
									_self.$nextTick(function() {
										//跳转到锚点
										if(_self.commentId != null && _self.commentId != ""){
										
											for(let i = 0; i<_self.commentElementNodes.length; i++){
												let commentElement = _self.commentElementNodes[i];
												let commentId = commentElement.getAttribute("commentId");
												
												if(commentId == _self.commentId){//跳转到当前评论
													//锚点距离浏览器顶部的高度 - <el-main>标签距离浏览器顶部的高度
											        document.querySelector(".el-main").scrollTop = commentElement.offsetTop - document.querySelector(".el-main").offsetTop;
													break;
												}
											}
										
										
										}
										if(_self.$route.query.replyId != undefined && _self.$route.query.replyId != ''){
											for(let i = 0; i<_self.replyElementNodes.length; i++){
												let replyElement = _self.replyElementNodes[i];
												let replyId = replyElement.getAttribute("replyId");
												if(replyId == _self.$route.query.replyId){//跳转到当前回复
													//锚点距离浏览器顶部的高度 - <el-main>标签距离浏览器顶部的高度
											        document.querySelector(".el-main").scrollTop = getElementTop(replyElement) - document.querySelector(".el-main").offsetTop -5;
													break;
												}
											}
										
										
										
										}
										
										
										
									});
									
									
			    				}
			    				
			    				
			    			}
			    		}
			    		
			    		if (Object.keys(_self.addCommentEditorCreateParameObject).length === 0) {//等于空
			    			let uploadPath = "control/comment/manage?method=uploadImage&userName="+_self.userName+"&isStaff=true&topicId="+_self.topicId;
					   		//创建富文本编辑器
							_self.addCommentEditor = createEditor(_self.$refs.commentContent, JSON.parse(_self.availableTag), uploadPath, null);
			    			_self.addCommentEditorCreateParameObject = {
			    				ref:_self.$refs.commentContent,
			    				availableTag:JSON.parse(_self.availableTag),
			    				uploadPath:uploadPath,
			    				userGradeList:null
			    			}
			    		
			    		}
						
						
			    		
						
			    	}else if(returnValue.code === 500){//错误
			    		let errorMap = returnValue.data;
			    		for (let key in errorMap){
			    			_self.$message({
					            showClose: true,
					            message: errorMap[key],
					            type: 'error'
					        });
			    	    }
			    	}
			    }
			})
			.catch(function (error) {
				console.log(error);
			});
		},
		//清空播放器
		clearVideoPlayer : function() {
			let _self = this;
			
			for(let i=0; i< _self.playerObjectList.length; i++){
				let playerObject = _self.playerObjectList[i];
				
				playerObject.destroy();//销毁播放器
			}
			for(let i=0; i< _self.playerHlsList.length; i++){
	            let hls = _self.playerHlsList[i];
	            hls.destroy();//销毁视频流
	        }
			_self.playerObjectList.length = 0;//清空数组
			_self.playerHlsList.length = 0;//清空数组
			_self.playerIdList.length = 0;//清空数组
			_self.playerNodeList.length = 0;//清空数组
		},
		//渲染视频播放器
		renderVideoPlayer : function() {
			let _self = this;
			
			
			
			
			for(let i=0; i< _self.playerIdList.length; i++){
				let playerId = _self.playerIdList[i];
				let url = document.getElementById(playerId).getAttribute("url");
        		let cover = document.getElementById(playerId).getAttribute("cover");//封面
        		let thumbnail = document.getElementById(playerId).getAttribute("thumbnail");//缩略图
				
				let dp = null;
        		if(url == ""){//如果视频处理中
        			dp = new DPlayer({
            			container: document.getElementById(playerId),//播放器容器元素
            			screenshot: false,//开启截图，如果开启，视频和视频封面需要开启跨域
            			
            			video: {
            			    
            			}
            		});
					let dom = document.createElement('div');
					dom.innerHTML="<div class='dplayer-process'><div class='box'><div class='prompt'>视频处理中，请稍后再刷新</div></div></div>";
					document.getElementById(playerId).appendChild(dom);
				}else{
					if(cover != undefined && cover != "" && thumbnail != undefined && thumbnail != ""){//切片视频
	        			let hls = null;
	        			dp = new DPlayer({
	            			container: document.getElementById(playerId),//播放器容器元素
	            			screenshot: false,//开启截图，如果开启，视频和视频封面需要开启跨域
	            			
	            			video: {
	            			    url: url,
	            			    pic: cover,//视频封面
	            			    thumbnails: thumbnail,//视频预览图
	            			    type: 'customHls',
	                            customType: {
	                                customHls: function (video, player) {
	                                    hls = new Hls();
	                                    hls.loadSource(video.src);
	                                    hls.attachMedia(video);
	                                    hls.config.xhrSetup = (xhr, url) => {
	                                        if(url.startsWith(_self.$store.state.baseURL+"videoRedirect?")){//如果访问视频重定向页
	                                            
	                                            //如果使用重定向跳转时会自动将标头Authorization发送到seaweedfs，seaweedfs会报501错误 A header you provided implies functionality that is not implemented
	                                            //这里发送X-Requested-With标头到后端，让后端返回需要跳转的地址
	                                            let videoRedirectDate = {};
	                                            nativeQueryVideoRedirect(url,function(date){
	                                                videoRedirectDate = date;
	                                            });
	                                            if(videoRedirectDate != null && Object.keys(videoRedirectDate).length>0 && videoRedirectDate.redirect != ''){
	                                                //告诉hls重新发送ts请求
	                                                xhr.open("GET", videoRedirectDate.redirect, true);//用重定向后的地址请求
	                                            }
	                                            
	                                            
	                                        }else{
	                                            // 请求ts的url 添加参数 props.fileid
	                                            //url = url + "?t=" + props.fileid;
	                                            // 这一步必须 告诉hls重新发送ts请求
	                                            xhr.open("GET", url, true);
	                                        }
	                                    };
	                                },
	                            },
	            			}
	            		});
	    				_self.playerHlsList.push(hls);
	        		}else{
	        			dp = new DPlayer({
	            			container: document.getElementById(playerId),//播放器容器元素
	            			screenshot: false,//开启截图，如果开启，视频和视频封面需要开启跨域
	            			
	            			video: {
	            			    url: url
	            			}
	            		});
	        		}
					
				}
				_self.playerObjectList.push(dp);
			}
			
			
			//添加播放器节点数据
			if(_self.playerObjectList.length >0){
				
				for(let i=0; i< _self.playerIdList.length; i++){
			    	let playerId = _self.playerIdList[i];
			    	let node = document.getElementById(playerId);//节点对象
			    	_self.playerNodeList.push(node);
			    }
			}
			
		},
		
		//递归绑定节点参数
		bindNode : function(node) {
			//先找到子节点
	        let nodeList = node.childNodes;
	        for(let i = 0;i < nodeList.length;i++){
	            //childNode获取到到的节点包含了各种类型的节点
	            //但是我们只需要元素节点  通过nodeType去判断当前的这个节点是不是元素节点
	            let childNode = nodeList[i];
	            let random = Math.random().toString().slice(2);
	            //判断是否是元素节点。如果节点是元素(Element)节点，则 nodeType 属性将返回 1。如果节点是属性(Attr)节点，则 nodeType 属性将返回 2。
	            if(childNode.nodeType == 1){
	            	
        	
	            	
	            	//处理图片
	            	if(childNode.nodeName.toLowerCase() == "img" ){
	            		let src = childNode.getAttribute("src");
            			
            			childNode.removeAttribute("src");//将原节点src属性删除，防止多请求一次
            		
						let html = '';
						let style = '';
						if(childNode.getAttribute("width") != null){//如果是表情，表情图不放大
							style = 'style="width: '+childNode.getAttribute("width")+'; height: '+childNode.getAttribute("height")+'"';
							html = '<el-image src="'+src+'" '+style+' lazy ></el-image>';
						}else{
						
							html = '<el-image src="'+src+'" '+style+' :preview-src-list=["'+src+'"] lazy hide-on-click-modal ></el-image>';
						}
						//创建要替换的元素
					//	let html = '<el-image src="'+src+'" '+style+' lazy></el-image>';
					//	let html = '<el-image src="'+src+'" '+style+' :preview-src-list=["http://127.0.0.1:8080/cms/common/tttttt/templates.jpg"] lazy hide-on-click-modal ></el-image>';
						
					
					
					//	let html = '<el-image src="backstage/images/null.gif" lazy></el-image>';
						let placeholder = document.createElement('div');
						placeholder.innerHTML = html;
						let node = placeholder.childNodes[0];
					//	node.setAttribute("src",src);
            			childNode.parentNode.replaceChild(node,childNode);//替换节点	 
	            	}
	            	
	            	
	            	
	            	//处理视频标签
	            	if(childNode.nodeName.toLowerCase() == "player" ){
	            		
	            		let id = "player_"+random+"_"+i;
	            		childNode.setAttribute("id",id);//设置Id
	            		this.playerIdList.push(id);	
	            	}
	            	//处理代码标签
	            	if(childNode.nodeName.toLowerCase() == "pre" ){
	            		let pre_html = childNode.innerHTML;
	            		let class_val = childNode.className;
	            		let lan_class = "";
	            		
	        	        let class_arr = new Array();
	        	        class_arr = class_val.split(' ');
	        	        
	        	        for(let k=0; k<class_arr.length; k++){
	        	        	let className = class_arr[k].trim();
	        	        	
	        	        	if(className != null && className != ""){
	        	        		if (className.lastIndexOf('lang-', 0) === 0) {
	        	        			lan_class = className;
	        			            break;
	        			        }
	        	        	}
	        	        }
	        	       
	        	        childNode.className = "line-numbers "+getLanguageClassName(lan_class);
	            		
	        	        let nodeHtml = "";

            			//删除code节点
            			let preChildNodeList = childNode.childNodes;
            			for(let p = 0;p < preChildNodeList.length;p++){
            				let preChildNode = preChildNodeList[p];
            				if(preChildNode.nodeName.toLowerCase() == "code" ){
            					nodeHtml += preChildNode.innerHTML;
            					preChildNode.parentNode.removeChild(preChildNode);
                			}
            				
            			}
            			
            			let dom = document.createElement('code');
            			dom.className = "line-numbers "+getLanguageClassName(lan_class);
	    				dom.innerHTML=nodeHtml;
	    				
	    				childNode.appendChild(dom);
	    				//渲染代码
	    				//Prism.highlightElement(dom);
	            		
	            	}
	            	
	            	this.bindNode(childNode);
	            }
	        }
	    },
	    
	    //递归渲染绑定节点
	    renderBindNode: function(node){	
	         //先找到子节点
	        let nodeList = node.childNodes;
	        for(let i = 0;i < nodeList.length;i++){
	            //childNode获取到到的节点包含了各种类型的节点
	            //但是我们只需要元素节点  通过nodeType去判断当前的这个节点是不是元素节点
	            let childNode = nodeList[i];
	            let random = Math.random().toString().slice(2);
	            //判断是否是元素节点。如果节点是元素(Element)节点，则 nodeType 属性将返回 1。如果节点是属性(Attr)节点，则 nodeType 属性将返回 2。
	            if(childNode.nodeType == 1){
	                //处理代码标签
	                if(childNode.nodeName.toLowerCase() == "pre" ){
	                    Prism.highlightAllUnder(childNode);
	                }
	                this.renderBindNode(childNode);
	            }
	        }
	    },
	    
	    
	    //分页
		page: function(page) {
		
			//删除缓存
			this.$store.commit('setCacheNumber');
			this.$router.push({
				path: '/admin/control/topic/manage/view', 
				query:{ 
					visible : this.$route.query.visible,
					topicView_beforeUrl:(this.$route.query.topicView_beforeUrl != undefined ? this.$route.query.topicView_beforeUrl:''),
					topicId : this.topicId,
					commentId : this.commentId,
					page : page
				}
			});
	    },
	    //审核话题
		auditTopic: function(topicId) {
			let _self = this;
			
			_self.$confirm('此操作将该话题状态改为已发布, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
				let formData = new FormData();
				formData.append('topicId',  topicId); 
				
				_self.$ajax({
			        method: 'post',
			        url: 'control/topic/manage?method=auditTopic',
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
				    		_self.$message.success("审核成功");
				    		
				    		//删除缓存
				    		_self.$store.commit('setCacheNumber');
				    		
				    		
				    		_self.queryTopic();
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
				    			}
				    			_self.error[key] = errorMap[key];
				    			
				    	    }
				    		
				    	}
				    }
				})
				.catch(function (error) {
					console.log(error);
				});
			
			}).catch((error) => {
	        	console.log(error);
	        });
	        
	        
	    },
	    //审核评论
		auditComment: function(commentId) {
			let _self = this;
			
			_self.$confirm('此操作将该评论状态改为已发布, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
				let formData = new FormData();
				formData.append('commentId',  commentId); 
				
				_self.$ajax({
			        method: 'post',
			        url: 'control/comment/manage?method=auditComment',
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
				    		_self.$message.success("审核成功");
				    		
				    		//删除缓存
				    		_self.$store.commit('setCacheNumber');
				    		
				    		
				    		_self.queryTopic();
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
				    			}
				    			_self.error[key] = errorMap[key];
				    			
				    	    }
				    		
				    	}
				    }
				})
				.catch(function (error) {
					console.log(error);
				});
			
			}).catch((error) => {
	        	console.log(error);
	        });
	    },
	    //审核回复
		auditReply: function(replyId) {
			let _self = this;
			
			_self.$confirm('此操作将该回复状态改为已发布, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
				let formData = new FormData();
				formData.append('replyId',  replyId); 
				
				_self.$ajax({
			        method: 'post',
			        url: 'control/comment/manage?method=auditReply',
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
				    		_self.$message.success("审核成功");
				    		
				    		//删除缓存
				    		_self.$store.commit('setCacheNumber');
				    		
				    		
				    		_self.queryTopic();
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
				    			}
				    			_self.error[key] = errorMap[key];
				    			
				    	    }
				    		
				    	}
				    }
				})
				.catch(function (error) {
					console.log(error);
				});
			
			}).catch((error) => {
	        	console.log(error);
	        });
	    },
	    
	    
	    
	    //引用评论表单
		addQuoteUI : function(commentId) {
			let _self = this;
			
			if(_self.quoteFormView.get(commentId) == true){//如果已打开
				return;
			}
			
			
			_self.quoteFormView.set(commentId,true);
			
			_self.$nextTick(function() {
				for(let i = 0; i<_self.quoteElementNodes.length; i++){
					let quoteElement = _self.quoteElementNodes[i];
					let _commentId = quoteElement.getAttribute("commentId");
					
					if(_commentId == commentId){
						let uploadPath = "control/comment/manage?method=uploadImage&userName="+_self.userName+"&isStaff=true&topicId="+_self.topicId;
						//创建富文本编辑器
						let editor = createEditor(quoteElement, JSON.parse(_self.availableTag), uploadPath, null);
						
						_self.quoteEditorMap.set(commentId,editor);
		
		    			_self.quoteEditorCreateParameMap.set(commentId,{
		    				ref:quoteElement,
		    				availableTag:JSON.parse(_self.availableTag),
		    				uploadPath:uploadPath,
		    				userGradeList:null
		    			});
		    			break;
					}
				}
			});
		},
		//取消引用评论
		cancelQuote : function(commentId) {
			let _self = this;
			
			let quoteEditor = _self.quoteEditorMap.get(commentId);
			if(quoteEditor != null){
				quoteEditor.remove();
				_self.quoteEditorMap.delete(commentId);
				_self.quoteEditorCreateParameMap.delete(commentId);
			}
			_self.quoteFormView.set(commentId,false);
			
		},
		
	    //引用评论
		addQuote : function(commentId) {
			let _self = this;
			
			_self.addQuote_disabled.set(commentId,true);
			
	        //清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			
			formData.append('commentId',  commentId); 
			
			
			for(let i = 0; i<_self.quoteElementNodes.length; i++){
				let quoteElement = _self.quoteElementNodes[i];
				let _commentId = quoteElement.getAttribute("commentId");
				
				if(_commentId == commentId){
					formData.append('content', quoteElement.value);
	    			break;
				}
			}
			
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/comment/manage?method=addQuote',
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
			    		_self.$message.success("添加成功");
			    		
			    		//删除缓存
			    		_self.$store.commit('setCacheNumber');
			    		
			    		
			    		
			    		
			    		//清除评论列表
			    		_self.clearCommentList();
						_self.quoteFormView.set(commentId,false);
			    		
			    	
			    		
			    		_self.queryTopic();
			    	}else if(returnValue.code === 500){//错误
			    		let errorMap = returnValue.data;
			    		for (let key in errorMap) {
			    			
		    				_self.$message({
					            showClose: true,
					            message: errorMap[key],
					            type: 'error'
					        });
			    			
			    	    }
			    		
			    	}
			    }
			    _self.addQuote_disabled.set(commentId,false);
			})
			.catch(function (error) {
				console.log(error);
			});
		
		},
	    
		//添加评论
		addCommentForm : function() {
			let _self = this;
			
			_self.addCommentForm_disabled = true;
			
	        //清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			if(_self.topicId != null && _self.topicId != ''){
				formData.append('topicId',  _self.topicId); 
			}
			
			if(_self.$refs.commentContent.value != null && _self.$refs.commentContent.value !=''){
				formData.append('content', _self.$refs.commentContent.value);
			}
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/comment/manage?method=add',
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
			    		//清空字段
			    		 _self.addCommentEditor.html("");
			    		 _self.addCommentEditor.remove();
			    		 _self.addCommentEditorCreateParameObject = {};
			    		 
			    		_self.queryTopic();
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
			    
			    _self.addCommentForm_disabled = false;
			})
			.catch(function (error) {
				console.log(error);
			});
	    },
	  
	    //修改评论表单
		editCommentUI : function(comment) {
			let _self = this;
			if(_self.editCommentFormView.get(comment.id) == true){//如果已打开
				return;
			}
			
			
			
			this.$ajax.get('control/comment/manage', {
			    params: {
			    	method : 'edit',
			    	commentId: comment.id,
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
			    			if(key == "comment"){//评论
			    				let _comment = mapData[key];
			    				
			    				_self.editCommentFormView.set(comment.id,true);
								_self.$nextTick(function() {
									for(let i = 0; i<_self.editCommentElementNodes.length; i++){
										let editCommentElement = _self.editCommentElementNodes[i];
										
										editCommentElement.value = _comment.content;
										
										let _commentId = editCommentElement.getAttribute("commentId");
									
										if(_commentId == comment.id){
											
											let uploadPath = "control/comment/manage?method=uploadImage&userName="+comment.userName+"&isStaff="+comment.isStaff+"&topicId="+_self.topicId;
											//创建富文本编辑器
											let editor = createEditor(editCommentElement, JSON.parse(_self.availableTag), uploadPath, null);
											
											_self.editCommentEditorMap.set(comment.id,editor);
								
							    			_self.editCommentEditorCreateParameMap.set(comment.id,{
							    				ref:editCommentElement,
							    				availableTag:JSON.parse(_self.availableTag),
							    				uploadPath:uploadPath,
							    				userGradeList:null
							    			});
							    			break;
										}
									}
								});
			    				
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
		
		//取消修改评论
		cancelEditComment : function(commentId) {
			let _self = this;
			
			let editCommentEditor = _self.editCommentEditorMap.get(commentId);
			if(editCommentEditor != null){
				editCommentEditor.remove();
				_self.editCommentEditorMap.delete(commentId);
				_self.editCommentEditorCreateParameMap.delete(commentId);
			}
			_self.editCommentFormView.set(commentId,false);
			
		},
		
	    //修改评论
		editComment : function(commentId) {
			let _self = this;
			_self.editComment_disabled.set(commentId,true);
			
	        //清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			formData.append('commentId',  commentId); 

			if(_self.commentList != null && _self.commentList.length > 0){
				for (let i = 0; i <_self.commentList.length; i++) {
					let comment = _self.commentList[i];
					if(comment.id == commentId){
						formData.append('status',  _self.commentStatusField[i]); 
						break;
					}
				}
			}
			
			for(let i = 0; i<_self.editCommentElementNodes.length; i++){
				let editCommentElement = _self.editCommentElementNodes[i];
				let _commentId = editCommentElement.getAttribute("commentId");
				
				if(_commentId == commentId){
					formData.append('content', editCommentElement.value);
	    			break;
				}
			}
			
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/comment/manage?method=edit',
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
			    		
			    		
			    		
			    		//清除评论列表
			    		_self.clearCommentList();
			    		
						_self.editCommentFormView.set(commentId,false);
			    		
			    		
			    		
			    		_self.queryTopic();
			    	}else if(returnValue.code === 500){//错误
			    		let errorMap = returnValue.data;
			    		for (let key in errorMap) {
			    			
		    				_self.$message({
					            showClose: true,
					            message: errorMap[key],
					            type: 'error'
					        });
			    			
			    	    }
			    		
			    	}
			    }
			    _self.editComment_disabled.set(commentId,false);
			})
			.catch(function (error) {
				console.log(error);
			});
		},
		
		
		
		//修改话题表单
		editTopicUI : function() {
			let _self = this;
			if(_self.editTopicFormView){//如果已打开
				return;
			}
			
			_self.editTopicFormView = true;
			
			
			_self.$ajax.get('control/topic/manage', {
			    params: {
			    	method : 'edit',
			    	topicId :_self.topicId,
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
			    		
			    		let userGradeList = null;
			    		let topic = null;
			    		
						for(let key in mapData){
			    			if(key == "userGradeList"){//所有设置的等级
								userGradeList = mapData[key];
			    			}else if(key == "topic"){
			    				topic = mapData[key];
			    			}
			    		}
						
						if(userGradeList != null && topic != null){
							
							_self.title = _self.topic.title;
								
							_self.sort = _self.topic.sort;
							_self.allow = _self.topic.allow;
							_self.status = _self.topic.status;
							_self.essence = _self.topic.essence;
		    				_self.$refs.topicContent.value = topic.content;
		    				
		    				
		    				_self.tagId = _self.topic.tagId;
		
							_self.tagIdGroup = _self.topic.tagId;
							let obj =new Object();
							obj.value = _self.topic.tagId;
							obj.label = _self.topic.tagName;
							_self.options.push(obj);
		    				
		    				
		    				
	    					let uploadPath = "control/topic/manage?method=upload&userName="+_self.topic.userName+"&isStaff="+_self.topic.isStaff;
				   		
					   		let availableTag = ['source', '|', 'preview', 'template', 'code',
						        '|', 'justifyleft', 'justifycenter', 'justifyright',
						        'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent', 'subscript',
						        'superscript', 'clearhtml', 'quickformat', 'selectall', '|',
						        'formatblock', 'fontname', 'fontsize', 'fullscreen',  '/', 'forecolor', 'hilitecolor', 'bold',
						        'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', '|', 'image', 'multiimage',
						         'media','embedVideo','uploadVideo', 'insertfile','emoticons','baidumap', 'table', 'hr',   'pagebreak',
						         'link', 'unlink','hide','hidePassword','hideComment','hideGrade','hidePoint','hideAmount'];
					   		
					   		//创建富文本编辑器
							_self.editTopicEditor = createEditor(_self.$refs.topicContent, availableTag, uploadPath, userGradeList);
			    			_self.editTopicEditorCreateParameObject = {
			    				ref:_self.$refs.topicContent,
			    				availableTag:availableTag,
			    				uploadPath:uploadPath,
			    				userGradeList:userGradeList
			    			}
						
						}
					}else if(returnValue.code === 500){//错误
			    		let errorMap = returnValue.data;
			    		for (let key in errorMap){
			    			_self.$message({
					            showClose: true,
					            message: errorMap[key],
					            type: 'error'
					        });
			    	    }
			    	}
				}
			})
			.catch(function (error) {
				console.log(error);
			});
		},
		
		//取消修改话题
		cancelEditTopic : function() {
			let _self = this;
			
			
			_self.editTopicEditor.remove();
			_self.editTopicEditorCreateParameObject ={};
			
			_self.title ='';
			_self.tagId ='';//标签Id
			_self.sort= 0;
			_self.allow = true;
			_self.status =0;
			_self.essence = false;
			_self.isAllowLoadTagGroup=true;//是否允许加载标签组
			_self.tagIdGroup =[];//标签Id组
			_self.loading =false;//是否正在从远程获取数据
			_self.options= [];//Select 选择器标签数据

			_self.editTopicFormView = false;
			
		},
		
		//修改话题
		editTopic : function() {
			let _self = this;
			_self.editTopic_disabled = true;
			
		 	//清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			formData.append('topicId', _self.topicId);
			if(_self.title != null && _self.title != ''){
				formData.append('title', _self.title);
				
			}
			
			if(_self.tagId != null && _self.tagId != ''){
				formData.append('tagId', _self.tagId); 
			}
			if(_self.sort != null && _self.sort >=0){
				formData.append('sort', _self.$refs.sort_ref.displayValue);
			}
			formData.append('allow', _self.allow);
			formData.append('status', _self.status);
			formData.append('essence', _self.essence);
			
			if(_self.$refs.topicContent.value != null && _self.$refs.topicContent.value !=''){
				formData.append('content', _self.$refs.topicContent.value);
			}
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/topic/manage?method=edit',
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
			    		
			    		//取消修改话题
						_self.cancelEditTopic();
			    		
			    		_self.queryTopic();
			    	}else if(returnValue.code === 500){//错误
			    		let errorMap = returnValue.data;
			    		for (let key in errorMap) {   
			    			if(key == "content"){
			    				_self.$message({
									duration :0,
						            showClose: true,
						            message: errorMap[key],
						            type: 'error'
						        });
						        continue;
			    			}
			    		
			    			_self.error[key] = errorMap[key];
			    	    }
			    		
			    	}
			    }
			   _self.editTopic_disabled = false;
			})
			.catch(function (error) {
				console.log(error);
			});
		
		},
		//查询标签
	    queryTagList: function(event) {
	        let _self = this;
	        if(!_self.isAllowLoadTagGroup){
	        	return;
	        }
			
			_self.$ajax.get('control/tag/manage', {
			    params: {
			    	method : 'allTag'
			    }
			})
			.then(function (response) {
				_self.isAllowLoadTagGroup = false;
				if(response == null){
					return;
				}
			    let result = response.data;
			    if(result){
			    	let returnValue = JSON.parse(result);
			    	if(returnValue.code === 200){//成功
			    		_self.options = [];
			    		let tagList = returnValue.data;
			    		if(tagList != null && tagList.length >0){
			    			for(let i=0; i<tagList.length; i++){
			    				let tag = tagList[i];
				    			let obj =new Object();
				    	    	obj.value = tag.id;
				    	    	obj.label = tag.name;
				    	    	_self.options.push(obj);
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
	    
	    
	    //选中标签
	    selectedTag: function(val) { 
	   
	        this.tagId = val;
	    },
		
		
		//添加回复对方表单
		addReplyFriendUI : function(replyId) {
			let _self = this;
			if(_self.addReplyFriendFormView.get(replyId) == true){//如果已打开
	            return;
	        }
	        
	        _self.addReplyFriendFormView.set(replyId,true);
		
		},
		//取消添加回复对方
		cancelAddReplyFriend : function(replyId) {
	        let _self = this;
			_self.addReplyFriendFormView.set(replyId,false);
	        
	    },
		//添加回复对方
		addReplyFriend : function(commentId,replyId) {
			let _self = this;
			
			_self.addReplyFriend_disabled.set(replyId,true);
	
	        //清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
	
	        let formData = new FormData();
				
	        formData.append('commentId',  commentId); 
	
	        formData.append('friendReplyId',  replyId); 
	        
	        formData.append('content', _self.addReplyFriendContentField[replyId]); 
		
			_self.$ajax({
			    method: 'post',
			    url: 'control/comment/manage?method=addReply',
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
			    		
			    		_self.addReplyFriendFormView.set(replyId,false);
			    		
			    		
						//清除评论列表
			    		_self.clearCommentList();
			    		
			    		_self.queryTopic();
			    	}else if(returnValue.code === 500){//错误
			    		let errorMap = returnValue.data;
			    		for (let key in errorMap) {
			    			
		    				_self.$message({
					            showClose: true,
					            message: errorMap[key],
					            type: 'error'
					        });
			    			
			    	    }
			    		
			    	}
			    }
			    _self.addReplyFriend_disabled.set(commentId,false);
			})
			.catch(function (error) {
				console.log(error);
			});
		},
		
		//添加回复表单
		addReplyUI : function(commentId) {
			let _self = this;
			if(_self.addReplyFormView.get(commentId) == true){//如果已打开
				return;
			}
			
			_self.addReplyFormView.set(commentId,true);
			
			
			
		},
		//取消添加回复
		cancelAddReply : function(commentId) {
			let _self = this;
			
			_self.addReplyFormView.set(commentId,false);
			
		},
		//添加回复
		addReply : function(commentId) {
			let _self = this;
			
			_self.addReply_disabled.set(commentId,true);
			
			
			//清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			formData.append('commentId',  commentId); 
			
			
			

			if(_self.commentList != null && _self.commentList.length > 0){
				for (let i = 0; i <_self.commentList.length; i++) {
					let comment = _self.commentList[i];
					if(comment.id == commentId){
						formData.append('content',  _self.addReplyContentField[i]);
						break; 
					}
					
				}
			}
			
		
			_self.$ajax({
			    method: 'post',
			    url: 'control/comment/manage?method=addReply',
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
			    		
			    		
						_self.addReplyFormView.set(commentId,false);
			    		if(_self.commentList != null && _self.commentList.length > 0){
							for (let i = 0; i <_self.commentList.length; i++) {
								let comment = _self.commentList[i];
								if(comment.id == commentId){
									_self.addReplyContentField[i] = "";//清空
									break; 
								}
								
							}
						}
						//清除评论列表
			    		_self.clearCommentList();
			    		
			    		_self.queryTopic();
			    	}else if(returnValue.code === 500){//错误
			    		let errorMap = returnValue.data;
			    		for (let key in errorMap) {
			    			
		    				_self.$message({
					            showClose: true,
					            message: errorMap[key],
					            type: 'error'
					        });
			    			
			    	    }
			    		
			    	}
			    }
			    _self.addReply_disabled.set(commentId,false);
			})
			.catch(function (error) {
				console.log(error);
			});
		},
		
		//修改回复表单
		editReplyUI : function(reply) {
			let _self = this;
			if(_self.editReplyFormView.get(reply.id) == true){//如果已打开
				return;
			}
			
			_self.editReplyFormView.set(reply.id,true);
			
			
			
		},
		//取消修改回复
		cancelEditReply : function(replyId) {
			let _self = this;
			
			_self.editReplyFormView.set(replyId,false);
			
		},
		//修改回复
		editReply : function(replyId) {
			let _self = this;
			
			_self.editReply_disabled.set(replyId,true);
			
			
			//清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			formData.append('replyId',  replyId); 
			
			
			

			if(_self.commentList != null && _self.commentList.length > 0){
				for (let i = 0; i <_self.commentList.length; i++) {
					let comment = _self.commentList[i];
					
					if(comment.replyList != null && comment.replyList.length >0){
						for (let j = 0; j <comment.replyList.length; j++) {
							let reply = comment.replyList[j];
							if(reply.id == replyId){
								formData.append('status', _self.editReplyStatusField[i][j]); 
								formData.append('content', _self.editReplyContentField [i][j]); 
							}
						}
					}
					
				}
			}
			
		
			_self.$ajax({
			    method: 'post',
			    url: 'control/comment/manage?method=editReply',
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
			    		
			    		//清除评论列表
			    		_self.clearCommentList();
						_self.editReplyFormView.set(replyId,false);
			    		
			    		
			    		
			    		_self.queryTopic();
			    	}else if(returnValue.code === 500){//错误
			    		let errorMap = returnValue.data;
			    		for (let key in errorMap) {
			    			
		    				_self.$message({
					            showClose: true,
					            message: errorMap[key],
					            type: 'error'
					        });
			    			
			    	    }
			    		
			    	}
			    }
			    _self.editReply_disabled.set(replyId,false);
			})
			.catch(function (error) {
				console.log(error);
			});
		},
		
		//删除话题
		deleteTopic : function(topicId) {
			let _self = this;
			
			this.$confirm('此操作将删除该话题, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	
		    	formData.append('topicId', topicId);
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/topic/manage?method=delete',
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
				    		_self.$message.success("操作成功");
				    		
				    		
				    		_self.$router.push({
				    			path: _self.sourceUrlObject.path, 
				    			query:_self.sourceUrlObject.query
							});
				    	}else if(returnValue.code === 500){//错误
				    		
				    		let errorMap = returnValue.data;
				    		for (let key in errorMap) {
				    			
			    				_self.$message({
						            showClose: true,
						            message: errorMap[key],
						            type: 'error'
						        });
				    			
				    	    }
				    		
				    		
				    	}
				    }
				})
				.catch(function (error) {
					console.log(error);
				});
	        }).catch((error) => {
	        	console.log(error);
	        });
			
			
			
		},
		//删除评论
		deleteComment : function(commentId) {
			let _self = this;
			
			this.$confirm('此操作将删除该评论, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	
		    	formData.append('commentId', commentId);
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/comment/manage?method=delete',
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
				    		_self.$message.success("操作成功");
				    		_self.queryTopic();
				    	}else if(returnValue.code === 500){//错误
				    		
				    		let errorMap = returnValue.data;
				    		for (let key in errorMap) {
				    			
			    				_self.$message({
						            showClose: true,
						            message: errorMap[key],
						            type: 'error'
						        });
				    			
				    	    }
				    		
				    		
				    	}
				    }
				})
				.catch(function (error) {
					console.log(error);
				});
	        }).catch((error) => {
	        	console.log(error);
	        });
			
			
			
		},
		//删除回复
		deleteReply : function(replyId) {
			let _self = this;
			
			this.$confirm('此操作将删除该回复, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	
		    	formData.append('replyId', replyId);
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/comment/manage?method=deleteReply',
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
				    		_self.$message.success("操作成功");
				    		_self.queryTopic();
				    	}else if(returnValue.code === 500){//错误
				    		
				    		let errorMap = returnValue.data;
				    		for (let key in errorMap) {
				    			
			    				_self.$message({
						            showClose: true,
						            message: errorMap[key],
						            type: 'error'
						        });
				    			
				    	    }
				    		
				    		
				    	}
				    }
				})
				.catch(function (error) {
					console.log(error);
				});
	        }).catch((error) => {
	        	console.log(error);
	        });
			
		},
		
		
		//恢复评论
		recoveryComment : function(commentId) {
			let _self = this;
			
			this.$confirm('此操作将恢复该评论, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	
		    	formData.append('commentId', commentId);
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/comment/manage?method=recoveryComment',
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
				    		_self.$message.success("操作成功");
				    		_self.queryTopic();
				    	}else if(returnValue.code === 500){//错误
				    		
				    		let errorMap = returnValue.data;
				    		for (let key in errorMap) {
				    			
			    				_self.$message({
						            showClose: true,
						            message: errorMap[key],
						            type: 'error'
						        });
				    			
				    	    }
				    		
				    		
				    	}
				    }
				})
				.catch(function (error) {
					console.log(error);
				});
	        }).catch((error) => {
	        	console.log(error);
	        });
			
		},
		//恢复回复
		recoveryReply : function(replyId) {
			let _self = this;
			this.$confirm('此操作将恢复该回复, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	
		    	formData.append('replyId', replyId);
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/comment/manage?method=recoveryReply',
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
				    		_self.$message.success("操作成功");
				    		_self.queryTopic();
				    	}else if(returnValue.code === 500){//错误
				    		
				    		let errorMap = returnValue.data;
				    		for (let key in errorMap) {
				    			
			    				_self.$message({
						            showClose: true,
						            message: errorMap[key],
						            type: 'error'
						        });
				    			
				    	    }
				    		
				    		
				    	}
				    }
				})
				.catch(function (error) {
					console.log(error);
				});
	        }).catch((error) => {
	        	console.log(error);
	        });
		
		},
		
		
		//清除评论列表
		clearCommentList : function() {
			let _self = this;
	        _self.quoteData.clear();
	        _self.commentList.length =0;
	        _self.editReplyContentField = {};
			_self.addReplyFriendContentField = {};
			_self.editCommentEditorMap.forEach(function(value,key){
				if(value != null){
	                value.html("");//清空字段
					value.remove();
	            }
		　　});
	
			
	        _self.editCommentEditorMap.clear();
	        _self.editCommentEditorCreateParameMap.clear();
	
			_self.quoteEditorMap.forEach(function(value,key){
				if(value != null){
	                value.html("");//清空字段
					value.remove();
	            }
		　　});
	        _self.quoteEditorMap.clear();
	        _self.quoteEditorCreateParameMap.clear();
	        
	        _self.editCommentFormView.clear();
	        _self.quoteFormView.clear();
	        _self.addReplyFormView.clear();
	        _self.editReplyFormView.clear();
	        
	        _self.addReplyFriendFormView.clear();
	    },
	    //点击回复层级
	    clickReplyLevel : function(commentId,replyId) {
			let _self = this;
	       	//是否点击已选中的项
			let isSelectedItem = false;
		
		
			if(_self.dot.size >0){
				let lastFriendReplyId = [..._self.dot][_self.dot.size-1];//最后一个元素
				if(lastFriendReplyId[0] == replyId){
					isSelectedItem = true;
				}
			}
		
			_self.dot.clear();
			_self.line.clear();
			if(!isSelectedItem){
				_self.showReplyLevel(commentId,replyId);
			}
	       
		},
	
		//展示回复层级
		showReplyLevel : function(commentId,replyId) {
			let _self = this;
	       	let dotArray = new Array();
			let replyList = [];
			if(_self.commentList != null && _self.commentList.length > 0){
	            A:for (let i = 0; i <_self.commentList.length; i++) {
	                let comment = _self.commentList[i];
					
	                 if(comment.id == commentId){
	                    //回复
	                    if(comment.replyList != null && comment.replyList.length >0){
	                        replyList = comment.replyList;
	                        for (let j = 0; j <comment.replyList.length; j++) {
	                            let reply = comment.replyList[j];
	                            if(reply.id == replyId && reply.friendUserName != null && reply.friendUserName != ''){
	                                let friendReplyIdArray = reply.friendReplyIdGroup.split(",");
	                                for (let k = 0; k <friendReplyIdArray.length; k++) {
	                                    let friendReplyId = friendReplyIdArray[k];
	                                    if(friendReplyId != '' && friendReplyId != '0'){
	                                        dotArray.push(friendReplyId);
	                                    }
	                                }
	                                break A;
	                            }
	                        }
	                    }
	                 }                       
	                
	            }
	        }
	
			//第一个有效层级
			let firstLevel = '';
	
			A:for (let i = 0; i <dotArray.length; i++) {
				let friendReplyId = dotArray[i];
				for (let j = 0; j <replyList.length; j++) {
					let reply = replyList[j];
					if(reply.id == friendReplyId){
						firstLevel = friendReplyId;
						break A;
					}
				}
			}
	
			//过滤无效的点
			A:for (let i = dotArray.length - 1; i >= 0; i--) {
				let friendReplyId = dotArray[i];
				for (let j = 0; j <replyList.length; j++) {
					let reply = replyList[j];
					if(reply.id == friendReplyId){
						continue A;
					}
				}
				dotArray.splice(i, 1);
			}
	
			if(dotArray.length >0){
				for (let i = 0; i <dotArray.length; i++) {
					let friendReplyId = dotArray[i];
					_self.dot.set(friendReplyId,true);//楼中楼的点
				}
				for (let i = 0; i <replyList.length; i++) {
					let reply = replyList[i];
					if(reply.id == firstLevel){
						_self.line.set(reply.id,true);//楼中楼的线
						continue;
					}
					if(reply.id == replyId){
						break;
					}
					if(_self.line.size >0){
						_self.line.set(reply.id,true);//楼中楼的线
					}
				}
	
				_self.dot.set(replyId,true);//楼中楼点击的层
			}
		}
	}
});

</script>