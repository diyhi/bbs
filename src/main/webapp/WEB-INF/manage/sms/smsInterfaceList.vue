<!-- 短信接口列表 -->
<template id="smsInterfaceList-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/smsInterface/manage/add'});">添加短信接口</el-button>
			</div>
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" @selection-change="handleSelectionChange" stripe empty-text="没有内容">
					<el-table-column prop="name" label="短信接口名称" align="center" ></el-table-column>
					<el-table-column label="接口产品" align="center" >
						<template #default="scope">
							<span v-if="scope.row.interfaceProduct == 1">阿里云短信</span>
						</template>
					</el-table-column>
					<el-table-column prop="uses" label="是否启用" align="center" min-width="100">
						<template #default="scope">	
							<el-switch v-model="enable[scope.$index]" @change="handleSmsChange(scope.row,scope.$index)"></el-switch>
				    	</template>
					</el-table-column>
					<el-table-column prop="sort" label="排序" align="center" width="200"></el-table-column>
					<el-table-column label="操作" align="center" width="200">
						<template #default="scope">
							<div class="button-group-wrapper">
								<el-button-group>
									<el-button type="primary" size="mini" @click="$router.push({path: '/admin/control/smsInterface/manage/edit', query:{ smsInterfaceId : scope.row.id, page:currentpage}})">修改</el-button>
									<el-button type="primary" size="mini" @click="deleteSmsInterface($event,scope.row)">删除</el-button>
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
//短信接口列表
export default({
	name: 'smsInterfaceList',//组件名称，keep-alive缓存需要本参数
	template : '#smsInterfaceList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
			multipleSelection: [],
		  
			enable:[],
			
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
		
		//初始化
		this.querySmsInterfaceList();
	},
	methods : {
		//处理选择模板
		handleSmsChange: function(row,index) {
			let _self = this;
			
			if(row.enable && !_self.enable[index]){//如果不选中
				_self.enable[index] = true;
				return;
			}
			
			
			
			let formData = new FormData();
	    	formData.append('smsInterfaceId', row.id);
	    	

			_self.$ajax({
		        method: 'post',
		        url: 'control/smsInterface/manage?method=enableInterface',
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
			    		_self.$message.success("设置成功");
			    		_self.querySmsInterfaceList();
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
		},
		
		//查询短信接口列表
		querySmsInterfaceList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			_self.enable = [];
			
			
			_self.$ajax.get('control/smsInterface/list', {
			    params: {
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
			    		let pageView = returnValue.data;
			    		let list = pageView.records;
			    		if(list != null && list.length >0){	
			    			for(let i=0; i<list.length; i++){
			    				_self.enable.push(list[i].enable);
			    			}
			    			
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
				path: '/admin/control/smsInterface/list', 
				query:{ page : page}
			});
	    },
		//删除短信接口
	    deleteSmsInterface : function(event,row) {
			//强制失去焦点
			let target = event.target;
			// 根据button组件内容 里面包括一个span标签，如果设置icon，则还包括一个i标签，其他情况请自行观察。
    		// 所以，在我们点击到button组件上的文字也就是span标签上时，直接执行e.target.blur()不会生效，所以要加一层判断。
        	if(target.nodeName == 'SPAN' || target.nodeName == 'I'){
            	target = event.target.parentNode;
       		}
        	target.blur();
			
			
	    	let _self = this;
	    	this.$confirm('此操作将删除该项, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	formData.append('smsInterfaceId', row.id);
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/smsInterface/manage?method=delete',
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
				    		_self.querySmsInterfaceList();
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
	    },
	  	
	}
});
</script>
