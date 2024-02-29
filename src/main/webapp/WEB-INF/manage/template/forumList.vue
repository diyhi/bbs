<!-- 版块列表 -->
<template id="forumList-template">
	<div>
		<div class="main">
			<div class="nav-breadcrumb">
				<el-breadcrumb separator-class="el-icon-arrow-right">
					<el-breadcrumb-item :to="{ path: '/admin/control/template/list' }">模板列表</el-breadcrumb-item>
					<el-breadcrumb-item :to="{ path: '/admin/control/layout/list', query:{ dirName : $route.query.dirName,page: ($route.query.sourcePage != undefined ? $route.query.sourcePage:'')}}">布局列表</el-breadcrumb-item>
					<el-breadcrumb-item>{{templates.name}} [{{templates.dirName}}]</el-breadcrumb-item>
					<el-breadcrumb-item>版块列表</el-breadcrumb-item>
				</el-breadcrumb>
			</div>
			<div class="navbar">
				<el-button type="primary" plain size="small" :disabled="publicForum ==true" @click="$router.push({path: '/admin/control/forum/manage/add',query:{layoutId:$route.query.layoutId, dirName : $route.query.dirName,sourcePage:($route.query.sourcePage != undefined ? $route.query.sourcePage:''),page:($route.query.page != undefined ? $route.query.page:'')}});">添加版块</el-button>
			</div>
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" @selection-change="handleSelectionChange" stripe empty-text="没有内容">
					<el-table-column prop="name" label="版块名称" align="center" ></el-table-column>
					<el-table-column prop="module" label="选择模块" align="center" ></el-table-column>
					<el-table-column label="调用方式" align="center" width="120">
						<template #default="scope">
							<span v-if="scope.row.invokeMethod == 1">引用代码</span>
							<span v-if="scope.row.invokeMethod == 2">调用对象</span>
				    	</template>
					</el-table-column>
					<el-table-column label="调用代码" align="center" width="120">
						<template #default="scope">
							<el-popover effect="light" trigger="hover" placement="top">
					        	<template #default>
					        		<span v-if="referenceCodeList.get(scope.row.id)">{{referenceCodeList.get(scope.row.id)}}</span>
					        	</template>
					        	<template #reference>
					          		<div>
										<!-- 引用代码 -->
										<span class="button-blue" v-if="layout.returnData == 0 && layout.type != 6 && scope.row.invokeMethod == 1" @click="copyText($event,scope.row)">复制</span>
										<!-- 调用对象  -->
										<span class="button-blue" v-if="scope.row.invokeMethod == 2" @click="copyText($event,scope.row)">复制</span>
									</div>
					        	</template>
					        </el-popover>
				    	</template>
					</el-table-column>
					<el-table-column label="操作" align="center" width="250">
						<template #default="scope">
							<el-button-group>
								<el-button type="primary" size="mini" v-if="layout.returnData == 0" @click="editForumSourceUI($event,scope.row)">源码编辑</el-button>
								<el-button type="primary" size="mini" v-if="layout.returnData == 1" @click="editForumSourceUI($event,scope.row)">示例程序</el-button>
								<el-button type="primary" size="mini" @click="$router.push({path: '/admin/control/forum/manage/edit', query:{ layoutId : $route.query.layoutId,forumId : scope.row.id,dirName : $route.query.dirName,sourcePage:($route.query.sourcePage != undefined ? $route.query.sourcePage:''),page:($route.query.page != undefined ? $route.query.page:'')}})">修改</el-button>
								<el-button type="primary" size="mini" @click="deleteForum($event,scope.row)">删除</el-button>
							</el-button-group>	
				    	</template>
					
					</el-table-column>
				</el-table>
				<div class="pagination-wrapper" v-if="isShowPage" >
					<el-pagination background  @current-change="page" :current-page="currentpage"  :page-size="maxresult" layout="total, prev, pager, next,jumper" :total="totalrecord"></el-pagination>
				</div>
			</div>
			<el-dialog title="源码编辑" v-model="editForumSourceForm" :before-close="handleEditForumSourceFormClose" width="100%" fullscreen="true" :close-on-click-modal="false" :close-on-press-escape="false" :destroy-on-close="true">
				<div class="editForumSourceModule">
					<div class="forumSource-container">
						<el-row >
							<el-col :span="24"><div class="nodeName">{{nodeName}}.html</div></el-col>
						</el-row>
						
						
						<el-tabs v-model="activeName" @tab-click="handleEditForumSourceFormClick">
						    <el-tab-pane label="电脑版" name="pc">
						    	<div class="code" >
									<textarea ref="pc_code" v-if="isPCHtmlExist">{{pc_html}}</textarea>
									<div class="notExistPrompt" v-if="!isPCHtmlExist">电脑版 {{nodeName}}.html 文件不存在</div>
								</div>
							</el-tab-pane>
						    <el-tab-pane label="移动版" name="wap">
						    	<div class="code">
									<textarea ref="wap_code" v-if="isWapHtmlExist">{{wap_html}}</textarea>
									<div class="notExistPrompt" v-if="!isWapHtmlExist">移动版 {{nodeName}}.html 文件不存在</div>
								</div>
						    </el-tab-pane>
						</el-tabs>
						
						
						<el-row>
							<el-col :span="24">
								 <el-button class="submitButton" type="primary" @click="editForumCode_submitForm" :disabled="submitForm_disabled">提交</el-button>
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
			</el-dialog>
		</div>
		 
	</div>
