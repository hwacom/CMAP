/**
 * 
 */
var scriptShowMaxLine = 2;
var _SCRIPT_TYPE_ = "SCRIPT_TYPE";
var STEP_NUM = 1;
var scriptCodeCheck = false;

$(document).ready(function() {
	initMenuStatus("toggleMenu_cm", "toggleMenu_cm_items", "cm_script");
	
	//info add
	$("#btnAdd").click(function(e) {
		var scriptType = $("#queryScriptType :selected").val();
		
		if (scriptType == "") {
			alert(msg_chooseType);
			return;
		}
		
		showPanel('add');
	});
	
	$("#btnModify").click(function() {
		if ($('input[name=radiobox]:checked:eq(0)').length == 0) {
			alert("請選取修改項目！");
			return false;
		}
		
		showPanel('modify');
	});
	
	$("#btnDelete").click(function(e) {
		if ($('input[name=radiobox]:checked:eq(0)').length == 0) {
			alert("請選取刪除項目！");
			return false;
		}
		
		confirm("請確認是否刪除", "doDeleteActionAjax")
	});
	
	$("#btnStepGoPrev").click(function(e) {		
		goStep(-1);
	});
	
	$("#btnStepGoNext").click(function(e) {
		e.preventDefault();
		goStep(1);
	});
	//info save
	$("#btnStepGoFire").click(function(e) {
		envAction('add');
	});
	
//	$('#addScriptCode').unbind('blur').bind('blur',function(){
//		if ($('input[name=radiobox]:checked:eq(0)').length == 0 && $('#addScriptCode').val().trim().length > 0) {
//			if($('#addScriptCode').val().trim().length > 3){
//				alert("輸入數值過大，請小於999");
//				return false;
//			}
//			$('#addScriptCode').val(paddingLeft($('#addScriptCode').val(), 3));
//			checkScriptCode();
//		}		
//    });
	
	$("#btnTypeModify").click(function(e) {
		window.sessionStorage.clear();
		var scriptType = $("#queryScriptType :selected");
		
		if (scriptType.val() == "") {
			$("#scriptTypeName").val("");
			$("#scriptTypeCode").val("").removeAttr("readonly");
		}else{
			$("#scriptTypeName").val(scriptType.text());
			$("#scriptTypeCode").val(scriptType.val()).attr("readonly", "readonly");
		}
		
		$("#addModifyModal").modal({
			backdrop : 'static'
		});
	});
	
	//Type save
	$("#btnSave").click(function() {
		envAction('saveType');
	});
});

