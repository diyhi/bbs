<!-- 帮助分类列表 -->
<template id="helpTypeList-template">
	<div>
		<div class="main">
			<div class="nav-breadcrumb">
				<el-breadcrumb separator-class="el-icon-arrow-right">
					<el-breadcrumb-item @click="$router.push({path: '/admin/control/helpType/list'});">全部分类</el-breadcrumb-item>
					<el-breadcrumb-item v-for="(value, key) in navigation" @click="$router.push({path: '/admin/control/helpType/list',query:{parentId:key}});">{{value}}</el-breadcrumb-item>
				</el-breadcrumb>
			</div>
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="$router.push({path: '/admin/control/helpType/manage/add',query:{sourceParentId:($route.query.parentId != undefined ? $route.query.parentId:''),parentId : parentId, page:($route.query.page != undefined ? $route.query.page:'')}});">添加分类</el-button>
			</div>
			<div class="data-table" >
				<el-table :data="tableData" tooltip-effect="dark" style="width: 100%" @cell-click="cellExpandRow"  stripe empty-text="没有内容">
					<el-table-column label="分类名称">
						<template #default="scope">
							<i class="icon icon-folder el-icon-folder" v-if="scope.row.childNodeNumber >0"></i>
							<i class="icon icon-file el-icon-document" v-if="scope.row.childNodeNumber ==0"></i>{{scope.row.name}}
				    	</template>
					</el-table-column>
					<el-table-column label="图片" align="center" width="200">
						<template #default="scope">
			          		<el-image v-if="scope.row.image != null && scope.row.image != ''" style="width: 120px; height: 120px" fit="contain" :src="scope.row.image+'?time='+new Date().getTime()" :preview-src-list="[scope.row.image+'?time='+new Date().getTime()]" hide-on-click-modal ></el-image>
				    	</template>
					</el-table-column>
					<el-table-column label="分类帮助数量" align="center" width="120">
						<template #default="scope">
							<span v-if="scope.row.childNodeNumber ==0">{{scope.row.totalHelp}}</span>
				    	</template>
					</el-table-column>
					<el-table-column prop="childNodeNumber" label="下级子类数量" align="center" width="120"></el-table-column>
					<el-table-column prop="sort" label="排序" align="center" width="100"></el-table-column>
					<el-table-column label="操作" align="center" width="300">
						<template #default="scope">
							<el-button-group>
								<el-button type="primary" size="mini" @click="$router.push({path: '/admin/control/helpType/manage/add',query:{sourceParentId:($route.query.parentId != undefined ? $route.query.parentId:''), parentId: scope.row.id,page:($route.query.page != undefined ? $route.query.page:'')}});">添加子类</el-button>
								<el-button type="primary" size="mini" v-if="scope.row.childNodeNumber == 0" @click="$router.push({path: '/admin/control/helpType/manage/merger',query:{ sourceParentId:($route.query.parentId != undefined ? $route.query.parentId:''),typeId: scope.row.id,parentId: scope.row.parentId,page:($route.query.page != undefined ? $route.query.page:'')}});">合并</el-button>
								<el-button type="primary" size="mini" @click="$router.push({path: '/admin/control/helpType/manage/edit',query:{ sourceParentId:($route.query.parentId != undefined ? $route.query.parentId:''),typeId: scope.row.id,parentId: scope.row.parentId,page:($route.query.page != undefined ? $route.query.page:'')}});">修改</el-button>
								<el-button type="primary" size="mini" @click="deleteType($event,scope.row)">删除</el-button>
							</el-button-group>
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
//标签列表
export default({
	name: 'helpTypeList',//组件名称，keep-alive缓存需要本参数
	template : '#helpTypeList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
			navigation : '',//导航
			
			parentId : '',//父Id
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
		if(this.$route.query.parentId != undefined && this.$route.query.parentId != ''){
			this.parentId = this.$route.query.parentId;
		}
		//初始化
		this.queryTypeList(this.parentId,this.currentpage);
	},
	methods : {
		
		//点击单元格选择
		cellExpandRow : function(row,column,event,cell) {
			if(column.label=="分类名称"){
				if(row.childNodeNumber >0){
					//删除缓存
					this.$store.commit('setCacheNumber');
					this.$router.push({
						path: '/admin/control/helpType/list', 
						query:{ parentId : row.id,page : 1}
					});
				}
			}
		},
		
		//查询分类列表
		queryTypeList : function(parentId,page) {
			let _self = this;
			
			_self.tableData = [];
			_self.navigation = '';
			
			_self.parentId = parentId;
			_self.$ajax.get('control/helpType/list', {
			    params: {
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
		//分页
		page : function(page) {
			//删除缓存
			this.$store.commit('setCacheNumber');
			this.$router.push({
				path: '/admin/control/helpType/list', 
				query:{ parentId : this.parentId,page : page}
			});
		},
		
		//删除分类
	    deleteType : function(event,row) {
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
		    	formData.append('typeId', row.id);
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/helpType/manage?method=delete',
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
				    		_self.queryTypeList(_self.parentId,_self.currentpage);
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