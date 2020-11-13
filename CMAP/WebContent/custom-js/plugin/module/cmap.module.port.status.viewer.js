/**
 * 
 */

var light_normal_icon;
var light_warning_icon;
var light_alarm_icon;
var light_error_icon;
var light_disable_icon;

$(document).ready(function() {
	initMenuStatus("toggleMenu_prtg", "toggleMenu_prtg_items", "mp_interfaceStatusList");
	
	//初始化設備選單
	changeDeviceMenu("queryDevice", $("#queryGroup").val());
	changeDeviceMenu("queryDevice_mobile", $("#queryGroup_mobile").val());
	
	//移除設備選單ALL選項
	removeDeviceOptionOfAll();
	
	$("#queryGroup").change(function() {
		removeDeviceOptionOfAll();
	});
	
	$("#queryGroup_mobile").change(function() {
		removeDeviceOptionOfAll();
	});
	
	$("#resultTable").on('xhr.dt', function ( e, settings, json, xhr ) {
		if (json.msg != null) {
			$(".myTableSection").hide();
			alert(json.msg);
		}
	});
	
	light_normal_icon = '<img src="' + _ctx + '/resources/images/light/light_normal.png" class="light_icon">';
	light_warning_icon = '<img src="' + _ctx + '/resources/images/light/light_warning.png" class="light_icon">';
	light_alarm_icon = '<img src="' + _ctx + '/resources/images/light/light_alarm.png" class="light_icon">';
	light_error_icon = '<img src="' + _ctx + '/resources/images/light/light_error.png" class="light_icon">';
	light_disable_icon = '<img src="' + _ctx + '/resources/images/light/light_disable.png" class="light_icon">';
});

function removeDeviceOptionOfAll() {
	//移除設備選單ALL選項
	$("#queryDevice option[value='']").remove();
	$("#queryDevice_mobile option[value='']").remove();
}

function getLightIcon(presentType) {
	switch (presentType) {
		case "NORMAL":
			return light_normal_icon;
		case "WARNING":
			return light_warning_icon;
		case "ALARM":
			return light_alarm_icon;
		case "ERROR":
			return light_error_icon;
		case "DISABLE":
			return light_disable_icon;
	}
}

//查詢按鈕動作
function findData(from) {
	$('#queryFrom').val(from);
	
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
			"bFilter" 		: false,
			"ordering" 		: false,
			"info" 			: false,
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
	        	$(row).children('td').eq(4).html(getLightIcon(data.portAdminStatusPresentType) + " " + data.portAdminStatusDesc + "(" + data.portAdminStatus + ")");
	        	$(row).children('td').eq(5).html(getLightIcon(data.portOperStatusPresentType) + " " + data.portOperStatusDesc + "(" + data.portOperStatus + ")");
	        },
			"ajax" : {
				"url" : _ctx + '/plugin/module/portStatusViewer/getInterfaceStatusList.json',
				"type" : 'POST',
				"data" : function ( d ) {
					if ($('#queryFrom').val() == 'WEB') {
						d.queryGroup = $("#queryGroup").val();
						d.queryDevice = $("#queryDevice").val();
					} else if ($('#queryFrom').val() == 'MOBILE') {
						d.queryGroup = $("#queryGroup_mobile").val();
						d.queryDevice = $("#queryDevice_mobile").val();
					}
					return d;
				},
				beforeSend : function() {
					showProcessing();
				},
				complete : function() {
					hideProcessing();
				},
				error : function(xhr, ajaxOptions, thrownError) {
					ajaxErrorHandler();
				},
				"timeout" : parseInt(_timeout) * 1000 //設定60秒Timeout
			},
			"initComplete": function(settings, json) {
				if (json.msg != null) {
					$(".myTableSection").hide();
//					alert(json.msg);
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
			},
			"columns" : [
				{},
				{ "data" : "groupName" , "className" : "center" },
				{ "data" : "deviceName" , "className" : "center" },
				{ "data" : "portName" },
				{ "data" : "portAdminStatus" },
				{ "data" : "portOperStatus" }
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