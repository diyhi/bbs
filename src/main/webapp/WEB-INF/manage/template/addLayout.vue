<!-- 添加布局 -->
<template id="addLayout-template">
	<div>
		<div class="main" >
			<div class="nav-breadcrumb">
				<el-breadcrumb separator-class="el-icon-arrow-right">
					<el-breadcrumb-item :to="{ path: '/admin/control/template/list' }">模板列表</el-breadcrumb-item>
					<el-breadcrumb-item :to="{ path: '/admin/control/layout/list',query:{ dirName : $route.query.dirName} }">布局列表</el-breadcrumb-item>
					<el-breadcrumb-item>{{templates.name}} [{{templates.dirName}}]</el-breadcrumb-item>
					<el-breadcrumb-item>添加布局</el-breadcrumb-item>
				</el-breadcrumb>
			</div>
			<div class="data-form label-width-blank" >
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="类型" :required="true" :error="error.type">
						<el-select v-model="type" placeholder="选择类型">
							<el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"></el-option>
						</el-select>
					</el-form-item>
					<el-form-item label="布局名称" v-if="type != 1" :required="true" :error="error.name">
						<el-input v-model.trim="name" maxlength="80" clearable="true" show-word-limit></el-input>
					</el-form-item>
					<el-form-item label="选择默认页" v-if="type == 1" :required="true" :error="error.layoutFile">
						<el-select v-model="layoutFile" placeholder="请选择">
							<el-option v-for="item in layoutFileOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
						</el-select>
					</el-form-item>
					
					<el-form-item label="版块数据" v-if="type == 3" :required="true" :error="error.forumData">
						<el-radio-group v-model="forumData">
						    <el-radio :label="3">在线帮助</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="返回数据" v-if="type == 4" :required="true" :error="error.returnData">
						<el-radio-group v-model="returnData">
						    <el-radio :label="0">html</el-radio>
						    <el-radio :label="1">json</el-radio>
						</el-radio-group>
					</el-form-item>
	
					<el-form-item label="URL名称" v-if="type == 4" :required="true" :error="error.referenceCode">
						<el-input v-model.trim="referenceCode" maxlength="40" clearable="true" show-word-limit @blur="checkUrlName"></el-input>
						<div class="form-help" >URL名称只能输入由数字、26个英文字母、下划线和或者左斜杆组成，左斜杆不能在最前面或最后面或连续出现。例一：aaa/ggg &nbsp;&nbsp; 例二：aaa </div>
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
//添加布局
export default({
	name: 'addLayout',//组件名称，keep-alive缓存需要本参数
	template : '#addLayout-template',
	inject:['reload'], 
	data : function data() {
		return {
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
	        
	        layoutFileOptions:[],//默认布局文件
			
			error : {
				name :'',
				type :'',
				referenceCode: '',
				layoutFile: '',
			},
			
			templates: '',
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	created : function created() {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		if(this.$route.query.dirName != undefined && this.$route.query.dirName != ''){
			this.dirName = this.$route.query.dirName;
		}
		
		this.queryLayoutList();
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
	methods : {
		//查询布局
		queryLayoutList : function() {
			let _self = this;
		
			_self.$ajax.get('control/layout/manage', {
			    params: {
			    	method : 'add',
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
			    			if(key == "defaultLayoutList"){
			    				let list = mapData[key];
			    				if(list != null && list.length >0){
					    			for(let i=0; i<list.length; i++){
					    				let defaultLayout = list[i];
					    				
					    				_self.layoutFileOptions.push({
					    					value: defaultLayout.layoutFile,
			        						label: defaultLayout.name,
					    					
					    				});
					    			}
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
			if(_self.type != null){
				formData.append('type', _self.type);
			}
			if(_self.name != null){
				formData.append('name', _self.name);
			}
			if(_self.referenceCode != null){
				formData.append('referenceCode', _self.referenceCode);
			}
			if(_self.accessRequireLogin != null){
				formData.append('accessRequireLogin', _self.accessRequireLogin);
			}
			
			
			if(_self.layoutFile != null){
				formData.append('layoutFile', _self.layoutFile);
			}
			if(_self.forumData != null){
				formData.append('forumData', _self.forumData);
			}
			if(_self.returnData != null){
				formData.append('returnData', _self.returnData);
			}
			if(_self.dirName != null){
				formData.append('dirName', _self.dirName);
				
			}

			_self.$ajax({
		        method: 'post',
		        url: 'control/layout/manage?method=add',
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
								page :_self.page,
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