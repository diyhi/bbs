<!-- 用户查看 -->
<template id="userShow-template">
	<div>
		<div class="main">
			<div class="navbar">
				<!-- 返回上级节点 -->
				<el-button type="primary" plain size="small"  @click="$router.push({path: sourceUrlObject.path, query:sourceUrlObject.query})">返回</el-button>
				<el-button type="primary" plain size="small"  @click="$router.push({path: '/admin/control/user/manage/edit', query:{ id : $route.query.id,beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">修改</el-button>
				
				<el-button type="primary" plain size="small"  @click="$router.push({path: '/admin/control/user/manage/allTopic', query:{ id : $route.query.id,userName : encodeURIComponent(user.userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">发表的话题</el-button>
				<el-button type="primary" plain size="small"  @click="$router.push({path: '/admin/control/user/manage/allComment', query:{ id : $route.query.id,userName : encodeURIComponent(user.userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">发表的评论</el-button>
				<el-button type="primary" plain size="small"  @click="$router.push({path: '/admin/control/user/manage/allReply', query:{ id : $route.query.id,userName : encodeURIComponent(user.userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">发表的回复</el-button>
				
				<el-button type="primary" plain size="small"  @click="$router.push({path: '/admin/control/user/manage/allQuestion', query:{ id : $route.query.id,userName : encodeURIComponent(user.userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">提交的问题</el-button>
				<el-button type="primary" plain size="small"  @click="$router.push({path: '/admin/control/user/manage/allAnswer', query:{ id : $route.query.id,userName : encodeURIComponent(user.userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">提交的答案</el-button>
				<el-button type="primary" plain size="small"  @click="$router.push({path: '/admin/control/user/manage/allAnswerReply', query:{id : $route.query.id,userName : encodeURIComponent(user.userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">提交的答案回复</el-button>
				
				
				<el-button type="primary" plain size="small"  @click="$router.push({path: '/admin/control/privateMessage/manage/privateMessageList', query:{ id : $route.query.id,userName : encodeURIComponent(user.userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">私信</el-button>
				<el-button type="primary" plain size="small"  @click="$router.push({path: '/admin/control/systemNotify/manage/subscriptionSystemNotifyList', query:{ id : $route.query.id,userName : encodeURIComponent(user.userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">系统通知</el-button>
				<el-button type="primary" plain size="small"  @click="$router.push({path: '/admin/control/remind/manage/remindList', query:{ id : $route.query.id,userName : encodeURIComponent(user.userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">提醒</el-button>
				<el-button type="primary" plain size="small"  @click="$router.push({path: '/admin/control/favorite/list', query:{ id : $route.query.id,userName : encodeURIComponent(user.userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">收藏夹</el-button>
				<el-button type="primary" plain size="small"  @click="$router.push({path: '/admin/control/like/list', query:{ id : $route.query.id,userName : encodeURIComponent(user.userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">点赞</el-button>
				<el-button type="primary" plain size="small"  @click="$router.push({path: '/admin/control/follow/list', query:{ id : $route.query.id,userName : encodeURIComponent(user.userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">关注</el-button>
				<el-button type="primary" plain size="small"  @click="$router.push({path: '/admin/control/follower/list', query:{ id : $route.query.id,userName : encodeURIComponent(user.userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">粉丝</el-button>
				<el-button type="primary" plain size="small"  @click="$router.push({path: '/admin/control/membershipCard/manage/membershipCardOrderList', query:{ id : $route.query.id,userName : encodeURIComponent(user.userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">会员卡订单</el-button>
				<el-button type="primary" plain size="small"  @click="$router.push({path: '/admin/control/redEnvelope/giveRedEnvelope/list', query:{ id : $route.query.id,userName : encodeURIComponent(user.userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">发红包</el-button>
				<el-button type="primary" plain size="small"  @click="$router.push({path: '/admin/control/redEnvelope/receiveRedEnvelope/list', query:{ id : $route.query.id,userName : encodeURIComponent(user.userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">收红包</el-button>	
				<el-button type="primary" plain size="small"  @click="$router.push({path: '/admin/control/pointLog/list', query:{ id : $route.query.id,userName : encodeURIComponent(user.userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">积分日志</el-button>
				<el-button type="primary" plain size="small"  @click="$router.push({path: '/admin/control/paymentLog/list', query:{ id : $route.query.id,userName : encodeURIComponent(user.userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">支付日志</el-button>
				<el-button type="primary" plain size="small"  @click="rechargeUI()">充值</el-button>
				<el-button type="primary" plain size="small"  @click="$router.push({path: '/admin/control/userReport/list', query:{ id : $route.query.id,userName : encodeURIComponent(user.userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">举报</el-button>
            
				<el-button type="primary" plain size="small"  @click="$router.push({path: '/admin/control/userLoginLog/list', query:{ id : $route.query.id,userName : encodeURIComponent(user.userName),beforeUrl:($route.query.beforeUrl != undefined ? $route.query.beforeUrl:'')}})">登录日志</el-button>
				<el-button type="primary" plain size="small"  @click="changeAvatarUI()">更换头像</el-button>
				<el-button type="primary" plain size="small"  @click="cancelAccount()">注销账号</el-button>
			</div>
			
			
			<div class="rechargeModule">
				<el-dialog title="充值" width="80%" v-model="popup_recharge" @close="closeRechargeWindow">
					
					<div class="data-form" >
						<el-form label-width="130px"  @submit.native.prevent>
							<el-form-item label="充值方式">
								<el-radio-group v-model="mode" >
								  	<el-radio-button :label="1">支付流水号充值</el-radio-button>
								    <el-radio-button :label="2">增减预存款</el-radio-button>
								    <el-radio-button :label="3">增减积分</el-radio-button>
								</el-radio-group>
							</el-form-item>
							
							<el-form-item label="流水号支付金额" :required="true" :error="error.paymentRunningNumberAmount" v-if="mode==1">
								<el-row>
									<el-col :span="10">
										<el-input v-model.trim="paymentRunningNumberAmount" maxlength="10" clearable="true" show-word-limit></el-input>
									</el-col>
								</el-row>
							</el-form-item>
							<el-form-item label="流水号" :required="true" :error="error.paymentRunningNumber" v-if="mode==1">
								<el-row :gutter="10">
									<el-col :span="18">
										<el-input v-model.trim="paymentRunningNumber" maxlength="40" clearable="true" show-word-limit></el-input>
									</el-col>
									<el-col :span="6">
										<el-button icon="el-icon-search" @click="paymentVerificationLogUI">流水号</el-button>
									</el-col>
								</el-row>
								<div class="form-help" >流水号有效期为发起支付7天内</div>
							</el-form-item>
							<el-form-item label="备注" v-if="mode==1">
								<el-input v-model="paymentRunningNumber_remark" type="textarea" :autosize="{ minRows: 2, maxRows: 4}" ></el-input>
							</el-form-item>
							
							<el-form-item label="预存款" :required="true" :error="error.deposit" v-if="mode==2">
								<el-row :gutter="10">
									<el-col :span="3">
										<el-select v-model="deposit_symbol" >
											<el-option v-for="item in deposit_symbol_options" :key="item.value" :label="item.label" :value="item.value"></el-option>
										</el-select>
									</el-col>
									<el-col :span="8">
										<el-input v-model.trim="deposit" maxlength="10" clearable="true" show-word-limit></el-input>
									</el-col>
								</el-row>
							</el-form-item>
							<el-form-item label="备注" v-if="mode==2">
								<el-input v-model="deposit_remark" type="textarea" :autosize="{ minRows: 2, maxRows: 4}" ></el-input>
							</el-form-item>
							
							<el-form-item label="积分" :required="true" :error="error.point" v-if="mode==3">
								<el-row :gutter="10">
									<el-col :span="3">
										<el-select v-model="point_symbol" >
											<el-option v-for="item in point_symbol_options" :key="item.value" :label="item.label" :value="item.value"></el-option>
										</el-select>
									</el-col>
									<el-col :span="8">
										<el-input v-model.trim="point" maxlength="10" clearable="true" show-word-limit></el-input>
									</el-col>
								</el-row>
							</el-form-item>
							<el-form-item label="备注" v-if="mode==3">
								<el-input v-model="point_remark" type="textarea" :autosize="{ minRows: 2, maxRows: 4}" ></el-input>
							</el-form-item>
		
							<el-form-item>
							    <el-button type="primary" class="submitButton"  @click="submitForm" :disabled="submitForm_disabled">提交</el-button>
							</el-form-item> 
						</el-form>
					</div>
					
				</el-dialog>
				
				<div class="data-form">
					<el-dialog title="流水号列表" width="70%"  v-model="popup_paymentVerificationLog" >
						<div class="dialog-data-table" >
							<el-table :data="tableData" @cell-click="cellExpandRow" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
								<el-table-column label="选择" align="right" width="50">
									<template #default="scope">
										<el-radio v-model="paymentRunningNumber" :label="paymentVerificationLogIdList[scope.$index]" >&nbsp;</el-radio>
							    	</template>
								</el-table-column>
								<el-table-column prop="id" label="流水号" align="center" ></el-table-column>
								<el-table-column prop="paymentAmount" label="支付金额" align="center" width="120"></el-table-column>
								<el-table-column prop="times" label="时间" align="center" width="180"></el-table-column>
							</el-table>
							<div class="pagination-wrapper" v-if="isShowPage">
								<el-pagination background  @current-change="page" :current-page="currentpage"  :page-size="maxresult" layout="total, prev, pager, next,jumper" :total="totalrecord"></el-pagination>
							</div>
						</div>
					</el-dialog>
				</div>
			</div>
			
			<div class="changeAvatarModule">
				<el-dialog title="更换头像" width="630px" v-model="popup_changeAvatar" @close="closeChangeAvatarWindow">
					<div class="original-box">
						<img ref="originalImage" src="data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7" alt="原图"/>
					</div>
					<div class="preview-pane-square">
						<div class="preview-square"></div> 
				    </div>
				    <div class="preview-pane-round">
						<div class="preview-round"></div> 
				    </div>
			        <div class="bottomInfo">
			          <div class="button-box">
			                <i id="progressBar" class="progressBar"></i>
			               	<div class="container">               
								<div class="progress">
									<el-progress :text-inside="true" :stroke-width="20" :percentage="progressPercent" />
								</div>
								<div>
									<el-upload ref="selectImage" action="#" :auto-upload="false" :show-file-list="false" :on-change="handleChange" :accept="'image/*'">
										<el-button type="primary" class="selectImage" plain >选择图片</el-button>
									</el-upload>
								</div>
								<div>
									<el-button type="primary" class="uploadImage" @click="uploadAvatar" :disabled="submitForm_disabled">上传</el-button>
								</div>
							</div>
			            </div>
			        </div>
				</el-dialog>
			</div>
			
			
			<div class="data-view" >
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">账号：</div></el-col>
					<el-col :span="20"><div class="content">{{user.account}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">呢称：</div></el-col>
					<el-col :span="10"><div class="content">{{user.nickname}}</div></el-col>
					<el-col :span="10">
						
						<el-image class="avatar" v-if="user.avatarName != null && user.avatarName != ''" style="width: 120px; height: 120px;" fit="contain" :src="user.avatarPath + user.avatarName+'?version='+user.userVersion" :preview-src-list="[user.avatarPath+user.avatarName+'?version='+user.userVersion]" hide-on-click-modal ></el-image>
						
					
					</el-col>
				</el-row>
				<el-row :gutter="10" type="flex" v-if="user.cancelAccountTime != '-1'">
					<el-col :span="4"><div class="name">注销账号时间：</div></el-col>
					<el-col :span="20"><div class="content remind">{{user.cancelAccountTime}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">注册时间：</div></el-col>
					<el-col :span="20"><div class="content">{{user.registrationDate}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex" v-if="user.type == 20">
					<el-col :span="4"><div class="name">手机号：</div></el-col>
					<el-col :span="20"><div class="content">{{user.mobile}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">类型：</div></el-col>
					<el-col :span="20">
						<div class="content">
							<span v-if="user.type == 10">账号密码用户</span>
							<span v-if="user.type == 20">手机用户</span>
							<span v-if="user.type == 30">邮箱用户</span>
							<span v-if="user.type == 40">微信用户</span>
							<span v-if="user.type == 80">其他用户</span>
						</div>
					</el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">等级：</div></el-col>
					<el-col :span="20"><div class="content">{{user.gradeName}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">Email地址：</div></el-col>
					<el-col :span="20"><div class="content">{{user.email}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">密码提示问题：</div></el-col>
					<el-col :span="20"><div class="content">{{user.issue}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">状态：</div></el-col>
					<el-col :span="20">
						<div class="content">
							<span v-if="user.state == 1">启用</span>
							<span v-if="user.state == 2">停用</span>
							<span v-if="user.state == 11">启用时删除</span>
							<span v-if="user.state == 12">停用时删除</span>
						</div>
					</el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">预存款：</div></el-col>
					<el-col :span="20"><div class="content">{{user.deposit}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">积分：</div></el-col>
					<el-col :span="20"><div class="content">{{user.point}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">备注：</div></el-col>
					<el-col :span="20"><div class="content">{{user.remarks}}</div></el-col>
				</el-row>
				
				<div v-for="(userCustom,index) in userCustomList">
					<el-row :gutter="10" type="flex" v-if="userCustom.chooseType ==1">
						<el-col :span="4"><div class="name">{{userCustom.name}}：</div></el-col>
						<el-col :span="20">
							<div class="content">
								<span v-for="(userInputValue) in userCustom.userInputValueList">{{userInputValue.content}}</span>
							</div>
						</el-col>
					</el-row>
					<el-row :gutter="10" type="flex" v-if="userCustom.chooseType ==2">
						<el-col :span="4"><div class="name">{{userCustom.name}}：</div></el-col>
						<el-col :span="20">
							<div class="content">
								<span v-for="(value, key) in userCustom.itemValue" ><!-- v-if="key==userInputValue.options" -->
									<span v-for="userInputValue in userCustom.userInputValueList" >	
										<span class="blue-tag" v-if="key==userInputValue.options">{{value}}</span>
									</span>
								</span>
							</div>
						</el-col>
					</el-row>
					<el-row :gutter="10" type="flex" v-if="userCustom.chooseType ==3">
						<el-col :span="4"><div class="name">{{userCustom.name}}：</div></el-col>
						<el-col :span="20">
							<div class="content">
								<span v-for="(value, key) in userCustom.itemValue" ><!-- v-if="key==userInputValue.options" -->
									<span v-for="userInputValue in userCustom.userInputValueList" >	
										<span class="blue-tag"  v-if="key==userInputValue.options">{{value}}</span>
									</span>
								</span>
							</div>
						</el-col>
					</el-row>
					<el-row :gutter="10" type="flex" v-if="userCustom.chooseType ==4">
						<el-col :span="4"><div class="name">{{userCustom.name}}：</div></el-col>
						<el-col :span="20">
							<div class="content">
								<span v-for="(value, key) in userCustom.itemValue" ><!-- v-if="key==userInputValue.options" -->
									<span v-for="userInputValue in userCustom.userInputValueList" >	
										<span class="blue-tag" v-if="key==userInputValue.options">{{value}}</span>
									</span>
								</span>
							</div>
						</el-col>
					</el-row>
					<el-row :gutter="10" type="flex" v-if="userCustom.chooseType ==5">
						<el-col :span="4"><div class="name">{{userCustom.name}}：</div></el-col>
						<el-col :span="20">
							<div class="content">
								<span v-for="(userInputValue) in userCustom.userInputValueList">{{userInputValue.content}}</span>
							</div>
						</el-col>
					</el-row>
				</div>
				
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">角色：</div></el-col>
					<el-col :span="20">
						<el-table ref="table" :data="userRoleList" tooltip-effect="dark" :row-class-name="tableRowClassName" :show-header="false" stripe style="width: 100%" empty-text="没有角色">
							<el-table-column label="角色名称" >
								<template #default="scope">	
									{{scope.row.name}}
									<span class="form-help" style="margin-left: 6px" v-if="scope.row.defaultRole">(默认角色)</span>
									
						    	</template>
							</el-table-column>
							<el-table-column label="有效期" >
								<template #default="scope">	
									<span v-if="!scope.row.defaultRole">{{scope.row.validPeriodEnd}} 到期</span>
						    	</template>
							</el-table-column>
						</el-table>
					</el-col>
				</el-row>
			
				
			</div>
		</div>
	</div>
</template>

<script>
//用户查看
export default({
	name: 'userShow',//组件名称，keep-alive缓存需要本参数
	template : '#userShow-template',
	inject:['reload'], 
	data : function data() {
		return {
			
			popup_recharge:false,//是否弹出充值窗口
			mode:2,//充值方式
			paymentRunningNumberAmount:'',//支付流水号充值--支付金额
			paymentRunningNumber:'',//支付流水号充值--流水号
			paymentRunningNumber_remark:'',//支付流水号充值--备注
			
			deposit_symbol:'+',//增减预存款--增减金额
			deposit:'',//增减预存款--预存款
			deposit_remark:'',//增减预存款--备注
			deposit_symbol_options: [{
				value:  '+',
				label: '增加'
			}, {
				value:  '-',
				label: '减少'
        	}],

        	point_symbol:'+',//增减积分--增减积分
        	point:'',//增减积分--积分
        	point_remark:'',//增减积分--备注
        	point_symbol_options: [{
				value:  '+',
				label: '增加'
			}, {
				value:  '-',
				label: '减少'
        	}],
			
			
			
			popup_paymentVerificationLog:false,//是否弹出支付校验日志窗口
			
			paymentVerificationLogIdList: [],
			tableData: [],//表格内容
			totalrecord : 0, //总记录数
		    currentpage : 1, //当前页码
			totalpage : 1, //总页数
			maxresult: 12, //每页显示记录数
			isShowPage:false,//是否显示分页 maxresult没返回结果前就显示会导致分页栏显示页码错误
			
			
			popup_changeAvatar:false,//是否弹出更换头像窗口
			avatarCropper:'',//头像Cropper
			progressPercent: 0, // 进度条默认为0
			
			error:{
				paymentRunningNumberAmount:'',
				paymentRunningNumber:'',
				paymentRunningNumber_remark:'',//支付流水号充值--备注
				deposit_symbol:'',//增减预存款--增减金额
				deposit:'',//增减预存款--预存款
				deposit_remark:'',//增减预存款--备注
				point_symbol:'',//增减积分--增减积分
	        	point:'',//增减积分--积分
	        	point_remark:'',//增减积分--备注
			},
			
			
			user:'',
			userRoleList:[],
			userCustomList:[],
			
			sourceUrlObject:{},//来源URL对象
		};
	},
	beforeRouteEnter (to, from, next) {
		//上级路由编码
		if(to.query.beforeUrl == undefined || to.query.beforeUrl==''){//前一个URL
			let parameterObj = new Object;
			parameterObj.path = from.path;
			let query = from.query;
			for(let q in query){
				query[q] = encodeURIComponent(query[q]);
			}
			
			parameterObj.query = query;
			//将请求参数转为base62
			let encrypt = delete_base62_equals(base62_encode(JSON.stringify(parameterObj)));
			
			
			let newFullPath = updateURLParameter(to.fullPath,'beforeUrl',encrypt);
			
			to.fullPath = newFullPath;
			
			let paramGroup = to.query;
			paramGroup.beforeUrl = encrypt;
			to.query = paramGroup;
		}
		next();
	},
	created : function created() {
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		if(this.$route.query.id != undefined && this.$route.query.id != ''){
			this.id = this.$route.query.id;
		}
		
		//上级路由解码
		if(this.$route.query.beforeUrl != undefined && this.$route.query.beforeUrl != ''){
			let decrypt = base62_decode(add_base62_equals(this.$route.query.beforeUrl));
			
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
		
		
	//	let fullPath = this.$route.fullPath;
	//	console.log("来路",this.beforeUrl);
		
		/**
		if(fullPath != ""){
			//解析url参数
    		let paramObject = analyzeUrlParam(fullPath);
    		
    		
    		if (Object.keys(paramObject).length === 0) {//如果为空
    			this.$router.push({
    				path : _fullPath
    			});
    		}else{
    			//含有中文的值要解码
    			for(let param in paramObject){
    				paramObject[param] = decodeURIComponent(paramObject[param]);
    			}
    			
    			this.$router.push({
	    			path : _fullPath,
	    			query : paramObject
	    		});
    			
    			
    		}
			
		}
		**/
		
		
		
		
		
		
		
		
		//初始化
		this.queryUser();
	},
	methods : {
		//隐藏行
		tableRowClassName: function({ row, rowIndex }) {
			//if(!row.selected){
			//	return 'hidden-row';
			//}
      		return '';
    	},
	
		//查询用户
	    queryUser: function(){
	        let _self = this;
			
			_self.userRoleList.length = 0;
			
			_self.$ajax.get('control/user/manage', {
			    params: {
			    	method : 'show',
			    	id : _self.id,
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
			    			if(key == "userRoleList"){
			    				_self.userRoleList = mapData[key];
			    			}else if(key == "userCustomList"){
			    				_self.userCustomList = mapData[key];
			    			}else if(key == "user"){
			    				_self.user = mapData[key];
			    				
			    				if(_self.user.cancelAccountTime != "-1"){
			    					_self.user.cancelAccountTime = dayjs(parseInt(_self.user.cancelAccountTime)).format('YYYY-MM-DD HH:mm:ss');
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
		//删除标签
	    deleteTag : function(event,row) {
			//强制失去焦点
			let target = event.target;
			// 根据button组件内容 里面包括一个span标签，如果设置icon，则还包括一个i标签，其他情况请自行观察。
    		// 所以，在我们点击到button组件上的文字也就是span标签上时，直接执行e.target.blur()不会生效，所以要加一层判断。
        	if(target.nodeName == 'SPAN' || target.nodeName == 'I'){
            	target = event.target.parentNode;
       		}
        	target.blur();
			
			
	    	let _self = this;
	    	this.$confirm('此操作将删除该标签, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	formData.append('tagId', row.id);
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/tag/manage?method=delete',
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
				    		_self.$message.success("删除成功");
				    		_self.queryTagList();
				    	}else if(returnValue.code === 500){//错误
				    		
				    		let errorMap = returnValue.data;
				    		let htmlContent = "";
				    		let count = 0;
				    		for (let key in errorMap) {   
				    			count++;
				    			htmlContent += "<p>"+count + ". " + errorMap[key]+"</p>";
				    			
				    	    }
				    		_self.$alert(htmlContent, '错误', {
				    			showConfirmButton :false,
				    			dangerouslyUseHTMLString: true
				    		})
				    		.catch(function (error) {
								console.log(error);
							});
				    		
				    		
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
	    
	  	//充值UI
	    rechargeUI: function(){
	    	this.popup_recharge=true;
	    	
	    	this.paymentRunningNumberAmount='';//支付流水号充值--支付金额
	    	this.paymentRunningNumber='';//支付流水号充值--流水号
	    	this.paymentRunningNumber_remark='';//支付流水号充值--备注
	    	
	    	this.deposit_symbol='+';//增减预存款--增减金额
	    	this.deposit='';//增减预存款--预存款
	    	this.deposit_remark='';//增减预存款--备注
	    	
	    	this.point_symbol='+';//增减积分--增减积分
	    	this.point='';//增减积分--积分
	    	this.point_remark='';//增减积分--备注
	    	
	    },
	  	//关闭充值弹出框
 		closeRechargeWindow: function() { 
 			this.popup_recharge=false;
 	    },
 	    
 	 	//点击单元格选择
  		cellExpandRow : function(row,column,event,cell) {
  			if(column.label=="选择"){
		        this.paymentRunningNumber = row.id;
		        this.paymentRunningNumberAmount = row.paymentAmount;
		        this.popup_paymentVerificationLog=false;
  			}
  		},
 	 	//支付校验日志UI
	    paymentVerificationLogUI: function(){
	    	this.popup_paymentVerificationLog=true;//弹出支付校验日志窗口
	    	
	    	
	    	//清空数据
			this.totalrecord = 0;//服务器返回的long类型已转为String类型
   			this.currentpage = 1;
			this.totalpage = 1;//服务器返回的long类型已转为String类型
			this.maxresult = 12;
			this.isShowPage = false;//显示分页栏
			
			this.queryPaymentVerificationLogList(1);
	    },
	  	//关闭支付校验日志弹出框
 		closeRechargeWindow: function() { 
 			this.popup_paymentVerificationLog=false;//关闭弹出支付校验日志窗口
 	    },
		//分页
		page : function(page) {
			
			this.queryPaymentVerificationLogList(page);
		},
	    
	    
		//查询支付校验日志
	    queryPaymentVerificationLogList: function(page) {
	        let _self = this;
	        
	        _self.tableData = [];
	        _self.paymentVerificationLogIdList = [];
	        
			_self.$ajax.get('control/paymentLog/manage', {
			    params: {
			    	method : 'ajax_paymentVerificationLogPage',
			    	paymentModule : 5,
			    	parameterId :_self.user.id,
			    	userName : _self.user.userName,
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
			    			for(let i = 0; i<list.length; i++){
			    				let paymentVerificationLog = list[i];
			    				_self.paymentVerificationLogIdList.push(paymentVerificationLog.id);
			    			
			    			}
			    			
			    			
			    			_self.tableData = list;
			 
			    			_self.totalrecord = parseInt(pageView.totalrecord);//服务器返回的long类型已转为String类型
			    			_self.currentpage = pageView.currentpage;
							_self.totalpage = parseInt(pageView.totalpage);//服务器返回的long类型已转为String类型
							_self.maxresult = pageView.maxresult;
							_self.isShowPage = true;//显示分页栏
			    		}
			    	}else if(returnValue.code === 500){//错误
			    		
			    		
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
      			_self.error[key] = "";
      	    }
  			let formData = new FormData();
  			formData.append('id', _self.$route.query.id);
  			formData.append('type', _self.mode);
  			if(_self.mode==1){//支付流水号充值
  				formData.append('paymentRunningNumberAmount', _self.paymentRunningNumberAmount);
  				formData.append('paymentRunningNumber', _self.paymentRunningNumber);
  				formData.append('paymentRunningNumber_remark', _self.paymentRunningNumber_remark);		
  			}else if(_self.mode==2){//增减预存款
  				formData.append('deposit_symbol', _self.deposit_symbol);
  				formData.append('deposit', _self.deposit);
  				formData.append('deposit_remark', _self.deposit_remark);
  			}else if(_self.mode==3){//增减积分
  				formData.append('point_symbol', _self.point_symbol);
  				formData.append('point', _self.point);
  				formData.append('point_remark', _self.point_remark);
  				
  			}
  			
  			_self.$ajax({
  		        method: 'post',
  		        url: 'control/user/manage?method=payment&a=a',//a=a参数的作用是仅增加连接符&
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
  			    		_self.popup_recharge=false;
  			    		_self.queryUser();
  			    		
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
  			    _self.submitForm_disabled = false;
  			})
  			.catch(function (error) {
  				console.log(error);
  			});
  	    },
  	    
  		//更换头像UI
	    changeAvatarUI: function(){
	    	let _self = this;
	    	_self.popup_changeAvatar=true;
	    	_self.progressPercent= 0; // 进度条默认为0
	   
	    	if(this.avatarCropper != ""){
 				this.avatarCropper.destroy();//销毁裁剪器并从图像中删除实例。
 				this.avatarCropper = "";
 			}
	    	
	    	
	    	_self.$nextTick(() => {
	    		 _self.$refs.originalImage.src = "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7";//空白1像素图片
	    		
	    		_self.avatarCropper = new Cropper(_self.$refs.originalImage, {	
	    			viewMode: 1,//视图模式
			        dragMode: 'crop',//拖拽模式
			        initialAspectRatio: 1,//定义裁切框的初始宽高比。默认情况下，它与画布（图像包装器）的纵横比相同。这个值只有在aspectRatio值不进行设置的时候生效
			        aspectRatio: 1,//裁剪框的宽高比
			        preview:[ document.querySelector('.preview-square'),
		                        document.querySelector('.preview-round')],//添加额外的元素(容器)以供预览
			        background: true,//显示容器的网格背景
			        autoCropArea: 0.6,//定义自动裁剪面积大小(百分比)和图片进行对比。 就是裁剪框显示的大小
			        zoomOnWheel: false,//是否可以通过移动鼠标来放大图像
			        minContainerWidth:400,
	                minContainerHeight:400,
				});
    		});
	    },
	 	//关闭更换头像弹出框
 		closeChangeAvatarWindow: function() { 
 			this.popup_changeAvatar=false;
 			
 			
 	    },
 	    
 	 	//创建Cropper
 		createCropper: function() { 
 			this.avatarCropper = new Cropper(this.$refs.originalImage, {	
    			viewMode: 1,//视图模式
		        dragMode: 'crop',//拖拽模式
		        initialAspectRatio: 1,//定义裁切框的初始宽高比。默认情况下，它与画布（图像包装器）的纵横比相同。这个值只有在aspectRatio值不进行设置的时候生效
		        aspectRatio: 1,//裁剪框的宽高比
		        preview:[ document.querySelector('.preview-square'),
	                        document.querySelector('.preview-round')],//添加额外的元素(容器)以供预览
		        background: true,//显示容器的网格背景
		        autoCropArea: 0.6,//定义自动裁剪面积大小(百分比)和图片进行对比。 就是裁剪框显示的大小
		        zoomOnWheel: false,//是否可以通过移动鼠标来放大图像
		        
		       
		        minContainerWidth:400,
                minContainerHeight:400,
			});
 			
 	    },
 	  
 	    
 	    
 	    
 	 	//处理上传图片
 		handleChange(file, fileList) {
 	    	let _self = this;
 	    	if (fileList.length > 1) {
	      		fileList.splice(0, 1);
			}
 	    	var reader = new FileReader();
 	    	//readAsDataURL方法可以将File对象转化为data:URL格式的字符串（base64编码）
 	        reader.readAsDataURL(file.raw);
 	        reader.onload = (e)=>{
 	            let dataURL = reader.result;
 	            //将img的src改为刚上传的文件的转换格式
 	           	_self.$refs.originalImage.src = dataURL;  
 	          // _self.avatarCropper.reset();
 	          
 	          
 	           	if(_self.avatarCropper != ""){
 	           		_self.avatarCropper.destroy();//销毁裁剪器并从图像中删除实例。
 	           		_self.avatarCropper = "";
 	 			}
 	          
 	          	_self.$nextTick(() => {
 	          		_self.createCropper();
 	        	});
 	        }

     	},
    	//上传头像
  		uploadAvatar : function() {
  			let _self = this;
  			
  			_self.submitForm_disabled = true;
  			
  			
  			if(_self.avatarCropper == "" || _self.$refs.originalImage.src == "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7"){
  				_self.$message({
		            showClose: true,
		            message: "请先选择图片",
		            type: 'error'
		        });
  				_self.submitForm_disabled = false;
 				return;
 			}
  			
  			let formData = new FormData();
  			
  			
  			let fileList = this.$refs.selectImage.uploadFiles;
  			if(fileList != null && fileList.length >0){
  				 formData.append('file', fileList[0].raw);
  			}
  			
  			// - x裁切框距离左边的距离 
  			// - y裁切框距离顶部的距离 
  			// - width裁切框的宽度 
  			// - height裁切框的高度 
  			// - rotate裁切框的旋转的角度 
  			// - scaleX缩放图像的横坐标 
  			// - scaleY缩放图像的纵坐标 
  			let dataObject = _self.avatarCropper.getData(true);//返回裁剪区域基于原图片!原尺寸!的位置和尺寸 rounded默认为false 表示是否显示四舍五入后的数据
  			formData.append('width', dataObject.width);
  			formData.append('height', dataObject.height);
  			formData.append('x', dataObject.x); 
  			formData.append('y', dataObject.y);
  			
  			
             
            formData.append('id', _self.$route.query.id);
             
            _self.$ajax({
   		        method: 'post',
   		        url: 'control/user/manage?method=updateAvatar',
   		        data: formData,
   		        timeout: 0,// 定义请求超时时间
   		     	onUploadProgress: progressEvent => {
		             if (progressEvent.lengthComputable) {
                    	let rate = progressEvent.loaded / progressEvent.total;  //已上传的比例
                        if (rate < 1) {
                        	//这里的进度只能表明文件已经上传到后台，但是后台有没有处理完还不知道
                            //因此不能直接显示为100%，不然用户会误以为已经上传完毕，关掉浏览器的话就可能导致上传失败
                            //等响应回来时，再将进度设为100%
                            // progressEvent.loaded:已上传文件大小
		            		// progressEvent.total:被上传文件的总大小
                            _self.progressPercent = (progressEvent.loaded / progressEvent.total * 100).toFixed(2);
                        }
                    }
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
   			    		_self.progressPercent = 100;
   			    		_self.$message.success("提交成功");
   			    		
   			    		//删除缓存
   			    		_self.$store.commit('setCacheNumber');
   			    		_self.popup_changeAvatar=false;
   			    		_self.queryUser();
   			    		
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
   			    _self.submitForm_disabled = false;
   			})
   			.catch(function (error) {
   				console.log(error);
   			});
  			
  			
  			/**
  			//getCroppedCanvas方法可以将裁剪区域的数据转换成canvas数据
           _self.avatarCropper.getCroppedCanvas({
                  maxWidth: 200,//输出Canvas的最大宽度；默认值是Infinity（无穷大）
                  maxHeight: 200,//输出Canvas的最大高度；默认值是Infinity（无穷大）
                  fillColor: '#fff',//在输出画布Canvas中填充任何alpha的颜色，默认值是透明的。  如果您打算从输出画布canvas中获得一个JPEG图像，您应该首先设置填色选项，否则，JPEG图像中的透明部分将在缺少情况下变为黑色。
                  imageSmoothingEnabled: true,//图片是否是光滑
                  imageSmoothingQuality: 'high',//图片的质量 默认low 还有medium、high
            }).toBlob((blob) => {//调用浏览器原生的toBlob方法将canvas数据转换成blob数据
            	
                 // 第三个参数为文件名，可选填. 'file', blob, 'example.jpg'
                 formData.append('file', blob);
                
                 
                 formData.append('id', _self.$route.query.id);
                 
                 _self.$ajax({
       		        method: 'post',
       		        url: 'control/user/manage?method=updateAvatar',
       		        data: formData,
       		        timeout: 0,// 定义请求超时时间
	       		    onUploadProgress: progressEvent => {
	 		            if (progressEvent.lengthComputable) {
                    		let rate = progressEvent.loaded / progressEvent.total;  //已上传的比例
                        	if (rate < 1) {
                        		//这里的进度只能表明文件已经上传到后台，但是后台有没有处理完还不知道
                            	//因此不能直接显示为100%，不然用户会误以为已经上传完毕，关掉浏览器的话就可能导致上传失败
                            	//等响应回来时，再将进度设为100%
                            	// progressEvent.loaded:已上传文件大小
		            			// progressEvent.total:被上传文件的总大小
                            	_self.progressPercent = (progressEvent.loaded / progressEvent.total * 100).toFixed(2);
                        	}
                    	}
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
       			    		_self.progressPercent = 100;
       			    		_self.$message.success("提交成功");
       			    		
       			    		//删除缓存
       			    		_self.$store.commit('setCacheNumber');
       			    		_self.popup_changeAvatar=false;
       			    		_self.queryUser();
       			    		
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
       			    _self.submitForm_disabled = false;
       			})
       			.catch(function (error) {
       				console.log(error);
       			});
            }, 'image/jpeg', 1);
  			**/
  			
  			
  			
  			
  			
  	    },
 	 	
	    //注销账号
	    cancelAccount: function(){
	    	let _self = this;
	    	this.$confirm('此操作将注销该账号, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	formData.append('id', _self.user.id);
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/user/manage?method=cancelAccount',
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
				    		_self.$message.success("注销账号成功");
				    		_self.queryUser();
				    	}else if(returnValue.code === 500){//错误
				    		
				    		let errorMap = returnValue.data;
				    		let htmlContent = "";
				    		let count = 0;
				    		for (let key in errorMap) {   
				    			count++;
				    			htmlContent += "<p>"+count + ". " + errorMap[key]+"</p>";
				    			
				    	    }
				    		_self.$alert(htmlContent, '错误', {
				    			showConfirmButton :false,
				    			dangerouslyUseHTMLString: true
				    		})
				    		.catch(function (error) {
								console.log(error);
							});
				    		
				    		
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
	    
  	    
	}
});
</script>