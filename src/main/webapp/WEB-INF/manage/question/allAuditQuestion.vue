<!-- 全部待审核问题 -->
<template id="allAuditQuestion-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small"  @click="deleteQuestion($event)">批量删除</el-button>
			</div>
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" @selection-change="handleSelectionChange" stripe empty-text="没有内容">
					<el-table-column type="selection" ></el-table-column>
					<el-table-column prop="title" label="标题" min-width="200">
						<template #default="scope">
							{{scope.row.title}}
							<span v-if="scope.row.giveRedEnvelopeId != null && scope.row.giveRedEnvelopeId != ''" class="redEnvelope">红包</span>
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
//全部待审核问题
export default({
	name: 'allAuditQuestion',//组件名称，keep-alive缓存需要本参数
	template : '#allAuditQuestion-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
			visible :'true',//是否显示 true:未删除问题 false:已删除问题
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

		if(this.$route.query.visible != undefined){
			this.visible = this.$route.query.visible;
		}
		if(this.$route.query.page != undefined){
			this.currentpage = this.$route.query.page;
		}

		this.queryQuestionList();
		
		
	},
	
	methods : {
		//查询问题列表
		queryQuestionList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			
			_self.$ajax.get('control/question/allAuditQuestion', {
			    params: {
			    	visible :_self.visible,
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
			    		let questionList = pageView.records;
			    		if(questionList != null && questionList.length >0){
			    			_self.tableData = questionList;
			 
			    			_self.totalrecord = parseInt(pageView.totalrecord);//服务器返回的long类型已转为String类型
			    			_self.currentpage = pageView.currentpage;
							_self.totalpage = parseInt(pageView.totalpage);//服务器返回的long类型已转为String类型
							_self.maxresult = pageView.maxresult;
							_self.isShowPage = true;//显示分页栏
			    		}
			    	}else if(returnValue.code === 500){//错误
			    		
			    		
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
				path: '/admin/control/question/allAuditQuestion', 
				query:{ page : page}
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
	    	
	    }
	}
});


</script>