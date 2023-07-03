<!-- 问答举报列表 -->
<template id="questionReportList-template">
	<div>
		<div class="main topicReportModule">
	        <div class="navbar">
	            <el-button type="primary" size="small" plain @click="$router.push({path: '/admin/control/question/manage/view', query:{ visible:($route.query.visible != undefined ? $route.query.visible:''),questionView_beforeUrl:($route.query.questionView_beforeUrl != undefined ? $route.query.questionView_beforeUrl:''),questionId :$route.query.questionId, answerId:($route.query.answerId != undefined ? $route.query.answerId:''), page:($route.query.questionPage != undefined ? $route.query.questionPage:'')}})">返回</el-button>
	        
	            <el-button type="primary" size="small" plain @click="reductionReport($event)">还原</el-button>
	            <el-button type="primary" size="small" plain @click="deleteReport($event,undefined)">批量删除</el-button>
	        </div>
	        <div class="headInfo">
	            <div class="title">{{currentQuestion.title}}</div>
	        </div>
			<div class="data-table" >
            <el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" @selection-change="handleSelectionChange"  stripe empty-text="没有内容">
                <el-table-column type="selection" ></el-table-column>
                <el-table-column prop="reason" label="理由" align="center" ></el-table-column>
                <el-table-column prop="reportTypeName" label="分类" align="center" ></el-table-column>
                <el-table-column label="图片" align="center">
                    <template #default="scope">
                        <span v-for="imageInfo in scope.row.imageInfoList" class="image-preview">
                             <!-- 本地存储 -->
                            <el-image v-if="$store.state.fileStorageSystem == 0" preview-teleported style="width: 60px; height: 60px" fit="cover" :src="imageInfo.path+imageInfo.name" :preview-src-list="[imageInfo.path+imageInfo.name]" hide-on-click-modal ></el-image>
                            <!-- SeaweedFS存储 使用nginx image_filter的缩略图处理功能 -->
                            <el-image v-if="$store.state.fileStorageSystem == 10" preview-teleported style="width: 60px; height: 60px" fit="cover" :src="imageInfo.path+imageInfo.name+'?width=60'" :preview-src-list="[imageInfo.path+imageInfo.name]" hide-on-click-modal ></el-image>
                            <!--  MinIO存储 使用nginx image_filter的缩略图处理功能 -->
                            <el-image v-if="$store.state.fileStorageSystem == 20" preview-teleported style="width: 60px; height: 60px" fit="cover" :src="imageInfo.path+imageInfo.name+'?width=60'" :preview-src-list="[imageInfo.path+imageInfo.name]" hide-on-click-modal ></el-image>
                            <!-- 阿里云OSS存储 -->
                            <el-image v-if="$store.state.fileStorageSystem == 30" preview-teleported style="width: 60px; height: 60px" fit="cover" :src="imageInfo.path+imageInfo.name+'?x-oss-process=image/resize,w_60'" :preview-src-list="[imageInfo.path+imageInfo.name]" hide-on-click-modal ></el-image>
                        </span>
                    </template>
                </el-table-column>
                <el-table-column label="会员" align="center" min-width="120">
                    <template #default="scope">
                        <el-popover effect="light" trigger="hover" placement="top">
                            <template #default>
                                <p v-if="scope.row.isStaff == false">呢称: {{scope.row.nickname}}</p>
                                <p>账号: {{scope.row.account}}</p>
                            </template>
                            <template #reference>
                                
                               
                                <div class="avatar-wrapper"  @click="$router.push({path: '/admin/control/user/manage/show', query:{ id : scope.row.userId}})">
                                    <div class="avatar-badge" v-if="scope.row.avatarName == null || scope.row.avatarName == ''">
                                        <el-avatar :size="48" icon="el-icon-user-solid"></el-avatar>
                                    </div>
                                    <div class="avatar-badge" v-if="scope.row.avatarName != null && scope.row.avatarName != ''">
                                        <el-avatar :size="48" :src="scope.row.avatarPath+'100x100/'+scope.row.avatarName"></el-avatar>
                                    </div>
                                    
                                    <div class="avatar-text">{{scope.row.account}}</div>
                                </div>
                            </template>
                        </el-popover>
                    </template>
                </el-table-column>
                <el-table-column align="center" width="120">
                    <template #header>
						被举报内容
                    </template>
                    <template #default="scope">
                        <router-link v-if="scope.row.module == 10" tag="div" class="link" :to="{path: '/admin/control/topic/manage/view', query:{ topicId : scope.row.parameterId, reportModule:scope.row.module}}">查看话题</router-link>
                        <router-link v-if="scope.row.module == 20" tag="div" class="link" :to="{path: '/admin/control/topic/manage/view', query:{ topicId : scope.row.extraParameterId,commentId:scope.row.parameterId, reportModule:scope.row.module}}">查看评论</router-link>
                        <router-link v-if="scope.row.module == 30" tag="div" class="link" :to="{path: '/admin/control/topic/manage/view', query:{ topicId : scope.row.extraParameterId.split('-')[0], commentId:scope.row.extraParameterId.split('-')[1], replyId:scope.row.parameterId, reportModule:scope.row.module}}">查看评论回复</router-link>
                        <router-link v-if="scope.row.module == 40" tag="div" class="link" :to="{path: '/admin/control/question/manage/view', query:{ questionId : scope.row.parameterId, reportModule:scope.row.module}}">查看问题</router-link>
                        <router-link v-if="scope.row.module == 50" tag="div" class="link" :to="{path: '/admin/control/question/manage/view', query:{ questionId : scope.row.extraParameterId,answerId:scope.row.parameterId, reportModule:scope.row.module}}">查看答案</router-link>
                        <router-link v-if="scope.row.module == 60" tag="div" class="link" :to="{path: '/admin/control/question/manage/view', query:{ questionId : scope.row.extraParameterId.split('-')[0], answerId:scope.row.extraParameterId.split('-')[1], replyId:scope.row.parameterId, reportModule:scope.row.module}}">查看答案回复</router-link>
                    </template>
                </el-table-column>
                <el-table-column label="状态" align="center" width="160">
                    <template #default="scope">
                        <el-tag effect="dark"  v-if="scope.row.status==10" class="tag-wrapper">待处理</el-tag>
                        <el-tag effect="dark"  v-if="scope.row.status==40" type="warning" class="tag-wrapper">投诉失败</el-tag>
                        <el-tag effect="dark"  v-if="scope.row.status==50" type="success" class="tag-wrapper">投诉成功</el-tag>
                        <el-tag effect="dark"  v-if="scope.row.status==1010" type="danger" class="tag-wrapper">待处理【用户删除】</el-tag>
                        <el-tag effect="dark"  v-if="scope.row.status==1040" type="danger" class="tag-wrapper">投诉失败【用户删除】</el-tag>
                        <el-tag effect="dark"  v-if="scope.row.status==1050" type="danger" class="tag-wrapper">投诉成功【用户删除】</el-tag>
                        <el-tag effect="dark"  v-if="scope.row.status==100010" type="danger" class="tag-wrapper">待处理【员工删除】</el-tag>
                        <el-tag effect="dark"  v-if="scope.row.status==100040" type="danger" class="tag-wrapper">投诉失败【员工删除】</el-tag>
                        <el-tag effect="dark"  v-if="scope.row.status==100050" type="danger" class="tag-wrapper">投诉成功【员工删除】</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="处理结果" align="center" width="180">
                    <template #default="scope">
                        <div>{{scope.row.processResult}}</div>
                    </template>
                </el-table-column>
                <el-table-column label="举报时间 / 处理完成时间" align="center" width="180">
                    <template #default="scope">
                        <div>{{scope.row.postTime}}</div>
                        <div v-if="scope.row.processCompleteTime" class="processCompleteTime">{{scope.row.processCompleteTime}}</div>
                        
                    </template>
                </el-table-column>
                <el-table-column prop="remark" label="备注" align="center" width="100"></el-table-column>
                <el-table-column label="IP归属地" align="center" width="100">
                    <template #header>
                        <el-popover effect="light" trigger="hover" placement="top" width="400">
                            <template #default>
                                <el-switch v-model="ip_text" inactive-text="IP" active-text="本列单元格显示IP"></el-switch><div><br></div>
                                <el-switch v-model="ipAddress_text" inactive-text="IP归属地" active-text="本列单元格显示IP归属地"></el-switch>
                            </template>
                            <template #reference>
                            	<div class="header-arrow">IP归属地<span class="el-icon-arrow-down header-arrow-icon"></span></div>
                            </template>
                        </el-popover>
                        
                    </template>
                    <template #default="scope">
                        <div v-if="ip_text == true">
                            {{scope.row.ip}}
                        </div>
                        <div v-if="ipAddress_text == true">
                            {{scope.row.ipAddress}}
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="staffAccount" label="处理员工" align="center" width="100"></el-table-column>
                <el-table-column label="操作" align="center" fixed="right" width="200">
                    <template #default="scope">
                        <div class="button-group-wrapper">
                            <el-button-group>
                                <el-button type="primary" size="mini" v-if="scope.row.status==10" @click="processUI(scope.row)">处理</el-button>
                                <el-button type="primary" size="mini" @click="$router.push({path: '/admin/control/report/manage/edit', query:{ reportId : scope.row.id}})">修改</el-button>
                                <el-button type="primary" size="mini" @click="deleteReport($event,scope.row)">删除</el-button>
                            </el-button-group>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
            <div class="pagination-wrapper" v-if="isShowPage">
                <el-pagination background  @current-change="page" :current-page="currentpage"  :page-size="maxresult" layout="total, prev, pager, next,jumper" :total="totalrecord"></el-pagination>
            </div>
            <div class="processModule">
            
                <el-dialog title="处理" width="60%" v-model="popup_process" @close="closeProcessWindow">
                    
                    <div class="data-form" >
                        <el-form label-width="130px"  @submit.native.prevent>
                            <el-form-item label="处理状态" >
                                <el-radio-group v-model="status" size="large">
                                    <el-radio-button :label="40">投诉失败</el-radio-button>
                                    <el-radio-button :label="50">投诉成功</el-radio-button>
                                </el-radio-group>
                            </el-form-item>
                            <el-form-item label="处理结果">
                                <el-input v-model="processResult" type="textarea" :autosize="{ minRows: 2, maxRows: 4}" ></el-input>
                            </el-form-item>
                            <el-form-item label="备注">
                                <el-input v-model="remark" type="textarea" :autosize="{ minRows: 2, maxRows: 4}" ></el-input>
                                <div class="form-help" >本内容不在前台显示</div>
                            </el-form-item>
                            
                            
                            <el-form-item>
                                <el-button type="primary" size="large" class="submitButton"  @click="submitForm" :disabled="submitForm_disabled">提交</el-button>
                            </el-form-item> 
                        </el-form>
                    </div>
                    
                </el-dialog>
            </div>
		</div>
	</div>
