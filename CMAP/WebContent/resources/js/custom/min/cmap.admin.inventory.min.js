
var startNum, pageLength;
$(document).ready(function() {
	initMenuStatus("toggleMenu_admin", "toggleMenu_admin_items", "bk_inventory");
	
	startNum = 0;
	pageLength = Number($("#pageLength").val());	

//	var today = new Date();
//	var year = today.getFullYear();
//	var month = parseInt(today.getMonth()) + 1;
//	month = (month < 10) ? ("0".concat(month)) : month;
//	var date = today.getDate();
//	date = (date < 10) ? ("0".concat(date)) : date;
//	var cDate = year+"-"+month+"-"+date;
//	
//	$("#queryDateBegin").val(cDate);
//	$("#queryDateEnd").val(cDate);
//	$("#queryTimeBegin").val("00:00");
//	$("#queryTimeEnd").val("23:59");
//	
});


//查詢按鈕動作
function findData(from) {
	$('#queryFrom').val(from);
	
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
			"bFilter" 		: false,
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
				"url" : _ctx + '/admin/inventory/getInventoryInfoData.json',
				"type" : 'POST',
				"data" : function ( d ) {
					if ($('#queryFrom').val() == 'WEB') {
						d.queryProbe = $("#queryProbe").val(),
						d.queryDeviceName = $("#queryDeviceName").val(),
						d.queryDeviceType = $("#queryDeviceType").val(),
						d.queryBrand = $("#queryBrand").val(),
						d.queryModel = $("#queryModel").val();
					
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
				"timeout" : parseInt(_timeout) * 1000 //設定60秒Timeout
			},
			"order": [[5 , 'acs' ]],
			"initComplete": function(settings, json) {
				if (json.msg != null) {
					$(".myTableSection").hide();
					alert(json.msg);
				}
				bindScrollEvent();
          },
			"drawCallback" : function(settings) {
				//搜尋
				$("div.dataTables_filter").parent().removeClass('col-sm-12');
				$("div.dataTables_filter").parent().addClass('col-sm-6');
				
				//資料筆數
				$("div.dataTables_info").parent().removeClass('col-sm-12');
				$("div.dataTables_info").parent().addClass('col-sm-6');
				
				startNum = pageLength; //初始查詢完成後startNum固定為pageLength大小
				lastScrollYPos = $(".dataTables_scrollBody").prop("scrollTop");
				$("#resultTable_filter").find("input").prop("placeholder","(模糊查詢速度較慢)")
//				getTotalTraffic();
//				getTotalFilteredCount();
				bindTrEvent();
			},
			"columns" : [
				{},
				{ "data" : "deviceId" , "orderable" : false },
				{ "data" : "probe" , "orderable" : false },
				{ "data" : "group" , "orderable" : false },
				{ "data" : "deviceName" , "orderable" : false },
				{ "data" : "deviceIp" , "orderable" : false },
				{ "data" : "deviceType" , "orderable" : false },
				{ "data" : "brand" , "orderable" : false },
				{ "data" : "model" , "orderable" : false },
				{ "data" : "systemVersion" , "orderable" : false },
				{ "data" : "serialNumber" , "orderable" : false },
				{ "data" : "manufactureDate" , "orderable" : false }
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

//[資料匯出]Modal >> 匯出確認按鈕事件 (由cmap.main.js呼叫)
function doDataExport(exportRecordCount) {
	var dataObj = new Object();
	if ($('#queryFrom').val() == 'WEB') {
		dataObj.queryProbe = $("#queryProbe").val(),
		dataObj.queryDeviceName = $("#queryDeviceName").val(),
		dataObj.queryDeviceType = $("#queryDeviceType").val(),
		dataObj.queryBrand = $("#queryBrand").val(),
		dataObj.queryModel = $("#queryModel").val();
		
	}
	
	dataObj.start = 0; //初始查詢一律從第0筆開始
	dataObj.length = pageLength;
	dataObj.exportRecordCount = exportRecordCount;
	
	$.ajax({
		url : _ctx + '/admin/inventory/dataExport.json',
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