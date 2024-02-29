<!-- 问题搜索 -->
<template id="questionSearch-template">
	<div>
		<div class="main">
			<div class="search">
				<el-form :inline="true" label-width="auto" @keyup.enter.native="submitForm">
					<el-form-item :error="error.dataSource" >
						<el-tooltip content="选择数据源" placement="top">
							<el-radio-group v-model="dataSource" >
							  	<el-radio-button :label="1">全文索引</el-radio-button>
							    <el-radio-button :label="2">数据库</el-radio-button>
							</el-radio-group>
						</el-tooltip>
					</el-form-item>
					<el-form-item :error="error.keyword" >
						<el-tooltip content="不限制请留空" placement="top">
							<el-input v-model.trim="keyword" maxlength="50" clearable="true" placeholder="关键词"></el-input>
						</el-tooltip>
					</el-form-item>
					<el-tooltip content="不限制请留空" placement="top">
					<el-form-item :error="error.tagId">
						<el-select ref="question_tag_ref"  v-model="tagIdGroup" @remove-tag="processRemoveTag" @focus="loadQuestionTag"  multiple placeholder="请选择" >
							<el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"></el-option>
						</el-select>
					</el-form-item>
					</el-tooltip>
					<el-form-item :error="error.account" style="width: 180px;">
						<el-tooltip content="不限制请留空" placement="top">
							<el-input v-model.trim="account" maxlength="30" clearable="true" placeholder="账号"></el-input>
						</el-tooltip>
					</el-form-item>
					<el-tooltip content="不限制请留空" placement="top">
						<el-form-item :error="error.start_postTime" >
							<el-date-picker v-model="start_postTime_format" type="datetime" format="YYYY-MM-DD HH:mm" placeholder="发表问题起始时间" ></el-date-picker>
						</el-form-item>
					</el-tooltip>
					<el-tooltip content="不限制请留空" placement="top">
						<el-form-item :error="error.end_postTime" >
							<el-date-picker v-model="end_postTime_format" type="datetime" format="YYYY-MM-DD HH:mm" placeholder="发表问题结束时间"></el-date-picker>
						</el-form-item>
					</el-tooltip>
					<el-form-item >
					    <el-button type="primary" class="submitButton" @click="submitForm" >搜索</el-button>
					</el-form-item>
					
				</el-form>
				<div class="data-form">
				<el-dialog title="选择标签" v-model="questionTagGroup.question_tag_form">
					<div class="dialog-nav-breadcrumb">
						<el-breadcrumb separator-class="el-icon-arrow-right">
							<el-breadcrumb-item @click="queryQuestionTag(1,'')">全部标签</el-breadcrumb-item>
							<el-breadcrumb-item v-for="(value, key) in questionTagGroup.navigation" @click="queryQuestionTag(1,key)">{{value}}</el-breadcrumb-item>
							
						</el-breadcrumb>
					</div>
					<div class="dialog-data-table" >
						<el-table :data="questionTagGroup.tableData" @cell-click="cellExpandRow_questionTag" :show-header="false" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
							<el-table-column label="选择" align="right" width="50">
								<template #default="scope">
									<el-radio v-model="tagId" v-if="scope.row.childNodeNumber ==0" :label="questionTagGroup.tagIdList[scope.$index]" >&nbsp;</el-radio>
						    	</template>
							</el-table-column>
							<el-table-column label="标签名称">
								<template #default="scope">
									<i class="icon icon-folder el-icon-folder" v-if="scope.row.childNodeNumber >0"></i>
									<i class="icon icon-file el-icon-document" v-if="scope.row.childNodeNumber ==0"></i>{{scope.row.name}}
						    	</template>
							</el-table-column>
						</el-table>
						<div class="pagination-wrapper" v-if="questionTagGroup.isShowPage">
							<el-pagination background  @current-change="questionTagPage" :current-page="questionTagGroup.currentpage"  :page-size="questionTagGroup.maxresult" layout="total, prev, pager, next,jumper" :total="questionTagGroup.totalrecord"></el-pagination>
						</div>
					</div>
				</el-dialog>
				</div>
			</div> 
		
			<div class="navbar" >
				<el-button type="primary" plain size="small"  @click="reductionQuestion($event)">批量还原</el-button>
				<el-button type="primary" plain size="small"  @click="deleteQuestion($event)">批量删除</el-button>
			</div>
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" @selection-change="handleSelectionChange" stripe empty-text="没有内容">
					<el-table-column type="selection" ></el-table-column>
					<el-table-column prop="title" label="标题" min-width="200">
						<template #default="scope">
				            <span v-html="scope.row.title"></span>
				        </template>
					</el-table-column>
					
					<el-table-column label="标签名称" align="center" min-width="100">
						<template #default="scope">
				            <span v-for="(questionTagAssociation,index) in scope.row.questionTagAssociationList" class="tag-wrapper tag-spacing tag-color-blue">{{questionTagAssociation.questionTagName}}</span>
				        </template>
					</el-table-column>
					<el-table-column label="状态" align="center" width="100">
						<template #default="scope">
							<el-tag effect="dark"  v-if="scope.row.status==10" class="tag-wrapper">待审核</el-tag>
							<el-tag effect="dark"  v-if="scope.row.status==20" type="success" class="tag-wrapper" >已发布</el-tag>
							<el-tag effect="dark"  v-if="scope.row.status==110" type="info" class="tag-wrapper" >待审核删除</el-tag>
							<el-tag effect="dark"  v-if="scope.row.status==120" type="warning" class="tag-wrapper" >已发布删除</el-tag>
				    	</template>
					
					</el-table-column>
					<el-table-column label="允许回答" align="center" width="80">
						<template #default="scope">
							<el-tag effect="dark"  v-if="scope.row.allow==true" type="success" class="tag-wrapper">允许</el-tag>
							<el-tag effect="dark"  v-if="scope.row.allow==false" type="danger" class="tag-wrapper" >禁止</el-tag>
				    	</template>
					</el-table-column>
					<el-table-column label="会员/员工" align="center" min-width="120">
						<template #default="scope">
							<el-popover effect="light" trigger="hover" placement="top">
					        	<template #default>
					        		<p v-if="scope.row.nickname != null && scope.row.nickname != ''">呢称: {{scope.row.nickname}}</p>
						            <p>账号: {{scope.row.account}}</p>
					        	</template>
					        	<template #reference v-if="scope.row.isStaff == false">
					          		<div class="avatar-wrapper" >
										<div class="avatar-badge" v-if="scope.row.avatarName == null || scope.row.avatarName == ''">
											<el-avatar :size="48" icon="el-icon-user-solid"></el-avatar>
										</div>
										<div class="avatar-badge" v-if="scope.row.avatarName != null && scope.row.avatarName != ''">
											<el-avatar :size="48" :src="scope.row.avatarPath+'100x100/'+scope.row.avatarName"></el-avatar>
										</div>
										
										<div class="avatar-text">{{scope.row.account}}</div>
									</div>
					        	</template>
					        	
					        	<template #reference v-if="scope.row.isStaff == true">
					        		<div class="avatar-wrapper">
										<el-badge value="员工" type="warning" class="avatar-badge" v-if="scope.row.avatarName == null || scope.row.avatarName == ''">
											<el-avatar :size="48" icon="el-icon-user-solid"></el-avatar>
										</el-badge>
										<el-badge value="员工" type="warning" class="avatar-badge" v-if="scope.row.avatarName != null && scope.row.avatarName != ''">
	                                        <el-avatar :size="48" :src="scope.row.avatarPath+'100x100/'+scope.row.avatarName"></el-avatar>
	                                    </el-badge>
										<div class="avatar-text">{{scope.row.account}}</div>
									</div>
					        	</template>
					        </el-popover>
				    	</template>
					</el-table-column>
					<el-table-column prop="postTime" label="发布时间" align="center" width="150"></el-table-column>
					<el-table-column prop="sort" label="排序" align="center" width="80"></el-table-column>
					<el-table-column label="操作" align="center" min-width="120">
						<template #default="scope">
							<div class="button-group-wrapper">
								<el-button-group>
									<el-button type="primary" size="mini" @click="$store.commit('setCacheNumber');$router.push({path: '/admin/control/question/manage/view', query:{ questionId : scope.row.id}})">查看</el-button>
									<el-button type="primary" size="mini" @click="deleteQuestion($event,scope.row)">删除</el-button>
								</el-button-group>
							</div>
				    	</template>
					
					</el-table-column>
				</el-table>
				<div class="pagination-wrapper" v-if="isShowPage">
					<el-pagination background  @current-change="page" :current-page="currentpage"  :page-size="maxresult" layout="total, prev, pager, next,jumper" :total="totalrecord"></el-pagination>
				</div>
			</div>
		</div> 
	</div>
