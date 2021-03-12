/**
 * 
 */
var remarkShowLength = 30;	//設定欄位顯示內容最大長度
	
$(document).ready(function() {
	initMenuStatus("toggleMenu_admin", "toggleMenu_admin_items", "bk_deviceLoginInfo");
	
	$("#btnModify").click(function() {
		var ckeckedCount = chkChecked();
		if (ckeckedCount) {
			initModal();
			
			var modifyRemark = $('input[name=chkbox]:checked').map(function() {
	        	return $(this).parents("tr").children().eq(2).text();
	        }).get();
			
			document.getElementById('modifyRemark').value = modifyRemark;
			document.getElementById('modifyRemark').disabled = "disabled";
			
			var modifyIds = $('input[name=chkbox]:checked').map(function() {
	        	return $(this).val();
	        }).get();
			document.getElementById('modifyIds').value = modifyIds;
			
			if(ckeckedCount == 1){
				document.getElementById('modifyConnectionMode').value=$('input[name=chkbox]:checked:eq(0)').parents("tr").children().eq(3).text(); 
				document.getElementById('modifyLoginAccount').value=$('input[name=chkbox]:checked:eq(0)').parents("tr").children().eq(4).text();
				document.getElementById('modifyLoginPassword').value=atob($('input[name=chkbox]:checked:eq(0)').parents("tr").children().eq(5).children().eq(0).val());
				document.getElementById('modifyEnablePassword').value=atob($('input[name=chkbox]:checked:eq(0)').parents("tr").children().eq(6).children().eq(0).val());
				document.getElementById('modifyEnableBackup').value=$('input[name=chkbox]:checked:eq(0)').parents("tr").children().eq(7).text();
			}
			
			$("#modifyModal").modal({
				backdrop : 'static'
			});
    		
    	} else {
    		alert('請勾選要修改的設備');
    	}
		
	});
	
	$("#btnDelete").click(function() {
		envAction('delete');
	});
	
	$("#btnSave").click(function() {
		envAction('modify');
	});
});

function chkChecked() {
	var hasChecked = 0;
	
	$('input[type=checkbox][name=chkbox]').each(function () {
        if (this.checked) {
        	hasChecked ++;
         	return false;
        }
	});
	
	return hasChecked;
}

function toggleActionBar() {
	$("#modifyActionBar").toggle();
	$("#defaultActionBar").toggle();
}

function envAction(action) {
	if (action == "modify") {
		var obj = new Object();
		
		var ids = $("input[name='chkbox']:checked").map(function() {
	     	return $(this).val();
	     }).get();
		
		if($("#modifyConnectionMode").length){
			var modifyConnectionMode = $('#modifyConnectionMode :selected').map(function() {
	        	return $(this).val();
	        }).get();
		}
		var modifyLoginAccount = $("input[name='modifyLoginAccount']").map(function() {
						        	return $(this).val();
						        }).get();
		var modifyLoginPassword = $("input[name='modifyLoginPassword']").map(function() {
						         	return $(this).val();
						         }).get();
		var modifyEnablePassword = $("input[name='modifyEnablePassword']").map(function() {
						         	 return $(this).val();
						          }).get();
		if($("#modifyEnableBackup").length){
			var modifyEnableBackup = $('#modifyEnableBackup :selected').map(function() {
	        	return $(this).val();
	        }).get();
		}
		
		obj.ids = ids;
		obj.modifyConnectionMode = modifyConnectionMode;		
		obj.modifyLoginAccount = modifyLoginAccount;
		obj.modifyLoginPassword = modifyLoginPassword;
		obj.modifyEnablePassword = modifyEnablePassword;
		obj.modifyEnableBackup = modifyEnableBackup;
		
		doActionAjax(obj, "save");
	} else if (action == "delete") {
		var checkedItem = $('input[name=chkbox]:checked');
		
		if (checkedItem.length == 0) {
			alert('請先勾選欲刪除的項目');
			return;
		}
		
		confirm("請確認是否刪除", "doDeleteActionAjax")
	}
	
}

