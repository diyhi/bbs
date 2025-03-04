<!-- 添加短信接口 -->
<template id="addSmsInterface-template">
	<div>
		<div class="main" >
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/smsInterface/list',query:{ page:($route.query.page != undefined ? $route.query.page:'')}})">返回</el-button>
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
					<el-form-item label="用户密钥Id(accessKeyId)" v-if="interfaceProduct ==1" :required="true" :error="error.alidayu_accessKeyId">
						<el-input type="textarea" v-model="alidayu_accessKeyId" :autosize="{minRows: 1}" placeholder="请输入密钥" ></el-input>	
						<div class="form-help" >
							<a href="backstage/images/help/alidayu_accessKey.jpg" target="_blank">获取方法演示</a>
						</div>
					</el-form-item>
					<el-form-item label="用户密钥(accessKeySecret)" v-if="interfaceProduct ==1" :required="true" :error="error.alidayu_accessKeySecret">
						<el-input type="textarea" v-model="alidayu_accessKeySecret" :autosize="{minRows: 1}" placeholder="请输入密钥" ></el-input>	
						<div class="form-help" >
							<a href="backstage/images/help/alidayu_accessKey.jpg" target="_blank">获取方法演示</a>
						</div>
					</el-form-item>
					<el-form-item label="排序" :required="true" :error="error.sort">
						<el-row><el-col :span="6"><el-input v-model.trim="sort" maxlength="8" clearable="true" show-word-limit></el-input></el-col></el-row>
					</el-form-item>
					
					<div class="data-view" v-for="(sendService,index) in sendServiceList">
						<el-form-item v-if="sendService.interfaceProduct == 1 && interfaceProduct ==1">
						   {{sendService.serviceName}}
						</el-form-item>
						<el-form-item label="国内短信签名" v-if="sendService.interfaceProduct == 1 && interfaceProduct ==1" :error="error.alidayu_signName.get(sendService.interfaceProduct+'_'+sendService.serviceId)">
							<el-row><el-col :span="18"><el-input v-model.trim="alidayu_signName[index]" maxlength="80" clearable="true" show-word-limit></el-input></el-col></el-row>
							<div class="form-help" >阿里云管理控制台审核通过的短信签名</div>
						</el-form-item>
						<el-form-item label="国内短信模板代码" v-if="sendService.interfaceProduct == 1 && interfaceProduct ==1" :error="error.alidayu_templateCode.get(sendService.interfaceProduct+'_'+sendService.serviceId)">
							<el-row><el-col :span="18"><el-input v-model.trim="alidayu_templateCode[index]" maxlength="80" clearable="true" show-word-limit></el-input></el-col></el-row>
							<div class="form-help" >例如：SMS_1000000</div>
						</el-form-item>
						
						<el-form-item label="国际短信签名" v-if="sendService.interfaceProduct == 1 && interfaceProduct ==1" :error="error.alidayu_internationalSignName.get(sendService.interfaceProduct+'_'+sendService.serviceId)">
							<el-row><el-col :span="18"><el-input v-model.trim="alidayu_internationalSignName[index]" maxlength="80" clearable="true" show-word-limit></el-input></el-col></el-row>
							<div class="form-help" >阿里云管理控制台审核通过的短信签名</div>
						</el-form-item>
						<el-form-item label="国际短信模板代码" v-if="sendService.interfaceProduct == 1 && interfaceProduct ==1" :error="error.alidayu_internationalTemplateCode.get(sendService.interfaceProduct+'_'+sendService.serviceId)">
							<el-row><el-col :span="18"><el-input v-model.trim="alidayu_internationalTemplateCode[index]" maxlength="80" clearable="true" show-word-limit></el-input></el-col></el-row>
							<div class="form-help" >例如：SMS_1000000</div>
						</el-form-item>
						
						<el-form-item label="支持变量" v-if="sendService.interfaceProduct == 1 && interfaceProduct ==1">
							<el-table ref="table" :data="sendService.alidayu_variable" tooltip-effect="dark" :show-header="false" stripe style="width: 100%" empty-text="没有变量">
								<el-table-column label="变量" >
									<template #default="scope">	
										{{scope.row.key}}
							    	</template>
								</el-table-column>
								<el-table-column label="备注" >
									<template #default="scope">	
										{{scope.row.value}}
							    	</template>
								</el-table-column>
							</el-table>
						</el-form-item>
					</div>
					
					
					
					<el-form-item>
					    <el-button type="primary" class="submitButton" @click="submitForm" :disabled="submitForm_disabled">提交</el-button>
					</el-form-item>
				</el-form>
				
				
			</div>
		</div>
	</div>
