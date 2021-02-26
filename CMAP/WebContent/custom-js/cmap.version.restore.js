/**
 * 
 */
var resultTable;
var remarkShowLength = 80;	//設定欄位顯示內容最大長度

$(document).ready(function() {
	initMenuStatus("toggleMenu_cm", "toggleMenu_cm_items", "cm_restore");
	
	changeDeviceMenu("queryDevice", $("#queryGroup").val());
	
	//查詢按鈕(Web)點擊事件
    $('#btnSearch_web').click(function (e) {
    	findData('WEB');
    });
  	
  	//查詢按鈕(Mobile)點擊事件
    $('#btnSearch_mobile').click(function (e) {
    	findData('MOBILE');
    });
    
    $('#btnRestore').click(function (e) {
    	showRestorePanel();
    });
    
    $('#btnRestoreCancel').click(function (e) {
    	$('#viewVersionModal').modal('hide');
    });
    
    $('#btnRestoreConfirm').click(function (e) {
    	doRestore();
    });
});

function resetTrBgColor() {
	$('tbody > tr').removeClass('mySelected');
}

function showRestorePanel() {
	var deviceListId = $('[type=radio]:checked').val();
	
	if (deviceListId == null || (deviceListId != null && deviceListId.trim().length == 0)) {
		alert("請先選擇要還原的設備");
		
	} else {
		// 查找要還原的設備備份歷史紀錄
		$.ajax({
			url : _ctx + '/version/getVersionRecords.json',
			data : {
				"deviceListId" : deviceListId
			},
			type : "POST",
			dataType : 'json',
			async: true,
			beforeSend : function() {
			},
			complete : function() {
			},
			success : function(resp) {
				if (resp.code == '200') {
					$("#viewScriptModal_versionSelect option").remove();
					
					var obj = resp.data.versionList;
					var idx = 1;
					$.each(obj, function(key, vo) {
						$("#viewScriptModal_versionSelect")
							.append($("<option></option>")
											.attr("value", vo.deviceListId+"@~"+vo.versionId)
											.text("(" + idx + ") " + vo.configVersion));
						idx++;
					});
					
					$('#viewVersionModal').modal('show');
					
				} else {
					alert(resp.message);
				}
			},
			error : function(xhr, ajaxOptions, thrownError) {
				ajaxErrorHandler();
			}
		});
	}
}

function doRestore() {
	var selectVal = $('#viewScriptModal_versionSelect').val();
	
	if (selectVal == null || (selectVal != null && selectVal.trim().length == 0)) {
		alert("請先選擇要還原的版本號");
		
	} else {
		confirm("按下確認後版本將立即還原且生效，請確認是否執行?", "doRestoreGo");
	}
}

function doRestoreGo() {
	var selectVal = $('#viewScriptModal_versionSelect').val();
	
	if (selectVal == null || (selectVal != null && selectVal.trim().length == 0)) {
		alert("請先選擇要還原的版本號");
		
	} else {
		var deviceListId = selectVal.split("@~")[0];
		var versionId = selectVal.split("@~")[1];
		
		// 查找要還原的設備備份歷史紀錄
		$.ajax({
			url : _ctx + '/version/restore/execute',
			data : {
				"deviceListId" : deviceListId,
				"versionId" : versionId
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
					$('#viewVersionModal').modal('hide');
					
				} else {
					alert(resp.message);
				}
			},
			error : function(xhr, ajaxOptions, thrownError) {
				ajaxErrorHandler();
			}
		});
	}
}

function chkChecked() {
	var hasChecked = false;
	
	$('input[type=checkbox][name=chkbox]').each(function () {
        if (this.checked) {
        	hasChecked = true;
         	return false;
        }
	});
	
	return hasChecked;
	//$("#btnBackup").prop( "disabled", disabled );
}

function pressViewConfig() {
	var selectVal = $('#viewScriptModal_versionSelect').val();
	
	if (selectVal == null || (selectVal != null && selectVal.trim().length == 0)) {
		alert("請先選擇要預覽的版本號");
		
	} else {
		var versionId = selectVal.split("@~")[1];
		viewConfig(versionId);
	}
}

//查詢按鈕動作
function findData(from) {
	$('#queryFrom').val(from);
	$('input[name=checkAll]').prop('checked', false);
	
	if (from == 'MOBILE') {
		$('#collapseExample').collapse('hide');
	}
	
	if (typeof resultTable !== "undefined") {
//		resultTable.clear().draw();
		resultTable.ajax.reload();
		
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
	        	   if(data.deviceName != null && data.deviceName.length > remarkShowLength) { //當內容長度超出設定值，加上onclick事件(切換顯示部分or全部)
	        	      $(row).children('td').eq(3).attr('onclick','javascript:changeShowContent(this, '+remarkShowLength+');');
	        	      $(row).children('td').eq(3).addClass('cursor_zoom_in');
	        	   }
	        	   $(row).children('td').eq(3).attr('content', data.deviceName);
	        	},
			"ajax" : {
				"url" : _ctx + '/version/getDeviceListData.json',
				"type" : 'POST',
				"data" : function ( d ) {
					if ($('#queryFrom').val() == 'WEB') {
						d.queryGroup = $("#queryGroup").val(),
						d.queryDevice = $("#queryDevice").val()
					
					} else if ($('#queryFrom').val() == 'MOBILE') {
						d.queryGroup = $("#queryGroup_mobile").val(),
						d.queryDevice = $("#queryDevice_mobile").val()
					}
					
					return d;
				},
				"error" : function(xhr, ajaxOptions, thrownError) {
					ajaxErrorHandler();
				}
			},
//			"order": [[3 , 'asc' ]],
			"initComplete": function(settings, json){
            },
			"drawCallback" : function(settings) {
				$.fn.dataTable.tables( { visible: true, api: true } ).columns.adjust();
				$("div.dataTables_length").parent().removeClass('col-sm-12');
				$("div.dataTables_length").parent().addClass('col-sm-6');
				$("div.dataTables_filter").parent().removeClass('col-sm-12');
				$("div.dataTables_filter").parent().addClass('col-sm-6');
				
				$("div.dataTables_info").parent().removeClass('col-sm-12');
				$("div.dataTables_info").parent().addClass('col-sm-6');
				$("div.dataTables_paginate").parent().removeClass('col-sm-12');
				$("div.dataTables_paginate").parent().addClass('col-sm-6');
				
				initCheckedItems();
				bindTrEvent();
			},
			"columns" : [
				{},{},
				{ "data" : "groupName" },
				{},
				{ "data" : "deviceModel" , "className" : "center" }
			],
			"columnDefs" : [
				{
					"targets" : [0],
					"className" : "center",
					"searchable": false,
					"orderable": false,
					"render" : function(data, type, row) {
								 var html = '<input type="radio" id="chkbox" name="chkbox" onclick="changeTrBgColor(this)" value='+row.deviceId+'>';
								 return html;
							 }
				},
				{
					"targets" : [1],
					"className" : "center",
					"searchable": false,
					"orderable": false,
					"render": function (data, type, row, meta) {
						       	return meta.row + meta.settings._iDisplayStart + 1;
						   	}
				},
				{
					"targets" : [3],
					"className" : "left",
					"searchable": true,
					"orderable": true,
					"render": function (data, type, row, meta) {
								if (row.deviceName != null && row.deviceName.length > remarkShowLength) {
									 return getPartialContentHtml(row.deviceName, remarkShowLength); //內容長度超出設定，僅顯示部分內容
								} else {
									return row.deviceName; 						//未超出設定則全部顯示
								}
						   	}
				}
			],
		});
	}
}