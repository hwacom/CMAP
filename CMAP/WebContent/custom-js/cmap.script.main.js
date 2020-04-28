/**
 * 
 */
var scriptShowMaxLine = 2;
var _SCRIPT_TYPE_ = "SCRIPT_TYPE";

$(document).ready(function() {
	initMenuStatus("toggleMenu_cm", "toggleMenu_cm_items", "cm_script");
	
	$("#btnAdd").click(function(e) {
		showPanel();
	});
	
	$("#btnStepGoPrev").click(function(e) {
		goStep(-1);
	});
	
	$("#btnStepGoNext").click(function(e) {
		e.preventDefault();
		goStep(1);
	});
	
	$("#btnStepGoFire").click(function(e) {
//		doDelivery();
	});
});

//查看腳本內容
function showScriptContent(scriptInfoId, type) {
	var obj = new Object();
	obj.scriptInfoId = scriptInfoId;
	obj.type = type;
	
	$.ajax({
		url : _ctx + '/script/view',
		data : JSON.stringify(obj),
		headers: {
		    'Accept': 'application/json',
		    'Content-Type': 'application/json'
		},
		type : "POST",
		async: true,
		/*
		beforeSend : function() {
			showProcessing();
		},
		complete : function() {
			hideProcessing();
		},
		*/
		success : function(resp) {
			if (resp.code == '200') {
				$('#viewScriptModal_scriptName').val(resp.data.script);
				$('#viewScriptModal_scriptContent').html(resp.data.content);
				
				$('#viewScriptModal').modal('show');
				
			} else {
				alert(resp.message);
			}
		},
		error : function(xhr, ajaxOptions, thrownError) {
			ajaxErrorHandler();
		}
	});
}

function getPartialContent(content) {
	var retVal = '';
	if (content != null && content != '') {
		var line = content.split('<br>');
		
		if (line.length > scriptShowMaxLine) {
			for (var i=0; i<scriptShowMaxLine; i++) {
				retVal += line[i];
				
				if (i != scriptShowMaxLine - 1) {
					retVal += '<br>';
				}
			}
			
			retVal += '&nbsp;&nbsp;<a href="javascript:void(0);" >...(顯示)</a>';
			
		} else {
			retVal = content;
		}
	}
	
	return retVal;
}

//查詢按鈕動作
function findData(from) {
	$('#queryFrom').val(from);
	$('input[name=checkAll]').prop('checked', false);
	
	if (from == 'MOBILE') {
		$('#collapseExample').collapse('hide');
	}
	
	if (typeof resultTable !== "undefined") {
		resultTable.clear().draw();
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
			"createdRow": function( row, data, dataIndex ) {
				if (data.enableModify == false) {
					$(row).children('td').eq(0).addClass('hide');
				}
				
				if(data.actionScript != null) { //加上onclick事件(查看script完整內容)
					$(row).children('td').eq(5).attr('onclick','javascript:showScriptContent(\''+data.scriptInfoId+'\', \'A\');');
					$(row).children('td').eq(5).addClass('cursor_zoom_in');
				}
        	   
				if(data.checkScript != null) { //加上onclick事件(查看script完整內容)
					$(row).children('td').eq(7).attr('onclick','javascript:showScriptContent(\''+data.scriptInfoId+'\', \'C\');');
					$(row).children('td').eq(7).addClass('cursor_zoom_in');
				}
        	},
			"columns" : [
				{},{},
				{ "data" : "scriptName" },
				{ "data" : "scriptTypeName" , "className" : "center" },
				{ "data" : "deviceModel" , "className" : "center" },
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
								return getPartialContent(row.actionScript); //當內容行數超出設定，僅顯示部分內容
						   	}
				},
				{
					"targets" : [7],
					"className" : "left",
					"searchable": true,
					"orderable": true,
					"render": function (data, type, row, meta) {
								return getPartialContent(row.checkScript); //當內容行數超出設定，僅顯示部分內容
						   	}
				}
			],
		});
	}
}


function showPanel() {
	var scriptType = $("#queryScriptType").val();
	
	if (scriptType == "") {
		alert(msg_chooseType);
		return;
	}
	
	window.sessionStorage.clear();
	
	STEP_NUM = 1;
	initStepBtn();
	initStepImg();
	$('#stepModal').modal({
		backdrop : 'static'
	});
	
}

function initStepBtn() {
	switch (STEP_NUM) {
		case 1:
			$("#btnStepGoPrev").hide();
			$("#btnStepGoNext").show();
			$("#btnStepGoFire").hide();
			break;
			
		case 2:
			$("#btnStepGoPrev").show();
			$("#btnStepGoNext").show();
			$("#btnStepGoFire").hide();
			break;
			
		case 3:
			$("#btnStepGoPrev").show();
			$("#btnStepGoNext").hide();
			$("#btnStepGoFire").show();
			break;
	}
}

