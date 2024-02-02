<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<style>
#adminList th{
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
$(document).ready(function() {
	// 전체선택 체크시 비활성화 체크박스제외 활성화 처리
	$('input[name=selectall]').click(function(){
		var checked = $('input[name=selectall]').is(':checked');
		if(checked) {
			$('input[name=seq]:checkbox:not(:disabled)').prop('checked',true);
		} else {
			$('input[name=seq]:checkbox:not(:disabled)').prop('checked',false);
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
	
	fn_select();
	fn_exceptList();
});

function fn_select() {
	var data = {};
	data.searchCol = $("#searchCol").val();
	data.searchText = $("#searchText").val();
	data.searchCol2 = $("#searchCol2").val();
	data.searchText2 = $("#searchText2").val();
	data.searchCol3 = $("#searchCol3").val();
	data.searchText3 = $("#searchText3").val();
	
    $.ajax({
       	url : '/nmsc/adminList',
       	type : "post",
		data : JSON.stringify(data),
		contentType: 'application/json',
		dataType : 'json',
       	success : function(result) {
          	try{
	          	tbody = $("#adminList tbody");
	      		tbody.children().remove();
				if(result.selMetadbList.length > 0){
					var len = result.selMetadbList.length;
					var num = 0;
					$(result.selMetadbList).each(function(index, item){
						var html = '';
						var hangNum = len-num;
			            html += '<tr>';
			            html += '<td>'+hangNum+'</td>';
			            html += '<td style="word-break:break-all" id="tSATELLITE">'+item["SATELLITE"]+'</td>';
			            html += '<td style="word-break:break-all" id="tSENSOR">'+item["SENSOR"]+'</td>';
			            html += '<td style="word-break:break-all" id="tDATA_LVL">'+item["DATA_LVL"]+'</td>';
			            html += '<td style="word-break:break-all" id="tDATA_TYPE">'+item["DATA_TYPE"]+'</td>';
			            html += '<td style="word-break:break-all" id="tDATA_FORMAT">'+item["DATA_FORMAT"]+'</td>';
			            html += '<td style="word-break:break-all" id="tDATA_AREA">'+item["DATA_AREA"]+'</td>';
			            html += '<td style="word-break:break-all" id="tDATA_RES">'+item["DATA_RES"]+'</td>';
			            html += '<td style="word-break:break-all" id="tDATA_PROJ">'+item["DATA_PROJ"]+'</td>';
			            html += '<td style="word-break:break-all" id="tFILE_PATH" style="text-align:left;"><font size="1px">'+item["FILE_PATH"]+'</font></td>';
			            html += '<td style="word-break:break-all" id="tFILE_PTN" style="text-align:left;"><font size="1px">'+item["FILE_PTN"]+'</font></td>';
			            html += '<td style="word-break:break-all" id="tPRODUCT_DETAIL_SQ">'+item["PRODUCT_DETAIL_SQ"]+'</td>';
			            html += '<td style="word-break:break-all" id="tPRODUCT_CYCLE"><input type="text" style="width:90px;" id="pc'+item["PRODUCT_DETAIL_SQ"]+'" value = "'+item["PRODUCT_CYCLE"]+'"/></td>';
			            html += '<td style="word-break:break-all" id="tPRODUCT_STD_TIME"><input type="text" style="width:90px;" id="pst'+item["PRODUCT_DETAIL_SQ"]+'" value = "'+item["PRODUCT_STD_TIME"]+'"/></td>';
			            html += '<td style="word-break:break-all" id="tMONITOR_YN"><input type="text" style="width:90px;" id="my'+item["PRODUCT_DETAIL_SQ"]+'" value = "'+item["MONITOR_YN"]+'"/></td>';
			            html += '<td style="word-break:break-all" id="tPRODUCT_CYCLE_YN"><input type="text" style="width:90px;" id="pcy'+item["PRODUCT_DETAIL_SQ"]+'" value = "'+item["PRODUCT_CYCLE_YN"]+'"/></td>';
			            html += '<td> <input type="checkbox" value = "'+item["PRODUCT_DETAIL_SQ"]+'" name="seq" /> </td>';
			            html += '</tr>';
			            tbody.append(html);
			            num++;
	      		 	});
				} else {
					html = "";
					html += '<tr class="NoData">';
					html += '<th colspan="17">';
					html += 'No Data';
					html += '</th>';
					html += '</tr>';
					tbody.append(html);
				} 
				
          	}catch(e){
	            console.log("try catch error : "+e.message);
	        }finally{
	        	 console.log("finally");
	        }
       },
       error:function(request, status, error){
	  		console.log("error:"+error);
	   }
  });
}

function fn_search(){
	if($("#searchCol").val() == '' && $("#searchText").val() == ''){
		alert("검색값을 입력해주세요.");
		return;
	}else{
		if(($("#searchCol2").val() == '' && $("#searchText2").val() != '') || ($("#searchCol2").val() != '' && $("#searchText2").val() == '')){
			alert("검색값2을 입력해주세요.");
			return;
		}
		if(($("#searchCol3").val() == '' && $("#searchText3").val() != '') || ($("#searchCol3").val() != '' && $("#searchText3").val() == '')){
			alert("검색값2을 입력해주세요.");
			return;
		}
	}
	fn_select();
	setTimeout(function() {
		fn_metadbChkList();
		console.log("fn_metadbChkList exec");
	}, 1000);
}
//멀티 업데이트
function fn_update() {
	if($("input[name=seq]").is(":checked") == false) {
		alert("체크된 파일이 없습니다.");
		return false;
	} else {
		
		var checkboxValues = [];
		$("input[name=seq]:checked").each(function(i){
			checkboxValues.push($(this).val()+","+$("#pc"+$(this).val()).val()+","+$("#pst"+$(this).val()).val()+","+$("#my"+$(this).val()).val()+","+$("#pcy"+$(this).val()).val());
		});
		var allData = {"checkArray":checkboxValues, "checkSize":$("input[name=seq]:checked").length};
		$.ajax({
			url:'/nmsc/metadbUpdate',
			type:'POST',
			data: allData,
			success:function(data){
				alert(data.updateCnt+"건 처리완료");
				fn_select();
				fn_exceptList();
			},
			error:function(request, status, error){
		  		console.log("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
		    }
		});
		
	}
}


var max5=5;//최대추가갯수
function add_table(){
	var is_number=$('#exceptTb >tbody tr').length; 
	if(is_number < max5){
		$('#exceptTb > tbody:last').append("<tr><td><select name='startHour' id='startHour"+is_number+"' style='height:30px; width:100px;'><c:forEach var='hour' begin='0' end='23'><option value='<c:if test='${hour < 10}'>0</c:if>${hour}'><c:if test='${hour < 10}'>0</c:if>${hour}</c:forEach></select> 시</td><td><select name='startMin' style='height:30px; width:100px;'><c:forEach var='min' begin='0' end='59'><option value='<c:if test='${min < 10}'>0</c:if>${min}'><c:if test='${min < 10}'>0</c:if>${min}</c:forEach></select> 분</td><td><select name='endHour' style='height:30px; width:100px;'><c:forEach var='hour' begin='0' end='23'><option value='<c:if test='${hour < 10}'>0</c:if>${hour}'><c:if test='${hour < 10}'>0</c:if>${hour}</c:forEach></select> 시</td><td><select name='endMin' style='height:30px; width:100px;'><c:forEach var='min' begin='0' end='59'><option value='<c:if test='${min < 10}'>0</c:if>${min}'><c:if test='${min < 10}'>0</c:if>${min}</c:forEach></select> 분<input type='checkbox' name='exceptChk' /></td></tr>");
	    is_number=is_number+1;//총갯수+5	
	}
}
//exceptChk
function remove_table(){
	if($("input[name='exceptChk']:checked").length == 0){
		alert("삭제할 항목을 선택해 주세요.");
		return;
	}
	
	$("input[name='exceptChk']:checked").each(function(k,kVal){
		let a = kVal.parentElement.parentElement;
		$(a).remove();
	});
}

function fn_exceptList() {
	var data = {};
	data.stDate = "20231110";
	data.edDate = "20231115";
    $.ajax({
       	url : '/nmsc/exceptList',
       	type : "post",
		data : JSON.stringify(data),
		contentType: 'application/json',
		dataType : 'json',
       	success : function(result) {
          	try{
	          	tbody = $("#exceptTb tbody");
	      		tbody.children().remove();
				if(result.exceptList.length > 0){
					$(result.exceptList).each(function(index, item){
						console.log("index : "+index);
						var html = "";
			            html += "<tr>";
			            html += "<td><select name='startHour' id='startHour"+index+"' style='height:30px; width:100px;'><c:forEach var='hour' begin='0' end='23'><option value='<c:if test='${hour < 10}'>0</c:if>${hour}'><c:if test='${hour < 10}'>0</c:if>${hour}</c:forEach></select> 시</td>";
			            html += "<td><select name='startMin' id='startMin"+index+"' style='height:30px; width:100px;'><c:forEach var='min' begin='0' end='59'><option value='<c:if test='${min < 10}'>0</c:if>${min}'><c:if test='${min < 10}'>0</c:if>${min}</c:forEach></select> 분</td>";
			            html += "<td><select name='endHour' id='endHour"+index+"' style='height:30px; width:100px;'><c:forEach var='hour' begin='0' end='23'><option value='<c:if test='${hour < 10}'>0</c:if>${hour}'><c:if test='${hour < 10}'>0</c:if>${hour}</c:forEach></select> 시</td>";
			            html += "<td><select name='endMin' id='endMin"+index+"' style='height:30px; width:100px;'><c:forEach var='min' begin='0' end='59'><option value='<c:if test='${min < 10}'>0</c:if>${min}'><c:if test='${min < 10}'>0</c:if>${min}</c:forEach></select> 분<input type='checkbox' name='exceptChk' /></td>";
			            html += '</tr>';
			            tbody.append(html);
			            
			            $('#startHour'+index+' option[value='+item['START_HOUR']+']').prop("selected",true);
			            $('#startMin'+index+' option[value='+item['START_MIN']+']').prop("selected",true);
			            $('#endHour'+index+' option[value='+item['END_HOUR']+']').prop("selected",true);
			            $('#endMin'+index+' option[value='+item['END_MIN']+']').prop("selected",true);
	      		 	});
				}
				
          	}catch(e){
	            console.log("try catch error : "+e.message);
	        }finally{
	        	 console.log("finally");
	        }
       },
       error:function(request, status, error){
	  		console.log("error:"+error);
	   }
  });
}

function fn_except() {
	var shLen = $("select[name=startHour] option").length;
	if(shLen == 0){
		alert("최소1개이상은 있어야 합니다.");
		fn_exceptList();
		return;
	}
	var sh = [];
	$("select[name=startHour] option:selected").each(function(i, selected){
		sh.push($(this).val());
	});
	var sm = [];
	$("select[name=startMin] option:selected").each(function(i, selected){
		sm.push($(this).val());
	});
	var eh = [];
	$("select[name=endHour] option:selected").each(function(i, selected){
		eh.push($(this).val());
	});
	var em = [];
	$("select[name=endMin] option:selected").each(function(i, selected){
		em.push($(this).val());
	});
	var allData = {"shArray":sh,"smArray":sm,"ehArray":eh,"emArray":em};
	$.ajax({
		url:'/nmsc/exceptUpdate',
		type:'POST',
		data: allData,
		success:function(data){
			alert(data.updateCnt+"건 처리완료");
			fn_exceptList();
		},
		error:function(request, status, error){
	  		console.log("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
	    }
	});
}

function fn_metadbChk(mdId, mdVal){
	var checked = $("#"+mdId).is(':checked');
	if(checked){
		$("#adminList th:nth-child("+mdVal+")").css("display","");
		$("#adminList td:nth-child("+mdVal+")").css("display","");
	}else{
		$("#adminList th:nth-child("+mdVal+")").css("display","none");
		$("#adminList td:nth-child("+mdVal+")").css("display","none");
	}
}

function fn_metadbChkList(){
	console.log('fn_metadbChkList');
	$("input:checkbox[name='metadbChk']").each(function(i){
		if($(this).is(":checked")==true){
			console.log('checked : '+$(this).val());
			$("#adminList th:nth-child("+$(this).val()+")").css("display","");
			$("#adminList td:nth-child("+$(this).val()+")").css("display","");
		}else{
			console.log('not checked : '+$(this).val());
			$("#adminList th:nth-child("+$(this).val()+")").css("display","none");
			$("#adminList td:nth-child("+$(this).val()+")").css("display","none");
		}
		
	});
}

</script>
</head>
<body>
<div>
<div>
	<table style="width:100%; background-color:white; margin: auto; text-align: center; border-bottom:1px solid black;">
		<tr>
			<td width="20%"></td>
			<td width="60%" style="color:white;"><a aria-label="home" href="/nmsc/itemListView"><H1>위성산출물 메타데이터 조회 시스템 - ADMIN MANAGE</H1></a></td>
			<td width="20%">
			</td>
		</tr>
	</table>
</div>
</div>
<div class="item-wrap">


	<div>
		<span style="display:inline-block; margin:10px; padding:5px; color:red; font-weight:bold; background:white;">GK2A 체크제외</span> &nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;
		<input style="height:30px; width:100px;" type="button" id="plust" value="+" onclick="add_table();" style="cursor:pointer"/>
		&nbsp;&nbsp;
		<input style="height:30px; width:100px;" type="button" id="minus" value="-" onclick="remove_table();" style="cursor:pointer"/>
		&nbsp;&nbsp;
		<input style="height:30px; width:100px;" type="button" value="변 경"  onclick="fn_except()"/>
	</div>
	<div>
		<span style="display:inline-block; margin:10px; padding:5px; color:blue; font-weight:bold; background:white;">※ 변경사항은 다음 시간대(UTC기준)부터 적용됩니다.</span>
	</div>
	<form id="excepForm">
	<div>
		<table id="exceptTb">
		<thead>
			<tr>
			<th colspan="2">start time</th> 
			<th colspan="2">end time</th> 
			</tr>
		</thead>
		<tbody>
		
		</tbody>
		</table>
	</div>
	</form>
	<br/><br/>
	<div>
		<span style="display:inline-block; margin:10px; padding:5px; color:red; font-weight:bold; background:white;">메타데이터 필드 항목 제어</span> &nbsp;&nbsp;&nbsp;&nbsp;
		<input style="height:20px; width:20px;" type="checkbox" name="metadbChk" id="SATELLITE" onclick="fn_metadbChk(this.id, 2)" value="2" checked/>SATELLITE
		<input style="height:20px; width:20px;" type="checkbox" name="metadbChk" id="SENSOR" onclick="fn_metadbChk(this.id, 3)" value="3" checked/>SENSOR
		<input style="height:20px; width:20px;" type="checkbox" name="metadbChk" id="DATA_LVL" onclick="fn_metadbChk(this.id, 4)" value="4" checked/>DATA_LVL
		<input style="height:20px; width:20px;" type="checkbox" name="metadbChk" id="DATA_TYPE" onclick="fn_metadbChk(this.id, 5)" value="5" checked/>DATA_TYPE
		<input style="height:20px; width:20px;" type="checkbox" name="metadbChk" id="DATA_FORMAT" onclick="fn_metadbChk(this.id, 6)" value="6" checked/>DATA_FORMAT
		<input style="height:20px; width:20px;" type="checkbox" name="metadbChk" id="DATA_AREA" onclick="fn_metadbChk(this.id, 7)" value="7" checked/>DATA_AREA
		<input style="height:20px; width:20px;" type="checkbox" name="metadbChk" id="DATA_RES" onclick="fn_metadbChk(this.id, 8)" value="8" checked/>DATA_RES
		<input style="height:20px; width:20px;" type="checkbox" name="metadbChk" id="DATA_PROJ" onclick="fn_metadbChk(this.id, 9)" value="9" checked/>DATA_PROJ
		<input style="height:20px; width:20px;" type="checkbox" name="metadbChk" id="FILE_PATH" onclick="fn_metadbChk(this.id, 10)" value="10" checked/>FILE_PATH
		<input style="height:20px; width:20px;" type="checkbox" name="metadbChk" id="FILE_PTN" onclick="fn_metadbChk(this.id, 11)" value="11" checked/>FILE_PTN
	</div>
	<br/>
	<form name="selectForm" id="selectForm" action="">
		<select style="height:30px; width:200px;" name="searchCol" id="searchCol">
			<option value="" >선택</option>
			<option value="SATELLITE" <c:if test="${paramMap.searchCol == 'SATELLITE'}">selected="selected"</c:if>>SATELLITE</option>
			<option value="SENSOR" <c:if test="${paramMap.searchCol == 'SENSOR'}">selected="selected"</c:if>>SENSOR</option>
			<option value="DATA_LVL" <c:if test="${paramMap.searchCol == 'DATA_LVL'}">selected="selected"</c:if>>DATA_LVL</option>
			<option value="DATA_TYPE" <c:if test="${paramMap.searchCol == 'DATA_TYPE'}">selected="selected"</c:if>>DATA_TYPE</option>
			<option value="DATA_FORMAT" <c:if test="${paramMap.searchCol == 'DATA_FORMAT'}">selected="selected"</c:if>>DATA_FORMAT</option>
			<option value="DATA_AREA" <c:if test="${paramMap.searchCol == 'DATA_AREA'}">selected="selected"</c:if>>DATA_AREA</option>
			<option value="DATA_RES" <c:if test="${paramMap.searchCol == 'DATA_RES'}">selected="selected"</c:if>>DATA_RES</option>
			<option value="DATA_PROJ" <c:if test="${paramMap.searchCol == 'DATA_PROJ'}">selected="selected"</c:if>>DATA_PROJ</option>
			<option value="FILE_PATH" <c:if test="${paramMap.searchCol == 'FILE_PATH'}">selected="selected"</c:if>>FILE_PATH</option>
			<option value="FILE_PTN" <c:if test="${paramMap.searchCol == 'FILE_PTN'}">selected="selected"</c:if>>FILE_PTN</option>
			<option value="PRODUCT_DETAIL_SQ" <c:if test="${paramMap.searchCol == 'PRODUCT_DETAIL_SQ'}">selected="selected"</c:if>>PRODUCT_DETAIL_SQ</option>
			<option value="PRODUCT_CYCLE" <c:if test="${paramMap.searchCol == 'PRODUCT_CYCLE'}">selected="selected"</c:if>>PRODUCT_CYCLE</option>
			<option value="PRODUCT_STD_TIME" <c:if test="${paramMap.searchCol == 'PRODUCT_STD_TIME'}">selected="selected"</c:if>>PRODUCT_STD_TIME</option>
			<option value="MONITOR_YN" <c:if test="${paramMap.searchCol == 'MONITOR_YN'}">selected="selected"</c:if>>MONITOR_YN</option>
			<option value="PRODUCT_CYCLE_YN" <c:if test="${paramMap.searchCol == 'PRODUCT_CYCLE_YN'}">selected="selected"</c:if>>PRODUCT_CYCLE_YN</option>
		</select>
		<input style="height:23px; width:300px;" type="text" name="searchText" id="searchText" style="width:350px;" value="${paramMap.searchText }" />
		<br/>
		<select style="height:30px; width:200px;" name="searchCol2" id="searchCol2">
			<option value="" >선택</option>
			<option value="SATELLITE" <c:if test="${paramMap.searchCol2 == 'SATELLITE'}">selected="selected"</c:if>>SATELLITE</option>
			<option value="SENSOR" <c:if test="${paramMap.searchCol2 == 'SENSOR'}">selected="selected"</c:if>>SENSOR</option>
			<option value="DATA_LVL" <c:if test="${paramMap.searchCol2 == 'DATA_LVL'}">selected="selected"</c:if>>DATA_LVL</option>
			<option value="DATA_TYPE" <c:if test="${paramMap.searchCol2 == 'DATA_TYPE'}">selected="selected"</c:if>>DATA_TYPE</option>
			<option value="DATA_FORMAT" <c:if test="${paramMap.searchCol2 == 'DATA_FORMAT'}">selected="selected"</c:if>>DATA_FORMAT</option>
			<option value="DATA_AREA" <c:if test="${paramMap.searchCol2 == 'DATA_AREA'}">selected="selected"</c:if>>DATA_AREA</option>
			<option value="DATA_RES" <c:if test="${paramMap.searchCol2 == 'DATA_RES'}">selected="selected"</c:if>>DATA_RES</option>
			<option value="DATA_PROJ" <c:if test="${paramMap.searchCol2 == 'DATA_PROJ'}">selected="selected"</c:if>>DATA_PROJ</option>
			<option value="FILE_PATH" <c:if test="${paramMap.searchCol2 == 'FILE_PATH'}">selected="selected"</c:if>>FILE_PATH</option>
			<option value="FILE_PTN" <c:if test="${paramMap.searchCol2 == 'FILE_PTN'}">selected="selected"</c:if>>FILE_PTN</option>
			<option value="PRODUCT_DETAIL_SQ" <c:if test="${paramMap.searchCol2 == 'PRODUCT_DETAIL_SQ'}">selected="selected"</c:if>>PRODUCT_DETAIL_SQ</option>
			<option value="PRODUCT_CYCLE" <c:if test="${paramMap.searchCol2 == 'PRODUCT_CYCLE'}">selected="selected"</c:if>>PRODUCT_CYCLE</option>
			<option value="PRODUCT_STD_TIME" <c:if test="${paramMap.searchCol2 == 'PRODUCT_STD_TIME'}">selected="selected"</c:if>>PRODUCT_STD_TIME</option>
			<option value="MONITOR_YN" <c:if test="${paramMap.searchCol2 == 'MONITOR_YN'}">selected="selected"</c:if>>MONITOR_YN</option>
			<option value="PRODUCT_CYCLE_YN" <c:if test="${paramMap.searchCol2 == 'PRODUCT_CYCLE_YN'}">selected="selected"</c:if>>PRODUCT_CYCLE_YN</option>
		</select>
		<input style="height:23px; width:300px;" type="text" name="searchText2" id="searchText2" style="width:350px;" value="${paramMap.searchText }" />
		<br/>
		<select style="height:30px; width:200px;" name="searchCol3" id="searchCol3">
			<option value="" >선택</option>
			<option value="SATELLITE" <c:if test="${paramMap.searchCol3 == 'SATELLITE'}">selected="selected"</c:if>>SATELLITE</option>
			<option value="SENSOR" <c:if test="${paramMap.searchCol3 == 'SENSOR'}">selected="selected"</c:if>>SENSOR</option>
			<option value="DATA_LVL" <c:if test="${paramMap.searchCol3 == 'DATA_LVL'}">selected="selected"</c:if>>DATA_LVL</option>
			<option value="DATA_TYPE" <c:if test="${paramMap.searchCol3 == 'DATA_TYPE'}">selected="selected"</c:if>>DATA_TYPE</option>
			<option value="DATA_FORMAT" <c:if test="${paramMap.searchCol3 == 'DATA_FORMAT'}">selected="selected"</c:if>>DATA_FORMAT</option>
			<option value="DATA_AREA" <c:if test="${paramMap.searchCol3 == 'DATA_AREA'}">selected="selected"</c:if>>DATA_AREA</option>
			<option value="DATA_RES" <c:if test="${paramMap.searchCol3 == 'DATA_RES'}">selected="selected"</c:if>>DATA_RES</option>
			<option value="DATA_PROJ" <c:if test="${paramMap.searchCol3 == 'DATA_PROJ'}">selected="selected"</c:if>>DATA_PROJ</option>
			<option value="FILE_PATH" <c:if test="${paramMap.searchCol3 == 'FILE_PATH'}">selected="selected"</c:if>>FILE_PATH</option>
			<option value="FILE_PTN" <c:if test="${paramMap.searchCol3 == 'FILE_PTN'}">selected="selected"</c:if>>FILE_PTN</option>
			<option value="PRODUCT_DETAIL_SQ" <c:if test="${paramMap.searchCol3 == 'PRODUCT_DETAIL_SQ'}">selected="selected"</c:if>>PRODUCT_DETAIL_SQ</option>
			<option value="PRODUCT_CYCLE" <c:if test="${paramMap.searchCol3 == 'PRODUCT_CYCLE'}">selected="selected"</c:if>>PRODUCT_CYCLE</option>
			<option value="PRODUCT_STD_TIME" <c:if test="${paramMap.searchCol3 == 'PRODUCT_STD_TIME'}">selected="selected"</c:if>>PRODUCT_STD_TIME</option>
			<option value="MONITOR_YN" <c:if test="${paramMap.searchCol3 == 'MONITOR_YN'}">selected="selected"</c:if>>MONITOR_YN</option>
			<option value="PRODUCT_CYCLE_YN" <c:if test="${paramMap.searchCol3 == 'PRODUCT_CYCLE_YN'}">selected="selected"</c:if>>PRODUCT_CYCLE_YN</option>
		</select>
		<input style="height:23px; width:300px;" type="text" name="searchText3" id="searchText3" style="width:350px;" value="${paramMap.searchText }" />
		
		<input style="height:30px; width:100px;" type="button" value="조 회"  onclick="fn_search()"/>
	</form>
	<div style="float:right">
		<input style="height:30px; width:100px;" type="button" value="체크 업데이트"  onclick="fn_update()"/>
	</div>
</div>
<br/>
<div>
<form id="tableForm" name="tableForm">
<table id ="adminList" style="width:100%;text-align: center; border-collapse: separate; border-spacing: 5px 5px; font-size:17px; table-layout:fixed;" border=1>
	<thead>
	<tr>
		<th width="50px">NUM</th>
		<th width="100px" style="word-break:break-all" id="hSATELLITE" style="width:6%">SATELLITE</th>
		<th width="100px" style="word-break:break-all" id="hSENSOR" style="width:6%">SENSOR</th>
		<th width="100px" style="word-break:break-all" id="hDATA_LVL" style="width:6%">DATA_LVL</th>
		<th width="100px" style="word-break:break-all" id="hDATA_TYPE" style="width:6%">DATA_TYPE</th>
		<th width="100px" style="word-break:break-all" id="hDATA_FORMAT" style="width:6%">DATA_FORMAT</th>
		<th width="100px" style="word-break:break-all" id="hDATA_AREA" style="width:6%">DATA_AREA</th>
		<th width="100px" style="word-break:break-all" id="hDATA_RES" style="width:6%">DATA_RES</th>
		<th width="100px" style="word-break:break-all" id="hDATA_PROJ" style="width:6%">DATA_PROJ</th>
		<th width="100px" style="word-break:break-all" id="hFILE_PATH" style="width:15%">FILE_PATH</th>
		<th width="100px" style="word-break:break-all" id="hFILE_PTN" style="width:10%">FILE_PTN</th>
		<th width="100px" style="word-break:break-all" id="hPRODUCT_DETAIL_SQ" style="width:6%">PRODUCT_DETAIL_SQ</th>
		<th width="100px" style="word-break:break-all" id="hPRODUCT_CYCLE" style="width:6%">PRODUCT_CYCLE</th>
		<th width="100px" style="word-break:break-all" id="hPRODUCT_STD_TIME" style="width:6%">PRODUCT_STD_TIME</th>
		<th width="100px" style="word-break:break-all" id="hMONITOR_YN" style="width:6%">MONITOR_YN</th>
		<th width="100px" style="word-break:break-all" id="hPRODUCT_CYCLE_YN" style="width:6%">PRODUCT_CYCLE_YN</th>
		<th width="50px"><input type="checkbox" name="selectall" value="AllSelect" /></th>
	</tr>
	</thead>
	<tbody>
	
	</tbody>
</table>
</form>
</div>


</body>
</html>