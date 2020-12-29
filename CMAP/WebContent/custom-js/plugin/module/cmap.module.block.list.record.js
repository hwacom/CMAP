/**
 * 
 */
var resultTable_blockedRecord;	//DataTable
var blockReasonShowLength = 30;
var openReasonShowLength = 30;
var _navAndMenuAndFooterHeight = 0;
var _deductHeight = 0;
var blockedTableHeight;

$(document).ready(function() {
	initMenuStatus("toggleMenu_plugin", "toggleMenu_plugin_items", "cm_blockedListRecord");
	
	$("#btnSearch_record_web").click(function(e) {
		$('#queryFrom').val("WEB");
		findBlockedRecordData();
	});
	
	$("#btnSearch_record_mobile").click(function(e) {
		$('#queryFrom').val("MOBILE");
		$('#collapseExample').collapse('hide');
		findBlockedRecordData();
	});
});

//查詢按鈕動作
function findBlockedRecordData() {
	calBlockedSectionHeight();

	if (typeof resultTable_blockedRecord !== "undefined" && typeof statusFlag == "undefined") {
		//resultTable.clear().draw(); server-side is enabled.
		resultTable_blockedRecord.ajax.reload();
		
	} else {
		$("#divBlockedRecord").show();
		resultTable_blockedRecord = $('#resultTable_blockedRecord').DataTable(
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
			"scrollY"		: blockedTableHeight,
			"scrollCollapse": true,
			"bDestroy"		: true,
			"language" : {
	    		"url" : _ctx + "/resources/js/dataTable/i18n/Chinese-traditional.json"
	        },
	        "createdRow": function( row, data, dataIndex ) {
	        	   if(data.blockReason != null && data.blockReason.length > blockReasonShowLength) { //當內容長度超出設定值，加上onclick事件(切換顯示部分or全部)
	        	      $(row).children('td').eq(8).attr('onclick','javascript:changeShowContent(this, '+blockReasonShowLength+');');
	        	      $(row).children('td').eq(8).addClass('cursor_zoom_in');
	        	   }
	        	   $(row).children('td').eq(8).attr('content', data.blockReason);
	        	   
	        	},
			"ajax" : {
				"url" : _ctx + '/plugin/module/blockedRecord/getBlockedData.json',
				"type" : 'POST',
				"data" : function ( d ) {
					if ($('#queryFrom').val() == 'WEB') {
						d.queryGroupId = $("#queryGroup").val()
						
					} else if ($('#queryFrom').val() == 'MOBILE') {
						d.queryGroupId = $("#queryGroup_mobile").val()
					}

					d.queryStatusFlag = 'B';

					return d;
				},
				"error" : function(xhr, ajaxOptions, thrownError) {
					ajaxErrorHandler();
				}
			},
			"order" : [[3 , 'asc' ],[6 , 'desc' ]],
			"pageLength" : 100,
			"rowCallback": function( row, data ) {
				$('td:eq(0)', row).attr('data-field', 'seq');
				$('td:eq(1)', row).attr('data-field', 'groupName');
				$('td:eq(2)', row).attr('data-field', 'deviceName');
				$('td:eq(3)', row).attr('data-field', 'blockType');
				$('td:eq(4)', row).attr('data-field', 'address');
				$('td:eq(5)', row).attr('data-field', 'ipDesc');
				$('td:eq(6)', row).attr('data-field', 'status');
				$('td:eq(7)', row).attr('data-field', 'blockTime');
				$('td:eq(8)', row).attr('data-field', 'blockReason');
				$('td:eq(9)', row).attr('data-field', 'blockBy');
				$('td:eq(10)', row).attr('data-field', 'scriptName');
			},
			"columns" : [
				{},
				{ "data" : "groupName" , "className" : "left" },
				{ "data" : "deviceName" , "className" : "left" },
				{ "data" : "blockType" , "className" : "center" },
				{ "data" : "address" , "className" : "left" },
				{ "data" : "ipDesc" , "className" : "left" },
				{ "data" : "statusFlag" , "className" : "center" },
				{ "data" : "blockTimeStr" , "className" : "center" },
				{},
				{ "data" : "blockBy" , "className" : "center" },
				{ "data" : "scriptName", "defaultContent" : ""}
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
				},
				{
					"targets" : [8],
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


/**********************************************************************************************************
 *** 計算BlockedList區塊DataTable可呈顯的高度
 **********************************************************************************************************/
function calBlockedSectionHeight() {
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
	
	blockedTableHeight = Math.round(window.innerHeight-_navAndMenuAndFooterHeight-_deductHeight);
	
	//避免手機裝置橫向狀態下高度縮太小無法閱讀資料，設定最小高度限制為165px
	blockedTableHeight = blockedTableHeight < 165 ? 165 : blockedTableHeight;
}
