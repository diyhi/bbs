<!-- 添加注册禁止的用户名称 -->
<template id="addDisableUserName-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/disableUserName/list',query:{ page : ($route.query.superiorPage != undefined ? $route.query.superiorPage :'')}})">返回</el-button>
			</div>
			<div class="data-form label-width-blank" >
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="名称" :required="true" :error="error.name">
						<el-input v-model.trim="name" maxlength="100"></el-input>
						<div class="form-help" >可使用通配符 *和?  &nbsp;  星号匹配0或者任意数量的字符；问号匹配任何单字符   &nbsp;&nbsp;例如：*admin* 表示不能含有字符admin</div>
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
//添加注册禁止的用户名称
export default({
	name: 'addDisableUserName',//组件名称，keep-alive缓存需要本参数
	template : '#addDisableUserName-template',
	inject:['reload'], 
	data : function data() {
		return {
			name :'',
			error : {
				name :'',
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
			if(_self.name != null && _self.name != ''){
				formData.append('name', _self.name);
				
			}
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/disableUserName/manage?method=add',
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
							path : '/admin/control/disableUserName/list'
						});
			    	}else if(returnValue.code === 500){//错误
			    		
			    		let errorMap = returnValue.data;
			    		for (let key in errorMap) {   
			    			_self.error[key] = errorMap[key];
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
