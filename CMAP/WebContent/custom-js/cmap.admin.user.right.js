/**
 * 
 */
var remarkShowLength = 30;	//設定欄位顯示內容最大長度

$(document).ready(function() {
	initMenuStatus("toggleMenu_admin", "toggleMenu_admin_items", "bk_usr");
	//查詢按鈕(Web)點擊事件
	if(isWEB){
		findData('WEB');
	}else{
		findData('MOBILE');
	}
    
	$("#btnAdd").click(function() {
		uncheckAll();
		initModal();
		
		$("#addModifyModal").modal({
			backdrop : 'static'
		});
	});
	
	$("#btnModify").click(function() {
		changeModifyView();
	});
	
	$("#btnDelete").click(function() {
		envAction('delete');
	});
	
	$("#btnModifySubmit").click(function() {
		envAction('modify');
	});
	
	$("#btnModifyCancel").click(function() {
		findData($("#queryFrom").val());
	});
	
	$("#btnSave").click(function() {
		envAction('add');
	});
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

function changeModifyView() {
	var checkedItem = $('input[name=chkbox]:checked');
	
	if (checkedItem.length == 0) {
		alert('請先勾選欲修改的項目');
		return;
	}
	
	if (checkedItem.length > 1) {
		alert('請僅勾選1個項目');
		return;
	}
	
	for (var i=0; i<checkedItem.length; i++) {
		
		var hasInnerText = (document.getElementsByTagName("body")[0].innerText !== undefined) ? true : false;
		
		$('input[name=chkbox]:checked:eq('+i+')').attr('disabled','disabled'); //關閉列表勾選框
		
		$('input[name=chkbox]:checked:eq('+i+')').parents("tr").children().eq(2).html(
			//切換「account」欄位為輸入框
			function() {
				var html = '<input type="text" name="modeifyAccount" value="' + $(this).text() +'" class="form-control form-control-sm" style="min-width: 100px" readonly/>';
				return html;
			}
		);
		$('input[name=chkbox]:checked:eq('+i+')').parents("tr").children().eq(3).html(
				//切換「UserName」欄位為輸入框
				function() {
					var html = '<input type="text" name="modifyUserName" value="' + $(this).text() +'" class="form-control form-control-sm" style="min-width: 150px"/>';
					return html;
				}
			);
		$('input[name=chkbox]:checked:eq('+i+')').parents("tr").children().eq(4).html(
			//切換「Password」欄位為輸入框
			function() {
				var html = '<input type="password" name="modifyPassword" value="' + $(this).children().val() +'" class="form-control form-control-sm" style="min-width: 100px" />';
				return html;
			}
		);
		$('input[name=chkbox]:checked:eq('+i+')').parents("tr").children().eq(7).html(
				//切換「Remark」欄位為輸入框
				function() {
					if(document.getElementById("isAdmin").value == "true"){
						var html = '<input type="text" name="modifyRemark" value="' + $(this).text() +'" class="form-control form-control-sm"style="min-width: 150px"/>';
						return html;
					}
				}
			);
		if($("#btnAdd").length){
			var selectedUserGroupIndex = 0;
			$('input[name=chkbox]:checked:eq('+i+')').parents("tr").children().eq(5).html(
					//切換「modifyUserGroup」欄位為輸入框
					function() {
						var html = '<select id="modifyUserGroup"  class="form-control form-control-sm" style="min-width: 120px">';
						var userGroupList = document.getElementById("addUserGroup");
						var index = $('input[name=chkbox]:checked:eq('+i+')').parents("tr").children().eq(1).text();
//						var textValue = $('input[name=chkbox]:checked:eq('+i+')').parents("tr").children().eq(6).text();
						var textValue = $("#resultTable").DataTable().columns( [6] ).data()[0][index-1];
						
						for (var j = 0, ilen = userGroupList.options.length; j < ilen; j++) {
							html = html + '<option value="'+userGroupList.options[j].value+'" >'+userGroupList.options[j].text+'</option>';					
							if(textValue === userGroupList.options[j].value){
								selectedUserGroupIndex = j;							
							}
						}
						html = html + '</select>';
						return html;
					}
				);
			
			var selectedLoginModeIndex = 0;
			$('input[name=chkbox]:checked:eq('+i+')').parents("tr").children().eq(6).html(
					//切換「modifyLoginMode」欄位為輸入框
					function() {
						var html = '<select id="modifyLoginMode"  class="form-control form-control-sm" style="min-width: 120px">';
						var loginModeList = document.getElementById("addLoginMode");
						
						for (var j = 0, ilen = loginModeList.options.length; j < ilen; j++) {
							html = html + '<option value="'+loginModeList.options[j].value+'" >'+loginModeList.options[j].text+'</option>';
							if($(this).text() === loginModeList.options[j].value){
								selectedLoginModeIndex = j;							
							}
						}
						html = html + '</select>';
						return html;
					}
				);
		}
		
	}
	
	if($("#btnAdd").length){
		document.getElementById("modifyUserGroup").options[selectedUserGroupIndex].selected = true;	
		document.getElementById("modifyLoginMode").options[selectedLoginModeIndex].selected = true;
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
	
	var ids = $("input[name='chkbox']:checked").map(function() {
     	return $(this).val();
     }).get();
	
	obj.ids = ids;
	
	if (action == "modify") {
		var modeifyAccount = $("input[name='modeifyAccount']").map(function() {
						        	return $(this).val();
						         }).get();
		var modifyUserName = $("input[name='modifyUserName']").map(function() {
						         	return $(this).val();
						         }).get();
		var modifyPassword = $("input[name='modifyPassword']").map(function() {
						         	return $(this).val();
						         }).get();
		var modifyRemark = $("input[name='modifyRemark']").map(function() {
        	 						return $(this).val();
         						}).get();
		if($("#modifyUserGroup").length){
			var modifyUserGroup = $('#modifyUserGroup :selected').map(function() {
	        	return $(this).val();
	        }).get();
		}else{
			var modifyUserGroup = null;
		}
		if($("#modifyLoginMode").length){
			var modifyLoginMode = $('#modifyLoginMode :selected').map(function() {
	        	return $(this).val();
	        }).get();
		}else{
			var modifyLoginMode = null;
		}
		
		var addIsAdmin = null;
		
		obj.modeifyAccount = modeifyAccount;
		obj.modifyUserName = modifyUserName;
		obj.modifyPassword = modifyPassword;
		obj.modifyUserGroup = modifyUserGroup;
		obj.modifyLoginMode = modifyLoginMode;
		obj.modifyRemark = modifyRemark;
		obj.addIsAdmin = addIsAdmin;
		
		doActionAjax(obj, "save");
	} else if (action == "delete") {
		var checkedItem = $('input[name=chkbox]:checked');
		
		if (checkedItem.length == 0) {
			alert('請先勾選欲刪除的項目');
			return;
		}
		
		confirm("請確認是否刪除", "doDeleteActionAjax")
	} else if (action == "add") {
		var checkFlag = true;
		var checkFields = document.getElementsByClassName("checkRequired");
		for(var i= 0 ; i < checkFields.length; i++){
			var val = checkFields[i].value;
			if(!val || /^\s*$/.test(val)){
				alert(checkFields[i].placeholder + "不可為空白");
				return ;
			}
		}
		
		var modeifyAccount = $("input[name='addAccount']").map(function() {
		        	return $(this).val();
		        }).get();
		var modifyUserName = $("input[name='addUserName']").map(function() {
		         	return $(this).val();
		         }).get();
		var modifyPassword = $("input[name='addPassword']").map(function() {
		         	 return $(this).val();
		          }).get();
		var modifyUserGroup = $('#addUserGroup :selected').map(function() {
			        	return $(this).val();
			        }).get();
		var modifyLoginMode = $('#addLoginMode :selected').map(function() {
			        	return $(this).val();
			        }).get();
		var modifyRemark = $("input[name='addRemark']").map(function() {
         	return $(this).val();
         }).get();
		var addIsAdmin = $('#addIsAdmin :selected').map(function() {
			       	 return $(this).val();
			        }).get();
		//obj.id = null;
		obj.modeifyAccount = modeifyAccount;
		obj.modifyUserName = modifyUserName;
		obj.modifyPassword = modifyPassword;
		obj.modifyUserGroup = modifyUserGroup;
		obj.modifyLoginMode = modifyLoginMode;
		obj.modifyRemark = modifyRemark;
		obj.addIsAdmin = addIsAdmin;
		
		doActionAjax(obj, "save");
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
		url : _ctx + '/userRight/'+action,
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
				
				if(obj.addIsAdmin != null){
					setTimeout(function(){
						$('#addModifyModal').modal('hide');
						
					}, 500);
				}
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
	
	initActionBar();
	
	if (typeof resultTable !== "undefined") {
		//resultTable.clear().draw(); server-side is enabled.
		resultTable.ajax.reload();
		
	} else {
		resultTable = $("#resultTable").DataTable(
		{
			"autoWidth" 	: true,
			"paging" 		: false,
			"bFilter" 		: false,
			"ordering" 		: true,
			"info" 			: true,
			"serverSide" 	: true,
			"bLengthChange" : true,
			"pagingType" 	: "full",
			"processing" 	: true,
			"scrollX"		: true,
			"scrollY"		: dataTableHeight -20,
			"scrollCollapse": true,
			"language" : {
	    		"url" : _ctx + "/resources/js/dataTable/i18n/Chinese-traditional.json"
	        },
//	        "createdRow": function( row, data, dataIndex ) {
//	        	   if(data.settingRemark != null && data.settingRemark.length > remarkShowLength) { //當內容長度超出設定值，加上onclick事件(切換顯示部分or全部)
//	        	      $(row).children('td').eq(2).attr('onclick','javascript:changeShowContent(this, '+remarkShowLength+');');
//	        	      $(row).children('td').eq(2).addClass('cursor_zoom_in');
//	        	   }
//	        	   $(row).children('td').eq(2).attr('content', data.settingRemark);
//	        	},
			"ajax" : {
				"url" : _ctx + "/userRight/getUserRightSetting",
				"type" : "POST",
				"data" : function ( d ) {},
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
				resultTable.columns( [6] ).visible( false );
				if(document.getElementById("isAdmin").value != "true"){
					resultTable.columns( [9] ).visible( false );
				}
				resultTable.columns( [10] ).visible( false );
				resultTable.columns( [11] ).visible( false );
				//resultTable.columns( [12] ).visible( false );
				//resultTable.columns( [13] ).visible( false );
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
				{ "data" : "account" },				
				{ "data" : "userName" },
				{ "data" : "password" },				
				{ "data" : "userGroupStr" },
				{ "data" : "userGroup", "className": "hiddenColumn" },
				{ "data" : "loginMode" },
				{ "data" : "remark" },
				{ "data" : "isAdmin" , "className" : "center" },
				{ "data" : "createTimeStr" },
				{ "data" : "createBy" },
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
								 var html = '<input type="checkbox" id="chkbox" name="chkbox" onclick="changeTrBgColor(this)" value="'+row.id+'">';
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
								var html = '<input type="password" value="'+row.password+'" style="max-width: 70px" readonly="readonly">';
								 return html;
						   	}
				}
			],
		});
	}
}