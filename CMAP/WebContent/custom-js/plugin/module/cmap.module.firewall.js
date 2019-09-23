/**
 * 
 */

var timer, startTime, timer_start, timer_end;
var waitForNextData = false;
var startNum, pageLength;
var lastScrollYPos = 0;

$(document).ready(function() {
	initMenuStatus("toggleMenu_plugin", "toggleMenu_plugin_items", "cm_firewallLog");
	
	startNum = 0;
	pageLength = Number($("#pageLength").val());
	
	$("input").val("");
	
	/*
	var inputIp = new Cleave('.input-ip', {
		numericOnly: true,
		delimiter: '.',
	    blocks: [3, 3, 3, 3]
	});
	*/
	
	var inputPort = new Cleave('.input-port', {
		numericOnly: true,
		blocks: [5]
	});
	
	/*
	var inputMac = new Cleave('.input-mac', {
		delimiter: ':',
		blocks: [2, 2, 2, 2, 2, 2],
		uppercase: true
	});
	*/
	
	$("#resultTable").on('xhr.dt', function ( e, settings, json, xhr ) {
		if (json.msg != null) {
			$(".myTableSection").hide();
			alert(json.msg);
		}
	});
	
	var today = new Date();
	var year = today.getFullYear();
	var month = parseInt(today.getMonth()) + 1;
	month = (month < 10) ? ("0".concat(month)) : month;
	var date = today.getDate();
	date = (date < 10) ? ("0".concat(date)) : date;
	var cDate = year+"-"+month+"-"+date;
	
	$("#queryDateBegin").val(cDate);
	$("#queryDateEnd").val(cDate);
	$("#queryTimeBegin").val("00:00");
	$("#queryTimeEnd").val("23:59");
	
	$("#queryType").change(function() {
		var option = $(this).val();
		
		if (option == "SYSTEM") {
			// 清空查詢欄位值，避免後續查詢因欄位不存在而報錯
			$('#querySrcIp').val("");
			$('#querySrcPort').val("");
			$('#queryDstIp').val("");
			$('#queryDstPort').val("");
			$('[data-ipPortSec]').hide();
		} else {
			$('[data-ipPortSec]').show();
		}
	});
});

function countDown(status) {
	if (status == 'START') {
		startTime = parseInt(_timeout) - 1;
		
		timer_start = performance.now();
		timer = setInterval(function () {
			$("#timeoutMsg").val("Timeout倒數 : " + startTime + " 秒");
			startTime--;
			
	    }, 1000);
		
	} else {
		timer_end = performance.now();
		var spent = (parseInt(timer_end) - parseInt(timer_start)) / 1000;
		$("#timeoutMsg").val("查詢花費時間 : " + spent + " 秒");
		
		clearInterval(timer);
	}
}

function bindScrollEvent() {
	$(".dataTables_scrollBody").scroll(function(e) {
		if ($(".dataTables_empty").length == 0) {
			var rowCount = $("#resultTable > tBody > tr").length;
			var scrollTop = $(this).prop("scrollTop");
			// 改用 scrollHeight + clientHeight 以支援大眾瀏覽器
			var scrollTopMax = $(this).prop("scrollHeight") - $(this).prop("clientHeight");
			
			if (scrollTop > lastScrollYPos) { //移動Y軸時才作動
				lastScrollYPos = scrollTop;

				if (rowCount >= pageLength) { //查詢結果筆數有超過分頁筆數才作動
					//if (scrollTop > (scrollTopMax - (scrollTopMax*0.3))) {
					//捲到最底才查找下一批資料
					if (scrollTop >= scrollTopMax) { 
						if (!waitForNextData) {
							waitForNextData = true;
							findNextData();
						}
					}
				}
			}
		}
	});
}

function unbindScrollEvent() {
	$(".dataTables_scrollBody").off("scroll");
}

