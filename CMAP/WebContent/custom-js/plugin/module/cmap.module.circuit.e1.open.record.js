/**
 * 
 */
var resultTable_OpenRecord;	//DataTable
var _navAndMenuAndFooterHeight = 0;
var _deductHeight = 0;
var blockedTableHeight;
var pageLength = 100;

$(document).ready(function() {
	initMenuStatus("toggleMenu_plugin", "toggleMenu_plugin_items", "cm_circuitE1OpenRecord");
	
//	$("#btnSearch_record_web").click(function(e) {
//		$('#queryFrom').val("WEB");
//		findData();
//	});
//	
//	$("#btnSearch_record_mobile").click(function(e) {
//		$('#queryFrom').val("MOBILE");
//		$('#collapseExample').collapse('hide');
//		findData();
//	});
	findData();
});

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
		url : _ctx + '/circuit/getE1OpenListData.json',
		data : {
			"queryStatus" : $("#queryStatus").val(),
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

function unbindScrollEvent() {
	$(".dataTables_scrollBody").off("scroll");
}

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
				"url" : _ctx + '/circuit/getE1OpenListData.json',
				"type" : 'POST',
				"data" : function ( d ) {
					
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
			"order": [[5 , 'desc' ]],
			"initComplete": function(settings, json) {
				if (json.msg != null) {
					$(".myTableSection").hide();
					alert(json.msg);
				}
				bindScrollEvent();
        },
			"drawCallback" : function(settings) {
				//資料筆數
				$("div.dataTables_info").parent().removeClass('col-sm-12');
				$("div.dataTables_info").parent().addClass('col-sm-6');
				
				startNum = pageLength; //初始查詢完成後startNum固定為pageLength大小
				lastScrollYPos = $(".dataTables_scrollBody").prop("scrollTop");
				$("#resultTable_filter").find("input").prop("placeholder","(模糊查詢速度較慢)")
				bindTrEvent();
			},
			"columns" : [
				{},
				{ "data" : "deviceId" , "className" : "center", "orderable" : false, "visible" : false},
				{ "data" : "circleName" , "className" : "center", "orderable" : false },
				{ "data" : "stationName" , "className" : "center", "orderable" : false },
				{ "data" : "stationEngName" , "className" : "center", "orderable" : false, "visible" : false },
				{ "data" : "neName" , "className" : "center", "orderable" : false },
				{ "data" : "srcSid" , "className" : "center", "orderable" : false, "visible" : false },
				{ "data" : "dstSid" , "className" : "center", "orderable" : false, "visible" : false },
				{ "data" : "localCsrIp" , "className" : "center", "orderable" : false },
				{ "data" : "remoteCsrIp" , "className" : "center", "orderable" : false },
				{ "data" : "srcE1gwIp" , "className" : "center", "orderable" : false },
				{ "data" : "dstE1gwIp" , "className" : "center", "orderable" : false },
				{ "data" : "srcPortNumber" , "className" : "center", "orderable" : false },
				{ "data" : "dstPortNumber" , "className" : "center", "orderable" : false },
				{ "data" : "remark" , "className" : "center", "orderable" : false, "visible" : false },
				{ "data" : "updateTimeStr" , "className" : "center", "orderable" : false }
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
//				,{
//					"targets" : [1],
//					"className" : "center",
//					"searchable": false,
//					"orderable": false,
//					"render": function (data, type, row, meta) {
//				       	return meta.row + meta.settings._iDisplayStart + 1;
//				   	}
//				}
			]
		});
	}
}
