<!-- 添加用户 -->
<template id="addUser-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/user/list', query:{ visible : ($route.query.visible != undefined ? $route.query.visible:''),page:($route.query.page != undefined ? $route.query.page:'')}})">返回</el-button>
			</div>
			<div class="data-form label-width-blank userModule" >
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="用户类型" :required="true" :error="error.type">
						<el-radio-group v-model="type">
						    <el-radio :label="10">本地账号密码用户</el-radio>
						    <el-radio :label="20">手机用户</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="账号" :required="true" :error="error.account" v-if="type == 10">
						<el-row><el-col :span="12"><el-input v-model.trim="account" maxlength="30" clearable="true" show-word-limit></el-input></el-col></el-row>
						<div class="form-help" >会员用户名只能输入由数字、26个英文字母或者下划线组成</div>
					</el-form-item>
					<el-form-item label="手机" :required="type == 10? false :true" :error="error.mobile" >
						<el-row><el-col :span="12"><el-input v-model.trim="mobile" maxlength="15" clearable="true" show-word-limit></el-input></el-col></el-row>
						<div class="form-help" >国内手机号：无区号的手机号码，例如 1390000****；国际/港澳台手机号格式：国际区号+号码，例如 +852000012****</div>
					</el-form-item>
					<el-form-item label="呢称" :error="error.nickname" >
						<el-row><el-col :span="12"><el-input v-model.trim="nickname" maxlength="30" clearable="true" show-word-limit></el-input></el-col></el-row>
					</el-form-item>
					<el-form-item label="密码" :required="true" :error="error.password" >
						<el-row><el-col :span="12"><el-input v-model.trim="password" type="password" maxlength="30" clearable="true" ></el-input></el-col></el-row>
					</el-form-item>
					<el-form-item label="Email地址" :error="error.email" >
						<el-row><el-col :span="12"><el-input v-model.trim="email" maxlength="50" clearable="true" show-word-limit></el-input></el-col></el-row>
					</el-form-item>
					<el-form-item label="密码提示问题" :required="true" :error="error.issue" v-if="type == 10">
						<el-row><el-col :span="12"><el-input v-model.trim="issue" maxlength="40" clearable="true" show-word-limit></el-input></el-col></el-row>
					</el-form-item>
					<el-form-item label="密码提示答案" :required="true" :error="error.answer" v-if="type == 10">
						<el-row><el-col :span="12"><el-input v-model.trim="answer" maxlength="40" clearable="true" show-word-limit></el-input></el-col></el-row>
					</el-form-item>
					<el-form-item label="实名认证" :required="true" :error="error.realNameAuthentication">
						<el-switch v-model="realNameAuthentication" ></el-switch>
					</el-form-item>
					<el-form-item label="用户状态" :required="true" :error="error.state">
						<el-radio-group v-model="state">
						    <el-radio :label="1">启用</el-radio>
						    <el-radio :label="2">停用</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="显示用户动态" :required="true" :error="error.allowUserDynamic">
						<el-switch v-model="allowUserDynamic" ></el-switch>
					</el-form-item>
					
					
					<div v-for="(userCustom,index) in userCustomList">
						<el-form-item :label="userCustom.name" :required="userCustom.required" :error="error.userCustom.get('userCustom_'+userCustom.id)" v-if="userCustom.chooseType ==1">
							<el-input v-model.trim="userBoundField[index]"  clearable="true" :maxlength="userCustom.maxlength"></el-input>
							<div class="form-help" >{{userCustom.tip}}</div>
						</el-form-item>
						<el-form-item :label="userCustom.name" :required="userCustom.required" :error="error.userCustom.get('userCustom_'+userCustom.id)" v-if="userCustom.chooseType ==2">
							<el-radio-group v-model="userBoundField[index]">
							    <el-radio :label="key" v-for="(value, key) in userCustom.itemValue" :key="key">{{value}}</el-radio>
							</el-radio-group>
							<div class="form-help" >{{userCustom.tip}}</div>
						</el-form-item>
						<el-form-item :label="userCustom.name" :required="userCustom.required" :error="error.userCustom.get('userCustom_'+userCustom.id)" v-if="userCustom.chooseType ==3">
							<el-checkbox-group v-model="userBoundField[index]">
							    <el-checkbox :label="key" v-for="(value, key) in userCustom.itemValue" :key="key">{{value}}</el-checkbox>
							</el-checkbox-group>
							<div class="form-help" >{{userCustom.tip}}</div>
						</el-form-item>
						<el-form-item :label="userCustom.name" :required="userCustom.required" :error="error.userCustom.get('userCustom_'+userCustom.id)" v-if="userCustom.chooseType ==4">
							<el-select v-model="userBoundField[index]" :multiple="userCustom.multiple" placeholder="请选择">
								<el-option v-for="(value, key) in userCustom.itemValue" :key="key" :label="value" :value="key"></el-option>
							</el-select>
							<div class="form-help" >{{userCustom.tip}}</div>
						</el-form-item>
						<el-form-item :label="userCustom.name" :required="userCustom.required" :error="error.userCustom.get('userCustom_'+userCustom.id)" v-if="userCustom.chooseType ==5">
							<el-input type="textarea" v-model="userBoundField[index]" :rows="userCustom.rows" clearable="true" :maxlength="userCustom.maxlength"></el-input>
							<div class="form-help" >{{userCustom.tip}}</div>
						</el-form-item>
					</div>
					
					<el-form-item label="用户角色" >
						<el-table ref="table" :data="userRoleList" @selection-change="handleSelectionChange" tooltip-effect="dark" :show-header="false" stripe style="width: 100%" empty-text="没有角色">
							<el-table-column type="selection" :selectable="checkSelectable"></el-table-column>
							<el-table-column label="角色名称" >
								<template #default="scope">	
									{{scope.row.name}}
									<span class="form-help" style="margin-left: 6px" v-if="scope.row.defaultRole">(默认角色)</span>
									
						    	</template>
							</el-table-column>
							<el-table-column label="有效期" >
								<template #default="scope">	
									
									<el-tooltip content="有效期" placement="top" v-if="!scope.row.defaultRole">
										<el-form-item :error="error.validPeriodEnd.get('validPeriodEnd_'+scope.row.id)">
											<el-date-picker v-model="validPeriodEnd_format[scope.$index]" type="datetime" format="YYYY-MM-DD HH:mm" placeholder="有效期" ></el-date-picker>
										</el-form-item>
									</el-tooltip>
									
						    	</template>
							</el-table-column>
						</el-table>
					</el-form-item>
					
					
					
					
					
					
					<el-form-item label="备注"  :error="error.remarks"  >
						<el-input type="textarea" v-model="remarks" :autosize="{minRows: 3}" placeholder="请输入内容" ></el-input>						
					</el-form-item>
					
					
					
					
					<el-form-item >
					    <el-button type="primary" class="submitButton" @click="submitForm" :disabled="submitForm_disabled">提交</el-button>
					</el-form-item>
				</el-form>
				
			</div>
		</div>
	</div>
