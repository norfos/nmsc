<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
function fn_dataPopup(mntTime,dataArea){
	$("#mntTime").val(mntTime);
	$("#dataArea").val(dataArea);
    var pop_title = "fileDataPopup";
	window.open('', pop_title, 'width=1600,height=300,resizeable,scrollbars');
	$("#dataPopupForm").attr("action", "/nmsc/fileDataPopup");
	$("#dataPopupForm").attr("target", pop_title);
	$("#dataPopupForm").attr("method", "post");
	$("#dataPopupForm").submit();
}
</script>
</head>
<body>
dataType : ${dataType}
<form id="dataPopupForm">
	<input type="hidden" id="mntTime" name="mntTime" value=""/>
	<input type="hidden" id="dataArea" name="dataArea" value=""/>
	<input type="hidden" id="dataType" name="dataType" value="${dataType }"/>
	<input type="hidden" id="dataLevel" name="dataLevel" value="${dataLevel }"/>
	<input type="hidden" id="yyyyMMdd" name="yyyyMMdd" value="${yyyyMMdd }"/>
	<input type="hidden" id="HH" name="HH" value="${HH }"/>
	<input type="hidden" id="mm" name="mm" value="${mm }"/>
</form>
<table id ="le1bList" width=100% border=1 bordercolor="black">
	<thead>
	<tr>
							
						
	<c:forEach var="h" items="${headList}" varStatus="status">
	<th><c:out value="${h}" /></th>
	</c:forEach>
	</tr>
	</thead>
	<c:forEach var="data" items="${dataList}" varStatus="status">
	<tr>
		<c:forEach var="h" items="${headList}" varStatus="status">
		<c:choose>
		<c:when test="${h eq 'MNT_TIME' }">
			<td><c:out value="${data[h]}" /></td>
		</c:when>
		<c:otherwise>
			<td onclick="fn_dataPopup('${data.MNT_TIME}','${h}'); return false;" style="cursor:pointer;"><c:out value="${data[h]}" /></td>
		</c:otherwise>
		</c:choose>
		</c:forEach>
	</tr>
	</c:forEach>
</table>

</body>
</html>