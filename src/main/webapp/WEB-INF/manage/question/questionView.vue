<!-- 问题查看 -->
<template id="questionView-template">
	<div>
		<div class="main blankBackground" >
			<div class="navbar">
				
				<el-button type="primary" plain size="small"  @click="$router.push({path: sourceUrlObject.path, query:sourceUrlObject.query})">返回</el-button>
			
			</div>
			<div class="questionViewModule">
	        	<div class="question-wrap">
					<div class="questionTag">
						<span class="tag" v-for="questionTag in question.questionTagAssociationList">{{questionTag.questionTagName}}</span>
					</div>
					<div class="operat">
						<el-link class="item" href="javascript:void(0);" v-if="question.status == 10" @click="auditQuestion(question.id);">审核</el-link>
						<el-link class="item" href="javascript:void(0);" @click="$router.push({path: '/admin/control/questionFavorite/list', query:{ visible:($route.query.visible != undefined ? $route.query.visible:''),questionView_beforeUrl:($route.query.questionView_beforeUrl != undefined ? $route.query.questionView_beforeUrl:''),questionId :question.id, answerId:($route.query.answerId != undefined ? $route.query.answerId:''), questionPage:($route.query.page != undefined ? $route.query.page:'')}})">收藏用户</el-link>
						<el-link class="item" href="javascript:void(0);" @click="appendQuestionUI();">追加提问</el-link>
						<el-link class="item" href="javascript:void(0);" @click="editQuestionUI();">修改</el-link>
						<el-link class="item" href="javascript:void(0);" @click="$router.push({path: '/admin/control/questionReport/list', query:{ visible:($route.query.visible != undefined ? $route.query.visible:''),questionView_beforeUrl:($route.query.questionView_beforeUrl != undefined ? $route.query.questionView_beforeUrl:''),questionId :question.id, answerId:($route.query.answerId != undefined ? $route.query.answerId:''),questionPage:($route.query.page != undefined ? $route.query.page:''),parameterId:question.id,module:40}})">举报</el-link>
                    	<el-link class="item" href="javascript:void(0);" @click="deleteQuestion(question.id)">删除</el-link>
					</div>
					<!-- 追加提问 -->
					<div class="appendQuestion-post" v-show="appendQuestionFormView">
						<div class="appendQuestion">
							<el-form label-width="auto"  @submit.native.prevent>
								<el-form-item label="追加提问" :required="true">
									<textarea ref="appendQuestionContent" style="width:100%;height:300px;visibility:hidden;"></textarea>
								</el-form-item>
								<el-form-item>
								    <el-button class="submitButton" type="primary" @mousedown.native="appendQuestion" :disabled="appendQuestion_disabled">提交</el-button>
								    <el-button class="submitButton" type="primary" plain @mousedown.native="cancelAppendQuestion();">取消</el-button>
								</el-form-item>
							</el-form>
						</div>
					</div>
					
					
					<!-- 修改 -->
					<div class="editQuestion-post" v-show="editQuestionFormView">
						<div class="editQuestion">
							<el-form label-width="auto"  @submit.native.prevent>
								<el-form-item label="标题" :required="true" :error="error.title">
									<el-input v-model.trim="title" maxlength="100" clearable="true" show-word-limit></el-input>
								</el-form-item>
								<el-form-item label="标签" :required="true" :error="error.tagId">
									<el-select ref="question_tag_ref"  v-model="tagIdGroup" @remove-tag="processRemoveTag" @focus="loadQuestionTag"  style="width: 100%;" multiple :placeholder="select_placeholder" >
										<el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"></el-option>
									</el-select>
								</el-form-item>
								<el-form-item label="悬赏积分" :error="error.point">
									<el-row><el-col :span="8"><el-input v-model.trim="point" maxlength="8" clearable="true" ></el-input></el-col></el-row>
									<div class="form-help" >最多允许使用积分{{maxPoint}}</div>
								</el-form-item>
								<el-form-item label="悬赏金额" :error="error.amount">
									<el-row><el-col :span="8"><el-input v-model.trim="amount" maxlength="8" clearable="true" ></el-input></el-col></el-row>
									<div class="form-help" >最多允许使用预存款{{maxDeposit}}</div>
								</el-form-item>
								<el-form-item label="排序" :required="true" :error="error.sort">
									<el-input-number v-model="sort" ref="sort_ref" controls-position="right" @change="handleChange" :min="0" :max="999999999"></el-input-number>
									<div class="form-help" >数字越大越在前</div>
								</el-form-item>
								<el-form-item label="允许回答" :required="true" :error="error.allow">
									<el-switch v-model="allow" ></el-switch>
								</el-form-item>
								<el-form-item label="状态" :required="true" :error="error.status">
									<el-radio-group v-model="status">
									    <el-radio :label="10">待审核</el-radio>
									    <el-radio :label="20">已发布</el-radio>
									</el-radio-group>
								</el-form-item>
								<el-form-item label="内容" :required="true">
									<textarea ref="questionContent" style="width:100%;height:300px;visibility:hidden;"></textarea>
								</el-form-item>
								<el-form-item>
								    <el-button class="submitButton" type="primary" @mousedown.native="editQuestion" :disabled="editQuestion_disabled">提交</el-button>
								    <el-button class="submitButton" type="primary" plain @mousedown.native="cancelEditQuestion();">取消</el-button>
								</el-form-item>
							</el-form>
						</div>
					</div>
					
					<div class="selectQuestionTagModule">
						<el-dialog title="选择标签" v-model="popup_allTag" @close="closeQuestionTag">
							<div class="questionTagNavigation">
			            		<ul class="nav">
			            			<li class="nav-item" v-for="questionTag in allTagList" >
			            				<span :class="selectedTagClass(questionTag.id)"  @click="selectChildTag(questionTag.id)" >{{questionTag.name}}</span>
									</li>
			            		</ul>
			            		<!-- 二级标签 -->
								<div class="tab-content">
									<div class="tab-pane">
										<span :class="selectedChildTagClass(childQuestionTag.id)" @click="selectedTag(childQuestionTag)"  v-for="childQuestionTag in childTagList" >{{childQuestionTag.name}}</span>
									</div>
								</div>
							</div>
						</el-dialog>
					</div>
					
					
					
					<div class="head">
		                <div class="title">{{question.title}}</div>
		                <div class="questionInfo clearfix" >
		                	<div class="avatar">
		                		<el-popover effect="light" trigger="hover" placement="top">
						        	<template #default>
						        		<p v-if="question.isStaff == false">呢称: {{question.nickname}}</p>
							            <p>账号: {{question.account}}</p>
							            <p v-if="question.userRoleNameList != null && question.userRoleNameList.length >0" >角色: 
							            	<span class="questionViewModule_question-wrap_head_questionInfo_userRoleName" v-for="roleName in question.userRoleNameList" >{{roleName}}</span>
							            </p> 
							            
							            
						        	</template>
						        	<template #reference v-if="question.isStaff == false">
						          		<div class="avatar-wrapper" >
											<div class="avatar-badge" v-if="question.avatarName == null || question.avatarName == ''">
												<el-avatar shape="square" :size="64" icon="el-icon-user-solid"></el-avatar>
											</div>
											<div class="avatar-badge" v-if="question.avatarName != null && question.avatarName != ''">
												<el-avatar shape="square" :size="64" :src="question.avatarPath+'100x100/'+question.avatarName"></el-avatar>
											</div>
										</div>
						        	</template>
						        	
						        	<template #reference v-if="question.isStaff == true">
						        		<div class="avatar-wrapper">
											<el-badge value="员工" type="warning" class="avatar-badge">
												<el-avatar shape="square" :size="64" icon="el-icon-user-solid"></el-avatar>
											</el-badge>
										</div>
						        	</template>
						        </el-popover>
		                	</div>
		                	<div class="userName" title="账号">
		                		{{question.account}}
		                		<div class="nickname" title="呢称">
		                			{{question.nickname}}
		                			<span class="questionViewModule_question-wrap_head_questionInfo_userRoleName" v-if="question.userRoleNameList != null && question.userRoleNameList.length >0" v-for="roleName in question.userRoleNameList" title="角色">{{roleName}}</span> 
		                		</div>
		                	</div>
		                	<div class="postTime" title="发表时间">{{question.postTime}}</div>
		                	<div class="viewTotal-icon" title="查看总数"><i class="el-icon-view"></i></div>
		                	<div class="viewTotal" title="查看总数">{{question.viewTotal}}次阅读</div>
		                	<div class="answer-icon" title="答案总数"><i class="el-icon-chat-dot-round"></i></div>
		                	<div class="answer" title="答案总数">{{question.answerTotal}}个答案</div>
		                	
		                	<div class="ipAddress" title="IP地址">
		                		{{question.ip}}&nbsp;{{question.ipAddress}}
		                		
		                		<div class="statusTagInfo">
									<span class="orange-tag" v-if="question.status ==10" title="问题状态">待审核</span>
									<span class="green-tag" v-if="question.status ==20" title="问题状态">已发布</span>
									<span class="red-tag" v-if="question.status ==110" title="问题状态">待审核员工删除</span>
									<span class="red-tag" v-if="question.status ==120" title="问题状态">已发布员工删除</span>	
		                		</div>
		                	</div>
		                </div>
					</div>
					<div class="main"  :class="$route.query.reportModule !=undefined && parseInt($route.query.reportModule) == 40 ? 'reportMark' : ''">
						<div class="reward" v-if="question.amount > 0 || question.point > 0">
		                	<div class="rewardInfo" >
		                		<i class="icon cms-deposit" ></i>
		                		悬赏<span v-if="question.amount > 0">金额<span class="symbol">¥</span><span class="amount">{{question.amount}}</span>元 </span>
		                		
		                		<span v-if="question.point > 0"><span class="point" >{{question.point}}</span>积分</span>
		                		
		                	</div>
		                </div>
						<div v-if="question.lastUpdateTime != null" class="lastUpdateTime" >最后修改时间：{{question.lastUpdateTime}}</div>
						<div :ref="'question_'+question.id">
							<component v-bind:is ="questionComponent(question.content)" v-bind="$props" />
						</div>
					</div>
					<div :class="(index%2)==0 ? 'appendBox odd' : 'appendBox even'" v-for="(appendQuestionItem,index) in question.appendQuestionItemList">
						<div class="appendHead">
							<span class="prompt">第{{index + 1}}条附言</span>
							<span class="appendTime">{{appendQuestionItem.postTime}}</span>
							
							<span class="operating">
								<el-link class="operat-btn" href="javascript:void(0);" @click="editAppendQuestionUI(appendQuestionItem)">修改</el-link>
							</span>
							<span class="operating">
				            	<el-link class="operat-btn" href="javascript:void(0);" @click="deleteAppendQuestion(appendQuestionItem.id)">删除</el-link>
							</span>
							
							
						</div>
	                	<div class="appendContent" :ref="'appendQuestion_'+appendQuestionItem.id">
	                		<component v-bind:is ="questionComponent(appendQuestionItem.content)" v-bind="$props" />
	                	</div> 
	                	
	                	<div class="editAppendQuestion-post" v-show="editAppendQuestionFormView.get(appendQuestionItem.id)">
							<div class="editAppendQuestion-formModule">
								<div class="editAppendQuestion-wrap">
									<div class="head-tag"><i class="cms-pencil-alt icon"></i></div>
									
									<textarea :ref="el => editAppendQuestionElementNodes[index]=el" :appendQuestionItemId="appendQuestionItem.id" class="editAppendQuestion-textarea" ></textarea>
										
									<div class="form-action">
										<div class="editAppendQuestionSubmit">
											<el-button class="submitButton" type="primary" @mousedown.native="editAppendQuestion(appendQuestionItem.id);" :disabled="editAppendQuestion_disabled.get(appendQuestionItem.id)">提交</el-button>
											<el-button class="submitButton" type="primary" plain @mousedown.native="cancelEditAppendQuestion(appendQuestionItem.id);">取消</el-button>
										</div>
									</div>
								</div>
							</div>
						</div>
	                	
					</div>
					
					
						
				</div>
			</div>
			
			<div class="answerModule">
				<div class="answerList">
					<div :class="[answer.adoption ? 'item activeItem' : 'item',$route.query.reportModule !=undefined && parseInt($route.query.reportModule) == 50 && answer.id == $route.query.answerId ? 'reportMark' : '']" v-for="(answer,index) in answerList" :key="answer.id" :answerId="answer.id"  :ref="handleNodes">
						<div class="head">
							<div class="avatarBox">
		                		<el-popover effect="light" trigger="hover" placement="top">
						        	<template #default>
						        		<p v-if="answer.isStaff == false">呢称: {{answer.nickname}}</p>
							            <p>账号: {{answer.account}}</p>
							            <p v-if="answer.userRoleNameList != null && answer.userRoleNameList.length >0" >角色: 
							            	<span class="questionViewModule_question-wrap_head_questionInfo_userRoleName" v-for="roleName in answer.userRoleNameList" >{{roleName}}</span>
							            </p> 
							            
							            
						        	</template>
						        	<template #reference v-if="answer.isStaff == false">
						          		<div class="avatar-wrapper" >
											<div class="avatar-badge" v-if="answer.avatarName == null || answer.avatarName == ''">
												<el-avatar shape="square" :size="64" icon="el-icon-user-solid"></el-avatar>
											</div>
											<div class="avatar-badge" v-if="answer.avatarName != null && answer.avatarName != ''">
												<el-avatar shape="square" :size="64" :src="answer.avatarPath+'100x100/'+answer.avatarName"></el-avatar>
											</div>
										</div>
						        	</template>
						        	
						        	<template #reference v-if="answer.isStaff == true">
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
						        		{{answer.account}}
						        		<div class="nickname" >
						        			{{answer.nickname}}
						        			<i class="userRoleName" v-for="roleName in answer.userRoleNameList" >{{roleName}}</i>
										    <i class="master" v-if="answer.userName == question.userName && answer.isStaff == question.isStaff">作者</i>
						        		</div>
						        	</div>
						        	
						        </h2>
	                        	
	                          	<div class="time clearfix">{{answer.postTime}}</div>
	                        </span>
	                        <span class="floor">{{index+1}}楼</span>                     
	                    </div>
	                    <div class="adoption" v-if="answer.adoption == true" >
							<div class="ribbon-wrapper">
								<div class="ribbon">最佳答案</div>
							</div>
						</div>
	                    <div class="main">
							<p class="answerContent" :ref="'answer_'+answer.id">
								<component v-bind:is ="answerDataComponent(answer.content)" v-bind="$props" />
							</p> 
							
							
							<div class="clearfix"></div>
							<div class="replyList" v-if="answer.answerReplyList.length >0">
								<ul class="timeline box">
									<li class="timeline-item replyItem-container" v-for="(reply,index2) in answer.answerReplyList" :key="reply.id"  :replyId="reply.id" :ref="handleReplyNodes">
										<div class="tail" v-if="line.get(reply.id)"></div>
                                    	<div class="node node--normal" v-if="dot.get(reply.id)"></div>
                                    	<div class="replyItem">
                                    		<div :class="$route.query.reportModule !=undefined && parseInt($route.query.reportModule) == 60 && reply.id == $route.query.replyId ? 'reply-reportMark' : ''">
                                       			<div class="reply-top" >
											    	<div class="avatarBox">
								                		<el-popover effect="light" trigger="hover" placement="top">
												        	<template #default>
												        		<p v-if="reply.isStaff == false">呢称: {{reply.nickname}}</p>
													            <p>账号: {{reply.account}}</p>
													            <p v-if="reply.userRoleNameList != null && reply.userRoleNameList.length >0" >角色: 
													            	<span class="questionViewModule_question-wrap_head_questionInfo_userRoleName" v-for="roleName in reply.userRoleNameList" >{{roleName}}</span>
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
																    <i class="master" v-if="reply.userName == question.userName && reply.isStaff == question.isStaff">作者</i>
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
	                                                                <i class="master" v-if="reply.friendUserName == question.userName && reply.isFriendStaff == question.isStaff">作者</i>
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
							                        	<el-link class="operat-btn" href="javascript:void(0);" @click="$router.push({path: '/admin/control/questionReport/list', query:{ visible:($route.query.visible != undefined ? $route.query.visible:''),questionView_beforeUrl:($route.query.questionView_beforeUrl != undefined ? $route.query.questionView_beforeUrl:''),questionId :question.id, answerId:($route.query.answerId != undefined ? $route.query.answerId:''),questionPage:($route.query.page != undefined ? $route.query.page:''),parameterId:reply.id,module:60}})">举报</el-link>
		                        
							                        	<el-link class="operat-btn" href="javascript:void(0);" @click="deleteReply(reply.id)">删除</el-link>
							                        
							                        </div>
							                        
												</div>
												<div style="clear:both; height: 0; line-height: 0; font-size: 0;"></div>
												<div class="editAnswerReply-formModule" v-show="editReplyFormView.get(reply.id)">
													<div class="editReply-wrap">
														<el-form label-width="auto"  @submit.native.prevent>
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
												
												<div class="replyContent" @click="clickReplyLevel(answer.id,reply.id)">{{reply.content}}</div>
												<!-- 回复对方 -->
		                                        <div class="addReplyFriend-post" v-show="addReplyFriendFormView.get(reply.id)">
		                                            <el-form @submit.native.prevent>
		                                                <el-form-item >
		                                                    <el-input type="textarea" :autosize="{minRows: 5}" placeholder="请输入内容" v-model="addReplyFriendContentField[reply.id]"></el-input>
		                                                </el-form-item>
		                                                <el-form-item>
		                                                    <el-button class="submitButton" size="large" type="primary" @mousedown.native="addReplyFriend(answer.id,reply.id)" :disabled="addReplyFriend_disabled.get(reply.id)">提交</el-button>
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
						
						<div class="addReply-post" v-show="addReplyFormView.get(answer.id)">
							<el-form label-width="auto"  @submit.native.prevent>
								<el-form-item >
									<el-input type="textarea" :autosize="{minRows: 5}" placeholder="请输入内容" v-model="addReplyContentField[index]"></el-input>
								</el-form-item>
								<el-form-item>
								    <el-button class="submitButton" type="primary" @mousedown.native="addReply(answer.id)" :disabled="addReply_disabled.get(answer.id)">提交</el-button>
								    <el-button class="submitButton" type="primary" plain @mousedown.native="cancelAddReply(answer.id);">取消</el-button>
								</el-form-item>
							</el-form>
						</div>
						<div class="editAnswer-post" v-show="editAnswerFormView.get(answer.id)">
							<div class="editAnswer-formModule">
								<div class="editAnswer-wrap">
									<div class="head-tag"><i class="cms-pencil-alt icon"></i></div>
									
									<el-form label-position="top" label-width="auto"  @submit.native.prevent>
										<el-form-item label="状态" >
											<el-radio-group v-model="answerStatusField[index]">
											    <el-radio :label="10">待审核</el-radio>
											    <el-radio :label="20">已发布</el-radio>
											</el-radio-group>
										</el-form-item>
									</el-form>
									
									
									<textarea :ref="handleEditAnswerNodes" :answerId="answer.id" class="editAnswer-textarea" ></textarea>
										
									<div class="form-action">
										<div class="editAnswerSubmit">
											<el-button class="submitButton" type="primary" @mousedown.native="editAnswer(answer.id);" :disabled="editAnswer_disabled.get(answer.id)">提交</el-button>
											<el-button class="submitButton" type="primary" plain @mousedown.native="cancelEditAnswer(answer.id);">取消</el-button>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="bottomInfo">
							<div class="toolbar">
							
								<span class="orange-tag" v-if="answer.status ==10" title="答案状态">待审核</span>
								<span class="green-tag" v-if="answer.status ==20" title="答案状态">已发布</span>
								<span class="red-tag" v-if="answer.status ==110" title="答案状态">待审核用户删除</span>
								<span class="red-tag" v-if="answer.status ==120" title="答案状态">已发布用户删除</span>
								<span class="red-tag" v-if="answer.status ==100010" title="答案状态">待审核员工删除</span>
								<span class="red-tag" v-if="answer.status ==100020" title="答案状态">已发布员工删除</span>
	
								<el-link class="operat-btn" href="javascript:void(0);"v-if="answer.status ==10" @click="auditAnswer(answer.id)">审核</el-link>
								<el-link class="operat-btn" href="javascript:void(0);"v-if="answer.adoption == true" @click="cancelAdoptionAnswer(answer.id)">取消采纳</el-link>
								<el-link class="operat-btn" href="javascript:void(0);"v-if="answer.adoption == false" @click="adoptionAnswer(answer.id)">采纳</el-link>
								
								
					            <el-link class="operat-btn" href="javascript:void(0);" @click="addReplyUI(answer.id)">回复</el-link>
					            <el-link class="operat-btn" href="javascript:void(0);" @click="editAnswerUI(answer)">修改</el-link>
					            <el-link class="operat-btn" href="javascript:void(0);" @click="$router.push({path: '/admin/control/questionReport/list', query:{ visible:($route.query.visible != undefined ? $route.query.visible:''),questionView_beforeUrl:($route.query.questionView_beforeUrl != undefined ? $route.query.questionView_beforeUrl:''),questionId :question.id, answerId:($route.query.answerId != undefined ? $route.query.answerId:''),questionPage:($route.query.page != undefined ? $route.query.page:''),parameterId:answer.id,module:50}})">举报</el-link>
                    
					            <el-link class="operat-btn" href="javascript:void(0);" @click="deleteAnswer(answer.id)">删除</el-link>
					            
							
							</div>
						</div>
					</div>
					
					
				    <div class="pagination-wrapper" v-if="isShowPage">
						<el-pagination background  @current-change="page" :current-page="currentpage"  :page-size="maxresult" layout="total, prev, pager, next,jumper" :total="totalrecord"></el-pagination>
					</div>
				    
				    
				    <div class="addAnswer" >
						<el-form label-width="auto"  @submit.native.prevent>
							<el-form-item :error="error.content">
								<textarea ref="answerContent" style="width:100%;height:300px;visibility:hidden;"></textarea>
							</el-form-item>
							<el-form-item>
							    <el-button class="submitButton" type="primary" @mousedown.native="addAnswerForm" :disabled="addAnswerForm_disabled">提交</el-button>
							    
							</el-form-item>
						</el-form>
					</div>
				</div>
				
			</div>
			
			
			
		</div>
	</div>
