<!-- 修改系统通知 -->
<template id="editSystemNotify-template">
	<div>
		<div class="main" >
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/systemNotify/list' ,query:{ page:($route.query.page != undefined ? $route.query.page:'')}})">返回</el-button>
			</div>
			<div class="data-form label-width-blank" >
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="通知内容" :required="true" :error="error.content">
						<el-input type="textarea" v-model="content" :autosize="{minRows: 8}" placeholder="请输入内容" ></el-input>	
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
//修改系统通知
export default({
	name: 'editSystemNotify',//组件名称，keep-alive缓存需要本参数
	template : '#editSystemNotify-template',
	inject:['reload'], 
	data : function data() {
		return {
			systemNotifyId :'',
			content :'',
			
			error : {
				content:'',
			},
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	created : function created() {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		if(this.$route.query.systemNotifyId != undefined && this.$route.query.systemNotifyId != ''){
			this.systemNotifyId = this.$route.query.systemNotifyId;
		}
		
		
		this.querySystemNotify();
	},
	methods : {
		 //查询修改系统通知
	    querySystemNotify: function(){
	        let _self = this;
			
			
			_self.$ajax.get('control/systemNotify/manage', {
			    params: {
			    	method : 'edit',
			    	systemNotifyId : _self.systemNotifyId,
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
			    		let systemNotify = returnValue.data;
			    		_self.content = systemNotify.content;
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
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			formData.append('systemNotifyId', _self.systemNotifyId);
			if(_self.content != null){
				formData.append('content', _self.content);
				
			}
		
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/systemNotify/manage?method=edit',
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
							path : '/admin/control/systemNotify/list',
							query:{ page:(_self.$route.query.page != undefined ? _self.$route.query.page:'')}
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