//查看腳本內容
function showScriptContent(scriptInfoId, type, action) {
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
				if(action == 'showContent'){
					$('#viewScriptModal_scriptName').val(resp.data.scriptName);
					$('#viewScriptModal_scriptContent').html(resp.data.content);
					
					$('#viewScriptModal').modal('show');
				}else if(action == 'modifyInfo'){
					if(resp.data.systemDefault == 'Y'){
						alert('此為系統預設腳本不可異動!!');
						return ;
					}
					
					$('#addScriptCode').val(resp.data.scriptCode).attr("readonly", "readonly");
					$('#addScriptName').val(resp.data.scriptName);
					$('#addScriptRemark').val(resp.data.remark);
					$('#addScriptContent').val(resp.data.content);
					
					var deviceModel = document.getElementById("addDeviceModel");
					for (var j = 0, ilen = deviceModel.options.length; j < ilen; j++) {
						if(resp.data.model === deviceModel.options[j].value){
							deviceModel.options[j].selected = true;
							break;
						}
					}
					$('#stepModal').modal({
						backdrop : 'static'
					});
					
					window.sessionStorage.setItem("addScriptType", resp.data.type);
				}				
				
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
					$(row).children('td').eq(5).attr('onclick','javascript:showScriptContent(\''+data.scriptInfoId+'\', \'A\', \'showContent\');');
					$(row).children('td').eq(5).addClass('cursor_zoom_in');
				}
        	   
				if(data.checkScript != null) { //加上onclick事件(查看script完整內容)
					$(row).children('td').eq(7).attr('onclick','javascript:showScriptContent(\''+data.scriptInfoId+'\', \'C\', \'showContent\');');
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
								 var html = '<input type="radio" id="radiobox" name="radiobox" onclick="resetTrBgColor();changeTrBgColor(this)" value='+row.scriptInfoId+'>';
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


function showPanel(action) {
	window.sessionStorage.clear();

	STEP_NUM = 1;
	initStepBtn();
	initStepImg();
	$("#step1_section").show();
	$("#step2_section").hide();
	$("#step3_section").hide();
	
	if(action == 'add'){
		var varInput = $("input[name=input_var]");
		if (varInput.length > 0) {
			$.each(varInput, function(key, input) {
				input.value = "";
			});
		}
		scriptCodeCheck = false;

		$('#stepModal').modal({
			backdrop : 'static'
		});
		
		window.sessionStorage.setItem("addScriptType", $("#queryScriptType :selected").val());
		checkScriptCode();		
	}else if (action == 'modify'){
		showScriptContent($("input[name='radiobox']:checked").val(), 'A', 'modifyInfo') ;
				
		scriptCodeCheck = true;
	}
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
	
	if(STEP_NUM == 1 && nextStep == 2){//進行到第二步
		$("#currentIndex").val(1);
		$("#showContentValue").val(window.sessionStorage.getItem("addScriptContentValue").split(",")[0]);
		
		var varInput = $("input[name=input_var2]");
		if (varInput.length > 0) {
			$.each(varInput, function(key, input) {
				input.value = "";
			});
		}
	}else if(STEP_NUM == 2 && nextStep == 1){//退回第一步 
		if($("#currentIndex").val() > 1){
			var varInput = $("input[name=input_var2]");
			if (varInput.length > 0) {
				$.each(varInput, function(key, input) {
					var storageValue = window.sessionStorage.getItem(input.id);
//					console.log("cut value = "+ input.id +", before = " + storageValue +", after = " + storageValue.substring(0, storageValue.lastIndexOf(",")));
					input.value = storageValue.substring(storageValue.lastIndexOf(",") +1 , storageValue.length);
//					console.log("change value = "+ input.id +", = " + input.value);
					window.sessionStorage.setItem(input.id, storageValue.substring(0, storageValue.lastIndexOf(",")));
				});
			}
			
			var idx = parseInt($("#currentIndex").val())-1;
			$("#currentIndex").val(idx);
			$("#showContentValue").val(window.sessionStorage.getItem("addScriptContentValue").split(",")[idx-1]);
			
			if(idx == 1){
				var varInput = $("input[name=input_var2]");
				if (varInput.length > 0) {
					$.each(varInput, function(key, input) {
//						console.log("removeItem id = "+ input.id +", ");
						window.sessionStorage.removeItem(input.id);
					});
				}
			}			
			return ;
		}
	}else if(STEP_NUM == 3 && nextStep == 2){//退回第二步
		var varInput = $("input[name=input_var2]");
		if (varInput.length > 0) {
			$.each(varInput, function(key, input) {
				var storageValue = window.sessionStorage.getItem(input.id);
//				console.log("cut value = "+ input.id +", before = " + storageValue +", after = " + storageValue.substring(0, storageValue.lastIndexOf(",")));
				input.value = storageValue.substring(storageValue.lastIndexOf(",") +1 , storageValue.length);
//				console.log("change value = "+ input.id +", = " + input.value);
				window.sessionStorage.setItem(input.id, storageValue.substring(0, storageValue.lastIndexOf(",")));
			});
		}
	}else if(STEP_NUM == 2 && nextStep == 3){
		if($("#currentIndex").val() < window.sessionStorage.getItem("addScriptContentLine")){
			var idx = parseInt($("#currentIndex").val())+1;
			$("#currentIndex").val(idx);
			$("#showContentValue").val(window.sessionStorage.getItem("addScriptContentValue").split(",")[idx-1]);
			
			var varInput = $("input[name=input_var2]");
			if (varInput.length > 0) {
				$.each(varInput, function(key, input) {
					input.value = "";
				});
			}
			return ;
		}
	}
	
	$("#step" + nextStep + "_section").show();
	$("#step" + nowStep + "_section").hide();
	
	STEP_NUM = parseInt(STEP_NUM) + parseInt(num);
	initStepBtn();
	initStepImg();
}

function checkB4Next(num) {
	var validateErrorObj = [];
	var inputVar = [];
	
	switch (num) {
		case 1:
			if(!scriptCodeCheck){
				return false;
			}
			var varInput = $("input[name=input_var]");
			var varText = $("textarea[name=input_var]");
			var success = true;
			if (varInput.length > 0) {
				$.each(varInput, function(key, input) {
					if (input.required && input.value.trim().length == 0) {
//						console.log("input word : id = "+ input.id +", is required!!");
						success = false
						validateErrorObj.push(input);
					} else {
//						console.log("input word : id = "+ input.id +", " + input.value.trim());
						window.sessionStorage.setItem(input.id, input.value.trim());
					}
				});
			}
			if (varText.length > 0) {
				var lineCount = 0;
				$.each(varText, function(key, input) {
					if (input.required && input.value.trim().length == 0) {
//						console.log("input word : id = "+ input.id +", is required!!");
						success = false
						validateErrorObj.push(input);
					} else {						
						var textValue = input.value.trim().split(/\r?\n|\r/g);
						$.each(textValue, function(key, value) {
//							console.log("TEXTAREA word : " + value);
							if(value.trim().length > 0){
								inputVar.push(value.trim().replace(",", " "));
								lineCount ++ ;
							}
						});
						window.sessionStorage.setItem(input.id+"Value", inputVar);
						window.sessionStorage.setItem(input.id+"Line", lineCount);
					}
//					console.log("TEXTAREA word : " + input.value.trim().length);
//					console.log("TEXTAREA line : " + input.value.trim().match(/\r?\n|\r/g).length);
//					console.log("TEXTAREA line 2 : " + lineCount);	
				});
				
				
			}
			if (!success) {
				$.each(validateErrorObj, function(key, input) {
					$(input).addClass("required");
				});
				
				alert("必填欄位請輸入資訊!");
				return false;
			}
			window.sessionStorage.setItem("addDeviceModel", $('#addDeviceModel :selected').val());
			
			return success;
			break;
		
		case 2:
			var varInput = $("input[name=input_var2]");
			var success = true;
			if (varInput.length > 0) {
				$.each(varInput, function(key, input) {
//					console.log("input word : id = "+ input.id +", " + input.value.trim());
					if (input.required && input.value.trim().length == 0) {
						console.log("input word : id = "+ input.id +", is required!!");
						success = false
						validateErrorObj.push(input);
					} else {
						if(window.sessionStorage.getItem(input.id) != null){
							inputVar = new Array(window.sessionStorage.getItem(input.id));
						}else{
							inputVar = [];
						}
						
						if(input.value.trim() == ""){
							input.value = "";
						}
						inputVar.push(input.value.replace(",", " "));
						window.sessionStorage.setItem(input.id, inputVar);
					}
				});
			}
			
			if (!success) {
				$.each(validateErrorObj, function(key, input) {
					$(input).addClass("required");
				});
				
				alert("必填欄位請輸入資訊!");
				return false;
			}
			return success;
			break;
		case 3:
			return success;
			break;
	}
}

//確認腳本代碼
function checkScriptCode() {
	var obj = new Object();
	obj.scriptType = $("#queryScriptType :selected").val();
	var result = true;
	
	$.ajax({
		url : _ctx + '/script/checkScriptCode.json',
		data : JSON.stringify(obj),
		headers: {
		    'Accept': 'application/json',
		    'Content-Type': 'application/json'
		},
		type : "POST",
		async: true,
		success : function(resp) {
			if (resp.code == '200') {
				$("#addScriptCode").val(resp.message);
				scriptCodeCheck = true;
			} else {
				alert(resp.message);
				scriptCodeCheck = false;
			}
		},
		error : function(xhr, ajaxOptions, thrownError) {
			ajaxErrorHandler();
			scriptCodeCheck = false;
		}
	});
}

function envAction(action) {
	if (action == "add") {
		var obj = new Object();
		var key = "";
		for (var i=0; i<window.sessionStorage.length; i++) {
			key = window.sessionStorage.key(i);
			obj[key] = window.sessionStorage.getItem(key);
//			console.log("get key =" + key +", value = " + window.sessionStorage.getItem(key));
		}
		
		doActionAjax(obj, "save");
	}else if(action == "saveType"){
		var obj = new Object();
		obj.scriptTypeCode = $("#scriptTypeCode").val();
		obj.scriptTypeName = $("#scriptTypeName").val();

		doActionAjax(obj, "saveType");
	}
}

function doDeleteActionAjax() {
	var obj = new Object();
	var scriptInfoId = $("input[name='radiobox']:checked").val(); 
	obj.scriptInfoId = scriptInfoId;

	doActionAjax(obj, "delete");
}

function doActionAjax(obj, action) {
	$.ajax({
		url : _ctx + '/script/'+action,
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
				
				if(action == "save"){
					setTimeout(function(){
						$('#stepModal').modal('hide');
						
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
