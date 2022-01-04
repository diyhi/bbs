<!-- 留言列表 -->
<template id="feedbackList-template">
	<div>
		<div class="main">
			<div class="search">
				<el-form :inline="true" label-width="auto" @keyup.enter.native="submitForm">
					<el-tooltip content="不限制请留空" placement="top">
						<el-form-item :error="error.start_createDate" >
							<el-date-picker v-model="start_createDate_format" type="datetime" format="YYYY-MM-DD HH:mm" placeholder="留言起始时间" ></el-date-picker>
						</el-form-item>
					</el-tooltip>
					<el-tooltip content="不限制请留空" placement="top">
						<el-form-item :error="error.end_createDate" >
							<el-date-picker v-model="end_createDate_format" type="datetime" format="YYYY-MM-DD HH:mm" placeholder="留言结束时间"></el-date-picker>
						</el-form-item>
					</el-tooltip>
					<el-form-item >
					    <el-button type="primary" class="submitButton" @click="submitForm" >筛选</el-button>
					</el-form-item>
					
				</el-form>
			</div> 
		
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" @selection-change="handleSelectionChange" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
					<el-table-column type="selection" v-if="false"></el-table-column>
					<el-table-column prop="name" label="称呼" align="center" width="170"></el-table-column>
					<el-table-column prop="contact" label="联系方式" align="center" width="170"></el-table-column>
					<el-table-column prop="content" label="内容" align="center"></el-table-column>
					<el-table-column prop="createDate" label="日期" align="center" width="170"></el-table-column>
					<el-table-column label="操作" align="center" width="200">
						<template #default="scope">
							<div class="button-group-wrapper">
								<el-button-group>
									<el-button type="primary" size="mini" @click="$store.commit('setCacheNumber');$router.push({path: '/admin/control/feedback/manage/view', query:{ feedbackId : scope.row.id}})">查看</el-button>
									<el-button type="primary" size="mini" @click="deleteFeedback($event,scope.row)">删除</el-button>
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
//留言列表
export default({
	name: 'feedbackList',//组件名称，keep-alive缓存需要本参数
	template : '#feedbackList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
		    multipleSelection: [],
		  
		    start_createDate :'',//留言日期 起始
			end_createDate :'',//留言日期 结束
			
			start_createDate_format :'',//留言日期 起始 格式化为适合el-date-picker标签的值
			end_createDate_format :'',//留言日期 结束  格式化为适合el-date-picker标签的值

			error :{
				start_createDate :'',
				end_createDate :'',
			},
			
		    
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

		if(this.$route.query.page != undefined && this.$route.query.page != ''){
			this.currentpage = this.$route.query.page;
		}
		
		if(this.$route.query.start_createDate != undefined && this.$route.query.start_createDate != ''){
			this.start_createDate_format = decodeURIComponent(this.$route.query.start_createDate);
			this.start_createDate = this.start_createDate_format;
		}
		if(this.$route.query.end_createDate != undefined && this.$route.query.end_createDate != ''){
			this.end_createDate_format = decodeURIComponent(this.$route.query.end_createDate);
			this.end_createDate = this.end_createDate_format;
		}

		this.queryFeedbackList();
		
		
	},
	
	methods : {
		//查询留言列表
		queryFeedbackList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			
			 _self.$ajax.get('control/feedback/list', {
			   params: {
				   start_createDate :_self.start_createDate,
					end_createDate :_self.end_createDate,
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
				path: '/admin/control/feedback/list', 
				query:{
					start_createDate :encodeURIComponent(this.start_createDate),
					end_createDate :encodeURIComponent(this.end_createDate),
					page : page
				}
			});
	    },
	  //处理多选
	    handleSelectionChange: function(val) {
	        this.multipleSelection = val;
	    },
	  	//提交表单
  		submitForm : function() {
  			let _self = this;
  			
  			//删除缓存
  			_self.$store.commit('setCacheNumber');

  			if(_self.start_createDate_format != null && _self.start_createDate_format != ''){
  				_self.start_createDate = dayjs(_self.start_createDate_format).format('YYYY-MM-DD HH:mm');
  			}else{
  				_self.start_createDate = '';
  			}
  			if(_self.end_createDate_format != null && _self.end_createDate_format != ''){
  				_self.end_createDate = dayjs(_self.end_createDate_format).format('YYYY-MM-DD HH:mm');
  			}else{
  				_self.end_createDate = '';
  			}
  			
  			
  			_self.$router.push({
  				path: '/admin/control/feedback/list', 
  				query:{ 
  					start_createDate :encodeURIComponent(_self.start_createDate),
  					end_createDate :encodeURIComponent(_self.end_createDate),
  					page : 1,
  					time: new Date().getTime()
  				}
  			});
  	    },
  		
	  	//删除留言
	    deleteFeedback : function(event,row) {
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
	    	
	    	this.$confirm('此操作将删除该项, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	
		    	for(let i=0; i<this.multipleSelection.length; i++){
		    		let rowData = this.multipleSelection[i];
		    		formData.append('feedbackId', rowData.id);
		    	}
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/feedback/manage?method=delete',
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
				    		_self.queryFeedbackList();
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