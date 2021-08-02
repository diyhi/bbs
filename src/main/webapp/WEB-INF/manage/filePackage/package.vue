<!-- 压缩文件打包 -->
<template id="package-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/filePackage/list'})">返回</el-button>
			</div>
			<div class="data-form label-width-blank packageModule" >
				<div class="headInfo">
					<div class="title">选择要打包的目录或文件</div>
				</div>
				<el-tree ref="tree" :props="props" :indent="23" :load="loadNode" lazy show-checkbox>
					<template #default="scope">
			        	<i class="icon icon-folder el-icon-folder" v-if="scope.node.data.leaf == false"></i>
						<i class="icon icon-file el-icon-document" v-if="scope.node.data.leaf == true"></i>
			        	<span>{{scope.node.label}}</span>
				    </template>
				</el-tree>
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item :error="error.package">
					    <el-button type="primary" class="submitButton" @click="submitForm" :disabled="submitForm_disabled">打包</el-button>
					    <div class="prompt">注意：根目录/WEB-INF/data/filePackage  下的文件将不打包</div>
					</el-form-item>
				</el-form>
			</div>
			
			
		
		</div>
	</div>
</template>

<script>
//压缩文件打包
export default({
	name: 'package',//组件名称，keep-alive缓存需要本参数
	template : '#package-template',
	inject:['reload'], 
	data : function data() {
		return {
			
			props: {
				label: 'name',
		        children: 'zones',
		        isLeaf: 'leaf',
		        parentId2: 'parentId'
			},
			
			
			
			error :{
				package: '',
			},
			
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	
	created : function created() {
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
	},
	methods : {
		/**
		loadNode(node, resolve) {
	        if (node.level === 0) {
	          return resolve([{ name: 'region' }]);
	        }
	        if (node.level > 1) return resolve([]);

	        setTimeout(() => {
	          const data = [{
	            name: 'leaf',
	            leaf: true
	          }, {
	            name: 'zone'
	          }];

	          resolve(data);
	        }, 500);
	    },
		**/
		
		
		//加载子节点
		loadNode : async function (node, resolve) {
			let _self = this;
			
			let parentId = "";
			
			if (node.level > 0) {//如果不是根节点
				if(node.data != undefined){
					parentId = node.data.id;
				}
		    }
			let return_resourceList = [];
			await _self.$ajax.get('control/filePackage/manage', {
			    params: {
			    	method: 'querySubdirectory',
			    	parentId :parentId,
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
			    		let list = returnValue.data;
			    		if(list != null && list.length >0){
			    			return_resourceList = list;
			    		}
			    	}else if(returnValue.code === 500){
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
			resolve(return_resourceList);
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
			
			
			if(_self.$refs.tree.getCheckedNodes().length ==0){
				_self.$message({
		            showClose: true,
		            message: "请选择要打包的目录或文件",
		            type: 'error'
		        });
				_self.submitForm_disabled = false;
			}
			
			for(let i=0; i<_self.$refs.tree.getCheckedNodes().length; i++){
				let node = _self.$refs.tree.getCheckedNodes()[i];
				formData.append('idGroup', node.id);
			}
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/filePackage/manage?method=package',
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
			    		_self.$message.success("提交成功,正在异步打包");
			    		
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

