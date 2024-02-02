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


<style type="text/css">
      
.container{
	display: flex;
    border-bottom: 1px solid #ccc;
}

ul.tabs{
	padding: 10px 20px;
      cursor: pointer;
      border: 1px solid #ccc;
      border-bottom: none;
      background-color: #f1f1f1;
}
ul.tabs li{
	background: none;
	color: #222;
	display: inline-block;
	padding: 10px 15px;
	cursor: pointer;
}

ul.tabs li.current{
	background-color: white;
      border-bottom: 1px solid white;
}

.tab-content{
	display: none;
}

.tab-content.current{
	display: inherit;
}

#loading{
	height:100%;
	left:0px;
	position:fixed;
	top:0px;
	width:100%;
	filter:alpha(opacity=50);
	-moz-opacity:0.5;
	opacity:0.5;
}
.loading{
	background-color:gray;
	z-index:999999;
}
#loading_img{
	position:absolute;
	top:50%;
	left:50%;
	height:200px;
	margin-top:-75px;
	margin-left:-75px;
	z-index:999999;
}

</style>
<script>
$(document).ready(function() {
	
	//로딩바 생성
	loading = $('<div id="loading" class="loading"></div><img id="loading_img" alt="loading" src="/resources/images/grim.png" />').appendTo(document.body).hide();
	
	$('ul.tabs li').click(function(){
        var tab_id = $(this).attr('data-tab');
        $('ul.tabs li').removeClass('current');
        $('.tab-content').removeClass('current');

        $(this).addClass('current');
        $("#"+tab_id).addClass('current');
		
    });
	
	
	
	
	gk2aAList(1);
	console.log("gk2aAList");
	
	setTimeout(function() {
		gk2aBList(1);
		console.log("gk2aBList");
		}, 500);
	
	setTimeout(function() {
		gk2aCList(1);
		console.log("gk2aCList");
		}, 1000);
	setTimeout(function() {
		otherGroup();
		console.log("otherList");
		}, 1500);
	setTimeout(function() {
		collectGroup();
		console.log("collectList");
		}, 2000);
	setTimeout(function() {
		swfcGroup();
		console.log("swfcList");
		}, 2500);
	
	
});
function fn_refresh(){
	gk2aAList(1);
	console.log("gk2aAList");
	
	setTimeout(function() {
		gk2aBList(1);
		console.log("gk2aBList");
		}, 500);
	
	setTimeout(function() {
		gk2aCList(1);
		console.log("gk2aCList");
		}, 1000);
	setTimeout(function() {
		otherGroup();
		console.log("otherList");
		}, 1500);
	setTimeout(function() {
		collectGroup();
		console.log("collectList");
		}, 2000);
	setTimeout(function() {
		swfcGroup();
		console.log("swfcList");
		}, 2500);
}

