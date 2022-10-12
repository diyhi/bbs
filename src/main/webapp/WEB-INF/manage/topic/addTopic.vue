<!-- 添加话题 -->
<template id="addTopic-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/topic/list', query:{ visible : ($route.query.visible != undefined ? $route.query.visible:''),page:($route.query.page != undefined ? $route.query.page:'')}})">返回</el-button>
			</div>
			<div class="data-form" >
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="标题" :required="true" :error="error.title" >
						<el-input v-model.trim="title" maxlength="100" clearable="true" show-word-limit></el-input>
					</el-form-item>
					<el-form-item label="标签" :required="true" :error="error.tagId">
						<el-select v-model="tagIdGroup" @focus="queryTagList" @change="selectedTag" no-match-text="还没有标签" placeholder="选择标签">
							<el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"></el-option>
						</el-select>
					</el-form-item>
					<el-form-item label="排序" :required="true" :error="error.sort">
						<el-input-number v-model="sort" ref="sort_ref" controls-position="right" @change="handleChange" :min="0" :max="999999999"></el-input-number>
						<div class="form-help" >数字越大越在前</div>
					</el-form-item>
					<el-form-item label="允许评论" :required="true" :error="error.allow">
						<el-switch v-model="allow" ></el-switch>
					</el-form-item>
					<el-form-item label="精华" :error="error.essence">
						<el-switch v-model="essence" ></el-switch>
					</el-form-item>
					<el-form-item label="状态" :required="true" :error="error.status">
						<el-radio-group v-model="status">
						    <el-radio :label="10">待审核</el-radio>
						    <el-radio :label="20">已发布</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="内容" :required="true" :error="error.content">
						<textarea ref="content" style="width:99%;height:400px;visibility:hidden;"></textarea>
					</el-form-item>
					<el-form-item>
					    <el-button type="primary" class="submitButton" @mousedown.native="submitForm" :disabled="submitForm_disabled">提交</el-button>
					</el-form-item>
				</el-form>
				
				
			</div>
		</div>
	</div>
</template>

<script>
//添加话题
export default({
	name: 'addTopic',//组件名称，keep-alive缓存需要本参数
	template : '#addTopic-template',
	inject:['reload'], 
	data : function data() {
		return {
			title :'',
			tagId :'',//标签Id
			sort : 0,
			allow : true,
			essence: false,
			status :20,
			content :'',
			
			isAllowLoadTagGroup:true,//是否允许加载标签组
			tagIdGroup :[],//标签Id组
			loading :false,//是否正在从远程获取数据
			options: [],//Select 选择器标签数据
			
			error : {
				title :'',
				tagId :'',
				sort :'',
				essence :'',
				content :'',
			},
			
			userName :'',//用户名称
			userGradeList :[],//会员等级
			
			addTopicEditor :'',//添加话题编辑器
			addTopicEditorCreateParameObject :{},//添加话题编辑器的创建参数
			
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	created : function created() {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		
		
		this.loadAddTopic();
		
		
	},
	//keep-alive 进入时
	activated: function () {
		if (Object.keys(this.addTopicEditorCreateParameObject).length != 0) {//不等于空
			//创建富文本编辑器
			this.addTopicEditor = createEditor(
				this.addTopicEditorCreateParameObject.ref, 
				this.addTopicEditorCreateParameObject.availableTag, 
				this.addTopicEditorCreateParameObject.uploadPath, 
				this.addTopicEditorCreateParameObject.userGradeList
			);
		}
	},
	// keep-alive 离开时
	deactivated : function () {
		this.addTopicEditor.remove();
	},
	methods : {
		//加载添加话题页
	    loadAddTopic: function() {
	    	let _self = this;
	      
			_self.$ajax.get('control/topic/manage', {
			    params: {
			    	method : 'add'
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
			    		let returnValueMap = returnValue.data;
		    			for(let key in returnValueMap){
		    				if(key == "userName"){
		    					_self.userName = returnValueMap[key];
		    				}else if(key == "userGradeList"){
		    					_self.userGradeList = returnValueMap[key];
		    				}
		    			}
		    			
		    			let availableTag = ['source', '|', 'preview', 'template', 'code',
				        '|', 'justifyleft', 'justifycenter', 'justifyright',
				        'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent', 'subscript',
				        'superscript', 'clearhtml', 'quickformat', 'selectall', '|',
				        'formatblock', 'fontname',  'fontsize','fullscreen',  '/', 'forecolor', 'hilitecolor', 'bold',
				        'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', '|', 'image', 'multiimage',
				         'media','embedVideo','uploadVideo', 'insertfile','emoticons','baidumap', 'table', 'hr',   'pagebreak',
				         'link', 'unlink','hide','hidePassword','hideComment','hideGrade','hidePoint'];
		    			let uploadPath = "control/topic/manage?method=upload&userName="+_self.userName+"&isStaff=true";
			    		//创建富文本编辑器
						_self.addTopicEditor = createEditor(_self.$refs.content, availableTag, uploadPath, _self.userGradeList);
						_self.addTopicEditorCreateParameObject = {
			    				ref:_self.$refs.content,
			    				availableTag:availableTag,
			    				uploadPath:uploadPath,
			    				userGradeList:_self.userGradeList
			    			}
						
						
			    	}else if(returnValue.code === 500){//错误
			    		
			    		
			    	}
			    }
			})
			.catch(function (error) {
				console.log(error);
			});
	    
	    },
	
	
		//查询标签
	    queryTagList: function(event) {
	        let _self = this;
	        if(!_self.isAllowLoadTagGroup){
	        	return;
	        }
			
			_self.$ajax.get('control/tag/manage', {
			    params: {
			    	method : 'allTag'
			    }
			})
			.then(function (response) {
				_self.isAllowLoadTagGroup = false;
				if(response == null){
					return;
				}
			    let result = response.data;
			    if(result){
			    	let returnValue = JSON.parse(result);
			    	if(returnValue.code === 200){//成功
			    		_self.options = [];
			    		let tagList = returnValue.data;
			    		if(tagList != null && tagList.length >0){
			    			for(let i=0; i<tagList.length; i++){
			    				let tag = tagList[i];
				    			let obj =new Object();
				    	    	obj.value = tag.id;
				    	    	obj.label = tag.name;
				    	    	_self.options.push(obj);
			    			}
			    		}
			    	}else if(returnValue.code === 500){//错误
			    		
			    		
			    	}
			    }
			})
			.catch(function (error) {
				console.log(error);
			});
	    },
	    
	    
	    //选中标签
	    selectedTag: function(val) { 
	   
	        this.tagId = val;
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
			if(_self.title != null && _self.title != ''){
				formData.append('title', _self.title);
				
			}
			
			if(_self.tagId != null && _self.tagId != ''){
				formData.append('tagId', _self.tagId); 
			}
			if(_self.sort != null && _self.sort >=0){
				formData.append('sort', _self.$refs.sort_ref.displayValue);
			}
			formData.append('allow', _self.allow);
			formData.append('status', _self.status);
			formData.append('essence', _self.essence);
			if(_self.$refs.content.value != null && _self.$refs.content.value !=''){
				formData.append('content', _self.$refs.content.value);
			}
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/topic/manage?method=add',
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
							path : '/admin/control/topic/list',
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