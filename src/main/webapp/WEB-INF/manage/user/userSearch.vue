<!-- 用户搜索列表 -->
<template id="userSearch-template">
	<div>
		<div class="main">
			<div class="search">
				<el-form :inline="true" label-width="auto" @keyup.enter.native="submitForm">
					<el-form-item :error="error.searchType" >
						<el-tooltip content="搜索类型" placement="top">
							<el-radio-group v-model="searchType" >
							  	<el-radio-button :label="1">用户</el-radio-button>
							    <el-radio-button :label="2">筛选条件</el-radio-button>
							</el-radio-group>
						</el-tooltip>
					</el-form-item>
					<el-form-item :error="error.userTypeCode"  v-if="searchType ==1">
	                    <el-select v-model="userTypeCode" size="large">
	                        <el-option v-for="(value, key)  in userTypeGroup" :label="value" :value="key"></el-option>
	                    </el-select>
	                </el-form-item>
					<el-form-item :error="error.keyword" style="width: 180px;" v-if="searchType ==1">
						<el-tooltip :content="'请输入'+userTypeGroup[userTypeCode]" placement="top">
	                        <el-input v-model.trim="keyword" maxlength="100" style="width: 100%;"  :placeholder="userTypeGroup[userTypeCode]"></el-input>
	                    </el-tooltip>
					</el-form-item>
					<el-form-item :error="error.start_point" style="width: 180px;" v-if="searchType ==2">
						<el-tooltip content="不限制请留空" placement="top">
							<el-input v-model.trim="start_point" maxlength="30" placeholder="起始积分"></el-input>
						</el-tooltip>
					</el-form-item>
					<el-form-item :error="error.end_point" style="width: 180px;"  v-if="searchType ==2">
						<el-tooltip content="不限制请留空" placement="top">
							<el-input v-model.trim="end_point" maxlength="30" placeholder="结束积分"></el-input>
						</el-tooltip>
					</el-form-item>
					<el-tooltip content="不限制请留空" placement="top" v-if="searchType ==2">
						<el-form-item :error="error.start_registrationDate" >
							<el-date-picker v-model="start_registrationDate_format" type="datetime" format="YYYY-MM-DD HH:mm" placeholder="起始注册日期" ></el-date-picker>
						</el-form-item>
					</el-tooltip>
					<el-tooltip content="不限制请留空" placement="top" v-if="searchType ==2">
						<el-form-item :error="error.end_registrationDate" >
							<el-date-picker v-model="end_registrationDate_format" type="datetime" format="YYYY-MM-DD HH:mm" placeholder="结束注册日期"></el-date-picker>
						</el-form-item>
					</el-tooltip>
					<el-form-item >
					    <el-button type="primary" class="submitButton"  @click="submitForm" >搜索</el-button>
					</el-form-item>
					
				</el-form>
			</div> 
			<div class="navbar" >
				<el-button type="primary" plain size="small"  @click="reductionUser($event)">批量还原</el-button>
				<el-button type="primary" plain size="small"  @click="deleteUser($event)">批量删除</el-button>
			</div>
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" @selection-change="handleSelectionChange" stripe empty-text="没有内容">
					<el-table-column type="selection" ></el-table-column>
					<el-table-column label="账号" align="center">
                    	<template #default="scope">
                        
                        	<div class="multipleInfo-wrapper" >
                            <div>{{scope.row.account}}</div>
				            	<div class="multipleInfo-wrapper-blue" v-if="scope.row.type ==20">
				                 	手机号{{scope.row.mobile}}
				                </div>
				                <div class="multipleInfo-wrapper-green" v-if="scope.row.type !=20 && scope.row.mobile !=null && scope.row.mobile !='' ">
									绑定手机{{scope.row.mobile}}
				                </div>
								<div class="multipleInfo-wrapper-tangerine" v-if="scope.row.type ==30">
									邮箱{{scope.row.email}}
								</div>
								<div class="multipleInfo-wrapper-pink" v-if="scope.row.type !=30 && scope.row.email !=null && scope.row.email !='' ">
									绑定邮箱{{scope.row.email}}
								</div>
								<el-tag effect="dark"  v-if="scope.row.cancelAccountTime !='-1'" type="danger" class="tag-wrapper">已注销</el-tag>
				              </div>  
				    	</template>
				    </el-table-column>
					<el-table-column prop="nickname" label="呢称" align="center" ></el-table-column>
					<el-table-column label="头像" align="center" min-width="100">
						<template #default="scope">
				        	
			          		<div class="user-avatar-wrapper" >
								<div class="avatar-badge" v-if="scope.row.avatarName == null || scope.row.avatarName == ''">
									<el-avatar :size="48" icon="el-icon-user-solid"></el-avatar>
								</div>
								<div class="avatar-badge" v-if="scope.row.avatarName != null && scope.row.avatarName != ''">
									<el-avatar :size="48" :src="scope.row.avatarPath+'100x100/'+scope.row.avatarName"></el-avatar>
								</div>
							</div>
				        	
				    	</template>
					</el-table-column>
					<el-table-column label="类型" align="center" width="110">
						<template #default="scope">
							<el-tag effect="dark"  v-if="scope.row.type==10" type="warning" class="tag-wrapper">账号密码用户</el-tag>
							<el-tag effect="dark"  v-if="scope.row.type==20" class="tag-wrapper" >手机用户</el-tag>
							<el-tag effect="dark"  v-if="scope.row.type==30" type="info" class="tag-wrapper-tangerine" >邮箱用户</el-tag>
							<el-tag effect="dark"  v-if="scope.row.type==40" type="success" class="tag-wrapper" >微信用户</el-tag>
							<el-tag effect="dark"  v-if="scope.row.type==80" class="tag-wrapper-purple" >其他用户</el-tag>
				    	</template>
					</el-table-column>
					<el-table-column prop="point" label="积分" align="center" ></el-table-column>
					<el-table-column prop="gradeName" label="会员等级" align="center" width="100"></el-table-column>
					<el-table-column prop="registrationDate" label="注册日期" align="center" width="150"></el-table-column>
					<el-table-column label="状态" align="center" width="100">
						<template #default="scope">
							<el-tag effect="dark"  v-if="scope.row.state == 1" type="success" class="tag-wrapper">启用</el-tag>
							<el-tag effect="dark"  v-if="scope.row.state == 2" type="info" class="tag-wrapper">停用</el-tag>
				    		<el-tag effect="dark"  v-if="scope.row.state ==11" type="warning" class="tag-wrapper">启用时删除</el-tag>
							<el-tag effect="dark"  v-if="scope.row.state ==12" type="danger" class="tag-wrapper">停用时删除</el-tag>
				    	</template>
					</el-table-column>
					<el-table-column label="操作" align="center" width="200">
						<template #default="scope">
							<div class="button-group-wrapper">
								<el-button-group>
									<el-button type="primary" size="mini" @click="$router.push({path: '/admin/control/user/manage/show', query:{ id : scope.row.id}})">查看</el-button>
									<el-button type="primary" size="mini" @click="$router.push({path: '/admin/control/user/manage/edit', query:{ id : scope.row.id}})">修改</el-button>
									<el-button type="primary" size="mini" @click="deleteUser($event,scope.row)">删除</el-button>
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
//用户搜索
export default({
	name: 'userSearch',//组件名称，keep-alive缓存需要本参数
	template : '#userSearch-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
			multipleSelection: [],
			
			userTypeGroup: {'10':'账号','20':'呢称','30':'用户名','40':'手机号','50':'邮箱'},

	        userTypeCode :'10',//用户类型
	        userTypeLabel:'账号',
			searchType :1,//搜索类型
			keyword :'',//关键词
			start_point :'',//起始积分
			end_point :'',//结束积分
			start_registrationDate :'',//起始注册日期
			end_registrationDate :'',//结束注册日期
			
			start_registrationDate_format :'',//发表日期 起始 格式化为适合el-date-picker标签的值
			end_registrationDate_format :'',//发表日期 结束  格式化为适合el-date-picker标签的值
			
			error :{
				searchType :'',
				userTypeCode :'',
				keyword :'',//关键词
				start_point :'',//起始积分
				end_point :'',//结束积分
				start_registrationDate :'',//起始注册日期
				end_registrationDate :'',//结束注册日期
			},
			
			visible :'true',//是否显示 true:未删除用户 false:已删除用户
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

		if(this.$route.query.searchType != undefined && this.$route.query.searchType != ''){
			this.searchType = this.$route.query.searchType;
		}
		if(this.$route.query.userTypeCode != undefined && this.$route.query.userTypeCode != ''){
			this.userTypeCode = this.$route.query.userTypeCode;
		}
		if(this.$route.query.keyword != undefined && this.$route.query.keyword !=''){
			this.keyword = decodeURIComponent(this.$route.query.keyword);
		}
		
		if(this.$route.query.start_point != undefined && this.$route.query.start_point !=''){
			this.start_point = decodeURIComponent(this.$route.query.start_point);
		}
		if(this.$route.query.end_point != undefined && this.$route.query.end_point !=''){
			this.end_point = decodeURIComponent(this.$route.query.end_point);
		}
		if(this.$route.query.start_registrationDate != undefined && this.$route.query.start_registrationDate != ''){
			this.start_registrationDate_format = decodeURIComponent(this.$route.query.start_registrationDate);
			this.start_registrationDate = this.start_registrationDate_format;
		}
		if(this.$route.query.end_registrationDate != undefined && this.$route.query.end_registrationDate != ''){
			this.end_registrationDate_format = decodeURIComponent(this.$route.query.end_registrationDate);
			this.end_registrationDate = this.end_registrationDate_format;
		}
		
		
		if(this.$route.query.page != undefined && this.$route.query.page != ''){
			this.currentpage = this.$route.query.page;
		}
		
		let parameterObj = new Object();
		parameterObj.searchType = encodeURIComponent(this.searchType);
		parameterObj.userTypeCode = encodeURIComponent(this.userTypeCode);
		parameterObj.keyword = encodeURIComponent(this.keyword);
		parameterObj.start_point = encodeURIComponent(this.start_point);
		parameterObj.end_point = encodeURIComponent(this.end_point);
		parameterObj.start_registrationDate = encodeURIComponent(this.start_registrationDate);
		parameterObj.end_registrationDate = encodeURIComponent(this.end_registrationDate);
		
		//将请求参数转为base62
		let encrypt = delete_base62_equals(base62_encode(JSON.stringify(parameterObj)));
		this.parameterGroup = encrypt;
		
		if(this.$route.query.superiorParameterGroup != undefined && this.$route.query.superiorParameterGroup != ''){//上级节点参数组
			let decrypt =base62_decode(add_base62_equals(this.$route.query.superiorParameterGroup));
			
			let parameObj = JSON.parse(decrypt);
			
			this.searchType = decodeURIComponent(parameObj.searchType);
			this.userTypeCode = decodeURIComponent(parameObj.userTypeCode);
			this.keyword = decodeURIComponent(parameObj.keyword);
			this.start_point = decodeURIComponent(parameObj.start_point);
			this.end_point = decodeURIComponent(parameObj.end_point);
			this.start_registrationDate = decodeURIComponent(parameObj.start_registrationDate);
			this.end_registrationDate = decodeURIComponent(parameObj.end_registrationDate);	
			
			
			this.start_registrationDate_format = this.start_registrationDate;
			this.end_registrationDate_format = this.end_registrationDate;

		}
		
		
		
		//初始化
		this.queryUserList();
	},
	methods : {
		//查询用户列表
		queryUserList : function() {
			let _self = this;
			_self.$ajax.get('control/user/search', {
			    params: {
			    	searchType :_self.searchType,//搜索类型
			    	userTypeCode :_self.userTypeCode,
			    	keyword :_self.keyword,
			    	start_point :_self.start_point,
					end_point :_self.end_point,
					start_registrationDate :_self.start_registrationDate,
					end_registrationDate :_self.end_registrationDate,
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
			    		let mapData = returnValue.data;
			    		for(let key in mapData){
			    			if(key == "pageView"){
			    				let pageView = mapData[key];
					    		let userList = pageView.records;
					    		if(userList != null && userList.length >0){
					    			_self.tableData = userList;
					 
					    			_self.totalrecord = parseInt(pageView.totalrecord);//服务器返回的long类型已转为String类型
					    			_self.currentpage = pageView.currentpage;
									_self.totalpage = parseInt(pageView.totalpage);//服务器返回的long类型已转为String类型
									_self.maxresult = pageView.maxresult;
									_self.isShowPage = true;//显示分页栏
					    		}
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
  		page: function(page) {;
  			
  			//删除缓存
  			this.$store.commit('setCacheNumber');
  			this.$router.push({
  				path: '/admin/control/user/search', 
  				query:{ 
  					searchType :this.searchType,//搜索类型
  					userTypeCode :this.userTypeCode,//搜索类型
  					keyword :encodeURIComponent(this.keyword),
  					start_point : encodeURIComponent(this.start_point),
					end_point :encodeURIComponent(this.end_point),
					start_registrationDate :encodeURIComponent(this.start_registrationDate),
					end_registrationDate :encodeURIComponent(this.end_registrationDate),
  					page : page
  				}
  			});
  	    },
  		//提交表单
		submitForm : function() {
			let _self = this;
			
			//删除缓存
			_self.$store.commit('setCacheNumber');

			
			if(_self.start_registrationDate_format != null && _self.start_registrationDate_format != ''){
				_self.start_registrationDate = dayjs(_self.start_registrationDate_format).format('YYYY-MM-DD HH:mm');
			}else{
				_self.start_registrationDate = '';
			}
			if(_self.end_registrationDate_format != null && _self.end_registrationDate_format != ''){
				_self.end_registrationDate = dayjs(_self.end_registrationDate_format).format('YYYY-MM-DD HH:mm');
			}else{
				_self.end_registrationDate = '';
			}
			
			
			_self.$router.push({
				path: '/admin/control/user/search', 
				query:{ 
					searchType :_self.searchType,//搜索类型
					userTypeCode :_self.userTypeCode,
					keyword :encodeURIComponent(_self.keyword),
					
					start_point :encodeURIComponent(_self.start_point),
					end_point :encodeURIComponent(_self.end_point),
					start_registrationDate :encodeURIComponent(_self.start_registrationDate),
					end_registrationDate :encodeURIComponent(_self.end_registrationDate),
					page : 1,
					time: new Date().getTime()
				}
			});
	    },
	    
		//删除用户
	    deleteUser : function(event,row) {
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
		    		formData.append('userId', rowData.id);
		    	}
		    	formData.append('visible', _self.visible);
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/user/manage?method=delete',
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
				    		_self.queryUserList();
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
	  	//处理多选
	    handleSelectionChange: function(val) {
	        this.multipleSelection = val;
	    },
	  	//还原用户
	    reductionUser : function(event) {
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
	    	
	    	this.$confirm('此操作将还原该用户, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	
		    	for(let i=0; i<this.multipleSelection.length; i++){
		    		let rowData = this.multipleSelection[i];
		    		formData.append('userId', rowData.id);
		    	}
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/user/manage?method=reduction',
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
				    		_self.queryUserList();
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
