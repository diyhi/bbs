<!-- 修改会员卡赠送任务 -->
<template id="editMembershipCardGiftTask-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/membershipCardGiftTask/list', query:{ page:($route.query.page != undefined ? $route.query.page:'')}})">返回</el-button>
			</div>
			<div class="data-form label-width-blank membershipCardGiftTaskModule" >
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="名称" :required="true" :error="error.name">
						<el-input v-model.trim="name" maxlength="100" clearable="true" show-word-limit></el-input>
					</el-form-item>
					<el-form-item label="用户角色" :required="true" :error="error.userRoleId">
						<el-radio-group v-model="userRoleId">
						    <el-radio :label="roles.id" v-for="roles in rolesList">{{roles.name}}</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="任务类型" >
						<span v-if="type == 10">长期</span>
						<span v-if="type == 20">一次性</span>
						<div class="form-help" >长期任务类型在任务有效期内，会员活跃时会自动触发领取会员卡的任务；一次性任务类型由管理后台提交任务时就开始发放会员卡</div>
					</el-form-item>
					
					<div class="restriction">
						<div class="title">限制条件</div>
						<el-form-item label="用户注册时间范围" >
							<el-form label-width="auto" inline="true" @submit.native.prevent style="position: relative; top:-8px;">
								<el-form-item :error="error.restrictionGroup.registrationTime_start">
									<el-date-picker v-model="restrictionGroup.registrationTime_start" type="datetime" format="YYYY-MM-DD HH:mm:ss" placeholder="起始日期" ></el-date-picker>
									<div class="form-help" >不限制请留空</div>
								</el-form-item>
								<span style="position: relative; top:8px; margin-left: 8px;margin-right: 16px;">至</span>
								<el-form-item :error="error.restrictionGroup.registrationTime_end">
									<el-date-picker v-model="restrictionGroup.registrationTime_end" type="datetime" format="YYYY-MM-DD HH:mm:ss" placeholder="结束日期" ></el-date-picker>
								</el-form-item>
							</el-form>
						</el-form-item>
						<el-form-item label="活动期间积分达到数量" :error="error.restrictionGroup.totalPoint">
							<el-row><el-col :span="4"><el-input v-model.trim="restrictionGroup.totalPoint" maxlength="9" clearable="true" ></el-input></el-col></el-row>
						</el-form-item>
					</div>
					<el-form-item label="任务有效期范围" v-if="type ==10">
						<el-form label-width="auto" inline="true" @submit.native.prevent style="position: relative; top:-8px;">
							<el-form-item :error="error.expirationDate_start">
								<el-date-picker v-model="expirationDate_start" type="datetime" format="YYYY-MM-DD HH:mm:ss" placeholder="起始日期" ></el-date-picker>
								<div class="form-help" >不限制请留空</div>
							</el-form-item>
							<span style="position: relative; top:8px; margin-left: 8px;margin-right: 16px;">至</span>
							<el-form-item :error="error.expirationDate_end">
								<el-date-picker v-model="expirationDate_end" type="datetime" format="YYYY-MM-DD HH:mm:ss" placeholder="结束日期" ></el-date-picker>
							</el-form-item>
						</el-form>
						<div class="form-help" >建议起始至结束时间至少间隔三个小时以上</div>
					</el-form-item>
					
					
					<el-form-item label="时长" :required="true">
						<el-row><el-col :span="12">
							<el-form label-width="auto" inline="true" @submit.native.prevent style="position: relative; top:-8px;">
								<el-form-item :error="error.duration">
									<el-input v-model.trim="duration" maxlength="8" clearable="true" show-word-limit></el-input>
								</el-form-item>
								<el-form-item :error="error.unit">
									<el-select v-model="unit"  no-match-text="还没有单位" placeholder="选择单位" style="margin-left: 2px;">
										<el-option v-for="item in unitOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
									</el-select>
								</el-form-item>
							</el-form>
						</el-col></el-row>
					</el-form-item>
					
					<el-form-item label="是否启用" :error="error.enable">
						<el-switch v-model="enable" ></el-switch>
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
//修改会员卡赠送任务
export default({
	name: 'editMembershipCardGiftTask',//组件名称，keep-alive缓存需要本参数
	template : '#editMembershipCardGiftTask-template',
	inject:['reload'], 
	data : function data() {
		return {
			membershipCardGiftTaskId:'',
			rolesList:'',
		
			name :'',
			type :10,
			userRoleId:'',
			expirationDate_start : '',
			expirationDate_end : '',
			enable:true,
			duration : '',
			unit: '',
				
			unitOptions: [{
		    	value: 10,
		        label: '小时'
		    }, {
		        value: 20,
		        label: '日'
		    }, {
		        value: 30,
		        label: '月'
		    }, {
		        value: 40,
		        label: '年'
		    }],
			
			restrictionGroup:{
				registrationTime_start : '',
				registrationTime_end : '',
				totalPoint : ''
			},
			
			
			
			error : {
				name :'',
				userRoleId:'',
				expirationDate_start : '',
				expirationDate_end : '',
				enable : '',
				duration : '',
				unit: '',
				restrictionGroup:{
					registrationTime_start : '',
					registrationTime_end : '',
					totalPoint : ''
				}
			},
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	created : function created() {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		if(this.$route.query.membershipCardGiftTaskId != undefined && this.$route.query.membershipCardGiftTaskId != ''){
			this.membershipCardGiftTaskId = this.$route.query.membershipCardGiftTaskId;
		}
		
		
		this.queryEditMembershipCardGiftTask();
	},
	methods : {
	
	
		//查询修改会员卡赠送任务
	    queryEditMembershipCardGiftTask: function(){
	        let _self = this;
			
			
			_self.$ajax.get('control/membershipCardGiftTask/manage', {
			    params: {
			    	method : 'edit',
			    	membershipCardGiftTaskId :_self.membershipCardGiftTaskId,
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
			    			if(key == "membershipCardGiftTask"){
			    				let membershipCardGiftTask = mapData[key];
			    				_self.name = membershipCardGiftTask.name;
			    				_self.userRoleId = membershipCardGiftTask.userRoleId;
			    				_self.type = membershipCardGiftTask.type;
			    				
			    				_self.expirationDate_start = membershipCardGiftTask.expirationDate_start;
								_self.expirationDate_end = membershipCardGiftTask.expirationDate_end;
								_self.enable = membershipCardGiftTask.enable;
								_self.duration = membershipCardGiftTask.duration;
								_self.unit = membershipCardGiftTask.unit;
			    				_self.restrictionGroup.registrationTime_start = membershipCardGiftTask.restrictionGroup.registrationTime_start;
			    				_self.restrictionGroup.registrationTime_end = membershipCardGiftTask.restrictionGroup.registrationTime_end;
			    				_self.restrictionGroup.totalPoint = membershipCardGiftTask.restrictionGroup.totalPoint;
			    				
			    				
			    			}else if(key == "userRoleList"){
			    				let rolesList = mapData[key];
			    				if(rolesList != null && rolesList.length >0){
			    					for(let i=0; i<rolesList.length; i++){
			    						let roles = rolesList[i];
			    						if(roles.selected){
			    							_self.userRoleId = roles.id;
			    						}
			    					}
			    				}
			    				_self.rolesList = rolesList;
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
			
			formData.append('membershipCardGiftTaskId', _self.membershipCardGiftTaskId);
			
			if(_self.name != null){
				formData.append('name', _self.name);
			}
			if(_self.type != null){
				formData.append('type', _self.type);
			}
			if(_self.userRoleId != null){
				formData.append('userRoleId', _self.userRoleId);
			}
			if(_self.expirationDate_start != null && _self.expirationDate_start != ''){
				formData.append('_expirationDate_start', dayjs(_self.expirationDate_start).format('YYYY-MM-DD HH:mm:ss'));
			}else{
				formData.append('_expirationDate_start', '');
			}
			if(_self.expirationDate_end != null && _self.expirationDate_end != ''){
				formData.append('_expirationDate_end', dayjs(_self.expirationDate_end).format('YYYY-MM-DD HH:mm:ss'));
			}else{
				formData.append('_expirationDate_end', '');
			}
			if(_self.enable != null){
				formData.append('enable', _self.enable);
			}
			if(_self.duration != null){
				formData.append('duration', _self.duration);
			}
			if(_self.unit != null){
				formData.append('unit', _self.unit);
			}
			
			if(_self.restrictionGroup.registrationTime_start != null && _self.restrictionGroup.registrationTime_start != ''){
				formData.append('_registrationTime_start', dayjs(_self.restrictionGroup.registrationTime_start).format('YYYY-MM-DD HH:mm:ss'));
			}else{
				formData.append('_registrationTime_start', '');
			}
			if(_self.restrictionGroup.registrationTime_end != null && _self.restrictionGroup.registrationTime_end != ''){
				formData.append('_registrationTime_end', dayjs(_self.restrictionGroup.registrationTime_end).format('YYYY-MM-DD HH:mm:ss'));
			}else{
				formData.append('_registrationTime_end', '');
			}
			if(_self.restrictionGroup.totalPoint != null){
				formData.append('restrictionGroup.totalPoint', _self.restrictionGroup.totalPoint);
			}
			
			
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/membershipCardGiftTask/manage?method=edit',
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
							path : '/admin/control/membershipCardGiftTask/list',
						});
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