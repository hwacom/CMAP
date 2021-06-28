
var startNum, pageLength;
$(document).ready(function() {
	initMenuStatus("toggleMenu_abnormalAlarm_items", "toggleMenu_abnormalAlarm_items", "mp_tickets");
	
	startNum = 0;
	pageLength = Number($("#pageLength").val());	

	var today = new Date();
	var year = today.getFullYear();
	var month = parseInt(today.getMonth()) + 1;
	month = (month < 10) ? ("0".concat(month)) : month;
	var date = today.getDate();
	date = (date < 10) ? ("0".concat(date)) : date;
	var cDate = year+"-"+month+"-"+date;
	
	$("#queryDateEnd").val(cDate);
	$("#queryTimeBegin").val("00:00");
	$("#queryTimeEnd").val("23:59");
	
	today.setDate(today.getDate() - 6);
	date = today.getDate();
	date = (date < 10) ? ("0".concat(date)) : date;
	cDate = year+"-"+month+"-"+date;
	$("#queryDateBegin").val(cDate);
	
	$("#btnSave").click(function() {
		var obj = new Object();
		obj.inputContent = $("#inputContent").val();
		obj.inputListId = $("#inputListId").val();
		obj.inputOwnerStr = $('#ticketOwner span').text().substring($('#ticketOwner span').text().indexOf(":")+1).replace(/[\t]*[\n]*[ ]*/g, '');
		doActionAjax(obj, 'save');
	});
	
	if($("#btnSearch_web").length == 1) {
		findData(isWEB?'WEB':'MOBILE');
	}
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
//		var rowCount = $("#resultTable > tBody > tr").length;
		var cTR = $("#resultTable > tbody > tr:eq(0)").clone();
//		$(cTR).find("td:eq(0)").html( ++rowCount );
		$(cTR).find("td:eq(0)").html( data.updateTimeStr );
		$(cTR).find("td:eq(1)").html( data.priority );
		$(cTR).find("td:eq(2)").html( data.listId );
		$(cTR).find("td:eq(3)").html( data.subject );
		$(cTR).find("td:eq(4)").html( data.ownerStr );
		$(cTR).find("td:eq(5)").html( data.status );
		$(cTR).find("td:eq(6)").html( data.remark );
		$("#resultTable > tbody").append($(cTR));
	}
	$.fn.dataTable.tables( { visible: true, api: true } ).columns.adjust();
}

