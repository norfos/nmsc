<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<style>
#le1bList th{
	position:sticky;
	top:0px;
	background-color:gray !important;
}
</style>
<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge" charset=utf-8>
	<link rel="stylesheet" href="/resources/css/main.css" type="text/css">
	<link rel="stylesheet" href="/resources/css/jquery-ui.min.css" type="text/css">
	<script src="/resources/js/jquery-3.6.0.min.js"></script>
	<script src="/resources/js/jquery-ui.min.js"></script>
	<script src="/resources/js/main.js"></script>
<script>

</script>
</head>
<body>
<c:choose>
<c:when test="${dataLevel == 'COLLECT' }">
mntTime : ${mntTime} &nbsp;&nbsp;/&nbsp;&nbsp;dataType : ${dataType}
</c:when>
<c:otherwise>
mntTime : ${mntTime} &nbsp;&nbsp;/&nbsp;&nbsp;dataType : ${dataTypeOne}
</c:otherwise>
</c:choose>


<table id ="le1bList" style="width:100%;text-align: center;" border=1 bordercolor="black">
	<thead>
	<tr>
		<th width="5%">NO.</th>
		<th width="10%">MNT_TIME</th>
		<th width="35%">FILE_PATH</th>
		<th width="35%">FILE_PTN</th>
		<th width="10%">DATA_AREA</th>
	</tr>
	</thead>
	<c:forEach var="data" items="${fileList}" varStatus="status">
	<tr <c:if test="${data.RCT_CD eq '4' }">bgcolor="#F78181" </c:if>
		<c:if test="${data.RCT_CD eq '2' or data.RCT_CD eq '3' }">bgcolor="#F7D358" </c:if>
	>
		<td><c:out value="${fn:length(fileList) - status.index}" /></td>
		<td><c:out value="${data.MNT_TIME}" /></td>
		<td><c:out value="${data.FILE_PATH}" /></td>
		<td><c:out value="${data.FILE_PTN}" /></td>
		<td><c:out value="${data.DATA_AREA}" /></td>
	</tr>
	</c:forEach>
</table>

</body>
</html>