//找下一批資料
function findNextData() {
	var sortIdx = -1;
	var sortStr;
	
	if ($(".dataTable > thead").find(".sorting_asc").length > 0) {
		sortIdx = $(".dataTable > thead").find(".sorting_asc").prop("cellIndex");
	} else if ($(".dataTable > thead").find(".sorting_desc").length > 0) {
		sortIdx = $(".dataTable > thead").find(".sorting_desc").prop("cellIndex");
	}
	
	var sortBy = $(".dataTable > thead > tr > th:eq(" + sortIdx + ")").attr("aria-sort");
	if (sortBy === "descending") {
		sortStr = "desc";
	} else {
		sortStr = "asc";
	}
	
	/*
	var dataObj = new Object();
	if ($('#queryFrom').val() == 'WEB') {
		dataObj.queryType = $("#queryType").val(),
		dataObj.queryDevName = $("#queryDevName").val(),
		dataObj.querySrcIp = $("#querySrcIp").val(),
		dataObj.querySrcPort = $("#querySrcPort").val(),
		dataObj.queryDstIp = $("#queryDstIp").val(),
		dataObj.queryDstPort = $("#queryDstPort").val(),
		dataObj.queryDateBegin = $("#queryDateBegin").val(),
		dataObj.queryDateEnd = $("#queryDateEnd").val(),
		dataObj.queryTimeBegin = $("#queryTimeBegin").val(),
		dataObj.queryTimeEnd = $("#queryTimeEnd").val()
	
	} else if ($('#queryFrom').val() == 'MOBILE') {
		dataObj.queryType = $("#queryTypeMobile").val(),
		dataObj.queryDevName = $("#queryDevNameMobile").val(),
		dataObj.querySrcIp = $("#query_SrcIp_mobile").val(),
		dataObj.querySrcPort = $("#query_SrcPort_mobile").val(),
		dataObj.queryDstIp = $("#query_DstIp_mobile").val(),
		dataObj.queryDstPort = $("#query_DstPort_mobile").val(),
		dataObj.queryDateBegin = $("#queryDateBegin_mobile").val(),
		dataObj.queryDateEnd = $("#queryDateEnd_mobile").val(),
		dataObj.queryTimeBegin = $("#queryTimeBegin").val(),
		dataObj.queryTimeEnd = $("#queryTimeEnd").val()
	}
	dataObj.start = startNum;
	dataObj.length = pageLength;
	dataObj.searchValue = $("#resultTable_filter").find("input").val(),
	dataObj.order[0][column] = sortIdx;
	dataObj.order[0][dir] = sortStr;
	*/
	
	$.ajax({
		url : _ctx + '/plugin/module/firewall/log/getFirewallLogData.json',
		data : {
			"queryType" : $("#queryType").val(),
			"queryDevName" : $("#queryDevName").val(),
			"querySrcIp" : $("#querySrcIp").val(),
			"querySrcPort" : $("#querySrcPort").val(),
			"queryDstIp" : $("#queryDstIp").val(),
			"queryDstPort" : $("#queryDstPort").val(),
			"queryDateBegin" : $("#queryDateBegin").val(),
			"queryDateEnd" : $("#queryDateEnd").val(),
			"queryTimeBegin" : $("#queryTimeBegin").val(),
			"queryTimeEnd" : $("#queryTimeEnd").val(),
			"start" : startNum,
			"length" : pageLength,
			"searchValue" : $("#resultTable_filter").find("input").val(),
			"order[0][column]" : sortIdx,
			"order[0][dir]" : sortStr
		},
		type : "POST",
		dataType : 'json',
		async: true,
		beforeSend : function(xhr) {
			countDown('START');
			showProcessing();
		},
		complete : function() {
			countDown('STOP');
			hideProcessing();
			waitForNextData = false;
		},
		initComplete : function(settings, json) {
			if (json.msg != null) {
				$(".myTableSection").hide();
				alert(json.msg);
			}
        },
		success : function(resp) {
			var count = resp.data.length;
			if (count > 0) {
				addRow(resp.data);
				startNum += pageLength;
			}
			
			var sNum, eNum;
			if ($(".dataTables_empty").length == 0) { //有查到資料
				sNum = 1;
				eNum = $("#resultTable > tBody > tr").length;
			} else {
				sNum = 0;
				eNum = 0;
			}
			
			$("#current_count").text('顯示第 ' + sNum + ' 至 ' + eNum + ' 項結果，共');
		},
		error : function(xhr, ajaxOptions, thrownError) {
			$("#current_count").text('<ERROR>');
			ajaxErrorHandler();
		}
	});
}

