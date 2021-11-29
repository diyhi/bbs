<!-- 浏览量列表 -->
<template id="pageViewList-template">
	<div>
		<div class="main">
			<div class="search">
				<el-form :inline="true" label-width="auto" @keyup.enter.native="submitForm">
					<el-tooltip content="不限制请留空" placement="top">
						<el-form-item :error="error.start_times" >
							<el-date-picker v-model="start_times_format" type="datetime" format="YYYY-MM-DD HH:mm" placeholder="起始日期" ></el-date-picker>
						</el-form-item>
					</el-tooltip>
					<el-tooltip content="不限制请留空" placement="top">
						<el-form-item :error="error.end_times" >
							<el-date-picker v-model="end_times_format" type="datetime" format="YYYY-MM-DD HH:mm" placeholder="结束日期"></el-date-picker>
						</el-form-item>
					</el-tooltip>
					<el-form-item >
					    <el-button type="primary" class="submitButton" @click="submitForm" >筛选</el-button>
					    <el-button type="primary" class="submitButton" @click="nowSubmit" >今天</el-button>
					    <el-button type="primary" class="submitButton" @click="yesterdaySubmit" >昨天</el-button>
					</el-form-item>
					
				</el-form>
			</div> 
		
			<div class="data-table" >
				<el-table ref="multipleTable" :data="tableData" @selection-change="handleSelectionChange" tooltip-effect="dark" style="width: 100%" stripe empty-text="没有内容">
					<el-table-column prop="times" label="浏览时间" align="center" width="170"></el-table-column>
					<el-table-column prop="url" label="受访" align="center" ></el-table-column>
					<el-table-column prop="referrer" label="页面来源" align="center"></el-table-column>
					<el-table-column prop="ip" label="IP" align="center" width="170"></el-table-column>
					<el-table-column prop="ipAddress" label="IP归属地" align="center" width="170"></el-table-column>
					<el-table-column prop="browserName" label="浏览器名称" align="center" ></el-table-column>
					<el-table-column prop="deviceType" label="访问设备类型" align="center" width="150"></el-table-column>
					<el-table-column prop="operatingSystem" label="访问设备系统" align="center" width="150"></el-table-column>

				</el-table>
				<div class="pagination-wrapper" v-if="isShowPage">
					<el-pagination background  @current-change="page" :current-page="currentpage"  :page-size="maxresult" layout="total, prev, pager, next,jumper" :total="totalrecord"></el-pagination>
				</div>
			</div>
		</div>
	</div>
</template>  

<script>
//浏览量列表
export default({
	name: 'pageViewList',//组件名称，keep-alive缓存需要本参数
	template : '#pageViewList-template',
	inject:['reload'], 
	data : function data() {
		return {
			tableData: [],//表格内容
		    multipleSelection: [],
		  
		    start_times :'',//留言日期 起始
			end_times :'',//留言日期 结束
			
			start_times_format :'',//留言日期 起始 格式化为适合el-date-picker标签的值
			end_times_format :'',//留言日期 结束  格式化为适合el-date-picker标签的值

			error :{
				start_times :'',
				end_times :'',
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
	
		if(this.$route.query.start_times != undefined && this.$route.query.start_times != ''){
			this.start_times_format = decodeURIComponent(this.$route.query.start_times);
			this.start_times = this.start_times_format;
		}
		if(this.$route.query.end_times != undefined && this.$route.query.end_times != ''){
			this.end_times_format = decodeURIComponent(this.$route.query.end_times);
			this.end_times = this.end_times_format;
		}
		
		this.queryPageViewList();
		
		
	},
	
	methods : {
		//查询浏览量列表
		queryPageViewList : function() {
			let _self = this;
			
			//清空内容
			_self.tableData = []; 
			
			
			 _self.$ajax.get('control/pageView/list', {
			   params: {
				   start_times :_self.start_times,
					end_times :_self.end_times,
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
				path: '/admin/control/pageView/list', 
				query:{
					start_times :encodeURIComponent(this.start_times),
					end_times :encodeURIComponent(this.end_times),
					page : page
				}
			});
	    },
	  	//提交表单
  		submitForm : function() {
  			let _self = this;
  			
  			//删除缓存
  			_self.$store.commit('setCacheNumber');

  			if(_self.start_times_format != null && _self.start_times_format != ''){
  				_self.start_times = dayjs(_self.start_times_format).format('YYYY-MM-DD HH:mm');
  			}else{
  				_self.start_times = '';
  			}
  			if(_self.end_times_format != null && _self.end_times_format != ''){
  				_self.end_times = dayjs(_self.end_times_format).format('YYYY-MM-DD HH:mm');
  			}else{
  				_self.end_times = '';
  			}
  			
  			
  			_self.$router.push({
  				path: '/admin/control/pageView/list', 
  				query:{ 
  					start_times :encodeURIComponent(_self.start_times),
  					end_times :encodeURIComponent(_self.end_times),
  					page : 1,
  					time: new Date().getTime()
  				}
  			});
  	    },
  		//今天筛选提交
  		nowSubmit : function() {
  			let _self = this;
  			//删除缓存
  			_self.$store.commit('setCacheNumber');
  			
  			
  			_self.start_times =  dayjs(_self.getDay(0)+" 00:00").format('YYYY-MM-DD HH:mm');
  			
  			_self.end_times =  dayjs(_self.getDay(0)+" 23:59").format('YYYY-MM-DD HH:mm');
  			
  			
  			
  			_self.$router.push({
  				path: '/admin/control/pageView/list', 
  				query:{ 
  					start_times :encodeURIComponent(_self.start_times),
  					end_times :encodeURIComponent(_self.end_times),
  					page : 1,
  					time: new Date().getTime()
  				}
  			});
  		},
  		//昨天筛选提交
  		yesterdaySubmit : function() {
  			let _self = this;
  			//删除缓存
  			_self.$store.commit('setCacheNumber');
  			
  			
  			_self.start_times =  dayjs(_self.getDay(-1)+" 00:00").format('YYYY-MM-DD HH:mm');
  			
  			_self.end_times =  dayjs(_self.getDay(-1)+" 23:59").format('YYYY-MM-DD HH:mm');
  			
  			
  			
  			_self.$router.push({
  				path: '/admin/control/pageView/list', 
  				query:{ 
  					start_times :encodeURIComponent(_self.start_times),
  					end_times :encodeURIComponent(_self.end_times),
  					page : 1,
  					time: new Date().getTime()
  				}
  			});
  		},
  		
  		//计算今天的前后日期  参数day: 天 如2为后天 0为今天 -1为昨天
  		getDay : function(day) {
  			let today = new Date();  
 		         
  			let targetday_milliseconds=today.getTime() + 1000*60*60*24*day;          
 		  
 		    today.setTime(targetday_milliseconds); //注意，这行是关键代码    
 		         
 			let tYear = today.getFullYear();  
 			let tMonth = today.getMonth();  
 			let tDate = today.getDate();  
 		    tMonth = this.doHandleMonth(tMonth + 1);  
 		    tDate = this.doHandleMonth(tDate);  
 		    return tYear+"-"+tMonth+"-"+tDate;  
  		},
  		//补零
  		doHandleMonth : function(month) {
  			let m = month;  
  		    if(month.toString().length == 1){  
  		    	m = "0" + month;  
  		    }  
  		    return m;  
  		},
  		
	}
});


</script>