<!-- 帮助查看 -->
<template id="helpView-template">
	<div>
		<div class="main">
			<div class="navbar">
				<!-- 返回上级节点 -->
				<el-button type="primary" plain size="small"  @click="$router.push({path: sourceUrlObject.path, query:sourceUrlObject.query})">返回</el-button>
			</div>
			<div class="helpViewModule" >
				<el-row>
					<el-col :span="24"><div class="name">{{help.name}}</div></el-col>
				</el-row>
				<el-row>
					<el-col :span="24"><div class="content" v-html="help.content"></div></el-col>
				</el-row>
				
				
			</div>
		</div>
	</div>
</template>

<script>
//帮助查看
export default({
	name: 'helpView',//组件名称，keep-alive缓存需要本参数
	template : '#helpView-template',
	inject:['reload'], 
	data : function data() {
		return {
			id:'',
			help:'',
			
			sourceUrlObject:{},//来源URL对象
			
			error:{},
		};
	},
	beforeRouteEnter (to, from, next) {
		//上级路由编码
		if(to.query.beforeUrl == undefined || to.query.beforeUrl==''){//前一个URL
			let parameterObj = new Object;
			parameterObj.path = from.path;
			let query = from.query;
			for(let q in query){
				query[q] = encodeURIComponent(query[q]);
			}
			
			parameterObj.query = query;
			//将请求参数转为base62
			let encrypt = delete_base62_equals(base62_encode(JSON.stringify(parameterObj)));
			
			
			let newFullPath = updateURLParameter(to.fullPath,'beforeUrl',encrypt);
			
			to.fullPath = newFullPath;
			
			let paramGroup = to.query;
			paramGroup.beforeUrl = encrypt;
			to.query = paramGroup;
		}
		next();
	},
	created : function created() {
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		if(this.$route.query.helpId != undefined && this.$route.query.helpId != ''){
			this.id = this.$route.query.helpId;
		}
		
		//上级路由解码
		if(this.$route.query.beforeUrl != undefined && this.$route.query.beforeUrl != ''){
			let decrypt = base62_decode(add_base62_equals(this.$route.query.beforeUrl));
			
			let decryptObject = JSON.parse(decrypt);
			
			let query = decryptObject.query;
			for(let q in query){
				query[q] = decodeURIComponent(query[q]);
			}
			this.sourceUrlObject = {
				path : decryptObject.path,
				query :query
			}
		}
		//初始化
		this.queryHelp();
	},
	methods : {
		//查询帮助
	    queryHelp: function(){
	        let _self = this;
			
			_self.help = '';
			
			_self.$ajax.get('control/help/manage', {
			    params: {
			    	method : 'view',
			    	helpId : _self.id,
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
			    		_self.help = returnValue.data;
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
	}
});
</script>