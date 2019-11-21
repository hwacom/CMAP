	/**
 * 
 */
var resultTable_blockedMacRecord;	//DataTable
var blockReasonShowLength = 30;
var openReasonShowLength = 30;
var _navAndMenuAndFooterHeight = 0;
var _deductHeight = 0;
var blockedMacTableHeight;

$(document).ready(function() {
	
});

/**********************************************************************************************************
 *** 計算BlockedMacList區塊DataTable可呈顯的高度
 **********************************************************************************************************/
function calBlockedMacSectionHeight() {
	_navAndMenuAndFooterHeight = 0;
	_deductHeight = 0;
	
	//計算dataTable區塊高度，以window高度扣除navbar、footer和mobile版選單高度
	if ($('.mobile-menu').css('display') != 'none' && $('.mobile-menu').css('display') != undefined) {
		_navAndMenuAndFooterHeight = $('.navbar').outerHeight() + $(".footer").outerHeight() + $('.mobile-menu').outerHeight();
	} else {
		_navAndMenuAndFooterHeight = $('.navbar').outerHeight() + $(".footer").outerHeight();
	}
	
	if ($('.search-bar-large').css('display') != 'none' && $('.search-bar-large').css('display') != undefined) {
		_navAndMenuAndFooterHeight += $('.search-bar-large').outerHeight();
		_deductHeight = 110;
	} else {
		_deductHeight = 210;
	}
	
	if ($('.mainTable').css('display') != 'none' && $('.mainTable').css('display') != undefined) {
		_deductHeight += $('.mainTable').outerHeight();
	}
	
	if ($('#divBlockedTitle').css('display') != 'none' && $('#divBlockedTitle').css('display') != undefined) {
		_deductHeight += $('#divBlockedTitle').outerHeight();
	}
	
	blockedMacTableHeight = Math.round(window.innerHeight-_navAndMenuAndFooterHeight-_deductHeight);
	
	//避免手機裝置橫向狀態下高度縮太小無法閱讀資料，設定最小高度限制為165px
	blockedMacTableHeight = blockedMacTableHeight < 165 ? 165 : blockedMacTableHeight;
}

/**
 * 執行Mac開通 by 「Mac解鎖/封鎖」功能中的「解鎖」按鈕
 */
