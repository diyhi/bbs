<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml"><HEAD>
<base href="${config:url(pageContext.request)}">
<TITLE>节点参数</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK href="backstage/css/list.css" type="text/css" rel="stylesheet">
<LINK href="backstage/css/table.css" type="text/css" rel="stylesheet">
<script language="JavaScript" src="backstage/jquery/jquery.min.js"></script>
<script language="JavaScript" src="backstage/jquery/jquery.jclock.js"></script>

<script type="text/javascript">
    $(function($) {
    	var start = new Date().getTime(); // 开始时间
    
		//获取服务器时间
		$.ajax({
			url:"admin/currentTime",
			async: false,
			cache:false,
			success: function(result) {
				var end = new Date().getTime(); // 结束时间
				//请求数据返回时长
				var time = Math.floor((end - start)/2);
				
				var serverDate = parseInt(result)+time;

				var options = {
			        format: '%Y-%m-%d %H:%M:%S', // 24小时制
			      	seedTime: serverDate
			    }
			    $('#jclock').jclock(options);
			}
		});   
    });
</script>



</HEAD>
<BODY>
<DIV class="d-box">
<TABLE class="t-table" cellSpacing="1" cellPadding="2" width="100%" border="0">
	<TBODY>
		<TR>
		    <TD class="t-content" width="100%" colSpan="2" height="28px" align="center">
		    	<span style="font-size: 14px;font-weight:bold;">当前节点参数</span>
		    </TD>
		</TR>
		<TR>
		    <TD class="t-label t-label-h">服务器时间：</TD>
		    <TD class="t-content" >
		    	<span id="jclock"></span>
		    </TD>
		</TR>
		<TR>
		    <TD class="t-label t-label-h">分配最大内存：</TD>
		    <TD class="t-content" >
		    	${systemNode.maxMemory}MB &nbsp;<span class="span-help">-Xmx 当前虚拟机实例从操作系统可分配到的最大内存</span>
		    </TD>
		</TR>
		<TR>
		    <TD class="t-label t-label-h" width="30%">已分配内存：</TD>
		    <TD class="t-content" width="70%" >
		    	${systemNode.totalMemory}MB &nbsp;<span class="span-help">-Xms 当前虚拟机实例已经从操作系统占用的内存</span>
		    </TD>
		</TR>
		<TR>
		    <TD class="t-label t-label-h">已分配内存中的剩余空间：</TD>
		    <TD class="t-content" >
		    	${systemNode.freeMemory}MB
		    </TD>
		</TR>
		<TR>
		    <TD class="t-label t-label-h">空闲内存：</TD>
		    <TD class="t-content" >
		    	${systemNode.usableMemory}MB &nbsp;<span class="span-help">当前虚拟机实例从操作系统分配到的最大内存中的剩余空间</span>
		    </TD>
		</TR>
		<TR>
    		<TD class="t-label t-label-h" >缓存服务器：</TD>
    		<TD class="t-content" >
    			${cacheName}
    		</TD>
  		</TR>

		<c:forEach items="${memcached_cacheStatusList}" var="cacheStatus">
			<TR>
	    		<TD class="t-label t-label-h" >服务器地址：</TD>
	    		<TD class="t-content" >
	    			${cacheStatus.serviceAddress}
	    		</TD>
	  		</TR>
	  		<TR>
	    		<TD class="t-label t-label-h">当前已使用的内存容量：</TD>
	    		<TD class="t-content">
	    			${cacheStatus.memCached_bytes}
	    		</TD>
	  		</TR>
	  		<TR>
	    		<TD class="t-label t-label-h">服务器本次启动以来，曾存储的Item总个数：</TD>
	    		<TD class="t-content" >
	    			${cacheStatus.memCached_total_items}
	    		</TD>
	  		</TR>
	  		<TR>
	    		<TD class="t-label t-label-h">MemCached服务版本：</TD>
	    		<TD class="t-content">
	    			${cacheStatus.memCached_version}
	    		</TD>
	  		</TR>
	  		<TR>
	    		<TD class="t-label t-label-h">MemCached服务器架构 ：</TD>
	    		<TD class="t-content" >
	    			${cacheStatus.memCached_pointer_size}
	    		</TD>
	  		</TR>
	  		<TR>
	    		<TD class="t-label t-label-h">服务器当前时间 ：</TD>
	    		<TD class="t-content" >
	    			${cacheStatus.memCached_time}
	    		</TD>
	  		</TR>
	  		<TR>
	    		<TD class="t-label t-label-h" >允许服务支配的最大内存容量 ：</TD>
	    		<TD class="t-content" >
	    			${cacheStatus.memCached_limit_maxbytes}
	    		</TD>
	  		</TR>
	  		<TR>
	    		<TD class="t-label t-label-h" >服务器本次启动以来，读取的数据量 ：</TD>
	    		<TD class="t-content" >
	    			${cacheStatus.memCached_bytes_read}
	    		</TD>
	  		</TR>
	  		<TR>
	    		<TD class="t-label t-label-h" >服务器本次启动以来，写入的数据量 ：</TD>
	    		<TD class="t-content">
	    			${cacheStatus.memCached_bytes_written}
	    		</TD>
	  		</TR>
	  		<TR>
	    		<TD class="t-label t-label-h" >服务器本次启动以来，总共运行时间  ：</TD>
	    		<TD class="t-content">
	    			${cacheStatus.memCached_uptime}
	    		</TD>
	  		</TR>
	  		<TR>
	    		<TD class="t-label t-label-h" >当前存储的Item个数  ：</TD>
	    		<TD class="t-content" >
	    			${cacheStatus.memCached_curr_items}
	    		</TD>
	  		</TR>
	  		<TR>
	    		<TD class="t-label t-label-h" >服务器本次启动以来，执行Get命令总次数  ：</TD>
	    		<TD class="t-content" >
	    			${cacheStatus.memCached_cmd_get}
	    		</TD>
	  		</TR>
	  		<TR>
	    		<TD class="t-label t-label-h" >服务器本次启动以来，执行Set命令总次数  ：</TD>
	    		<TD class="t-content" >
	    			${cacheStatus.memCached_cmd_set}
	    		</TD>
	  		</TR>
		</c:forEach>
	</TBODY>
</TABLE>
</DIV>
</BODY></HTML>
