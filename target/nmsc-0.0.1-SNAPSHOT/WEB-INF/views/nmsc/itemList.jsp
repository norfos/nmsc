<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
$(document).ready(function() {

	/* itemDefaultList();
	setTimeout(function() {
		itemEirList();
		}, 500);
	setTimeout(function() {
		itemGgbList();
		}, 1000);
	setTimeout(function() {
		reqItemLvl3List();
		}, 1500);
	setTimeout(function() {
		reqItemLvl4List();
		}, 2000);
	setTimeout(function() {
		reqItemLvl2List();
		}, 2500);
	setTimeout(function() {
		reqItemOhterList();
		}, 3000);
	setTimeout(function() {
		reqItemCollectList();
		}, 3500);
	setTimeout(function() {
		reqItemSwfcList();
		}, 4000);
 
	//1분마다 새로고침
	setInterval(function(){
		refresh();
	},60000);
	*/
	
	setInterval(function(){
		location.reload();
	},60000);
});



function refresh(){
	itemDefaultList();
	setTimeout(function() {
		itemEirList();
		}, 500);
	setTimeout(function() {
		itemGgbList();
		}, 1000);
	setTimeout(function() {
		reqItemLvl3List();
		}, 1500);
	setTimeout(function() {
		reqItemLvl4List();
		}, 2000);
	setTimeout(function() {
		reqItemLvl2List();
		}, 2500);
	setTimeout(function() {
		reqItemOhterList();
		}, 3000);
	setTimeout(function() {
		reqItemCollectList();
		}, 3500);
	setTimeout(function() {
		reqItemSwfcList();
		}, 4000);
}

