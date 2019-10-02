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
		envAction('add');
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
	var groupId = $("#queryGroup").val();
	
	if (groupId == "") {
		alert(msg_chooseGroup);
		return;
	}
	
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

function transDoubleQuota(oriText) {
	var newText = oriText;
	if ((newText.indexOf("\"") == 0) && (newText.lastIndexOf("\"") == (newText.length - 1))) {
		//console.log("first.");
		// 表示此資料含保留字
		// 替換「""」為「"」
		//console.log("(Ori):" + newText);
		newText = newText.replace(/\"\"/g, '"');
		//console.log("(1):" + newText);
		newText = newText.substring(1);
		newText = newText.substring(0, newText.length-1);
		//console.log("(2):" + newText);
		
	} else if ((newText.indexOf("\"") == 0) && (newText.lastIndexOf("\"") != (newText.length - 1))) {
		//console.log("second.");
		//console.log("(Ori):" + newText);
		newText = newText.replace(/\"\"/g, '"');
		//console.log("(1):" + newText);
		newText = newText.substring(1);
		//console.log("(2):" + newText);
		
	} else if ((newText.indexOf("\"") != 0) && (newText.lastIndexOf("\"") == (newText.length - 1))) {
		//console.log("third.");
		//console.log("(Ori):" + newText);
		newText = newText.replace(/\"\"/g, '"');
		//console.log("(1):" + newText);
		newText = newText.substring(0, newText.length-1);
		//console.log("(2):" + newText);
	}
	return newText;
}

function showAddConfirmPanel() {
	var dataSet = $("#ipDataImportModal_dataSet").val();
	
	if (dataSet == null || dataSet == "" || (dataSet != null && dataSet.length == 0)) {
		alert("資料為空，請重新確認!");
		return;
	}

	var groupName = $("#queryGroup option:selected").text();
	var tr, td;
	var dataList = dataSet.split("\n");
	var dataLine, ipAddr, ipDesc;
	for (var i=0; i<dataList.length; i++) {
		dataLine = dataList[i];
		
		var firstCommaIdx = dataLine.indexOf(",");
		if (firstCommaIdx == -1) {
			alert("第 " + (i+1) + " 行格式錯誤! (未含逗點)");
			$("#confirm_panel_table > tbody > tr").remove();	// 清空table tbody內容
			return;
		}
		
		ipAddr = transDoubleQuota(dataLine.substring(0, firstCommaIdx).trim());
		ipDesc = transDoubleQuota(dataLine.substring(firstCommaIdx+1, dataLine.length).trim());
		
		tr = $("<tr></tr>");
		tr.append($("<td></td>").text(i + 1));			// 序欄位
		tr.append($("<td></td>").text(groupName));
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
				var content = $(this).attr("content");
				var html = '<input type="text" name="modifyIpDesc" value="' + content +'" class="form-control form-control-sm" style="min-width: 200px" />';
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
	
	if (action != "add") {
		var groupIds = $("input[name='chkbox']:checked").map(function() {
	     	return $(this).data('groupid');
	     }).get();
		
		var ipAddrs = $("input[name='chkbox']:checked").map(function() {
	     	return $(this).data('ipaddr');
	     }).get();
		
		obj.groupIds = groupIds;
		obj.ipAddrs = ipAddrs;
	}
	
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
		
	} else if (action == "add") {
		var dataSet = $("#ipDataImportModal_dataSet").val();
		
		if (dataSet == null || dataSet == "" || (dataSet != null && dataSet.length == 0)) {
			alert("資料為空，請重新確認!");
			return;
		}
		
		var obj = new Object();
		var array_ipAddr = new Array();
		var array_ipDesc = new Array();
		var dataList = dataSet.split("\n");
		
		for (var i=0; i<dataList.length; i++) {
			dataLine = dataList[i];
			
			var firstCommaIdx = dataLine.indexOf(",");
			if (firstCommaIdx == -1) {
				alert("第 " + (i+1) + " 行格式錯誤! (未含逗點)");
				$("#confirm_panel_table > tbody > tr").remove();	// 清空table tbody內容
				return;
			}
			
			ipAddr = transDoubleQuota(dataLine.substring(0, firstCommaIdx).trim());
			ipDesc = transDoubleQuota(dataLine.substring(firstCommaIdx+1, dataLine.length).trim());
			
			array_ipAddr.push(ipAddr);
			array_ipDesc.push(ipDesc);
		}
		
		obj.groupId = $("#queryGroup").val();
		obj.modifyIpAddr = array_ipAddr;
		obj.modifyIpDesc = array_ipDesc;
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
				
				setTimeout(function(){
					$('#ipDataImportModal').modal('hide');
					
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
	        	$(row).children('td').eq(4).attr('content', htmlSpecialChars(data.ipDesc));
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
								 var html = '<input type="checkbox" id="chkbox" name="chkbox" onclick="changeTrBgColor(this)" value='+row.settingId+' data-groupId='+row.groupId+' data-ipAddr='+row.ipAddr+'>';
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