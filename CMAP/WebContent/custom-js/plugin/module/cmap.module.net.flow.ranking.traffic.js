/**
 * 
 */

var timer, startTime, timer_start, timer_end;

$(document).ready(function() {
	var pathname = window.location.pathname;
	var lastPath = pathname.substring(pathname.lastIndexOf('/'), pathname.length);
	if (lastPath === "/all") {
		initMenuStatus("toggleMenu_prtg", "toggleMenu_prtg_items", "mp_netFlowCurrentRanking_traffic_all");
	} else {
		initMenuStatus("toggleMenu_prtg", "toggleMenu_prtg_items", "mp_netFlowCurrentRanking_traffic");
	}
	// 宣告按鈕函式功能
	// btn set queryDate period
	$("#btn_1d_web").click(function() {
			var dateBegin = new Date();
			var dateEnd = new Date();
			var datePeriod = 1; // 日期區間
			dateBegin.setDate(dateBegin.getDate() - datePeriod + 1);
			$("#queryDateBegin").val(dateBegin.format("yyyy-MM-dd"));
			$("#queryDateEnd").val(dateEnd.format("yyyy-MM-dd"));
	});
	$("#btn_3d_web").click(function() {
		var dateBegin = new Date();
		var dateEnd = new Date();
		var datePeriod = 3; // 日期區間
		dateBegin.setDate(dateBegin.getDate() - datePeriod + 1);
		
		$("#queryDateBegin").val(dateBegin.format("yyyy-MM-dd"));
		$("#queryDateEnd").val(dateEnd.format("yyyy-MM-dd"));
	});
	$("#btn_7d_web").click(function() {
		var dateBegin = new Date();
		var dateEnd = new Date();
		var datePeriod = 7; // 日期區間
		dateBegin.setDate(dateBegin.getDate() - datePeriod + 1);
		
		$("#queryDateBegin").val(dateBegin.format("yyyy-MM-dd"));
		$("#queryDateEnd").val(dateEnd.format("yyyy-MM-dd"));
	});
	//btn set queryDate period (mobile)
	$("#btn_1d_mobile").click(function() {
		var dateBegin = new Date();
		var dateEnd = new Date();
		var datePeriod = 1; // 日期區間
		dateBegin.setDate(dateBegin.getDate() - datePeriod + 1);
		$("#queryDateBegin_mobile").val(dateBegin.format("yyyy-MM-dd"));
		$("#queryDateEnd_mobile").val(dateEnd.format("yyyy-MM-dd"));
	});
	$("#btn_3d_mobile").click(function() {
		var dateBegin = new Date();
		var dateEnd = new Date();
		var datePeriod = 3; // 日期區間
		dateBegin.setDate(dateBegin.getDate() - datePeriod + 1);
	
		$("#queryDateBegin_mobile").val(dateBegin.format("yyyy-MM-dd"));
		$("#queryDateEnd_mobile").val(dateEnd.format("yyyy-MM-dd"));
	});
	$("#btn_7d_mobile").click(function() {
		var dateBegin = new Date();
		var dateEnd = new Date();
		var datePeriod = 7; // 日期區間
		dateBegin.setDate(dateBegin.getDate() - datePeriod + 1);
	
		$("#queryDateBegin_mobile").val(dateBegin.format("yyyy-MM-dd"));
		$("#queryDateEnd_mobile").val(dateEnd.format("yyyy-MM-dd"));
	});
	// 初始化預設值欄位
	var dateBegin = new Date();
	var dateEnd = new Date();
	var datePeriod = 1; // 日期區間
	dateBegin.setDate(dateBegin.getDate() - datePeriod + 1);
	$("#queryDateBegin").val(dateBegin.format("yyyy-MM-dd"));
	$("#queryDateEnd").val(dateEnd.format("yyyy-MM-dd"));
	$("#queryDateBegin_mobile").val(dateBegin.format("yyyy-MM-dd"));
	$("#queryDateEnd_mobile").val(dateEnd.format("yyyy-MM-dd"));
	// 宣告input欄位檢核
	var inputDateBegin = new Cleave('.input-date-begin', {
	    date: true,
	    delimiter: '-',
	    datePattern: ['Y', 'm', 'd']
	});
	var inputDateEnd = new Cleave('.input-date-end', {
	    date: true,
	    delimiter: '-',
	    datePattern: ['Y', 'm', 'd']
	});
	var inputDateBeginMobile = new Cleave('.input-date-begin-mobile', {
	    date: true,
	    delimiter: '-',
	    datePattern: ['Y', 'm', 'd']
	});
	var inputDateEndMobile = new Cleave('.input-date-end-mobile', {
	    date: true,
	    delimiter: '-',
	    datePattern: ['Y', 'm', 'd']
	});
});

