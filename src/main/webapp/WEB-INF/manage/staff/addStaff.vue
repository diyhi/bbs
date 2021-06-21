<!-- 添加员工 -->
<template id="addStaff-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/staff/list', query:{ page:($route.query.page != undefined ? $route.query.page:'')}})">返回</el-button>
			</div>
			<div class="data-form label-width-blank userModule" >
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="账号" :required="true" :error="error.userAccount">
						<el-row><el-col :span="12"><el-input v-model.trim="userAccount" maxlength="20" clearable="true" show-word-limit></el-input></el-col></el-row>
					</el-form-item>
					<el-form-item label="姓名" :required="true" :error="error.fullName">
						<el-row><el-col :span="12"><el-input v-model.trim="fullName" maxlength="20" clearable="true" show-word-limit></el-input></el-col></el-row>
					</el-form-item>
					<el-form-item label="密码" :required="true" :error="error.userPassword">
						<el-row><el-col :span="12"><el-input v-model.trim="userPassword" type="password" maxlength="20" clearable="true" show-word-limit></el-input></el-col></el-row>
					</el-form-item>
					<el-form-item label="重复密码" :required="true" :error="error.repeatPassword">
						<el-row><el-col :span="12"><el-input v-model.trim="repeatPassword" type="password" maxlength="20" clearable="true" show-word-limit></el-input></el-col></el-row>
					</el-form-item>
					<el-form-item label="超级管理员" :required="true" :error="error.issys" v-if="isSysAdmin == true">
						<el-radio-group v-model="issys">
						    <el-radio :label="true">是</el-radio>
						    <el-radio :label="false">否</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="备注"  :error="error.userDesc"  >
						<el-input v-model.trim="userDesc" maxlength="150" clearable="true" show-word-limit></el-input>					
					</el-form-item>
					<el-form-item label="职位" :error="error.userDuty">
						<el-row><el-col :span="12"><el-input v-model.trim="userDuty" maxlength="30" clearable="true" show-word-limit></el-input></el-col></el-row>
					</el-form-item>
					<el-form-item label="是否使用" :required="true" :error="error.enabled">
						<el-radio-group v-model="enabled">
						    <el-radio :label="true">启用</el-radio>
						    <el-radio :label="false">停用</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="用户角色" >
						<el-checkbox-group v-model="sysRolesId">
						    <el-checkbox :label="roles.id" :disabled="roles.logonUserPermission == false" :checked="roles.selected" v-for="roles in rolesList">{{roles.name}}</el-checkbox>
						</el-checkbox-group>
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
//添加员工
export default({
	name: 'addStaff',//组件名称，keep-alive缓存需要本参数
	template : '#addStaff-template',
	inject:['reload'], 
	data : function data() {
		return {
			isSysAdmin:false,//是否是超级管理员
			rolesList:'',
			
			
			userAccount :'',
			fullName :'',
			userPassword :'',
			repeatPassword :'',
			issys :false,
			userDesc :'',
			userDuty :'',
			enabled :true,
			sysRolesId:[],
			
			error : {
				userAccount :'',
				fullName :'',
				userPassword :'',
				repeatPassword :'',
				issys :'',
				userDesc :'',
				userDuty :'',
				enabled :'',
			},
			
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	created : function created() {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		
		this.queryAddStaff();
	},
	methods : {
		 //查询添加员工
	    queryAddStaff: function(){
	        let _self = this;
			
			
			_self.$ajax.get('control/staff/manage', {
			    params: {
			    	method : 'addStaff',
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
			    			if(key == "isSysAdmin"){
			    				_self.isSysAdmin = mapData[key];
			    			}else if(key == "sysRolesList"){
			    				_self.rolesList = mapData[key];
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
    			if(getType(_self.error[key]) == 'map'){
    				_self.error[key].clear();
    			}else{
    				_self.error[key] = "";
    			}
    	    }
			let formData = new FormData();
			
			formData.append('userAccount', _self.userAccount);
			formData.append('fullName', _self.fullName);
			formData.append('userPassword', _self.userPassword);
			formData.append('repeatPassword', _self.repeatPassword);
			formData.append('issys', _self.issys);
			formData.append('userDesc', _self.userDesc);
			formData.append('userDuty', _self.userDuty);
			formData.append('enabled', _self.enabled);
		

			
			for(let i=0; i<_self.sysRolesId.length; i++){
	    		let rolesId = _self.sysRolesId[i];
	    		formData.append('sysRolesId', rolesId);
	    	}
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/staff/manage?method=addStaff',
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
							path : '/admin/control/staff/list',
						});
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
	    }
	}
});

</script>