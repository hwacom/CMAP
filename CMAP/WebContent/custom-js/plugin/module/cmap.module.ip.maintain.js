/**
 * 
 */

$(document).ready(function() {
	initMenuStatus("toggleMenu_setting", "toggleMenu_setting_items", "st_ipMaintain");
	
	$("#queryGroup").change(function(e) {
		findData("WEB");
	});
	
	$("#queryGroup_mobile").change(function(e) {
		findData("MOBILE");
	});
	
	$("#btnAdd").click(function() {
		showAddModal();
	});
	
	$("#btnIpDataImportBackStep").click(function() {
		showAddEditPanel();
	});
	
	$("#btnIpDataImportNextStep").click(function() {
		showAddConfirmPanel();
	});
	
	$("#btnIpDataImportConfirm").click(function() {
		doAdd();
	});
	
	$("#btnModify").click(function() {
		changeModifyView();
	});
	
	$("#btnDelete").click(function() {
		envAction('delete');
	});
	
	$("#btnModifySubmit").click(function() {
		envAction('update');
	});
	
	$("#btnModifyCancel").click(function() {
		findData($("#queryFrom").val());
	});
	
	findData("WEB");
});

function initActionBar() {
	$("#modifyActionBar").hide();
	$("#defaultActionBar").show();
	$(".dataTable").find("thead").eq(0).find("tr > th").css( 'pointer-events', 'auto' );
	
	$("select[name=resutTable_length]").removeAttr('disabled'); //開放分頁筆數選單
	$("input[type=search]").removeAttr('disabled'); //開放內文搜尋輸入框
}

function toggleActionBar() {
	$("#modifyActionBar").toggle();
	$("#defaultActionBar").toggle();
}

function showAddModal() {
	//初始化狀態
	$("#ipDataImportModal_dataSet").val(""); 			// 清空textarea內容
	$("#div_edit_panel").show();						// Edit Panel打開
	$("#btnIpDataImportNextStep").parent().show();		// 打開下一步按鈕
	
	$("#confirm_panel_table > tbody > tr").remove();	// 清空table tbody內容
	
	$("#div_confirm_panel").hide();						// Confirm panel先隱藏
	$("#btnIpDataImportBackStep").parent().hide();		// 上一步按鈕先隱藏
	$("#btnIpDataImportConfirm").parent().hide();		// 確認按鈕先隱藏
	
	$("#ipDataImportModal").modal({
		backdrop : 'static'
	});
}

function showAddEditPanel() {
	$("#div_edit_panel").show();						// Edit Panel打開
	$("#btnIpDataImportNextStep").parent().show();		// 打開下一步按鈕
	
	$("#confirm_panel_table > tbody > tr").remove();	// 清空table tbody內容
	
	$("#div_confirm_panel").hide();						// Confirm panel先隱藏
	$("#btnIpDataImportBackStep").parent().hide();		// 上一步按鈕先隱藏
	$("#btnIpDataImportConfirm").parent().hide();		// 確認按鈕先隱藏
}

function showAddConfirmPanel() {
	var dataSet = $("#ipDataImportModal_dataSet").val();
	
	if (dataSet == null || dataSet == "" || (dataSet != null && dataSet.length == 0)) {
		alert("資料為空，請重新確認!");
		return;
	}

	var tr, td;
	var dataList = dataSet.split("\n");
	var dataLine, ipAddr, ipDesc;
	for (var i=0; i<dataList.length; i++) {
		dataLine = dataList[i];
		
		ipAddr = dataLine.split(",")[0].trim();
		ipDesc = dataLine.split(",")[1].trim();
		
		tr = $("<tr></tr>");
		tr.append($("<td></td>").text(ipAddr));
		tr.append($("<td></td>").text(ipDesc));
		$("#confirm_panel_table > tbody").append(tr);	// 將資料加到table tbody內
	}
	
	$("#div_edit_panel").hide();						// Edit panel隱藏
	$("#btnClose").parent().hide();						// 關閉按鈕隱藏
	$("#btnIpDataImportNextStep").parent().hide();		// 下一步按鈕隱藏
	
	$("#div_confirm_panel").show();						// Confirm panel打開
	$("#btnIpDataImportBackStep").parent().show();		// 上一步按鈕打開
	$("#btnIpDataImportConfirm").parent().show();		// 確認按鈕打開
}

