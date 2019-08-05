<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>   
<%@ include file="/WEB-INF/jsp/common/taglib.jsp" %>
<!-- 模板 管理  版块模板文件名显示 -->
<select class="form-select" id="module" name="module" onChange="selectTemplateDisplayType(this);">
	<option value="请选择">请选择</option>
	<c:forEach items="${forumCodeNodeList }" var="forumCodeNode">
		<option value="${forumCodeNode.nodeName}">${forumCodeNode.nodeName}<c:if test="${forumCodeNode.remark != null && forumCodeNode.remark != ''}">&nbsp;(${forumCodeNode.remark})</c:if></option>
	</c:forEach>
</select>



