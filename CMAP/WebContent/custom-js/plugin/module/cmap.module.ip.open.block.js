/**
 * 
 */
var resultTable_blockedIpRecord;	//DataTable
var blockReasonShowLength = 30;
var _navAndMenuAndFooterHeight = 0;
var _deductHeight = 0;
var blockedIpTableHeight;

$(document).ready(function() {
	
});

function checkboxOnChangeEvent() {
	// 判斷CheckBox有無勾選，決定解鎖按鈕是否可按
	$('input[name=chkbox]').change(function (e) {
		if ($('input[name=chkbox]:checked').length > 0) {
			$('#btnIpOpen').attr('disabled', false);
		} else {
			$('#btnIpOpen').attr('disabled', true);
		}
	});
}

/**********************************************************************************************************
 *** 計算BlockedIpList區塊DataTable可呈顯的高度
 **********************************************************************************************************/
function calBlockedIpSectionHeight() {
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
	
	blockedIpTableHeight = Math.round(window.innerHeight-_navAndMenuAndFooterHeight-_deductHeight);
	
	//避免手機裝置橫向狀態下高度縮太小無法閱讀資料，設定最小高度限制為165px
	blockedIpTableHeight = blockedIpTableHeight < 165 ? 165 : blockedIpTableHeight;
}

//查詢按鈕動作
function findBlockedIpRecordData() {
	calBlockedIpSectionHeight();

	if (typeof resultTable_blockedIpRecord !== "undefined") {
		//resultTable.clear().draw(); server-side is enabled.
		resultTable_blockedIpRecord.ajax.reload();
		
	} else {
		$("#divBlockedIpRecord").show();
		
		resultTable_blockedIpRecord = $('#resultTable_blockedIpRecord').DataTable(
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
			"scrollY"		: blockedIpTableHeight,
			"scrollCollapse": true,
			"language" : {
	    		"url" : _ctx + "/resources/js/dataTable/i18n/Chinese-traditional.json"
	        },
	        "createdRow": function( row, data, dataIndex ) {
	        	   if(data.blockReason != null && data.blockReason.length > blockReasonShowLength) { //當內容長度超出設定值，加上onclick事件(切換顯示部分or全部)
	        	      $(row).children('td').eq(5).attr('onclick','javascript:showFullScript($(this));');
	        	      $(row).children('td').eq(5).addClass('cursor_zoom_in');
	        	   }
	        	   $(row).children('td').eq(5).attr('content', data.blockReason);
	        	},
			"ajax" : {
				"url" : _ctx + '/delivery/getBlockedIpData.json',
				"type" : 'POST',
				"data" : function ( d ) {
					d.queryGroupId = '15717';
					return d;
				},
				"error" : function(xhr, ajaxOptions, thrownError) {
					ajaxErrorHandler();
				}
			},
			"order" : [[3 , 'desc' ]],
			"pageLength" : 100,
			/*
			"initComplete": function(settings, json){
            },
            */
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
				
				bindTrEvent();
				checkboxOnChangeEvent();
			},
			"columns" : [
				{},{},
				{ "data" : "groupName" , "className" : "center" },
				{ "data" : "ipAddress" , "className" : "center" },
				{ "data" : "blockTimeStr" , "className" : "center" },
				{},
				{ "data" : "blockBy" , "className" : "center" },
			],
			"columnDefs" : [ 
				{
					"targets" : [0],
					"className" : "center",
					"searchable": false,
					"orderable": false,
					"render" : function(data, type, row) {
								 var html = '<input type="checkbox" id="chkbox" name="chkbox" onclick="changeTrBgColor(this)" value='+row.listId+'>';
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
					"targets" : [5],
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
			],
		});
	}
}