function addRow(dataList) {
	for (var i=0; i<dataList.length; i++) {
		var data = dataList[i];
		var rowCount = $("#resultTable > tBody > tr").length;
		var cTR = $("#resultTable > tbody > tr:eq(0)").clone();
		$(cTR).find("td:eq(0)").html( ++rowCount );
		$(cTR).find("td:eq(1)").html( data.type );
		$(cTR).find("td:eq(2)").html( data.devName );
		$(cTR).find("td:eq(3)").html( data.dateStr );
		$(cTR).find("td:eq(4)").html( data.timeStr );
		$(cTR).find("td:eq(5)").html( data.severity );
		$(cTR).find("td:eq(6)").html( data.srcIp);
		$(cTR).find("td:eq(7)").html( data.srcPort );
		$(cTR).find("td:eq(8)").html( data.srcCountry );
		$(cTR).find("td:eq(9)").html( data.dstIp );
		$(cTR).find("td:eq(10)").html( data.dstPort );
		$(cTR).find("td:eq(11)").html( data.proto );
		$(cTR).find("td:eq(12)").html( data.service );
		$(cTR).find("td:eq(13)").html( data.url );
		$(cTR).find("td:eq(14)").html( data.app );
		$(cTR).find("td:eq(15)").html( data.action );
		$(cTR).find("td:eq(16)").html( data.sentByte );
		$(cTR).find("td:eq(17)").html( data.rcvdByte );
		$(cTR).find("td:eq(18)").html( data.utmAction );
		$(cTR).find("td:eq(19)").html( data.level );
		$(cTR).find("td:eq(20)").html( data.user );
		$(cTR).find("td:eq(21)").html( data.message );
		$(cTR).find("td:eq(22)").html( data.attack );
		$("#resultTable > tbody").append($(cTR));
	}
	$.fn.dataTable.tables( { visible: true, api: true } ).columns.adjust();
}

