<!-- 维护数据 -->
<template id="maintainData-template">
	<div>
		<div class="main" >
			<div class="data-form label-width-blank" >
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="重建话题索引" :error="error.rebuildTopicIndex">
						<el-button-group>
							<el-button type="primary" size="mini" @click="rebuildTopicIndex()">开始重建</el-button>
						</el-button-group>
						<div class="form-help" >需要时间取决于话题数量</div>
					</el-form-item>
					<el-form-item label="重建问题索引" :error="error.rebuildQuestionIndex">
						<el-button-group>
							<el-button type="primary" size="mini" @click="rebuildQuestionIndex()">开始重建</el-button>
						</el-button-group>
						<div class="form-help" >需要时间取决于问题数量</div>
					</el-form-item>
					<el-form-item label="修改数据库密码">
						<el-button-group>
							<el-button type="primary" size="mini" @click="updateDatabasePasswordUI()">修改</el-button>
						</el-button-group>
						<div class="form-help" >修改完成后需重启Tomcat等应用服务器才能生效</div>
					</el-form-item>
					
					<el-dialog title="修改数据库密码" width="60%" v-model="updateDatabasePasswordForm">
						<el-form label-width="auto"  @submit.native.prevent>
							<el-form-item label="旧密码" :required="true" :error="error.oldPassword">
								<el-input type="password" v-model.trim="oldPassword" maxlength="80" clearable="true" show-word-limit></el-input>
							</el-form-item>
							<el-form-item label="新密码" :required="true" :error="error.newPassword">
								<el-input type="password" v-model.trim="newPassword" maxlength="80" clearable="true" show-word-limit></el-input>
							</el-form-item>
							<el-form-item label="重复密码" :required="true" :error="error.repeatPassword">
								<el-input type="password" v-model.trim="repeatPassword" maxlength="80" clearable="true" show-word-limit></el-input>
							</el-form-item>
		  				</el-form>
		  				<template #footer>
						    <span class="dialog-footer">
						    	<el-button class="submitButton" type="primary" @click="updateDatabasePassword_submitForm" :disabled="submitForm_disabled">提交</el-button>
						    </span>
						</template>
					</el-dialog>
					
					<el-form-item label="清空系统所有缓存" :error="error.clearAllCache">
						<el-button-group>
							<el-button type="primary" size="mini" @click="clearAllCache()">清空</el-button>
						</el-button-group>
					</el-form-item>
					
					<el-form-item label="删除浏览量数据" :error="error.deletePageViewData_beforeTime" >
						<div class="singleRowTable">
							<div>
								<el-date-picker v-model="deletePageViewData_beforeTime" type="datetime" format="YYYY-MM-DD HH:mm" placeholder="指定日期"></el-date-picker>
							</div>
							<div class="rightCell">
								之前的数据
							</div>
							<div class="rightCell">
								<el-button-group>
									<el-button type="primary" size="mini" @click="deletePageViewData()">开始</el-button>
								</el-button-group>
							</div>
						</div>
						<div class="form-help" >需要时间取决于数据数量</div>
					</el-form-item>
					<el-form-item label="删除用户登录日志数据" :error="error.deleteUserLoginLogData_beforeTime" >
						<div class="singleRowTable">
							<div>
								<el-date-picker v-model="deleteUserLoginLogData_beforeTime" type="datetime" format="YYYY-MM-DD HH:mm" placeholder="指定日期"></el-date-picker>
							</div>
							<div class="rightCell">
								之前的数据
							</div>
							<div class="rightCell">
								<el-button-group>
									<el-button type="primary" size="mini" @click="deleteUserLoginLogData()">开始</el-button>
								</el-button-group>
							</div>
						</div>
						<div class="form-help" >需要时间取决于日志数量</div>
					</el-form-item>
					
					
					
				</el-form>
				
				
			</div>
		</div>
	</div>
</template>

