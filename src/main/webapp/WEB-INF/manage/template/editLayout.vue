<!-- 修改布局 -->
<template id="editLayout-template">
	<div>
		<div class="main" >
			<div class="nav-breadcrumb">
				<el-breadcrumb separator-class="el-icon-arrow-right">
					<el-breadcrumb-item :to="{ path: '/admin/control/template/list' }">模板列表</el-breadcrumb-item>
					<el-breadcrumb-item :to="{ path: '/admin/control/layout/list',query:{ dirName : $route.query.dirName} }">布局列表</el-breadcrumb-item>
					<el-breadcrumb-item>{{templates.name}} [{{templates.dirName}}]</el-breadcrumb-item>
					<el-breadcrumb-item>修改布局</el-breadcrumb-item>
				</el-breadcrumb>
			</div>
			<div class="data-form label-width-blank" >
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="类型" >
						<span v-if="type == 1">默认页</span>
						<span v-if="type == 3">更多</span>
						<span v-if="type == 4">空白页</span>
						<span v-if="type == 5">公共页(生成新引用页)</span>
						<span v-if="type == 6">公共页(引用版块值)</span>
						<span v-if="type == 7">站点栏目详细页</span>
					</el-form-item>
					<el-form-item label="布局名称" :required="true" :error="error.name">
						<el-input v-model.trim="name" maxlength="80" clearable="true" show-word-limit></el-input>
					</el-form-item>
					<el-form-item label="默认页" v-if="type == 1">
						{{layoutFile}}
					</el-form-item>
					
					<el-form-item label="版块数据" v-if="type == 3" :required="true" :error="error.forumData">
						<el-radio-group v-model="forumData">
						    <el-radio :label="3">在线帮助</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="返回数据" v-if="type == 4">
						<span v-if="returnData == 0">html</span>
                    	<span v-if="returnData == 1">json</span>
					</el-form-item>
	
					<el-form-item label="URL名称" v-if="type == 4" :required="true" :error="error.referenceCode">
						<el-input v-model.trim="referenceCode" maxlength="40" clearable="true" show-word-limit @blur="checkUrlName"></el-input>
						<div class="form-help" >URL名称只能输入由数字、26个英文字母、下划线和或者左斜杆组成，左斜杆不能在最前面或最后面或连续出现。例一：aaa/ggg &nbsp;&nbsp; 例二：aaa </div>
					</el-form-item>
					<el-form-item label="模板引用代码值" v-if="type != 1 && type != 4" >
						{{referenceCode}}
					</el-form-item>
					
					<el-form-item label="访问需要登录" v-if="type != 5 && type != 6">
						<el-switch v-model="accessRequireLogin" :disabled="disabled_accessRequireLogin"></el-switch>
						<div class="form-help" >访问URI为user/开头的地址则默认需要登录后才能访问，本设置无效</div>
						<div v-if="disabled_accessRequireLogin" style="color: #ffa940;">URL名称为user/开头的地址不需要设置本参数</div>
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
//修改布局
export default({
	name: 'editLayout',//组件名称，keep-alive缓存需要本参数
	template : '#editLayout-template',
	inject:['reload'], 
	data : function data() {
		return {
			id : '',
			type :4,
			name :'',
			referenceCode :'',
			layoutFile: '',
			forumData: 3,
			returnData: 0,
			accessRequireLogin :false,//访问需要登录
			
			disabled_accessRequireLogin:false,
			
			options: [{//类型
				value: 1,
				label: '默认页'
			}, {
				value: 3,
				label: '更多'
        	}, {
	        	value: 4,
	        	label: '空白页'
	        }, {
	        	value: 5,
	        	label: '公共页(生成新引用页)'
	        }, {
	        	value: 6,
	        	label: '公共页(引用版块值)'
	        }],
	        
			
			error : {
				name :'',
				type :'',
				referenceCode: '',
			},
			
			templates: '',
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	watch: {
		//监听版块子类型值变化
		referenceCode: {
	　　　　handler(newValue, oldValue) {
				if(newValue.toLowerCase().startsWith("user/")){
					this.disabled_accessRequireLogin = true;
				}else{
					this.disabled_accessRequireLogin = false;
				}
	　　　　}
	　　},
	},
	created : function created() {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		if(this.$route.query.dirName != undefined && this.$route.query.dirName != ''){
			this.dirName = this.$route.query.dirName;
		}
		if(this.$route.query.layoutId != undefined && this.$route.query.layoutId != ''){
			this.id = this.$route.query.layoutId;
		}
		
		
		this.queryLayoutList();
	},
	methods : {
		//查询布局
		queryLayoutList : function() {
			let _self = this;
		
			_self.$ajax.get('control/layout/manage', {
			    params: {
			    	method : 'editLayout',
			    	layoutId: _self.id,
			    	dirName: _self.dirName,
			    },
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
			    			if(key == "layout"){
			    				let layout = mapData[key];
			    				if(layout != null){
			    					_self.type = layout.type;
									_self.name = layout.name;
									_self.referenceCode = layout.referenceCode;
									_self.layoutFile = layout.layoutFile;
									_self.forumData = layout.type;
									_self.returnData = layout.returnData;
									
									_self.accessRequireLogin = layout.accessRequireLogin;
			
									
			    				}
			    			}else if(key == "templates"){
			    				_self.templates = mapData[key];
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
	
		//校验URL名称
		checkUrlName : function() {
			let _self = this;

			_self.error.referenceCode = "";
			
			
			_self.$ajax.get('control/layout/manage', {
			    params: {
			    	method : 'checkUrlName',
			    	layoutId: _self.id,
			    	dirName: _self.dirName,
			    	referenceCode: _self.referenceCode,
			    },
			    headers: {
				  	'showLoading': false,//是否显示图标
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
		
	
	
	
		//提交表单
		submitForm : function() {
			let _self = this;
			_self.submitForm_disabled = true;
			
	        //清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			formData.append('layoutId', _self.id);
			if(_self.name != null){
				formData.append('name', _self.name);
			}
			if(_self.referenceCode != null){
				formData.append('referenceCode', _self.referenceCode);
			}
			if(_self.forumData != null){
				formData.append('forumData', _self.forumData);
			}
			if(_self.returnData != null){
				formData.append('returnData', _self.returnData);
			}
			if(_self.accessRequireLogin != null){
				formData.append('accessRequireLogin', _self.accessRequireLogin);
			}
			if(_self.dirName != null){
				formData.append('dirName', _self.dirName);
				
			}

			_self.$ajax({
		        method: 'post',
		        url: 'control/layout/manage?method=editLayout&a=a',//a=a参数的作用是仅增加连接符&
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
							path : '/admin/control/layout/list',
							query:{ 
								dirName : _self.dirName,
								page : _self.$route.query.page,
							}
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