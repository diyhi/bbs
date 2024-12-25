<!-- 修改话题 -->
<template id="editHelp-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/help/manage/view', query:{ visible:($route.query.visible != undefined ? $route.query.visible:''),helpView_beforeUrl:($route.query.helpView_beforeUrl != undefined ? $route.query.helpView_beforeUrl:''),helpId :$route.query.helpId, page:($route.query.helpPage != undefined ? $route.query.helpPage:'')}})">返回</el-button>
			</div>
			<div class="data-form" >
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item label="名称" :required="true" :error="error.name" >
						<el-input v-model.trim="name" maxlength="50" clearable="true" show-word-limit></el-input>
					</el-form-item>
					<el-form-item label="选择分类" :required="true"  :error="error.helpTypeId">
						<el-select ref="helpType_ref" v-model="helpTypeIdGroup" @focus="loadHelpType" multiple placeholder="请选择">
							<el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"></el-option>
						</el-select>
					</el-form-item>
					<el-form-item label="内容" :required="true" :error="error.content">
						<textarea ref="content" style="width:99%;height:400px;visibility:hidden;"></textarea>
					</el-form-item>
					<el-form-item>
					    <el-button type="primary" class="submitButton" @click="submitForm" :disabled="submitForm_disabled">提交</el-button>
					</el-form-item>
				</el-form>
				
				<el-dialog title="选择分类" v-model="helpType_form">
					<div class="dialog-nav-breadcrumb">
						<el-breadcrumb separator-class="el-icon-arrow-right">
							<el-breadcrumb-item @click="queryHelpTypeList(1,'')">全部标签</el-breadcrumb-item>
							<el-breadcrumb-item v-for="(value, key) in navigation" @click="queryHelpTypeList(1,key)">{{value}}</el-breadcrumb-item>
							
						</el-breadcrumb>
					</div>
					<div class="dialog-data-table" >
						<el-table :data="tableData" @cell-click="cellExpandRow" :show-header="false" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
							<el-table-column label="选择" align="right" width="50">
								<template #default="scope">
									<el-radio v-model="helpTypeId" v-if="scope.row.childNodeNumber ==0" :label="helpTypeIdList[scope.$index]" >&nbsp;</el-radio>
						    	</template>
							</el-table-column>
							<el-table-column label="分类名称">
								<template #default="scope">
									<i class="icon icon-folder el-icon-folder" v-if="scope.row.childNodeNumber >0"></i>
									<i class="icon icon-file el-icon-document" v-if="scope.row.childNodeNumber ==0"></i>{{scope.row.name}}
						    	</template>
							</el-table-column>
						</el-table>
						<div class="pagination-wrapper" v-if="isShowPage">
							<el-pagination background  @current-change="page" :current-page="currentpage"  :page-size="maxresult" layout="total, prev, pager, next,jumper" :total="totalrecord"></el-pagination>
						</div>
					</div>
				</el-dialog>
			</div>
		</div>
	</div>
</template>