function initStepImg() {
	var idx = parseInt(STEP_NUM) - 1;
	$(".step-img").removeClass('step-current');
	$(".step-img").eq(idx).addClass('step-current');
}

function goStep(num) {
	var nextStep = parseInt(STEP_NUM) + parseInt(num);
	var nowStep = parseInt(STEP_NUM);
	
	var pass = false;
	if (parseInt(nextStep) > parseInt(nowStep)) {
		$(".required").removeClass("required");
		if(!checkB4Next(nowStep)){
			return ;
		}
		
	}
	
	$("#step" + nextStep + "_section").show();
	$("#step" + nowStep + "_section").hide();
	
	STEP_NUM = parseInt(STEP_NUM) + parseInt(num);
	initStepBtn();
	initStepImg();
	
	/*
	console.log("_DELIVERY_SCRIPT_INFO_ID_ : " + window.sessionStorage.getItem(_DELIVERY_SCRIPT_INFO_ID_));
	console.log("_DELIVERY_SCRIPT_CODE_ : " + window.sessionStorage.getItem(_DELIVERY_SCRIPT_CODE_));
	console.log("_DELIVERY_SCRIPT_NAME_ : " + window.sessionStorage.getItem(_DELIVERY_SCRIPT_NAME_));
	console.log("_DELIVERY_DEVICE_MENU_JSON_STR_ : " + window.sessionStorage.getItem(_DELIVERY_DEVICE_MENU_JSON_STR_));
	console.log("_DELIVERY_DEVICE_ID_ : " + window.sessionStorage.getItem(_DELIVERY_DEVICE_ID_));
	console.log("_DELIVERY_DEVICE_GROUP_NAME_ : " + window.sessionStorage.getItem(_DELIVERY_DEVICE_GROUP_NAME_));
	console.log("_DELIVERY_DEVICE_NAME_ : " + window.sessionStorage.getItem(_DELIVERY_DEVICE_NAME_));
	console.log("_DELIVERY_REASON_ : " + window.sessionStorage.getItem(_DELIVERY_REASON_));
	console.log("_DELIVERY_VAR_KEY_ : " + window.sessionStorage.getItem(_DELIVERY_VAR_KEY_));
	console.log("_DELIVERY_VAR_VALUE_ : " + window.sessionStorage.getItem(_DELIVERY_VAR_VALUE_));
	console.log("_DELIVERY_GROUP_ID_ : " + window.sessionStorage.getItem(_DELIVERY_GROUP_ID_));
	*/
}

function checkB4Next(num) {
	var validateErrorObj = [];
	var inputVar = [];
	
	switch (num) {
		case 1:
			var varInput = $("input[name=input_var]");
			var varText = $("textarea[name=input_var]");
			var success = true;
			if (varInput.length > 0) {
				$.each(varInput, function(key, input) {
					if (input.value.trim().length == 0) {
						success = false
						validateErrorObj.push(input);
						
					} else {
						var varIdx = $(input).data("idx");
						
						if (inputVar[varIdx] === undefined) {
							inputVar[varIdx] = [];
						}
						
						inputVar[varIdx].push(input.value.trim());
					}
				});
			}
			if (varText.length > 0) {
				$.each(varText, function(key, input) {
					if (input.value.trim().length == 0) {
						success = false
						validateErrorObj.push(input);
						
					} else {
						var varIdx = $(input).data("idx");
						
						if (inputVar[varIdx] === undefined) {
							inputVar[varIdx] = [];
						}
						
						inputVar[varIdx].push(input.value.trim());
						
						var textValue = input.value.trim().split(/\r?\n|\r/g);
						$.each(textValue, function(key, value) {
							console.log("TEXTAREA word : " + value);
						});
					}
					console.log("TEXTAREA word : " + input.value.trim().length);
					console.log("TEXTAREA line : " + input.value.trim().match(/\r?\n|\r/g).length + 1);	
				});
				
				
			}
			if (!success) {
				$.each(validateErrorObj, function(key, input) {
					$(input).addClass("required");
				});
				
				alert("請輸入變數值");
				return false;
			}			
					
			window.sessionStorage.setItem(_DELIVERY_VAR_VALUE_, JSON.stringify(inputVar));
			
			return success;
			break;
			
		case 2:
			$("#btnStepGoPrev").show();
			$("#btnStepGoNext").show();
			$("#btnStepGoFire").hide();
			break;
			
		case 3:
			$("#btnStepGoPrev").show();
			$("#btnStepGoNext").hide();
			$("#btnStepGoFire").show();
			break;
	}
}