</template>

<script>
//问题查看
export default({
	name: 'questionView',//组件名称，keep-alive缓存需要本参数
	template : '#questionView-template',
	inject:['reload'], 
	data : function () {
		return {
			questionId :'',//问题Id
			answerId :'',//答案Id
			question :'',//问题
			giveRedEnvelope:'',//红包
			availableTag:'',//富文本编辑器允许使用的标签
			userName:'',//用户名称
			answerList:'',
			totalrecord : 0, //总记录数
		    currentpage : 1, //当前页码
			totalpage : 1, //总页数
			maxresult: 12, //每页显示记录数
			isShowPage:false,//是否显示分页 maxresult没返回结果前就显示会导致分页栏显示页码错误
			
			
			addAnswerEditor :'',//添加答案编辑器
			addAnswerEditorCreateParameObject :{},//添加答案编辑器的创建参数
			
			
			editAnswerFormView : new Map(),//修改答案表单  key:答案Id value:是否显示
			editAnswerEditorMap:new Map(),//修改答案富文本编辑器集合 key:答案Id value:富文本编辑器
			editAnswerEditorCreateParameMap:new Map(),//修改答案编辑器的创建参数 key:答案Id value:编辑器参数对象
			
			editAnswerData : new Map(),//修改答案数据 map格式 key:答案Id value:修改html数据
			answerStatusField : [], //答案状态项绑定
			
			title :'',
			sort : 0,
			allow : true,
			status :0,
			point :'',//积分
			amount :'',//金额
			maxDeposit : 0,//允许使用的预存款
			maxPoint : 0,//允许使用的积分
			
			tagIdGroup : [], //标签Id组
			options: [],//Select 选择器标签数据
			editQuestionFormView:false,//修改问题表单是否显示
			editQuestionEditor :'',//修改问题编辑器
			editQuestionEditorCreateParameObject :{},//修改问题编辑器的创建参数
			
			
			popup_allTag :false,//是否弹出问题标签表单
			allTagList: '',//全部标签
			selectedFirstTagId: '',//选中的一级标签Id
			childTagList: '',//选中一级标签的子标签集合
			select_placeholder: '请选择',

			
			appendQuestionFormView:false,//追加提问表单是否显示
			appendQuestionEditor :'',//追加提问编辑器
			appendQuestionEditorCreateParameObject :{},//追加提问编辑器的创建参数
			
			
			editAppendQuestionFormView : new Map(),//修改追加问题表单  key:追加问题Id value:是否显示
			editAppendQuestionEditorMap:new Map(),//修改追加问题富文本编辑器集合 key:追加问题Id value:富文本编辑器
			editAppendQuestionEditorCreateParameMap:new Map(),//修改追加问题编辑器的创建参数 key:追加问题Id value:编辑器参数对象
			editAppendQuestionData : new Map(),//修改追加问题数据 map格式 key:追加问题Id value:修改html数据
			editAppendQuestionElementNodes:[],//处理修改追加问题Element节点集合
			
	
			
			error : {
				title :'',
				tagId :'',
				sort :'',
				content :'',
				point :'',//积分
				amount :'',//金额
			},
			
		    
		    playerIdList: [],//视频播放Id列表
		    playerObjectList: [],//视频播放对象集合
		    playerNodeList: [],//视频节点对象集合
		    
		    answerElementNodes:[],//答案列表项Element节点集合
		    editAnswerElementNodes:[],//修改答案项Element节点集合
			replyElementNodes:[],//回复列表项Element节点集合
		    
		    addAnswerForm_disabled:false,//提交答案按钮是否禁用
		    editAnswer_disabled : new Map(),//提交修改答案按钮是否禁用 map格式 key:答案Id value:是否禁用
		    editQuestion_disabled:false,//提交修改问题按钮是否禁用
		    addReplyFriend_disabled: new Map(),//提交添加回复按钮是否禁用 map格式 key:一语论Id value:是否禁用
		    addReply_disabled: new Map(),//提交添加回复按钮是否禁用 map格式 key:一语论Id value:是否禁用
		    editReply_disabled: new Map(),//提交修改回复按钮是否禁用 map格式 key:回复Id value:是否禁用
		    appendQuestion_disabled:false,//追加提问按钮是否禁用
		    editAppendQuestion_disabled : new Map(),//提交修改追加问题按钮是否禁用 map格式 key:追加问题Id value:是否禁用
		 
		    editReplyStatusField : [], //修改回复状态项绑定(二维数组)
		    editReplyContentField : [], //修改回复内容项绑定(二维数组)
			editReplyFormView : new Map(),//修改回复表单  key:回复Id value:是否显示
			
			
			addReplyContentField : [], //添加回复内容项绑定
			addReplyFormView : new Map(),//添加回复表单  key:答案Id value:是否显示
			
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
		if (Object.keys(_self.addAnswerEditorCreateParameObject).length != 0) {//不等于空
			//创建富文本编辑器
			_self.addAnswerEditor = createEditor(
				_self.addAnswerEditorCreateParameObject.ref, 
				_self.addAnswerEditorCreateParameObject.availableTag, 
				_self.addAnswerEditorCreateParameObject.uploadPath, 
				null
			);
		}
		
		
	
		//创建富文本编辑器
		_self.editAnswerEditorCreateParameMap.forEach(function(value,key){
			let editor = createEditor(
				value.ref, 
				value.availableTag, 
				value.uploadPath, 
				null
			);
		
			_self.editAnswerEditorMap.set(key,editor);
	　　});
	
		if (Object.keys(_self.editQuestionEditorCreateParameObject).length != 0) {//不等于空
			//创建富文本编辑器
			_self.editQuestionEditor = createEditor(
				_self.editQuestionEditorCreateParameObject.ref, 
				_self.editQuestionEditorCreateParameObject.availableTag, 
				_self.editQuestionEditorCreateParameObject.uploadPath, 
				_self.editQuestionEditorCreateParameObject.userGradeList
			);
		}
		
		if (Object.keys(_self.appendQuestionEditorCreateParameObject).length != 0) {//不等于空
			//创建富文本编辑器
			_self.appendQuestionEditor = createEditor(
				_self.appendQuestionEditorCreateParameObject.ref, 
				_self.appendQuestionEditorCreateParameObject.availableTag, 
				_self.appendQuestionEditorCreateParameObject.uploadPath, 
				_self.appendQuestionEditorCreateParameObject.userGradeList
			);
		}
		
		//创建富文本编辑器
		_self.editAppendQuestionEditorCreateParameMap.forEach(function(value,key){
			let editor = createEditor(
				value.ref, 
				value.availableTag, 
				value.uploadPath, 
				null
			);
		
			_self.editAppendQuestionEditorMap.set(key,editor);
	　　});
		
	},
	// keep-alive 离开时
	deactivated : function () {
		if(this.addAnswerEditor != ""){
			this.addAnswerEditor.remove();
		}
		
		
		this.editAnswerEditorMap.forEach(function(value,key){
			value.remove();
	　　});
		this.editAnswerEditorMap.clear();
		
		if(this.editQuestionEditor != ""){
			this.editQuestionEditor.remove();
		}
		
		if(this.appendQuestionEditor != ""){
			this.appendQuestionEditor.remove();
		}
		this.editAppendQuestionEditorMap.forEach(function(value,key){
			value.remove();
	　　});
		
		
	},
	//生命周期钩子 -- 响应数据修改时运行
	updated : function () {
		let _self = this;
		_self.$nextTick(function() {
			if(_self.questionId != ''){
				_self.$nextTick(function() {
					let questionRefValue = _self.$refs['question_'+_self.questionId];
					if(questionRefValue != undefined){
						_self.renderBindNode(questionRefValue); 
					}
    				
    				if(_self.question != '' && _self.question.appendQuestionItemList != null && _self.question.appendQuestionItemList.length >0){
						for(var i=0; i<_self.question.appendQuestionItemList.length; i++){
							var appendQuestionItem = _self.question.appendQuestionItemList[i];
							
							let appendQuestionRefValue = _self.$refs['appendQuestion_'+appendQuestionItem.id];
							if(appendQuestionRefValue != undefined){
								_self.renderBindNode(appendQuestionRefValue); 
							}
    						
							
						}
						
					}
				});
								
			}
			if(_self.answerList != null && _self.answerList != '' && _self.answerList.length > 0){
				for (let i = 0; i <_self.answerList.length; i++) {
					let answer = _self.answerList[i];
					let answerRefValue = _self.$refs['answer_'+answer.id];
					if(answerRefValue != undefined){
						_self.renderBindNode(answerRefValue);
					}
				}
			}
		})
	},
	beforeRouteEnter (to, from, next) {
		//上级路由编码
		if(to.query.questionView_beforeUrl == undefined || to.query.questionView_beforeUrl==''){//前一个URL
			let parameterObj = new Object;
			parameterObj.path = from.path;
			let query = from.query;
			for(let q in query){
				query[q] = encodeURIComponent(query[q]);
			}
			
			parameterObj.query = query;
			//将请求参数转为base62
			let encrypt = delete_base62_equals(base62_encode(JSON.stringify(parameterObj)));
			
			
			let newFullPath = updateURLParameter(to.fullPath,'questionView_beforeUrl',encrypt);
			
			to.fullPath = newFullPath;
			
			let paramGroup = to.query;
			paramGroup.questionView_beforeUrl = encrypt;
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
		if(this.$route.query.questionId != undefined && this.$route.query.questionId != ''){
			this.questionId = this.$route.query.questionId;
		}
		if(this.$route.query.answerId != undefined && this.$route.query.answerId != ''){
			this.answerId = this.$route.query.answerId;
		}
		if(this.$route.query.page != undefined && this.$route.query.page != ''){
			this.currentpage = this.$route.query.page;
		}
		
		//上级路由解码
		if(this.$route.query.questionView_beforeUrl != undefined && this.$route.query.questionView_beforeUrl != ''){
			let decrypt = base62_decode(add_base62_equals(this.$route.query.questionView_beforeUrl));
			
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
		
		
		
		//将问题对象先设置空字符串,防止ajax未加载完成时显示undefined
		this.question = {content :''};
		
		this.queryQuestion();
	},
	computed: {
		//动态解析答案模板数据
		answerDataComponent: function() {
			return function (html) {
				return {
					template: "<div>"+ html +"</div>", // use content as template for this component 必须用<div>标签包裹，否则会有部分内容不显示
					props: this.$props, // re-use current props definitions
					
				};
			};	
		},
		//动态解析问题模板数据
		questionComponent: function() {
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
			this.answerElementNodes.push(el);
		},
		//修改答案ref节点处理
		handleEditAnswerNodes(el) {
			this.editAnswerElementNodes.push(el);
		},
		//回复ref节点处理
		handleReplyNodes(el) {
			this.replyElementNodes.push(el);
		},
	
	
		//查询问题
		queryQuestion : function() {
			let _self = this;
			
			let params = {};
			if(_self.$route.query.page != undefined && _self.$route.query.page != ''){
				params = {
			    	method : 'view',
			    	questionId :_self.questionId,
			    	answerId :_self.answerId,
			    	page :_self.currentpage,
			    }
			}else{
				params = {
			    	method : 'view',
			    	questionId :_self.questionId,
			    	answerId :_self.answerId,
			    	
			    }
			}
			_self.$ajax.get('control/question/manage', {
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
			    			if(key == "question"){//问题
			    				let question = mapData[key];
			    				
								//处理自定义标签
								let contentNode = document.createElement("div");
								contentNode.innerHTML = question.content;
								
								_self.bindNode(contentNode);
								question.content = escapeVueHtml(contentNode.innerHTML);
								
								
								if(question.appendQuestionItemList != null && question.appendQuestionItemList.length >0){
									for(var i=0; i<question.appendQuestionItemList.length; i++){
										var appendQuestionItem = question.appendQuestionItemList[i];
										
										//处理图片放大标签
										var contentNode2 = document.createElement("div");
										contentNode2.innerHTML = appendQuestionItem.content;
										_self.bindNode(contentNode2);
										appendQuestionItem.content = escapeVueHtml(contentNode2.innerHTML);
										
										
									}
									
								}
								
								
								
								
								_self.question = question;
								
								
			    			}else if(key == "availableTag"){//答案富文本框支持标签
			    				_self.availableTag = mapData[key];
				    			
			    			}else if(key == "userName"){//用户名称
								let userName = mapData[key];
			    				_self.userName = userName;
			    			}else if(key == "pageView"){//答案分页列表
			    				let pageView = mapData[key];
			    				
			    				let answerList = pageView.records;
			    				
			    				//清空
								_self.answerElementNodes.length = 0;
			    				_self.editAnswerElementNodes.length = 0;
			    				_self.answerStatusField.length = 0;
			    				_self.editReplyStatusField.length = 0;
			    				_self.editReplyContentField.length = 0;
			    				_self.answerList = '';
			    				
			    				
			    				if(answerList != null && answerList.length > 0){
				    				for (let i = 0; i <answerList.length; i++) {
										let answer = answerList[i];
										
										
										//定义回复数组
										let editReplyStatusField_reply_array = [];
										let editReplyContentField_reply_array = [];
										
										//回复
										if(answer.answerReplyList != null && answer.answerReplyList.length >0){
											for (let j = 0; j <answer.answerReplyList.length; j++) {
												let reply = answer.answerReplyList[j];
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
										contentNode.innerHTML = answer.content;
										_self.bindNode(contentNode);
										answer.content = escapeVueHtml(contentNode.innerHTML);
				    				
				    					_self.answerStatusField.push(answer.status);
				    					
				    					
			    					}
			    					_self.answerList = answerList;
			    				
				    				_self.totalrecord = parseInt(pageView.totalrecord);//服务器返回的long类型已转为String类型
					    			_self.currentpage = pageView.currentpage;
									_self.totalpage = parseInt(pageView.totalpage);//服务器返回的long类型已转为String类型
									_self.maxresult = pageView.maxresult;
									_self.isShowPage = true;//显示分页栏
									
									
									_self.$nextTick(function() {
										
										//跳转到锚点
										if(_self.answerId != null && _self.answerId != ""){
										
											for(let i = 0; i<_self.answerElementNodes.length; i++){
												let answerElement = _self.answerElementNodes[i];
												let answerId = answerElement.getAttribute("answerId");
												
												if(answerId == _self.answerId){//跳转到当前答案
													//锚点距离浏览器顶部的高度 - <el-main>标签距离浏览器顶部的高度
											        document.querySelector(".el-main").scrollTop = answerElement.offsetTop - document.querySelector(".el-main").offsetTop;
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
			    		
			    		if (Object.keys(_self.addAnswerEditorCreateParameObject).length === 0) {//等于空
			    			let uploadPath = "control/answer/manage?method=uploadImage&userName="+_self.userName+"&isStaff=true&questionId="+_self.questionId;
					   		//创建富文本编辑器
							_self.addAnswerEditor = createEditor(_self.$refs.answerContent, JSON.parse(_self.availableTag), uploadPath, null);
			    			_self.addAnswerEditorCreateParameObject = {
			    				ref:_self.$refs.answerContent,
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
				path: '/admin/control/question/manage/view', 
				query:{ 
					visible : this.$route.query.visible,
					questionView_beforeUrl:(this.$route.query.questionView_beforeUrl != undefined ? this.$route.query.questionView_beforeUrl:''),
					questionId : this.questionId,
					answerId : this.answerId,
					page : page
				}
			});
	    },
	    //审核问题
		auditQuestion: function(questionId) {
			let _self = this;
			
			_self.$confirm('此操作将该问题状态改为已发布, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
				let formData = new FormData();
				formData.append('questionId',  questionId); 
				
				_self.$ajax({
			        method: 'post',
			        url: 'control/question/manage?method=auditQuestion',
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
				    		
				    		
				    		_self.queryQuestion();
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
	    //审核答案
		auditAnswer: function(answerId) {
			let _self = this;
			
			_self.$confirm('此操作将该答案状态改为已发布, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
				let formData = new FormData();
				formData.append('answerId',  answerId); 
				
				_self.$ajax({
			        method: 'post',
			        url: 'control/answer/manage?method=auditAnswer',
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
				    		
				    		
				    		_self.queryQuestion();
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
				formData.append('answerReplyId',  replyId); 
				
				_self.$ajax({
			        method: 'post',
			        url: 'control/answer/manage?method=auditAnswerReply',
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
				    		
				    		
				    		_self.queryQuestion();
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
	  	//采纳答案
  		adoptionAnswer: function(answerId) {
  			let _self = this;
  			
  			_self.$confirm('此操作将采纳该答案, 是否继续?', '提示', {
  	            confirmButtonText: '确定',
  	            cancelButtonText: '取消',
  	            type: 'warning'
  	        }).then(() => {
  				let formData = new FormData();
  				formData.append('answerId',  answerId); 
  				
  				_self.$ajax({
  			        method: 'post',
  			        url: 'control/answer/manage?method=adoptionAnswer',
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
  				    		_self.$message.success("采纳成功");
  				    		
  				    		//删除缓存
  				    		_self.$store.commit('setCacheNumber');
  				    		
  				    		
  				    		_self.queryQuestion();
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
	    
  		//取消采纳答案
  		cancelAdoptionAnswer: function(answerId) {
  			let _self = this;
  			
  			_self.$confirm('此操作将取消采纳该答案, 是否继续?', '提示', {
  	            confirmButtonText: '确定',
  	            cancelButtonText: '取消',
  	            type: 'warning'
  	        }).then(() => {
  				let formData = new FormData();
  				formData.append('answerId',  answerId); 
  				
  				_self.$ajax({
  			        method: 'post',
  			        url: 'control/answer/manage?method=cancelAdoptionAnswer',
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
  				    		_self.$message.success("取消采纳成功");
  				    		
  				    		//删除缓存
  				    		_self.$store.commit('setCacheNumber');
  				    		
  				    		
  				    		_self.queryQuestion();
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
	    
		//添加答案
		addAnswerForm : function() {
			let _self = this;
			
			_self.addAnswerForm_disabled = true;
			
	        //清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			if(_self.questionId != null && _self.questionId != ''){
				formData.append('questionId',  _self.questionId); 
			}
			
			if(_self.$refs.answerContent.value != null && _self.$refs.answerContent.value !=''){
				formData.append('content', _self.$refs.answerContent.value);
			}
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/answer/manage?method=add',
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
			    		 _self.addAnswerEditor.html("");
			    		 _self.addAnswerEditor.remove();
			    		 _self.addAnswerEditorCreateParameObject = {};
			    		 
			    		_self.queryQuestion();
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
			    
			    _self.addAnswerForm_disabled = false;
			})
			.catch(function (error) {
				console.log(error);
			});
	    },
	  
	    //修改答案表单
		editAnswerUI : function(answer) {
			let _self = this;
			if(_self.editAnswerFormView.get(answer.id) == true){//如果已打开
				return;
			}
			
			
			
			this.$ajax.get('control/answer/manage', {
			    params: {
			    	method : 'edit',
			    	answerId: answer.id,
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
			    			if(key == "answer"){//答案
			    				let _answer = mapData[key];
			    				
			    				_self.editAnswerFormView.set(answer.id,true);
								_self.$nextTick(function() {
									for(let i = 0; i<_self.editAnswerElementNodes.length; i++){
										let editAnswerElement = _self.editAnswerElementNodes[i];
										
										editAnswerElement.value = _answer.content;
										
										let _answerId = editAnswerElement.getAttribute("answerId");
									
										if(_answerId == answer.id){
											
											let uploadPath = "control/answer/manage?method=uploadImage&userName="+answer.userName+"&isStaff="+answer.isStaff+"&questionId="+_self.questionId;
											//创建富文本编辑器
											let editor = createEditor(editAnswerElement, JSON.parse(_self.availableTag), uploadPath, null);
											
											_self.editAnswerEditorMap.set(answer.id,editor);
								
							    			_self.editAnswerEditorCreateParameMap.set(answer.id,{
							    				ref:editAnswerElement,
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
		
		//取消修改答案
		cancelEditAnswer : function(answerId) {
			let _self = this;
			
			let editAnswerEditor = _self.editAnswerEditorMap.get(answerId);
			if(editAnswerEditor != null){
				editAnswerEditor.remove();
				_self.editAnswerEditorMap.delete(answerId);
				_self.editAnswerEditorCreateParameMap.delete(answerId);
			}
			_self.editAnswerFormView.set(answerId,false);
			
		},
		
	    //修改答案
		editAnswer : function(answerId) {
			let _self = this;
			_self.editAnswer_disabled.set(answerId,true);
			
	        //清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			formData.append('answerId',  answerId); 

			if(_self.answerList != null && _self.answerList.length > 0){
				for (let i = 0; i <_self.answerList.length; i++) {
					let answer = _self.answerList[i];
					if(answer.id == answerId){
						formData.append('status',  _self.answerStatusField[i]); 
						break;
					}
				}
			}
			
			for(let i = 0; i<_self.editAnswerElementNodes.length; i++){
				let editAnswerElement = _self.editAnswerElementNodes[i];
				let _answerId = editAnswerElement.getAttribute("answerId");
				
				if(_answerId == answerId){
					formData.append('content', editAnswerElement.value);
	    			break;
				}
			}
			
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/answer/manage?method=edit',
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
			    		
			    	
						
						//清除答案列表
						_self.clearAnswerList();
			    		
			    		
			    		
			    		_self.queryQuestion();
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
			    _self.editAnswer_disabled.set(answerId,false);
			})
			.catch(function (error) {
				console.log(error);
			});
		},
		
		
		
		//追加提问表单
		appendQuestionUI : function() {
			let _self = this;
			if(_self.appendQuestionFormView){//如果已打开
				return;
			}
			
			_self.appendQuestionFormView = true;
			
			
			_self.$ajax.get('control/question/manage', {
			    params: {
			    	method : 'appendQuestion',
			    	questionId :_self.questionId,
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
			    		
			    		let question = null;
			    		
						for(let key in mapData){
			    			if(key == "question"){
			    				question = mapData[key];
			    			}
			    		}
						if(question != null){
							
							
		    				_self.$refs.appendQuestionContent.value = "";
		    				
	
		    				
	    					let uploadPath = "control/question/manage?method=upload&userName="+_self.question.userName+"&isStaff="+_self.question.isStaff;
				   		
					   		let availableTag = ['source', '|', 'preview', 'template', 'code',
					   		 		        '|', 'justifyleft', 'justifycenter', 'justifyright',
					   				        'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent', 'subscript',
					   				        'superscript', 'clearhtml', 'quickformat', 'selectall', '|', 
					   				        'formatblock', 'fontname', 'fontsize','fullscreen', '/', 'forecolor', 'hilitecolor', 'bold',
					   				        'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', '|', 'image', 'multiimage',
					   				         'media','embedVideo','uploadVideo', 'insertfile','emoticons','baidumap', 'table', 'hr',   'pagebreak',
					   				         'link', 'unlink'];
					   		
					   		//创建富文本编辑器
							_self.appendQuestionEditor = createEditor(_self.$refs.appendQuestionContent, availableTag, uploadPath, null);
			    			_self.appendQuestionEditorCreateParameObject = {
			    				ref:_self.$refs.appendQuestionContent,
			    				availableTag:availableTag,
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
		
		//取消追加提问
		cancelAppendQuestion : function() {
			let _self = this;
			
			
			_self.appendQuestionEditor.remove();
			_self.appendQuestionEditorCreateParameObject ={};

			_self.appendQuestionFormView = false;
			
		},
		
		//追加提问
		appendQuestion : function() {
			let _self = this;
			_self.appendQuestion_disabled = true;
			
		 	//清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			formData.append('questionId', _self.questionId);
			
			
			if(_self.$refs.appendQuestionContent.value != null && _self.$refs.appendQuestionContent.value !=''){
				formData.append('content', _self.$refs.appendQuestionContent.value);
			}
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/question/manage?method=appendQuestion',
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
			    		
			    		//取消追加提问
						_self.cancelAppendQuestion();
			    		
			    		_self.queryQuestion();
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
			   _self.appendQuestion_disabled = false;
			})
			.catch(function (error) {
				console.log(error);
			});
		
		},
		
		
		//修改追加问题表单
		editAppendQuestionUI : function(appendQuestionItem) {
			let _self = this;
			if(_self.editAppendQuestionFormView.get(appendQuestionItem.id) == true){//如果已打开
				return;
			}
			
			
			
			this.$ajax.get('control/question/manage', {
			    params: {
			    	method : 'editAppendQuestion',
			    	questionId: _self.questionId,
			    	appendQuestionItemId: appendQuestionItem.id,
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
			    		let question = null;
		    			let appendQuestionItem = null;
			    		for(let key in mapData){
			    			
			    			if(key == "question"){
			    				question = mapData[key];
			    			}else if(key == "appendQuestionItem"){
			    				appendQuestionItem = mapData[key];
			    			}
			    		}
			    	
			    		if(question != null && appendQuestionItem != null){
			    			
			    			_self.editAppendQuestionFormView.set(appendQuestionItem.id,true);
							_self.$nextTick(function() {
								for(let i = 0; i<_self.editAppendQuestionElementNodes.length; i++){
									let editAppendQuestionElement = _self.editAppendQuestionElementNodes[i];
									
									editAppendQuestionElement.value = appendQuestionItem.content;
									
									let _appendQuestionItem = editAppendQuestionElement.getAttribute("appendQuestionItemId");
								
									if(_appendQuestionItem == appendQuestionItem.id){
										
										let uploadPath = "control/question/manage?method=upload&userName="+question.userName+"&isStaff="+question.isStaff;
										
										let availableTag = ['source', '|', 'preview', 'template', 'code',
									   		 		        '|', 'justifyleft', 'justifycenter', 'justifyright',
									   				        'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent', 'subscript',
									   				        'superscript', 'clearhtml', 'quickformat', 'selectall', '|', 
									   				        'formatblock', 'fontname', 'fontsize','fullscreen', '/', 'forecolor', 'hilitecolor', 'bold',
									   				        'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', '|', 'image', 'multiimage',
									   				         'media','embedVideo','uploadVideo', 'insertfile','emoticons','baidumap', 'table', 'hr',   'pagebreak',
									   				         'link', 'unlink'];
										
										//创建富文本编辑器
										let editor = createEditor(editAppendQuestionElement, availableTag, uploadPath, null);
										
										_self.editAppendQuestionEditorMap.set(appendQuestionItem.id,editor);
							
						    			_self.editAppendQuestionEditorCreateParameMap.set(appendQuestionItem.id,{
						    				ref:editAppendQuestionElement,
						    				availableTag:availableTag,
						    				uploadPath:uploadPath,
						    				userGradeList:null
						    			});
						    			break;
									}
								}
							});
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
		
		//取消修改追加问题
		cancelEditAppendQuestion : function(appendQuestionItemId) {
			let _self = this;
			
			let editAppendQuestionEditor = _self.editAppendQuestionEditorMap.get(appendQuestionItemId);
			if(editAppendQuestionEditor != null){
				editAppendQuestionEditor.remove();
				_self.editAppendQuestionEditorMap.delete(appendQuestionItemId);
				_self.editAppendQuestionEditorCreateParameMap.delete(appendQuestionItemId);
			}
			_self.editAppendQuestionFormView.set(appendQuestionItemId,false);
			
		},
		
	    //修改追加问题
		editAppendQuestion : function(appendQuestionItemId) {
			let _self = this;
			_self.editAppendQuestion_disabled.set(appendQuestionItemId,true);
			
	        //清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			formData.append('questionId',  _self.questionId); 
			formData.append('appendQuestionItemId',  appendQuestionItemId); 

			
			for(let i = 0; i<_self.editAppendQuestionElementNodes.length; i++){
				let editAppendQuestionElement = _self.editAppendQuestionElementNodes[i];
				let _appendQuestionItemId = editAppendQuestionElement.getAttribute("appendQuestionItemId");
				
				if(_appendQuestionItemId == appendQuestionItemId){
					formData.append('content', editAppendQuestionElement.value);
	    			break;
				}
			}
			
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/question/manage?method=editAppendQuestion',
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
			    		
			    		
			    		
			    		
			    		let editAppendQuestionEditor = _self.editAppendQuestionEditorMap.get(appendQuestionItemId);
						if(editAppendQuestionEditor != null){
						//	editAnswerEditor.html("");//清空字段
							editAppendQuestionEditor.remove();
							_self.editAppendQuestionEditorMap.delete(appendQuestionItemId);
							_self.editAppendQuestionEditorCreateParameMap.delete(appendQuestionItemId);
						}
						_self.editAppendQuestionFormView.set(appendQuestionItemId,false);
			    		
			    		
			    		
			    		_self.queryQuestion();
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
			    _self.editAppendQuestion_disabled.set(appendQuestionItemId,false);
			})
			.catch(function (error) {
				console.log(error);
			});
		},
		
		//修改问题表单
		editQuestionUI : function() {
			let _self = this;
			if(_self.editQuestionFormView){//如果已打开
				return;
			}
			
			_self.editQuestionFormView = true;
			
			
			_self.$ajax.get('control/question/manage', {
			    params: {
			    	method : 'editQuestion',
			    	questionId :_self.questionId,
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
			    		
			    		let maxDeposit = null;
			    		let maxPoint = null;
			    		let question = null;
			    		
						for(let key in mapData){
			    			if(key == "maxDeposit"){//所有设置的等级
			    				maxDeposit = mapData[key];
			    			}else if(key == "maxPoint"){
			    				maxPoint = mapData[key];
			    			}else if(key == "question"){
			    				question = mapData[key];
			    			}
			    		}
						if(maxDeposit != null){
							_self.maxDeposit = maxDeposit;
						}
						if(maxPoint != null){
							_self.maxPoint = maxPoint;
						}
						
						if(question != null){
							
							_self.title = _self.question.title;
								
							_self.sort = _self.question.sort;
							_self.allow = _self.question.allow;
							_self.status = _self.question.status;
							_self.point = _self.question.point;
							_self.amount = _self.question.amount;
		    				_self.$refs.questionContent.value = question.content;
		    				
		    				
		    				if(question.questionTagAssociationList != null && question.questionTagAssociationList.length >0){
		    					for(let i = 0; i <question.questionTagAssociationList.length; i++){
		    						let questionTagAssociation = question.questionTagAssociationList[i];
		    						_self.tagIdGroup.push(questionTagAssociation.questionTagId);
		    						let obj =new Object();
									obj.value = questionTagAssociation.questionTagId;
									obj.label = questionTagAssociation.questionTagName;
									_self.options.push(obj);
		    					}
		    					_self.select_placeholder = "";
		    				}
		    				
							
		    				
		    				
		    				
	    					let uploadPath = "control/question/manage?method=upload&userName="+_self.question.userName+"&isStaff="+_self.question.isStaff;
				   		
					   		let availableTag = ['source', '|', 'preview', 'template', 'code',
					   		 		        '|', 'justifyleft', 'justifycenter', 'justifyright',
					   				        'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent', 'subscript',
					   				        'superscript', 'clearhtml', 'quickformat', 'selectall', '|', 
					   				        'formatblock', 'fontname', 'fontsize','fullscreen', '/', 'forecolor', 'hilitecolor', 'bold',
					   				        'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', '|', 'image', 'multiimage',
					   				         'media','embedVideo','uploadVideo', 'insertfile','emoticons','baidumap', 'table', 'hr',   'pagebreak',
					   				         'link', 'unlink'];
					   		
					   		//创建富文本编辑器
							_self.editQuestionEditor = createEditor(_self.$refs.questionContent, availableTag, uploadPath, null);
			    			_self.editQuestionEditorCreateParameObject = {
			    				ref:_self.$refs.questionContent,
			    				availableTag:availableTag,
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
		
		//取消修改问题
		cancelEditQuestion : function() {
			let _self = this;
			
			
			_self.editQuestionEditor.remove();
			_self.editQuestionEditorCreateParameObject ={};
			
			_self.title ='';
			_self.sort= 0;
			_self.allow = true;
			_self.status =0;
			_self.tagIdGroup =[];//标签Id组
			_self.options= [];//Select 选择器标签数据
			_self.point='';//积分
			_self.amount='';//金额
			_self.maxDeposit = 0;//允许使用的预存款
			_self.maxPoint = 0;//允许使用的积分
			
			
			_self.editQuestionFormView = false;
			
		},
		
		//修改问题
		editQuestion : function() {
			let _self = this;
			_self.editQuestion_disabled = true;
			
		 	//清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			formData.append('questionId', _self.questionId);
			if(_self.title != null && _self.title != ''){
				formData.append('title', _self.title);
				
			}
			
			if(_self.tagIdGroup != null && _self.tagIdGroup.length >0){
				for(let i = 0; i <_self.tagIdGroup.length; i++){
					let tagId = _self.tagIdGroup[i];
					formData.append('tagId', tagId); 
				}
			}
			if(_self.sort != null){
				formData.append('sort', _self.$refs.sort_ref.displayValue);
			}
			
			if(_self.point != null){
				formData.append('point', _self.point);
			}
			if(_self.amount != null){
				formData.append('amount', _self.amount);
			}
			
			formData.append('allow', _self.allow);
			formData.append('status', _self.status);
			
			
			if(_self.$refs.questionContent.value != null && _self.$refs.questionContent.value !=''){
				formData.append('content', _self.$refs.questionContent.value);
			}
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/question/manage?method=editQuestion',
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
			    		
			    		//取消修改问题
						_self.cancelEditQuestion();
			    		
			    		_self.queryQuestion();
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
			   _self.editQuestion_disabled = false;
			})
			.catch(function (error) {
				console.log(error);
			});
		
		},
		
		
		
		
		//加载问题标签
	    loadQuestionTag: function() { 
	    	
	    	this.popup_allTag = true;
	       	this.$refs.question_tag_ref.blur();//使Select选择器失去焦点，并隐藏下拉框
	       	if(this.tagIdGroup.length ==0){
	       		this.options.length = 0;//清空	
	       	}
	       	
	        this.queryAllTag();
		},
		//关闭问题标签弹出框
		closeQuestionTag: function() { 
	    	this.popup_allTag = false;

	    },
	  	//处理删除标签
	    processRemoveTag: function(val) { 
			if(this.tagIdGroup.length ==0){
				
  				this.select_placeholder = "请选择";
  			}
	    },
		//查询所有标签
		queryAllTag : function() {
			var _self = this;

			
			_self.$ajax.get('control/questionTag/manage', {
			    params: {
			    	method : 'allTag'
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
			    		let tagList = returnValue.data;
			    		if (tagList != null && tagList.length > 0) {
							_self.allTagList = tagList;
							
							for (var i= 0; i < tagList.length; i++) {
								var questionTag = tagList[i];
								//第一次选中第一个标签
								if(_self.selectedFirstTagId == ''){
									_self.childTagList = questionTag.childTag;
									_self.selectedFirstTagId = questionTag.id;
									break;
								}else{
									if(_self.selectedFirstTagId == questionTag.id){
										_self.childTagList = questionTag.childTag;
										break;
									}
								}
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
		//选择子标签
		selectChildTag : function(questionTagId) {
			var _self = this;
			_self.selectedFirstTagId = questionTagId;
			
			if(_self.allTagList != '' && _self.allTagList.length >0){
				for (var i= 0; i < _self.allTagList.length; i++) {
					var questionTag = _self.allTagList[i];
					
					if(questionTagId == questionTag.id){
						_self.childTagList = questionTag.childTag;
						
						//如果只有一个节点，则允许选择本标签
						if(questionTag.childNodeNumber == 0){
							_self.selectedTag(questionTag);
						}

						break;
					}
					
				}
				
			}
			
		},
		//选中标签
		selectedTag : function(childQuestionTag) {
			//判断是否重复选择,如果重复则取消选择
			if(this.options != null && this.options.length >0){
				for(var i=0; i<this.options.length; i++){
					var selectedTag = this.options[i];
					if(selectedTag.value == childQuestionTag.id){
						//删除标签
						this.deleteTag(selectedTag.value);
						return;
					}
				}
				
			}

			var o = new Object();
			o.value = childQuestionTag.id;
			o.label = childQuestionTag.name;

			this.options.push(o);
			
			
			this.tagIdGroup.push(childQuestionTag.id);
			
			this.select_placeholder = "";
		},
		//删除标签
		deleteTag : function(questionTagId) {
			if(this.options != null && this.options.length >0){
				for(let i=0; i<this.options.length; i++){
					let selectedTag = this.options[i];
					if(selectedTag.value == questionTagId){
						this.options.splice(i, 1);
						
						for(let j=0; j<this.tagIdGroup.length; j++){
							if(this.tagIdGroup[j] == questionTagId){
								this.tagIdGroup.splice(j, 1);
								break;
							}
							
						}
						
						if(this.tagIdGroup.length ==0){
							this.select_placeholder = "请选择";
						}
						return;
					}
				}
				
			}
		
			
			
		},
		//选中一级标签样式
		selectedTagClass : function(questionTagId) {
			var className = "nav-link";
			if(this.selectedFirstTagId == questionTagId){
				className += " active";
				
			}
			if(this.options != null && this.options.length >0){
				for(var i=0; i<this.options.length; i++){
					var selectedTag = this.options[i];
					if(selectedTag.value == questionTagId){
						className += " selected";
					}
				}
				
			}
			return className;
		},
		
		//选中二级标签样式
		selectedChildTagClass : function(questionTagId) {
			if(this.options != null && this.options.length >0){
				for(var i=0; i<this.options.length; i++){
					var selectedTag = this.options[i];
					if(selectedTag.value == questionTagId){
						return "child-tag selected";
					}
				}
				
			}
			return "child-tag";
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
		addReplyFriend : function(answerId,replyId) {
			let _self = this;
			
			_self.addReplyFriend_disabled.set(replyId,true);
	
	        //清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
	
	        let formData = new FormData();
				
	        formData.append('answerId',  answerId); 
	
	        formData.append('friendReplyId',  replyId); 
	        
	        formData.append('content', _self.addReplyFriendContentField[replyId]); 
		
			_self.$ajax({
			    method: 'post',
			    url: 'control/answer/manage?method=addAnswerReply',
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
			    		
			    		
			    		//清除答案列表
                        _self.clearAnswerList();

			    		_self.queryQuestion();
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
			    _self.addReplyFriend_disabled.set(answerId,false);
			})
			.catch(function (error) {
				console.log(error);
			});
		},
		
		//添加回复表单
		addReplyUI : function(answerId) {
			let _self = this;
			if(_self.addReplyFormView.get(answerId) == true){//如果已打开
				return;
			}
			
			_self.addReplyFormView.set(answerId,true);
			
			
			
		},
		//取消添加回复
		cancelAddReply : function(answerId) {
			let _self = this;
			
			_self.addReplyFormView.set(answerId,false);
			
		},
		//添加回复
		addReply : function(answerId) {
			let _self = this;
			
			_self.addReply_disabled.set(answerId,true);
			
			
			//清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			formData.append('answerId',  answerId); 
			
			
			

			if(_self.answerList != null && _self.answerList.length > 0){
				for (let i = 0; i <_self.answerList.length; i++) {
					let answer = _self.answerList[i];
					if(answer.id == answerId){
						formData.append('content',  _self.addReplyContentField[i]);
						break; 
					}
					
				}
			}
			
		
			_self.$ajax({
			    method: 'post',
			    url: 'control/answer/manage?method=addAnswerReply',
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
			    		
			    		
						_self.addReplyFormView.set(answerId,false);
			    		if(_self.answerList != null && _self.answerList.length > 0){
							for (let i = 0; i <_self.answerList.length; i++) {
								let answer = _self.answerList[i];
								if(answer.id == answerId){
									_self.addReplyContentField[i] = "";//清空
									break; 
								}
								
							}
						}
						//清除答案列表
						_self.clearAnswerList();
						
			    		_self.queryQuestion();
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
			    _self.addReply_disabled.set(answerId,false);
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
			formData.append('answerReplyId',  replyId); 
			
			
			

			if(_self.answerList != null && _self.answerList.length > 0){
				for (let i = 0; i <_self.answerList.length; i++) {
					let answer = _self.answerList[i];
					
					if(answer.answerReplyList != null && answer.answerReplyList.length >0){
						for (let j = 0; j <answer.answerReplyList.length; j++) {
							let reply = answer.answerReplyList[j];
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
			    url: 'control/answer/manage?method=editAnswerReply',
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
			    		
			    		
						//清除答案列表
						_self.clearAnswerList();
			    		
			    		
			    		
			    		_self.queryQuestion();
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
		
		//删除问题
		deleteQuestion : function(questionId) {
			let _self = this;
			
			this.$confirm('此操作将删除该问题, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	
		    	formData.append('questionId', questionId);
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/question/manage?method=deleteQuestion',
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
				    		
				    		_self.queryQuestion();
				    		
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
		
		//删除追加问题
		deleteAppendQuestion : function(appendQuestionItemId) {
			let _self = this;
			
			this.$confirm('此操作将删除该追加问题, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
	        	formData.append('questionId', _self.questionId);
		    	formData.append('appendQuestionItemId', appendQuestionItemId);
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/question/manage?method=deleteAppendQuestion',
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
				    		_self.queryQuestion();
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
		//删除答案
		deleteAnswer : function(answerId) {
			let _self = this;
			
			this.$confirm('此操作将删除该答案, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	
		    	formData.append('answerId', answerId);
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/answer/manage?method=delete',
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
				    		_self.queryQuestion();
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
		    	
		    	formData.append('answerReplyId', replyId);
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/answer/manage?method=deleteAnswerReply',
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
				    		_self.queryQuestion();
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
			        url: 'control/answer/manage?method=recoveryReply',
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
				    		_self.queryQuestion();
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
		
		//清除答案列表
		clearAnswerList : function() {
			let _self = this;
			_self.answerList.length =0;
	        _self.editReplyContentField = [];
	        _self.addReplyFriendContentField = {};
	
	        for (const [key, value] of Object.entries(_self.editAnswerEditorMap)){
	            if(value != null){
	                value.html("");//清空字段
					value.remove();
	            }
	        }
	        _self.editAnswerEditorMap.clear();
	        _self.editAnswerEditorCreateParameMap.clear();
	
	        _self.editAnswerFormView.clear();
	        _self.addReplyFormView.clear();
	        _self.editReplyFormView.clear();
	        _self.addReplyFriendFormView.clear();
	    },
	    //点击回复层级
	    clickReplyLevel : function(answerId,replyId) {
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
				_self.showReplyLevel(answerId,replyId);
			}
	       
		},
		//展示回复层级
		showReplyLevel : function(answerId,replyId) {
			let _self = this;
	       	let dotArray = new Array();
			let replyList = [];
			if(_self.answerList != null && _self.answerList.length > 0){
				A:for (let i = 0; i <_self.answerList.length; i++) {
					let answer = _self.answerList[i];
	               
					if(answer.id == answerId){
						//回复
						if(answer.answerReplyList != null && answer.answerReplyList.length >0){
							replyList = answer.answerReplyList;
							for (let j = 0; j <answer.answerReplyList.length; j++) {
								let reply = answer.answerReplyList[j];
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