function getTotalFilteredCount() {
	var dataObj = new Object();
	if ($('#queryFrom').val() == 'WEB') {
		dataObj.queryType = $("#queryType").val(),
		dataObj.queryDevName = $("#queryDevName").val(),
		dataObj.querySrcIp = $("#querySrcIp").val(),
		dataObj.querySrcPort = $("#querySrcPort").val(),
		dataObj.queryDstIp = $("#queryDstIp").val(),
		dataObj.queryDstPort = $("#queryDstPort").val(),
		dataObj.queryDateBegin = $("#queryDateBegin").val(),
		dataObj.queryDateEnd = $("#queryDateEnd").val(),
		dataObj.queryTimeBegin = $("#queryTimeBegin").val(),
		dataObj.queryTimeEnd = $("#queryTimeEnd").val()
	
	} else if ($('#queryFrom').val() == 'MOBILE') {
		dataObj.queryType = $("#queryTypeMobile").val(),
		dataObj.queryDevName = $("#queryDevNameMobile").val(),
		dataObj.querySrcIp = $("#query_SrcIp_mobile").val(),
		dataObj.querySrcPort = $("#query_SrcPort_mobile").val(),
		dataObj.queryDstIp = $("#query_DstIp_mobile").val(),
		dataObj.queryDstPort = $("#query_DstPort_mobile").val(),
		dataObj.queryDateBegin = $("#queryDateBegin_mobile").val(),
		dataObj.queryDateEnd = $("#queryDateEnd_mobile").val(),
		dataObj.queryTimeBegin = $("#queryTimeBegin").val(),
		dataObj.queryTimeEnd = $("#queryTimeEnd").val()
	}
	
	$.ajax({
		url : _ctx + '/plugin/module/firewall/log/getFirewallLogCount.json',
		data : dataObj,
		type : "POST",
		dataType : 'json',
		async: true,
		beforeSend : function(xhr) {
			//resultTable_info  顯示第 0 至 0 項結果，共 0 項
			var sNum, eNum;
			if ($(".dataTables_empty").length == 0) { //有查到資料
				sNum = 1;
				eNum = $("#resultTable > tBody > tr").length;
			} else {
				sNum = 0;
				eNum = 0;
			}
			$("#resultTable_info").html(
				'<div class="row" style="padding-left: 15px;">' +
					'<div id="current_count">顯示第 ' + sNum + ' 至 ' + eNum + ' 項結果，共</div>' +
				    '<div id="total_count">' +
				       '<img id="searchWaiting2" class="img_searchWaiting" alt="loading..." src="/resources/images/Processing_4.gif">' +
				    '</div>' +
				    '<div style="">筆</div>' +
				'</div>'
			);
		},
		complete : function() {
			//hideProcessing();
			$("#searchWaiting2").hide();
		},
		initComplete : function(settings, json) {
        },
		success : function(resp) {
			var count = resp.data.FILTERED_COUNT;
			$("#total_count").html('&nbsp;' + count + '&nbsp;');
		},
		error : function(xhr, ajaxOptions, thrownError) {
			//ajaxErrorHandler();
			$("#total_count").html('&nbsp;N/A&nbsp;');
		}
	});
}