function doOpenByBtn() {
	var listId = new Array();
	
	var checkedItem = $('input[name=radioBox_2]:checked');
	for (var i=0; i<checkedItem.length; i++) {
		listId.push(checkedItem[i].value);
	}
	
	var reason = $("#openReasonModal_reason").val();
	
	$.ajax({
		url : _ctx + '/delivery/doMacOpenByBtn.json',
		data : {
			"listId" : listId,
			"reason" : reason
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
				alert(resp.message);
				
				findBlockedMacRecordData('B');
				
				setTimeout(function() {
					$('#openReasonModal').modal('hide');
				}, 500);
				
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
function findBlockedMacRecordData(statusFlag) {
	calBlockedMacSectionHeight();

	if (typeof resultTable_blockedMacRecord !== "undefined") {
		//resultTable.clear().draw(); server-side is enabled.
		resultTable_blockedMacRecord.ajax.reload();
		
	} else {
		$("#divBlockedMacRecord").show();
		
		resultTable_blockedMacRecord = $('#resultTable_blockedMacRecord').DataTable(
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
			"scrollY"		: blockedMacTableHeight,
			"scrollCollapse": true,
			"language" : {
	    		"url" : _ctx + "/resources/js/dataTable/i18n/Chinese-traditional.json"
	        },
	        "createdRow": function( row, data, dataIndex ) {
	        	   if(data.blockReason != null && data.blockReason.length > blockReasonShowLength) { //當內容長度超出設定值，加上onclick事件(切換顯示部分or全部)
	        	      $(row).children('td').eq(7).attr('onclick','javascript:changeShowContent(this, '+blockReasonShowLength+');');
	        	      $(row).children('td').eq(7).addClass('cursor_zoom_in');
	        	   }
	        	   $(row).children('td').eq(7).attr('content', data.blockReason);
	        	   
	        	   if(data.openReason != null && data.openReason.length > openReasonShowLength) { //當內容長度超出設定值，加上onclick事件(切換顯示部分or全部)
	        	      $(row).children('td').eq(8).attr('onclick','javascript:changeShowContent(this, '+openReasonShowLength+');');
	        	      $(row).children('td').eq(8).addClass('cursor_zoom_in');
	        	   }
	        	   $(row).children('td').eq(8).attr('content', data.openReason);
	        	},
			"ajax" : {
				"url" : _ctx + '/delivery/getBlockedMacData.json',
				"type" : 'POST',
				"data" : function ( d ) {
					if ($('#queryFrom').val() == 'WEB') {
						d.queryGroupId = $("#queryGroup").val()
						
					} else if ($('#queryFrom').val() == 'MOBILE') {
						d.queryGroupId = $("#queryGroup_mobile").val()
					}
					if (statusFlag == 'B') {
						d.queryStatusFlag = statusFlag;
					}
					return d;
				},
				"error" : function(xhr, ajaxOptions, thrownError) {
					ajaxErrorHandler();
				}
			},
			"order" : [[5 , 'desc' ]],
			"pageLength" : 100,
			/*
			"initComplete": function(settings, json){
            },
            */
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
				
				bindTrEventForSpecifyTableRadio('dataTable_2', 'radioBox_2');
				
				var pathname = window.location.pathname;
				var lastPath = pathname.substring(pathname.lastIndexOf('/'), pathname.length);
				if (lastPath === "/macOpenBlock") {
					$('[data-field="status"]').hide();
					$('[data-field="openTime"]').hide();
					$('[data-field="openReason"]').hide();
					$('[data-field="openBy"]').hide();
					
					recordSectionRadioBoxOnChangeEvent();
					
				} else if (lastPath === "/macBlocked") {
					$('[data-field="action"]').hide();
				}
				
				$.fn.dataTable.tables( { visible: true, api: true } ).columns.adjust();
			},
			"rowCallback": function( row, data ) {
				$('td:eq(0)', row).attr('data-field', 'action');
				$('td:eq(1)', row).attr('data-field', 'seq');
				$('td:eq(2)', row).attr('data-field', 'groupName');
				$('td:eq(3)', row).attr('data-field', 'macAddress');
				$('td:eq(4)', row).attr('data-field', 'status');
				$('td:eq(5)', row).attr('data-field', 'blockTime');
				$('td:eq(6)', row).attr('data-field', 'openTime');
				$('td:eq(7)', row).attr('data-field', 'blockReason');
				$('td:eq(8)', row).attr('data-field', 'openReason');
				$('td:eq(9)', row).attr('data-field', 'blockBy');
				$('td:eq(10)', row).attr('data-field', 'openBy');
			},
			"columns" : [
				{},{},
				{ "data" : "groupName" , "className" : "left" },
				{ "data" : "macAddress" , "className" : "left" },
				{ "data" : "statusFlag" , "className" : "center" },
				{ "data" : "blockTimeStr" , "className" : "center" },
				{ "data" : "openTimeStr" , "className" : "center" },
				{},
				{},
				{ "data" : "blockBy" , "className" : "center" },
				{ "data" : "openBy" , "className" : "center" },
			],
			"columnDefs" : [ 
				{
					"targets" : [0],
					"className" : "center",
					"searchable": false,
					"orderable": false,
					"render" : function(data, type, row) {
								 var html = '<input type="radio" id="radioBox_2" name="radioBox_2" onclick="changeTrBgColor(this)" value='+row.listId+'>';
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
					"targets" : [7],
					"className" : "left",
					"searchable": true,
					"orderable": false,
					"render" : function(data, type, row) {
						if (row.blockReason != null && row.blockReason.length > blockReasonShowLength) {
							 return getPartialContentHtml(row.blockReason, blockReasonShowLength); 	//內容長度超出設定，僅顯示部分內容
						} else {
							return row.blockReason; 							//未超出設定則全部顯示
						}
					}
				}
				,
				{
					"targets" : [8],
					"className" : "left",
					"searchable": true,
					"orderable": false,
					"render" : function(data, type, row) {
						if (row.openReason != null && row.openReason.length > openReasonShowLength) {
							 return getPartialContentHtml(row.openReason, openReasonShowLength); 	//內容長度超出設定，僅顯示部分內容
						} else {
							return row.openReason; 							//未超出設定則全部顯示
						}
					}
				}
			],
		});
	}
}