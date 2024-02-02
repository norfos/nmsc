<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge" charset=utf-8>
	<link rel="stylesheet" href="/resources/css/main.css" type="text/css">
	<link rel="stylesheet" href="/resources/css/jquery-ui.min.css" type="text/css">
	<script src="/resources/js/jquery-3.6.0.min.js"></script>
	<script src="/resources/js/jquery-ui.min.js"></script>
	<script src="/resources/js/main.js"></script>
<script>
//파일 다운로드
function fn_select(){
	
	if($("#search").val() == '' || $("#searchVal").val() == ''){
		alert("파일경로나 파일명을 선택해주세요.");
		return;
	}else{
		$('#selectForm').attr({
			action : '/nmsc/metadb2',
			target : '_self',
			method : 'post'
		}).submit(); 
	}
}
</script>
</head>
<body>
<div>
<div>
	<table style="width:100%; background-color:#58D3F7; margin: auto; text-align: center;">
		<tr>
			<td width="20%"></td>
			<td width="60%" style="color:white;"><H1>위성산출물 메타데이터 조회 시스템</H1></td>
			<td width="20%">
			</td>
		</tr>
	</table>
</div>
</div>
<div class="item-wrap">
<form name="selectForm" id="selectForm" action="">
<select name="search" id="search">
	<option value="" >선택</option>
	<option value="path" <c:if test="${paramMap.search == 'path'}">selected="selected"</c:if>>파일경로</option>
	<option value="ptn" <c:if test="${paramMap.search == 'ptn'}">selected="selected"</c:if>>파일명</option>
</select>
<input type="text" name="searchVal" id="searchVal" style="width:350px;" value="${paramMap.searchVal }" />

<input type="button" class="submit" value="조회"  onclick="fn_select()"/>
</form>
</div>
<br/>
<div>
<table id ="le1bList" style="width:100%;text-align: center; border-collapse: separate; border-spacing: 5px 5px; font-size:17px;" border=1>
	<thead>
	<tr>
		<th style="width:6%">NO.</th>
		<th style="width:6%">SATELLITE</th>
		<th style="width:6%">SENSOR</th>
		<th style="width:6%">DATA_LVL</th>
		<th style="width:6%">DATA_TYPE</th>
		<th style="width:6%">DATA_FORMAT</th>
		<th style="width:6%">DATA_AREA</th>
		<th style="width:6%">DATA_RES</th>
		<th style="width:6%">DATA_PROJ</th>
		<th style="width:15%">FILE_PATH</th>
		<th style="width:10%">FILE_PTN</th>
		<th style="width:6%">PRODUCT_DETAIL_SQ</th>
	</tr>
	</thead>
	<c:forEach var="data" items="${selMetadbList}" varStatus="status">
	<tr >
		<td><c:out value="${fn:length(selMetadbList) - status.index}" /></td>
		<td><c:out value="${data.SATELLITE}" /></td>
		<td><c:out value="${data.SENSOR}" /></td>
		<td><c:out value="${data.DATA_LVL}" /></td>
		<td><c:out value="${data.DATA_TYPE}" /></td>
		<td><c:out value="${data.DATA_FORMAT}" /></td>
		<td><c:out value="${data.DATA_AREA}" /></td>
		<td><c:out value="${data.DATA_RES}" /></td>
		<td><c:out value="${data.DATA_PROJ}" /></td>
		<td style="text-align:left;"><c:out value="${data.FILE_PATH}" /></td>
		<td style="text-align:left;"><c:out value="${data.FILE_PTN}" /></td>
		<td><c:out value="${data.PRODUCT_DETAIL_SQ}" /></td>
	</tr>
	</c:forEach>
</table>
</div>


</body>
</html>