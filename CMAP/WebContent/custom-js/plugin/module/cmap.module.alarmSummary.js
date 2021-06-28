
var startNum, pageLength;
var resultTable2;
$(document).ready(function() {
	initMenuStatus("toggleMenu_abnormalAlarm_items", "toggleMenu_abnormalAlarm_items", "mp_alarmSummary2");
	
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
		
	if($("#btnSearch_web").length == 1 && $("div .show").attr("id").endsWith("tab1")) {
		findData(isWEB?'WEB':'MOBILE');
	}
	
	//異動狀態按鈕點擊事件
    $('#btnUpdateStatus').click(function (e) {
    	var count = 0;
    	if($("div .show").attr("id").endsWith("tab1")){
    		count = resultTable.rows( { selected: true } ).count();
    	}else{
    		count = resultTable2.rows( { selected: true } ).count();
    	}
    	
		if (count == 0) {
			alert('請先勾選欲執行的項目!');
			return;
		}

		$("#updateStatusDialogModal").modal({
			backdrop : 'static'
		});
    });

	//異動狀態確認
	$("#btnConfirmUpdate").click(function() {
		
		var obj = new Object();
		var datas = [];
		if($("div .show").attr("id").endsWith("tab1")){
			datas = resultTable.rows( { selected: true } ).data();
    	}else{
    		datas = resultTable2.rows( { selected: true } ).data();
    	}
        var ids = [];
        for(i=0;i<datas.length;i++){
        	ids.push(datas[i].alarmId);
    	}
//        console.log(ids);
        
		if($("#updateStatus").val() == 'doing'){
			if(ids.length > 1){
				alert('開立工單請僅勾選1個項目!!');
				return;
			}
			
			if(datas[0].alarmDataStatus == 'doing'){
				alert('此警報已開立工單!!');
				return;
			}
			
			setTimeout(function(){
				$('#updateStatusDialogModal').modal('hide');
				
			}, 500);
			$("#addNewTicketDialogModal").modal({
				backdrop : 'static'
			});
			return;
		}
		
		
		obj.ids = ids;
		obj.updateStatus = $("#updateStatus").val();
		doActionAjax(obj, 'updateStatus');
	});
	
    //新增工單按鈕點擊事件
//    $('#btnCreateTicket').click(function (e) {
//    	var checkedItem = resultTable.rows( { selected: true } ).count();
//		
//		if (checkedItem.length == 0) {
//			alert('請先勾選欲執行的項目');
//			return;
//		}
//		
//		if (checkedItem.length > 1) {
//			alert('請僅勾選1個項目');
//			return;
//		}
//		
//		$("#addNewTicketDialogModal").modal({
//			backdrop : 'static'
//		});
//    });
    
    //新增工單確認
	$("#btnConfirmCreate").click(function() {
		confirm("請確認是否新增?", "addNewTicket");
	});
	
	$("#chkAllBox1").click(function() {
//		console.log(resultTable.rows().data());
	    if ($("#resultTable th.select-checkbox").hasClass("selected")) {
	        resultTable.rows().deselect();
	        $("#resultTable th.select-checkbox").removeClass("selected");
	        $("#chkAllBox1").prop("checked", false);
	    } else {
	        resultTable.rows().select();
	        $("#resultTable th.select-checkbox").addClass("selected");
	        $("#chkAllBox1").prop("checked", true);
	    }
	}).on("select deselect", function() {
	    if (resultTable.rows({
	            selected: true
	        }).count() !== resultTable.rows().count()) {
	        $("#resultTable th.select-checkbox").removeClass("selected");
	    } else {
	        $("#resultTable th.select-checkbox").addClass("selected");
	    }
	});
	
	$("#chkAllBox2").click(function() {
	    if ($("#resultTable2 th.select-checkbox").hasClass("selected")) {
	        resultTable2.rows().deselect();
	        $("#resultTable2 th.select-checkbox").removeClass("selected");
	        $("#chkAllBox2").prop("checked", false)
	    } else {
	        resultTable2.rows().select();
	        $("#resultTable2 th.select-checkbox").addClass("selected");
	        $("#chkAllBox2").prop("checked", true)
	    }
	}).on("select deselect", function() {
	    if (resultTable2.rows({
	            selected: true
	        }).count() !== resultTable2.rows().count()) {
	        $("#resultTable2 th.select-checkbox").removeClass("selected");
	    } else {
	        $("#resultTable2 th.select-checkbox").addClass("selected");
	    }
	});
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

//function addRow(dataList) {
//	for (var i=0; i<dataList.length; i++) {
//		var data = dataList[i];
//		var rowCount = $("#resultTable > tBody > tr").length;
//		var cTR = $("#resultTable > tbody > tr:eq(0)").clone();
//		$(cTR).find("td:eq(0)").html( '<input type="checkbox" id="chkbox" name="chkbox" value="'+data.alarmId+'">' );
//		$(cTR).find("td:eq(0)").html( data.sensorName );
//		$(cTR).find("td:eq(1)").html( data.groupName );
//		$(cTR).find("td:eq(2)").html( data.deviceName );
//		$(cTR).find("td:eq(3)").html( data.status );
//		$(cTR).find("td:eq(4)").html( data.alarmTime );
//		$(cTR).find("td:eq(5)").html( data.lastValue );
//		$(cTR).find("td:eq(6)").html( data.message );
//		$(cTR).find("td:eq(7)").html( data.priority );
//		$(cTR).find("td:eq(8)").html( data.remark );
//		$("#resultTable > tbody").append($(cTR));
//	}
//	$.fn.dataTable.tables( { visible: true, api: true } ).columns.adjust();
//}

//取得查詢條件下總筆數
function getTotalFilteredCount() {
	$.ajax({
		url : _ctx + '/plugin/module/alarmSummary/getTotalFilteredCount.json',
		data : {
			"queryDateBegin" : $("#queryDateBegin").val(),
			"queryDateEnd" : $("#queryDateEnd").val(),
			"queryTimeBegin" : $("#queryTimeBegin").val(),
			"queryTimeEnd" : $("#queryTimeEnd").val(),
			"querySensorType" : $("#querySensorType").val(),
			"queryStatus" : $("#queryStatus").val(),
			"queryDataStatus" : 'active',
			"queryMessage" : $("#queryMessage").val()
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
//function findNextData() {
//	var sortIdx = -1;
//	var sortStr;
//	
//	if ($(".dataTable > thead").find(".sorting_asc").length > 0) {
//		sortIdx = $(".dataTable > thead").find(".sorting_asc").prop("cellIndex");
//	} else if ($(".dataTable > thead").find(".sorting_desc").length > 0) {
//		sortIdx = $(".dataTable > thead").find(".sorting_desc").prop("cellIndex");
//	}
//
//	var sortBy = $(".dataTable > thead > tr > th:eq(" + sortIdx + ")").attr("aria-sort");
//	if (sortBy === "descending") {
//		sortStr = "desc";
//	} else {
//		sortStr = "asc";
//	}
//
//	$.ajax({
//		url : _ctx + '/plugin/module/alarmSummary/getAlarmData.json',
//		data : {
//			"queryDateBegin" : $("#queryDateBegin").val(),
//			"queryDateEnd" : $("#queryDateEnd").val(),
//			"queryTimeBegin" : $("#queryTimeBegin").val(),
//			"queryTimeEnd" : $("#queryTimeEnd").val(),
//			"queryStatus" : $("#queryStatus").val(),
//			"queryMessage" : $("#queryMessage").val(),
//			"start" : startNum,
//			"length" : pageLength,
//			"order[0][column]" : sortIdx,
//			"order[0][dir]" : sortStr
//		},
//		type : "POST",
//		dataType : 'json',
//		async: true,
//		beforeSend : function(xhr) {
//			countDown('START');
//			showProcessing();
//		},
//		complete : function() {
//			countDown('STOP');
//			hideProcessing();
//			waitForNextData = false;
//		},
//		initComplete : function(settings, json) {
//			if (json.msg != null) {
//				$(".myTableSection").hide();
//				alert(json.msg);
//			}
//      },
//		success : function(resp) {
//			var count = resp.data.length;
//			console.log("query success... count: " + count);
//			if (count > 0) {
//				addRow(resp.data);
//				startNum += pageLength;
//			}
//			
//			var sNum, eNum;
//			if ($(".dataTables_empty").length == 0) { //有查到資料
//				sNum = 1;
//				eNum = $("#resultTable > tBody > tr").length;
//			} else {
//				sNum = 0;
//				eNum = 0;
//			}
//			$("#current_count").text('顯示第 ' + sNum + ' 至 ' + eNum + ' 項結果，共');
//		},
//		error : function(xhr, ajaxOptions, thrownError) {
//			$("#current_count").text('<ERROR>');
//			ajaxErrorHandler();
//		}
//	});
//}

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
	
	if($("div .show").attr("id").endsWith("tab2")){
		findHistoryData();
		return ;
	}
	
	if (typeof resultTable !== "undefined") {
		$(".dataTables_scrollBody").scrollTop(0);
		resultTable.ajax.reload();
//		$("#tableSection1").show();
		
	} else {
//		$("#tableSection1").show();
		
		resultTable = $('#resultTable').DataTable(
		{
			"autoWidth" 	: true,
//			"paging" 		: false,
//			"bFilter" 		: false,
			"ordering" 		: true,
			"info" 			: true,
//			"serverSide" 	: true, //false關閉server端排序
			"bLengthChange" : true,
//			"pagingType" 	: "full",
			"processing" 	: true,
			"scrollX"		: true,
			"scrollY"		: dataTableHeight,
			"scrollCollapse": true,
			"pageLength"	: pageLength,
			"search"		: {
		        // 大小寫不敏感
		        caseInsensitive: true
		    },
			"dom"			: "Bfrtip",
			"buttons" 		: [ //Bootstrap 預設功能
				{ extend: 'copy', text: '<i class="fas fa-copy"></i>複製至剪貼簿', 'titleAttr' : '複製至剪貼簿', 'className' : 'btn btn-sm btn-primary ml-2' }, 
				{ extend: 'excel', text: '<i class="fas fa-file-excel"></i>匯出xlsx', 'titleAttr' : '匯出xlsx', 'className' : 'btn btn-sm btn-primary ml-2' },
//				{ extend: 'excelHtml5', text: '<i class="fas fa-file-excel"></i>匯出excelHtml5', 'titleAttr' : '匯出xlsx', 'className' : 'btn btn-sm btn-primary ml-2' },
				{ extend: 'csv', text: '<i class="fas fa-file-csv"></i>匯出csv', 'titleAttr' : '匯出csv', 'className' : 'btn btn-sm btn-primary ml-2' }, 
				{ extend: 'pdf', text: '<i class="fas fa-file-pdf"></i> PDF', 'className' : 'btn btn-sm btn-primary ml-2' },
				{ extend: 'print', text: '<i class="fas fa-print"></i> Print', 'titleAttr': '列印', 'className' : 'btn btn-sm btn-primary ml-2' }, 
				{ extend: 'colvis', text: '欄位選擇', collectionLayout: 'fixed two-column',  columns: ':gt(0)' },
//				{ extend: 'selectAll', text: 'selectAll', 'className' : 'btn btn-sm btn-primary ml-2' },
//				{ extend: 'selectNone', text: 'selectNone', 'className' : 'btn btn-sm btn-primary ml-2' },
				{
	                text: 'Get selected data',
	                action: function () {
	                	if(resultTable.rows( { selected: true } ).count() == 0) return;
	                    var datas = resultTable.rows( { selected: true } ).data();
	                    var ids = [];
	                    for(i=0;i<datas.length;i++){
	                    	ids.push(datas[i].alarmId);
                    	}
	                    return console.log(ids);
	                }
	            }
				],
			"language" : {
	    		"url" : _ctx + "/resources/js/dataTable/i18n/Chinese-traditional.json"
	        },
	        "createdRow": function( row, data, dataIndex ) {
	        },
			"ajax" : {
				"url" : _ctx + '/plugin/module/alarmSummary/getAlarmData.json',
				"type" : 'POST',
				"data" : function ( d ) {
					if ($('#queryFrom').val() == 'WEB') {
						d.queryDateBegin = $("#queryDateBegin").val(),
						d.queryDateEnd = $("#queryDateEnd").val(),
						d.queryTimeBegin = $("#queryTimeBegin").val(),
						d.queryTimeEnd = $("#queryTimeEnd").val(),
						d.querySensorType = $("#querySensorType").val(),
						d.queryStatus = $("#queryStatus").val(),
						d.queryDataStatus = 'active',
						d.queryMessage = $("#queryMessage").val();
						
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
			"order": [[6 , 'desc' ]],
			"initComplete": function(settings, json) {
				if (json.msg != null) {
//					$("#tableSection1").hide();
					alert(json.msg);
				}
//				bindScrollEvent();
          },
			"drawCallback" : function(settings) {
				//資料筆數
//				$("div.dataTables_info").parent().removeClass('col-sm-12');
//				$("div.dataTables_info").parent().addClass('col-sm-6');
				
				startNum = pageLength; //初始查詢完成後startNum固定為pageLength大小
				lastScrollYPos = $(".dataTables_scrollBody").prop("scrollTop");
				$("#resultTable_filter").find("input").prop("placeholder","(模糊查詢速度較慢)")
				bindTrEvent();
//          bindTrEventForSpecifyTableRadio('dataTable_2', 'radioBox_2');
			},
			"columns" : [
				{
					data: function (row, type, set) {
						return '';
//					 return '<input type="hidden" id="row'+row.alarmId+'" value='+row.alarmId+'>';
					}
				},
	            {},
				{ "data" : "sensorType" , "className" : "center", "orderable" : false },
				{ "data" : "groupName" , "className" : "center", "orderable" : false },
				{ "data" : "deviceName" , "className" : "center", "orderable" : false },
				{ "data" : "alarmStatus" , "className" : "center", "orderable" : false },
				{ "data" : "alarmTimeStr" , "className" : "center", "orderable" : false },
				{ "data" : "closeTimeStr" , "className" : "center", "orderable" : false },
				{ "data" : "lastValue" , "className" : "center", "orderable" : false },
				{ "data" : "message" , "className" : "center", "orderable" : false },
				{ "data" : "priority" , "className" : "center", "orderable" : false },
				{ "data" : "remark" , "orderable" : false },
				{}
			],
			select: {
		        style: 'multi',
//		        selector: 'td:first-child'
		    },
			"columnDefs" : [
//				{
//					"targets" : [0],
//					"className": 'center',
//					"searchable": false,
//					"orderable": false,
//					"render" : function(data, type, row) {
//								 var html = '<input type="checkbox" id="chkbox" name="chkbox" class="select-checkbox" value='+row.alarmId+'>';
//								 return html;
//							 }
//				},
				{
					orderable: false,
			        className: 'select-checkbox',
			        targets: 0
			    },
				{
					"targets" : [1],
					"className" : "center",
					"searchable": false,
					"orderable": false,
					"render": function (data, type, row, meta) {
							var html = '<a href="#" onclick="alarmDialog(\''+row.sensorName+'\', \''+row.groupName+'\', \''
							+row.deviceName+'\', \''+row.sensorType+'\', \''+row.alarmStatus+'\', \''+row.alarmTimeStr+'\', \''+row.lastValue+'\', \''
							+row.message+'\', \''+row.priority+'\', \''+row.remark+'\')"><span data-feather="zoom-in"></span>'+row.sensorName+'</a>';
							return html;
					}
				},
				{
					"targets" : [12],
					"className" : "center",
					"searchable": false,
					"orderable": false,
					"render" : function(data, type, row) {
									var html = '<i class="fas fa-clipboard-list fa-lg" onclick="showLogDialog('+row.alarmId+')" ></i>';
									return html;
							 }
				}
			]
		});
	}
}

//history table
function findHistoryData() {
	if (typeof resultTable2 !== "undefined") {
		$(".dataTables_scrollBody").scrollTop(0);
		resultTable2.ajax.reload();
		
	} else {
		
		resultTable2 = $('#resultTable2').DataTable(
		{
			"autoWidth" 	: true,
			"ordering" 		: true,
			"info" 			: true,
			"bLengthChange" : true,
			"processing" 	: true,
			"scrollX"		: true,
			"scrollY"		: dataTableHeight,
			"scrollCollapse": true,
			"pageLength"	: pageLength,
			"search"		: {
		        // 大小寫不敏感
		        caseInsensitive: true
		    },
			"dom"			: "Bfrtip",
			"buttons" 		: [ //Bootstrap 預設功能
				{ extend: 'copy', text: '<i class="fas fa-copy"></i>複製至剪貼簿', 'titleAttr' : '複製至剪貼簿', 'className' : 'btn btn-sm btn-primary ml-2' }, 
				{ extend: 'excel', text: '<i class="fas fa-file-excel"></i>匯出xlsx', 'titleAttr' : '匯出xlsx', 'className' : 'btn btn-sm btn-primary ml-2' },
				{ extend: 'csv', text: '<i class="fas fa-file-csv"></i>匯出csv', 'titleAttr' : '匯出csv', 'className' : 'btn btn-sm btn-primary ml-2' }, 
				{ extend: 'pdf', text: '<i class="fas fa-file-pdf"></i> PDF', 'className' : 'btn btn-sm btn-primary ml-2' },
				{ extend: 'print', text: '<i class="fas fa-print"></i> Print', 'titleAttr': '列印', 'className' : 'btn btn-sm btn-primary ml-2' }, 
				{ extend: 'colvis', text: '欄位選擇', collectionLayout: 'fixed two-column',  columns: ':gt(0)' },
				{
	                text: 'Get selected data',
	                action: function () {
	                	if(resultTable2.rows( { selected: true } ).count() == 0) return;
	                    var datas = resultTable2.rows( { selected: true } ).data();
	                    var ids = [];
	                    for(i=0;i<datas.length;i++){
	                    	ids.push(datas[i].alarmId);
                    	}
	                    return console.log(ids);
	                }
	            }
				],
			"language" : {
	    		"url" : _ctx + "/resources/js/dataTable/i18n/Chinese-traditional.json"
	        },
	        "createdRow": function( row, data, dataIndex ) {
	        },
			"ajax" : {
				"url" : _ctx + '/plugin/module/alarmSummary/getAlarmData.json',
				"type" : 'POST',
				"data" : function ( d ) {
					if ($('#queryFrom').val() == 'WEB') {
						d.queryDateBegin = $("#queryDateBegin").val(),
						d.queryDateEnd = $("#queryDateEnd").val(),
						d.queryTimeBegin = $("#queryTimeBegin").val(),
						d.queryTimeEnd = $("#queryTimeEnd").val(),
						d.querySensorType = $("#querySensorType").val(),
						d.queryStatus = $("#queryStatus").val(),
						d.queryDataStatus = 'history',
						d.queryMessage = $("#queryMessage").val();
						
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
			"order": [[6 , 'desc' ]],
			"initComplete": function(settings, json) {
				if (json.msg != null) {
					alert(json.msg);
				}
			},
			"drawCallback" : function(settings) {
				//資料筆數
//				$("div.dataTables_info").parent().removeClass('col-sm-12');
//				$("div.dataTables_info").parent().addClass('col-sm-6');
				
				startNum = pageLength; //初始查詢完成後startNum固定為pageLength大小
				lastScrollYPos = $(".dataTables_scrollBody").prop("scrollTop");
				$("#resultTable2_filter").find("input").prop("placeholder","(模糊查詢速度較慢)");
			},
			"columns" : [
				{
					data: function (row, type, set) {
						return '';
					}
				},
	            {},
				{ "data" : "sensorType" , "className" : "center", "orderable" : false },
				{ "data" : "groupName" , "className" : "center", "orderable" : false },
				{ "data" : "deviceName" , "className" : "center", "orderable" : false },
				{ "data" : "alarmStatus" , "className" : "center", "orderable" : false },
				{ "data" : "alarmTimeStr" , "className" : "center", "orderable" : false },
				{ "data" : "closeTimeStr" , "className" : "center", "orderable" : false },
				{ "data" : "lastValue" , "className" : "center", "orderable" : false },
				{ "data" : "message" , "className" : "center", "orderable" : false },
				{ "data" : "priority" , "className" : "center", "orderable" : false },
				{ "data" : "remark" , "orderable" : false },
				{}
			],
			select: {
		        style: 'multi'
		    },
			"columnDefs" : [
				{
					orderable: false,
			        className: 'select-checkbox',
			        targets: 0
			    },
				{
					"targets" : [1],
					"className" : "center",
					"searchable": false,
					"orderable": false,
					"render": function (data, type, row, meta) {
							var html = '<a href="#" onclick="alarmDialog(\''+row.sensorName+'\', \''+row.groupName+'\', \''
							+row.deviceName+'\', \''+row.sensorType+'\', \''+row.alarmStatus+'\', \''+row.alarmTimeStr+'\', \''+row.lastValue+'\', \''
							+row.message+'\', \''+row.priority+'\', \''+row.remark+'\')"><span data-feather="zoom-in"></span>'+row.sensorName+'</a>';
							return html;
					}
				},
				{
					"targets" : [12],
					"className" : "center",
					"searchable": false,
					"orderable": false,
					"render" : function(data, type, row) {
									var html = '<i class="fas fa-clipboard-list fa-lg" onclick="showLogDialog('+row.alarmId+')" ></i>';
									return html;
							 }
				}
			]
		});
		
		$('a[data-toggle="tab"]').on('shown.bs.tab', function(e){
		   $($.fn.dataTable.tables(true)).DataTable().columns.adjust();
		});
			
//		resultTable2.columns.adjust().draw();
	}
}

function showLogDialog(alarmId) {
	var obj = new Object();
	$(".alarmLogTitle").text(alarmId);
	obj.alarmId = alarmId;
	
	$.ajax({
		url : _ctx + '/plugin/module/alarmSummary/getAlarmLog.json',
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
				var div = document.getElementById("alarmLogDiv");
				div.remove();
				var contentDiv = document.getElementById("alarmLogBody");
				var newDiv = document.createElement("div");
				newDiv.setAttribute("id", "alarmLogDiv");
				newDiv.setAttribute("class", "form-group row");
				newDiv.innerHTML = resp.message;
				contentDiv.appendChild(newDiv);
				
				$("#alarmLogDialogModal").modal({
					backdrop : 'static'
				});
				
			} else {
				alert(resp.message);
			}
		},
		error : function(xhr, ajaxOptions, thrownError) {
			ajaxErrorHandler();
		}
	});
}

function doActionAjax(obj, action) {
	$.ajax({
		url : _ctx + '/plugin/module/alarmSummary/'+action,
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
				setTimeout(function(){
					$('#updateStatusDialogModal').modal('hide');
					
				}, 500);
				
				alert(resp.message);
				
				resultTable.ajax.reload();
			} else {
//				alert('envAction > success > else :: resp.code: '+resp.code);
				alert(resp.message);
			}
		},
		error : function(xhr, ajaxOptions, thrownError) {
			ajaxErrorHandler();
		}
	});
}

function addNewTicket(){
	var obj = new Object();
	
	var datas = [];
	if($("div .show").attr("id").endsWith("tab1")){
		datas = resultTable.rows( { selected: true } ).data();
	}else{
		alert("已封存項目不可開立工單!!");
		return ;
	}
    var ids = [];
    for(i=0;i<datas.length;i++){
    	ids.push(datas[i].alarmId);
	}
    
	obj.ids = ids;
	obj.updateStatus = 'doing';
	obj.inputOwner = $("#inputOwner option:selected")[0].parentNode.label + "-" + $("#inputOwner").val();
	
	setTimeout(function(){
		$('#addNewTicketDialogModal').modal('hide');
		
	}, 500);
	
	$.ajax({
		url : _ctx + '/plugin/module/alarmSummary/addNewTicket',
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
				alert(resp.message);
				setTimeout(function(){
					location.href = "tickets";
					
				}, 750);
			} else {
//				alert('envAction > success > else :: resp.code: '+resp.code);
				alert(resp.message);
			}
		},
		error : function(xhr, ajaxOptions, thrownError) {
			ajaxErrorHandler();
		}
	});
}

function alarmDialog(sensorName, groupName, deviceName, sensorType, alarmStatus, alarmTimeStr, lastValue, message, priority, remark){
	$(".alarmTitle").text(groupName+"-"+deviceName+"	"+sensorName);
	$(".alarmPriority").text(priority);
	$(".alarmSensorType").text(sensorType);
	$(".alarmStatus").text(alarmStatus);
	$(".alarmTimeStr").text(alarmTimeStr);
	$(".alarmLastValue").text(lastValue);
	$(".alarmTimeStr").text(message);
	$(".alarmMessage").text(priority);
	$(".alarmRemark").text(remark);
	
	$("#alarmDialogModal").modal({
		backdrop : 'static'
	});
}


//[資料匯出]Modal >> 匯出確認按鈕事件 (由cmap.main.js呼叫)
function doDataExport(exportRecordCount) {
	var dataObj = new Object();
	dataObj.queryDateBegin =  $("#queryDateBegin").val(),
	dataObj.queryDateEnd = $("#queryDateEnd").val(),
	dataObj.queryTimeBegin = $("#queryTimeBegin").val(),
	dataObj.queryTimeEnd = $("#queryTimeEnd").val(),
	dataObj.queryStatus = $("#queryStatus").val(),
	dataObj.queryMessage = $("#queryMessage").val();
		
	dataObj.start = 0; //初始查詢一律從第0筆開始
	dataObj.length = pageLength;
	dataObj.exportRecordCount = exportRecordCount;
	
	$.ajax({
		url : _ctx + '/plugin/module/alarmSummary/dataExport.json',
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
