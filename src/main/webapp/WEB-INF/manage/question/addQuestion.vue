<!-- 添加问题 -->
<template id="addQuestion-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/question/list', query:{ visible : ($route.query.visible != undefined ? $route.query.visible:''),page:($route.query.page != undefined ? $route.query.page:'')}})">返回</el-button>
			</div>
			<div class="data-form" >
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="标题" :required="true" :error="error.title" >
						<el-input v-model.trim="title" maxlength="150" clearable="true" show-word-limit></el-input>
					</el-form-item>
					<el-form-item label="标签" :required="true" :error="error.tagId">
						<el-select ref="question_tag_ref"  v-model="tagIdGroup" @remove-tag="processRemoveTag" @focus="loadQuestionTag"  style="width: 100%;" multiple :placeholder="select_placeholder" >
							<el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"></el-option>
						</el-select>
					</el-form-item>
					
					<el-form-item label="排序" :required="true" :error="error.sort">
						<el-input-number v-model="sort" ref="sort_ref" controls-position="right" :min="0" :max="999999999"></el-input-number>
						<div class="form-help" >数字越大越在前</div>
					</el-form-item>
					<el-form-item label="允许回答" :required="true" :error="error.allow">
						<el-switch v-model="allow" ></el-switch>
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
			
			<div class="selectQuestionTagModule">
				<el-dialog title="选择标签" v-model="popup_allTag" @close="closeQuestionTag">
					<div class="questionTagNavigation">
	            		<ul class="nav">
	            			<li class="nav-item" v-for="questionTag in allTagList" >
	            				<span :class="selectedTagClass(questionTag.id)"  @click="selectChildTag(questionTag.id)" >{{questionTag.name}}</span>
							</li>
	            		</ul>
	            		<!-- 二级标签 -->
						<div class="tab-content">
							<div class="tab-pane">
								<span :class="selectedChildTagClass(childQuestionTag.id)" @click="selectedTag(childQuestionTag)"  v-for="childQuestionTag in childTagList" >{{childQuestionTag.name}}</span>
							</div>
						</div>
					</div>
				</el-dialog>
			</div>
			
		</div>
	</div>
</template>