</template>

<script>
//添加短信接口
export default({
	name: 'addSmsInterface',//组件名称，keep-alive缓存需要本参数
	template : '#addSmsInterface-template',
	inject:['reload'], 
	data : function data() {
		return {
			sendServiceList:'',
			
			name :'',
			interfaceProduct :'',
			options: [],
			sort:0,
			alidayu_accessKeyId :'',
			alidayu_accessKeySecret :'',
			alidayu_signName:[],
			alidayu_templateCode:[],
			alidayu_internationalSignName:[],
			alidayu_internationalTemplateCode:[],
			
			error : {
				name :'',
				interfaceProduct :'',
				sort :'',
				alidayu_accessKeyId :'',
				alidayu_accessKeySecret :'',
				alidayu_signName : new Map(),
				alidayu_templateCode : new Map(),
				alidayu_internationalSignName : new Map(),
				alidayu_internationalTemplateCode : new Map(),
			},
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	created : function created() {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		this.queryAddSmsInterface();
	},
	methods : {
		//查询添加短信接口
	    queryAddSmsInterface: function(){
	        let _self = this;
			
			
			_self.$ajax.get('control/smsInterface/manage', {
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
			    			if(key == "sendServiceList"){
			    				let sendServiceList = mapData[key];
			    				if(sendServiceList != null && sendServiceList.length >0){
			    					for(let i=0; i<sendServiceList.length; i++){
			    						let sendService = sendServiceList[i];
			    						_self.alidayu_signName.push("");
			    						_self.alidayu_templateCode.push("");
			    						_self.alidayu_internationalSignName.push("");
			    						_self.alidayu_internationalTemplateCode.push("");
			    						
			    						
			    						let variable = sendService.alidayu_variable;
			    						let variableList = [];
			    						for(let key in variable){//Map转为List
			    							let obj =new Object();
							    	    	obj.key = key;
							    	    	obj.value = variable[key];
							    	    	variableList.push(obj);
			    						}
			    						sendService.alidayu_variable = variableList;
			    					}
			    				}
			    				_self.sendServiceList = sendServiceList;
			    			}else if(key == "smsInterfaceProductMap"){
			    				let smsInterfaceProductMap = mapData[key];
			    				let count = 0;
				    			for(let interfaceProduct in smsInterfaceProductMap){
				    				let obj =new Object();
					    	    	obj.value = interfaceProduct;
					    	    	obj.label = smsInterfaceProductMap[interfaceProduct];
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
			if(_self.alidayu_accessKeyId != null){
				formData.append('alidayu_accessKeyId', _self.alidayu_accessKeyId);
			}
			if(_self.alidayu_accessKeySecret != null){
				formData.append('alidayu_accessKeySecret', _self.alidayu_accessKeySecret);
			}
			
			if(_self.sendServiceList != null && _self.sendServiceList.length >0){
				for(let i=0; i<_self.sendServiceList.length; i++){
					let sendService = _self.sendServiceList[i];
					
					if(_self.alidayu_signName[i] != null){
						formData.append('alidayu_signName_'+sendService.interfaceProduct+"_"+sendService.serviceId, _self.alidayu_signName[i]);
					}
					if(_self.alidayu_templateCode[i] != null){
						formData.append('alidayu_templateCode_'+sendService.interfaceProduct+"_"+sendService.serviceId, _self.alidayu_templateCode[i]);
					}
					if(_self.alidayu_internationalSignName[i] != null){
						formData.append('alidayu_internationalSignName_'+sendService.interfaceProduct+"_"+sendService.serviceId, _self.alidayu_internationalSignName[i]);
					}
					if(_self.alidayu_internationalTemplateCode[i] != null){
						formData.append('alidayu_internationalTemplateCode_'+sendService.interfaceProduct+"_"+sendService.serviceId, _self.alidayu_internationalTemplateCode[i]);
					}
				}
			}
			
			
			
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/smsInterface/manage?method=add',
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
							path : '/admin/control/smsInterface/list',
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