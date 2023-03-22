<!-- 帮助列表 -->
<template id="helpList-template">
	<div>
		<div class="main">
			<div class="navbar">
				<el-button type="primary" plain size="small" v-if="visible == 'false'" @click="$router.push({path: '/admin/control/help/list', query:{ visible : true}})">返回</el-button>
				<el-button type="primary" plain size="small" v-if="visible == 'false'" @click="reductionHelp($event)">还原</el-button>
				<el-button type="primary" plain size="small" v-if="visible == 'true'" @click="$router.push({path: '/admin/control/help/manage/add', query:{ visible : true,page:($route.query.page != undefined ? $route.query.page:'')}})">添加帮助</el-button>
				<el-button type="primary" plain size="small" v-if="visible == 'true'" @click="$router.push({path: '/admin/control/help/list', query:{ visible : false}})">回收站</el-button>
				<el-button type="primary" plain size="small" v-if="visible == 'true'" @click="moveUI($event)">移动</el-button>
				<el-button type="primary" plain size="small"  @click="deleteHelp($event)">批量删除</el-button>
			</div>
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" @selection-change="handleSelectionChange" stripe empty-text="没有内容">
					<el-table-column type="selection" ></el-table-column>
					<el-table-column prop="name" label="帮助名称" min-width="200">
						<template #default="scope">
							{{scope.row.name}}
				    	</template>
					</el-table-column>
					<el-table-column prop="helpTypeName" label="分类名称" align="center" min-width="80"></el-table-column>
					<el-table-column label="发布员工" align="center" width="150">
						<template #default="scope">
			        		<div class="avatar-wrapper">
								<el-badge value="员工" type="warning" class="avatar-badge">
									<el-avatar :size="48" icon="el-icon-user-solid"></el-avatar>
								</el-badge>
								<div class="avatar-text">{{scope.row.userName}}</div>
							</div>
				    	</template>
					</el-table-column>
					<el-table-column prop="times" label="发布时间" align="center" width="150"></el-table-column>
					<el-table-column label="操作" align="center" width="200">
						<template #default="scope">
							<div class="button-group-wrapper">
								<el-button-group>
									<el-button type="primary" size="mini" @click="$store.commit('setCacheNumber');$router.push({path: '/admin/control/help/manage/view', query:{helpId : scope.row.id, visible : ($route.query.visible != undefined ? $route.query.visible:''),page : ($route.query.page != undefined ? $route.query.page:'')}})">查看</el-button>
									<el-button type="primary" size="mini" @click="deleteHelp($event,scope.row)">删除</el-button>
								</el-button-group>
							</div>
				    	</template>
					
					</el-table-column>
				</el-table>
				<div class="pagination-wrapper" v-if="isShowPage">
					<el-pagination background  @current-change="page" :current-page="currentpage"  :page-size="maxresult" layout="total, prev, pager, next,jumper" :total="totalrecord"></el-pagination>
				</div>
			</div>
			
			<div class="data-form" >
				<el-dialog title="选择分类" v-model="move.helpType_form">
					<div class="dialog-nav-breadcrumb">
						<el-breadcrumb separator-class="el-icon-arrow-right">
							<el-breadcrumb-item @click="queryHelpTypeList(1,'')">全部标签</el-breadcrumb-item>
							<el-breadcrumb-item v-for="(value, key) in move.navigation" @click="queryHelpTypeList(1,key)">{{value}}</el-breadcrumb-item>
							
						</el-breadcrumb>
					</div>
					<div class="dialog-data-table" >
						<el-table :data="move.tableData" @cell-click="cellExpandRow" :show-header="false" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
							<el-table-column label="选择" align="right" width="50">
								<template #default="scope">
									<el-radio v-model="move.helpTypeId" @click.native.prevent="radioClickEvent($event)" v-if="scope.row.childNodeNumber ==0" :label="scope.row.id" >&nbsp;</el-radio>
						    	</template>
							</el-table-column>
							<el-table-column label="分类名称">
								<template #default="scope">
									<i class="icon icon-folder el-icon-folder" v-if="scope.row.childNodeNumber >0"></i>
									<i class="icon icon-file el-icon-document" v-if="scope.row.childNodeNumber ==0"></i>{{scope.row.name}}
						    	</template>
							</el-table-column>
						</el-table>
						<div class="pagination-wrapper" v-if="move.isShowPage">
							<el-pagination background  @current-change="move_page" :current-page="move.currentpage"  :page-size="move.maxresult" layout="total, prev, pager, next,jumper" :total="move.totalrecord"></el-pagination>
						</div>
					</div>
				</el-dialog>
			</div>
			
			
		</div>
	</div>
</template>  

