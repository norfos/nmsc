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
	$('#selectForm').attr({
		action : '/nmsc/metadb',
		target : '_self',
		method : 'post'
	}).submit(); 
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
<select name="satellite" id="satellite">
	<option value="">위성종류</option>
	<c:forEach var="data" items="${satelliteList}" varStatus="status">
		<option value="<c:out value="${data.SATELLITE}" />" <c:if test="${paramMap.satellite == data.SATELLITE}">selected="selected"</c:if>><c:out value="${data.SATELLITE}" /> </option>
	</c:forEach>
</select>
<select name="sensor" id="sensor">
	<option value="">센서</option>
	<c:forEach var="data" items="${sensorList}" varStatus="status">
		<option value="<c:out value="${data.SENSOR}" />" <c:if test="${paramMap.sensor == data.SENSOR}">selected="selected"</c:if>><c:out value="${data.SENSOR}" /> </option>
	</c:forEach>
</select>
<select name="dataLvl" id="dataLvl">
	<option value="">데이터레벨</option>
	<c:forEach var="data" items="${dataLvlList}" varStatus="status">
		<option value="<c:out value="${data.DATA_LVL}" />" <c:if test="${paramMap.dataLvl == data.DATA_LVL}">selected="selected"</c:if>><c:out value="${data.DATA_LVL}" /> </option>
	</c:forEach>
</select>
<select name="dataType" id="dataType">
	<option value="">데이터타입</option>
	<c:forEach var="data" items="${dataTypeList}" varStatus="status">
		<option value="<c:out value="${data.DATA_TYPE}" />" <c:if test="${paramMap.dataType == data.DATA_TYPE}">selected="selected"</c:if>><c:out value="${data.DATA_TYPE}" /> </option>
	</c:forEach>
</select>
<select name="dataFormat" id="dataFormat">
	<option value="">데이터포맷</option>
	<c:forEach var="data" items="${dataFormatList}" varStatus="status">
		<option value="<c:out value="${data.DATA_FORMAT}" />" <c:if test="${paramMap.dataFormat == data.DATA_FORMAT}">selected="selected"</c:if>><c:out value="${data.DATA_FORMAT}" /> </option>
	</c:forEach>
</select>
<select name="dataArea" id="dataArea">
	<option value="">데이터지역</option>
	<c:forEach var="data" items="${dataAreaList}" varStatus="status">
		<option value="<c:out value="${data.DATA_AREA}" />" <c:if test="${paramMap.dataArea == data.DATA_AREA}">selected="selected"</c:if>><c:out value="${data.DATA_AREA}" /> </option>
	</c:forEach>
</select>
<select name="dataRes" id="dataRes">
	<option value="">데이터RES</option>
	<c:forEach var="data" items="${dataResList}" varStatus="status">
		<option value="<c:out value="${data.DATA_RES}" />" <c:if test="${paramMap.dataRes == data.DATA_RES}">selected="selected"</c:if>><c:out value="${data.DATA_RES}" /> </option>
	</c:forEach>
</select>
<select name="dataProj" id="dataProj">
	<option value="">데이터PROJ</option>
	<c:forEach var="data" items="${dataProjList}" varStatus="status">
		<option value="<c:out value="${data.DATA_PROJ}" />" <c:if test="${paramMap.dataProj == data.DATA_PROJ}">selected="selected"</c:if>><c:out value="${data.DATA_PROJ}" /> </option>
	</c:forEach>
</select>
<input type="button" class="submit" value="조회" onclick="fn_select()"/>
</form>
</div>
<br/>
<div>
<table id ="le1bList" style="width:65%;text-align: center; border-collapse: separate; border-spacing: 5px 5px; font-size:17px;" border=1>
	<thead>
	<tr>
		<th style="width:5%">NO.</th>
		<th style="width:25%">FILE_PATH</th>
		<th style="width:25%">FILE_PTN</th>
		<th style="width:10%">DETAIL_SEQ</th>
	</tr>
	</thead>
	<c:forEach var="data" items="${selFileList}" varStatus="status">
	<tr >
		<td><c:out value="${fn:length(selFileList) - status.index}" /></td>
		<td style="text-align:left;"><c:out value="${data.FILE_PATH}" /></td>
		<td style="text-align:left;"><c:out value="${data.FILE_PTN}" /></td>
		<td><c:out value="${data.PRODUCT_DETAIL_SQ}" /></td>
	</tr>
	</c:forEach>
</table>
</div>


</body>
</html>