</template>

<script>
//版块列表
export default({
	name: 'forumList',//组件名称，keep-alive缓存需要本参数
	template : '#forumList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
			
			layoutId :'',
			dirName :'',
			layout :'',
			templates :'',
			publicForum:false,//公共版块 true:已有一个以上版块   false:还未有版块

			referenceCodeList:new Map(),//引用代码集合
			
		    totalrecord : 0, //总记录数
		    currentpage : 1, //当前页码
			totalpage : 1, //总页数
			maxresult: 12, //每页显示记录数
			isShowPage:false,//是否显示分页 maxresult没返回结果前就显示会导致分页栏显示页码错误

			id :'',
			nodeName: '',
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
			editForumSourceForm:false,//是否显示源码编辑表单
			
			error : {
			},
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	
	created : function created() {
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);

		if(this.$route.query.layoutId != undefined && this.$route.query.layoutId != ''){
			this.layoutId = this.$route.query.layoutId;
		}
		if(this.$route.query.dirName != undefined && this.$route.query.dirName != ''){
			this.dirName = this.$route.query.dirName;
		}
		if(this.$route.query.page != undefined && this.$route.query.page != ''){
			this.currentpage = this.$route.query.page;
		}
		
		CodeMirror.commands.autocomplete = function(cm) {
			CodeMirror.simpleHint(cm, CodeMirror.javascriptHint); 
		};
		
		//初始化
		this.queryForumList();
	},
	methods : {
		//查询版块列表
		queryForumList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			_self.$ajax.get('control/forum/list', {
			    params: {
			    	layoutId:_self.layoutId,
			    	dirName :_self.dirName,
			    	page :  _self.currentpage
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
			    			if(key == "pageView"){
			    				let pageView = mapData[key];
					    		let list = pageView.records;
					    		if(list != null && list.length >0){
					    			_self.tableData = list;
					 				
					    			for(let i=0; i<list.length; i++){
					    				let forum = list[i];
					    				
					    				
										if(_self.layout.returnData == 0 && _self.layout.type != 6 && forum.invokeMethod == 1){
										//	let referenceCode = "&lt;@include action=&quot;&#36;&#123;"+layout.referenceCode+"&#125;&quot;/&gt;";
											let referenceCode = '<@include action="${'+ forum.referenceCode +'}"/>';
					
											_self.referenceCodeList.set(forum.id,referenceCode);
					    				}
										if(forum.invokeMethod == 2){
					    					let pos=forum.module.lastIndexOf("_");
	   										let moduleCode = forum.module.substr(0,pos);
					    					let referenceCode = '<@object action="${'+forum.referenceCode+'}"><#assign value = '+moduleCode+'></@object>';
					    					_self.referenceCodeList.set(forum.id,referenceCode);
					    				}
					    			}

					    			
					    			_self.totalrecord = parseInt(pageView.totalrecord);//服务器返回的long类型已转为String类型
					    			_self.currentpage = pageView.currentpage;
									_self.totalpage = parseInt(pageView.totalpage);//服务器返回的long类型已转为String类型
									_self.maxresult = pageView.maxresult;
									_self.isShowPage = true;//显示分页栏
									
					    		}
			    			}else if(key == "publicForum"){
			    				_self.publicForum = mapData[key];
			    			}else if(key == "layout"){
			    				_self.layout = mapData[key];
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
		//分页
		page: function(page) {
			//删除缓存
			this.$store.commit('setCacheNumber');
			this.$router.push({
				path: '/admin/control/forum/list', 
				query:{ 
					layoutId:this.layoutId,
			    	dirName :this.dirName,
			    	sourcePage:(this.$route.query.sourcePage != undefined ? this.$route.query.sourcePage:''),
					page : page
				}
			});
	    },
		//复制文本
	    copyText: function(event,row) {
	    	let value = this.referenceCodeList.get(row.id);
	    	if(value){
	    		var tempInput = document.createElement("input");
		        tempInput.style = "position: absolute; left: -1000px; top: -1000px";
		        tempInput.value = value;
		        document.body.appendChild(tempInput);
		        tempInput.select();
		        document.execCommand("copy");
		        document.body.removeChild(tempInput);
	    	}
	    },
	    
	    
	    
	  	//源码编辑UI
	    editForumSourceUI : function(event,row) {
	    	let _self = this;
    		_self.editForumSourceForm = true;
    		_self.id = row.id;
    		_self.nodeName= row.module;
    	
    		_self.$ajax.get('control/forumCode/manage', {
			    params: {
			    	method:'source',
			    	dirName:_self.dirName,
			    	layoutId:row.layoutId,
			    	forumId:row.id,
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
    	//提交源码编辑
		editForumCode_submitForm : function() {
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
				formData.append('forumId', _self.id);
			}
			if(_self.dirName != null){
				formData.append('dirName', _self.dirName);
			}
			
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/forumCode/manage?method=source&a=a',//a=a参数的作用是仅增加连接符&
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
			    		_self.editForumSourceForm = false;
			    		
			    		_self.queryForumList();
						
						_self.handleEditForumSourceFormClose(function(){});
			    		
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
    	//处理源码编辑表单关闭
    	handleEditForumSourceFormClose : function(done) {
    		if(this.editor_pc != ""){
    			this.editor_pc.toTextArea();//删除编辑器，并恢复原始文本区（内容与编辑器的当前内容保持一致）
    			this.editor_pc = "";
    		}
    		if(this.editor_wap != ""){
    			this.editor_wap.toTextArea();//删除编辑器，并恢复原始文本区（内容与编辑器的当前内容保持一致）
    			this.editor_wap = "";
    		}
    		this.activeName = "pc";
    		this.activeDoc='example';
    		
    		if(this.example_editor != null && this.example_editor.length >0){
    			for(let i =0; i <this.example_editor.length; i++ ){
    				let editor = this.example_editor[i];
    				editor.toTextArea();//删除编辑器，并恢复原始文本区（内容与编辑器的当前内容保持一致）
    			}
    			this.example_editor = [];
    		}
    		if(this.common_editor != null && this.common_editor.length >0){
    			for(let i =0; i <this.common_editor.length; i++ ){
    				let editor = this.common_editor[i];
    				editor.toTextArea();//删除编辑器，并恢复原始文本区（内容与编辑器的当前内容保持一致）
    			}
    			this.common_editor = [];
    		}
    		
			done();
		},
				
				
		//删除版块
	    deleteForum : function(event,row) {
			//强制失去焦点
			let target = event.target;
			// 根据button组件内容 里面包括一个span标签，如果设置icon，则还包括一个i标签，其他情况请自行观察。
    		// 所以，在我们点击到button组件上的文字也就是span标签上时，直接执行e.target.blur()不会生效，所以要加一层判断。
        	if(target.nodeName == 'SPAN' || target.nodeName == 'I'){
            	target = event.target.parentNode;
       		}
        	target.blur();
			
			
	    	let _self = this;
	    	_self.$confirm('此操作将删除该项, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	formData.append('forumId', row.id);
		 
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/forum/manage?method=delete&a=a',//a=a参数的作用是仅增加连接符&
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
				    		_self.$message.success("删除成功");
				    		_self.queryForumList();
				    	}else if(returnValue.code === 500){//错误
				    		
				    		let errorMap = returnValue.data;
				    		let htmlContent = "";
				    		let count = 0;
				    		for (let key in errorMap) {   
				    			count++;
				    			htmlContent += "<p>"+count + ". " + errorMap[key]+"</p>";
				    			
				    	    }
				    		_self.$alert(htmlContent, '错误', {
				    			showConfirmButton :false,
				    			dangerouslyUseHTMLString: true
				    		})
				    		.catch(function (error) {
								console.log(error);
							});
				    		
				    		
				    	}
				    }
				})
				.catch(function (error) {
					console.log(error);
				});
	        	
	        }).catch((error) => {
	        	console.log(error);
	        });
	    	
	    	
	    	
	    	
	    }
	}
});
</script>