</template>

<script>
//问答举报列表
export default({
	name: 'questionReportList',//组件名称，keep-alive缓存需要本参数
	template : '#questionReportList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
			currentQuestion :{},
	        questionId:'',
	        parameterId:'',
	        module:0,
	        multipleSelection: [],
	        
	        totalrecord : 0, //总记录数
	        currentpage : 1, //当前页码
	        totalpage : 1, //总页数
	        maxresult: 12, //每页显示记录数
	        isShowPage:false,//是否显示分页 maxresult没返回结果前就显示会导致分页栏显示页码错误
	
	        ip_text :false,//IP文本
	        ipAddress_text :true,//IP归属地文本
	
	
	        popup_process:false,//是否弹出处理窗口
	        submitForm_disabled:false,//提交按钮是否禁用
	        reportId:'',//举报Id
	        remark:'',//备注
	        status:50,//处理状态
	        processResult:'',//处理结果
	        version:0,//版本号
		};
	},
	
	created : function created() {
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		if(this.$route.query.questionId != undefined && this.$route.query.questionId != ''){
			this.questionId = this.$route.query.questionId;
		}
		if(this.$route.query.parameterId != undefined && this.$route.query.parameterId != ''){
			this.parameterId = this.$route.query.parameterId;
		}
		if(this.$route.query.module != undefined && this.$route.query.module != ''){
			this.module = this.$route.query.module;
		}
		if(this.$route.query.page != undefined && this.$route.query.page != ''){
			this.currentpage = this.$route.query.page;
		}
		
		if(this.$route.query.ip_text != undefined){
			this.ip_text = String(this.$route.query.ip_text).toLowerCase() === 'true';
		}else{
			this.ip_text = false;
		}
		if(this.$route.query.ipAddress_text != undefined){
			this.ipAddress_text = String(this.$route.query.ipAddress_text).toLowerCase() === 'true';
		}else{
			this.ipAddress_text = true;
		}
		
		
		//初始化
		this.queryQuestionReportList();
	},
	methods : {

		//查询问答举报列表
		queryQuestionReportList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			_self.$ajax.get('control/questionReport/list', {
			    params: {
	                questionId :_self.questionId,
	                parameterId :_self.parameterId,
	                module :_self.module,
	                page :_self.currentpage
	            },
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
			    			if(key == "currentQuestion"){
			    				_self.currentQuestion = mapData[key];
			    				
			    			}else if(key == "pageView"){
			    				let pageView = mapData[key];
			    				let list = pageView.records;
					    		if(list != null && list.length >0){
					    			_self.tableData = list;
					 
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
		//分页
		page: function(page) {
			//删除缓存
			this.$store.commit('setCacheNumber');
			this.$router.push({
				path: '/admin/control/questionReport/list', 
	            query:{ 
	                visible:(this.$route.query.visible != undefined ? this.$route.query.visible:''),
	                questionView_beforeUrl:(this.$route.query.questionView_beforeUrl != undefined ?this.$route.query.questionView_beforeUrl:''),
	                questionId :this.questionId,
	                answerId :(this.$route.query.answerId != undefined ? this.$route.query.answerId:''),
	                questionPage :(this.$route.query.questionPage != undefined ? this.$route.query.questionPage:''),
	                parameterId : this.parameterId,
	                module: this.module,
	                ip_text : this.ip_text,
	                ipAddress_text : this.ipAddress_text,
	                page : page
	            }
        	});
	    },
	    //处理多选
	    handleSelectionChange: function(val) {
	        this.multipleSelection = val;
	    },
	    
	    
	    //处理UI
	    processUI: function(row) {
	        this.popup_process=true;
		    this.reportId = row.id;
	        this.version = row.version;
	        //清空内容
	        this.status = 50;
	        this.processResult = '';
	        this.remark = '';
	    },
	    //关闭处理弹出框
	    closeProcessWindow: function() {
	        this.popup_process=false;
	    },
	    //提交表单
	    submitForm: function() {
	    	let _self = this;
			_self.submitForm_disabled = true;
			
		 	//清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			
			formData.append('reportId', _self.reportId);
	        formData.append('status', _self.status);
	        formData.append('processResult', _self.processResult);
	        formData.append('remark', _self.remark);
	        formData.append('version', _self.version);
			
			
			
			_self.$ajax({
		        method: 'post',
		        url: 'control/report/manage?method=reportHandle',
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
			    		
			    		_self.closeProcessWindow()
                        _self.queryQuestionReportList();
			    	}else if(returnValue.code === 500){//错误
			    		let errorMap = returnValue.data;
			    		for (let key in errorMap) {   
			    			if(key == "content"){
			    				_self.$message({
									duration :0,
						            showClose: true,
						            message: errorMap[key],
						            type: 'error'
						        });
						        continue;
			    			}
			    		
			    			_self.error[key] = errorMap[key];
			    	    }
			    		
			    	}
			    }
			   _self.submitForm_disabled = false;
			})
			.catch(function (error) {
				console.log(error);
			});
	    },
	    
		//删除举报
	    deleteReport : function(event,row) {
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
		    		formData.append('reportId', rowData.id);
		    	}
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/report/manage?method=delete',
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
				    		_self.queryQuestionReportList();
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
	    //还原举报
	    reductionReport : function(event) {
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
	    	
	    	this.$confirm('此操作将还原该举报, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	let formData = new FormData();
		    	
		    	for(let i=0; i<this.multipleSelection.length; i++){
		    		let rowData = this.multipleSelection[i];
		    		formData.append('reportId', rowData.id);
		    	}
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/report/manage?method=reduction',
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
				    		//删除缓存
			    			_self.$store.commit('setCacheNumber');
				    		_self.$message.success("还原成功");
				    		_self.queryQuestionReportList();
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