//取得查詢條件下總筆數
function getTotalFilteredCount() {
	$.ajax({
		url : _ctx + '/plugin/module/tickets/getTotalFilteredCount.json',
		data : {
			"queryDateBegin" : $("#queryDateBegin").val(),
			"queryDateEnd" : $("#queryDateEnd").val(),
			"queryTimeBegin" : $("#queryTimeBegin").val(),
			"queryTimeEnd" : $("#queryTimeEnd").val(),
			"queryOwner" : $("#queryOwner option:selected")[0].parentNode.label + "-" + $("#queryOwner").val(),
			"queryStatus" : $("#queryStatus").val()
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
			$("#searchWaiting2").hide();
		},
		initComplete : function(settings, json) {
      },
		success : function(resp) {
			var count = resp.data.FILTERED_COUNT;
			$("#total_count").html('&nbsp;' + count + '&nbsp;');
		},
		error : function(xhr, ajaxOptions, thrownError) {
			$("#total_count").html('&nbsp;N/A&nbsp;');
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
		url : _ctx + '/plugin/module/tickets/getTicketData.json',
		data : {
			"queryDateBegin" : $("#queryDateBegin").val(),
			"queryDateEnd" : $("#queryDateEnd").val(),
			"queryTimeBegin" : $("#queryTimeBegin").val(),
			"queryTimeEnd" : $("#queryTimeEnd").val(),
			"queryOwner" :  $("#queryOwner option:selected")[0].parentNode.label + "-" + $("#queryOwner").val(),
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

//查詢按鈕動作
function findData(from) {
	$('#queryFrom').val(from);
	
	if ($("#queryDateBegin").val().trim().length == 0 || $("#queryDateEnd").val().trim().length == 0) {
		alert(msg_chooseDate);
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
				"url" : _ctx + '/plugin/module/tickets/getTicketData.json',
				"type" : 'POST',
				"data" : function ( d ) {
					if ($('#queryFrom').val() == 'WEB') {
						d.queryDateBegin = $("#queryDateBegin").val(),
						d.queryDateEnd = $("#queryDateEnd").val(),
						d.queryTimeBegin = $("#queryTimeBegin").val(),
						d.queryTimeEnd = $("#queryTimeEnd").val(),
						d.queryOwner =  $("#queryOwner option:selected")[0].parentNode.label + "-" + $("#queryOwner").val(),
						d.queryStatus = $("#queryStatus").val();
						
					} else if ($('#queryFrom').val() == 'MOBILE') {
						d.queryDateBegin = $("#queryDateBegin_mobile").val(),
						d.queryDateEnd = $("#queryDateEnd_mobile").val();
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
			"order": [[0 , 'desc' ]],
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
				{ "data" : "updateTimeStr" , "className" : "center", "orderable" : true },
				{ "data" : "priority" , "className" : "center", "orderable" : false },
				{ "data" : "listId" , "className" : "center", "orderable" : false },
				{ "data" : "subject" , "className" : "center", "orderable" : false },
				{ "data" : "ownerStr" , "className" : "center", "orderable" : false },
				{ "data" : "status" , "className" : "center", "orderable" : false },
				{ "data" : "remark" , "orderable" : false }
			],
			"columnDefs" : [
				{
					"targets" : [0],
					"className" : "center",
					"searchable": false,
					"orderable": false,
					"render": function (data, type, row, meta) {
							var html ;
							if((row.ownerType == 'G' && row.owner == $("#userGroup").val()) ||
									(row.ownerType == 'U' && row.owner == $("#userAccount").val())){
								html = '<a href="tickets/getTicketDetail.json?queryListId='+row.listId+'">'+row.updateTimeStr+'</a>';
							}else{
								html = row.updateTimeStr;
							}
							return html;
					}
				}
			]
		});
	}
}

function doActionAjax(obj, action) {
	$.ajax({
		url : _ctx + '/plugin/module/tickets/'+action,
		data : JSON.stringify(obj),
		headers: {
		    'Accept': 'application/json',
		    'Content-Type': 'application/json'
		},
		type : "POST",
		dataType : 'json',
		async: true,
		beforeSend : function() {
			showProcessing();
		},
		complete : function() {
			hideProcessing();
		},
		success : function(resp) {
			if (resp.code == '200') {
				location.href = "getTicketDetail.json?queryListId="+resp.message;
			} else {
				alert('envAction > success > else :: resp.code: '+resp.code);
				alert(resp.message);
			}
		},
		error : function(xhr, ajaxOptions, thrownError) {
			ajaxErrorHandler();
		}
	});
}

function confirmForward(){
	confirm("請確認是否指派給"+$("#queryOwner option:selected").text()+"?", "forward");
	
}

function forward(){
	var obj = new Object();
	obj.inputContent = $("#inputOwner").val() + msg_reasign + "：" + $("#queryOwner option:selected")[0].parentNode.label + "-" + $("#queryOwner option:selected").text();
	obj.inputListId = $("#inputListId").val();
	obj.inputOwnerStr = $('#ticketOwner span').text().substring($('#ticketOwner span').text().indexOf(":")+1).replace(/[\t]*[\n]*[ ]*/g, '');
	obj.inputForwardOwnerType = $("#queryOwner option:selected")[0].parentNode.label;
	obj.inputForwardOwner = $("#queryOwner option:selected").val();
	
	$.ajax({
		url : _ctx + '/plugin/module/tickets/forward',
		data : JSON.stringify(obj),
		headers: {
		    'Accept': 'application/json',
		    'Content-Type': 'application/json'
		},
		type : "POST",
		dataType : 'json',
		async: true,
		beforeSend : function() {
			showProcessing();
		},
		complete : function() {
			hideProcessing();
		},
		success : function(resp) {
			if (resp.code == '200') {
				location.href = "../tickets";
			} else {
				alert('envAction > success > else :: resp.code: '+resp.code);
				alert(resp.message);
			}
		},
		error : function(xhr, ajaxOptions, thrownError) {
			ajaxErrorHandler();
		}
	});
}