</template>  

<script>
//问题搜索
export default({
	name: 'questionSearch',//组件名称，keep-alive缓存需要本参数
	template : '#questionSearch-template',
	inject:['reload'], 
	data : function data() {
		return {
			dataSource :1,//数据源
			keyword :'',//关键词
			tagId :'',//标签Id
			tagName :'',//标签名称
			account :'',//账号
			start_postTime :'',//发表日期 起始
			end_postTime :'',//发表日期 结束
			
			start_postTime_format :'',//发表日期 起始 格式化为适合el-date-picker标签的值
			end_postTime_format :'',//发表日期 结束  格式化为适合el-date-picker标签的值

			error :{
				dataSource :'',
				keyword :'',
				tagId :'',
				account :'',
				start_postTime :'',
				end_postTime :'',
			},
			
			
			
			questionTagGroup :{//问题标签到列表  分页	
				//tagIdGroup :[],//标签Id组
				//tagOptions:[],
				tableData: [],//表格内容
				parentId : '',//父Id
				tagIdList: [],//可选择Id集合
				totalrecord : 0, //总记录数
			    currentpage : 1, //当前页码
				totalpage : 1, //总页数
				maxresult: 12, //每页显示记录数
				isShowPage:false,//是否显示分页 maxresult没返回结果前就显示会导致分页栏显示页码错误
				question_tag_form:false,//是否显示问题标签表单
				navigation: '',
			},
			
			
		//	isAllowLoadTagGroup:true,//是否允许加载标签组
			tagIdGroup :[],//标签Id组
			options: [],//Select 选择器标签数据
		
			tableData: [],//表格内容
		    multipleSelection: [],
		  
		    totalrecord : 0, //总记录数
		    currentpage : 1, //当前页码
			totalpage : 1, //总页数
			maxresult: 12, //每页显示记录数
			isShowPage:false,//是否显示分页 maxresult没返回结果前就显示会导致分页栏显示页码错误
		};
	},

	created : function created() {
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		if(this.$route.query.dataSource != undefined && this.$route.query.dataSource != ''){
			this.dataSource = this.$route.query.dataSource;
		}
		
		if(this.$route.query.keyword != undefined && this.$route.query.keyword != ''){
			this.keyword = decodeURIComponent(this.$route.query.keyword);
		}
		if(this.$route.query.account != undefined && this.$route.query.account !=''){
			this.account = decodeURIComponent(this.$route.query.account);
		}
		if(this.$route.query.start_postTime != undefined && this.$route.query.start_postTime != ''){
			this.start_postTime_format = decodeURIComponent(this.$route.query.start_postTime);
			this.start_postTime = this.start_postTime_format;
		}
		if(this.$route.query.end_postTime != undefined && this.$route.query.end_postTime != ''){
			this.end_postTime_format = decodeURIComponent(this.$route.query.end_postTime);
			this.end_postTime = this.end_postTime_format;
		}
		if(this.$route.query.tagId != undefined && this.$route.query.tagId != ''){
			this.tagId = this.$route.query.tagId;
			this.tagName = decodeURIComponent(this.$route.query.tagName);
			this.tagIdGroup.push(this.tagId);
			let obj =new Object();
			obj.value = this.tagId;
			obj.label = this.tagName;
			this.options.push(obj);
		}
		
		
		if(this.$route.query.page != undefined && this.$route.query.page != ''){
			this.currentpage = this.$route.query.page;
		}
		

		   
		
		this.queryQuestionList();
		
		
	},
	methods : {
		//查询问题列表
		queryQuestionList : function() {
			let _self = this;
			
			this.$ajax.get('control/question/search', {
			    params: {
			    	dataSource :_self.dataSource,//数据源
			    	keyword :_self.keyword,
			    	tagId :_self.tagId,
			    	tagName : _self.tagName,
			    	account :_self.account,
					start_postTime :_self.start_postTime,
					end_postTime :_self.end_postTime,
			    	page :_self.currentpage
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
			    		let pageView = returnValue.data;
			    		let list = pageView.records;
			    		if(list != null && list.length >0){
			    			_self.tableData = list;
			 
			    			_self.totalrecord = parseInt(pageView.totalrecord);//服务器返回的long类型已转为String类型
			    			_self.currentpage = pageView.currentpage;
							_self.totalpage = parseInt(pageView.totalpage);//服务器返回的long类型已转为String类型
							_self.maxresult = pageView.maxresult;
							_self.isShowPage = true;//显示分页栏
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
		//分页
		page: function(page) {;
			
			//删除缓存
			this.$store.commit('setCacheNumber');
			this.$router.push({
				path: '/admin/control/question/search', 
				query:{ 
					dataSource :this.dataSource,//数据源
					keyword :encodeURIComponent(this.keyword),
					tagId :this.tagId,
					tagName :encodeURIComponent(this.tagName),
					account :encodeURIComponent(this.account),
					start_postTime :encodeURIComponent(this.start_postTime),
					end_postTime :encodeURIComponent(this.end_postTime),
					page : page
				}
			});
	    },
	  
		
	    
	    //提交表单
		submitForm : function() {
			let _self = this;
			
			//删除缓存
			_self.$store.commit('setCacheNumber');

			if(_self.start_postTime_format != null && _self.start_postTime_format != ''){
				_self.start_postTime = dayjs(_self.start_postTime_format).format('YYYY-MM-DD HH:mm');
			}else{
				_self.start_postTime = '';
			}
			if(_self.end_postTime_format != null && _self.end_postTime_format != ''){
				_self.end_postTime = dayjs(_self.end_postTime_format).format('YYYY-MM-DD HH:mm');
			}else{
				_self.end_postTime = '';
			}
			
			
			_self.$router.push({
				path: '/admin/control/question/search', 
				query:{ 
					dataSource :_self.dataSource,//数据源
					keyword :encodeURIComponent(_self.keyword),
					tagId :_self.tagId,
					tagName : encodeURIComponent(_self.tagName),
					account :encodeURIComponent(_self.account),
					start_postTime :encodeURIComponent(_self.start_postTime),
					end_postTime :encodeURIComponent(_self.end_postTime),
					page : 1,
					time: new Date().getTime()
				}
			});
	    },
	    
	  	//点击单元格选择
  		cellExpandRow_questionTag : function(row,column,event,cell) {
  			if(column.label=="选择"){
  				if(row.childNodeNumber ==0){
  					this.tagIdGroup.push(row.id);
  					
  			        this.tagId = row.id;
  			      	this.tagName = row.name;
  					let obj =new Object();
  					obj.value = row.id;
  					obj.label = row.name;
  					this.options.length = 0;//清空
  					this.options.push(obj);
  					
  					
  					//因为只允许选中一个，所以将已选择的清空
  			    	if (Object.keys(this.tagIdGroup).length >1) {
  			        	this.tagIdGroup.shift();//从Array 头部移除元素
  			        }
  			        this.questionTagGroup.question_tag_form = false;
  				}
  			}else if(column.label=="标签名称"){
  				if(row.childNodeNumber >0){
  					this.questionTagGroup.parentId = row.id;
  					this.queryQuestionTag(1,row.id);
  				}
  			}
  		},
  		
  		
  		//处理删除标签
	    processRemoveTag: function(val) { 
	    	this.tagId = '';
			this.tagName = '';
			this.options.length = 0;//清空
			this.tagIdGroup.length = 0;
	    },
	  	//加载问题标签
	    loadQuestionTag: function() { 
	    	
	    	this.questionTagGroup.question_tag_form = true;
	       	this.$refs.question_tag_ref.blur();//使Select选择器失去焦点，并隐藏下拉框
	       	
	       	if(this.tagIdGroup.length ==0){
	       		this.questionTagGroup.question_tagId = "";
	       		this.options.length = 0;//清空	
	       	}
	       	
	       	
	       	//清空数据
			this.questionTagGroup.totalrecord = 0;//服务器返回的long类型已转为String类型
   			this.questionTagGroup.currentpage = 1;
			this.questionTagGroup.totalpage = 1;//服务器返回的long类型已转为String类型
			this.questionTagGroup.maxresult = 12;
			this.questionTagGroup.isShowPage = false;//显示分页栏
			this.questionTagGroup.parentId = '';
	       	
	        this.queryQuestionTag(1,'');
		},
		//问题标签分页
		questionTagPage : function(page) {
			
			this.queryQuestionTag(page, this.questionTagGroup.parentId);
		},
		//查询问题标签
		queryQuestionTag : function(page,parentId) {
			let _self = this;
			
			_self.questionTagGroup.tableData = [];
			_self.questionTagGroup.tagIdList = [];
			_self.questionTagGroup.navigation = '';
			
			_self.questionTagGroup.parentId = parentId;
			_self.$ajax.get('control/questionTag/manage', {
			    params: {
			    	method : 'questionTagPage',
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
			    				let tagView = mapData[key];
					    		let tagList = tagView.records;
					    		if(tagList != null && tagList.length >0){
					    			for(let i = 0; i<tagList.length; i++){
					    				let tag = tagList[i];
					    				_self.questionTagGroup.tagIdList.push(tag.id);
					    			
					    			}
					    		
					    		
					    			_self.questionTagGroup.tableData = tagList;
					 
					    			_self.questionTagGroup.totalrecord = parseInt(tagView.totalrecord);//服务器返回的long类型已转为String类型
					    			_self.questionTagGroup.currentpage = tagView.currentpage;
									_self.questionTagGroup.totalpage = parseInt(tagView.totalpage);//服务器返回的long类型已转为String类型
									_self.questionTagGroup.maxresult = tagView.maxresult;
									_self.questionTagGroup.isShowPage = true;//显示分页栏
					    		}
			    			}else if(key == "navigation"){
			    				_self.questionTagGroup.navigation = mapData[key];
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
	    
	    
	    
	    
	    //处理多选
	    handleSelectionChange: function(val) {
	        this.multipleSelection = val;
	    },
	    //删除问题
	    deleteQuestion : function(event,row) {
	    	//强制失去焦点
			let target = event.target;
			// 根据button组件内容 里面包括一个span标签，如果设置icon，则还包括一个i标签，其他情况请自行观察。
    		// 所以，在我们点击到button组件上的文字也就是span标签上时，直接执行e.target.blur()不会生效，所以要加一层判断。
        	if(target.nodeName == 'SPAN' || target.nodeName == 'I'){
            	target = event.target.parentNode;
       		}
        	target.blur();
	    	let _self = this;
	    	if (row) {//选中行
	    		this.$refs.multipleTable.toggleRowSelection(row,true);
	    	}
	    	
	    	if(this.multipleSelection.length <1){
	    		this.$message.error('至少要选择一行数据');
	    		return;
	    	}
	    	
	    	this.$confirm('此操作将删除该问题, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	
		    	for(let i=0; i<this.multipleSelection.length; i++){
		    		let rowData = this.multipleSelection[i];
		    		formData.append('questionId', rowData.id);
		    	}
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/question/manage?method=deleteQuestion',
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
				    		_self.queryQuestionList();
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
	        	//取消选中行
	        	this.$refs.multipleTable.toggleRowSelection(row,false);
	        	
	        	console.log(error);
	        });
	    	
	    },
	    //还原问题
	    reductionQuestion : function(event) {
	    	//强制失去焦点
			let target = event.target;
			// 根据button组件内容 里面包括一个span标签，如果设置icon，则还包括一个i标签，其他情况请自行观察。
    		// 所以，在我们点击到button组件上的文字也就是span标签上时，直接执行e.target.blur()不会生效，所以要加一层判断。
        	if(target.nodeName == 'SPAN' || target.nodeName == 'I'){
            	target = event.target.parentNode;
       		}
	    	target.blur();
	    	
	    	let _self = this;
	    	
	    	if(this.multipleSelection.length <1){
	    		this.$message.error('至少要选择一行数据');
	    		return;
	    	}
	    	
	    	this.$confirm('此操作将还原该问题, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	
		    	for(let i=0; i<this.multipleSelection.length; i++){
		    		let rowData = this.multipleSelection[i];
		    		formData.append('questionId', rowData.id);
		    	}
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/question/manage?method=reduction',
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
				    		_self.$message.success("还原成功");
				    		_self.queryQuestionList();
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
