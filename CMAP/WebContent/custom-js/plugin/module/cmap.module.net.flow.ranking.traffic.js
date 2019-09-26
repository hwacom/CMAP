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
	
	$("#btnSearch_1_web").click(function(e) {
		findData('WEB', 1);
	});
	$("#btnSearch_3_web").click(function(e) {
		findData('WEB', 3);
	});
	$("#btnSearch_7_web").click(function(e) {
		findData('WEB', 7);
	});
	$("#btnSearch_1_mobile").click(function(e) {
		findData('MOBILE', 1);
	});
	$("#btnSearch_3_mobile").click(function(e) {
		findData('MOBILE', 3);
	});
	$("#btnSearch_7_mobile").click(function(e) {
		findData('MOBILE', 7);
	});
	$("#btnExport_1d").click(function(e) {
		showExportPanelSelf(1);
	});
	$("#btnExport_3d").click(function(e) {
		showExportPanelSelf(3);
	});
	$("#btnExport_7d").click(function(e) {
		showExportPanelSelf(7);
	});
});

function showExportPanelSelf(day) {
	$("#dataExportModal").modal({
		backdrop : 'static'
	});
	
	$("#dataExportModal_recordCountSelect").val("").change();
	$("#dataExportModal_recordCountInput").val("");
	
	$("#dataExportModal_var1").val(day + "D");
}

//[資料匯出]Modal >> 匯出確認按鈕事件 (由cmap.main.js呼叫)
function doDataExport(exportRecordCount) {
	var var1 = $("#dataExportModal_var1").val();
	var queryDatePeriod = var1.replace("D", "");
	var dataObj = new Object();
	if ($('#queryFrom').val() == 'WEB') {
		dataObj.queryGroup = $("#queryGroup").val();
	} else if ($('#queryFrom').val() == 'MOBILE') {
		dataObj.queryGroup = $("#queryGroup_mobile").val();
	}
	dataObj.queryDatePeriod = queryDatePeriod;
	dataObj.var1 = var1;
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
function findData(from, period) {
	$('#queryFrom').val(from);
	$("#queryDatePeriod").val(period);
	
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
					} else if ($('#queryFrom').val() == 'MOBILE') {
						d.queryGroup = $("#queryGroup_mobile").val();
					}
					d.queryDatePeriod = $("#queryDatePeriod").val();
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