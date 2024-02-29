<!-- 布局代码编辑 -->
<template id="editLayoutCode-template">
	<div>
		<div class="main">
			<div class="nav-breadcrumb">
				<el-breadcrumb separator-class="el-icon-arrow-right">
					<el-breadcrumb-item :to="{ path: '/admin/control/template/list' }">模板列表</el-breadcrumb-item>
					<el-breadcrumb-item :to="{ path: '/admin/control/layout/list',query:{ dirName : $route.query.dirName} }">布局列表</el-breadcrumb-item>
					<el-breadcrumb-item>{{templates.name}} [{{templates.dirName}}]</el-breadcrumb-item>
					<el-breadcrumb-item>布局代码编辑</el-breadcrumb-item>
				</el-breadcrumb>
			</div>	
			<div class="editLayoutCodeModule">
				<div class="forumSource-container">
					<el-row >
						<el-col :span="24"><div class="layoutFile">{{layoutFile}}</div></el-col>
					</el-row>
					
					
					<el-tabs v-model="activeName" @tab-click="handleEditForumSourceFormClick">
					    <el-tab-pane label="电脑版" name="pc">
					    	<div class="code" >
								<textarea ref="pc_code" v-if="isPCHtmlExist">{{pc_html}}</textarea>
								<div class="notExistPrompt" v-if="!isPCHtmlExist">电脑版 {{layoutFile}}.html 文件不存在</div>
							</div>
						</el-tab-pane>
					    <el-tab-pane label="移动版" name="wap">
					    	<div class="code">
								<textarea ref="wap_code" v-if="isWapHtmlExist">{{wap_html}}</textarea>
								<div class="notExistPrompt" v-if="!isWapHtmlExist">移动版 {{layoutFile}}.html 文件不存在</div>
							</div>
					    </el-tab-pane>
					</el-tabs>
					
					
					<el-row>
						<el-col :span="24">
							 <el-button class="submitButton" type="primary" @click="editLayoutCode_submitForm" :disabled="submitForm_disabled">提交</el-button>
						</el-col>
					</el-row>
				</div>
				<el-row >
					<el-col :span="24">
						<div class="doc">
							<el-tabs v-model="activeDoc" type="card"  @tab-click="handleDocClick">
							    <el-tab-pane label="文档参数" name="example">
								    <div ref="example_doc" v-html="example"></div>
							    </el-tab-pane>
							    <el-tab-pane label="公共API" name="common">
							    	<div ref="common_doc" v-html="common"></div>
							    </el-tab-pane>
							</el-tabs>
						</div>
					</el-col>
				</el-row>
			</div>
			
		</div>
	</div>
</template>