<script>
//维护数据
export default({
	name: 'maintainData',//组件名称，keep-alive缓存需要本参数
	template : '#maintainData-template',
	inject:['reload'], 
	data : function data() {
		return {
			updateDatabasePasswordForm:false, //是否显示修改数据库密码表单
			oldPassword:'',
			newPassword:'',
	       	repeatPassword:'',
	       	deletePageViewData_beforeTime :'',//日期  格式化为适合el-date-picker标签的值
	       	deleteUserLoginLogData_beforeTime :'',
	       	
	       	
			error : {
				rebuildTopicIndex:'',
				rebuildQuestionIndex:'',
				oldPassword:'',
				newPassword:'',
		       	repeatPassword:'',
		       	clearAllCache:'',
		       	deletePageViewData_beforeTime:'',
		       	deleteUserLoginLogData_beforeTime :'',
		       	
		       
			},
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	created : function created() {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
	},
	methods : {
		
	
		//重建话题索引
		rebuildTopicIndex : function() {
			let _self = this;
			_self.submitForm_disabled = true;
			
	        //清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
		
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/systemSetting/manage?method=rebuildTopicIndex',
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
			    		_self.$message.success("执行任务成功");
			    		
			    		//删除缓存
			    		_self.$store.commit('setCacheNumber');
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
	    
	    
	  	//重建问题索引
  		rebuildQuestionIndex : function() {
  			let _self = this;
  			_self.submitForm_disabled = true;
  			
  	        //清除错误
  			for (let key in _self.error) { 
      			_self.error[key] = "";
      	    }
  			let formData = new FormData();
  		
  			
  			_self.$ajax({
  		        method: 'post',
  		        url: 'control/systemSetting/manage?method=rebuildQuestionIndex',
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
  			    		_self.$message.success("执行任务成功");
  			    		
  			    		//删除缓存
  			    		_self.$store.commit('setCacheNumber');
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
  	    
  		//修改数据库密码UI
		updateDatabasePasswordUI: function() { 
			//清除错误
			for (let key in this.error) { 
	   			this.error[key] = "";
	   	    }
	       	this.updateDatabasePasswordForm =true;//显示修改数据库密码表单
	       	
			
			this.oldPassword = "";
			this.newPassword= "";
	       	this.repeatPassword = "";
	       	
			this.submitForm_disabled = false;//提交按钮是否禁用
		},
		//修改数据库密码
  		updateDatabasePassword_submitForm : function() {
  			let _self = this;
  			_self.submitForm_disabled = true;
  			
  	        //清除错误
  			for (let key in _self.error) { 
      			_self.error[key] = "";
      	    }
  			let formData = new FormData();
  		
  			formData.append('oldPassword', _self.oldPassword);
  			formData.append('newPassword', _self.newPassword);
  			formData.append('repeatPassword', _self.repeatPassword);
  			
  			
  			if(_self.newPassword != _self.repeatPassword){
  				_self.error.repeatPassword = "两次输入新密码不一致";
  				_self.submitForm_disabled = false;
  				return;
  			}
  			
  			
  			
  			_self.$ajax({
  		        method: 'post',
  		        url: 'control/systemSetting/manage?method=updateDatabasePassword',
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
  			    		
  			    		_self.updateDatabasePasswordForm = false;
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
  	  
  		//清空系统所有缓存
		clearAllCache : function() {
			let _self = this;
			_self.submitForm_disabled = true;
			
	        //清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
		
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/systemSetting/manage?method=clearAllCache',
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
			    		_self.$message.success("执行任务成功");
			    		
			    		//删除缓存
			    		_self.$store.commit('setCacheNumber');
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
	    
	  	//删除浏览量数据
  		deletePageViewData : function() {
  			let _self = this;
  			_self.submitForm_disabled = true;
  			
  	        //清除错误
  			for (let key in _self.error) { 
      			_self.error[key] = "";
      	    }
  			let formData = new FormData();
  			if(_self.deletePageViewData_beforeTime != null && _self.deletePageViewData_beforeTime != ''){
  				formData.append('deletePageViewData_beforeTime', dayjs(_self.deletePageViewData_beforeTime).format('YYYY-MM-DD HH:mm'));
  			}
  			
  			_self.$ajax({
  		        method: 'post',
  		        url: 'control/systemSetting/manage?method=deletePageViewData',
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
  			    		_self.$message.success("执行任务成功");
  			    		
  			    		//删除缓存
  			    		_self.$store.commit('setCacheNumber');
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
  		//删除浏览量数据
  		deleteUserLoginLogData : function() {
  			let _self = this;
  			_self.submitForm_disabled = true;
  			
  	        //清除错误
  			for (let key in _self.error) { 
      			_self.error[key] = "";
      	    }
  			let formData = new FormData();
  			if(_self.deleteUserLoginLogData_beforeTime != null && _self.deleteUserLoginLogData_beforeTime != ''){
  				formData.append('deleteUserLoginLogData_beforeTime', dayjs(_self.deleteUserLoginLogData_beforeTime).format('YYYY-MM-DD HH:mm'));
  			}
  			
  			_self.$ajax({
  		        method: 'post',
  		        url: 'control/systemSetting/manage?method=deleteUserLoginLogData',
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
  			    		_self.$message.success("执行任务成功");
  			    		
  			    		//删除缓存
  			    		_self.$store.commit('setCacheNumber');
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
  	   
	}
});

</script>