function doAdd() {
	
}

function changeModifyView() {
	var checkedItem = $('input[name=chkbox]:checked');
	
	if (checkedItem.length == 0) {
		alert('請先勾選欲修改的項目');
		return;
	}
	
	for (var i=0; i<checkedItem.length; i++) {
		
		var hasInnerText = (document.getElementsByTagName("body")[0].innerText !== undefined) ? true : false;
		
		$('input[name=chkbox]:checked:eq('+i+')').attr('disabled','disabled'); //關閉列表勾選框
		
		$('input[name=chkbox]:checked:eq('+i+')').parents("tr").children().eq(4).html(
			//切換「IP備註」欄位為輸入框
			function() {
				var html = '<input type="text" name="modifyIpDesc" value="' + $(this).attr("content") +'" class="form-control form-control-sm" style="min-width: 200px" />';
				return html;
			}
		);
	}
	
	toggleActionBar(); //切換action bar按鈕
	$.fn.dataTable.tables( { visible: true, api: true } ).columns.adjust(); //重繪表頭寬度
	$(".dataTable").find("thead").eq(0).find("tr > th").removeClass("sorting sorting_asc sorting_desc"); //移除表頭排序箭頭圖示
	$(".dataTable").find("thead").eq(0).find("tr > th").css( 'pointer-events', 'none' ); //移除表頭排序功能
	$("select[name=resutTable_length]").attr('disabled','disabled'); //關閉分頁筆數選單
	$("input[type=search]").attr('disabled','disabled'); //關閉內文搜尋輸入框
	$("li[id^=resutTable_]").addClass('disabled'); //關閉右下角分頁按鈕
}

function envAction(action) {
	var obj = new Object();
	
	var settingIds = $("input[name='chkbox']:checked").map(function() {
     	return $(this).val();
     }).get();
	
	obj.settingIds = settingIds;
	
	if (action == "update") {
		var modifyIpDesc = $("input[name='modifyIpDesc']").map(function() {
						         	 return $(this).val();
						          }).get();
		
		obj.modifyIpDesc = modifyIpDesc;
		
	} else if (action == "delete") {
		var checkedItem = $('input[name=chkbox]:checked');
		
		if (checkedItem.length == 0) {
			alert('請先勾選欲刪除的項目');
			return;
		}
	}
	
	$.ajax({
		url : _ctx + '/plugin/module/ipMaintain/'+action,
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
				findData($("#queryFrom").val());
				
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
	
	if (from == 'MOBILE') {
		$('#collapseExample').collapse('hide');
	}
	
	initActionBar();
	$("#checkAll").prop('checked', false);
	
	if (typeof resultTable !== "undefined") {
		resultTable.ajax.reload();
		$(".myTableSection").show();
		
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
	        	$(row).children('td').eq(4).attr('content', data.ipDesc);
	        },
			"ajax" : {
				"url" : _ctx + '/plugin/module/ipMaintain/getIpDataSetting.json',
				"type" : 'POST',
				"data" : function ( d ) {
					if ($('#queryFrom').val() == 'WEB') {
						d.queryGroup = $("#queryGroup").val();
					} else if ($('#queryFrom').val() == 'MOBILE') {
						d.queryGroup = $("#queryGroup_mobile").val();
					}
					return d;
				},
				beforeSend : function() {
					//countDown('START');
				},
				complete : function() {
					//countDown('STOP');
				},
				"error" : function(xhr, ajaxOptions, thrownError) {
					ajaxErrorHandler();
				},
				/*
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
				*/
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
			},
			"columns" : [
				{},{},
				{ "data" : "groupName" , "className" : "center" },
				{ "data" : "ipAddr" , "className" : "center" },
				{ "data" : "ipDesc" }
			],
			"columnDefs" : [
				{
					"targets" : [0],
					"className" : "center",
					"searchable": false,
					"orderable": false,
					"render" : function(data, type, row) {
								 var html = '<input type="checkbox" id="chkbox" name="chkbox" onclick="changeTrBgColor(this)" value='+row.settingId+'>';
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
				}
			],
		});
	}
}