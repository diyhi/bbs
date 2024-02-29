<!-- 修改在线支付接口 -->
<template id="editOnlinePaymentInterface-template">
	<div>
		<div class="main" >
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/onlinePaymentInterface/list',query:{ page:($route.query.page != undefined ? $route.query.page:'')}})">返回</el-button>
			</div>
			<div class="data-form label-width-blank" >
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="名称" :required="true" :error="error.name">
						<el-row><el-col :span="18"><el-input v-model.trim="name" maxlength="80" clearable="true" show-word-limit></el-input></el-col></el-row>
					</el-form-item>
					<el-form-item label="接口产品">
						<span v-if="interfaceProduct == 1">支付宝即时到账</span>
						<span v-if="interfaceProduct == 4">支付宝手机网站(alipay.trade.wap.pay)</span>
					</el-form-item>
					<el-form-item label="APPID" v-if="interfaceProduct ==1" :required="true" :error="error.direct_app_id">
						<el-input type="textarea" v-model="direct_app_id" :autosize="{minRows: 1}" placeholder="请输入APPID" ></el-input>	
						<div class="form-help" >应用的APPID</div>
					</el-form-item>
					<el-form-item label="商户签名算法类型" v-if="interfaceProduct ==1" >
						RSA2(2048位)
					</el-form-item>
					<el-form-item label="商户的私钥(pkcs8格式)" v-if="interfaceProduct ==1" :required="true" :error="error.direct_rsa_private_key">
						<el-input type="textarea" v-model="direct_rsa_private_key" :autosize="{minRows: 5}" placeholder="请输入私钥" ></el-input>	
					</el-form-item>
					<el-form-item label="支付宝公钥" v-if="interfaceProduct ==1" :required="true" :error="error.direct_alipay_public_key">
						<el-input type="textarea" v-model="direct_alipay_public_key" :autosize="{minRows: 5}" placeholder="请输入支付宝公钥" ></el-input>	
						<div class="form-help" >查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥</div>
					</el-form-item>
					
					
					<el-form-item label="APPID" v-if="interfaceProduct ==4" :required="true" :error="error.mobile_app_id">
						<el-input type="textarea" v-model="mobile_app_id" :autosize="{minRows: 1}" placeholder="请输入APPID" ></el-input>	
						<div class="form-help" >应用的APPID</div>
					</el-form-item>
					<el-form-item label="商户签名算法类型" v-if="interfaceProduct ==4" >
						RSA2(2048位)
					</el-form-item>
					<el-form-item label="商户的私钥(pkcs8格式)" v-if="interfaceProduct ==4" :required="true" :error="error.mobile_rsa_private_key">
						<el-input type="textarea" v-model="mobile_rsa_private_key" :autosize="{minRows: 5}" placeholder="请输入私钥" ></el-input>	
					</el-form-item>
					<el-form-item label="支付宝公钥" v-if="interfaceProduct ==4" :required="true" :error="error.mobile_alipay_public_key">
						<el-input type="textarea" v-model="mobile_alipay_public_key" :autosize="{minRows: 5}" placeholder="请输入支付宝公钥" ></el-input>	
						<div class="form-help" >查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥</div>
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
//修改在线支付接口
export default({
	name: 'editOnlinePaymentInterface',//组件名称，keep-alive缓存需要本参数
	template : '#editOnlinePaymentInterface-template',
	inject:['reload'], 
	data : function data() {
		return {
			onlinePaymentInterfaceId :'',
			
			name :'',
			interfaceProduct :'',
			sort:0,
			enable:true,
			
			direct_app_id :'',
			direct_rsa_private_key :'',
			direct_alipay_public_key :'',
			
			mobile_app_id :'',
			mobile_rsa_private_key :'',
			mobile_alipay_public_key :'',
			
			
			
			error : {
				name :'',
				interfaceProduct :'',
				sort :'',
				enable :'',
				
				direct_app_id :'',
				direct_rsa_private_key :'',
				direct_alipay_public_key :'',
				
				mobile_app_id :'',
				mobile_rsa_private_key :'',
				mobile_alipay_public_key :'',
				
			},
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	created : function created() {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		if(this.$route.query.onlinePaymentInterfaceId != undefined && this.$route.query.onlinePaymentInterfaceId != ''){
			this.onlinePaymentInterfaceId = this.$route.query.onlinePaymentInterfaceId;
		}
		
		this.queryEditOnlinePaymentInterface();
	},
	methods : {
		//查询修改在线支付接口
	    queryEditOnlinePaymentInterface: function(){
	        let _self = this;
			
			
			_self.$ajax.get('control/onlinePaymentInterface/manage', {
			    params: {
			    	method : 'edit',
			    	onlinePaymentInterfaceId: _self.onlinePaymentInterfaceId,
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
			    			if(key == "onlinePaymentInterface"){
			    				let onlinePaymentInterface = mapData[key];
			    				
			    				_self.name = onlinePaymentInterface.name;
			    				_self.interfaceProduct = onlinePaymentInterface.interfaceProduct;
			    				_self.sort = onlinePaymentInterface.sort;
			    				_self.enable = onlinePaymentInterface.enable;
			    			}else if(key == "alipayDirect"){
			    				let alipayDirect = mapData[key];
			    				_self.direct_app_id = alipayDirect.app_id;
			    				_self.direct_rsa_private_key = alipayDirect.rsa_private_key;
			    				_self.direct_alipay_public_key = alipayDirect.alipay_public_key;
			    			}else if(key == "alipayMobile"){
			    				let alipayMobile = mapData[key];
			    				_self.mobile_app_id = alipayMobile.app_id;
			    				_self.mobile_rsa_private_key = alipayMobile.rsa_private_key;
			    				_self.mobile_alipay_public_key = alipayMobile.alipay_public_key;
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
			formData.append('onlinePaymentInterfaceId', _self.onlinePaymentInterfaceId);
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
			if(_self.direct_app_id != null){
				formData.append('direct_app_id', _self.direct_app_id);
			}
			if(_self.direct_rsa_private_key != null){
				formData.append('direct_rsa_private_key', _self.direct_rsa_private_key);
			}
			if(_self.direct_alipay_public_key != null){
				formData.append('direct_alipay_public_key', _self.direct_alipay_public_key);
			}
			
			if(_self.mobile_app_id != null){
				formData.append('mobile_app_id', _self.mobile_app_id);
			}
			if(_self.mobile_rsa_private_key != null){
				formData.append('mobile_rsa_private_key', _self.mobile_rsa_private_key);
			}
			if(_self.mobile_alipay_public_key != null){
				formData.append('mobile_alipay_public_key', _self.mobile_alipay_public_key);
			}
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/onlinePaymentInterface/manage?method=edit&a=a',//a=a参数的作用是仅增加连接符&
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
							path : '/admin/control/onlinePaymentInterface/list',
							query:{ page:(_self.$route.query.page != undefined ? _self.$route.query.page:'')}
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