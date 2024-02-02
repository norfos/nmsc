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
.BlackBtn{
	position:fixed;
	bottom:20px;
	right:20px;
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
$(document).ready(function() {
	// 전체선택 체크시 비활성화 체크박스제외 활성화 처리
	$('input[name=selectall]').click(function(){
		var checked = $('input[name=selectall]').is(':checked');
		if(checked) {
			$('input:checkbox:not(:disabled)').prop('checked',true);
		} else {
			$('input:checkbox:not(:disabled)').prop('checked',false);
		}
	});
	
	// 전체선택후 체크박스 클릭시 전체선택 해제-활성화 처리
	$(document).on('click', 'input[name=seq]', function(){
    	if($('input[name=seq]:checkbox:checked').length == $("input[name=seq]").length) {
			$("input[name=selectall]").prop("checked",true);
		} else {
			$("input[name=selectall]").prop("checked",false);
		}
	});
});


function fn_dataPopup(satellite,mntTime,dataType){
	$("#satellite").val(satellite);
	$("#mntTime").val(mntTime);
	$("#dataTypeOne").val(dataType);
    var pop_title = "fileDataPopup";
	window.open('', pop_title, 'width=1600,height=300,resizeable,scrollbars');
	$("#dataPopupForm").attr("action", "/nmsc/fileDataPopup");
	$("#dataPopupForm").attr("target", pop_title);
	$("#dataPopupForm").attr("method", "post");
	$("#dataPopupForm").submit();
}



//체크 업데이트
function checkkViewYn() {
	if($("input[name=seq]").is(":checked") == false) {
		alert("체크된 파일이 없습니다.");
		return false;
	} else {
		
		var dataType = $("#dataType").val();
		var dataLevel = $("#dataLevel").val();
		var checkboxValues = [];
		$("input[name='seq']:checked").each(function(i){
			checkboxValues.push($(this).val());
		});
		var allData = {"dataType":dataType, "dataLevel":dataLevel,  "checkArray":checkboxValues};
		$.ajax({
			url:'/nmsc/checkViewYn',
			type:'POST',
			data: allData,
			success:function(data){
				alert(data.updateed+"건 예약완료 \n최대1분후 처리됩니다.");
				window.opener.location.reload();
				self.close();
			},
			error:function(request, status, error){
		  		alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
		    }
		});
	}
}
</script>
</head>
<body>
<c:choose>
<c:when test="${satellite == 'GK2A' }">dataType : ${dataType} </c:when>
<c:when test="${dataLevel == 'COLLECT'}">dataType : ${dataType} </c:when>
<c:otherwise>위성명 : ${satellite}</c:otherwise>
</c:choose>

<form id="dataPopupForm">
	<input type="hidden" id="satellite" name="satellite" value="${satellite }"/>
	<input type="hidden" id="mntTime" name="mntTime" value=""/>
	<input type="hidden" id="dataTypeOne" name="dataTypeOne" value=""/>
	<input type="hidden" id="dataType" name="dataType" value="${dataType }"/>
	<input type="hidden" id="dataLevel" name="dataLevel" value="${dataLevel }"/>
	<input type="hidden" id="yyyyMMdd" name="yyyyMMdd" value="${yyyyMMdd }"/>
	<input type="hidden" id="HH" name="HH" value="${HH }"/>
	<input type="hidden" id="mm" name="mm" value="${mm }"/>
</form>
<form id="tableForm" name="tableForm">
<input type="hidden" id="dataType" name="dataType" value="${dataType}"/>
<table id ="le1bList"  border=1 style="text-align: center; width:100%; table-layout:fixed;">
	<thead>
	<tr>
		<th width="160px"><c:out value="MNT_TIME" /></th>
		<th width="160px"><c:out value="SATELLITE" /></th>
		<c:forEach var="h" items="${headList}" varStatus="status">
			<c:if test="${h ne 'MNT_TIME' and h ne 'SATELLITE' }">
				<th width="100px" style="word-break:break-all"><c:out value="${h}" /></th>
			</c:if>
		</c:forEach>
		<th width="50px"><input type="checkbox" name="selectall" value="AllSelect" /></th>
	</tr>
	</thead>
	<tbody>
	<c:forEach var="data" items="${dataList}" varStatus="status">
		<c:set var="minVal" value="${fn:substring(data.MNT_TIME,12,14)}"/>
		<c:set var="cycleVal" value="${fn:substring(data.MNT_TIME,17,19)}"/>
		<tr>
			<td style="position:sticky; left:0; z-index:10; background-color:gray !important;"><c:out value="${data.MNT_TIME}" /></td>
			<td><c:out value="${data.SATELLITE}" /></td>
			<c:forEach var="h" items="${headList}" varStatus="status">
				<c:if test="${h ne 'MNT_TIME' and h ne 'SATELLITE'}">
					<c:choose>
						<c:when test="${h eq 'RCT_NOT_CNT'}">
							<td><c:out value="${data[h]}" /></td>
						</c:when>
						<c:otherwise>
							<c:set var="gab" value="${fn:split(data[h],'/')}"/>
							<c:set var="gubun" value="/"/>
							<td 
								<c:if test="${gab[3] > 0 }">bgcolor="#F7D358" </c:if>
								<c:if test="${gab[4] > 0 }">bgcolor="#F78181" </c:if>
								<c:choose>
								<c:when test="${gab[0] == 0}">
								><c:out value="" />
								</c:when>
								<c:otherwise>
								onclick="fn_dataPopup('${data.SATELLITE}','${data.MNT_TIME}','${h}'); return false;" style="cursor:pointer;"><c:out value="${gab[0]}${gubun}${gab[1]}${gubun}${gab[2]}" />
								</c:otherwise>
								</c:choose>
						</c:otherwise>
					</c:choose>
					</td>
				</c:if>
			</c:forEach>
			<c:choose>
			<c:when test="${data.RCT_NOT_CNT > 0 and dataLevel != 'NOTSYNC'}">
			<td> <input type="checkbox" value = "${data.MNT_TIME}" name="seq" /> </td>
			</c:when>
			<c:otherwise>
			<td>  </td>
			</c:otherwise>
			</c:choose>
		</tr>
		<c:if test="${minVal % 10 == 0 && cycleVal == '10'}">
		<tr style="height:10px;"><td style="border-right:none; border-left:none; border-top:none; border-bottom:none;">&nbsp;</td></tr>
		</c:if>
	</c:forEach>
	</tbody>
</table>
</form>
<!-- <div style="position:fixed; display:flex; flex-direction:column; align-items:center; justify-content:center; margin: 0 5px;"> -->
	<button type="button" class="BlackBtn" onclick="checkkViewYn()">
		CHECK EXECUTE
		<svg width="18" height="18"><g transform="translate(-1422.5 -684.591)"><circle cx="9" cy="9" r="9" transform="translate(1422.5 684.591)"/><g fill="none" stroke="#fff" stroke-linecap="round" stroke-width="2"><path d="m1427.525 689.615 7.92 7.92M1435.445 689.615l-7.92 7.92"/></g></g></svg>
	</button>
<!-- </div> -->
</body>
</html>