//[資料匯出]Modal >> 匯出確認按鈕事件 (由cmap.main.js呼叫)
function doDataExport(exportRecordCount) {
	// 確認日期區間必填
	if ($("#queryDateBegin").val().trim().length == 0 || $("#queryDateEnd").val().trim().length == 0){
		alert(msg_chooseDate);
		return;
	}
	var dataObj = new Object();
	//Mobile不支援Export匯出
	dataObj.queryGroup = $("#queryGroup").val();
	dataObj.queryDateBegin = $("#queryDateBegin").val();
	dataObj.queryDateEnd = $("#queryDateEnd").val();
	dataObj.exportRecordCount = exportRecordCount;
	
	$.ajax({
		url : _ctx + '/plugin/module/netFlow/ranking/trafficDataExport.json',
		data : dataObj,
		type : "POST",
		dataType : 'json',
		async: true,
		beforeSend : function(xhr) {
			showProcessing();
		},
		complete : function() {
			hideProcessing();
		},
		initComplete : function(settings, json) {
        },
		success : function(resp) {
			if (resp.code == 200) {
				const fileId = resp.data.fileId;
				const url = getResourceDownloadLink(fileId);
				// 彈出下載視窗
				location.href = url;
				// 關閉Modal視窗
				closeExportPanel();
				
			} else {
				alert(resp.message);
			}
		},
		error : function(xhr, ajaxOptions, thrownError) {
			ajaxErrorHandler();
		}
	});
}

//查詢按鈕動作
function findData(from) {
	//接收form參數值
	$('#queryFrom').val(from);
	// 確認日期區間必填
	if ($("#queryDateBegin").val().trim().length == 0 || $("#queryDateEnd").val().trim().length == 0){
		alert(msg_chooseDate);
		return;
	}
	
	if (from == 'MOBILE') {
		$('#collapseExample').collapse('hide');
	}
	
	if (typeof resultTable !== "undefined") {
		resultTable.ajax.reload();
		$(".myTableSection").show();
		
	} else {
		$(".myTableSection").show();
		
		resultTable = $('#resultTable').DataTable(
		{
			"autoWidth" 	: true,
			"paging" 		: true,
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
			"pageLength"	: 100,
			"language" : {
	    		"url" : _ctx + "/resources/js/dataTable/i18n/Chinese-traditional.json"
	        },
	        "createdRow": function( row, data, dataIndex ) {
	        },
			"ajax" : {
				"url" : _ctx + '/plugin/module/netFlow/ranking/getNetFlowRankingTrafficData.json',
				"type" : 'POST',
				"data" : function ( d ) {
					if ($('#queryFrom').val() == 'WEB') {
						d.queryGroup = $("#queryGroup").val();
						d.queryDateBegin = $("#queryDateBegin").val();
						d.queryDateEnd = $("#queryDateEnd").val();
					} else if ($('#queryFrom').val() == 'MOBILE') {
						d.queryGroup = $("#queryGroup_mobile").val();
						d.queryDateBegin = $("#queryDateBegin_mobile").val();
						d.queryDateEnd = $("#queryDateEnd_mobile").val();
					}
					return d;
				},
				beforeSend : function() {
					//countDown('START');
				},
				complete : function() {
					//countDown('STOP');
				},
				"error" : function(xhr, ajaxOptions, thrownError) {
					ajaxErrorHandler();
				},
				"timeout" : parseInt(_timeout) * 1000 //設定60秒Timeout
			},
			"order": [[4 , 'desc' ]],
			"initComplete": function(settings, json) {
				if (json.msg != null) {
					$(".myTableSection").hide();
					alert(json.msg);
				}
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
				
				if ($("#resultTable > tbody > tr").length > 1) {
					$("#resultTable > tbody > tr:eq(0)").addClass("summary-tr");
				}
				bindTrEvent();
			},
			"columns" : [
				{},
				{ "data" : "ipAddress" },
				{ "data" : "ipDesc" },
				{ "data" : "groupName" , "searchable" : false },
				{ "data" : "percent" , "searchable" : false },
				{ "data" : "totalTraffic" , "searchable" : false },
				{ "data" : "uploadTraffic" , "searchable" : false },
				{ "data" : "downloadTraffic" , "searchable" : false }
			],
			"columnDefs" : [
				{
					"targets" : [0],
					"className" : "center",
					"searchable": false,
					"orderable": false,
					"render": function (data, type, row, meta) {
								if (meta.row == 0) {
									return "";
								} else {
									return meta.row + meta.settings._iDisplayStart;
								}
						   	}
				}
			],
		});
	}
}