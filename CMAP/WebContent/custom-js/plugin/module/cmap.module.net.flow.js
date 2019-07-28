/**
 * 
 */

var timer, startTime, timer_start, timer_end;

$(document).ready(function() {
	initMenuStatus("toggleMenu_plugin", "toggleMenu_plugin_items", "cm_netflow");
	
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
	
	$("#queryDateBegin").val(year+"-"+month+"-"+date);
	
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
		var scrollTop = $(this).prop("scrollTop");
		var scrollTopMax = $(".dataTables_scrollBody").prop("scrollTopMax");
		if (scrollTop > scrollTopMax - 300) {
			console.log("...");
		}
	});
}

function findNextData() {
	$.ajax({
		url : _ctx + '/plugin/module/netFlow/getNetFlowData.json',
		data : function ( d ) {
			if ($('#queryFrom').val() == 'WEB') {
				d.queryGroup = $("#queryGroup").val(),
				d.querySourceIp = $("#query_SourceIp").val(),
				d.queryDestinationIp = $("#query_DestinationIp").val(),
				d.querySenderIp = $("#query_SenderIp").val(),
				d.querySourcePort = $("#query_SourcePort").val(),
				d.queryDestinationPort = $("#query_DestinationPort").val(),
				//d.queryMac = $("#queryMac").val(),
				d.queryDateBegin = $("#queryDateBegin").val(),
				//d.queryDateEnd = $("#queryDateEnd").val()
				d.queryTimeBegin = $("#queryTimeBegin").val(),
				d.queryTimeEnd = $("#queryTimeEnd").val()
			
			} else if ($('#queryFrom').val() == 'MOBILE') {
				d.queryGroup = $("#queryGroup_mobile").val(),
				d.querySourceIp = $("#query_SourceIp_mobile").val(),
				d.queryDestinationIp = $("#query_DestinationIp_mobile").val(),
				d.querySenderIp = $("#query_SenderIp_mobile").val(),
				d.querySourcePort = $("#query_SourcePort_mobile").val(),
				d.queryDestinationPort = $("#query_DestinationPort_mobile").val(),
				//d.queryMac = $("#queryMac_mobile").val();
				d.queryDateBegin = $("#queryDateBegin_mobile").val(),
				//d.queryDateEnd = $("#queryDateEnd_mobile").val()
				d.queryTimeBegin = $("#queryTimeBegin").val(),
				d.queryTimeEnd = $("#queryTimeEnd").val()
			}
			
			return d;
		},
		headers: {
		    'Accept': 'application/json',
		    'Content-Type': 'application/json'
		},
		type : "POST",
		dataType : 'json',
		async: true,
		beforeSend : function(xhr) {
			showProcessing();
		},
		complete : function() {
			hideProcessing();
		},
		success : function(resp) {
			if (resp.code == '200') {
				console.log(resp.length);
				
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
	$('#queryFrom').val(from);
	
	if ($("#queryGroup").val().trim().length == 0) {
		alert(msg_chooseGroup);
		return;
	}
	
	if ($("#queryDateBegin").val().trim().length == 0) {
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
			"pageLength"	: 300,
			"language" : {
	    		"url" : _ctx + "/resources/js/dataTable/i18n/Chinese-traditional.json"
	        },
	        "createdRow": function( row, data, dataIndex ) {
	        },
			"ajax" : {
				"url" : _ctx + '/plugin/module/netFlow/getNetFlowData.json',
				"type" : 'POST',
				"data" : function ( d ) {
					if ($('#queryFrom').val() == 'WEB') {
						d.queryGroup = $("#queryGroup").val(),
						d.querySourceIp = $("#query_SourceIp").val(),
						d.queryDestinationIp = $("#query_DestinationIp").val(),
						d.querySenderIp = $("#query_SenderIp").val(),
						d.querySourcePort = $("#query_SourcePort").val(),
						d.queryDestinationPort = $("#query_DestinationPort").val(),
						//d.queryMac = $("#queryMac").val(),
						d.queryDateBegin = $("#queryDateBegin").val(),
						//d.queryDateEnd = $("#queryDateEnd").val()
						d.queryTimeBegin = $("#queryTimeBegin").val(),
						d.queryTimeEnd = $("#queryTimeEnd").val()
					
					} else if ($('#queryFrom').val() == 'MOBILE') {
						d.queryGroup = $("#queryGroup_mobile").val(),
						d.querySourceIp = $("#query_SourceIp_mobile").val(),
						d.queryDestinationIp = $("#query_DestinationIp_mobile").val(),
						d.querySenderIp = $("#query_SenderIp_mobile").val(),
						d.querySourcePort = $("#query_SourcePort_mobile").val(),
						d.queryDestinationPort = $("#query_DestinationPort_mobile").val(),
						//d.queryMac = $("#queryMac_mobile").val();
						d.queryDateBegin = $("#queryDateBegin_mobile").val(),
						//d.queryDateEnd = $("#queryDateEnd_mobile").val()
						d.queryTimeBegin = $("#queryTimeBegin").val(),
						d.queryTimeEnd = $("#queryTimeEnd").val()
					}
					
					return d;
				},
				beforeSend : function() {
					$("#div_TotalFlow").hide();
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
							$("#result_TotalFlow").text("總流量：" + json.otherMsg);
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
				
				bindTrEvent();
				bindScrollEvent();
			},
			"columns" : [
				{},
				{ "data" : "groupName" , "orderable" : false },
				{ "data" : "now" , "orderable" : false },
				{ "data" : "fromDateTime" },
				{ "data" : "toDateTime" , "orderable" : false },
				{ "data" : "ethernetType" , "orderable" : false },
				{ "data" : "protocol" , "orderable" : false },
				{ "data" : "sourceIP" , "orderable" : false },
				{ "data" : "sourcePort" , "orderable" : false },
				{ "data" : "sourceMAC" , "orderable" : false },
				{ "data" : "destinationIP" , "orderable" : false },
				{ "data" : "destinationPort" , "orderable" : false },
				{ "data" : "destinationMAC" , "orderable" : false },
				{ "data" : "size" , "orderable" : false },
				{ "data" : "channelID" , "orderable" : false },
				{ "data" : "toS" , "orderable" : false },
				{ "data" : "senderIP" , "orderable" : false },
				{ "data" : "inboundInterface" , "orderable" : false },
				{ "data" : "outboundInterface" , "orderable" : false },
				{ "data" : "sourceASI" , "orderable" : false },
				{ "data" : "destinationASI" , "orderable" : false },
				{ "data" : "sourceMask" , "orderable" : false },
				{ "data" : "destinationMask" , "orderable" : false },
				{ "data" : "nextHop" , "orderable" : false },
				{ "data" : "sourceVLAN" , "orderable" : false },
				{ "data" : "destinationVLAN" , "orderable" : false },
				{ "data" : "flowID" , "orderable" : false }
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