<!-- 留言查看 -->
<template id="feedbackView-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small"  @click="$router.push({path: sourceUrlObject.path, query:sourceUrlObject.query})">返回</el-button>
			</div>
			<div class="data-view" >
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">称呼：</div></el-col>
					<el-col :span="20"><div class="content">{{feedback.name}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">留言时间：</div></el-col>
					<el-col :span="20"><div class="content">{{feedback.createDate}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">IP：</div></el-col>
					<el-col :span="20"><div class="content">{{feedback.ipAddress}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">联系方式：</div></el-col>
					<el-col :span="20"><div class="content">{{feedback.contact}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex">
					<el-col :span="4"><div class="name">内容：</div></el-col>
					<el-col :span="20"><div class="content">{{feedback.content}}</div></el-col>
				</el-row>
			</div>
		</div>
	</div>
</template>

<script>
//留言查看
export default({
	name: 'feedbackView',//组件名称，keep-alive缓存需要本参数
	template : '#feedbackView-template',
	inject:['reload'], 
	data : function data() {
		return {
			feedbackId:'',
			feedback:'',
			
			error:{},
			
			sourceUrlObject:{},//来源URL对象
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
		
		if(this.$route.query.feedbackId != undefined && this.$route.query.feedbackId != ''){
			this.feedbackId = this.$route.query.feedbackId;
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
		this.queryFeedback();
	},
	methods : {
		//查询留言
	    queryFeedback: function(){
	        let _self = this;
			
			_self.feedback = '';
			
			_self.$ajax.get('control/feedback/manage', {
			    params: {
			    	method : 'view',
			    	feedbackId : _self.feedbackId,
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
			    		_self.feedback = returnValue.data;
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