function itemDefaultList(){
	
	
	var data = {};
	data.yyyyMMdd = "${yyyyMMdd}";
	data.HH = "${HH}";
	data.mm = "${mm}";
		$.ajax({
		      url : "/nmsc/itemDefaultList",   
		      type : "post",
		      data : JSON.stringify(data),
			  contentType: 'application/json',
			  dataType : 'json',
		      success : function(result){
		    	  $("#kdate").text(result.kdate);
		    	  $("#udate").text(result.udate);
		    	  try{
		        	tbody = $("#le1bList tbody");
		        	tbody.children().remove();
		        	if(result.reqItemLvl1List.length > 0){
		        		$(result.reqItemLvl1List).each(function(index, item){
							var html = "";
							var mntTime = "";
							html += '<tr>';
							var tdType = "";
							if(item["F_CNT"] > 0){	
								tdType = '<td bgcolor="#F78181">';
								mntTime = item["MNT_TIME"];
							}else if(item["R_CNT"] > 0){
								tdType = '<td bgcolor="#F7D358">';
							}else{
								tdType = '<td bgcolor="#81BEF7">';
							}
							var dataType = "";
							if(item["DATA_TYPE"] == "RGB"){
								dataType = 'RGB';
							}else if(item["DATA_TYPE"] == "EIR"){
								dataType = '컬러강조';
							}else if(item["DATA_TYPE"] == "DEFAULT"){
								dataType = '기본';
							}
							html += tdType+'<div id="'+item["DATA_TYPE"]+'" onclick="fn_areaPopup(this.id, \'LE1B\', \'GK2A\', \'0\'); return false;" style="cursor:pointer;">'+dataType+'</div></td>';
							html += tdType+'<div id="'+item["DATA_TYPE"]+'" onclick="fn_areaPopup(this.id, \'LE1B\', \'GK2A\', \'0\'); return false;" style="cursor:pointer;">'+item["TOT_CNT"]+' / '+item["SUC_CNT"]+' / '+item["FAIL_CNT"]+'</div></td>';
							html += tdType+'<div id="'+item["DATA_TYPE"]+'" onclick="fn_areaPopup(this.id, \'LE1B\', \'GK2A\', \'0\'); return false;" style="cursor:pointer;">'+mntTime+'</div></td>';
							html += '</tr>';
							tbody.append(html);
	        		 	});
		        		 	
					} else {
							html = "";
							html += '<tr class="NoData">';
							html += '<th colspan="1">';
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

function itemEirList(){
	var data = {};
	data.yyyyMMdd = "${yyyyMMdd}";
	data.HH = "${HH}";
	data.mm = "${mm}";
		$.ajax({
		      url : "/nmsc/itemEirList",   
		      type : "post",
		      data : JSON.stringify(data),
			  contentType: 'application/json',
			  dataType : 'json',
		      success : function(result){
		    	  try{
		        	tbody = $("#le1bList tbody");
		        	if(result.reqItemLvl1List.length > 0){
		        		$(result.reqItemLvl1List).each(function(index, item){
							var html = "";
							var mntTime = "";
							html += '<tr>';
							var tdType = "";
							if(item["F_CNT"] > 0){	
								tdType = '<td bgcolor="#F78181">';
								mntTime = item["MNT_TIME"];
							}else if(item["R_CNT"] > 0){
								tdType = '<td bgcolor="#F7D358">';
							}else{
								tdType = '<td bgcolor="#81BEF7">';
							}
							var dataType = "";
							if(item["DATA_TYPE"] == "RGB"){
								dataType = 'RGB';
							}else if(item["DATA_TYPE"] == "EIR"){
								dataType = '컬러강조';
							}else if(item["DATA_TYPE"] == "DEFAULT"){
								dataType = '기본';
							}
							html += tdType+'<div id="'+item["DATA_TYPE"]+'" onclick="fn_areaPopup(this.id, \'LE1B\', \'GK2A\', \'0\'); return false;" style="cursor:pointer;">'+dataType+'</div></td>';
							html += tdType+'<div id="'+item["DATA_TYPE"]+'" onclick="fn_areaPopup(this.id, \'LE1B\', \'GK2A\', \'0\'); return false;" style="cursor:pointer;">'+item["TOT_CNT"]+' / '+item["SUC_CNT"]+' / '+item["FAIL_CNT"]+'</div></td>';
							html += tdType+'<div id="'+item["DATA_TYPE"]+'" onclick="fn_areaPopup(this.id, \'LE1B\', \'GK2A\', \'0\'); return false;" style="cursor:pointer;">'+mntTime+'</div></td>';
							html += '</tr>';
							tbody.append(html);
	        		 	});
		        		 	
					} else {
							html = "";
							html += '<tr class="NoData">';
							html += '<th colspan="1">';
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

function itemGgbList(){
	var data = {};
	data.yyyyMMdd = "${yyyyMMdd}";
	data.HH = "${HH}";
	data.mm = "${mm}";
		$.ajax({
		      url : "/nmsc/itemGgbList",   
		      type : "post",
		      data : JSON.stringify(data),
			  contentType: 'application/json',
			  dataType : 'json',
		      success : function(result){
		    	  try{
		        	tbody = $("#le1bList tbody");
		        	if(result.reqItemLvl1List.length > 0){
		        		$(result.reqItemLvl1List).each(function(index, item){
							var html = "";
							var mntTime = "";
							html += '<tr>';
							var tdType = "";
							if(item["F_CNT"] > 0){	
								tdType = '<td bgcolor="#F78181">';
								mntTime = item["MNT_TIME"];
							}else if(item["R_CNT"] > 0){
								tdType = '<td bgcolor="#F7D358">';
							}else{
								tdType = '<td bgcolor="#81BEF7">';
							}
							var dataType = "";
							if(item["DATA_TYPE"] == "RGB"){
								dataType = 'RGB';
							}else if(item["DATA_TYPE"] == "EIR"){
								dataType = '컬러강조';
							}else if(item["DATA_TYPE"] == "DEFAULT"){
								dataType = '기본';
							}
							html += tdType+'<div id="'+item["DATA_TYPE"]+'" onclick="fn_areaPopup(this.id, \'LE1B\', \'GK2A\', \'0\'); return false;" style="cursor:pointer;">'+dataType+'</div></td>';
							html += tdType+'<div id="'+item["DATA_TYPE"]+'" onclick="fn_areaPopup(this.id, \'LE1B\', \'GK2A\', \'0\'); return false;" style="cursor:pointer;">'+item["TOT_CNT"]+' / '+item["SUC_CNT"]+' / '+item["FAIL_CNT"]+'</div></td>';
							html += tdType+'<div id="'+item["DATA_TYPE"]+'" onclick="fn_areaPopup(this.id, \'LE1B\', \'GK2A\', \'0\'); return false;" style="cursor:pointer;">'+mntTime+'</div></td>';
							html += '</tr>';
							tbody.append(html);
	        		 	});
		        		 	
					} else {
							html = "";
							html += '<tr class="NoData">';
							html += '<th colspan="1">';
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

function reqItemLvl2List(){
	var data = {};
	data.yyyyMMdd = "${yyyyMMdd}";
	data.HH = "${HH}";
	data.mm = "${mm}";
	$.ajax({
	      url : "/nmsc/reqItemLvl2List",   
	      type : "post",
	      data : JSON.stringify(data),
		  contentType: 'application/json',
		  dataType : 'json',
	      success : function(result){
	    	  try{
	        	tbody = $("#le2List tbody");
	        	tbody.children().remove();
	        	if(result.reqItemLvl2List.length > 0){
	        		$(result.reqItemLvl2List).each(function(index, item){
						var html = "";
						var mntTime = "";
						html += '<tr>';
						var tdType = "";
						if(item["F_CNT"] > 0){	
							tdType = '<td bgcolor="#F78181">';
							mntTime = item["MNT_TIME"];
						}else if(item["R_CNT"] > 0){
							tdType = '<td bgcolor="#F7D358">';
						}else{
							tdType = '<td bgcolor="#81BEF7">';
						}
						var dataType = "";
						if(item["DATA_TYPE"] == "RGB"){
							dataType = 'RGB';
						}else if(item["DATA_TYPE"] == "EIR"){
							dataType = '컬러강조';
						}else if(item["DATA_TYPE"] == "DEFAULT"){
							dataType = '기본';
						}
						html += tdType+'<div id="'+item["GR_TYPE"]+'" onclick="fn_areaPopup(this.id, \'LE2\', \'GK2A\', \''+item["MNT_CYCLE"]+'\'); return false;" style="cursor:pointer;">'+item["GR_TYPE"]+'</div></td>';
						html += tdType+'<div id="'+item["GR_TYPE"]+'" onclick="fn_areaPopup(this.id, \'LE2\', \'GK2A\', \''+item["MNT_CYCLE"]+'\'); return false;" style="cursor:pointer;">'+item["TOT_CNT"]+' / '+item["SUC_CNT"]+' / '+item["FAIL_CNT"]+'</div></td>';
						html += tdType+'<div id="'+item["GR_TYPE"]+'" onclick="fn_areaPopup(this.id, \'LE2\', \'GK2A\', \''+item["MNT_CYCLE"]+'\'); return false;" style="cursor:pointer;">'+item["MNT_TIME"]+'</div></td>';
						html += '</tr>';
						tbody.append(html);
        		 	});
	        		 	
				} else {
						html = "";
						html += '<tr class="NoData">';
						html += '<th colspan="1">';
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

function reqItemLvl3List(){
	var data = {};
	data.yyyyMMdd = "${yyyyMMdd}";
	data.HH = "${HH}";
	data.mm = "${mm}";
	$.ajax({
	      url : "/nmsc/reqItemLvl3List",   
	      type : "post",
	      data : JSON.stringify(data),
		  contentType: 'application/json',
		  dataType : 'json',
	      success : function(result){
	    	  try{
	        	tbody = $("#le3List tbody");
	        	tbody.children().remove();
	        	if(result.reqItemLvl3List.length > 0){
	        		$(result.reqItemLvl3List).each(function(index, item){
						var html = "";
						var mntTime = "";
						html += '<tr>';
						var tdType = "";
						if(item["F_CNT"] > 0){	
							tdType = '<td bgcolor="#F78181">';
							mntTime = item["MNT_TIME"];
						}else if(item["R_CNT"] > 0){
							tdType = '<td bgcolor="#F7D358">';
						}else{
							tdType = '<td bgcolor="#81BEF7">';
						}
						var dataType = "";
						if(item["DATA_TYPE"] == "RGB"){
							dataType = 'RGB';
						}else if(item["DATA_TYPE"] == "EIR"){
							dataType = '컬러강조';
						}else if(item["DATA_TYPE"] == "DEFAULT"){
							dataType = '기본';
						}
						html += tdType+'<div id="'+item["GR_TYPE"]+'" onclick="fn_areaPopup(this.id, \'LE3\', \'GK2A\', \''+item["MNT_CYCLE"]+'\'); return false;" style="cursor:pointer;">'+item["GR_TYPE"]+'</div></td>';
						html += tdType+'<div id="'+item["GR_TYPE"]+'" onclick="fn_areaPopup(this.id, \'LE3\', \'GK2A\', \''+item["MNT_CYCLE"]+'\'); return false;" style="cursor:pointer;">'+item["TOT_CNT"]+' / '+item["SUC_CNT"]+' / '+item["FAIL_CNT"]+'</div></td>';
						html += tdType+'<div id="'+item["GR_TYPE"]+'" onclick="fn_areaPopup(this.id, \'LE3\', \'GK2A\', \''+item["MNT_CYCLE"]+'\'); return false;" style="cursor:pointer;">'+item["MNT_TIME"]+'</div></td>';
						html += '</tr>';
						tbody.append(html);
        		 	});
	        		 	
				} else {
						html = "";
						html += '<tr class="NoData">';
						html += '<th colspan="1">';
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

function reqItemLvl4List(){
	var data = {};
	data.yyyyMMdd = "${yyyyMMdd}";
	data.HH = "${HH}";
	data.mm = "${mm}";
	$.ajax({
	      url : "/nmsc/reqItemLvl4List",   
	      type : "post",
	      data : JSON.stringify(data),
		  contentType: 'application/json',
		  dataType : 'json',
	      success : function(result){
	    	  try{
	        	tbody = $("#le4List tbody");
	        	tbody.children().remove();
	        	if(result.reqItemLvl4List.length > 0){
	        		$(result.reqItemLvl4List).each(function(index, item){
						var html = "";
						var mntTime = "";
						html += '<tr>';
						var tdType = "";
						if(item["F_CNT"] > 0){	
							tdType = '<td bgcolor="#F78181">';
							mntTime = item["MNT_TIME"];
						}else if(item["R_CNT"] > 0){
							tdType = '<td bgcolor="#F7D358">';
						}else{
							tdType = '<td bgcolor="#81BEF7">';
						}
						var dataType = "";
						if(item["DATA_TYPE"] == "RGB"){
							dataType = 'RGB';
						}else if(item["DATA_TYPE"] == "EIR"){
							dataType = '컬러강조';
						}else if(item["DATA_TYPE"] == "DEFAULT"){
							dataType = '기본';
						}
						html += tdType+'<div id="'+item["GR_TYPE"]+'" onclick="fn_areaPopup(this.id, \'LE4\', \'GK2A\', \''+item["MNT_CYCLE"]+'\'); return false;" style="cursor:pointer;">'+item["GR_TYPE"]+'</div></td>';
						html += tdType+'<div id="'+item["GR_TYPE"]+'" onclick="fn_areaPopup(this.id, \'LE4\', \'GK2A\', \''+item["MNT_CYCLE"]+'\'); return false;" style="cursor:pointer;">'+item["TOT_CNT"]+' / '+item["SUC_CNT"]+' / '+item["FAIL_CNT"]+'</div></td>';
						html += tdType+'<div id="'+item["GR_TYPE"]+'" onclick="fn_areaPopup(this.id, \'LE4\', \'GK2A\', \''+item["MNT_CYCLE"]+'\'); return false;" style="cursor:pointer;">'+item["MNT_TIME"]+'</div></td>';
						html += '</tr>';
						tbody.append(html);
        		 	});
	        		 	
				} else {
						html = "";
						html += '<tr class="NoData">';
						html += '<th colspan="1">';
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

function reqItemOhterList(){
	var data = {};
	data.yyyyMMdd = "${yyyyMMdd}";
	data.HH = "${HH}";
	data.mm = "${mm}";
	$.ajax({
	      url : "/nmsc/reqItemOhterList",   
	      type : "post",
	      data : JSON.stringify(data),
		  contentType: 'application/json',
		  dataType : 'json',
	      success : function(result){
	    	  try{
	        	tbody = $("#otherList tbody");
	        	tbody.children().remove();
	        	if(result.reqItemOhterList.length > 0){
	        		$(result.reqItemOhterList).each(function(index, item){
						var html = "";
						var mntTime = "";
						html += '<tr>';
						var tdType = "";
						if(item["F_CNT"] > 0){	
							tdType = '<td bgcolor="#F78181">';
							mntTime = item["MNT_TIME"];
						}else if(item["R_CNT"] > 0){
							tdType = '<td bgcolor="#F7D358">';
						}else{
							tdType = '<td bgcolor="#81BEF7">';
						}
						var dataType = "";
						if(item["DATA_TYPE"] == "RGB"){
							dataType = 'RGB';
						}else if(item["DATA_TYPE"] == "EIR"){
							dataType = '컬러강조';
						}else if(item["DATA_TYPE"] == "DEFAULT"){
							dataType = '기본';
						}
						html += tdType+'<div id="'+item["SATELLITE"]+'" onclick="fn_areaPopup(this.id, \'OTHER\', \''+item["SATELLITE"]+'\', \'0\'); return false;" style="cursor:pointer;">'+item["SATELLITE"]+'</div></td>';
						html += tdType+'<div id="'+item["SATELLITE"]+'" onclick="fn_areaPopup(this.id, \'OTHER\', \''+item["SATELLITE"]+'\', \'0\'); return false;" style="cursor:pointer;">'+item["TOT_CNT"]+' / '+item["SUC_CNT"]+' / '+item["FAIL_CNT"]+'</div></td>';
						html += tdType+'<div id="'+item["SATELLITE"]+'" onclick="fn_areaPopup(this.id, \'OTHER\', \''+item["SATELLITE"]+'\', \'0\'); return false;" style="cursor:pointer;">'+item["MNT_TIME"]+'</div></td>';
						html += '</tr>';
						tbody.append(html);
        		 	});
	        		 	
				} else {
						html = "";
						html += '<tr class="NoData">';
						html += '<th colspan="1">';
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

function reqItemCollectList(){
	var data = {};
	data.yyyyMMdd = "${yyyyMMdd}";
	data.HH = "${HH}";
	data.mm = "${mm}";
	$.ajax({
	      url : "/nmsc/reqItemCollectList",   
	      type : "post",
	      data : JSON.stringify(data),
		  contentType: 'application/json',
		  dataType : 'json',
	      success : function(result){
	    	  try{
	        	tbody = $("#collectList tbody");
	        	tbody.children().remove();
	        	if(result.reqItemCollectList.length > 0){
	        		$(result.reqItemCollectList).each(function(index, item){
						var html = "";
						var mntTime = "";
						html += '<tr>';
						var tdType = "";
						if(item["F_CNT"] > 0){	
							tdType = '<td bgcolor="#F78181">';
							mntTime = item["MNT_TIME"];
						}else if(item["R_CNT"] > 0){
							tdType = '<td bgcolor="#F7D358">';
						}else{
							tdType = '<td bgcolor="#81BEF7">';
						}
						var dataType = "";
						if(item["DATA_TYPE"] == "RGB"){
							dataType = 'RGB';
						}else if(item["DATA_TYPE"] == "EIR"){
							dataType = '컬러강조';
						}else if(item["DATA_TYPE"] == "DEFAULT"){
							dataType = '기본';
						}
						html += tdType+'<div id="'+item["DATA_TYPE"]+'" onclick="fn_areaPopup(this.id, \'COLLECT\', \''+item["DATA_TYPE"]+'\', \'0\'); return false;" style="cursor:pointer;">'+item["DATA_TYPE"]+'</div></td>';
						html += tdType+'<div id="'+item["DATA_TYPE"]+'" onclick="fn_areaPopup(this.id, \'COLLECT\', \''+item["DATA_TYPE"]+'\', \'0\'); return false;" style="cursor:pointer;">'+item["TOT_CNT"]+' / '+item["SUC_CNT"]+' / '+item["FAIL_CNT"]+'</div></td>';
						html += tdType+'<div id="'+item["DATA_TYPE"]+'" onclick="fn_areaPopup(this.id, \'COLLECT\', \''+item["DATA_TYPE"]+'\', \'0\'); return false;" style="cursor:pointer;">'+item["MNT_TIME"]+'</div></td>';
						html += '</tr>';
						tbody.append(html);
        		 	});
	        		 	
				} else {
						html = "";
						html += '<tr class="NoData">';
						html += '<th colspan="1">';
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

function reqItemSwfcList(){
	var data = {};
	data.yyyyMMdd = "${yyyyMMdd}";
	data.HH = "${HH}";
	data.mm = "${mm}";
	$.ajax({
	      url : "/nmsc/reqItemSwfcList",   
	      type : "post",
	      data : JSON.stringify(data),
		  contentType: 'application/json',
		  dataType : 'json',
	      success : function(result){
	    	  try{
	        	tbody = $("#swfcList tbody");
	        	tbody.children().remove();
	        	if(result.reqItemSwfcList.length > 0){
	        		$(result.reqItemSwfcList).each(function(index, item){
						var html = "";
						var mntTime = "";
						html += '<tr>';
						var tdType = "";
						if(item["F_CNT"] > 0){	
							tdType = '<td bgcolor="#F78181">';
							mntTime = item["MNT_TIME"];
						}else if(item["R_CNT"] > 0){
							tdType = '<td bgcolor="#F7D358">';
						}else{
							tdType = '<td bgcolor="#81BEF7">';
						}
						
						html += tdType+'<div id="'+item["SATELLITE"]+'" onclick="fn_areaPopup(this.id, \'OTHER\', \''+item["DATA_TYPE"]+'\', \'0\'); return false;" style="cursor:pointer;">'+item["DATA_TYPE"]+'</div></td>';
						html += tdType+'<div id="'+item["SATELLITE"]+'" onclick="fn_areaPopup(this.id, \'OTHER\', \''+item["DATA_TYPE"]+'\', \'0\'); return false;" style="cursor:pointer;">'+item["TOT_CNT"]+' / '+item["SUC_CNT"]+' / '+item["FAIL_CNT"]+'</div></td>';
						html += tdType+'<div id="'+item["SATELLITE"]+'" onclick="fn_areaPopup(this.id, \'OTHER\', \''+item["DATA_TYPE"]+'\', \'0\'); return false;" style="cursor:pointer;">'+item["MNT_TIME"]+'</div></td>';
						html += '</tr>';
						tbody.append(html);
        		 	});
	        		 	
				} else {
						html = "";
						html += '<tr class="NoData">';
						html += '<th colspan="1">';
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
	
function fn_areaPopup(typeVal, lvVal, sattleVal, mntCycle){
	$("#dataType").val(typeVal);
	$("#dataLevel").val(lvVal);
	$("#mntCycle").val(mntCycle);
	$("#satellite").val(sattleVal);
    var pop_title = "dataTypePopup";
	window.open('', pop_title, 'width=1800,height=800,resizeable,scrollbars');
	$("#areaPopupForm").attr("action", "/nmsc/dataTypePopup");
	$("#areaPopupForm").attr("target", pop_title);
	$("#areaPopupForm").attr("method", "post");
	$("#areaPopupForm").submit();
}

function fn_guide(){
    var popup = document.getElementById("guidePop");
    popup.style.display = "block";
}
function fn_close(){
	var popup = document.getElementById("guidePop");
    popup.style.display = "none";
}

</script>

</head>
<form id="areaPopupForm">
	<input type="hidden" id="dataType" name="dataType" value=""/>
	<input type="hidden" id="dataLevel" name="dataLevel" value=""/>
	<input type="hidden" id="satellite" name="satellite" value=""/>
	<input type="hidden" id="mntCycle" name="mntCycle" value=""/>
	<input type="hidden" id="yyyyMMdd" name="yyyyMMdd" value="${yyyyMMdd}"/>
	<input type="hidden" id="HH" name="HH" value="${HH}"/>
	<input type="hidden" id="mm" name="mm" value="${mm}"/>
</form>
<body>
<div id="guidePop" style="display:none; width:35%; position:fixed; top:40%; left:80%; transform:translate(-50%,-50%); padding:20px; background-color:#fff; border:1px solid #ccc; z-index:1;">
	<button onclick="fn_close();" style="background-color:black; color:white; min-height:30px; min-width:100px;font-size:20px; cursor:pointer; float:right;">닫기</button>
	<table style="width:100%; background-color:white; margin: auto; text-align: center; border-bottom:1px solid black;">
		<tr>
			<td><img id="guide_img" alt="guide" src="/resources/images/guide01.png" /></td>
		</tr>
		<tr><td style="text-align:left;">① : 데이터 종류 </td></tr>
		<tr><td style="text-align:left;">② : 체크시간내 표출(파랑색) </td></tr>
		<tr><td style="text-align:left;">③ : 체크시간외 표출/미표출(노랑색) </td></tr>
		<tr><td style="text-align:left;">④ : 체크시간외 재체크 후 미표출(빨강색) </td></tr>
		<tr><td style="text-align:left;">⑤ : 총건수/표출건수/미표출건수 </td></tr>
		<tr><td style="text-align:left;">⑥ : 최근 미표출 시간 </td></tr>
	</table>
	<b>※데이터 설정</b><br/>
	- 천리안위성 AMI-LE1 : GK2A / LE1B / RGB,EIR,NOT RGB AND NOT EIR / 최근 1시간<BR/>
	- 천리안위성 AMI-LE2 : GK2A / LE2  / 최근 1시간(1440분 주기데이터 포함)<BR/>
	- 천리안위성 AMI-LE3 : GK2A / LE3  / 최근 1시간(1440분 주기데이터 포함)<BR/>
	- 천리안위성 AMI-LE4 : GK2A / LE4  / 최근 1시간(1440분 주기데이터 포함)<BR/>
	- 해외 및 기타 위성 : NOT GK2A AND NOT SWFC / 최근 24시간<BR/>
	- 수집데이터 : 비동기 / 최근 12시간<BR/>
	- 우주기상 : SWFC / 최근 24시간
</div>
<div>
<div>
	<table style="width:100%; background-color:white; margin: auto; text-align: center; border-bottom:1px solid black;">
		<tr>
			<td width="20%"><button onclick="refresh();" style="background-color:black; color:white; min-height:30px; min-width:200px;font-size:20px; cursor:pointer;">새로고침</button></td>
			<td width="60%" style="color:white;"><a aria-label="home" href="/nmsc/itemListView"><H1>위성산출물 통합 모니터링 시스템</H1></a></td>
			<td width="20%">
				<H3 id="kdate">${kdate}</H3>
				<H3 id="udate">${udate}</H3>
			</td>
		</tr>
	</table>
	<button onclick="fn_guide();" style="background-color:blue; color:white; min-height:30px; min-width:200px;font-size:20px; cursor:pointer; float:right; display:inline-block;">도움말</button>
	<a aria-label="home" href="/nmsc/adminListView" target="_blank" style="display:block; width:300px;"><H3>ADMIN PAGE</H3></a>
	<a aria-label="home" href="/nmsc/notRctListView" target="_blank" style="display:block; width:300px;"><H3>미표출 통계</H3></a>
</div>
</div>
<div class="item-wrap">
	<div class="items">
		<div class="item left">
			<div class="inner">
				<table id ="le1bList"  style="width:100%; float:left; text-align: center;">
					<thead>
						<tr>
							<th colspan="3">천리안위성 AMI-LE1</th>
						</tr>
					</thead>
					<tbody>
					
					<c:forEach var="data" items="${chkList}" varStatus="status">
					<tr>
					<c:choose>
					<c:when test="${data.F_CNT > 0 }">
					<c:set var="tdType" value="#F78181"/>
					</c:when>
					<c:when test="${data.R_CNT > 0 }">
					<c:set var="tdType" value="#F7D358"/>
					</c:when>
					<c:otherwise>
					<c:set var="tdType" value="#81BEF7"/>
					</c:otherwise>
					</c:choose>
					<c:choose>
					<c:when test="${data.DATA_TYPE == 'RGB' }">
					<c:set var="dataType" value="RGB"/>
					</c:when>
					<c:when test="${data.DATA_TYPE == 'EIR' }">
					<c:set var="dataType" value="컬러강조"/>
					</c:when>
					<c:otherwise>
					<c:set var="dataType" value="기본"/>
					</c:otherwise>
					</c:choose>
					<td bgcolor="${tdType}"><div id="${data.DATA_TYPE}" onclick="fn_areaPopup(this.id, 'LE1B', 'GK2A', '0'); return false;" style="cursor:pointer;">${dataType}</div></td>
					<td bgcolor="${tdType}"><div id="${data.DATA_TYPE}" onclick="fn_areaPopup(this.id, 'LE1B', 'GK2A', '0'); return false;" style="cursor:pointer;">${data.TOT_CNT}/${data.SUC_CNT}/${data.FAIL_CNT}</div></td>
					<td bgcolor="${tdType}"><div id="${data.DATA_TYPE}" onclick="fn_areaPopup(this.id, 'LE1B', 'GK2A', '0'); return false;" style="cursor:pointer;">${data.MNT_TIME}</div></td>
					</tr>
					</c:forEach>
					</tbody>
				</table>
				
				<table id ="le3List"  style="width:100%; float:left; text-align: center;">
					<thead>
						<tr>
							<th colspan="3">천리안위성 AMI-LE3</th>
						</tr>
					</thead>
					<tbody>
					<c:forEach var="data" items="${reqItemLvl3List}" varStatus="status">
					<tr>
					<c:choose>
					<c:when test="${data.F_CNT > 0 }">
					<c:set var="tdType" value="#F78181"/>
					</c:when>
					<c:when test="${data.R_CNT > 0 }">
					<c:set var="tdType" value="#F7D358"/>
					</c:when>
					<c:otherwise>
					<c:set var="tdType" value="#81BEF7"/>
					</c:otherwise>
					</c:choose>
					<td bgcolor="${tdType}"><div id="${data.GR_TYPE}" onclick="fn_areaPopup(this.id, 'LE3', 'GK2A', '${data.MNT_CYCLE}'); return false;" style="cursor:pointer;">${data.GR_TYPE}</div></td>
					<td bgcolor="${tdType}"><div id="${data.GR_TYPE}" onclick="fn_areaPopup(this.id, 'LE3', 'GK2A', '${data.MNT_CYCLE}'); return false;" style="cursor:pointer;">${data.TOT_CNT}/${data.SUC_CNT}/${data.FAIL_CNT}</div></td>
					<td bgcolor="${tdType}"><div id="${data.GR_TYPE}" onclick="fn_areaPopup(this.id, 'LE3', 'GK2A', '${data.MNT_CYCLE}'); return false;" style="cursor:pointer;">${data.MNT_TIME}</div></td>
					</tr>
					</c:forEach>
					</tbody>
				</table>
				
				<table id ="le4List"  style="width:100%; float:left; text-align: center;">
					<thead>
						<tr>
							<th colspan="3">천리안위성 AMI-LE4</th>
						</tr>
					</thead>
					<tbody>
					<c:forEach var="data" items="${reqItemLvl4List}" varStatus="status">
					<tr>
					<c:choose>
					<c:when test="${data.F_CNT > 0 }">
					<c:set var="tdType" value="#F78181"/>
					</c:when>
					<c:when test="${data.R_CNT > 0 }">
					<c:set var="tdType" value="#F7D358"/>
					</c:when>
					<c:otherwise>
					<c:set var="tdType" value="#81BEF7"/>
					</c:otherwise>
					</c:choose>
					<td bgcolor="${tdType}"><div id="${data.GR_TYPE}" onclick="fn_areaPopup(this.id, 'LE4', 'GK2A', '${data.MNT_CYCLE}'); return false;" style="cursor:pointer;">${data.GR_TYPE}</div></td>
					<td bgcolor="${tdType}"><div id="${data.GR_TYPE}" onclick="fn_areaPopup(this.id, 'LE4', 'GK2A', '${data.MNT_CYCLE}'); return false;" style="cursor:pointer;">${data.TOT_CNT}/${data.SUC_CNT}/${data.FAIL_CNT}</div></td>
					<td bgcolor="${tdType}"><div id="${data.GR_TYPE}" onclick="fn_areaPopup(this.id, 'LE4', 'GK2A', '${data.MNT_CYCLE}'); return false;" style="cursor:pointer;">${data.MNT_TIME}</div></td>
					</tr>
					</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
		<div class="item left">
			<div class="inner">
				<table id ="le2List"  style="width:100%; float:left; text-align: center;">
					<thead>
						<tr>
							<th colspan="3">천리안위성 AMI-LE2</th>
						</tr>
					</thead>
					<tbody>
					<c:forEach var="data" items="${reqItemLvl2List}" varStatus="status">
					<tr>
					<c:choose>
					<c:when test="${data.F_CNT > 0 }">
					<c:set var="tdType" value="#F78181"/>
					</c:when>
					<c:when test="${data.R_CNT > 0 }">
					<c:set var="tdType" value="#F7D358"/>
					</c:when>
					<c:otherwise>
					<c:set var="tdType" value="#81BEF7"/>
					</c:otherwise>
					</c:choose>
					<td bgcolor="${tdType}"><div id="${data.GR_TYPE}" onclick="fn_areaPopup(this.id, 'LE2', 'GK2A', '${data.MNT_CYCLE}'); return false;" style="cursor:pointer;">${data.GR_TYPE}</div></td>
					<td bgcolor="${tdType}"><div id="${data.GR_TYPE}" onclick="fn_areaPopup(this.id, 'LE2', 'GK2A', '${data.MNT_CYCLE}'); return false;" style="cursor:pointer;">${data.TOT_CNT}/${data.SUC_CNT}/${data.FAIL_CNT}</div></td>
					<td bgcolor="${tdType}"><div id="${data.GR_TYPE}" onclick="fn_areaPopup(this.id, 'LE2', 'GK2A', '${data.MNT_CYCLE}'); return false;" style="cursor:pointer;">${data.MNT_TIME}</div></td>
					</tr>
					</c:forEach>
					</tbody>
				</table>
				
			</div>
		</div>
		<div class="item left">
			<div class="inner">
				<table id ="otherList" style="width:100%; float:left; text-align: center;">
					<thead>
						<tr>
							<th colspan="3">해외 및 기타 위성</th>
						</tr>
					</thead>
					<tbody>
					<c:forEach var="data" items="${reqItemOhterList}" varStatus="status">
					<tr>
					<c:choose>
					<c:when test="${data.F_CNT > 0 }">
					<c:set var="tdType" value="#F78181"/>
					</c:when>
					<c:when test="${data.R_CNT > 0 }">
					<c:set var="tdType" value="#F7D358"/>
					</c:when>
					<c:otherwise>
					<c:set var="tdType" value="#81BEF7"/>
					</c:otherwise>
					</c:choose>
					<td bgcolor="${tdType}"><div id="${data.SATELLITE}" onclick="fn_areaPopup(this.id, 'OTHER', '${data.SATELLITE}', '0'); return false;" style="cursor:pointer;">${data.SATELLITE}</div></td>
					<td bgcolor="${tdType}"><div id="${data.SATELLITE}" onclick="fn_areaPopup(this.id, 'OTHER', '${data.SATELLITE}', '0'); return false;" style="cursor:pointer;">${data.TOT_CNT}/${data.SUC_CNT}/${data.FAIL_CNT}</div></td>
					<td bgcolor="${tdType}"><div id="${data.SATELLITE}" onclick="fn_areaPopup(this.id, 'OTHER', '${data.SATELLITE}', '0'); return false;" style="cursor:pointer;">${data.MNT_TIME}</div></td>
					</tr>
					</c:forEach>
					</tbody>
				</table>
				
				<table id ="collectList" style="width:100%; float:right; text-align: center;">
					<thead>
						<tr>
							<th colspan="3">수집데이터</th>
						</tr>
					</thead>
					<tbody>
					<c:forEach var="data" items="${reqItemCollectList}" varStatus="status">
					<tr>
					<c:choose>
					<c:when test="${data.F_CNT > 0 }">
					<c:set var="tdType" value="#F78181"/>
					</c:when>
					<c:when test="${data.R_CNT > 0 }">
					<c:set var="tdType" value="#F7D358"/>
					</c:when>
					<c:otherwise>
					<c:set var="tdType" value="#81BEF7"/>
					</c:otherwise>
					</c:choose>
					<td bgcolor="${tdType}"><div id="${data.DATA_TYPE}" onclick="fn_areaPopup(this.id, 'COLLECT', '${data.SATELLITE}', '0'); return false;" style="cursor:pointer;">${data.DATA_TYPE}</div></td>
					<td bgcolor="${tdType}"><div id="${data.DATA_TYPE}" onclick="fn_areaPopup(this.id, 'COLLECT', '${data.SATELLITE}', '0'); return false;" style="cursor:pointer;">${data.TOT_CNT}/${data.SUC_CNT}/${data.FAIL_CNT}</div></td>
					<td bgcolor="${tdType}"><div id="${data.DATA_TYPE}" onclick="fn_areaPopup(this.id, 'COLLECT', '${data.SATELLITE}', '0'); return false;" style="cursor:pointer;">${data.MNT_TIME}</div></td>
					</tr>
					</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
		<div class="item left">
			<div class="inner">
				<table id ="swfcList" style="width:100%; float:right; text-align: center;">
					<thead>
						<tr>
							<th colspan="3">우주기상</th>
						</tr>
					</thead>
					<tbody>
					<c:forEach var="data" items="${reqItemSwfcList}" varStatus="status">
					<tr>
					<c:choose>
					<c:when test="${data.F_CNT > 0 }">
					<c:set var="tdType" value="#F78181"/>
					</c:when>
					<c:when test="${data.R_CNT > 0 }">
					<c:set var="tdType" value="#F7D358"/>
					</c:when>
					<c:otherwise>
					<c:set var="tdType" value="#81BEF7"/>
					</c:otherwise>
					</c:choose>
					<td bgcolor="${tdType}"><div id="${data.GR_TYPE}" onclick="fn_areaPopup(this.id, 'SWFC', '${data.SATELLITE}', '0'); return false;" style="cursor:pointer;">${data.GR_TYPE}</div></td>
					<td bgcolor="${tdType}"><div id="${data.GR_TYPE}" onclick="fn_areaPopup(this.id, 'SWFC', '${data.SATELLITE}', '0'); return false;" style="cursor:pointer;">${data.TOT_CNT}/${data.SUC_CNT}/${data.FAIL_CNT}</div></td>
					<td bgcolor="${tdType}"><div id="${data.GR_TYPE}" onclick="fn_areaPopup(this.id, 'SWFC', '${data.SATELLITE}', '0'); return false;" style="cursor:pointer;">${data.MNT_TIME}</div></td>
					</tr>
					</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
		
	</div>
</div>
		
</body>
</html>