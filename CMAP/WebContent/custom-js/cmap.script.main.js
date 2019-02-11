/**
 * 
 */
var scriptShowLength = 30;

$(document).ready(function() {
	initMenuStatus("toggleMenu_cm", "toggleMenu_cm_items", "cm_script");
			
});

//查詢按鈕動作
function findData(from) {
	$('#queryFrom').val(from);
	$('input[name=checkAll]').prop('checked', false);
	
	if (from == 'MOBILE') {
		$('#collapseExample').collapse('hide');
	}
	
	if (typeof resutTable !== "undefined") {
		resutTable.clear().draw();
		resutTable.ajax.reload();
		
	} else {
		$(".myTableSection").show();
		
		resutTable = $('#resutTable').DataTable(
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
			"ajax" : {
				"url" : _ctx + '/script/getScriptData.json',
				"type" : 'POST',
				"data" : function ( d ) {
					if ($('#queryFrom').val() == 'WEB') {
						d.queryScriptTypeCode = $("#queryScriptType").val();
					
					} else if ($('#queryFrom').val() == 'MOBILE') {
						d.queryScriptTypeCode = $("#queryScriptType_mobile").val();
					}
					
					return d;
				},
				"error" : function(xhr, ajaxOptions, thrownError) {
					ajaxErrorHandler();
				}
			},
			"order": [[3 , 'asc' ]],
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
				initCheckedItems();
			},
			"createdRow": function( row, data, dataIndex ) {
	        	   if(data.actionScript != null && data.actionScript.length > scriptShowLength) { //當內容長度超出設定值，加上onclick事件(切換顯示部分or全部)
	        	      $(row).children('td').eq(5).attr('onclick','javascript:showScriptContent('+data.scriptInfoId+', \'A\');');
	        	      $(row).children('td').eq(5).addClass('cursor_zoom_in');
	        	   }
	        	   $(row).children('td').eq(5).attr('content', data.actionScript);
	        	   
	        	   if(data.checkScript != null && data.checkScript.length > scriptShowLength) { //當內容長度超出設定值，加上onclick事件(切換顯示部分or全部)
	        	      $(row).children('td').eq(7).attr('onclick','javascript:showScriptContent('+data.scriptInfoId+', \'C\');');
	        	      $(row).children('td').eq(7).addClass('cursor_zoom_in');
	        	   }
	        	   $(row).children('td').eq(7).attr('content', data.checkScript);
	        	},
			"columns" : [
				{},{},
				{ "data" : "scriptName" },
				{ "data" : "scriptTypeName" , "className" : "center" },
				{ "data" : "systemVersion" , "className" : "center" },
				{},
				{ "data" : "actionScriptRemark"},
				{},
				{ "data" : "checkScriptRemark"},
				{ "data" : "createTimeStr" , "className" : "center" },
				{ "data" : "updateTimeStr" , "className" : "center" }
			],
			"columnDefs" : [
				{
					"targets" : [0],
					"className" : "center",
					"searchable": false,
					"orderable": false,
					"render" : function(data, type, row) {
								 var html = '<input type="radio" id="radiobox" name="radiobox" onclick="resetTrBgColor();changeTrBgColor(this)" value='+row.scriptTypeId+'>';
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
					"orderable": true,
					"render": function (data, type, row, meta) {
								if (row.actionScript != null && row.actionScript.length > scriptShowLength) {
									return getPartialContentHtml(row.actionScript, scriptShowLength); //內容長度超出設定，僅顯示部分內容
								} else {
									return row.actionScript; 						//未超出設定則全部顯示
								}
						   	}
				},
				{
					"targets" : [7],
					"className" : "left",
					"searchable": true,
					"orderable": true,
					"render": function (data, type, row, meta) {
								if (row.checkScript != null && row.checkScript.length > scriptShowLength) {
									return getPartialContentHtml(row.checkScript, scriptShowLength); //內容長度超出設定，僅顯示部分內容
								} else {
									return row.checkScript; 						//未超出設定則全部顯示
								}
						   	}
				}
			],
		});
	}
}