<script>
//修改话题
export default({
	name: 'editHelp',//组件名称，keep-alive缓存需要本参数
	template : '#editHelp-template',
	inject:['reload'], 
	data : function data() {
		return {
			id :'',
			name :'',
			helpTypeId :'',//分类Id
			content :'',
			
			
			
			helpTypeIdGroup :[],//分类Id组
			options:[],
			tableData: [],//表格内容
			parentId : '',//父Id
			helpTypeIdList: [],//可选择Id集合
			totalrecord : 0, //总记录数
		    currentpage : 1, //当前页码
			totalpage : 1, //总页数
			maxresult: 12, //每页显示记录数
			isShowPage:false,//是否显示分页 maxresult没返回结果前就显示会导致分页栏显示页码错误
			helpType_form:false,//是否显示问题标签表单
			navigation: '',
			
			
			error : {
				name :'',
				helpTypeId :'',//分类Id
				content :'',
			},
			
			userName :'',//用户名称
			
			addHelpEditor :'',//添加帮助编辑器
			addHelpEditorCreateParameObject :{},//添加帮助编辑器的创建参数
			
			submitForm_disabled:false,//提交按钮是否禁用
		};
	},
	created : function created() {
		//当前路由组件名this.$router.currentRoute.value.name
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		if(this.$route.query.helpId != undefined && this.$route.query.helpId != ''){
			this.id = this.$route.query.helpId;
		}
		
		
		this.loadAddHelp();
		
		
	},
	//keep-alive 进入时
	activated: function () {
		if (Object.keys(this.addHelpEditorCreateParameObject).length != 0) {//不等于空
			//创建富文本编辑器
			this.addHelpEditor = createEditor(
				this.addHelpEditorCreateParameObject.ref, 
				this.addHelpEditorCreateParameObject.availableTag, 
				this.addHelpEditorCreateParameObject.uploadPath, 
				this.addHelpEditorCreateParameObject.userGradeList
			);
		}
	},
	// keep-alive 离开时
	deactivated : function () {
		if(this.addHelpEditor != ''){
			this.addHelpEditor.remove();
		}
	},
	methods : {
		//加载添加帮助页
	    loadAddHelp: function() {
	    	let _self = this;
	      
			_self.$ajax.get('control/help/manage', {
			    params: {
			    	method : 'edit',
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
			    		let help = returnValue.data;
			    		_self.name = help.name;
			    		_self.helpTypeId = help.helpTypeId;
			    		_self.$refs.content.value = help.content;
			    		
			    		_self.helpTypeIdGroup.push(help.helpTypeId);
		    			let obj =new Object();
		    			obj.value = help.helpTypeId;
		    			obj.label = help.helpTypeName;
		    			_self.options.push(obj);
			    		
		    			let availableTag = ['source', '|', 'preview', 'template', 'code', 
		    				        '|', 'justifyleft', 'justifycenter', 'justifyright',
		    				        'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent', 'subscript',
		    				        'superscript', 'clearhtml', 'quickformat', 'selectall', '|', 
		    				        'formatblock', 'fontname', 'fontsize','fullscreen', '/', 'forecolor', 'hilitecolor', 'bold',
		    				        'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', '|', 'image', 'multiimage',
		    				         'media','embedVideo','uploadVideo', 'insertfile','emoticons', 'table', 'hr',   'pagebreak',
		    				         'link', 'unlink'];
		    			let uploadPath = "control/help/manage?method=upload&userName="+_self.userName+"&isStaff=true";
			    		//创建富文本编辑器
						_self.addHelpEditor = createEditor(_self.$refs.content, availableTag, uploadPath, null);
						_self.addHelpEditorCreateParameObject = {
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
	    
		//在线帮助列表 点击单元格选择
  		cellExpandRow : function(row,column,event,cell) {
  			if(column.label=="选择"){
  				if(row.childNodeNumber ==0){
  					this.helpTypeIdGroup.push(row.id);
  					
  			        this.helpTypeId = row.id;
  			        
  					let obj =new Object();
  					obj.value = row.id;
  					obj.label = row.name;
  					this.options.length = 0;//清空
  					this.options.push(obj);
  					
  					
  					//因为只允许选中一个，所以将已选择的清空
  			    	if (Object.keys(this.helpTypeIdGroup).length >1) {
  			        	this.helpTypeIdGroup.shift();//从Array 头部移除元素
  			        }
  			        
  			        this.helpType_form = false;
  				}
  			}else if(column.label=="分类名称"){
  				if(row.childNodeNumber >0){
  					this.parentId = row.id;
  					this.queryHelpTypeList(1,row.id);
  				}
  			}
  		},
	
		//加载帮助分类
	    loadHelpType: function() { 
	    	
	    	this.helpType_form = true;
	       	this.$refs.helpType_ref.blur();//使Select选择器失去焦点，并隐藏下拉框
	      
	       	if(this.helpTypeIdGroup.length ==0){
	       		this.helpTypeId = "";
	       		this.options.length = 0;//清空	
	       	}
	       	
	       	
	       	//清空数据
			this.totalrecord = 0;//服务器返回的long类型已转为String类型
   			this.currentpage = 1;
			this.totalpage = 1;//服务器返回的long类型已转为String类型
			this.maxresult = 12;
			this.isShowPage = false;//显示分页栏
			this.parentId = '';
	       	
	        this.queryHelpTypeList(1,'');
		},
		//分页
		page : function(page) {
			
			this.queryHelpTypeList(page, this.parentId);
		},
	    
	    
		//查询分类
	    queryHelpTypeList: function(page,parentId) {
	        let _self = this;
	        
	        _self.tableData = [];
			_self.helpTypeIdList = [];
			_self.navigation = '';
			
			_self.parentId = parentId;
	        
	        
			_self.$ajax.get('control/helpType/manage', {
			    params: {
			    	method : 'helpTypePageSelect_move',
			    	parentId : parentId,
			    	page : page
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
			    				let typeView = mapData[key];
					    		let typeList = typeView.records;
					    		if(typeList != null && typeList.length >0){
					    			for(let i = 0; i<typeList.length; i++){
					    				let type = typeList[i];
					    				_self.helpTypeIdList.push(type.id);
					    			
					    			}
					    		
					    		
					    			_self.tableData = typeList;
					 
					    			_self.totalrecord = parseInt(typeView.totalrecord);//服务器返回的long类型已转为String类型
					    			_self.currentpage = typeView.currentpage;
									_self.totalpage = parseInt(typeView.totalpage);//服务器返回的long类型已转为String类型
									_self.maxresult = typeView.maxresult;
									_self.isShowPage = true;//显示分页栏
					    		}
			    			}else if(key == "navigation"){
			    				_self.navigation = mapData[key];
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
	    
	    
	    //选中分类
	    selectedHelpType: function(val) { 
	   
	        this.helpTypeId = val;
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
			formData.append('helpId', _self.id);
			
			if(_self.name != null){
				formData.append('name', _self.name);
				
			}
			
			if(_self.helpTypeIdGroup != null && _self.helpTypeIdGroup.length >0){
				for(let i=0; i<_self.helpTypeIdGroup.length; i++){
					let typeId = _self.helpTypeIdGroup[i];
					formData.append('helpTypeId', typeId);
					break;
				}
			}
			
			if(_self.$refs.content.value != null && _self.$refs.content.value !=''){
				formData.append('content', _self.$refs.content.value);
			}
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/help/manage?method=edit&t=',
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
							path : '/admin/control/help/manage/view',
							query:{
								visible : (_self.$route.query.visible != undefined ? _self.$route.query.visible:''),
								page:(_self.$route.query.page != undefined ? _self.$route.query.page:'')
							}
						});
						
						_self.$router.push({
							path : '/admin/control/help/manage/view',
							query:{ 
								visible:(_self.$route.query.visible != undefined ? _self.$route.query.visible:''),
								helpView_beforeUrl:(_self.$route.query.helpView_beforeUrl != undefined ? _self.$route.query.helpView_beforeUrl:''),
								helpId :_self.$route.query.helpId, 
								page:(_self.$route.query.helpPage != undefined ? _self.$route.query.helpPage:'')
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
	    }
	}
});

</script>