function doDeleteActionAjax() {
	var obj = new Object();
	
	var ids = $("input[name='chkbox']:checked").map(function() {
     	return $(this).val();
     }).get();
	
	obj.ids = ids;
	
	doActionAjax(obj, "delete");
}

function doActionAjax(obj, action) {
	$.ajax({
		url : _ctx + '/deviceLoginInfo/'+action,
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
					$('#modifyModal').modal('hide');
					
				}, 500);
				
				alert(resp.message);
				
				findData($("#queryFrom").val());
				
			} else {
				alert('envAction > success > else :: resp.code: '+resp.code);
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
	$("#queryFrom").val(from);
	$("input[name=checkAll]").prop("checked", false);
	
	if (from == "MOBILE") {
		$("#collapseExample").collapse("hide");
	}
		
	if (typeof resultTable !== "undefined") {
		//resultTable.clear().draw(); server-side is enabled.
		resultTable.ajax.reload();
		
	} else {
		resultTable = $("#resultTable").DataTable(
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
			"scrollY"		: dataTableHeight -20,
			"scrollCollapse": true,
			"pageLength" 	: 100,
			"language" : {
	    		"url" : _ctx + "/resources/js/dataTable/i18n/Chinese-traditional.json"
	        },
			"ajax" : {
				"url" : _ctx + "/deviceLoginInfo/getDeviceLoginInfo",
				"type" : "POST",
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
				"dataSrc" : function (json) {
					$("#diffMsg").text(json.msg);
					return json.data;
				},
				"error" : function(xhr, ajaxOptions, thrownError) {
					ajaxErrorHandler();
				}
			},
			"order": [[2 , "asc" ]],
			"initComplete": function(settings, json) {
            },
			"drawCallback" : function(settings) {
				$.fn.dataTable.tables( { visible: true, api: true } ).columns.adjust();
				$("div.dataTables_length").parent().removeClass("col-sm-12");
				$("div.dataTables_length").parent().addClass("col-sm-6");
				$("div.dataTables_filter").parent().removeClass("col-sm-12");
				$("div.dataTables_filter").parent().addClass("col-sm-6");
				
				$("div.dataTables_info").parent().removeClass("col-sm-12");
				$("div.dataTables_info").parent().addClass("col-sm-6");
				$("div.dataTables_paginate").parent().removeClass("col-sm-12");
				$("div.dataTables_paginate").parent().addClass("col-sm-6");
				
				bindTrEvent();
			},
			"columns" : [
				{},{},
				{ "data" : "remark", "searchable": true },				
				{ "data" : "connectionMode", "searchable": false },
				{ "data" : "loginAccount", "searchable": false },				
				{ "data" : "loginPassword", "searchable": false },
				{ "data" : "enablePassword", "searchable": false },
				{ "data" : "enableBackup", "searchable": false },
				{ "data" : "communityString", "searchable": false },
				{ "data" : "udpPort", "searchable": false, "className" : "right"},
				{ "data" : "updateTimeStr" },
				{ "data" : "updateBy" },
			],
			"columnDefs" : [
				{
					"targets" : [0],
					"className" : "center",
					"searchable": false,
					"orderable": false,
					"render" : function(data, type, row) {
								 var html = '<input type="checkbox" id="chkbox" name="chkbox" onclick="changeTrBgColor(this)" value="'+row.deviceId+'">';
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
					"targets" : [4],
					"className" : "center",
					"searchable": false,
					"orderable": false,
					"render": function (data, type, row, meta) {
								 return atob(row.loginAccount);
						   	}
				},
				{
					"targets" : [5],
					"className" : "center",
					"searchable": false,
					"orderable": false,
					"render": function (data, type, row, meta) {
								var html = '<input type="password" value="'+row.loginPassword+'" style="max-width: 50px" readonly="readonly">';
								 return html;
						   	}
				},
				{
					"targets" : [6],
					"className" : "center",
					"searchable": false,
					"orderable": false,
					"render": function (data, type, row, meta) {
								var html = '<input type="password" value="'+row.enablePassword+'" style="max-width: 50px" readonly="readonly">';
								 return html;
						   	}
				}
			]
		});
	}
}