//查詢按鈕動作
function findData(from) {
	$('#queryFrom').val(from);
	
	/*
	 * 類別改為可以選ALL，不檢核
	if ($("#queryType").val().trim().length == 0) {
		alert(msg_chooseType);
		return;
	}
	*/
	if ($("#queryDateBegin").val().trim().length == 0) {
		alert(msg_chooseDate);
		return;
	}
	if ($("#queryTimeBegin").val().trim().length == 0 || $("#queryTimeEnd").val().trim().length == 0) {
		alert(msg_chooseTime);
		return;
	}
	
	if (from == 'MOBILE') {
		$('#collapseExample').collapse('hide');
	}
	
	startNum = 0;
	if (typeof resultTable !== "undefined") {
		$(".dataTables_scrollBody").scrollTop(0);
		resultTable.ajax.reload();
		$(".myTableSection").show();
		
	} else {
		$(".myTableSection").show();
		/*$('[data-field]').hide();*/
		
		resultTable = $('#resultTable').DataTable(
		{
			"autoWidth" 	: true,
			"paging" 		: false,
			"bFilter" 		: true,
			"ordering" 		: true,
			"info" 			: true,
			"serverSide" 	: true,
			"bLengthChange" : true,
			"pagingType" 	: "full",
			"processing" 	: true,
			"scrollX"		: true,
			"scrollY"		: dataTableHeight,
			"scrollCollapse": true,
			"pageLength"	: pageLength,
			"language" : {
	    		"url" : _ctx + "/resources/js/dataTable/i18n/Chinese-traditional.json"
	        },
	        "createdRow": function( row, data, dataIndex ) {
	        },
			"ajax" : {
				"url" : _ctx + '/plugin/module/firewall/log/getFirewallLogData.json',
				"type" : 'POST',
				"data" : function ( d ) {
					if ($('#queryFrom').val() == 'WEB') {
						d.queryType = $("#queryType").val(),
						d.queryDevName = $("#queryDevName").val(),
						d.querySrcIp = $("#querySrcIp").val(),
						d.querySrcPort = $("#querySrcPort").val(),
						d.queryDstIp = $("#queryDstIp").val(),
						d.queryDstPort = $("#queryDstPort").val(),
						d.queryDateBegin = $("#queryDateBegin").val(),
						d.queryDateEnd = $("#queryDateEnd").val(),
						d.queryTimeBegin = $("#queryTimeBegin").val(),
						d.queryTimeEnd = $("#queryTimeEnd").val()
					
					} else if ($('#queryFrom').val() == 'MOBILE') {
						d.queryType = $("#queryTypeMobile").val(),
						d.queryDevName = $("#queryDevNameMobile").val(),
						d.querySrcIp = $("#query_SrcIp_mobile").val(),
						d.querySrcPort = $("#query_SrcPort_mobile").val(),
						d.queryDstIp = $("#query_DstIp_mobile").val(),
						d.queryDstPort = $("#query_DstPort_mobile").val(),
						d.queryDateBegin = $("#queryDateBegin_mobile").val(),
						d.queryDateEnd = $("#queryDateEnd_mobile").val(),
						d.queryTimeBegin = $("#queryTimeBegin").val(),
						d.queryTimeEnd = $("#queryTimeEnd").val()
					}
					d.start = 0; //初始查詢一律從第0筆開始
					d.length = pageLength;
					return d;
				},
				beforeSend : function() {
					countDown('START');
				},
				complete : function() {
					countDown('STOP');
				},
				"error" : function(xhr, ajaxOptions, thrownError) {
					ajaxErrorHandler();
				},
				"dataSrc" : function(json) {
					if (json.data.length > 0) {
						if (json.otherMsg != null && json.otherMsg != "") {
							$("#div_TotalFlow").css("display", "contents");
							//$("#result_TotalFlow").text("總流量：" + json.otherMsg);
						}
						
					} else {
						$("#div_TotalFlow").css("display", "contents");
						$("#result_TotalFlow").text("查無符合資料");
					}
					return json.data;
				},
				"timeout" : parseInt(_timeout) * 1000 //設定60秒Timeout
			},
			"order": [[3 , 'desc' ]],
			"initComplete": function(settings, json) {
				if (json.msg != null) {
					$(".myTableSection").hide();
					alert(json.msg);
				}
				bindScrollEvent();
            },
			"drawCallback" : function(settings) {
				//$.fn.dataTable.tables( { visible: true, api: true } ).columns.adjust();
				$("div.dataTables_length").parent().removeClass('col-sm-12');
				$("div.dataTables_length").parent().addClass('col-sm-6');
				$("div.dataTables_filter").parent().removeClass('col-sm-12');
				$("div.dataTables_filter").parent().addClass('col-sm-6');
				
				$("div.dataTables_info").parent().removeClass('col-sm-12');
				$("div.dataTables_info").parent().addClass('col-sm-6');
				$("div.dataTables_paginate").parent().removeClass('col-sm-12');
				$("div.dataTables_paginate").parent().addClass('col-sm-6');
				
				/*
				$('[data-field]').hide();
				*/
				$('[data-field]').removeClass('td-na');
				var queryType = $("#queryType").val();
				if (queryType == "APP") {
					/*
					$('[data-field="srcIp"]').show();
					$('[data-field="srcPort"]').show();
					$('[data-field="dstIp"]').show();
					$('[data-field="dstPort"]').show();
					$('[data-field="proto"]').show();
					$('[data-field="app"]').show();
					$('[data-field="action"]').show();
					
					$('[data-field="severity"]').hide();
					$('[data-field="srcCountry"]').hide();
					$('[data-field="service"]').hide();
					$('[data-field="url"]').hide();
					$('[data-field="sentByte"]').hide();
					$('[data-field="rcvdByte"]').hide();
					$('[data-field="utmAction"]').hide();
					$('[data-field="level"]').hide();
					$('[data-field="user"]').hide();
					$('[data-field="message"]').hide();
					$('[data-field="attack"]').hide();
					*/
					$('[data-field="severity"]').eq(0).addClass('td-na');
					$('[data-field="srcCountry"]').eq(0).addClass('td-na');
					$('[data-field="service"]').eq(0).addClass('td-na');
					$('[data-field="url"]').eq(0).addClass('td-na');
					$('[data-field="sentByte"]').eq(0).addClass('td-na');
					$('[data-field="rcvdByte"]').eq(0).addClass('td-na');
					$('[data-field="utmAction"]').eq(0).addClass('td-na');
					$('[data-field="level"]').eq(0).addClass('td-na');
					$('[data-field="user"]').eq(0).addClass('td-na');
					$('[data-field="message"]').eq(0).addClass('td-na');
					$('[data-field="attack"]').eq(0).addClass('td-na');
					
				} else if (queryType == "FORWARDING") {
					/*
					$('[data-field="srcIp"]').show();
					$('[data-field="srcPort"]').show();
					$('[data-field="dstIp"]').show();
					$('[data-field="dstPort"]').show();
					$('[data-field="proto"]').show();
					$('[data-field="app"]').show();
					$('[data-field="action"]').show();
					$('[data-field="sentByte"]').show();
					$('[data-field="rcvdByte"]').show();
					$('[data-field="utmAction"]').show();
					*/
					
					$('[data-field="severity"]').eq(0).addClass('td-na');
					$('[data-field="srcCountry"]').eq(0).addClass('td-na');
					$('[data-field="service"]').eq(0).addClass('td-na');
					$('[data-field="url"]').eq(0).addClass('td-na');
					$('[data-field="level"]').eq(0).addClass('td-na');
					$('[data-field="user"]').eq(0).addClass('td-na');
					$('[data-field="message"]').eq(0).addClass('td-na');
					$('[data-field="attack"]').eq(0).addClass('td-na');
					
				} else if (queryType == "SYSTEM") {
					/*
					$('[data-field="level"]').show();
					$('[data-field="user"]').show();
					$('[data-field="message"]').show();
					*/
					
					$('[data-field="severity"]').eq(0).addClass('td-na');
					$('[data-field="srcIp"]').eq(0).addClass('td-na');
					$('[data-field="srcPort"]').eq(0).addClass('td-na');
					$('[data-field="srcCountry"]').eq(0).addClass('td-na');
					$('[data-field="dstIp"]').eq(0).addClass('td-na');
					$('[data-field="dstPort"]').eq(0).addClass('td-na');
					$('[data-field="proto"]').eq(0).addClass('td-na');
					$('[data-field="service"]').eq(0).addClass('td-na');
					$('[data-field="url"]').eq(0).addClass('td-na');
					$('[data-field="app"]').eq(0).addClass('td-na');
					$('[data-field="action"]').eq(0).addClass('td-na');
					$('[data-field="sentByte"]').eq(0).addClass('td-na');
					$('[data-field="rcvdByte"]').eq(0).addClass('td-na');
					$('[data-field="utmAction"]').eq(0).addClass('td-na');
					$('[data-field="attack"]').eq(0).addClass('td-na');
					
				} else if (queryType == "INTRUSION") {
					/*
					$('[data-field="severity"]').show();
					$('[data-field="srcIp"]').show();
					$('[data-field="srcPort"]').show();
					$('[data-field="srcCountry"]').show();
					$('[data-field="dstIp"]').show();
					$('[data-field="dstPort"]').show();
					$('[data-field="proto"]').show();
					$('[data-field="attack"]').show();
					$('[data-field="action"]').show();
					*/
					
					$('[data-field="service"]').eq(0).addClass('td-na');
					$('[data-field="url"]').eq(0).addClass('td-na');
					$('[data-field="app"]').eq(0).addClass('td-na');
					$('[data-field="sentByte"]').eq(0).addClass('td-na');
					$('[data-field="rcvdByte"]').eq(0).addClass('td-na');
					$('[data-field="utmAction"]').eq(0).addClass('td-na');
					$('[data-field="level"]').eq(0).addClass('td-na');
					$('[data-field="user"]').eq(0).addClass('td-na');
					$('[data-field="message"]').eq(0).addClass('td-na');
					
				} else if (queryType == "WEBFILTER") {
					/*
					$('[data-field="srcIp"]').show();
					$('[data-field="srcPort"]').show();
					$('[data-field="dstIp"]').show();
					$('[data-field="dstPort"]').show();
					$('[data-field="proto"]').show();
					$('[data-field="service"]').show();
					$('[data-field="action"]').show();
					$('[data-field="url"]').show();
					$('[data-field="sentByte"]').show();
					$('[data-field="rcvdByte"]').show();
					*/
					
					$('[data-field="severity"]').eq(0).addClass('td-na');
					$('[data-field="srcCountry"]').eq(0).addClass('td-na');
					$('[data-field="app"]').eq(0).addClass('td-na');
					$('[data-field="utmAction"]').eq(0).addClass('td-na');
					$('[data-field="level"]').eq(0).addClass('td-na');
					$('[data-field="user"]').eq(0).addClass('td-na');
					$('[data-field="message"]').eq(0).addClass('td-na');
					$('[data-field="attack"]').eq(0).addClass('td-na');
				}
				
				$.fn.dataTable.tables( { visible: true, api: true } ).columns.adjust();
				startNum = pageLength; //初始查詢完成後startNum固定為pageLength大小
				lastScrollYPos = $(".dataTables_scrollBody").prop("scrollTop");
				getTotalFilteredCount();
				bindTrEvent();
			},
			"rowCallback": function( row, data ) {
				$('td:eq(4)', row).attr('data-field', 'severity');
				$('td:eq(5)', row).attr('data-field', 'srcIp');
				$('td:eq(6)', row).attr('data-field', 'srcPort');
				$('td:eq(7)', row).attr('data-field', 'srcCountry');
				$('td:eq(8)', row).attr('data-field', 'dstIp');
				$('td:eq(9)', row).attr('data-field', 'dstPort');
				$('td:eq(10)', row).attr('data-field', 'proto');
				$('td:eq(11)', row).attr('data-field', 'service');
				$('td:eq(12)', row).attr('data-field', 'url');
				$('td:eq(13)', row).attr('data-field', 'app');
				$('td:eq(14)', row).attr('data-field', 'action');
				$('td:eq(15)', row).attr('data-field', 'sentByte');
				$('td:eq(16)', row).attr('data-field', 'rcvdByte');
				$('td:eq(17)', row).attr('data-field', 'utmAction');
				$('td:eq(18)', row).attr('data-field', 'level');
				$('td:eq(19)', row).attr('data-field', 'user');
				$('td:eq(20)', row).attr('data-field', 'message');
				$('td:eq(21)', row).attr('data-field', 'attack');
			},
			"columns" : [
				{},
				{ "data" : "type" },
				{ "data" : "devName" },
				{ "data" : "dateStr" },
				{ "data" : "timeStr" },
				{ "data" : "severity" },	//severity
				{ "data" : "srcIp" },		//srcIp
				{ "data" : "srcPort" },		//srcPort
				{ "data" : "srcCountry" },	//srcCountry
				{ "data" : "dstIp" },		//dstIp
				{ "data" : "dstPort" },		//dstPort
				{ "data" : "proto" },		//proto
				{ "data" : "service" },		//service
				{ "data" : "url" },			//url
				{ "data" : "app" },			//app
				{ "data" : "action" },		//action
				{ "data" : "sentByte" , "className" : "right" },	//sentByte
				{ "data" : "rcvdByte" , "className" : "right" },	//rcvdByte
				{ "data" : "utmAction" },	//utmAction
				{ "data" : "level" },		//level
				{ "data" : "user" },		//user
				{ "data" : "message" },		//message
				{ "data" : "attack" }		//attack
			],
			"columnDefs" : [
				{
					"targets" : [0],
					"className" : "center",
					"searchable": false,
					"orderable": false,
					"render": function (data, type, row, meta) {
						       	return meta.row + meta.settings._iDisplayStart + 1;
						   	}
				}
			],
		});
	}
}