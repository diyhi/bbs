<!-- 添加第三方登录接口 -->
<template id="addThirdPartyLoginInterface-template">
	<div>
		<div class="main" >
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/thirdPartyLoginInterface/list',query:{ page:($route.query.page != undefined ? $route.query.page:'')}})">返回</el-button>
			</div>
			<div class="data-form label-width-blank" >
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="名称" :required="true" :error="error.name">
						<el-row><el-col :span="18"><el-input v-model.trim="name" maxlength="80" clearable="true" show-word-limit></el-input></el-col></el-row>
					</el-form-item>
					<el-form-item label="接口产品" :required="true" :error="error.interfaceProduct">
						<el-select v-model="interfaceProduct" placeholder="请选择">
							<el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"></el-option>
						</el-select>
						<div class="form-help" >每种接口只能添加一次</div>
					</el-form-item>
					<el-form-item label="微信开放平台 AppID" v-if="interfaceProduct ==10" :required="true" :error="error.weixin_op_appID">
						<el-input type="textarea" v-model="weixin_op_appID" :autosize="{minRows: 1}" placeholder="请输入AppID" ></el-input>	
						<div class="form-help" >微信开放平台应用唯一标识</div>
					</el-form-item>
					<el-form-item label="微信开放平台 AppSecret" v-if="interfaceProduct ==10" :required="true" :error="error.weixin_op_appSecret">
						<el-input type="textarea" v-model="weixin_op_appSecret" :autosize="{minRows: 5}" placeholder="请输入密钥" ></el-input>	
						<div class="form-help" >应用密钥</div>
					</el-form-item>
					<el-form-item label="微信公众号 AppID" v-if="interfaceProduct ==10" :required="true" :error="error.weixin_oa_appID">
						<el-input type="textarea" v-model="weixin_oa_appID" :autosize="{minRows: 5}" ></el-input>	
						<div class="form-help" >微信公众平台服务号的应用唯一标识</div>
					</el-form-item>
					<el-form-item label="微信公众号 AppSecret" v-if="interfaceProduct ==10" :required="true" :error="error.weixin_oa_appSecret">
						<el-input type="textarea" v-model="weixin_oa_appSecret" :autosize="{minRows: 5}" ></el-input>	
						<div class="form-help" >应用密钥</div>
					</el-form-item>
					<el-form-item label="注意" v-if="interfaceProduct ==10">
						<div class="form-help" >
							1、需要在微信公众平台的“开发 - 接口权限 - 网页服务 - 网页帐号 - 网页授权获取用户基本信息”的配置选项中，修改授权回调域名 (域名格式不需要加http://协议头)。前后端分离模式需要填写前端域名<br>		
			    			2、需要在微信开放平台的“管理中心 - 开发信息 - 授权回调域”的配置选项中，修改授权回调域名 (域名格式不需要加http://协议头)。前后端分离模式需要填写前端域名<br>
							3、需要在微信开放平台绑定公众号
						</div>
					</el-form-item>
					
					<el-form-item label="排序" :required="true" :error="error.sort">
						<el-row><el-col :span="6"><el-input v-model.trim="sort" maxlength="8" clearable="true" show-word-limit></el-input></el-col></el-row>
					</el-form-item>
					<el-form-item label="是否启用" :required="true" :error="error.enable">
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
//添加第三方登录接口
export default({
	name: 'addThirdPartyLoginInterface',//组件名称，keep-alive缓存需要本参数
	template : '#addThirdPartyLoginInterface-template',
	inject:['reload'], 
	data : function data() {
		return {
			
			name :'',
			interfaceProduct :'',
			options: [],
			sort:0,
			enable:true,
			weixin_op_appID :'',
			weixin_op_appSecret :'',
			weixin_oa_appID :'',
			weixin_oa_appSecret :'',
			
			
			error : {
				name :'',
				interfaceProduct :'',
				sort :'',
				enable :'',
				
				weixin_op_appID :'',
				weixin_op_appSecret :'',
				weixin_oa_appID :'',
				weixin_oa_appSecret :'',
				
			},
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	created : function created() {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		this.queryAddThirdPartyLoginInterface();
	},
	methods : {
		//查询添加第三方登录接口
	    queryAddThirdPartyLoginInterface: function(){
	        let _self = this;
			
			
			_self.$ajax.get('control/thirdPartyLoginInterface/manage', {
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
			    			if(key == "interfaceProductMap"){
			    				let interfaceProductMap = mapData[key];
			    				let count = 0;
				    			for(let interfaceProduct in interfaceProductMap){
				    				let obj =new Object();
					    	    	obj.value = interfaceProduct;
					    	    	obj.label = interfaceProductMap[interfaceProduct];
					    	    	_self.options.push(obj);
					    	    	count++;
				    			}
					    		if(count == 0){
					    			_self.error.interfaceProduct = "接口已添加完";
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
			if(_self.name != null){
				formData.append('name', _self.name);
			}
			if(_self.interfaceProduct != null){
				formData.append('interfaceProduct', _self.interfaceProduct);
			}
			if(_self.sort != null){
				formData.append('sort', _self.sort);
			}
			if(_self.enable != null){
				formData.append('enable', _self.enable);
			}
			
			if(_self.weixin_op_appID != null){
				formData.append('weixin_op_appID', _self.weixin_op_appID);
			}
			if(_self.weixin_op_appSecret != null){
				formData.append('weixin_op_appSecret', _self.weixin_op_appSecret);
			}
			if(_self.weixin_oa_appID != null){
				formData.append('weixin_oa_appID', _self.weixin_oa_appID);
			}
			if(_self.weixin_oa_appSecret != null){
				formData.append('weixin_oa_appSecret', _self.weixin_oa_appSecret);
			}
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/thirdPartyLoginInterface/manage?method=add',
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
							path : '/admin/control/thirdPartyLoginInterface/list',
						});
			    	}else if(returnValue.code === 500){//错误
			    		
			    		let errorMap = returnValue.data;
			    		for (let key in errorMap) {   
			    			if(key.split("_").length >3){//alidayu_signName_1_1
			    				let parameterName = key.split("_")[0] +"_"+ key.split("_")[1];
			    				
								if(parameterName != "" && getType(_self.error[parameterName]) == 'map'){
									_self.error[parameterName].set(key.split("_")[2] +"_"+ key.split("_")[3],errorMap[key]);
									continue;
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