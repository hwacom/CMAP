/**
 * 
 */

var timer, startTime, timer_start, timer_end;
var waitForNextData = false;
var startNum, pageLength;
var lastScrollYPos = 0;

$(document).ready(function() {
	initMenuStatus("toggleMenu_prtg", "toggleMenu_prtg_items", "cm_wifi");
	
	startNum = 0;
	pageLength = Number($("#pageLength").val());

	var inputPort = new Cleave('.input-port', {
		numericOnly: true,
		blocks: [5]
	});
	
	$("#resultTable").on('xhr.dt', function ( e, settings, json, xhr ) {
		if (json.msg != null) {
			$(".myTableSection").hide();
			alert(json.msg);
		}
	});
	
	//Query condition fields binding auto-trim function while onBlur event
	//$('#queryGroup').unbind('blur').bind('blur',function(){
    //   $(this).val($(this).val().trim());
    //});
	$('#query_ClientMac').unbind('blur').bind('blur',function(){
        $(this).val($(this).val().trim());
    });
	$('#query_ClientIp').unbind('blur').bind('blur',function(){
		$(this).val($(this).val().trim());
	});
	$('#query_ApName').unbind('blur').bind('blur',function(){
		$(this).val($(this).val().trim());
	});
	$('#query_Ssid').unbind('blur').bind('blur',function(){
		$(this).val($(this).val().trim());
	});
	
	var today = new Date();
	var year = today.getFullYear();
	var month = parseInt(today.getMonth()) + 1;
	month = (month < 10) ? ("0".concat(month)) : month;
	var date = today.getDate();
	date = (date < 10) ? ("0".concat(date)) : date;
	
	$("#query_DateBegin").val(year+"-"+month+"-"+date);
	$("#query_DateEnd").val(year+"-"+month+"-"+date);
	$("#query_TimeBegin").val("00:00");
	$("#query_TimeEnd").val("23:59");
});

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
					if (scrollTop >= ( scrollTopMax - 100 )) { // scrollTopMax - 100 確保解析度問題導致 scrollTop 達不到 scrollTopMax 位置
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

function addRow(dataList) {
	for (var i=0; i<dataList.length; i++) {
		var data = dataList[i];
		var rowCount = $("#resultTable > tBody > tr").length;
		var cTR = $("#resultTable > tbody > tr:eq(0)").clone();
		$(cTR).find("td:eq(0)").html( ++rowCount );
		$(cTR).find("td:eq(1)").html( data.groupName );
		$(cTR).find("td:eq(2)").html( data.clientMac );
		$(cTR).find("td:eq(3)").html( data.startTime );
		$(cTR).find("td:eq(4)").html( data.endTime );
		$(cTR).find("td:eq(5)").html( data.clientIp );
		$(cTR).find("td:eq(6)").html( data.apName );
		$(cTR).find("td:eq(7)").html( data.ssid );
		$(cTR).find("td:eq(8)").html( data.totalTraffic );
		$(cTR).find("td:eq(9)").html( data.uploadTraffic );
		$(cTR).find("td:eq(10)").html( data.downloadTraffic );
		$(cTR).find("td:eq(11)").html( '<i class="fas fa-clipboard-list fa-2x" onclick="viewWifiDetail(' +'\''+data.groupName+'\',' +'\''+data.clientMac+'\',' +'\''+data.clientIp+'\',' +'\''+data.startTime+'\',' +'\''+data.endTime+'\'' + ')" ></i>' );
		$("#resultTable > tbody").append($(cTR));
	}
	$.fn.dataTable.tables( { visible: true, api: true } ).columns.adjust();
}

//取得查詢條件下總筆數
function getTotalFilteredCount() {
	$.ajax({
		url : _ctx + '/plugin/module/wifiPoller/getTotalFilteredCount.json',
		data : {
			"queryGroupId" : $("#queryGroup").val(),
			"queryDateBegin" : $("#query_DateBegin").val(),
			"queryDateEnd" : $("#query_DateEnd").val(),
			"queryTimeBegin" : $("#query_TimeBegin").val(),
			"queryTimeEnd" : $("#query_TimeEnd").val(),
			"queryClientMac" : $("#query_ClientMac").val(),
			"queryClientIp" : $("#query_ClientIp").val(),
			"queryApName" : $("#query_ApName").val(),
			"querySsid" : $("#query_Ssid").val()
		},
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

// 跳窗顯示WifiDetail資料
function viewWifiDetail(groupName, clientMac, clientIp, startTime, endTime) {

	var obj = new Object();
	obj.groupName = groupName;
	obj.clientMac = clientMac;
	obj.clientIp = clientIp;
	obj.startTime = startTime;
	obj.endTime = endTime
	
	$.ajax({
		url : _ctx + '/plugin/module/wifiPoller/getWifiDetailData.json',
		data : JSON.stringify(obj),
		headers: {
		    'Accept': 'application/json',
		    'Content-Type': 'application/json'
		},
		type : "POST",
		async: true,
		beforeSend : function() {
			showProcessing();
		},
		complete : function() {
			hideProcessing();
		},
		success : function(resp) {
			if (resp.code == '200') {
				$('#viewWifiDetailModal_groupName').parent().show();
				$('#viewWifiDetailModal_clientMac').parent().show();
				$('#viewWifiDetailModal_clientIp').parent().show();
				$('#viewWifiDetailModal_pollingTime').parent().show();
				//填入對應欄位值
				$('#viewWifiDetailModal_groupName').html(resp.data.groupName);
				$('#viewWifiDetailModal_clientMac').html(resp.data.clientMac);
				$('#viewWifiDetailModal_clientIp').html(resp.data.clientIp);
				$('#viewWifiDetailModal_pollingTime').html(resp.data.startTime +" ~ "+resp.data.endTime);
				var trafficChartHtml = 
				"<canvas id=\"canvasTraffic\" width=\"300\" height=\"100\"></canvas> \
				<script> \
				var ctxTraffic = document.getElementById(\'canvasTraffic\').getContext(\'2d\'); \
				var colorTraffic = Chart.helpers.color; \
				var configTraffic = { \
					type: \'line\', \
					data: { \
						datasets: [ \
						    { \
								label: \'Upload Traffic\', \
								borderColor: window.chartColors.red, \
								fill: false, \
								data: "+ resp.data.uploadTrafficDataList  +
							"}, { \
								label: \'Download Traffic\', \
								borderColor: window.chartColors.blue, \
								fill: false, \
								data: "+ resp.data.downloadTrafficDataList  +							
							"}, { \
								label: \'Total Traffic\', \
								borderColor: window.chartColors.green, \
								fill: false, \
								data: "+ resp.data.totalTrafficDataList  +							
							"} \
						] \
					}, \
					options: { \
						responsive: true, \
						title: { \
							display: true, \
							text: \"Wifi Traffic Throughput Chart\" \
						}, \
						scales: { \
							xAxes: [{ \
								type: \'time\', \
								display: true, \
								scaleLabel: { \
									display: true, \
									labelString: \'Polling時間\' \
								}, \
								ticks: { \
									major: { \
										fontStyle: \'bold\', \
										fontColor: \'#FF0000\' \
									} \
								} \
							}], \
							yAxes: [{ \
								display: true, \
								scaleLabel: { \
									display: true, \
									labelString: \'流量(Octets)\' \
								} \
							}] \
						} \
					} \
				}; \
				var chartTraffic = new Chart(ctxTraffic, configTraffic); \
				</script> \
				";
				$('#viewWifiDetailModal_trafficData').html(trafficChartHtml);
				var qualityChartHtml = 
					"<canvas id=\"canvasQuality\" width=\"300\" height=\"100\"></canvas> \
					<script> \
					var ctxQuality = document.getElementById(\'canvasQuality\').getContext(\'2d\'); \
					var colorQuality = Chart.helpers.color; \
					var configQuality = { \
						type: \'line\', \
						data: { \
							datasets: [ \
							    { \
									label: \'SNR\', \
									borderColor: window.chartColors.red, \
									fill: false, \
									data: "+ resp.data.snrDataList  +
								"}, { \
									label: \'Noise\', \
									borderColor: window.chartColors.blue, \
									fill: false, \
									data: "+ resp.data.noiseDataList  +							
								"}, { \
									label: \'RSSI\', \
									borderColor: window.chartColors.green, \
									fill: false, \
									data: "+ resp.data.rssiDataList  +							
								"} \
							] \
						}, \
						options: { \
							responsive: true, \
							title: { \
								display: true, \
								text: \"Wifi Signal Quality Chart\" \
							}, \
							scales: { \
								xAxes: [{ \
									type: \'time\', \
									display: true, \
									scaleLabel: { \
										display: true, \
										labelString: \'Polling時間\' \
									}, \
									ticks: { \
										major: { \
											fontStyle: \'bold\', \
											fontColor: \'#FF0000\' \
										} \
									} \
								}], \
								yAxes: [{ \
									display: true, \
									scaleLabel: { \
										display: true, \
										labelString: \'分貝毫瓦(dBm)\' \
									} \
								}] \
							} \
						} \
					}; \
					var chartQuality = new Chart(ctxQuality, configQuality); \
					</script> \
					";				
				$('#viewWifiDetailModal_qualityData').html(qualityChartHtml);
				$('#viewWifiDetailModal').modal('show');			
			} else {
				alert(resp.message);
			}
		},
		error : function(xhr, ajaxOptions, thrownError) {
			ajaxErrorHandler();
		}
	});
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

	$.ajax({
		url : _ctx + '/plugin/module/wifiPoller/getWifiMstData.json',
		data : {
			"queryDate" : $("#query_Date").val(),
			"queryTimeBegin" : $("#query_TimeBegin").val(),
			"queryTimeEnd" : $("#query_TimeEnd").val(),
			"queryClientMac" : $("#query_ClientMac").val(),
			"queryClientIp" : $("#query_ClientIp").val(),
			"queryApName" : $("#query_ApName").val(),
			"querySsid" : $("#query_Ssid").val(),
			"start" : startNum,
			"length" : pageLength,
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
			console.log("query success... count: " + count);
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

//查詢按鈕動作
function findData(from) {
	var chkQueryGroup;
	var chkQueryDevice;
	var chkQueryDateTimeBegin;
	var chkQueryDateTimeEnd;
	var chkQueryDateTime;
	var chkQueryClientIp;
	var chkQueryClientMac;
	
	$('#queryFrom').val(from);
	//確認Group篩選條件輸入狀態
	if ($("#queryGroup").val().trim().length != 0) {
		chkQueryGroup = 'Y';
		//alert(msg_chooseGroup);
		//	return;
	}else{
		chkQueryGroup = 'N';
	}
	//確認ClientIP篩選條件輸入狀態
	if ($("#query_ClientIp").val().trim().length != 0) {
		chkQueryClientIp = 'Y';
	}else{
		chkQueryClientIp = 'N';
	}
	//確認ClientMac篩選條件輸入狀態
	if ($("#query_ClientMac").val().trim().length != 0) {
		chkQueryClientMac = 'Y';
	}else{
		chkQueryClientMac = 'N';
	}
	if ($("#query_DateBegin").val().trim().length != 0 && $("#query_TimeBegin").val().trim().length != 0){
		chkQueryDateTimeBegin = 'Y';
	}else if ($("#query_DateBegin").val().trim().length == 0 && $("#query_TimeBegin").val().trim().length == 0){
		chkQueryDateTimeBegin = 'N';
	}else{
		alert(msg_chooseDate);
		return;
	}
	if ($("#query_DateEnd").val().trim().length != 0 && $("#query_TimeEnd").val().trim().length != 0){
		chkQueryDateTimeEnd = 'Y';
	}else if ($("#query_DateEnd").val().trim().length == 0 && $("#query_TimeEnd").val().trim().length == 0){
		chkQueryDateTimeEnd = 'N';
	}else{
		alert(msg_chooseDate);
		return;
	}	
	//確認日期條件輸入狀態	開始跟結束都沒輸入時才是時間篩選條件為空
	if( chkQueryDateTimeBegin=='N' && chkQueryDateTimeEnd=='N') {
		chkQueryDateTime='N';
	}else{
		chkQueryDateTime='Y'
	}
	//至少要輸入一種條件篩選
	if(chkQueryGroup=='N' && chkQueryClientIp=='N' && chkQueryClientMac=='N' && chkQueryDateTime=='N'){
		alert(msg_chooseOne);
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
		
		resultTable = $('#resultTable').DataTable(
		{
			"autoWidth" 	: true,
			"paging" 		: false,
			"bFilter" 		: true,
			"ordering" 		: true,
			"info" 			: true,
			"serverSide" 	: true, //false關閉server端排序
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
				"url" : _ctx + '/plugin/module/wifiPoller/getWifiMstData.json',
				"type" : 'POST',
				"data" : function ( d ) {
					if ($('#queryFrom').val() == 'WEB') {
						d.queryGroupId = $("#queryGroup").val(),
						d.queryDateBegin = $("#query_DateBegin").val(),
						d.queryDateEnd = $("#query_DateEnd").val(),
						d.queryTimeBegin = $("#query_TimeBegin").val(),
						d.queryTimeEnd = $("#query_TimeEnd").val(),
						d.queryClientMac = $("#query_ClientMac").val(),
						d.queryClientIp = $("#query_ClientIp").val(),
						d.queryApName = $("#query_ApName").val(),
						d.querySsid = $("#query_Ssid").val()	
					} else if ($('#queryFrom').val() == 'MOBILE') {
						//d.queryGroup = $("#queryGroup_mobile").val(),
						d.queryDate = $("#query_Date_mobile").val(),
						//d.queryTimeBegin = $("#query_TimeBegin_mobile").val(),
						//d.queryTimeEnd = $("#query_TimeEnd_mobile").val(),
						d.queryClientMac = $("#query_ClientMac_mobile").val(),
						d.queryClientIp = $("#query_ClientIp_mobile").val(),
						d.queryApName = $("#query_ApName_mobile").val(),
						d.querySsid = $("#query_Ssid_mobile").val()
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
				/*
				"dataSrc" : function(json) {
					if (json.data.length > 0) {
						if (json.otherMsg != null && json.otherMsg != "") {
							$("#div_TotalFlow").css("display", "contents");
							$("#result_TotalFlow").text("總流量：" + json.otherMsg);
						}
						
					} else {
						$("#div_TotalFlow").css("display", "contents");
						$("#result_TotalFlow").text("查無符合資料");
					}
					return json.data;
				},
				*/
				"timeout" : parseInt(_timeout) * 1000 //設定60秒Timeout
			},
			"order": [[ 2, 'desc' ]], //用startTime排序
			"initComplete": function(settings, json) {
				if (json.msg != null) {
					$(".myTableSection").hide();
					alert(json.msg);
				}
				bindScrollEvent();
            },
			"drawCallback" : function(settings) {
				//$.fn.dataTable.tables( { visible: true, api: true } ).columns.adjust();
				/*
				//分頁筆數選單
				$("div.dataTables_length").parent().removeClass('col-sm-12');
				$("div.dataTables_length").parent().addClass('col-sm-6');
				*/
				
				//搜尋
				/*
				$("div.dataTables_filter").parent().removeClass('col-sm-12');
				$("div.dataTables_filter").parent().addClass('col-sm-6');
				*/
				
				//資料筆數
				$("div.dataTables_info").parent().removeClass('col-sm-12');
				$("div.dataTables_info").parent().addClass('col-sm-6');
				
				/*
				//分頁按鈕
				$("div.dataTables_paginate").parent().removeClass('col-sm-12');
				$("div.dataTables_paginate").parent().addClass('col-sm-6');
				*/
				
				startNum = pageLength; //初始查詢完成後startNum固定為pageLength大小
				lastScrollYPos = $(".dataTables_scrollBody").prop("scrollTop");
				//$("#resultTable_filter").find("input").prop("placeholder","(模糊查詢速度較慢)")
				getTotalFilteredCount();
				bindTrEvent();
			},
			"columns" : [
				{},
				{ "data" : "groupName" , "orderable" : true },
				{ "data" : "clientMac" , "orderable" : true },
				{ "data" : "startTime", "orderable" : true },
				{ "data" : "endTime" , "orderable" : true},
				{ "data" : "clientIp" , "orderable" : true },
				{ "data" : "apName" , "orderable" : true },
				{ "data" : "ssid" , "orderable" : true },
				{ "data" : "totalTraffic", "orderable" : true },
				{ "data" : "uploadTraffic", "orderable" : true },
				{ "data" : "downloadTraffic", "orderable" : true },
				{}
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
				},
				{
					"targets" : [11],
					"className" : "center",
					"searchable": false,
					"orderable": false,
					"render" : function(data, type, row) {
									var html = '<i class="fas fa-clipboard-list fa-2x" onclick="viewWifiDetail('+'\''+row.groupName+'\',' +'\''+row.clientMac+'\',' +'\''+row.clientIp+'\','+'\''+row.startTime+'\',' +'\''+row.endTime+'\'' + ')" ></i>';
									return html;
							 }
				}
			],
		});
	}
}