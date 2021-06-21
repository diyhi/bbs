<!--添加缩略图裁剪尺寸 -->
<template id="addThumbnail-template">
	<div>
		<div class="main" >
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/thumbnail/list'})">返回</el-button>
			</div>
			<div class="data-form label-width-blank" >
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="名称" :required="true" :error="error.name" >
						<el-row><el-col :span="12"><el-input v-model.trim="name" maxlength="30" clearable="true" show-word-limit></el-input></el-col></el-row>
					</el-form-item>
					<el-form-item label="宽" :required="true" :error="error.width" >
						<el-row><el-col :span="8"><el-input v-model.trim="width" maxlength="10" clearable="true" show-word-limit></el-input></el-col></el-row>
					</el-form-item>
					<el-form-item label="高" :required="true" :error="error.high" >
						<el-row><el-col :span="8"><el-input v-model.trim="high" maxlength="10" clearable="true" show-word-limit></el-input></el-col></el-row>
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
//添加缩略图裁剪尺寸
export default({
	name: 'addThumbnail',//组件名称，keep-alive缓存需要本参数
	template : '#addThumbnail-template',
	inject:['reload'], 
	data : function data() {
		return {
			name :'',
			width :'',
			high :'',
			
			error : {
				name:'',
				width :'',
				high :'',
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
		
	
		//提交表单
		submitForm : function() {
			let _self = this;
			_self.submitForm_disabled = true;
			
	        //清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			if(_self.name != null){
				formData.append('name', _self.name);
			}
			if(_self.width != null){
				formData.append('width', _self.width);
			}
			if(_self.high != null){
				formData.append('high', _self.high);
			}
			
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/thumbnail/manage?method=add',
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
							path : '/admin/control/thumbnail/list',
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