<script>
//添加问题
export default({
	name: 'addQuestion',//组件名称，keep-alive缓存需要本参数
	template : '#addQuestion-template',
	inject:['reload'], 
	data : function data() {
		return {
			title :'',
			tagId :'',//标签Id
			sort : 0,
			allow : true,
			status :20,
			content :'',
			
			tagIdGroup :[],//标签Id组
			options: [],//Select 选择器标签数据
			
			
			popup_allTag :false,//是否弹出问题标签表单
			allTagList: '',//全部标签
			selectedFirstTagId: '',//选中的一级标签Id
			childTagList: '',//选中一级标签的子标签集合
			select_placeholder: '请选择',
			
			error : {
				title :'',
				tagId :'',
				sort :'',
				content :'',
			},
			
			userName :'',//用户名称
			userGradeList :[],//会员等级
			
			addQuestionEditor :'',//添加问题编辑器
			addQuestionEditorCreateParameObject :{},//添加问题编辑器的创建参数
			
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	created : function created() {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		
		
		this.loadAddQuestion();
		
		
	},
	//keep-alive 进入时
	activated: function () {
		if (Object.keys(this.addQuestionEditorCreateParameObject).length != 0) {//不等于空
			//创建富文本编辑器
			this.addQuestionEditor = createEditor(
				this.addQuestionEditorCreateParameObject.ref, 
				this.addQuestionEditorCreateParameObject.availableTag, 
				this.addQuestionEditorCreateParameObject.uploadPath, 
				this.addQuestionEditorCreateParameObject.userGradeList
			);
		}
	},
	// keep-alive 离开时
	deactivated : function () {
		this.addQuestionEditor.remove();
	},
	methods : {
		//加载添加问题页
	    loadAddQuestion: function() {
	    	let _self = this;
	      
			_self.$ajax.get('control/question/manage', {
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
		    				}
		    			}
		    			
		    			let availableTag = ['source', '|', 'preview', 'template', 'code',
					   		 		        '|', 'justifyleft', 'justifycenter', 'justifyright',
					   				        'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent', 'subscript',
					   				        'superscript', 'clearhtml', 'quickformat', 'selectall', '|', 
					   				        'formatblock', 'fontname', 'fontsize','fullscreen', '/', 'forecolor', 'hilitecolor', 'bold',
					   				        'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', '|', 'image', 'multiimage',
					   				         'media','embedVideo','uploadVideo', 'insertfile','emoticons', 'table', 'hr',   'pagebreak',
					   				         'link', 'unlink'];
		    			let uploadPath = "control/question/manage?method=upload&userName="+_self.userName+"&isStaff=true";
			    		//创建富文本编辑器
						_self.addQuestionEditor = createEditor(_self.$refs.content, availableTag, uploadPath,null);
						_self.addQuestionEditorCreateParameObject = {
			    				ref:_self.$refs.content,
			    				availableTag:availableTag,
			    				uploadPath:uploadPath,
			    				userGradeList:null
			    			}
						
						
			    	}else if(returnValue.code === 500){//错误
			    		
			    		
			    	}
			    }
			})
			.catch(function (error) {
				console.log(error);
			});
	    
	    },
	
	
	  //加载问题标签
	    loadQuestionTag: function() { 
	    	
	    	this.popup_allTag = true;
	       	this.$refs.question_tag_ref.blur();//使Select选择器失去焦点，并隐藏下拉框
	       	if(this.tagIdGroup.length ==0){
	       		this.options.length = 0;//清空	
	       	}
	       	
	        this.queryAllTag();
		},
		//关闭问题标签弹出框
		closeQuestionTag: function() { 
	    	this.popup_allTag = false;

	    },
	  	//处理删除标签
	    processRemoveTag: function(val) { 
			if(this.tagIdGroup.length ==0){
				
  				this.select_placeholder = "请选择";
  			}
	    },
		//查询所有标签
		queryAllTag : function() {
			var _self = this;

			
			_self.$ajax.get('control/questionTag/manage', {
			    params: {
			    	method : 'allTag'
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
			    		let tagList = returnValue.data;
			    		if (tagList != null && tagList.length > 0) {
							_self.allTagList = tagList;
							
							for (var i= 0; i < tagList.length; i++) {
								var questionTag = tagList[i];
								//第一次选中第一个标签
								if(_self.selectedFirstTagId == ''){
									_self.childTagList = questionTag.childTag;
									_self.selectedFirstTagId = questionTag.id;
									break;
								}else{
									if(_self.selectedFirstTagId == questionTag.id){
										_self.childTagList = questionTag.childTag;
										break;
									}
								}
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
		//选择子标签
		selectChildTag : function(questionTagId) {
			var _self = this;
			_self.selectedFirstTagId = questionTagId;
			
			if(_self.allTagList != '' && _self.allTagList.length >0){
				for (var i= 0; i < _self.allTagList.length; i++) {
					var questionTag = _self.allTagList[i];
					
					if(questionTagId == questionTag.id){
						_self.childTagList = questionTag.childTag;
						
						//如果只有一个节点，则允许选择本标签
						if(questionTag.childNodeNumber == 0){
							_self.selectedTag(questionTag);
						}

						break;
					}
					
				}
				
			}
			
		},
		//选中标签
		selectedTag : function(childQuestionTag) {
			//判断是否重复选择,如果重复则取消选择
			if(this.options != null && this.options.length >0){
				for(var i=0; i<this.options.length; i++){
					var selectedTag = this.options[i];
					if(selectedTag.value == childQuestionTag.id){
						//删除标签
						this.deleteTag(selectedTag.value);
						return;
					}
				}
				
			}

			var o = new Object();
			o.value = childQuestionTag.id;
			o.label = childQuestionTag.name;

			this.options.push(o);
			
			
			this.tagIdGroup.push(childQuestionTag.id);
			
			this.select_placeholder = "";
		},
		//删除标签
		deleteTag : function(questionTagId) {
			if(this.options != null && this.options.length >0){
				for(let i=0; i<this.options.length; i++){
					let selectedTag = this.options[i];
					if(selectedTag.value == questionTagId){
						this.options.splice(i, 1);
						
						for(let j=0; j<this.tagIdGroup.length; j++){
							if(this.tagIdGroup[j] == questionTagId){
								this.tagIdGroup.splice(j, 1);
								break;
							}
							
						}
						
						if(this.tagIdGroup.length ==0){
							this.select_placeholder = "请选择";
						}
						return;
					}
				}
				
			}
		
			
			
		},
		//选中一级标签样式
		selectedTagClass : function(questionTagId) {
			var className = "nav-link";
			if(this.selectedFirstTagId == questionTagId){
				className += " active";
				
			}
			if(this.options != null && this.options.length >0){
				for(var i=0; i<this.options.length; i++){
					var selectedTag = this.options[i];
					if(selectedTag.value == questionTagId){
						className += " selected";
					}
				}
				
			}
			return className;
		},
		
		//选中二级标签样式
		selectedChildTagClass : function(questionTagId) {
			if(this.options != null && this.options.length >0){
				for(var i=0; i<this.options.length; i++){
					var selectedTag = this.options[i];
					if(selectedTag.value == questionTagId){
						return "child-tag selected";
					}
				}
				
			}
			return "child-tag";
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
			
			if(_self.tagIdGroup != null && _self.tagIdGroup.length >0){
				for(let i = 0; i <_self.tagIdGroup.length; i++){
					let tagId = _self.tagIdGroup[i];
					formData.append('tagId', tagId); 
				}
			}
			if(_self.sort != null){
				formData.append('sort', _self.$refs.sort_ref.displayValue);
			}
			formData.append('allow', _self.allow);
			formData.append('status', _self.status);
			if(_self.point != null){
				formData.append('point', _self.point);
			}
			if(_self.amount != null){
				formData.append('amount', _self.amount);
			}
			
			if(_self.$refs.content.value != null && _self.$refs.content.value !=''){
				formData.append('content', _self.$refs.content.value);
			}
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/question/manage?method=add',
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
							path : '/admin/control/question/list',
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