<script>
//布局代码编辑
export default({
	name: 'editLayoutCode',//组件名称，keep-alive缓存需要本参数
	template : '#editLayoutCode-template',
	inject:['reload'], 
	data : function data() {
		return {
			id: '',
			dirName: '',
			templates: '',//模板
			
			layoutFile: '',
			editLayoutCode:false,//是否显示布局代码编辑表单
			submitForm_disabled:false,//提交按钮是否禁用
			
			error :{
				name: '',
				displayType: '',
				remark: '',
			},
			
			
			activeName : 'pc',//源码表单标签选择
			activeDoc : 'example',//文档选择common
			example: '',
			common: '',
			pc_html: '',
			wap_html: '',
			editor_pc:'',
			editor_wap:'',
			
			
			isPCHtmlExist:false,//是否存在电脑版html文件
			isWapHtmlExist:false,//是否存在手机版html文件
			example_editor:[],
			common_editor:[],
		};
	},
	
	created : function created() {
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		if(this.$route.query.dirName != undefined && this.$route.query.dirName != ''){
			this.dirName = this.$route.query.dirName;
		}
		if(this.$route.query.layoutId != undefined && this.$route.query.layoutId != ''){
			this.id = this.$route.query.layoutId;
		}
		CodeMirror.commands.autocomplete = function(cm) {
			CodeMirror.simpleHint(cm, CodeMirror.javascriptHint); 
		};
		
		
		//初始化
		this.queryLayoutCodeUI();
	},
	methods : {
    	//查询布局代码
	    queryLayoutCodeUI : function() {
	    	let _self = this;
    		
    		_self.$ajax.get('control/layout/manage', {
			    params: {
			    	method:'editLayoutCode',
			    	dirName:_self.dirName,
			    	layoutId:_self.id,
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
			    			if(key == "example"){
			    				_self.example = mapData[key];   				
			    			}else if(key == "common"){
			    				_self.common = mapData[key];
			    			}else if(key == "pc_html"){
			    				_self.pc_html = mapData[key];
			    			}else if(key == "wap_html"){
			    				_self.wap_html = mapData[key];
			    			}else if(key == "isPCHtmlExist"){
			    				_self.isPCHtmlExist = mapData[key];
			    			}else if(key == "isWapHtmlExist"){
			    				_self.isWapHtmlExist = mapData[key];
			    			}else if(key == "layout"){
			    				let layout = mapData[key];
			    				_self.layoutFile = layout.layoutFile;
			    				
			    			}else if(key == "templates"){
			    				_self.templates = mapData[key];
			    			}
			    		}
			    		
			    		_self.$nextTick(function() {
	    					if(_self.activeDoc == "example"){
	    						//markRaw 方法可以将原始数据标记为非响应式的
			    				const { markRaw } = Vue;
	    						let exampleCodeList = _self.$refs.example_doc.querySelectorAll("[name*='exampleCode']");
		    					for(let i = 0; i < exampleCodeList.length; ++i) {
									let exampleCode = exampleCodeList[i];
									let editor = markRaw(CodeMirror.fromTextArea(exampleCode, {
								      	lineNumbers: true,
								        mode: 'text/html',
								        indentUnit: 4,
								        extraKeys: {"Alt-/": "autocomplete"},
								        indentWithTabs: true,
								        autoCloseTags: true,
								 	}));
								    editor.setSize("100%","100%");//设置自适应高度 
									
									_self.example_editor.push(editor);
								}
	    					}
	    				});
			    		_self.$nextTick(function() {
	    					if(_self.activeDoc == "common"){
	    						//markRaw 方法可以将原始数据标记为非响应式的
			    				const { markRaw } = Vue;
	    						let exampleCodeList = _self.$refs.common_doc.querySelectorAll("[name*='exampleCode']");
		    					for(let i = 0; i < exampleCodeList.length; ++i) {
									let exampleCode = exampleCodeList[i];
									let editor = markRaw(CodeMirror.fromTextArea(exampleCode, {
								      	lineNumbers: true,
								        mode: 'text/html',
								        indentUnit: 4,
								        extraKeys: {"Alt-/": "autocomplete"},
								        indentWithTabs: true,
								        autoCloseTags: true,
								 	}));
								    editor.setSize("100%","100%");//设置自适应高度 
									
									_self.common_editor.push(editor);
								}
	    					}
	    				});
			    		if(_self.activeName == "pc" && _self.isPCHtmlExist){
			    			_self.$nextTick(function() {
			    				//markRaw 方法可以将原始数据标记为非响应式的
			    				const { markRaw } = Vue;
			    				_self.editor_pc = markRaw(CodeMirror.fromTextArea(_self.$refs.pc_code, {
							      	lineNumbers: true,
							        mode: 'text/html',
							        indentUnit: 4,
							        extraKeys: {"Alt-/": "autocomplete"},
							        indentWithTabs: true,
							        autoCloseTags: true,
							 	}));
							 //	_self.editor_pc.setValue(_self.pc_html);
							    _self.editor_pc.setSize("100%","100%");//设置自适应高度 
		    				
		    				});
					    }
			    		if(_self.activeName == "wap" && _self.isWapHtmlExist){
	    					_self.$nextTick(function() {
			    				//markRaw 方法可以将原始数据标记为非响应式的
			    				const { markRaw } = Vue;
			    				_self.editor_wap = markRaw(CodeMirror.fromTextArea(_self.$refs.wap_code, {
							      	lineNumbers: true,//是否在编辑器左侧显示行号
							        mode: 'text/html',
							        indentUnit: 4,//缩进单位，值为空格数，默认为2 
							        extraKeys: {"Alt-/": "autocomplete"},//给编辑器绑定快捷键
							        indentWithTabs: true,//在缩进时，是否需要把 n*tab宽度个空格替换成n个tab字符，默认为false
							        autoCloseTags: true,
							    }));
							    _self.editor_wap.setSize("100%","100%");//设置自适应高度 
		    				});
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
    	
    	},
    	//提交布局代码编辑
		editLayoutCode_submitForm : function() {
			let _self = this;
			_self.submitForm_disabled = true;
			
	        //清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			if(_self.editor_pc != null && _self.editor_pc != ''){
				formData.append('pc_code', _self.editor_pc.getValue());	
			}else{
				formData.append('pc_code', _self.pc_html);	
			}
			if(_self.editor_wap != null && _self.editor_wap != ''){
				formData.append('wap_code', _self.editor_wap.getValue());
			}else{
				formData.append('wap_code', _self.wap_html);
			}
			if(_self.id != null){
				formData.append('layoutId', _self.id);
			}
			if(_self.dirName != null){
				formData.append('dirName', _self.dirName);
			}
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/layout/manage?method=editLayoutCode&a=a',//a=a参数的作用是仅增加连接符&
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
		},
		//处理源码编辑表单选择
		handleEditForumSourceFormClick(tab, event) {
			let _self = this;

        	if(tab.props.name == "pc" && _self.isPCHtmlExist){
        		_self.$nextTick(function() {
        			if(_self.editor_pc == ""){
        				//markRaw 方法可以将原始数据标记为非响应式的
			    		const { markRaw } = Vue;
        				_self.editor_pc = markRaw(CodeMirror.fromTextArea(_self.$refs.pc_code, {
					      	lineNumbers: true,
					        mode: 'text/html',
					        indentUnit: 4,
					        extraKeys: {"Alt-/": "autocomplete"},
					        indentWithTabs: true,
					        autoCloseTags: true,
					 	}));
					    _self.editor_pc.setSize("100%","100%");//设置自适应高度 
        			}
   				});
        	
        	}
        	if(tab.props.name == "wap" && _self.isWapHtmlExist){
        		_self.$nextTick(function() {
    				if(_self.editor_wap == ""){
    					//markRaw 方法可以将原始数据标记为非响应式的
			    		const { markRaw } = Vue;
	    				_self.editor_wap = markRaw(CodeMirror.fromTextArea(_self.$refs.wap_code, {
					      	lineNumbers: true,//是否在编辑器左侧显示行号
					        mode: 'text/html',
					        indentUnit: 4,//缩进单位，值为空格数，默认为2 
					        extraKeys: {"Alt-/": "autocomplete"},//给编辑器绑定快捷键
					        indentWithTabs: true,//在缩进时，是否需要把 n*tab宽度个空格替换成n个tab字符，默认为false
					        autoCloseTags: true,
					    }));
					    _self.editor_wap.setSize("100%","100%");//设置自适应高度 
				    }
   				});
        	}
    	},
    	//处理文档表单选择
		handleDocClick(tab, event) {
			let _self = this;

			if(tab.props.name == "example"){
        		_self.$nextTick(function() {
        			if(_self.example_editor.length ==0){
        				//markRaw 方法可以将原始数据标记为非响应式的
			    		const { markRaw } = Vue;
        				let exampleCodeList = _self.$refs.example_doc.querySelectorAll("[name*='exampleCode']");
    					for(let i = 0; i < exampleCodeList.length; ++i) {
							let exampleCode = exampleCodeList[i];
							let editor = markRaw(CodeMirror.fromTextArea(exampleCode, {
						      	lineNumbers: true,
						        mode: 'text/html',
						        indentUnit: 4,
						        extraKeys: {"Alt-/": "autocomplete"},
						        indentWithTabs: true,
						        autoCloseTags: true,
						 	}));
						    editor.setSize("100%","100%");//设置自适应高度 
							
							_self.example_editor.push(editor);
						}
        			}
   				});
        	
        	}
        	if(tab.props.name == "common"){
	        	_self.$nextTick(function() {
	        		if(_self.common_editor.length ==0){
	       				//markRaw 方法可以将原始数据标记为非响应式的
			    		const { markRaw } = Vue;
	       				let exampleCodeList = _self.$refs.common_doc.querySelectorAll("[name*='exampleCode']");
	   					for(let i = 0; i < exampleCodeList.length; ++i) {
							let exampleCode = exampleCodeList[i];
							let editor = markRaw(CodeMirror.fromTextArea(exampleCode, {
						      	lineNumbers: true,
						        mode: 'text/html',
						        indentUnit: 4,
						        extraKeys: {"Alt-/": "autocomplete"},
						        indentWithTabs: true,
						        autoCloseTags: true,
						 	}));
						    editor.setSize("100%","100%");//设置自适应高度 
							
							_self.common_editor.push(editor);
						}
	       			}
       			});
        	}
		},
	}
});
</script>