</template>

<script>
//添加用户
export default({
	name: 'addUser',//组件名称，keep-alive缓存需要本参数
	template : '#addUser-template',
	inject:['reload'], 
	data : function data() {
		return {
			type : 10,
			account :'',
			mobile :'',
			nickname :'',
			password :'',
			email :'',
			issue :'',
			answer :'',
			realNameAuthentication : false,
			state :1,
			allowUserDynamic :true,
			remarks :'',
			validPeriodEnd_format:[],
			userBoundField : [], //用户自定义注册功能项绑定

			error : {
				type :'',
				account :'',
				mobile :'',
				nickname :'',
				password :'',
				email :'',
				issue :'',
				answer :'',
				realNameAuthentication :'',
				state :'',
				allowUserDynamic :'',
				remarks :'',
				validPeriodEnd:new Map(),
				userCustom:new Map(),
			},
			
			multipleSelection: [],
			userRoleList:[],
			userCustomList:[],
			
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	created : function created() {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		
		this.queryUser();
	},
	methods : {
		 //查询用户
	    queryUser: function(){
	        let _self = this;
			
			_self.userRoleList.length = 0;
			
			_self.$ajax.get('control/user/manage', {
			    params: {
			    	method : 'add',
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
			    				
			    				if(_self.userRoleList != null && _self.userRoleList.length >0){
			    					for(let i=0; i<_self.userRoleList.length; i++){
			    						_self.validPeriodEnd_format.push(_self.userRoleList[i].validPeriodEnd);
			    						if(_self.userRoleList[i].defaultRole|| _self.userRoleList[i].selected){
			    							_self.$nextTick(function () {
			    								_self.$refs.table.toggleRowSelection(_self.userRoleList[i],true);
			    							});	
			    						}
			    					}
			    				}
			    			}else if(key == "userCustomList"){
			    				_self.userCustomList = mapData[key];
								if (_self.userCustomList != null && _self.userCustomList.length > 0) {
									for (let i = 0; i < _self.userCustomList.length; i++) {
										let userCustom = _self.userCustomList[i];
										_self.boundField(userCustom);
									}
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
		//默认角色不允许选择
		checkSelectable: function(row, index) {
			return !row.defaultRole;
		},
		//处理多选
	    handleSelectionChange: function(val) {
	        this.multipleSelection = val;
	    },
	    
	  	//绑定字段
  		boundField : function(userCustom) {
  			if (userCustom.chooseType == 1) { //文本框
  				let content = "";
  				for (let i = 0; i < userCustom.userInputValueList.length; i++) {
  					content = userCustom.userInputValueList[i].content;
  				}
  				this.userBoundField.push(content);
  			}else if (userCustom.chooseType == 2) { //单选框
  				let checked = "";

  				for (let i = 0; i < userCustom.userInputValueList.length; i++) {
  					let userInputValue = userCustom.userInputValueList[i];
  					checked = userInputValue.options;

  				}
  				//默认选第一项 
  				if (checked == "") {
  					for (let itemValue in userCustom.itemValue) {
  						checked = itemValue;
  						break;
  					}
  				}
  				this.userBoundField.push(checked);
  			} else if (userCustom.chooseType == 3) { //多选框
  				let checked = new Array();

  				for (let i = 0; i < userCustom.userInputValueList.length; i++) {
  					let userInputValue = userCustom.userInputValueList[i];
  					checked.push(userInputValue.options);
  				}
  				this.userBoundField.push(checked);
  			}else if (userCustom.chooseType == 4) { //下拉列表
  				if (userCustom.multiple == true) { //允许多选
  					let checked = new Array();

  					A:for (let itemValue in userCustom.itemValue) {
  						for (let i = 0; i < userCustom.userInputValueList.length; i++) {
  							let userInputValue = userCustom.userInputValueList[i];
  							if (itemValue == userInputValue.options) {
  								checked.push(itemValue);
  								continue A;
  							}
  						}
  					}
  					this.userBoundField.push(checked);

  				} else {
  					
  					let checked = "";

  					A:for (let itemValue in userCustom.itemValue) {
  						for (let i = 0; i < userCustom.userInputValueList.length; i++) {
  							let userInputValue = userCustom.userInputValueList[i];
  							if (itemValue == userInputValue.options) {
  								checked = itemValue;
  								break A;
  							}
  						}
  					}
  					this.userBoundField.push(checked);
  				}


  			} else if (userCustom.chooseType == 5) { //文本域
  				let content = "";
  				for (let i = 0; i < userCustom.userInputValueList.length; i++) {
  					content = userCustom.userInputValueList[i].content;
  				}
  				this.userBoundField.push(content);
  			}
  			
  		
  		},
	    
	    
		//提交表单
		submitForm : function() {
			let _self = this;
			_self.submitForm_disabled = true;
			
	        //清除错误
			for (let key in _self.error) { 
    			if(getType(_self.error[key]) == 'map'){
    				_self.error[key].clear();
    			}else{
    				_self.error[key] = "";
    			}
    	    }
			let formData = new FormData();
			
			formData.append('type', _self.type);
			formData.append('account', _self.account);
			formData.append('mobile', _self.mobile);
			formData.append('nickname', _self.nickname);
			formData.append('password', _self.password);
			formData.append('email', _self.email);
			formData.append('issue', _self.issue);
			formData.append('answer', _self.answer);
			formData.append('realNameAuthentication', _self.realNameAuthentication);
			formData.append('state', _self.state);
			formData.append('allowUserDynamic', _self.allowUserDynamic);
			formData.append('remarks', _self.remarks);
		
			
			if (_self.userCustomList != null && _self.userCustomList.length > 0) {
				for (let i = 0; i < _self.userCustomList.length; i++) {
					let userCustom = _self.userCustomList[i];
					
					var fieldValue = _self.userBoundField[i];

					if (userCustom.chooseType == 1) { //文本框
						formData.append('userCustom_'+ userCustom.id, fieldValue);
					} else if (userCustom.chooseType == 2) { //单选框
						formData.append('userCustom_'+ userCustom.id, fieldValue);
					} else if (userCustom.chooseType == 3) { //多选框
						for (var value in fieldValue) {
							formData.append('userCustom_'+ userCustom.id, fieldValue[value]);
						}
					} else if (userCustom.chooseType == 4) { //下拉列表
						if (userCustom.multiple == true) { //允许多选
							for (var value in fieldValue) {
								formData.append('userCustom_'+ userCustom.id, fieldValue[value]);
							}
						}else{
							formData.append('userCustom_'+ userCustom.id, fieldValue);
							
						}
					} else if (userCustom.chooseType == 5) { //文本域
						formData.append('userCustom_'+ userCustom.id, fieldValue);
					}
					
					
				}
			}
			
			for(let i=0; i<_self.multipleSelection.length; i++){
	    		let rowData = _self.multipleSelection[i];
	    		formData.append('userRolesId', rowData.id);
	    		
	    		
	    		for(let j=0; j<_self.userRoleList.length; j++){
	    			if(_self.userRoleList[j].id == rowData.id){
	    				formData.append('validPeriodEnd_'+rowData.id, dayjs(_self.validPeriodEnd_format[j]).format('YYYY-MM-DD HH:mm'));
	    				break;
	    			}
					
				}
	    	}
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/user/manage?method=add',
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
							path : '/admin/control/user/list',
						});
			    	}else if(returnValue.code === 500){//错误
			    		
			    		let errorMap = returnValue.data;
			    		for (let key in errorMap) { 
			    			if(key.indexOf("validPeriodEnd_") == 0){
			    				_self.error.validPeriodEnd.set(key ,errorMap[key]);
			    				continue;
			    			}
			    			if(key.indexOf("userCustom_") == 0){
			    				_self.error.userCustom.set(key ,errorMap[key]);
			    				continue;
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