function gk2aAList(pageNum) {
	var data = {};
	data.page = pageNum;
	
    $.ajax({
       	url : '/nmsc/notRctAList',
       	type : "post",
		data : JSON.stringify(data),
		contentType: 'application/json',
		dataType : 'json',
       	success : function(result) {
          	try{
	          	tbody = $("#gk2aAList tbody");
	      		tbody.children().remove();
				if(result.gk2aAList.length > 0){
		          	var a = '';
		          	var page = result.page;
		          	var startpage = result.startpage;
		          	var endpage = result.endpage;
					$(result.gk2aAList).each(function(index, item){
						var html = '';
			            html += '<tr>';
			            html += '<td>'+item["INSERT_TIME"]+'</td>';
			            html += '<td><font size="1px">'+item["FILE_PTN"]+'</font></td>';
			            html += '<td>'+item["DATA_AREA"]+'</td>';
			            html += '<td>'+item["DELAY_TIME"]+'</td>';
			            html += '</tr>';
			            tbody.append(html);
	      		 	});
					for (var num=startpage; num<=endpage; num++) {
		            	if (num == page) {
		                	a +=  num ;
		             	} else {
		                	a += '<a href="#" onclick="gk2aAList('+num+'); return false;" class="page-btn">' + num + '</a>';
		             	}
		            	a += '&nbsp;&nbsp;';
		          	}
		        	$('.AListPaging').html(a);
		        	var gg = '';
		        	var ymd = '';
		        	$(result.gk2aATimeGroup).each(function(index, item){
		        		if(ymd != item["MNT_IYMD"]){
		        			gg += "<br/>"+item["MNT_IYMD"]+"<br/>";
		        		}
		        		ymd = item["MNT_IYMD"];
		        		gg += item["MNT_TIME"]+"("+item["GROUP_CNT"]+")"+'&nbsp;,';
		        	});
		        	gg = gg.substr(0,gg.length-1);
		        	$('.ATimeGroup').html(gg);
				} else {
					html = "";
					html += '<tr class="NoData">';
					html += '<th colspan="4">';
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

function gk2aBList(pageNum) {
	var data = {};
	data.page = pageNum;
	
    $.ajax({
       	url : '/nmsc/notRctBList',
       	type : "post",
		data : JSON.stringify(data),
		contentType: 'application/json',
		dataType : 'json',
       	success : function(result) {
          	try{
	          	tbody = $("#gk2aBList tbody");
	      		tbody.children().remove();
				if(result.gk2aBList.length > 0){
		          	var a = '';
		          	var page = result.page;
		          	var startpage = result.startpage;
		          	var endpage = result.endpage;
					$(result.gk2aBList).each(function(index, item){
						var html = '';
			            html += '<tr>';
			            html += '<td>'+item["INSERT_TIME"]+'</td>';
			            html += '<td><font size="1px">'+item["FILE_PTN"]+'</font></td>';
			            html += '<td>'+item["DATA_AREA"]+'</td>';
			            html += '<td>'+item["DELAY_TIME"]+'</td>';
			            html += '</tr>';
			            tbody.append(html);
	      		 	});
					for (var num=startpage; num<=endpage; num++) {
		            	if (num == page) {
		                	a +=  num ;
		             	} else {
		                	a += '<a href="#" onclick="gk2aBList('+num+'); return false;" class="page-btn">' + num + '</a>';
		             	}
		            	a += '&nbsp;&nbsp;';
		          	}
		        	$('.BListPaging').html(a);
	        		var gg = '';
	        		var ymd = '';
		        	$(result.gk2aBTimeGroup).each(function(index, item){
		        		if(ymd != item["MNT_IYMD"]){
		        			gg += "<br/>"+item["MNT_IYMD"]+"<br/>";
		        		}
		        		ymd = item["MNT_IYMD"];
		        		gg += item["MNT_TIME"]+"("+item["GROUP_CNT"]+")"+'&nbsp;,';
		        	});
		        	gg = gg.substr(0,gg.length-1);
		        	$('.BTimeGroup').html(gg);
				} else {
					html = "";
					html += '<tr class="NoData">';
					html += '<th colspan="4">';
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

function gk2aCList(pageNum) {
	var data = {};
	data.page = pageNum;
	
    $.ajax({
       	url : '/nmsc/notRctCList',
       	type : "post",
		data : JSON.stringify(data),
		contentType: 'application/json',
		dataType : 'json',
       	success : function(result) {
          	try{
	          	tbody = $("#gk2aCList tbody");
	      		tbody.children().remove();
				if(result.gk2aCList.length > 0){
		          	var a = '';
		          	var page = result.page;
		          	var startpage = result.startpage;
		          	var endpage = result.endpage;
					$(result.gk2aCList).each(function(index, item){
						var html = '';
			            html += '<tr>';
			            html += '<td>'+item["INSERT_TIME"]+'</td>';
			            html += '<td><font size="1px">'+item["FILE_PTN"]+'</font></td>';
			            html += '<td>'+item["DATA_AREA"]+'</td>';
			            html += '<td>'+item["DELAY_TIME"]+'</td>';
			            html += '</tr>';
			            tbody.append(html);
	      		 	});
					for (var num=startpage; num<=endpage; num++) {
		            	if (num == page) {
		                	a +=  num ;
		             	} else {
		                	a += '<a href="#" onclick="gk2aCList('+num+'); return false;" class="page-btn">' + num + '</a>';
		             	}
		            	a += '&nbsp;&nbsp;';
		          	}
		        	$('.CListPaging').html(a);
		        	var gg = '';
		        	var ymd = '';
		        	$(result.gk2aCTimeGroup).each(function(index, item){
		        		if(ymd != item["MNT_IYMD"]){
		        			gg += "<br/>"+item["MNT_IYMD"]+"<br/>";
		        		}
		        		ymd = item["MNT_IYMD"];
		        		gg += item["MNT_TIME"]+"("+item["GROUP_CNT"]+")"+'&nbsp;,';
		        	});
		        	gg = gg.substr(0,gg.length-1);
		        	$('.CTimeGroup').html(gg);
				} else {
					html = "";
					html += '<tr class="NoData">';
					html += '<th colspan="4">';
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

function otherGroup() {
	var data = {};
	data.page = 1;
    $.ajax({
       	url : '/nmsc/notRctOtherGroup',
       	type : "post",
		data : JSON.stringify(data),
		contentType: 'application/json',
		dataType : 'json',
       	success : function(result) {
          	try{
          		var div = $("#tab-2 .parent");
          		div.children().remove();
				if(result.otherGroup.length > 0){
		          	var time = 1000;
					$(result.otherGroup).each(function(index, item){
						var that = this;
						setTimeout(function() {
							otherList('1',item["SATELLITE"],item["SYNC_YN"]);
						}, time*index);
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

function otherList(pageNum,satellite,syncYn) {
	var data = {};
	data.page = pageNum;
	data.satellite = satellite;
	
    $.ajax({
       	url : '/nmsc/notRctOtherList',
       	type : "post",
		data : JSON.stringify(data),
		contentType: 'application/json',
		dataType : 'json',
       	success : function(result) {
          	try{
          		var group = result.satellite;
          		var div = $("#tab-2 .parent");
          		var child = '<div class="child"><table id ="other'+group+'" border="1" style="width:100%; text-align: center;">';
          		child += '<caption>'+group+'</caption>';
          		if(syncYn == 'N'){
	          		child += '<thead><tr><th>입력시간</th><th>자료명</th><th>영역</th><th>타입</th><th>미표출시간</th></tr></thead><tbody></tbody></table><div id="otherListPaging'+group+'" style="text-align: center;"></div><div id="otherTimeGroup'+group+'" style="text-align: left;"></div></div>';
          		}else{
	          		child += '<thead><tr><th>입력시간</th><th>자료명</th><th>영역</th><th>미표출시간</th></tr></thead><tbody></tbody></table><div id="otherListPaging'+group+'" style="text-align: center;"></div><div id="otherTimeGroup'+group+'" style="text-align: left;"></div></div>';
          		}
          		if($('#other'+group).length <= 0){
          			div.append(child);
          		}
	          	var tbody = $('#other'+group+' tbody');
	      		tbody.children().remove();
				if(result.otherList.length > 0){
		          	var a = '';
		          	var page = result.page;
		          	var startpage = result.startpage;
		          	var endpage = result.endpage;
					$(result.otherList).each(function(index, item){
						
						var html = '';
			            html += '<tr>';
			            html += '<td>'+item["INSERT_TIME"]+'</td>';
			            html += '<td><font size="1px">'+item["FILE_PTN"]+'</font></td>';
			            html += '<td>'+item["DATA_AREA"]+'</td>';
			            if(syncYn == 'N'){
			            	html += '<td>'+item["DATA_TYPE"]+'</td>';
			            }
			            html += '<td>'+item["DELAY_TIME"]+'</td>';
			            html += '</tr>';
			            tbody.append(html);
	      		 	});
					for (var num=startpage; num<=endpage; num++) {
						
		            	if (num == page) {
		                	a +=  num ;
		             	} else {
		                	a += '<a href="#" onclick="otherList('+num+',\''+satellite+'\',\''+syncYn+'\'); return false;" class="page-btn">' + num + '</a>';
		             	}
		            	a += '&nbsp;&nbsp;';
		          	}
					$('#otherListPaging'+group).html(a);
					var gg = "";
					$(result.otherTimeGroup).each(function(index, item){
						gg += item["GROUP_CNT"]+'&nbsp;,';
					});
					gg = gg.substr(0,gg.length-1);
					$('#otherTimeGroup'+group).html(gg);
				} else {
					html = "";
					html += '<tr class="NoData">';
					html += '<th colspan="4">';
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

function collectGroup() {
	var data = {};
	data.page = 1;
    $.ajax({
       	url : '/nmsc/notRctCollectGroup',
       	type : "post",
		data : JSON.stringify(data),
		contentType: 'application/json',
		dataType : 'json',
       	success : function(result) {
          	try{
				if(result.collectGroup.length > 0){
		          	
					$(result.collectGroup).each(function(index, item){
						setTimeout(function() {
							collectList('1',item["DATA_TYPE"]);
						}, 1000);
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

function collectList(pageNum, dataType) {
	console.log("###dataType : "+dataType);
	var data = {};
	data.page = pageNum;
	data.dataType = dataType;
	
    $.ajax({
       	url : '/nmsc/notRctCollectList',
       	type : "post",
		data : JSON.stringify(data),
		contentType: 'application/json',
		dataType : 'json',
       	success : function(result) {
          	try{
          		var group = result.dataType;
          		var div = $("#tab-3 .parent");
          		div.children().remove();
          		var child = '<div class="child"><table id ="collect'+group+'" border="1" style="width:100%; text-align: center;">';
          		child += '<caption>'+group+'</caption>';
          		child += '<thead><tr><th>입력시간</th><th>자료갯수</th><th>영역</th><th>미표출시간</th></tr></thead><tbody></tbody></table><div class="'+group+'CollectListPaging" style="text-align: center;"></div></div>';
          		div.append(child);
	          	var tbody = $('#collect'+group+' tbody');
	      		tbody.children().remove();
				if(result.collectList.length > 0){
		          	var a = '';
		          	var page = result.page;
		          	var startpage = result.startpage;
		          	var endpage = result.endpage;
					$(result.collectList).each(function(index, item){
						
						var html = '';
			            html += '<tr>';
			            html += '<td>'+item["INSERT_TIME"]+'</td>';
			            html += '<td>'+item["FILE_PTN"]+'</td>';
			            html += '<td>'+item["DATA_AREA"]+'</td>';
			            html += '<td>'+item["DELAY_TIME"]+'</td>';
			            html += '</tr>';
			            tbody.append(html);
	      		 	});
					for (var num=startpage; num<=endpage; num++) {
		            	if (num == page) {
		                	a +=  num ;
		             	} else {
		                	a += '<a href="#" onclick="collectList('+num+','+dataType+'); return false;" class="page-btn">' + num + '</a>';
		             	}
		            	a += '&nbsp;&nbsp;';
		          	}
		        	$('.'+group+'CollectListPaging').html(a);
				} else {
					html = "";
					html += '<tr class="NoData">';
					html += '<th colspan="4">';
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

function swfcGroup() {
	var data = {};
	data.page = 1;
    $.ajax({
       	url : '/nmsc/notRctSwfcGroup',
       	type : "post",
		data : JSON.stringify(data),
		contentType: 'application/json',
		dataType : 'json',
       	success : function(result) {
          	try{
				if(result.swfcGroup.length > 0){
		          	
					$(result.swfcGroup).each(function(index, item){
						setTimeout(function() {
							swfcList('1',item["DATA_TYPE"]);
						}, 1000);
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

function swfcList(pageNum, dataType) {
	console.log("###dataType : "+dataType);
	var data = {};
	data.page = pageNum;
	data.dataType = dataType;
	
    $.ajax({
       	url : '/nmsc/notRctSwfcList',
       	type : "post",
		data : JSON.stringify(data),
		contentType: 'application/json',
		dataType : 'json',
       	success : function(result) {
          	try{
          		var group = result.dataType;
          		var div = $("#tab-4 .parent");
          		div.children().remove();
          		var child = '<div class="child"><table id ="swfc'+group+'" border="1" style="width:100%; text-align: center;">';
          		child += '<caption>'+group+'</caption>';
          		child += '<thead><tr><th>입력시간</th><th>자료갯수</th><th>영역</th><th>미표출시간</th></tr></thead><tbody></tbody></table><div class="'+group+'SwfcListPaging" style="text-align: center;"></div></div>';
          		div.append(child);
	          	var tbody = $('#swfc'+group+' tbody');
	      		tbody.children().remove();
				if(result.swfcList.length > 0){
		          	var a = '';
		          	var page = result.page;
		          	var startpage = result.startpage;
		          	var endpage = result.endpage;
					$(result.swfcList).each(function(index, item){
						
						var html = '';
			            html += '<tr>';
			            html += '<td>'+item["INSERT_TIME"]+'</td>';
			            html += '<td>'+item["FILE_PTN"]+'</td>';
			            html += '<td>'+item["DATA_AREA"]+'</td>';
			            html += '<td>'+item["DELAY_TIME"]+'</td>';
			            html += '</tr>';
			            tbody.append(html);
	      		 	});
					for (var num=startpage; num<=endpage; num++) {
		            	if (num == page) {
		                	a +=  num ;
		             	} else {
		                	a += '<a href="#" onclick="swfcList('+num+','+dataType+'); return false;" class="page-btn">' + num + '</a>';
		             	}
		            	a += '&nbsp;&nbsp;';
		          	}
		        	$('.'+group+'SwfcListPaging').html(a);
				} else {
					html = "";
					html += '<tr class="NoData">';
					html += '<th colspan="4">';
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


//미표출 데이터 재스캔 업데이트
function fn_update(satellite,dataArea) {
	
	loading.show();
	
	var data = {};
	data.satellite = satellite;
	data.dataArea = dataArea;
	timer = setTimeout(function(){	//로딩바를 위해 1.5초 뒤 ajax실행
		$.ajax({
			url:'/nmsc/notRctUpdate',
			type:'POST',
			data : JSON.stringify(data),
			contentType: 'application/json',
			dataType : 'json',
			success:function(data){
				alert(data.updateCnt+"건 예약완료 \n최대1분후 처리됩니다. ");
				fn_refresh();
				loading.hide();
			},
			error:function(request, status, error){
		  		console.log("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
		  		loading.hide();
		    }
		});
	},1500);
		
}

//미표출 페이지 조회시간 변경
function fn_notRctSelTimeUpdate(){
	var data = {};
	data.notRctSelTime = $("#notRctSelTime").val();
	
	$.ajax({
		url:'/nmsc/notRctSelTimeUpdate',
		type:'POST',
		data : JSON.stringify(data),
		contentType: 'application/json',
		dataType : 'json',
		success:function(data){
			alert("미표출페이지 조회시간이 최근 "+data.time+"시간으로 변경되었습니다.");
			fn_refresh();
		},
		error:function(request, status, error){
	  		console.log("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
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
				<td width="20%"><button onclick="fn_refresh(); return false;" style="background-color:black; color:white; min-height:30px; min-width:200px;font-size:20px;">새로고침</button></td>
				<td width="60%" style="color:white;"><a aria-label="home" href="/nmsc/itemListView"><H1>위성산출물 통합 모니터링 시스템</H1></a></td>
				<td width="20%">
					<H3 id="kdate"></H3>
					<H3 id="udate"></H3>
				</td>
			</tr>
		</table>
	</div>
</div>
<div class="container">
    <ul class="tabs">
        <li class="tab-link current" data-tab="tab-1">GK2A</li>
        <li class="tab-link" data-tab="tab-2">외국/기타 위성</li>
        <li class="tab-link" data-tab="tab-3">수집데이터</li>
        <li class="tab-link" data-tab="tab-4">우주기상</li>
    </ul>
    
</div>
<div>
	<select name="notRctSelTime" id="notRctSelTime" style="height:30px; width:150px;">
		<option value="24" <c:if test="${notRctTime == '24'}">selected="selected"</c:if>>24</option>
		<option value="48" <c:if test="${notRctTime == '48'}">selected="selected"</c:if>>48</option>
	</select>
	<input style="height:30px; width:150px;" type="button" value="최근조회시간 변경"  onclick="fn_notRctSelTimeUpdate();"/>
</div>
<div id="tab-1" class="tab-content current">
	<div class="parent">
		<div class="child">
			<table id ="gk2aAList" border="1" style="width:100%; text-align: center;  ">
			<caption>천리안위성 FD,TP,EA,EXA</caption>
				<thead>
					<tr>
						<th>입력시간</th>
						<th>자료명</th>
						<th>영역</th>
						<th>미표출시간</th>
					</tr>
				</thead>
				<tbody>
				
				</tbody>
			</table>
			<div class="AListPaging" style="text-align: center;"></div>
			<div style="float:right">
				<input style="height:30px; width:150px;" type="button" value="미표출 다시 체크"  onclick="fn_update('GK2A','a')"/>
			</div>
			<div class="ATimeGroup" style="text-align: left;width: 100%;display: block;height: auto; word-break: break-all;"></div>
		</div>
		<div class="child">
			<table id ="gk2aBList" border="1"  style="width:100%;  text-align: center; ">
			<caption>천리안위성 ELA,KO.SKO,NKO</caption>
				<thead>
					<tr>
						<th>입력시간</th>
						<th>자료명</th>
						<th>영역</th>
						<th>미표출시간</th>
					</tr>
				</thead>
				<tbody>
				
				</tbody>
			</table>
			<div class="BListPaging" style="text-align: center;"></div>
			<div style="float:right">
				<input style="height:30px; width:150px;" type="button" value="미표출 다시 체크"  onclick="fn_update('GK2A','b')"/>
			</div>
			<div class="BTimeGroup" style="text-align: left;width: 100%;display: block;height: auto; word-break: break-all;"></div>
		</div>
		<div class="child">
			<table id ="gk2aCList" border="1"  style="width:100%;  text-align: center; ">
			<caption>천리안위성 LA</caption>
				<thead>
					<tr>
						<th>입력시간</th>
						<th>자료명</th>
						<th>영역</th>
						<th>미표출시간</th>
					</tr>
				</thead>
				<tbody>
				
				</tbody>
			</table>
			<div class="CListPaging" style="text-align: center;"></div>
			<div style="float:right">
				<input style="height:30px; width:150px;" type="button" value="미표출 다시 체크"  onclick="fn_update('GK2A','c')"/>
			</div>
			<div class="CTimeGroup" style="text-align: left;width: 100%;display: block;height: auto; word-break: break-all;"></div>
		</div>
	</div>
</div>

<div id="tab-2" class="tab-content">
	<div class="parent">
		
	</div>
	

</div>

<div id="tab-3" class="tab-content">
	<div class="parent">
		
	</div>
</div>
<div id="tab-4" class="tab-content">
	<div class="parent">
		
	</div>
</div>


</body>
</html>