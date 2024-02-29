<!-- 话题搜索 -->
<template id="topicSearch-template">
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
						<el-select v-model="tagIdGroup" @focus="queryTagList" @change="selectedTag" multiple  no-match-text="还没有标签" placeholder="选择标签">
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
							<el-date-picker v-model="start_postTime_format" type="datetime" format="YYYY-MM-DD HH:mm" placeholder="发表话题起始时间" ></el-date-picker>
						</el-form-item>
					</el-tooltip>
					<el-tooltip content="不限制请留空" placement="top">
						<el-form-item :error="error.end_postTime" >
							<el-date-picker v-model="end_postTime_format" type="datetime" format="YYYY-MM-DD HH:mm" placeholder="发表话题结束时间"></el-date-picker>
						</el-form-item>
					</el-tooltip>
					<el-form-item >
					    <el-button type="primary" class="submitButton" @click="submitForm" >搜索</el-button>
					</el-form-item>
					
				</el-form>
			</div> 
		
			<div class="navbar" >
				<el-button type="primary" plain size="small"  @click="reductionTopic($event)">批量还原</el-button>
				<el-button type="primary" plain size="small"  @click="deleteTopic($event)">批量删除</el-button>
			</div>
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" @selection-change="handleSelectionChange" stripe empty-text="没有内容">
					<el-table-column type="selection" ></el-table-column>
					<el-table-column prop="title" label="话题标题" min-width="200">
						<template #default="scope">
				            <span v-html="scope.row.title"></span>
				            <span class="essence" v-if="scope.row.essence == true">精华</span>
		                	<span class="top" v-if="scope.row.sort >0">置顶</span>
				            <span v-if="scope.row.giveRedEnvelopeId != null && scope.row.giveRedEnvelopeId != ''" class="redEnvelope">红包</span>
				        </template>
					</el-table-column>
					<el-table-column prop="tagName" label="标签名称" align="center" min-width="80"></el-table-column>
					<el-table-column label="状态" align="center" width="100">
						<template #default="scope">
							<el-tag effect="dark"  v-if="scope.row.status==10" class="tag-wrapper">待审核</el-tag>
							<el-tag effect="dark"  v-if="scope.row.status==20" type="success" class="tag-wrapper" >已发布</el-tag>
							<el-tag effect="dark"  v-if="scope.row.status==110" type="info" class="tag-wrapper" >待审核删除</el-tag>
							<el-tag effect="dark"  v-if="scope.row.status==120" type="warning" class="tag-wrapper" >已发布删除</el-tag>
				    	</template>
					
					</el-table-column>
					<el-table-column label="允许评论" align="center" width="80">
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
									<el-button type="primary" size="mini" @click="$store.commit('setCacheNumber');$router.push({path: '/admin/control/topic/manage/view', query:{ topicId : scope.row.id}})">查看</el-button>
									<el-button type="primary" size="mini" @click="deleteTopic($event,scope.row)">删除</el-button>
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
//话题搜索
export default({
	name: 'topicSearch',//组件名称，keep-alive缓存需要本参数
	template : '#topicSearch-template',
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
			
			
			isAllowLoadTagGroup:true,//是否允许加载标签组
			tagIdGroup :[],//标签Id组
			loading :false,//是否正在从远程获取数据
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
		

		   
		
		this.queryTopicList();
		
		
	},
	methods : {
		//查询话题列表
		queryTopicList : function() {
			let _self = this;
			
			this.$ajax.get('control/topic/search', {
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
			    		let topicList = pageView.records;
			    		if(topicList != null && topicList.length >0){
			    			_self.tableData = topicList;
			 
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
				path: '/admin/control/topic/search', 
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
				path: '/admin/control/topic/search', 
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
	        //因为只允许选中一个，所以将已选择的清空
	    	if (Object.keys(this.tagIdGroup).length >1) {
	        	this.tagIdGroup.shift();//从Array 头部移除元素
	        }
	        
	        this.tagId = val[0];
	        
	        for(let option in this.options){
	        	if(this.options[option].value == this.tagId){
	        		this.tagName = this.options[option].label;
	        		break;
	        	}
	        }
	    },
	    
	    
	    //处理多选
	    handleSelectionChange: function(val) {
	        this.multipleSelection = val;
	    },
	    //删除话题
	    deleteTopic : function(event,row) {
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
	    	
	    	this.$confirm('此操作将删除该话题, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	
		    	for(let i=0; i<this.multipleSelection.length; i++){
		    		let rowData = this.multipleSelection[i];
		    		formData.append('topicId', rowData.id);
		    	}
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/topic/manage?method=delete',
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
				    		_self.queryTopicList();
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
	    //还原话题
	    reductionTopic : function(event) {
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
	    	
	    	this.$confirm('此操作将还原该话题, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	
		    	for(let i=0; i<this.multipleSelection.length; i++){
		    		let rowData = this.multipleSelection[i];
		    		formData.append('topicId', rowData.id);
		    	}
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/topic/manage?method=reduction',
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
				    		_self.queryTopicList();
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
