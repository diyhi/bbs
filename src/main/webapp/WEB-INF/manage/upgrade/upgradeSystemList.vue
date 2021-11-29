<!-- 升级 -->
<template id="upgradeSystemList-template">
	<div>
		<div class="main upgradeModule">
			<div class="navbar">
				<el-button type="primary" plain size="small" @click="uploadUpgradePackageUI()">上传升级包</el-button>
			</div>
			<el-dialog title="上传升级包" v-model="uploadForm">
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item :error="error.file">
						<el-upload class="upload" drag :http-request="uploadUpgradePackage" :file-list="fileList" :show-file-list="false" :accept="'.zip'">
							<i class="el-icon-upload"></i>
							<div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
							<template #tip>
							    <div class="el-upload__tip">
							    	只允许上传 zip 格式文件
							    </div>
							</template>
						</el-upload>
					</el-form-item>
					<el-form-item>
						<el-progress :text-inside="true" :stroke-width="20" :percentage="progressPercent" />
			        </el-form-item>
  				</el-form>
			</el-dialog>
			
			<div class="data-form" >
				<el-form label-width="auto"  @submit.native.prevent>
					<el-tabs v-model="item" type="card"  @tab-click="handleClick">
					    <el-tab-pane label="升级" name="1"></el-tab-pane>
					    <el-tab-pane label="升级包列表" name="2"></el-tab-pane>
					</el-tabs>
					<el-form-item  v-if="item ==1 && notCompletedUpgrade == null">
						<span style="font-size: 16px;color: #409EFF;margin-left: 12px;">当前BBS版本 {{currentVersion}}</span>
					</el-form-item>
					<div v-if="item ==1 && notCompletedUpgrade != null">
						<el-form-item>
							<div class="singleRowTable">
								<div class="leftCell">
									 <el-button type="primary" class="submitButton" @click="upgradeNow($event,notCompletedUpgrade)" plain :disabled="upgradeNow_disabled" v-if="upgradeNow_show">立即升级</el-button>
								</div>
								<div>
									 <el-button type="primary" class="submitButton" @click="continueUpgrade($event,notCompletedUpgrade)" :disabled="continueUpgrade_disabled" v-if="continueUpgrade_show">继续升级</el-button>
								</div>
							</div>
						</el-form-item>
						<el-form-item v-if="isUpgradeComplete">
							<span style="font-size: 16px;color: #67C23A;margin-left: 12px;">升级完成</span>
						</el-form-item>
						
						<el-form-item v-if="interruptError">
							<span style="font-size: 16px;color: #F56C6C;margin-left: 12px;">出现错误升级已中断,原因请查看日志</span>
						</el-form-item>
						<el-form-item v-if="interruptRestart">
							<span style="font-size: 16px;color: #E6A23C;margin-left: 12px;">需重启应用服务器才能继续升级</span>
						</el-form-item>
						
						<el-form-item>
							<div style="font-size: 16px;color: #F56C6C;margin-left: 12px;">1.升级前请先备份所有数据</div>
							<div style="font-size: 16px;color: #F56C6C;margin-left: 12px;">2.升级过程不能中断</div>
						</el-form-item>
						
						
					</div>
					
				</el-form>
			</div>
			
			<div class="detail-data" v-if="item ==1 && notCompletedUpgrade != null">
				<el-row :gutter="10" type="flex" class="item">
					<el-col :span="4"><div class="name">升级包文件名称</div></el-col>
					<el-col :span="20"><div class="content">{{notCompletedUpgrade.updatePackageName}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex" class="item">
					<el-col :span="4"><div class="name">升级包上传时间</div></el-col>
					<el-col :span="20"><div class="content">{{notCompletedUpgrade.updatePackageTime}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex" class="item">
					<el-col :span="4"><div class="name">旧BBS版本</div></el-col>
					<el-col :span="20"><div class="content">{{notCompletedUpgrade.oldSystemVersion}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex" class="item">
					<el-col :span="4"><div class="name">升级包版本</div></el-col>
					<el-col :span="20"><div class="content">{{notCompletedUpgrade.updatePackageVersion}}</div></el-col>
				</el-row>
				<el-row :gutter="10" type="flex" class="item">
					<el-col :span="4"><div class="name">升级日志</div></el-col>
					<el-col :span="20">
						<div class="content">
							<div class="singleRowTable" v-for="upgradeLog in notCompletedUpgrade.upgradeLogList">
								<div class="leftCell">
									<span class="time">{{upgradeLog.time}}</span>
								</div>
								<div>
									<span v-if="upgradeLog.grade == 2" style="color: #F56C6C;">{{upgradeLog.content}}</span>
									<span v-else>{{upgradeLog.content}}</span>
								</div>
							</div>
						</div>
					</el-col>
				</el-row>
				<el-row :gutter="10" type="flex" class="item">
					<el-col :span="4"><div class="name">说明</div></el-col>
					<el-col :span="20"><div class="content" v-html="notCompletedUpgrade.explanation"></div></el-col>
				</el-row>
			</div>
			
			<div class="data-table detail-data" v-if="item ==1">
				<el-table ref="multipleTable" :data="tableData" tooltip-effect="dark" style="width: 100%" row-key="id" @cell-click="cellExpandRow" stripe empty-text="没有内容">
					<el-table-column prop="id" label="当前版本" align="center" ></el-table-column>
					<el-table-column prop="oldSystemVersion" label="旧BBS版本" align="center" width="150"></el-table-column>
					<el-table-column prop="updatePackageVersion" label="升级包版本" align="center" width="150"></el-table-column>
					<el-table-column prop="upgradeTime" label="升级时间" align="center" width="170"></el-table-column>
					<el-table-column label="操作" align="center" width="200">
						<template #default="scope">
							<el-button type="text" @click="toogleExpand(scope.row)">升级日志</el-button>
							<el-button type="text" @click="explanationView(scope.row)">说明</el-button>
				    	</template>
					
					</el-table-column>
					<el-table-column type="expand" width="1"><!-- width="1"用来隐藏带箭头列 -->
				    	<template #default="props">
				    		<el-form>
					        	<span style="color: #99a9bf;">升级日志</span>
					        </el-form>
					        <el-form label-position="left">
					        	<div class="singleRowView" v-for="upgradeLog in props.row.upgradeLogList">
									<div class="leftCell">
										<span class="time">{{upgradeLog.time}}</span>
									</div>
									<div>
										<span v-if="upgradeLog.grade == 2" style="color: #F56C6C;">{{upgradeLog.content}}</span>
										<span v-else>{{upgradeLog.content}}</span>
									</div>
								</div>
					        </el-form>
				    	</template>
				    </el-table-column>
				</el-table>
			</div>
			
			
			<el-dialog title="说明" v-model="explanation_popup">
				<el-form label-width="auto"  @submit.native.prevent>
					<el-form-item>
						<span v-html="explanation_data"></span>
					</el-form-item>
  				</el-form>
			</el-dialog>
			
			
			<div class="data-table detail-data" v-if="item ==2">
				<el-table ref="upgradePackage_multipleTable" :data="upgradePackage_tableData" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
					<el-table-column prop="name" label="升级包名称" align="center" ></el-table-column>
					<el-table-column prop="size" label="文件大小" align="center" width="150"></el-table-column>
					<el-table-column prop="lastModifiedTime" label="上传时间" align="center" width="170"></el-table-column>
					<el-table-column label="操作" align="center" width="200">
						<template #default="scope">
							<el-button type="text" @click="deleteUpgradePackage($event,scope.row)">删除</el-button>
				    	</template>
					</el-table-column>
				</el-table>
			</div>
			
		</div>
	</div>
</template>

<script>
//升级
export default({
	name: 'upgradeSystemList',//组件名称，keep-alive缓存需要本参数
	template : '#upgradeSystemList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
			
			upgradePackage_tableData: [],//升级包表格内容
			
			
			item :'1',
			
			uploadForm:false,//是否显示上传表单
			fileList:[],//上传的文件列表
			progressPercent: 0, // 进度条默认为0
			
			error:{
				file:'',
			},
			
			currentVersion:'',
			notCompletedUpgrade:'',
			
			explanation_popup:false,//说明弹出层
			explanation_data:'',//说明弹出层数据
			
			interruptError:false,//中断提示
			interruptRestart:false,//重启提示
			isUpgradeComplete:false,//是否升级完成
			upgradeNow_show:false,//立即升级按钮是否显示
			upgradeNow_disabled:true,//立即升级按钮是否禁用按钮
			continueUpgrade_show:false,//继续升级按钮是否显示
			continueUpgrade_disabled:true,//继续升级按钮是否禁用按钮
		};
	},
	
	created : function created() {
		//设置缓存
		this.$store.commit('setCacheComponents',  [this.$route.name]);
		
		
		//初始化
		this.queryUpgradeSystemList();
	},
	methods : {
		handleClick : function(tab, event) {
	        if(tab.props.name == "2"){
	        	this.queryUpgradePackageList();
	        }
	    },
		
		
		//查询升级
		queryUpgradeSystemList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = [];
			
			_self.$ajax.get('control/upgrade/manage', {
			    params: {
			    	method :  'upgradeSystemList'
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
			    			if(key == "currentVersion"){
			    				_self.currentVersion = mapData[key];
			    			}else if(key == "notCompletedUpgrade"){
			    				let notCompletedUpgrade = mapData[key];
			    				if(notCompletedUpgrade != null){
			    					if(notCompletedUpgrade.runningStatus == 0){
			    						_self.upgradeNow_show = true;//显示立即升级按钮
			    						_self.upgradeNow_disabled = false;//立即升级按钮启用
			    						_self.continueUpgrade_show = false;//隐藏立即升级按钮
			    					}else{
			    						_self.upgradeNow_show = false;//隐藏立即升级按钮
			    						_self.upgradeNow_disabled = true;//立即升级按钮禁用
			    						
			    						_self.continueUpgrade_show = true;//显示立即升级按钮
			    						
			    					}
			    					if(notCompletedUpgrade.interruptStatus == 0 && notCompletedUpgrade.runningStatus > 20){
			    						_self.continueUpgrade_disabled = true;//继续升级按钮禁用
		    						}else{
		    							_self.continueUpgrade_disabled = false;//继续升级按钮启用
		    							
		    						}
			    					
			    				
					    			if(notCompletedUpgrade.interruptStatus == 1){//1:错误
					    				_self.interruptError = true;//中断提示
					    				
					    				//按钮设为可用 disabled="disabled"
					    				_self.continueUpgrade_disabled = false;//继续升级按钮是否禁用按钮
					        
					    			}
			    					
			    					
			    				}
			    				
			    				_self.notCompletedUpgrade = notCompletedUpgrade;
			    			}else if(key == "upgradeSystemList"){
			    				let upgradeSystemList = mapData[key];
			    				if(upgradeSystemList != null &&  upgradeSystemList.length >0){
			    					for(let i = upgradeSystemList.length - 1; i >= 0; i--) {
			    						let upgradeSystem = upgradeSystemList[i];
			    						if(upgradeSystem.runningStatus != 9999){
			    							upgradeSystemList.splice(i, 1);
			    						}
			    						
			    					}
			    				}
			    				
			    				_self.tableData = upgradeSystemList;
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
		//升级日志 展开行
		toogleExpand : function(row) {
			this.$refs.multipleTable.toggleRowExpansion(row);
		},
		//说明 弹出层数据
		explanationView : function(row) {
			this.explanation_popup =true;
			this.explanation_data = row.explanation;
		},
		

		//查询当前升级 timing：是否定时执行
		queryCurrentUpgrade : function(id,timing) {
			let _self = this;
			
			_self.$ajax.get('control/upgrade/manage', {
			    params: {
			    	method :  'queryUpgrade',
			    	upgradeSystemId :  encodeURIComponent(id)
			    },
			    headers: {
			      	'showLoading': false,//是否显示图标
			      	'loadingMask':false// 是否显示遮罩层
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
			    		let upgradeSystem = returnValue.data;
			    		if(upgradeSystem != null){
			    			if(upgradeSystem.upgradeLogList != null && upgradeSystem.upgradeLogList.length >0){
				    			_self.notCompletedUpgrade.upgradeLogList = upgradeSystem.upgradeLogList;
				    		}
			    			
			    			
			    			if(upgradeSystem.runningStatus == 9999){
			    				_self.continueUpgrade_show = false;//隐藏继续升级按钮
			    				
			    				//显示完成升级
			    				_self.isUpgradeComplete = true;
			    			}
			    			//中断状态
			    			if(upgradeSystem.interruptStatus == 0){//0:正常
			    				_self.interruptError = false;//中断提示
			    				_self.interruptRestart = false;//重启提示
			    			}
			    			if(upgradeSystem.interruptStatus == 1){//1:错误
			    				_self.interruptError = true;//中断提示
			    				
			    				//按钮设为可用 disabled="disabled"
			    				_self.continueUpgrade_disabled = false;//继续升级按钮是否禁用按钮
			        
			    			}
			            	if(upgradeSystem.interruptStatus == 2){//2:待重启
			            		_self.interruptRestart = true;//重启提示
			    			}
			    			
			    			if(timing == true && upgradeSystem.runningStatus <9999 && upgradeSystem.interruptStatus < 2){
			    				setTimeout(function(){
			    					_self.queryCurrentUpgrade(id,timing)
			    				}, 5000);//5秒钟刷新 
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
		
		//立即升级
	    upgradeNow : function(event,upgradeSystem) {
			//强制失去焦点
			let target = event.target;
			// 根据button组件内容 里面包括一个span标签，如果设置icon，则还包括一个i标签，其他情况请自行观察。
    		// 所以，在我们点击到button组件上的文字也就是span标签上时，直接执行e.target.blur()不会生效，所以要加一层判断。
        	if(target.nodeName == 'SPAN' || target.nodeName == 'I'){
            	target = event.target.parentNode;
       		}
        	target.blur();
			
			
	    	let _self = this;
	    	
	    	
	    	
	    	this.$confirm('此操作将升级系统, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	//隐藏‘立即升级’按钮 disabled="disabled"
		    	_self.upgradeNow_show = false;
		    	
	        	
	        	let formData = new FormData();
		    	formData.append('updatePackageName', upgradeSystem.updatePackageName);
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/upgrade/manage?method=upgradeNow',
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
				    		_self.$message.success("升级任务开始运行");
				    		//读取当前升级
	        				_self.queryCurrentUpgrade(upgradeSystem.id,false);
	        				//显示继续升级按钮
	        				_self.continueUpgrade_show = true;
	        				_self.continueUpgrade_disabled =false;
	        				
	        				_self.upgradeNow_show = false;//隐藏立即升级按钮
    						_self.upgradeNow_disabled = true;//立即升级按钮禁用
	        				
	        				//执行继续升级
	        				_self.continueUpgrade(event,upgradeSystem);
				    		
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
	    
	  	//继续升级
	    continueUpgrade : function(event,upgradeSystem) {
			//强制失去焦点
			let target = event.target;
			// 根据button组件内容 里面包括一个span标签，如果设置icon，则还包括一个i标签，其他情况请自行观察。
    		// 所以，在我们点击到button组件上的文字也就是span标签上时，直接执行e.target.blur()不会生效，所以要加一层判断。
        	if(target.nodeName == 'SPAN' || target.nodeName == 'I'){
            	target = event.target.parentNode;
       		}
        	target.blur();
			
			
	    	let _self = this;
	    	
	    	//按钮设为不可用 disabled="disabled"
	    	_self.continueUpgrade_disabled = true;
	    	
	    	
        	let formData = new FormData();
	    	formData.append('upgradeId', upgradeSystem.id);
	    	
	    	
			this.$ajax({
		        method: 'post',
		        url: 'control/upgrade/manage?method=continueUpgrade',
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
			    		_self.$message.success("继续升级任务开始运行");
			    		//执行定时刷新	
						_self.queryCurrentUpgrade(upgradeSystem.id,true);
	
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
			    		_self.continueUpgrade_disabled = false;
			    		
			    	}
			    }
			})
			.catch(function (error) {
				console.log(error);
			});
	    },
	  	//查询升级包列表
  		queryUpgradePackageList : function() {
  			let _self = this;
  			
  			//清空内容
  			_self.upgradePackage_tableData = [];
  			
  			_self.$ajax.get('control/upgrade/manage', {
  			    params: {
  			    	method :  'queryUpgradePackageList'
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
  			    		_self.upgradePackage_tableData = returnValue.data;
  			    		
  			    		
  			    	}else if(returnValue.code === 500){//错误
  			    		
  			    		
  			    	}
  			    }
  			})
  			.catch(function (error) {
  				console.log(error);
  			});
  		},
  		
  		//上传升级包UI
		uploadUpgradePackageUI: function() {
			//清除错误
			for (let key in this.error) { 
    			this.error[key] = "";
    	    }
        	
        	this.uploadForm =true;//是否显示上传表单
        	this.progressPercent= 0; // 进度条默认为0
        	this.fileList=[];
		},
	
		//上传升级包
		uploadUpgradePackage: function(param) {
			let _self = this;


			//清除错误
			for (let key in _self.error) { 
    			_self.error[key] = "";
    	    }
			let formData = new FormData();
			formData.append('file', param.file);
			
	    	
			_self.$ajax({
		        method: 'post',
		        url: 'control/upgrade/manage?method=uploadUpgradePackage',
		        data: formData,
		        onUploadProgress: progressEvent => {
		            if (progressEvent.lengthComputable) {
                    	let rate = progressEvent.loaded / progressEvent.total;  //已上传的比例
                        if (rate < 1) {
                        	//这里的进度只能表明文件已经上传到后台，但是后台有没有处理完还不知道
                            //因此不能直接显示为100%，不然用户会误以为已经上传完毕，关掉浏览器的话就可能导致上传失败
                            //等响应回来时，再将进度设为100%
                            // progressEvent.loaded:已上传文件大小
		            		// progressEvent.total:被上传文件的总大小
                            _self.progressPercent = (progressEvent.loaded / progressEvent.total * 100).toFixed(2);
                        }
                    }
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
			    		_self.progressPercent = 100;
			    		_self.$message.success("上传成功");
			    		
			    		//删除缓存
			    		_self.$store.commit('setCacheNumber');
			    		
			    		_self.uploadForm = false;
			    		_self.fileList=[];
			    		_self.queryUpgradePackageList();
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
  			
  		//删除升级包
	    deleteUpgradePackage : function(event,upgradePackage) {
			//强制失去焦点
			let target = event.target;
			// 根据button组件内容 里面包括一个span标签，如果设置icon，则还包括一个i标签，其他情况请自行观察。
    		// 所以，在我们点击到button组件上的文字也就是span标签上时，直接执行e.target.blur()不会生效，所以要加一层判断。
        	if(target.nodeName == 'SPAN' || target.nodeName == 'I'){
            	target = event.target.parentNode;
       		}
        	target.blur();
			
			
	    	let _self = this;
	    	
	    	
	    	
	    	this.$confirm('此操作将删除升级包, 是否继续?', '提示', {
	            confirmButtonText: '确定',
	            cancelButtonText: '取消',
	            type: 'warning'
	        }).then(() => {
	        	
	        	
	        	let formData = new FormData();
		    	formData.append('fileName', upgradePackage.name);
		    	
		    	
				this.$ajax({
			        method: 'post',
			        url: 'control/upgrade/manage?method=deleteUpgradePackage',
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
				    		
	        				_self.queryUpgradePackageList();
				    		
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