<script>
//帮助列表
export default({
	name: 'helpList',//组件名称，keep-alive缓存需要本参数
	template : '#helpList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
			visible :'true',//是否显示 true:未删除帮助 false:已删除帮助
		    multipleSelection: [],
		    
		    move:{
		    	helpTypeId: '',
				tableData: [],//表格内容
				parentId : '',//父Id
				totalrecord : 0, //总记录数
			    currentpage : 1, //当前页码
				totalpage : 1, //总页数
				maxresult: 12, //每页显示记录数
				isShowPage:false,//是否显示分页 maxresult没返回结果前就显示会导致分页栏显示页码错误
				helpType_form:false,//是否显示问题标签表单
				navigation: '',
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

		if(this.$route.query.visible != undefined && this.$route.query.visible != ''){
			this.visible = this.$route.query.visible;
		}
		if(this.$route.query.page != undefined && this.$route.query.page != ''){
			this.currentpage = this.$route.query.page;
		}

		this.queryHelpList();
		
		
	},
	
	methods : {
		
		
		//查询帮助列表
		queryHelpList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
	
			this.$ajax.get('control/help/list', {
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
			    		let helpList = pageView.records;
			    		if(helpList != null && helpList.length >0){
			    			_self.tableData = helpList;
			 
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
				path: '/admin/control/help/list', 
				query:{ visible : this.visible,page : page}
			});
	    },
	    //处理多选
	    handleSelectionChange: function(val) {
	        this.multipleSelection = val;
	    },
	    
	    
		//移动UI
	    moveUI : function(event) {
	    	//强制失去焦点
			let target = event.target;
			// 根据button组件内容 里面包括一个span标签，如果设置icon，则还包括一个i标签，其他情况请自行观察。
    		// 所以，在我们点击到button组件上的文字也就是span标签上时，直接执行e.target.blur()不会生效，所以要加一层判断。
        	if(target.nodeName == 'SPAN' || target.nodeName == 'I'){
            	target = event.target.parentNode;
       		}
        	target.blur();
	    	let _self = this;
	    	
	    	if(_self.multipleSelection.length <1){
	    		_self.$message.error('至少要选择一行数据');
	    		return;
	    	}
	    	
	    	_self.move.helpType_form = true;
	    	_self.loadHelpType();
	    	
	    },
	  	//空的点击事件  不写上本方法点击cellExpandRow时会运行两次
	  	radioClickEvent : function(event) {
	  		
	  	},
	  	//点击单元格选择
  		cellExpandRow : function(row,column,event,cell) {
  			let _self = this;
  			if(column.label=="选择"){
  				if(row.childNodeNumber ==0){
  					
  				
  					
  					this.$confirm('此操作将选中的项移动到[ '+row.name+' ], 是否继续?', '提示', {
  			            confirmButtonText: '确定',
  			            cancelButtonText: '取消',
  			            type: 'warning'
  			        }).then(() => {
  			        	
  			        	let formData = new FormData();
  				    	
  				    	for(let i=0; i<this.multipleSelection.length; i++){
  				    		let rowData = this.multipleSelection[i];
  				    		formData.append('helpId', rowData.id);
  				    	}
  				    	formData.append('helpTypeId', row.id);
  				    	
  						this.$ajax({
  					        method: 'post',
  					        url: 'control/help/manage?method=move',
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
  						    		_self.$message.success("移动成功");
  						    		_self.move.helpType_form = false;
  				  					
  				  					//删除缓存
  						    		_self.$store.commit('setCacheNumber');
  						    		_self.queryHelpList();
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
  			}else if(column.label=="分类名称"){
  				if(row.childNodeNumber >0){
  					this.move.parentId = row.id;
  					this.queryHelpTypeList(1,row.id);
  				}
  			}
  		},
	
		//加载帮助分类
	    loadHelpType: function() { 
	    	
	    	this.move.helpType_form = true;
	    	this.move.helpTypeId = "";
	       	//清空数据
			this.move.totalrecord = 0;//服务器返回的long类型已转为String类型
   			this.move.currentpage = 1;
			this.move.totalpage = 1;//服务器返回的long类型已转为String类型
			this.move.maxresult = 12;
			this.move.isShowPage = false;//显示分页栏
			this.move.parentId = '';
	       	
	        this.queryHelpTypeList(1,'');
		},
		//分页
		move_page : function(page) {
			this.queryHelpTypeList(page, this.move.parentId);
		},
	    
	    
		//查询分类
	    queryHelpTypeList: function(page,parentId) {
	        let _self = this;
	        
	        _self.move.tableData = [];
			_self.move.navigation = '';
			
			_self.move.parentId = parentId;
	        
	        
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
					    			_self.move.tableData = typeList;
					 
					    			_self.move.totalrecord = parseInt(typeView.totalrecord);//服务器返回的long类型已转为String类型
					    			_self.move.currentpage = typeView.currentpage;
									_self.move.totalpage = parseInt(typeView.totalpage);//服务器返回的long类型已转为String类型
									_self.move.maxresult = typeView.maxresult;
									_self.move.isShowPage = true;//显示分页栏
					    		}
			    			}else if(key == "navigation"){
			    				_self.move.navigation = mapData[key];
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
	    
	    
	    //删除帮助
	    deleteHelp : function(event,row) {
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
		    		formData.append('helpId', rowData.id);
		    	}
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/help/manage?method=delete',
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
				    		//删除缓存
				    		_self.$store.commit('setCacheNumber');
				    		_self.queryHelpList();
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
	    //还原帮助
	    reductionHelp : function(event) {
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
	    	
	    	this.$confirm('此操作将还原该项, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	
		    	for(let i=0; i<this.multipleSelection.length; i++){
		    		let rowData = this.multipleSelection[i];
		    		formData.append('helpId', rowData.id);
		    	}
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/help/manage?method=reduction',
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
				    		//删除缓存
				    		_self.$store.commit('setCacheNumber');
				    		_self.queryHelpList();
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